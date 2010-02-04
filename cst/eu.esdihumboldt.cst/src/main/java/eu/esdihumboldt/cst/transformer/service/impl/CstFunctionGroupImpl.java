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
import java.util.List;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.CstFunction;
import eu.esdihumboldt.cst.transformer.CstFunctionGroup;

/**
 * Implementation of {@link CstFunctionGroup}.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class CstFunctionGroupImpl 
	implements CstFunctionGroup {

	private List<CstFunction> functions = new ArrayList<CstFunction>();
	
	private final Feature source;
	private final FeatureCollection<? extends FeatureType, ? extends Feature> sourceCollection;
	
	/**
	 * Use this constructor for a {@link CstFunctionGroup} that will work from 
	 * a single source Feature (i.e. instance cardinalities 1:1 and 1:n).
	 * @param source the source {@link Feature}.
	 */
	public CstFunctionGroupImpl(Feature source) {
		this.sourceCollection = null;
		this.source = source;
	}
	
	/**
	 * Use this constructor for a {@link CstFunctionGroup} that will work from 
	 * a source FeatureCollection (i.e. instance cardinalities n:1 and n:m).
	 * @param source the source {@link Feature}.
	 */
	public CstFunctionGroupImpl(
			FeatureCollection<? extends FeatureType, ? extends Feature> source) {
		this.sourceCollection = source;
		this.source = null;
	}
	
	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunctionGroup#addCstFunction(eu.esdihumboldt.cst.transformer.CstFunction)
	 */
	public void addCstFunction(CstFunction cstf) {
		this.addCstFunction(cstf);
	}

	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunctionGroup#executeFunctionGroup(org.opengis.feature.Feature)
	 */
	public Feature executeFunctionGroup(Feature target) {
		for (CstFunction cstf : this.functions) {
			target = cstf.transform(this.source, target);
		}
		return target;
	}

}
