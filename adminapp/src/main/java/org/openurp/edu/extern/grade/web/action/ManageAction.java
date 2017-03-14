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
package org.openurp.edu.extern.grade.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.metadata.Model;
import org.beangle.commons.lang.Strings;
import org.beangle.commons.transfer.TransferListener;
import org.beangle.commons.transfer.TransferResult;
import org.beangle.commons.transfer.excel.ExcelItemReader;
import org.beangle.commons.transfer.excel.ExcelTemplateWriter;
import org.beangle.commons.transfer.exporter.Context;
import org.beangle.commons.transfer.exporter.Exporter;
import org.beangle.commons.transfer.exporter.TemplateExporter;
import org.beangle.commons.transfer.exporter.TemplateWriter;
import org.beangle.commons.transfer.importer.EntityImporter;
import org.beangle.commons.transfer.importer.MultiEntityImporter;
import org.beangle.commons.transfer.importer.listener.ImporterForeignerListener;
import org.beangle.commons.transfer.io.TransferFormat;
import org.beangle.commons.web.util.RequestUtils;
import org.openurp.base.model.Semester;
import org.openurp.edu.base.code.model.ScoreMarkStyle;
import org.openurp.edu.base.model.Project;
import org.openurp.edu.base.model.Student;
import org.openurp.edu.base.service.StudentService;
import org.openurp.edu.extern.code.model.ExamCategory;
import org.openurp.edu.extern.code.model.ExamSubject;
import org.openurp.edu.extern.model.ExamSignupConfig;
import org.openurp.edu.extern.model.ExternExamGrade;
import org.openurp.edu.extern.service.ExternExamGradeImportListener;

import com.opensymphony.xwork2.ActionContext;

public class ManageAction extends SearchAction {

  protected StudentService studentService;

  protected List<? extends TransferListener> importerListeners = CollectUtils.newArrayList();

  public void editSetting() {
    put("examSubjects", codeService.getCodes(ExamSubject.class));
    put("examCategories", codeService.getCodes(ExamCategory.class));
    put("semesters", entityDao.getAll(Semester.class));
    put("markStyles", codeService.getCodes(ScoreMarkStyle.class));
    put("semesters", entityDao.getAll(Semester.class));
  }

  public String index() {
    OqlBuilder<ExamSignupConfig> seasonQuery = OqlBuilder.from(ExamSignupConfig.class, "season");
    seasonQuery.where("season.project = :project", getProject());
    seasonQuery.orderBy("season.beginAt desc");
    put("seasons", entityDao.search(seasonQuery));

    put("examSubjects", codeService.getCodes(ExamSubject.class));
    put("examCategories", codeService.getCodes(ExamCategory.class));
    // put("calendars", semesterService.getCalendars(getProjects()));
    put("markStyles", codeService.getCodes(ScoreMarkStyle.class));
    put("departments", getTeachDeparts());
    put("semesters", entityDao.getAll(Semester.class));
    String info = get("info");
    put("info", info);

    return forward();
  }

  /**
   * 查询
   */
  public String search() {
    String info = get("info");
    if (Strings.isNotBlank(info)) {
      put("info", info);
    }
    return super.search();
  }

  /**
   * 新建和修改
   */
  public String edit() {
    Long examGradeId = getLongId("examGrade");
    if (examGradeId != null) {
      put("examGrade", entityDao.get(ExternExamGrade.class, examGradeId));
    } else {
      ExternExamGrade examGrade = Model.newInstance(ExternExamGrade.class);
      Semester semester = getSemester();
      if (null != semester) {
        examGrade.setSemester(semester);
      }
      put("examGrade", examGrade);
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
    if (examGrade.getStd() == null && Strings.isNotBlank(get("examGrade.std.code"))) {
      Student student = studentService.getStudent(projectId,get("examGrade.std.code"));
      examGrade.setStd(student);
    }
    if (examGrade.getStd() == null) { return redirect("search", "保存失败,学号不存在"); }
    if (isExist(examGrade)) { return redirect("search", "保存失败,成绩重复"); }
    examGrade.setScoreText(gradeRateService.getConverter(project, examGrade.getMarkStyle()).convert(examGrade.getScore()));
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
    query.where("grade.semester.id = :semesterid", examGrade.getSemester().getId());
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

  /**
   * 下载模版
   * 
   * @return
   */
  public String downloadTemplate() throws IOException {
    Context context = new Context();
    context.put("format", TransferFormat.Xls);
    Exporter exporter = new TemplateExporter();
    HttpServletResponse response = ServletActionContext.getResponse();
    // 设置下载项信息
    String template = get("template");
    TemplateWriter templateWriter = new ExcelTemplateWriter();
    templateWriter.setTemplate(getResource(template));
    templateWriter.setOutputStream(response.getOutputStream());
    templateWriter.setContext(context);
    exporter.setWriter(templateWriter);
    response.setContentType("application/vnd.ms-excel;charset=GBK");
    String oldFileName = "资格考试成绩导入模版.xls";
    String fileName = RequestUtils.encodeAttachName(ServletActionContext.getRequest(), oldFileName);
    response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
    exporter.setContext(context);
    exporter.transfer(new TransferResult());
    return null;
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
    importer.addListener(l).addListener(
        new ExternExamGradeImportListener(entityDao, getProject(), gradeRateService));
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
      Student student = studentService.getStudent(getProject().getId(),studentCode);
      if (null != student) {
        put("student", student);
      }
    }
    return forward();
  }

  public void setStudentService(StudentService studentService) {
    this.studentService = studentService;
  }
}
