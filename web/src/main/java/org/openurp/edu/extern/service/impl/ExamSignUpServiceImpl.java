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
package org.openurp.edu.extern.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Strings;
import org.openurp.base.model.Campus;
import org.openurp.base.model.Semester;
import org.openurp.edu.base.model.Project;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.dao.ExamSignUpDao;
import org.openurp.edu.extern.model.ExamSignUp;
import org.openurp.edu.extern.model.ExamSignUpConfig;
import org.openurp.edu.extern.model.ExamSignUpSetting;
import org.openurp.edu.extern.service.ExamSignUpService;
import org.openurp.edu.extern.service.checker.ExamSignUpChecker;

public class ExamSignUpServiceImpl extends BaseServiceImpl implements ExamSignUpService {

  private ExamSignUpDao otherExamSignUpDao;

  private List<ExamSignUpChecker> checkerStack = CollectUtils.newArrayList();

  /**
   * 报名
   */
  public String signUp(ExamSignUp signUp, ExamSignUpSetting setting) {
    String msg = "";
    Student student = signUp.getStd();
    if (!setting.getPermitStds().contains(student)) {
      if (setting.getForbiddenStds().contains(student)) {
        msg = "你不能报名此考试科目";
      } else {
        msg = canSignUp(signUp.getStd(), setting);
      }
    }
    if (Strings.isEmpty(msg)) {
      // 检查科目设置中的最大人数
      Integer maxStd = setting.getMaxStd();
      if (null != maxStd && maxStd.intValue() > 0) {
        synchronized (setting) {
          int countStd = otherExamSignUpDao.getSignUpCount(setting);
          if (maxStd.intValue() <= countStd) { return "other.failure.maxStdArrived"; }
        }
      }
      if (signUp.getCampus() == null || signUp.getCampus().getId() == null) {
        signUp.setCampus(signUp.getStd().getCampus());
      }
      signUp.setSemester(setting.getConfig().getSemester());
      signUp.setSubject(setting.getSubject());
      try {
        entityDao.saveOrUpdate(signUp);
      } catch (Exception e) {
        return "报名失败";
      }
      return null;
    } else {
      return msg;
    }
  }

  /**
   * 判断是否能够报名，检查报名条件
   */
  public String canSignUp(Student student, ExamSignUpSetting setting) {
    for (ExamSignUpChecker checker : checkerStack) {
      String msg = checker.check(student, setting);
      if (Strings.isNotBlank(msg)) { return msg; }
    }
    return null;
  }

  public String cancelSignUp(Student std, ExamSignUpSetting setting) {
    // 判断时间
    if (!setting.getConfig().isOpened() || !setting.getConfig().isTimeSuitable()) { return ExamSignUpChecker.notInTime; }
    ExamSignUp signUp = getExamSignUp(std, setting);
    if (null != signUp) {
      entityDao.remove(signUp);
    }
    return null;
  }

  public ExamSignUp getExamSignUp(Student std, ExamSignUpSetting setting) {
    OqlBuilder<ExamSignUp> query = OqlBuilder.from(ExamSignUp.class, "signUp");
    query.where("signUp.std = :std", std);
    query.where("signUp.signUpAt between :start and :end", setting.getBeginAt(), setting.getEndAt());
    query.where("signUp.subject = :subject", setting.getSubject());
    List<ExamSignUp> existSignUps = entityDao.search(query);
    return CollectUtils.isEmpty(existSignUps) ? null : existSignUps.get(0);
  }

  public ExamSignUp getExamSignUp(Student std, Semester semester, ExamSignUpSetting setting) {
    OqlBuilder<ExamSignUp> query = OqlBuilder.from(ExamSignUp.class, "signUp");
    query.where("signUp.std = :std", std);
    query.where("signUp.semester = :semester", semester);
    query.where("signUp.signUpAt between :start and :end", setting.getBeginAt(), setting.getEndAt());
    query.where("signUp.subject = :subject", setting.getSubject());
    List<ExamSignUp> existSignUps = entityDao.search(query);
    return CollectUtils.isEmpty(existSignUps) ? null : existSignUps.get(0);
  }

  public List<ExamSignUp> getExamSignUps(Student std, Date start, Date end) {
    OqlBuilder<ExamSignUp> query = OqlBuilder.from(ExamSignUp.class, "signUp");
    query.where("signUp.std = :std", std);
    query.where("signUp.signUpAt between :start and :end", start, end);
    return entityDao.search(query);
  }

  public List<ExamSignUpSetting> getOpenedSettings(Project project) {
    OqlBuilder<ExamSignUpSetting> query = OqlBuilder.from(ExamSignUpSetting.class, "setting");
    query.where("setting.config.opend = true");
    query.where("setting.config.project = :project", project);
    query.where(":now  between setting.config.beginAt and setting.config.endAt", new Date());
    return entityDao.search(query);
  }

  /**
   * 根据考试类型来获得某个考试类型的所有期号
   * 
   * @param kindId
   * @return
   */
  public List<ExamSignUpConfig> getConfigs(Long kindId) {
    OqlBuilder<ExamSignUpConfig> query = OqlBuilder.from(ExamSignUpConfig.class, "config");
    query.where("config.category.id = :categoryId", kindId);
    return entityDao.search(query);
  }

