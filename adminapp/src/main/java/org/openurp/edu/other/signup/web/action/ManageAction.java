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
package org.openurp.edu.other.signup.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.struts2.ServletActionContext;
import org.beangle.commons.bean.transformers.PropertyTransformer;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.Entity;
import org.beangle.commons.entity.metadata.Model;
import org.beangle.commons.lang.Strings;
import org.beangle.commons.transfer.excel.ExcelItemReader;
import org.beangle.commons.transfer.exporter.PropertyExtractor;
import org.beangle.commons.transfer.importer.EntityImporter;
import org.beangle.commons.transfer.importer.MultiEntityImporter;
import org.beangle.commons.transfer.importer.listener.ImporterForeignerListener;
import org.beangle.commons.web.util.RequestUtils;
import org.beangle.ems.avatar.model.FileAvatar;
import org.beangle.ems.avatar.service.AvatarBase;
import org.beangle.ems.util.ZipUtils;
import org.openurp.base.model.Campus;
import org.openurp.base.model.Semester;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.base.service.StudentService;
import org.openurp.edu.eams.web.util.DownloadHelper;
import org.openurp.edu.other.code.model.OtherExamCategory;
import org.openurp.edu.other.code.model.OtherExamSubject;
import org.openurp.edu.other.model.OtherExamFeeConfig;
import org.openurp.edu.other.model.OtherExamSignUp;
import org.openurp.edu.other.model.OtherExamSignUpConfig;
import org.openurp.edu.other.model.OtherExamSignUpLogger;
import org.openurp.edu.other.service.OtherExamFeeConfigService;
import org.openurp.edu.other.service.OtherExamPropertyExtractor;
import org.openurp.edu.other.service.OtherExamSignUpCalculator;
import org.openurp.edu.other.service.OtherExamSignUpLoggerService;
import org.openurp.edu.other.service.OtherExamSignUpService;
import org.openurp.edu.other.service.listener.OtherExamSignUpImportListener;
import org.openurp.edu.web.action.SemesterSupportAction;
import org.openurp.fee.code.model.PayState;
import org.openurp.fee.model.Bill;
import org.openurp.fee.model.BillLog.BillLogType;
import org.openurp.fee.service.BillCodeGenerator;
import org.openurp.fee.service.BillService;
import org.openurp.fee.service.PaymentService;
import org.openurp.fee.service.impl.BillGenContext;

import com.opensymphony.xwork2.ActionContext;

public class ManageAction extends SemesterSupportAction {

  protected OtherExamSignUpService otherExamSignUpService;

  protected StudentService studentService;

  protected AvatarBase avatarBase;

  protected OtherExamFeeConfigService otherExamFeeConfigService;

  protected BillCodeGenerator otherExamBillCodeGenerator;

  protected BillService billService;

  protected OtherExamSignUpLoggerService otherExamSignUpLoggerService;

  protected PaymentService paymentService;

  public String index() {
    put("stdTypeList", getStdTypes());
    put("departmentList", getColleges());
    getSemester();
    indexSetting();
    String info = get("info");
    put("info", info);
    put("Mname", get("Mname"));
    return forward();
  }

  protected void indexSetting() {
    OqlBuilder<OtherExamSignUpConfig> seasonQuery = OqlBuilder.from(OtherExamSignUpConfig.class, "season");
    seasonQuery.where("season.project = :project", getProject());
    seasonQuery.orderBy("season.beginAt desc");
    put("seasons", entityDao.search(seasonQuery));

    put("otherExamCategories", codeService.getCodes(OtherExamCategory.class));
    put("otherExamSubjects", codeService.getCodes(OtherExamSubject.class));
    put("campuses", baseInfoService.getBaseInfos(Campus.class, getProject().getSchool()));

    put("departments", getColleges());
    put("educations", getEducations());
    put("payStates", codeService.getCodes(PayState.class));
  }

  @Override
  public String search() {
    String info = get("info");
    if (null != info && !info.equals("")) {
      put("info", info);
    }
    put("payStates", entityDao.get(PayState.class, "id", PayState.PAID, PayState.UNPAID));
    put("canceled", entityDao.get(PayState.class, PayState.CANCEL));
    put("payOpen", !otherExamFeeConfigService.getOpenConfigs(getProject(), getSemester()).isEmpty());
    return super.search();
  }

