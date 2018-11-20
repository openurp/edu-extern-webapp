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
package org.openurp.edu.extern.identification.code.service;

import org.beangle.commons.dao.EntityDao;
import org.beangle.commons.entity.Entity;
import org.beangle.commons.transfer.TransferResult;
import org.openurp.edu.common.service.IdentificationAppImporterListener;

/**
 * @author zhouqi 2017年12月14日
 *
 */
public abstract class IdentificationAppIdentificateImporterListener<T extends Entity<Integer>> extends IdentificationAppImporterListener<Integer, T> {

  public IdentificationAppIdentificateImporterListener(EntityDao entityDao) {
    super(entityDao);
  }

  protected void itemStart() {
    itemStartExtra();

    if (!hasError()) {
      try {
        T entity = loadExistedEntity();
        settingPropertyExtraInEntity(entity);
        importer.setCurrent(entity);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  protected abstract T loadExistedEntity();

  protected void itemStartExtra() {
    ;
  }

  protected void settingPropertyExtraInEntity(T entity) throws Exception {
    ;
  }

  protected void itemBeforeSave(TransferResult tr) {
    ;
  }
}
