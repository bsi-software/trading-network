package org.eclipse.scout.tradingnetwork.client.ethereum;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.booleanfield.AbstractBooleanField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.imagefield.AbstractImageField;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.eclipse.scout.tradingnetwork.client.ethereum.AccountForm.MainBox.CancelButton;
import org.eclipse.scout.tradingnetwork.client.ethereum.AccountForm.MainBox.OkButton;
import org.eclipse.scout.tradingnetwork.client.ethereum.AccountForm.MainBox.TopBox;
import org.eclipse.scout.tradingnetwork.client.ethereum.AccountForm.MainBox.TopBox.AddressField;
import org.eclipse.scout.tradingnetwork.client.ethereum.AccountForm.MainBox.TopBox.FileContentField;
import org.eclipse.scout.tradingnetwork.client.ethereum.AccountForm.MainBox.TopBox.FilePathField;
import org.eclipse.scout.tradingnetwork.client.ethereum.AccountForm.MainBox.TopBox.NameField;
import org.eclipse.scout.tradingnetwork.client.ethereum.AccountForm.MainBox.TopBox.PasswordField;
import org.eclipse.scout.tradingnetwork.client.ethereum.AccountForm.MainBox.TopBox.PasswordVisibleField;
import org.eclipse.scout.tradingnetwork.client.ethereum.AccountForm.MainBox.TopBox.PersonField;
import org.eclipse.scout.tradingnetwork.client.ethereum.AccountForm.MainBox.TopBox.QrCoceField;
import org.eclipse.scout.tradingnetwork.shared.ethereum.AccountFormData;
import org.eclipse.scout.tradingnetwork.shared.ethereum.IAccountService;
import org.eclipse.scout.tradingnetwork.shared.person.PersonLookupCall;

@FormData(value = AccountFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class AccountForm extends AbstractForm {

  public static final int QR_CODE_SIZE = 190;

  @Override
  public Object computeExclusiveKey() {
    return getAddressField().getValue();
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Account");
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

  public TopBox getTopBox() {
    return getFieldByClass(TopBox.class);
  }

  public QrCoceField getQrCoceField() {
    return getFieldByClass(QrCoceField.class);
  }

  public AddressField getAddressField() {
    return getFieldByClass(AddressField.class);
  }

  public PasswordField getPasswordField() {
    return getFieldByClass(PasswordField.class);
  }

  public FileContentField getFileContentField() {
    return getFieldByClass(FileContentField.class);
  }

  public FilePathField getFilePathField() {
    return getFieldByClass(FilePathField.class);
  }

  public PersonField getPersonField() {
    return getFieldByClass(PersonField.class);
  }

  public PasswordVisibleField getPasswordVisibleField() {
    return getFieldByClass(PasswordVisibleField.class);
  }

  public NameField getNameField() {
    return getFieldByClass(NameField.class);
  }

  public OkButton getOkButton() {
    return getFieldByClass(OkButton.class);
  }

  public void setQrCode(String address) {
    if (StringUtility.hasText(address)) {
      getQrCoceField().setImage(QRCodeGenerator.generate(address, QR_CODE_SIZE));
    }
  }

  @Order(1000)
  public class MainBox extends AbstractGroupBox {

    @Order(1000)
    public class TopBox extends AbstractGroupBox {

      @Order(1000)
      public class QrCoceField extends AbstractImageField {

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("QRCode");
        }

        @Override
        protected boolean getConfiguredLabelVisible() {
          return true;
        }

        @Override
        protected boolean getConfiguredAutoFit() {
          return true;
        }

        @Override
        protected int getConfiguredHorizontalAlignment() {
          return -1;
        }

        @Override
        protected int getConfiguredGridH() {
          return 5;
        }
      }

      @Order(2000)
      public class FilePathField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("WalletPath");
        }

        @Override
        protected boolean getConfiguredEnabled() {
          return false;
        }

        @Override
        protected int getConfiguredGridW() {
          return 2;
        }
      }

      @Order(3000)
      public class AddressField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Address");
        }

        @Override
        protected boolean getConfiguredEnabled() {
          return false;
        }

        @Override
        protected int getConfiguredGridW() {
          return 2;
        }
      }

      @Order(4000)
      public class NameField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Name");
        }

        @Override
        protected boolean getConfiguredMandatory() {
          return true;
        }

        @Override
        protected int getConfiguredMaxLength() {
          return 128;
        }
      }

      @Order(5000)
      public class PersonField extends AbstractSmartField<String> {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Owner");
        }

        @Override
        protected boolean getConfiguredEnabled() {
          return false;
        }

        @Override
        protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
          return PersonLookupCall.class;
        }
      }

      @Order(6000)
      public class PasswordField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Password");
        }

        @Override
        protected int getConfiguredMaxLength() {
          return 128;
        }

        @Override
        protected boolean getConfiguredVisible() {
          return false;
        }

        @Override
        protected void execInitField() {
          setInputMasked(true);
        }
      }

      @Order(6500)
      public class PasswordVisibleField extends AbstractBooleanField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("ShowPassword");
        }

        @Override
        protected boolean getConfiguredVisible() {
          return false;
        }

        @Override
        protected void execChangedValue() {
          getPasswordField().setInputMasked(!getValue());
        }
      }

      @Order(7000)
      public class FileContentField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("WalletFile");
        }

        @Override
        protected boolean getConfiguredEnabled() {
          return false;
        }

        @Override
        protected boolean getConfiguredMultilineText() {
          return true;
        }

        @Override
        protected boolean getConfiguredWrapText() {
          return true;
        }

        @Override
        protected int getConfiguredGridH() {
          return 3;
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

  public class ModifyHandler extends AbstractFormHandler {

    @Override
    protected void execLoad() {
      IAccountService service = BEANS.get(IAccountService.class);
      AccountFormData formData = new AccountFormData();
      exportFormData(formData);
      formData = service.load(formData);
      importFormData(formData);
    }

    @Override
    protected void execStore() {
      IAccountService service = BEANS.get(IAccountService.class);
      AccountFormData formData = new AccountFormData();
      exportFormData(formData);
      service.store(formData);
    }
  }

  public class NewHandler extends AbstractFormHandler {

    @Override
    protected void execLoad() {
      IAccountService service = BEANS.get(IAccountService.class);
      AccountFormData formData = new AccountFormData();
      exportFormData(formData);
      formData = service.prepareCreate(formData);
      importFormData(formData);

      getPasswordField().setVisible(true);
      getPasswordVisibleField().setVisible(true);
      getAddressField().setVisible(false);
      getQrCoceField().setVisible(false);
      getFilePathField().setVisible(false);
      getFileContentField().setVisible(false);
    }

    @Override
    protected void execStore() {
      IAccountService service = BEANS.get(IAccountService.class);
      AccountFormData formData = new AccountFormData();
      exportFormData(formData);
      service.create(formData);
    }
  }
}
