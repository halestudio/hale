/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.cst.transformer.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.commons.goml.align.Alignment;
import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.omwg.ComposedProperty;
import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.cst.transformer.service.rename.RenameFeatureFunction;
import eu.esdihumboldt.specification.cst.align.IAlignment;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.IEntity;
import eu.esdihumboldt.specification.cst.align.ext.ITransformation;

/**
 * The {@link AlignmentIndex} is used by the {@link SchemaTranslationController}
 * to access information about a given {@link Alignment} in convenient ways.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class AlignmentIndex {
	
	private static Logger _log = Logger.getLogger(AlignmentIndex.class);
	
	private final IAlignment alignment;
	
	Set<String> targetTypes = new HashSet<String>();
	Set<String> abstractTargetTypes = new HashSet<String>();
	Set<String> sourceTypes = new HashSet<String>();
	Map<String, List<ICell>> cellsFtIndex = 
		new HashMap<String, List<ICell>>();
	
	// constructors ............................................................

	public AlignmentIndex(IAlignment al) {
		this.alignment = al;
		// identify concretely mapped Feature Classes
		for (ICell c : this.alignment.getMap()) {
			if (c.getEntity1().getClass().isAssignableFrom(FeatureClass.class)) {
				this.sourceTypes.add(c.getEntity1().getAbout().getAbout());
			}
			if (c.getEntity2().getClass().isAssignableFrom(FeatureClass.class)) {
				this.targetTypes.add(c.getEntity2().getAbout().getAbout());
			}
		}
		
		// handle "orphan" property cells
		for (ICell c : this.alignment.getMap()) {
			if (c.getEntity2().getClass().isAssignableFrom(Property.class)) {
				Property p = ((Property) c.getEntity2());
				if (!this.targetTypes.contains(p.getFeatureClassName())) {
					this.abstractTargetTypes.add(getKeyFromEntity(p));
				}
			}
			_log.info("Recognized " + this.abstractTargetTypes.size()  //$NON-NLS-1$
					+ " orphan cells."); //$NON-NLS-1$
		}
		
		this.determineCellsPerFeatureType(al);
	}
	
	// main methods ............................................................
	
	/**
	 * @param key
	 *         a String identifying a {@link FeatureType}
	 * @return a {@link List} of {@link String}s that denote all
	 *         {@link FeatureType}s that are mapped to the {@link FeatureType}
	 *         identified by key. If key identifies a source {@link FeatureType}
	 *         , mapped target {@link FeatureType}s will be returned and vice
	 *         versa.
	 */
	public Set<String> getAllMappedFeatureTypes(String key){
		Set<String> result = new HashSet<String>();
		for (ICell cell : this.cellsFtIndex.get(key)) {
			if (this.isSourceType(key)) {
				result.add(this.getKeyFromEntity((Entity) cell.getEntity2()));
			}
			else if (this.isTargetType(key)) {
				if (cell.getEntity1().getTransformation() != null) { // no augmentation
					result.add(this.getKeyFromEntity((Entity) cell.getEntity1()));
				}
			}
			else {
				throw new RuntimeException("Unknown Entity String."); //$NON-NLS-1$
			}
		}
		return result;
	}
	
	/**
	 * @param targetFtName a {@link String} identifying a {@link FeatureType}
	 * @return a {@link List} of all {@link ICell}s that contain the given 
	 * {@link Entity} String. If a {@link FeatureType} URL is passed, it will also 
	 * return all property {@link ICell}s below.
	 */
	public List<ICell> getCellsPerEntity(String targetFtName) {
		List<ICell> result = new ArrayList<ICell>();
		
		// get FeatureType for the passed typename
		FeatureType keyType = TargetSchemaProvider.getInstance().getType(targetFtName);
		
		// find out all supertypes
		List<FeatureType> relevantTypes = new ArrayList<FeatureType>();
		if (keyType != null) {
			Set<String> allTypeNames = new HashSet<String>();
			allTypeNames.addAll(this.abstractTargetTypes);
			allTypeNames.addAll(this.targetTypes);
			for (String typeName : allTypeNames) {
				FeatureType ft = TargetSchemaProvider.getInstance().getType(typeName);
				if (this.isSuperTypeOf(keyType, ft))  {
					relevantTypes.add(ft);
				}
			}
		}
		
		// add cells for the identified relevant types
		for (FeatureType type: relevantTypes) {
			result.addAll(this.cellsFtIndex.get(
					type.getName().getNamespaceURI() + "/"  //$NON-NLS-1$
					+ type.getName().getLocalPart()));
		}
		
		// add cells declared directly on the Type identified by the key.
		result.addAll(this.cellsFtIndex.get(targetFtName));
		
		return result;
	}
	
	/**
	 * @param key the identifier of a target {@link FeatureType}.
	 * @return a List with just the {@link Cell}s defining an attributive 
	 * transformation, augmentations and renames are not returned.
	 */
	public List<ICell> getAttributiveCellsPerEntity(String key) {
		List<ICell> result = new ArrayList<ICell>();
		for (ICell cell : this.getCellsPerEntity(key)) {
			ITransformation t = cell.getEntity1().getTransformation();
			if (t != null && t.getService() != null
					&& !t.getService().getLocation().equals(
							RenameFeatureFunction.class.getName())) {
				result.add(cell);
			}
		}
		return result;
	}
	
	/**
	 * @param key the identifier of a target {@link FeatureType}.
	 * @return a List with just the {@link Cell}s defining an augmentation 
	 * transformation
	 */
	public List<ICell> getAugmentationCellsPerEntity(String key) {
		List<ICell> result = new ArrayList<ICell>();
		for (ICell cell : this.getCellsPerEntity(key)) {
			if (cell.getEntity2().getTransformation() != null 
					&& cell.getEntity2().getTransformation().getService() != null) {
				result.add(cell);
			}
		}
		return result;
	}
	
	/**
	 * Check if a type is the child of the given potential parent type
	 * 
	 * @param potentialChild the potential child
	 * @param potentialParent the potential parent
	 * 
	 * @return true if potentialParent is a supertype of potentialChild
	 */
	private boolean isSuperTypeOf(AttributeType potentialChild, AttributeType potentialParent) {
		boolean result = false;
		if (potentialChild != null && potentialChild.getSuper() != null && potentialParent != null) {
			if (potentialChild.getSuper().getName().equals(
							potentialParent.getName())) {
				result = true;
			}
			else {
				result = this.isSuperTypeOf(
						potentialChild.getSuper(), 
						potentialParent);
			}
		}
		return result;
	}

	/**
	 * @param key {@link URL} identifying a {@link FeatureType}
	 * @return true if the given {@link URL} identifies a known target 
	 * {@link FeatureType}.
	 */
	public boolean isTargetType(String key) {
		return this.targetTypes.contains(key);
	}
	
	/**
	 * @param key {@link URL} identifying a {@link FeatureType} 
	 * @return true if the given {@link URL} identifies a known source 
	 * {@link FeatureType}.
	 */
	public boolean isSourceType(String key) {
		return this.sourceTypes.contains(key);
	}
	
	/**
	 * @return a {@link Set} of {@link URL}s with all target {@link FeatureType} 
	 * identifiers.
	 */
	public Set<String> getTargetTypes() {
		return this.targetTypes;
	}
	
	/**
	 * @return a {@link Set} of {@link URL}s with all source {@link FeatureType} 
	 * identifiers.
	 */
	public Set<String> getSourceTypes() {
		return this.sourceTypes;
	}
	
	// helper and initialization methods .......................................
	
	/**
	 * @param alignment
	 * @return a Map that gives, for each source and target FeatureType, the 
	 * cells that are relevant.
	 */
	private void determineCellsPerFeatureType(IAlignment alignment) {
		
		// step through all cells
		for (ICell cell : alignment.getMap()) {
			List<ICell> thisCellList = null;
			
			// determine FT key, i.e. ignore Property names.
			Entity e1 = (Entity) cell.getEntity1();
			Entity e2 = (Entity) cell.getEntity2();
			String key1 = this.getKeyFromEntity(e1);
			String key2 = this.getKeyFromEntity(e2);
			
			// add Cells to source FeatureTypes
			if (cellsFtIndex.containsKey(key1)) {
				thisCellList = cellsFtIndex.get(key1);
				thisCellList.add(cell);
			}
			else {
				thisCellList = new ArrayList<ICell>();
				thisCellList.add(cell);
				cellsFtIndex.put(key1, thisCellList);
			}
			
			// add Cells to target FeatureTypes
			if (cellsFtIndex.containsKey(key2)) {
				thisCellList = cellsFtIndex.get(key2);
				thisCellList.add(cell);
			}
			else {
				thisCellList = new ArrayList<ICell>();
				thisCellList.add(cell);
				cellsFtIndex.put(key2, thisCellList);
			}
		}
	}
	
	/**
	 * This method will return a {@link FeatureType} key for both 
	 * {@link FeatureType} keys and {@link Property} keys.
	 * @param key the input key
	 * @return a {@link String} for a {@link FeatureType} key.
	 */
	public static String getFeatureTypeKey(String key, String namespace) {
		if (!namespace.endsWith("/")) { //$NON-NLS-1$
			namespace = namespace + "/"; //$NON-NLS-1$
		}
		String localpart = key.replace(namespace, ""); //$NON-NLS-1$
		if (localpart.contains("/")) { //$NON-NLS-1$
			String[] localpartSubstrings = localpart.split("\\/"); //$NON-NLS-1$
			return namespace + localpartSubstrings[0];
		}
		else {
			return key;
		}
	}

	/**
	 * @param ftName a URL identifying a {@link FeatureType} for which
	 * {@link ICell}s containing an Equivalence Relation is looked after.
	 * @return a {@link List} with all {@link ICell}s containing an 
	 * Equivalence relation, or an empty list if no such ICell is encountered.
	 */
	private List<ICell> getRenameCell(String ftName) {
		List<ICell> result = new ArrayList<ICell>();
		for (ICell cell : this.getCellsPerEntity(ftName)) {
			if (ICell.RelationType.Equivalence.equals(cell.getRelation())) {
				result.add(cell);
			}
		}
		return result;
	}
	
	/**
	 * Get the rename cell for the mapping from the given source and target
	 * feature types
	 * 
	 * @param targetName the target feature type identifier 
	 * @param sourceName the source feature type identifier
	 * 
	 * @return the rename cell
	 */
	public ICell getRenameCell(String targetName, String sourceName) {
		List<ICell> candidates = getRenameCell(targetName);
		
		for (ICell cell : candidates) {
			IEntity sourceEntity = cell.getEntity1();
			if (sourceEntity.getAbout().getAbout().equals(sourceName)) {
				return cell;
			}
		}
		
		return null;
	}
	
	private String getKeyFromEntity(Entity e) {
		String key = null;
		if (e.getClass().isAssignableFrom(FeatureClass.class)) {
			key = e.getAbout().getAbout();
		}
		else if (e.getClass().isAssignableFrom(Property.class)) {
			key = ((Property)e).getNamespace() + "/"  //$NON-NLS-1$
					+ ((Property)e).getFeatureClassName();
		}
		else if (e.getClass().isAssignableFrom(ComposedProperty.class)) {
			// FIXME determine if multiple FTs are involved, and if yes, register all of them!
			Property firstProperty = ((ComposedProperty)e).getCollection().get(0);
			key = firstProperty.getNamespace() + "/"  //$NON-NLS-1$
					+ firstProperty.getFeatureClassName();
		}
		return key;
	}
	
}