  @Override
  protected OqlBuilder<?> getQueryBuilder() {
    OqlBuilder<?> builder = (OqlBuilder<?>) super.getQueryBuilder();
    Date signUpAt_start = getDate("signUpAt_start");
    if (null != signUpAt_start) {
      builder.where(getShortName() + ".signUpAt>=:start", signUpAt_start);
    }
    Date signUpAt_end = getDate("signUpAt_end");
    if (null != signUpAt_end) {
      builder.where(getShortName() + ".signUpAt<=:end", signUpAt_end);
    }
    builder.where("otherExamSignUp.std.project =:project", getProject());
    if (getInt("examType.id") != null) {

    }
    Integer semesterId = getInt("semester.id");
    if (null != semesterId) {
      builder.where("otherExamSignUp.semester.id = :semesterId", semesterId);
      getSemester();
    }
    if (getLong("fake.signupSeason.id") != null) {
      Long seasonId = getLong("fake.signupSeason.id");
      OtherExamSignUpConfig season = entityDao.get(OtherExamSignUpConfig.class, seasonId);
      builder.where("otherExamSignUp.signUpAt between :beginAt and :endAt", season.getBeginAt(),
          season.getEndAt());
    }
    return builder;
  }

  public String searchStudent() {
    String studentCode = get("studentCode");
    if (Strings.isNotEmpty(studentCode)) {
      Student student = studentService.getStudent( getProject().getId(),studentCode);
      if (null != student) {
        put("student", student);
      }
    }
    return forward();
  }

  public void editSetting(Entity<?> entity) {
    put("otherExamSubjects", codeService.getCodes(OtherExamSubject.class));
    put("campuses", baseInfoService.getBaseInfos(Campus.class, getProject().getSchool()));
    // Project project = getProject();
    // put("semesters", project.getCalendar().getSemesters());
    if (entity.isTransient()) {
      Semester semester = getSemester();
      if (null != semester) {
        ((OtherExamSignUp) entity).setSemester(semester);
      }
    }
  }

  public String updateBillState() {
    List<OtherExamSignUp> signUps = getModels(OtherExamSignUp.class, getLongIds("otherExamSignUp"));
    if (signUps.isEmpty()) {
      OqlBuilder<OtherExamSignUp> builder = OqlBuilder.from(OtherExamSignUp.class, "signUp").where(
          "(signUp.bill.state.id=" + PayState.UNPAID + ") or (signUp.payState.id=" + PayState.UNPAID
              + " and signUp.bill is not null)");
      signUps = entityDao.search(builder);
    }
    List<Bill> bills = CollectUtils.newArrayList();
    for (OtherExamSignUp otherExamSignUp : signUps) {
      Bill bill = otherExamSignUp.getBill();
      if (bill != null && bill.getState().getId().equals(PayState.UNPAID)
          && paymentService.checkBillOnPurpose(bill)) {
        bills.add(bill);
      }
    }
    try {
      paymentService.updatePayInfos(bills, BillLogType.PAID_ADMIN);
    } catch (Exception e) {
      return redirect("search", "更新失败");
    }
    return redirect("search", "更新了" + bills.size() + "条");
  }

