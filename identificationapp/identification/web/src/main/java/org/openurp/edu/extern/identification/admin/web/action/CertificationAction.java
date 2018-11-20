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
package org.openurp.edu.extern.identification.admin.web.action;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.code.geo.model.Division;
import org.openurp.edu.base.code.model.CertificateLevel;
import org.openurp.edu.base.code.model.CertificateType;
import org.openurp.edu.base.code.model.ExternExamSubject;
import org.openurp.edu.base.code.model.ExternExamTime;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.IdentificationConst;
import org.openurp.edu.extern.identification.code.web.action.CertificationSupportAction;
import org.openurp.edu.extern.identification.model.CertScore;
import org.openurp.edu.extern.identification.model.CertScoreCourse;
import org.openurp.edu.extern.identification.model.Certificate;
import org.openurp.edu.extern.identification.model.Certification;
import org.openurp.edu.extern.identification.model.CertificationCourse;
import org.openurp.edu.extern.identification.model.ExternExamSubjectField;
import org.openurp.edu.extern.identification.model.ExternExamSubjectSetting;
import org.openurp.edu.extern.model.ExternExamGrade;

/**
 * @author zhouqi 2018年3月14日
 */
public class CertificationAction extends CertificationSupportAction<Integer, Certification> {

  private final Map<String, String> CERT_ELEMENT_OBJECT_MAP = certElementObjectMap();

  private final Map<String, String> certElementObjectMap() {
    Map<String, String> certObjMap = CollectUtils.newHashMap();
    certObjMap.put(IdentificationConst.CERT_TYPE, CertificateType.class.getName());
    certObjMap.put(IdentificationConst.CERT_LEVEL, CertificateLevel.class.getName());
    certObjMap.put(IdentificationConst.DIVISION, Division.class.getName());
    certObjMap.put(IdentificationConst.EXAM_TIME, ExternExamTime.class.getName());
    return certObjMap;
  }

  protected void indexSetting() {
    super.indexSetting();
  }

  public String apply() {
    String no = get("no");
    if (StringUtils.isBlank(no)) {
      return "apply1";
    } else {
      Long stdId = getLongId("std");
      if (null == stdId) {
        return apply2();
      } else {
        Integer settingId = getIntId("setting");
        if (null == settingId) {
          return apply3(stdId);
        } else {
          return apply4(stdId, settingId);
        }
      }
    }
  }

  public String apply1CheckAjax() {
    put("isOk", CollectionUtils.isNotEmpty(entityDao.get(Student.class, "user.code", get("no"))));
    return forward();
  }

  private String apply2() {
    put("std", entityDao.get(Student.class, "user.code", get("no")).get(0));

    // 证书大类，必须是“证书认定标准”和“数据来源”的交集，两者缺一不可。
    OqlBuilder<ExternExamSubject> builder = OqlBuilder.from(ExternExamSubject.class, "examSubject");
    Date now = new Date(System.currentTimeMillis());
    StringBuilder hql = new StringBuilder();
    hql.append("exists (");
    hql.append("  from ").append(CertScore.class.getName()).append(" certScore");
    hql.append(" where certScore.examSubject = examSubject");
    hql.append("   and exists (from certScore.courses csCourse)");
    hql.append("   and certScore.beginOn <= :now");
    hql.append("   and (certScore.endOn >= :now or certScore.endOn is null)");
    hql.append(")");
    builder.where(hql.toString(), now);
    hql = new StringBuilder();
    hql.append("exists (");
    hql.append("  from ").append(ExternExamSubjectSetting.class.getName()).append(" setting");
    hql.append(" where setting.examSubject = examSubject");
    hql.append("   and setting.beginOn <= :now");
    // 这里的 is null ，是因为在 setting 中可能是包含的，后面同理
    hql.append("   and (setting.endOn >= :now or setting.endOn is null)");
    hql.append(")");
    builder.where(hql.toString(), now);
    builder.where("examSubject.beginOn <= :now", now);
    builder.where("examSubject.endOn >= :now or examSubject.endOn is null", now);
    put("examSubjects", entityDao.search(builder));
    return "apply2";
  }

  public String apply2CheckAjax() {
    Integer examSubjectId = getInt("examSubjectId");
    if (null == examSubjectId) {
      put("isOk", false);
      return forward();
    }

    OqlBuilder<ExternExamSubjectSetting> builder = OqlBuilder.from(ExternExamSubjectSetting.class, "setting");
    builder.where("setting.examSubject.id = :examSubjectId", examSubjectId);
    Date now = new Date(System.currentTimeMillis());
    builder.where("setting.beginOn <= :now", now);
    builder.where(":now <= setting.endOn or setting.endOn is null", now);
    put("isOk", !entityDao.search(builder).isEmpty());
    return forward();
  }

