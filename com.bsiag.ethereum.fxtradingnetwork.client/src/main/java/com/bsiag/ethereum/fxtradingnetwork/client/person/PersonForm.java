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
package com.bsiag.ethereum.fxtradingnetwork.client.person;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.regex.Pattern;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractDateTimeColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.IValueField;
import org.eclipse.scout.rt.client.ui.form.fields.booleanfield.AbstractBooleanField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractLinkButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.datefield.AbstractDateField;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.imagefield.AbstractImageField;
import org.eclipse.scout.rt.client.ui.form.fields.radiobuttongroup.AbstractRadioButtonGroup;
import org.eclipse.scout.rt.client.ui.form.fields.sequencebox.AbstractSequenceBox;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tabbox.AbstractTabBox;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.status.IStatus;
import org.eclipse.scout.rt.platform.status.Status;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.platform.util.IOUtility;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import com.bsiag.ethereum.fxtradingnetwork.client.Icons;
import com.bsiag.ethereum.fxtradingnetwork.client.common.AbstractDirtyFormHandler;
import com.bsiag.ethereum.fxtradingnetwork.client.common.CountryLookupCall;
import com.bsiag.ethereum.fxtradingnetwork.client.common.MapForm;
import com.bsiag.ethereum.fxtradingnetwork.client.common.PictureUrlForm;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.CancelButton;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.ContactInfoBox;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.ContactInfoBox.AddressBox;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.ContactInfoBox.AddressBox.LocationBox.CityField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.ContactInfoBox.AddressBox.LocationBox.CountryField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.ContactInfoBox.EmailField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.ContactInfoBox.MobileField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.ContactInfoBox.PhoneField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.NotesBox;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.NotesBox.NotesField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.WalletBox.CreateWalletButton;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.WalletBox.ShowPasswordField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.WalletBox.WalletAddressField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.WalletBox.WalletPasswordField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.WalletBox.WalletPathField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.WorkBox;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.WorkBox.EmailWorkField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.WorkBox.OrganizationField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.WorkBox.PhoneWorkField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.DetailsBox.WorkBox.PositionField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.GeneralBox;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.GeneralBox.DateOfBirthField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.GeneralBox.FirstNameField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.GeneralBox.GenderGroup;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.GeneralBox.LastNameField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.GeneralBox.PictureField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.GeneralBox.PictureUrlField;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonForm.MainBox.OkButton;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.OrganizationLookupCall;
import com.bsiag.ethereum.fxtradingnetwork.shared.person.GenderCodeType;
import com.bsiag.ethereum.fxtradingnetwork.shared.person.IPersonService;
import com.bsiag.ethereum.fxtradingnetwork.shared.person.PersonFormData;

// tag::init[]
@FormData(value = PersonFormData.class, sdkCommand = FormData.SdkCommand.CREATE) // <1>
// tag::structure[]
// tag::validate[]
public class PersonForm extends AbstractForm {

  // end::validate[]
  // end::structure[]
  // represents the person's primary key
  private String personId;

  @FormData // <2>
  public String getPersonId() {
    return personId;
  }

  @FormData // <2>
  public void setPersonId(String personId) {
    this.personId = personId;
  }

  @Override
  public Object computeExclusiveKey() { // <3>
    return getPersonId();
  }

  @Override
  protected int getConfiguredDisplayHint() { // <4>
    return IForm.DISPLAY_HINT_VIEW;
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Person");
  }
  //end::init[]

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

  public PictureUrlField getPictureUrlField() {
    return getFieldByClass(PictureUrlField.class);
  }

  public NotesBox getNotesBox() {
    return getFieldByClass(NotesBox.class);
  }

  public NotesField getNotesField() {
    return getFieldByClass(NotesField.class);
  }

  public OrganizationField getOrganizationField() {
    return getFieldByClass(OrganizationField.class);
  }

  public ContactInfoBox getPersonDetailsBox() {
    return getFieldByClass(ContactInfoBox.class);
  }

  public DateOfBirthField getDateOfBirthField() {
    return getFieldByClass(DateOfBirthField.class);
  }

  public DetailsBox getDetailsBox() {
    return getFieldByClass(DetailsBox.class);
  }

  public EmailField getEmailField() {
    return getFieldByClass(EmailField.class);
  }

  public EmailWorkField getEmailWorkField() {
    return getFieldByClass(EmailWorkField.class);
  }

  public FirstNameField getFirstNameField() {
    return getFieldByClass(FirstNameField.class);
  }

  public GenderGroup getGenderGroup() {
    return getFieldByClass(GenderGroup.class);
  }

  public GeneralBox getGeneralBox() {
    return getFieldByClass(GeneralBox.class);
  }

