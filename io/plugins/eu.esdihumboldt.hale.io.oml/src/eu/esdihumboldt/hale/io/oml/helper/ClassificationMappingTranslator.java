/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.oml.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingFunction;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.Property;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.Restriction;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ICell;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IValueExpression;

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
	 *      eu.esdihumboldt.hale.io.oml.internal.model.align.ICell)
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
