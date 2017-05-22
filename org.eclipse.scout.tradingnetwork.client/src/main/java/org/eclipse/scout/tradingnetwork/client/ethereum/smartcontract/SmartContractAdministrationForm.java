package org.eclipse.scout.tradingnetwork.client.ethereum.smartcontract;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.IValueField;
import org.eclipse.scout.rt.client.ui.form.fields.booleanfield.AbstractBooleanField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.messagebox.IMessageBox;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBoxes;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.platform.util.TypeCastUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;
import org.eclipse.scout.tradingnetwork.client.ethereum.smartcontract.SmartContractAdministrationForm.MainBox.TopBox;
import org.eclipse.scout.tradingnetwork.client.ethereum.smartcontract.SmartContractAdministrationForm.MainBox.TopBox.AddressField;
import org.eclipse.scout.tradingnetwork.client.ethereum.smartcontract.SmartContractAdministrationForm.MainBox.TopBox.CreateContractField;
import org.eclipse.scout.tradingnetwork.client.ethereum.smartcontract.SmartContractAdministrationForm.MainBox.TopBox.EnvironmentField;
import org.eclipse.scout.tradingnetwork.client.ethereum.smartcontract.SmartContractAdministrationForm.MainBox.TopBox.OrderBookTypeField;
import org.eclipse.scout.tradingnetwork.client.ethereum.smartcontract.SmartContractAdministrationForm.MainBox.TopBox.OwnerAddressField;
import org.eclipse.scout.tradingnetwork.shared.ethereum.EthereumClientCodeType;
import org.eclipse.scout.tradingnetwork.shared.ethereum.smartcontract.ISmartContractAdminstrationService;
import org.eclipse.scout.tradingnetwork.shared.order.OrderBookTypeCodeType;

@FormData(value = SmartContractAdministrationFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class SmartContractAdministrationForm extends AbstractForm {

  @Override
  protected int getConfiguredModalityHint() {
    return MODALITY_HINT_MODAL;
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("SmartContract");
  }

  public AddressField getAddressField() {
    return getFieldByClass(AddressField.class);
  }

  public CreateContractField getCreateContractField() {
    return getFieldByClass(CreateContractField.class);
  }

  public TopBox getTopBox() {
    return getFieldByClass(TopBox.class);
  }

  public OwnerAddressField getOwnerAddressField() {
    return getFieldByClass(OwnerAddressField.class);
  }

  public OrderBookTypeField getOrderBookTypeField() {
    return getFieldByClass(OrderBookTypeField.class);
  }

  public EnvironmentField getEnvironmentField() {
    return getFieldByClass(EnvironmentField.class);
  }

  public void startNew() {
    startInternal(new NewHandler());
  }

  public void startModify() {
    startInternal(new ModifyHandler());
  }

  public class MainBox extends AbstractGroupBox {

    @Order(0)
    public class TopBox extends AbstractGroupBox {

      @Override
      protected int getConfiguredGridW() {
        return 2;
      }

      @Override
      protected int getConfiguredGridH() {
        return 1;
      }

      @Override
      protected int getConfiguredGridColumnCount() {
        return 1;
      }

      @Order(1000)
      public class EnvironmentField extends AbstractSmartField<String> {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Environment");
        }

        @Override
        protected Class<? extends ICodeType<?, String>> getConfiguredCodeType() {
          return EthereumClientCodeType.class;
        }

        @Override
        protected boolean getConfiguredMandatory() {
          return true;
        }
      }

      @Order(2000)
      public class OrderBookTypeField extends AbstractSmartField<String> {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("OrderBookType");
        }

        @Override
        protected Class<? extends ICodeType<?, String>> getConfiguredCodeType() {
          return OrderBookTypeCodeType.class;
        }

        @Override
        protected boolean getConfiguredMandatory() {
          return true;
        }
      }

      @Order(3000)
      public class CreateContractField extends AbstractBooleanField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("CreateContract");
        }

        @Override
        protected Class<? extends IValueField> getConfiguredMasterField() {
          return AddressField.class;
        }

        @Override
        protected void execChangedMasterValue(Object newMasterValue) {
          String masterValue = TypeCastUtility.castValue(newMasterValue, String.class);
          boolean enabled = false;
          if (StringUtility.isNullOrEmpty(masterValue)) {
            enabled = true;
          }
          else {
            setValue(false);
          }
          setEnabled(enabled);
        }
      }

      @Order(4000)
      public class OwnerAddressField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("OwnerAddress");
        }

        @Override
        protected int getConfiguredMaxLength() {
          return 256;
        }
      }

      @Order(5000)
      public class AddressField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("ContractAddress");
        }

        @Override
        protected int getConfiguredMaxLength() {
          return 256;
        }
      }
    }

    @Order(1000)
    public class OkButton extends AbstractOkButton {
    }

    @Order(2000)
    public class CancelButton extends AbstractCancelButton {
    }

  }

  public class NewHandler extends AbstractFormHandler {

    @Override
    protected void execLoad() {
    }

    @Override
    protected boolean execValidate() {
      boolean valid = true;
      if (getCreateContractField().getValue()) {
        int result = MessageBoxes.createYesNo().withBody(TEXTS.get("ConfirmCreateContract")).show();
        if (IMessageBox.NO_OPTION == result) {
          valid = false;
        }
      }
      return valid;
    }

    @Override
    protected void execStore() {
      SmartContractAdministrationFormData formData = new SmartContractAdministrationFormData();
      exportFormData(formData);
      formData = BEANS.get(ISmartContractAdminstrationService.class).create(formData);
      importFormData(formData);
    }

  }

  public class ModifyHandler extends AbstractFormHandler {

    @Override
    protected void execLoad() {
      SmartContractAdministrationFormData formData = new SmartContractAdministrationFormData();
      exportFormData(formData);
      formData = BEANS.get(ISmartContractAdminstrationService.class).load(formData);
      importFormData(formData);
    }

    @Override
    protected void execStore() {
      SmartContractAdministrationFormData formData = new SmartContractAdministrationFormData();
      exportFormData(formData);
      formData = BEANS.get(ISmartContractAdminstrationService.class).store(formData, true);
      importFormData(formData);
    }

  }

}
