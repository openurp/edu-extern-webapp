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

import org.beangle.commons.dao.EntityDao;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.model.ExamSignUpSetting;
import org.openurp.edu.extern.model.ExamGrade;

public class ExamSuperCategoryChecker extends AbstarctExamSignUpChecker {

  private EntityDao entityDao;

  protected String doCheck(Student student, ExamSignUpSetting setting) {
    // 考试科目设置的约束,判断要求通过的科目是否已经通过
    if (null != setting.getSuperSubject() && checkAppliable(setting, student)) {
      OqlBuilder<ExamGrade> builder = OqlBuilder.from(ExamGrade.class, "otherGrade");
      builder.where("otherGrade.std = :student", student)
          .where("otherGrade.subject = :subject", setting.getSuperSubject())
          .where("otherGrade.passed is true");
      if (entityDao.search(builder).isEmpty()) {
        return "error.other.notPassSuperCategory";
      } else {
        return null;
      }
    }
    return null;
  }

  // 专升本，六级不做检查
  private boolean checkAppliable(ExamSignUpSetting setting, Student std) {
    if (std.getEducation().getName().contains("专升本")) {
      return !setting.getSubject().getName().contains("大学英语六级");
    } else {
      return true;
    }
  }

  public void setEntityDao(EntityDao entityDao) {
    this.entityDao = entityDao;
  }

}
