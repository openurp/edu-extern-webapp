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
package org.openurp.edu.extern.grade.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Strings;
import org.beangle.commons.transfer.TransferListener;
import org.beangle.commons.transfer.excel.ExcelItemReader;
import org.beangle.commons.transfer.importer.EntityImporter;
import org.beangle.commons.transfer.importer.MultiEntityImporter;
import org.beangle.commons.transfer.importer.listener.ImporterForeignerListener;
import org.joda.time.LocalDate;
import org.openurp.code.edu.model.GradingMode;
import org.openurp.edu.base.model.Course;
import org.openurp.edu.base.model.Project;
import org.openurp.edu.base.model.Semester;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.base.service.StudentService;
import org.openurp.edu.eams.web.util.DownloadHelper;
import org.openurp.edu.extern.code.model.ExamCategory;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.grade.service.CourseGradeUpdator;
import org.openurp.edu.extern.grade.utils.ParamUtils;
import org.openurp.edu.extern.model.ExamSignupConfig;
import org.openurp.edu.extern.model.ExternExamGrade;
import org.openurp.edu.extern.service.ExternExamGradeImportListener;
import org.openurp.edu.grade.Grade;
import org.openurp.edu.grade.course.model.CourseGrade;
import org.openurp.edu.grade.course.service.GradeRateService;
import org.openurp.edu.program.plan.model.PlanCourse;
import org.openurp.edu.program.plan.service.CoursePlanProvider;

public class ManageAction extends SearchAction {

  protected StudentService studentService;

  protected List<? extends TransferListener> importerListeners = CollectUtils.newArrayList();

  protected CoursePlanProvider coursePlanProvider;

  protected GradeRateService gradeRateService;

  public void editSetting() {
    put("examSubjects", codeService.getCodes(ExamSubject.class));
    put("examCategories", codeService.getCodes(ExamCategory.class));
    put("semesters", entityDao.getAll(Semester.class));
    put("gradingModes", codeService.getCodes(GradingMode.class));
    put("semesters", entityDao.getAll(Semester.class));
  }

  public String index() {
    OqlBuilder<ExamSignupConfig> seasonQuery = OqlBuilder.from(ExamSignupConfig.class, "season");
    seasonQuery.where("season.project = :project", getProject());
    seasonQuery.orderBy("season.beginAt desc");
    put("seasons", entityDao.search(seasonQuery));

    put("examSubjects", codeService.getCodes(ExamSubject.class));
    put("examCategories", codeService.getCodes(ExamCategory.class));
    put("gradingModes", codeService.getCodes(GradingMode.class));
    put("departments", getTeachDeparts());
    put("semesters", entityDao.getAll(Semester.class));

    return forward();
  }

  /**
   * 新建和修改
   */
  public String edit() {
    Long examGradeId = getLongId("examGrade");
    if (examGradeId != null) {
      put("examGrade", entityDao.get(ExternExamGrade.class, examGradeId));
    }

    editSetting();
    return forward();
  }

  /**
   * 保存
   */
  public String save() {
    ExternExamGrade examGrade = populateEntity(ExternExamGrade.class, "examGrade");
    Project project = getProject();
    Integer projectId = project.getId();
    if (examGrade.getStd() == null && Strings.isNotBlank(get("examGrade.std.user.code"))) {
      Student student = studentService.getStudent(projectId, get("examGrade.std.user.code"));
      examGrade.setStd(student);
    }
    if (examGrade.getStd() == null) { return redirect("search", "保存失败,学号不存在"); }
    if (isExist(examGrade)) { return redirect("search", "保存失败,成绩重复"); }
    examGrade.setStatus(Grade.Status.Published);
    examGrade.setUpdatedAt(new Date());
    saveOrUpdate(examGrade);
    return redirect("search", "info.save.success");
  }

  /**
   * 判断是否已存在该成绩
   *
   * @param examGrade
   * @return true:存在 fasle:不存在
   */
  public boolean isExist(ExternExamGrade examGrade) {
    OqlBuilder<ExternExamGrade> query = OqlBuilder.from(ExternExamGrade.class, "grade");
    query.where("grade.std.id = :stdid", examGrade.getStd().getId());
    query.where("grade.acquiredOn = :acquiredOn", examGrade.getAcquiredOn());
    query.where("grade.subject = :subject", examGrade.getSubject());

    if (examGrade.getId() == null) {
      List<ExternExamGrade> sizeExternExamGrade = entityDao.search(query);
      if (!CollectUtils.isEmpty(sizeExternExamGrade)) { return true; }
    } else {
      query.where("grade.id <>:id", examGrade.getId());
      List<ExternExamGrade> sizeExternExamGrade = entityDao.search(query);
      if (!CollectUtils.isEmpty(sizeExternExamGrade)) { return true; }
    }
    return false;
  }

  /**
   * 删除
   */
  public String remove() {
    Long[] examGradeIds = Strings.splitToLong(get("examGradeIds"));
    if (examGradeIds != null) {
      List<ExternExamGrade> examGradeList = entityDao.get(ExternExamGrade.class, examGradeIds);
      entityDao.remove(examGradeList);
      return redirect("search", "info.remove.success", get("params"));
    }
    return redirect("search", "info.remove.failure", get("params"));
  }

