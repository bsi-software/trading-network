package org.eclipse.scout.tradingnetwork.client.organization;

import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractDecimalColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.form.fields.decimalfield.IDecimalField;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;

import org.eclipse.scout.tradingnetwork.shared.order.CurrencyCodeType;

public abstract class AbstractOrganizationBankAccountTable extends AbstractTable {

  public BalanceColumn getBalanceColumn() {
    return getColumnSet().getColumnByClass(BalanceColumn.class);
  }

  public OrganizationIdColumn getOrganizationIdColumn() {
    return getColumnSet().getColumnByClass(OrganizationIdColumn.class);
  }

  public CurrencyColumn getCurrencyColumn() {
    return getColumnSet().getColumnByClass(CurrencyColumn.class);
  }

  @Order(0)
  public class OrganizationIdColumn extends AbstractStringColumn {
    @Override
    protected String getConfiguredHeaderText() {
      return TEXTS.get("OrganizationId");
    }

    @Override
    protected boolean getConfiguredVisible() {
      return false;
    }

    @Override
    protected int getConfiguredWidth() {
      return 100;
    }
  }

  @Order(1000)
  public class CurrencyColumn extends AbstractSmartColumn<String> {
    @Override
    protected String getConfiguredHeaderText() {
      return TEXTS.get("Currency");
    }

    @Override
    protected int getConfiguredWidth() {
      return 100;
    }

    @Override
    protected Class<? extends ICodeType<?, String>> getConfiguredCodeType() {
      return CurrencyCodeType.class;
    }
  }

  @Order(2000)
  public class BalanceColumn extends AbstractDecimalColumn<Double> {

    @Override
    protected String getConfiguredHeaderText() {
      return TEXTS.get("Balance");
    }

    @Override
    protected int getConfiguredWidth() {
      return 100;
    }

    @Override
    protected IDecimalField<Double> createDefaultEditor() {
      return null;
    }

    @Override
    protected Double getConfiguredMinValue() {
      return null;
    }

    @Override
    protected Double getConfiguredMaxValue() {
      return null;
    }
  }

}
