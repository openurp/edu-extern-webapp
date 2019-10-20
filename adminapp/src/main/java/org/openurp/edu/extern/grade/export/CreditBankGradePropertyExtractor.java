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
import java.util.Map;

import org.beangle.commons.lang.Strings;
import org.beangle.commons.text.i18n.TextResource;
import org.beangle.commons.transfer.exporter.DefaultPropertyExtractor;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.grade.course.model.CourseGrade;
import org.openurp.edu.student.info.model.Graduation;

/**
 * @author zhouqi 2019年10月12日
 */
public class CreditBankGradePropertyExtractor extends DefaultPropertyExtractor {

  private Map<String, Object> dataMap;

  public CreditBankGradePropertyExtractor(TextResource textResource, Map<String, Object> dataMap) {
    super(textResource);
    this.dataMap = dataMap;
  }

  @Override
  public Object getPropertyValue(Object target, String property) throws Exception {
    CourseGrade grade = (CourseGrade) target;
    Graduation graduation = ((Map<Student, Graduation>) dataMap.get("graduationMap")).get(grade.getStd());
    if ("scoreText".equals(property)) {
      StringBuilder scoreText = new StringBuilder();
      if (null != grade.getScore()) {
        scoreText.append(grade.getScore());
        if (Strings.isBlank(scoreText)) {
          scoreText.append("(").append(grade.getScoreText()).append(")");
        }
      }
      return scoreText.toString();
    } else if ("semester.beginOn".equals(property)) {
      return new SimpleDateFormat("yyyyMM").format(grade.getSemester().getBeginOn());
    } else if ("graduation.graduateOn".equals(property)) {
      return new SimpleDateFormat("yyyy").format(graduation.getGraduateOn());
    } else if ("graduation.educationResult.code".equals(property)) {
      return graduation.getEducationResult().getName();
    }
    return super.getPropertyValue(target, property);
  }
}
