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
package org.eclipse.scout.tradingnetwork.server.sql;

public interface SQLs {

//  String SELECT_TABLE_NAMES = ""
//      + "SELECT   UPPER(tablename) "
//      + "FROM     sys.systables "
//      + "INTO     :result";

  // Postgresql
  String SELECT_TABLE_NAMES = ""
      + "SELECT   UPPER(table_name) "
      + "FROM     information_schema.tables "
      + "WHERE    table_schema='public' "
      + "AND      table_type='BASE TABLE' "
      + "INTO     :result";

  String ORGANIZATION_CREATE_TABLE = ""
      + "CREATE   TABLE ORGANIZATION "
      + "         (organization_id VARCHAR(64) NOT NULL CONSTRAINT ORGANIZATION_PK PRIMARY KEY,"
      + "          name VARCHAR(64), "
      + "          logo_url VARCHAR(512), "
      + "          url VARCHAR(64), "
      + "          street VARCHAR(64), "
      + "          city VARCHAR(64), "
      + "          country VARCHAR(2), "
      + "          phone VARCHAR(20), "
      + "          email VARCHAR(64), "
      + "          notes VARCHAR(1024), "
      + "          user_id VARCHAR(64) "
      + "         )";

  String PERSON_CREATE_TABLE = ""
      + "CREATE   TABLE PERSON "
      + "         (person_id VARCHAR(64) NOT NULL CONSTRAINT PERSON_PK PRIMARY KEY, "
      + "          first_name VARCHAR(64), "
      + "          last_name VARCHAR(64), "
      + "          picture_url VARCHAR(512), "
      + "          date_of_birth DATE, "
      + "          gender VARCHAR(1), "
      + "          street VARCHAR(64), "
      + "          city VARCHAR(64), "
      + "          country VARCHAR(2), "
      + "          phone VARCHAR(20), "
      + "          mobile VARCHAR(20), "
      + "          email VARCHAR(64), "
      + "          organization_id VARCHAR(64), "
      + "          position VARCHAR(512), "
      + "          phone_work VARCHAR(20), "
      + "          email_work VARCHAR(64), "
      + "          notes VARCHAR(1024), "
      + "          CONSTRAINT ORGANIZATION_FK FOREIGN KEY (organization_id) REFERENCES ORGANIZATION (organization_id)"
      + "         )";
// end::createDB[]

  String PERSON_LOOKUP = ""
      + "SELECT   person_id, "
      + "         CASE "
      + "           WHEN first_name IS null "
      + "            THEN last_name "
      + "           WHEN last_name IS null "
      + "            THEN first_name "
      + "           ELSE "
      + "            first_name || ' ' || last_name "
      + "         END "
      + "FROM     PERSON "
      + "WHERE    1 = 1 "
      + "<key>    AND person_id = :key</key> "
      + "<text>   AND (UPPER(first_name) LIKE UPPER('%'||:text||'%') "
      + "         OR UPPER(last_name) LIKE UPPER('%'||:text||'%')) "
      + "</text>"
      + "<all> </all>";

  String ORGANIZATION_LOOKUP = ""
      + "SELECT   organization_id, "
      + "         name "
      + "FROM     ORGANIZATION "
      + "WHERE    1 = 1 "
      + "<key>    AND organization_id = :key</key> "
      + "<text>   AND UPPER(name) LIKE UPPER(:text||'%') </text> "
      + "<all></all>";

  String AND_LIKE_CAUSE = "AND LOWER(%s) LIKE LOWER(:%s || '%%') ";

  String ORGANIZATION_PAGE_SELECT = ""
      + "SELECT   organization_id, "
      + "         name, "
      + "         city, "
      + "         country, "
      + "         url "
      + "FROM     ORGANIZATION ";

  String ORGANIZATION_PAGE_DATA_SELECT_INTO = ""
      + "INTO     :{page.organizationId}, "
      + "         :{page.name}, "
      + "         :{page.city}, "
      + "         :{page.country}, "
      + "         :{page.homepage}";

  String ORGANIZATION_INSERT = ""
      + "INSERT   INTO "
      + "ORGANIZATION  (organization_id) "
      + "VALUES   (:organizationId)";

  String ORGANIZATION_SELECT = ""
      + "SELECT   name, "
      + "         logo_url, "
      + "         url, "
      + "         phone, "
      + "         email, "
      + "         street, "
      + "         city, "
      + "         country, "
      + "         notes "
      + "FROM     ORGANIZATION "
      + "WHERE    organization_id = :organizationId "
      + "INTO     :name, "
      + "         :picture.url, "
      + "         :homepage, "
      + "         :phone, "
      + "         :email, "
      + "         :addressBox.street, "
      + "         :addressBox.city, "
      + "         :addressBox.country, "
      + "         :notesBox.notes";

  String ORGANIZATION_UPDATE = ""
      + "UPDATE   ORGANIZATION "
      + "SET      name = :name, "
      + "         logo_url = :picture.url, "
      + "         url = :homepage, "
      + "         phone = :phone, "
      + "         email = :email, "
      + "         street = :addressBox.street, "
      + "         city = :addressBox.city, "
      + "         country = :addressBox.country, "
      + "         notes = :notesBox.notes "
      + "WHERE    organization_id = :organizationId";

