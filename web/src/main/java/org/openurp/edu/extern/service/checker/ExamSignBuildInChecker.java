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
package org.openurp.edu.extern.service.checker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.beangle.commons.collection.CollectUtils;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExclusiveSubject;
import org.openurp.edu.extern.model.ExamSignUpConfig;
import org.openurp.edu.extern.model.ExamSignUpSetting;

public class ExamSignBuildInChecker extends AbstarctExamSignUpChecker {

  public String doCheck(Student student, ExamSignUpSetting setting) {
    if (!student.getState().isInschool()) { return "other.failure.isInSchoolOrIsInValidLimit"; }

    // 不能重复报名
    if (otherExamSignUpDao.isRepeatSignUp(student, setting)) { return "other.failure.repeatSignUp"; }

    // FIXME zhouqi 2011-06-10 下面一段还需要修改
    // 通过科目冲突表来查看是否有冲突
    // 这次已经报名的科目和现在要报名的科目比较，是否冲突
    ExamSignUpConfig config = setting.getConfig();
    if (CollectUtils.isNotEmpty(config.getExclusiveSubjects())) {
      Set<ExamSubject> categories = new HashSet<ExamSubject>(otherExamSignUpDao.getSignUpSubjects(
          student, setting.getConfig()));
      if (CollectUtils.isNotEmpty(categories)) {
        ExamSubject subject = setting.getSubject();
        for (ExclusiveSubject exclusiveSubject : config.getExclusiveSubjects()) {
          if ((exclusiveSubject.getSubjectOne().equals(subject) && categories.contains(exclusiveSubject
              .getSubjectTwo()))
              || (exclusiveSubject.getSubjectTwo().equals(subject) && categories.contains(exclusiveSubject
                  .getSubjectOne()))) { return "other.failure.categoryExclusive"; }
        }
      }
    }

    // 科目冲突(根据时间)
    if (isTimeCollision(setting, student)) { return "other.failure.categoryExclusive"; }

    // 所有科目只要通过了就不能再报名
    if (!setting.isReExamAllowed()) {
      if (otherGradeService.isPass(student, setting.getSubject())) { return "other.failure.isHasPassed"; }
    }
    return null;
  }

  public boolean isRepeatSignUp() {
    return false;
  }

  private boolean isTimeCollision(ExamSignUpSetting setting, Student student) {
    ExamSignUpConfig config = setting.getConfig();
    List<ExamSignUpSetting> signedSettings = otherExamSignUpDao.getSignedSettings(student, config);
    for (ExamSignUpSetting existed : signedSettings) {
      if (existed.isTimeCollision(setting)) { return true; }
    }
    return false;
  }
}
