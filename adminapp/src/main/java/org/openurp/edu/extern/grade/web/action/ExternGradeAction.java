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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.Entity;
import org.beangle.commons.transfer.exporter.PropertyExtractor;
import org.openurp.code.edu.model.EduCategory;
import org.openurp.code.edu.model.EducationLevel;
import org.openurp.code.edu.model.GradingMode;
import org.openurp.edu.base.model.Course;
import org.openurp.edu.base.model.Semester;
import org.openurp.edu.base.service.StudentService;
import org.openurp.edu.extern.grade.export.ExternGradePropertyExtractor;
import org.openurp.edu.extern.grade.service.CourseGradeUpdator;
import org.openurp.edu.extern.grade.utils.ParamUtils;
import org.openurp.edu.extern.model.ExternGrade;
import org.openurp.edu.extern.model.ExternSchool;
import org.openurp.edu.grade.course.model.CourseGrade;
import org.openurp.edu.grade.course.service.GradeRateService;
import org.openurp.edu.program.plan.model.PlanCourse;
import org.openurp.edu.program.plan.service.CoursePlanProvider;
import org.openurp.edu.web.action.RestrictionSupportAction;

/**
 * 外校课程成绩
 *
 * @author zhouqi 2018年11月26日
 */
public class ExternGradeAction extends RestrictionSupportAction {

  protected StudentService studentService;

  protected CoursePlanProvider coursePlanProvider;

  protected GradeRateService gradeRateService;

  protected String getEntityName() {
    return ExternGrade.class.getName();
  }

  protected void indexSetting() {
    put("schools", codeService.getCodes(ExternSchool.class));
    put("levels", codeService.getCodes(EducationLevel.class));
    put("eduCategories", codeService.getCodes(EduCategory.class));
  }

  @Override
  protected OqlBuilder<ExternGrade> getQueryBuilder() {
    OqlBuilder<ExternGrade> builder = super.getQueryBuilder();
    Date fromAt = ParamUtils.getOnlyYMDDate("fromAt");
    Date toAt = ParamUtils.getOnlyYMDDate("toAt");
    if (null != fromAt) {
      builder.where("to_date(to_char(externGrade.updatedAt, 'yyyy-MM-dd'), 'yyyy-MM-dd') >= :fromAt", fromAt);
    }
    if (null != toAt) {
      builder.where("to_date(to_char(externGrade.updatedAt, 'yyyy-MM-dd'), 'yyyy-MM-dd') <= :toAt", toAt);
    }
    Boolean hasCourseGrades = getBoolean("hasCourseGrades");
    if (null != hasCourseGrades) {
      builder.where((hasCourseGrades.booleanValue() ? StringUtils.EMPTY : "not ")
          + "exists (from externGrade.courseGrades courseGrade)");
    }
    return builder;
  }

  @Override
  protected void editSetting(Entity<?> entity) {
    indexSetting();
  }

  public String loadStdAjax() {
    put("std", studentService.getStudent(getProject().getId(), get("code")));
    return forward();
  }

  public String checkAjax() {
    OqlBuilder<ExternGrade> builder = OqlBuilder.from(ExternGrade.class, "externGrade");
    builder.where("externGrade.std.id = :stdId", getLongId("std"));
    builder.where("externGrade.courseName = :courseName", get("courseName"));
    builder.where("externGrade.acquiredOn = :acquiredOn", getDate("acquiredOn"));
    Long id = getLong("id");
    if (null != id) {
      builder.where("externGrade.id != :id", id);
    }
    put("isOk", CollectionUtils.isEmpty(entityDao.search(builder)));
    return forward();
  }

  public String convertList() {
    ExternGrade externGrade = entityDao.get(ExternGrade.class, getLongId("externGrade"));
    put("externGrade", externGrade);

    List<PlanCourse> planCourses = coursePlanProvider.getPlanCourses(externGrade.getStd());
    if (CollectionUtils.isEmpty(planCourses)) { return "noPlanMsg"; }

    Map<Course, PlanCourse> coursesMap = CollectUtils.newHashMap();
    for (PlanCourse planCourse : planCourses) {
      coursesMap.put(planCourse.getCourse(), planCourse);
    }

    List<CourseGrade> courseGrades = entityDao.get(CourseGrade.class, new String[] { "std", "course" },
        externGrade.getStd(), coursesMap.keySet());
    courseGrades.addAll(externGrade.getCourseGrades());
    for (CourseGrade courseGrade : courseGrades) {
      if (courseGrade.isPassed()) {
        coursesMap.remove(courseGrade.getCourse());
      }
    }
    put("planCourses", coursesMap.values());
    put("gradingModes", codeService.getCodes(GradingMode.class));
    return forward();
  }

  public String convert() {
    ExternGrade externGrade = entityDao.get(ExternGrade.class, getLongId("externGrade"));
    List<PlanCourse> planCourses = entityDao.get(PlanCourse.class, getLongIds("planCourse"));
    String remark = "成绩来自外校成绩";
    CourseGradeUpdator updator = new CourseGradeUpdator(entityDao, gradeRateService);
    for (PlanCourse planCourse : planCourses) {
      for (Semester semester : coursePlanProvider.getSemesterByPlanCourse(planCourse)) {
        CourseGrade courseGrade = updator.updateGrade(planCourse, externGrade.getStd(), semester,
            getInt("gradingMode.id" + planCourse.getId()), getFloat("score" + planCourse.getId()),
            get("scoreText" + planCourse.getId()), remark);
        externGrade.addCourseGrade(courseGrade);
      }
    }
    entityDao.saveOrUpdate(externGrade);
    return redirect("search", "info.action.success");
  }

  public String undistribute() {
    ExternGrade externGrade = entityDao.get(ExternGrade.class, getLongId("externGrade"));
    CourseGrade courseGrade = entityDao.get(CourseGrade.class, getLongId("courseGrade"));
    externGrade.getCourseGrades().remove(courseGrade);
    entityDao.saveOrUpdate(externGrade);
    entityDao.remove(courseGrade);
    return redirect("search", "info.action.success");
  }

  @Override
  protected PropertyExtractor getPropertyExtractor() {
    return new ExternGradePropertyExtractor();
  }

  public String identificationReport() {
    OqlBuilder<ExternGrade> builder = getQueryBuilder();
    builder.limit(null);
    put("externGrades", entityDao.search(builder));
    return forward();
  }

  public void setStudentService(StudentService studentService) {
    this.studentService = studentService;
  }

  public void setCoursePlanProvider(CoursePlanProvider coursePlanProvider) {
    this.coursePlanProvider = coursePlanProvider;
  }

  public void setGradeRateService(GradeRateService gradeRateService) {
    this.gradeRateService = gradeRateService;
  }
}