  String PERSON_PAGE_SELECT = ""
      + "SELECT   person_id, "
      + "         first_name, "
      + "         last_name, "
      + "         city, "
      + "         country, "
      + "         phone, "
      + "         mobile, "
      + "         email, "
      + "         organization_id "
      + "FROM     PERSON ";

  String PERSON_PAGE_DATA_SELECT_INTO = ""
      + "INTO     :{page.personId}, "
      + "         :{page.firstName}, "
      + "         :{page.lastName}, "
      + "         :{page.city}, "
      + "         :{page.country}, "
      + "         :{page.phone}, "
      + "         :{page.mobile}, "
      + "         :{page.email}, "
      + "         :{page.organization}";

  String PERSON_INSERT = ""
      + "INSERT   INTO "
      + "PERSON  (person_id) "
      + "VALUES   (:personId)";

  String PERSON_SELECT = ""
      + "SELECT   first_name, "
      + "         last_name, "
      + "         picture_url, "
      + "         date_of_birth, "
      + "         gender, "
      + "         phone, "
      + "         mobile, "
      + "         email, "
      + "         street, "
      + "         city, "
      + "         country, "
      + "         position, "
      + "         organization_id, "
      + "         phone_work, "
      + "         email_work, "
      + "         notes "
      + "FROM     PERSON "
      + "WHERE    person_id = :personId "
      + "INTO     :firstName, "
      + "         :lastName, "
      + "         :pictureUrl, "
      + "         :dateOfBirth, "
      + "         :genderGroup, "
      + "         :phone, "
      + "         :mobile, "
      + "         :email, "
      + "         :street, "
      + "         :city, "
      + "         :country, "
      + "         :position, "
      + "         :organization, "
      + "         :phoneWork, "
      + "         :emailWork, "
      + "         :notes";

  String PERSON_UPDATE = ""
      + "UPDATE   PERSON "
      + "SET      first_name = :firstName, "
      + "         last_name = :lastName, "
      + "         picture_url = :pictureUrl, "
      + "         date_of_birth = :dateOfBirth, "
      + "         gender = :genderGroup, "
      + "         phone  = :phone, "
      + "         mobile = :mobile, "
      + "         email = :email, "
      + "         street = :street, "
      + "         city = :city, "
      + "         country = :country, "
      + "         position = :position, "
      + "         organization_id = :organization, "
      + "         phone_work = :phoneWork, "
      + "         email_work = :emailWork, "
      + "         notes = :notes "
      + "WHERE    person_id = :personId";

  String ORGANIZATION_INSERT_SAMPLE = ""
      + "INSERT   INTO ORGANIZATION "
      + "        (organization_id, "
      + "         name, "
      + "         city, "
      + "         country, "
      + "         url, "
      + "         logo_url) ";

//  String ORGANIZATION_VALUES_01 = ""
//      + "VALUES  ('org01', "
//      + "         'Alice''s Adventures in Wonderland', "
//      + "         'London', "
//      + "         'GB', "
//      + "         'http://en.wikipedia.org/wiki/Alice%27s_Adventures_in_Wonderland', "
//      + "         'https://upload.wikimedia.org/wikipedia/en/3/3f/Alice_in_Wonderland%2C_cover_1865.jpg')";

  String ORGANIZATION_VALUES_01 = ""
      + "VALUES  ('org01', "
      + "         'BSI Business Systems Integration AG', "
      + "         'Daettwil, Baden', "
      + "         'CH', "
      + "         'https://www.bsi-software.com', "
      + "         'https://wiki.eclipse.org/images/4/4f/Bsiag.png')";

  String ORGANIZATION_VALUES_02 = ""
      + "VALUES  ('org02', "
      + "         'ABB Ltd', "
      + "         'Zurich', "
      + "         'CH', "
      + "         'http://new.abb.com/', "
      + "         'http://www02.abb.com/global/abbzh/abbzh252.nsf/bf177942f19f4a98c1257148003b7a0a/8f503a1b9869cc4dc1256b0400552aa9/$FILE/ABB_standard.jpg.2/ABB_standard.jpg')";

//  String ORGANIZATION_VALUES_03 = ""
//      + "VALUES  ('org03', "
//      + "         'Nestlé', "
//      + "         'Vevey', "
//      + "         'CH', "
//      + "         'http://www.nestle.com/', "
//      + "         'http://logodatabases.com/wp-content/uploads/2012/03/nestle-logo.png')";
//
//  String ORGANIZATION_VALUES_04 = ""
//      + "VALUES  ('org04', "
//      + "         'Roche', "
//      + "         'Basel', "
//      + "         'CH', "
//      + "         'http://www.roche.com/', "
//      + "         'https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/Roche_Logo.svg/2000px-Roche_Logo.svg.png')";

