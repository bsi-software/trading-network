package com.bsiag.ethereum.fxtradingnetwork.events.server.event;

import java.util.List;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.platform.util.TypeCastUtility;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.trading.network.Order;
import org.eclipse.scout.trading.network.OrderBookService;
import org.eclipse.scout.trading.network.OrderMatch;

import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.INetworkService;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.NetworkTablePageData;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.NetworkTablePageData.NetworkTableRowData;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.TradingActionCodeType;

public class NetworkService implements INetworkService {

  @Override
  public NetworkTablePageData getNetworkTableData(SearchFilter filter, String orderBookTypeId) {
    OrderBookService orderBookService = BEANS.get(OrderBookService.class);
    List<Order> orders = orderBookService.getOrders(orderBookTypeId);
    OrderMatch match = orderBookService.getMatch(orderBookTypeId);

    NetworkTablePageData pageData = convertToTablePageData(orders, match);

    return pageData;
  }

  @Override
  public void executeMerge(Long dealId1, Long dealId2) {
    // TODO [uko]
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
