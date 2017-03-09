package com.bsiag.ethereum.fxtradingnetwork.events.client.organization;

import java.math.BigDecimal;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.bigdecimalfield.AbstractBigDecimalField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;

import com.bsiag.ethereum.fxtradingnetwork.events.client.organization.OrganizationBankAccountForm.MainBox.BalanceField;
import com.bsiag.ethereum.fxtradingnetwork.events.client.organization.OrganizationBankAccountForm.MainBox.CancelButton;
import com.bsiag.ethereum.fxtradingnetwork.events.client.organization.OrganizationBankAccountForm.MainBox.CurrencyField;
import com.bsiag.ethereum.fxtradingnetwork.events.client.organization.OrganizationBankAccountForm.MainBox.OkButton;
import com.bsiag.ethereum.fxtradingnetwork.events.client.organization.OrganizationBankAccountForm.MainBox.OrganizationIdField;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.CurrencyCodeType;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.organization.IOrganizationBankAccountService;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.organization.OrganizationBankAccountFormData;

@FormData(value = OrganizationBankAccountFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class OrganizationBankAccountForm extends AbstractForm {

  @Override
  protected String getConfiguredTitle() {
    // TODO [uko] verify translation
    return TEXTS.get("AccountBalance");
  }

  public void startModify(String organizationId, String currencyUid) {
    getOrganizationIdField().setValue(organizationId);
    getCurrencyField().setValue(currencyUid);
    startInternalExclusive(new ModifyHandler());
  }

  public void startNew(String organizationId) {
    getOrganizationIdField().setValue(organizationId);
    startInternal(new NewHandler());
  }

  public CancelButton getCancelButton() {
    return getFieldByClass(CancelButton.class);
  }

  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  public CurrencyField getCurrencyField() {
    return getFieldByClass(CurrencyField.class);
  }

  public BalanceField getBalanceField() {
    return getFieldByClass(BalanceField.class);
  }

  public OrganizationIdField getOrganizationIdField() {
    return getFieldByClass(OrganizationIdField.class);
  }

  public OkButton getOkButton() {
    return getFieldByClass(OkButton.class);
  }

  @Order(1000)
  public class MainBox extends AbstractGroupBox {

    @Override
    protected boolean getConfiguredGridUseUiHeight() {
      return true;
    }

    @Order(1000)
    public class OrganizationIdField extends AbstractStringField {
      @Override
      protected String getConfiguredLabel() {
        return TEXTS.get("OrganizationId");
      }

      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }

      @Override
      protected int getConfiguredMaxLength() {
        return 128;
      }

      @Override
      protected boolean getConfiguredMandatory() {
        return true;
      }
    }

    @Order(2000)
    public class CurrencyField extends AbstractSmartField<String> {
      @Override
      protected String getConfiguredLabel() {
        return TEXTS.get("Currency");
      }

      @Override
      protected Class<? extends ICodeType<?, String>> getConfiguredCodeType() {
        return CurrencyCodeType.class;
      }

      @Override
      protected boolean getConfiguredMandatory() {
        return true;
      }
    }

    @Order(3000)
    public class BalanceField extends AbstractBigDecimalField {
      @Override
      protected String getConfiguredLabel() {
        return TEXTS.get("Balance");
      }

      @Override
      protected BigDecimal getConfiguredMinValue() {
        return new BigDecimal("-9999999999999999999");
      }

      @Override
      protected BigDecimal getConfiguredMaxValue() {
        return new BigDecimal("9999999999999999999");
      }

      @Override
      protected boolean getConfiguredMandatory() {
        return true;
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
      IOrganizationBankAccountService service = BEANS.get(IOrganizationBankAccountService.class);
      OrganizationBankAccountFormData formData = new OrganizationBankAccountFormData();
      exportFormData(formData);
      formData = service.load(formData);
      importFormData(formData);
    }

    @Override
    protected void execPostLoad() {
      getCurrencyField().setEnabled(false);
      super.execPostLoad();
    }

    @Override
    protected void execStore() {
      IOrganizationBankAccountService service = BEANS.get(IOrganizationBankAccountService.class);
      OrganizationBankAccountFormData formData = new OrganizationBankAccountFormData();
      exportFormData(formData);
      service.store(formData);
    }
  }

  public class NewHandler extends AbstractFormHandler {

    @Override
    protected void execLoad() {
      IOrganizationBankAccountService service = BEANS.get(IOrganizationBankAccountService.class);
      OrganizationBankAccountFormData formData = new OrganizationBankAccountFormData();
      exportFormData(formData);
      formData = service.prepareCreate(formData);
      importFormData(formData);
    }

    @Override
    protected void execStore() {
      IOrganizationBankAccountService service = BEANS.get(IOrganizationBankAccountService.class);
      OrganizationBankAccountFormData formData = new OrganizationBankAccountFormData();
      exportFormData(formData);
      service.create(formData);
    }
  }
}
