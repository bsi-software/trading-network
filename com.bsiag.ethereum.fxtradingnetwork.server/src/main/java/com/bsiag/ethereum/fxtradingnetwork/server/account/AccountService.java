package com.bsiag.ethereum.fxtradingnetwork.server.account;

import java.io.File;
import java.math.BigDecimal;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.IOUtility;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bsiag.ethereum.fxtradingnetwork.server.account.model.Account;
import com.bsiag.ethereum.fxtradingnetwork.shared.account.AccountFormData;
import com.bsiag.ethereum.fxtradingnetwork.shared.account.AccountTablePageData;
import com.bsiag.ethereum.fxtradingnetwork.shared.account.AccountTablePageData.AccountTableRowData;
import com.bsiag.ethereum.fxtradingnetwork.shared.account.IAccountService;

public class AccountService implements IAccountService {

  private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

  @Override
  public AccountTablePageData getAccountTableData(SearchFilter filter, String personId) {
    AccountTablePageData pageData = new AccountTablePageData();

    BEANS.get(EthereumService.class).getWallets()
        .stream()
        .forEach(address -> {
          Account wallet = BEANS.get(EthereumService.class).getWallet(address);

          if (personId == null || personId.equals(wallet.getPersonId())) {
            addRow(wallet.getPersonId(), wallet.getName(), address, pageData);
          }
        });

    if (personId == null) {
      addRow(null, "Dummy 1", "0x0731F6b07eA5a2143E8EDd7C75E52a4f7d42E244", pageData);
      addRow(null, "Dummy 2", "0x61B2feE671a2f20E7ed04be9af076BeB356b0702", pageData);
    }

    return pageData;
  }

  private void addRow(String personId, String name, String address, AccountTablePageData pageData) {
    AccountTableRowData rowData = pageData.addRow();
    rowData.setPerson(personId);
    rowData.setAccountName(name);
    rowData.setAddress(address);

    try {
      BigDecimal balance = BEANS.get(EthereumService.class).getBalance(address);
      rowData.setBalance(balance);
    }
    catch (Exception e) {
      LOG.error("failed to fetch balance for account " + address, e);
    }
  }

  @Override
  public AccountFormData prepareCreate(AccountFormData formData) {
    // TODO [mzi] add business logic here.
    return formData;
  }

  @Override
  public AccountFormData create(AccountFormData formData) {
    String personId = formData.getPerson().getValue();
    String name = formData.getName().getValue();
    String password = formData.getPassword().getValue();

    Account wallet = new Account(personId, name, password, createWalletPath());
    BEANS.get(EthereumService.class).save(wallet);

    return formData;
  }

  private String createWalletPath() {
    try {
      File tmpFile = File.createTempFile("tmp", ".txt");
      return tmpFile.getParent();
    }
    catch (Exception e) {
      LOG.error("Failed to create path to temp file", e);
    }

    return null;
  }

  @Override
  public AccountFormData load(AccountFormData formData) {
    String address = formData.getAddress().getValue();
    Account wallet = BEANS.get(EthereumService.class).getWallet(address);
    String fileName = wallet.getFile().getAbsolutePath();
    String fileContent = new String(IOUtility.getContent(fileName));

    formData.getPerson().setValue(wallet.getPersonId());
    formData.getName().setValue(wallet.getName());
    formData.getFilePath().setValue(fileName);
    formData.getFileContent().setValue(fileContent);

    return formData;
  }

  @Override
  public AccountFormData store(AccountFormData formData) {
    String address = formData.getAddress().getValue();
    Account wallet = BEANS.get(EthereumService.class).getWallet(address);

    wallet.setName(formData.getName().getValue());
    BEANS.get(EthereumService.class).save(wallet);

    return formData;
  }

  @Override
  public String getPerson(String address) {
    return BEANS.get(EthereumService.class).getWallet(address).getPersonId();
  }
}
