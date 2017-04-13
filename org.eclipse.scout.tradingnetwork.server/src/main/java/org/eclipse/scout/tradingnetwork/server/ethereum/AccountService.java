package org.eclipse.scout.tradingnetwork.server.ethereum;

import java.io.File;
import java.math.BigDecimal;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.platform.util.IOUtility;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.tradingnetwork.server.ethereum.EthereumProperties.EthereumWalletLocation;
import org.eclipse.scout.tradingnetwork.server.ethereum.model.Account;
import org.eclipse.scout.tradingnetwork.server.sql.SQLs;
import org.eclipse.scout.tradingnetwork.shared.ethereum.AccountFormData;
import org.eclipse.scout.tradingnetwork.shared.ethereum.AccountTablePageData;
import org.eclipse.scout.tradingnetwork.shared.ethereum.AccountTablePageData.AccountTableRowData;
import org.eclipse.scout.tradingnetwork.shared.ethereum.IAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountService implements IAccountService {

  private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

  @Override
  public AccountTablePageData getAccountTableData(SearchFilter filter, String personId) {
    AccountTablePageData pageData = new AccountTablePageData();

    SQL.selectInto(SQLs.ACCOUNT_SELECT_ALL, pageData);

    boolean isPersonIdSet = StringUtility.hasText(personId);
    for (AccountTableRowData rowData : pageData.getRows()) {
      if (isPersonIdSet && !personId.equals(rowData.getPerson())) {
        pageData.removeRow(rowData);
      }
      else {
        try {
          BigDecimal balance = BEANS.get(EthereumService.class).getBalance(rowData.getAddress());
          rowData.setBalance(balance);
        }
        catch (Exception e) {
          LOG.error("failed to fetch balance for account " + rowData.getAddress(), e);
        }
      }
    }

    return pageData;
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
    formData.getAddress().setValue(wallet.getAddress());
    SQL.insert(SQLs.ACCOUNT_CREATE, formData);
    BEANS.get(EthereumService.class).save(wallet);

    return formData;
  }

  private String createWalletPath() {
    String path = CONFIG.getPropertyValue(EthereumWalletLocation.class);
    if (StringUtility.isNullOrEmpty(path)) {
      try {
        File tmpFile = File.createTempFile("tmp", ".txt");
        path = tmpFile.getParent();
      }
      catch (Exception e) {
        LOG.error("Failed to create path to temp file", e);
      }
    }

    return path;
  }

  @Override
  public AccountFormData load(AccountFormData formData) {
    String address = formData.getAddress().getValue();
    SQL.selectInto(SQLs.ACCOUNT_SELECT, formData);

    Account wallet = BEANS.get(EthereumService.class).getWallet(address, formData.getPassword().getValue());
    if (null != wallet) {
      String fileName = wallet.getFile().getAbsolutePath();
      String fileContent = new String(IOUtility.getContent(fileName));

      formData.getFilePath().setValue(fileName);
      formData.getFileContent().setValue(fileContent);
    }

    return formData;
  }

  @Override
  public AccountFormData load(String address) {
    AccountFormData formData = new AccountFormData();
    formData.getAddress().setValue(address);
    return load(formData);
  }

  @Override
  public AccountFormData store(AccountFormData formData) {
    SQL.update(SQLs.ACCOUNT_UPDATE, formData);

    return formData;
  }

  @Override
  public String getPerson(String address) {
    AccountFormData formData = new AccountFormData();
    formData.getAddress().setValue(address);
    formData = load(formData);
    return formData.getPerson().getValue();
  }

  @Override
  public String getPassword(String address) {
    AccountFormData formData = load(address);
    return formData.getPassword().getValue();
  }
}
