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
package org.openurp.edu.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhouqi 2017年12月13日
 *
 */
public class DateUtils {

  private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

  public static boolean isValidDate(String dateValue, String dateFormat) {
    if (StringUtils.isBlank(dateValue)) {
      return false;
    }

    if (StringUtils.isBlank(dateFormat)) {
      throw new RuntimeException("dateFormat of arg is null or empty!!!");
    }

    try {
      SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
      Date parseDate = sdf.parse(dateValue);
      return StringUtils.equals(dateValue, sdf.format(parseDate));
    } catch (ParseException e) {
      logger.error(e.getMessage());
      return false;
    }
  }

  public static Date toUtilDate(String dateValue, String dateFormat) {
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    try {
      return sdf.parse(dateValue);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static java.sql.Date toSqlDate(String dateValue, String dateFormat) {
    return new java.sql.Date(toUtilDate(dateValue, dateFormat).getTime());
  }

  public static String toFormatString(Date date, String dateFormat) {
    return new SimpleDateFormat(dateFormat).format(date);
  }

  public static String toFormatString(java.sql.Date date, String dateFormat) {
    return new SimpleDateFormat(dateFormat).format(date);
  }
}
