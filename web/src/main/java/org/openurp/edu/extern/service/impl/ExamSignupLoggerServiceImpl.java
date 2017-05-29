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
package org.openurp.edu.extern.service.impl;

import java.util.Collection;
import java.util.Date;

import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.entity.metadata.Model;
import org.openurp.edu.extern.model.ExamSignup;
import org.openurp.edu.extern.model.ExamSignupLogger;
import org.openurp.edu.extern.service.ExamSignupLoggerService;

public class ExamSignupLoggerServiceImpl extends BaseServiceImpl implements ExamSignupLoggerService {

  public void logger(String code, String actionType, String remoteAddr,
      Collection<ExamSignup> examSignups) {
    for (ExamSignup signup : examSignups) {
      ExamSignupLogger logger = Model.newInstance(ExamSignupLogger.class);
      logger.setCode(code);
      logger.setStdCode(signup.getStd().getUser().getCode());
      logger.setSubject(signup.getSubject().getName());
      logger.setSemester(signup.getSemester().getCode());
      logger.setActionType(actionType);
      logger.setRemoteAddr(remoteAddr);
      logger.setLogAt(new Date());
      logger(logger);
    }
  }

  public void logger(String code, String actionType, String remoteAddr, ExamSignup signup) {
    ExamSignupLogger logger = Model.newInstance(ExamSignupLogger.class);
    logger.setCode(code);
    logger.setStdCode(signup.getStd().getUser().getCode());
    logger.setSubject(signup.getSubject().getName());
    logger.setSemester(signup.getSemester().getCode());
    logger.setActionType(actionType);
    logger.setRemoteAddr(remoteAddr);
    logger.setLogAt(new Date());
    logger(logger);
  }

  public void logger(ExamSignupLogger logger) {
    entityDao.saveOrUpdate(logger);
  }
}
