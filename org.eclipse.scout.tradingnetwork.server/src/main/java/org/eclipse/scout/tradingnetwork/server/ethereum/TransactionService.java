package org.eclipse.scout.tradingnetwork.server.ethereum;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

import org.eclipse.scout.tradingnetwork.server.ethereum.model.Transaction;
import org.eclipse.scout.tradingnetwork.shared.ethereum.ITransactionService;
import org.eclipse.scout.tradingnetwork.shared.ethereum.TransactionFormData;
import org.eclipse.scout.tradingnetwork.shared.ethereum.TransactionTablePageData;
import org.eclipse.scout.tradingnetwork.shared.ethereum.TransactionTablePageData.TransactionTableRowData;

public class TransactionService implements ITransactionService {

  private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);

  @Override
  public TransactionTablePageData getTransactionTableData(SearchFilter filter) {
    TransactionTablePageData pageData = new TransactionTablePageData();

    BEANS.get(EthereumService.class).getTransactions()
        .stream()
        .forEach(txId -> {
          Transaction tx = BEANS.get(EthereumService.class).getTransaction(txId);
          addRow(txId, tx, pageData);
        });

    return pageData;
  }

  private void addRow(String txId, Transaction tx, TransactionTablePageData pageData) {
    TransactionTableRowData rowData = pageData.addRow();
    rowData.setId(txId);
    rowData.setFrom(tx.getFromAddress());
    rowData.setTo(tx.getToAddress());
    BigDecimal valueEther = Convert.fromWei(new BigDecimal(tx.getValue()), Unit.ETHER);
    rowData.setValue(valueEther);
    rowData.setStatus(tx.getStatus());
    rowData.setHash(tx.getHash());
    TransactionReceipt receipt = tx.getTransactionReceipt();
    if (receipt != null) {
      try {
        rowData.setBlock(receipt.getBlockNumber().longValue());
      }
      catch (Exception e) {
        LOG.info("failed to fetch tx block number", e);
      }
    }
  }

  @Override
  public void refresh(String transactionId) {
    BEANS.get(EthereumService.class).refreshStatus(transactionId);
  }

  @Override
  public TransactionFormData prepareCreate(TransactionFormData formData) {
    formData.getStatus().setValue(Transaction.OFFLINE);
    formData.getGasPrice().setValue(new BigDecimal(Transaction.GAS_PRICE_DEFAULT));
    formData.getGasLimit().setValue(new BigDecimal(Transaction.GAS_LIMIT_DEFAULT));
    formData.getTxFee().setValue(convertToEther(new BigDecimal(Transaction.TX_FEE_DEFAULT)));

    return formData;
  }

  @Override
  public TransactionFormData create(TransactionFormData formData) {
    return createNew(formData);
  }

  @Override
  public TransactionFormData load(TransactionFormData formData) {
    String txId = formData.getId();
    Transaction tx = BEANS.get(EthereumService.class).getTransaction(txId);

    if (tx != null) {
      String from = tx.getFromAddress();
      String to = tx.getToAddress();

      formData.getFrom().setValue(from);
      formData.getTo().setValue(to);
      formData.getAmount().setValue(convertToEther(new BigDecimal(tx.getValue())));
      formData.getStatus().setValue(tx.getStatus());
      formData.getCreated().setValue(tx.getCreated());
      formData.getSent().setValue(tx.getSent());

      BigDecimal gasPrice = new BigDecimal(tx.getRawTransaction().getGasPrice());

      RawTransaction txRaw = tx.getRawTransaction();
      if (txRaw != null) {
        BigDecimal gasLimit = new BigDecimal(tx.getRawTransaction().getGasLimit());
        formData.getData().setValue(tx.getRawTransaction().getData());
        formData.getNonce().setValue(new BigDecimal(tx.getRawTransaction().getNonce()));
        formData.getGasPrice().setValue(gasPrice);
        formData.getGasLimit().setValue(gasLimit);
        formData.getTxFee().setValue(convertToEther(gasPrice.multiply(gasLimit)));
      }

      TransactionReceipt txReceipt = tx.getTransactionReceipt();
      if (txReceipt != null) {
        BigDecimal gasUsed = new BigDecimal(txReceipt.getGasUsed());
        formData.getBlock().setValue(txReceipt.getBlockNumber().toString());
        formData.getTxHash().setValue(txReceipt.getTransactionHash());
        formData.getGasUsed().setValue(gasUsed);
        formData.getTxFee().setValue(convertToEther(gasPrice.multiply(gasUsed)));
      }
    }

    return formData;
  }

  @Override
  public TransactionFormData store(TransactionFormData formData) {

    // only update offline transactions
    int status = formData.getStatus().getValue();
    if (status != Transaction.OFFLINE) {
      LOG.warn("can only replace tx with offline state. tx.state=" + status + " tx.id=" + formData.getId());
      return formData;
    }

    formData = createNew(formData);

    Transaction tx = BEANS.get(EthereumService.class).getTransaction(formData.getId());
    tx.setStatus(Transaction.REPLACED);
    BEANS.get(EthereumService.class).save(tx);

    return formData;
  }

  @Override
  public void send(String transactionId) {
    EthereumService service = BEANS.get(EthereumService.class);
    Transaction tx = service.getTransaction(transactionId);
    service.send(tx);
  }

  private TransactionFormData createNew(TransactionFormData formData) {
    String from = formData.getFrom().getValue();
    String to = formData.getTo().getValue();
    BigInteger amountWei = convertToWei(formData.getAmount().getValue());
    String data = formData.getData().getValue();

    BigDecimal n = formData.getNonce().getValue();
    BigDecimal gp = formData.getGasPrice().getValue();
    BigDecimal gl = formData.getGasLimit().getValue();

    BigInteger nonce = n == null ? null : n.toBigInteger();
    BigInteger gasPrice = gp == null ? null : gp.toBigInteger();
    BigInteger gasLimit = gl == null ? null : gl.toBigInteger();

    BEANS.get(EthereumService.class).createTransaction(from, to, amountWei, data, nonce, gasPrice, gasLimit);

    return formData;
  }

  @Override
  public BigDecimal convertToEther(BigDecimal weiAmount) {
    return Convert.fromWei(weiAmount, Unit.ETHER);
  }

  public BigInteger convertToWei(BigDecimal etherAmount) {
    // TODO check/fix this conversion
    return Convert.toWei(etherAmount, Unit.ETHER).toBigInteger();
  }

}
