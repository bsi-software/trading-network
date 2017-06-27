package org.eclipse.scout.tradingnetwork.server.jobs;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.tradingnetwork.server.tradeingcenter.NetworkService;

public class ReloadOrdersFromOrderBookJob implements IRunnable {
//  private static final Logger LOG = LoggerFactory.getLogger(ReloadOrdersFromOrderBookJob.class);

  public static final String ID = "NetworkService.reloadOrdersFromOrderBook";

  @Override
  public void run() throws Exception {
    BEANS.get(NetworkService.class).synchronizeOrderBooks();
  }

}
