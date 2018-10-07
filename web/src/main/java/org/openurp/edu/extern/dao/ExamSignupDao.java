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
package org.openurp.edu.extern.dao;

import java.util.List;

import org.openurp.edu.base.model.Semester;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.code.model.ExamCategory;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExamSignup;
import org.openurp.edu.extern.model.ExamSignupConfig;
import org.openurp.edu.extern.model.ExamSignupSetting;

public interface ExamSignupDao {

  public int getSignupCount(ExamSignupSetting setting);

  /**
   * 根据学生id和试科目设置来判断一个学生是否重复报
   */
  public boolean isRepeatSignup(Student std, ExamSignupSetting setting);

  /**
   * 根据期号和学生获得学生已经报名的科目
   *
   * @param std
   * @param config
   */
  public List<ExamCategory> getSignupCategories(Student std, ExamSignupConfig config);

  /**
   * 根据期号和学生获得学生已经报名的settings
   *
   * @param std
   * @param config
   * @return
   */
  public List<ExamSignupSetting> getSignedSettings(Student std, ExamSignupConfig config);

  public List<ExamSubject> getSignupSubjects(Student std, ExamSignupConfig config);

  public ExamSignup getSignup(Student std, ExamSignupSetting setting);

  public ExamSignup getSignup(Student std, Semester semester, ExamSubject subject);
}
