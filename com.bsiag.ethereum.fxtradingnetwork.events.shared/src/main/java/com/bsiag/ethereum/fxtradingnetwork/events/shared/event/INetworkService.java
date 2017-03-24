package com.bsiag.ethereum.fxtradingnetwork.events.shared.event;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

/**
 * <h3>{@link INetworkService}</h3>
 *
 * @author uko
 */
@ApplicationScoped
@TunnelToServer
public interface INetworkService extends IService {

  NetworkTablePageData getNetworkTableData(SearchFilter filter, String orderBookTypeId);

  NetworkTablePageData getNetworkTableDataFromCache(SearchFilter filter, String orderBookTypeId);

  void executeMerge(String orderBookTypeId, Long dealId1, Long dealId2);

  void synchronizeOrderBooks();
}
