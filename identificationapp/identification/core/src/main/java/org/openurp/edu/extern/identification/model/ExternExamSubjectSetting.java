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
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.entity.pojo.NumberIdTimeObject;
import org.openurp.edu.base.code.model.ExternExamSubject;

/**
 * 证书大类数据来源配置
 *
 * @author zhouqi 2017年12月17日
 */
@Entity(name = "org.openurp.edu.extern.identification.model.ExternExamSubjectSetting")
@Table(name = "xb_exam_subject_settings")
public class ExternExamSubjectSetting extends NumberIdTimeObject<Integer> {

  private static final long serialVersionUID = -1701921462477141937L;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExternExamSubject examSubject;

  @NotNull
  private String url;

  @ManyToMany
  @JoinTable(name = "e_s_settings_req_fields", joinColumns = @JoinColumn(name = "setting_id", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "field_id", referencedColumnName = "ID"))
  private List<ExternExamSubjectField> requestFields;

  @ManyToMany
  @JoinTable(name = "e_s_settings_res_fields", joinColumns = @JoinColumn(name = "setting_id", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "field_id", referencedColumnName = "ID"))
  private List<ExternExamSubjectField> responseFields;

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

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public List<ExternExamSubjectField> getRequestFields() {
    return requestFields;
  }

  public ExternExamSubjectField getRequestField(String innerField) {
    if (StringUtils.isBlank(innerField) && CollectionUtils.isEmpty(requestFields)) {
      return null;
    }

    for (ExternExamSubjectField field : requestFields) {
      if (StringUtils.equals(innerField, field.getInnerField())) {
        return field;
      }
    }

    return null;
  }

  public void setRequestFields(List<ExternExamSubjectField> requestFields) {
    this.requestFields = requestFields;
  }

  public void requestFieldsClear() {
    if (null == requestFields) {
      requestFields = CollectUtils.newArrayList();
    }
    requestFields.clear();
  }

  public void addRequestFields(ExternExamSubjectField... fields) {
    if (null == requestFields) {
      requestFields = CollectUtils.newArrayList();
    }
    requestFields.addAll(Arrays.asList(fields));
  }

  public List<ExternExamSubjectField> getResponseFields() {
    return responseFields;
  }

  public ExternExamSubjectField getResponseField(String innerField) {
    if (StringUtils.isBlank(innerField) && CollectionUtils.isEmpty(responseFields)) {
      return null;
    }

    for (ExternExamSubjectField field : responseFields) {
      if (StringUtils.equals(innerField, field.getInnerField())) {
        return field;
      }
    }

    return null;
  }

  public void setResponseFields(List<ExternExamSubjectField> responseFields) {
    this.responseFields = responseFields;
  }

  public void responseFieldsClear() {
    if (null == responseFields) {
      responseFields = CollectUtils.newArrayList();
    }
    responseFields.clear();
  }

  public void addResponseFields(ExternExamSubjectField... fields) {
    if (null == responseFields) {
      responseFields = CollectUtils.newArrayList();
    }
    responseFields.addAll(Arrays.asList(fields));
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
