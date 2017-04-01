package org.eclipse.scout.tradingnetwork.server;

import org.eclipse.scout.rt.platform.IPlatform.State;
import org.eclipse.scout.rt.platform.IPlatformListener;
import org.eclipse.scout.rt.platform.PlatformEvent;

public class PlatformListener implements IPlatformListener {

  @Override
  public void stateChanged(PlatformEvent event) {
    if (event.getState() == State.BeanManagerValid) {
      JobUtility.registerJobs();
    }
  }

}
