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

package eu.esdihumboldt.hale.io.xls.test.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.eclipse.core.runtime.content.IContentType;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import eu.esdihumboldt.cst.test.TransformationExample;
import eu.esdihumboldt.cst.test.TransformationExamples;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.csv.InstanceTableIOConstants;
import eu.esdihumboldt.hale.io.xls.writer.XLSInstanceWriter;

/**
 * Test class for {@link XLSInstanceWriter}
 * 
 * @author Yasmina Kammeyer
 */
public class XLSInstanceWriterTest {

	/**
	 * a temporary folder to safely store tmp files. Will be deleted after test
	 * (successfully or not)
	 */
	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	/**
	 * Wait for needed services to be running
	 */
	@BeforeClass
	public static void waitForServices() {
		TestUtil.startConversionService();
	}

	/**
	 * Test - write simple data, without nested properties and useSchema=true -
	 * test if order/number of column persisted from original schema when in the
	 * instance an attribute has no values
	 * 
	 * @throws Exception , if an error occurs
	 */
	@Test
	public void testWriteSimpleSchemaColOrder() throws Exception {

		// create an example schema with 2 attributes and an instance with a
		// missing value Schema schema =
		Schema schema = XLSInstanceWriterTestUtil.createExampleSchema();
		InstanceCollection instance = XLSInstanceWriterTestUtil
				.createExampleInstancesNoPopulation(schema);

		List<String> header = Arrays.asList("name", "population");
		List<String> firstDataRow = Arrays.asList("Darmstadt", "");

		// set instances to xls instance writer
		XLSInstanceWriter writer = new XLSInstanceWriter();
		IContentType contentType = HalePlatform.getContentTypeManager()
				.getContentType("eu.esdihumboldt.hale.io.xls.xls");
		writer.setParameter(InstanceTableIOConstants.SOLVE_NESTED_PROPERTIES, Value.of(true));
		writer.setParameter(InstanceTableIOConstants.USE_SCHEMA, Value.of(true));

		File tmpFile = tmpFolder.newFile("excelTestWriteSimpleSchema.xls");

		writer.setInstances(instance);
		// write instances to a temporary XLS file
		writer.setTarget(new FileIOSupplier(tmpFile));
		writer.setContentType(contentType);
		IOReport report = writer.execute(null);
		assertTrue(report.isSuccess());

		Workbook wb = WorkbookFactory.create(tmpFile);
		Sheet sheet = wb.getSheetAt(0);

		Row writtenHeader = sheet.getRow(sheet.getFirstRowNum());
		for (Cell cell : writtenHeader) {
			System.out.println("Strings in written header " + cell.getStringCellValue());
		}
		checkHeader4EmptyInstances(sheet, header);
		checkSheetName(sheet, "city");
		// checkFirstDataRow4EmptyInstances(sheet, firstDataRow);
//		  
//		  // Check the order of the columns
//		  
//		  
//		  
//		  // I this case I read the cols of the first row of the instance
//		  
//		  // assuming "column headers" are in the first row Row header_row =
//		  sheet.getRow(0);
//		  
//		  int i = 0; while (true) { Cell header_cell = header_row.getCell(i);
//		  if (header_cell == null) { break; } String writtenHeader =
//		  header_cell.getStringCellValue();
//		  System.out.println("TEST written header " + writtenHeader);
//		  assertEquals("The order of the columns is not equal to the original source file"
//		  , writtenHeader, header.subList(i, i + 1)); i++; }
//		  
//		  
//		  
//		  int i = 0; for (Cell cell : datarow) {
//		  System.out.println("TEST cell " + cell.getStringCellValue());
//		  System.out.println("TEST header " + header.subList(i, i + 1));
//		  assertEquals("The order of the columns is not equal to the original source file"
//		  , cell.getStringCellValue(), header.subList(i, i + 1)); i++; }

	}

	/**
	 * Test - write data of complex schema and analyze result
	 * 
	 * @throws Exception , if an error occurs
	 */
	@Test
	public void testWriteComplexSchema() throws Exception {

		TransformationExample example = TransformationExamples
				.getExample(TransformationExamples.SIMPLE_COMPLEX);
		// alternative the data could be generated by iterating through the
		// exempleproject's sourcedata
		List<String> header = Arrays.asList("id", "name", "details.age", "details.income",
				"details.address.street", "details.address.city");
		List<String> firstDataRow = Arrays.asList("id0", "name0", "age0", "income0", "street0",
				"city0");

		// set instances to xls instance writer
		XLSInstanceWriter writer = new XLSInstanceWriter();
		IContentType contentType = HalePlatform.getContentTypeManager()
				.getContentType("eu.esdihumboldt.hale.io.xls.xls");
		writer.setParameter(InstanceTableIOConstants.SOLVE_NESTED_PROPERTIES, Value.of(true));
		writer.setParameter(InstanceTableIOConstants.USE_SCHEMA, Value.of(false));

		File tmpFile = tmpFolder.newFile("excelTestWriteComplexSchema.xls");

		writer.setInstances(example.getSourceInstances());
		// write instances to a temporary XLS file
		writer.setTarget(new FileIOSupplier(tmpFile));
		writer.setContentType(contentType);
		IOReport report = writer.execute(null);
		assertTrue(report.isSuccess());

		Workbook wb = WorkbookFactory.create(tmpFile);
		Sheet sheet = wb.getSheetAt(0);

		checkHeader(sheet, header);

		checkSheetName(sheet, "person");

		checkFirstDataRow(sheet, firstDataRow);
	}

