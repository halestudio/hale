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

package eu.esdihumboldt.hale.io.csv.writer.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;

import com.google.common.io.Files;

import au.com.bytecode.opencsv.CSVWriter;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.csv.InstanceTableIOConstants;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVUtil;
import eu.esdihumboldt.hale.io.csv.writer.AbstractTableInstanceWriter;

/**
 * Provider for exporting instances as csv files
 * 
 * @author Patrick Lieb
 */
public class CSVInstanceWriter extends AbstractTableInstanceWriter {

	private char sep;
	private char quote;
	private char esc;

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		// get separation, quote and escape sign
		sep = CSVUtil.getSep(this);
		quote = CSVUtil.getQuote(this);
		esc = CSVUtil.getEscape(this);

		String exportType = getParameter(InstanceTableIOConstants.EXPORT_TYPE).as(String.class);
		ArrayList<QName> selectedFeatureTypes = new ArrayList<>();

		if (exportType != null) {
			String[] splitExportType = exportType.split(",");
			for (String featureType : splitExportType) {
				selectedFeatureTypes.add(QName.valueOf(featureType));
			}

			for (QName selectedTypeName : selectedFeatureTypes) {
				// get all instances of the selected Type
				InstanceCollection instances = getInstanceCollection(selectedTypeName);

				if (selectedFeatureTypes.size() != 1 && getTarget().getLocation() != null) {
					URI location = getTarget().getLocation();

					String filePath = Paths.get(location).getParent().toString();
					String baseFilename = Paths.get(location).getFileName().toString();
					baseFilename = baseFilename.substring(0, baseFilename.lastIndexOf("."));
					String filenameWithType = filePath + FileSystems.getDefault().getSeparator()
							+ baseFilename + InstanceTableIOConstants.UNDERSCORE
							+ selectedTypeName.getLocalPart()
							+ InstanceTableIOConstants.CSV_EXTENSION;

					File outputFile = new File(filenameWithType);
					try (FileOutputStream fos = new FileOutputStream(filePath)) {
						addCSVFileByQName(reporter, new FileOutputStream(outputFile), instances);
						fos.close();
					} catch (FileNotFoundException fnfe) {
						reporter.error(new IOMessageImpl("Cannot create csv file", fnfe));
						reporter.setSuccess(false);
						return reporter;
					}
				}
				else {
					try (OutputStream os = getTarget().getOutput()) {
						addCSVFileByQName(reporter, os, instances);
						os.close();
					} catch (FileNotFoundException fnfe) {
						reporter.error(new IOMessageImpl("Cannot create csv file", fnfe));
						reporter.setSuccess(false);
						return reporter;
					}
				}
			}
		}

		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * @param reporter
	 * @param outputStream
	 * @param selectedTypeName
	 * @param instances InstanceCollection available
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void addCSVFileByQName(IOReporter reporter, OutputStream outputStream,
			InstanceCollection instances) throws IOException, FileNotFoundException {

		boolean solveNestedProperties = getParameter(
				InstanceTableIOConstants.SOLVE_NESTED_PROPERTIES).as(Boolean.class, false);

		List<String> headerRow = new ArrayList<String>(); // empty list
		// use ResourceIterator<Instance> in a try block because is closable -
		// avoid infinite
		// cleaning project after exporting data
		try (ResourceIterator<Instance> instanceIterator = instances.iterator();) {
			Instance instance = null;
			try {
				instance = instanceIterator.next();
			} catch (NoSuchElementException e) {
				return;
			}

			// get definition of current instance (only this properties with
			// this
			// definition type will be written to csv file)
			TypeDefinition definition = instance.getDefinition();

			// first csv file doesn't have a header row, so it' necessary to
			// write
			// it to a temp directory
			File tempDir = Files.createTempDir();
			File tempFile = new File(tempDir, "_tempInstances.csv");

			// write instances to csv file (without header)
			// try to be sure resources are all closed after try-block
			try (CSVWriter writer = new CSVWriter(
					new OutputStreamWriter(new FileOutputStream(tempFile)), sep, quote, esc);) {
				writeLine(solveNestedProperties, headerRow, instance, writer);

				while (instanceIterator.hasNext()) {
					Instance nextInst = instanceIterator.next();
					if (nextInst.getDefinition().equals(definition)) {

						writeLine(solveNestedProperties, headerRow, nextInst, writer);
					}
				}
			}

			// header is only finished if all properties have been processed
			// insert header to temp file and write it to output
			insertHeader(tempFile, outputStream, headerRow);
			outputStream.close();
			FileUtils.deleteDirectory(tempDir);
		}
	}

	@Override
	public boolean isPassthrough() {
		return true;
	}

	// write current instance to csv file
	private void writeLine(boolean solveNestedProperties, List<String> headerRow, Instance instance,
			CSVWriter writer) {
		boolean useSchema = getParameter(InstanceTableIOConstants.USE_SCHEMA).as(Boolean.class,
				true);
		List<String> line = new ArrayList<String>();
		Map<String, Object> row = super.getPropertyMap(instance, headerRow, useSchema,
				solveNestedProperties);
		for (String key : headerRow) {
			Object entry = row.get(key);
			line.add(getValueOfProperty(entry));
		}
		writer.writeNext(line.toArray(new String[line.size()]));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "csv file";
	}

	// insert header into csv file (at beginning of file)
	private void insertHeader(File source, OutputStream dest, List<String> header)
			throws IOException {

		String line = "";

		try (FileInputStream fis = new FileInputStream(source);
				BufferedReader in = new BufferedReader(new InputStreamReader(fis));
				PrintWriter out = new PrintWriter(dest);) {

			for (String entry : header) {
				line += entry;
				line += sep;
			}

			// remove last separator
			line = line.substring(0, line.lastIndexOf(sep));

			out.println(line);

			String thisLine = "";

			while ((thisLine = in.readLine()) != null) {
				out.println(thisLine);
			}

			source.delete();
		}
	}

	private String getValueOfProperty(Object property) {
		if (property == null) {
			return "";
		}
		else {

			if (property instanceof Instance) {
				Object instValue = ((Instance) property).getValue();
				if (instValue != null) {
					return (instValue.toString());
				}
			}
			return property.toString();
		}
	}

}
