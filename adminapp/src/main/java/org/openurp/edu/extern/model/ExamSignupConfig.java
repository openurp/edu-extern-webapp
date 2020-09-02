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

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.entity.pojo.LongIdObject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openurp.base.model.Campus;
import org.openurp.edu.base.model.Project;
import org.openurp.edu.base.model.Semester;
import org.openurp.edu.extern.code.model.ExamCategory;
import org.openurp.edu.extern.code.model.ExamSubject;

/**
 * 资格考试报名设置（期号）
 *
 * @author chaostone
 */
@Cacheable
@Cache(region = "eams.teach", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity(name = "org.openurp.edu.extern.model.ExamSignupConfig")
public class ExamSignupConfig extends LongIdObject {

  private static final long serialVersionUID = 2286911547046363101L;

  /** 考试类型 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExamCategory category;

  /** 期号 */
  @NotNull
  private String code;

  /** 教学项目 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Project project;

  /** 期号名称 */
  @NotNull
  private String name;

  /** 报名科目设置 */
  @OneToMany(cascade = { CascadeType.ALL }, mappedBy = "config", orphanRemoval = true)
  private Set<ExamSignupSetting> settings;

  /** 开始时间 */
  @NotNull
  private Date beginAt;

  /** 结束时间 */
  @NotNull
  private Date endAt;

  /** 在规定的时间段内,是否可以开放 */
  @NotNull
  private boolean opened = false;

  /** 备注 */
  private String remark;

  /** 通知 */
  @Size(max = 2000)
  private String notice;

  /**允许跨校区报名*/
  private boolean allowCrossCampus;
  /** 报名校区 */
  @ManyToMany
  @NotNull
  private Set<Campus> campuses = CollectUtils.newHashSet();

  /** 报名科目设置 */
  @OneToMany(cascade = { CascadeType.ALL }, mappedBy = "config", orphanRemoval = true)
  private Set<ExclusiveSubject> exclusiveSubjects = CollectUtils.newHashSet();

  /** 学年学期 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Semester semester;

  public boolean isTimeSuitable() {
    if (null == getBeginAt() || null == getEndAt()) return false;
    Date now = new Date();
    return now.after(getBeginAt()) && now.before(getEndAt());
  }

  public void addSetting(ExamSignupSetting setting) {
    if (null == settings) {
      settings = CollectUtils.newHashSet();
    }
    settings.add(setting);
    setting.setConfig(this);
  }

  public Collection<ExamSubject> getSubjects() {
    Set<ExamSubject> categories = CollectUtils.newHashSet();
    for (ExamSignupSetting setting : settings) {
      categories.add(setting.getSubject());
    }
    return categories;
  }

  public Date getBeginAt() {
    return beginAt;
  }

  public void setBeginAt(Date beginAt) {
    this.beginAt = beginAt;
  }

  public Date getEndAt() {
    return endAt;
  }

  public void setEndAt(Date endAt) {
    this.endAt = endAt;
  }

  public boolean isOpened() {
    return opened;
  }

  public void setOpened(boolean opened) {
    this.opened = opened;
  }

  public Set<ExamSignupSetting> getSettings() {
    return settings;
  }

  public void setSettings(Set<ExamSignupSetting> settings) {
    this.settings = settings;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getNotice() {
    return notice;
  }

  public void setNotice(String notice) {
    this.notice = notice;
  }

  public Semester getSemester() {
    return semester;
  }

  public void setSemester(Semester semester) {
    this.semester = semester;
  }

  public Set<Campus> getCampuses() {
    return campuses;
  }

  public void setCampuses(Set<Campus> campuses) {
    this.campuses = campuses;
  }

  public ExamCategory getCategory() {
    return category;
  }

  public void setCategory(ExamCategory category) {
    this.category = category;
  }

  public Set<ExclusiveSubject> getExclusiveSubjects() {
    return exclusiveSubjects;
  }

  public void setExclusiveSubjects(Set<ExclusiveSubject> exclusiveSubjects) {
    this.exclusiveSubjects = exclusiveSubjects;
  }

  public Set<ExclusiveCategory> getExclusiveCategories() {

    return null;
  }

  public void setExclusiveCategories(Set<ExclusiveCategory> exclusiveCategories) {

  }

  public boolean addCampus(Campus campus) {
    return this.campuses.add(campus);
  }

  public boolean addCampuses(Collection<Campus> campuses) {
    return this.campuses.addAll(campuses);
  }

  public boolean removeCampus(Campus campus) {
    return this.campuses.remove(campus);
  }

  public boolean removeCampuses(Collection<Campus> campuses) {
    return this.campuses.removeAll(campuses);
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public boolean isAllowCrossCampus() {
    return allowCrossCampus;
  }

  public void setAllowCrossCampus(boolean allowCrossCampus) {
    this.allowCrossCampus = allowCrossCampus;
  }

}
