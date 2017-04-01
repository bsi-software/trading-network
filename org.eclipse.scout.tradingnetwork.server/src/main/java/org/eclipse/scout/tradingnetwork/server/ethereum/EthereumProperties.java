package org.eclipse.scout.tradingnetwork.server.ethereum;

import org.eclipse.scout.rt.platform.config.AbstractStringConfigProperty;

public class EthereumProperties {

  public static class EthereumClientIpProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return "fxtradingnetwork.ethereum.client.ip";
    }

  }

  public static class EthereumClientPortProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return "fxtradingnetwork.ethereum.client.port";
    }

  }

  public static class EthereumClientProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return "fxtradingnetwork.ethereum.client";
    }

    @Override
    protected String getDefaultValue() {
      return "TESTNET";
    }

  }

}
