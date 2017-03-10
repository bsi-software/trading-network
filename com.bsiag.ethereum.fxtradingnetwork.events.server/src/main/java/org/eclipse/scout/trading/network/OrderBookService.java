package org.eclipse.scout.trading.network;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.bsiag.ethereum.fxtradingnetwork.events.account.Alice;
import com.bsiag.ethereum.fxtradingnetwork.events.account.EthereumService;
import com.bsiag.ethereum.fxtradingnetwork.events.server.FXTrading;

/**
 * Wrapper class for generated contract class. Keeps app code separate from smart contract code.
 */

@ApplicationScoped
public class OrderBookService {

  private static final Logger LOG = LoggerFactory.getLogger(EthereumService.class);

  public static final BigInteger GAS_PRICE_DEFAULT = BigInteger.valueOf(20_000_000_000L);
  public static final BigInteger GAS_LIMIT_DEFAULT = BigInteger.valueOf(40_000L);

  private FXTrading m_contract;
  private int m_dealCounter = 1;

  /**
   * Deploys the order book service contract. The method blocks until the contract is deployed.
   *
   * @return address of the deployed contract
   * @throws ExecutionException
   * @throws InterruptedException
   */
  public String deploy(Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String symbol)
      throws InterruptedException, ExecutionException {
    m_contract = FXTrading
        .deploy(getWeb3j(), credentials, gasPrice, gasLimit, BigInteger.valueOf(0), new Utf8String(symbol))
        .get();

    LOG.info("contract successfully deployed at address" + m_contract.getContractAddress());

    return m_contract.getContractAddress();
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
    Utf8String owner = new Utf8String(order.getOwner());
    Bool buy = new Bool(order.isBuy());
    Uint256 dealQuantity = new Uint256(BigInteger.valueOf(order.getAmount()));
    Uint256 dealPrice = new Uint256(BigInteger.valueOf((long) (100 * order.getPrice())));
    try {
      TransactionReceipt receipt = getContract(order.getCurrencyPair()).createDeal(dealQuantity, dealPrice, buy, owner).get();
      dealNr = m_dealCounter;
      m_dealCounter = m_dealCounter + 1;
      dealNrString = order.getCurrencyPair() + dealNr;
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
  private List<Order> getBuyOrders(FXTrading contract) {
    return getOrders(true, contract);
  }

  /**
   * @param contract
   * @return
   */
  private List<Order> getSellOrders(FXTrading contract) {
    return getOrders(false, contract);
  }

  /**
   * @param buy
   * @param contract
   * @return
   */
  private List<Order> getOrders(boolean buy, FXTrading contract) {
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
    return orders;
  }

  /**
   * @param buy
   * @param index
   * @param contract
   * @return
   */
  private Order getOrderAtIndex(boolean buy, int index, FXTrading contract) {
    List<Type> list = null;
    if (buy) {
      try {
        list = contract.buyDeals(new Uint256(BigInteger.valueOf(index))).get();
      }
      catch (InterruptedException | ExecutionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    else {
      try {
        list = contract.sellDeals(new Uint256(BigInteger.valueOf(index))).get();
      }
      catch (InterruptedException | ExecutionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
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

  private FXTrading getContract(String currencyPair) throws InterruptedException, ExecutionException {
    if (null == m_contract) {
      deploy(Alice.CREDENTIALS, GAS_PRICE_DEFAULT, BigInteger.valueOf(2_000_000L), currencyPair);
    }
    return m_contract;
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
      order = new Order(type, quantity.intValue() / 100, price.doubleValue());
      order.setId(dealNr.intValue());
    }
    return order;

  }
}
