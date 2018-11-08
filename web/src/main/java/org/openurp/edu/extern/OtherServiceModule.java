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
package org.openurp.edu.extern;

import org.beangle.commons.inject.bind.AbstractBindModule;
import org.openurp.edu.extern.dao.internal.ExamSignupDaoHibernate;
import org.openurp.edu.extern.service.checker.ExamConditionChecker;
import org.openurp.edu.extern.service.checker.ExamExistChecker;
import org.openurp.edu.extern.service.checker.ExamPaymentChecker;
import org.openurp.edu.extern.service.checker.ExamSignBuildInChecker;
import org.openurp.edu.extern.service.checker.ExamSuperCategoryChecker;
import org.openurp.edu.extern.service.checker.ExamTimeChecker;
import org.openurp.edu.extern.service.impl.ExamFeeConfigServiceImpl;
import org.openurp.edu.extern.service.impl.ExamSignupConfigServiceImpl;
import org.openurp.edu.extern.service.impl.ExamSignupLoggerServiceImpl;
import org.openurp.edu.extern.service.impl.ExamSignupServiceImpl;
import org.openurp.edu.extern.service.impl.ExternExamGradeServiceImpl;
import org.openurp.edu.extern.service.listener.ExamBillStateChangeEventListener;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

public class OtherServiceModule extends AbstractBindModule {
  @Override
  protected void doBinding() {
    bind("examSignupConfigService", ExamSignupConfigServiceImpl.class);
    bind("examSignupLoggerService", ExamSignupLoggerServiceImpl.class);
    bind("examGradeService", ExternExamGradeServiceImpl.class);
    bind("examSignupDao", TransactionProxyFactoryBean.class).proxy("target",
        ExamSignupDaoHibernate.class).parent("baseTransactionProxy");

    bind("examSignBuildInChecker", ExamSignBuildInChecker.class);
    bind("examExistChecker", ExamExistChecker.class);
    bind("examConditionChecker", ExamConditionChecker.class);
    bind("examSuperCategoryChecker", ExamSuperCategoryChecker.class);
    bind("examTimeChecker", ExamTimeChecker.class);

    bind("examSignupService", ExamSignupServiceImpl.class).property(
        "checkerStack",
        list(ref("examSignBuildInChecker"), ref("examExistChecker"), ref("examConditionChecker"),
            ref("examSuperCategoryChecker")));

    bind("examFeeConfigService", ExamFeeConfigServiceImpl.class);
    bind("examPaymentChecker", ExamPaymentChecker.class);
    bind(ExamBillStateChangeEventListener.class);
  }
}
