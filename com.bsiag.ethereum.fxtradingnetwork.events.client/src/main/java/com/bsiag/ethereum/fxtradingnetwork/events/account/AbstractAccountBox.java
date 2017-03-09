package com.bsiag.ethereum.fxtradingnetwork.events.account;

import com.bsiag.ethereum.fxtradingnetwork.events.account.IWalletLookupService;
import com.bsiag.ethereum.fxtradingnetwork.events.account.WalletLookupCall;
import com.bsiag.ethereum.fxtradingnetwork.shared.person.PersonLookupCall;
import org.eclipse.scout.rt.client.ui.form.fields.sequencebox.AbstractSequenceBox;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

public class AbstractAccountBox extends AbstractSequenceBox {

  public PersonField getPersonField() {
    return getFieldByClass(PersonField.class);
  }

  public AccountField getAccountField() {
    return getFieldByClass(AccountField.class);
  }

  protected void execAddressChanged(String address) {

  }

  @Override
  protected String getConfiguredLabel() {
    return TEXTS.get("From");
  }

  @Override
  protected boolean getConfiguredAutoCheckFromTo() {
    return false;
  }

  @Order(1000)
  public class PersonField extends AbstractSmartField<String> {

    @Override
    protected String getConfiguredLabel() {
      return TEXTS.get("Person");
    }

    @Override
    protected boolean getConfiguredMandatory() {
      return true;
    }

    @Override
    protected int getConfiguredLabelPosition() {
      return LABEL_POSITION_ON_FIELD;
    }

    @Override
    protected void execChangedValue() {
      BEANS.get(IWalletLookupService.class).setPersonId(getValue());
    }

    @Override
    protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
      return PersonLookupCall.class;
    }
  }

  @Order(2000)
  public class AccountField extends AbstractSmartField<String> {
    @Override
    protected String getConfiguredLabel() {
      return TEXTS.get("Account");
    }

    @Override
    protected boolean getConfiguredMandatory() {
      return true;
    }

    @Override
    protected int getConfiguredLabelPosition() {
      return LABEL_POSITION_ON_FIELD;
    }

    @Override
    protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
      return WalletLookupCall.class;
    }

    @Override
    protected void execChangedValue() {
      execAddressChanged(getValue());
    }
  }
}
