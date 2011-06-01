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
package eu.esdihumboldt.hale.io.shp.reader.internal;

import java.io.IOException;
import java.text.MessageFormat;

import javax.xml.namespace.QName;

import org.geotools.data.DataStore;
import org.geotools.data.FileDataStoreFinder;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.core.io.report.IOReport;
import eu.esdihumboldt.hale.core.io.report.IOReporter;
import eu.esdihumboldt.hale.io.shp.ShapefileIO;
import eu.esdihumboldt.hale.io.shp.internal.Messages;
import eu.esdihumboldt.hale.schema.io.SchemaReader;
import eu.esdihumboldt.hale.schema.io.impl.AbstractSchemaReader;
import eu.esdihumboldt.hale.schema.model.Schema;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.schema.model.constraints.property.CardinalityConstraint;
import eu.esdihumboldt.hale.schema.model.constraints.property.NillableFlag;
import eu.esdihumboldt.hale.schema.model.constraints.type.BindingConstraint;
import eu.esdihumboldt.hale.schema.model.constraints.type.MappableFlag;
import eu.esdihumboldt.hale.schema.model.constraints.type.SimpleFlag;
import eu.esdihumboldt.hale.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.schema.model.impl.DefaultSchema;
import eu.esdihumboldt.hale.schema.model.impl.DefaultTypeDefinition;

/**
 * Reads a schema from a shapefile.
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public class ShapeSchemaReader extends AbstractSchemaReader {

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
		progress.begin(Messages.getString("ShapeSchemaProvider.1"), ProgressIndicator.UNKNOWN); //$NON-NLS-1$
		
//		DataStore store = new ShapefileDataStoreFactory().createDataStore(location.toURL());
		DataStore store = FileDataStoreFinder.getDataStore(getSource().getLocation().toURL());
		
		//TODO namespace from configuration parameter?!
		String namespace = ShapefileIO.SHAPEFILE_NS;
		schema = new DefaultSchema(namespace, getSource().getLocation());
		
		progress.setCurrentTask(Messages.getString("ShapeSchemaProvider.2")); //$NON-NLS-1$

		// build type definitions based on Schema extracted by geotools
		for (Name name : store.getNames()) {
			SimpleFeatureType sft = store.getSchema(name);
			try {
				// create type definition
				DefaultTypeDefinition type = new DefaultTypeDefinition(
						new QName(namespace, sft.getName().getLocalPart()));
				
				for (AttributeDescriptor ad : sft.getAttributeDescriptors()) {
					DefaultPropertyDefinition property = new DefaultPropertyDefinition(
							new QName(ad.getLocalName()), 
							type, 
							getTypeFromAttributeType(ad.getType(), schema,
									namespace));
					
					// set constraints on property
					property.setConstraint((ad.isNillable())?(NillableFlag.ENABLED):(NillableFlag.DISABLED)); // nillable
					property.setConstraint(CardinalityConstraint.getCardinality(ad.getMinOccurs(), ad.getMaxOccurs())); // cardinality
					
					// set metadata
					property.setLocation(getSource().getLocation());
				}
				
				schema.addType(type);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			progress.setCurrentTask(MessageFormat.format(Messages.getString("ShapeSchemaProvider.7"),  //$NON-NLS-1$
					sft.getTypeName()));
		}

		return reporter;
	}

	/**
	 * Create a type definition for a simple attribute type
	 * 
	 * @param type the attribute type
	 * @param schema the schema
	 * @param namespace the namespace to use for the type definition
	 * @return the type definition
	 */
	private TypeDefinition getTypeFromAttributeType(AttributeType type,
			DefaultSchema schema, String namespace) {
		QName typeName = new QName(namespace, type.getName().getLocalPart());
		TypeDefinition result;
		
		// check shared types
		result = getSharedTypes().getType(typeName);
		
		if (result == null) {
			// get type from schema
			result = schema.getType(typeName);
		}
		
		if (result == null) {
			// create new type
			DefaultTypeDefinition typeDef = new DefaultTypeDefinition(typeName);
			
			// set constraints
			typeDef.setConstraint(MappableFlag.DISABLED); // not mappable
			typeDef.setConstraint(BindingConstraint.getBinding(type.getBinding())); // binding
			typeDef.setConstraint(SimpleFlag.ENABLED); // simple type
			
			// set metadata
			typeDef.setLocation(getSource().getLocation());
			typeDef.setDescription(type.getDescription().toString());
			
			result = typeDef;
			schema.addType(result);
		}
		
		return result;
	}

	/**
	 * @see AbstractIOProvider#getDefaultContentType()
	 */
	@Override
	protected ContentType getDefaultContentType() {
		return ShapefileIO.SHAPEFILE_CT;
	}

}
