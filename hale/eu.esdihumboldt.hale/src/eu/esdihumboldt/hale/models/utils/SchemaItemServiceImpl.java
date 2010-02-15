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

package eu.esdihumboldt.hale.models.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.models.schema.SchemaServiceAdapter;
import eu.esdihumboldt.hale.models.schema.SchemaServiceListener;
import eu.esdihumboldt.hale.rcp.views.model.AttributeItem;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject;
import eu.esdihumboldt.hale.rcp.views.model.TreeParent;
import eu.esdihumboldt.hale.rcp.views.model.TypeItem;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Schema item service implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SchemaItemServiceImpl implements SchemaItemService {
	
	private final SchemaService schemaService;
	
	private final Set<SchemaServiceListener> listeners = new HashSet<SchemaServiceListener>();
	
	/**
	 * Source schema items that have an associated defintion
	 */
	private final Map<String, SchemaItem> sourceSchemaItems = new HashMap<String, SchemaItem>();
	
	/**
	 * Target schema items that have an associated defintion
	 */
	private final Map<String, SchemaItem> targetSchemaItems = new HashMap<String, SchemaItem>();
	
	private SchemaItem sourceRoot;
	
	private SchemaItem targetRoot;
	
	/**
	 * Create the schema item service
	 * 
	 * @param schemaService the schema servic
	 */
	public SchemaItemServiceImpl(SchemaService schemaService) {
		super();
		this.schemaService = schemaService;
		
		schemaService.addListener(new SchemaServiceAdapter() {
			
			@Override
			public void schemaChanged(SchemaType schema) {
				updateRoot(schema);
				for (SchemaServiceListener listener : listeners) {
					listener.schemaChanged(schema);
				}
			}
			
		});
		
		updateRoot(SchemaType.SOURCE);
		updateRoot(SchemaType.TARGET);
	}

	/**
	 * @see SchemaItemService#addListener(SchemaServiceListener)
	 */
	@Override
	public void addListener(SchemaServiceListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see SchemaItemService#getRoot(SchemaType)
	 */
	@Override
	public SchemaItem getRoot(SchemaType schema) {
		switch (schema) {
		case SOURCE:
			return sourceRoot;
		case TARGET:
			return targetRoot;
		default:
			return null;
		}
	}

	/**
	 * @see SchemaItemService#getSchemaItem(String, SchemaType)
	 */
	@Override
	public SchemaItem getSchemaItem(String identifier, SchemaType schemaType) {
		switch (schemaType) {
		case SOURCE:
			return sourceSchemaItems.get(identifier);
		case TARGET:
			return targetSchemaItems.get(identifier);
		default:
			return null;
		}
	}

	/**
	 * @see SchemaItemService#getSchemaItem(String)
	 */
	@Override
	public SchemaItem getSchemaItem(String identifier) {
		SchemaItem result = getSchemaItem(identifier, SchemaType.SOURCE);
		if (result == null) {
			return getSchemaItem(identifier, SchemaType.TARGET);
		}
		else {
			return result;
		}
	}

	/**
	 * @see SchemaItemService#removeListener(SchemaServiceListener)
	 */
	@Override
	public void removeListener(SchemaServiceListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Build the root for the given schema type
	 * 
	 * @param schemaType the schema type
	 */
	private void updateRoot(SchemaType schemaType) {
		switch (schemaType) {
		case SOURCE:
			sourceRoot = translateSchema(schemaService.getSourceSchema(), 
					schemaService.getSourceNameSpace(), schemaType);
			break;
		case TARGET:
			targetRoot = translateSchema(schemaService.getTargetSchema(), 
					schemaService.getTargetNameSpace(), schemaType);
			break;
		}
	}
	
	/**
	 * @param schema
	 *            the {@link Collection} of {@link FeatureType}s that represent
	 *            the schema to display.
	 * @param namespace the namespace
	 * @param schemaType the schema type
	 * 
	 * @return the root item
	 */
	private SchemaItem translateSchema(Collection<TypeDefinition> schema, String namespace, SchemaType schemaType) {
		Map<String, SchemaItem> itemMap;
		switch (schemaType) {
		case SOURCE:
			itemMap = sourceSchemaItems;
			break;
		case TARGET:
			itemMap = targetSchemaItems;
			break;
		default:
			throw new RuntimeException("Schema type must be specified");
		}
		
		itemMap.clear();
		
		if (schema == null || schema.size() == 0) {
			return new TreeParent("", null, TreeObjectType.ROOT, null);
		}

		// first, find out a few things about the schema to define the root
		// type.
		// TODO add metadata on schema here.
		// TODO is should be possible to attach attributive data for a flyout.
		TreeParent hidden_root = new TreeParent("ROOT", null, TreeObjectType.ROOT, null);
		TreeParent root = new TreeParent(namespace, null, TreeObjectType.ROOT, null);
		hidden_root.addChild(root);

		// finally, build the tree, starting with those types that don't have
		// supertypes.
		for (TypeDefinition type : schema) {
			if (type.getSuperType() == null) {
				root.addChild(buildSchemaTree(type, schema, namespace, itemMap));
			}
		}

		// TODO show references to Properties which are FTs already added as
		// links.
		return hidden_root;
	}
	
	/**
	 * Recursive method for setting up the inheritance tree.
	 * 
	 * @param type the type definition
	 * @param schema the collection of types to display
	 * @param namespace the namespace
	 * @param itemMap map to add the created items to (definition identifier mapped to item)
	 * @return a {@link SchemaItem} that contains all Properties and all
	 *         subtypes and their property, starting with the given FT.
	 */
	private TreeObject buildSchemaTree(TypeDefinition type, Collection<TypeDefinition> schema, String namespace, Map<String, SchemaItem> itemMap) {
		TypeItem featureItem = new TypeItem(type);
		itemMap.put(type.getIdentifier(), featureItem);
		
		// add properties
		addProperties(featureItem, type, itemMap);
		
		// add children recursively
		for (TypeDefinition subType : type.getSubTypes()) {
			if (schema.contains(subType)) {
				featureItem.addChild(buildSchemaTree(subType, schema, namespace, itemMap));
			}
		}
		return featureItem;
	}
	
	/**
	 * Add properties of the given feature type to the given tree parent
	 * 
	 * @param parent the tree parent
	 * @param type the type definition
	 * @param itemMap map to add the created items to (definition identifier mapped to item)
	 */
	private static void addProperties(TreeParent parent, TypeDefinition type, Map<String, SchemaItem> itemMap) {
		for (AttributeDefinition attribute : type.getAttributes()) {
			if (attribute.getAttributeType() != null) { // only properties with an associated type
				AttributeItem property = new AttributeItem(attribute);
				
				if (itemMap != null) {
					itemMap.put(attribute.getIdentifier(), property);
				}
				
				addProperties(property, attribute.getAttributeType(), null); // null map to prevent adding to item map (would be duplicate)
				
				parent.addChild(property);
			}
		}
	}

}
