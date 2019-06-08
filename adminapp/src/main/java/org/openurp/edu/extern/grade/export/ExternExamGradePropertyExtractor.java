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
package org.openurp.edu.extern.grade.export;

import org.beangle.commons.transfer.exporter.DefaultPropertyExtractor;
import org.openurp.edu.extern.model.ExternExamGrade;

/**
 * @author zhouqi 2018年12月25日
 */
public class ExternExamGradePropertyExtractor extends DefaultPropertyExtractor {
  @Override
  public Object getPropertyValue(Object target, String property) throws Exception {
    ExternExamGrade externExamGrade = (ExternExamGrade) target;
    if ("courseGradeSize".equals(property)) {
      return externExamGrade.getGrades().size();
    }
    return super.getPropertyValue(target, property);
  }
}