  String ORGANIZATION_VALUES_03 = ""
      + "VALUES  ('org03', "
      + "         'Munich RE', "
      + "         'Munich', "
      + "         'DE', "
      + "         'http://www.nestle.com/', "
      + "         'https://upload.wikimedia.org/wikipedia/en/thumb/b/b8/Munich_Re.svg/250px-Munich_Re.svg.png')";

  String ORGANIZATION_VALUES_04 = ""
      + "VALUES  ('org04', "
      + "         'BMW', "
      + "         'Munich', "
      + "         'DE', "
      + "         'https://www.bmw.com/en/index.html', "
      + "         'https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/BMW.svg/150px-BMW.svg.png')";

  String ORGANIZATION_VALUES_05 = ""
      + "VALUES  ('org05', "
      + "         'Swiss RE', "
      + "         'Zurich', "
      + "         'CH', "
      + "         'http://www.swissre.com/', "
      + "         'http://media.swissre.com/designimages/sr_lake.jpg')";

  String PERSON_INSERT_SAMPLE = ""
      + "INSERT   INTO PERSON "
      + "         (person_id, "
      + "          first_name, "
      + "          last_name, "
      + "          picture_url, "
      + "          date_of_birth, "
      + "          gender, "
      + "          street, "
      + "          city, "
      + "          country, "
      + "          position, "
      + "          organization_id) ";

//  String PERSON_VALUES_01 = ""
//      + "VALUES   ('prs01', "
//      + "          'Alice', "
//      + "          null, "
//      + "          'http://www.uergsel.de/uploads/Alice.png', "
//      + "          '11/26/1865', "
//      + "          'F', "
//      + "          null, "
//      + "          'Daresbury, Cheshire', "
//      + "          'GB', "
//      + "          'The curious girl', "
//      + "          'org01')";
//
  String PERSON_VALUES_01 = ""
      + "VALUES   ('prs01', "
      + "          'Lena', "
      + "          'Meier', "
      + "          '/images/local_lena.png', "
      + "          '10/30/1995', "
      + "          'F', "
      + "          null, "
      + "          'Baden', "
      + "          'CH', "
      + "          null, "
      + "          'org01')";

  String PERSON_VALUES_02 = ""
      + "VALUES   ('prs02', "
      + "          'Anna', "
      + "          '', "
      + "          null, "
      + "          '11/26/1865', "
      + "          'F', "
      + "          null, "
      + "          'Winterthur', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_03 = ""
      + "VALUES   ('prs03', "
      + "          'Nicole', "
      + "          '', "
      + "          null, "
      + "          '11/26/1865', "
      + "          'F', "
      + "          null, "
      + "          'Montreux', "
      + "          'CH', "
      + "          null, "
      + "          'org03')";

  String PERSON_VALUES_04 = ""
      + "VALUES   ('prs04', "
      + "          'Reto', "
      + "          '', "
      + "          null, "
      + "          '11/26/1865', "
      + "          'M', "
      + "          null, "
      + "          'Basel', "
      + "          'CH', "
      + "          null, "
      + "          'org04')";

  String PERSON_VALUES_05 = ""
      + "VALUES   ('prs05', "
      + "          'Stefan', "
      + "          '', "
      + "          null, "
      + "          '11/26/1865', "
      + "          'M', "
      + "          null, "
      + "          'Thalwil', "
      + "          'CH', "
      + "          null, "
      + "          'org05')";

//  String PERSON_VALUES_03 = ""
//      + "VALUES   ('prs03', "
//      + "          'Gegor', "
//      + "          'Bauer', "
//      + "          'https://wiki.eclipse.org/images/5/54/Scout_contacts_112.png', "
//      + "          null, "
//      + "          'M', "
//      + "          null, "
//      + "          'Aarau', "
//      + "          'CH', "
//      + "          null, "
//      + "          'org02')";
//
//  String PERSON_VALUES_04 = ""
//      + "VALUES   ('prs04', "
//      + "          'Alexandre', "
//      + "          'Schroder', "
//      + "          'https://wiki.eclipse.org/images/5/54/Scout_contacts_105.png', "
//      + "          '30.05.1976', "
//      + "          'M', "
//      + "          'Zypressenstrasse 60', "
//      + "          'Zürich', "
//      + "          'CH', "
//      + "          null, "
//      + "          'org02')";
//
//  String PERSON_VALUES_05 = ""
//      + "VALUES   ('prs05', "
//      + "          'André', "
//      + "          'Wegmüller', "
//      + "          'https://wiki.eclipse.org/images/f/ff/Scout_contacts_103.png', "
//      + "          '04.11.1975', "
//      + "          'M', "
//      + "          'Rüttihubel 29', "
//      + "          'Walkringen', "
//      + "          'CH', "
//      + "          null, "
//      + "          'org02')";

