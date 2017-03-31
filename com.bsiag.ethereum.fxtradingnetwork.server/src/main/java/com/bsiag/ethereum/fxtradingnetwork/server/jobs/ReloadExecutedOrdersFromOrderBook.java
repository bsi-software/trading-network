package com.bsiag.ethereum.fxtradingnetwork.server.jobs;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;

import com.bsiag.ethereum.fxtradingnetwork.server.tradeingcenter.NetworkService;

public class ReloadExecutedOrdersFromOrderBook implements IRunnable {

  public static final String ID = "NetworkService.reloadExecutedOrdersFromOrderBook";

  @Override
  public void run() throws Exception {
    BEANS.get(NetworkService.class).synchronizeExecutedOrdersFromOrderBooks();
  }

}
