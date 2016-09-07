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
package org.openurp.edu.other.service.listener;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.beangle.commons.dao.EntityDao;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.metadata.Model;
import org.beangle.commons.transfer.TransferResult;
import org.beangle.commons.transfer.importer.listener.ItemImporterListener;
import org.openurp.base.model.Semester;
import org.openurp.edu.other.model.OtherExamSignUp;

public class OtherExamSignUpImportListener extends ItemImporterListener {

  private EntityDao entityDao;

  public OtherExamSignUpImportListener() {
    super();
  }

  public OtherExamSignUpImportListener(EntityDao entityDao) {
    super();
    this.entityDao = entityDao;
  }

  public void onFinish(TransferResult tr) {
  }

  public void onItemFinish(TransferResult tr) {
    Map datas = (Map) importer.getCurrent();

    OtherExamSignUp otherExamSignUp = (OtherExamSignUp) datas.get("otherExam");
    otherExamSignUp.setSemester(getSemester(otherExamSignUp.getSemester()));

    if (validateDatas(otherExamSignUp, tr)) {

      OtherExamSignUp signUp = getOtherExamSignUp(otherExamSignUp);
      if (signUp == null) {
        signUp = Model.newInstance(OtherExamSignUp.class);
        signUp.setCampus(otherExamSignUp.getCampus());
        signUp.setSemester(getSemester(otherExamSignUp.getSemester()));
        signUp.setSubject(otherExamSignUp.getSubject());
        signUp.setStd(otherExamSignUp.getStd());
      }
      signUp.setSignUpAt(new Date());
      entityDao.saveOrUpdate(signUp);
    }

  }

  private boolean validateDatas(OtherExamSignUp signUp, TransferResult tr) {
    Boolean bool = true;
    if (signUp.getStd() == null) {
      tr.addFailure("学号有误!", "");
      bool = false;
    } else if (signUp.getCampus() == null) {
      tr.addFailure("校区代码有误!", "");
      bool = false;
    } else if (signUp.getSemester() == null) {
      tr.addFailure("学年学期有误!", "");
      bool = false;
    } else if (signUp.getSubject() == null) {
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

  private OtherExamSignUp getOtherExamSignUp(OtherExamSignUp signUp) {
    OqlBuilder<OtherExamSignUp> query = OqlBuilder.from(OtherExamSignUp.class, "signUp");
    query.where("signUp.std =:std", signUp.getStd());
    query.where("signUp.semester =:semester", signUp.getSemester());
    query.where("signUp.subject =:subject", signUp.getSubject());
    List<OtherExamSignUp> examSignUps = entityDao.search(query);
    return examSignUps.size() > 0 ? examSignUps.get(0) : null;
  }

  public EntityDao getEntityDao() {
    return entityDao;
  }

  public void setEntityDao(EntityDao entityDao) {
    this.entityDao = entityDao;
  }
}
