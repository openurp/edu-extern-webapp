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
package org.openurp.edu.other;

import org.beangle.commons.inject.bind.AbstractBindModule;
import org.openurp.edu.other.dao.internal.OtherExamSignUpDaoHibernate;
import org.openurp.edu.other.service.checker.OtherExamExistChecker;
import org.openurp.edu.other.service.checker.OtherExamPaymentChecker;
import org.openurp.edu.other.service.impl.OtherExamExportServiceImpl;
import org.openurp.edu.other.service.impl.OtherExamFeeConfigServiceImpl;
import org.openurp.edu.other.service.impl.OtherExamSignUpConfigServiceImpl;
import org.openurp.edu.other.service.impl.OtherExamSignUpLoggerServiceImpl;
import org.openurp.edu.other.service.impl.OtherGradeServiceImpl;
import org.openurp.edu.other.service.listener.OtherExamBillStateChangeEventListener;
import org.openurp.edu.other.web.action.OtherExamFeeConfigAction;
import org.openurp.edu.other.web.action.OtherExamSignUpAction;
import org.openurp.edu.other.web.action.OtherExamSignUpConfigAction;
import org.openurp.edu.other.web.action.OtherExamSignUpSettingAction;
import org.openurp.edu.other.web.action.OtherExamSignUpStatAction;
import org.openurp.edu.other.web.action.OtherExamSignUpSummaryAction;
import org.openurp.edu.other.web.action.OtherGradeAction;
import org.openurp.edu.other.web.action.OtherGradeSearchAction;
import org.openurp.edu.other.web.action.OtherGradeStatAction;
import org.openurp.edu.other.web.action.SignUpByTeacherAction;
import org.openurp.edu.other.web.action.StdOtherExamSignUpAction;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

public class OtherBeanModule extends AbstractBindModule {
  @Override
  protected void doBinding() {
    bind(OtherExamSignUpAction.class);
    bind(OtherExamSignUpConfigAction.class);
    bind(OtherExamSignUpSettingAction.class);
    bind(OtherExamSignUpStatAction.class);
    bind(OtherExamSignUpSummaryAction.class);
    bind(OtherGradeAction.class);
    bind(OtherGradeStatAction.class);
    bind(SignUpByTeacherAction.class);
    bind("stdOtherExamSignUp", StdOtherExamSignUpAction.class);
    bind(OtherGradeSearchAction.class);
    bind(OtherExamFeeConfigAction.class);
    bind("otherExamExportService", OtherExamExportServiceImpl.class);
    bind("otherExamSignUpConfigService", OtherExamSignUpConfigServiceImpl.class);
    bind("otherExamSignUpLoggerService", OtherExamSignUpLoggerServiceImpl.class);
    bind("otherGradeService", OtherGradeServiceImpl.class);
    bind("otherExamSignUpDao", TransactionProxyFactoryBean.class).proxy("target",
        OtherExamSignUpDaoHibernate.class).parent("baseTransactionProxy");

    bind("otherExamSignUpChecker", OtherExamExistChecker.class);
    bind("otherExamFeeConfigService", OtherExamFeeConfigServiceImpl.class);
    bind("otherExamPaymentChecker", OtherExamPaymentChecker.class);
    bind(OtherExamBillStateChangeEventListener.class);
  }
}