  public String convertList() {
    ExternExamGrade externExamGrade = entityDao.get(ExternExamGrade.class, getLongId("examGrade"));
    put("externExamGrade", externExamGrade);

    List<PlanCourse> planCourses = coursePlanProvider.getPlanCourses(externExamGrade.getStd());
    if (CollectionUtils.isEmpty(planCourses)) { return "noPlanMsg"; }

    Map<Course, PlanCourse> coursesMap = CollectUtils.newHashMap();
    for (PlanCourse planCourse : planCourses) {
      coursesMap.put(planCourse.getCourse(), planCourse);
    }

    List<CourseGrade> courseGrades = entityDao.get(CourseGrade.class, new String[] { "std", "course" },
        externExamGrade.getStd(), coursesMap.keySet());
    courseGrades.addAll(externExamGrade.getGrades());
    for (CourseGrade courseGrade : courseGrades) {
      if (courseGrade.isPassed()) {
        coursesMap.remove(courseGrade.getCourse());
      }
    }
    put("planCourses", coursesMap.values());
    put("gradingModes", codeService.getCodes(GradingMode.class));
    return forward();
  }

  public String convert() {
    ExternExamGrade externExamGrade = entityDao.get(ExternExamGrade.class, getLongId("examGrade"));
    List<PlanCourse> planCourses = entityDao.get(PlanCourse.class, getLongIds("planCourse"));
    String remark = "成绩来自" + externExamGrade.getSubject().getName();
    CourseGradeUpdator updator = new CourseGradeUpdator(entityDao, gradeRateService);
    for (PlanCourse planCourse : planCourses) {
      for (Semester semester : coursePlanProvider.getSemesterByPlanCourse(planCourse)) {
        CourseGrade courseGrade = updator.updateGrade(planCourse, externExamGrade.getStd(), semester,
            getInt("gradingMode.id" + planCourse.getId()), getFloat("score" + planCourse.getId()),
            get("scoreText" + planCourse.getId()), remark);
        externExamGrade.addCourseGrade(courseGrade);
      }
    }
    entityDao.saveOrUpdate(externExamGrade);
    return redirect("search", "info.action.success");
  }

  public String undistribute() {
    ExternExamGrade externExamGrade = entityDao.get(ExternExamGrade.class, getLongId("externExamGrade"));
    CourseGrade courseGrade = entityDao.get(CourseGrade.class, getLongId("courseGrade"));
    externExamGrade.getGrades().remove(courseGrade);
    entityDao.saveOrUpdate(externExamGrade);
    entityDao.remove(courseGrade);
    return redirect("search", "info.action.success");
  }

  /**
   * 下载模版
   *
   * @return
   */
  public String downloadTemplate() throws IOException {
    DownloadHelper.download(getRequest(), getResponse(), getClass().getClassLoader().getResource(get("file")),
        get("display"));
    return null;
  }

  protected EntityImporter buildEntityImporter() {
    String upload = "importFile";
    try {
      UploadedFile file = get(upload, UploadedFile.class);
      if (file == null) {
        logger.error("cannot get {} file.", upload);
      }
      String fileName = get(upload + "FileName");
      InputStream is = new FileInputStream((File) file.getContent());
      if (fileName.endsWith(".xls")) {
        HSSFWorkbook wb = new HSSFWorkbook(is);
        if (wb.getNumberOfSheets() < 1 || wb.getSheetAt(0).getLastRowNum() == 0) { return null; }
        EntityImporter importer = new MultiEntityImporter();
        importer.setReader(new ExcelItemReader(wb, 0));
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

  /**
   * 配置导入监听器
   */
  protected void configImporter(EntityImporter importer) {
    MultiEntityImporter mimporter = (MultiEntityImporter) importer;
    mimporter.addForeignedKeys("name");
    mimporter.addEntity("examGrade", ExternExamGrade.class);
    mimporter.addEntity("semesterSchoolYear", String.class);
    mimporter.addEntity("semesterTerm", String.class);
    ImporterForeignerListener l = new ImporterForeignerListener(entityDao);
    l.addForeigerKey("name");
    importer.addListener(l).addListener(new ExternExamGradeImportListener(entityDao, getProject()));
  }

  public List<? extends TransferListener> getImporterListeners() {
    return importerListeners;
  }

  public void setImporterListeners(List<? extends TransferListener> importerListeners) {
    this.importerListeners = importerListeners;
  }

  public String searchStudent() {
    String info = get("info");
    if (null != info && !info.equals("")) {
      put("info", info);
    }
    String studentCode = get("studentCode");
    if (Strings.isNotEmpty(studentCode)) {
      Student student = studentService.getStudent(getProject().getId(), studentCode);
      if (null != student) {
        put("student", student);
      }
    }
    return forward();
  }

  public String identificationReport() {
    OqlBuilder<ExternExamGrade> builder = getQueryBuilder();
    builder.limit(null);
    put("externExamGrades", entityDao.search(builder));

    Date convertFrom = ParamUtils.getOnlyYMDDate("convertFromAt");
    Date convertTo = ParamUtils.getOnlyYMDDate("convertToAt");

    if (null == convertFrom) {
      convertFrom = LocalDate.parse("1970-01-01").toDate();
    }

    if (convertTo == null) {
      convertTo = LocalDate.parse("9999-12-31").toDate();
    } else {
      convertTo = LocalDate.parse(get("convertToAt")).plusDays(1).toDate();
    }
    put("convertFrom",convertFrom);
    put("convertTo",convertTo);
    return forward();
  }


  public void setStudentService(StudentService studentService) {
    this.studentService = studentService;
  }

  public void setCoursePlanProvider(CoursePlanProvider coursePlanProvider) {
    this.coursePlanProvider = coursePlanProvider;
  }

  public void setGradeRateService(GradeRateService gradeRateService) {
    this.gradeRateService = gradeRateService;
  }
}
