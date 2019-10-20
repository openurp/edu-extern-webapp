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

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.collection.Order;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Strings;
import org.beangle.commons.transfer.exporter.Context;
import org.beangle.commons.transfer.exporter.Exporter;
import org.openurp.code.edu.model.GradingMode;
import org.openurp.edu.base.model.Semester;
import org.openurp.edu.extern.code.model.ExamCategory;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.grade.data.ExternExamGradeData;
import org.openurp.edu.extern.grade.export.ExternExamGradePropertyExtractor;
import org.openurp.edu.extern.grade.utils.ParamUtils;
import org.openurp.edu.extern.model.ExamSignupConfig;
import org.openurp.edu.extern.model.ExternExamGrade;
import org.openurp.edu.grade.course.model.CourseGrade;
import org.openurp.edu.graduation.audit.model.GraduateResult;
import org.openurp.edu.graduation.audit.model.GraduateSession;
import org.openurp.edu.web.action.SemesterSupportAction;

public class SearchAction extends SemesterSupportAction {

  /**
   * 主页面
   */
  public String index() {
    OqlBuilder<ExamSignupConfig> seasonQuery = OqlBuilder.from(ExamSignupConfig.class, "season");
    seasonQuery.where("season.project = :project", getProject());
    seasonQuery.orderBy("season.beginAt desc");
    put("seasons", entityDao.search(seasonQuery));

    put("examSubjects", codeService.getCodes(ExamSubject.class));
    put("examCategories", codeService.getCodes(ExamCategory.class));
    // put("calendars", semesterService.getCalendars(getProjects()));
    put("gradingModes", codeService.getCodes(GradingMode.class));
    put("departments", getTeachDeparts());
    put("semesters", entityDao.getAll(Semester.class));

    put("sessions", entityDao.getAll(GraduateSession.class));
    return forward();
  }

  /**
   * 查询
   */
  public String search() {
    put("examGrades", entityDao.search(getQueryBuilder()));
    return forward();
  }

  @Override
  protected OqlBuilder<ExternExamGrade> getQueryBuilder() {
    OqlBuilder<ExternExamGrade> builder = OqlBuilder.from(ExternExamGrade.class, "examGrade");
    populateConditions(builder);
    Float from = getFloat("from");
    Float to = getFloat("to");
    if (null != from) {
      if (null != to) {
        builder.where("examGrade.score between :F and :T", from, to);
      } else {
        builder.where("examGrade.score >=:F", from);
      }
    } else if (null != to) {
      builder.where("examGrade.score <=:T", to);
    }
    builder.where("examGrade.std.project = :project", getProject());
    Date fromAt = ParamUtils.getOnlyYMDDate("fromAt");
    Date toAt = ParamUtils.getOnlyYMDDate("toAt");
    if (null != fromAt) {
      builder.where("to_date(to_char(examGrade.updatedAt, 'yyyy-MM-dd'), 'yyyy-MM-dd') >= :fromAt", fromAt);
    }
    if (null != toAt) {
      builder.where("to_date(to_char(examGrade.updatedAt, 'yyyy-MM-dd'), 'yyyy-MM-dd') <= :toAt", toAt);
    }

    Date convertFromAt = ParamUtils.getOnlyYMDDate("convertFromAt");
    Date convertToAt = ParamUtils.getOnlyYMDDate("convertToAt");
    Boolean hasCourseGrades = getBoolean("hasCourseGrades");
    if (null != convertFromAt || null != convertToAt) {
      if (null != convertFromAt && null != convertToAt) {
        builder.where(
            "exists(from examGrade.grades as cg where to_date(to_char(cg.updatedAt, 'yyyy-MM-dd'), 'yyyy-MM-dd') between :convertFrom and :convertTo)",
            convertFromAt, convertToAt);
      } else if (null != convertFromAt) {
        builder.where(
            "exists(from examGrade.grades as cg where to_date(to_char(cg.updatedAt, 'yyyy-MM-dd'), 'yyyy-MM-dd') >= :convertFrom)",
            convertFromAt);
      } else if (null != convertToAt) {
        builder.where(
            "exists(from examGrade.grades as cg where to_date(to_char(cg.updatedAt, 'yyyy-MM-dd'), 'yyyy-MM-dd') <= :convertToAt)",
            convertToAt);
      }
    }

    if (null != hasCourseGrades) {
      builder.where((hasCourseGrades.booleanValue() ? StringUtils.EMPTY : "not ")
          + "exists (from examGrade.grades courseGrade)");
    }

    Long sessionId = getLongId("session");
    if (null != sessionId) {
      GraduateSession session = entityDao.get(GraduateSession.class, sessionId);
      put("graduateSession", session);
      StringBuilder hql1 = new StringBuilder();
      hql1.append("exists (");
      hql1.append("  from ").append(GraduateResult.class.getName()).append(" result");
      hql1.append(" where result.std = examGrade.std");
      hql1.append("   and result.session = :session");
      hql1.append(")");
      builder.where(hql1.toString(), session);
    }

    builder.orderBy(get(Order.ORDER_STR)).limit(getPageLimit());
    return builder;
  }

  @Override
  protected void configExporter(Exporter exporter, Context context) throws IOException {
    String dataInSource = get("dataInSource");
    if ("courseGrade".equals(dataInSource)) {
      context.put("items", getInCourseGradeData());
    } else {
      context.put("items", getInNormalData());
    }
    context.put(Context.EXTRACTOR, new ExternExamGradePropertyExtractor(dataInSource));
  }

  private List<ExternExamGrade> getInNormalData() {
    Long[] examGrades = Strings.splitToLong(get("examGradeIds"));
    if (examGrades.length != 0) {
      return entityDao.get(ExternExamGrade.class, examGrades);
    } else {
      return entityDao.search(getQueryBuilder().limit(null));
    }
  }

  /**
   * @return
   */
  private List<ExternExamGradeData> getInCourseGradeData() {
    List<ExternExamGrade> inNormalData = getInNormalData();
    List<ExternExamGradeData> dataList = CollectUtils.newArrayList();
    for (ExternExamGrade eeGrade : inNormalData) {
      if (CollectionUtils.isEmpty(eeGrade.getGrades())) {
        dataList.add(new ExternExamGradeData(eeGrade, null));
      } else {
        for (CourseGrade courseGrade : eeGrade.getGrades()) {
          dataList.add(new ExternExamGradeData(eeGrade, courseGrade));
        }
      }
    }
    return dataList;
  }

  public String categorySubject() {
    Integer categoryId = getInt("categoryId");
    if (null != categoryId) {
      put("subjects", entityDao.get(ExamSubject.class, "category.id", categoryId));
    } else {
      put("subjects", Collections.emptyList());
    }
    return forward();
  }

  /**
   * 打印成绩
   *
   * @return
   */
  public String printShow() {
    Long[] examGrades = Strings.splitToLong(get("examGradeIds"));
    if (examGrades.length != 0) {
      put("examGrades", entityDao.get(ExternExamGrade.class, examGrades));
    } else {
      put("examGrades", entityDao.search(getQueryBuilder().limit(null)));
    }
    return forward();
  }

}
