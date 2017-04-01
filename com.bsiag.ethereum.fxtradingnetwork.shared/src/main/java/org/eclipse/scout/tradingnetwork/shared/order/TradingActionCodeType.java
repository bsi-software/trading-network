package org.eclipse.scout.tradingnetwork.shared.order;

import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCode;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCodeType;

public class TradingActionCodeType extends AbstractCodeType<String, String> {

  private static final long serialVersionUID = 1L;
  public static final String ID = "431736b7-fa43-4485-9745-594013bca826";

  @Override
  public String getId() {
    return ID;
  }

  @Order(1000)
  public static class BuyCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "BUY";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("Buy");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(2000)
  public static class SellCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "SELL";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("Sell");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

}
