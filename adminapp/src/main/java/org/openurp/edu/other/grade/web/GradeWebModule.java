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
package org.openurp.edu.other.grade.web;

import org.beangle.commons.inject.bind.AbstractBindModule;
import org.openurp.edu.other.grade.web.action.ManageAction;
import org.openurp.edu.other.grade.web.action.SearchAction;
import org.openurp.edu.other.grade.web.action.StatAction;

public class GradeWebModule extends AbstractBindModule {
  @Override
  protected void doBinding() {
    bind(ManageAction.class);
    bind(StatAction.class);
    bind(SearchAction.class);
  }
}
