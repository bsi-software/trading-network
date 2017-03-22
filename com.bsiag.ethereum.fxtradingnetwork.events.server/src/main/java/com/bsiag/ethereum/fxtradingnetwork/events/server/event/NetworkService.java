package com.bsiag.ethereum.fxtradingnetwork.events.server.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.platform.util.TypeCastUtility;
import org.eclipse.scout.rt.server.clientnotification.ClientNotificationRegistry;
import org.eclipse.scout.rt.shared.services.common.code.ICode;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.trading.network.Order;
import org.eclipse.scout.trading.network.OrderBookService;
import org.eclipse.scout.trading.network.OrderMatch;

import com.bsiag.ethereum.fxtradingnetwork.events.shared.OrderBookTypeCodeType;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.INetworkService;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.NetworkTablePageData;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.NetworkTablePageData.NetworkTableRowData;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.notification.OrderBookSynchronizedNotification;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.TradingActionCodeType;

public class NetworkService implements INetworkService {

  private static final Object SYNC_OBJECT = new Object();
  private Map<String, List<Order>> m_orderBookOrdersCache = new HashMap<String, List<Order>>();
  private Map<String, OrderMatch> m_orderBookMatchCache = new HashMap<String, OrderMatch>();

  @Override
  public NetworkTablePageData getNetworkTableData(SearchFilter filter, String orderBookTypeId) {
    return getNetworkTableData(filter, orderBookTypeId, false);
  }

  @Override
  public NetworkTablePageData getNetworkTableData(SearchFilter filter, String orderBookTypeId, boolean useCache) {
    List<Order> orders = null;
    OrderMatch match = null;
    if (useCache) {
      orders = m_orderBookOrdersCache.get(orderBookTypeId);
      match = m_orderBookMatchCache.get(orderBookTypeId);
    }

    if (null == orders) {
      OrderBookService orderBookService = BEANS.get(OrderBookService.class);
      orders = orderBookService.getOrders(orderBookTypeId);
      match = orderBookService.getMatch(orderBookTypeId);
    }
    return convertToTablePageData(orders, match);
  }

  @Override
  public void executeMerge(String orderBookTypeId, Long dealId1, Long dealId2) {
    BEANS.get(OrderBookService.class).executeMatch(orderBookTypeId, dealId1.intValue(), dealId2.intValue());
  }

  @Override
  public void synchronizeOrderBooks() {
    for (ICode<String> code : BEANS.get(OrderBookTypeCodeType.class).getCodes()) {
      OrderBookService orderBookService = BEANS.get(OrderBookService.class);
      List<Order> orders = orderBookService.getOrders(code.getId());
      OrderMatch match = orderBookService.getMatch(code.getId());
      updateOrdersAndMatchCache(code.getId(), orders, match);
      BEANS.get(ClientNotificationRegistry.class).putForAllSessions(new OrderBookSynchronizedNotification(code.getId()));
    }
  }

  private NetworkTablePageData convertToTablePageData(List<Order> orders, OrderMatch match) {
    NetworkTablePageData data = new NetworkTablePageData();

    for (Order order : orders) {
      NetworkTableRowData row = data.addRow();
      row.setExchangeRate(order.getPrice());
      row.setDealId(TypeCastUtility.castValue(order.getId(), Long.class));
      //TODO is own order
      if (null != match && CompareUtility.isOneOf(order.getId(), match.getBuyNr(), match.getSellNr())) {
        row.setIsMatched(true);
      }
      if (order.isBuy()) {
        row.setBuyerQuantity(TypeCastUtility.castValue(order.getAmount(), Long.class));
        row.setBuyerSide(TradingActionCodeType.BuyCode.ID);
        row.setSide(TradingActionCodeType.BuyCode.ID);
      }
      else {
        row.setSellerQuantity(TypeCastUtility.castValue(order.getAmount(), Long.class));
        row.setSellerSide(TradingActionCodeType.SellCode.ID);
        row.setSide(TradingActionCodeType.SellCode.ID);
      }
    }

    return data;
  }

  private void updateOrdersAndMatchCache(String orderBookTypeId, List<Order> orders, OrderMatch match) {
    synchronized (SYNC_OBJECT) {
      m_orderBookOrdersCache.put(orderBookTypeId, orders);
      m_orderBookMatchCache.put(orderBookTypeId, match);
    }
  }
}
