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
package org.openurp.edu.extern.service;

import java.util.List;
import java.util.Map;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.EntityDao;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.transfer.TransferResult;
import org.beangle.commons.transfer.importer.listener.ItemImporterListener;
import org.openurp.base.model.Semester;
import org.openurp.edu.base.model.Project;
import org.openurp.edu.grade.course.service.GradeRateService;
import org.openurp.edu.extern.model.ExamGrade;

/**
 * 成绩导入监听器,实现全部数据导入的完整性。<br>
 * 依照学生、学期和考试类型作为唯一标识
 * 
 * @author chaostone
 */
public class ExamGradeImportListener extends ItemImporterListener {

  private EntityDao entityDao;

  private Project project;

  protected GradeRateService gradeRateService;

  public ExamGradeImportListener() {
    super();
  }

  public ExamGradeImportListener(EntityDao entityDao, Project project, GradeRateService gradeRateService) {
    super();
    this.entityDao = entityDao;
    this.project = project;
    this.gradeRateService = gradeRateService;
  }

  @Override
  public void onItemStart(TransferResult tr) {
    tr.getMsgs().addAll(tr.getErrs());
    tr.getErrs().clear();
    Map<String, Object> datas = importer.getCurData();
    String stdCode = (String) datas.get("otherGrade.std.code");
    String semesterSchoolYear = (String) datas.get("otherGrade.semester.schoolYear");
    String semesterName = (String) datas.get("otherGrade.semester.name");
    String subjectName = (String) datas.get("otherGrade.subject.name");
    OqlBuilder<ExamGrade> builder = OqlBuilder.from(ExamGrade.class, "otherGrade");
    builder.where("otherGrade.std.code = :stdCode", stdCode);
    builder.where("otherGrade.semester.schoolYear = :semesterSchoolYear", semesterSchoolYear);
    builder.where("otherGrade.semester.name = :semesterName", semesterName);
    builder.where("otherGrade.subject.name = :subjectName", subjectName);
    List<ExamGrade> otherGrades = entityDao.search(builder);

    Map currentMap = (Map) importer.getCurrent();
    if (otherGrades.size() > 0) {
      currentMap.put("otherGrade", otherGrades.get(0));
    }
  }

  private Semester getSemester(String schoolYear, String term, Project project) {
    OqlBuilder<Semester> query = OqlBuilder.from(Semester.class, "semester");
    query
        .where("semester.schoolYear = :sy", schoolYear)
        .where("semester.name = :term", term)
        .where(
            "exists(from " + Project.class.getName()
                + " p where p.calendar=semester.calendar and p = :project)", project).cacheable();
    List<Semester> semesters = entityDao.search(query);
    if (CollectUtils.isNotEmpty(semesters)) { return semesters.get(0); }
    return null;
  }

  @Override
  public void onItemFinish(TransferResult tr) {
    Map datas = (Map) importer.getCurrent();
    ExamGrade otherGrade = (ExamGrade) datas.get("otherGrade");
    if (otherGrade.isTransient()) {
      String semesterSchoolYear = (String) importer.getCurData().get("otherGrade.semester.schoolYear");
      String semesterName = (String) importer.getCurData().get("otherGrade.semester.name");
      otherGrade.setSemester(getSemester(semesterSchoolYear, semesterName, project));
    }
    if (otherGradeVilidate(otherGrade, tr)) {
//      if (otherGrade.isTransient()) {
//        otherGrade.setCreatedAt(new Date(System.currentTimeMillis()));
//      }
//      otherGrade.setUpdatedAt(new Date(System.currentTimeMillis()));
      otherGrade.setScoreText(gradeRateService.getConverter(project, otherGrade.getMarkStyle()).convert(otherGrade.getScore()));
      entityDao.saveOrUpdate(otherGrade);
    }
  }

  private boolean otherGradeVilidate(ExamGrade otherGrade, TransferResult tr) {
    boolean bool = true;
    // 验证学号
    if (null == otherGrade.getStd()) {
      tr.addFailure("学号不能为空", "");
      bool = false;
    }

    // 考试科目
    if (null == otherGrade.getSubject()) {
      tr.addFailure("考试科目不能为空", "");
      bool = false;
    }

    // 验证分数
    if (null == otherGrade.getScore()) {
      tr.addFailure("分数不能为空", "");
      bool = false;
    } else {
      float score = otherGrade.getScore();
      if (score < 0) {
        tr.addFailure("分数不能小于0", otherGrade.getScore());
        bool = false;
      }
    }

    // 验证学年度-学期
    if (null == otherGrade.getSemester()) {
      tr.addFailure("查询不到学年度学期", "");
      bool = false;
    }

    // 验证成绩记录方式
    if (null == otherGrade.getMarkStyle()) {
      tr.addFailure("成绩记录方式不能为空", "");
      bool = false;
    }

    String passed = (String) importer.getCurData().get("otherGrade.passed");
    if (null == passed) {
      tr.addFailure("是否合格不能为空", "");
      bool = false;
    } else {
      if (!"1".equals(passed) && !"0".equals(passed)) {
        tr.addFailure("是否合格格式非法,1代表合格,0代表不合格", passed);
        bool = false;
      }
    }
    return bool;
  }

}