  public String countNum() {
    OqlBuilder<OtherExamSignUp> builder = OqlBuilder.from(OtherExamSignUp.class, "signUp").where(
        "(signUp.bill.state.id=" + PayState.UNPAID + ") or (signUp.payState.id=" + PayState.UNPAID
            + " and signUp.bill is not null)");
    List<OtherExamSignUp> signUps = entityDao.search(builder);
    List<OtherExamSignUp> paids = CollectUtils.newArrayList();
    for (OtherExamSignUp otherExamSignUp : signUps) {
      if (paymentService.checkBillOnPurpose(otherExamSignUp.getBill())) {
        paids.add(otherExamSignUp);
      }
    }
    Workbook workbook = new HSSFWorkbook();
    Sheet sheet = workbook.createSheet("已支付未缴费名单");
    int i = 1;
    Row row = sheet.createRow(0);
    row.createCell(0).setCellValue("学号");
    row.createCell(1).setCellValue("姓名");
    row.createCell(2).setCellValue("订单号");
    row.createCell(3).setCellValue("应缴金额");
    row.createCell(4).setCellValue("已付金额");
    row.createCell(5).setCellValue("订单状态");
    row.createCell(6).setCellValue("报名记录状态");
    row.createCell(7).setCellValue("报名科目");
    row.createCell(8).setCellValue("报名日期");
    for (OtherExamSignUp otherExamSignUp : paids) {
      row = sheet.createRow(i);
      row.createCell(0).setCellValue(otherExamSignUp.getStd().getCode());
      row.createCell(1).setCellValue(otherExamSignUp.getStd().getPerson().getName());
      row.createCell(2).setCellValue(otherExamSignUp.getBill().getCode());
      row.createCell(3).setCellValue(otherExamSignUp.getBill().getAmount());
      row.createCell(4).setCellValue(otherExamSignUp.getBill().getPaid());
      row.createCell(5).setCellValue(otherExamSignUp.getBill().getState().getName());
      row.createCell(6).setCellValue(otherExamSignUp.getPayState().getName());
      row.createCell(7).setCellValue(otherExamSignUp.getSubject().getName());
      row.createCell(8).setCellValue(otherExamSignUp.getSignUpAt());
      i++;
    }
    HttpServletResponse response = getResponse();
    response.setContentType("application/vnd.ms-excel;charset=GBK");
    response.setHeader(
        "Content-Disposition",
        "attachment;filename="
            + RequestUtils.encodeAttachName(ServletActionContext.getRequest(), "已支付未更改状态名单.xls"));
    try {
      workbook.write(response.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  protected String saveAndForward(Entity<?> entity) {
    OtherExamSignUp signUp = (OtherExamSignUp) entity;
    Student student = null;
    if (Strings.isNotBlank(get("otherExamSignUp.std.code"))) {
      student = studentService.getStudent(getProject().getId(),get("otherExamSignUp.std.code"));
      signUp.setStd(student);
    }
    if (student == null) { return redirect("search", "保存失败,学号不存在"); }
    // 记录不能重复
    if (otherExamSignUpService.isExist(signUp)) { return redirect("search", "保存失败,报名重复"); }
    if (signUp.isTransient()) {
      signUp.setSignUpAt(new Date());
    }
    Semester semester = entityDao.get(Semester.class, getInt("otherExamSignUp.semester.id"));
    OtherExamSubject subject = entityDao.get(OtherExamSubject.class, getInt("otherExamSignUp.subject.id"));
    signUp.setSemester(semester);
    signUp.setSubject(subject);
    entityDao.saveOrUpdate(entity);

    List<OtherExamFeeConfig> configs = otherExamFeeConfigService.getOpenConfigs(getProject(), semester);
    if (configs.size() == 1) {
      OtherExamFeeConfig config = configs.get(0);
      if (config.getFeeType() != null) {
        BillGenContext context = BillGenContext.create(student, config.getFeeType(), semester,
            OtherExamSignUpCalculator.calOtherExamFee(signUp)).setRemark("PAYFOROTHEREXAM");
        context.put("otherExamSignUpConfig", config).setBillCodeGenerator(otherExamBillCodeGenerator);
        Bill bill = billService.genBill(config, context);
        signUp.setBill(bill);
        billService.saveOrUpdate(bill, BillLogType.CREATED, signUp);
      }
    }
    otherExamSignUpLoggerService.logger(getUsername(),
        OtherExamSignUpLogger.CREATE, getRemoteAddr(), signUp);
    boolean addNext = getBool("addNext");
    if (addNext) {
      getFlash().put("otherExamSignUp.semester.id", signUp.getSemester().getId());
      getFlash().put("otherExamSignUp.subject.id", signUp.getSubject().getId());
      return redirect("edit", "info.save.success");
    }
    return redirect("search", "info.save.success");
  }

  /**
   * 学校为了打印新增
   * 
   * @return
   */
  public String printShow() {
    OqlBuilder<?> builder = getQueryBuilder();
    builder.limit(null);
    put((new StringBuilder()).append(getShortName()).append("s").toString(), entityDao.search(builder));
    return forward();
  }

  /**
   * 得到按照班级分组的结果
   * 
   * @param queryList
   * @return
   */
  private Map<Object, List<Object>> groupByAdminclass(List<Object[]> queryList) {
    Map<Object, List<Object>> resultMap = CollectUtils.newHashMap();
    if (CollectUtils.isNotEmpty(queryList)) {
      for (int i = 0; i < queryList.size(); i++) {
        Object[] resultArr = queryList.get(i);
        if (resultMap.containsKey(resultArr[1])) {
          List<Object> otherExamSignUpList = resultMap.get(resultArr[1]);
          otherExamSignUpList.add(resultArr[0]);
          resultMap.put(resultArr[1], otherExamSignUpList);
        } else {
          List<Object> otherExamSignUpList = CollectUtils.newArrayList();
          otherExamSignUpList.add(resultArr[0]);
          resultMap.put(resultArr[1], otherExamSignUpList);
        }
      }
    }
    return resultMap;
  }

  /**
   * 导出excel(为了工技大个性化的导出,特写了原生态的excel导出)
   * 要求:所有字段均是字符型,同时入学年份,年级只要两位的年份
   * 确认是从index页面提交
   * 
   * @return
   */
  @Override
  protected Collection<?> getExportDatas() {
    OqlBuilder<?> builder = getQueryBuilder();
    builder.limit(null);
    return entityDao.search(builder);
  }

  public String getStd() {
    String code = get("stdCode");
    if (Strings.isNotBlank(code)) {
      Student std = (Student) entityDao.get(Student.class, "code", code);
      put("std", std);
    }
    return forward();
  }

  protected PropertyExtractor getPropertyExtractor() {
    return new OtherExamPropertyExtractor(getTextResource());
  }

  @SuppressWarnings("unchecked")
  @Override
  protected String removeAndForward(Collection<?> entities) {
    List<OtherExamSignUp> otherExamSignUps = (List<OtherExamSignUp>) entities;
    List<OtherExamSignUp> toRemoved = CollectUtils.newArrayList();
    List<Bill> toCancledBills = CollectUtils.newArrayList();
    for (OtherExamSignUp otherExamSignUp : otherExamSignUps) {
      Bill bill = otherExamSignUp.getBill();
      if (bill == null) {
        if (!PayState.PAID.equals(otherExamSignUp.getPayState().getId())) {
          toRemoved.add(otherExamSignUp);
        }
      } else if (bill.getState().getId().equals(PayState.UNPAID)
          && otherExamFeeConfigService.getOpenConfigs(otherExamSignUp.getStd().getProject(),
              otherExamSignUp.getSemester()).isEmpty() && !paymentService.checkBillOnPurpose(bill)) {
        toCancledBills.add(bill);
        otherExamSignUp.setBill(null);
        toRemoved.add(otherExamSignUp);
      }
    }
    try {
      billService.cancel(toCancledBills);
      remove(toRemoved);
      otherExamSignUpLoggerService.logger(getUsername(),
          OtherExamSignUpLogger.DELETE, getRemoteAddr(), toRemoved);
    } catch (Exception e) {
      logger.info("removeAndForwad failure", e);
      return redirect("search", "info.delete.failure");
    }
    return redirect("search", toRemoved.isEmpty() ? "已缴费报名或支付中报名无法删除" : "info.remove.success");
  }

  @Override
  protected String getEntityName() {
    return OtherExamSignUp.class.getName();
  }

  public String savePayState() {
    String signUpIdSeq = get("otherExamSignUpIds");
    if (Strings.isNotBlank(signUpIdSeq)) {
      OqlBuilder<OtherExamSignUp> query = OqlBuilder.from(OtherExamSignUp.class, "otherExamSignUp");
      query.where("otherExamSignUp.id in(:ids)", Strings.splitToLong(signUpIdSeq));
      List<OtherExamSignUp> signUps = entityDao.search(query);
      List<OtherExamSignUp> toChangeStates = CollectUtils.newArrayList();
      for (OtherExamSignUp otherExamSignUp : signUps) {
        if (null == otherExamSignUp.getBill()) {
          Integer payStateId = getInt("otherExamSignUp.payState.id");
          otherExamSignUp.setPayState(Model.newInstance(PayState.class, payStateId));
          toChangeStates.add(otherExamSignUp);
        }
      }
      try {
        entityDao.saveOrUpdate(toChangeStates);
      } catch (Exception e) {
        return redirect("search", "info.save.failure");
      }
      return redirect("search", "info.save.success");
    }
    return redirect("search", "info.save.failure");
  }

  public String categorySubject() {
    Integer categoryId = getInt("categoryIdId");
    if (null != categoryId) {
      put("subjects", entityDao.get(OtherExamSubject.class, "category.id", categoryId));
    } else {
      put("subjects", CollectionUtils.EMPTY_COLLECTION);
    }
    return forward();
  }

  @SuppressWarnings("unchecked")
  public String downloadAvatorBatch() {
    List<OtherExamSignUp> signUps = getModels(OtherExamSignUp.class, getLongIds("otherExamSignUp"));
    if (signUps.isEmpty()) {
      signUps = (List<OtherExamSignUp>) entityDao.search(getQueryBuilder().limit(null));
    }
    List<String> usernames = CollectUtils.collect(signUps, new PropertyTransformer("std.code"));
    List<String> filenames = CollectUtils.newArrayList();
    for (String username : usernames) {
      FileAvatar avatar = (FileAvatar) avatarBase.getAvatar(username);
      if (avatar != null) {
        filenames.add(avatar.getFile().toString());
      }
    }
    String tmpPath = System.getProperty("java.io.tmpdir") + "/eams-teach-other-photo.zip";
    ZipUtils.zip(filenames, tmpPath);
    File tmpAvatar = new File(tmpPath);
    DownloadHelper.download(getRequest(), getResponse(), tmpAvatar, "学生照片打包下载");
    tmpAvatar.delete();
    return null;
  }

  protected void configImporter(EntityImporter importer) {
    MultiEntityImporter mimporter = (MultiEntityImporter) importer;
    mimporter.addForeignedKeys("code");
    mimporter.addEntity("otherExam", OtherExamSignUp.class);

    ImporterForeignerListener l = new ImporterForeignerListener(entityDao);
    l.addForeigerKey("code");
    importer.addListener(l).addListener(new OtherExamSignUpImportListener(entityDao));
  }

  protected EntityImporter buildEntityImporter() {
    String upload = "importFile";
    try {
      File[] files = (File[]) ActionContext.getContext().getParameters().get(upload);
      if (files == null || files.length < 1) {
        logger.error("cannot get {} file.", upload);
      }
      String fileName = get(upload + "FileName");
      InputStream is = new FileInputStream(files[0]);
      if (fileName.endsWith(".xls")) {
        HSSFWorkbook wb = new HSSFWorkbook(is);
        if (wb.getNumberOfSheets() < 1 || wb.getSheetAt(0).getLastRowNum() == 0) { return null; }
        EntityImporter importer = new MultiEntityImporter();
        importer.setReader(new ExcelItemReader(wb, 1));
        put("importer", importer);
        return importer;
      } else {
        throw new RuntimeException("donot support other format except excel");
      }
    } catch (Exception e) {
      logger.error("error", e);
      return null;
    }
  }

  public void setOtherExamSignUpService(OtherExamSignUpService otherExamSignUpService) {
    this.otherExamSignUpService = otherExamSignUpService;
  }

  public void setStudentService(StudentService studentService) {
    this.studentService = studentService;
  }

  public void setAvatarBase(AvatarBase avatarBase) {
    this.avatarBase = avatarBase;
  }

  public void setOtherExamFeeConfigService(OtherExamFeeConfigService otherExamFeeConfigService) {
    this.otherExamFeeConfigService = otherExamFeeConfigService;
  }

  public void setOtherExamBillCodeGenerator(BillCodeGenerator otherExamBillCodeGenerator) {
    this.otherExamBillCodeGenerator = otherExamBillCodeGenerator;
  }

  public void setBillService(BillService billService) {
    this.billService = billService;
  }

  public void setOtherExamSignUpLoggerService(OtherExamSignUpLoggerService otherExamSignUpLoggerService) {
    this.otherExamSignUpLoggerService = otherExamSignUpLoggerService;
  }

  public void setPaymentService(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

}
