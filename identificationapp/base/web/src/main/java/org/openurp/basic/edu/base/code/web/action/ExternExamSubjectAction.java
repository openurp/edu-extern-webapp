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
package org.openurp.basic.edu.base.code.web.action;

import java.sql.Date;

import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.transfer.importer.EntityImporter;
import org.openurp.basic.edu.base.code.service.ExternExamSubjectImporter;
import org.openurp.edu.base.code.model.ExternExamSubject;

/**
 * @author zhouqi 2017年12月11日
 *
 */
public class ExternExamSubjectAction extends ExternBaseAction<Integer, ExternExamSubject> {

  protected String entityName() {
    return "examSubject";
  }

  protected void settingOtherInSearch(OqlBuilder<ExternExamSubject> builder) {
    Date nowAt = new Date(System.currentTimeMillis());
    builder.where(entityName + ".beginOn <= :nowAt");
    builder.where(entityName + ".endOn is null or " + entityName + ".endOn >= :nowAt", nowAt);
  }

  protected void configImporter(EntityImporter importer) {
    importer.addListener(new ExternExamSubjectImporter(entityDao));
  }
}
