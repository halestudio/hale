/*
 * Copyright (c) 2014 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.xls;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Factory for common xls cell styles
 * 
 * @author Patrick Lieb
 */
public class XLSCellStyles {

	/**
	 * @param workbook the workbook of the cell
	 * @return the header cell style
	 */
	public static CellStyle getHeaderStyle(Workbook workbook) {

		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		DataFormat df = workbook.createDataFormat();
		// use bold font
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		// set a medium border
		headerStyle.setBorderBottom(BorderStyle.MEDIUM);
		// set cell data format to text
		headerStyle.setDataFormat(df.getFormat("@"));

		return headerStyle;
	}

	/**
	 * @param workbook the workbook of the cell
	 * @param strikeOut true, if cell should be striked out
	 * @return the normal cell style
	 */
	public static CellStyle getNormalStyle(Workbook workbook, boolean strikeOut) {

		// create cell style
		CellStyle cellStyle = workbook.createCellStyle();
		DataFormat df = workbook.createDataFormat();
		// set thin border around the cell
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		// set cell data format to text
		cellStyle.setDataFormat(df.getFormat("@"));
		// display multiple lines
		cellStyle.setWrapText(true);

		if (strikeOut) {
			// strike out font
			Font disabledFont = workbook.createFont();
			disabledFont.setStrikeout(true);
			disabledFont.setColor(IndexedColors.GREY_40_PERCENT.getIndex());
			cellStyle.setFont(disabledFont);
		}

		return cellStyle;
	}

	/**
	 * @param workbook the workbook of the cell
	 * @param strikeOut true, if cell should be striked out
	 * @return the highlighted cell style
	 */
	public static CellStyle getHighlightedStyle(Workbook workbook, boolean strikeOut) {

		// create highlight style for type cells
		CellStyle highlightStyle = workbook.createCellStyle();
		DataFormat df = workbook.createDataFormat();
		// set thin border around the cell
		highlightStyle.setBorderBottom(BorderStyle.THIN);
		highlightStyle.setBorderLeft(BorderStyle.THIN);
		highlightStyle.setBorderRight(BorderStyle.THIN);
		// set cell data format to text
		highlightStyle.setDataFormat(df.getFormat("@"));
		// display multiple lines
		highlightStyle.setWrapText(true);
		highlightStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		highlightStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		if (strikeOut) {
			Font disabledTypeFont = workbook.createFont();
			disabledTypeFont.setStrikeout(true);
			disabledTypeFont.setColor(IndexedColors.BLACK.getIndex());
			highlightStyle.setFont(disabledTypeFont);
		}

		return highlightStyle;
	}
}
