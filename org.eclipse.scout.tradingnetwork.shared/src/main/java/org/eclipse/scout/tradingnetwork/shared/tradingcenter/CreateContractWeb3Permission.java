package org.eclipse.scout.tradingnetwork.shared.tradingcenter;

import java.security.BasicPermission;

public class CreateContractWeb3Permission extends BasicPermission {

  private static final long serialVersionUID = 1L;

  public CreateContractWeb3Permission() {
    super(CreateContractWeb3Permission.class.getSimpleName());
  }
}
