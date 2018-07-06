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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.collection.Order;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.metadata.Model;
import org.beangle.commons.lang.Strings;
import org.beangle.struts2.convention.route.Action;
import org.openurp.base.model.Department;
import org.openurp.base.model.Semester;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.eams.util.stat.StatGroup;
import org.openurp.edu.eams.util.stat.StatHelper;
import org.openurp.edu.extern.code.model.ExamCategory;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExamSignup;
import org.openurp.edu.extern.model.ExamSignupConfig;
import org.openurp.edu.extern.model.ExamSignupSetting;
import org.openurp.edu.extern.model.ExternExamGrade;
import org.openurp.edu.extern.model.SignupStat;
import org.openurp.edu.web.action.SemesterSupportAction;

public class StatAction extends SemesterSupportAction {

  @Override
  protected String getEntityName() {
    return ExamSignup.class.getName();
  }

  @Override
  protected void indexSetting() {
    put("examSubjects", codeService.getCodes(ExamSubject.class));
    put("examCategories", codeService.getCodes(ExamCategory.class));
    getSemester();
  }

  public String search() {
    return forward(new Action("signupInfo"));
  }

  /**
   * 统计应收报名费
   *
   * @return
   */
  @SuppressWarnings("unchecked")
  public String signupInfo() {
    Long semester = getLong("examSignup.semester.id");
    Integer subject = getInt("examSignup.subject.id");
    Integer category = getInt("examSignup.subject.category.id");
    OqlBuilder<?> query = OqlBuilder.from(ExamSignup.class, "examSignup");
    // if(semester!=null||semester!=""||semester!="..."||category!=null||category!="..."||category!=""||subject!=null){
    if (semester != null) {
      query.where("examSignup.semester.id =:semester", semester);
    }
    if (category != null && subject != null) {
      query.where("examSignup.subject.category.id =:category", category);
      query.where("examSignup.subject.id =:subject", subject);
    } else {
      if (subject != null) {
        query.where("examSignup.subject.id =:subject", subject);
      }

    }
    query
        .select("examSignup.semester.schoolYear,examSignup.semester.name,examSignup.subject.name,examSignup.subject.category.name,"
            + "count(*),"
            + "sum(examSignup.feeOfOutline),"
            + "sum(examSignup.feeOfMaterial),"
            + "sum(examSignup.feeOfSignup),"
            + "examSignup.semester.id,"
            + "examSignup.subject.id");
    query
        .groupBy("examSignup.semester.schoolYear,examSignup.semester.name,examSignup.subject.name,examSignup.subject.category.name,examSignup.semester.id,examSignup.subject.id");
    populateConditions(query);
    Integer semesterId = getInt("semester.id");
    if (null != semesterId) {
      query.where("examSignup.semester.id=:semesterId", semesterId);
      getSemester();
    }
    query.limit(getPageLimit());
    query.orderBy(Order.parse(get("orderBy")));
    List<Object[]> rows = (List<Object[]>) entityDao.search(query);
    List<SignupStat> signupStatlist = CollectUtils.newArrayList(rows.size());
    Iterator<Object[]> iter = rows.iterator();
    Object[] signupObject;
    SignupStat signupStat;

    OqlBuilder<ExamSignupSetting> settingQuery = OqlBuilder
        .from(ExamSignupSetting.class, "setting");
    List<ExamSignupSetting> list2 = entityDao.search(settingQuery);
    while (iter.hasNext()) {
      signupObject = iter.next();
      signupStat = new SignupStat();

      ExamSignupSetting setting = null;
      for (int i1 = 0; i1 < list2.size(); i1++) {
        if (list2.get(i1).getConfig().getSemester().getId() == signupObject[8]
            && list2.get(i1).getSubject().getId() == signupObject[9]) {
          setting = list2.get(i1);
          break;
        }
      }
      if (setting == null) {
        setting = Model.newInstance(ExamSignupSetting.class);
        setting.setFeeOfMaterial(0.0);
        setting.setFeeOfOutline(0.0);
        setting.setFeeOfSignup(0.0);

      }

      signupStat.setSchoolYear(signupObject[0].toString());
      signupStat.setSemesterName(signupObject[1].toString());
      signupStat.setSubjectName(signupObject[2].toString());
      signupStat.setCategoryName(signupObject[3].toString());
      signupStat.setCount(signupObject[4].toString());
      signupStat.setSumOfOutline((Long) signupObject[4] * setting.getFeeOfOutline());
      signupStat.setSumOfMaterial((Long) signupObject[4] * setting.getFeeOfMaterial());
      signupStat.setSumOfSignup((Long) signupObject[4] * setting.getFeeOfSignup());
      signupStatlist.add(signupStat);
    }
    put("signupStats", signupStatlist);
    put("examSubjects", codeService.getCodes(ExamSubject.class));
    put("examCategories", codeService.getCodes(ExamCategory.class));
    put("examSignupConfigs", entityDao.getAll(ExamSignupConfig.class));
    put("semesters", entityDao.getAll(Semester.class));
    return forward("signupInfo");
  }

