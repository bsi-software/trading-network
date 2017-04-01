package org.eclipse.scout.tradingnetwork.server.ethereum;

import java.util.List;

import org.eclipse.scout.rt.server.services.lookup.AbstractLookupService;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;

import org.eclipse.scout.tradingnetwork.shared.ethereum.ITransactionStatusLookupService;

public class TransactionStatusLookupService extends AbstractLookupService<Integer> implements ITransactionStatusLookupService {

  @Override
  public List<? extends ILookupRow<Integer>> getDataByKey(ILookupCall<Integer> call) {
    // TODO [mzi] Auto-generated method stub.
    return null;
  }

  @Override
  public List<? extends ILookupRow<Integer>> getDataByRec(ILookupCall<Integer> call) {
    // TODO [mzi] Auto-generated method stub.
    return null;
  }

  @Override
  public List<? extends ILookupRow<Integer>> getDataByText(ILookupCall<Integer> call) {
    // TODO [mzi] Auto-generated method stub.
    return null;
  }

  @Override
  public List<? extends ILookupRow<Integer>> getDataByAll(ILookupCall<Integer> call) {
    // TODO [mzi] Auto-generated method stub.
    return null;
  }
}
