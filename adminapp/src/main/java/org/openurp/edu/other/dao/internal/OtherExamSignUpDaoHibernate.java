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
package org.openurp.edu.other.dao.internal;

import java.util.List;
import java.util.Set;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.orm.hibernate.HibernateEntityDao;
import org.openurp.base.model.Semester;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.other.code.model.OtherExamCategory;
import org.openurp.edu.other.code.model.OtherExamSubject;
import org.openurp.edu.other.dao.OtherExamSignUpDao;
import org.openurp.edu.other.model.OtherExamSignUp;
import org.openurp.edu.other.model.OtherExamSignUpConfig;
import org.openurp.edu.other.model.OtherExamSignUpSetting;

public class OtherExamSignUpDaoHibernate extends HibernateEntityDao implements OtherExamSignUpDao {

  public int getSignUpCount(OtherExamSignUpSetting setting) {
    OqlBuilder<OtherExamSignUp> query = OqlBuilder.from(OtherExamSignUp.class, "signUp");
    query.where("signUp.semester = :semester", setting.getConfig().getSemester());
    query.where("signUp.subject = :category", setting.getSubject());
    List<OtherExamSignUp> otherExamSignUps = search(query);
    return CollectUtils.isEmpty(otherExamSignUps) ? 0 : otherExamSignUps.size();
  }

  public boolean isRepeatSignUp(Student std, OtherExamSignUpSetting setting) {
    return null != getSignUp(std, setting);
  }

  public OtherExamSignUp getSignUp(Student std, Semester semester, OtherExamSubject subject) {
    OqlBuilder<OtherExamSignUp> query = OqlBuilder.from(OtherExamSignUp.class, "signUp");
    query.where("signUp.subject = :subject", subject);
    query.where("signUp.std = :std", std);
    query.where("signUp.semester = :semester", semester);
    List<OtherExamSignUp> signUps = search(query);
    return CollectUtils.isEmpty(signUps) ? null : signUps.get(0);
  }

  public OtherExamSignUp getSignUp(Student std, OtherExamSignUpSetting setting) {
    return getSignUp(std, setting.getConfig().getSemester(), setting.getSubject());
  }

  public List<OtherExamSubject> getSignUpSubjects(Student std, OtherExamSignUpConfig config) {
    OqlBuilder<OtherExamSubject> query = OqlBuilder.from(OtherExamSubject.class, "subject");
    query
        .where(
            "exists (from org.openurp.edu.other.OtherExamSignUp signUp where signUp.subject = subject and signUp.std = :std and signUp.semester = :semester)",
            std, config.getSemester());
    return search(query);
  }

  public List<OtherExamCategory> getSignUpCategories(Student std, OtherExamSignUpConfig config) {
    OqlBuilder<OtherExamCategory> query = OqlBuilder.from(OtherExamCategory.class, "category");
    query
        .where(
            "exists (from org.openurp.edu.other.OtherExamSignUp signUp where signUp.subject = category and signUp.std = :std and signUp.semester = :semester)",
            std, config.getSemester());
    return search(query);
  }

  public List<OtherExamSignUpSetting> getSignedSettings(Student std, OtherExamSignUpConfig config) {
    Set<OtherExamSubject> categories = CollectUtils.newHashSet((getSignUpSubjects(std, config)));
    List<OtherExamSignUpSetting> settings = CollectUtils.newArrayList();
    for (OtherExamSignUpSetting setting : config.getSettings()) {
      if (categories.contains(setting.getSubject())) {
        settings.add(setting);
      }
    }
    return settings;
  }
}
