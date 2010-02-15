/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Component    : HALE
 * Created on   : Jun 3, 2009 -- 4:50:10 PM
 */
package eu.esdihumboldt.hale.models.schema;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.schemaprovider.ProgressIndicator;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.SchemaProvider;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Implementation of {@link SchemaService}. It uses a {@link SchemaProvider}
 * for actually loading the Schema
 * 
 * @author Simon Templer, Fraunhofer IGD
 * @version $Id$
 * @param <T> 
 */
public class SchemaProviderService<T extends SchemaProvider> 
	extends AbstractSchemaService {
	
	/**
	 * The instance map
	 */
	private static final Map<Class<? extends SchemaProvider>, SchemaProviderService<?>> instances = new HashMap<Class<? extends SchemaProvider>, SchemaProviderService<?>>();

	/**
	 * Source schema
	 */
	private Schema sourceSchema = Schema.EMPTY_SCHEMA;

	/** 
	 * Target schema 
	 */
	private Schema targetSchema = Schema.EMPTY_SCHEMA;
	
	/**
	 * The schema provider
	 */
	private final SchemaProvider schemaProvider;
	
	/**
	 * Creates the schema service
	 * 
	 * @param schemaProviderType the type of the schema provider 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private SchemaProviderService(Class<T> schemaProviderType) throws InstantiationException, IllegalAccessException {
		super();
		
		schemaProvider = schemaProviderType.newInstance();
	}
	
	/**
	 * Get the {@link SchemaProviderService} instance
	 * 
	 * @param <T> the type of the schema provider
	 * @param schemaProviderType the type of the schema provider
	 * 
	 * @return the {@link SchemaProviderService} instance
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static <T extends SchemaProvider> SchemaService getInstance(Class<T> schemaProviderType) throws InstantiationException, IllegalAccessException {
		SchemaProviderService<?> instance = instances.get(schemaProviderType);
		if (instance == null) {
			instance = new SchemaProviderService<T>(schemaProviderType);
			instances.put(schemaProviderType, instance);
		}
		return instance;
	}

	/**
	 * @see SchemaService#cleanSourceSchema()
	 */
	public boolean cleanSourceSchema() {
		sourceSchema = Schema.EMPTY_SCHEMA;
		notifySchemaChanged(SchemaType.SOURCE);
		
		return true;
	}

	/**
	 * @see SchemaService#cleanTargetSchema()
	 */
	public boolean cleanTargetSchema() {
		targetSchema = Schema.EMPTY_SCHEMA;
		notifySchemaChanged(SchemaType.TARGET);
		
		return true;
	}

	/**
	 * @see SchemaService#getSourceSchema()
	 */
	public Collection<TypeDefinition> getSourceSchema() {
		return sourceSchema.getTypes().values();
	}

	/**
	 * @see SchemaService#getTargetSchema()
	 */
	public Collection<TypeDefinition> getTargetSchema() {
		return targetSchema.getTypes().values();
	}

	/**
	 * @see SchemaService#loadSchema(URI, SchemaType, ProgressIndicator)
	 */
	@Override
	public boolean loadSchema(URI location, SchemaType type, ProgressIndicator progress) throws IOException {
		Schema schema = schemaProvider.loadSchema(location, progress);
		
		if (type.equals(SchemaType.SOURCE)) {
			sourceSchema = schema;
		} 
		else {
			targetSchema = schema;
		}
		
		notifySchemaChanged(type);
		return true;
	}

	/**
	 * @see SchemaService#getSourceNameSpace()
	 */
	public String getSourceNameSpace() {
		return sourceSchema.getNamespace();
	}

	/**
	 * @see SchemaService#getSourceURL()
	 */
	public URL getSourceURL() {
		return sourceSchema.getLocation();
	}

	/**
	 * @see SchemaService#getTargetNameSpace()
	 */
	public String getTargetNameSpace() {
		return targetSchema.getNamespace();
	}

	/**
	 * @see SchemaService#getTargetURL()
	 */
	public URL getTargetURL() {
		return targetSchema.getLocation();
	}

	/**
	 * @see SchemaService#getFeatureTypeByName(String)
	 */
	public TypeDefinition getFeatureTypeByName(String name) {
		TypeDefinition result = null;
		// handles cases where a full name was given.
		if (!getSourceNameSpace().equals("") && name.contains(getSourceNameSpace())) {
			for (TypeDefinition ft : getSourceSchema()) {
				if (ft.getName().getLocalPart().equals(name)) {
					result = ft;
					break;
				}
			}
		}
		else if (!getTargetNameSpace().equals("") && name.contains(getTargetNameSpace())) {
			for (TypeDefinition ft : getTargetSchema()) {
				if (ft.getName().getLocalPart().equals(name)) {
					result = ft;
					break;
				}
			}
		}
		// handle case where only the local part was given.
		else {
			Collection<TypeDefinition> allFTs = new HashSet<TypeDefinition>();
			allFTs.addAll(getSourceSchema());
			allFTs.addAll(getTargetSchema());
			for (TypeDefinition ft : allFTs) {
				if (ft.getName().getLocalPart().equals(name)) {
					result = ft;
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * @see SchemaService#getSchema(SchemaService.SchemaType)
	 */
	public Collection<TypeDefinition> getSchema(SchemaType schemaType) {
		if (SchemaType.SOURCE.equals(schemaType)) {
			return getSourceSchema();
		}
		else {
			return getTargetSchema();
		}
	}

	/**
	 * @see SchemaService#getDefinition(String)
	 */
	@Override
	public Definition getDefinition(String identifier) {
		//XXX improve implementation?
		Definition result = getDefinition(identifier, sourceSchema);
		if (result == null) {
			result = getDefinition(identifier, targetSchema);
		}
		
		return result;
	}

	/**
	 * @see SchemaService#getDefinition(String, SchemaType)
	 */
	@Override
	public Definition getDefinition(String identifier, SchemaType schema) {
		return getDefinition(identifier, (schema == SchemaType.SOURCE)?(sourceSchema):(targetSchema));
	}

	private Definition getDefinition(String identifier, Schema schema) {
		//XXX improve implementation?
		Definition result = schema.getTypes().get(identifier);
		
		if (result == null) {
			// not found as type, may be attribute
			int index = identifier.lastIndexOf('/');
			if (index > 0) {
				String subIdentifier = identifier.substring(0, index);
				String attributeName = identifier.substring(index + 1);
				
				TypeDefinition type = schema.getTypes().get(subIdentifier);
				
				if (type != null) {
					// try to find attribute
					for (AttributeDefinition attribute : type.getAttributes()) {
						if (attribute.getName().equals(attributeName)) {
							return attribute;
						}
					}
				}
			}
		}
		
		return result;
	}
	
}
