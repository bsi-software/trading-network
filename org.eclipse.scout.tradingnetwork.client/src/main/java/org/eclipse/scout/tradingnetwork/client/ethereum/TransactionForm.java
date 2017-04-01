package org.eclipse.scout.tradingnetwork.client.ethereum;

import java.math.BigDecimal;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.bigdecimalfield.AbstractBigDecimalField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.datefield.AbstractDateTimeField;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.longfield.AbstractLongField;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tabbox.AbstractTabBox;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.eclipse.scout.tradingnetwork.client.ethereum.AbstractAccountBox.AccountField;
import org.eclipse.scout.tradingnetwork.client.ethereum.AbstractAccountBox.PersonField;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.CancelButton;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.DetailsBox;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.GeneralBox;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.OkButton;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.DetailsBox.ConfirmationBox;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.DetailsBox.DataBox;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.DetailsBox.FeesBox;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.DetailsBox.ConfirmationBox.BlockField;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.DetailsBox.ConfirmationBox.BlockTimestampField;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.DetailsBox.ConfirmationBox.FromField;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.DetailsBox.ConfirmationBox.ToField;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.DetailsBox.ConfirmationBox.TxHashField;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.DetailsBox.DataBox.DataField;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.DetailsBox.FeesBox.GasLimitField;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.DetailsBox.FeesBox.GasPriceField;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.DetailsBox.FeesBox.GasUsedField;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.DetailsBox.FeesBox.TxFeeField;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.GeneralBox.AmountField;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.GeneralBox.CreatedField;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.GeneralBox.FromBox;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.GeneralBox.SentField;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.GeneralBox.StatusField;
import org.eclipse.scout.tradingnetwork.client.ethereum.TransactionForm.MainBox.GeneralBox.ToBox;
import org.eclipse.scout.tradingnetwork.shared.ethereum.IAccountService;
import org.eclipse.scout.tradingnetwork.shared.ethereum.ITransactionService;
import org.eclipse.scout.tradingnetwork.shared.ethereum.TransactionFormData;
import org.eclipse.scout.tradingnetwork.shared.ethereum.TransactionStatusLookupCall;

