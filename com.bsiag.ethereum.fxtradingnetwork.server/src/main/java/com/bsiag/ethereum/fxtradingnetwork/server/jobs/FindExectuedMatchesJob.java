package com.bsiag.ethereum.fxtradingnetwork.server.jobs;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;

import com.bsiag.ethereum.fxtradingnetwork.server.order.DealService;

public class FindExectuedMatchesJob implements IRunnable {

  public static final String ID = "DealService.findExecutedMatches";

  @Override
  public void run() throws Exception {
    BEANS.get(DealService.class).checkForExecutedOrders();
  }

}
