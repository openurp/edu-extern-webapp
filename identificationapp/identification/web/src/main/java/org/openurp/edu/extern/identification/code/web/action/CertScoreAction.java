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

import java.sql.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.collection.page.PageLimit;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.edu.base.model.Course;
import org.openurp.edu.extern.identification.model.CertScore;
import org.openurp.edu.extern.identification.model.CertScoreCourse;

/**
 * @author zhouqi 2018年1月20日
 *
 */
public class CertScoreAction extends CertificationSupportAction<Integer, CertScore> {

  public String coursesAjax() {
    Long[] ids = getLongIds("course");

    OqlBuilder<Course> builder = OqlBuilder.from(Course.class, "course");
    populateConditions(builder);
    if (ArrayUtils.isNotEmpty(ids)) {
      builder.where("course.id not in (:ids)", ids);
    }
    Date now = new Date(System.currentTimeMillis());
    builder.where("course.beginOn <= :now", now);
    builder.where("course.endOn is null or course.endOn >= :now", now);
    String orderBy = get("orderBy");
    if (StringUtils.isBlank(orderBy)) {
      orderBy = "course.id";
    } else {
      orderBy += "," + "course.id";
    }
    builder.orderBy(orderBy);
    builder.limit(new PageLimit(getPageIndex(), 10));
    put("courses", entityDao.search(builder));
    return forward();
  }

  public String loadCourseDataAjax() {
    List<CertScoreCourse> csCourses = CollectUtils.newArrayList();

    for (int i = 0; i > -1; i++) {
      Long courseId = getLong("csCourse" + i + ".course.id");
      if (null == courseId) {
        break;
      }

      CertScoreCourse csCourse = populateEntity(CertScoreCourse.class, "csCourse" + i);
      if (null == csCourse.getId()) {
        csCourse.setCourse(entityDao.get(Course.class, courseId));
      }
      csCourses.add(csCourse);
    }
    put("csCourses", csCourses);
    return forward();
  }

  public String checkAjax() {
    Integer id = getInt("id");
    Integer examSubjectId = getIntId("examSubject");
    Integer certTypeId = getIntId("certType");
    Integer certLevelId = getIntId("certLevel");
    Integer divisionId = getIntId("division");
    Integer examTimeId = getIntId("examTime");

    OqlBuilder<CertScore> builder = OqlBuilder.from(CertScore.class, "certScore");
    if (null != id) {
      builder.where("certScore.id != :id", id);
    }
    builder.where("certScore.examSubject.id = :examSubjectId", examSubjectId);
    if (null != certTypeId) {
      builder.where("certScore.certType.id = :certTypeId or certScore.certType is null", certTypeId);
    }
    if (null != certLevelId) {
      builder.where("certScore.certLevel.id = :certLevelId or certScore.certLevel is null", certLevelId);
    }
    if (null != divisionId) {
      builder.where("certScore.division.id = :divisionId or certScore.division is null", divisionId);
    }
    if (null != examTimeId) {
      builder.where("certScore.examTime.id = :examTimeId or certScore.examTime is null", examTimeId);
    }
    put("isOk", entityDao.search(builder).isEmpty());

    return forward();
  }

  protected boolean beforeSave(CertScore certScore) {
    Long[] courseIds = getLongIds("course");

    certScore.clearCourses();
    for (int i = 0; i < courseIds.length; i++) {
      certScore.addCourse(populateEntity(CertScoreCourse.class, "csCourse" + i));
    }

    return super.beforeSave(certScore);
  }
}
