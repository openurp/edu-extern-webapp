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
package org.openurp.edu.extern.grade.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.query.builder.Condition;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.base.model.Department;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.eams.util.stat.StatGroup;
import org.openurp.edu.eams.util.stat.StatHelper;
import org.openurp.edu.extern.code.model.ExamCategory;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExternExamGrade;
import org.openurp.edu.web.action.RestrictionSupportAction;

/**
 * 资格考试成绩统计
 * 
 * @author chaostone
 */
public class StatAction extends RestrictionSupportAction {

  public String index() {
    put("examSubjects", codeService.getCodes(ExamSubject.class));
    put("examCategories", codeService.getCodes(ExamCategory.class));
    return forward();
  }

  public String statGrade() {
    String grade = get("grade.std.grade");
    Integer categoryId = getInt("grade.subject.category.id");
    Integer subjectId = getInt("grade.subject.id");
    OqlBuilder<Student> stdquery = OqlBuilder.from(Student.class, "std");
    OqlBuilder gradeQuery = OqlBuilder.from(ExternExamGrade.class, "grade");
    if (null != grade) {
      stdquery.where("std.grade like :grade", grade);
      gradeQuery.where(Condition.like("grade.std.grade", grade));
    }
    stdquery.select("std.department.id,count(*)");
    stdquery.groupBy("std.department.id");

    List<ExamCategory> categories = CollectUtils.newArrayList();
    List<ExamSubject> subjects = CollectUtils.newArrayList();
    if (null != subjectId) {
      gradeQuery.where("grade.subject.id=:subjectId", subjectId);
      subjects.add(codeService.getCode(ExamSubject.class, subjectId));
    } else {
      OqlBuilder<ExamSubject> subjectquery = OqlBuilder.from(ExamSubject.class, "subject");
      // OqlBuilder categoryQuery = OqlBuilder.from(ExamCategory.class, "category");
      // categoryQuery.where("category.enabled=true"));
      if (null != categoryId) {
        subjectquery.where("subject.category.id=:categoryId", categoryId);
        // categoryQuery.where("category.id=:categoryId", categoryId));
        gradeQuery.where("grade.subject.category.id=:categoryId", categoryId);
      }
      subjects = entityDao.search(subjectquery);
      // categories = entityDao.search(categoryQuery);
    }
    put("subjects", subjects);
    // put("categories", categories);
    gradeQuery.select("grade.std.department.id,grade.subject.id,count(*)");
    gradeQuery.groupBy("grade.std.department.id,grade.subject.id");
    // gradeQuery.where("grade.passed=true"));
    List<Object[]> datas = (List) entityDao.search(stdquery);
    new StatHelper(entityDao).replaceIdWith(datas, new Class[] { Department.class });
    Map departMap = new HashMap();
    for (Object[] data : datas) {
      departMap.put(((Department) data[0]).getId().toString(), data);
    }

    List<Object[]> data2 = (List) entityDao.search(gradeQuery);
    new StatHelper(entityDao).replaceIdWith(data2, new Class[] { Department.class, ExamCategory.class });
    List<StatGroup> statGroups = StatGroup.buildStatGroups(data2);
    Map gradeMap = new HashMap();
    for (StatGroup g : statGroups) {
      gradeMap.put(((Department) g.getWhat()).getId().toString(), g);
    }
    put("departMap", departMap);
    put("gradeMap", gradeMap);
    return forward();
  }
}
