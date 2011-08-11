/*
 * HUMBOLDT: A Framework for Data Harmonization and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.io.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;

import javax.xml.namespace.QName;

import au.com.bytecode.opencsv.CSVReader;

import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.core.io.report.IOReport;
import eu.esdihumboldt.hale.core.io.report.IOReporter;
import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.schema.io.SchemaReader;
import eu.esdihumboldt.hale.schema.io.impl.AbstractSchemaReader;
import eu.esdihumboldt.hale.schema.model.Schema;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.schema.model.impl.DefaultSchema;
import eu.esdihumboldt.hale.schema.model.impl.DefaultTypeDefinition;

/**
 * Reads a schema from a shapefile.
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public class CSVSchemaReader extends AbstractSchemaReader {
	
	/**
	 * Name of the parameter specifying the type name
	 */
	public static String PARAM_TYPENAME = "typename";

	private DefaultSchema schema;
	
	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see SchemaReader#getSchema()
	 */
	@Override
	public Schema getSchema() {
		return schema;
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Load CSV schema", ProgressIndicator.UNKNOWN); //$NON-NLS-1$
		
		//TODO namespace from configuration parameter?!
		String namespace = null; //ShapefileIO.SHAPEFILE_NS;
		schema = new DefaultSchema(namespace, getSource().getLocation());
		
		Reader streamReader = new BufferedReader(
				new InputStreamReader(getSource().getInput()));
		CSVReader reader = new CSVReader(streamReader);
		
		//TODO create type definition and add to schema
		String typename = getParameter(PARAM_TYPENAME);
		
		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * @see AbstractIOProvider#getDefaultContentType()
	 */
	@Override
	protected ContentType getDefaultContentType() {
		//TODO
		return null;
	}

}
