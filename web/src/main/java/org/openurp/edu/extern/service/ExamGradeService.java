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

import java.util.Collection;
import java.util.List;

import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.code.model.ExamCategory;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExamGrade;

public interface ExamGradeService {

  public void saveOrUpdate(ExamGrade otherGrade);

  /**
   * FIXME 目前资格考试的功能还未移植，表没有，今后还需要建表
   * 
   * @param std
   * @param category
   * @return
   */
  public ExamGrade getBestGrade(Student std, ExamCategory category);

  /** 查询通过的考试成绩 */
  public List<ExamGrade> getPassGradesOf(Student std, Collection<ExamSubject> otherExternExamSubjects);

  /**
   * 查询某类成绩类型是否通过
   */
  public boolean isPass(Student std, ExamSubject subject);

  /**
   * 查询学生所有资格考试每学期最佳成绩
   * 
   * @param std
   * @param isBest
   * @return
   */
  public Collection<ExamGrade> getExamGrades(Student std, Boolean isBest);
}