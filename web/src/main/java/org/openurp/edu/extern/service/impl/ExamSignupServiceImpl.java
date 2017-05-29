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
import org.openurp.edu.extern.dao.ExamSignupDao;
import org.openurp.edu.extern.model.ExamSignup;
import org.openurp.edu.extern.model.ExamSignupConfig;
import org.openurp.edu.extern.model.ExamSignupSetting;
import org.openurp.edu.extern.service.ExamSignupService;
import org.openurp.edu.extern.service.checker.ExamSignupChecker;

public class ExamSignupServiceImpl extends BaseServiceImpl implements ExamSignupService {

  private ExamSignupDao examSignupDao;

  private List<ExamSignupChecker> checkerStack = CollectUtils.newArrayList();

  /**
   * 报名
   */
  public String signup(ExamSignup signup, ExamSignupSetting setting) {
    String msg = "";
    Student student = signup.getStd();
    if (!setting.getPermitStds().contains(student)) {
      if (setting.getForbiddenStds().contains(student)) {
        msg = "你不能报名此考试科目";
      } else {
        msg = canSignup(signup.getStd(), setting);
      }
    }
    if (Strings.isEmpty(msg)) {
      // 检查科目设置中的最大人数
      Integer maxStd = setting.getMaxStd();
      if (null != maxStd && maxStd.intValue() > 0) {
        synchronized (setting) {
          int countStd = examSignupDao.getSignupCount(setting);
          if (maxStd.intValue() <= countStd) { return "extern.exam.failure.maxStdArrived"; }
        }
      }
      if (signup.getCampus() == null || signup.getCampus().getId() == null) {
        signup.setCampus(signup.getStd().getCampus());
      }
      signup.setSemester(setting.getConfig().getSemester());
      signup.setSubject(setting.getSubject());
      try {
        entityDao.saveOrUpdate(signup);
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
  public String canSignup(Student student, ExamSignupSetting setting) {
    for (ExamSignupChecker checker : checkerStack) {
      String msg = checker.check(student, setting);
      if (Strings.isNotBlank(msg)) { return msg; }
    }
    return null;
  }

  public String cancelSignup(Student std, ExamSignupSetting setting) {
    // 判断时间
    if (!setting.getConfig().isOpened() || !setting.getConfig().isTimeSuitable()) { return ExamSignupChecker.notInTime; }
    ExamSignup signup = getExamSignup(std, setting);
    if (null != signup) {
      entityDao.remove(signup);
    }
    return null;
  }

  public ExamSignup getExamSignup(Student std, ExamSignupSetting setting) {
    ExamSignupConfig config = setting.getConfig();
    OqlBuilder<ExamSignup> query = OqlBuilder.from(ExamSignup.class, "signup");
    query.where("signup.std = :std", std);
    query.where("signup.signupAt between :start and :end", config.getBeginAt(), config.getEndAt());
    query.where("signup.subject = :subject", setting.getSubject());
    List<ExamSignup> existSignups = entityDao.search(query);
    return CollectUtils.isEmpty(existSignups) ? null : existSignups.get(0);
  }

  public ExamSignup getExamSignup(Student std, Semester semester, ExamSignupSetting setting) {
    ExamSignupConfig config = setting.getConfig();
    OqlBuilder<ExamSignup> query = OqlBuilder.from(ExamSignup.class, "signup");
    query.where("signup.std = :std", std);
    query.where("signup.semester = :semester", semester);
    query.where("signup.signupAt between :start and :end", config.getBeginAt(), config.getEndAt());
    query.where("signup.subject = :subject", setting.getSubject());
    List<ExamSignup> existSignups = entityDao.search(query);
    return CollectUtils.isEmpty(existSignups) ? null : existSignups.get(0);
  }

  public List<ExamSignup> getExamSignups(Student std, Date start, Date end) {
    OqlBuilder<ExamSignup> query = OqlBuilder.from(ExamSignup.class, "signup");
    query.where("signup.std = :std", std);
    query.where("signup.signupAt between :start and :end", start, end);
    return entityDao.search(query);
  }

  public List<ExamSignupSetting> getOpenedSettings(Project project) {
    OqlBuilder<ExamSignupSetting> query = OqlBuilder.from(ExamSignupSetting.class, "setting");
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
  public List<ExamSignupConfig> getConfigs(Long kindId) {
    OqlBuilder<ExamSignupConfig> query = OqlBuilder.from(ExamSignupConfig.class, "config");
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
    ExamSignupConfig config = entityDao.get(ExamSignupConfig.class, configId);
    if (CollectUtils.isEmpty(config.getSettings())) { return null; }
    for (ExamSignupSetting setting : config.getSettings()) {
      subjects.add(setting.getSubject());
    }
    return subjects;
  }

  public Set<Campus> getCampusesByConfigId(Long configId) {
    ExamSignupConfig config = this.entityDao.get(ExamSignupConfig.class, configId);
    if (CollectUtils.isEmpty(config.getCampuses())) { return null; }
    return config.getCampuses();
  }

  /**
   * 获得这次开放的期号中某个学生的所有报名记录
   */
  public List<ExamSignup> getSignups(Student std, ExamSignupConfig config) {
    OqlBuilder<ExamSignup> query = OqlBuilder.from(ExamSignup.class, "signup");
    query.where("signup.semester = :semester", config.getSemester());
    query.where("signup.std = :std", std);
    query.where("signup.subject in (:subjects)", config.getSubjects());
    return entityDao.search(query);
  }

  /**
   * 根据期号和科目来获得唯一的科目设置
   */
  public ExamSignupSetting getSettingByConfigCategory(Long configId, Long subjectId) {
    OqlBuilder<ExamSignupSetting> query = OqlBuilder.from(ExamSignupSetting.class, "setting");
    query.where("setting.config.id = :configId", configId);
    query.where("setting.subject.id = :subjectId", subjectId);
    List<ExamSignupSetting> settings = entityDao.search(query);
    return CollectUtils.isEmpty(settings) ? null : settings.get(0);
  }

  /**
   * 根据考试类型id来获得某个考试类型当前开放的期号
   */
  public List<ExamSignupConfig> getOpenedConfigs(Project project) {
    return getOpenedConfigs(project, null);
  }

  public List<ExamSignupConfig> getOpenedConfigs(Project project, ExamSignupSetting setting) {
    OqlBuilder<ExamSignupConfig> query = OqlBuilder.from(ExamSignupConfig.class, "config");
    if (null != setting) {
      query.where("config = :config", setting.getConfig());
    }
    query.where("config.project = :project", project);
    query.where("config.opened = true");
    query.where(":time between config.beginAt and config.endAt ", new Date());
    return entityDao.search(query);
  }

  public void setExamSignupDao(ExamSignupDao examSignupDao) {
    this.examSignupDao = examSignupDao;
  }

  public List<ExamSignupChecker> getCheckerStack() {
    return checkerStack;
  }

  public void setCheckerStack(List<ExamSignupChecker> checkerStack) {
    this.checkerStack = checkerStack;
  }

  public boolean isFree(Student student, ExamSubject subject) {
    OqlBuilder<?> query = OqlBuilder.from(ExamSignup.class, "signup");
    query.where("signup.std =:std", student);
    query.where("signup.category=:category", subject);
    query.select("count(*)");
    // return subject.isFreeMode() && ((Long) entityDao.search(query).iterator().next() == 0);
    return false;
  }

  public boolean isExist(ExamSignup signup) {
    OqlBuilder<ExamSignup> builder = OqlBuilder.from(ExamSignup.class, "examSignup");
    builder.where("examSignup.subject.id =:subjectId", signup.getSubject().getId());
    builder.where("examSignup.semester.id =:semesterId", signup.getSemester().getId());
    builder.where("examSignup.std.user.code =:code", signup.getStd().getUser().getCode());
    if (signup.getId() == null) {
      List<ExamSignup> sizeExam = entityDao.search(builder);
      if (!CollectUtils.isEmpty(sizeExam)) { return true; }
    } else {
      builder.where("examSignup.id <>:id", signup.getId());
      List<ExamSignup> sizeExam = entityDao.search(builder);
      if (!CollectUtils.isEmpty(sizeExam)) { return true; }
    }
    return false;

  }
}
