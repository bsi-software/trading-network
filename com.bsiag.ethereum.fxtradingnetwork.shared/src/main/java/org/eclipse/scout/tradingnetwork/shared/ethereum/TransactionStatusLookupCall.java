package org.eclipse.scout.tradingnetwork.shared.ethereum;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.lookup.LocalLookupCall;
import org.eclipse.scout.rt.shared.services.lookup.LookupRow;

public class TransactionStatusLookupCall extends LocalLookupCall<Integer> {

  private static final long serialVersionUID = 1L;

  /**
   * see {@link Transaction} class on server.
   */
  public static final int ERROR = -2;
  public static final int REPLACED = -1;
  public static final int UNDEFINED = 0;
  public static final int OFFLINE = 1;
  public static final int PENDING = 2;
  public static final int CONFIRMED = 3;

  @Override
  protected List<LookupRow<Integer>> execCreateLookupRows() throws ProcessingException {
    ArrayList<LookupRow<Integer>> rows = new ArrayList<>();

    rows.add(new LookupRow<>(ERROR, TEXTS.get("TxStatusError")));
    rows.add(new LookupRow<>(REPLACED, TEXTS.get("TxStatusReplaced")));
    rows.add(new LookupRow<>(UNDEFINED, TEXTS.get("TxStatusUndefined")));
    rows.add(new LookupRow<>(OFFLINE, TEXTS.get("TxStatusOffline")));
    rows.add(new LookupRow<>(PENDING, TEXTS.get("TxStatusPending")));
    rows.add(new LookupRow<>(CONFIRMED, TEXTS.get("TxStatusConfirmed")));

    return rows;
  }
}
