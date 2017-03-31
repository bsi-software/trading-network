package com.bsiag.ethereum.fxtradingnetwork.shared.account;

import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCode;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCodeType;

/**
 * <h3>{@link EthereumClientCodeType}</h3>
 *
 * @author uko
 */
public class EthereumClientCodeType extends AbstractCodeType<String, String> {

  private static final long serialVersionUID = 1L;
  public static final String ID = "62384c32-57dd-4516-955c-8fe78c89a698";

  @Override
  public String getId() {
    return ID;
  }

  @Order(1000)
  public static class TestRpcCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "TESTRPC";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("TESTRPC");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(2000)
  public static class TestnetCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "TESTNET";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("TESTNET");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(3000)
  public static class MainNetCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "MAINNET";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("MAINNET");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

}
