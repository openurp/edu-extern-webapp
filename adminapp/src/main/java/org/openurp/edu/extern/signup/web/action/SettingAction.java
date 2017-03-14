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
package org.openurp.edu.extern.signup.web.action;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.collection.Order;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Strings;
import org.beangle.commons.lang.time.HourMinute;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExamSignupConfig;
import org.openurp.edu.extern.model.ExamSignupSetting;
import org.openurp.edu.web.action.RestrictionSupportAction;

/**
 * 考试科目设置
 * 
 * @author chaostone
 */
public class SettingAction extends RestrictionSupportAction {

  @Override
  protected String getEntityName() {
    return ExamSignupSetting.class.getName();
  }

  /**
   * 新增和修改
   */

  public String edit() {
    ExamSignupSetting setting = populateEntity(ExamSignupSetting.class, "examSignupSetting");
    ExamSignupConfig config = getSignupConfig();
    Integer categoryId = config.getCategory().getId();
    // 查询报考科目
    OqlBuilder<ExamSubject> query = OqlBuilder.from(ExamSubject.class, "subject");
    query.where("subject.category.id = :categoryId", categoryId);
    query
        .where(
            "not exists (from "+ExamSignupSetting.class.getName()+" setting where setting.subject.id =subject.id and setting.config.id =:configId)",
            config.getId());

    Set<ExamSubject> set = CollectUtils.newHashSet();

    List<ExamSubject> subjects = entityDao.search(query);
    set.addAll(subjects);
    if (setting.isPersisted()) {
      set.add(setting.getSubject());
    }
    put("subjects", set);
    // 查询必须通过的科目
    OqlBuilder<ExamSubject> query2 = OqlBuilder.from(ExamSubject.class, "subject");
    query2.where("subject.category.id=:categoryId", categoryId);
    put("superSubjects", entityDao.search(query2));
    put("examSignupSetting", setting);
    if (setting.isPersisted()) {
      Iterator<Student> permitIt = setting.getPermitStds().iterator();
      String permitSeq = "";
      for (; permitIt.hasNext();) {
        Student student = permitIt.next();
        permitSeq += student.getCode();
        if (permitIt.hasNext()) {
          permitSeq += "\n";
        }
      }
      Iterator<Student> forbiddenIt = setting.getForbiddenStds().iterator();
      String forbiddenSeq = "";
      for (; forbiddenIt.hasNext();) {
        Student student = forbiddenIt.next();
        forbiddenSeq += student.getCode();
        if (forbiddenIt.hasNext()) {
          forbiddenSeq += "\n";
        }
      }
      put("permitSeq", permitSeq);
      put("forbiddenSeq", forbiddenSeq);
    }
    return forward();
  }

  /**
   * 删除
   */
  public String remove() {
    Long[] examSignupSettingIds = Strings.splitToLong(get("examSignupSetting.id"));
    List<ExamSignupSetting> examSignupSettingList = entityDao.get(ExamSignupSetting.class,
        examSignupSettingIds);
    return removeAndForward(examSignupSettingList);
  }

  protected String removeAndForward(Collection<?> entities) {
    try {
      remove(entities);
    } catch (Exception e) {
      logger.info("removeAndForwad failure", e);
      return redirect("search", "info.delete.failure", "&examSignupSetting.config.id="
          + getSignupConfig().getId());
    }
    return redirect("search", "info.remove.success", "&examSignupSetting.config.id="
        + getSignupConfig().getId());
  }

  public String save() {
    ExamSignupSetting setting = populateEntity(ExamSignupSetting.class, "examSignupSetting");
    String beginAt1 = get("examSignupSetting.beginAt");
    String endAt1 = get("examSignupSetting.endAt");
    String permitStdCodes = get("permitStds");
    String forbiddenStdCodes = get("forbiddenStds");
    List<Student> permitStds = CollectUtils.newArrayList();
    List<Student> forbiddenStds = CollectUtils.newArrayList();
    setting.getPermitStds().clear();
    setting.getForbiddenStds().clear();
    if (Strings.isNotEmpty(permitStdCodes)) {
      String[] permitStdCodeSeq = Strings.split(permitStdCodes);
      permitStds = entityDao.get(Student.class, "code", permitStdCodeSeq);
      if (!permitStds.isEmpty()) {
        setting.addPermitStds(permitStds);
      }
    }
    if (Strings.isNotEmpty(forbiddenStdCodes)) {
      String[] forbiddenStdCodeSeq = Strings.split(forbiddenStdCodes);
      forbiddenStds = entityDao.get(Student.class, "code", forbiddenStdCodeSeq);
      if (!forbiddenStds.isEmpty()) {
        setting.addForbiddenStds(forbiddenStds);
      }
    }
    String beginAt = get("examBeginAt");
    String endAt = get("examEndAt");
    setting.setExamBeginAt(new HourMinute(beginAt));
    setting.setExamEndAt(new HourMinute(endAt));
    
    put("examSignupSetting.config.id", setting.getConfig().getId());
    try {
      saveOrUpdate(setting);
      return redirect("search", "info.save.success", "&examSignupSetting.config.id="
          + setting.getConfig().getId());
    } catch (Exception e) {
      logger.info("saveAndForwad failure", e);
      return redirect("search", "info.save.failure", "&examSignupSetting.config.id="
          + setting.getConfig().getId());
    }
  }

  public String search() {
    getSignupConfig();
    OqlBuilder<ExamSignupSetting> query = OqlBuilder.from(ExamSignupSetting.class,
        "examSignupSetting");
    populateConditions(query);
    query.limit(getPageLimit());
    query.orderBy(Order.parse(get("orderBy")));
    put("examSignupSettings", entityDao.search(query));
    return forward();
  }

  public String batchEdit() {
    put("examSignupSettings",
        entityDao.get(ExamSignupSetting.class, Strings.splitToLong(get("examSignupSettingIds"))));
    getSignupConfig();
    return forward();
  }

  ExamSignupConfig getSignupConfig() {
    Long configId = getLong("examSignupSetting.config.id");
    if (configId != null) {
      ExamSignupConfig config = entityDao.get(ExamSignupConfig.class, configId);
      if (config != null) {
        put("config", config);
        return config;
      }
    }
    return null;
  }

  public String batchSave() {
    Integer settingSize = getInt("settingSize");
    List<ExamSignupSetting> settings = CollectUtils.newArrayList();
    for (int i = 0; i < settingSize; i++) {
      settings.add(populateEntity(ExamSignupSetting.class, "examSignupSetting" + i));
    }
    entityDao.saveOrUpdate(settings);

    return redirect("search", "info.save.success", "&examSignupSetting.config.id="
        + getSignupConfig().getId());
  }
}
