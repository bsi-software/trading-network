/*******************************************************************************
 * Copyright (c) 2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package com.bsiag.ethereum.fxtradingnetwork.client.organization;

import com.bsiag.ethereum.fxtradingnetwork.client.Icons;
import com.bsiag.ethereum.fxtradingnetwork.client.common.AbstractAddressBox;
import com.bsiag.ethereum.fxtradingnetwork.client.common.AbstractDirtyFormHandler;
import com.bsiag.ethereum.fxtradingnetwork.client.common.AbstractEmailField;
import com.bsiag.ethereum.fxtradingnetwork.client.common.AbstractNotesBox;
import com.bsiag.ethereum.fxtradingnetwork.client.common.AbstractNotesBox.NotesField;
import com.bsiag.ethereum.fxtradingnetwork.client.common.AbstractUrlImageField;
import com.bsiag.ethereum.fxtradingnetwork.client.organization.OrganizationForm.MainBox.CancelButton;
import com.bsiag.ethereum.fxtradingnetwork.client.organization.OrganizationForm.MainBox.DetailsBox;
import com.bsiag.ethereum.fxtradingnetwork.client.organization.OrganizationForm.MainBox.DetailsBox.ContactInfoBox;
import com.bsiag.ethereum.fxtradingnetwork.client.organization.OrganizationForm.MainBox.DetailsBox.ContactInfoBox.AddressBox;
import com.bsiag.ethereum.fxtradingnetwork.client.organization.OrganizationForm.MainBox.DetailsBox.ContactInfoBox.EmailField;
import com.bsiag.ethereum.fxtradingnetwork.client.organization.OrganizationForm.MainBox.DetailsBox.ContactInfoBox.PhoneField;
import com.bsiag.ethereum.fxtradingnetwork.client.organization.OrganizationForm.MainBox.DetailsBox.NotesBox;
import com.bsiag.ethereum.fxtradingnetwork.client.organization.OrganizationForm.MainBox.GeneralBox;
import com.bsiag.ethereum.fxtradingnetwork.client.organization.OrganizationForm.MainBox.GeneralBox.HomepageField;
import com.bsiag.ethereum.fxtradingnetwork.client.organization.OrganizationForm.MainBox.GeneralBox.NameField;
import com.bsiag.ethereum.fxtradingnetwork.client.organization.OrganizationForm.MainBox.GeneralBox.OpenInBrowserButton;
import com.bsiag.ethereum.fxtradingnetwork.client.organization.OrganizationForm.MainBox.GeneralBox.PictureField;
import com.bsiag.ethereum.fxtradingnetwork.client.organization.OrganizationForm.MainBox.OkButton;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.IOrganizationService;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.OrganizationFormData;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.OrganizationUpdatePermission;
import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.desktop.OpenUriAction;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.IValueField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractLinkButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tabbox.AbstractTabBox;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;

@FormData(value = OrganizationFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
// tag::layout[]
public class OrganizationForm extends AbstractForm {

  private String organizationId;

  @FormData
  public String getOrganizationId() {
    return organizationId;
  }

  @FormData
  public void setOrganizationId(String organizationId) {
    this.organizationId = organizationId;
  }

  @Override
  public Object computeExclusiveKey() {
    return getOrganizationId();
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Organization");
  }

  @Override
  protected int getConfiguredDisplayHint() {
    return IForm.DISPLAY_HINT_VIEW;
  }
  // end::layout[]

  public void startModify() {
    startInternalExclusive(new ModifyHandler());
  }

  public void startNew() {
    startInternal(new NewHandler());
  }

  public AddressBox getAddressBox() {
    return getFieldByClass(AddressBox.class);
  }

  public CancelButton getCancelButton() {
    return getFieldByClass(CancelButton.class);
  }

  public NotesBox getNotesBox() {
    return getFieldByClass(NotesBox.class);
  }

  public NotesField getNotesField() {
//    return getFieldByClass(NotesField.class);
    return getNotesBox().getNotesField();
  }

  public ContactInfoBox getOrganizationDetailsBox() {
    return getFieldByClass(ContactInfoBox.class);
  }

  public DetailsBox getDetailsBox() {
    return getFieldByClass(DetailsBox.class);
  }

  public EmailField getEmailField() {
    return getFieldByClass(EmailField.class);
  }

  public GeneralBox getGeneralBox() {
    return getFieldByClass(GeneralBox.class);
  }

  public HomepageField getHomepageField() {
    return getFieldByClass(HomepageField.class);
  }

  public PictureField getLogoField() {
    return getFieldByClass(PictureField.class);
  }

  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  public NameField getNameField() {
    return getFieldByClass(NameField.class);
  }

  public OkButton getOkButton() {
    return getFieldByClass(OkButton.class);
  }

  public OpenInBrowserButton getOpenInBrowserButton() {
    return getFieldByClass(OpenInBrowserButton.class);
  }

  public PhoneField getPhoneField() {
    return getFieldByClass(PhoneField.class);
  }
  // tag::layout[]

  // tag::refactor[]
  @Order(10)
  public class MainBox extends AbstractGroupBox {

    @Order(10)
    public class GeneralBox extends AbstractGroupBox {
      // end::refactor[]
      // tag::picture[]

      @Order(10)
      public class PictureField extends AbstractUrlImageField { // <1>
        // end::picture[]

        @Override
        protected int getConfiguredGridH() { // <2>
          return 4;
        }
        // end::layout[]

        @Override
        protected String getConfiguredImageId() {
          return Icons.Organization;
        }
        // tag::layout[]
        // tag::picture[]
      }
      // end::layout[]

      // additional form field
      // tag::layout[]

      // end::picture[]
      @Order(20)
      public class NameField extends AbstractStringField {

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Name");
        }

        @Override
        protected boolean getConfiguredMandatory() { // <3>
          return true;
        }
      }

      @Order(30)
      public class HomepageField extends AbstractStringField {

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Homepage");
        }
      }
      // end::layout[]

      @Order(40)
      public class OpenInBrowserButton extends AbstractLinkButton {

        @Override
        protected int getConfiguredHorizontalAlignment() {
          return 1;
        }

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("OpenInWebBrowser");
        }

        @Override
        protected Class<? extends IValueField> getConfiguredMasterField() {
          return OrganizationForm.MainBox.GeneralBox.HomepageField.class;
        }

        @Override
        protected boolean getConfiguredMasterRequired() {
          return true;
        }

        @Override
        protected boolean getConfiguredProcessButton() {
          return false;
        }

        @Override
        protected void execClickAction() {
          getDesktop().openUri(getHomepageField().getValue(), OpenUriAction.NEW_WINDOW);
        }
      }
      // tag::layout[]
      // tag::refactor[]
    }
    // end::refactor[]

    @Order(20)
    public class DetailsBox extends AbstractTabBox {

      @Order(10)
      public class ContactInfoBox extends AbstractGroupBox {

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("ContactInfo");
        }

        @Order(10)
        public class AddressBox extends AbstractAddressBox { // <4>
        }

        @Order(20)
        public class PhoneField extends AbstractStringField {

          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Phone");
          }
        }

        @Order(30)
        public class EmailField extends AbstractEmailField { // <5>
        }
      }

      @Order(20)
      public class NotesBox extends AbstractNotesBox { // <6>
      }
    }

    @Order(30)
    public class OkButton extends AbstractOkButton {
    }

    @Order(40)
    public class CancelButton extends AbstractCancelButton {
    }
    // tag::refactor[]
  }
  // end::refactor[]
  // end::layout[]

  public class ModifyHandler extends AbstractDirtyFormHandler {

    @Override
    protected void execLoad() {
      OrganizationFormData formData = new OrganizationFormData();
      exportFormData(formData);
      formData = BEANS.get(IOrganizationService.class).load(formData);
      importFormData(formData);
      setEnabledPermission(new OrganizationUpdatePermission());

      getForm().setSubTitle(calculateSubTitle());
    }

    @Override
    protected void execStore() {
      OrganizationFormData formData = new OrganizationFormData();
      exportFormData(formData);
      formData = BEANS.get(IOrganizationService.class).store(formData);
    }

    @Override
    protected void execDirtyStatusChanged(boolean dirty) {
      getForm().setSubTitle(calculateSubTitle());
    }

    @Override
    protected boolean getConfiguredOpenExclusive() {
      return true;
    }
  }

  public class NewHandler extends AbstractDirtyFormHandler {

    @Override
    protected void execStore() {
      OrganizationFormData formData = new OrganizationFormData();
      exportFormData(formData);
      formData = BEANS.get(IOrganizationService.class).create(formData);
    }

    @Override
    protected void execDirtyStatusChanged(boolean dirty) {
      getForm().setSubTitle(calculateSubTitle());
    }
  }

  private String calculateSubTitle() {
    return getNameField().getValue();
  }
  // tag::layout[]
}
// end::layout[]