  public LastNameField getLastNameField() {
    return getFieldByClass(LastNameField.class);
  }

  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  public MobileField getMobileField() {
    return getFieldByClass(MobileField.class);
  }

  public OkButton getOkButton() {
    return getFieldByClass(OkButton.class);
  }

  public PhoneField getPhoneField() {
    return getFieldByClass(PhoneField.class);
  }

  public PhoneWorkField getPhoneWorkField() {
    return getFieldByClass(PhoneWorkField.class);
  }

  public PictureField getPictureField() {
    return getFieldByClass(PictureField.class);
  }

  public PositionField getPositionField() {
    return getFieldByClass(PositionField.class);
  }

  public WorkBox getWorkBox() {
    return getFieldByClass(WorkBox.class);
  }

  public WalletPathField getWalletPathField() {
    return getFieldByClass(WalletPathField.class);
  }

  public CreateWalletButton getCreateWalletButton() {
    return getFieldByClass(CreateWalletButton.class);
  }

  public WalletAddressField getWalletAddressField() {
    return getFieldByClass(WalletAddressField.class);
  }

  public ShowPasswordField getShowPasswordField() {
    return getFieldByClass(ShowPasswordField.class);
  }

  public WalletPasswordField getWalletPasswordField() {
    return getFieldByClass(WalletPasswordField.class);
  }

  @Order(10)
  public class MainBox extends AbstractGroupBox {

    @Order(1000)
    public class GeneralBox extends AbstractGroupBox {

      @Override
      protected String getConfiguredLabel() {
        return TEXTS.get("General");
      }

      @Override
      protected boolean getConfiguredLabelVisible() {
        return false;
      }

      @Order(10)
      public class PictureUrlField extends AbstractStringField {

        @Override
        protected boolean getConfiguredVisible() {
          return false;
        }
      }

      @Order(20)
      public class PictureField extends AbstractImageField {

        @Override
        protected Class<PictureUrlField> getConfiguredMasterField() {
          return PictureUrlField.class;
        }

        @Override
        protected void execChangedMasterValue(Object newMasterValue) {
          updateImage((String) newMasterValue);
        }

        @Override
        protected boolean getConfiguredLabelVisible() {
          return false;
        }

        @Override
        protected int getConfiguredGridH() {
          return 5;
        }

        @Override
        protected String getConfiguredImageId() {
          return Icons.Person;
        }

        @Order(10)
        public class EditURLMenu extends AbstractMenu {

          @Override
          protected String getConfiguredText() {
            return TEXTS.get("EditURL");
          }

          @Override
          protected void execAction() {
            String oldUrl = getPictureUrlField().getValue();
            PictureUrlForm form = new PictureUrlForm();

            if (StringUtility.hasText(oldUrl)) {
              form.getUrlField().setValue(oldUrl);
            }

            form.startModify();
            form.waitFor();

            if (form.isFormStored()) {
              getPictureUrlField().setValue(form.getUrlField().getValue());
            }
          }
        }

        protected void updateImage(String url) {
          clearErrorStatus();

          if (url == null) {
            setImage(null);
          }
          else if (url.contains("local")) {
            try {
              URL url1 = this.getClass().getResource(url);

              setImage(IOUtility.readFromUrl(url1));

              setAutoFit(true);
            }
            catch (IOException e) {
              e.printStackTrace();
            }
          }
          else {
            try {
              setImage(IOUtility.readFromUrl(new URL((String) url)));
              setAutoFit(true);
            }
            catch (MalformedURLException e) {
              addErrorStatus(new Status(TEXTS.get("InvalidImageUrl"), IStatus.WARNING));
            }
            catch (Exception e) {
              String message = TEXTS.get("FailedToAccessImageFromUrl");
              addErrorStatus(new Status(message, IStatus.WARNING));
            }
          }
        }
      }

      @Order(30)
      public class FirstNameField extends AbstractStringField {

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("FirstName");
        }
      }

      @Order(40)
      public class LastNameField extends AbstractStringField {

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("LastName");
        }
      }

      @Order(50)
      public class DateOfBirthField extends AbstractDateField {

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("DateOfBirth");
        }

