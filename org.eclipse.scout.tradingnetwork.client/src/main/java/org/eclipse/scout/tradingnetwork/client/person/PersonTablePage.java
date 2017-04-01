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
package org.eclipse.scout.tradingnetwork.client.person;

import java.util.Set;

import org.eclipse.scout.rt.client.dto.PageData;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.ISearchForm;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.AbstractIcons;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import org.eclipse.scout.tradingnetwork.client.Icons;
import org.eclipse.scout.tradingnetwork.client.common.CountryLookupCall;
import org.eclipse.scout.tradingnetwork.shared.organization.OrganizationLookupCall;
import org.eclipse.scout.tradingnetwork.shared.person.IPersonService;
import org.eclipse.scout.tradingnetwork.shared.person.PersonTablePageData;

@PageData(PersonTablePageData.class)
public class PersonTablePage extends AbstractPageWithTable<PersonTablePage.Table> {

  private String organizationId;

  public String getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(String organizationId) {
    this.organizationId = organizationId;
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Persons"); // <1>
  }

  @Override
  protected IPage<?> execCreateChildPage(ITableRow row) {
    PersonNodePage childPage = new PersonNodePage();
    childPage.setPersonId(getTable().getPersonIdColumn().getValue(row));
    return childPage;
  }

  @Override
  protected void execLoadData(SearchFilter filter) {
    importPageData(BEANS.get(IPersonService.class)
        .getPersonTableData(filter, getOrganizationId()));
  }

  @Override // <3>
  protected boolean getConfiguredLeaf() {
    return false;
  }

  public class Table extends AbstractTable {

    public LastNameColumn getLastNameColumn() {
      return getColumnSet().getColumnByClass(LastNameColumn.class);
    }

    @Override
    protected String getConfiguredDefaultIconId() {
      return Icons.Person;
    }

    @Override
    protected Class<? extends IMenu> getConfiguredDefaultMenu() { // <1>
      return EditMenu.class;
    }

    public CountryColumn getCountryColumn() {
      return getColumnSet().getColumnByClass(CountryColumn.class);
    }

    public EmailColumn getEmailColumn() {
      return getColumnSet().getColumnByClass(PersonTablePage.Table.EmailColumn.class);
    }

    public OrganizationColumn getOrganizationColumn() {
      return getColumnSet().getColumnByClass(OrganizationColumn.class);
    }

    public CityColumn getCityColumn() {
      return getColumnSet().getColumnByClass(CityColumn.class);
    }

    public FirstNameColumn getFirstNameColumn() {
      return getColumnSet().getColumnByClass(FirstNameColumn.class);
    }

    public PersonIdColumn getPersonIdColumn() {
      return getColumnSet().getColumnByClass(PersonIdColumn.class);
    }

    @Order(10)
    public class EditMenu extends AbstractMenu {

      @Override
      protected String getConfiguredIconId() {
        return Icons.Pencil;
      }

      @Override
      protected String getConfiguredKeyStroke() {
        return "alt-e";
      }

      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Edit");
      }

      @Override
      protected void execAction() {
        PersonForm form = new PersonForm();
        form.setPersonId(getPersonIdColumn().getSelectedValue()); // <2>
        form.addFormListener(new PersonFormListener());
        // start the form using its modify handler
        form.startModify();
      }
    }

    @Order(20)
    public class NewMenu extends AbstractMenu {

      @Override
      protected String getConfiguredKeyStroke() {
        return "alt-n";
      }

      @Override
      protected String getConfiguredIconId() {
        // get unicode from http://fontawesome.io/icon/magic/
        return "font:awesomeIcons \uf0d0";
      }

      @Override
      protected String getConfiguredText() {
        return TEXTS.get("New");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.<IMenuType> hashSet(
            TableMenuType.EmptySpace, TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        PersonForm form = new PersonForm();
        form.getOrganizationField().setValue(getOrganizationId());
        form.addFormListener(new PersonFormListener());
        // start the form using its new handler
        form.startNew();
      }
    }

    private class PersonFormListener implements FormListener {

      @Override
      public void formChanged(FormEvent e) {
        // reload page to reflect new/changed data after saving any changes
        if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
          reloadPage();
        }
      }
    }

    @Order(1)
    public class PersonIdColumn extends AbstractStringColumn {

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
    public class FirstNameColumn extends AbstractStringColumn {

      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("FirstName");
      }

      @Override
      protected int getConfiguredWidth() {
        return 120;
      }
    }

    @Order(3)
    public class LastNameColumn extends AbstractStringColumn {

      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("LastName");
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
    public class CountryColumn extends AbstractSmartColumn<String> {

      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Country");
      }

      @Override
      protected int getConfiguredWidth() {
        return 120;
      }

      @Override
      protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
        return CountryLookupCall.class;
      }
    }

    @Order(6)
    public class PhoneColumn extends AbstractStringColumn {

      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Phone");
      }

      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }

      @Override
      protected int getConfiguredWidth() {
        return 120;
      }
    }

    @Order(7)
    public class MobileColumn extends AbstractStringColumn {

      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Mobile");
      }

      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }

      @Override
      protected int getConfiguredWidth() {
        return 120;
      }
    }

    @Order(8)
    public class EmailColumn extends AbstractStringColumn {

      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Email");
      }

      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }

      @Override
      protected int getConfiguredWidth() {
        return 200;
      }
    }

    @Order(9)
    public class OrganizationColumn extends AbstractSmartColumn<String> {

      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Organization");
      }

      @Override
      protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
        return OrganizationLookupCall.class;
      }

      @Override
      protected int getConfiguredWidth() {
        return 200;
      }
    }

    @Order(10)
    public class EventsColumn extends AbstractLongColumn {

      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Events");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }
  }

  @Override
  protected String getConfiguredIconId() {
    return AbstractIcons.Person;
  }

  @Override
  protected Class<? extends ISearchForm> getConfiguredSearchForm() {
    return PersonSearchForm.class;
  }
}
