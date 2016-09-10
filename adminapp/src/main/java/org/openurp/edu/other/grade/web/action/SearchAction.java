/*
 * OpenURP, Agile Development Scaffold and Toolkit
 *
 * Copyright (c) 2005-2015, OpenURP Software.
 *
 * OpenURP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenURP is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenURP.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openurp.edu.other.grade.web.action;

import java.util.Collection;
import java.util.Collections;

import org.beangle.commons.collection.Order;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.Entity;
import org.beangle.commons.lang.Strings;
import org.openurp.base.model.Semester;
import org.openurp.edu.base.code.model.ScoreMarkStyle;
import org.openurp.edu.grade.course.service.GradeRateService;
import org.openurp.edu.other.code.model.OtherExamCategory;
import org.openurp.edu.other.code.model.OtherExamSubject;
import org.openurp.edu.other.model.OtherExamSignUpConfig;
import org.openurp.edu.other.model.OtherGrade;
import org.openurp.edu.web.action.SemesterSupportAction;

public class SearchAction extends SemesterSupportAction {

  protected GradeRateService gradeRateService;

  /**
   * 主页面
   */
  public String index() {
    OqlBuilder<OtherExamSignUpConfig> seasonQuery = OqlBuilder.from(OtherExamSignUpConfig.class, "season");
    seasonQuery.where("season.project = :project", getProject());
    seasonQuery.orderBy("season.beginAt desc");
    put("seasons", entityDao.search(seasonQuery));

    put("otherExamSubjects", codeService.getCodes(OtherExamSubject.class));
    put("otherExamCategories", codeService.getCodes(OtherExamCategory.class));
    // put("calendars", semesterService.getCalendars(getProjects()));
    put("markStyles", codeService.getCodes(ScoreMarkStyle.class));
    put("departments", getTeachDeparts());
    put("semesters", entityDao.getAll(Semester.class));

    return forward();
  }

  /**
   * 查询
   */
  public String search() {
    put("otherGrades", entityDao.search(getQueryBuilder()));
    return forward();
  }

  @Override
  protected <T extends Entity<?>> OqlBuilder<T> getQueryBuilder() {
    OqlBuilder<T> builder = OqlBuilder.from(OtherGrade.class.getName(), "otherGrade");
    populateConditions(builder);
    Float from = getFloat("from");
    Float to = getFloat("to");
    if (null != from) {
      if (null != to) {
        builder.where("otherGrade.score between :F and :T", from, to);
      } else {
        builder.where("otherGrade.score >=:F", from);
      }
    } else if (null != to) {
      builder.where("otherGrade.score <=:T", to);
    }
    Integer semesterId = getInt("semester.id");
    if (null != semesterId) {
      builder.where("otherGrade.semester.id = :semesterId", semesterId);
    } else {
      builder.where("otherGrade.semester = :semester", getSemester());
    }
    builder.where("otherGrade.std.project = :cproject", getProject());
//    restrictionContext.applyRestriction(builder);
    builder.orderBy(get(Order.ORDER_STR)).limit(getPageLimit());
    return builder;
  }

  public void setGradeRateService(GradeRateService gradeRateService) {
    this.gradeRateService = gradeRateService;
  }

  /**
   * 导出excel
   * 
   * @return
   */
  @Override
  protected Collection<?> getExportDatas() {
    Long[] otherGrades = Strings.splitToLong(get("otherGradeIds"));
    if (otherGrades.length != 0) {
      return entityDao.get(OtherGrade.class, otherGrades);
    } else {
      return entityDao.search(getQueryBuilder().limit(null));
    }
  }

  public String categorySubject() {
    Integer categoryId = getInt("categoryId");
    if (null != categoryId) {
      put("subjects", entityDao.get(OtherExamSubject.class, "category.id", categoryId));
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
    Long[] otherGrades = Strings.splitToLong(get("otherGradeIds"));
    if (otherGrades.length != 0) {
      put("otherGrades", entityDao.get(OtherGrade.class, otherGrades));
    } else {
      put("otherGrades", entityDao.search(getQueryBuilder().limit(null)));
    }
    return forward();
  }

}
