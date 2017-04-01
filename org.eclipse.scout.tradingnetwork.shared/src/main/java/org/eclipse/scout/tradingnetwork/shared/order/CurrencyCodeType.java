package org.eclipse.scout.tradingnetwork.shared.order;

import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCode;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCodeType;

public class CurrencyCodeType extends AbstractCodeType<String, String> {

  private static final long serialVersionUID = 1L;
  public static final String ID = "ISO4217";

  @Override
  public String getId() {
    return null;
  }

  @Order(1000)
  public static class EURCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "EUR";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("Euro");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(2000)
  public static class USDCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "USD";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("Dollar");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(3000)
  public static class GBPCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "GBP";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("Pfund");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(4000)
  public static class JPYCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "JPY";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("Yen");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(5000)
  public static class AUDCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "AUD";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("AustrallianDollar");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(6000)
  public static class CADCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "CAD";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("CanadianDollar");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(7000)
  public static class CHFCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "CHF";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("SwissFranc");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(8000)
  public static class CNYCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "CNY";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("ChineseYuan");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(9000)
  public static class SECCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "SEC";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("SweedischKronaKr");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(10000)
  public static class MXNCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "MXN";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("MexicanPeso");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(11000)
  public static class NZDCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "NZD";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("NewZealandDollar");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(12000)
  public static class SGDCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "SGD";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("SingaporeDollar");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

}
