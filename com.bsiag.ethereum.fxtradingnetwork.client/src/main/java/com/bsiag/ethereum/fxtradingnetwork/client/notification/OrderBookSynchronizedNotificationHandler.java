package com.bsiag.ethereum.fxtradingnetwork.client.notification;

import org.eclipse.scout.rt.shared.notification.INotificationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bsiag.ethereum.fxtradingnetwork.client.Desktop;
import com.bsiag.ethereum.fxtradingnetwork.shared.notification.OrderBookSynchronizedNotification;
import com.bsiag.ethereum.fxtradingnetwork.shared.order.OrderBookTypeCodeType;

public class OrderBookSynchronizedNotificationHandler implements INotificationHandler<OrderBookSynchronizedNotification> {
  private static final Logger LOG = LoggerFactory.getLogger(OrderBookSynchronizedNotificationHandler.class);

  @Override
  public void handleNotification(OrderBookSynchronizedNotification notification) {
    try {
      OrderBookTypeCodeType.NotificationEnum changeObject = OrderBookTypeCodeType.NotificationEnum.valueOf(notification.getOrderBookTypeId());
      Desktop.get().dataChanged(changeObject);
    }
    catch (Exception e) {
      LOG.error("Error for OrderBook: " + notification.getOrderBookTypeId(), e);
    }
  }

}
