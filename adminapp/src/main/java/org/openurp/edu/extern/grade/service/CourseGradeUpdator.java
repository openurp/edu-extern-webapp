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
package org.openurp.edu.extern.grade.service;

import java.util.List;
import java.util.Map;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.EntityDao;
import org.beangle.commons.lang.Numbers;
import org.beangle.security.Securities;
import org.openurp.code.edu.model.CourseTakeType;
import org.openurp.code.edu.model.GradeType;
import org.openurp.code.edu.model.GradingMode;
import org.openurp.edu.base.model.Semester;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.course.model.CourseTaker;
import org.openurp.edu.grade.Grade;
import org.openurp.edu.grade.course.model.CourseGrade;
import org.openurp.edu.grade.course.model.GaGrade;
import org.openurp.edu.grade.course.service.GradeRateService;
import org.openurp.edu.grade.course.service.ScoreConverter;
import org.openurp.edu.program.plan.model.PlanCourse;

public class CourseGradeUpdator {

  private final EntityDao entityDao;
  private final GradeRateService gradeRateService;
  private final GradeType gaGradeType;
  private final CourseTakeType courseTakeType;
  private final Map<Integer, GradingMode> gradingModeMap;

  public CourseGradeUpdator(EntityDao entityDao, GradeRateService gradeRateService) {
    super();
    this.entityDao = entityDao;
    this.gradeRateService = gradeRateService;
    gaGradeType = entityDao.get(GradeType.class, GradeType.GA_ID);
    courseTakeType = new CourseTakeType(CourseTakeType.Exemption);
    List<GradingMode> gradingModes = entityDao.getAll(GradingMode.class);
    gradingModeMap = CollectUtils.newHashMap();
    for (GradingMode gradingMode : gradingModes) {
      gradingModeMap.put(gradingMode.getId(), gradingMode);
    }
  }

  public CourseGrade updateGrade(PlanCourse pc, Student std, Semester semester, Integer gradingModeId,
      Float score, String scoreText, String remark) {
    String operator = Securities.getUsername();
    List<CourseGrade> courseGrades = entityDao.get(CourseGrade.class,
        new String[] { "std", "course", "semester" }, std, pc.getCourse(), semester);
    CourseGrade courseGrade = null;
    if (courseGrades.isEmpty()) {
      courseGrade = new CourseGrade();
      courseGrade.setProject(std.getProject());
      courseGrade.setStd(std);
      courseGrade.setSemester(semester);
      courseGrade.setCourse(pc.getCourse());
      courseGrade.setCourseType(pc.getGroup().getCourseType());
    } else {
      courseGrade = courseGrades.get(0);
    }
    List<CourseTaker> takers = entityDao.get(CourseTaker.class, new String[] { "std", "course", "semester" },
        std, pc.getCourse(), semester);
    if (takers.size() == 1) {
      CourseTaker taker = takers.get(0);
      courseGrade.setClazz(taker.getClazz());
      courseGrade.setCrn(taker.getClazz().getCrn());
      taker.setTakeType(courseTakeType);
      taker.setFreeListening(true);
      entityDao.saveOrUpdate(taker);
    }
    courseGrade.setCourseTakeType(courseTakeType);
    courseGrade.setExamMode(pc.getCourse().getExamMode());
    GradingMode gradingMode = gradingModeMap.get(gradingModeId);
    courseGrade.setGradingMode(gradingMode);
    courseGrade.setFreeListening(true);
    courseGrade.setPassed(true);
    courseGrade.setScoreText(scoreText);
    courseGrade.setScore(score);
    if (null == score && gradingMode.isNumerical() && Numbers.isNumber(scoreText)) {
      courseGrade.setScore(Float.parseFloat(scoreText));
    }
    courseGrade.setStatus(Grade.Status.Published);
    ScoreConverter converter = gradeRateService.getConverter(std.getProject(), courseGrade.getGradingMode());
    courseGrade.setGp(converter.calcGp(courseGrade.getScore()));
    courseGrade.setOperator(operator);
    courseGrade.setUpdatedAt(new java.util.Date());
    courseGrade.setRemark(remark);

    GaGrade gaGrade = courseGrade.getGaGrade(gaGradeType);
    if (gaGrade == null) {
      gaGrade = new GaGrade(gaGradeType, courseGrade.getScore());
      courseGrade.addGaGrade(gaGrade);
    }
    gaGrade.setGradingMode(courseGrade.getGradingMode());
    gaGrade.setScoreText(courseGrade.getScoreText());
    gaGrade.setScore(courseGrade.getScore());
    gaGrade.setPassed(true);
    gaGrade.setStatus(Grade.Status.Published);
    gaGrade.setGp(courseGrade.getGp());
    gaGrade.setOperator(operator);
    gaGrade.setUpdatedAt(new java.util.Date());
    gaGrade.setRemark(remark);

    return courseGrade;
  }

}
