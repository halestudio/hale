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

import java.util.Iterator;
import java.util.Map;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.transformer.AbstractCstFunction;
import eu.esdihumboldt.cst.transformer.CstFunction;
import eu.esdihumboldt.cst.transformer.service.impl.TargetSchemaProvider;

/**
 * CstFunction for feature renaming, i.e. the creation of new {@link Feature}s 
 * in the target schema. Also copies the default geometry if possible.
 * 
 * @author Thorsten Reitz, Jan Jezek
 * @version $Id: RenameFeatureFunction.java 2418 2009-12-22 11:35:12Z jjezek $ 
 */
public class RenameFeatureFunction 
	extends AbstractCstFunction {

	// private String featureName = null;

	private String newName;

	public static final String TARGET_FEATURETYPE_NAME = "ENTITY_2_NAME";

	@SuppressWarnings("unchecked")
	public FeatureCollection<? extends FeatureType, ? extends Feature> transform(
			FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
		
		FeatureCollection targetfc = FeatureCollections.newCollection();
		SimpleFeatureType targetType = this.getTargetType(this.newName);
		for (Iterator<? extends Feature> i = fc.iterator(); i.hasNext();) {
			Feature source = i.next();
			Feature target = SimpleFeatureBuilder.build(
					targetType, new Object[]{}, source.getIdentifier().getID());
			
			// copy geometry by default if possible
			this.copyGeometry(source, target);
		}
		return targetfc;
	}

	/**
	 * Note that the target parameter is ignored by this transformer.
	 * @see CstFunction#transform(Feature, Feature)
	 */
	public Feature transform(Feature source, Feature target) {
		SimpleFeatureType targetType = this.getTargetType(this.newName);
		target = SimpleFeatureBuilder.build(
				targetType, new Object[]{}, source.getIdentifier().getID());
		
		// copy geometry by default if possible
		this.copyGeometry(source, target);
		return target;
	}
	
	private void copyGeometry(Feature source, Feature target) {
		try {
			if (source.getDefaultGeometryProperty() != null 
					&& target.getDefaultGeometryProperty() != null) {
				((SimpleFeatureImpl)target).setDefaultGeometry(
						source.getDefaultGeometryProperty().getValue());
				
			}
		} catch (IllegalAttributeException iea) {
			throw new RuntimeException(iea.getMessage());
		}
	}

	public boolean configure(Map<String, String> parametersValues) {
		return false;
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
		return true;
	}

	@Override
	protected void setParametersTypes(Map<String, Class<?>> parametersTypes) {
		 parametersTypes.put(TARGET_FEATURETYPE_NAME, String.class);		
	}
	
}
