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
/**
 *
 */
package org.openurp.basic.edu.base.code.web.action;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.Entity;
import org.beangle.commons.transfer.TransferResult;
import org.beangle.commons.transfer.importer.EntityImporter;
import org.openurp.edu.eams.web.util.DownloadHelper;
import org.openurp.edu.web.action.BaseAction;

/**
 * @author zhouqi 2017年12月12日
 *
 */
public abstract class ExternBaseAction<ID extends Serializable, E extends Entity<ID>> extends BaseAction {

  protected final Class<ID> idType = idType();

  protected final Class<E> entityType = entityType();

  protected final String entityName = entityName();

  protected final String entityNames = entityNames();

  private final Class<ID> idType() {
    Type e = getClass().getGenericSuperclass();
    Type[] es = ((ParameterizedType) e).getActualTypeArguments();
    return (Class<ID>) es[0];
  }

  private final Class<E> entityType() {
    Type e = getClass().getGenericSuperclass();
    Type[] es = ((ParameterizedType) e).getActualTypeArguments();
    return (Class<E>) es[1];
  }

  protected String entityName() {
    return StringUtils.uncapitalize(entityType.getSimpleName());
  }

  protected String entityNames() {
    switch (StringUtils.right(entityName, 1).charAt(0)) {
      case 's':
        return entityName + "es";
      case 'x':
      case 'o':
        return entityName + "es";
      default:
        return entityName + "s";
    }
  }

  protected String getEntityName() {
    return entityType.getName();
  }

  public String downloadTemplate() {
    DownloadHelper.download(getRequest(), getResponse(),
        getClass().getClassLoader().getResource(get("file")), get("display"));
    return null;
  }

  public String search() {
    OqlBuilder<E> builder = OqlBuilder.from(entityType, entityName);
    populateConditions(builder);
    settingSearch(builder);
    put(entityNames, entityDao.search(builder));
    otherSomethingInSearch();
    return forward();
  }

  private void settingSearch(OqlBuilder<E> builder) {
    settingLimitInSearch(builder);
    settingOrderByInSearch(builder);
    settingOtherInSearch(builder);
  }

  protected void settingLimitInSearch(OqlBuilder<E> builder) {
    builder.limit(getPageLimit());
  }

  protected void settingOrderByInSearch(OqlBuilder<E> builder) {
    String orderBy = get("orderBy");
    if (StringUtils.isBlank(orderBy)) {
      orderBy = entityName + ".id";
    } else {
      orderBy += "," + entityName + ".id";
    }
    builder.orderBy(orderBy);
  }

  protected void settingOtherInSearch(OqlBuilder<E> builder) {
    ;
  }

  protected void otherSomethingInSearch() {
    ;
  }

  public String edit() {
    ID id = getId(entityName, idType);
    if (null != id) {
      put(entityName, entityDao.get(entityType, id));
    }
    extraEdit(id);
    return forward();
  }

  protected void extraEdit(ID id) {
    ;
  }

  public String checkAjax() {
    ID id = get("id", idType);
    String code = get("code");

    OqlBuilder<E> builder = OqlBuilder.from(entityType, entityName);
    if (null != id) {
      builder.where(entityName + ".id != :id", id);
    }
    builder.where(entityName + ".code = :code", StringUtils.trim(code));
    put("isOk", entityDao.search(builder).isEmpty());

    return forward();
  }

  public String save() {
    E entity = populateEntity(entityType, entityName);
    if (beforeSave(entity))  {
      entityDao.saveOrUpdate(entity);
      return redirect("search", "info.save.success");
    } else {
      return forwardError("info.save.failure");
    }
  }

  protected boolean beforeSave(E entity) {
    try {
      BeanUtils.setProperty(entity, "updatedAt", new Date());
      return true;
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
  }

  public String remove() {
    return removeAndForward(entityDao.get(entityType, getIds(entityName, idType)));
  }

  /**
   * 导入信息
   */
  public String importData() {
    TransferResult tr = new TransferResult();
    EntityImporter importer = buildEntityImporter();
    if (null == importer) { return forward("/components/importData/error"); }
    try {
      configImporter(importer);
      importer.transfer(tr);
      put("importer", importer);
      put("importResult", tr);
      if (tr.hasErrors()) {
        return forward("/components/importData/error");
      } else {
        return forward("/components/importData/result");
      }
    } catch (RuntimeException e) {
      put("importResult", tr);
      return forward("/components/importData/error");
    }
  }
}