@FormData(value = TransactionFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class TransactionForm extends AbstractForm {

  public static final int ETHER_MAX_FRACTION_DIGIT = 6;

  private String id;

  @FormData
  public String getId() {
    return id;
  }

  @FormData
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public Object computeExclusiveKey() {
    return getId();
  }

  @Override
  public String getFormId() {
    return super.getFormId();
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Transaction");
  }

  public void startView() {
    startInternalExclusive(new ViewHandler());
  }

  public void startModify() {
    startInternalExclusive(new ModifyHandler());
  }

  public void startNew() {
    startInternal(new NewHandler());
  }

  public CancelButton getCancelButton() {
    return getFieldByClass(CancelButton.class);
  }

  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  public GeneralBox getGeneralBox() {
    return getFieldByClass(GeneralBox.class);
  }

  public DetailsBox getDetailsBox() {
    return getFieldByClass(DetailsBox.class);
  }

  public FeesBox getFeesBox() {
    return getFieldByClass(FeesBox.class);
  }

  public DataBox getDataBox() {
    return getFieldByClass(DataBox.class);
  }

  public ConfirmationBox getTxDetailsBox() {
    return getFieldByClass(ConfirmationBox.class);
  }

  public FromBox getFromBox() {
    return getFieldByClass(FromBox.class);
  }

  public ToBox getToBox() {
    return getFieldByClass(ToBox.class);
  }

  public PersonField getPersonFromField() {
    return getFromBox().getPersonField();
  }

  public AccountField getAccountFromField() {
    return getFromBox().getAccountField();
  }

  public AmountField getAmountField() {
    return getFieldByClass(AmountField.class);
  }

  public GasPriceField getGasPriceField() {
    return getFieldByClass(GasPriceField.class);
  }

  public GasLimitField getGasLimitField() {
    return getFieldByClass(GasLimitField.class);
  }

  public GasUsedField getGasUsedField() {
    return getFieldByClass(GasUsedField.class);
  }

  public TxFeeField getTxFeeField() {
    return getFieldByClass(TxFeeField.class);
  }

  public DataField getDataField() {
    return getFieldByClass(DataField.class);
  }

  public TxHashField getTxHashField() {
    return getFieldByClass(TxHashField.class);
  }

  public FromField getFromField() {
    return getFieldByClass(FromField.class);
  }

  public ToField getToField() {
    return getFieldByClass(ToField.class);
  }

  public StatusField getStatusField() {
    return getFieldByClass(StatusField.class);
  }

  public SentField getSentField() {
    return getFieldByClass(SentField.class);
  }

  public BlockField getBlockField() {
    return getFieldByClass(BlockField.class);
  }

  public BlockTimestampField getBlockTimestampField() {
    return getFieldByClass(BlockTimestampField.class);
  }

  public CreatedField getCreatedField() {
    return getFieldByClass(CreatedField.class);
  }

  public OkButton getOkButton() {
    return getFieldByClass(OkButton.class);
  }

  @Order(1000)
  public class MainBox extends AbstractGroupBox {

    @Order(1000)
    public class GeneralBox extends AbstractGroupBox {

      @Order(1000)
      public class FromBox extends AbstractAccountBox {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("From");
        }

        @Override
        protected void execAddressChanged(String address) {
          getFromField().setValue(address);
        }
      }

      @Order(2000)
      public class ToBox extends AbstractAccountBox {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("To");
        }

        @Override
        protected void execAddressChanged(String address) {
          getToField().setValue(address);
        }
      }

      @Order(3000)
      public class AmountField extends AbstractBigDecimalField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("AmountEther");
        }

        @Override
        protected boolean getConfiguredMandatory() {
          return true;
        }

        @Override
        protected BigDecimal getConfiguredMinValue() {
          return new BigDecimal(10 ^ (-ETHER_MAX_FRACTION_DIGIT));
        }

        @Override
        protected BigDecimal getConfiguredMaxValue() {
          return new BigDecimal("1000");
        }

        @Override
        protected int getConfiguredMaxFractionDigits() {
          return ETHER_MAX_FRACTION_DIGIT;
        }
      }

      @Order(4000)
      public class NonceField extends AbstractBigDecimalField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Nonce");
        }

        @Override
        protected BigDecimal getConfiguredMinValue() {
          return new BigDecimal("0");
        }

        @Override
        protected int getConfiguredMaxFractionDigits() {
          return 0;
        }
      }

      @Order(5000)
      public class StatusField extends AbstractSmartField<Integer> {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Status");
        }

        @Override
        protected boolean getConfiguredEnabled() {
          return false;
        }

        @Override
        protected Class<? extends ILookupCall<Integer>> getConfiguredLookupCall() {
          return TransactionStatusLookupCall.class;
        }
      }

      @Order(6000)
      public class CreatedField extends AbstractDateTimeField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Created");
        }

        @Override
        protected boolean getConfiguredEnabled() {
          return false;
        }
      }

      @Order(7000)
      public class SentField extends AbstractDateTimeField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Sent");
        }

        @Override
        protected boolean getConfiguredEnabled() {
          return false;
        }
      }
    }

    @Order(2000)
    public class DetailsBox extends AbstractTabBox {

      @Order(1000)
      public class FeesBox extends AbstractGroupBox {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Fees");
        }

        @Order(1000)
        public class GasPriceField extends AbstractBigDecimalField {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("GasPriceWei");
          }

          @Override
          protected BigDecimal getConfiguredMinValue() {
            return new BigDecimal("0");
          }

          @Override
          protected BigDecimal getConfiguredMaxValue() {
            return new BigDecimal("999999999999");
          }

          @Override
          protected int getConfiguredMaxFractionDigits() {
            return 0;
          }

          @Override
          protected void execChangedValue() {
            getTxFeeField().calculate();
          }
        }

        @Order(2000)
        public class GasLimitField extends AbstractBigDecimalField {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("GasLimitGas");
          }

          @Override
          protected BigDecimal getConfiguredMinValue() {
            return new BigDecimal("0");
          }

          @Override
          protected BigDecimal getConfiguredMaxValue() {
            return new BigDecimal("1000000");
          }

          @Override
          protected int getConfiguredMaxFractionDigits() {
            return 0;
          }

          @Override
          protected void execChangedValue() {
            getTxFeeField().calculate();
          }
        }

        @Order(3000)
        public class GasUsedField extends AbstractBigDecimalField {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("GasUsed");
          }

          @Override
          protected int getConfiguredMaxFractionDigits() {
            return 0;
          }

          @Override
          protected boolean getConfiguredEnabled() {
            return false;
          }
        }

        @Order(4000)
        public class TxFeeField extends AbstractBigDecimalField {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("FeesEther");
          }

          @Override
          protected boolean getConfiguredEnabled() {
            return false;
          }

          @Override
          protected int getConfiguredMaxFractionDigits() {
            return ETHER_MAX_FRACTION_DIGIT;
          }

          public void calculate() {
            BigDecimal price = getGasPriceField().getValue();
            BigDecimal limit = getGasLimitField().getValue();
            BigDecimal used = getGasUsedField().getValue();

            if (price != null && limit != null) {
              ITransactionService service = BEANS.get(ITransactionService.class);

              if (used == null) {
                setValue(service.convertToEther(price.multiply(limit)));
              }
              else {
                setValue(service.convertToEther(price.multiply(used)));
              }
            }
          }
        }
      }

      @Order(2000)
      public class DataBox extends AbstractGroupBox {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Data");
        }

        @Order(1000)
        public class DataField extends AbstractStringField {

          @Override
          protected boolean getConfiguredMultilineText() {
            return true;
          }

          @Override
          protected boolean getConfiguredLabelVisible() {
            return false;
          }

          @Override
          protected int getConfiguredGridH() {
            return 3;
          }

          @Override
          protected int getConfiguredMaxLength() {
            return 1000;
          }
        }

      }

      @Order(3000)
      public class ConfirmationBox extends AbstractGroupBox {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Confirmation");
        }

        @Order(1000)
        public class FromField extends AbstractStringField {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("From");
          }

          @Override
          protected int getConfiguredGridW() {
            return 2;
          }

          @Override
          protected boolean getConfiguredEnabled() {
            return false;
          }
        }

        @Order(2000)
        public class ToField extends AbstractStringField {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("To");
          }

          @Override
          protected int getConfiguredGridW() {
            return 2;
          }

          @Override
          protected boolean getConfiguredEnabled() {
            return false;
          }
        }

        @Order(3000)
        public class TxHashField extends AbstractStringField {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("TXHash");
          }

          @Override
          protected int getConfiguredGridW() {
            return 2;
          }

          @Override
          protected boolean getConfiguredEnabled() {
            return false;
          }
        }

        @Order(4000)
        public class BlockField extends AbstractStringField {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("BlockNumber");
          }

          @Override
          protected boolean getConfiguredEnabled() {
            return false;
          }
        }

        @Order(5000)
        public class BlockTimestampField extends AbstractLongField {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("BlockTimestamp");
          }

          @Override
          protected boolean getConfiguredEnabled() {
            return false;
          }
        }

      }
    }

    @Order(100000)
    public class OkButton extends AbstractOkButton {
    }

    @Order(101000)
    public class CancelButton extends AbstractCancelButton {
    }
  }

  public class ViewHandler extends AbstractFormHandler {

    @Override
    protected void execLoad() {
      ITransactionService service = BEANS.get(ITransactionService.class);
      TransactionFormData formData = new TransactionFormData();
      exportFormData(formData);
      formData = service.load(formData);
      importFormData(formData);

      // TODO check if we can do better
      loadAccountFields();

      // TODO Check how to efficiently disable all fields
//      setEnabledGranted(false);
    }
  }

  public class ModifyHandler extends AbstractFormHandler {

    @Override
    protected void execLoad() {
      ITransactionService service = BEANS.get(ITransactionService.class);
      TransactionFormData formData = new TransactionFormData();
      exportFormData(formData);
      formData = service.load(formData);
      importFormData(formData);
    }

    @Override
    protected void execStore() {
      ITransactionService service = BEANS.get(ITransactionService.class);
      TransactionFormData formData = new TransactionFormData();
      exportFormData(formData);
      service.store(formData);
    }
  }

  public class NewHandler extends AbstractFormHandler {

    @Override
    protected void execLoad() {
      ITransactionService service = BEANS.get(ITransactionService.class);
      TransactionFormData formData = new TransactionFormData();
      exportFormData(formData);
      formData = service.prepareCreate(formData);
      importFormData(formData);
    }

    @Override
    protected void execStore() {
      ITransactionService service = BEANS.get(ITransactionService.class);
      TransactionFormData formData = new TransactionFormData();
      exportFormData(formData);
      service.create(formData);
    }
  }

  private void loadAccountFields() {
    String accountFrom = getFromField().getValue();
    String accountTo = getToField().getValue();
    String personFrom = BEANS.get(IAccountService.class).getPerson(accountFrom);
    String personTo = BEANS.get(IAccountService.class).getPerson(accountTo);

    if (personFrom != null) {
      getFromBox().getPersonField().setValue(personFrom);
      getFromBox().getAccountField().setValue(accountFrom);
    }

    if (personTo != null) {
      getToBox().getPersonField().setValue(personTo);
      getToBox().getAccountField().setValue(accountTo);
    }
  }
}
