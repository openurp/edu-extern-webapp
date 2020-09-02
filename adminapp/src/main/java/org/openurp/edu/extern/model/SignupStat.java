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
package org.openurp.edu.extern.model;

public class SignupStat {

  private String schoolYear;

  private String semesterName;

  private String subjectName;

  private String categoryName;

  private String count;

  private Double sumOfOutline;

  private Double sumOfMaterial;

  private Double sumOfSignup;

  public String getSchoolYear() {
    return schoolYear;
  }

  public void setSchoolYear(String schoolYear) {
    this.schoolYear = schoolYear;
  }

  public String getSemesterName() {
    return semesterName;
  }

  public void setSemesterName(String semesterName) {
    this.semesterName = semesterName;
  }

  public String getSubjectName() {
    return subjectName;
  }

  public void setSubjectName(String subjectName) {
    this.subjectName = subjectName;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public String getCount() {
    return count;
  }

  public void setCount(String count) {
    this.count = count;
  }

  public Double getSumOfOutline() {
    return sumOfOutline;
  }

  public void setSumOfOutline(Double sumOfOutline) {
    this.sumOfOutline = sumOfOutline;
  }

  public Double getSumOfMaterial() {
    return sumOfMaterial;
  }

  public void setSumOfMaterial(Double sumOfMaterial) {
    this.sumOfMaterial = sumOfMaterial;
  }

  public Double getSumOfSignup() {
    return sumOfSignup;
  }

  public void setSumOfSignup(Double sumOfSignup) {
    this.sumOfSignup = sumOfSignup;
  }

}
