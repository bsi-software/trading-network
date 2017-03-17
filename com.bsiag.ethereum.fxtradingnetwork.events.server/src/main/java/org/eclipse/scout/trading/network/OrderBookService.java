package org.eclipse.scout.trading.network;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.holders.StringHolder;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.bsiag.ethereum.fxtradingnetwork.events.account.Alice;
import com.bsiag.ethereum.fxtradingnetwork.events.account.EthereumService;
import com.bsiag.ethereum.fxtradingnetwork.events.server.OrderBook;

/**
 * Wrapper class for generated contract class. Keeps app code separate from smart contract code.
 */

@ApplicationScoped
public class OrderBookService {

  private static final Logger LOG = LoggerFactory.getLogger(EthereumService.class);

  private static final boolean USE_TESTRPC = false;

  public static final BigInteger GAS_PRICE_DEFAULT = BigInteger.valueOf(20_000_000_000L);
  public static final BigInteger GAS_LIMIT_DEFAULT = BigInteger.valueOf(40_000L);

  private Map<String, OrderBook> m_orderBookMap;
  private OrderBook m_contract;
  private int m_dealCounter = 1;

  @PostConstruct
  private void init() {
    if (null == m_orderBookMap) {
      m_orderBookMap = new HashMap<String, OrderBook>();
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

    LOG.info("contract successfully deployed at address" + contract.getContractAddress());

    saveContractAddressToDatabase(symbol, contract.getContractAddress());
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
   * @return the id of the new order. Empty String if error occurred .
   */
  public String publish(Order order) {
    //TODO return real dealNr. Check if order contains dealNr => not allowed
    int dealNr = -1;
    String dealNrString = "";
    Bool buy = new Bool(order.isBuy());
    Uint256 dealQuantity = new Uint256(BigInteger.valueOf(order.getAmount()));
    Uint256 dealPrice = new Uint256(BigInteger.valueOf((long) (100 * order.getPrice())));
    try {
      TransactionReceipt receipt = getContract(order.getCurrencyPair()).createOrder(dealQuantity, dealPrice, buy).get();
      //TODO actual orderId. Change contract...
      dealNr = m_dealCounter;
      m_dealCounter = m_dealCounter + 1;
      dealNrString = "" + dealNr;
    }
    catch (InterruptedException | ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return dealNrString;
  }

  /**
   * Gets the specified order.
   *
   * @param int
   *          order id
   * @return the order of it exists in the order book. null otherwise.
   */
  public Order get(String order) {
    // TODO implement
    if (StringUtility.hasText(order)) {
      String currencyPair = order.substring(0, 5);
      int dealNr = Integer.valueOf(order.substring(6));
    }
    return null;
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
      matchExists = contract.matchExists().get();
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
    return match;
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

  private OrderBook getContract(String currencyPair) throws InterruptedException, ExecutionException {
    // TODO change behavior depending on environment.
    OrderBook contract = m_orderBookMap.get(currencyPair);
    if (null == contract) {
      String address = loadContractAddressFromDatabase(currencyPair);
      if (StringUtility.hasText(address)) {
        contract = load(address, Alice.CREDENTIALS, GAS_PRICE_DEFAULT, BigInteger.valueOf(4_000_000L));
      }
      if (null == contract) {
        deploy(Alice.CREDENTIALS, GAS_PRICE_DEFAULT, BigInteger.valueOf(4_000_000L), currencyPair);
        contract = m_orderBookMap.get(currencyPair);
      }
      else {
        m_orderBookMap.put(currencyPair, contract);
      }
    }
    return contract;
  }

  private void saveContractAddressToDatabase(String currencyPair, String address) {
    //TODO change environment to config variable
    int updateResult = SQL.update(""
        + " UPDATE DEPLOYED_ORDER_BOOK "
        + " SET ADDRESS = :address "
        + " WHERE ORDER_BOOK_TYPE = :orderBookType "
        + "   AND ENVIRONMENT = :environment ",
        new NVPair("address", address),
        new NVPair("orderBookType", currencyPair),
        new NVPair("environment", "TESTRPC"));
    if (updateResult == 0) {
      SQL.insert("INSERT INTO DEPLOYED_ORDER_BOOK (ENVIRONMENT, ORDER_BOOK_TYPE, ADDRESS) "
          + " VALUES (:environment, :orderBookType, :address) ",
          new NVPair("address", address),
          new NVPair("orderBookType", currencyPair),
          new NVPair("environment", "TESTRPC"));
    }
  }

  private String loadContractAddressFromDatabase(String currencyPair) {
    StringHolder address = new StringHolder();
    SQL.selectInto(""
        + " SELECT OB.ADDRESS FROM DEPLOYED_ORDER_BOOK OB "
        + " WHERE OB.ORDER_BOOK_TYPE = :orderBookType "
        + "   AND OB.ENVIRONMENT = :environment "
        + " LIMIT 1 "
        + " INTO :address ",
        new NVPair("address", address),
        new NVPair("orderBookType", currencyPair),
        new NVPair("environment", "TESTRPC"));

    return address.getValue();
  }

  private Order convertToOrder(List<Type> list) {
    Order order = null;
    if (null != list && list.size() > 0) {
      BigInteger quantity = ((Uint256) list.get(0)).getValue();
      BigInteger price = ((Uint256) list.get(1)).getValue();
      Boolean buy = ((Bool) list.get(2)).getValue();
      // TODO check whats wrong here
      String company;
//    company = ((UTF8String) list.get(3)).get();// toString();
      BigInteger dealNr = ((Uint256) list.get(4)).getValue();
      Order.Type type = Order.Type.BUY;
      if (!buy) {
        type = Order.Type.SELL;
      }
      order = new Order(type, quantity.intValue(), price.doubleValue() / 100);
      order.setId(dealNr.intValue());
    }
    return order;
  }
}
