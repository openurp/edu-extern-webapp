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
package org.openurp.edu.extern.identification.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.entity.pojo.NumberIdObject;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.openurp.edu.base.model.Course;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.model.ExternExamGrade;

/**
 * 学生证书
 *
 * @author zhouqi 2018年3月14日
 */
@Entity(name = "org.openurp.edu.extern.identification.model.Certification")
public class Certification extends NumberIdObject<Integer> {

  private static final long serialVersionUID = 1056722418216651938L;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Student std;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Certificate certificate;

  /** 所获证书编号 */
  private String code;

  /** 所获证书的成绩 */
  @NotNull
  private String score;

  @NotNull
  private String happenBy;

  @NotNull
  private Date happenAt;

  @OneToMany(mappedBy = "certification", fetch = FetchType.LAZY, orphanRemoval = true)
  @Cascade(CascadeType.ALL)
  private List<CertificationCourse> courses;

  /** 入库凭证（即，非null示为申请同意） */
  @ManyToOne(fetch = FetchType.LAZY)
  private ExternExamGrade grade;

  /** 入库人 */
  private String lastBy;

  /** 入库时间 */
  private Date lastAt;

  public Student getStd() {
    return std;
  }

  public void setStd(Student std) {
    this.std = std;
  }

  public Certificate getCertificate() {
    return certificate;
  }

  public void setCertificate(Certificate certificate) {
    this.certificate = certificate;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getScore() {
    return score;
  }

  public void setScore(String score) {
    this.score = score;
  }

  public String getHappenBy() {
    return happenBy;
  }

  public void setHappenBy(String happenBy) {
    this.happenBy = happenBy;
  }

  public Date getHappenAt() {
    return happenAt;
  }

  public void setHappenAt(Date happenAt) {
    this.happenAt = happenAt;
  }

  public List<CertificationCourse> getCourses() {
    return courses;
  }

  public void setCourses(List<CertificationCourse> courses) {
    this.courses = courses;
  }

  public void addCertificationCourse(Course course, Float score, String scoreValue) {
    if (null == courses) {
      courses = CollectUtils.newArrayList();
    }
    courses.add(new CertificationCourse(this, course, score, scoreValue));
  }

  public ExternExamGrade getGrade() {
    return grade;
  }

  public void setGrade(ExternExamGrade grade) {
    this.grade = grade;
  }

  public String getLastBy() {
    return lastBy;
  }

  public void setLastBy(String lastBy) {
    this.lastBy = lastBy;
  }

  public Date getLastAt() {
    return lastAt;
  }

  public void setLastAt(Date lastAt) {
    this.lastAt = lastAt;
  }
}
