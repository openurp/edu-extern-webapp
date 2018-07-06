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

import org.beangle.commons.dao.EntityDao;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.dao.ExamSignupDao;
import org.openurp.edu.extern.model.ExamSignupSetting;
import org.openurp.edu.extern.service.ExternExamGradeService;

public abstract class AbstarctExamSignupChecker implements ExamSignupChecker {

  protected ExternExamGradeService examGradeService;

  protected ExamSignupDao examSignupDao;

  public String check(Student student, ExamSignupSetting setting) {
    if (!except(student, setting)) { return doCheck(student, setting); }
    return null;
  }

  /**
   * 进行检查
   *
   * @param student
   * @param setting
   * @return
   */
  abstract protected String doCheck(Student student, ExamSignupSetting setting);

  public boolean hasExternExamGrade(Student student, EntityDao entityDao, String code) {
    return false;
  }

  /**
   * 例外情况
   *
   * @param student
   * @param setting
   * @return
   */
  protected boolean except(Student student, ExamSignupSetting setting) {
    return false;
  }

  public void setExternExamGradeService(ExternExamGradeService examGradeService) {
    this.examGradeService = examGradeService;
  }

  public void setExamSignupDao(ExamSignupDao examSignupDao) {
    this.examSignupDao = examSignupDao;
  }
}
