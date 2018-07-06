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
package org.openurp.edu.extern.service.listener;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.beangle.commons.dao.EntityDao;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.metadata.Model;
import org.beangle.commons.transfer.TransferResult;
import org.beangle.commons.transfer.importer.listener.ItemImporterListener;
import org.openurp.base.model.Semester;
import org.openurp.edu.extern.model.ExamSignup;

public class ExamSignupImportListener extends ItemImporterListener {

  private EntityDao entityDao;

  public ExamSignupImportListener() {
    super();
  }

  public ExamSignupImportListener(EntityDao entityDao) {
    super();
    this.entityDao = entityDao;
  }

  public void onFinish(TransferResult tr) {
  }

  public void onItemFinish(TransferResult tr) {
    Map datas = (Map) importer.getCurrent();

    ExamSignup examSignup = (ExamSignup) datas.get("exam");
    examSignup.setSemester(getSemester(examSignup.getSemester()));

    if (validateDatas(examSignup, tr)) {

      ExamSignup signup = getExamSignup(examSignup);
      if (signup == null) {
        signup = Model.newInstance(ExamSignup.class);
        signup.setCampus(examSignup.getCampus());
        signup.setSemester(getSemester(examSignup.getSemester()));
        signup.setSubject(examSignup.getSubject());
        signup.setStd(examSignup.getStd());
      }
      signup.setSignupAt(new Date());
      entityDao.saveOrUpdate(signup);
    }

  }

  private boolean validateDatas(ExamSignup signup, TransferResult tr) {
    Boolean bool = true;
    if (signup.getStd() == null) {
      tr.addFailure("学号有误!", "");
      bool = false;
    } else if (signup.getCampus() == null) {
      tr.addFailure("校区代码有误!", "");
      bool = false;
    } else if (signup.getSemester() == null) {
      tr.addFailure("学年学期有误!", "");
      bool = false;
    } else if (signup.getSubject() == null) {
      tr.addFailure("科目代码有误!", "");
      bool = false;
    }
    return bool;
  }

  private Semester getSemester(Semester semester) {
    OqlBuilder<Semester> query = OqlBuilder.from(Semester.class, "semester");
    query.where("semester.name =:name", semester.getName());
    query.where("semester.schoolYear =:schoolYear", semester.getSchoolYear());
    List<Semester> semesters = entityDao.search(query);
    return semesters.size() > 0 ? semesters.get(0) : null;
  }

  private ExamSignup getExamSignup(ExamSignup signup) {
    OqlBuilder<ExamSignup> query = OqlBuilder.from(ExamSignup.class, "signup");
    query.where("signup.std =:std", signup.getStd());
    query.where("signup.semester =:semester", signup.getSemester());
    query.where("signup.subject =:subject", signup.getSubject());
    List<ExamSignup> examSignups = entityDao.search(query);
    return examSignups.size() > 0 ? examSignups.get(0) : null;
  }

  public EntityDao getEntityDao() {
    return entityDao;
  }

  public void setEntityDao(EntityDao entityDao) {
    this.entityDao = entityDao;
  }
}
