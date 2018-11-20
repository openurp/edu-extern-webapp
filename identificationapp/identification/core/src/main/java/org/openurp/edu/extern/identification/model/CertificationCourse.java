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
package org.openurp.edu.extern.identification.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.beangle.commons.entity.pojo.NumberIdObject;
import org.openurp.edu.base.model.Course;

/**
 * @author zhouqi 2018年4月9日
 */
@Entity(name = "org.openurp.edu.extern.identification.model.CertificationCourse")
public class CertificationCourse extends NumberIdObject<Integer> {

  private static final long serialVersionUID = 4389788706207331936L;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Certification certification;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Course course;

  @NotNull
  private Float score;

  private String scoreValue;

  public CertificationCourse() {
    super();
  }

  CertificationCourse(Certification certification, Course course, Float score, String scoreValue) {
    this();
    this.certification = certification;
    this.course = course;
    this.score = score;
    this.scoreValue = scoreValue;
  }

  public Certification getCertification() {
    return certification;
  }

  public void setCertification(Certification certification) {
    this.certification = certification;
  }

  public Course getCourse() {
    return course;
  }

  public void setCourse(Course course) {
    this.course = course;
  }

  public Float getScore() {
    return score;
  }

  public void setScore(Float score) {
    this.score = score;
  }

  public String getScoreValue() {
    return scoreValue;
  }

  public void setScoreValue(String scoreValue) {
    this.scoreValue = scoreValue;
  }
}
