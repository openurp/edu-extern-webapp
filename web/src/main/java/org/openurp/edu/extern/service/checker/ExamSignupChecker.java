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
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.model.ExamSignupSetting;

public interface ExamSignupChecker {

  /** 没有完成要求的上级考试要求 */
  public static final String notPassSuperCategory = "error.extern.exam.notPassSuperCategory";

  /** 重复报名 */
  public static final String existExamSignup = "error.extern.exam.existExamSignup";

  /** 不再报名时间 */
  public static final String notInTime = "error.extern.exam.notInTime";

  /** 通过的不能报名了 */
  public static final String hasPassed = "error.extern.exam.isHasPassed";

  public String check(Student student, ExamSignupSetting setting);

  public boolean hasExternExamGrade(Student student, EntityDao entityDao, String code);
}