  private String apply3(Long stdId) {
    put("std", entityDao.get(Student.class, stdId));
    ExternExamSubject examSubject = entityDao.get(ExternExamSubject.class, getIntId("examSubject"));
    put("examSubject", examSubject);

    try {
      ExternExamSubjectSetting setting = entityDao.get(ExternExamSubjectSetting.class, "examSubject",
          examSubject).get(0);
      put("setting", setting);
      List<ExternExamSubjectField> fields = CollectUtils.newArrayList();
      for (String var : IdentificationConst.certFields) {
        for (ExternExamSubjectField field : setting.getRequestFields()) {
          if (StringUtils.equals(var, field.getInnerField())) {
            fields.add(field);
            break;
          }
        }
      }
      put("fields", fields);
      return "apply3";
    } catch (Exception e) {
      e.printStackTrace();
      return forwardError("error.illegal");
    }
  }

  protected static <T> void settingValue(List<T> fields, int index, T value) {
    if (index < fields.size()) {
      fields.set(index, value);
    } else {
      for (int i = fields.size(); i <= index; i++) {
        fields.add(i, null);
      }
      fields.set(index, value);
    }
  }

  private String apply4(Long stdId, Integer settingId) {
    Student std = entityDao.get(Student.class, stdId);

    // 首先根据条件看看数据库中是否已经有该学生此证书了
    ExternExamSubjectSetting setting = entityDao.get(ExternExamSubjectSetting.class, settingId);

    OqlBuilder<Certificate> builder = OqlBuilder.from(Certificate.class, "certificate");
    builder.where("certificate.type.examSubject.id = :examSubjectId", getIntId("examSubject"));
    Map<String, Object> outerValueMap = CollectUtils.newHashMap();
    List<Object> innerValues = CollectUtils.newArrayList();
    for (ExternExamSubjectField field : setting.getRequestFields()) {
      if (StringUtils.equals(IdentificationConst.ID_NUMBER, field.getInnerField())) {
        outerValueMap.put(field.getOuterField(), std.getPerson().getCode());
      } else if (StringUtils.equals(IdentificationConst.STD_NAME, field.getInnerField())) {
        outerValueMap.put(field.getOuterField(), std.getUser().getName());
      } else {
        String var = IdentificationConst.saveFieldMap.get(field.getInnerField());
        if (StringUtils.isNotBlank(var)) {
          Integer id = getIntId(field.getInnerField());
          builder.where("certificate." + var + ".id = :" + field.getInnerField() + "Id or certificate." + var
              + " is null", id);
          innerValues.add(new Object[] { field.getInnerField(), id });
          outerValueMap.put(field.getOuterField(), loadValue(field.getInnerField(), id));
        }
      }
    }
    Certificate certificate = entityDao.search(builder).get(0);
    put("certificate", certificate);

    put("certifications",
        entityDao.get(Certification.class, new String[] { "std", "certificate" }, new Object[] { std,
            certificate }));
    put("std", std);
    put("setting", setting);
    put("innerValues", innerValues); // 如果当前学生没有取得证书，则提供为学生已经选择的数据，供前台显示
    put("outerValueMap", outerValueMap);
    return "apply4";
  }

  private Object loadValue(String innerField, Integer id) {
    OqlBuilder<String> builder = OqlBuilder.from(CERT_ELEMENT_OBJECT_MAP.get(innerField), innerField);
    builder.where(innerField + ".id = :id", id);
    builder.select("code");
    return entityDao.search(builder).get(0);
  }

  public String apply4ResultAjax() {
    boolean isOk = false;

    ExternExamSubjectSetting setting = entityDao.get(ExternExamSubjectSetting.class, getInt("settingId"));
    Student std = null;
    Certificate certificate = null; // 证书大类

    // FIXME 2018-06-14 zhouqi 这里暂时只针对一个学校中一个学生只有一个学籍信息
    // FIXME 2018-06-14 zhouqi 还是这里不确定是否真的会反馈身份证号，所以这里假设会反馈
    String idNumber = get(setting.getResponseField(IdentificationConst.ID_NUMBER).getOuterField());
    String certCode = get(setting.getResponseField(IdentificationConst.CERT_CODE).getOuterField());
    String score = get(setting.getResponseField(IdentificationConst.SCORE).getOuterField());

    // 是否真的获得了证书：没有获得，if ；否则 else
    List<Object> certItems = CollectUtils.newArrayList(); // 证书明细
    if (StringUtils.isBlank(idNumber) || StringUtils.isBlank(certCode) || StringUtils.isBlank(score)) {
      std = entityDao.get(Student.class, getLongId("std"));
      certificate = entityDao.get(Certificate.class, getIntId("certificate"));

      for (String var : IdentificationConst.certFields) {
        for (ExternExamSubjectField field : setting.getRequestFields()) {
          if (StringUtils.equals(var, field.getInnerField())) {
            certItems.add(new Object[] {
                field,
                entityDao.get(CERT_ELEMENT_OBJECT_MAP.get(field.getInnerField()),
                    getIntId(field.getInnerField())) });
            break;
          }
        }
      }
    } else {
      isOk = true;

      std = entityDao.get(Student.class, "person.code", idNumber).get(0);
      put("certCode", certCode);
      put("score", score);

      OqlBuilder<Certificate> builder = OqlBuilder.from(Certificate.class, "certificate");
      for (String innerField : IdentificationConst.certFields) {
        for (ExternExamSubjectField field : setting.getRequestFields()) {
          if (StringUtils.equals(innerField, field.getInnerField())) {
            String var = IdentificationConst.saveFieldMap.get(field.getInnerField());
            String code = get(field.getOuterField());
            builder.where("certificate." + var + ".code = :" + var + "Code or certificate." + var
                + " is null", code);
            certItems.add(new Object[] { field,
                entityDao.get(CERT_ELEMENT_OBJECT_MAP.get(field.getInnerField()), "code", code).get(0) });
            break;
          }
        }
      }

      certificate = entityDao.search(builder).get(0);
    }

    put("std", std);
    put("certificate", certificate);
    put("certItems", certItems);
    put("isOk", isOk);

    return forward();
  }

