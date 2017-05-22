package org.eclipse.scout.tradingnetwork.server.tradeingcenter;

import java.util.List;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.platform.util.TypeCastUtility;
import org.eclipse.scout.rt.server.clientnotification.ClientNotificationRegistry;
import org.eclipse.scout.rt.shared.ISession;
import org.eclipse.scout.rt.shared.services.common.code.ICode;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.session.Sessions;
import org.eclipse.scout.tradingnetwork.server.order.DealService;
import org.eclipse.scout.tradingnetwork.server.orderbook.OrderBookService;
import org.eclipse.scout.tradingnetwork.server.orderbook.model.Order;
import org.eclipse.scout.tradingnetwork.server.orderbook.model.OrderBookCache;
import org.eclipse.scout.tradingnetwork.server.orderbook.model.OrderMatch;
import org.eclipse.scout.tradingnetwork.shared.notification.OrderBookSynchronizedNotification;
import org.eclipse.scout.tradingnetwork.shared.order.DealFormData;
import org.eclipse.scout.tradingnetwork.shared.order.IDealService;
import org.eclipse.scout.tradingnetwork.shared.order.OrderBookTypeCodeType;
import org.eclipse.scout.tradingnetwork.shared.order.TradingActionCodeType;
import org.eclipse.scout.tradingnetwork.shared.organization.IOrganizationService;
import org.eclipse.scout.tradingnetwork.shared.tradingcenter.INetworkService;
import org.eclipse.scout.tradingnetwork.shared.tradingcenter.NetworkTablePageData;
import org.eclipse.scout.tradingnetwork.shared.tradingcenter.NetworkTablePageData.NetworkTableRowData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    BEANS.get(DealService.class).checkStatusForPendingOrders(orderBookTypeId, orders);

    return convertToTablePageData(orders, match);
  }

  @Override
  public void executeMerge(String orderBookTypeId, Long dealId1, Long dealId2) {
    BEANS.get(OrderBookService.class).executeMatch(orderBookTypeId, dealId1.intValue(), dealId2.intValue());
  }

  public void synchronizeOrderBooks() {
    for (ICode<String> code : BEANS.get(OrderBookTypeCodeType.class).getCodes()) {
      try {
        OrderBookService orderBookService = BEANS.get(OrderBookService.class);
        List<Order> orders = orderBookService.getOrders(code.getId());
        OrderMatch match = orderBookService.getMatch(code.getId());
        BEANS.get(OrderBookCache.class).updateOrderBookCache(code.getId(), orders, match);
        BEANS.get(ClientNotificationRegistry.class).putForAllSessions(new OrderBookSynchronizedNotification(code.getId()));
      }
      catch (Exception e) {
        // nop
        LOG.error(e.getMessage());
      }
    }
  }

  public void synchronizeExecutedOrdersFromOrderBooks() {
    for (ICode<String> code : BEANS.get(OrderBookTypeCodeType.class).getCodes()) {
      try {
        OrderBookService orderBookService = BEANS.get(OrderBookService.class);
        List<Order> orders = orderBookService.getExecutedOrders(code.getId());
        BEANS.get(OrderBookCache.class).updatedOrderBookExecutedCache(code.getId(), orders);
      }
      catch (Exception e) {
        // nop
        LOG.error(e.getMessage());
      }
    }
  }

  private NetworkTablePageData convertToTablePageData(List<Order> orders, OrderMatch match) {
    NetworkTablePageData data = new NetworkTablePageData();
    String userId = Sessions.currentSession(ISession.class).getUserId();
    String ownOrganization = BEANS.get(IOrganizationService.class).getOrganizationIdForUser(userId);

    for (Order order : orders) {
      NetworkTableRowData row = data.addRow();
      row.setExchangeRate(order.getPrice());
      row.setDealId(TypeCastUtility.castValue(order.getId(), Long.class));
      String organizationId = "";
      if (StringUtility.hasText(ownOrganization)) {
        DealFormData dealData = BEANS.get(IDealService.class).loadByDealNr(order.getCurrencyPair(), row.getDealId());
        if (null != dealData) {
          organizationId = dealData.getOrganizationId();
          row.setOwnDeal(ownOrganization.equals(dealData.getOrganizationId()));
        }
      }
      if (null != match && CompareUtility.isOneOf(order.getId(), match.getBuyNr(), match.getSellNr())) {
        row.setIsMatched(true);
      }
      if (order.isBuy()) {
        row.setBuyerQuantity(TypeCastUtility.castValue(order.getAmount(), Long.class));
        row.setBuyerSide(TradingActionCodeType.BuyCode.ID);
        row.setSide(TradingActionCodeType.BuyCode.ID);
        row.setBuyerOrganization(organizationId);
      }
      else {
        row.setSellerQuantity(TypeCastUtility.castValue(order.getAmount(), Long.class));
        row.setSellerSide(TradingActionCodeType.SellCode.ID);
        row.setSide(TradingActionCodeType.SellCode.ID);
        row.setSellerOrganization(organizationId);
      }
    }

    return data;

  }

  @Override
  public String getContractAddress(String orderBookTypeId) {
    return BEANS.get(OrderBookService.class).getContractAddress(orderBookTypeId);
  }

}
