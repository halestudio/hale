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

import eu.esdihumboldt.commons.goml.rdf.DetailedAbout;
import eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.NamedEntityBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.FormattedStringFunction;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;

/**
 * Class to translate concatenation of attributes to the formatted string
 * function
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class FormattedStringTranslator implements FunctionTranslator,
		FormattedStringFunction {

	private static final String INTERNALSEPERATOR = "--!-split-!--"; //$NON-NLS-1$

	private static final String SEPARATOR = "seperator"; //$NON-NLS-1$

	private static final String CONCATENATION = "concatenation"; //$NON-NLS-1$

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
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	public List<ParameterValue> getNewParameters(List<ParameterValue> params,
			CellBean cellBean, IOReporter reporter) {

		List<ParameterValue> newList = new ArrayList<ParameterValue>();

		String separator = "";
		String concatenation = "";

		for (ParameterValue val : params) {
			if (val.getName().equals(SEPARATOR)) {
				separator = val.getValue();
			}

			if (val.getName().equals(CONCATENATION)) {
				concatenation = val.getValue();
			}
			newList.add(val);
		}

		String[] concat = concatenation.split(INTERNALSEPERATOR);
		String finalConcatString = ""; //$NON-NLS-1$
		List<NamedEntityBean> src = cellBean.getSource();
		for (String thisElement : concat) {
			String[] properties = thisElement.split(String
					.valueOf(DetailedAbout.PROPERTY_DELIMITER));
			for (String str : properties) {

				if (finalConcatString.length() > 0) {
					if (separator.isEmpty()) {
						finalConcatString += " ";
					} else {
						finalConcatString += separator;
					}
				}

				for (NamedEntityBean bean : src) {
					if (str.equals(bean.getName())) {
						finalConcatString += "{" + str + "}";
						break;
					}
				}
				if (finalConcatString.endsWith(separator)) {
					finalConcatString += str;
				}
			}
		}
		newList.add(new ParameterValue(PARAMETER_PATTERN, finalConcatString));

		return newList;
	}

}
