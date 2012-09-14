/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.oml.helper;

import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.cst.functions.inspire.GeographicalNameFunction;
import eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ParameterValue;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.ComposedProperty;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.Property;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ICell;
import eu.esdihumboldt.hale.io.oml.internal.model.align.IEntity;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IParameter;

/**
 * Translator class for geographical name functions
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class GeographicalNameTranslator implements FunctionTranslator, GeographicalNameFunction {

	/**
	 * @see eu.esdihumboldt.hale.io.oml.helper.FunctionTranslator#getTransformationId()
	 */
	@Override
	public String getTransformationId() {
		return ID;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.oml.helper.FunctionTranslator#getNewParameters(java.util.List,
	 *      eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter,
	 *      eu.esdihumboldt.hale.io.oml.internal.model.align.ICell)
	 */
	@Override
	public List<ParameterValue> getNewParameters(List<ParameterValue> params, CellBean cellBean,
			IOReporter reporter, ICell cell) {

		// create the new parameter list
		List<ParameterValue> newParams = new ArrayList<ParameterValue>();

		IEntity source = cell.getEntity1();
		if (source instanceof ComposedProperty) {
			ComposedProperty cp = (ComposedProperty) source;
			// usually the composed property should only have one element in the
			// collection
			Property coll = cp.getCollection().get(0);
			// that should be a composed property too
			if (coll instanceof ComposedProperty) {
				ComposedProperty comProp = (ComposedProperty) coll;
				// parameters defined by the parameter page
				List<IParameter> pageParams = comProp.getTransformation().getParameters();
				// add each parameter defined by the parameter page
				for (IParameter p : pageParams) {
					newParams.add(new ParameterValue(p.getName(), p.getValue()));
				}
				// the collection of the collection contains the parameters
				// defined for the spellings
				List<Property> props = comProp.getCollection();
				for (Property prop : props) {
					// each property has a list of 3 parameters (text, script,
					// transliterationScheme)
					List<IParameter> spellingParams = prop.getTransformation().getParameters();
					for (IParameter p : spellingParams) {
						newParams.add(new ParameterValue(p.getName(), p.getValue()));
					}
				}
			}

		}

		return newParams;
	}

}
