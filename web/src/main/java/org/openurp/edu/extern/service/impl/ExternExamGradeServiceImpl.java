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
package org.openurp.edu.extern.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.collection.Order;
import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.Condition;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.code.model.ExamCategory;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExternExamGrade;
import org.openurp.edu.extern.service.ExternExamGradeService;

public class ExternExamGradeServiceImpl extends BaseServiceImpl implements ExternExamGradeService {

  public void saveOrUpdate(ExternExamGrade examGrade) {
    entityDao.saveOrUpdate(ExternExamGrade.class.getName(), examGrade);
  }

  public ExternExamGrade getBestGrade(Student std, ExamCategory category) {
    OqlBuilder<ExternExamGrade> query = OqlBuilder.from(ExternExamGrade.class, "grade");
    query.where("grade.std = :std", std);
    query.where("grade.category = :category", category);
    query.orderBy(Order.desc("grade.score"));
    List<ExternExamGrade> grades = entityDao.search(query);
    return CollectUtils.isEmpty(grades) ? null : grades.get(0);
  }

  /**
   * isPerTermBest 为 null 或 false 时，为查询全部；否则为每学期最佳成绩
   */
  public Collection<ExternExamGrade> getExternExamGrades(Student std, Boolean isBest) {
    OqlBuilder<ExternExamGrade> query = OqlBuilder.from(ExternExamGrade.class, "grade");
    query.where(new Condition("grade.std=:std", std));
    query.orderBy(Order.parse("grade.category.kind.id,grade.score"));
    List<ExternExamGrade> grades = entityDao.search(query);
    if (CollectUtils.isEmpty(grades)) {
      return CollectUtils.newArrayList(0);
    } else {
      if (null != isBest && isBest.booleanValue()) {
        Map<Integer, ExternExamGrade> gradesMap = CollectUtils.newHashMap();
        for (ExternExamGrade grade : grades) {
          gradesMap.put(grade.getSubject().getId(), grade);
        }
        return gradesMap.values();
      } else {
        return grades;
      }
    }
  }

  public List<ExternExamGrade> getPassGradesOf(Student std, Collection<ExamSubject> examSubjects) {
    OqlBuilder<ExternExamGrade> query = OqlBuilder.from(ExternExamGrade.class, "grade");
    query.where("grade.std = :std", std);
    query.where("grade.subject in (:examSubjects)", examSubjects);
    query.where("grade.passed=true");
    return entityDao.search(query);
  }

  public boolean isPass(Student std, ExamSubject subject) {
    OqlBuilder<ExternExamGrade> query = OqlBuilder.from(ExternExamGrade.class, "grade");
    query.where("grade.std = :std", std);
    query.where("grade.subject = :subject", subject);
    query.where("grade.passed = true");
    return CollectUtils.isNotEmpty(entityDao.search(query));
  }
}
