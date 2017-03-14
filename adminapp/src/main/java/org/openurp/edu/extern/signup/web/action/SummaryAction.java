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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.collection.Order;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.base.model.Department;
import org.openurp.base.model.Semester;
import org.openurp.edu.extern.code.model.ExamCategory;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExamSignup;
import org.openurp.edu.web.action.SemesterSupportAction;

import com.opensymphony.xwork2.ActionContext;

/**
 * 对已报名的科目进行报名人数统计
 * 
 * @author chaostone
 */
public class SummaryAction extends SemesterSupportAction {

  public String index() {

    // FIXME zhouqi 2011-06-10 下面的变量没有人使用
    Semester defulteSemester = null;
    Integer semesterId = getInt("semester.id");
    put("examKindList", codeService.getCodes(ExamCategory.class));
    if (null == semesterId) {
      defulteSemester = (Semester) ActionContext.getContext().getContextMap().get("semester");
    } else {
      defulteSemester = entityDao.get(Semester.class, semesterId);
    }
    return forward();
  }

  /**
   * 得到院系报名学生数量
   * 
   * @return
   */
  public String signupSummaryByDept() {
    Semester semester = entityDao.get(Semester.class, getInt("semester.id"));
    Integer examKindId = getInt("examCategory.id");
    ExamCategory examCategory = null;
    if (null != examKindId) {
      examCategory = entityDao.get(ExamCategory.class, examKindId);
      put("categorys", getCategory(examCategory));
      put("signupMap", buildSumMap(getSignupSum(semester, examCategory)));
    }
    put("depts", getDeparts());
    put("semester", semester);
    return forward();
  }

  /**
   * 得到院系的该课程报名情况
   * 
   * @return
   */
  public String showDeptDetail() {
    Integer semesterid = getInt("semester.id");
    Semester semester = entityDao.get(Semester.class, semesterid);
    String key = get("key");
    String[] keyArr = key.split("_");
    Department dept = entityDao.get(Department.class, Integer.valueOf(keyArr[0]));
    ExamCategory category = entityDao.get(ExamCategory.class, Integer.valueOf(keyArr[1]));
    put("sumList", getDeptSumDetail(dept, category, semester));
    put("dept", dept);
    put("category", category);
    return forward();
  }

  /**
   * 得到院系详细的报名情况
   * 
   * @param dept
   * @param category
   * @return
   */
  private List getDeptSumDetail(Department dept, ExamCategory category, Semester semester) {
    OqlBuilder sumDetailQuery = OqlBuilder.from(ExamSignup.class, "signup");
    sumDetailQuery.from("ExamSignup signup,Student std");
    sumDetailQuery.where("signup.std=std");
    sumDetailQuery.where("std.department=:dept", dept);
    sumDetailQuery.join("left", "std.adminClasses", "adminClass");
    sumDetailQuery.where("signup.category=:category", category);
    sumDetailQuery.where("signup.semester=:semester", semester);
    sumDetailQuery.select("adminClass.name,count(*)");
    sumDetailQuery.orderBy("adminClass.name");
    sumDetailQuery.groupBy("adminClass.name");
    List sumList = entityDao.search(sumDetailQuery);
    return sumList;
  }

  /**
   * 以"院系(班级)id_科目id"作为主键,报名人数作为value,创建map
   * 
   * @param sumList
   * @return
   */
  private Map buildSumMap(List sumList) {
    Map signupMap = new HashMap();
    if (!CollectUtils.isEmpty(sumList)) {
      for (int i = 0; i < sumList.size(); i++) {
        Object[] sumArray = (Object[]) sumList.get(i);
        String key = sumArray[0].toString() + "_" + sumArray[1].toString();
        signupMap.put(key, sumArray[2]);
      }
    }
    return signupMap;
  }

  /**
   * 按照科目和院系对报名结果进行分组查询
   * 
   * @param semester
   * @return
   */
  private List getSignupSum(Semester semester, ExamCategory examCategory) {
    OqlBuilder sumQuery = OqlBuilder.from(ExamSignup.class, "signup");
    sumQuery.from("ExamSignup signup,Student std ,Department dept");
    sumQuery.where("signup.std=std");
    sumQuery.where("std.department =dept");
    sumQuery.where("dept in(:depts)", getDeparts());
    sumQuery.where("signup.semester=:semester", semester);
    if (examCategory != null) {
      sumQuery.where("exists(select 1 from ExamCategory ec "
          + " where ec=signup.category and ec.enabled =true and ec.kind=:examCategory)",
          examCategory);
    } else {
      sumQuery.where("exists(select 1 from ExamCategory ec "
          + " where ec=signup.category and ec.enabled =true )");
    }
    sumQuery.groupBy("dept.id,signup.category.id");
    sumQuery.select("dept.id,signup.category.id,count(*)");
    List sumList = entityDao.search(sumQuery);
    return sumList;
  }

  /**
   * 得到资格考试科目
   * 
   * @return
   */
  private List<ExamSubject> getCategory(ExamCategory examCategory) {
    OqlBuilder<ExamSubject> query = OqlBuilder.from(ExamSubject.class, "subject");
    query.where("subject.endOn is null or subject.endOn > :nowAt", new Date());
    if (null != examCategory) {
      query.where("subject.category = :category", examCategory);
    }
    query.orderBy(Order.parse("category.code"));
    return entityDao.search(query);
  }
}
