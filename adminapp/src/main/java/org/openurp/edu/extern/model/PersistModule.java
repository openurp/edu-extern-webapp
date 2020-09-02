package org.openurp.edu.extern.model;

import org.beangle.commons.entity.orm.AbstractPersistModule;

public class PersistModule extends AbstractPersistModule {
  @Override
  protected void doConfig() {
    add(org.openurp.edu.extern.model.ExamGradeAlterInfo.class,
        org.openurp.edu.extern.model.ExamSignupSetting.class,
        org.openurp.edu.extern.model.ExamSignupLogger.class,
        org.openurp.edu.extern.model.ExamSignupConfig.class,
        org.openurp.edu.extern.model.ExamSignupCondition.class, org.openurp.edu.extern.model.ExamSignup.class,
        org.openurp.edu.extern.model.ExclusiveSubject.class,
        org.openurp.edu.extern.model.ExclusiveCategory.class,
        org.openurp.edu.extern.model.ExternExamGrade.class, org.openurp.edu.extern.model.ExternCourse.class,
        org.openurp.edu.extern.model.ExternSchool.class, org.openurp.edu.extern.code.model.ExamCategory.class,
        org.openurp.edu.extern.code.model.ExamSubject.class,
        org.openurp.edu.extern.model.ExternGrade.class);
  }
}
