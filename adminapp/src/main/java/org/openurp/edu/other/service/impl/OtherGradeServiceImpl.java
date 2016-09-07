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
package org.openurp.edu.other.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.collection.Order;
import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.Condition;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.other.code.model.OtherExamCategory;
import org.openurp.edu.other.code.model.OtherExamSubject;
import org.openurp.edu.other.model.OtherGrade;
import org.openurp.edu.other.service.OtherGradeService;

public class OtherGradeServiceImpl extends BaseServiceImpl implements OtherGradeService {

  public void saveOrUpdate(OtherGrade otherGrade) {
    entityDao.saveOrUpdate(OtherGrade.class.getName(), otherGrade);
  }

  public OtherGrade getBestGrade(Student std, OtherExamCategory category) {
    OqlBuilder<OtherGrade> query = OqlBuilder.from(OtherGrade.class, "grade");
    query.where("grade.std = :std", std);
    query.where("grade.category = :category", category);
    query.orderBy(Order.desc("grade.score"));
    List<OtherGrade> grades = entityDao.search(query);
    return CollectUtils.isEmpty(grades) ? null : grades.get(0);
  }

  /**
   * isPerTermBest 为 null 或 false 时，为查询全部；否则为每学期最佳成绩
   * 
   * @see org.openurp.edu.other.service.service.teach.grade.other.OtherGradeService#getOtherGrades(org.openurp.edu.base.model.student.Student,
   *      java.lang.Boolean)
   */
  public Collection<OtherGrade> getOtherGrades(Student std, Boolean isBest) {
    OqlBuilder<OtherGrade> query = OqlBuilder.from(OtherGrade.class, "grade");
    query.where(new Condition("grade.std=:std", std));
    query.orderBy(Order.parse("grade.category.kind.id,grade.score"));
    List<OtherGrade> grades = entityDao.search(query);
    if (CollectUtils.isEmpty(grades)) {
      return CollectUtils.newArrayList(0);
    } else {
      if (null != isBest && isBest.booleanValue()) {
        Map<Integer, OtherGrade> gradesMap = CollectUtils.newHashMap();
        for (OtherGrade grade : grades) {
          gradesMap.put(grade.getSubject().getId(), grade);
        }
        return gradesMap.values();
      } else {
        return grades;
      }
    }
  }

  public List<OtherGrade> getPassGradesOf(Student std, Collection<OtherExamSubject> otherExamSubjects) {
    OqlBuilder<OtherGrade> query = OqlBuilder.from(OtherGrade.class, "grade");
    query.where("grade.std = :std", std);
    query.where("grade.subject in (:otherExamSubjects)", otherExamSubjects);
    query.where("grade.passed=true");
    return entityDao.search(query);
  }

  public boolean isPass(Student std, OtherExamSubject subject) {
    OqlBuilder<OtherGrade> query = OqlBuilder.from(OtherGrade.class, "grade");
    query.where("grade.std = :std", std);
    query.where("grade.subject = :subject", subject);
    query.where("grade.passed = true");
    return CollectUtils.isNotEmpty(entityDao.search(query));
  }
}
