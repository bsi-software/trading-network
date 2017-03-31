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
package com.bsiag.ethereum.fxtradingnetwork.server.sql;

import javax.security.auth.Subject;

import org.eclipse.scout.rt.platform.config.AbstractBooleanConfigProperty;
import org.eclipse.scout.rt.platform.config.AbstractStringConfigProperty;
import org.eclipse.scout.rt.platform.config.AbstractSubjectConfigProperty;

public class DatabaseProperties {

  public static class DatabaseAutoCreateProperty extends AbstractBooleanConfigProperty {
    // defines default value and key

    @Override
    protected Boolean getDefaultValue() {
      return Boolean.FALSE;
    }

    @Override
    public String getKey() {
      return "fxtradingnetwork.database.autocreate";
    }
  }

  public static class DatabaseAutoPopulateProperty extends AbstractBooleanConfigProperty {

    @Override
    protected Boolean getDefaultValue() {
      return Boolean.FALSE;
    }

    @Override
    public String getKey() {
      return "fxtradingnetwork.database.autopopulate";
    }
  }

  public static class JdbcMappingNameProperty extends AbstractStringConfigProperty {

    @Override
    protected String getDefaultValue() {
      return "jdbc:postgresql://10.0.42.146:5432/postgres";
    }

    @Override
    public String getKey() {
      return "fxtradingnetwork.database.jdbc.mapping.name";
    }
  }

  public static class JdbcUsernameProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return "fxtradingnetwork.database.jdbc.username";
    }

  }

  public static class JdbcPasswordProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return "fxtradingnetwork.database.jdbc.password";
    }

  }

  public static class SuperUserSubjectProperty extends AbstractSubjectConfigProperty {

    @Override
    protected Subject getDefaultValue() {
      return convertToSubject("system");
    }

    @Override
    public String getKey() {
      return "fxtradingnetwork.superuser";
    }
  }
}
