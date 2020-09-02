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

import java.util.Date;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.beangle.commons.entity.pojo.NumberIdTimeObject;

/**
 * 资格考试报名日志
 *
 * @author chaostone
 */
@Entity(name = "org.openurp.edu.extern.model.ExamSignupLogger")
public class ExamSignupLogger extends NumberIdTimeObject<Long> {

  private static final long serialVersionUID = 1L;
  /** 操作类型常量 */
  public static final String CREATE = "create";
  public static final String READ = "read";
  public static final String UPDATE = "update";
  public static final String DELETE = "delete";

  public static ExamSignupLogger createLogger(String code, String stdCode, Date logAt, String semester,
      String subject, String actionType, String remoteAddr) {
    ExamSignupLogger logger = new ExamSignupLogger();
    logger.setCode(code);
    logger.setStdCode(stdCode);
    logger.setLogAt(logAt);
    logger.setSemester(semester);
    logger.setSubject(subject);
    logger.setActionType(actionType);
    logger.setRemoteAddr(remoteAddr);
    return logger;
  }

  /** 操作人ID */
  @NotNull
  private String code;

  /** 被操作学生学号 */
  @NotNull
  private String stdCode;

  /** 操作时间 */
  @NotNull
  private Date logAt;

  /** 考试期号 */
  @NotNull
  private String semester;

  /** 报名科目 */
  @NotNull
  private String subject;

  /** 操作类型 */
  @NotNull
  private String actionType;

  /** 客户端IP */
  @NotNull
  private String remoteAddr;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getActionType() {
    return actionType;
  }

  public void setActionType(String actionType) {
    this.actionType = actionType;
  }

  public String getRemoteAddr() {
    return remoteAddr;
  }

  public void setRemoteAddr(String remoteAddr) {
    this.remoteAddr = remoteAddr;
  }

  public String getStdCode() {
    return stdCode;
  }

  public void setStdCode(String stdCode) {
    this.stdCode = stdCode;
  }

  public Date getLogAt() {
    return logAt;
  }

  public void setLogAt(Date logAt) {
    this.logAt = logAt;
  }

  public String getSemester() {
    return semester;
  }

  public void setSemester(String semester) {
    this.semester = semester;
  }
}