  /**
   * 根据考试类型来获得某个考试类型的所有科目
   */
  public List<ExamSubject> getSubjects(Long categoryId) {
    OqlBuilder<ExamSubject> query = OqlBuilder.from(ExamSubject.class, "subject");
    query.where("subject.category.id =:categoryId", categoryId);
    query.select("subject.id,subject.name");
    return entityDao.search(query);
  }

  /*
   * public Map<String,String> getSubjectMap(Long categoryId) {
   * OqlBuilder<ExamSubject> query = OqlBuilder.from(ExamSubject.class, "subject");
   * query.where("subject.category.id =:categoryId", categoryId);
   * List subjectList = entityDao.search(query);
   * return entityDao.search(query);
   * }
   */

  /**
   * 根据期号来获得某个期号的所有科目
   */
  public List<ExamSubject> getSubjectsByConfigId(Long configId) {
    List<ExamSubject> subjects = CollectUtils.newArrayList();
    ExamSignUpConfig config = entityDao.get(ExamSignUpConfig.class, configId);
    if (CollectUtils.isEmpty(config.getSettings())) { return null; }
    for (ExamSignUpSetting setting : config.getSettings()) {
      subjects.add(setting.getSubject());
    }
    return subjects;
  }

  public Set<Campus> getCampusesByConfigId(Long configId) {
    ExamSignUpConfig config = this.entityDao.get(ExamSignUpConfig.class, configId);
    if (CollectUtils.isEmpty(config.getCampuses())) { return null; }
    return config.getCampuses();
  }

  /**
   * 获得这次开放的期号中某个学生的所有报名记录
   */
  public List<ExamSignUp> getSignUps(Student std, ExamSignUpConfig config) {
    OqlBuilder<ExamSignUp> query = OqlBuilder.from(ExamSignUp.class, "signUp");
    query.where("signUp.semester = :semester", config.getSemester());
    query.where("signUp.std = :std", std);
    query.where("signUp.subject in (:subjects)", config.getSubjects());
    return entityDao.search(query);
  }

  /**
   * 根据期号和科目来获得唯一的科目设置
   */
  public ExamSignUpSetting getSettingByConfigCategory(Long configId, Long subjectId) {
    OqlBuilder<ExamSignUpSetting> query = OqlBuilder.from(ExamSignUpSetting.class, "setting");
    query.where("setting.config.id = :configId", configId);
    query.where("setting.subject.id = :subjectId", subjectId);
    List<ExamSignUpSetting> settings = entityDao.search(query);
    return CollectUtils.isEmpty(settings) ? null : settings.get(0);
  }

  /**
   * 根据考试类型id来获得某个考试类型当前开放的期号
   */
  public List<ExamSignUpConfig> getOpenedConfigs(Project project) {
    return getOpenedConfigs(project, null);
  }

  public List<ExamSignUpConfig> getOpenedConfigs(Project project, ExamSignUpSetting setting) {
    OqlBuilder<ExamSignUpConfig> query = OqlBuilder.from(ExamSignUpConfig.class, "config");
    if (null != setting) {
      query.where("config = :config", setting.getConfig());
    }
    query.where("config.project = :project", project);
    query.where("config.opened = true");
    query.where(":time between config.beginAt and config.endAt ", new Date());
    return entityDao.search(query);
  }

  public void setExamSignUpDao(ExamSignUpDao otherExamSignUpDao) {
    this.otherExamSignUpDao = otherExamSignUpDao;
  }

  public List<ExamSignUpChecker> getCheckerStack() {
    return checkerStack;
  }

  public void setCheckerStack(List<ExamSignUpChecker> checkerStack) {
    this.checkerStack = checkerStack;
  }

  public boolean isFree(Student student, ExamSubject subject) {
    OqlBuilder<?> query = OqlBuilder.from(ExamSignUp.class, "signUp");
    query.where("signUp.std =:std", student);
    query.where("signUp.category=:category", subject);
    query.select("count(*)");
    // return subject.isFreeMode() && ((Long) entityDao.search(query).iterator().next() == 0);
    return false;
  }

  public boolean isExist(ExamSignUp signUp) {
    OqlBuilder<ExamSignUp> builder = OqlBuilder.from(ExamSignUp.class, "otherExamSignUp");
    builder.where("otherExamSignUp.subject.id =:subjectId", signUp.getSubject().getId());
    builder.where("otherExamSignUp.semester.id =:semesterId", signUp.getSemester().getId());
    builder.where("otherExamSignUp.std.code =:code", signUp.getStd().getCode());
    if (signUp.getId() == null) {
      List<ExamSignUp> sizeExam = entityDao.search(builder);
      if (!CollectUtils.isEmpty(sizeExam)) { return true; }
    } else {
      builder.where("otherExamSignUp.id <>:id", signUp.getId());
      List<ExamSignUp> sizeExam = entityDao.search(builder);
      if (!CollectUtils.isEmpty(sizeExam)) { return true; }
    }
    return false;

  }
}
