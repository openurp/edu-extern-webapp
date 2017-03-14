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
import org.openurp.edu.extern.dao.ExamSignupDao;
import org.openurp.edu.extern.model.ExamSignup;
import org.openurp.edu.extern.model.ExamSignupConfig;
import org.openurp.edu.extern.model.ExamSignupSetting;

public class ExamSignupDaoHibernate extends HibernateEntityDao implements ExamSignupDao {

  public int getSignupCount(ExamSignupSetting setting) {
    OqlBuilder<ExamSignup> query = OqlBuilder.from(ExamSignup.class, "signup");
    query.where("signup.semester = :semester", setting.getConfig().getSemester());
    query.where("signup.subject = :category", setting.getSubject());
    List<ExamSignup> examSignups = search(query);
    return CollectUtils.isEmpty(examSignups) ? 0 : examSignups.size();
  }

  public boolean isRepeatSignup(Student std, ExamSignupSetting setting) {
    return null != getSignup(std, setting);
  }

  public ExamSignup getSignup(Student std, Semester semester, ExamSubject subject) {
    OqlBuilder<ExamSignup> query = OqlBuilder.from(ExamSignup.class, "signup");
    query.where("signup.subject = :subject", subject);
    query.where("signup.std = :std", std);
    query.where("signup.semester = :semester", semester);
    List<ExamSignup> signups = search(query);
    return CollectUtils.isEmpty(signups) ? null : signups.get(0);
  }

  public ExamSignup getSignup(Student std, ExamSignupSetting setting) {
    return getSignup(std, setting.getConfig().getSemester(), setting.getSubject());
  }

  public List<ExamSubject> getSignupSubjects(Student std, ExamSignupConfig config) {
    OqlBuilder<ExamSubject> query = OqlBuilder.from(ExamSubject.class, "subject");
    query
        .where(
            "exists (from  "+ExamSignup.class.getName()+" signup where signup.subject = subject and signup.std = :std and signup.semester = :semester)",
            std, config.getSemester());
    return search(query);
  }

  public List<ExamCategory> getSignupCategories(Student std, ExamSignupConfig config) {
    OqlBuilder<ExamCategory> query = OqlBuilder.from(ExamCategory.class, "category");
    query
        .where(
            "exists (from "+ExamSignup.class.getName()+" signup where signup.subject = category and signup.std = :std and signup.semester = :semester)",
            std, config.getSemester());
    return search(query);
  }

  public List<ExamSignupSetting> getSignedSettings(Student std, ExamSignupConfig config) {
    Set<ExamSubject> categories = CollectUtils.newHashSet((getSignupSubjects(std, config)));
    List<ExamSignupSetting> settings = CollectUtils.newArrayList();
    for (ExamSignupSetting setting : config.getSettings()) {
      if (categories.contains(setting.getSubject())) {
        settings.add(setting);
      }
    }
    return settings;
  }
}
