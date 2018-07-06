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
package org.openurp.edu.extern.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openurp.edu.extern.service.ExamExportService;

public class ExamExportServiceImpl extends ExamExportService {

  // 模板路径
  private String xlsTemplatePath;

  // 标题
  private List<String> titles;

  // 导出数据
  private List<Object[]> datas;

  // 输出流
  private OutputStream ofs;

  // 字体方向水平,普通字体，宋体，9
  private HSSFCellStyle style_hirizonal_common;

  // 字体方向水平,粗体，宋体，9
  private HSSFCellStyle style_hirizonal_bold;

  // 导出方法
  public synchronized void exportExcel() throws IOException {
    ClassLoader classLoader = this.getClass().getClassLoader();
    InputStream ifs = classLoader.getResourceAsStream(xlsTemplatePath);

    // 获得workbook,xls文档对象
    HSSFWorkbook workbook = prepareWorkbook(ifs);
    HSSFSheet sheet = workbook.getSheetAt(0);
    // 准备字体
    prepareStyleAndFont(workbook);
    // 设置标题
    xlsTitle(sheet);
    // 开始绘制课程
    xlsDatas(sheet);

    workbook.write(ofs);
    ifs.close();
    ofs.close();
  }

  /**
   * 设置标题
   *
   * @param sheet
   */
  private void xlsTitle(HSSFSheet sheet) {
    HSSFRow titleRow = sheet.createRow(0);
    for (int i = 0; i < titles.size(); i++) {
      HSSFCell cell = titleRow.createCell((short) i);
      cell.setCellValue(new HSSFRichTextString((String) titles.get(i)));
      cell.setCellStyle(style_hirizonal_bold);
    }
  }

  private void xlsDatas(HSSFSheet sheet) {
    int startRow = 1;
    for (Iterator<Object[]> iterator = datas.iterator(); iterator.hasNext();) {
      Object[] data = iterator.next();
      HSSFRow row = sheet.createRow(startRow);
      int cellCol = 0;
      for (int i = 0; i < data.length; i++) {
        HSSFCell cell = row.createCell((short) cellCol);
        cell.setCellStyle(style_hirizonal_common);
        if (null != data[i]) {
          String object = data[i].toString();
          cell.setCellValue(new HSSFRichTextString(object));
        } else {
          cell.setCellValue(new HSSFRichTextString(""));
        }
        cellCol++;
      }
      startRow++;
    }
  }

  private void prepareStyleAndFont(HSSFWorkbook workbook) {
    HSSFFont font_common = xlsFont(workbook, "宋体", (short) 11);
    style_hirizonal_common = xlsBorderStyle(workbook, "", 0);
    style_hirizonal_common.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    style_hirizonal_common.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
    style_hirizonal_common.setFont(font_common);
    style_hirizonal_common.setWrapText(true);

    HSSFFont font_bold = xlsFont(workbook, "宋体", (short) 11);
    font_bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    style_hirizonal_bold = xlsBorderStyle(workbook, "", 0);
    style_hirizonal_bold.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    style_hirizonal_bold.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
    style_hirizonal_bold.setFont(font_bold);
  }

  public String getXlsTemplatePath() {
    return xlsTemplatePath;
  }

  public void setXlsTemplatePath(String xlsTemplatePath) {
    this.xlsTemplatePath = xlsTemplatePath;
  }

  public List<String> getTitles() {
    return titles;
  }

  public void setTitles(List<String> titles) {
    this.titles = titles;
  }

  public OutputStream getOfs() {
    return ofs;
  }

  public void setOfs(OutputStream ofs) {
    this.ofs = ofs;
  }

  public List<Object[]> getDatas() {
    return datas;
  }

  public void setDatas(List<Object[]> datas) {
    this.datas = datas;
  }

}
