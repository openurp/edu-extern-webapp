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
package org.openurp.edu.other.signup.web.action;

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
import org.openurp.edu.other.code.model.OtherExamCategory;
import org.openurp.edu.other.code.model.OtherExamSubject;
import org.openurp.edu.other.model.OtherExamSignUp;
import org.openurp.edu.other.model.OtherExamSignUpConfig;
import org.openurp.edu.other.model.OtherExamSignUpSetting;
import org.openurp.edu.other.model.OtherGrade;
import org.openurp.edu.other.model.SignUpStat;
import org.openurp.edu.web.action.SemesterSupportAction;

public class StatAction extends SemesterSupportAction {

  @Override
  protected String getEntityName() {
    return OtherExamSignUp.class.getName();
  }

  @Override
  protected void indexSetting() {
    put("otherExamSubjects", codeService.getCodes(OtherExamSubject.class));
    put("otherExamCategories", codeService.getCodes(OtherExamCategory.class));
    getSemester();
  }

  public String search() {
    return forward(new Action("signUpInfo"));
  }

  /**
   * 统计应收报名费
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public String signUpInfo() {
    Long semester = getLong("otherExamSignUp.semester.id");
    Integer subject = getInt("otherExamSignUp.subject.id");
    Integer category = getInt("otherExamSignUp.subject.category.id");
    OqlBuilder<?> query = OqlBuilder.from(OtherExamSignUp.class, "otherExamSignUp");
    // if(semester!=null||semester!=""||semester!="..."||category!=null||category!="..."||category!=""||subject!=null){
    if (semester != null) {
      query.where("otherExamSignUp.semester.id =:semester", semester);
    }
    if (category != null && subject != null) {
      query.where("otherExamSignUp.subject.category.id =:category", category);
      query.where("otherExamSignUp.subject.id =:subject", subject);
    } else {
      if (subject != null) {
        query.where("otherExamSignUp.subject.id =:subject", subject);
      }

    }
    query
        .select("otherExamSignUp.semester.schoolYear,otherExamSignUp.semester.name,otherExamSignUp.subject.name,otherExamSignUp.subject.category.name,"
            + "count(*),"
            + "sum(otherExamSignUp.feeOfOutline),"
            + "sum(otherExamSignUp.feeOfMaterial),"
            + "sum(otherExamSignUp.feeOfSignUp),"
            + "otherExamSignUp.semester.id,"
            + "otherExamSignUp.subject.id");
    query
        .groupBy("otherExamSignUp.semester.schoolYear,otherExamSignUp.semester.name,otherExamSignUp.subject.name,otherExamSignUp.subject.category.name,otherExamSignUp.semester.id,otherExamSignUp.subject.id");
    populateConditions(query);
    Integer semesterId = getInt("semester.id");
    if (null != semesterId) {
      query.where("otherExamSignUp.semester.id=:semesterId", semesterId);
      getSemester();
    }
    query.limit(getPageLimit());
    query.orderBy(Order.parse(get("orderBy")));
    List<Object[]> rows = (List<Object[]>) entityDao.search(query);
    List<SignUpStat> signUpStatlist = CollectUtils.newArrayList(rows.size());
    Iterator<Object[]> iter = rows.iterator();
    Object[] signUpObject;
    SignUpStat signUpStat;

    OqlBuilder<OtherExamSignUpSetting> settingQuery = OqlBuilder
        .from(OtherExamSignUpSetting.class, "setting");
    List<OtherExamSignUpSetting> list2 = entityDao.search(settingQuery);
    while (iter.hasNext()) {
      signUpObject = iter.next();
      signUpStat = new SignUpStat();

      OtherExamSignUpSetting setting = null;
      for (int i1 = 0; i1 < list2.size(); i1++) {
        if (list2.get(i1).getConfig().getSemester().getId() == signUpObject[8]
            && list2.get(i1).getSubject().getId() == signUpObject[9]) {
          setting = list2.get(i1);
          break;
        }
      }
      if (setting == null) {
        setting = Model.newInstance(OtherExamSignUpSetting.class);
        setting.setFeeOfMaterial(0.0);
        setting.setFeeOfOutline(0.0);
        setting.setFeeOfSignUp(0.0);

      }

      signUpStat.setSchoolYear(signUpObject[0].toString());
      signUpStat.setSemesterName(signUpObject[1].toString());
      signUpStat.setSubjectName(signUpObject[2].toString());
      signUpStat.setCategoryName(signUpObject[3].toString());
      signUpStat.setCount(signUpObject[4].toString());
      signUpStat.setSumOfOutline((Long) signUpObject[4] * setting.getFeeOfOutline());
      signUpStat.setSumOfMaterial((Long) signUpObject[4] * setting.getFeeOfMaterial());
      signUpStat.setSumOfSignUp((Long) signUpObject[4] * setting.getFeeOfSignUp());
      signUpStatlist.add(signUpStat);
    }
    put("signUpStats", signUpStatlist);
    put("otherExamSubjects", codeService.getCodes(OtherExamSubject.class));
    put("otherExamCategories", codeService.getCodes(OtherExamCategory.class));
    put("otherExamSignUpConfigs", entityDao.getAll(OtherExamSignUpConfig.class));
    put("semesters", entityDao.getAll(Semester.class));
    return forward("signUpInfo");
  }

  // 统计实收报名费
  // @SuppressWarnings("unchecked")
  // public String signUpInfo() {
  // Long semester = getLong("otherExamSignUp.semester.id");
  // Long subject = getLong("otherExamSignUp.subject.id");
  // Long category = getLong("otherExamSignUp.subject.category.id");
  // OqlBuilder<?> query = OqlBuilder.from(OtherExamSignUp.class, "otherExamSignUp");
  // //
  // if(semester!=null||semester!=""||semester!="..."||category!=null||category!="..."||category!=""||subject!=null){
  // if (semester != null) {
  // query.where("otherExamSignUp.semester.id =:semester", semester);
  // }
  // if (category != null && subject != null) {
  // query.where("otherExamSignUp.subject.category.id =:category", category);
  // query.where("otherExamSignUp.subject.id =:subject", subject);
  // } else {
  // if (subject != null) {
  // query.where("otherExamSignUp.subject.id =:subject", subject);
  // }
  //
  // }
  // query.select("otherExamSignUp.semester.schoolYear,otherExamSignUp.semester.name,otherExamSignUp.subject.name,otherExamSignUp.subject.category.name,"
  // + "count(*),"
  // + "sum(otherExamSignUp.feeOfOutline),"
  // + "sum(otherExamSignUp.feeOfMaterial),"
  // + "sum(otherExamSignUp.feeOfSignUp)");
  // query.groupBy("otherExamSignUp.semester.schoolYear,otherExamSignUp.semester.name,otherExamSignUp.subject.name,otherExamSignUp.subject.category.name");
  // populateConditions(query);
  // query.limit(getPageLimit());
  // query.orderBy(Order.parse(get("orderBy")));
  // List<Object[]> list = (List<Object[]>) entityDao.search(query);
  // List<SignUpStat> signUpStatlist = CollectUtils.newArrayList(list.size());
  // Iterator<Object[]> iter = list.iterator();
  // Object[] signUpObject;
  // SignUpStat signUpStat;
  // while (iter.hasNext()) {
  // signUpObject = iter.next();
  // signUpStat = new SignUpStat();
  // signUpStat.setSchoolYear(signUpObject[0].toString());
  // signUpStat.setSemesterName(signUpObject[1].toString());
  // signUpStat.setSubjectName(signUpObject[2].toString());
  // signUpStat.setCategoryName(signUpObject[3].toString());
  // signUpStat.setCount(signUpObject[4].toString());
  // signUpStat.setSumOfOutline((Double) signUpObject[5]);
  // signUpStat.setSumOfMaterial((Double) signUpObject[6]);
  // signUpStat.setSumOfSignUp((Double) signUpObject[7]);
  // signUpStatlist.add(signUpStat);
  // }
  // put("signUpStats", signUpStatlist);
  // put("otherExamSubjects", codeService.getCodes(OtherExamSubject.class));
  // put("otherExamCategories", codeService.getCodes(OtherExamCategory.class));
  // put("otherExamSignUpConfigs", entityDao.get(OtherExamSignUpConfig.class));
  // put("semesters", entityDao.get(Semester.class));
  // return forward();
  // }

  /*
   * @SuppressWarnings("unchecked")
   * public String statGrade() {
   * String grade = get("otherExamSignUp.std.grade");
   * Long subjectId = getLong("otherExamSignUp.subject.id");
   * Long categoryId = getLong("otherExamSignUp.subject.category.id");
   * // OqlBuilder<OtherExamSignUp> query = OqlBuilder.from(OtherExamSignUp.class,
   * // "otherExamSignUp");
   * OqlBuilder<?> stdQuery = OqlBuilder.from(Student.class, "std");
   * OqlBuilder<?> gradeQuery = OqlBuilder.from(OtherGrade.class, "grade");
   * if (Strings.isNotEmpty(grade)) {
   * stdQuery.where("std.grade like :grade", grade);
   * gradeQuery.where("grade.std.grade like :grade", grade);
   * }
   * stdQuery.select("std.department.id,count(*)");
   * stdQuery.groupBy("std.department.id");
   * stdQuery.where("std.department is not null");
   * gradeQuery.where("grade.std.department is not null");
   * gradeQuery.where("grade.passed is true");
   * List<OtherExamSubject> subjects = CollectUtils.newArrayList();
   * if (null != subjectId) {
   * gradeQuery.where("grade.subject.id=:subjectId", subjectId);
   * subjects.add(codeService.getCode(OtherExamSubject.class, subjectId));
   * } else {
   * OqlBuilder<OtherExamSubject> categoryQuery = OqlBuilder.from(OtherExamSubject.class,
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
   * OtherExamCategory.class });
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
    String grade = get("otherExamSignUp.std.grade");
    Integer subjectId = getInt("otherExamSignUp.subject.id");
    Integer categoryId = getInt("otherExamSignUp.subject.category.id");
    // OqlBuilder<OtherExamSignUp> query = OqlBuilder.from(OtherExamSignUp.class,
    // "otherExamSignUp");
    OqlBuilder<?> stdQuery = OqlBuilder.from(Student.class, "std");
    OqlBuilder<?> gradeQuery = OqlBuilder.from(OtherGrade.class, "grade");
    if (Strings.isNotEmpty(grade)) {
      stdQuery.where("std.grade like :grade", grade);
      gradeQuery.where("grade.std.grade like :grade", grade);
    }
    stdQuery.select("std.department.id,count(*)");
    stdQuery.groupBy("std.department.id");
    stdQuery.where("std.department is not null");
    gradeQuery.where("grade.std.department is not null");
    gradeQuery.where("grade.passed is true");
    List<OtherExamSubject> subjects = CollectUtils.newArrayList();
    if (null != subjectId) {
      gradeQuery.where("grade.subject.id=:subjectId", subjectId);
      subjects.add(codeService.getCode(OtherExamSubject.class, subjectId));
    } else {
      OqlBuilder<OtherExamSubject> categoryQuery = OqlBuilder.from(OtherExamSubject.class, "subject");
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
    new StatHelper(entityDao).replaceIdWith(data2, new Class[] { Department.class, OtherExamSubject.class });
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
    Integer semesterId = getInt("otherExamSignUp.semester.id");
    Integer subjectId = getInt("otherExamSignUp.subject.id");
    Integer categoryId = getInt("otherExamSignUp.subject.category.id");

    OqlBuilder<?> query = OqlBuilder.from(OtherExamSignUp.class, "otherExamSignUp");
    OqlBuilder<?> gradeQuery = OqlBuilder.from(OtherGrade.class, "grade");
    OqlBuilder<?> gdQuery = OqlBuilder.from(OtherGrade.class, "grade");
    if (null != semesterId) {
      gradeQuery.where("grade.semester.id=:semesterId", semesterId);
      gdQuery.where("grade.semester.id=:semesterId", semesterId);
      query.where("otherExamSignUp.semester.id=:semesterId", semesterId);
    }
    if (null != categoryId) {
      gradeQuery.where("grade.subject.category.id=:categoryId", categoryId);
      gdQuery.where("grade.subject.category.id=:categoryId", categoryId);
      query.where("otherExamSignUp.subject.category.id=:categoryId", categoryId);
    }
    if (null != subjectId) {
      gradeQuery.where("grade.subject.id=:subjectId", subjectId);
      gdQuery.where("grade.subject.id=:subjectId", subjectId);
      query.where("otherExamSignUp.subject.id=:subjectId", subjectId);
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
        .where("exists(select signUp.std.id from "
            + OtherExamSignUp.class.getName()
            + " signUp where signUp.std.id = otherExamSignUp.std.id group by signUp.std.id having count(signUp.std.id)=1)");
    query.select("otherExamSignUp.semester.id,count(*)");
    query.groupBy("otherExamSignUp.semester.id");
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
    gdQuery.where("exists(select gd.std.id from " + OtherGrade.class.getName()
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
      OqlBuilder<OtherExamSubject> query = OqlBuilder.from(OtherExamSubject.class, "otherExamSubject");
      query.where("otherExamSubject.category.id =:categoryId", categoryId);
      query
          .where(
              "otherExamSubject.beginOn <= :now and (otherExamSubject.endOn is null or otherExamSubject.endOn >= :now)",
              new java.util.Date());
      put("subjects", entityDao.search(query));
    } else {
      put("subjects", CollectionUtils.EMPTY_COLLECTION);
    }
    return forward();
  }
}
