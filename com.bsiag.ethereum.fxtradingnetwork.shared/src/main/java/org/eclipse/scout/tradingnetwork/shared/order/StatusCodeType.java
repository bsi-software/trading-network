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
    public static final String ID = "7edf08d0-1423-400f-8e20-b022694aa0ea";

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
    public static final String ID = "dc010b18-9598-4031-89e6-70924d0589b5";

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
    public static final String ID = "9750ea5d-cec9-48a8-a837-522f4395c800";

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
    public static final String ID = "ce83dd7a-be69-4679-8bf5-f39e96971b0b";

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
    public static final String ID = "3ca92b83-3b67-4e10-aedb-56cf0d8a2fd3";

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
