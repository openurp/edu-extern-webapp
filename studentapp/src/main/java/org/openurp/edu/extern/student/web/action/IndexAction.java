/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright (c) 2005, The OpenURP Software.
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
package org.openurp.edu.extern.student.web.action;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.beangle.commons.bean.transformers.PropertyTransformer;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.metadata.Model;
import org.beangle.commons.lang.Strings;
import org.beangle.security.codec.EncryptUtil;
import org.openurp.app.Urp;
import org.openurp.base.model.Campus;
import org.openurp.base.model.Semester;
import org.openurp.edu.base.model.Project;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExamFeeConfig;
import org.openurp.edu.extern.model.ExamSignup;
import org.openurp.edu.extern.model.ExamSignupConfig;
import org.openurp.edu.extern.model.ExamSignupLogger;
import org.openurp.edu.extern.model.ExamSignupSetting;
import org.openurp.edu.extern.model.ExternExamGrade;
import org.openurp.edu.extern.service.ExamFeeConfigService;
import org.openurp.edu.extern.service.ExamSignupCalculator;
import org.openurp.edu.extern.service.ExamSignupLoggerService;
import org.openurp.edu.extern.service.ExamSignupService;
import org.openurp.edu.extern.service.checker.ExamSignupChecker;
import org.openurp.edu.extern.service.checker.ExamSuperCategoryChecker;
import org.openurp.edu.web.action.StudentProjectSupport;
import org.openurp.fee.code.model.PayState;
import org.openurp.fee.model.Bill;
import org.openurp.fee.model.BillLog.BillLogType;
import org.openurp.fee.service.BillCodeGenerator;
import org.openurp.fee.service.BillService;
import org.openurp.fee.service.PaymentChecker;
import org.openurp.fee.service.PaymentService;
import org.openurp.fee.service.impl.BillGenContext;
import org.openurp.fee.service.impl.PaymentContext;

/**
 * 学生进行资格考试报名
 *
 * @author chaostone
 */
public class IndexAction extends StudentProjectSupport {

  protected ExamSignupChecker examSignupChecker;

  protected ExamSignupService examSignupService;

  protected ExamSignupLoggerService examSignupLoggerService;

  protected BillCodeGenerator examBillCodeGenerator;

  protected ExamFeeConfigService examFeeConfigService;

  private ExamSuperCategoryChecker examSuperCategoryChecker;

  protected PaymentService paymentService;

  protected BillService billService;

  protected List<PaymentChecker> checkers = CollectUtils.newArrayList();

  @Override
  protected String getEntityName() {
    return ExamSignup.class.getName();
  }

  @Override
  public String innerIndex() {
    Student std = getLoginStudent();
    OqlBuilder<ExamSignup> builder = OqlBuilder.from(ExamSignup.class, "signup").where(
        "signup.std =:std", std);
    List<ExamSignup> signupList = entityDao.search(builder);
    for (ExamSignup examSignup : signupList) {
      Bill bill = examSignup.getBill();
      if (null != bill && bill.getState().getId().equals(PayState.UNPAID)) {
        if (paymentService.checkBillOnPurpose(bill)) {
          paymentService.updatePayInfo(bill, BillLogType.PAID_SEARCH);
        }
      } else if (null != bill && bill.getState().getId().equals(PayState.PAID)
          && examSignup.getPayState().getId().equals(PayState.UNPAID)) {
        if (paymentService.checkBillOnPurpose(bill)) {
          examSignup.setPayState(Model.newInstance(PayState.class, PayState.PAID));
          entityDao.save(examSignup);
        }
      }
    }
    // 查询已有成绩
    OqlBuilder<ExternExamGrade> gradeBuilder = OqlBuilder.from(ExternExamGrade.class, "grade").where("grade.std =:std",
        std);
    put("grades", entityDao.search(gradeBuilder));
    put("signups", signupList);
    put("unpaid", PayState.UNPAID);
    return forward();
  }

