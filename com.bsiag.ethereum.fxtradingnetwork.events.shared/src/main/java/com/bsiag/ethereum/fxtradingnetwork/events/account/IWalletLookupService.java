package com.bsiag.ethereum.fxtradingnetwork.events.account;

import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.lookup.ILookupService;

/**
 * <h3>{@link IWalletLookupService}</h3>
 *
 * @author mzi
 */
@TunnelToServer
public interface IWalletLookupService extends ILookupService<String> {
  void setPersonId(String personId);
}
