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

import org.beangle.commons.conversion.converter.String2DateConverter;
import org.beangle.commons.dao.EntityDao;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Numbers;
import org.beangle.commons.transfer.TransferResult;
import org.beangle.commons.transfer.importer.listener.ItemImporterListener;
import org.openurp.base.model.Semester;
import org.openurp.edu.base.model.Project;
import org.openurp.edu.base.service.SemesterService;
import org.openurp.edu.extern.model.ExternExamGrade;

/**
 * 成绩导入监听器,实现全部数据导入的完整性。<br>
 * 依照学生、学期和考试类型作为唯一标识
 * 
 * @author chaostone
 */
public class ExternExamGradeImportListener extends ItemImporterListener {

  private EntityDao entityDao;

  private Project project;

  private SemesterService semesterService;

  public ExternExamGradeImportListener() {
    super();
  }

  public ExternExamGradeImportListener(EntityDao entityDao, Project project,
      SemesterService semesterService) {
    super();
    this.entityDao = entityDao;
    this.project = project;
    this.semesterService = semesterService;
  }

  @Override
  public void onItemStart(TransferResult tr) {
    tr.getMsgs().addAll(tr.getErrs());
    tr.getErrs().clear();
    Map<String, Object> datas = importer.getCurData();
    String examOn = (String) datas.get("examGrade.examOn");
    if (null != examOn) {
      datas.put("examGrade.examOn",
          new String2DateConverter().convert(examOn, java.lang.String.class, java.sql.Date.class));
    }
    String stdCode = (String) datas.get("examGrade.std.user.code");
    String semesterSchoolYear = (String) datas.get("examGrade.semester.schoolYear");
    String semesterName = (String) datas.get("examGrade.semester.name");
    String subjectName = (String) datas.get("examGrade.subject.name");
    OqlBuilder<ExternExamGrade> builder = OqlBuilder.from(ExternExamGrade.class, "examGrade");
    builder.where("examGrade.std.user.code = :stdCode", stdCode);
    builder.where("examGrade.semester.schoolYear = :semesterSchoolYear", semesterSchoolYear);
    builder.where("examGrade.semester.name = :semesterName", semesterName);
    builder.where("examGrade.subject.name = :subjectName", subjectName);
    List<ExternExamGrade> examGrades = entityDao.search(builder);

    Map currentMap = (Map) importer.getCurrent();
    if (examGrades.size() > 0) {
      currentMap.put("examGrade", examGrades.get(0));
    }
  }

  private Semester getSemester(java.sql.Date examOn, Project project) {
    return semesterService.getSemester(project.getCalendars().get(0), examOn);
  }

  @Override
  public void onItemFinish(TransferResult tr) {
    Map datas = (Map) importer.getCurrent();
    ExternExamGrade examGrade = (ExternExamGrade) datas.get("examGrade");
    if (examGrade.isTransient()) {
      java.sql.Date examOn = (java.sql.Date) importer.getCurData().get("examGrade.examOn");
      examGrade.setSemester(getSemester(examOn, project));
      if (null == examGrade.getExamNo()) {
        examGrade.setExamOn(examOn);
      }
    }
    if (null == examGrade.getScore() && Numbers.isNumber(examGrade.getScoreText())) {
      examGrade.setScore(Numbers.toFloat(examGrade.getScoreText()));
    }
    if (examGradeVilidate(examGrade, tr)) {
      examGrade.setUpdatedAt(new java.util.Date(System.currentTimeMillis()));
      entityDao.saveOrUpdate(examGrade);
    }
  }

  private boolean examGradeVilidate(ExternExamGrade examGrade, TransferResult tr) {
    boolean bool = true;
    // 验证学号
    if (null == examGrade.getStd()) {
      tr.addFailure("学号不能为空", "");
      bool = false;
    }

    // 考试科目
    if (null == examGrade.getSubject()) {
      tr.addFailure("考试科目不能为空", "");
      bool = false;
    }

    // 验证分数
    if (null == examGrade.getScoreText()) {
      tr.addFailure("分数不能为空", "");
      bool = false;
    } else {
      if (null != examGrade.getScore()) {
        float score = examGrade.getScore();
        if (score < 0) {
          tr.addFailure("分数不能小于0", examGrade.getScore());
          bool = false;
        }
      }
    }

    // 验证学年度-学期
    if (null == examGrade.getSemester()) {
      tr.addFailure("查询不到学年度学期", "");
      bool = false;
    }

    // 验证成绩记录方式
    if (null == examGrade.getMarkStyle()) {
      tr.addFailure("成绩记录方式不能为空", "");
      bool = false;
    }

    String passed = (String) importer.getCurData().get("examGrade.passed");
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
