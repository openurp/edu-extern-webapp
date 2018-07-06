/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright (c) 2005, The OpenURP Software.
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
package org.openurp.edu.extern.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.base.model.Semester;
import org.openurp.edu.base.model.Project;
import org.openurp.edu.extern.model.ExamFeeConfig;
import org.openurp.edu.extern.service.ExamFeeConfigService;

public class ExamFeeConfigServiceImpl extends BaseServiceImpl implements ExamFeeConfigService {

  public List<ExamFeeConfig> getConfigs(Project project, Semester... semesters) {
    OqlBuilder<ExamFeeConfig> builder = OqlBuilder.from(ExamFeeConfig.class, "config");
    builder.where("config.project = :project", project);
    builder.where("config.semester in (:semesters)", semesters);
    return entityDao.search(builder);
  }

  public List<ExamFeeConfig> getCurrOpenConfigs() {
    Date date = new Date();
    OqlBuilder<ExamFeeConfig> builder = OqlBuilder.from(ExamFeeConfig.class, "config");
    builder.where("exists(from " + Semester.class.getName() + " semester "
        + "where config.semester=semester "
        + "and semester.beginOn <= :beginOn and semester.endOn >= :beginOn)", date);
    builder.where("config.opened is true");
    builder.where("config.openAt is null or config.openAt <=:openAt", date);
    builder.where("config.closeAt is null or config.closeAt >= :closeAt", date);
    return entityDao.search(builder);
  }

  public List<ExamFeeConfig> getOpenConfigs(Semester... semesters) {
    return entityDao.search(getOpenConfigBuilder(null, semesters));
  }

  public List<ExamFeeConfig> getOpenConfigs(Project project, Semester... semesters) {
    return entityDao.search(getOpenConfigBuilder(project, semesters));
  }

  public OqlBuilder<ExamFeeConfig> getOpenConfigBuilder(Project project, Semester... semesters) {
    Date date = new Date();
    OqlBuilder<ExamFeeConfig> builder = OqlBuilder.from(ExamFeeConfig.class, "config");
    if (null == project) {
      builder.where("config.project = :project", project);
    }
    builder.where("config.semester in (:semesters)", semesters);
    builder.where("config.opened is true");
    builder.where("config.openAt is null or config.openAt <=:date", date);
    builder.where("config.closeAt is null or config.closeAt >=:date", date);
    return builder;
  }

  public ExamFeeConfig getConfig(ExamFeeConfig config) {
    Date now = new Date();
    Date openAt = config.getOpenAt();
    Date closeAt = config.getCloseAt();
    OqlBuilder<ExamFeeConfig> builder = OqlBuilder.from(ExamFeeConfig.class, "config");
    Map<String, Object> params = CollectUtils.newHashMap();
    builder.where("config.project = :project and config.semester = :semester and config.feeType=:feeType");
    if (!(null == openAt && null == closeAt)) {
      if (null == openAt && null != closeAt) {
        if (now.getTime() <= closeAt.getTime()) {
          openAt = now;
        } else {
          try {
            openAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("0000-01-01 00:00:00");
          } catch (ParseException e) {
          }
        }
      }
      if (null == closeAt && null != openAt) {
        if (now.getTime() <= openAt.getTime()) {
          openAt = now;
        } else {
          try {
            openAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2099-01-01 00:00:00");
          } catch (ParseException e) {
          }
        }
      }
      params.put("openAt", openAt);
      params.put("closeAt", closeAt);
      builder.where("config.openAt is null or config.openAt <=:openAt");
      builder.where("config.closeAt is null or config.closeAt >=:openAt");
      builder.where("config.openAt is null or config.openAt <=:closeAt");
      builder.where("config.closeAt is null or config.closeAt >=:closeAt");
    }
    params.put("project", config.getProject());
    params.put("semester", config.getSemester());
    params.put("feeType", config.getFeeType());
    return entityDao.uniqueResult(builder.params(params));
  }

  public boolean doCheck(Project project, Semester... semesters) {
    Date date = new Date();
    OqlBuilder<ExamFeeConfig> builder = OqlBuilder.from(ExamFeeConfig.class, "config");
    builder.where("config.project = :project", project);
    builder.where("config.semester in (:semesters)", semesters);
    builder.where("config.opened is true");
    builder.where("config.openAt is null or config.openAt <=:date", date);
    builder.where("config.closeAt is null or config.closeAt >=:date", date);
    return !entityDao.search(builder).isEmpty();
  }

  public boolean doCheck(ExamFeeConfig config) {
    Date date = new Date();
    return config.isOpened() && config.getOpenAt() == null ? true : config.getOpenAt().before(date)
        && config.getCloseAt() == null ? true : config.getCloseAt().after(date);
  }

  public void saveOrUpdate(ExamFeeConfig config) throws Exception {
    entityDao.saveOrUpdate(config);
  }
}
