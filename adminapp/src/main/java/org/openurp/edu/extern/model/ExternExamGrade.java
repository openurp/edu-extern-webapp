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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.entity.pojo.NumberIdTimeObject;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.openurp.code.edu.model.ExamStatus;
import org.openurp.code.edu.model.GradingMode;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.grade.course.model.CourseGrade;

/**
 * 资格考试成绩 <br>
 *
 * @author chaostone
 */

@Entity(name = "org.openurp.edu.extern.model.ExternExamGrade")
public class ExternExamGrade extends NumberIdTimeObject<Long> {

  private static final long serialVersionUID = -4394645753927819458L;

  /**
   * 学生
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Student std;

  /**
   * 得分
   */
  private Float score;

  /**
   * 得分等级/等分文本内容
   */
  private String scoreText;

  /**
   * 是否合格
   */
  @NotNull
  private boolean passed;

  /** 考试科目 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExamSubject subject;

  /** 准考证号 */
  @Size(max = 50)
  private String examNo;

  /** 证书编号 */
  private String certificate;

  /***/
  private java.sql.Date acquiredOn;

  private int status;
  /**
   * 成绩记录方式
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  protected GradingMode gradingMode;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExamStatus examStatus;

  @ManyToMany
  @Cascade(CascadeType.ALL)
  private List<CourseGrade> grades;

  public Student getStd() {
    return std;
  }

  public void setStd(Student std) {
    this.std = std;
  }

  public Float getScore() {
    return score;
  }

  public void setScore(Float score) {
    this.score = score;
  }

  public String getScoreText() {
    return scoreText;
  }

  public void setScoreText(String scoreText) {
    this.scoreText = scoreText;
  }

  public boolean isPassed() {
    return passed;
  }

  public void setPassed(boolean passed) {
    this.passed = passed;
  }

  public ExamSubject getSubject() {
    return subject;
  }

  public void setSubject(ExamSubject subject) {
    this.subject = subject;
  }

  public String getExamNo() {
    return examNo;
  }

  public void setExamNo(String examNo) {
    this.examNo = examNo;
  }

  public String getCertificate() {
    return certificate;
  }

  public void setCertificate(String certificate) {
    this.certificate = certificate;
  }

  public java.sql.Date getAcquiredOn() {
    return acquiredOn;
  }

  public void setAcquiredOn(java.sql.Date acquiredOn) {
    this.acquiredOn = acquiredOn;
  }

  public GradingMode getGradingMode() {
    return gradingMode;
  }

  public void setGradingMode(GradingMode gradingMode) {
    this.gradingMode = gradingMode;
  }

  public ExamStatus getExamStatus() {
    return examStatus;
  }

  public void setExamStatus(ExamStatus examStatus) {
    this.examStatus = examStatus;
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

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

}
