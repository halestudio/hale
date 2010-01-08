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

package eu.esdihumboldt.cst.transformer.impl;

import java.util.HashMap;
import java.util.Map;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.transformer.AbstractCstFunction;

/**
 * CstFunction for filtering features based on CQL. The CQL string that filters
 * the data is specified in constructor. Input is FeatureCollection and output
 * is FeatureCollection filtered by CQL specified in constructor.
 * 
 * @author jezekjan
 * 
 */
public class FilterTransformer extends AbstractCstFunction {

	
	/**
	 * CQL Filter
	 */
	private Filter cqlfilter;

	/**
	 * CQL Filter parametr name
	 */
	public static final String CQL_PARAMETER = "CQL";

	

	public SimpleFeatureType getTargetType(FeatureType ft) {
		// TODO Auto-generated method stub
		/**
		 * this transformer leave the schema unchanged
		 */
		return (SimpleFeatureType) ft;
	}

	@SuppressWarnings("unchecked")
	public Feature transform(Feature source, Feature target) {
		// TODO Cell c is ignored
		FeatureCollection fc = new DefaultFeatureCollection("id",
				(SimpleFeatureType) source.getType());
		fc.add(source);
		return transform(fc).features().next();
	}


	/* (non-Javadoc)
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#transform(org.geotools.feature.FeatureCollection)
	 */
	@SuppressWarnings("unchecked")
	public FeatureCollection<? extends FeatureType, ? extends Feature> transform(
			FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
		FeatureCollection targetfc = null;

		try {
			SimpleFeatureType targetType = getTargetType(fc.getSchema());
			targetfc = new DefaultFeatureCollection("id", targetType);

			targetfc.addAll(fc.subCollection(cqlfilter));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return targetfc;
	}

	/*
	 * (non-Javadoc)
	 * @see eu.esdihumboldt.cst.transformer.AbstractCstFunction#configure(java.util.Map)
	 */
	public boolean configure(Map<String, String> parametersValues) {		
		try {

			String filter = parametersValues.get(CQL_PARAMETER);
			cqlfilter = CQL.toFilter(filter);
		} catch (CQLException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	

	@Override
	protected void setParametersTypes(Map<String, Class<?>> parameters) {
		parameterTypes.put(CQL_PARAMETER, String.class);
		
	}

	public boolean configure(ICell cell) {
		// TODO Auto-generated method stub
		return false;
	}

}
