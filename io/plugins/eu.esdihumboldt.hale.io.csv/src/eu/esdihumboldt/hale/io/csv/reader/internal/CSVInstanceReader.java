/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.csv.reader.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import au.com.bytecode.opencsv.CSVReader;
import eu.esdihumboldt.hale.common.core.io.ContentType;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceReader;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.OInstance;
import eu.esdihumboldt.hale.io.csv.CSVFileIO;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Reads instances from a CSVfile
 * 
 * @author Kevin Mais
 */
public class CSVInstanceReader extends AbstractInstanceReader {

	private DefaultInstanceCollection instances;

	/**
	 * The separating sign for the CSV file to be read (can be '\t' or ',' or
	 * ' ')
	 */
	public static char separator = '\t';

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		instances = new DefaultInstanceCollection(new ArrayList<Instance>());

		Reader streamReader = new BufferedReader(new InputStreamReader(
				getSource().getInput()));
		CSVReader reader = new CSVReader(streamReader, separator);

		// build instances
		TypeDefinition type = getSourceSchema().getType(new QName("muh"));

		PropertyDefinition[] propAr = type.getChildren().toArray(
				new PropertyDefinition[type.getChildren().size()]);
		String[] nextLine;

		// nextLine[] is an array of values in the first line (we don't need
		// them)
		nextLine = reader.readNext();

		while ((nextLine = reader.readNext()) != null) {
			MutableInstance instance = new OInstance(type);
			// nextLine[] is now an array of all values in the line (starting in
			// second line)
			int index = 0;
			for (String part : nextLine) {
				PropertyDefinition property = propAr[index];
				instance.addProperty(property.getName(), part);
				index++;
			}

			instances.add(instance);
		}
		return null;
	}

	/**
	 * @see AbstractIOProvider#getDefaultContentType()
	 */
	@Override
	protected ContentType getDefaultContentType() {
		return CSVFileIO.CSVFILE_CT;
	}

	/**
	 * @see InstanceReader#getInstances()
	 */
	@Override
	public InstanceCollection getInstances() {
		return instances;
	}
}
