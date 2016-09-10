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

import java.util.Date;
import java.util.List;

import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.edu.other.code.model.OtherExamCategory;
import org.openurp.edu.other.code.model.OtherExamSubject;
import org.openurp.edu.other.model.OtherExamSignUpConfig;
import org.openurp.edu.other.model.OtherExamSignUpSetting;
import org.openurp.edu.other.service.OtherExamSignUpConfigService;

public class OtherExamSignUpConfigServiceImpl extends BaseServiceImpl implements OtherExamSignUpConfigService {

  /**
   * 根据考试类型和期号来创建默认的某个期号的科目设置
   * 
   * @param examCategory
   * @param config
   */
  public void createDefaultSubject(OtherExamCategory examCategory, OtherExamSignUpConfig config) {
    OqlBuilder<OtherExamSubject> query = OqlBuilder.from(OtherExamSubject.class, "subject");
    query.where("subject.category = :category", examCategory);
    List<OtherExamSubject> subjectList = entityDao.search(query);
    for (int i = 0; i < subjectList.size(); i++) {
      OtherExamSubject subject = subjectList.get(i);
      Date beginAt = config.getBeginAt();
      Date endAt = config.getEndAt();
      config.addSetting(new OtherExamSignUpSetting(subject, beginAt, endAt));
    }
    entityDao.saveOrUpdate(OtherExamSignUpConfig.class.getName(), config);
  }

  public OtherExamSignUpConfig configDefaultSubject(OtherExamCategory examCategory,
      OtherExamSignUpConfig config) {
    OqlBuilder<OtherExamSubject> query = OqlBuilder.from(OtherExamSubject.class, "subject");
    query.where("subject.category = :category", examCategory);
    query.where("subject.beginOn <= :now and (subject.endOn is null or subject.endOn >= :now)",
        new java.util.Date());
    List<OtherExamSubject> subjectList = entityDao.search(query);
    for (int i = 0; i < subjectList.size(); i++) {
      OtherExamSubject subject = subjectList.get(i);
      Date beginAt = config.getBeginAt();
      Date endAt = config.getEndAt();
      config.addSetting(new OtherExamSignUpSetting(subject, beginAt, endAt));
    }
    return config;
  }

}
