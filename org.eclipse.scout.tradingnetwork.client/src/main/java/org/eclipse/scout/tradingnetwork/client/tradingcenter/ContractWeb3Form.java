package org.eclipse.scout.tradingnetwork.client.tradingcenter;

import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.tradingnetwork.client.tradingcenter.ContractWeb3Form.MainBox.CancelButton;
import org.eclipse.scout.tradingnetwork.client.tradingcenter.ContractWeb3Form.MainBox.InfoBox;
import org.eclipse.scout.tradingnetwork.client.tradingcenter.ContractWeb3Form.MainBox.InfoBox.Web3InfoField;
import org.eclipse.scout.tradingnetwork.client.tradingcenter.ContractWeb3Form.MainBox.OkButton;
import org.eclipse.scout.tradingnetwork.shared.tradingcenter.CreateContractWeb3Permission;
import org.eclipse.scout.tradingnetwork.shared.tradingcenter.UpdateContractWeb3Permission;

public class ContractWeb3Form extends AbstractForm {

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("ContractWeb3");
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

  public InfoBox getInfoBox() {
    return getFieldByClass(InfoBox.class);
  }

  public Web3InfoField getWeb3InfoField() {
    return getFieldByClass(Web3InfoField.class);
  }

  public OkButton getOkButton() {
    return getFieldByClass(OkButton.class);
  }

  @Order(1000)
  public class MainBox extends AbstractGroupBox {

    @Order(1000)
    public class InfoBox extends AbstractGroupBox {

      @Order(1000)
      public class Web3InfoField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Web3Script");
        }

        @Override
        protected boolean getConfiguredEnabled() {
          return true;
        }

        @Override
        protected boolean getConfiguredMultilineText() {
          return true;
        }

        @Override
        protected int getConfiguredGridW() {
          return 2;
        }

        @Override
        protected int getConfiguredGridH() {
          return 8;
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

      setEnabledPermission(new UpdateContractWeb3Permission());
    }

    @Override
    protected void execStore() {
    }
  }

  public class NewHandler extends AbstractFormHandler {

    @Override
    protected void execLoad() {

      setEnabledPermission(new CreateContractWeb3Permission());
    }

    @Override
    protected void execStore() {
    }
  }
}