  String PERSON_VALUES_06 = ""
      + "VALUES   ('prs06', "
      + "          'Catherine', "
      + "          'Crowden', "
      + "          'https://wiki.eclipse.org/images/9/96/Scout_contacts_111.png', "
      + "          '01.01.2000', "
      + "          'F', "
      + "          'Poststrasse 7', "
      + "          'Bümpliz', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_07 = ""
      + "VALUES   ('prs07', "
      + "          'Cédric', "
      + "          'Amstalden', "
      + "          'https://wiki.eclipse.org/images/f/ff/Scout_contacts_118.png', "
      + "          null, "
      + "          'M', "
      + "          'Brünigstrasse 5', "
      + "          'Alpnach Dorf', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_08 = ""
      + "VALUES   ('prs08', "
      + "          'Christian', "
      + "          'Braun', "
      + "          'https://wiki.eclipse.org/images/a/ab/Scout_contacts_108.png', "
      + "          '04.11.1975', "
      + "          'M', "
      + "          'Huttenstrasse 22', "
      + "          'Zürich', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_09 = ""
      + "VALUES   ('prs09', "
      + "          'Christoph', "
      + "          'Bräunlich', "
      + "          'https://wiki.eclipse.org/images/0/0e/Scout_contacts_122.png', "
      + "          null, "
      + "          'M', "
      + "          null, "
      + "          'Baar', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_10 = ""
      + "VALUES   ('prs10', "
      + "          'Fabian', "
      + "          'Laubacher', "
      + "          'https://wiki.eclipse.org/images/1/16/Scout_contacts_115.png', "
      + "          '23.07.1977', "
      + "          'M', "
      + "          'Steiggistrasse 6b', "
      + "          'Auw', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_11 = ""
      + "VALUES   ('prs11', "
      + "          'Glen', "
      + "          'Reif', "
      + "          'https://wiki.eclipse.org/images/0/00/Scout_contacts_101.png', "
      + "          '04.11.1975', "
      + "          'M', "
      + "          'Marienplatz 1', "
      + "          'München', "
      + "          'DE', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_12 = ""
      + "VALUES   ('prs12', "
      + "          'Ivan', "
      + "          'Motsch', "
      + "          'https://wiki.eclipse.org/images/a/ab/Scout_contacts_124.png', "
      + "          null, "
      + "          'M', "
      + "          null, "
      + "          'Luzern', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_13 = ""
      + "VALUES   ('prs13', "
      + "          'Christian', "
      + "          'Frey', "
      + "          'https://wiki.eclipse.org/images/0/0f/Scout_contacts_126.png', "
      + "          null, "
      + "          'M', "
      + "          null, "
      + "          'Thun', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_14 = ""
      + "VALUES   ('prs14', "
      + "          'Jens', "
      + "          'Thuesen', "
      + "          'https://wiki.eclipse.org/images/0/02/Scout_contacts_121.png', "
      + "          '14.01.1964', "
      + "          'M', "
      + "          'Weidstrasse 9', "
      + "          'Zufikon', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_15 = ""
      + "VALUES   ('prs15', "
      + "          'Patrick', "
      + "          'Gerber', "
      + "          'https://wiki.eclipse.org/images/c/c1/Scout_contacts_117.png', "
      + "          null, "
      + "          'M', "
      + "          null, "
      + "          'Freiburg', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_16 = ""
      + "VALUES   ('prs16', "
      + "          'Jürg', "
      + "          'Perner', "
      + "          'https://wiki.eclipse.org/images/f/f8/Scout_contacts_107.png', "
      + "          '27.05.1966', "
      + "          'M', "
      + "          'Birkenweg 6', "
      + "          'Triengen', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_17 = ""
      + "VALUES   ('prs17', "
      + "          'Luc', "
      + "          'Hansen', "
      + "          'https://wiki.eclipse.org/images/8/89/Scout_contacts_119.png', "
      + "          null, "
      + "          'M', "
      + "          'Via Engiadina', "
      + "          'Sils', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_18 = ""
      + "VALUES   ('prs18', "
      + "          'Markus', "
      + "          'Brunold', "
      + "          'https://wiki.eclipse.org/images/f/f1/Scout_contacts_113.png', "
      + "          '12.06.1976', "
      + "          'M', "
      + "          'Grundstrasse 1', "
      + "          'Baden', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_19 = ""
      + "VALUES   ('prs19', "
      + "          'Martin', "
      + "          'Grunder', "
      + "          'https://wiki.eclipse.org/images/3/33/Scout_contacts_110.png', "
      + "          null, "
      + "          'M', "
      + "          null, "
      + "          'St. Gallen', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_20 = ""
      + "VALUES   ('prs20', "
      + "          'Matthias', "
      + "          'Zimmermann', "
      + "          'https://wiki.eclipse.org/images/2/28/Scout_contacts_125.png', "
      + "          null, "
      + "          'M', "
      + "          'Schänzlistrasse', "
      + "          'Bern', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_21 = ""
      + "VALUES   ('prs21', "
      + "          'Nicolas', "
      + "          'Born', "
      + "          'https://wiki.eclipse.org/images/f/f6/Scout_contacts_102.png', "
      + "          '25.05.1986', "
      + "          'M', "
      + "          'Bremgartenstrasse 117', "
      + "          'Bern', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_22 = ""
      + "VALUES   ('prs22', "
      + "          'Oliver', "
      + "          'Schmid', "
      + "          'https://wiki.eclipse.org/images/1/1d/Scout_contacts_116.png', "
      + "          null, "
      + "          'M', "
      + "          null, "
      + "          'Winterthur', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_23 = ""
      + "VALUES   ('prs23', "
      + "          'Adrian', "
      + "          'Meier', "
      + "          'https://wiki.eclipse.org/images/1/13/Scout_contacts_123.png', "
      + "          null, "
      + "          'M', "
      + "          null, "
      + "          'Basel', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_24 = ""
      + "VALUES   ('prs24', "
      + "          'Peter', "
      + "          'Seitel', "
      + "          'https://wiki.eclipse.org/images/f/f1/Scout_contacts_127.png', "
      + "          null, "
      + "          'M', "
      + "          null, "
      + "          'München', "
      + "          'DE', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_25 = ""
      + "VALUES   ('prs25', "
      + "          'Robert', "
      + "          'Echelmeyer', "
      + "          'https://wiki.eclipse.org/images/b/b6/Scout_contacts_120.png', "
      + "          '27.07.1989', "
      + "          'M', "
      + "          'Gladbacher Str. 116', "
      + "          'Düsseldorf', "
      + "          'DE', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_26 = ""
      + "VALUES   ('prs26', "
      + "          'Michael', "
      + "          'Richter', "
      + "          'https://wiki.eclipse.org/images/e/ea/Scout_contacts_104.png', "
      + "          null, "
      + "          'M', "
      + "          null, "
      + "          'Frankfurt', "
      + "          'DE', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_27 = ""
      + "VALUES   ('prs27', "
      + "          'Sion', "
      + "          'Huws', "
      + "          'https://wiki.eclipse.org/images/0/03/Scout_contacts_114.png', "
      + "          '11.05.1978', "
      + "          'M', "
      + "          'Zürichstr. 133', "
      + "          'Affoltern a. A.', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_28 = ""
      + "VALUES   ('prs28', "
      + "          'Stefan', "
      + "          'Leicht Vogt', "
      + "          'https://wiki.eclipse.org/images/0/05/Scout_contacts_106.png', "
      + "          '19.04.1987', "
      + "          'M', "
      + "          'Matschweg 3', "
      + "          'Gebenstorf', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String PERSON_VALUES_29 = ""
      + "VALUES   ('prs29', "
      + "          'Zeno', "
      + "          'Hug', "
      + "          'https://wiki.eclipse.org/images/c/c6/Scout_contacts_109.png', "
      + "          '14.06.1972', "
      + "          'M', "
      + "          'Dorfstrasse 1', "
      + "          'Baden', "
      + "          'CH', "
      + "          null, "
      + "          'org02')";

