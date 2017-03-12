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
package org.openurp.edu.extern;

import org.beangle.commons.inject.bind.AbstractBindModule;
import org.openurp.edu.extern.dao.internal.ExamSignUpDaoHibernate;
import org.openurp.edu.extern.service.checker.ExamExistChecker;
import org.openurp.edu.extern.service.checker.ExamGradeChecker;
import org.openurp.edu.extern.service.checker.ExamPaymentChecker;
import org.openurp.edu.extern.service.checker.ExamSignBuildInChecker;
import org.openurp.edu.extern.service.checker.ExamSuperCategoryChecker;
import org.openurp.edu.extern.service.checker.ExamTimeChecker;
import org.openurp.edu.extern.service.impl.ExamExportServiceImpl;
import org.openurp.edu.extern.service.impl.ExamFeeConfigServiceImpl;
import org.openurp.edu.extern.service.impl.ExamSignUpConfigServiceImpl;
import org.openurp.edu.extern.service.impl.ExamSignUpLoggerServiceImpl;
import org.openurp.edu.extern.service.impl.ExamSignUpServiceImpl;
import org.openurp.edu.extern.service.impl.ExamGradeServiceImpl;
import org.openurp.edu.extern.service.listener.ExamBillStateChangeEventListener;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

public class OtherServiceModule extends AbstractBindModule {
  @Override
  protected void doBinding() {
    bind("otherExamExportService", ExamExportServiceImpl.class);
    bind("otherExamSignUpConfigService", ExamSignUpConfigServiceImpl.class);
    bind("otherExamSignUpLoggerService", ExamSignUpLoggerServiceImpl.class);
    bind("otherGradeService", ExamGradeServiceImpl.class);
    bind("otherExamSignUpDao", TransactionProxyFactoryBean.class).proxy("target",
        ExamSignUpDaoHibernate.class).parent("baseTransactionProxy");

    bind("otherExamSignBuildInChecker", ExamSignBuildInChecker.class);
    bind("otherExamExistChecker", ExamExistChecker.class);
    bind("otherExamGradeChecker", ExamGradeChecker.class);
    bind("otherExamSuperCategoryChecker", ExamSuperCategoryChecker.class);
    bind("otherExamTimeChecker", ExamTimeChecker.class);

    bind("otherExamSignUpService", ExamSignUpServiceImpl.class).property(
        "checkerStack",
        list(ref("otherExamSignBuildInChecker"), ref("otherExamExistChecker"), ref("otherExamGradeChecker"),
            ref("otherExamSuperCategoryChecker")));

    bind("otherExamFeeConfigService", ExamFeeConfigServiceImpl.class);
    bind("otherExamPaymentChecker", ExamPaymentChecker.class);
    bind(ExamBillStateChangeEventListener.class);
  }
}
