package org.eclipse.scout.tradingnetwork.server.sql;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.CreateImmediately;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.platform.context.RunContext;
import org.eclipse.scout.rt.platform.exception.ExceptionHandler;
import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.holders.StringArrayHolder;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// tag::service[]
@ApplicationScoped
@CreateImmediately
public class DatabaseSetupService implements IDataStoreService {
  private static final Logger LOG = LoggerFactory.getLogger(DatabaseSetupService.class);

  private static boolean databaseReady = false;

  @Override
  public boolean dataStoreIsReady() {
    return databaseReady;
  }

  @PostConstruct
  public void autoCreateDatabase() {
    if (CONFIG.getPropertyValue(DatabaseProperties.DatabaseAutoCreateProperty.class)) {
      try {
        RunContext context = BEANS.get(SuperUserRunContextProducer.class).produce();
        IRunnable runnable = new IRunnable() {

          @Override
          public void run() throws Exception {
            dropDataStore();
            createDataStore();
          }
        };

        context.run(runnable);
      }
      catch (RuntimeException e) {
        BEANS.get(ExceptionHandler.class).handle(e);
      }
    }

    databaseReady = true;
  }

  public void createOrganizationTable() {
    if (!getExistingTables().contains("ORGANIZATION")) {
      SQL.insert(SQLs.ORGANIZATION_CREATE_TABLE);
      LOG.info("Database table 'ORGANIZATION' created");

      if (CONFIG.getPropertyValue(DatabaseProperties.DatabaseAutoPopulateProperty.class)) {
        SQL.insert(SQLs.ORGANIZATION_INSERT_SAMPLE + SQLs.ORGANIZATION_VALUES_01);
        SQL.insert(SQLs.ORGANIZATION_INSERT_SAMPLE + SQLs.ORGANIZATION_VALUES_02);
        SQL.insert(SQLs.ORGANIZATION_INSERT_SAMPLE + SQLs.ORGANIZATION_VALUES_03);
        SQL.insert(SQLs.ORGANIZATION_INSERT_SAMPLE + SQLs.ORGANIZATION_VALUES_04);
        SQL.insert(SQLs.ORGANIZATION_INSERT_SAMPLE + SQLs.ORGANIZATION_VALUES_05);

        // connect userid with organizations
        SQL.update("update ORGANIZATION set user_id = 'abb' where organization_id='org02'");
//        SQL.update("update ORGANIZATION set user_id = 'nestle' where organization_id='org03'");
//        SQL.update("update ORGANIZATION set user_id = 'roche' where organization_id='org04'");
        SQL.update("update ORGANIZATION set user_id = 'munichre' where organization_id='org03'");
        SQL.update("update ORGANIZATION set user_id = 'bmw' where organization_id='org04'");
        SQL.update("update ORGANIZATION set user_id = 'swissre' where organization_id='org05'");

        LOG.info("Database table 'ORGANIZATION' populated with sample data");
      }
    }
  }

  private void createPersonTable() {
    if (!getExistingTables().contains("PERSON")) {
      SQL.insert(SQLs.PERSON_CREATE_TABLE);
      LOG.info("Database table 'PERSON' created");

      if (CONFIG.getPropertyValue(DatabaseProperties.DatabaseAutoPopulateProperty.class)) {
        SQL.insert(SQLs.PERSON_INSERT_SAMPLE + SQLs.PERSON_VALUES_01);
        SQL.insert(SQLs.PERSON_INSERT_SAMPLE + SQLs.PERSON_VALUES_02);
        SQL.insert(SQLs.PERSON_INSERT_SAMPLE + SQLs.PERSON_VALUES_03);
        SQL.insert(SQLs.PERSON_INSERT_SAMPLE + SQLs.PERSON_VALUES_04);
        SQL.insert(SQLs.PERSON_INSERT_SAMPLE + SQLs.PERSON_VALUES_05);
        LOG.info("Database table 'PERSON' populated with sample data");
      }
    }
  }

