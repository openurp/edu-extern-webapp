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
import javax.validation.constraints.Size;

import org.beangle.commons.entity.pojo.LongIdObject;

/**
 * 其他成绩修改信息
 *
 * @author chaostone
 */
@Entity(name = "org.openurp.edu.extern.model.ExamGradeAlterInfo")
public class ExamGradeAlterInfo extends LongIdObject {

  private static final long serialVersionUID = -5693797098911475651L;

  /**
   * 被修改的成绩
   */
  @ManyToOne(fetch = FetchType.LAZY)
  private ExternExamGrade grade;

  /**
   * 修改前成绩
   */
  private Float scoreBefore;

  /**
   * 修改后成绩
   */
  private Float scoreAfter;

  /**
   * 修改于
   */
  private Date updatedAt;

  /**
   * 备注
   */
  @Size(max = 255)
  private String remark;

  /**
   * 修改人
   */
  private String modifier;

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public ExternExamGrade getGrade() {
    return grade;
  }

  public void setGrade(ExternExamGrade examGrade) {
    this.grade = examGrade;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public Float getScoreAfter() {
    return scoreAfter;
  }

  public void setScoreAfter(Float scoreAfter) {
    this.scoreAfter = scoreAfter;
  }

  public Float getScoreBefore() {
    return scoreBefore;
  }

  public void setScoreBefore(Float scoreBefore) {
    this.scoreBefore = scoreBefore;
  }

  public String getModifier() {
    return modifier;
  }

  public void setModifier(String modifier) {
    this.modifier = modifier;
  }
}
