package org.eclipse.scout.tradingnetwork.server.orderbook;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.tradingnetwork.client.ethereum.smartcontract.SmartContractAdministrationFormData;
import org.eclipse.scout.tradingnetwork.server.ethereum.EthereumProperties.EthereumClientContractAddressUsdEur;
import org.eclipse.scout.tradingnetwork.server.ethereum.EthereumProperties.EthereumClientProperty;
import org.eclipse.scout.tradingnetwork.server.ethereum.EthereumProperties.EthereumDefaultAccount;
import org.eclipse.scout.tradingnetwork.server.ethereum.EthereumService;
import org.eclipse.scout.tradingnetwork.server.ethereum.model.Account;
import org.eclipse.scout.tradingnetwork.server.ethereum.model.Alice;
import org.eclipse.scout.tradingnetwork.server.orderbook.model.Order;
import org.eclipse.scout.tradingnetwork.server.orderbook.model.OrderMatch;
import org.eclipse.scout.tradingnetwork.shared.ethereum.IAccountService;
import org.eclipse.scout.tradingnetwork.shared.ethereum.smartcontract.ISmartContractAdminstrationService;
import org.eclipse.scout.tradingnetwork.shared.order.OrderBookTypeCodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert.Unit;

/**
 * Wrapper class for generated contract class. Keeps app code separate from smart contract code.
 */

@ApplicationScoped
public class OrderBookService {

  private static final Logger LOG = LoggerFactory.getLogger(EthereumService.class);

  public static final BigInteger GAS_PRICE_DEFAULT = BigInteger.valueOf(20_000_000_000L);
  public static final BigInteger GAS_LIMIT_DEFAULT = BigInteger.valueOf(40_000L);

  private ConcurrentMap<String, OrderBook> m_orderBookMap;

  @PostConstruct
  private void init() {
    if (null == m_orderBookMap) {
      m_orderBookMap = new ConcurrentHashMap<String, OrderBook>();
    }
  }

  /**
   * Deploys the order book service contract. The method blocks until the contract is deployed.
   *
   * @return address of the deployed contract
   * @throws ExecutionException
   * @throws InterruptedException
   */
  public String deploy(Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String symbol)
      throws InterruptedException, ExecutionException {

    OrderBook contract = OrderBook
        .deploy(getWeb3j(), credentials, gasPrice, gasLimit, BigInteger.valueOf(0), new Utf8String(symbol))
        .get();

    LOG.info(String.format("contract for '%s' successfully deployed at address %s", symbol, contract.getContractAddress()));

    m_orderBookMap.put(symbol, contract);

    return contract.getContractAddress();
  }

  private OrderBook load(String address, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
    OrderBook contract = OrderBook
        .load(address, getWeb3j(), credentials, gasPrice, gasLimit);

    return contract;
  }

  /**
   * Publish the specified order.
   *
   * @param type
   *          order type
   * @param amount
   * @param price
   * @return the transaction hash of the new order. Empty String if error occurred .
   */
  public String publish(Order order) {
    //TODO [uko] Check if order contains dealNr => not allowed
    Bool buy = new Bool(order.isBuy());
    Uint256 dealQuantity = new Uint256(BigInteger.valueOf(order.getAmount()));
    Uint256 dealPrice = new Uint256(BigInteger.valueOf((long) (100 * order.getPrice())));
    Uint256 extId = new Uint256(BigInteger.valueOf((long) order.getExtId()));

    String transactionHash = "";
    try {
      TransactionReceipt receipt = getContract(order.getCurrencyPair())
          .createOrder(
              dealQuantity,
              dealPrice,
              buy,
              extId)
          .get();

      transactionHash = receipt.getTransactionHash();
    }
    catch (InterruptedException | ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return transactionHash;
  }

  /**
   * @param contract
   * @return
   */
  private List<Order> getBuyOrders(OrderBook contract) {
    return getOrders(true, contract);
  }

  /**
   * @param contract
   * @return
   */
  private List<Order> getSellOrders(OrderBook contract) {
    return getOrders(false, contract);
  }

  /**
   * @param buy
   * @param contract
   * @return
   */
  private List<Order> getOrders(boolean buy, OrderBook contract) {
    List<Order> orders = new ArrayList<Order>();
    boolean reachedLastOrder = false;
    int index = 0;
    while (!reachedLastOrder) {
      Order orderAtIndex = getOrderAtIndex(buy, index, contract);
      if (null != orderAtIndex) {
        orders.add(orderAtIndex);
      }
      else {
        reachedLastOrder = true;
      }
      index += 1;
    }
    if (buy) {
      Collections.reverse(orders);
    }
    return orders;
  }

  /**
   * @param buy
   * @param index
   * @param contract
   * @return
   */
  private Order getOrderAtIndex(boolean buy, int index, OrderBook contract) {
    List<Type> list = null;
    if (buy) {
      try {
        list = contract.buyOrders(new Uint256(BigInteger.valueOf(index))).get();
      }
      catch (InterruptedException | ExecutionException e) {
        // TODO Auto-generated catch block
        // e.printStackTrace();
      }
    }
    else {
      try {
        list = contract.sellOrders(new Uint256(BigInteger.valueOf(index))).get();
      }
      catch (InterruptedException | ExecutionException e) {
        // TODO Auto-generated catch block
        // e.printStackTrace();
      }
    }
    Order order = null;
    if (null != list && list.size() > 0) {
      order = convertToOrder(list);
    }
    return order;
  }

  /**
   * Provides the sorted list of orders in the order book.
   *
   * @return
   * @throws @throws
   *           Exception
   */
  public List<Order> getOrders(String currencyPair) throws ProcessingException {
    List<Order> orders = new ArrayList<Order>();
    try {
      orders.addAll(getSellOrders(getContract(currencyPair)));
      orders.addAll(getBuyOrders(getContract(currencyPair)));
      orders.forEach((o) -> o.setCurrencyPair(currencyPair));
    }
    catch (InterruptedException | ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new ProcessingException(e.getMessage());
    }
    return orders;
  }

  public OrderMatch getMatch(String currencyPair) throws ProcessingException {
    Bool matchExists = Bool.DEFAULT; // default is false
    OrderMatch match = null;
    try {
      OrderBook contract = getContract(currencyPair);
      Future<Bool> matchExistsFuture = contract.matchExists();
      matchExists = matchExistsFuture.get();
      if (matchExists.getValue()) {
        Int256 topBuyOrderId = contract.topBuyOrderId().get();
        Int256 topSellOrderId = contract.topSellOrderId().get();
        match = new OrderMatch(
            topBuyOrderId.getValue().intValue(),
            topSellOrderId.getValue().intValue());
      }
    }
    catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
//      throw new ProcessingException(e.getMessage());
    }
    catch (Exception e) {
      // ignore => no match
    }
    return match;
  }