  String ORGANIZATION_FOR_USER_SELECT = ""
      + "           SELECT O.ORGANIZATION_ID "
      + "           FROM ORGANIZATION O "
      + "           WHERE O.USER_ID = :userId "
      + "           LIMIT 1 "
      + "           INTO :organizationId ";

  String USER_FOR_ORGANIZATION_SELECT = ""
      + "           SELECT O.USER_ID "
      + "           FROM ORGANIZATION O "
      + "           WHERE O.ORGANIZATION_ID = :organizationId "
      + "           LIMIT 1 "
      + "           INTO :userId ";

  String PERSON_DROP_TABLE = "DROP TABLE PERSON";
  String ORGANIZATION_DROP_TABLE = "DROP TABLE ORGANIZATION";
  String ACCOUNT_DROP_TABLE = "DROP TABLE ACCOUNT";
  String DEAL_DROP_TABLE = "DROP TABLE DEAL";
  String DEPLOYED_ORDER_BOOK_DROP_TABLE = "DROP TABLE DEPLOYED_ORDER_BOOK";

  String BANK_ACCOUNT_CREATE = ""
      + "CREATE       "
      + "TABLE        BANK_ACCOUNT "
      + "            (organization_id VARCHAR(64) NOT NULL, "
      + "             currency_uid VARCHAR(3) NOT NULL, "
      + "             balance numeric(15,2), "
      + "             CONSTRAINT ORGANIZATION_FK FOREIGN KEY (organization_id) REFERENCES ORGANIZATION(organization_id))";

  String BANK_ACCOUNT_INSERT = ""
      + " INSERT "
      + " INTO BANK_ACCOUNT (ORGANIZATION_ID, CURRENCY_UID, BALANCE)"
      + " VALUES (:organizationId, :currency, :balance) ";

  String BANK_ACCOUNT_SELECT = ""
      + " SELECT "
      + "     ORGANIZATION_ID, "
      + "     CURRENCY_UID, "
      + "     BALANCE "
      + " FROM BANK_ACCOUNT "
      + " WHERE ORGANIZATION_ID = :orgId "
      + " INTO "
      + "     :{organizationId}, "
      + "     :{currency}, "
      + "     :{balance} ";

  String BANK_ACCOUNT_SELECT_BALANCE = ""
      + " SELECT BALANCE "
      + " FROM   BANK_ACCOUNT BA "
      + " WHERE  ORGANIZATION_ID = :organizationId "
      + " AND    CURRENCY_UID = :currency "
      + " INTO   :balance ";

