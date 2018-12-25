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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.beangle.commons.collection.Order;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Strings;
import org.beangle.commons.transfer.exporter.PropertyExtractor;
import org.openurp.code.edu.model.GradingMode;
import org.openurp.edu.base.model.Semester;
import org.openurp.edu.extern.code.model.ExamCategory;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.grade.export.ExternExamGradePropertyExtractor;
import org.openurp.edu.extern.grade.utils.ParamUtils;
import org.openurp.edu.extern.model.ExamSignupConfig;
import org.openurp.edu.extern.model.ExternExamGrade;
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
    Boolean hasCourseGrades = getBoolean("hasCourseGrades");
    if (null != hasCourseGrades) {
      builder.where((hasCourseGrades.booleanValue() ? StringUtils.EMPTY : "not ")
          + "exists (from examGrade.courseGrades courseGrade)");
    }
    builder.orderBy(get(Order.ORDER_STR)).limit(getPageLimit());
    return builder;
  }

  /**
   * 导出excel
   *
   * @return
   */
  @Override
  protected Collection<?> getExportDatas() {
    Long[] examGrades = Strings.splitToLong(get("examGradeIds"));
    if (examGrades.length != 0) {
      return entityDao.get(ExternExamGrade.class, examGrades);
    } else {
      return entityDao.search(getQueryBuilder().limit(null));
    }
  }

  @Override
  protected PropertyExtractor getPropertyExtractor() {
    return new ExternExamGradePropertyExtractor();
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
