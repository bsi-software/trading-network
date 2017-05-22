package org.eclipse.scout.tradingnetwork.server.ethereum;

import org.eclipse.scout.rt.platform.config.AbstractStringConfigProperty;
import org.eclipse.scout.tradingnetwork.server.ethereum.model.Alice;

public class EthereumProperties {

  public static class EthereumClientContractAddressUsdEur extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return "fxtradingnetwork.ethereum.contract.address.usdeur";
    }

  }

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

  public static class EthereumWalletLocation extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return "fxtradingnetwork.ethereum.wallet.location";
    }

  }

  public static class EthereumDefaultAccount extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return "fxtradingnetwork.ethereum.default.account";
    }

    @Override
    public String getValue() {
      try {
        return Alice.CREDENTIALS.getAddress();
      }
      catch (Exception e) {
        return null;
      }
    }

  }

}
