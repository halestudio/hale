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

package eu.esdihumboldt.cst.transformer.service.rename;

import java.util.Collection;
import java.util.List;

import org.geotools.feature.FeatureImpl;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeatureType;

import eu.esdihumboldt.cst.AbstractCstFunction;
import eu.esdihumboldt.cst.CstFunction;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.transformer.service.impl.TargetSchemaProvider;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;

/**
 * CstFunction for feature renaming, i.e. the creation of new {@link Feature}s 
 * in the target schema. Also copies the default geometry if possible.
 * 
 * @author Thorsten Reitz, Jan Jezek, Ulrich Schaeffler
 * @version $Id: RenameFeatureFunction.java 2418 2009-12-22 11:35:12Z jjezek $ 
 */
public class RenameFeatureFunction 
	extends AbstractCstFunction {
	
	/**
	 * Parameter name for instance merge condition
	 */
	public static final String PARAMETER_INSTANCE_MERGE_CONDITION = "InstanceMergeCondition";

	/**
	 * Parameter name for instance split condition
	 */
	public static final String PARAMETER_INSTANCE_SPLIT_CONDITION = "InstanceSplitCondition";
	
	public static final String ONATTRIBUTE = "SelectedAttribute";
	
	public static final String TARGETATTRIBUTE = "TargetAttribute";
	
	enum Mode { normal, split, merge, join }

	private FeatureSplitter splitter = null;
	
	private FeatureAggregator merger = null;
	//TODO: Spatial Joiner
	private FeatureSpatialJoiner spatialjoiner = null;

	private String newName;

	/**
	 * Note that the target parameter is ignored by this transformer.
	 * @see CstFunction#transform(Feature, Feature)
	 */
	public Feature transform(Feature source, Feature target) {
		SimpleFeatureType targetType = this.getTargetType(this.newName);
		target = FeatureBuilder.buildFeature(targetType, source, false);
		
		// copy geometry by default if possible
		this.copyGeometry(source, target);
		return target;
	}
	
	private void copyGeometry(Feature source, Feature target) {
		try {
			if (source.getDefaultGeometryProperty() != null) {
				Object sourceGeom = source.getDefaultGeometryProperty().getValue();
				if (target instanceof SimpleFeatureImpl) {
					((SimpleFeatureImpl)target).setDefaultGeometry(sourceGeom);
				}
				else if (target instanceof FeatureImpl) {
					GeometryAttribute gattr = target.getDefaultGeometryProperty();
					gattr.setValue(sourceGeom);
				}
			}
		} catch (IllegalAttributeException iea) {
			throw new RuntimeException(iea.getMessage());
		}
	}

	
	private SimpleFeatureType getTargetType(String sourceType) {
		if (sourceType == null) {
			throw new NullPointerException(
					"The passed SourceType may not be null.");
		}
		SimpleFeatureType tft = (SimpleFeatureType)
						TargetSchemaProvider.getInstance().getType(sourceType);
		return tft;
	}

	public boolean configure(ICell cell) {
		this.newName = cell.getEntity2().getAbout().getAbout();
		List<IParameter> paramList = cell.getEntity1().getTransformation().getParameters();
		Mode mode = Mode.normal; // default

		String rule = null;
		String onAttribute = null;
		String targetAttribute = null;

		for (IParameter ip : paramList) {
			if (ip.getName().equals(PARAMETER_INSTANCE_SPLIT_CONDITION)) {
				mode = Mode.split;
				rule = ip.getValue();
			}
			if (ip.getName().equals(PARAMETER_INSTANCE_MERGE_CONDITION)) {
				mode = Mode.merge;
				rule = ip.getValue();
			}
			if (ip.getName().equals(ONATTRIBUTE)){
				onAttribute = ip.getValue();
			}
			if (ip.getName().equals(TARGETATTRIBUTE)){
				targetAttribute = ip.getValue();
			}
		}
		
		if (mode.equals(Mode.split) && rule != null && onAttribute!=null &&targetAttribute!=null){
			this.splitter = new FeatureSplitter(onAttribute, rule, targetAttribute);
		}
		else if (mode.equals(Mode.merge) && rule != null && onAttribute!=null&&targetAttribute!=null){
			this.merger = new FeatureAggregator(onAttribute, rule, targetAttribute);
		}
		
		if (this.splitter != null && this.merger != null) {
			throw new RuntimeException("Only a Merge OR a Split condition " +
					"may be used, not both.");
		}
		
		return true;
	}
	
	/**
	 * @see CstFunction#getParameters()
	 */
	public Cell getParameters() {
		Cell parameterCell = new Cell();
		Property entity1 = new Property(new About(""));
		Property entity2 = new Property(new About(""));
	
		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}

	/**
	 * A non-standard operation for CstFunctions which allows to create multiple 
	 * {@link Feature}s from a single one.
	 * @param sourceFeature
	 * @param targetFeatures
	 * @return a {@link List} with the splitted {@link Feature}s.
	 */
	public List<Feature> transformSplit(Feature sourceFeature, List<Feature> targetFeatures) {
		if (this.splitter != null) {
			targetFeatures = this.splitter.split(sourceFeature, 
					this.getTargetType(this.newName));
		}
		return targetFeatures;
	}
	
	
	
	/**
	 * A non-standard operation for CstFunctions which allows merging of 
	 * {@link Feature}s.
	 * @param sourceFeature
	 * @param targetFeatures
	 * @return a {@link List} with the merged {@link Feature}s.
	 */
	public List<Feature> transformMerge(Collection<? extends Feature> sourceFeature, List<Feature> targetFeatures) {
		if (this.merger != null) {
			targetFeatures = this.merger.aggregate(sourceFeature, 
					this.getTargetType(this.newName));
		}
		return targetFeatures;
	}
	
	
}
