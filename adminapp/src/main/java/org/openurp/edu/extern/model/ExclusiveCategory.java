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
/*
 * OpenURP, Agile Development Scaffold and Toolkit
 *
 * Copyright (c) 2005-2015, OpenURP Software.
 *
 * OpenURP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenURP is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenURP.  If not, see <http://www.gnu.org/licenses/>.
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
import org.openurp.edu.extern.code.model.ExamCategory;

/**
 * 资格考试报名冲突科目表
 *
 * @author chaostone
 */
@Cacheable
@Cache(region = "eams.teach", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity(name = "org.openurp.edu.extern.model.ExclusiveCategory")
public class ExclusiveCategory extends LongIdObject {

  private static final long serialVersionUID = -8997363952002632495L;

  /** 报名设置(期号) */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExamSignupConfig config;

  /** 冲突的科目一方 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExamCategory categoryOne;

  /** 冲突的科目另一方 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExamCategory categoryTwo;

  public ExamSignupConfig getConfig() {
    return config;
  }

  public void setConfig(ExamSignupConfig config){
    this.config = config;
  }

  /** 冲突的科目一方 */
  public ExamCategory getCategoryOne(){
    return categoryOne;
  }

  public void setCategoryOne(ExamCategory categoryOne){
    this.categoryOne = categoryOne;
  }

  /** 冲突的科目另一方 */
  public ExamCategory getCategoryTwo(){
    return categoryTwo;
  }

  public void setCategoryTwo(ExamCategory categoryTwo){
    this.categoryTwo = categoryTwo;
  }

}
