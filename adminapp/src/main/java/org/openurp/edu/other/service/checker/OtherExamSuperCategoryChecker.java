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
package org.openurp.edu.other.service.checker;

import org.openurp.edu.base.model.Student;
import org.openurp.edu.other.model.OtherExamSignUpSetting;

public class OtherExamSuperCategoryChecker extends AbstarctOtherExamSignUpChecker {

  protected String doCheck(Student student, OtherExamSignUpSetting setting) {
    // 考试科目设置的约束
    // 判断要求通过的科目是否已经通过
    if (null != setting.getSuperSubject()) {
      // FIXME zhouqi 2011-06-10 下面语句需要修改
      // if (!otherGradeService.isPass(student, setting.getSuperCategory())) {
      // return "otherExam.failure.isMustPassed";
      // }
    }
    return null;
  }
}
