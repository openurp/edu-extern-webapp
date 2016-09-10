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
package org.openurp.edu.other.service.impl;

import java.util.Collection;
import java.util.Date;

import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.entity.metadata.Model;
import org.openurp.edu.other.model.OtherExamSignUp;
import org.openurp.edu.other.model.OtherExamSignUpLogger;
import org.openurp.edu.other.service.OtherExamSignUpLoggerService;

public class OtherExamSignUpLoggerServiceImpl extends BaseServiceImpl implements OtherExamSignUpLoggerService {

  public void logger(String code, String actionType, String remoteAddr,
      Collection<OtherExamSignUp> otherExamSignUps) {
    for (OtherExamSignUp signUp : otherExamSignUps) {
      OtherExamSignUpLogger logger = Model.newInstance(OtherExamSignUpLogger.class);
      logger.setCode(code);
      logger.setStdCode(signUp.getStd().getCode());
      logger.setSubject(signUp.getSubject().getName());
      logger.setSemester(signUp.getSemester().getCode());
      logger.setActionType(actionType);
      logger.setRemoteAddr(remoteAddr);
      logger.setLogAt(new Date());
      logger(logger);
    }
  }

  public void logger(String code, String actionType, String remoteAddr, OtherExamSignUp signUp) {
    OtherExamSignUpLogger logger = Model.newInstance(OtherExamSignUpLogger.class);
    logger.setCode(code);
    logger.setStdCode(signUp.getStd().getCode());
    logger.setSubject(signUp.getSubject().getName());
    logger.setSemester(signUp.getSemester().getCode());
    logger.setActionType(actionType);
    logger.setRemoteAddr(remoteAddr);
    logger.setLogAt(new Date());
    logger(logger);
  }

  public void logger(OtherExamSignUpLogger logger) {
    entityDao.saveOrUpdate(logger);
  }
}
