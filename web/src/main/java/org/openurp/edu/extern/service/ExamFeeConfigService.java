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

import java.util.List;

import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.base.model.Semester;
import org.openurp.edu.base.model.Project;
import org.openurp.edu.extern.model.ExamFeeConfig;

public interface ExamFeeConfigService {

  public List<ExamFeeConfig> getConfigs(Project project, Semester... semesters);

  public List<ExamFeeConfig> getCurrOpenConfigs();

  public List<ExamFeeConfig> getOpenConfigs(Semester... semesters);

  public List<ExamFeeConfig> getOpenConfigs(Project project, Semester... semesters);

  public OqlBuilder<ExamFeeConfig> getOpenConfigBuilder(Project project, Semester... semesters);

  public ExamFeeConfig getConfig(ExamFeeConfig config);

  public boolean doCheck(Project project, Semester... semesters);

  public boolean doCheck(ExamFeeConfig config);

  public void saveOrUpdate(ExamFeeConfig config) throws Exception;

}
