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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.beangle.commons.entity.pojo.LongIdObject;
import org.openurp.base.model.Campus;
import org.openurp.edu.base.model.Semester;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.fee.model.Bill;

/**
 * 资格考试报名记录
 *
 * @author chaostone
 */
@Entity(name = "org.openurp.edu.extern.model.ExamSignup")
public class ExamSignup extends LongIdObject {
  private static final long serialVersionUID = 8340967443216568299L;

  /** 学生 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Student std;

  /** 学年学期 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Semester semester;

  /** 报名科目 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExamSubject subject;

  /** 报名费 */
  private Double feeOfSignup;

  /** 考纲费 */
  private Double feeOfOutline;

  /** 材料费 */
  private Double feeOfMaterial;

  /** 报名时间 */
  @NotNull
  private Date signupAt;

  /** 准考证号码 */
  @Size(max = 32)
  private String examNo;

  /** 校区 */
  @ManyToOne(fetch = FetchType.LAZY)
  private Campus campus;

  /** 是否乘坐校车 */
  private boolean takeBus = false;

  /**  */
  @ManyToOne(fetch = FetchType.LAZY)
  private Bill bill;

  public boolean isTakeBus() {
    return takeBus;
  }

  public void setTakeBus(boolean takeBus) {
    this.takeBus = takeBus;
  }

  public Double getFeeOfSignup() {
    return feeOfSignup;
  }

  public Double getFeeOfOutline() {
    return feeOfOutline;
  }

  public Double getFeeOfMaterial() {
    return feeOfMaterial;
  }

  public void setFeeOfSignup(Double feeOfSignup) {
    this.feeOfSignup = feeOfSignup;
  }

  public void setFeeOfOutline(Double feeOfOutline) {
    this.feeOfOutline = feeOfOutline;
  }

  public void setFeeOfMaterial(Double feeOfMaterial) {
    this.feeOfMaterial = feeOfMaterial;
  }

  /**
   * 根据setting中是否需要考纲和材料来获得学生应缴总费
   *
   * @return
   */
  public Double getTotal() {
    double total = 0.0;
    total += (null == getFeeOfSignup()) ? 0 : getFeeOfSignup().doubleValue();
    total += (null == getFeeOfMaterial()) ? 0 : getFeeOfMaterial().doubleValue();
    total += (null == getFeeOfOutline()) ? 0 : getFeeOfOutline().doubleValue();
    return new Double(total);
  }

  public Date getSignupAt() {
    return signupAt;
  }

  public void setSignupAt(Date signupAt) {
    this.signupAt = signupAt;
  }

  public Student getStd() {
    return std;
  }

  public void setStd(Student std) {
    this.std = std;
  }

  public Campus getCampus() {
    return campus;
  }

  public void setCampus(Campus campus) {
    this.campus = campus;
  }

  public String getExamNo() {
    return examNo;
  }

  public void setExamNo(String examNo) {
    this.examNo = examNo;
  }

  public ExamSubject getSubject() {
    return subject;
  }

  public void setSubject(ExamSubject subject) {
    this.subject = subject;
  }

  public Semester getSemester() {
    return semester;
  }

  public void setSemester(Semester semester) {
    this.semester = semester;
  }

  public Bill getBill() {
    return bill;
  }

  public void setBill(Bill bill) {
    this.bill = bill;
  }

  public int getTotalPrice() {
    return (int) ((this.feeOfSignup + ((this.feeOfMaterial == null) ? 0 : this.feeOfMaterial) + ((this.feeOfOutline == null) ? 0
        : this.feeOfOutline)) * 100);
  }
}