  // 统计实收报名费
  // @SuppressWarnings("unchecked")
  // public String signupInfo() {
  // Long semester = getLong("examSignup.semester.id");
  // Long subject = getLong("examSignup.subject.id");
  // Long category = getLong("examSignup.subject.category.id");
  // OqlBuilder<?> query = OqlBuilder.from(ExamSignup.class, "examSignup");
  // //
  // if(semester!=null||semester!=""||semester!="..."||category!=null||category!="..."||category!=""||subject!=null){
  // if (semester != null) {
  // query.where("examSignup.semester.id =:semester", semester);
  // }
  // if (category != null && subject != null) {
  // query.where("examSignup.subject.category.id =:category", category);
  // query.where("examSignup.subject.id =:subject", subject);
  // } else {
  // if (subject != null) {
  // query.where("examSignup.subject.id =:subject", subject);
  // }
  //
  // }
  // query.select("examSignup.semester.schoolYear,examSignup.semester.name,examSignup.subject.name,examSignup.subject.category.name,"
  // + "count(*),"
  // + "sum(examSignup.feeOfOutline),"
  // + "sum(examSignup.feeOfMaterial),"
  // + "sum(examSignup.feeOfSignup)");
  // query.groupBy("examSignup.semester.schoolYear,examSignup.semester.name,examSignup.subject.name,examSignup.subject.category.name");
  // populateConditions(query);
  // query.limit(getPageLimit());
  // query.orderBy(Order.parse(get("orderBy")));
  // List<Object[]> list = (List<Object[]>) entityDao.search(query);
  // List<SignupStat> signupStatlist = CollectUtils.newArrayList(list.size());
  // Iterator<Object[]> iter = list.iterator();
  // Object[] signupObject;
  // SignupStat signupStat;
  // while (iter.hasNext()) {
  // signupObject = iter.next();
  // signupStat = new SignupStat();
  // signupStat.setSchoolYear(signupObject[0].toString());
  // signupStat.setSemesterName(signupObject[1].toString());
  // signupStat.setSubjectName(signupObject[2].toString());
  // signupStat.setCategoryName(signupObject[3].toString());
  // signupStat.setCount(signupObject[4].toString());
  // signupStat.setSumOfOutline((Double) signupObject[5]);
  // signupStat.setSumOfMaterial((Double) signupObject[6]);
  // signupStat.setSumOfSignup((Double) signupObject[7]);
  // signupStatlist.add(signupStat);
  // }
  // put("signupStats", signupStatlist);
  // put("examSubjects", codeService.getCodes(ExamSubject.class));
  // put("examCategories", codeService.getCodes(ExamCategory.class));
  // put("examSignupConfigs", entityDao.get(ExamSignupConfig.class));
  // put("semesters", entityDao.get(Semester.class));
  // return forward();
  // }

