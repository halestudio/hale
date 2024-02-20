/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xls;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.format.DateTimeFormatter;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Do analysis on simple Excel tables.
 * 
 * @author Simon Templer
 */
public abstract class AbstractAnalyseTable {

	private FormulaEvaluator evaluator;

	/**
	 * Load table to analyse from an Excel file (first sheet).
	 * 
	 * @param source the source to load the file from
	 * @param xlsx
	 * @throws Exception if an error occurs loading the file
	 */
	protected void analyse(LocatableInputSupplier<InputStream> source, boolean xlsx)
			throws Exception {
		analyse(source, xlsx, 0, 0, null);
	}

	/**
	 * Load table to analyse from an Excel file.
	 * 
	 * @param source the source to load the file from
	 * @param isXlsx if the file should be loaded as XLSX file
	 * @param sheetNum number of the sheet that should be loaded (0-based)
	 * @param skipNlines number of lines to skip
	 * @param dateTime
	 * @throws Exception if an error occurs loading the file
	 */
	protected void analyse(LocatableInputSupplier<? extends InputStream> source, boolean isXlsx,
			int sheetNum, int skipNlines, String dateTime) throws Exception {
		try (InputStream inp = new BufferedInputStream(source.getInput());) {
//			https://poi.apache.org/components/spreadsheet/quick-guide.html#FileInputStream
			URI location = source.getLocation();
			Workbook wb = loadWorkbook(inp, location, isXlsx);

			Sheet sheet = wb.getSheetAt(sheetNum);
			evaluator = wb.getCreationHelper().createFormulaEvaluator();

			DateTimeFormatter dateFormatter = null;
			if (dateTime != null) {
				dateFormatter = DateTimeFormatter.ofPattern(dateTime);
			}
			// the first might row represents the header
			analyseHeader(sheet, dateFormatter);

			// load configuration entries
			analyseContent(sheet, skipNlines, dateFormatter);
		} finally {
			// reset evaluator reference
			evaluator = null;
		}
	}

	/**
	 * Load a workbook from a stream.
	 * 
	 * @param input the input stream to load
	 * @param location an optional location that can be used to determine the
	 *            file type
	 * @param isXlsx if the file should be loaded as XLSX file
	 * @return the loaded workbook
	 * @throws IOException if an error occurs reading the file
	 * @throws InvalidFormatException if file has an invalid format when
	 *             attempting to load as OpenXML file
	 */
	public static Workbook loadWorkbook(InputStream input, URI location, boolean isXlsx)
			throws IOException, InvalidFormatException {
		if (location != null && !isXlsx && location.getPath().toLowerCase().endsWith(".xls")) {
			try (POIFSFileSystem fs = new POIFSFileSystem(input)) {
				return new HSSFWorkbook(fs.getRoot(), true);
			}
		}
		else {
			OPCPackage pkg = OPCPackage.open(input);
			return new XSSFWorkbook(pkg);
		}
	}

	/**
	 * Analyzes the table header.
	 * 
	 * @param sheet the table sheet
	 * @param dateTimeFormatter date formatter
	 */
	protected void analyseHeader(Sheet sheet, DateTimeFormatter dateTimeFormatter) {
		Row header = sheet.getRow(0);
		if (header != null) {

			// identify columns
			int count = 0;
			for (int i = header.getFirstCellNum(); i < header.getLastCellNum(); i++) {
				Cell cell = header.getCell(i);
				String text = extractText(cell, sheet, dateTimeFormatter);
				// cell cannot be empty to extract the text
				if (text != null) {
					headerCell(count, text);
					count++;
				}
			}
		}
	}

	/**
	 * @param num zero based column index
	 * @param text the header
	 */
	protected abstract void headerCell(int num, String text);

	/**
	 * Analyse the table content if skipNlines <=0 that don't analyse first row,
	 * which has already been analyse into the header else analyse starting with
	 * the skip line
	 * 
	 * @param sheet the table sheet
	 * @param skipNlines skip N lines
	 * @param dateTimeFormatter date formatter
	 */
	private void analyseContent(Sheet sheet, int skipNlines, DateTimeFormatter dateTimeFormatter) {
		for (int i = skipNlines; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			analyseRow(i, row, sheet, dateTimeFormatter);
		}
	}

	/**
	 * Analyse a content row.
	 * 
	 * @param num the row number (starting from one as the header row is handled
	 *            separately)
	 * @param row the table row
	 * @param sheet the sheet
	 * @param dateTimeFormatter date formatter
	 */
	protected abstract void analyseRow(int num, Row row, Sheet sheet,
			DateTimeFormatter dateTimeFormatter);

	/**
	 * Extract the text from a given cell. Formulas are evaluated, for blank or
	 * error cells <code>null</code> is returned
	 * 
	 * @param cell the cell
	 * @param sheet to extract text
	 * @param dateTimeFormatter to convert the date into
	 * @return the cell text
	 */
	protected String extractText(Cell cell, Sheet sheet, DateTimeFormatter dateTimeFormatter) {
		return XLSUtil.extractText(cell, evaluator, sheet, dateTimeFormatter);
	}

}