  String BANK_ACCOUNT_UPDATE = ""
      + " UPDATE BANK_ACCOUNT SET BALANCE = :balance "
      + " WHERE ORGANIZATION_ID = :organizationId AND CURRENCY_UID = :currency ";

  String BANK_ACCOUNT_DROP = "DROP TABLE BANK_ACCOUNT";

  String DEAL_CREATE_TABLE = ""
      + "CREATE       "
      + "TABLE        DEAL "
      + "            (deal_id numeric(15) NOT NULL CONSTRAINT DEAL_PK PRIMARY KEY, "
      + "             organization_id VARCHAR(64) NOT NULL, "
      + "             deal_nr VARCHAR(64), "
      + "             quantity numeric (15), "
      + "             exchange_rate numeric (15, 2), "
      + "             order_book_type VARCHAR(64), "
      + "             trading_action VARCHAR(64), "
      + "             status VARCHAR(64), "
      + "             publish_transaction_hash VARCHAR(130), "
      + "             CONSTRAINT ORGANIZATION_FK FOREIGN KEY (organization_id) REFERENCES ORGANIZATION(organization_id))";

  String DEAL_PAGE_DATA_SELECT = ""
      + "SELECT       d.deal_id, "
      + "             d.deal_nr, "
      + "             d.quantity, "
      + "             d.exchange_rate, "
      + "             d.order_book_type, "
      + "             d.trading_action, "
      + "             d.status, "
      + "             d.order_book_type || ' ' || d.deal_nr "
      + "FROM         DEAL d";

  String DEAL_PAGE_DATA_WHERE_CLAUSE = ""
      + "AND          organization_id = :organizationId ";

  String DEAL_PAGE_DATA_INTO = ""
      + "INTO         :{page.dealId}, "
      + "             :{page.dealNr}, "
      + "             :{page.quantity}, "
      + "             :{page.exchangeRate}, "
      + "             :{page.orderBookType}, "
      + "             :{page.tradingAction}, "
      + "             :{page.status}, "
      + "             :{page.displayedDealNr} ";
  //TODO remove organizationId/_id from DEAL_INSERT
  String DEAL_INSERT = ""
      + "INSERT     INTO "
      + "DEAL      (deal_id,organization_id) "
      + "VALUES     (:dealId,:organizationId)";

  String DEAL_UPDATE = ""
      + "UPDATE       DEAL "
      + "SET          deal_nr = :dealNr, "
      + "             quantity = :quantity, "
      + "             exchange_rate = :exchangeRate, "
      + "             order_book_type = :orderBookType, "
      + "             trading_action = :tradingActionBox, "
      + "             status = :status,"
      + "             publish_transaction_hash = :publishTransactionHash "
      + "WHERE        deal_id = :dealId";

  String DEAL_INSERT_SAMPLE = ""
      + "INSERT     INTO "
      + "DEAL      (deal_id, "
      + "           deal_nr, "
      + "           quantity, "
      + "           exchange_rate, "
      + "           order_book_type, "
      + "           trading_action, "
      + "           status, "
      + "           organization_id) ";

  String DEAL_VALUES_01 = ""
      + "VALUES    (10,"
      + "           null, "
      + "           500000, "
      + "           0.85, "
      + "           'USDEUR', "
      + "           'BUY', "
      + "           'INACTIVE', "
      + "           'org03')";

  String DEAL_VALUES_02 = ""
      + "VALUES    (11,"
      + "           null, "
      + "           1000000, "
      + "           0.80, "
      + "           'USDEUR', "
      + "           'BUY', "
      + "           'INACTIVE', "
      + "           'org03')";

  String DEAL_VALUES_03 = ""
      + "VALUES    (12,"
      + "           null, "
      + "           200000, "
      + "           0.9, "
      + "           'USDEUR', "
      + "           'SELL', "
      + "           'INACTIVE', "
      + "           'org04')";

  String DEAL_VALUES_04 = ""
      + "VALUES    (13,"
      + "           null, "
      + "           400000, "
      + "           1.0, "
      + "           'USDEUR', "
      + "           'SELL', "
      + "           'INACTIVE', "
      + "           'org04')";

  String DEAL_SELECT = ""
      + "SELECT       deal_nr,"
      + "       organization_id, "
      + "             quantity, "
      + "             exchange_rate, "
      + "             order_book_type, "
      + "             trading_action , "
      + "             status "
      + "FROM         DEAL "
      + "WHERE        deal_id = :dealId "
      + "INTO         :dealNr, "
      + "       :organizationId, "
      + "             :quantity, "
      + "             :exchangeRate, "
      + "             :orderBookType, "
      + "             :tradingActionBox, "
      + "             :status ";

  String DEAL_SELECT_BY_DEAL_NR = ""
      + "SELECT       deal_id,"
      + "             organization_id, "
      + "             quantity, "
      + "             exchange_rate, "
      + "             trading_action , "
      + "             status "
      + "FROM         DEAL "
      + "WHERE        deal_nr = :dealNr "
      + "AND          order_book_type = :orderBookType "
      + "INTO         :dealId, "
      + "             :organizationId, "
      + "             :quantity, "
      + "             :exchangeRate, "
      + "             :tradingActionBox, "
      + "             :status ";

