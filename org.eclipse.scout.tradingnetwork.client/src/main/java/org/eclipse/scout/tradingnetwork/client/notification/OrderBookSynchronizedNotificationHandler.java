package org.eclipse.scout.tradingnetwork.client.notification;

import org.eclipse.scout.rt.shared.notification.INotificationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.scout.tradingnetwork.client.Desktop;
import org.eclipse.scout.tradingnetwork.shared.notification.OrderBookSynchronizedNotification;
import org.eclipse.scout.tradingnetwork.shared.order.OrderBookTypeCodeType;

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
