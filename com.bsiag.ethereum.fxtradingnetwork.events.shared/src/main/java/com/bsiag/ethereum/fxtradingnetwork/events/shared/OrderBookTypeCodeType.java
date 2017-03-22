package com.bsiag.ethereum.fxtradingnetwork.events.shared;

import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCode;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCodeType;

public class OrderBookTypeCodeType extends AbstractCodeType<String, String> {

  private static final long serialVersionUID = 1L;
  private static final String ID = "ca9b65cb-924e-4582-abe9-0a7ac02cd371";

  public static enum NotificationEnum {
    USDEUR,
    EURJPY,
    GBPEUR,
    USDJPY
  }

  @Override
  public String getId() {
    return ID;
  }

  @Order(1000)
  public static class UsdEurCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "USDEUR";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("USD-EUR");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(2000)
  public static class EurJpyCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "EURJPY";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("EURJPY");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(3000)
  public static class GbpEurCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "GBPEUR";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("GBP-EUR");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(4000)
  public static class UsdJpyCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "USDJPY";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("USD-JPY");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

}