  String EVENT_CREATE_TABLE = ""
      + "CREATE       "
      + "TABLE        EVENT "
      + "            (event_id VARCHAR(64) NOT NULL CONSTRAINT EVENT_PK PRIMARY KEY, "
      + "             title VARCHAR(64), "
      + "             date_start TIMESTAMP, "
      + "             date_end TIMESTAMP, "
      + "             city VARCHAR(64), "
      + "             country VARCHAR(2), "
      + "             phone VARCHAR(20), "
      + "             email VARCHAR(64), "
      + "             url VARCHAR(64), "
      + "             notes VARCHAR(1024))";

  String EVENT_INSERT_SAMPLE = ""
      + "INSERT       "
      + "INTO         EVENT "
      + "            (event_id, "
      + "             title, "
      + "             date_start, "
      + "             date_end, "
      + "             city, "
      + "             country, "
      + "             url) ";

  String EVENT_INSERT_VALUES_01 = ""
      + "VALUES      ('evt01', "
      + "             'JavaLand 2016', "
      + "             '2016-03-08 09:00:00', "
      + "             '2016-03-10 17:00:00', "
      + "             'Bruehl', "
      + "             'DE', "
      + "             'https://www.javaland.eu/de/archiv-2016/')";

  String EVENT_INSERT_VALUES_02 = ""
      + "VALUES      ('evt02', "
      + "             'EclipseCon Europe 2016', "
      + "             '2016-10-25 09:00:00', "
      + "             '2016-10-27 17:00:00', "
      + "             'Ludwigsburg', "
      + "             'DE', "
      + "             'https://www.eclipsecon.org/europe2016/')";

  String PARTICIPANT_CREATE_TABLE = ""
      + "CREATE       "
      + "TABLE        PARTICIPANT "
      + "             (event_id VARCHAR(64) NOT NULL, "
      + "              person_id VARCHAR(64) NOT NULL, "
      + "PRIMARY KEY  (event_id, person_id))";

  String PARTICIPANT_INSERT_SAMPLE = ""
      + "INSERT       "
      + "INTO         PARTICIPANT "
      + "            (event_id, "
      + "             person_id) ";

  String PARTICIPANT_INSERT_VALUES_01 = "VALUES ('evt01', 'prs01')";
  String PARTICIPANT_INSERT_VALUES_02 = "VALUES ('evt02', 'prs01')";
  String PARTICIPANT_INSERT_VALUES_03 = "VALUES ('evt01', 'prs02')";

  String DEPLOYED_ORDER_BOOK_CREATE = ""
      + " CREATE TABLE DEPLOYED_ORDER_BOOK"
      + " ( "
      + "   ENVIRONMENT VARCHAR(15) NOT NULL, "
      + "   ORDER_BOOK_TYPE VARCHAR(15) NOT NULL, "
      + "   ADDRESS VARCHAR(255) NOT NULL, "
      + "   CONSTRAINT DEPLOYED_ORDER_BOOK_PK1 PRIMARY KEY (ENVIRONMENT, ORDER_BOOK_TYPE) "
      + " )";

  String PERSON_EVENT_SELECT = ""
      + "SELECT       e.event_id, "
      + "             e.title, "
      + "             e.date_start, "
      + "             e.city, "
      + "             e.country "
      + "FROM         PARTICIPANT p "
      + "LEFT JOIN    EVENT e "
      + "ON           e.event_id = p.event_id "
      + "WHERE        p.person_id = :personId "
      + "INTO         :{events.id}, "
      + "             :{events.title}, "
      + "             :{events.starts}, "
      + "             :{events.city}, "
      + "             :{events.country}";

  String EVENT_PAGE_DATA_SELECT = ""
      + "SELECT       e.event_id, "
      + "             e.title, "
      + "             e.date_start, "
      + "             e.date_end, "
      + "             e.city, "
      + "             e.country, "
      + "             e.url, "
      + "             (SELECT   COUNT(1) "
      + "             FROM     PARTICIPANT p "
      + "             WHERE    P.event_id = e.event_id) "
      + "FROM         EVENT e";

  String EVENT_PAGE_DATA_WHERE_CLAUSE = ""
      + "AND          e.event_id in (SELECT DISTINCT p.event_id "
      + "                            FROM    PARTICIPANT p, "
      + "                                    PERSON c "
      + "                            WHERE   p.person_id = c.person_id "
      + "                            AND     organization_id = :organizationId)";

  String EVENT_PAGE_DATA_INTO = ""
      + "INTO         :{page.deal_id}, "
      + "             :{page.title}, "
      + "             :{page.starts}, "
      + "             :{page.ends}, "
      + "             :{page.city}, "
      + "             :{page.country}, "
      + "             :{page.homepage}, "
      + "             :{page.participants}";

  String EVENT_INSERT = ""
      + "INSERT     INTO "
      + "EVENT      (event_id) "
      + "VALUES     (:eventId)";

