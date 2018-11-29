/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright © 2014, The OpenURP Software.
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
package org.openurp.edu.extern.service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.beangle.commons.dao.EntityDao;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Numbers;
import org.beangle.commons.transfer.TransferResult;
import org.beangle.commons.transfer.importer.listener.ItemImporterListener;
import org.openurp.edu.base.model.Project;
import org.openurp.edu.base.model.Student;
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

  public ExternExamGradeImportListener() {
    super();
  }

  public ExternExamGradeImportListener(EntityDao entityDao, Project project) {
    super();
    this.entityDao = entityDao;
    this.project = project;
  }

  @Override
  public void onItemStart(TransferResult tr) {
    tr.getMsgs().addAll(tr.getErrs());
    tr.getErrs().clear();
    Map<String, Object> datas = importer.getCurData();
    String acquiredOnValue = (String) datas.get("examGrade.acquiredOn");
    Date acquiredOn = null;
    if (StringUtils.isBlank(acquiredOnValue)) {
      tr.addFailure("考试日期不能为空", "");
      return;
    } else {
      try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date value = new Date(sdf.parse(acquiredOnValue).getTime());
        if (StringUtils.equals(sdf.format(value), acquiredOnValue)) {
          acquiredOn = (Date) value;
        } else {
          throw new IllegalArgumentException(acquiredOnValue
              + " of value is invalid in 'examGrade.acquiredOn'!");
        }
      } catch (Exception e) {
        tr.addFailure("考试日期无效", acquiredOnValue);
        return;
      }
    }
    datas.put("examGrade.acquiredOn", acquiredOn);

    String stdCode = (String) datas.get("examGrade.std.user.code");
    OqlBuilder<Student> stdQuery = OqlBuilder.from(Student.class, "s").where(
        "s.user.code=:code and s.project=:project", stdCode, project);

    List<Student> stds = entityDao.search(stdQuery);
    if (stds.isEmpty()) {
      tr.addFailure("学号有误", "");
      return;
    }
    Student std = stds.get(0);
    datas.remove("examGrade.std.user.code");
    datas.put("examGrade.std", std);
    String subjectName = (String) datas.get("examGrade.subject.name");
    OqlBuilder<ExternExamGrade> builder = OqlBuilder.from(ExternExamGrade.class, "examGrade");
    builder.where("examGrade.std = :std", std);
    builder.where("examGrade.acquiredOn=:acquiredOn", acquiredOn);
    builder.where("examGrade.subject.name = :subjectName", subjectName);
    List<ExternExamGrade> examGrades = entityDao.search(builder);

    Map currentMap = (Map) importer.getCurrent();
    if (examGrades.size() > 0) {
      currentMap.put("examGrade", examGrades.get(0));
    }
  }

  @Override
  public void onItemFinish(TransferResult tr) {
    Map datas = (Map) importer.getCurrent();
    ExternExamGrade examGrade = (ExternExamGrade) datas.get("examGrade");
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
      if (Numbers.isNumber(examGrade.getScoreText())) {
        examGrade.setScore(Numbers.toFloat(examGrade.getScoreText()));
      }
    }

    // 验证成绩记录方式
    if (null == examGrade.getGradingMode()) {
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
