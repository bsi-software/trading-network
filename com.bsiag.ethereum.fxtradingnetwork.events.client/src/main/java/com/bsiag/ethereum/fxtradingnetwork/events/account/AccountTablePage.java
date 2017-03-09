package com.bsiag.ethereum.fxtradingnetwork.events.account;

import java.util.Set;

import com.bsiag.ethereum.fxtradingnetwork.client.Icons;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonNodePage;
import com.bsiag.ethereum.fxtradingnetwork.events.account.AccountTablePageData;
import com.bsiag.ethereum.fxtradingnetwork.events.account.IAccountService;
import com.bsiag.ethereum.fxtradingnetwork.events.account.AccountTablePage.Table;
import com.bsiag.ethereum.fxtradingnetwork.shared.person.PersonLookupCall;
import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBigDecimalColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

@Data(AccountTablePageData.class)
public class AccountTablePage extends AbstractPageWithTable<Table> {

  private String personId = null;

  public String getPersonId() {
    return personId;
  }

  public void setPersonId(String personId) {
    this.personId = personId;
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("AccountTablePage");
  }

  @Override
  protected void execInitPage() {
    if (getParentPage() instanceof PersonNodePage) {
      String person = ((PersonNodePage) getParentPage()).getPersonId();
      setPersonId(person);
      getTable().getPersonColumn().setVisible(person != null);
    }
  }

  @Override
  protected IPage<?> execCreateChildPage(ITableRow row) {
    TransactionTablePage childPage = new TransactionTablePage();
    childPage.getCellForUpdate().setText(childPage.getConfiguredTitle());
    childPage.setAddress(getTable().getAddressColumn().getValue(row));
    return childPage;
  }

  @Override
  protected void execLoadData(SearchFilter filter) {
    importPageData(BEANS.get(IAccountService.class).getAccountTableData(filter, getPersonId()));
  }

  public class Table extends AbstractTable {

    public AccountNameColumn getAccountNameColumn() {
      return getColumnSet().getColumnByClass(AccountNameColumn.class);
    }

    public PersonColumn getPersonColumn() {
      return getColumnSet().getColumnByClass(PersonColumn.class);
    }

    public BalanceColumn getBalanceColumn() {
      return getColumnSet().getColumnByClass(BalanceColumn.class);
    }

    public AddressColumn getAddressColumn() {
      return getColumnSet().getColumnByClass(AddressColumn.class);
    }

    @Order(1000)
    public class NewMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("New");
      }

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
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection, TableMenuType.EmptySpace);
      }

      @Override
      protected void execInitAction() {
        setVisible(getPersonId() == null);
        super.execInitAction();
      }

      @Override
      protected void execAction() {
        AccountForm form = new AccountForm();
        form.setTitle(TEXTS.get("CreateAccount"));
        form.getPersonField().setValue(getPersonId());
        form.startNew();

        form.waitFor();
        if (form.isFormStored()) {
          reloadPage();
        }
      }
    }

    @Order(2000)
    public class EditMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Edit");
      }

      @Override
      protected String getConfiguredIconId() {
        return Icons.Pencil;
      }

      @Override
      protected String getConfiguredKeyStroke() {
        return "alt-e";
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        String address = getAddressColumn().getSelectedValue();
        AccountForm form = new AccountForm();
        form.setQrCode(address);
        form.getAddressField().setValue(address);
        form.startModify();

        form.waitFor();
        if (form.isFormStored()) {
          reloadPage();
        }
      }
    }

    @Order(3000)
    public class CreateAliceLenaTransactionMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("AddTransaction");
      }

      @Override
      protected String getConfiguredIconId() {
        // get unicode from http://fontawesome.io/icon/plus/
        return "font:awesomeIcons \uf067";
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        TransactionForm form = new TransactionForm();

        if (getTable().getSelectedRowCount() == 1) {
          String person = getTable().getPersonColumn().getSelectedValue();
          String address = getTable().getAddressColumn().getSelectedValue();

          form.getFromField().setValue(address);
          form.getFromBox().getPersonField().setValue(person);
          form.getFromBox().getAccountField().setValue(address);
          form.getToBox().getPersonField().requestFocus();
        }

        form.startNew();
      }
    }

    @Order(0)
    public class PersonColumn extends AbstractSmartColumn<String> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Person");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }

      @Override
      protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
        return PersonLookupCall.class;
      }
    }

    @Order(1000)
    public class AddressColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("AccountAddress");
      }

      @Override
      protected boolean getConfiguredPrimaryKey() {
        return true;
      }

      @Override
      protected int getConfiguredWidth() {
        return 200;
      }
    }

    @Order(2000)
    public class AccountNameColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Name");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(3000)
    public class BalanceColumn extends AbstractBigDecimalColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Balance");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }
  }
}