  /*
   * @SuppressWarnings("unchecked")
   * public String statGrade() {
   * String grade = get("examSignup.std.grade");
   * Long subjectId = getLong("examSignup.subject.id");
   * Long categoryId = getLong("examSignup.subject.category.id");
   * // OqlBuilder<ExamSignup> query = OqlBuilder.from(ExamSignup.class,
   * // "examSignup");
   * OqlBuilder<?> stdQuery = OqlBuilder.from(Student.class, "std");
   * OqlBuilder<?> gradeQuery = OqlBuilder.from(ExternExamGrade.class, "grade");
   * if (Strings.isNotEmpty(grade)) {
   * stdQuery.where("std.grade like :grade", grade);
   * gradeQuery.where("grade.std.grade like :grade", grade);
   * }
   * stdQuery.select("std.department.id,count(*)");
   * stdQuery.groupBy("std.department.id");
   * stdQuery.where("std.department is not null");
   * gradeQuery.where("grade.std.department is not null");
   * gradeQuery.where("grade.passed is true");
   * List<ExamSubject> subjects = CollectUtils.newArrayList();
   * if (null != subjectId) {
   * gradeQuery.where("grade.subject.id=:subjectId", subjectId);
   * subjects.add(codeService.getCode(ExamSubject.class, subjectId));
   * } else {
   * OqlBuilder<ExamSubject> categoryQuery = OqlBuilder.from(ExamSubject.class,
   * "subject");
   * if (null != categoryId) {
   * categoryQuery.where("subject.category.id=:categoryId", categoryId);
   * gradeQuery.where("grade.subject.category.id=:categoryId", categoryId);
   * }
   * subjects = entityDao.search(categoryQuery);
   * }
   * put("subjects", subjects);
   * gradeQuery.select("grade.std.department.id,grade.subject.id,count(*)");
   * gradeQuery.groupBy("grade.std.department.id,grade.subject.id");
   * List<Object[]> datas = (List<Object[]>) entityDao.search(stdQuery);
   * new StatHelper(entityDao).replaceIdWith(datas, new Class[] { Department.class });
   * Map<String, Object> departMap = CollectUtils.newHashMap();
   * for (Object[] data : datas) {
   * if (data[0] != null) {
   * departMap.put(((Department) data[0]).getId().toString(), data);
   * } else {
   * departMap.put("", data);
   * }
   * }
   * List<Object[]> data2 = (List<Object[]>) entityDao.search(gradeQuery);
   * new StatHelper(entityDao).replaceIdWith(data2, new Class[] { Department.class,
   * ExamCategory.class });
   * List<StatGroup> statGroups = StatGroup.buildStatGroups(data2);
   * Map<String, StatGroup> gradeMap = CollectUtils.newHashMap();
   * for (StatGroup g : statGroups) {
   * gradeMap.put(((Department) g.getWhat()).getId().toString(), g);
   * }
   * put("departMap", departMap);
   * put("gradeMap", gradeMap);
   * return forward();
   * }
   */
  @SuppressWarnings("unchecked")
  public String statGrade() {
    String grade = get("examSignup.std.grade");
    Integer subjectId = getInt("examSignup.subject.id");
    Integer categoryId = getInt("examSignup.subject.category.id");
    // OqlBuilder<ExamSignup> query = OqlBuilder.from(ExamSignup.class,
    // "examSignup");
    OqlBuilder<?> stdQuery = OqlBuilder.from(Student.class, "std");
    OqlBuilder<?> gradeQuery = OqlBuilder.from(ExternExamGrade.class, "grade");
    if (Strings.isNotEmpty(grade)) {
      stdQuery.where("std.grade like :grade", grade);
      gradeQuery.where("grade.std.grade like :grade", grade);
    }
    stdQuery.select("std.department.id,count(*)");
    stdQuery.groupBy("std.department.id");
    stdQuery.where("std.department is not null");
    gradeQuery.where("grade.std.department is not null");
    gradeQuery.where("grade.passed is true");
    List<ExamSubject> subjects = CollectUtils.newArrayList();
    if (null != subjectId) {
      gradeQuery.where("grade.subject.id=:subjectId", subjectId);
      subjects.add(codeService.getCode(ExamSubject.class, subjectId));
    } else {
      OqlBuilder<ExamSubject> categoryQuery = OqlBuilder.from(ExamSubject.class, "subject");
      if (null != categoryId) {
        categoryQuery.where("subject.category.id=:categoryId", categoryId);
        gradeQuery.where("grade.subject.category.id=:categoryId", categoryId);
      }
      subjects = entityDao.search(categoryQuery);
    }
    put("subjects", subjects);
    gradeQuery.select("grade.std.department.id,grade.subject.id,count(*)");
    gradeQuery.groupBy("grade.std.department.id,grade.subject.id");
    List<Object[]> datas = (List<Object[]>) entityDao.search(stdQuery);
    new StatHelper(entityDao).replaceIdWith(datas, new Class[] { Department.class });
    Map<String, Object> departMap = CollectUtils.newHashMap();
    for (Object[] data : datas) {
      if (data[0] != null) {
        departMap.put(((Department) data[0]).getId().toString(), data);
      } else {
        departMap.put("", data);
      }
    }
    List<Object[]> data2 = (List<Object[]>) entityDao.search(gradeQuery);
    new StatHelper(entityDao).replaceIdWith(data2, new Class[] { Department.class, ExamSubject.class });
    List<StatGroup> statGroups = StatGroup.buildStatGroups(data2);
    Map<String, StatGroup> gradeMap = CollectUtils.newHashMap();
    for (StatGroup g : statGroups) {
      gradeMap.put(((Department) g.getWhat()).getId().toString(), g);
    }
    put("departMap", departMap);
    put("gradeMap", gradeMap);
    return forward();
  }

