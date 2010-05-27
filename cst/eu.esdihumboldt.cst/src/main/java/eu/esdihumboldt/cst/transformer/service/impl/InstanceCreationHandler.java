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
package eu.esdihumboldt.cst.transformer.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.Feature;

import eu.esdihumboldt.cst.CstFunction;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.transformer.service.CstFunctionFactory;
import eu.esdihumboldt.cst.transformer.service.rename.RenameFeatureFunction;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Restriction;

/**
 * The {@link InstanceCreationHandler} collects those methods used to control
 * the creation of instances before additional attributive and augmentation 
 * transformations are applied.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class InstanceCreationHandler {
	
	/**
	 * Handle simple one-to-one instance creation cases
	 * 
	 * @param targetFtName
	 * @param sourceFtName
	 * @param partitionedSourceFeatures
	 * @param ai
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static InstanceMap oneToOne(
				String targetFtName, 
				String sourceFtName, 
				Map<String, List<Feature>> partitionedSourceFeatures,
				ICell renameCell) {
			
			FeatureCollection sourceFeatures = FeatureCollections.newCollection();
			List<Feature> features = partitionedSourceFeatures.get(sourceFtName);
			// FIXME temp solution until solution for correct typename is found.
			if (features == null) {
				String modifiedTypeName = null;
				if (sourceFtName.lastIndexOf("_Type") != -1) {
					modifiedTypeName = sourceFtName.substring(0, sourceFtName.lastIndexOf("_Type"));
				}
				else if (sourceFtName.lastIndexOf("Type") != -1) {
					modifiedTypeName = sourceFtName.substring(0, sourceFtName.lastIndexOf("Type"));
				}
				features = partitionedSourceFeatures.get(modifiedTypeName);
			}
			if (features != null && features.size() > 0) {
				for (Feature f : features) {
					sourceFeatures.add(f);
				}
			}
			
			List<Feature> transformedFeatures = new ArrayList<Feature>();
			List<Feature> sourceList = new ArrayList<Feature>();
			
			// create one new Feature per source Feature
			CstFunction rtf = CstFunctionFactory.getInstance().getCstFunction(renameCell);
			// check and apply relevant filters on the cell
			sourceFeatures = filterCollection(sourceFeatures, renameCell);
			
			FeatureIterator<Feature> fi = sourceFeatures.features();
			while (fi.hasNext()) {
				Feature sourceFeature = fi.next();
				transformedFeatures.add(rtf.transform(sourceFeature, null));
				sourceList.add(sourceFeature);
			}
			
			return new InstanceMap(sourceList, transformedFeatures);
	}

	/**
	 * Handle split cases.
	 * 
	 * @param targetFtName
	 * @param sourceFtName
	 * @param partitionedSourceFeatures
	 * @param renameCell
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static InstanceSplitMap oneToMany(
			String targetFtName,
			String sourceFtName,
			Map<String, List<Feature>> partitionedSourceFeatures,
			ICell renameCell) {
		
		FeatureCollection sourceFeatures = FeatureCollections.newCollection();
		List<Feature> features = partitionedSourceFeatures.get(sourceFtName);
		if (features != null && features.size() > 0) {
			for (Feature f : features) {
				sourceFeatures.add(f);
			}
		}
		
		List<List<Feature>> transformedFeatures = new ArrayList<List<Feature>>();
		List<Feature> sourceList = new ArrayList<Feature>();
		
		// create one new Feature per source Feature
		RenameFeatureFunction rtf = new RenameFeatureFunction();
		rtf.configure(renameCell);
		
		sourceFeatures = filterCollection(sourceFeatures, renameCell);
		FeatureIterator<Feature> fi = sourceFeatures.features();
		while (fi.hasNext()) {
			Feature sourceFeature = fi.next();
			transformedFeatures.add(rtf.transformSplit(sourceFeature, null));
			sourceList.add(sourceFeature);
		}
		
		return new InstanceSplitMap(sourceList, transformedFeatures);
	}
	
	/**
	 * Handle merge cases.
	 * 
	 * @param targetFtName
	 * @param sourceFtName
	 * @param partitionedSourceFeatures
	 * @param ai
	 * @return
	 */
	public static InstanceAggregateMap manyToOne(String targetFtName,
			String sourceFtName,
			Map<String, List<Feature>> partitionedSourceFeatures,
			ICell renameCell) {
		
		FeatureCollection sourceFeatures = FeatureCollections.newCollection();
		List<Feature> features = partitionedSourceFeatures.get(sourceFtName);
		if (features != null && features.size() > 0) {
			for (Feature f : features) {
				sourceFeatures.add(f);
			}
		}
		
		List<Feature> transformedFeatures = new ArrayList<Feature>();
		List<Feature> sourceList = new ArrayList<Feature>();
		
		// create one new Feature per source Feature
		RenameFeatureFunction rtf = new RenameFeatureFunction();
		rtf.configure(renameCell);
		
		sourceFeatures = filterCollection(sourceFeatures, renameCell);
		List<Feature> temp = new ArrayList<Feature>();
		FeatureIterator<Feature> fi = sourceFeatures.features();
		while (fi.hasNext()) {
			Feature sourceFeature = fi.next();
			temp.add(sourceFeature);
		}
		
		transformedFeatures.addAll(rtf.transformMerge(temp, null));
		sourceList.addAll(temp);
		
		
		return new InstanceAggregateMap(sourceList, transformedFeatures);
	}
	
	@SuppressWarnings("unchecked")
	private static FeatureCollection filterCollection(FeatureCollection sourceFeatures, ICell renameCell) {
		// check for any relevant filters on the cell
		String cql = null;
		List<Restriction> avclist = ((FeatureClass)renameCell.getEntity1()).getAttributeValueCondition();
		if (avclist != null 
				&& avclist.size() > 0 
				&& avclist.get(0) != null) {
			cql = avclist.get(0).getCqlStr();
		}
		
		// now, create filter and apply to create a subCollection
		if (cql != null) {
			try {
				sourceFeatures = sourceFeatures.subCollection(CQL.toFilter(cql));
			} catch (CQLException e) {
				throw new RuntimeException("The given CQL string could not be " +
						"used to build a Filter: ", e);
			}
		}
		return sourceFeatures;
	}

}
