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
import org.openurp.edu.extern.model.ExamSignUp;
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
    put("otherExamKindList", codeService.getCodes(ExamCategory.class));
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
  public String signUpSummaryByDept() {
    Semester semester = entityDao.get(Semester.class, getInt("semester.id"));
    Integer otherExamKindId = getInt("otherExternExamCategory.id");
    ExamCategory otherExternExamCategory = null;
    if (null != otherExamKindId) {
      otherExternExamCategory = entityDao.get(ExamCategory.class, otherExamKindId);
      put("categorys", getCategory(otherExternExamCategory));
      put("signUpMap", buildSumMap(getSignUpSum(semester, otherExternExamCategory)));
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
    OqlBuilder sumDetailQuery = OqlBuilder.from(ExamSignUp.class, "signUp");
    sumDetailQuery.from("ExamSignUp signUp,Student std");
    sumDetailQuery.where("signUp.std=std");
    sumDetailQuery.where("std.department=:dept", dept);
    sumDetailQuery.join("left", "std.adminClasses", "adminClass");
    sumDetailQuery.where("signUp.category=:category", category);
    sumDetailQuery.where("signUp.semester=:semester", semester);
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
    Map signUpMap = new HashMap();
    if (!CollectUtils.isEmpty(sumList)) {
      for (int i = 0; i < sumList.size(); i++) {
        Object[] sumArray = (Object[]) sumList.get(i);
        String key = sumArray[0].toString() + "_" + sumArray[1].toString();
        signUpMap.put(key, sumArray[2]);
      }
    }
    return signUpMap;
  }

  /**
   * 按照科目和院系对报名结果进行分组查询
   * 
   * @param semester
   * @return
   */
  private List getSignUpSum(Semester semester, ExamCategory otherExternExamCategory) {
    OqlBuilder sumQuery = OqlBuilder.from(ExamSignUp.class, "signUp");
    sumQuery.from("ExamSignUp signUp,Student std ,Department dept");
    sumQuery.where("signUp.std=std");
    sumQuery.where("std.department =dept");
    sumQuery.where("dept in(:depts)", getDeparts());
    sumQuery.where("signUp.semester=:semester", semester);
    if (otherExternExamCategory != null) {
      sumQuery.where("exists(select 1 from ExamCategory other "
          + " where other=signUp.category and other.enabled =true and other.kind=:otherExternExamCategory)",
          otherExternExamCategory);
    } else {
      sumQuery.where("exists(select 1 from ExamCategory other "
          + " where other=signUp.category and other.enabled =true )");
    }
    sumQuery.groupBy("dept.id,signUp.category.id");
    sumQuery.select("dept.id,signUp.category.id,count(*)");
    List sumList = entityDao.search(sumQuery);
    return sumList;
  }

  /**
   * 得到资格考试科目
   * 
   * @return
   */
  private List<ExamSubject> getCategory(ExamCategory otherExternExamCategory) {
    OqlBuilder<ExamSubject> query = OqlBuilder.from(ExamSubject.class, "subject");
    query.where("subject.endOn is null or subject.endOn > :nowAt", new Date());
    if (null != otherExternExamCategory) {
      query.where("subject.category = :category", otherExternExamCategory);
    }
    query.orderBy(Order.parse("category.code"));
    return entityDao.search(query);
  }
}
