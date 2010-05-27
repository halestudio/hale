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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.metadata.iso.lineage.LineageImpl;
import org.geotools.metadata.iso.lineage.ProcessStepImpl;
import org.geotools.util.SimpleInternationalString;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.metadata.lineage.Lineage;

import eu.esdihumboldt.cst.CstFunction;
import eu.esdihumboldt.cst.align.IAlignment;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.transformer.capabilities.impl.FunctionDescriptionImpl;
import eu.esdihumboldt.cst.transformer.service.CstFunctionFactory;
import eu.esdihumboldt.cst.transformer.service.CstServiceFactory.ToleranceLevel;
import eu.esdihumboldt.cst.transformer.service.rename.RenameFeatureFunction;

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
	
	private final AlignmentIndex ai;
	
	private final MappingInfo mapping;
	
	private boolean strict = true;
	
	private boolean addLineage = true;
	
	/**
	 * Constructor
	 * @param tl the {@link ToleranceLevel} that this {@link SchemaTranslationController} 
	 * should use in handling errors.
	 * @param createLineage true if {@link Lineage} metadata should be added to 
	 * transformed features.
	 * @param alignment the alignment
	 */
	public SchemaTranslationController(ToleranceLevel tl, 
			boolean createLineage, IAlignment alignment) {
		// get an AlignmentIndex
		this.ai = new AlignmentIndex(alignment);
		
		// Analyze Alignment to determine later processing
		this.mapping = this.determineCardinalities(alignment);
		
		if (tl != null) {
			if (ToleranceLevel.lenient.equals(tl)) {
				this.strict = false;
			}
		}
		
		this.addLineage = createLineage;
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
		//Set<String> checkTypes = new HashSet<String>();

		for (String targetType : mapping.getTargetTypes()) {
			List<Feature> transformed = new ArrayList<Feature>();
			Set<String> sourceTypes = mapping.getSourceTypes(targetType);
			
			// one_to_one, many_to_one (no inter-type instance merge)
			for (String sourceType : sourceTypes) {
				RetypeInfo info = mapping.getRetypeInfo(sourceType, targetType);
				
				switch (info.getInstanceCardinality()) {
				case one_to_one:
					{
						// create one new Feature per source Feature
						InstanceMap transformMap = InstanceCreationHandler.oneToOne(
								targetType, sourceType, 
								partitionedSourceFeatures, 
								info.getRenameCell());
						
						_log.info("Handled FTCardinality.one_to_one/" +
								"InstanceCardinality.one_to_one, created " 
								+ transformMap.getTransformedFeatures().size() + " target features.");
						
						// apply additional attributive transformations
						for (ICell cell : info.getAttributiveCells()) {
							// create CstFunction by using CstFunctionFactory
							applyAttributiveFunction(cell, transformMap);
						}
						
						transformed.addAll(transformMap.getTransformedFeatures());
					}
					break;
				case one_to_many: // instance split
					{
						// create multiple new Features per source Feature
						InstanceSplitMap transformMap = InstanceCreationHandler.oneToMany(
								targetType, sourceType, 
								partitionedSourceFeatures, 
								info.getRenameCell());
						
						_log.info("Handled FTCardinality.one_to_one/" +
								"InstanceCardinality.one_to_many, created " 
								+ transformMap.getTransformedFeatures().size() + " target features.");
						
						// apply additional attributive transformations
						for (ICell cell : info.getAttributiveCells()) {
							// create CstFunction by using CstFunctionFactory
							applyAttributiveFunction(cell, transformMap);
						}
						for (List<Feature> thisList : transformMap.getTransformedFeatures()) {
							transformed.addAll(thisList);
						}
					}
					break;
				case many_to_one: // instance aggregate
				{
					// create multiple new Features per source Feature
					InstanceAggregateMap transformMap = InstanceCreationHandler.manyToOne(
							targetType, sourceType, 
							partitionedSourceFeatures, 
							info.getRenameCell());
					
					_log.info("Handled FTCardinality.one_to_one/" +
							"InstanceCardinality.many_to_one, created " 
							+ transformMap.getTransformedFeatures().size() + " target features.");
					
					// apply additional attributive transformations
					for (ICell cell : info.getAttributiveCells()) {
						// create CstFunction by using CstFunctionFactory
						applyAttributiveFunction(cell, transformMap);
					}
					for (Feature thisList : transformMap.getTransformedFeatures()) {
						transformed.add(thisList);
					}
				}
					break;
				}
			}
			
			// build new Feature(s) first, depending on Cardinalities and applying a filter as needed
			/*CellCardinalityType thisFTCardinality = this.cardinalities.get(targetFtName)[0];
			CellCardinalityType thisInstanceCardinality = this.cardinalities.get(targetFtName)[1];
			switch (thisFTCardinality) {
			case one_to_one:
			case many_to_one:
				{
					// one_to_one: there is exactly one relevant source FeatureType, which we now have to find.
					// many_to_one: multiple source feature types -> handle each source feature type as a one_to_one case
					
					Set<String> sourceFtNames = this.ai.getAllMappedFeatureTypes(targetFtName);
					
					for (String sourceFtName : sourceFtNames) {
						if (thisInstanceCardinality.equals(CellCardinalityType.one_to_one)) {
							// create one new Feature per source Feature
							InstanceMap transformMap = InstanceCreationHandler.oneToOne(
									targetFtName, sourceFtName, 
									partitionedSourceFeatures, 
									ai.getRenameCell(targetFtName, sourceFtName));
							_log.info("Handled FTCardinality.one_to_one/" +
									"InstanceCardinality.one_to_one, created " 
									+ transformMap.getTransformedFeatures().size() + " target features.");
							
							// apply additional attributive transformations
							for (ICell cell : ai.getAttributiveCellsPerEntity(targetFtName)) {
								// create CstFunction by using CstFunctionFactory
								this.applyAttributiveFunction(cell, transformMap);
							}
							transformed.addAll(transformMap.getTransformedFeatures());
						}
						else if (thisInstanceCardinality.equals(CellCardinalityType.one_to_many)) {
							// create multiple new Features per source Feature
							InstanceSplitMap transformMap = InstanceCreationHandler.oneToMany(
									targetFtName, sourceFtName, 
									partitionedSourceFeatures, 
									ai.getRenameCell(targetFtName, sourceFtName));
							_log.info("Handled FTCardinality.one_to_one/" +
									"InstanceCardinality.one_to_many, created " 
									+ transformMap.getTransformedFeatures().size() + " target features.");
							
							// apply additional attributive transformations
							for (ICell cell : ai.getCellsPerEntity(targetFtName)) {
								// create CstFunction by using CstFunctionFactory
								this.applyAttributiveFunction(cell, transformMap);
							}
							for (List<Feature> thisList : transformMap.getTransformedFeatures()) {
								transformed.addAll(thisList);
							}
							
						}
						else if (thisInstanceCardinality.equals(CellCardinalityType.many_to_one)) {
							// TODO aggregate case
						}
					}
				}
				break;
			case one_to_many:
				{
					// In this case, there is also exactly one relevant source 
					// FeatureType, which we now have to find. In addition, we need 
					// to ensure that the operation is not performed needlessly a 
					// second time.
					String sourceFtName = ai.getAllMappedFeatureTypes(targetFtName).iterator().next();
					if (!checkTypes.contains(sourceFtName)) {
						if (thisInstanceCardinality.equals(CellCardinalityType.one_to_one)) {
							// create one new Feature per source Feature
							InstanceMap transformMap = InstanceCreationHandler.oneToOne(
									targetFtName, sourceFtName, 
									partitionedSourceFeatures, 
									ai.getRenameCell(targetFtName, sourceFtName));
							
							_log.info("Handled FTCardinality.one_to_many/" +
									"InstanceCardinality.one_to_one, created " 
									+ transformMap.getTransformedFeatures().size() 
									+ " target features.");
							
							// apply additional attributive transformations
							for (ICell cell : ai.getCellsPerEntity(targetFtName)) {
								// create CstFunction by using CstFunctionFactory
								this.applyAttributiveFunction(cell, transformMap);
							}
							
							transformed.addAll(transformMap.getTransformedFeatures());
						}
						checkTypes.add(sourceFtName);
					}
				}
				break;
			case many_to_many:
				{
					throw new UnsupportedOperationException(
							"FeatureType CellCardinalityType.many_to_many not " +
							"yet implemented");
				}
				//break;
			}*/
			
			// add transformed Features to partitionedTargetFeatures
			if (transformed != null) {
				partitionedTargetFeatures.put(targetType, 
						transformed);
			}
		}
		
		// execute Augmentations batchwise per target FeatureType
		// Augmentations don't create new Features, but change the existing ones.
		for (Entry<String, List<Feature>> entry : partitionedTargetFeatures.entrySet()) {
			String url = entry.getKey();
			for (ICell cell : ai.getAugmentationCellsPerEntity(url)) {
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
					List<Feature> featureList = entry.getValue();
					for (Feature f : featureList) {
						f = cstf.transform(f, f);
					}
				}
			}
		}
		FeatureCollection result = FeatureCollections.newCollection();
		long timeStart = System.currentTimeMillis();
		for (List<Feature> featureList : partitionedTargetFeatures.values()) {
			for (Feature complexFeature : featureList) {
				// create new SimpleFeature from complexFeature
				Feature simplifiedFeature = SimpleFeatureBuilder.build(
						(SimpleFeatureType)complexFeature.getType(), 
						new Object[]{},
						complexFeature.getIdentifier().getID());
				for (org.opengis.feature.Property p : complexFeature.getProperties()) {
					simplifiedFeature.getProperty(p.getName()).setValue(p.getValue());
					simplifiedFeature.getProperty(p.getName()).getUserData().putAll(p.getUserData());
				}
				simplifiedFeature.setDefaultGeometryProperty(
						complexFeature.getDefaultGeometryProperty());
				simplifiedFeature.getUserData().putAll(complexFeature.getUserData());
				result.add(simplifiedFeature);
			}
		}
		_log.info("Simplifying features took " + (System.currentTimeMillis() - timeStart) + "ms.");
		return result;
	}
	
	private void applyAttributiveFunction(ICell cell,
			InstanceSplitMap transformMap) {
		try {
			// create CstFunction by using CstFunctionFactory
			CstFunction cstf = CstFunctionFactory.getInstance().getCstFunction(cell);
		
			// invoke CstFunction with Target Features List. 
			if (cstf != null && !cstf.getClass().equals(RenameFeatureFunction.class)) {
				_log.info("Applying a " + cstf.getClass().getName() + " function.");
				for (int i = 0; i < transformMap.getTransformedFeatures().size(); i++) {
					for (Feature target : transformMap.getTransformedFeatures().get(i)) {
						String error = null;
						try {
							cstf.transform(
									transformMap.getSourceFeatures().get(i), 
									target);
							
						} catch (Exception e) {
							if (this.strict) {
								throw new RuntimeException("Executing the requested " +
										"CstFunction failed: ", e);
							}
							else {
								error = e.getMessage();
							}
						}
						if (this.addLineage) {
							this.addLineage(error, cstf, target, cell);
						}
					}
				}
			}
		} catch (Exception e) {
			if (this.strict) {
				throw new RuntimeException("Executing the requested " +
						"CstFunction failed: ", e);
			}
			else {
				_log.error("Executing the requested " +
						"CstFunction failed: ", e);
			}
		}
	}
	
	
	private void applyAttributiveFunction(ICell cell,
			InstanceAggregateMap transformMap) {
		try {
			// create CstFunction by using CstFunctionFactory
			CstFunction cstf = CstFunctionFactory.getInstance().getCstFunction(cell);
		
			// invoke CstFunction with Target Features List. 
			if (cstf != null && !cstf.getClass().equals(RenameFeatureFunction.class)) {
				_log.info("Applying a " + cstf.getClass().getName() + " function.");
				for (int i = 0; i < transformMap.getTransformedFeatures().size(); i++) {
					String error = null;
					try {
						cstf.transform(transformMap.getSourceFeatures().get(i), 
								transformMap.getTransformedFeatures().get(i));
					} catch (Exception e) {
						if (this.strict) {
							throw new RuntimeException("Executing the requested " +
									"CstFunction failed: ", e);
						}
						else {
							error = e.getMessage();
						}
					}
					if (this.addLineage) {
						this.addLineage(error, cstf, 
								transformMap.getTransformedFeatures().get(i), cell);
					}
				}
			}
		} catch (Exception e) {
			if (this.strict) {
				throw new RuntimeException("Executing the requested " +
						"CstFunction failed: ", e);
			}
			else {
				_log.error("Executing the requested " +
						"CstFunction failed: ", e);
			}
		}
	}

	private void applyAttributiveFunction(ICell cell, InstanceMap transformMap) {	
		try {
			// create CstFunction by using CstFunctionFactory
			CstFunction cstf = CstFunctionFactory.getInstance().getCstFunction(cell);
		
			// invoke CstFunction with Target Features List. 
			if (cstf != null && !cstf.getClass().equals(RenameFeatureFunction.class)) {
				_log.info("Applying a " + cstf.getClass().getName() + " function.");
				for (int i = 0; i < transformMap.getTransformedFeatures().size(); i++) {
					String error = null;
					try {
						cstf.transform(transformMap.getSourceFeatures().get(i), 
								transformMap.getTransformedFeatures().get(i));
					} catch (Exception e) {
						if (this.strict) {
							throw new RuntimeException("Executing the requested " +
									"CstFunction failed: ", e);
						}
						else {
							error = e.getMessage();
						}
					}
					if (this.addLineage) {
						this.addLineage(error, cstf, 
								transformMap.getTransformedFeatures().get(i), cell);
					}
				}
			}
		} catch (Exception e) {
			if (this.strict) {
				throw new RuntimeException("Executing the requested " +
						"CstFunction failed: ", e);
			}
			else {
				_log.error("Executing the requested " +
						"CstFunction failed: ", e);
			}
		}
	}
	
	private void addLineage(String error, CstFunction cstf, Feature target, ICell cell) {
		// add information about transformation to Feature
		ProcessStepImpl ps = new ProcessStepImpl();
		ps.setDate(new Date());
		if (error != null) {
			ps.setDescription(new SimpleInternationalString(
					"HUMBOLDT Conceptual Schema Transformer 1.0.0: " +
					cstf.getClass().getName() + " execution error: " + error));
		}
		else {
			ps.setDescription(new SimpleInternationalString(
					"HUMBOLDT Conceptual Schema Transformer 1.0.0: " 
					+ cstf.getClass().getName().substring(cstf.getClass().getName().lastIndexOf('.') + 1)
					+ " applied with the following parameters: "
					+ new FunctionDescriptionImpl(cell).toString()));
		}	
		
		Object o = target.getUserData().get("METADATA_LINEAGE");
		if (o != null) {
			((LineageImpl)o).getProcessSteps().add(ps);
		}
		else {
			Lineage lineage = new LineageImpl();
			((LineageImpl)lineage).getProcessSteps().add(ps);
			target.getUserData().put("METADATA_LINEAGE", lineage);
		}
	}
	
	/**
	 * Determine cardinalities on Instance and FeatureType level.
	 * @param alignment the {@link IAlignment} to analyse.
	 * @return a {@link Map} with the name as key and a 
	 * Array of two values, the first one indicating the 
	 * {@link FeatureType} level cardinality, the second one indicating the 
	 * {@link Feature} level cardinality (instance splits and merges)
	 */
	private MappingInfo determineCardinalities(IAlignment alignment) {
		MappingInfo info = new MappingInfo();
		
		for (ICell cell : alignment.getMap()) {
			info.addCell(cell);
		}
		
		return info;
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
	
}
