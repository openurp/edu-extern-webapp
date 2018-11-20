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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.openurp.base.model.NumberIdTimeObject;
import org.openurp.code.geo.model.Division;
import org.openurp.edu.base.code.model.CertificateLevel;
import org.openurp.edu.base.code.model.CertificateType;
import org.openurp.edu.base.code.model.ExternExamTime;

/**
 * @author zhouqi 2017年12月13日
 *
 */
@Entity(name = "org.openurp.edu.extern.identification.model.Certificate")
public class Certificate extends NumberIdTimeObject<Integer> {

  private static final long serialVersionUID = 3980122866996113913L;

  @NotNull
  private String code;

  @NotNull
  private String name;

  /** 证书类型（含级别） */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private CertificateType type;

  /** 证书类型（含级别） */
  @ManyToOne(fetch = FetchType.LAZY)
  private CertificateLevel level;

  /** 报考省份 */
  @ManyToOne(fetch = FetchType.LAZY)
  private Division division;

  /** 报考时间 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExternExamTime examTime;

  /** 生效时间 */
  @NotNull
  private Date beginOn;

  /** 失效时间 */
  private Date endOn;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CertificateType getType() {
    return type;
  }

  public void setType(CertificateType type) {
    this.type = type;
  }

  public CertificateLevel getLevel() {
    return level;
  }

  public void setLevel(CertificateLevel level) {
    this.level = level;
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