  String EVENT_SELECT = ""
      + "SELECT       title, "
      + "             date_start, "
      + "             date_end, "
      + "             city, "
      + "             country, "
      + "             phone, "
      + "             email, "
      + "             url, "
      + "             notes "
      + "FROM         EVENT "
      + "WHERE        event_id = :eventId "
      + "INTO         :title, "
      + "             :starts, "
      + "             :ends, "
      + "             :locationBox.city, "
      + "             :locationBox.country, "
      + "             :phone, "
      + "             :email, "
      + "             :homepage, "
      + "             :notesBox.notes";

  String EVENT_UPDATE = ""
      + "UPDATE       EVENT "
      + "SET          title = :title, "
      + "             date_start = :starts, "
      + "             date_end = :ends, "
      + "             url = :homepage, "
      + "             city = :locationBox.city, "
      + "             country = :locationBox.country, "
      + "             phone = :phone, "
      + "             email = :email, "
      + "             notes = :notesBox.notes "
      + "WHERE        event_id = :eventId";

  String EVENT_PARTICIPANTS_SELECT = ""
      + "SELECT       c.person_id, "
      + "             c.first_name, "
      + "             c.last_name, "
      + "             c.organization_id "
      + "FROM         PARTICIPANT p "
      + "LEFT JOIN    PERSON c "
      + "ON           c.person_id = p.person_id "
      + "WHERE        p.event_id = :eventId "
      + "INTO         :{participantTableField.personId}, "
      + "             :{participantTableField.firstName}, "
      + "             :{participantTableField.lastName}, "
      + "             :{participantTableField.organization}";

  String EVENT_PARTICIPANTS_DELETE = ""
      + "DELETE       FROM PARTICIPANT "
      + "WHERE        event_id = :eventId "
      + "AND          person_id = :{personId}";

  String EVENT_PARTICIPANTS_INSERT = ""
      + "INSERT       INTO "
      + "PARTICIPANT  (event_id, "
      + "              person_id) "
      + "VALUES       (:eventId, "
      + "              :{personId})";

  String EVENT_COUNT_BY_PERSON = ""
      + "SELECT       person_id, "
      + "             COUNT(event_id) "
      + "FROM         PARTICIPANT "
      + "GROUP BY     person_id "
      + "INTO         :{bean.personId}, "
      + "             :{bean.eventCount}";

  String PARTICIPANT_DROP_TABLE = "DROP TABLE PARTICIPANT";

  String EVENT_DROP_TABLE = "DROP TABLE EVENT";

  String DEALS_SELECT_IN_STATUS_FORM_ORDERBOOK = ""
      + "SELECT     deal_id, "
      + "           deal_nr "
      + "FROM       deal "
      + "WHERE      order_book_type = :orderBookType "
      + "AND        status = :status "
      + "INTO       :{dealId}, "
      + "           :{dealNr} ";

  String DEALS_UPDATE_STATUS_AND_DEAL_NR = ""
      + "UPDATE deal "
      + "SET    status = :status, "
      + "       deal_nr = :dealNr "
      + "WHERE  deal_id = :dealId ";

  String SMART_CONTRACT_SELECT_DEPLOYED_ORDER_BOOKS = ""
      + "SELECT dob.environment, "
      + "       dob.order_book_type,"
      + "       dob.address "
      + "FROM   deployed_order_book dob "
      + "INTO   :{environment}, "
      + "       :{orderBookType}, "
      + "       :{address} ";

  String ACCOUNT_CREATE_TABLE = ""
      + "CREATE       "
      + "TABLE        ACCOUNT "
      + "            (address VARCHAR(255) NOT NULL CONSTRAINT ACCOUNT_PK PRIMARY KEY, "
      + "             password VARCHAR(64) NOT NULL, "
      + "             person_id VARCHAR(64) NOT NULL, "
      + "             name VARCHAR(64))";

  String ACCOUNT_SELECT_ALL = ""
      + "SELECT a.person_id, "
      + "       LOWER(a.address), "
      + "       a.name "
      + "FROM   account a "
      + "INTO   :{person}, "
      + "       :{address}, "
      + "       :{accountName} ";

  String ACCOUNT_CREATE = ""
      + "INSERT INTO account (person_id, address, name, password) "
      + "VALUES (:person, LOWER(:address), :name, :password) ";

  String ACCOUNT_SELECT = ""
      + "SELECT a.person_id, "
      + "       a.name, "
      + "       a.password "
      + "FROM   account a "
      + "WHERE  LOWER(a.address) = LOWER(:address) "
      + "INTO   :person, "
      + "       :name,"
      + "       :password ";

  String ACCOUNT_UPDATE = ""
      + "UPDATE account "
      + "SET    person_id = :person, "
      + "       name = :name "
      + "WHERE  LOWER(address) = LOWER(:address) ";

  String DEAL_SEQUENCE_CREATE = "CREATE SEQUENCE deal_deal_id_seq START WITH 20";

  String DEAL_SEQUENCE_DROP = "DROP SEQUENCE IF EXISTS deal_deal_id_seq";

}
