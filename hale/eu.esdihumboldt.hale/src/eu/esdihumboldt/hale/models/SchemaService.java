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

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.opengis.feature.type.FeatureType;

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
	public Collection<FeatureType> getSourceSchema();
	
	/**
	 * @return the {@link Collection} of all root {@link FeatureType}s belonging
	 * to the currently loaded target schema, i.e. {@link FeatureType}s which do
	 * not have a supertype.
	 */
	public Collection<FeatureType> getTargetSchema();
	
	/**
	 * Loads the schema defined under the given URL as the target or source 
	 * schema.
	 * May point to different source, such as a XSD or a a WFS.
	 * @param file the {@link URI} to the file from which to load the schema.
	 * @param type the schema type
	 * @return true if the loading was successful.
	 */
	public boolean loadSchema(URI file, SchemaType type);
	
	/**
	 * Loads multiple schemas into the target or source schema.
	 * 
	 * @param uris the {@link URI}s to the schemas
	 * @param type the schema type
	 * @return if the loading was successful
	 */
	public boolean loadSchema(List<URI> uris, SchemaType type);
	
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
	 * Get the {@link FeatureType} identified by the given name
	 * 
	 * @param name may either consist of only a local part or of a full
	 *         name, i.e. namespace + local name part
	 *         
	 * @return returns a {@link FeatureType} identified by the given name
	 */
	public FeatureType getFeatureTypeByName(String name);
	
	public enum SchemaType {
		SOURCE,
		TARGET
	}

	/**
	 * @param schemaType
	 * @return
	 */
	public Collection<FeatureType> getSchema(SchemaType schemaType);

}