  public String applySave() {
    Certification certification = new Certification();
    certification.setStd(entityDao.get(Student.class, getLongId("std")));
    certification.setCertificate(entityDao.get(Certificate.class, getIntId("certificate")));
    certification.setCode(get("certCode"));
    certification.setScore(get("score"));
    certification.setHappenBy(getUsername());
    certification.setHappenAt(new Date(System.currentTimeMillis()));
    entityDao.saveOrUpdate(certification);
    return redirect("search", "info.save.success");
  }

  /**
   * 分配<br>
   * (FIXME 2018-11-16 zhouqi 暂且合在一起)
   *
   * @return
   */
  public String distributeIndex() {
    put("certification", entityDao.get(Certification.class, getIntId("certification")));
    return forward();
  }

  public String distributeList() {
    put("courses", entityDao.get(CertificationCourse.class, "certification.id", getIntId("certification")));
    return forward();
  }

  public String toDistribute() {
    OqlBuilder<CertScoreCourse> builder = OqlBuilder.from(CertScoreCourse.class, "certScoreCourse");
    StringBuilder hql = new StringBuilder();
    hql.append("exists (");
    hql.append("  from ").append(Certification.class.getName()).append(" certification");
    hql.append(" where certification.id = :certificationId");
    hql.append("   and certScoreCourse.certScore.certType = certification.certificate.type");
    hql.append("   and (certScoreCourse.certScore.certLevel is null or certScoreCourse.certScore.certLevel = certification.certificate.level)");
    hql.append("   and (certScoreCourse.certScore.division is null or certScoreCourse.certScore.division = certification.certificate.division)");
    hql.append("   and (certScoreCourse.certScore.examTime is null or certScoreCourse.certScore.examTime = certification.certificate.examTime)");
    hql.append("   and not exists (");
    hql.append("         from certification.courses certificationCourse");
    hql.append("        where certificationCourse.course = certScoreCourse.course");
    hql.append("       )");
    hql.append(")");
    builder.where(hql.toString(), getIntId("certification"));
    put("certScoreCourses", entityDao.search(builder));
    return forward();
  }

  public String distribute() {
    Certification certification = entityDao.get(Certification.class, getIntId("certification"));
    List<CertScoreCourse> certScoreCourses = entityDao.get(CertScoreCourse.class,
        getIntIds("certScoreCourse"));
    for (CertScoreCourse certScoreCourse : certScoreCourses) {
      certification.addCertificationCourse(certScoreCourse.getCourse(), certScoreCourse.getScore(),
          certScoreCourse.getScoreValue());
    }
    entityDao.saveOrUpdate(certification);
    return redirect("search", "info.action.success");
  }

  public String distributeActivate() {
    Certification certification = entityDao.get(Certification.class, getIntId("certification"));
    if (CollectionUtils.isEmpty(certification.getCourses())) {
      return forwardError("当前证书尚未进行分配，不能批准入库！");
    }
    // FIXME 2018-11-16 zhouqi 目前进行到考试网上的考试科目与本地如何对接了
    ExternExamGrade grade = new ExternExamGrade();
    grade.setStd(certification.getStd());
    List<CertScoreCourse> certScoreCourses = entityDao.get(CertScoreCourse.class,
        getIntIds("certScoreCourse"));
    for (CertScoreCourse certScoreCourse : certScoreCourses) {
      certification.addCertificationCourse(certScoreCourse.getCourse(), certScoreCourse.getScore(),
          certScoreCourse.getScoreValue());
    }
    entityDao.saveOrUpdate(certification);
    return redirect("search", "info.action.success");
  }
}
