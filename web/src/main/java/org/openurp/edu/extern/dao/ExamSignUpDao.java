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
package org.openurp.edu.extern.dao;

import java.util.List;

import org.openurp.base.model.Semester;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.code.model.ExamCategory;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExamSignUp;
import org.openurp.edu.extern.model.ExamSignUpConfig;
import org.openurp.edu.extern.model.ExamSignUpSetting;

public interface ExamSignUpDao {

  public int getSignUpCount(ExamSignUpSetting setting);

  /**
   * 根据学生id和试科目设置来判断一个学生是否重复报
   */
  public boolean isRepeatSignUp(Student std, ExamSignUpSetting setting);

  /**
   * 根据期号和学生获得学生已经报名的科目
   * 
   * @param std
   * @param config
   */
  public List<ExamCategory> getSignUpCategories(Student std, ExamSignUpConfig config);

  /**
   * 根据期号和学生获得学生已经报名的settings
   * 
   * @param std
   * @param config
   * @return
   */
  public List<ExamSignUpSetting> getSignedSettings(Student std, ExamSignUpConfig config);

  public List<ExamSubject> getSignUpSubjects(Student std, ExamSignUpConfig config);

  public ExamSignUp getSignUp(Student std, ExamSignUpSetting setting);

  public ExamSignUp getSignUp(Student std, Semester semester, ExamSubject subject);
}
