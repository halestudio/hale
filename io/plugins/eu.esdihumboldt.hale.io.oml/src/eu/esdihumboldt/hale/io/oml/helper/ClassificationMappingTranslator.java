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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.omwg.Restriction;
import eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingFunction;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ext.IValueExpression;

/**
 * Translator class to convert old classification mappings to the new
 * classification mapping
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class ClassificationMappingTranslator implements FunctionTranslator,
		ClassificationMappingFunction {

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
	 *      eu.esdihumboldt.specification.cst.align.ICell)
	 */
	@Override
	public List<ParameterValue> getNewParameters(List<ParameterValue> params, CellBean cellBean,
			IOReporter reporter, ICell cell) {

		List<ParameterValue> newList = new ArrayList<ParameterValue>();

		List<Restriction> sourceRest = new ArrayList<Restriction>();
		List<Restriction> tarRest = new ArrayList<Restriction>();

		sourceRest = ((Property) cell.getEntity1()).getValueCondition();
		tarRest = ((Property) cell.getEntity2()).getValueCondition();

		for (int i = 0; i < sourceRest.size(); i++) {

			Restriction r1 = sourceRest.get(i);
			Restriction r2 = tarRest.get(i);

			Joiner joiner = Joiner.on(" ").skipNulls();

			List<String> encodedParams = new ArrayList<String>();

			// first encode and add the associated value to the string list ...
			for (IValueExpression ival : r2.getValue()) {
				String temp = null;
				try {
					temp = URLEncoder.encode(ival.getLiteral(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					reporter.error(new IOMessageImpl("Parameter could not be encoded.", null));
					e.printStackTrace();
				}
				encodedParams.add(temp);
			}

			// then encode and add the value expressions to the string list
			for (IValueExpression ival : r1.getValue()) {
				String temp = null;
				try {
					temp = URLEncoder.encode(ival.getLiteral(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					reporter.error(new IOMessageImpl("Parameter could not be encoded.", null));
					e.printStackTrace();
				}
				encodedParams.add(temp);
			}

			String encodedParameter = joiner.join(encodedParams);

			newList.add(new ParameterValue(PARAMETER_CLASSIFICATIONS, encodedParameter));

		}

		return newList;
	}

}
