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

package eu.esdihumboldt.hale.ui.service.schemaitem.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.opengis.feature.type.FeatureType;

import com.vividsolutions.jts.geom.Geometry;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.model.schema.AttributeItem;
import eu.esdihumboldt.hale.ui.model.schema.ElementItem;
import eu.esdihumboldt.hale.ui.model.schema.SchemaItem;
import eu.esdihumboldt.hale.ui.model.schema.TreeObject;
import eu.esdihumboldt.hale.ui.model.schema.TreeObject.TreeObjectType;
import eu.esdihumboldt.hale.ui.model.schema.TreeParent;
import eu.esdihumboldt.hale.ui.model.schema.TypeItem;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceAdapter;
import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceListener;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService.SchemaType;
import eu.esdihumboldt.hale.ui.service.schemaitem.SchemaItemService;
import eu.esdihumboldt.specification.cst.align.IEntity;

/**
 * Schema item service implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SchemaItemServiceImpl implements SchemaItemService {
	
	private static final ALogger log = ALoggerFactory.getLogger(SchemaItemServiceImpl.class);
	
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
	
	/**
	 * Entity about's mapped to source schema items
	 */
	private final Map<String, SchemaItem> sourceAboutSchemaItems = new HashMap<String, SchemaItem>();
	
	/**
	 * Entity about's mapped to source schema items
	 */
	private final Map<String, SchemaItem> targetAboutSchemaItems = new HashMap<String, SchemaItem>();
	
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
	 * @see SchemaItemService#getSchemaItem(IEntity, SchemaType)
	 */
	@Override
	public SchemaItem getSchemaItem(IEntity entity, SchemaType schemaType) {
		switch (schemaType) {
		case SOURCE:
			return sourceAboutSchemaItems.get(entity.getAbout().getAbout());
		case TARGET:
			return targetAboutSchemaItems.get(entity.getAbout().getAbout());
		default:
			return null;
		}
	}

	/**
	 * @see SchemaItemService#getSchemaItem(IEntity)
	 */
	@Override
	public SchemaItem getSchemaItem(IEntity entity) {
		SchemaItem result = getSchemaItem(entity, SchemaType.SOURCE);
		if (result == null) {
			return getSchemaItem(entity, SchemaType.TARGET);
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
			sourceRoot = translateSchema(schemaService.getSourceSchemaElements(), 
					schemaService.getSourceNameSpace(), schemaType);
			break;
		case TARGET:
			targetRoot = translateSchema(schemaService.getTargetSchemaElements(), 
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
	private SchemaItem translateSchema(Collection<SchemaElement> schema, String namespace, SchemaType schemaType) {
		Map<String, SchemaItem> itemMap;
		Map<String, SchemaItem> aboutMap;
		switch (schemaType) {
		case SOURCE:
			itemMap = sourceSchemaItems;
			aboutMap = sourceAboutSchemaItems;
			break;
		case TARGET:
			itemMap = targetSchemaItems;
			aboutMap = targetAboutSchemaItems;
			break;
		default:
			throw new RuntimeException("Schema type must be specified"); //$NON-NLS-1$
		}
		
		itemMap.clear();
		aboutMap.clear();
		
		if (schema == null || schema.size() == 0) {
			return new TreeParent("", null, TreeObjectType.ROOT, null, schemaType); //$NON-NLS-1$
		}

		// first, find out a few things about the schema to define the root
		// type.
		// TODO add metadata on schema here.
		// TODO is should be possible to attach attributive data for a flyout.
		TreeParent hidden_root = new TreeParent("ROOT", null, TreeObjectType.ROOT, null, schemaType); //$NON-NLS-1$
		TreeParent root = new TreeParent(namespace, null, TreeObjectType.ROOT, null, schemaType);
		hidden_root.addChild(root);
		
		// collect element types
		Set<TypeDefinition> elementTypes = new HashSet<TypeDefinition>();
		for (SchemaElement element : schema) {
			elementTypes.add(element.getType());
		}
		
		// determine super types that are not present in the element types
		Queue<TypeDefinition> toTest = new LinkedList<TypeDefinition>(elementTypes);
		Set<TypeDefinition> tested = new HashSet<TypeDefinition>();
		Set<TypeDefinition> additions = new HashSet<TypeDefinition>();
		while (!toTest.isEmpty()) {
			TypeDefinition type = toTest.poll();
			tested.add(type);
			
			if (!elementTypes.contains(type)) {
				additions.add(type);
			}
			
			// test super types
			TypeDefinition superType = type.getSuperType();
			if (superType != null) {
				if (!tested.contains(superType)) {
					toTest.add(superType);
				}
			}
		}
		
		Set<SchemaElement> elements = new HashSet<SchemaElement>(schema);

		// finally, build the tree, starting with those types that don't have
		// supertypes.
		for (TypeDefinition type : additions) {
			if (type.getSuperType() == null) {
				root.addChild(buildSchemaTree(null, type, elements, namespace, 
						itemMap, aboutMap, schemaType, additions));
			}
		}
		for (SchemaElement element : elements) {
			if (element.getType().getSuperType() == null) {
				root.addChild(buildSchemaTree(element, element.getType(), elements, namespace, 
						itemMap, aboutMap, schemaType, additions));
			}
		}

		// TODO show references to Properties which are FTs already added as
		// links.
		return hidden_root;
	}
	
	/**
	 * Recursive method for setting up the inheritance tree.
	 * 
	 * @param element the element declaration, may be <code>null</code>
	 * @param type the type definition
	 * @param schema the collection of types to display
	 * @param namespace the namespace
	 * @param itemMap map to add the created items to (definition identifier mapped to item)
	 * @param aboutMap map to add the created items to (about string mapped to item) 
	 * @param schemaType the schema type
	 * @param additions set of type definitions that must be added because of the hierarchy
	 * @return a {@link SchemaItem} that contains all Properties and all
	 *         subtypes and their property, starting with the given FT.
	 */
	private TreeObject buildSchemaTree(SchemaElement element, 
			TypeDefinition type, Collection<SchemaElement> schema, String namespace, 
			Map<String, SchemaItem> itemMap, Map<String, SchemaItem> aboutMap,
			SchemaType schemaType, Set<TypeDefinition> additions) {
		TreeParent featureItem;
		if (element != null) {
			featureItem = new ElementItem(element, schemaType);
			itemMap.put(element.getIdentifier(), featureItem);
		}
		else {
			featureItem = new TypeItem(type, schemaType);
			itemMap.put(type.getIdentifier(), featureItem);
		}
		aboutMap.put(featureItem.getEntity().getAbout().getAbout(), featureItem);
		
		// add properties
		addProperties(featureItem, type, itemMap, aboutMap, new HashSet<TypeDefinition>(), schemaType);
		
		// add children recursively
		for (TypeDefinition subType : type.getSubTypes()) {
			if (additions.contains(subType)) {
				// add type item needed for hierarchy 
				featureItem.addChild(buildSchemaTree(null, subType, schema, namespace, itemMap, aboutMap, schemaType, additions));
			}
			else if (subType.getDeclaringElements().isEmpty()) {
				// subtype w/o element declaration
				
				// check if super types have valid schema element
				TypeDefinition superType = type;
				boolean parentHasElement = false;
				while (!parentHasElement && superType != null) {
					if (additions.contains(superType)) {
						// ignore elements defined for additions
						superType = null;
					}
					else {
						parentHasElement = !superType.getDeclaringElements().isEmpty(); //TODO improve check?
						superType = superType.getSuperType();
					}
				}
				
				if (parentHasElement) {
					featureItem.addChild(buildSchemaTree(null, subType, schema, namespace, itemMap, aboutMap, schemaType, additions));
				}
			}
			else {
				// subtype with element declaration(s)
				for (SchemaElement subTypeElement : subType.getDeclaringElements()) {
					if (schema.contains(subTypeElement)) {
						featureItem.addChild(buildSchemaTree(subTypeElement, 
								subType, schema, namespace, itemMap, aboutMap, 
								schemaType, additions));
					}
				}
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
	 * @param aboutMap map to add the created items to (about string mapped to item)
	 * @param resolving the currently resolving types (to prevent loops)
	 * @param schemaType the schema type
	 */
	private void addProperties(TreeParent parent, TypeDefinition type, Map<String, SchemaItem> itemMap,
			Map<String, SchemaItem> aboutMap, Set<TypeDefinition> resolving,
			SchemaType schemaType) {
		if (resolving.contains(type)) {
			log.debug("Cycle in properties, skipping adding property items"); //$NON-NLS-1$
		}
		else {
			resolving.add(type);
			
			for (AttributeDefinition attribute : type.getSortedAttributes()) {
				if (attribute.getAttributeType() != null) { // only properties with an associated type
					AttributeItem property = new AttributeItem(attribute, schemaType);
					
					if (itemMap != null) {
						itemMap.put(attribute.getIdentifier(), property);
					}
					
					parent.addChild(property);
					
					// don't add children if there is a geometry binding
					boolean addChildren = !Geometry.class.isAssignableFrom(attribute.getAttributeType().getType(null).getBinding());
					
					if (addChildren) {
						addProperties(property, attribute.getAttributeType(), null, 
								aboutMap, new HashSet<TypeDefinition>(resolving), schemaType); // null map to prevent adding to item map (would be duplicate)
					}
					
					aboutMap.put(property.getEntity().getAbout().getAbout(), property);
				}
			}
		}
	}

}
