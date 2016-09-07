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
import org.openurp.edu.other.code.model.OtherExamCategory;
import org.openurp.edu.other.code.model.OtherExamSubject;
import org.openurp.edu.other.model.OtherExamSignUp;
import org.openurp.edu.other.model.OtherExamSignUpSetting;
import org.openurp.edu.other.service.OtherExamSignUpService;
import org.openurp.edu.web.action.RestrictionSupportAction;

public class SignUpByTeacherAction extends RestrictionSupportAction {
  protected OtherExamSignUpService signUpByTeacherService;

  public String index() {
    put("otherExamSubjects", codeService.getCodes(OtherExamSubject.class));
    put("otherExamCategorys", codeService.getCodes(OtherExamCategory.class));
    put("campuss", baseInfoService.getBaseInfos(Campus.class, getProject().getSchool()));
    put("semesters", entityDao.getAll(Semester.class));
    put("departments", getTeachDeparts());
    put("calendars", semesterService.getCalendars(getProjects()));
    return forward();
  }

  @SuppressWarnings("unchecked")
  public void editSetting(Entity otherGrade) {
    put("otherExamCategories", codeService.getCodes(OtherExamCategory.class));
    put("campuss", baseInfoService.getBaseInfos(Campus.class, getProject().getSchool()));
    put("semesters", entityDao.search(OqlBuilder.from("from Semester")));
    List<Calendar> calendars = semesterService.getCalendars(getProjects());
    if (otherGrade.isTransient() && CollectUtils.isNotEmpty(calendars)) {
      Semester semester = semesterService.getCurSemester((Calendar) calendars.get(0));
      if (null != semester) {
        ((OtherExamSignUp) otherGrade).setSemester(semester);
      }
    }
    put("departments", getTeachDeparts());
  }

  protected String saveAndForward(Entity entity) {
    // 根据学号得到学生
    Student student = (Student) entityDao.get(Student.class, getLong("otherExamSignUp.std.id"));
    // 得到选择的科目
    OtherExamCategory otherExamCategory = (OtherExamCategory) entityDao.get(OtherExamCategory.class,
        getInt("otherExamSignUp.category.id"));
    // 学年学期
    Semester semester = (Semester) entityDao.get(Semester.class, getInt("otherExamSignUp.semester.id"));
    // 资格考试报名科目设置
    OqlBuilder settingQuery = OqlBuilder.from(OtherExamSignUpSetting.class, "otherExamSignUpSetting");
    settingQuery.where("otherExamSignUpSetting.category =:category", otherExamCategory);
    settingQuery.where("otherExamSignUpSetting.config.semester =:semester", semester);
    List settings = entityDao.search(settingQuery);
    OtherExamSignUpSetting setting = null;
    if (settings.size() != 0) {
      setting = (OtherExamSignUpSetting) settings.get(0);
      // 检查是够可以报名
      String msg = signUpByTeacherService.canSignUp(student, setting);
      if (Strings.isNotEmpty(msg)) {
        return redirect("search", msg);
      } else {
        OtherExamSignUp signUp = (OtherExamSignUp) entity;
        // 不能重复
        if (signUp.isTransient()) {
          OqlBuilder query = OqlBuilder.from(OtherExamSignUp.class, "signUp");
          query.where("signUp.std=:std", signUp.getStd());
          query.where("signUp.semester=:semester", signUp.getSemester());
          query.where("signUp.category=:category", signUp.getSubject());

          List existSignUps = (List) entityDao.search(query);
          if (!existSignUps.isEmpty()) { return redirect("edit", "info.save.failure"); }
        }
        saveOrUpdate(Collections.singletonList(entity));
        boolean addNext = getBool("addNext");
        if (addNext) {
          getFlash().put("otherExamSignUp.semester.id", signUp.getSemester().getId());
          getFlash().put("otherExamSignUp.category.id", signUp.getSubject().getId());
          return redirect("edit", "info.save.success");
        }
        return redirect("search", "info.save.success");
      }
    } else {
      return redirect("search", "本学期考试没有这门科目！");
    }
  }

  protected OqlBuilder getQueryBuilder() {
    OqlBuilder query = OqlBuilder.from(OtherExamSignUp.class, "otherExamSignUp");
    populateConditions(query, "adminClassesName");
    query.join("otherExamSignUp.std", "std");
    query.where(QueryHelper.extractConditions(Student.class, "std", null));

    String adminClass = get("adminClassesName");
    if (Strings.isNotEmpty(adminClass)) {
      query.join("std.adminClasses", "adminClass");
      query.where(Condition.like("adminClass.name", adminClass));
    }
    query.limit(getPageLimit());
    return query;
  }

  public void setSignUpByTeacherService(OtherExamSignUpService signUpByTeacherService) {
    this.signUpByTeacherService = signUpByTeacherService;
  }

}
