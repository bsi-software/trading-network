package org.eclipse.scout.tradingnetwork.shared.tradingcenter;

import java.security.BasicPermission;

public class ReadContractWeb3Permission extends BasicPermission {

  private static final long serialVersionUID = 1L;

  public ReadContractWeb3Permission() {
    super(ReadContractWeb3Permission.class.getSimpleName());
  }
}
