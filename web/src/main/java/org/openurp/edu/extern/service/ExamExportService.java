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
package org.openurp.edu.extern.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;

/**
 * 考试模块导出excel的帮助类
 * 
 * @author chaostone
 */
public abstract class ExamExportService {

  protected HSSFCellStyle xlsBorderStyle(HSSFWorkbook workbook, String style, int border) {
    HSSFCellStyle cellStyle = workbook.createCellStyle();
    if (style.indexOf("l") >= 0) cellStyle.setBorderLeft((short) border);
    if (style.indexOf("r") >= 0) cellStyle.setBorderRight((short) border);
    if (style.indexOf("t") >= 0) cellStyle.setBorderTop((short) border);
    if (style.indexOf("b") >= 0) cellStyle.setBorderBottom((short) border);
    return cellStyle;

  }

  protected Region xlsRegion(int rowFrom, int colFrom, int rowTo, int colTo) {
    return new Region((short) rowFrom, (short) colFrom, (short) rowTo, (short) colTo);
  }

  protected HSSFWorkbook prepareWorkbook(InputStream is) throws FileNotFoundException {
    // FIXME zhouqi 2011-06-11 需要维护
    // Map beans = new HashMap();
    // XLSTransformer transformer = new XLSTransformer();
    // return transformer.transformXLS(is, beans);
    return null;
  }

  protected HSSFFont xlsFont(HSSFWorkbook workbook, String fontName, short height) {
    HSSFFont font = workbook.createFont();
    font.setFontName(fontName);
    font.setFontHeightInPoints(height);
    return font;
  }

  // todo
  protected void drawBorderAroundRegion(HSSFSheet sheet, Region region, int border) {
    drawLeftBorderAroundRegion(sheet, region, border);
    drawRightBorderAroundRegion(sheet, region, border);
    drawTopBorderAroundRegion(sheet, region, border);
    drawButtomBorderAroundRegion(sheet, region, border);
  }

  protected void drawLeftBorderAroundRegion(HSSFSheet sheet, Region region, int border) {
    int rowFrom = region.getRowFrom();
    int rowTo = region.getRowTo();
    int colFrom = region.getColumnFrom();

    for (int i = rowFrom; i <= rowTo; i++) {
      HSSFCell cell = sheet.getRow(i).getCell((short) colFrom);
    }
  }

  protected void drawRightBorderAroundRegion(HSSFSheet sheet, Region region, int border) {
    int rowStart = region.getRowFrom();
    int rowEnd = region.getRowTo();
    int column = region.getColumnFrom();

    for (int i = rowStart; i <= rowEnd; i++) {
    }
  }

  protected void drawTopBorderAroundRegion(HSSFSheet sheet, Region region, int border) {
    int rowStart = region.getRowFrom();
    int rowEnd = region.getRowTo();
    int column = region.getColumnFrom();

    for (int i = rowStart; i <= rowEnd; i++) {
    }
  }

  protected void drawButtomBorderAroundRegion(HSSFSheet sheet, Region region, int border) {
    int rowStart = region.getRowFrom();
    int rowEnd = region.getRowTo();
    int column = region.getColumnFrom();

    for (int i = rowStart; i <= rowEnd; i++) {
    }
  }

  public abstract void exportExcel() throws IOException;

  public abstract void setXlsTemplatePath(String xlsTemplatePath);

  public abstract void setTitles(List<String> titles);

  public abstract void setOfs(OutputStream ofs);

  public abstract void setDatas(List<Object[]> datas);

}
