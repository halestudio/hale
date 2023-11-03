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
	 * @throws Exception if an error occurs loading the file
	 */
	protected void analyse(LocatableInputSupplier<InputStream> source, boolean xlsx)
			throws Exception {
		analyse(source, xlsx, 0);
	}

	/**
	 * Load table to analyse from an Excel file.
	 * 
	 * @param source the source to load the file from
	 * @param sheetNum number of the sheet that should be loaded (0-based)
	 * @throws Exception if an error occurs loading the file
	 */
	protected void analyse(LocatableInputSupplier<? extends InputStream> source, boolean xlsx,
			int sheetNum) throws Exception {
		try (InputStream inp = new BufferedInputStream(source.getInput());) {
//			https://poi.apache.org/components/spreadsheet/quick-guide.html#FileInputStream
			URI location = source.getLocation();
			Workbook wb = loadWorkbook(inp, location, xlsx);

			Sheet sheet = wb.getSheetAt(sheetNum);
			evaluator = wb.getCreationHelper().createFormulaEvaluator();

			// the first row represents the header
			analyseHeader(sheet);

			// load configuration entries
			analyseContent(sheet);
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
	 * @param xlsx if the file should be loaded as XLSX file
	 * @return the loaded workbook
	 * @throws IOException if an error occurs reading the file
	 * @throws InvalidFormatException if file has an invalid format when
	 *             attempting to load as OpenXML file
	 */
	public static Workbook loadWorkbook(InputStream input, URI location, boolean xlsx)
			throws IOException, InvalidFormatException {
		if (location != null && !xlsx && location.getPath().toLowerCase().endsWith(".xls")) {
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
	 */
	protected void analyseHeader(Sheet sheet) {
		Row header = sheet.getRow(0);

		// identify columns
		for (int i = header.getFirstCellNum(); i < header.getLastCellNum(); i++) {
			Cell cell = header.getCell(i);
			String text = extractText(cell);

			headerCell(i, text);
		}
	}

	/**
	 * @param num zero based column index
	 * @param text the header
	 */
	protected abstract void headerCell(int num, String text);

	/**
	 * Analyse the table content.
	 * 
	 * @param sheet the table sheet
	 */
	private void analyseContent(Sheet sheet) {
		// for each row starting from the second
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			analyseRow(i, row);
		}
	}

	/**
	 * Analyse a content row.
	 * 
	 * @param num the row number (starting from one as the header row is handled
	 *            separately)
	 * @param row the table row
	 */
	protected abstract void analyseRow(int num, Row row);

	/**
	 * Extract the text from a given cell. Formulas are evaluated, for blank or
	 * error cells <code>null</code> is returned
	 * 
	 * @param cell the cell
	 * @return the cell text
	 */
	protected String extractText(Cell cell) {
		return XLSUtil.extractText(cell, evaluator);
	}

}