  private void createDealTable() {
    if (!getExistingTables().contains("DEAL")) {
      SQL.insert(SQLs.DEAL_CREATE_TABLE);
      LOG.info("Database table 'DEAL' created");
    }

//    TransactionReceipt receipt = getContract(order.getCurrencyPair())
//        .createOrder(
//            dealQuantity,
//            dealPrice,
//            buy,
//            extId)
//        .get();

    if (CONFIG.getPropertyValue(DatabaseProperties.DatabaseAutoPopulateProperty.class)) {
      SQL.insert(SQLs.DEAL_INSERT_SAMPLE + SQLs.DEAL_VALUES_01);
      SQL.insert(SQLs.DEAL_INSERT_SAMPLE + SQLs.DEAL_VALUES_02);
      SQL.insert(SQLs.DEAL_INSERT_SAMPLE + SQLs.DEAL_VALUES_03);
      SQL.insert(SQLs.DEAL_INSERT_SAMPLE + SQLs.DEAL_VALUES_04);
      LOG.info("Database table 'DEAL' populated with sample data");
    }

  }

  private void createDeployedOrderBookTable() {
    if (!getExistingTables().contains("DEPLOYED_ORDER_BOOK")) {
      SQL.insert(SQLs.DEPLOYED_ORDER_BOOK_CREATE);
      LOG.info("Database table 'DEPLOYED_ORDER_BOOK' created");
    }
  }

  private void createBankAccountTable() {
    if (!getExistingTables().contains("BANK_ACCOUNT")) {
      SQL.insert(SQLs.BANK_ACCOUNT_CREATE);
      LOG.info("Database table 'BANK_ACCOUNT' created");
    }
  }

  private void createAccountTable() {
    if (!getExistingTables().contains("ACCOUNT")) {
      SQL.insert(SQLs.ACCOUNT_CREATE_TABLE);
      LOG.info("Database table 'ACCOUNT' created");
    }
  }

  private void createDealSequence() {
    try {
      SQL.insert(SQLs.DEAL_SEQUENCE_CREATE);
      LOG.info("Database sequence created: " + SQLs.DEAL_SEQUENCE_CREATE);
    }
    catch (Exception e) {
      LOG.error("Creating sequence failed: " + SQLs.DEAL_SEQUENCE_CREATE);
    }
  }

  private void dropDealSequence() {
    try {
      SQL.update(SQLs.DEAL_SEQUENCE_DROP);
      LOG.info("Database sequence created: " + SQLs.DEAL_SEQUENCE_DROP);
    }
    catch (Exception e) {
      LOG.error("Dropping sequence failed: " + SQLs.DEAL_SEQUENCE_DROP);
    }
  }

  private Set<String> getExistingTables() {
    StringArrayHolder tables = new StringArrayHolder();
    SQL.selectInto(SQLs.SELECT_TABLE_NAMES, new NVPair("result", tables)); // <1>
    return CollectionUtility.hashSet(tables.getValue());
  }
  // end::service[]

  @Override
  public void dropDataStore() {
    Set<String> tables = getExistingTables();

    if (tables.contains("BANK_ACCOUNT")) {
      SQL.update(SQLs.BANK_ACCOUNT_DROP);
    }
    if (tables.contains("DEAL")) {
      SQL.update(SQLs.DEAL_DROP_TABLE);
    }
    if (tables.contains("DEPLOYED_ORDER_BOOK")) {
      SQL.update(SQLs.DEPLOYED_ORDER_BOOK_DROP_TABLE);
    }
    if (tables.contains("ACCOUNT")) {
      SQL.update(SQLs.ACCOUNT_DROP_TABLE);
    }
    if (tables.contains("PERSON")) {
      SQL.update(SQLs.PERSON_DROP_TABLE);
    }
    if (tables.contains("ORGANIZATION")) {
      SQL.update(SQLs.ORGANIZATION_DROP_TABLE);
    }

    dropDealSequence();
  }

  @Override
  public void createDataStore() {
    createOrganizationTable();
    createPersonTable();
    createAccountTable();
    createDealTable();
    createDeployedOrderBookTable();
    createBankAccountTable();

    createDealSequence();
  }
  // tag::service[]

}
// end::service[]
