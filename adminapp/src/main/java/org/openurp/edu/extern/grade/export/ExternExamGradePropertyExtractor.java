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

import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.beangle.commons.lang.Strings;
import org.beangle.commons.transfer.exporter.DefaultPropertyExtractor;
import org.openurp.edu.extern.grade.data.ExternExamGradeData;
import org.openurp.edu.extern.model.ExternExamGrade;

/**
 * @author zhouqi 2018年12月25日
 */
public class ExternExamGradePropertyExtractor extends DefaultPropertyExtractor {

  private String dataInSource;

  public ExternExamGradePropertyExtractor(String dataInSource) {
    super();
    this.dataInSource = dataInSource;
  }

  @Override
  public Object getPropertyValue(Object target, String property) throws Exception {
    if ("courseGrade".equals(dataInSource)) {
      ExternExamGradeData data = (ExternExamGradeData) target;
      if ("original.course.code".equals(property)) {
        return "02";
      } else if ("original.major.name".equals(property)) {
        return "待补";
      } else if ("original.school.name".equals(property)) {
        return "待补";
      } else if ("original.level.code".equals(property)) {
        return "21?";
      } else if ("original.project.category.code".equals(property)) {
        return "30?";
      } else if ("original.course.credits".equals(property)) {
        return "0";
      } else if ("original.course.creditHours".equals(property)) {
        return "0";
      } else if ("courseGrade.scoreText".equals(property)) {
        if (null == data.getCourseGrade()) {
          return StringUtils.EMPTY;
        } else {
          StringBuilder scoreText = new StringBuilder();
          if (null != data.getCourseGrade().getScore()) {
            scoreText.append(data.getCourseGrade().getScore());
            if (Strings.isBlank(scoreText)) {
              scoreText.append("(").append(data.getCourseGrade().getScoreText()).append(")");
            }
          }
          return scoreText.toString();
        }
      } else if ("info.acquiredOn".equals(property)) {
        return new SimpleDateFormat("yyyyMM").format(data.getInfo().getAcquiredOn());
      }
    } else {
      ExternExamGrade externExamGrade = (ExternExamGrade) target;
      if ("courseGradeSize".equals(property)) {
        return externExamGrade.getGrades().size();
      }
    }
    return super.getPropertyValue(target, property);
  }
}
