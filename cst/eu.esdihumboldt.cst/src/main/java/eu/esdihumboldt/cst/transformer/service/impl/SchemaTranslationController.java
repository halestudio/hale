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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.align.IAlignment;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.transformer.CstFunction;
import eu.esdihumboldt.cst.transformer.service.CstFunctionFactory;
import eu.esdihumboldt.cst.transformer.service.rename.RenameFeatureFunction;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Restriction;

/**
 * This class provides Schema Translation capabilities, including complex 
 * merging and splitting cases. The algorithms used are described in the 
 * "Handling execution of gOML cells" section of the CST's reference 
 * implementation documentation.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SchemaTranslationController {
	
	private static Logger _log = Logger.getLogger(SchemaTranslationController.class);
	
	AlignmentIndex ai = null;
	
	Map<String, CellCardinalityType[]> cardinalities = null;
	
	public SchemaTranslationController(IAlignment alignment) {
		// get an AlignmentIndex
		this.ai = new AlignmentIndex(alignment);
		
		// Analyze Alignment to determine later processing
		this.cardinalities = this.determineCardinalities(alignment);
	}

	/**
	 * 
	 * @param features the input features to use for translation.
	 * @return a {@link FeatureCollection} with the translated features.
	 */
	@SuppressWarnings("unchecked")
	public FeatureCollection<FeatureType, Feature> translate(
			FeatureCollection<FeatureType, Feature> features) {
		
		// Order FeatureCollection by FeatureType
		Map<String, List<Feature>> partitionedSourceFeatures = 
			this.partitionFeaturesByType(features);
		
		// send Feature objects batchwise (grouped by target FeatureType) to 
		// CstFunctions. This step creates new Features of the target 
		// FeatureTypes.
		Map<String, List<Feature>> partitionedTargetFeatures = 
			new HashMap<String, List<Feature>>();
		
		// used for verifying whether a given FT has been processed before
		Set<String> checkTypes = new HashSet<String>();

		for (String targetFtName : this.ai.getTargetTypes()) {
		
			List<Feature> transformedFeatures = new ArrayList<Feature>();
			// build new Feature(s) first, depending on Cardinalities and applying a filter as needed
			CellCardinalityType thisFTCardinality = this.cardinalities.get(targetFtName)[0];
			CellCardinalityType thisInstanceCardinality = this.cardinalities.get(targetFtName)[1];
			if (thisFTCardinality.equals(CellCardinalityType.one_to_one)) {
				// there is exactly one relevant source FeatureType, which we now have to find.
				String sourceFtName = this.ai.getAllMappedFeatureTypes(targetFtName).iterator().next();
				if (thisInstanceCardinality.equals(CellCardinalityType.one_to_one)) {
					// create one new Feature per source Feature
					transformedFeatures = this.handleOneToOneInstanceCreation(
							targetFtName, sourceFtName, partitionedSourceFeatures);
					_log.info("Handled FTCardinality.one_to_one/" +
							"InstanceCardinality.one_to_one, created " 
							+ transformedFeatures.size() + " target features.");
					
					// apply additional attributive transformations
					List<Feature> sourceFeatures = partitionedSourceFeatures.get(sourceFtName);
					for (ICell cell : ai.getCellsPerEntity(targetFtName)) {
						// create CstFunction by using CstFunctionFactory
						CstFunction cstf = null;
						try {
							cstf = CstFunctionFactory.getInstance().getCstFunction(cell);
						} catch (Exception e) {
							throw new RuntimeException("Getting the requested " +
									"CstFunction failed: ", e);
						}
						// invoke CstFunction with Target Features List. 
						if (cstf != null && !cstf.getClass().equals(RenameFeatureFunction.class)) {
							_log.info("Applying a " + cstf.getClass().getName() + " function.");
							for (int i = 0; i < transformedFeatures.size(); i++) {
								cstf.transform(sourceFeatures.get(i), 
										transformedFeatures.get(i));
							}
						}
					}
				}
			}
			else if (thisFTCardinality.equals(CellCardinalityType.one_to_many)) {
				// In this case, there is also exactly one relevant source 
				// FeatureType, which we now have to find. In addition, we need 
				// to ensure that the operation is not performed needlessly a 
				// second time.
				String sourceFT = ai.getAllMappedFeatureTypes(targetFtName).iterator().next();
				if (!checkTypes.contains(sourceFT)) {
					if (thisInstanceCardinality.equals(CellCardinalityType.one_to_one)) {
						// create one new Feature per source Feature
						transformedFeatures = this.handleOneToOneInstanceCreation(
								targetFtName, sourceFT, partitionedSourceFeatures);
					}
					checkTypes.add(sourceFT);
				}
				_log.info("Handled FTCardinality.one_to_many/" +
						"InstanceCardinality.one_to_one, created " 
						+ transformedFeatures.size() + " target features.");
			}
			else if (thisFTCardinality.equals(CellCardinalityType.many_to_one)) {
				throw new UnsupportedOperationException(
						"FeatureType CellCardinalityType.many_to_one not " +
						"yet implemented");
			}
			else if (thisFTCardinality.equals(CellCardinalityType.many_to_many)) {
				throw new UnsupportedOperationException(
						"FeatureType CellCardinalityType.many_to_many not " +
						"yet implemented");
			}
			
			// add transformed Features to partitionedTargetFeatures
			partitionedTargetFeatures.put(targetFtName, transformedFeatures);
		}
		
		// execute Augmentations batchwise per target FeatureType
		// Augmentations don't create new Features, but change the existing ones.
		for (String url : partitionedTargetFeatures.keySet()) {
			for (ICell cell : ai.getCellsPerEntity(url)) {
				// create CstFunction by using CstFunctionFactory
				CstFunction cstf = null;
				try {
					cstf = CstFunctionFactory.getInstance().getCstAugmentationFunction(cell);
				} catch (Exception e) {
					throw new RuntimeException("Getting the requested " +
							"CstFunction failed: ", e);
				}
				// invoke CstFunction with Target Features List. 
				if (cstf != null) {
					for (Feature f : partitionedTargetFeatures.get(url)) {
						f = cstf.transform(f, f);
					}
				}
			}
		}
		FeatureCollection result = FeatureCollections.newCollection();
		
		for (List<Feature> featureList : partitionedTargetFeatures.values()) {
			result.addAll(featureList);
		}
		
		return result;
	}
	
	/**
	 * Determine cardinalities on Instance and FeatureType level.
	 * @param alignment the {@link IAlignment} to analyse.
	 * @return a {@link Map} with the name as key and a 
	 * Array of two values, the first one indicating the 
	 * {@link FeatureType} level cardinality, the second one indicating the 
	 * {@link Feature} level cardinality (instance splits and merges)
	 * @throws MalformedURLException 
	 */
	private Map<String, CellCardinalityType[]> determineCardinalities(IAlignment alignment) {
		Map<String, CellCardinalityType[]> result = 
			new HashMap<String, CellCardinalityType[]>();
		
		// fill map with null values
		for (ICell cell : alignment.getMap()) {
			if (cell.getEntity2().getClass().isAssignableFrom(FeatureClass.class)) {
				String key = cell.getEntity2().getAbout().getAbout();
				CellCardinalityType[] value = new CellCardinalityType[]{null, null};
				result.put(key, value);
			}
		}
		
		// Analyse actual FT cardinalities first
		Set<String> encounteredTargetTypes = new HashSet<String>();
		for (ICell cell : alignment.getMap()) {
			// we're only looking for cells that map FeatureClasses
			if (cell.getEntity2().getClass().isAssignableFrom(FeatureClass.class)) {
				FeatureClass fc1 = (FeatureClass)cell.getEntity1();
				FeatureClass fc2 = (FeatureClass)cell.getEntity2();
				
				CellCardinalityType[] cct = result.get(fc2.getAbout().getAbout());
				
				if (cct[0] == null) {
					cct[0] = CellCardinalityType.one_to_one;
				}
				else {
					// look for Filters to identify one_to_many
					if (fc1.getAttributeValueCondition() != null 
							&& !fc1.getAttributeValueCondition().isEmpty() 
							&& fc1.getAttributeValueCondition().get(0).getCqlStr() != null) {
						if (cct[0].equals(CellCardinalityType.one_to_one)) {
							cct[0] = CellCardinalityType.one_to_many;
						}
						else if (cct[0].equals(CellCardinalityType.many_to_one)) {
							cct[0] = CellCardinalityType.many_to_many;
						}
					}
					
					// look for target types that occur more than once to identify many_to_one cases
					if (encounteredTargetTypes.contains(fc2.getAbout().getAbout())) {
						if (cct[0].equals(CellCardinalityType.one_to_one)) {
							cct[0] = CellCardinalityType.many_to_one;
						}
						else if (cct[0].equals(CellCardinalityType.one_to_many)) {
							cct[0] = CellCardinalityType.many_to_many;
						}
					} 
					else {
						encounteredTargetTypes.add(fc2.getAbout().getAbout());
					}
				}
			}
		}
		
		// for instance cardinalities, we're looking for split and merge conditions.
		// FIXME for now only returning 1to1
		for (CellCardinalityType[] cardinalities : result.values()) {
			cardinalities[1] = CellCardinalityType.one_to_one;
		}
		
		return result;
	}
	
	private Map<String, List<Feature>> partitionFeaturesByType(
			FeatureCollection<FeatureType, Feature> features) {
		Map<String, List<Feature>> result = 
			new HashMap<String, List<Feature>>();
		
		FeatureIterator<Feature> fi = features.features();
		while (fi.hasNext()) {
			Feature f = fi.next();
			String ftk  = f.getType().getName().getNamespaceURI() 
						+ "/" + f.getType().getName().getLocalPart();
			List<Feature> list = null;
			if (result.keySet().contains(ftk)) {
				list = result.get(ftk);
			}
			else {
				list = new ArrayList<Feature>();
				result.put(ftk, list);
			}
			list.add(f);
		}
		return result;
	}

	
	@SuppressWarnings("unchecked")
	private List<Feature> handleOneToOneInstanceCreation(
			String targetFtName, String sourceFtName, Map<String, List<Feature>> partitionedSourceFeatures) {
		
		FeatureCollection sourceFeatures = FeatureCollections.newCollection();
		List<Feature> features = partitionedSourceFeatures.get(sourceFtName);
		if (features != null && features.size() > 0) {
			for (Feature f : features) {
				sourceFeatures.add(f);
			}
		}
		List<Feature> transformedFeatures = new ArrayList<Feature>();
		
		// create one new Feature per source Feature
		List<ICell> cells = this.ai.getRenameCell(targetFtName);
		for (ICell cell : cells) {
			CstFunction rtf = CstFunctionFactory.getInstance().getCstFunction(cell);
			// check for any relevant filters on the cell
			String cql = null;
			List<Restriction> avclist = ((FeatureClass)cell.getEntity1()).getAttributeValueCondition();
			if (avclist != null 
					&& avclist.size() > 0 
					&& avclist.get(0) != null) {
				cql = avclist.get(0).getCqlStr();
			}
			
			// now, apply renaming Feature by Feature, ignoring those which don't fulfill the filtering criteria
			if (cql != null) {
				try {
					sourceFeatures = sourceFeatures.subCollection(CQL.toFilter(cql));
				} catch (CQLException e) {
					throw new RuntimeException("The given CQL string could not be " +
							"used to build a Filter: ", e);
				}
			}
			FeatureIterator<Feature> fi = sourceFeatures.features();
			while (fi.hasNext()) {
				transformedFeatures.add(rtf.transform(fi.next(), null));
			}
		}
		return transformedFeatures;
	}
	

	
	/**
	 * This enumeration is used to describe both the cardinality a given Cell 
	 * describes on the level of {@link FeatureType}s and of the {@link Feature}
	 * instances.
	 */
	public enum CellCardinalityType {
		one_to_one,
		one_to_many,
		many_to_one,
		many_to_many
	}
	
}
