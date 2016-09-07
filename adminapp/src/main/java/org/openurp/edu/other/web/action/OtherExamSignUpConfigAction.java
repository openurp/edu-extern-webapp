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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.Entity;
import org.beangle.commons.entity.metadata.Model;
import org.beangle.commons.lang.Strings;
import org.openurp.base.model.Campus;
import org.openurp.edu.other.code.model.OtherExamCategory;
import org.openurp.edu.other.code.model.OtherExamSubject;
import org.openurp.edu.other.model.ExclusiveSubject;
import org.openurp.edu.other.model.OtherExamSignUpConfig;
import org.openurp.edu.other.service.OtherExamSignUpConfigService;
import org.openurp.edu.web.action.RestrictionSupportAction;

public class OtherExamSignUpConfigAction extends RestrictionSupportAction {

  protected OtherExamSignUpConfigService otherExamSignUpConfigService;

  public void setOtherExamSignUpConfigService(OtherExamSignUpConfigService otherExamSignUpConfigService) {
    this.otherExamSignUpConfigService = otherExamSignUpConfigService;
  }

  @Override
  protected String getEntityName() {
    return OtherExamSignUpConfig.class.getName();
  }

  @Override
  public void indexSetting() {
    put("otherExamCategries", codeService.getCodes(OtherExamCategory.class));
  }

  protected void editSetting(Entity<?> entity) {
    put("semesters",  getProject().getCalendars().get(0).getSemesters());
    OtherExamSignUpConfig config = (OtherExamSignUpConfig) entity;
    List<?> campuses = baseInfoService.getBaseInfos(Campus.class, getProject().getSchool());
    campuses.removeAll(config.getCampuses());
    put("campuses", campuses);
    List<OtherExamCategory> otherExamCategories = codeService.getCodes(OtherExamCategory.class);
    put("otherExamCategories", otherExamCategories);
    // 查询互斥组
    Set<OtherExamSubject> firstSubjects = CollectUtils.newHashSet();
    Set<OtherExamSubject> secondSubjects = CollectUtils.newHashSet();
    for (ExclusiveSubject exclusiveSubject : config.getExclusiveSubjects()) {
      firstSubjects.add(exclusiveSubject.getSubjectOne());
      secondSubjects.add(exclusiveSubject.getSubjectTwo());
    }
    put("firstCategories", firstSubjects);
    put("secondCategories", secondSubjects);
    if (config.getCategory() != null) {
      OqlBuilder<OtherExamSubject> query = OqlBuilder.from(OtherExamSubject.class, "subject").where(
          "subject.category=:category", config.getCategory());
      if (CollectUtils.isNotEmpty(config.getExclusiveSubjects())) {
        Set<OtherExamSubject> subjects = CollectUtils.newHashSet();
        subjects.addAll(firstSubjects);
        subjects.addAll(secondSubjects);
        query.where("subject not in (:subject)", subjects);
      }
      put("subjects", entityDao.search(query));
    } else {
      if (otherExamCategories.isEmpty()) {
        put("categorySubjects", Collections.emptyMap());
      } else {
        Map<Integer, List<OtherExamSubject>> categorySubjects = CollectUtils.newHashMap();
        List<OtherExamSubject> subjects = entityDao.get(OtherExamSubject.class, "category",
            otherExamCategories);
        for (OtherExamSubject otherExamSubject : subjects) {
          List<OtherExamSubject> oneCategorySubjects = categorySubjects.get(otherExamSubject.getCategory()
              .getId());
          if (null == oneCategorySubjects) {
            oneCategorySubjects = CollectUtils.newArrayList();
            categorySubjects.put(otherExamSubject.getCategory().getId(), oneCategorySubjects);
          }
          oneCategorySubjects.add(otherExamSubject);
        }
        put("categorySubjects", categorySubjects);
      }
    }
    put("otherExamSignUpConfig", config);
  }

  public String save() throws ParseException {
    OtherExamSignUpConfig config = (OtherExamSignUpConfig) populateEntity();
    OqlBuilder<OtherExamSignUpConfig> query = OqlBuilder.from(OtherExamSignUpConfig.class, "config");
    query.where("config.code = :configCode", config.getCode());
    query.where("config.name = :configName", config.getName());
    List<OtherExamSignUpConfig> configs = entityDao.search(query);
    if (CollectUtils.isNotEmpty(configs) && config.isTransient()) { return redirect("edit", "期号名称重复"); }
    String campusIdSeq = get("selectCampus");
    config.getCampuses().clear();
    if (Strings.isNotEmpty(campusIdSeq)) {
      config.addCampuses(entityDao.get(Campus.class, Strings.splitToInt(campusIdSeq)));
    }
    config.setProject(getProject());
    boolean createDefaultSubject = getBool("createDefaultSubject");
    if (createDefaultSubject) {
      otherExamSignUpConfigService.configDefaultSubject(
          entityDao.get(OtherExamCategory.class, getInt("otherExamSignUpConfig.category.id")), config);
    }
    // 生成考试科目
    config.getExclusiveSubjects().clear();
    Collection<ExclusiveSubject> exclusiveList = CollectUtils.newArrayList();
    String subjectOneString = get("subjectOne");
    String subjectTwoString = get("subjectTwo");
    String[] subjectOnes = Strings.split(subjectOneString, ",");
    String[] subjectTwos = Strings.split(subjectTwoString, ",");
    if (null != subjectOnes && null != subjectTwos) {
      for (int i = 0; i < subjectOnes.length; i++) {
        Integer subjectOneId = new Integer(subjectOnes[i]);
        OtherExamSubject subjectOne = entityDao.get(OtherExamSubject.class, subjectOneId);
        for (int j = 0; j < subjectTwos.length; j++) {
          Integer subjectTwoId = new Integer(subjectTwos[j]);
          OtherExamSubject subjectTwo = entityDao.get(OtherExamSubject.class, subjectTwoId);
          ExclusiveSubject exclusive = Model.newInstance(ExclusiveSubject.class);
          exclusive.setSubjectOne(subjectOne);
          exclusive.setSubjectTwo(subjectTwo);
          exclusive.setConfig(config);
          exclusiveList.add(exclusive);
        }
      }
    }
    config.getExclusiveSubjects().addAll(exclusiveList);
    try {
      entityDao.saveOrUpdate(config);
      return redirect("search", "info.save.success");
    } catch (Exception e) {
      logger.info("saveAndForwad failure", e);
      return redirect("search", "info.save.failure");
    }
  }

  public String getOtherExamSubjects() {
    Long categoryId = getLong("categoryId");
    if (categoryId != null) {
      OqlBuilder<OtherExamSubject> query = OqlBuilder.from(OtherExamSubject.class, "subject");
      query.where("subject.category.id =:categoryId", categoryId);
      List<OtherExamSubject> subjects = entityDao.search(query);
      put("datas", subjects);
    } else {
      put("datas", Collections.emptyList());
    }
    return forward("otherExamSubject");
  }
}
