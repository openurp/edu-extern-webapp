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

import java.util.Collection;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.entity.pojo.LongIdObject;
import org.beangle.commons.lang.time.HourMinute;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.code.model.ExamSubject;

/**
 * 资格考试报名科目设置
 *
 * @author chaostone
 */
@Cacheable
@Cache(region = "eams.teach", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity(name = "org.openurp.edu.extern.model.ExamSignupSetting")
public class ExamSignupSetting extends LongIdObject {

  private static final long serialVersionUID = -1722126073788441946L;

  /** 报名科目 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExamSubject subject;

  /** 报名设置(期号) */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExamSignupConfig config;

  /** 要求报名费 */
  @NotNull
  private Double feeOfSignup;

  /** 要求材料费 */
  private Double feeOfMaterial;

  /** 要求考纲费 */
  private Double feeOfOutline;

  /** 最大学生数(0或者null表示不限制) */
  private Integer maxStd;

  /** 白名单 */
  @OneToMany
  @JoinTable(name = "exam_signup_settings_whites")
  private Set<Student> permitStds = CollectUtils.newHashSet();

  /** 黑名单 */
  @OneToMany
  @JoinTable(name = "exam_signup_settings_blacks")
  private Set<Student> forbiddenStds = CollectUtils.newHashSet();

  /** 报名时要求通过的科目 */
  @ManyToOne(fetch = FetchType.LAZY)
  private ExamSubject superSubject;

  /** 通过后是否可以重考 */
  @NotNull
  private boolean reExamAllowed = false;

  private java.sql.Date examOn;

  @NotNull
  @Type(type = "org.beangle.commons.lang.time.hibernate.HourMinuteType")
  private HourMinute examBeginAt = HourMinute.Zero;

  @NotNull
  @Type(type = "org.beangle.commons.lang.time.hibernate.HourMinuteType")
  private HourMinute examEndAt = HourMinute.Zero;

  /** 报名条件 */
  @OneToMany(cascade = { CascadeType.ALL }, mappedBy = "setting", orphanRemoval = true)
  private Set<ExamSignupCondition> conditions = CollectUtils.newHashSet();

  public java.sql.Date getExamOn() {
    return examOn;
  }

  public void setExamOn(java.sql.Date examOn) {
    this.examOn = examOn;
  }

  public HourMinute getExamBeginAt() {
    return examBeginAt;
  }

  public void setExamBeginAt(HourMinute examBeginAt) {
    this.examBeginAt = examBeginAt;
  }

  public HourMinute getExamEndAt() {
    return examEndAt;
  }

  public void setExamEndAt(HourMinute examEndAt) {
    this.examEndAt = examEndAt;
  }

  public ExamSubject getSuperSubject() {
    return superSubject;
  }

  public void setSuperSubject(ExamSubject superSubject) {
    this.superSubject = superSubject;
  }

  public ExamSignupSetting() {
    super();
  }

  public ExamSignupSetting(ExamSubject subject, java.sql.Date examOn, HourMinute beginAt, HourMinute endAt) {
    setSubject(subject);
    setReExamAllowed(false);
    // 不要预先设定考纲和材料
    // setFeeOfMaterial(new Double(0));
    // setFeeOfOutline(new Double(0));
    setFeeOfSignup(new Double(0));
    setMaxStd(new Integer(0));
    setExamOn(examOn);
    setExamBeginAt(beginAt);
    setExamEndAt(endAt);
  }

  public boolean isTimeCollision(ExamSignupSetting setting) {
    if (null == getExamOn()) return false;
    else return (setting.getExamOn().equals(getExamOn()) && setting.getExamBeginAt().lt(getExamEndAt()) && getExamBeginAt()
        .lt(setting.getExamEndAt()));
  }

  public ExamSubject getSubject() {
    return subject;
  }

  public void setSubject(ExamSubject subject) {
    this.subject = subject;
  }

  public Double getFeeOfSignup() {
    return feeOfSignup;
  }

  public void setFeeOfSignup(Double feeOfSignup) {
    this.feeOfSignup = feeOfSignup;
  }

  public Double getFeeOfMaterial() {
    return feeOfMaterial;
  }

  public void setFeeOfMaterial(Double feeOfMaterial) {
    this.feeOfMaterial = feeOfMaterial;
  }

  public Double getFeeOfOutline() {
    return feeOfOutline;
  }

  public void setFeeOfOutline(Double feeOfOutline) {
    this.feeOfOutline = feeOfOutline;
  }

  public Integer getMaxStd() {
    return maxStd;
  }

  public void setMaxStd(Integer maxStd) {
    this.maxStd = maxStd;
  }

  public ExamSignupConfig getConfig() {
    return config;
  }

  public void setConfig(ExamSignupConfig config) {
    this.config = config;
  }

  public boolean isReExamAllowed() {
    return reExamAllowed;
  }

  public void setReExamAllowed(boolean reExamAllowed) {
    this.reExamAllowed = reExamAllowed;
  }

  public Set<Student> getPermitStds() {
    return permitStds;
  }

  public void setPermitStds(Set<Student> permitStds) {
    this.permitStds = permitStds;
  }

  public Set<Student> getForbiddenStds() {
    return forbiddenStds;
  }

  public void setForbiddenStds(Set<Student> forbiddenStds) {
    this.forbiddenStds = forbiddenStds;
  }

  public boolean addPermitStd(Student student) {
    return this.permitStds.add(student);
  }

  public boolean addPermitStds(Collection<Student> students) {
    return this.permitStds.addAll(students);
  }

  public boolean removePermitStd(Student student) {
    return this.permitStds.remove(student);
  }

  public boolean removePermitStds(Collection<Student> students) {
    return this.permitStds.removeAll(students);
  }

  public boolean addForbiddenStd(Student student) {
    return this.forbiddenStds.add(student);
  }

  public boolean addForbiddenStds(Collection<Student> students) {
    return this.forbiddenStds.addAll(students);
  }

  public boolean removeForbiddenStd(Student student) {
    return this.forbiddenStds.remove(student);
  }

  public boolean removeForbiddenStds(Collection<Student> students) {
    return this.forbiddenStds.removeAll(students);
  }

  public Set<ExamSignupCondition> getConditions() {
    return conditions;
  }

  public void setConditions(Set<ExamSignupCondition> conditions) {
    this.conditions = conditions;
  }

}
