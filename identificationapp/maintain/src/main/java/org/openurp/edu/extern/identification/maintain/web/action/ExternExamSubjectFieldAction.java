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
package org.openurp.edu.extern.identification.maintain.web.action;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.basic.edu.base.code.web.action.ExternBaseAction;
import org.openurp.edu.extern.IdentificationConst;
import org.openurp.edu.extern.identification.model.ExternExamSubjectField;

/**
 * @author zhouqi 2018年5月15日
 *
 */
public class ExternExamSubjectFieldAction extends ExternBaseAction<Integer, ExternExamSubjectField> {

  protected String entityName() {
    return "field";
  }

  @Override
  protected void settingOtherInSearch(OqlBuilder<ExternExamSubjectField> builder) {
    // 只接受规定的内部字段，其它的一律过滤
    builder.where("field.innerField in (:innerFields)", IdentificationConst.innerFields);
  }

  @Override
  protected void otherSomethingInSearch() {
    put("fixedRequestFieldMap", IdentificationConst.fixedRequestFieldMap);
    put("fixedResponseFieldMap", IdentificationConst.fixedResponseFieldMap);
  }

  @Override
  protected void extraEdit(Integer id) {
    put("innerFieldMap", IdentificationConst.innerFieldMap);
    put("fixedRequestFields", IdentificationConst.fixedRequestFields);
    put("fixedResponseFields", IdentificationConst.fixedResponseFields);
  }

  public String checkAjax() {
    OqlBuilder<ExternExamSubjectField> builder = OqlBuilder.from(ExternExamSubjectField.class, "field");
    Integer id = getInt("id");
    if (null != id) {
      builder.where("field.id != :fieldId", id);
    }
    builder.where("field.outerField = :outerField", StringUtils.trim(get("outerField")).replaceAll("\\s", StringUtils.EMPTY));
    builder.where("field.innerField = :innerField", get("innerField"));
    builder.where("field.name = :name", StringUtils.trim(get("name")));
    put("isOk", CollectionUtils.isEmpty(entityDao.search(builder)));
    return forward();
  }

  @Override
  protected boolean beforeSave(ExternExamSubjectField field) {
    field.setOuterField(field.getOuterField().replaceAll("\\s", StringUtils.EMPTY)); // 字段变量不能有空白字符
    return super.beforeSave(field);
  }
}
