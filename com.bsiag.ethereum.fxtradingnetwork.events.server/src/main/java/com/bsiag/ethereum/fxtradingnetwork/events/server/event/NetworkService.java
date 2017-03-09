package com.bsiag.ethereum.fxtradingnetwork.events.server.event;

import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.INetworkService;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.NetworkTablePageData;

public class NetworkService implements INetworkService {

  @Override
  public NetworkTablePageData getNetworkTableData(SearchFilter filter) {
    NetworkTablePageData pageData = new NetworkTablePageData();
    // TODO [uko] fill pageData from SmartContract and Database
    return pageData;
  }

  @Override
  public void executeMerge(Long dealId1, Long dealId2) {
    // TODO [uko]
  }
}
