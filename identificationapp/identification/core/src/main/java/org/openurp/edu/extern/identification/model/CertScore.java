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

import java.sql.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.beangle.commons.collection.CollectUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.openurp.base.model.NumberIdTimeObject;
import org.openurp.code.geo.model.Division;
import org.openurp.edu.base.code.model.CertificateLevel;
import org.openurp.edu.base.code.model.CertificateType;
import org.openurp.edu.base.code.model.ExternExamSubject;
import org.openurp.edu.base.code.model.ExternExamTime;

/**
 * 证书在我校中认定的成绩配置
 *
 * @author zhouqi 2018年1月20日
 *
 */
@Entity(name = "org.openurp.edu.extern.identification.model.CertScore")
public class CertScore extends NumberIdTimeObject<Integer> {

  private static final long serialVersionUID = -6591797873587692556L;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExternExamSubject examSubject;

  @ManyToOne(fetch = FetchType.LAZY)
  private CertificateType certType;

  @ManyToOne(fetch = FetchType.LAZY)
  private CertificateLevel certLevel;

  @ManyToOne(fetch = FetchType.LAZY)
  private Division division;

  @ManyToOne(fetch = FetchType.LAZY)
  private ExternExamTime examTime;

  @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "certScore")
  @Cascade(CascadeType.ALL)
  private List<CertScoreCourse> courses;

  /** 生效时间 */
  @NotNull
  private Date beginOn;

  /** 失效时间 */
  private Date endOn;

  public ExternExamSubject getExamSubject() {
    return examSubject;
  }

  public void setExamSubject(ExternExamSubject examSubject) {
    this.examSubject = examSubject;
  }

  public CertificateType getCertType() {
    return certType;
  }

  public void setCertType(CertificateType certType) {
    this.certType = certType;
  }

  public CertificateLevel getCertLevel() {
    return certLevel;
  }

  public void setCertLevel(CertificateLevel certLevel) {
    this.certLevel = certLevel;
  }

  public Division getDivision() {
    return division;
  }

  public void setDivision(Division division) {
    this.division = division;
  }

  public ExternExamTime getExamTime() {
    return examTime;
  }

  public void setExamTime(ExternExamTime examTime) {
    this.examTime = examTime;
  }

  public List<CertScoreCourse> getCourses() {
    return courses;
  }

  public void clearCourses() {
    if (null != courses) {
      courses.clear();
    }
  }

  public void addCourse(CertScoreCourse course) {
    if (null == this.courses) {
      this.courses = CollectUtils.newArrayList();
    }
    course.setCertScore(this);
    this.courses.add(course);
  }

  public void addCourses(List<CertScoreCourse> courses) {
    for (CertScoreCourse csCourse : courses) {
      addCourse(csCourse);
    }
  }

  public void setCourses(List<CertScoreCourse> courses) {
    this.courses = courses;
  }

  public Date getBeginOn() {
    return beginOn;
  }

  public void setBeginOn(Date beginOn) {
    this.beginOn = beginOn;
  }

  public Date getEndOn() {
    return endOn;
  }

  public void setEndOn(Date endOn) {
    this.endOn = endOn;
  }
}
