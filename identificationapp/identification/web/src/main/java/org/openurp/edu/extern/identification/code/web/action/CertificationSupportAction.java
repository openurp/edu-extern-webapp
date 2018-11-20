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

import java.io.Serializable;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.query.builder.Condition;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.Entity;
import org.openurp.basic.edu.base.code.web.action.ExternBaseAction;
import org.openurp.code.geo.model.Division;
import org.openurp.edu.base.code.model.CertificateLevel;
import org.openurp.edu.base.code.model.CertificateType;
import org.openurp.edu.base.code.model.ExternExamSubject;
import org.openurp.edu.base.code.model.ExternExamTime;
import org.openurp.edu.extern.identification.model.Certificate;

/**
 * @author zhouqi 2018年1月21日
 *
 */
public abstract class CertificationSupportAction<ID extends Serializable, E extends Entity<ID>> extends ExternBaseAction<ID, E> {

  protected final String EXAM_SUBJECT = "examSubject";

  protected final String CERT_TYPE = "certType";

  protected final String CERT_LEVEL = "certLevel";

  protected final String DIVISION = "division";

  protected final String EXAM_TIME = "examTime";

  private final Map<String, Integer> dataFromMap = initDataFromMap();

  private final Map<String, String> fieldFromMap = initFieldFromMap();

  private Map<String, Integer> initDataFromMap() {
    Map<String, Integer> map = CollectUtils.newHashMap();
    map.put(EXAM_SUBJECT, 1);
    map.put(CERT_TYPE, 2);
    map.put(CERT_LEVEL, 3);
    map.put(DIVISION, 4);
    map.put(EXAM_TIME, 5);
    return map;
  }

  private Map<String, String> initFieldFromMap() {
    Map<String, String> map = CollectUtils.newHashMap();
    map.put(CERT_TYPE, "type");
    map.put(CERT_LEVEL, "level");
    map.put(DIVISION, DIVISION);
    map.put(EXAM_TIME, EXAM_TIME);
    return map;
  }

  protected void indexSetting() {
    put("examSubjects", codeService.getCodes(ExternExamSubject.class));
  }

  protected void extraEdit(ID id) {
    indexSetting();
  }

  public final String dataAjax() {
    String from = get("from");

    loadData(from);

    switch (dataFromMap.get(from)) {
      case 1:
        return "/component/certificate/dataFromExamSubjectAjax";
      case 2:
        return "/component/certificate/dataFromCertTypeAjax";
      case 3:
        return "/component/certificate/dataFromCertLevelAjax";
      default:
        return "/component/certificate/dataFromDivsionAjax";
    }
  }

  protected final void loadData(String from) {
    Integer examSubjectId = getIntId("examSubject");
    Integer certTypeId = getIntId("certType");
    Integer certLevelId = getIntId("certLevel");
    Integer divisionId = getIntId("division");

    Date now = new Date(System.currentTimeMillis());
    switch (dataFromMap.get(from)) {
      case 1: {
        OqlBuilder<CertificateType> builder = OqlBuilder.from(CertificateType.class, "certType");
        if (null != examSubjectId) {
          StringBuilder where = new StringBuilder();
          where.append("exists (");
          where.append("  from ").append(Certificate.class.getName()).append(" certificate");
          where.append(" where certificate.type = certType");
          where.append("   and certificate.type.examSubject.id = :id");
          where.append(")");
          builder.where(where.toString(), examSubjectId);
        }
        builder.where("certType.beginOn <= :now", now);
        builder.where("certType.endOn is null or certType.endOn >= :now", now);
        put("certTypes", entityDao.search(builder));
      }
      case 2: {
        OqlBuilder<CertificateLevel> builder = OqlBuilder.from(CertificateLevel.class, "certLevel");
        StringBuilder where = new StringBuilder();
        where.append("exists (");
        where.append("  from ").append(Certificate.class.getName()).append(" certificate");
        where.append(" where certificate.level = certLevel");
        if (null == certTypeId) {
          where.append("   and certificate.type.examSubject.id = :examSubjectId");
        } else {
          where.append("   and certificate.type.id = :certTypeId");
        }
        where.append(")");
        if (null == certTypeId) {
          builder.where(where.toString(), examSubjectId);
        } else {
          builder.where(where.toString(), certTypeId);
        }
        builder.where("certLevel.beginOn <= :now", now);
        builder.where("certLevel.endOn is null or certLevel.endOn >= :now", now);
        List<CertificateLevel> certLevels = entityDao.search(builder);
        if (CollectionUtils.isEmpty(certLevels)) {
          certLevels = codeService.getCodes(CertificateLevel.class);
        }
        put("certLevels", certLevels);
      }
      case 3: {
        OqlBuilder<Division> builder = OqlBuilder.from(Division.class, "division");
        StringBuilder where = new StringBuilder();
        List<Object> params = CollectUtils.newArrayList();
        where.append("exists (");
        where.append("  from ").append(Certificate.class.getName()).append(" certificate");
        where.append(" where certificate.division = division");
        where.append("   and certificate.type.examSubject.id = :examSubjectId");
        params.add(examSubjectId);
        if (null != certTypeId) {
          where.append("   and certificate.type.id = :certTypeId");
          params.add(certTypeId);
        }
        if (null != certLevelId) {
          where.append("   and certificate.level.id = :certLevelId");
          params.add(certLevelId);
        }
        where.append(")");
        Condition condition = new Condition(where.toString());
        condition.params(params);
        builder.where(condition);
        builder.where("division.code like '__0000'");
        builder.where("division.beginOn <= :now", now);
        builder.where("division.endOn is null or division.endOn >= :now", now);
        List<Division> divisions = entityDao.search(builder);
        if (CollectionUtils.isEmpty(divisions)) {
          divisions = entityDao.search(OqlBuilder.from(Division.class, "division").where("division.code like '__0000'").where("division.beginOn <= :now and (division.endOn is null or division.endOn >= :now)", now));
        }
        put("divisions", divisions);
      }
      default: {
        OqlBuilder<ExternExamTime> builder = OqlBuilder.from(ExternExamTime.class, "examTime");
        StringBuilder where = new StringBuilder();
        List<Object> params = CollectUtils.newArrayList();
        where.append("exists (");
        where.append("  from ").append(Certificate.class.getName()).append(" certificate");
        where.append(" where certificate.examTime = examTime");
        where.append("   and certificate.type.examSubject.id = :examSubjectId");
        params.add(examSubjectId);
        if (null != certTypeId) {
          where.append("   and certificate.type.id = :certTypeId");
          params.add(certTypeId);
        }
        if (null != certLevelId) {
          where.append("   and certificate.level.id = :certLevelId");
          params.add(certLevelId);
        }
        if (null != divisionId) {
          where.append("   and certificate.division.id = :id");
          params.add(divisionId);
        }
        where.append(")");
        Condition condition = new Condition(where.toString());
        condition.params(params);
        builder.where(condition);
        builder.where("examTime.beginOn <= :now", now);
        builder.where("examTime.endOn is null or examTime.endOn >= :now", now);
        List<ExternExamTime> examTimes = entityDao.search(builder);
        if (CollectionUtils.isEmpty(examTimes)) {
          examTimes = codeService.getCodes(ExternExamTime.class);
        }
        put("examTimes", examTimes);
      }
    }
  }

  public Map<String, Integer> getDataFromMap() {
    return new HashMap<String, Integer>(dataFromMap);
  }

  public Map<String, String> getFieldFromMap() {
    return new HashMap<String, String>(fieldFromMap);
  }
}
