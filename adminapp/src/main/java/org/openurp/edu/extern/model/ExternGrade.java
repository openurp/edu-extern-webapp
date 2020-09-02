/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright © 2014, The OpenURP Software.
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
package org.openurp.edu.extern.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.entity.pojo.NumberIdTimeObject;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.NaturalId;
import org.openurp.code.edu.model.EduCategory;
import org.openurp.code.edu.model.EducationLevel;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.grade.course.model.CourseGrade;

/**
 * 校外成绩
 */
@Entity(name = "org.openurp.edu.extern.model.ExternGrade")
public class ExternGrade extends NumberIdTimeObject<Long> {

  private static final long serialVersionUID = 7652949825553454930L;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExternSchool school;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private EducationLevel level;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private EduCategory category;

  private String majorName;

  @NaturalId(mutable = true)
  @NotNull
  private String courseName;

  @NotNull
  private Float credits;

  @NaturalId(mutable = true)
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Student std;

  @NaturalId(mutable = true)
  @NotNull
  private Date acquiredOn;

  @NotNull
  private String scoreText;

  @ManyToMany
  @Cascade(CascadeType.ALL)
  private List<CourseGrade> grades;

  private String remark;

  public ExternSchool getSchool() {
    return school;
  }

  public void setSchool(ExternSchool school) {
    this.school = school;
  }

  public EducationLevel getLevel() {
    return level;
  }

  public void setLevel(EducationLevel level) {
    this.level = level;
  }

  public EduCategory getCategory() {
    return category;
  }

  public void setCategory(EduCategory category) {
    this.category = category;
  }

  public String getMajorName() {
    return majorName;
  }

  public void setMajorName(String majorName) {
    this.majorName = majorName;
  }

  public String getCourseName() {
    return courseName;
  }

  public void setCourseName(String courseName) {
    this.courseName = courseName;
  }

  public Float getCredits() {
    return credits;
  }

  public void setCredits(Float credits) {
    this.credits = credits;
  }

  public Student getStd() {
    return std;
  }

  public void setStd(Student std) {
    this.std = std;
  }

  public Date getAcquiredOn() {
    return acquiredOn;
  }

  public void setAcquiredOn(Date acquiredOn) {
    this.acquiredOn = acquiredOn;
  }

  public String getScoreText() {
    return scoreText;
  }

  public void setScoreText(String scoreText) {
    this.scoreText = scoreText;
  }

  public List<CourseGrade> getGrades() {
    return grades;
  }

  public void setGrades(List<CourseGrade> grades) {
    this.grades = grades;
  }

  public void addCourseGrade(CourseGrade courseGrade) {
    if (null == grades) {
      grades = CollectUtils.newArrayList();
    }
    grades.add(courseGrade);
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

}
