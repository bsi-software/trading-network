package com.bsiag.ethereum.fxtradingnetwork.server.orderbook.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.util.StringUtility;

@ApplicationScoped
public class OrderBookCache implements Serializable {

  private static final long serialVersionUID = 1L;

  private ConcurrentHashMap<String, List<Order>> m_orderBookOrdersCache = new ConcurrentHashMap<String, List<Order>>();
  private ConcurrentHashMap<String, List<Order>> m_orderBookExecutedOrdersCache = new ConcurrentHashMap<String, List<Order>>();
  private ConcurrentHashMap<String, OrderMatch> m_orderBookMatchCache = new ConcurrentHashMap<String, OrderMatch>();

  public void updateOrderBookCache(String orderBookTypeId, List<Order> orders, OrderMatch match) throws ProcessingException {
    checkOrderBookId(orderBookTypeId);

    if (null != orders) {
      m_orderBookOrdersCache.put(orderBookTypeId, orders);
    }
    else {
      m_orderBookOrdersCache.remove(orderBookTypeId);
    }

    if (null != match) {
      m_orderBookMatchCache.put(orderBookTypeId, match);
    }
    else {
      m_orderBookMatchCache.remove(orderBookTypeId);
    }
  }

  public void updatedOrderBookExecutedCache(String orderBookTypeId, List<Order> orders) throws ProcessingException {
    checkOrderBookId(orderBookTypeId);

    if (null != orders) {
      m_orderBookExecutedOrdersCache.put(orderBookTypeId, orders);
    }
    else {
      m_orderBookExecutedOrdersCache.remove(orderBookTypeId);
    }
  }

  public List<Order> loadOrders(String orderBookTypeId) throws ProcessingException {
    checkOrderBookId(orderBookTypeId);

    List<Order> orders = m_orderBookOrdersCache.get(orderBookTypeId);
    if (null == orders) {
      orders = new ArrayList<Order>();
    }

    return orders;
  }

  public OrderMatch loadMatch(String orderBookTypeId) throws ProcessingException {
    checkOrderBookId(orderBookTypeId);

    return m_orderBookMatchCache.get(orderBookTypeId);
  }

  public List<Order> loadExecutedOrders(String orderBookTypeId) throws ProcessingException {
    checkOrderBookId(orderBookTypeId);

    List<Order> orders = m_orderBookExecutedOrdersCache.get(orderBookTypeId);
    if (null == orders) {
      orders = new ArrayList<Order>();
    }

    return orders;
  }

  private void checkOrderBookId(String orderBookTypeId) throws ProcessingException {
    if (StringUtility.isNullOrEmpty(orderBookTypeId)) {
      throw new ProcessingException("OrderBookTypeId is not allowed to be null or empty.");
    }
  }
}
