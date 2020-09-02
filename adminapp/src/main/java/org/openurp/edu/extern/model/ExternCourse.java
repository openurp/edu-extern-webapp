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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.openurp.base.model.NumberIdTimeObject;

/**
 * 交流课程库
 *
 *
 * @since 2012-07-25
 **/
@Entity(name = "org.openurp.edu.extern.model.ExternCourse")
public class ExternCourse extends NumberIdTimeObject<Long> {
  private static final long serialVersionUID = 1007435099197814451L;

  /** 名称 **/
  @Size(max = 100)
  @NotNull
  private String name;

  /** 英文名称 **/
  @Size(max = 255)
  private String enName;

  /** 交流学校 **/
  @ManyToOne(fetch = FetchType.LAZY)
  private ExternSchool school;

  /** 学分 **/
  private Float credits;

  /** 备注 **/
  private String remark;

  /** 开始时间 **/
  private java.sql.Date beginOn;

  /** 结束时间 **/
  private java.sql.Date endOn;

  public ExternSchool getSchool() {
    return school;
  }

  public void setSchool(ExternSchool school) {
    this.school = school;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEnName() {
    return enName;
  }

  public void setEnName(String enName) {
    this.enName = enName;
  }

  public Float getCredits() {
    return credits;
  }

  public void setCredits(Float credits) {
    this.credits = credits;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public java.sql.Date getBeginOn() {
    return beginOn;
  }

  public void setBeginOn(java.sql.Date beginOn) {
    this.beginOn = beginOn;
  }

  public java.sql.Date getEndOn() {
    return endOn;
  }

  public void setEndOn(java.sql.Date endOn) {
    this.endOn = endOn;
  }

}
