package com.bsiag.ethereum.fxtradingnetwork.server.orderbook.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.util.StringUtility;

@ApplicationScoped
public class OrderBookCache implements Serializable {

  private static final long serialVersionUID = 1L;

  private ConcurrentHashMap<String, List<Order>> m_orderBookOrdersCache = new ConcurrentHashMap<String, List<Order>>();
  private ConcurrentHashMap<String, OrderMatch> m_orderBookMatchCache = new ConcurrentHashMap<String, OrderMatch>();

  public void updateOrderBookCache(String orderBookTypeId, List<Order> orders, OrderMatch match) throws NullPointerException {
    if (StringUtility.isNullOrEmpty(orderBookTypeId)) {
      throw new NullPointerException("OrderBookTypeId is not allowed to be null or empty.");
    }

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

  public List<Order> loadOrders(String orderBookTypeId) throws NullPointerException {
    if (StringUtility.isNullOrEmpty(orderBookTypeId)) {
      throw new NullPointerException("OrderBookTypeId is not allowed to be null or empty.");
    }

    List<Order> orders = m_orderBookOrdersCache.get(orderBookTypeId);
    if (null == orders) {
      orders = new ArrayList<Order>();
    }

    return orders;
  }

  public OrderMatch loadMatch(String orderBookTypeId) throws NullPointerException {
    if (StringUtility.isNullOrEmpty(orderBookTypeId)) {
      throw new NullPointerException("OrderBookTypeId is not allowed to be null or empty.");
    }

    return m_orderBookMatchCache.get(orderBookTypeId);
  }
}
