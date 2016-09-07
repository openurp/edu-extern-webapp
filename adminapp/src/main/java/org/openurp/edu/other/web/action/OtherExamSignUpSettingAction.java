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
package org.openurp.edu.other.web.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.collection.Order;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Strings;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.other.code.model.OtherExamSubject;
import org.openurp.edu.other.model.OtherExamSignUpConfig;
import org.openurp.edu.other.model.OtherExamSignUpSetting;
import org.openurp.edu.web.action.RestrictionSupportAction;

/**
 * 考试科目设置
 * 
 * @author chaostone
 */
public class OtherExamSignUpSettingAction extends RestrictionSupportAction {

  @Override
  protected String getEntityName() {
    return OtherExamSignUpSetting.class.getName();
  }

  /**
   * 新增和修改
   */

  public String edit() {
    OtherExamSignUpSetting setting = populateEntity(OtherExamSignUpSetting.class, "otherExamSignUpSetting");
    OtherExamSignUpConfig config = getSignupConfig();
    Integer categoryId = config.getCategory().getId();
    // 查询报考科目
    OqlBuilder<OtherExamSubject> query = OqlBuilder.from(OtherExamSubject.class, "subject");
    query.where("subject.category.id = :categoryId", categoryId);
    query
        .where(
            "not exists (from org.openurp.edu.other.OtherExamSignUpSetting setting where setting.subject.id =subject.id and setting.config.id =:configId)",
            config.getId());

    Set<OtherExamSubject> set = CollectUtils.newHashSet();

    List<OtherExamSubject> subjects = entityDao.search(query);
    set.addAll(subjects);
    if (setting.isPersisted()) {
      set.add(setting.getSubject());
    }
    put("subjects", set);
    // 查询必须通过的科目
    OqlBuilder<OtherExamSubject> query2 = OqlBuilder.from(OtherExamSubject.class, "subject");
    query2.where("subject.category.id=:categoryId", categoryId);
    put("superSubjects", entityDao.search(query2));
    put("otherExamSignUpSetting", setting);
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
    Long[] otherExamSignUpSettingIds = Strings.splitToLong(get("otherExamSignUpSetting.id"));
    List<OtherExamSignUpSetting> otherExamSignUpSettingList = entityDao.get(OtherExamSignUpSetting.class,
        otherExamSignUpSettingIds);
    return removeAndForward(otherExamSignUpSettingList);
  }

  protected String removeAndForward(Collection<?> entities) {
    try {
      remove(entities);
    } catch (Exception e) {
      logger.info("removeAndForwad failure", e);
      return redirect("search", "info.delete.failure", "&otherExamSignUpSetting.config.id="
          + getSignupConfig().getId());
    }
    return redirect("search", "info.remove.success", "&otherExamSignUpSetting.config.id="
        + getSignupConfig().getId());
  }

  public String save() {
    OtherExamSignUpSetting setting = populateEntity(OtherExamSignUpSetting.class, "otherExamSignUpSetting");
    String beginAt1 = get("otherExamSignUpSetting.beginAt");
    String endAt1 = get("otherExamSignUpSetting.endAt");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date beginAt = null;
    Date endAt = null;
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
    try {
      beginAt = sdf.parse(beginAt1);
      endAt = sdf.parse(endAt1);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    setting.setBeginAt(beginAt);
    setting.setEndAt(endAt);
    put("otherExamSignUpSetting.config.id", setting.getConfig().getId());
    try {
      saveOrUpdate(setting);
      return redirect("search", "info.save.success", "&otherExamSignUpSetting.config.id="
          + setting.getConfig().getId());
    } catch (Exception e) {
      logger.info("saveAndForwad failure", e);
      return redirect("search", "info.save.failure", "&otherExamSignUpSetting.config.id="
          + setting.getConfig().getId());
    }
  }

  public String search() {
    getSignupConfig();
    OqlBuilder<OtherExamSignUpSetting> query = OqlBuilder.from(OtherExamSignUpSetting.class,
        "otherExamSignUpSetting");
    populateConditions(query);
    query.limit(getPageLimit());
    query.orderBy(Order.parse(get("orderBy")));
    put("otherExamSignUpSettings", entityDao.search(query));
    return forward();
  }

  public String batchEdit() {
    put("otherExamSignUpSettings",
        entityDao.get(OtherExamSignUpSetting.class, Strings.splitToLong(get("otherExamSignUpSettingIds"))));
    getSignupConfig();
    return forward();
  }

  OtherExamSignUpConfig getSignupConfig() {
    Long configId = getLong("otherExamSignUpSetting.config.id");
    if (configId != null) {
      OtherExamSignUpConfig config = entityDao.get(OtherExamSignUpConfig.class, configId);
      if (config != null) {
        put("config", config);
        return config;
      }
    }
    return null;
  }

  public String batchSave() {
    Integer settingSize = getInt("settingSize");
    List<OtherExamSignUpSetting> settings = CollectUtils.newArrayList();
    for (int i = 0; i < settingSize; i++) {
      settings.add(populateEntity(OtherExamSignUpSetting.class, "otherExamSignUpSetting" + i));
    }
    entityDao.saveOrUpdate(settings);

    return redirect("search", "info.save.success", "&otherExamSignUpSetting.config.id="
        + getSignupConfig().getId());
  }
}
