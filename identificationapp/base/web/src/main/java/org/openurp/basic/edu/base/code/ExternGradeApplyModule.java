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
/**
 *
 */
package org.openurp.basic.edu.base.code;

import org.beangle.commons.inject.bind.AbstractBindModule;
import org.openurp.basic.edu.base.code.web.action.CertificateLevelAction;
import org.openurp.basic.edu.base.code.web.action.CertificateTypeAction;
import org.openurp.basic.edu.base.code.web.action.ExternExamSubjectAction;
import org.openurp.basic.edu.base.code.web.action.ExternExamTimeAction;

/**
 * @author zhouqi 2017年12月8日
 *
 */
public class ExternGradeApplyModule extends AbstractBindModule {

  @Override
  protected void doBinding() {
    bind(ExternExamSubjectAction.class, CertificateLevelAction.class, CertificateTypeAction.class, ExternExamTimeAction.class);
  }
}
