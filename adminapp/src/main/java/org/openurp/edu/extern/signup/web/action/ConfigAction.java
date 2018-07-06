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
import org.openurp.edu.extern.code.model.ExamCategory;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExclusiveSubject;
import org.openurp.edu.extern.model.ExamSignupConfig;
import org.openurp.edu.extern.service.ExamSignupConfigService;
import org.openurp.edu.web.action.RestrictionSupportAction;

public class ConfigAction extends RestrictionSupportAction {

  protected ExamSignupConfigService examSignupConfigService;

  public void setExamSignupConfigService(ExamSignupConfigService examSignupConfigService) {
    this.examSignupConfigService = examSignupConfigService;
  }

  @Override
  protected String getEntityName() {
    return ExamSignupConfig.class.getName();
  }

  @Override
  public void indexSetting() {
    put("examCategries", codeService.getCodes(ExamCategory.class));
  }

  protected void editSetting(Entity<?> entity) {
    put("semesters",  getProject().getCalendars().get(0).getSemesters());
    ExamSignupConfig config = (ExamSignupConfig) entity;
    List<?> campuses = baseInfoService.getBaseInfos(Campus.class, getProject().getSchool());
    campuses.removeAll(config.getCampuses());
    put("campuses", campuses);
    List<ExamCategory> examCategories = codeService.getCodes(ExamCategory.class);
    put("examCategories", examCategories);
    // 查询互斥组
    Set<ExamSubject> firstSubjects = CollectUtils.newHashSet();
    Set<ExamSubject> secondSubjects = CollectUtils.newHashSet();
    for (ExclusiveSubject exclusiveSubject : config.getExclusiveSubjects()) {
      firstSubjects.add(exclusiveSubject.getSubjectOne());
      secondSubjects.add(exclusiveSubject.getSubjectTwo());
    }
    put("firstCategories", firstSubjects);
    put("secondCategories", secondSubjects);
    if (config.getCategory() != null) {
      OqlBuilder<ExamSubject> query = OqlBuilder.from(ExamSubject.class, "subject").where(
          "subject.category=:category", config.getCategory());
      if (CollectUtils.isNotEmpty(config.getExclusiveSubjects())) {
        Set<ExamSubject> subjects = CollectUtils.newHashSet();
        subjects.addAll(firstSubjects);
        subjects.addAll(secondSubjects);
        query.where("subject not in (:subject)", subjects);
      }
      put("subjects", entityDao.search(query));
    } else {
      if (examCategories.isEmpty()) {
        put("categorySubjects", Collections.emptyMap());
      } else {
        Map<Integer, List<ExamSubject>> categorySubjects = CollectUtils.newHashMap();
        List<ExamSubject> subjects = entityDao.get(ExamSubject.class, "category",
            examCategories);
        for (ExamSubject examSubject : subjects) {
          List<ExamSubject> oneCategorySubjects = categorySubjects.get(examSubject.getCategory()
              .getId());
          if (null == oneCategorySubjects) {
            oneCategorySubjects = CollectUtils.newArrayList();
            categorySubjects.put(examSubject.getCategory().getId(), oneCategorySubjects);
          }
          oneCategorySubjects.add(examSubject);
        }
        put("categorySubjects", categorySubjects);
      }
    }
    put("examSignupConfig", config);
  }

  public String save() throws ParseException {
    ExamSignupConfig config = (ExamSignupConfig) populateEntity();
    OqlBuilder<ExamSignupConfig> query = OqlBuilder.from(ExamSignupConfig.class, "config");
    query.where("config.code = :configCode", config.getCode());
    query.where("config.name = :configName", config.getName());
    List<ExamSignupConfig> configs = entityDao.search(query);
    if (CollectUtils.isNotEmpty(configs) && config.isTransient()) { return redirect("edit", "期号名称重复"); }
    String campusIdSeq = get("selectCampus");
    config.getCampuses().clear();
    if (Strings.isNotEmpty(campusIdSeq)) {
      config.addCampuses(entityDao.get(Campus.class, Strings.splitToInt(campusIdSeq)));
    }
    config.setProject(getProject());
    boolean createDefaultSubject = getBool("createDefaultSubject");
    if (createDefaultSubject) {
      examSignupConfigService.configDefaultSubject(
          entityDao.get(ExamCategory.class, getInt("examSignupConfig.category.id")), config);
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
        ExamSubject subjectOne = entityDao.get(ExamSubject.class, subjectOneId);
        for (int j = 0; j < subjectTwos.length; j++) {
          Integer subjectTwoId = new Integer(subjectTwos[j]);
          ExamSubject subjectTwo = entityDao.get(ExamSubject.class, subjectTwoId);
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

  public String getExternExamSubjects() {
    Long categoryId = getLong("categoryId");
    if (categoryId != null) {
      OqlBuilder<ExamSubject> query = OqlBuilder.from(ExamSubject.class, "subject");
      query.where("subject.category.id =:categoryId", categoryId);
      List<ExamSubject> subjects = entityDao.search(query);
      put("datas", subjects);
    } else {
      put("datas", Collections.emptyList());
    }
    return forward("examSubject");
  }
}
