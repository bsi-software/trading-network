package org.eclipse.scout.tradingnetwork.shared.order;

import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCode;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCodeType;

public class StatusCodeType extends AbstractCodeType<String, String> {

  private static final long serialVersionUID = 1L;
  public static final String ID = "Status";

  @Override
  public String getId() {
    return null;
  }

  @Order(1000)
  public static class InactiveCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "INACTIVE";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("Inactive");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(2000)
  public static class PendingCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "PENDING";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("Pending0");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(3000)
  public static class PublishedCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "PUBLISHED";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("Published");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(4000)
  public static class PartiallyCompletedCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "PARTIALLY";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("PartiallyCompleted");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

  @Order(5000)
  public static class CompletedCode extends AbstractCode<String> {
    private static final long serialVersionUID = 1L;
    public static final String ID = "COMPLETED";

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("Completed");
    }

    @Override
    public String getId() {
      return ID;
    }
  }

}
