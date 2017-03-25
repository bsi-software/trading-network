package com.bsiag.ethereum.fxtradingnetwork.client.organization;

import java.util.Set;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import com.bsiag.ethereum.fxtradingnetwork.client.Icons;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.IOrganizationBankAccountService;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.OrganizationBankAccountTablePageData;

@Data(OrganizationBankAccountTablePageData.class)
public class OrganizationBankAccountTablePage extends AbstractPageWithTable<OrganizationBankAccountTablePage.Table> {

  private String m_organizationId;

  public String getOrganizationId() {
    return m_organizationId;
  }

  public void setOrganizationId(String organiztaionId) {
    m_organizationId = organiztaionId;
  }

  @Override
  protected String getConfiguredTitle() {
    // TODO [uko] verify translation
    return TEXTS.get("AccountBalance");
  }

  @Override
  protected void execInitPage() {
    OrganizationNodePage organizationParentPage = getParentNode(OrganizationNodePage.class);

    if (organizationParentPage != null) {
      setOrganizationId(organizationParentPage.getOrganizationId());
    }
  }

  @Override
  protected void execLoadData(SearchFilter filter) {
    importPageData(BEANS.get(IOrganizationBankAccountService.class).getOrganizationBankAccountTableData(filter, getOrganizationId()));
  }

  public class Table extends AbstractOrganizationBankAccountTable {

    @Order(1000)
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
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        OrganizationBankAccountForm form = new OrganizationBankAccountForm();
        form.addFormListener(new FormListener() {

          @Override
          public void formChanged(FormEvent e) {
            if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
              reloadPage();
            }
          }
        });

        String currencyUId = getCurrencyColumn().getSelectedValue();
        form.startModify(getOrganizationId(), currencyUId);
      }
    }

    @Order(2000)
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
      protected void execAction() {
        OrganizationBankAccountForm form = new OrganizationBankAccountForm();
        form.addFormListener(new FormListener() {

          @Override
          public void formChanged(FormEvent e) {
            if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
              reloadPage();
            }
          }
        });

        form.startNew(getOrganizationId());
      }
    }

  }
}
