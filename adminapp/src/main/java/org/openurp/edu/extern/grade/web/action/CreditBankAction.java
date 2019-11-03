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
package org.openurp.edu.extern.grade.web.action;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.query.builder.Condition;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Strings;
import org.beangle.commons.transfer.exporter.PropertyExtractor;
import org.openurp.code.edu.model.CourseTakeType;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.grade.export.CreditBankPropertyExtractor;
import org.openurp.edu.grade.course.model.CourseGrade;
import org.openurp.edu.graduation.audit.model.GraduateResult;
import org.openurp.edu.graduation.audit.model.GraduateSession;
import org.openurp.edu.student.info.model.Graduation;
import org.openurp.edu.web.action.RestrictionSupportAction;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 学分银行成绩
 *
 * @author zhouqi 2019年10月11日
 */
public class CreditBankAction extends RestrictionSupportAction {

  @Override
  protected void indexSetting() {
    put("sessions", entityDao.getAll(GraduateSession.class));
  }

  @Override
  public String search() {
    OqlBuilder<CourseGrade> builder = builderQuery();
    builder.limit(getPageLimit());
    put("grades", entityDao.search(builder));
    return forward();
  }

  protected OqlBuilder<CourseGrade> builderQuery() {
    OqlBuilder<CourseGrade> builder = OqlBuilder.from(CourseGrade.class, "grade");
    populateConditions(builder);
    Long sessionId = getLongId("session");
    GraduateSession session = entityDao.get(GraduateSession.class, sessionId);
    put("graduateSession", session);
    StringBuilder hql1 = new StringBuilder();
    hql1.append("exists (");
    hql1.append("  from ").append(GraduateResult.class.getName()).append(" result");
    hql1.append(" where result.std = grade.std and result.passed=true");
    hql1.append("   and result.session = :session");
    hql1.append(")");
    builder.where(hql1.toString(), session);
    builder.where("grade.courseTakeType.id != :courseTakeTypeId", CourseTakeType.Exemption);
    StringBuilder hql2 = new StringBuilder();
    hql2.append("not exists (");
    hql2.append("  from ").append(CourseGrade.class.getName()).append(" grade2");
    hql2.append(" where grade.std = grade2.std");
    hql2.append("   and grade.course = grade2.course");
    hql2.append("   and grade.id != grade2.id");
    hql2.append("   and (");
    hql2.append("         coalesce(grade2.score, 0) = coalesce(grade.score, 0) and grade2.id > grade.id");
    hql2.append("         or");
    hql2.append("         coalesce(grade2.score, 0) > coalesce(grade.score, 0)");
    hql2.append("       )");
    hql2.append(")");

    builder.where(hql2.toString());
    builder.where("grade.passed=true");
    String orderBy = get("orderBy");
    if (Strings.isBlank(orderBy)) {
      orderBy = "grade.id";
    } else {
      orderBy += ",grade.id";
    }
    builder.orderBy(orderBy);
    return builder;
  }

  @Override
  protected PropertyExtractor getPropertyExtractor() {
    return new CreditBankPropertyExtractor(getTextResource(), loadDataMap());
  }

  @Override
  protected Collection<?> getExportDatas() {
    OqlBuilder<CourseGrade> builder = builderQuery();
    builder.limit(null);
    return entityDao.search(builder);
  }

  /**
   * @param
   * @return
   */
  private Map<String, Object> loadDataMap() {
    Map<String, Object> dataMap = CollectUtils.newHashMap();
    dataMap.put("graduationMap", loadGraduationData());
    return dataMap;
  }

  private Map<Student, Graduation> loadGraduationData() {
    OqlBuilder<Graduation> gBuilder = OqlBuilder.from(Graduation.class, "graduation");
    OqlBuilder<CourseGrade> cgBuilder = builderQuery();
    cgBuilder.where("graduation.std = grade.std");
    StringBuilder hql = new StringBuilder();
    List<Object> params = CollectUtils.newArrayList();
    hql.append("exists (");
    hql.append("  from ").append(CourseGrade.class.getName()).append(" grade");
    hql.append(" where ");
    for (int i = 0; i < cgBuilder.getConditions().size(); i++) {
      if (i > 0) {
        hql.append(" and ");
      }
      Condition cgCond = cgBuilder.getConditions().get(i);
      hql.append("(").append(cgCond.getContent()).append(")");
      params.addAll(cgCond.getParams());
    }
    hql.append(")");
    Condition gCond = new Condition(hql.toString());
    gCond.params(params);
    gBuilder.where(gCond);
    List<Graduation> graduations = entityDao.search(gBuilder);
    Map<Student, Graduation> graduationMap = CollectUtils.newHashMap();
    for (Graduation graduation : graduations) {
      graduationMap.put(graduation.getStd(), graduation);
    }
    return graduationMap;
  }
}
