package org.eclipse.scout.tradingnetwork.client.organization;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.labelfield.AbstractLabelField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;
import org.eclipse.scout.tradingnetwork.client.ClientSession;
import org.eclipse.scout.tradingnetwork.client.Icons;
import org.eclipse.scout.tradingnetwork.client.common.AbstractUrlImageField;
import org.eclipse.scout.tradingnetwork.client.common.CountryLookupCall;
import org.eclipse.scout.tradingnetwork.client.organization.OrganizationOverview.MainBox.GeneralBox;
import org.eclipse.scout.tradingnetwork.client.organization.OrganizationOverview.MainBox.GeneralBox.CountryField;
import org.eclipse.scout.tradingnetwork.client.organization.OrganizationOverview.MainBox.GeneralBox.GreetingField;
import org.eclipse.scout.tradingnetwork.client.organization.OrganizationOverview.MainBox.GeneralBox.OrganizationLogoField;
import org.eclipse.scout.tradingnetwork.client.organization.OrganizationOverview.MainBox.GeneralBox.OverviewBox;
import org.eclipse.scout.tradingnetwork.client.organization.OrganizationOverview.MainBox.GeneralBox.OverviewBox.OverviewTableField;
import org.eclipse.scout.tradingnetwork.client.organization.OrganizationOverview.MainBox.GeneralBox.RefreshButton;
import org.eclipse.scout.tradingnetwork.shared.organization.IOrganizationOverviewPageService;
import org.eclipse.scout.tradingnetwork.shared.organization.OrganizationOverviewData;

