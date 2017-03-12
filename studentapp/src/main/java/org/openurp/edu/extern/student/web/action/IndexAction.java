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
import org.openurp.base.Urp;
import org.openurp.base.model.Campus;
import org.openurp.base.model.Semester;
import org.openurp.edu.base.model.Project;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExamFeeConfig;
import org.openurp.edu.extern.model.ExamSignUp;
import org.openurp.edu.extern.model.ExamSignUpConfig;
import org.openurp.edu.extern.model.ExamSignUpLogger;
import org.openurp.edu.extern.model.ExamSignUpSetting;
import org.openurp.edu.extern.model.ExamGrade;
import org.openurp.edu.extern.service.ExamFeeConfigService;
import org.openurp.edu.extern.service.ExamSignUpCalculator;
import org.openurp.edu.extern.service.ExamSignUpLoggerService;
import org.openurp.edu.extern.service.ExamSignUpService;
import org.openurp.edu.extern.service.checker.ExamSignUpChecker;
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

  protected ExamSignUpChecker otherExamSignUpChecker;

  protected ExamSignUpService otherExamSignUpService;

  protected ExamSignUpLoggerService otherExamSignUpLoggerService;

  protected BillCodeGenerator otherExamBillCodeGenerator;

  protected ExamFeeConfigService otherExamFeeConfigService;

  private ExamSuperCategoryChecker otherExamSuperCategoryChecker;

  protected PaymentService paymentService;

  protected BillService billService;

  protected List<PaymentChecker> checkers = CollectUtils.newArrayList();

  @Override
  protected String getEntityName() {
    return ExamSignUp.class.getName();
  }

  @Override
  public String innerIndex() {
    Student std = getLoginStudent();
    OqlBuilder<ExamSignUp> builder = OqlBuilder.from(ExamSignUp.class, "signUp").where(
        "signUp.std =:std", std);
    List<ExamSignUp> signUpList = entityDao.search(builder);
    for (ExamSignUp otherExamSignUp : signUpList) {
      Bill bill = otherExamSignUp.getBill();
      if (null != bill && bill.getState().getId().equals(PayState.UNPAID)) {
        if (paymentService.checkBillOnPurpose(bill)) {
          paymentService.updatePayInfo(bill, BillLogType.PAID_SEARCH);
        }
      } else if (null != bill && bill.getState().getId().equals(PayState.PAID)
          && otherExamSignUp.getPayState().getId().equals(PayState.UNPAID)) {
        if (paymentService.checkBillOnPurpose(bill)) {
          otherExamSignUp.setPayState(Model.newInstance(PayState.class, PayState.PAID));
          entityDao.save(otherExamSignUp);
        }
      }
    }
    // 查询已有成绩
    OqlBuilder<ExamGrade> gradeBuilder = OqlBuilder.from(ExamGrade.class, "grade").where("grade.std =:std",
        std);
    put("grades", entityDao.search(gradeBuilder));
    put("signUps", signUpList);
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
    List<ExamSignUpConfig> configs = otherExamSignUpService.getOpenedConfigs(student.getProject());
    // 本次报名记录
    List<ExamSignUp> signUpList = new ArrayList<ExamSignUp>();
    for (Iterator<ExamSignUpConfig> iter = configs.iterator(); iter.hasNext();) {
      ExamSignUpConfig config = (ExamSignUpConfig) iter.next();
      List<ExamSignUp> signUps = otherExamSignUpService.getSignUps(student, config);
      signUpList.addAll(signUps);
    }
    for (ExamSignUp otherExamSignUp : signUpList) {
      Bill bill = otherExamSignUp.getBill();
      if (null != bill && bill.getState().getId().equals(PayState.UNPAID)) {
        if (paymentService.checkBillOnPurpose(bill)) {
          paymentService.updatePayInfo(bill, BillLogType.PAID_SEARCH);
        }
      } else if (null != bill && bill.getState().getId().equals(PayState.PAID)
          && otherExamSignUp.getPayState().getId().equals(PayState.UNPAID)) {
        boolean paid = paymentService.checkBillOnPurpose(bill);
        if (paid) {
          otherExamSignUp.setPayState(Model.newInstance(PayState.class, PayState.PAID));
          entityDao.save(otherExamSignUp);
        }
      }
    }
    if (!signUpList.isEmpty()) {
      put("signUpSubjects", CollectUtils.collect(signUpList, new PropertyTransformer("subject")));
    }
    // 查询已有成绩
    List<ExamGrade> grades = entityDao.get(ExamGrade.class, "std", student);
    Set<ExamSubject> passedSubjects = CollectUtils.newHashSet();
    for (ExamGrade otherGrade : grades) {
      if (otherGrade.isPassed()) {
        passedSubjects.add(otherGrade.getSubject());
      }
    }
    put("passedSubjects", passedSubjects);
    put("signUpList", signUpList);
    put("configs", configs);
    put("student", student);
    Project project = student.getProject();
    put("feeOpen", !otherExamFeeConfigService
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
    ExamSignUpSetting setting = (ExamSignUpSetting) getEntity(ExamSignUpSetting.class,
        "setting");
    Student student = getLoginStudent();
    if (setting.getSuperSubject() != null) {
      String message = otherExamSuperCategoryChecker.check(student, setting);
      if (null != message) {
        addFlashError(message);
        return redirect("configList");
      }
    }

    ExamSignUpConfig config = setting.getConfig();
    List<ExamSignUpConfig> configs = otherExamSignUpService.getOpenedConfigs(student.getProject());
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
  public String signUpForm() {
    Student student = getLoginStudent();
    ExamSignUpSetting setting = (ExamSignUpSetting) getEntity(ExamSignUpSetting.class,
        "setting");
    List<ExamSignUpConfig> configs = otherExamSignUpService.getOpenedConfigs(student.getProject());
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
      msg = otherExamSignUpService.canSignUp(student, setting);
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
    ExamSignUp signUp = populateEntity(ExamSignUp.class, getShortName());
    if (signUp.isPersisted()) {
      ExamSignUp persistedSignup = entityDao.get(ExamSignUp.class, signUp.getId());
      if (!(student.equals(persistedSignup.getStd()) && null != persistedSignup.getStd() && persistedSignup
          .getStd().equals(signUp.getStd()))) { return forwardError("非法操作!"); }
    }
    ExamSignUpSetting setting = (ExamSignUpSetting) entityDao.get(ExamSignUpSetting.class,
        getLong("setting.id"));
    signUp.setStd(student);
    Boolean needMaterial = getBoolean("needMaterial");
    if (Boolean.TRUE.equals(needMaterial)) {
      signUp.setFeeOfMaterial(setting.getFeeOfMaterial());
    }
    Boolean needOutline = getBoolean("needOutline");
    if (Boolean.TRUE.equals(needOutline)) {
      signUp.setFeeOfOutline(setting.getFeeOfOutline());
    }
    signUp.setFeeOfSignUp(setting.getFeeOfSignUp());
    signUp.setSignUpAt(new Timestamp(System.currentTimeMillis()));
    Boolean takeBus = getBoolean("otherExamSignUp.takeBus");
    if (Boolean.TRUE.equals(takeBus)) {
      signUp.setTakeBus(takeBus);
    }
    String msg = "";
    Project project = student.getProject();
    Semester semester = semesterService.getCurSemester(project);
    msg = otherExamSignUpService.signUp(signUp, setting);
    List<ExamFeeConfig> configs = otherExamFeeConfigService.getOpenConfigs(project, semester);
    if (configs.size() == 1 && Strings.isEmpty(msg)) {
      ExamFeeConfig config = configs.get(0);
      if (config.getFeeType() != null) {
        BillGenContext context = BillGenContext.create(student, config.getFeeType(), semester,
            ExamSignUpCalculator.calExamFee(signUp)).setRemark("PAYFOROTHEREXAM");
        context.put("otherExamSignUpConfig", config).setBillCodeGenerator(otherExamBillCodeGenerator);
        Bill bill = billService.genBill(config, context);
        signUp.setBill(bill);
        billService.saveOrUpdate(bill, BillLogType.CREATED, signUp);
        msg = "info.signup.success";
        String remoteAddr = getRemoteAddr();
        otherExamSignUpLoggerService.logger(signUp.getStd().getCode(), ExamSignUpLogger.CREATE,
            remoteAddr, signUp);
      } else {
        msg = "收费项目尚未设置";
      }
    }
    return redirect("configList", Strings.isEmpty(msg) ? "info.signup.success" : msg);
  }

  public String cancelSignUp() {
    ExamSignUp signUp = getEntity(ExamSignUp.class, "signUp");
    Student student = getLoginStudent();
    if (signUp.isPersisted()) {
      List<ExamSignUpConfig> configs = otherExamSignUpService.getOpenedConfigs(student.getProject());
      boolean openConfig = false;
      for (ExamSignUpConfig otherExamSignUpConfig : configs) {
        if (otherExamSignUpService.getSignUps(student, otherExamSignUpConfig).contains(signUp)) {
          openConfig = true;
          break;
        }
      }
      if (openConfig && signUp.getStd().equals(student)) {
        Bill bill = signUp.getBill();
        if (null != bill) {
          if (PayState.UNPAID.equals(bill.getState().getId()) && !paymentService.checkBillOnPurpose(bill)) {
            bill.setState(Model.newInstance(PayState.class, PayState.CANCEL));
            billService.cancel(bill);
            signUp.setBill(null);
          } else {
            return redirect("configList", "取消报名失败!");
          }
        }
        entityDao.remove(signUp);
        String remoteAddr = getRemoteAddr();
        otherExamSignUpLoggerService.logger(signUp.getStd().getCode(), ExamSignUpLogger.DELETE,
            remoteAddr, signUp);
        return redirect("configList", "取消报名成功!");
      }
    }
    return redirect("configList", "取消报名失败!");
  }

  public void setExamSignUpService(ExamSignUpService otherExamSignUpService) {
    this.otherExamSignUpService = otherExamSignUpService;
  }

  public void setExamSignUpLoggerService(ExamSignUpLoggerService otherExamSignUpLoggerService) {
    this.otherExamSignUpLoggerService = otherExamSignUpLoggerService;
  }

  public void setExamSignUpChecker(ExamSignUpChecker otherExamSignUpChecker) {
    this.otherExamSignUpChecker = otherExamSignUpChecker;
  }

  protected void preparePaymentContext(PaymentContext context) {
    Long signUpId = getLong("signUpId");
    if (null != signUpId) {
      ExamSignUp signUp = entityDao.get(ExamSignUp.class, signUpId);
      Project project = signUp.getStd().getProject();
      Semester semester = signUp.getSemester();
      context.put("student", getLoginStudent());
      context.put("semester", semester);
      context.put("project", project);
      context.put("feeConfigs", otherExamFeeConfigService.getOpenConfigs(project, semester));
      context.put("billId", signUp.getBill().getId());
      context.setBill(signUp.getBill());
    }
    String remoteAddr = getRemoteAddr();
    context.put("remoteAddr", remoteAddr);
  }

  public String payment() {
    PaymentContext context = PaymentContext.create();
    context.put("request", getRequest());
    context.put("response", getResponse());
    Long signUpId = getLong("signUpId");
    if (null != signUpId) {
      ExamSignUp signUp = entityDao.get(ExamSignUp.class, signUpId);
      if (signUp.getBill() == null) { return redirect("configList", "此报名记录未生成订单。请取消后重新报名"); }
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

  public void setExamFeeConfigService(ExamFeeConfigService otherExamFeeConfigService) {
    this.otherExamFeeConfigService = otherExamFeeConfigService;
  }

  public void setExamBillCodeGenerator(BillCodeGenerator otherExamBillCodeGenerator) {
    this.otherExamBillCodeGenerator = otherExamBillCodeGenerator;
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

  public void setExamSuperCategoryChecker(ExamSuperCategoryChecker otherExamSuperCategoryChecker) {
    this.otherExamSuperCategoryChecker = otherExamSuperCategoryChecker;
  }

}
