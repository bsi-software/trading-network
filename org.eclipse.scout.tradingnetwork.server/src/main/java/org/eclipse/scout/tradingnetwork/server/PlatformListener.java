package org.eclipse.scout.tradingnetwork.server;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.IPlatform.State;
import org.eclipse.scout.rt.platform.IPlatformListener;
import org.eclipse.scout.rt.platform.PlatformEvent;
import org.eclipse.scout.tradingnetwork.server.sql.DatabaseSetupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatformListener implements IPlatformListener {
  private static final Logger LOG = LoggerFactory.getLogger(PlatformListener.class);

  @Override
  public void stateChanged(PlatformEvent event) {
    if (event.getState() == State.BeanManagerValid) {

      // wait for ok from data store
      while (!BEANS.get(DatabaseSetupService.class).dataStoreIsReady()) {
        try {
          Thread.sleep(1000);
        }
        catch (InterruptedException e) {
          LOG.error("Unexpected exception: " + e.getMessage());
        }
      }

      JobUtility.registerJobs();
    }
  }

}
