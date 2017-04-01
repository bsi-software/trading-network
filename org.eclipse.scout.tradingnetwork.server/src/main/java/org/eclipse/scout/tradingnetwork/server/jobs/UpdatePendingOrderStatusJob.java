package org.eclipse.scout.tradingnetwork.server.jobs;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;

import org.eclipse.scout.tradingnetwork.server.order.DealService;

public class UpdatePendingOrderStatusJob implements IRunnable {

  public static final String ID = "DealService.updatePendingOrders";

  @Override
  public void run() throws Exception {
    BEANS.get(DealService.class).checkStatusForPendingOrders();
  }

}
