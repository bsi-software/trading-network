package com.bsiag.ethereum.fxtradingnetwork.events.shared.notification;

import java.io.Serializable;

public class OrderBookSynchronizedNotification implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String m_orderBookTypeId;

  public OrderBookSynchronizedNotification(String orderBookTypeId) {
    m_orderBookTypeId = orderBookTypeId;
  }

  public String getOrderBookTypeId() {
    return m_orderBookTypeId;
  }

}
