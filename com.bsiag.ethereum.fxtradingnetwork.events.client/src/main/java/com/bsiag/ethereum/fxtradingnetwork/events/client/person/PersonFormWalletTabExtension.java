package com.bsiag.ethereum.fxtradingnetwork.events.client.person;

import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox;
import com.bsiag.ethereum.fxtradingnetwork.events.client.person.PersonFormWalletTabExtension.WalletBox.CreateWalletButton;
import com.bsiag.ethereum.fxtradingnetwork.events.client.person.PersonFormWalletTabExtension.WalletBox.ShowPasswordField;
import com.bsiag.ethereum.fxtradingnetwork.events.client.person.PersonFormWalletTabExtension.WalletBox.WalletAddressField;
import com.bsiag.ethereum.fxtradingnetwork.events.client.person.PersonFormWalletTabExtension.WalletBox.WalletPasswordField;
import com.bsiag.ethereum.fxtradingnetwork.events.client.person.PersonFormWalletTabExtension.WalletBox.WalletPathField;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.person.PersonFormWalletTabExtensionData;
import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.extension.ui.form.fields.tabbox.AbstractTabBoxExtension;
import org.eclipse.scout.rt.client.ui.form.fields.booleanfield.AbstractBooleanField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;

// TODO check if type can be removed
@Data(PersonFormWalletTabExtensionData.class)
public class PersonFormWalletTabExtension extends AbstractTabBoxExtension<PersonForm.MainBox.DetailsBox> {

  public PersonFormWalletTabExtension(DetailsBox owner) {
    super(owner);
  }

  public WalletPathField getWalletPathField() {
    return getOwner().getFieldByClass(WalletPathField.class);
  }

  public CreateWalletButton getCreateWalletButton() {
    return getOwner().getFieldByClass(CreateWalletButton.class);
  }

  public WalletAddressField getWalletAddressField() {
    return getOwner().getFieldByClass(WalletAddressField.class);
  }

  public ShowPasswordField getShowPasswordField() {
    return getOwner().getFieldByClass(ShowPasswordField.class);
  }

  public WalletPasswordField getWalletPasswordField() {
    return getOwner().getFieldByClass(WalletPasswordField.class);
  }

  @Order(25)
  public class WalletBox extends AbstractGroupBox {

    @Override
    protected String getConfiguredLabel() {
      return TEXTS.get("Wallet");
    }

    @Order(1000)
    public class WalletPathField extends AbstractStringField {
      @Override
      protected String getConfiguredLabel() {
        return TEXTS.get("PathToWallet");
      }

      @Override
      protected int getConfiguredMaxLength() {
        return 128;
      }
    }

    @Order(1500)
    public class WalletAddressField extends AbstractStringField {
      @Override
      protected String getConfiguredLabel() {
        return TEXTS.get("WalletAddress");
      }

      @Override
      protected boolean getConfiguredEnabled() {
        return false;
      }

      @Override
      protected int getConfiguredMaxLength() {
        return 128;
      }
    }

    @Order(2000)
    public class WalletPasswordField extends AbstractStringField {
      @Override
      protected String getConfiguredLabel() {
        return TEXTS.get("Password");
      }

      @Override
      protected int getConfiguredMaxLength() {
        return 128;
      }

      @Override
      protected boolean getConfiguredInputMasked() {
        return true;
      }
    }

    @Order(3000)
    public class ShowPasswordField extends AbstractBooleanField {
      @Override
      protected String getConfiguredLabel() {
        return TEXTS.get("ShowPassword");
      }

      @Override
      protected void execInitField() {
        setValue(false);
      }

      @Override
      protected void execChangedValue() {
        getWalletPasswordField().setInputMasked(!getValue());
      }
    }

    @Order(4000)
    public class CreateWalletButton extends AbstractButton {
      @Override
      protected String getConfiguredLabel() {
        return TEXTS.get("CreateWallet");
      }

      @Override
      protected boolean getConfiguredProcessButton() {
        return false;
      }

      @Override
      protected void execClickAction() {
//        String walletPath = getWalletPathField().getValue();
//        String password = getWalletPasswordField().getValue();
//        String address = BEANS.get(IWalletService.class).create(walletPath, password);
      }
    }

  }
}
