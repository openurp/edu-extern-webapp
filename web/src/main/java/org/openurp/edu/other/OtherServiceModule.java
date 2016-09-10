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
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

public class OtherServiceModule extends AbstractBindModule {
  @Override
  protected void doBinding() {
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
