package com.bsiag.ethereum.fxtradingnetwork.server.account;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.server.services.lookup.AbstractLookupService;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;
import org.eclipse.scout.rt.shared.services.lookup.LookupRow;

import com.bsiag.ethereum.fxtradingnetwork.server.account.model.Account;
import com.bsiag.ethereum.fxtradingnetwork.shared.account.IWalletLookupService;

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

    Account wallet = BEANS.get(EthereumService.class).getWallet(call.getKey());
    rows.add(new LookupRow<>(wallet.getAddress(), wallet.getName()));

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

    for (String address : BEANS.get(EthereumService.class).getWallets(getPersonId())) {
      Account wallet = BEANS.get(EthereumService.class).getWallet(address);
      if (wallet.getName().toLowerCase().contains(searchText)) {
        rows.add(new LookupRow<>(wallet.getAddress(), wallet.getName()));
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

    BEANS.get(EthereumService.class).getWallets(getPersonId())
        .stream()
        .forEach(address -> {
          Account wallet = BEANS.get(EthereumService.class).getWallet(address);
          rows.add(new LookupRow<>(wallet.getAddress(), wallet.getName()));
        });

    return rows;
  }
}