  @SuppressWarnings("unchecked")
  public String passRate() {
    Integer semesterId = getInt("examSignup.semester.id");
    Integer subjectId = getInt("examSignup.subject.id");
    Integer categoryId = getInt("examSignup.subject.category.id");

    OqlBuilder<?> query = OqlBuilder.from(ExamSignup.class, "examSignup");
    OqlBuilder<?> gradeQuery = OqlBuilder.from(ExternExamGrade.class, "grade");
    OqlBuilder<?> gdQuery = OqlBuilder.from(ExternExamGrade.class, "grade");
    if (null != semesterId) {
      gradeQuery.where("grade.semester.id=:semesterId", semesterId);
      gdQuery.where("grade.semester.id=:semesterId", semesterId);
      query.where("examSignup.semester.id=:semesterId", semesterId);
    }
    if (null != categoryId) {
      gradeQuery.where("grade.subject.category.id=:categoryId", categoryId);
      gdQuery.where("grade.subject.category.id=:categoryId", categoryId);
      query.where("examSignup.subject.category.id=:categoryId", categoryId);
    }
    if (null != subjectId) {
      gradeQuery.where("grade.subject.id=:subjectId", subjectId);
      gdQuery.where("grade.subject.id=:subjectId", subjectId);
      query.where("examSignup.subject.id=:subjectId", subjectId);
    }
    gradeQuery.select("grade.semester.id,count(*)");
    gradeQuery.groupBy("grade.semester.id");
    List<Object[]> datas = (List<Object[]>) entityDao.search(gradeQuery);
    new StatHelper(entityDao).replaceIdWith(datas, new Class[] { Semester.class });
    Map<String, Object[]> SemesterMap = new HashMap<String, Object[]>();
    for (Object[] data : datas) {
      SemesterMap.put(((Semester) data[0]).getId().toString(), data);
    }
    query
        .where("exists(select signup.std.id from "
            + ExamSignup.class.getName()
            + " signup where signup.std.id = examSignup.std.id group by signup.std.id having count(signup.std.id)=1)");
    query.select("examSignup.semester.id,count(*)");
    query.groupBy("examSignup.semester.id");
    List<Object[]> data5 = (List<Object[]>) entityDao.search(query);
    new StatHelper(entityDao).replaceIdWith(data5, new Class[] { Semester.class });
    Map<String, Object[]> firstMap = new HashMap<String, Object[]>();
    for (Object[] data : data5) {
      firstMap.put(((Semester) data[0]).getId().toString(), data);
    }

    gradeQuery.where("grade.passed=true");
    List<Object[]> data2 = (List<Object[]>) entityDao.search(gradeQuery);
    new StatHelper(entityDao).replaceIdWith(data2, new Class[] { Semester.class });
    Map<String, Object[]> gradeMap = new HashMap<String, Object[]>();
    for (Object[] data : data2) {
      gradeMap.put(((Semester) data[0]).getId().toString(), data);
    }
    gdQuery.where("exists(select gd.std.id from " + ExternExamGrade.class.getName()
        + " gd where gd.std.id = grade.std.id group by gd.std.id having count(gd.std.id)=1)");
    gdQuery.select("grade.semester.id,count(*)");
    gdQuery.groupBy("grade.semester.id");
    List<Object[]> data3 = (List<Object[]>) entityDao.search(gdQuery);
    new StatHelper(entityDao).replaceIdWith(data3, new Class[] { Semester.class });
    Map<String, Object[]> firstTMap = new HashMap<String, Object[]>();
    for (Object[] data : data3) {
      firstTMap.put(((Semester) data[0]).getId().toString(), data);
    }

    gdQuery.where("grade.passed=true");
    List<Object[]> data4 = (List<Object[]>) entityDao.search(gdQuery);
    new StatHelper(entityDao).replaceIdWith(data4, new Class[] { Semester.class });
    Map<String, Object[]> TFirstMap = new HashMap<String, Object[]>();
    for (Object[] data : data4) {
      TFirstMap.put(((Semester) data[0]).getId().toString(), data);
    }

    put("TFirst", TFirstMap);// 第一次参考及格率
    put("first", firstMap);// 第一次参考(报名)
    put("firstT", firstTMap);// 第一次通过
    put("SemesterMap", SemesterMap);
    put("gradeMap", gradeMap);
    return forward();
  }

  // 查询科目
  public String categorySubject() {
    Integer categoryId = getIntId("category");
    if (null != categoryId) {
      OqlBuilder<ExamSubject> query = OqlBuilder.from(ExamSubject.class, "examSubject");
      query.where("examSubject.category.id =:categoryId", categoryId);
      query
          .where(
              "examSubject.beginOn <= :now and (examSubject.endOn is null or examSubject.endOn >= :now)",
              new java.util.Date());
      put("subjects", entityDao.search(query));
    } else {
      put("subjects", CollectionUtils.EMPTY_COLLECTION);
    }
    return forward();
  }
}
