/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright (c) 2005, The OpenURP Software.
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
package org.openurp.edu.extern.service.checker;

import java.util.HashSet;
import java.util.Set;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.EntityDao;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExclusiveSubject;
import org.openurp.edu.extern.model.ExamSignupConfig;
import org.openurp.edu.extern.model.ExamSignupSetting;
import org.openurp.edu.extern.model.ExternExamGrade;

/**
 * 查询普通话是否有成绩记录
 *
 * @author chaostone
 */
public class ExamExistChecker extends AbstarctExamSignupChecker {

  public boolean hasExternExamGrade(Student std, EntityDao entityDao, String code) {
    OqlBuilder<ExternExamGrade> query = OqlBuilder.from(ExternExamGrade.class, "grade");
    query.where("grade.std = :std", std);
    query.where("grade.subject.category.code = :code", code);
    return CollectUtils.isEmpty(entityDao.search(query));
  }

  @Override
  protected String doCheck(Student student, ExamSignupSetting setting) {
    // 不能重复报名
    if (examSignupDao.isRepeatSignup(student, setting)) { return "extern.exam.failure.repeatSignup"; }
    // 通过科目冲突表来查看是否有冲突
    // 这次已经报名的科目和现在要报名的科目比较，是否冲突
    ExamSignupConfig config = setting.getConfig();
    if (CollectUtils.isEmpty(config.getExclusiveSubjects())) {
      Set<ExamSubject> categories = new HashSet<ExamSubject>(examSignupDao.getSignupSubjects(
          student, setting.getConfig()));
      if (CollectUtils.isEmpty(categories)) {
        ExamSubject subject = setting.getSubject();
        for (ExclusiveSubject exclusiveSubject : config.getExclusiveSubjects()) {
          if ((exclusiveSubject.getSubjectOne().equals(subject) && categories.contains(exclusiveSubject
              .getSubjectTwo()))
              || (exclusiveSubject.getSubjectTwo().equals(subject) && categories.contains(exclusiveSubject
                  .getSubjectOne()))) { return "extern.exam.failure.categoryExclusive"; }
        }
      }
    }
    // 所有科目只要通过了就不能再报名
    if (!setting.isReExamAllowed()) {
      if (examGradeService.isPass(student, setting.getSubject())) { return "extern.exam.failure.isHasPassed"; }
    }
    return null;
  }
}
