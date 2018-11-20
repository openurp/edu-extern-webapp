/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright © 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openurp.edu.extern;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhouqi 2018年5月22日
 *
 */
public final class IdentificationConst {

  public final static String ID_NUMBER = "idNumber";

  public final static String SCORE = "score";

  public final static String CERT_CODE = "certCode";

  public final static String CERT_TYPE = "certType";

  public final static String CERT_LEVEL = "certLevel";

  public final static String DIVISION = "division";

  public final static String EXAM_TIME = "examTime";

  public final static String STD_NAME = "stdName";

  public final static List<String> innerFields = innerFields();

  public final static List<String> fixedRequestFields = fixedRequestFields();

  public final static List<String> fixedResponseFields = fixedResponseFields();

  public final static List<String> certFields = certFields();

  public final static Map<String, String> innerFieldMap = loadInnerFieldMap();

  public final static Map<String, String> fixedRequestFieldMap = loadFixedRequestFieldMap();

  public final static Map<String, String> fixedResponseFieldMap = loadFixedResponseFieldMap();

  public final static Map<String, String> saveFieldMap = saveFieldMap();

  private final static List<String> innerFields() {
    return loadFields("field.inner");
  }

  private final static List<String> loadFields(String key) {
    try {
      Properties prop = new Properties();
      prop.load(IdentificationConst.class.getResourceAsStream("/const.properties"));
      return Arrays.asList(StringUtils.split(prop.getProperty(key), ","));
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private final static List<String> fixedRequestFields() {
    return loadFields("field.request.fixed");
  }

  private final static List<String> fixedResponseFields() {
    return loadFields("field.response.fixed");
  }

  private final static List<String> certFields() {
    return loadFields("cert.field");
  }

  private final static Map<String, String> loadInnerFieldMap() {
    return loadFieldMap(innerFields);
  }

  private final static Map<String, String> loadFieldMap(List<String> fields) {
    Map<String, String> fieldMap = new LinkedMap();
    Collections.sort(fields);
    for (String innerField : fields) {
      fieldMap.put(innerField, innerField);
    }
    return fieldMap;
  }

  private final static Map<String, String> loadFixedRequestFieldMap() {
    return loadFieldMap(fixedRequestFields);
  }

  private final static Map<String, String> loadFixedResponseFieldMap() {
    return loadFieldMap(fixedResponseFields);
  }

  private final static Map<String, String> saveFieldMap() {
    List<String> saves = loadFields("save.field");
    Map<String, String> fieldMap = new LinkedMap();
    for (int i = 0; i < certFields.size(); i++) {
      fieldMap.put(certFields.get(i), saves.get(i));
    }
    return fieldMap;
  }
}
