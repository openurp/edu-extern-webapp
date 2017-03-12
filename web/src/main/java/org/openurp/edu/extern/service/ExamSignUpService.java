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
package org.openurp.edu.extern.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openurp.base.model.Campus;
import org.openurp.base.model.Semester;
import org.openurp.edu.base.model.Project;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExamSignUp;
import org.openurp.edu.extern.model.ExamSignUpConfig;
import org.openurp.edu.extern.model.ExamSignUpSetting;

public interface ExamSignUpService {

  /**
   * 报名(成功时，保存报名记录)<br>
   * 1.检查报名时间是否合适<br>
   * 2.检查如果在报名约束中说明应该完成的其他考试，则检查成绩。<br>
   * 除非该学生的类别处在免考虑的学生类别范围内。<br>
   * 3.不能重复报名<br>
   * 4.在同一时间段内不能同时报两种以上(含)的考试类别 --（5.不能报已经通过的科目,但是六级可以）
   * 
   * @param signUp
   * @return 返回""如果成功,否则返回错误信息
   */
  public String signUp(ExamSignUp signUp, ExamSignUpSetting setting);

  /**
   * 取消报名<br>
   * 1.检查报名时间是否合适
   * 
   * @param std
   * @param setting
   * @return
   */
  public String cancelSignUp(Student std, ExamSignUpSetting setting);

  /**
   * 查询在特定设置条件下的报名记录
   * 
   * @param std
   * @param setting
   * @return
   */
  public ExamSignUp getExamSignUp(Student std, ExamSignUpSetting setting);

  /**
   * 查询在特定设置条件下的报名记录
   * 
   * @param std
   * @param semester
   * @param setting
   * @return
   */
  public ExamSignUp getExamSignUp(Student std, Semester semester, ExamSignUpSetting setting);

  /**
   * 查询在一定时间段内的学生的报名记录
   * 
   * @param std
   * @param start
   * @param end
   * @return
   */
  public List<ExamSignUp> getExamSignUps(Student std, Date start, Date end);

  /**
   * 返回现在开放，并且在时间内的设置
   * 
   * @return
   */
  public List<ExamSignUpSetting> getOpenedSettings(Project project);

  public List<ExamSignUpConfig> getConfigs(Long kindId);

  public List<ExamSubject> getSubjects(Long kindId);

  public List<ExamSubject> getSubjectsByConfigId(Long configId);

  public Set<Campus> getCampusesByConfigId(Long configId);

  /**
   * 判断该学生是否可以报名（true可以报名，false则否
   * 
   * @param student
   * @param setting
   * @return
   */
  public String canSignUp(Student student, ExamSignUpSetting setting);

  /**
   * 获得学生这次期号中的报名记录
   * 
   * @param std
   * @param config
   * @return
   */
  public List<ExamSignUp> getSignUps(Student std, ExamSignUpConfig config);

  /**
   * 获得这次期号中某门科目开放的期号
   * 
   * @return
   */
  public List<ExamSignUpConfig> getOpenedConfigs(Project project);

  /**
   * 根据期号和科目来找到唯一的科目设
   * 
   * @param configId
   * @param subjectId
   * @return
   */
  public ExamSignUpSetting getSettingByConfigCategory(Long configId, Long subjectId);

  public boolean isFree(Student student, ExamSubject subject);

  public boolean isExist(ExamSignUp signUp);

  public List<ExamSignUpConfig> getOpenedConfigs(Project project, ExamSignUpSetting setting);
}
