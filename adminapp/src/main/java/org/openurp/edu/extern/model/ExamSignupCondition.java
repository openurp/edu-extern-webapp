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
import javax.validation.constraints.Size;

import org.beangle.commons.entity.pojo.LongIdObject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openurp.code.edu.model.EducationLevel;

/**
 * ExamSignupConfig
 * 报考条件
 *
 * @author chaostone
 */
@Cacheable
@Cache(region = "eams.teach", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity(name = "org.openurp.edu.extern.model.ExamSignupCondition")
public class ExamSignupCondition extends LongIdObject {

  private static final long serialVersionUID = -3350110982208813940L;

  @ManyToOne(fetch = FetchType.LAZY)
  private ExamSignupSetting setting;

  @Size(max = 30)
  private String grades;

  @ManyToOne(fetch = FetchType.LAZY)
  private EducationLevel level;

  private boolean inclusive;

  public ExamSignupSetting getSetting() {
    return setting;
  }

  public void setSetting(ExamSignupSetting setting) {
    this.setting = setting;
  }

  public String getGrades() {
    return grades;
  }

  public void setGrades(String grades) {
    this.grades = grades;
  }

  public EducationLevel getLevel() {
    return level;
  }

  public void setLevel(EducationLevel level) {
    this.level = level;
  }

  public boolean isInclusive() {
    return inclusive;
  }

  public void setInclusive(boolean inclusive) {
    this.inclusive = inclusive;
  }

}