  /**
   * 列举出可以报名的期号设置(操作第一步)
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return @
   */
  public String configList() {
    Student student = getLoginStudent();
    // 可以开放的期号设置
    List<ExamSignupConfig> configs = examSignupService.getOpenedConfigs(student.getProject());
    // 本次报名记录
    List<ExamSignup> signupList = new ArrayList<ExamSignup>();
    for (Iterator<ExamSignupConfig> iter = configs.iterator(); iter.hasNext();) {
      ExamSignupConfig config = (ExamSignupConfig) iter.next();
      List<ExamSignup> signups = examSignupService.getSignups(student, config);
      signupList.addAll(signups);
    }
    for (ExamSignup examSignup : signupList) {
      Bill bill = examSignup.getBill();
      if (null != bill && bill.getState().getId().equals(PayState.UNPAID)) {
        if (paymentService.checkBillOnPurpose(bill)) {
          paymentService.updatePayInfo(bill, BillLogType.PAID_SEARCH);
        }
      } else if (null != bill && bill.getState().getId().equals(PayState.PAID)
          && examSignup.getPayState().getId().equals(PayState.UNPAID)) {
        boolean paid = paymentService.checkBillOnPurpose(bill);
        if (paid) {
          examSignup.setPayState(Model.newInstance(PayState.class, PayState.PAID));
          entityDao.save(examSignup);
        }
      }
    }
    if (!signupList.isEmpty()) {
      put("signupSubjects", CollectUtils.collect(signupList, new PropertyTransformer("subject")));
    }
    // 查询已有成绩
    List<ExternExamGrade> grades = entityDao.get(ExternExamGrade.class, "std", student);
    Set<ExamSubject> passedSubjects = CollectUtils.newHashSet();
    for (ExternExamGrade examGrade : grades) {
      if (examGrade.isPassed()) {
        passedSubjects.add(examGrade.getSubject());
      }
    }
    put("passedSubjects", passedSubjects);
    put("signupList", signupList);
    put("configs", configs);
    put("student", student);
    Project project = student.getProject();
    put("feeOpen", !examFeeConfigService
        .getOpenConfigs(project, semesterService.getCurSemester(project)).isEmpty());
    put("unpaid", PayState.UNPAID);
    put("avatarUrl", Urp.Instance.getServicePath("/sns/photo/"
        + EncryptUtil.encode(getUsername() + "@" + student.getProject().getSchool().getCode() + ".edu.cn")
        + ".jpg"));
    return forward();
  }

  /**
   * 显示报名须知(操作第二步)
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return @
   */
  public String notice() {
    ExamSignupSetting setting = (ExamSignupSetting) getEntity(ExamSignupSetting.class,
        "setting");
    Student student = getLoginStudent();
    if (setting.getSuperSubject() != null) {
      String message = examSuperCategoryChecker.check(student, setting);
      if (null != message) {
        addFlashError(message);
        return redirect("configList");
      }
    }

    ExamSignupConfig config = setting.getConfig();
    List<ExamSignupConfig> configs = examSignupService.getOpenedConfigs(student.getProject());
    if (!configs.contains(config)) {
      addFlashError("你不能报名此考试科目");
      return redirect("configList");
    }
    put("config", config);
    return forward();
  }

  /**
   * 显示报名数据表单(操作第三步)
   */
  public String signupForm() {
    Student student = getLoginStudent();
    ExamSignupSetting setting = (ExamSignupSetting) getEntity(ExamSignupSetting.class,
        "setting");
    List<ExamSignupConfig> configs = examSignupService.getOpenedConfigs(student.getProject());
    if (!configs.contains(setting.getConfig())) {
      addFlashError("你不能报名此考试科目");
      return redirect("configList");
    }
    String msg = "";
    if (!setting.getPermitStds().contains(student)) {
      if (setting.getForbiddenStds().contains(student)) {
        addFlashError("你不能报名此考试科目");
        return redirect("configList");
      }
      msg = examSignupService.canSignup(student, setting);
    }
    if (Strings.isNotEmpty(msg)) {
      addFlashError(msg);
      return redirect("configList");
    }
    put("campuses", baseInfoService.getBaseInfos(Campus.class, getProject().getSchool()));
    put("setting", setting);
    put("student", student);
    return forward();
  }

  /**
   * 提交报名数据(操作第四步)
   */
  public String save() {
    Student student = getLoginStudent();
    ExamSignup signup = populateEntity(ExamSignup.class, getShortName());
    if (signup.isPersisted()) {
      ExamSignup persistedSignup = entityDao.get(ExamSignup.class, signup.getId());
      if (!(student.equals(persistedSignup.getStd()) && null != persistedSignup.getStd() && persistedSignup
          .getStd().equals(signup.getStd()))) { return forwardError("非法操作!"); }
    }
    ExamSignupSetting setting = (ExamSignupSetting) entityDao.get(ExamSignupSetting.class,
        getLong("setting.id"));
    signup.setStd(student);
    Boolean needMaterial = getBoolean("needMaterial");
    if (Boolean.TRUE.equals(needMaterial)) {
      signup.setFeeOfMaterial(setting.getFeeOfMaterial());
    }
    Boolean needOutline = getBoolean("needOutline");
    if (Boolean.TRUE.equals(needOutline)) {
      signup.setFeeOfOutline(setting.getFeeOfOutline());
    }
    signup.setFeeOfSignup(setting.getFeeOfSignup());
    signup.setSignupAt(new Timestamp(System.currentTimeMillis()));
    Boolean takeBus = getBoolean("examSignup.takeBus");
    if (Boolean.TRUE.equals(takeBus)) {
      signup.setTakeBus(takeBus);
    }
    String msg = "";
    Project project = student.getProject();
    Semester semester = semesterService.getCurSemester(project);
    msg = examSignupService.signup(signup, setting);
    List<ExamFeeConfig> configs = examFeeConfigService.getOpenConfigs(project, semester);
    if (configs.size() == 1 && Strings.isEmpty(msg)) {
      ExamFeeConfig config = configs.get(0);
      if (config.getFeeType() != null) {
        BillGenContext context = BillGenContext.create(student, config.getFeeType(), semester,
            ExamSignupCalculator.calExamFee(signup)).setRemark("PAYFOROTHEREXAM");
        context.put("examSignupConfig", config).setBillCodeGenerator(examBillCodeGenerator);
        Bill bill = billService.genBill(config, context);
        signup.setBill(bill);
        billService.saveOrUpdate(bill, BillLogType.CREATED, signup);
        msg = "info.signup.success";
        String remoteAddr = getRemoteAddr();
        examSignupLoggerService.logger(signup.getStd().getUser().getCode(), ExamSignupLogger.CREATE,
            remoteAddr, signup);
      } else {
        msg = "收费项目尚未设置";
      }
    }
    return redirect("configList", Strings.isEmpty(msg) ? "info.signup.success" : msg);
  }

