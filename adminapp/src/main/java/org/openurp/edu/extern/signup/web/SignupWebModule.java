/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright (c) 2005, The OpenURP Software.
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
package org.openurp.edu.extern.signup.web;

import org.beangle.commons.inject.bind.AbstractBindModule;
import org.openurp.edu.extern.signup.web.action.ConfigAction;
import org.openurp.edu.extern.signup.web.action.FeeConfigAction;
import org.openurp.edu.extern.signup.web.action.ManageAction;
import org.openurp.edu.extern.signup.web.action.SettingAction;
import org.openurp.edu.extern.signup.web.action.StatAction;
import org.openurp.edu.extern.signup.web.action.SummaryAction;
import org.openurp.edu.extern.signup.web.action.TeacherAction;

public class SignupWebModule extends AbstractBindModule {
  @Override
  protected void doBinding() {
    bind(ManageAction.class);
    bind(ConfigAction.class);
    bind(SettingAction.class);
    bind(SummaryAction.class);
    bind(StatAction.class);
    bind(TeacherAction.class);
    bind(FeeConfigAction.class);
  }
}