  public void executeMatch(String currencyPair, int buyOrderId, int sellOrderId) {
    try {
      getContract(currencyPair).executeMatch(new Int256(BigInteger.valueOf(buyOrderId)), new Int256(BigInteger.valueOf(sellOrderId)));
    }
    catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      throw new ProcessingException();
    }
  }

  public List<Order> getExecutedOrders(String currencyPair) {
    List<Order> orders = new ArrayList<Order>();
    try {
      orders.addAll(getExecutedOrders(getContract(currencyPair)));
      orders.forEach((o) -> o.setCurrencyPair(currencyPair));
    }
    catch (InterruptedException | ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new ProcessingException(e.getMessage());
    }

    return orders;
  }

  private List<Order> getExecutedOrders(OrderBook contract) {
    List<Order> orders = new ArrayList<Order>();
    boolean reachedLastOrder = false;
    int index = 0;
    while (!reachedLastOrder) {
      Order orderAtIndex = getExecutedOrderAtIndex(index, contract);
      if (null != orderAtIndex) {
        orders.add(orderAtIndex);
      }
      else {
        reachedLastOrder = true;
      }
      index += 1;
    }
    return orders;
  }

  private Order getExecutedOrderAtIndex(int index, OrderBook contract) {
    List<Type> list = null;
    try {
      list = contract.executedOrders(new Uint256(BigInteger.valueOf(index))).get();
    }
    catch (InterruptedException | ExecutionException e) {
      // TODO Auto-generated catch block
      // e.printStackTrace();
    }
    Order order = null;
    if (null != list && list.size() > 0) {
      order = convertToOrder(list);
    }
    return order;
  }

  /**
   * Returns the top buy order (or null) for the given order book
   *
   * @param currencyPair
   * @return
   */
  public Order getTopBuyOrder(String currencyPair) {
    Order topBuy = null;
    try {
      OrderBook contract = getContract(currencyPair);
      // TODO create method in contract to load specific order by id
//      Int256 orderId = contract.topBuyOrderId().get();
//      List<Type> orderList = contract.buyOrders(new Uint256(orderId.getValue())).get();
      Uint256 orderCount = contract.getNumberOfBuyOrders().get();
      topBuy = getOrderAtIndex(true, orderCount.getValue().intValue() - 1, contract);
    }
    catch (InterruptedException | ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return topBuy;
  }

  /**
   * Returns the top sell order (or null) for the given order book
   *
   * @param currencyPair
   * @return
   */
  public Order getTopSellOrder(String currencyPair) {
    Order topSell = null;
    try {
      OrderBook contract = getContract(currencyPair);
//      Int256 orderId = contract.topSellOrderId().get();
//      List<Type> orderList = contract.sellOrders(new Uint256(orderId.getValue())).get();
      Uint256 orderCount = contract.getNumberOfSellOrders().get();
      topSell = getOrderAtIndex(false, orderCount.getValue().intValue() - 1, contract);
    }
    catch (InterruptedException | ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return topSell;
  }

  /**
   * Removes the order from the order book
   *
   * @param order
   *          the id of the order to cancel
   * @return true if the order has been successfully deleted
   */
  public boolean remove(int order) {
    return false;
  }

  private Web3j getWeb3j() {
    return BEANS.get(EthereumService.class).getWeb3j();
  }

  public String getContractAddress(String currencyPair) {
    return m_orderBookMap.get(currencyPair).getContractAddress();
  }

  private OrderBook getContract(String currencyPair) throws InterruptedException, ExecutionException {
    // TODO [uko] GAS LIMIT?
    OrderBook orderBookContract = m_orderBookMap.get(currencyPair);

    // no order book: try to load order book via address stored in data base
    if (null == orderBookContract) {
      String contractAddress = loadContractAddressFromDatabase(currencyPair);
      BigInteger gasLimit = BigInteger.valueOf(4_000_000L);

      if (StringUtility.hasText(contractAddress)) {
        String accountAddress = CONFIG.getPropertyValue(EthereumDefaultAccount.class);
        String accountPassword = BEANS.get(IAccountService.class).getPassword(accountAddress);

        if (StringUtility.hasText(accountAddress)) {
          Account account = BEANS.get(EthereumService.class).getWallet(accountAddress, accountPassword);
          BigDecimal gasCost = new BigDecimal(GAS_PRICE_DEFAULT.multiply(gasLimit));

          try {
            BEANS.get(EthereumService.class).ensureFunds(accountAddress, gasCost, Unit.WEI);
          }
          catch (Exception e) {
            throw new ExecutionException(e);
          }

          orderBookContract = load(contractAddress, account.getCredentials(), GAS_PRICE_DEFAULT, gasLimit);
        }
      }

      // check if loading via address from db was successful
      if (null == orderBookContract) {

        // still no order book: deploy a new contract for the testrpc case
        if (BEANS.get(EthereumService.class).isUseTestrpc()) {
          BigDecimal gasCost = new BigDecimal(GAS_PRICE_DEFAULT.multiply(gasLimit));

          try {
            BEANS.get(EthereumService.class).ensureFunds(Alice.CREDENTIALS.getAddress(), gasCost, Unit.WEI);
          }
          catch (Exception e) {
            throw new ExecutionException(e);
          }

          contractAddress = deploy(Alice.CREDENTIALS, GAS_PRICE_DEFAULT, gasLimit, currencyPair);
          orderBookContract = m_orderBookMap.get(currencyPair);
          saveContractAddressToDatabase(currencyPair, orderBookContract.getContractAddress());
        }

        // failed to deploy -> give up ...
        if (null == orderBookContract) {
          throw new ProcessingException("Could not load order book contract (currency pair=" + currencyPair + ")");
        }
      }

      m_orderBookMap.put(currencyPair, orderBookContract);
    }
    return orderBookContract;
  }

  public void removeContractFromCache(String currencyPair) {
    m_orderBookMap.remove(currencyPair);
  }

  private void saveContractAddressToDatabase(String currencyPair, String address) {
    SmartContractAdministrationFormData formData = new SmartContractAdministrationFormData();
    formData.getEnvironment().setValue(CONFIG.getPropertyValue(EthereumClientProperty.class));
    formData.getOrderBookType().setValue(currencyPair);
    formData.getAddress().setValue(address);

    BEANS.get(ISmartContractAdminstrationService.class).store(formData, true);
  }

  private String loadContractAddressFromDatabase(String currencyPair) {

    // check in config properties first
    if (OrderBookTypeCodeType.UsdEurCode.ID.equals(currencyPair)) {
      String contractAddress = CONFIG.getPropertyValue(EthereumClientContractAddressUsdEur.class);

      if (StringUtility.hasText(contractAddress)) {
        return contractAddress;
      }
    }

    // no default specified in config properties: check in database
    SmartContractAdministrationFormData formData = new SmartContractAdministrationFormData();
    formData.getEnvironment().setValue(CONFIG.getPropertyValue(EthereumClientProperty.class));
    formData.getOrderBookType().setValue(currencyPair);

    formData = BEANS.get(ISmartContractAdminstrationService.class).load(formData);

    return formData.getAddress().getValue();
  }

  private Order convertToOrder(List<Type> list) {
    Order order = null;
    if (null != list && list.size() > 0) {
      BigInteger quantity = ((Uint256) list.get(0)).getValue();
      BigInteger price = ((Uint256) list.get(1)).getValue();
      Boolean buy = ((Bool) list.get(2)).getValue();
      String ownerAddress = ((Address) list.get(3)).toString();
      BigInteger dealNr = ((Uint256) list.get(4)).getValue();
      BigInteger extId = ((Uint256) list.get(5)).getValue();
      Order.Type type = Order.Type.BUY;
      if (!buy) {
        type = Order.Type.SELL;
      }
      order = new Order(type, quantity.intValue(), price.doubleValue() / 100, extId.intValue());
      order.setId(dealNr.intValue());
      order.setOwner(ownerAddress);
    }
    return order;
  }
}
