package com.bsiag.ethereum.fxtradingnetwork.events.shared.event;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

/**
 * <h3>{@link INetworkService}</h3>
 *
 * @author uko
 */
@TunnelToServer
public interface INetworkService extends IService {

  /**
   * @param filter
   * @return
   */
  NetworkTablePageData getNetworkTableData(SearchFilter filter);

  void executeMerge(Long dealId1, Long dealId2);
}
