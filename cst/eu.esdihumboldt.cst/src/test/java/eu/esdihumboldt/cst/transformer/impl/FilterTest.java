package eu.esdihumboldt.cst.transformer.impl;
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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;

import eu.esdihumboldt.cst.DataUtils;


public class FilterTest {

	@Test
	public void testFilterFeature(){
		FeatureCollection fc = (new DataUtils()).getMyFC();
		String filter = "Name = 'Here1'";
		FilterTransformer ft = new FilterTransformer();
		Map<String, String> params = new HashMap();
		params.put(FilterTransformer.CQL_PARAMETER, filter);
		ft.configure(params);
		FeatureCollection target = ft.transform(fc);
	    assertEquals(target.size(),1);
		
	}
	
	@Test
	public void testCQL() {
		
		FeatureCollection features = FeatureCollections.newCollection();
		SimpleFeatureType sourceType = null;
		
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(GenericMathFunctionTest.sourceNamespace);
			ftbuilder.setNamespaceURI(GenericMathFunctionTest.sourceLocalname);
			for (String s : new String[]{
					GenericMathFunctionTest.sourceLocalnamePropertyA, 
					GenericMathFunctionTest.sourceLocalnamePropertyB, 
					GenericMathFunctionTest.sourceLocalnamePropertyC}) {
				ftbuilder.add(s, Double.class);
			}
			sourceType = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
		// build source Feature(s)
		for (int i = 0; i < 1000; i++) {
			Feature source = SimpleFeatureBuilder.build(
					sourceType, new Object[]{i * 1.0, i * 2.0, i / 2.0}, "" + i);
			features.add(source);
		}
		
		try {
			FeatureCollection filtered = features.subCollection(CQL.toFilter("PropertyA < 500"));
			assertTrue(filtered.size() == 500);
			System.out.println("Size of subcollection: " + filtered.size());
		} catch (CQLException e) {
			throw new RuntimeException(e);
		}
		
	}
}
