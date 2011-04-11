/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.models;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Set;

import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.schemaprovider.ProgressIndicator;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * The SchemaService is used internally to provide access to the currently 
 * loaded schemas.
 * 
 * @author Thorsten Reitz
 * @version {$Id}
 */
public interface SchemaService 
	extends UpdateService {
	
	/**
	 * @return the {@link Collection} of all root {@link FeatureType}s belonging
	 * to the currently loaded source schema, i.e. {@link FeatureType}s which do
	 * not have a supertype.
	 */
	public Collection<SchemaElement> getSourceSchemaElements();
	
	/**
	 * @return the {@link Collection} of all root {@link FeatureType}s belonging
	 * to the currently loaded target schema, i.e. {@link FeatureType}s which do
	 * not have a supertype.
	 */
	public Collection<SchemaElement> getTargetSchemaElements();
	
	/**
	 * Get the source schema format
	 * 
	 * @return the source schema format
	 */
	public String getSourceSchemaFormat();
	
	/**
	 * Get the target schema format
	 * 
	 * @return the target schema format
	 */
	public String getTargetSchemaFormat();
	
	/**
	 * Loads the schema defined under the given URL as the target or source 
	 * schema.
	 * May point to different source, such as a XSD or a a WFS.
	 * @param file the {@link URI} to the file from which to load the schema.
	 * @param schemaFormat the schema format identifier
	 * @param type the schema type
	 * @param progress the progress indicator, may be <code>null</code>
	 * @return true if the loading was successful.
	 * @throws IOException if loading the schema fails
	 */
	public boolean loadSchema(URI file, String schemaFormat, SchemaType type, ProgressIndicator progress) throws IOException;
	
	/**
	 * Get the target schema
	 * 
	 * @return the target schema
	 */
	public Schema getTargetSchema();
	
	/**
	 * Get the source schema
	 * 
	 * @return the source schema
	 */
	public Schema getSourceSchema();
	
	/**
	 * Loads multiple schemas into the target or source schema.
	 * 
	 * @param uris the {@link URI}s to the schemas
	 * @param type the schema type
	 * @return if the loading was successful
	 */
	//XXX not supported for now - public boolean loadSchema(List<URI> uris, SchemaType type);
	
	/**
	 * Invoke this operation if you want to clear out the source schema stored. 
	 * @return true if the cleaning was successful.
	 */
	public boolean cleanSourceSchema();
	
	/**
	 * Invoke this operation if you want to clear out the target schema stored. 
	 * @return true if the cleaning was successful.
	 */
	public boolean cleanTargetSchema();

	/**
	 * @return the namespace of the source schema.
	 */
	public String getSourceNameSpace();
	
	/**
	 * @return the namespace of the target schema.
	 */
	public String getTargetNameSpace();

	/**
	 * @return the URL that identifies the location from which the source schema
	 *         was loaded.
	 */
	public URL getSourceURL();

	/**
	 * @return the URL that identifies the location from which the target schema
	 *         was loaded.
	 */
	public URL getTargetURL();
	
	/**
	 * Get the type or element definition identified by the given name
	 * 
	 * @param name may either consist of only a local part or of a full
	 *         name, i.e. namespace + local name part
	 *         
	 * @return returns the definition identified by the given name
	 */
	public Definition getTypeByName(String name);
	
	/**
	 * Schema type enum
	 */
	public enum SchemaType {
		/** Source schema */
		SOURCE,
		/** Target schema */
		TARGET
	}

	/**
	 * Get the feature types for the given schema type
	 * 
	 * @param schemaType the schema type
	 * 
	 * @return the feature types
	 */
	public Collection<SchemaElement> getSchema(SchemaType schemaType);
	
	/**
	 * Get the definition for the given identifier if it is part of the given
	 *   schema
	 * 
	 * @param identifier the identifier
	 * @param schema the schema type
	 * 
	 * @return the definition (either a {@link TypeDefinition} or an
	 *   {@link AttributeDefinition})
	 */
	public Definition getDefinition(String identifier, SchemaType schema);

	/**
	 * Get the definition for the given identifier
	 * 
	 * @param identifier the identifier
	 * 
	 * @return the definition (either a {@link TypeDefinition} or an
	 *   {@link AttributeDefinition})
	 */
	public Definition getDefinition(String identifier);

	/**
	 * Get the supported schema formats that may be used with
	 * {@link #loadSchema(URI, String, SchemaType, ProgressIndicator)}
	 * 
	 * @return the supported schema formats
	 */
	public Set<String> getSupportedSchemaFormats();

}
