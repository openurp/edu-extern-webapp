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
package org.openurp.edu.extern.service;

import java.util.Collection;

import org.openurp.edu.extern.model.ExamSignup;
import org.openurp.edu.extern.model.ExamSignupLogger;

public interface ExamSignupLoggerService {

  public void logger(String code, String actionType, String remoteAddr, ExamSignup signup);

  public void logger(ExamSignupLogger logger);

  public void logger(String code, String actionType, String remoteAddr,
      Collection<ExamSignup> examSignups);
}
