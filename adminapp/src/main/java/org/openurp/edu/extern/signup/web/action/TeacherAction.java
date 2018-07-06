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

import java.util.Collections;
import java.util.List;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.query.builder.Condition;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.Entity;
import org.beangle.commons.lang.Strings;
import org.beangle.struts2.helper.QueryHelper;
import org.openurp.base.model.Calendar;
import org.openurp.base.model.Campus;
import org.openurp.base.model.Semester;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.code.model.ExamCategory;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExamSignup;
import org.openurp.edu.extern.model.ExamSignupSetting;
import org.openurp.edu.extern.service.ExamSignupService;
import org.openurp.edu.web.action.RestrictionSupportAction;

public class TeacherAction extends RestrictionSupportAction {
  protected ExamSignupService signupByTeacherService;

  public String index() {
    put("examSubjects", codeService.getCodes(ExamSubject.class));
    put("examCategorys", codeService.getCodes(ExamCategory.class));
    put("campuss", baseInfoService.getBaseInfos(Campus.class, getProject().getSchool()));
    put("semesters", entityDao.getAll(Semester.class));
    put("departments", getTeachDeparts());
    put("calendars", semesterService.getCalendars(getProjects()));
    return forward();
  }

  @SuppressWarnings("unchecked")
  public void editSetting(Entity examGrade) {
    put("examCategories", codeService.getCodes(ExamCategory.class));
    put("campuss", baseInfoService.getBaseInfos(Campus.class, getProject().getSchool()));
    put("semesters", entityDao.search(OqlBuilder.from("from Semester")));
    List<Calendar> calendars = semesterService.getCalendars(getProjects());
    if (examGrade.isTransient() && CollectUtils.isNotEmpty(calendars)) {
      Semester semester = semesterService.getCurSemester((Calendar) calendars.get(0));
      if (null != semester) {
        ((ExamSignup) examGrade).setSemester(semester);
      }
    }
    put("departments", getTeachDeparts());
  }

  protected String saveAndForward(Entity entity) {
    // 根据学号得到学生
    Student student = (Student) entityDao.get(Student.class, getLong("examSignup.std.id"));
    // 得到选择的科目
    ExamCategory examCategory = (ExamCategory) entityDao.get(ExamCategory.class,
        getInt("examSignup.category.id"));
    // 学年学期
    Semester semester = (Semester) entityDao.get(Semester.class, getInt("examSignup.semester.id"));
    // 资格考试报名科目设置
    OqlBuilder settingQuery = OqlBuilder.from(ExamSignupSetting.class, "examSignupSetting");
    settingQuery.where("examSignupSetting.category =:category", examCategory);
    settingQuery.where("examSignupSetting.config.semester =:semester", semester);
    List settings = entityDao.search(settingQuery);
    ExamSignupSetting setting = null;
    if (settings.size() != 0) {
      setting = (ExamSignupSetting) settings.get(0);
      // 检查是够可以报名
      String msg = signupByTeacherService.canSignup(student, setting);
      if (Strings.isNotEmpty(msg)) {
        return redirect("search", msg);
      } else {
        ExamSignup signup = (ExamSignup) entity;
        // 不能重复
        if (signup.isTransient()) {
          OqlBuilder query = OqlBuilder.from(ExamSignup.class, "signup");
          query.where("signup.std=:std", signup.getStd());
          query.where("signup.semester=:semester", signup.getSemester());
          query.where("signup.category=:category", signup.getSubject());

          List existSignups = (List) entityDao.search(query);
          if (!existSignups.isEmpty()) { return redirect("edit", "info.save.failure"); }
        }
        saveOrUpdate(Collections.singletonList(entity));
        boolean addNext = getBool("addNext");
        if (addNext) {
          getFlash().put("examSignup.semester.id", signup.getSemester().getId());
          getFlash().put("examSignup.category.id", signup.getSubject().getId());
          return redirect("edit", "info.save.success");
        }
        return redirect("search", "info.save.success");
      }
    } else {
      return redirect("search", "本学期考试没有这门科目！");
    }
  }

  protected OqlBuilder getQueryBuilder() {
    OqlBuilder query = OqlBuilder.from(ExamSignup.class, "examSignup");
    populateConditions(query, "adminClassesName");
    query.join("examSignup.std", "std");
    query.where(QueryHelper.extractConditions(Student.class, "std", null));

    String adminClass = get("adminClassesName");
    if (Strings.isNotEmpty(adminClass)) {
      query.join("std.adminClasses", "adminClass");
      query.where(Condition.like("adminClass.name", adminClass));
    }
    query.limit(getPageLimit());
    return query;
  }

  public void setSignupByTeacherService(ExamSignupService signupByTeacherService) {
    this.signupByTeacherService = signupByTeacherService;
  }

}
