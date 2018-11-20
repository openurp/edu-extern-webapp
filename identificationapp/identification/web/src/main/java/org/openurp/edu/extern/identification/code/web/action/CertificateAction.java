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
package org.openurp.edu.extern.identification.code.web.action;

import java.util.Date;

import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.transfer.importer.EntityImporter;
import org.openurp.basic.edu.base.code.web.action.ExternBaseAction;
import org.openurp.code.geo.model.Division;
import org.openurp.edu.base.code.model.CertificateLevel;
import org.openurp.edu.base.code.model.CertificateType;
import org.openurp.edu.base.code.model.ExternExamSubject;
import org.openurp.edu.base.code.model.ExternExamTime;
import org.openurp.edu.extern.identification.code.service.CertificateImporter;
import org.openurp.edu.extern.identification.model.Certificate;

/**
 * @author zhouqi 2017年12月13日
 *
 */
public class CertificateAction extends ExternBaseAction<Integer, Certificate> {

  protected void indexSetting() {
    put("subjects", codeService.getCodes(ExternExamSubject.class));
    put("types", codeService.getCodes(CertificateType.class));
    put("levels", codeService.getCodes(CertificateLevel.class));
    put("divisions", entityDao.search(OqlBuilder.from(Division.class, "division").where("division.code like '__0000'").where("division.beginOn <= :now and (division.endOn is null or division.endOn >= :now)", new Date())));
    put("times", codeService.getCodes(ExternExamTime.class));
  }

  protected void settingOtherInSearch(OqlBuilder<Certificate> builder) {
    Date nowAt = new Date();
    builder.where(entityName + ".beginOn <= :nowAt", nowAt);
    builder.where(entityName + ".endOn is null or " + entityName + ".endOn >= :nowAt", nowAt);
  }

  protected void extraEdit(Integer id) {
    indexSetting();
  }

  public String loadCertTypesAjax() {
    OqlBuilder<CertificateType> builder = OqlBuilder.from(CertificateType.class, "type");
    builder.where("type.examSubject.id = :id", getInt("id"));
    builder.where("type.beginOn <= :now");
    builder.where("type.endOn is null or type.endOn >= :now", new Date());
    put("certTypes", entityDao.search(builder));

    return forward();
  }

  public String checkPrimaryAjax() {
    Integer id = getInt("id");
    String code = get("code");

    OqlBuilder<Certificate> builder = OqlBuilder.from(entityType, entityName);
    if (null != id) {
      builder.where(entityName + ".id != :id", id);
    }
    builder.where(entityName + ".code = :code", code);
    put("isOk", entityDao.search(builder).isEmpty());

    return forward();
  }

  protected void configImporter(EntityImporter importer) {
    importer.addListener(new CertificateImporter(entityDao));
  }
}
