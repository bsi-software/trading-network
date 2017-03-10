package com.bsiag.ethereum.fxtradingnetwork.events.server.event;

import java.util.List;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.TypeCastUtility;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.trading.network.Order;
import org.eclipse.scout.trading.network.OrderBookService;

import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.INetworkService;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.NetworkTablePageData;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.NetworkTablePageData.NetworkTableRowData;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.TradingActionCodeType;

public class NetworkService implements INetworkService {

  @Override
  public NetworkTablePageData getNetworkTableData(SearchFilter filter) {
    // TODO right order book
    List<Order> orders = BEANS.get(OrderBookService.class).getOrders("USDEUR");

    return convertToTablePageData(orders);
  }

  @Override
  public void executeMerge(Long dealId1, Long dealId2) {
    // TODO [uko]
  }

  private NetworkTablePageData convertToTablePageData(List<Order> orders) {
    NetworkTablePageData data = new NetworkTablePageData();

    for (Order order : orders) {
      NetworkTableRowData row = data.addRow();
      row.setExchangeRate(order.getPrice());
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
