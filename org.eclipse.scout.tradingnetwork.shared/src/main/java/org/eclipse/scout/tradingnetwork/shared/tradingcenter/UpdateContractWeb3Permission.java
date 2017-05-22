package org.eclipse.scout.tradingnetwork.shared.tradingcenter;

import java.security.BasicPermission;

public class UpdateContractWeb3Permission extends BasicPermission {

  private static final long serialVersionUID = 1L;

  public UpdateContractWeb3Permission() {
    super(UpdateContractWeb3Permission.class.getSimpleName());
  }
}
