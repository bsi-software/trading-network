package com.bsiag.ethereum.fxtradingnetwork.server.jobs;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bsiag.ethereum.fxtradingnetwork.shared.tradingcenter.INetworkService;

public class ReloadOrdersFromOrderBookJob implements IRunnable {
  private static final Logger LOG = LoggerFactory.getLogger(ReloadOrdersFromOrderBookJob.class);

  public static final String ID = "NetworkService.reloadOrdersFromOrderBook";

  @Override
  public void run() throws Exception {
    BEANS.get(INetworkService.class).synchronizeOrderBooks();
  }

}
