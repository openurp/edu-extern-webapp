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

package org.openurp.edu.extern.grade.data;

import org.openurp.edu.extern.model.ExternGrade;
import org.openurp.edu.grade.course.model.CourseGrade;

/**
 * @author zhouqi 2019年10月18日
 */
public class ExternGradeData {

  private ExternGrade info;

  private CourseGrade courseGrade;

  private ExternGradeData() {
    super();
  }

  public ExternGradeData(ExternGrade info, CourseGrade courseGrade) {
    this();
    this.info = info;
    this.courseGrade = courseGrade;
  }

  public ExternGrade getInfo() {
    return info;
  }

  public void setInfo(ExternGrade info) {
    this.info = info;
  }

  public CourseGrade getCourseGrade() {
    return courseGrade;
  }

  public void setCourseGrade(CourseGrade courseGrade) {
    this.courseGrade = courseGrade;
  }
}