  public String cancelSignup() {
    ExamSignup signup = getEntity(ExamSignup.class, "signup");
    Student student = getLoginStudent();
    if (signup.isPersisted()) {
      List<ExamSignupConfig> configs = examSignupService.getOpenedConfigs(student.getProject());
      boolean openConfig = false;
      for (ExamSignupConfig examSignupConfig : configs) {
        if (examSignupService.getSignups(student, examSignupConfig).contains(signup)) {
          openConfig = true;
          break;
        }
      }
      if (openConfig && signup.getStd().equals(student)) {
        Bill bill = signup.getBill();
        if (null != bill) {
          if (PayState.UNPAID.equals(bill.getState().getId()) && !paymentService.checkBillOnPurpose(bill)) {
            bill.setState(Model.newInstance(PayState.class, PayState.CANCEL));
            billService.cancel(bill);
            signup.setBill(null);
          } else {
            return redirect("configList", "取消报名失败!");
          }
        }
        entityDao.remove(signup);
        String remoteAddr = getRemoteAddr();
        examSignupLoggerService.logger(signup.getStd().getUser().getCode(), ExamSignupLogger.DELETE,
            remoteAddr, signup);
        return redirect("configList", "取消报名成功!");
      }
    }
    return redirect("configList", "取消报名失败!");
  }

  public void setExamSignupService(ExamSignupService examSignupService) {
    this.examSignupService = examSignupService;
  }

  public void setExamSignupLoggerService(ExamSignupLoggerService examSignupLoggerService) {
    this.examSignupLoggerService = examSignupLoggerService;
  }

  public void setExamSignupChecker(ExamSignupChecker examSignupChecker) {
    this.examSignupChecker = examSignupChecker;
  }

  protected void preparePaymentContext(PaymentContext context) {
    Long signupId = getLong("signupId");
    if (null != signupId) {
      ExamSignup signup = entityDao.get(ExamSignup.class, signupId);
      Project project = signup.getStd().getProject();
      Semester semester = signup.getSemester();
      context.put("student", getLoginStudent());
      context.put("semester", semester);
      context.put("project", project);
      context.put("feeConfigs", examFeeConfigService.getOpenConfigs(project, semester));
      context.put("billId", signup.getBill().getId());
      context.setBill(signup.getBill());
    }
    String remoteAddr = getRemoteAddr();
    context.put("remoteAddr", remoteAddr);
  }

  public String payment() {
    PaymentContext context = PaymentContext.create();
    context.put("request", getRequest());
    context.put("response", getResponse());
    Long signupId = getLong("signupId");
    if (null != signupId) {
      ExamSignup signup = entityDao.get(ExamSignup.class, signupId);
      if (signup.getBill() == null) { return redirect("configList", "此报名记录未生成订单。请取消后重新报名"); }
    }
    preparePaymentContext(context);
    context.put("paymentAction", this.getClass());
    context.put("returnUrl", paymentService.getReturnUrl(context));
    for (PaymentChecker checker : checkers) {
      String msg = checker.check(context);
      if (Strings.isNotBlank(msg)) {
        put("paymentCheckMsg", msg);
        return forward();
      }
    }
    put("bill", context.getBill());
    put("payParams", paymentService.getPaymentParams(context));
    put("paymentUrl", paymentService.getPaymentUrl(context));
    put("paymentActionURL",
        Strings.substringBeforeLast(Strings.uncapitalize(this.getClass().getSimpleName()), "Action"));
    return forward();
  }

  public void setExamFeeConfigService(ExamFeeConfigService examFeeConfigService) {
    this.examFeeConfigService = examFeeConfigService;
  }

  public void setExamBillCodeGenerator(BillCodeGenerator examBillCodeGenerator) {
    this.examBillCodeGenerator = examBillCodeGenerator;
  }

  public void setPaymentService(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  public void setBillService(BillService billService) {
    this.billService = billService;
  }

  public void setCheckers(List<PaymentChecker> checkers) {
    this.checkers = checkers;
  }

  public void setExamSuperCategoryChecker(ExamSuperCategoryChecker examSuperCategoryChecker) {
    this.examSuperCategoryChecker = examSuperCategoryChecker;
  }

}
