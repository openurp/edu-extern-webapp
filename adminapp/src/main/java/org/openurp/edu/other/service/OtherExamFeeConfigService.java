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
package org.openurp.edu.other.service;

import java.util.List;

import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.base.model.Semester;
import org.openurp.edu.base.model.Project;
import org.openurp.edu.other.model.OtherExamFeeConfig;

public interface OtherExamFeeConfigService {

  public List<OtherExamFeeConfig> getConfigs(Project project, Semester... semesters);

  public List<OtherExamFeeConfig> getCurrOpenConfigs();

  public List<OtherExamFeeConfig> getOpenConfigs(Semester... semesters);

  public List<OtherExamFeeConfig> getOpenConfigs(Project project, Semester... semesters);

  public OqlBuilder<OtherExamFeeConfig> getOpenConfigBuilder(Project project, Semester... semesters);

  public OtherExamFeeConfig getConfig(OtherExamFeeConfig config);

  public boolean doCheck(Project project, Semester... semesters);

  public boolean doCheck(OtherExamFeeConfig config);

  public void saveOrUpdate(OtherExamFeeConfig config) throws Exception;

}
