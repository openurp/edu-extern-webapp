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

import java.util.Arrays;
import java.util.List;

import org.beangle.commons.lang.Strings;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.model.ExamSignUpSetting;

/**
 * 年级限制
 * 
 * @author chaostone
 */
public class ExamGradeChecker extends AbstarctExamSignUpChecker {

  protected String doCheck(Student student, ExamSignUpSetting setting) {
    if (Strings.isNotEmpty(setting.getGrade())) {
      String gradeString = setting.getGrade();
      if (Strings.isNotEmpty(gradeString)) {
        gradeString = Strings.replace(gradeString, "，", ",");
      }
      List<String> grades = Arrays.asList(Strings.split(gradeString, ","));
      if ((setting.isGradePermited() && !grades.contains(student.getGrade()))
          || (!setting.isGradePermited() && grades.contains(student.getGrade()))) { return "other.failure.gradeLimit"; }
    }
    return null;
  }
}
