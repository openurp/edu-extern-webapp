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

import java.util.Arrays;
import java.util.List;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.lang.Strings;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.model.ExamSignupCondition;
import org.openurp.edu.extern.model.ExamSignupSetting;

/**
 * 年级限制
 *
 * @author chaostone
 */
public class ExamConditionChecker extends AbstarctExamSignupChecker {

  protected String doCheck(Student student, ExamSignupSetting setting) {
    if (setting.getConditions().isEmpty()) return null;

    List<ExamSignupCondition> exclusives = CollectUtils.newArrayList();
    List<ExamSignupCondition> inclusives = CollectUtils.newArrayList();
    int inclusiveCount = 0;
    for (ExamSignupCondition condition : setting.getConditions()) {
      if (condition.isInclusive()) inclusiveCount += 1;
      boolean gradeMatch = false;
      boolean educationMatch = false;
      if (Strings.isNotEmpty(condition.getGrades())) {
        List<String> grades = Arrays.asList(Strings.split(condition.getGrades(), ","));
        if (grades.contains(student.getGrade())) gradeMatch = true;
      } else {
        gradeMatch = true;
      }

      if (null != condition.getEducation()) {
        if (condition.getEducation().equals(student.getEducation())) educationMatch = true;
      } else {
        educationMatch = true;
      }
      if (gradeMatch && educationMatch) {
        if (condition.isInclusive()) {
          inclusives.add(condition);
        } else {
          exclusives.add(condition);
        }
      }

    }
    if (!exclusives.isEmpty()) return "extern.exam.failure.gradeLimit";
    if (inclusiveCount > 0 && inclusives.isEmpty()) return "extern.exam.failure.gradeLimit";
    return null;
  }
}
