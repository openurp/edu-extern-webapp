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

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.beangle.commons.entity.pojo.LongIdObject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openurp.edu.extern.code.model.ExamSubject;

/**
 * 资格考试报名冲突科目表
 *
 * @author chaostone
 */
@Cacheable
@Cache(region = "eams.teach", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity(name = "org.openurp.edu.extern.model.ExclusiveSubject")
public class ExclusiveSubject extends LongIdObject {

  private static final long serialVersionUID = -8997363952002632495L;

  /** 报名设置(期号) */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExamSignupConfig config;

  /** 冲突的科目一方 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExamSubject subjectOne;

  /** 冲突的科目另一方 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExamSubject subjectTwo;

  public ExamSubject getSubjectOne() {
    return subjectOne;
  }

  public void setSubjectOne(ExamSubject subjectOne) {
    this.subjectOne = subjectOne;
  }

  public ExamSubject getSubjectTwo() {
    return subjectTwo;
  }

  public void setSubjectTwo(ExamSubject subjectTwo) {
    this.subjectTwo = subjectTwo;
  }

  public ExamSignupConfig getConfig() {
    return config;
  }

  public void setConfig(ExamSignupConfig config) {
    this.config = config;
  }
}
