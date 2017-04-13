package org.eclipse.scout.tradingnetwork.server.ethereum.model;

import java.io.File;
import java.math.BigInteger;

import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;

public class Account {
  private static final Logger LOG = LoggerFactory.getLogger(Account.class);

  private String personId;
  private String name;
  private Credentials credentials = null;
  private String fileName;
  private String pathToFile;
  private String password;

  public Account() {

  }

  public Account(String personId, String name, String password, String pathToFile) {
    LOG.info("Creating account '" + name + "' for person '" + personId + "' with password '" + password + "'");

    this.personId = personId;
    this.name = name;
    this.pathToFile = pathToFile;
    this.password = password;

    try {
      fileName = WalletUtils.generateFullNewWalletFile(password, new File(pathToFile));
      credentials = getCredentials();

      if (credentials == null) {
        throw new ProcessingException("Failed to obtain account credentials");
      }
    }
    catch (Exception e) {
      LOG.error("Failed to create account", e);
      throw new ProcessingException("Failed to create account", e);
    }

    LOG.info("Account successfully created. File at " + pathToFile + "/" + fileName);
  }

  public static Account load(String name, String password, String pathToFile, String fileName) {
    Account account = new Account();
    account.name = name;
    account.pathToFile = pathToFile;
    account.fileName = fileName;
    account.password = password;
    account.getCredentials();

    return account;
  }

  public String getPersonId() {
    return personId;
  }

  public void setPersonId(String personId) {
    this.personId = personId;
  }

  public String getFileName() {
    return fileName;
  }

  public String getPathToFile() {
    return pathToFile;
  }

  public String getAddress() {
    return getCredentials().getAddress();
  }

  public void setCredentials(Credentials credentials) {
    this.credentials = credentials;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public File getFile() {
    return new File(pathToFile, fileName);
  }

  public void setFile(File file) {
    this.pathToFile = file.getAbsolutePath();
    this.fileName = file.getName();
  }

  public Credentials getCredentials() {
    if (credentials != null) {
      return credentials;
    }

    try {
      String fileWithPath = getFile().getAbsolutePath();
      credentials = WalletUtils.loadCredentials(password, fileWithPath);

      // TODO verify again (performance issue gone now?)
// tried to figure out where the time is used up
//      File file = new File(fileWithPath);
//      ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper(); // 8sec
//      WalletFile walletFile = objectMapper.readValue(file, WalletFile.class);
//      ECKeyPair keyPair = org.web3j.crypto.Wallet.decrypt(password, walletFile); // > 30s
// -> "culprit" is Wallet.generateDerivedScryptKey() inside of Wallet.decrypt
//      Credentials credentials = Credentials.create(keyPair);

      return credentials;
    }
    catch (Exception e) {
      LOG.error("failed to access credentials in file '" + getFile().getAbsolutePath() + "'", e);
      return null;
    }
  }

  public Transaction createSignedTransaction(String to, BigInteger amountWei, String data, BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit) {
    Transaction tx = new Transaction(to, amountWei, data, nonce, gasPrice, gasLimit);

    tx.setFromAddress(getAddress());
    byte[] signedMessage = TransactionEncoder.signMessage(tx.getRawTransaction(), getCredentials());
    tx.setSignedContent(Numeric.toHexString(signedMessage));

    return tx;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (obj instanceof Account) {
      String address = getAddress();
      String thatAddress = ((Account) obj).getAddress();
      return address.equals(thatAddress);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return getAddress().hashCode();
  }
}