@FormData(value = OrganizationOverviewData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class OrganizationOverview extends AbstractForm {

  private String m_userId = "";
  private String m_organizationName = "";

  @FormData
  public String getUserId() {
    return m_userId;
  }

  @FormData
  public void setUserId(String userName) {
    m_userId = userName;
  }

  @FormData
  public String getOrganizationName() {
    return m_organizationName;
  }

  @FormData
  public void setOrganizationName(String organizationName) {
    m_organizationName = organizationName;
  }

  public OverviewTableField getOverviewField() {
    return getFieldByClass(OverviewTableField.class);
  }

//  public AccountBalanceTableField getAccountBalanceTableField() {
//    return getFieldByClass(AccountBalanceTableField.class);
//  }

  public CountryField getCountryField() {
    return getFieldByClass(CountryField.class);
  }

//  public AccountBalanceBox getAccountBalanceBox() {
//    return getFieldByClass(AccountBalanceBox.class);
//  }

  public OverviewBox getOverviewBox() {
    return getFieldByClass(OverviewBox.class);
  }

  public RefreshButton getRefreshButton() {
    return getFieldByClass(RefreshButton.class);
  }

  public OrganizationLogoField getOrganizationLogoField() {
    return getFieldByClass(OrganizationLogoField.class);
  }

  public GeneralBox getHeaderBox() {
    return getFieldByClass(GeneralBox.class);
  }

  public GreetingField getGreetingField() {
    return getFieldByClass(GreetingField.class);
  }

  @Override
  public void start() {
    setUserId(ClientSession.get().getUserId());
    startInternal(new DefaultHandler());
  }

  @Override
  protected void execFormActivated() {
//    registerDataChangeListener(ListenerObjectEnum.ORGANIZATION_OVERVIEW);
  }

  @Override
  protected void execDataChanged(Object... dataTypes) {
    updateOverview();
  }

  public class MainBox extends AbstractGroupBox {

//    @Override
//    protected boolean getConfiguredBorderVisible() {
//      return true;
//    }
//
//    @Override
//    protected boolean getConfiguredFillHorizontal() {
//      return true;
//    }
//
//    @Override
//    protected boolean getConfiguredFillVertical() {
//      return true;
//    }
//
//    @Override
//    protected int getConfiguredGridColumnCount() {
//      return 2;
//    }
//
//    @Override
//    protected int getConfiguredGridH() {
//      return super.getConfiguredGridH();
//    }
//
//    @Override
//    protected int getConfiguredGridW() {
//      return super.getConfiguredGridW();
//    }

    @Order(1000)
    public class GeneralBox extends AbstractGroupBox {

//      @Override
//      protected int getConfiguredGridColumnCount() {
//        return 1;
//      }
//
//      @Override
//      protected int getConfiguredGridH() {
//        return 4;
//      }
//
//      @Override
//      protected int getConfiguredGridW() {
//        return 1;
//      }

      @Override
      protected String getConfiguredLabel() {
        return "";
      }

      @Override
      protected boolean getConfiguredLabelVisible() {
        return false;
      }

      @Order(1000)
      public class GreetingField extends AbstractLabelField {

        @Override
        protected int getConfiguredGridW() {
          return 1;
        }

        @Override
        protected boolean getConfiguredLabelVisible() {
          return false;
        }

        @Override
        protected String getConfiguredFont() {
          return "BOLD";
        }

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("OrganizationOverviewGreetings");
        }

        @Override
        protected void execInitField() {
          setValue(TEXTS.get("OrganizationOverviewGreetings"));
          super.execInitField();
        }
      }

      @Order(2000)
      public class CountryField extends AbstractLabelField {

        @Override
        protected int getConfiguredGridW() {
          return 1;
        }

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Country");
        }

        @Override
        protected void execInitField() {
          super.execInitField();
        }

        @Override
        protected String execFormatValue(String value) {
          CountryLookupCall lookup = new CountryLookupCall();
          lookup.setKey(value);
          for (ILookupRow<String> row : lookup.getDataByKey()) {
            value = row.getText();
            break;
          }
          return super.execFormatValue(value);
        }
      }

      /*
      @Order(2000)
      public class AccountBalanceBox extends AbstractGroupBox {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Balance");
        }
      
        @Override
        protected int getConfiguredGridW() {
          return 1;
        }
      
        @Order(1000)
        public class AccountBalanceTableField extends AbstractTableField<AccountBalanceTableField.Table> {
      
          public class Table extends AbstractOrganizationBankAccountTable {
      
            @Order(1000)
            public class ReloadMenu extends AbstractMenu {
              @Override
              protected String getConfiguredText() {
                return TEXTS.get("Refresh");
              }
      
              @Override
              protected Set<? extends IMenuType> getConfiguredMenuTypes() {
                return CollectionUtility.hashSet(TableMenuType.Header);
              }
      
              @Override
              protected void execAction() {
      
              }
            }
      
          }
      
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Balance");
          }
      
          @Override
          protected boolean getConfiguredLabelVisible() {
            return false;
          }
      
          @Override
          protected int getConfiguredGridW() {
            return 1;
          }
        }
      }
      */

      @Order(2500)
      public class RefreshButton extends AbstractButton {

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("RefreshOverview");
        }

        @Override
        protected String getConfiguredIconId() {
          return "font:awesomeIcons \uf021";
        }

        @Override
        protected boolean getConfiguredProcessButton() {
          return false;
        }

        @Override
        protected void execClickAction() {
          updateOverview();
        }
      }

      @Order(3000)
      public class OrganizationLogoField extends AbstractUrlImageField {

        @Override
        protected String getConfiguredLabel() {
          return "";
        }

        @Override
        protected boolean getConfiguredAutoFit() {
          return true;
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
        protected boolean getConfiguredLabelVisible() {
          return false;
        }

        @Override
        protected String getConfiguredImageId() {
          return Icons.Organization;
        }

        @Override
        protected boolean getConfiguredEnabled() {
          return false;
        }
      }

      /*
      @Order(2000)
      public class AccountBalanceBox extends AbstractGroupBox {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Balance");
        }
      
        @Override
        protected int getConfiguredGridW() {
          return 1;
        }
      
        @Order(1000)
        public class AccountBalanceTableField extends AbstractTableField<AccountBalanceTableField.Table> {
      
          public class Table extends AbstractOrganizationBankAccountTable {
      
            @Order(1000)
            public class ReloadMenu extends AbstractMenu {
              @Override
              protected String getConfiguredText() {
                return TEXTS.get("Refresh");
              }
      
              @Override
              protected Set<? extends IMenuType> getConfiguredMenuTypes() {
                return CollectionUtility.hashSet(TableMenuType.Header);
              }
      
              @Override
              protected void execAction() {
      
              }
            }
      
          }
      
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Balance");
          }
      
          @Override
          protected boolean getConfiguredLabelVisible() {
            return false;
          }
      
          @Override
          protected int getConfiguredGridW() {
            return 1;
          }
        }
      }
      */

      @Order(4000)
      public class OverviewBox extends AbstractGroupBox {

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Overview");
        }

        @Override
        protected int getConfiguredGridW() {
          return 2;
        }

        @Override
        protected int getConfiguredGridH() {
          return 3;
        }

        @Order(1000)
        public class OverviewTableField extends AbstractTableField<OverviewTableField.Table> {
          public class Table extends AbstractTable {

            @Override
            protected boolean getConfiguredMultiSelect() {
              return false;
            }

            public CountColumn getCountColumn() {
              return getColumnSet().getColumnByClass(CountColumn.class);
            }

            public InfoTypeColumn getInfoTypeColumn() {
              return getColumnSet().getColumnByClass(InfoTypeColumn.class);
            }

            @Order(1000)
            public class InfoTypeColumn extends AbstractStringColumn {
              @Override
              protected String getConfiguredHeaderText() {
                return TEXTS.get("Information");
              }

              @Override
              protected int getConfiguredWidth() {
                return 300;
              }
            }

            @Order(2000)
            public class CountColumn extends AbstractLongColumn {
              @Override
              protected String getConfiguredHeaderText() {
                return TEXTS.get("Count");
              }

              @Override
              protected int getConfiguredWidth() {
                return 100;
              }
            }

          }

          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Overview");
          }

          @Override
          protected boolean getConfiguredLabelVisible() {
            return false;
          }

          @Override
          protected int getConfiguredGridW() {
            return 1;
          }
        }
      }

    }

    /*
    @Order(2000)
    public class AccountBalanceBox extends AbstractGroupBox {
      @Override
      protected String getConfiguredLabel() {
        return TEXTS.get("Balance");
      }

      @Override
      protected int getConfiguredGridW() {
        return 1;
      }

      @Order(1000)
      public class AccountBalanceTableField extends AbstractTableField<AccountBalanceTableField.Table> {

        public class Table extends AbstractOrganizationBankAccountTable {

          @Order(1000)
          public class ReloadMenu extends AbstractMenu {
            @Override
            protected String getConfiguredText() {
              return TEXTS.get("Refresh");
            }

            @Override
            protected Set<? extends IMenuType> getConfiguredMenuTypes() {
              return CollectionUtility.hashSet(TableMenuType.Header);
            }

            @Override
            protected void execAction() {

            }
          }

        }

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Balance");
        }

        @Override
        protected boolean getConfiguredLabelVisible() {
          return false;
        }

        @Override
        protected int getConfiguredGridW() {
          return 1;
        }
      }
    }
    */

  }

  public class DefaultHandler extends AbstractFormHandler {
    @Override
    protected void execLoad() {
      updateOverview();
    }

    @Override
    protected void execStore() {
    }
  }

  private void updateOverview() {
    IOrganizationOverviewPageService service = BEANS.get(IOrganizationOverviewPageService.class);
    OrganizationOverviewData overviewData = new OrganizationOverviewData();
    exportFormData(overviewData);
    overviewData = service.loadPageData(overviewData);
    importFormData(overviewData);
  }

}
