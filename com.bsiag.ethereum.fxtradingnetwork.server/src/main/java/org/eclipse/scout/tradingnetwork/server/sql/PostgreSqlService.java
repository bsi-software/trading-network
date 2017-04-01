package org.eclipse.scout.tradingnetwork.server.sql;

import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.server.jdbc.postgresql.AbstractPostgreSqlService;

import org.eclipse.scout.tradingnetwork.server.sql.DatabaseProperties.JdbcMappingNameProperty;
import org.eclipse.scout.tradingnetwork.server.sql.DatabaseProperties.JdbcPasswordProperty;
import org.eclipse.scout.tradingnetwork.server.sql.DatabaseProperties.JdbcUsernameProperty;

/**
 * <h3>{@link PostgreSqlService}</h3>
 *
 * @author uko
 */
public class PostgreSqlService extends AbstractPostgreSqlService {

  @Override
  protected String getConfiguredJdbcMappingName() {
//    String host = "10.0.42.146:5432"; // Docker IP as obtained from 'docker-machine ip default'
//    return "jdbc:postgresql://" + host + "/postgres";

    String mappingName = CONFIG.getPropertyValue(JdbcMappingNameProperty.class);

    return mappingName;
  }

  @Override
  protected String getConfiguredUsername() {
    return CONFIG.getPropertyValue(JdbcUsernameProperty.class);
  }

  @Override
  protected String getConfiguredPassword() {
    return CONFIG.getPropertyValue(JdbcPasswordProperty.class);
  }
}
