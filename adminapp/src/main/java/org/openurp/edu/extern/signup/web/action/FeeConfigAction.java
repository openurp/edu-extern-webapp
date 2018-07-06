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
package org.openurp.edu.extern.signup.web.action;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.beangle.commons.entity.Entity;
import org.openurp.edu.extern.model.ExamFeeConfig;
import org.openurp.edu.extern.service.ExamFeeConfigService;
import org.openurp.edu.web.action.SemesterSupportAction;
import org.openurp.fee.code.model.FeeType;

public class FeeConfigAction extends SemesterSupportAction {

  protected ExamFeeConfigService examFeeConfigService;

  @Override
  protected String getEntityName() {
    return ExamFeeConfig.class.getName();
  }

  @Override
  protected void indexSetting() {
    put("configs", examFeeConfigService.getConfigs(getProject(), getSemester()));
  }

  @Override
  protected void editSetting(Entity<?> entity) {
    put("project", getProject());
    put("semester", getSemester());
    put("feeTypes", codeService.getCodes(FeeType.class));
    put("timeUnits", TimeUnit.values());
  }

  public String calPayDuration() {
    String fromUnit = get("fromUnit");
    String toUnit = get("toUnit");
    Long duration = getLong("duration");
    if (!fromUnit.equals(toUnit)) {
      duration = calPayDuration(get("fromUnit"), get("toUnit"), duration);
    }
    put("duration", duration);
    return forward();
  }

  private Long calPayDuration(String fromUnit, String toUnit, Long duration) {
    if (null == duration) { return duration; }
    TimeUnit fromTimeUnit = TimeUnit.valueOf(fromUnit);
    TimeUnit toTimeUnit = TimeUnit.valueOf(toUnit);
    switch (toTimeUnit) {
    case DAYS:
      return fromTimeUnit.toDays(duration);
    case HOURS:
      return fromTimeUnit.toHours(duration);
    case MINUTES:
      return fromTimeUnit.toMinutes(duration);
    case SECONDS:
      return fromTimeUnit.toSeconds(duration);
    case MICROSECONDS:
      return fromTimeUnit.toMicros(duration);
    case NANOSECONDS:
      return fromTimeUnit.toNanos(duration);
    default:
      return fromTimeUnit.toMillis(duration);
    }
  }

  /**
   * 保存对象
   *
   * @param entity
   * @return
   */
  protected String saveAndForward(Entity<?> entity) {
    ExamFeeConfig config = (ExamFeeConfig) entity;
    if (config.isTransient()) {
      ExamFeeConfig persistedConfig = examFeeConfigService.getConfig(config);
      if (null != persistedConfig) { return redirect("index", "设置的开放时间段已存在"
          + persistedConfig.getFeeType().getName() + "的缴费设置"); }
    }
    Date date = new Date();
    config.setUpdatedAt(date);
    Long payDuration = calPayDuration(get("timeUnit"), TimeUnit.MILLISECONDS.toString(), getLong("duration"));
    if (null != payDuration && payDuration > 0) {
      config.setPayDuration(payDuration);
    }
    try {
      examFeeConfigService.saveOrUpdate(config);
      return redirect("index", "info.save.success");
    } catch (Exception e) {
      logger.info("info.save.failure", e);
      return redirect("index", "info.save.failure");
    }
  }

  @Override
  protected String removeAndForward(Collection<?> entities) {
    try {
      remove(entities);
    } catch (Exception e) {
      logger.info("removeAndForwad failure", e);
      return redirect("index", "info.delete.failure");
    }
    return redirect("index", "info.remove.success");
  }

  public void setExamFeeConfigService(ExamFeeConfigService examFeeConfigService) {
    this.examFeeConfigService = examFeeConfigService;
  }

}
