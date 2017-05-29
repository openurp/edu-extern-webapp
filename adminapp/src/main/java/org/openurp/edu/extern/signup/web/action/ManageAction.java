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
package org.openurp.edu.extern.signup.web.action;

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
import org.openurp.edu.extern.code.model.ExamCategory;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExamFeeConfig;
import org.openurp.edu.extern.model.ExamSignup;
import org.openurp.edu.extern.model.ExamSignupConfig;
import org.openurp.edu.extern.model.ExamSignupLogger;
import org.openurp.edu.extern.service.ExamFeeConfigService;
import org.openurp.edu.extern.service.ExamPropertyExtractor;
import org.openurp.edu.extern.service.ExamSignupCalculator;
import org.openurp.edu.extern.service.ExamSignupLoggerService;
import org.openurp.edu.extern.service.ExamSignupService;
import org.openurp.edu.extern.service.listener.ExamSignupImportListener;
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

  protected ExamSignupService examSignupService;

  protected StudentService studentService;

  protected AvatarBase avatarBase;

  protected ExamFeeConfigService examFeeConfigService;

  protected BillCodeGenerator examBillCodeGenerator;

  protected BillService billService;

  protected ExamSignupLoggerService examSignupLoggerService;

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
    OqlBuilder<ExamSignupConfig> seasonQuery = OqlBuilder.from(ExamSignupConfig.class, "season");
    seasonQuery.where("season.project = :project", getProject());
    seasonQuery.orderBy("season.beginAt desc");
    put("seasons", entityDao.search(seasonQuery));

    put("examCategories", codeService.getCodes(ExamCategory.class));
    put("examSubjects", codeService.getCodes(ExamSubject.class));
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
    put("payOpen", !examFeeConfigService.getOpenConfigs(getProject(), getSemester()).isEmpty());
    return super.search();
  }

  @Override
  protected OqlBuilder<?> getQueryBuilder() {
    OqlBuilder<?> builder = (OqlBuilder<?>) super.getQueryBuilder();
    Date signupAt_start = getDate("signupAt_start");
    if (null != signupAt_start) {
      builder.where(getShortName() + ".signupAt>=:start", signupAt_start);
    }
    Date signupAt_end = getDate("signupAt_end");
    if (null != signupAt_end) {
      builder.where(getShortName() + ".signupAt<=:end", signupAt_end);
    }
    builder.where("examSignup.std.project =:project", getProject());
    if (getInt("examType.id") != null) {

    }
    Integer semesterId = getInt("semester.id");
    if (null != semesterId) {
      builder.where("examSignup.semester.id = :semesterId", semesterId);
      getSemester();
    }
    if (getLong("fake.signupSeason.id") != null) {
      Long seasonId = getLong("fake.signupSeason.id");
      ExamSignupConfig season = entityDao.get(ExamSignupConfig.class, seasonId);
      builder.where("examSignup.signupAt between :beginAt and :endAt", season.getBeginAt(),
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
    put("examSubjects", codeService.getCodes(ExamSubject.class));
    put("campuses", baseInfoService.getBaseInfos(Campus.class, getProject().getSchool()));
    // Project project = getProject();
    // put("semesters", project.getCalendar().getSemesters());
    if (entity.isTransient()) {
      Semester semester = getSemester();
      if (null != semester) {
        ((ExamSignup) entity).setSemester(semester);
      }
    }
  }

  public String updateBillState() {
    List<ExamSignup> signups = getModels(ExamSignup.class, getLongIds("examSignup"));
    if (signups.isEmpty()) {
      OqlBuilder<ExamSignup> builder = OqlBuilder.from(ExamSignup.class, "signup").where(
          "(signup.bill.state.id=" + PayState.UNPAID + ") or (signup.payState.id=" + PayState.UNPAID
              + " and signup.bill is not null)");
      signups = entityDao.search(builder);
    }
    List<Bill> bills = CollectUtils.newArrayList();
    for (ExamSignup examSignup : signups) {
      Bill bill = examSignup.getBill();
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
    OqlBuilder<ExamSignup> builder = OqlBuilder.from(ExamSignup.class, "signup").where(
        "(signup.bill.state.id=" + PayState.UNPAID + ") or (signup.payState.id=" + PayState.UNPAID
            + " and signup.bill is not null)");
    List<ExamSignup> signups = entityDao.search(builder);
    List<ExamSignup> paids = CollectUtils.newArrayList();
    for (ExamSignup examSignup : signups) {
      if (paymentService.checkBillOnPurpose(examSignup.getBill())) {
        paids.add(examSignup);
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
    for (ExamSignup examSignup : paids) {
      row = sheet.createRow(i);
      row.createCell(0).setCellValue(examSignup.getStd().getUser().getCode());
      row.createCell(1).setCellValue(examSignup.getStd().getPerson().getName());
      row.createCell(2).setCellValue(examSignup.getBill().getCode());
      row.createCell(3).setCellValue(examSignup.getBill().getAmount());
      row.createCell(4).setCellValue(examSignup.getBill().getPaid());
      row.createCell(5).setCellValue(examSignup.getBill().getState().getName());
      row.createCell(6).setCellValue(examSignup.getPayState().getName());
      row.createCell(7).setCellValue(examSignup.getSubject().getName());
      row.createCell(8).setCellValue(examSignup.getSignupAt());
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
    ExamSignup signup = (ExamSignup) entity;
    Student student = null;
    if (Strings.isNotBlank(get("examSignup.std.user.code"))) {
      student = studentService.getStudent(getProject().getId(),get("examSignup.std.user.code"));
      signup.setStd(student);
    }
    if (student == null) { return redirect("search", "保存失败,学号不存在"); }
    // 记录不能重复
    if (examSignupService.isExist(signup)) { return redirect("search", "保存失败,报名重复"); }
    if (signup.isTransient()) {
      signup.setSignupAt(new Date());
    }
    Semester semester = entityDao.get(Semester.class, getInt("examSignup.semester.id"));
    ExamSubject subject = entityDao.get(ExamSubject.class, getInt("examSignup.subject.id"));
    signup.setSemester(semester);
    signup.setSubject(subject);
    entityDao.saveOrUpdate(entity);

    List<ExamFeeConfig> configs = examFeeConfigService.getOpenConfigs(getProject(), semester);
    if (configs.size() == 1) {
      ExamFeeConfig config = configs.get(0);
      if (config.getFeeType() != null) {
        BillGenContext context = BillGenContext.create(student, config.getFeeType(), semester,
            ExamSignupCalculator.calExamFee(signup)).setRemark("PAYFOROTHEREXAM");
        context.put("examSignupConfig", config).setBillCodeGenerator(examBillCodeGenerator);
        Bill bill = billService.genBill(config, context);
        signup.setBill(bill);
        billService.saveOrUpdate(bill, BillLogType.CREATED, signup);
      }
    }
    examSignupLoggerService.logger(getUsername(),
        ExamSignupLogger.CREATE, getRemoteAddr(), signup);
    boolean addNext = getBool("addNext");
    if (addNext) {
      getFlash().put("examSignup.semester.id", signup.getSemester().getId());
      getFlash().put("examSignup.subject.id", signup.getSubject().getId());
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
          List<Object> examSignupList = resultMap.get(resultArr[1]);
          examSignupList.add(resultArr[0]);
          resultMap.put(resultArr[1], examSignupList);
        } else {
          List<Object> examSignupList = CollectUtils.newArrayList();
          examSignupList.add(resultArr[0]);
          resultMap.put(resultArr[1], examSignupList);
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
    return new ExamPropertyExtractor(getTextResource());
  }

  @SuppressWarnings("unchecked")
  @Override
  protected String removeAndForward(Collection<?> entities) {
    List<ExamSignup> examSignups = (List<ExamSignup>) entities;
    List<ExamSignup> toRemoved = CollectUtils.newArrayList();
    List<Bill> toCancledBills = CollectUtils.newArrayList();
    for (ExamSignup examSignup : examSignups) {
      Bill bill = examSignup.getBill();
      if (bill == null) {
        if (!PayState.PAID.equals(examSignup.getPayState().getId())) {
          toRemoved.add(examSignup);
        }
      } else if (bill.getState().getId().equals(PayState.UNPAID)
          && examFeeConfigService.getOpenConfigs(examSignup.getStd().getProject(),
              examSignup.getSemester()).isEmpty() && !paymentService.checkBillOnPurpose(bill)) {
        toCancledBills.add(bill);
        examSignup.setBill(null);
        toRemoved.add(examSignup);
      }
    }
    try {
      billService.cancel(toCancledBills);
      remove(toRemoved);
      examSignupLoggerService.logger(getUsername(),
          ExamSignupLogger.DELETE, getRemoteAddr(), toRemoved);
    } catch (Exception e) {
      logger.info("removeAndForwad failure", e);
      return redirect("search", "info.delete.failure");
    }
    return redirect("search", toRemoved.isEmpty() ? "已缴费报名或支付中报名无法删除" : "info.remove.success");
  }

  @Override
  protected String getEntityName() {
    return ExamSignup.class.getName();
  }

  public String savePayState() {
    String signupIdSeq = get("examSignupIds");
    if (Strings.isNotBlank(signupIdSeq)) {
      OqlBuilder<ExamSignup> query = OqlBuilder.from(ExamSignup.class, "examSignup");
      query.where("examSignup.id in(:ids)", Strings.splitToLong(signupIdSeq));
      List<ExamSignup> signups = entityDao.search(query);
      List<ExamSignup> toChangeStates = CollectUtils.newArrayList();
      for (ExamSignup examSignup : signups) {
        if (null == examSignup.getBill()) {
          Integer payStateId = getInt("examSignup.payState.id");
          examSignup.setPayState(Model.newInstance(PayState.class, payStateId));
          toChangeStates.add(examSignup);
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
      put("subjects", entityDao.get(ExamSubject.class, "category.id", categoryId));
    } else {
      put("subjects", CollectionUtils.EMPTY_COLLECTION);
    }
    return forward();
  }

  @SuppressWarnings("unchecked")
  public String downloadAvatorBatch() {
    List<ExamSignup> signups = getModels(ExamSignup.class, getLongIds("examSignup"));
    if (signups.isEmpty()) {
      signups = (List<ExamSignup>) entityDao.search(getQueryBuilder().limit(null));
    }
    List<String> usernames = CollectUtils.collect(signups, new PropertyTransformer("std.user.code"));
    List<String> filenames = CollectUtils.newArrayList();
    for (String username : usernames) {
      FileAvatar avatar = (FileAvatar) avatarBase.getAvatar(username);
      if (avatar != null) {
        filenames.add(avatar.getFile().toString());
      }
    }
    String tmpPath = System.getProperty("java.io.tmpdir") + "/openurp-edu-extern-exam-photo.zip";
    ZipUtils.zip(filenames, tmpPath);
    File tmpAvatar = new File(tmpPath);
    DownloadHelper.download(getRequest(), getResponse(), tmpAvatar, "学生照片打包下载");
    tmpAvatar.delete();
    return null;
  }

  protected void configImporter(EntityImporter importer) {
    MultiEntityImporter mimporter = (MultiEntityImporter) importer;
    mimporter.addForeignedKeys("code");
    mimporter.addEntity("exam", ExamSignup.class);

    ImporterForeignerListener l = new ImporterForeignerListener(entityDao);
    l.addForeigerKey("code");
    importer.addListener(l).addListener(new ExamSignupImportListener(entityDao));
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
        throw new RuntimeException("donot support format except excel");
      }
    } catch (Exception e) {
      logger.error("error", e);
      return null;
    }
  }

  public void setExamSignupService(ExamSignupService examSignupService) {
    this.examSignupService = examSignupService;
  }

  public void setStudentService(StudentService studentService) {
    this.studentService = studentService;
  }

  public void setAvatarBase(AvatarBase avatarBase) {
    this.avatarBase = avatarBase;
  }

  public void setExamFeeConfigService(ExamFeeConfigService examFeeConfigService) {
    this.examFeeConfigService = examFeeConfigService;
  }

  public void setExamBillCodeGenerator(BillCodeGenerator examBillCodeGenerator) {
    this.examBillCodeGenerator = examBillCodeGenerator;
  }

  public void setBillService(BillService billService) {
    this.billService = billService;
  }

  public void setExamSignupLoggerService(ExamSignupLoggerService examSignupLoggerService) {
    this.examSignupLoggerService = examSignupLoggerService;
  }

  public void setPaymentService(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

}
