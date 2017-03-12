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
package org.openurp.edu.extern.dao.internal;

import java.util.List;
import java.util.Set;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.orm.hibernate.HibernateEntityDao;
import org.openurp.base.model.Semester;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.code.model.ExamCategory;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.dao.ExamSignUpDao;
import org.openurp.edu.extern.model.ExamSignUp;
import org.openurp.edu.extern.model.ExamSignUpConfig;
import org.openurp.edu.extern.model.ExamSignUpSetting;

public class ExamSignUpDaoHibernate extends HibernateEntityDao implements ExamSignUpDao {

  public int getSignUpCount(ExamSignUpSetting setting) {
    OqlBuilder<ExamSignUp> query = OqlBuilder.from(ExamSignUp.class, "signUp");
    query.where("signUp.semester = :semester", setting.getConfig().getSemester());
    query.where("signUp.subject = :category", setting.getSubject());
    List<ExamSignUp> otherExamSignUps = search(query);
    return CollectUtils.isEmpty(otherExamSignUps) ? 0 : otherExamSignUps.size();
  }

  public boolean isRepeatSignUp(Student std, ExamSignUpSetting setting) {
    return null != getSignUp(std, setting);
  }

  public ExamSignUp getSignUp(Student std, Semester semester, ExamSubject subject) {
    OqlBuilder<ExamSignUp> query = OqlBuilder.from(ExamSignUp.class, "signUp");
    query.where("signUp.subject = :subject", subject);
    query.where("signUp.std = :std", std);
    query.where("signUp.semester = :semester", semester);
    List<ExamSignUp> signUps = search(query);
    return CollectUtils.isEmpty(signUps) ? null : signUps.get(0);
  }

  public ExamSignUp getSignUp(Student std, ExamSignUpSetting setting) {
    return getSignUp(std, setting.getConfig().getSemester(), setting.getSubject());
  }

  public List<ExamSubject> getSignUpSubjects(Student std, ExamSignUpConfig config) {
    OqlBuilder<ExamSubject> query = OqlBuilder.from(ExamSubject.class, "subject");
    query
        .where(
            "exists (from  "+ExamSignUp.class.getName()+" signUp where signUp.subject = subject and signUp.std = :std and signUp.semester = :semester)",
            std, config.getSemester());
    return search(query);
  }

  public List<ExamCategory> getSignUpCategories(Student std, ExamSignUpConfig config) {
    OqlBuilder<ExamCategory> query = OqlBuilder.from(ExamCategory.class, "category");
    query
        .where(
            "exists (from "+ExamSignUp.class.getName()+" signUp where signUp.subject = category and signUp.std = :std and signUp.semester = :semester)",
            std, config.getSemester());
    return search(query);
  }

  public List<ExamSignUpSetting> getSignedSettings(Student std, ExamSignUpConfig config) {
    Set<ExamSubject> categories = CollectUtils.newHashSet((getSignUpSubjects(std, config)));
    List<ExamSignUpSetting> settings = CollectUtils.newArrayList();
    for (ExamSignUpSetting setting : config.getSettings()) {
      if (categories.contains(setting.getSubject())) {
        settings.add(setting);
      }
    }
    return settings;
  }
}
