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
package org.openurp.edu.extern.base.code.web.action;

import org.apache.commons.collections.CollectionUtils;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.Entity;
import org.openurp.edu.web.action.RestrictionSupportAction;

/**
 * @author zhouqi 2018年11月21日
 */
public abstract class ExternBaseCodeAction extends RestrictionSupportAction {

  public String checkAjax() {
    Object id = null;
    Entity<?> entity = getEntity();
    if (entity.getId() instanceof Integer) {
      id = getInt("id");
    } else {
      id = getLong("id");
    }
    OqlBuilder<?> builder = OqlBuilder.from(entity.getClass(), getShortName());
    builder.where(getShortName() + ".code = :code", get("code"));
    if (null != id) {
      builder.where(getShortName() + ".id != :id", id);
    }
    put("isOk", CollectionUtils.isEmpty(entityDao.search(builder)));
    return forward();
  }
}
