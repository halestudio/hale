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

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.models.UpdateService;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.SchemaProvider;
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
	implements SchemaService {
	
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
	 * Listeners
	 */
	private Collection<HaleServiceListener> listeners = new HashSet<HaleServiceListener>();
	
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
		updateListeners();
		
		return true;
	}

	/**
	 * @see SchemaService#cleanTargetSchema()
	 */
	public boolean cleanTargetSchema() {
		targetSchema = Schema.EMPTY_SCHEMA;
		updateListeners();
		
		return true;
	}

	/**
	 * @see SchemaService#getSourceSchema()
	 */
	public Collection<TypeDefinition> getSourceSchema() {
		return sourceSchema.getTypes();
	}

	/**
	 * @see SchemaService#getTargetSchema()
	 */
	public Collection<TypeDefinition> getTargetSchema() {
		return targetSchema.getTypes();
	}

	/**
	 * @see SchemaService#loadSchema(URI, SchemaType)
	 */
	public boolean loadSchema(URI location, SchemaType type) {
		Schema schema = schemaProvider.loadSchema(location);
		
		if (type.equals(SchemaType.SOURCE)) {
			sourceSchema = schema;
		} 
		else {
			targetSchema = schema;
		}
		
		this.updateListeners();
		return true;
	}
	
	/**
	 * @see UpdateService#addListener(HaleServiceListener)
	 */
	public boolean addListener(HaleServiceListener sl) {
		return this.listeners.add(sl);
	}
	
	/**
	 * Inform {@link HaleServiceListener}s of an update.
	 */
	@SuppressWarnings("unchecked")
	private void updateListeners() {
		for (HaleServiceListener hsl : this.listeners) {
			hsl.update(new UpdateMessage(SchemaService.class, null)); // FIXME
		}
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
	
}



