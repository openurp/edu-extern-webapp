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

import org.beangle.commons.dao.EntityDao;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.other.dao.OtherExamSignUpDao;
import org.openurp.edu.other.model.OtherExamSignUpSetting;
import org.openurp.edu.other.service.OtherGradeService;

public abstract class AbstarctOtherExamSignUpChecker implements OtherExamSignUpChecker {

  protected OtherGradeService otherGradeService;

  protected OtherExamSignUpDao otherExamSignUpDao;

  public String check(Student student, OtherExamSignUpSetting setting) {
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
  abstract protected String doCheck(Student student, OtherExamSignUpSetting setting);

  public boolean hasOtherGrade(Student student, EntityDao entityDao, String code) {
    return false;
  }

  /**
   * 例外情况
   * 
   * @param student
   * @param setting
   * @return
   */
  protected boolean except(Student student, OtherExamSignUpSetting setting) {
    return false;
  }

  public void setOtherGradeService(OtherGradeService otherGradeService) {
    this.otherGradeService = otherGradeService;
  }

  public void setOtherExamSignUpDao(OtherExamSignUpDao otherExamSignUpDao) {
    this.otherExamSignUpDao = otherExamSignUpDao;
  }
}
