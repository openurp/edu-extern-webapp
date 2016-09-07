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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.collection.Order;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.base.model.Department;
import org.openurp.base.model.Semester;
import org.openurp.edu.other.code.model.OtherExamCategory;
import org.openurp.edu.other.code.model.OtherExamSubject;
import org.openurp.edu.other.model.OtherExamSignUp;
import org.openurp.edu.web.action.SemesterSupportAction;

import com.opensymphony.xwork2.ActionContext;

/**
 * 对已报名的科目进行报名人数统计
 * 
 * @author chaostone
 */
public class OtherExamSignUpSummaryAction extends SemesterSupportAction {

  public String index() {

    // FIXME zhouqi 2011-06-10 下面的变量没有人使用
    Semester defulteSemester = null;
    Integer semesterId = getInt("semester.id");
    put("otherExamKindList", codeService.getCodes(OtherExamCategory.class));
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
    Integer otherExamKindId = getInt("otherExamCategory.id");
    OtherExamCategory otherExamCategory = null;
    if (null != otherExamKindId) {
      otherExamCategory = entityDao.get(OtherExamCategory.class, otherExamKindId);
      put("categorys", getCategory(otherExamCategory));
      put("signUpMap", buildSumMap(getSignUpSum(semester, otherExamCategory)));
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
    OtherExamCategory category = entityDao.get(OtherExamCategory.class, Integer.valueOf(keyArr[1]));
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
  private List getDeptSumDetail(Department dept, OtherExamCategory category, Semester semester) {
    OqlBuilder sumDetailQuery = OqlBuilder.from(OtherExamSignUp.class, "signUp");
    sumDetailQuery.from("OtherExamSignUp signUp,Student std");
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
  private List getSignUpSum(Semester semester, OtherExamCategory otherExamCategory) {
    OqlBuilder sumQuery = OqlBuilder.from(OtherExamSignUp.class, "signUp");
    sumQuery.from("OtherExamSignUp signUp,Student std ,Department dept");
    sumQuery.where("signUp.std=std");
    sumQuery.where("std.department =dept");
    sumQuery.where("dept in(:depts)", getDeparts());
    sumQuery.where("signUp.semester=:semester", semester);
    if (otherExamCategory != null) {
      sumQuery.where("exists(select 1 from OtherExamCategory other "
          + " where other=signUp.category and other.enabled =true and other.kind=:otherExamCategory)",
          otherExamCategory);
    } else {
      sumQuery.where("exists(select 1 from OtherExamCategory other "
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
  private List<OtherExamSubject> getCategory(OtherExamCategory otherExamCategory) {
    OqlBuilder<OtherExamSubject> query = OqlBuilder.from(OtherExamSubject.class, "subject");
    query.where("subject.endOn is null or subject.endOn > :nowAt", new Date());
    if (null != otherExamCategory) {
      query.where("subject.category = :category", otherExamCategory);
    }
    query.orderBy(Order.parse("category.code"));
    return entityDao.search(query);
  }
}
