package com.bsiag.ethereum.fxtradingnetwork.server.tradeingcenter;

import java.util.List;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.platform.util.TypeCastUtility;
import org.eclipse.scout.rt.server.clientnotification.ClientNotificationRegistry;
import org.eclipse.scout.rt.shared.services.common.code.ICode;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bsiag.ethereum.fxtradingnetwork.server.order.DealService;
import com.bsiag.ethereum.fxtradingnetwork.server.orderbook.OrderBookService;
import com.bsiag.ethereum.fxtradingnetwork.server.orderbook.model.Order;
import com.bsiag.ethereum.fxtradingnetwork.server.orderbook.model.OrderBookCache;
import com.bsiag.ethereum.fxtradingnetwork.server.orderbook.model.OrderMatch;
import com.bsiag.ethereum.fxtradingnetwork.shared.notification.OrderBookSynchronizedNotification;
import com.bsiag.ethereum.fxtradingnetwork.shared.order.OrderBookTypeCodeType;
import com.bsiag.ethereum.fxtradingnetwork.shared.order.TradingActionCodeType;
import com.bsiag.ethereum.fxtradingnetwork.shared.tradingcenter.INetworkService;
import com.bsiag.ethereum.fxtradingnetwork.shared.tradingcenter.NetworkTablePageData;
import com.bsiag.ethereum.fxtradingnetwork.shared.tradingcenter.NetworkTablePageData.NetworkTableRowData;

public class NetworkService implements INetworkService {

  private static final Logger LOG = LoggerFactory.getLogger(NetworkService.class);

  @Override
  public NetworkTablePageData getNetworkTableData(SearchFilter filter, String orderBookTypeId) {
    OrderBookService orderBookService = BEANS.get(OrderBookService.class);
    List<Order> orders = orderBookService.getOrders(orderBookTypeId);
    OrderMatch match = orderBookService.getMatch(orderBookTypeId);

    BEANS.get(DealService.class).checkStatusForPendingOrders(orderBookTypeId, orders);

    return convertToTablePageData(orders, match);
  }

  @Override
  public NetworkTablePageData getNetworkTableDataFromCache(SearchFilter filter, String orderBookTypeId) {
    List<Order> orders = BEANS.get(OrderBookCache.class).loadOrders(orderBookTypeId);
    OrderMatch match = BEANS.get(OrderBookCache.class).loadMatch(orderBookTypeId);

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
      BEANS.get(OrderBookCache.class).updateOrderBookCache(code.getId(), orders, match);
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
}