        @Override
        protected Date execValidateValue(Date rawValue) {
          if (CompareUtility.compareTo(rawValue, new Date()) > 0) {
            throw new VetoException(TEXTS.get("DateOfBirthCanNotBeInFuture"));
          }

          return super.execValidateValue(rawValue);
        }
      }

      @Order(60)
      public class GenderGroup extends AbstractRadioButtonGroup<String> {

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Gender");
        }

        @Override
        protected Class<? extends ICodeType<?, String>> getConfiguredCodeType() {
          return GenderCodeType.class;
        }
      }
    }

    @Order(2000)
    public class DetailsBox extends AbstractTabBox {

      @Order(1000)
      public class ContactInfoBox extends AbstractGroupBox {

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("ContactInfo");
        }

        @Order(10)
        public class AddressBox extends AbstractGroupBox {

          @Override
          protected boolean getConfiguredBorderVisible() {
            return false;
          }

          @Override
          protected int getConfiguredGridH() {
            return 3;
          }

          @Override
          protected int getConfiguredGridW() {
            return 1;
          }

          @Override
          protected int getConfiguredGridColumnCount() {
            return 1;
          }

          public StreetField getStreetField() {
            return getFieldByClass(StreetField.class);
          }

          public LocationBox getLocationBox() {
            return getFieldByClass(LocationBox.class);
          }

          public CityField getCityField() {
            return getFieldByClass(CityField.class);
          }

          public CountryField getCountryField() {
            return getFieldByClass(CountryField.class);
          }

          public ShowOnMapButton getShowOnMapButton() {
            return getFieldByClass(ShowOnMapButton.class);
          }

          @Order(10)
          public class StreetField extends AbstractStringField {

            @Override
            protected String getConfiguredLabel() {
              return TEXTS.get("Street");
            }

            @Override
            protected void execChangedValue() {
              validateAddressFields();
            }
          }

          @Order(20)
          public class LocationBox extends AbstractSequenceBox {

            @Override
            protected String getConfiguredLabel() {
              return TEXTS.get("Location");
            }

            @Override
            protected boolean getConfiguredAutoCheckFromTo() {
              return false;
            }

            @Order(10)
            public class CityField extends AbstractStringField {

              @Override
              protected String getConfiguredLabel() {
                return TEXTS.get("City");
              }

              @Override
              protected int getConfiguredLabelPosition() {
                return LABEL_POSITION_ON_FIELD;
              }

              @Override
              protected void execChangedValue() {
                validateAddressFields();
              }
            }

            @Order(20)
            public class CountryField extends AbstractSmartField<String> {

              @Override
              protected String getConfiguredLabel() {
                return TEXTS.get("Country");
              }

              @Override
              protected void execChangedValue() {
                validateAddressFields();
              }

              @Override
              protected int getConfiguredLabelPosition() {
                return LABEL_POSITION_ON_FIELD;
              }

              @Override
              protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
                return CountryLookupCall.class;
              }
            }
          }

          @Order(30)
          public class ShowOnMapButton extends AbstractLinkButton {

            @Override
            protected int getConfiguredHorizontalAlignment() {
              return 1;
            }

            @Override
            protected String getConfiguredLabel() {
              return TEXTS.get("ShowOnMap");
            }

            @Override
            protected Class<? extends IValueField> getConfiguredMasterField() {
              return CountryField.class;
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
              MapForm mapForm = new MapForm();
              mapForm.setStreet(getStreetField().getValue());
              mapForm.setCity(getCityField().getValue());
              mapForm.setCountry(getCountryField().getValue());
              mapForm.startModify();
            }
          }

          protected void validateAddressFields() {
            boolean hasStreet = StringUtility.hasText(getStreetField().getValue());
            boolean hasCity = StringUtility.hasText(getCityField().getValue());

            getCityField().setMandatory(hasStreet);
            getCountryField().setMandatory(hasStreet || hasCity);
          }
        }

        @Order(20)
        public class PhoneField extends AbstractStringField {

          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Phone");
          }
        }

        @Order(30)
        public class MobileField extends AbstractStringField {

          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Mobile");
          }
        }

        @Order(40)
        public class EmailField extends AbstractStringField {

          // http://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
          private static final String EMAIL_PATTERN = // <1>
              "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                  "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Email");
          }

          @Override
          protected int getConfiguredMaxLength() {
            return 64;
          }

          @Override
          protected String execValidateValue(String rawValue) {
            if (rawValue != null && !Pattern.matches(EMAIL_PATTERN, rawValue)) {
              throw new VetoException(TEXTS.get("BadEmailAddress")); // <4>
            }

            return rawValue;
          }
        }
      }

      @Order(2000)
      public class WorkBox extends AbstractGroupBox {

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Work");
        }

        @Order(10)
        public class PositionField extends AbstractStringField {

          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Position");
          }
        }

        @Order(20)
        public class OrganizationField extends AbstractSmartField<String> {

          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Organization");
          }

          @Override
          protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
            return OrganizationLookupCall.class;
          }
        }

        @Order(30)
        public class PhoneWorkField extends AbstractStringField {

          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Phone");
          }
        }

        @Order(40)
        public class EmailWorkField extends AbstractStringField {

          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Email");
          }
        }
      }

      @Order(3000)
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
//            String walletPath = getWalletPathField().getValue();
//            String password = getWalletPasswordField().getValue();
//            String address = BEANS.get(IWalletService.class).create(walletPath, password);
          }
        }

      }

      @Order(4000)
      public class EventBox extends AbstractGroupBox {

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Events");
        }

        @Order(1000)
        public class EventsField extends AbstractTableField<EventsField.Table> {

          @Override
          protected int getConfiguredGridH() {
            return 3;
          }

          @Override
          protected boolean getConfiguredLabelVisible() {
            return false;
          }

          public class Table extends AbstractTable {

            @Override
            protected String getConfiguredDefaultIconId() {
              return Icons.Deal;
            }

            public IdColumn getIdColumn() {
              return getColumnSet().getColumnByClass(IdColumn.class);
            }

            public TitleColumn getTitleColumn() {
              return getColumnSet().getColumnByClass(TitleColumn.class);
            }

            public StartsColumn getStartsColumn() {
              return getColumnSet().getColumnByClass(StartsColumn.class);
            }

            public CityColumn getCityColumn() {
              return getColumnSet().getColumnByClass(CityColumn.class);
            }

            public CountryColumn getCountryColumn() {
              return getColumnSet().getColumnByClass(CountryColumn.class);
            }

            @Order(1)
            public class IdColumn extends AbstractStringColumn {

              @Override
              protected boolean getConfiguredDisplayable() {
                return false;
              }

              @Override
              protected boolean getConfiguredPrimaryKey() {
                return true;
              }
            }

            @Order(2)
            public class TitleColumn extends AbstractStringColumn {

              @Override
              protected String getConfiguredHeaderText() {
                return TEXTS.get("Title");
              }

              @Override
              protected int getConfiguredWidth() {
                return 250;
              }
            }

            @Order(3)
            public class StartsColumn extends AbstractDateTimeColumn {

              @Override
              protected String getConfiguredHeaderText() {
                return TEXTS.get("Starts");
              }

              @Override
              protected int getConfiguredSortIndex() {
                return 0;
              }

              @Override
              protected int getConfiguredWidth() {
                return 120;
              }

            }

            @Order(4)
            public class CityColumn extends AbstractStringColumn {

              @Override
              protected String getConfiguredHeaderText() {
                return TEXTS.get("City");
              }

              @Override
              protected int getConfiguredWidth() {
                return 120;
              }
            }

            @Order(5)
            public class CountryColumn extends AbstractStringColumn {

              @Override
              protected String getConfiguredHeaderText() {
                return TEXTS.get("Country");
              }
            }
          }
        }
      }

      @Order(5000)
      public class NotesBox extends AbstractGroupBox {

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Notes");
        }

        @Order(10)
        public class NotesField extends AbstractStringField {

          @Override
          protected int getConfiguredGridH() {
            return 4;
          }

          @Override
          protected boolean getConfiguredLabelVisible() {
            return false;
          }

          @Override
          protected boolean getConfiguredMultilineText() {
            return true;
          }
        }
      }
    }

    @Order(30)
    public class OkButton extends AbstractOkButton {
    }

    @Order(40)
    public class CancelButton extends AbstractCancelButton {
    }
  }

  public class ModifyHandler extends AbstractDirtyFormHandler {

    @Override
    protected void execLoad() {
      IPersonService service = BEANS.get(IPersonService.class);
      PersonFormData formData = new PersonFormData();
      exportFormData(formData);
      formData = service.load(formData);
      importFormData(formData);

      getForm().setSubTitle(calculateSubTitle());
    }

    @Override
    protected void execStore() {
      IPersonService service = BEANS.get(IPersonService.class);
      PersonFormData formData = new PersonFormData();
      exportFormData(formData);
      service.store(formData);
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
      IPersonService service = BEANS.get(IPersonService.class);
      PersonFormData formData = new PersonFormData();
      exportFormData(formData);
      service.create(formData);
    }

    @Override
    protected void execDirtyStatusChanged(boolean dirty) {
      getForm().setSubTitle(calculateSubTitle());
    }
  }

  @Override
  protected boolean execValidate() {
    boolean noFirstName = StringUtility.isNullOrEmpty(getFirstNameField().getValue());
    boolean noLastName = StringUtility.isNullOrEmpty(getLastNameField().getValue());

    getGeneralBox().clearErrorStatus();

    if (noFirstName && noLastName) {
      getGeneralBox().addErrorStatus(TEXTS.get("MissingName"));
      getFirstNameField().requestFocus();

      throw new VetoException(TEXTS.get("MissingName"));
    }

    return true;
  }

  private String calculateSubTitle() {
    return StringUtility.join(" ", getFirstNameField().getValue(),
        getLastNameField().getValue());
  }
}