	/**
	 * Test - write data of complex schema and analyze result The
	 * implementation, this test based on, does not work correctly at the
	 * moment.
	 * 
	 * @throws Exception , if an error occurs
	 */
	@Test
	public void testWriteNotNestedProperties() throws Exception {

		TransformationExample example = TransformationExamples
				.getExample(TransformationExamples.SIMPLE_COMPLEX);
		// alternative the data could be generated by iterating through the
		// exempleproject's sourcedata
		List<String> header = Arrays.asList("id", "name");
		List<String> firstDataRow = Arrays.asList("id0", "name0");

		// set instances to xls instance writer
		XLSInstanceWriter writer = new XLSInstanceWriter();
		IContentType contentType = HalePlatform.getContentTypeManager()
				.getContentType("eu.esdihumboldt.hale.io.xls.xls");
		writer.setParameter(InstanceTableIOConstants.SOLVE_NESTED_PROPERTIES, Value.of(false));
		writer.setParameter(InstanceTableIOConstants.USE_SCHEMA, Value.of(false));

		File tmpFile = tmpFolder.newFile("excelNotNestedProperties.xls");

		writer.setInstances(example.getSourceInstances());
		// write instances to a temporary XLS file
		writer.setTarget(new FileIOSupplier(tmpFile));
		writer.setContentType(contentType);
		IOReport report = writer.execute(null);
		assertTrue(report.isSuccess());

		Workbook wb = WorkbookFactory.create(tmpFile);
		Sheet sheet = wb.getSheetAt(0);

		checkHeader(sheet, header);

		checkSheetName(sheet, "person");

		checkFirstDataRow(sheet, firstDataRow);
	}

	/**
	 * 
	 * @param sheet the excel file sheet
	 * @param sheetName The sheet name
	 * @throws Exception , if an error occurs
	 */
	private void checkSheetName(Sheet sheet, String sheetName) throws Exception {

		assertTrue("There is no sheet in the file named: " + sheet.getSheetName(),
				sheetName.equals(sheet.getSheetName()));

	}

	private void checkHeader(Sheet sheet, List<String> headerNames) throws Exception {

		Row header = sheet.getRow(sheet.getFirstRowNum());

		assertEquals("There are not enough header cells.", headerNames.size(),
				header.getPhysicalNumberOfCells()); // getPhysicalNumberOfCells
													// gets only non-empty
													// columns

		for (Cell cell : header) {
			assertTrue("Not expecting header cell value.",
					headerNames.contains(cell.getStringCellValue()));
		}
	}

	private void checkHeader4EmptyInstances(Sheet sheet, List<String> headerNames)
			throws Exception {

		Row header = sheet.getRow(sheet.getFirstRowNum());

//		assertEquals("There are not enough header cells.", headerNames.size(),
//				header.getLastCellNum());

		int i = 0;
		for (Cell cell : header) {
			String col = headerNames.subList(i, i + 1).toString();
			assertTrue("Not all expecting header cell values are in the XLS file.",
					cell.getStringCellValue().contains(col));
		}
	}

	private void checkFirstDataRow(Sheet sheet, List<String> firstDataRow) {
		Row datarow = sheet.getRow(sheet.getFirstRowNum() + 1);

		assertEquals("There are not enough data cells.", firstDataRow.size(),
				datarow.getPhysicalNumberOfCells());

		for (Cell cell : datarow) {
			assertTrue("Not expecting data value.",
					firstDataRow.contains(cell.getStringCellValue()));
		}
	}

	private void checkFirstDataRow4EmptyInstances(Sheet sheet, List<String> firstDataRow) {
		Row datarow = sheet.getRow(sheet.getFirstRowNum() + 1);

		assertEquals("There are not enough data cells.", firstDataRow.size(),
				datarow.getPhysicalNumberOfCells());

		int i = 0;
		for (Cell cell : datarow) {
			String col = firstDataRow.subList(i, i + 1).toString();
			assertTrue("Not all expected data values are in XLS file.",
					cell.getStringCellValue().contains(col));
			i++;
		}
	}

	/**
	 * test - if a complex schema with data is present and this schema contains
	 * more than one type, the exporter should export all types (one sheet per
	 * type) or the selected one XXX not supported under current circumstances
	 */
	public void testExportChoosenType() {
		// TODO
	}

	/**
	 * test - if a complex schema has a type containing an object attribute with
	 * maxOccures > 1, one type can contain more than one instance of that
	 * object. If this is the case than... TODO Export special case.
	 */
	public void testMultipleInstances() {
		// TransformationExample example =
		// TransformationExamples.getExample(TransformationExamples.CM_NESTED_1);
		// TODO
	}

}
