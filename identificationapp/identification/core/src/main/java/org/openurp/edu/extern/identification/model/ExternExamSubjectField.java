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

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.beangle.commons.entity.pojo.NumberIdObject;

/**
 * @author zhouqi 2018年5月15日
 *
 */
@Entity(name = "org.openurp.edu.extern.identification.model.ExternExamSubjectField")
@Table(name = "xb_exam_subject_fields")
public class ExternExamSubjectField extends NumberIdObject<Integer> {

  private static final long serialVersionUID = -8263985250644283665L;

  @NotNull
  @Size(max = 100)
  private String outerField;

  @NotNull
  @Size(max = 100)
  private String innerField;

  @NotNull
  @Size(max = 100)
  private String name;

  public String getOuterField() {
    return outerField;
  }

  public void setOuterField(String outerField) {
    this.outerField = outerField;
  }

  public String getInnerField() {
    return innerField;
  }

  public void setInnerField(String innerField) {
    this.innerField = innerField;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public final String getLabel() {
    return name + "(" + outerField + ")";
  }
}
