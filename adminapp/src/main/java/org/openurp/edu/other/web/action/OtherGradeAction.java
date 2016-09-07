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
package org.openurp.edu.other.web.action;

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
import org.openurp.edu.other.code.model.OtherExamCategory;
import org.openurp.edu.other.code.model.OtherExamSubject;
import org.openurp.edu.other.model.OtherExamSignUpConfig;
import org.openurp.edu.other.model.OtherGrade;
import org.openurp.edu.other.service.OtherGradeImportListener;

import com.opensymphony.xwork2.ActionContext;

public class OtherGradeAction extends OtherGradeSearchAction {

  protected StudentService studentService;

  protected List<? extends TransferListener> importerListeners = CollectUtils.newArrayList();

  public void editSetting() {
    put("otherExamSubjects", codeService.getCodes(OtherExamSubject.class));
    put("otherExamCategories", codeService.getCodes(OtherExamCategory.class));
    put("semesters", entityDao.getAll(Semester.class));
    put("markStyles", codeService.getCodes(ScoreMarkStyle.class));
    put("semesters", entityDao.getAll(Semester.class));
  }

  public String index() {
    OqlBuilder<OtherExamSignUpConfig> seasonQuery = OqlBuilder.from(OtherExamSignUpConfig.class, "season");
    seasonQuery.where("season.project = :project", getProject());
    seasonQuery.orderBy("season.beginAt desc");
    put("seasons", entityDao.search(seasonQuery));

    put("otherExamSubjects", codeService.getCodes(OtherExamSubject.class));
    put("otherExamCategories", codeService.getCodes(OtherExamCategory.class));
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
    Long otherGradeId = getLongId("otherGrade");
    if (otherGradeId != null) {
      put("otherGrade", entityDao.get(OtherGrade.class, otherGradeId));
    } else {
      OtherGrade otherGrade = Model.newInstance(OtherGrade.class);
      Semester semester = getSemester();
      if (null != semester) {
        otherGrade.setSemester(semester);
      }
      put("otherGrade", otherGrade);
    }

    editSetting();
    return forward();
  }

  /**
   * 保存
   */
  public String save() {
    OtherGrade otherGrade = populateEntity(OtherGrade.class, "otherGrade");
    Project project = getProject();
    Integer projectId = project.getId();
    if (otherGrade.getStd() == null && Strings.isNotBlank(get("otherGrade.std.code"))) {
      Student student = studentService.getStudent(projectId,get("otherGrade.std.code"));
      otherGrade.setStd(student);
    }
    if (otherGrade.getStd() == null) { return redirect("search", "保存失败,学号不存在"); }
    if (isExist(otherGrade)) { return redirect("search", "保存失败,成绩重复"); }
    otherGrade.setScoreText(gradeRateService.getConverter(project, otherGrade.getMarkStyle()).convert(otherGrade.getScore()));
    saveOrUpdate(otherGrade);
    return redirect("search", "info.save.success");
  }

  /**
   * 判断是否已存在该成绩
   * 
   * @param otherGrade
   * @return true:存在 fasle:不存在
   */
  public boolean isExist(OtherGrade otherGrade) {
    OqlBuilder<OtherGrade> query = OqlBuilder.from(OtherGrade.class, "grade");
    query.where("grade.std.id = :stdid", otherGrade.getStd().getId());
    query.where("grade.semester.id = :semesterid", otherGrade.getSemester().getId());
    query.where("grade.subject = :subject", otherGrade.getSubject());

    if (otherGrade.getId() == null) {
      List<OtherGrade> sizeOtherGrade = entityDao.search(query);
      if (!CollectUtils.isEmpty(sizeOtherGrade)) { return true; }
    } else {
      query.where("grade.id <>:id", otherGrade.getId());
      List<OtherGrade> sizeOtherGrade = entityDao.search(query);
      if (!CollectUtils.isEmpty(sizeOtherGrade)) { return true; }
    }
    return false;
  }

  /**
   * 删除
   */
  public String remove() {
    Long[] otherGradeIds = Strings.splitToLong(get("otherGradeIds"));
    if (otherGradeIds != null) {
      List<OtherGrade> otherGradeList = entityDao.get(OtherGrade.class, otherGradeIds);
      entityDao.remove(otherGradeList);
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
        throw new RuntimeException("donot support other format except excel");
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
    mimporter.addEntity("otherGrade", OtherGrade.class);
    mimporter.addEntity("semesterSchoolYear", String.class);
    mimporter.addEntity("semesterTerm", String.class);
    ImporterForeignerListener l = new ImporterForeignerListener(entityDao);
    l.addForeigerKey("name");
    importer.addListener(l).addListener(
        new OtherGradeImportListener(entityDao, getProject(), gradeRateService));
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
