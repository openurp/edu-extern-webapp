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
package org.openurp.edu.extern.service.impl;

import java.util.Date;
import java.util.List;

import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.time.HourMinute;
import org.openurp.edu.extern.code.model.ExamCategory;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExamSignupConfig;
import org.openurp.edu.extern.model.ExamSignupSetting;
import org.openurp.edu.extern.service.ExamSignupConfigService;

public class ExamSignupConfigServiceImpl extends BaseServiceImpl implements ExamSignupConfigService {

  /**
   * 根据考试类型和期号来创建默认的某个期号的科目设置
   *
   * @param examCategory
   * @param config
   */
  public void createDefaultSubject(ExamCategory examCategory, ExamSignupConfig config) {
    OqlBuilder<ExamSubject> query = OqlBuilder.from(ExamSubject.class, "subject");
    query.where("subject.category = :category", examCategory);
    List<ExamSubject> subjectList = entityDao.search(query);
    for (int i = 0; i < subjectList.size(); i++) {
      ExamSubject subject = subjectList.get(i);
      java.sql.Date examOn = new java.sql.Date(config.getBeginAt().getTime());
      config.addSetting(new ExamSignupSetting(subject, examOn, HourMinute.of(config.getBeginAt()), HourMinute
          .of(config.getEndAt())));
    }
    entityDao.saveOrUpdate(ExamSignupConfig.class.getName(), config);
  }

  public ExamSignupConfig configDefaultSubject(ExamCategory examCategory, ExamSignupConfig config) {
    OqlBuilder<ExamSubject> query = OqlBuilder.from(ExamSubject.class, "subject");
    query.where("subject.category = :category", examCategory);
    query.where("subject.beginOn <= :now and (subject.endOn is null or subject.endOn >= :now)",
        new java.util.Date());
    List<ExamSubject> subjectList = entityDao.search(query);
    for (int i = 0; i < subjectList.size(); i++) {
      ExamSubject subject = subjectList.get(i);
      java.sql.Date examOn = new java.sql.Date(config.getBeginAt().getTime());
      config.addSetting(new ExamSignupSetting(subject, examOn, HourMinute.of(config.getBeginAt()), HourMinute
          .of(config.getEndAt())));
    }
    return config;
  }
}
