package org.eclipse.scout.tradingnetwork.server.ethereum;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.server.services.lookup.AbstractLookupService;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;
import org.eclipse.scout.rt.shared.services.lookup.LookupRow;
import org.eclipse.scout.tradingnetwork.shared.ethereum.AccountFormData;
import org.eclipse.scout.tradingnetwork.shared.ethereum.AccountTablePageData;
import org.eclipse.scout.tradingnetwork.shared.ethereum.AccountTablePageData.AccountTableRowData;
import org.eclipse.scout.tradingnetwork.shared.ethereum.IAccountService;
import org.eclipse.scout.tradingnetwork.shared.ethereum.IWalletLookupService;

public class WalletLookupService extends AbstractLookupService<String> implements IWalletLookupService {

  private String personId = null;

  public String getPersonId() {
    return personId;
  }

  @Override
  public void setPersonId(String personId) {
    this.personId = personId;
  }

  @Override
  public List<? extends ILookupRow<String>> getDataByKey(ILookupCall<String> call) {
    ArrayList<LookupRow<String>> rows = new ArrayList<>();
    AccountFormData formData = BEANS.get(IAccountService.class).load(call.getKey());

    rows.add(new LookupRow<>(formData.getAddress().getValue(), formData.getName().getValue()));

    return rows;
  }

  @Override
  public List<? extends ILookupRow<String>> getDataByRec(ILookupCall<String> call) {
    // TODO [mzi] Auto-generated method stub.
    return null;
  }

  @Override
  public List<? extends ILookupRow<String>> getDataByText(ILookupCall<String> call) {
    ArrayList<LookupRow<String>> rows = new ArrayList<>();
    String searchText = getSearchText(call);
    AccountTablePageData pageData = BEANS.get(IAccountService.class).getAccountTableData(new SearchFilter(), getPersonId());

    for (AccountTableRowData row : pageData.getRows()) {
      if (StringUtility.containsStringIgnoreCase(row.getAccountName(), searchText)) {
        rows.add(new LookupRow<>(row.getAddress(), row.getAccountName()));
      }
    }

    return rows;
  }

  private String getSearchText(ILookupCall<String> call) {
    String searchText = call.getText().toLowerCase();

    if (searchText.endsWith("*")) {
      searchText = searchText.substring(0, searchText.length() - 1);
    }

    return searchText;
  }

  @Override
  public List<? extends ILookupRow<String>> getDataByAll(ILookupCall<String> call) {
    ArrayList<LookupRow<String>> rows = new ArrayList<>();
    AccountTablePageData pageData = BEANS.get(IAccountService.class).getAccountTableData(new SearchFilter(), getPersonId());

    for (AccountTableRowData row : pageData.getRows()) {
      rows.add(new LookupRow<>(row.getAddress(), row.getAccountName()));
    }

    return rows;
  }
}
