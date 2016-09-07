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
package org.openurp.edu.other.dao;

import java.util.List;

import org.openurp.base.model.Semester;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.other.code.model.OtherExamCategory;
import org.openurp.edu.other.code.model.OtherExamSubject;
import org.openurp.edu.other.model.OtherExamSignUp;
import org.openurp.edu.other.model.OtherExamSignUpConfig;
import org.openurp.edu.other.model.OtherExamSignUpSetting;

public interface OtherExamSignUpDao {

  public int getSignUpCount(OtherExamSignUpSetting setting);

  /**
   * 根据学生id和试科目设置来判断一个学生是否重复报
   */
  public boolean isRepeatSignUp(Student std, OtherExamSignUpSetting setting);

  /**
   * 根据期号和学生获得学生已经报名的科目
   * 
   * @param std
   * @param config
   */
  public List<OtherExamCategory> getSignUpCategories(Student std, OtherExamSignUpConfig config);

  /**
   * 根据期号和学生获得学生已经报名的settings
   * 
   * @param std
   * @param config
   * @return
   */
  public List<OtherExamSignUpSetting> getSignedSettings(Student std, OtherExamSignUpConfig config);

  public List<OtherExamSubject> getSignUpSubjects(Student std, OtherExamSignUpConfig config);

  public OtherExamSignUp getSignUp(Student std, OtherExamSignUpSetting setting);

  public OtherExamSignUp getSignUp(Student std, Semester semester, OtherExamSubject subject);
}
