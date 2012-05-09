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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Joiner;

import eu.esdihumboldt.commons.goml.rdf.DetailedAbout;
import eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ChildContextBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.NamedEntityBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ParameterValue;
import eu.esdihumboldt.hale.common.align.io.impl.internal.PropertyBean;
import eu.esdihumboldt.hale.common.align.model.functions.FormattedStringFunction;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.specification.cst.align.ICell;

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
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter,
	 *      eu.esdihumboldt.specification.cst.align.ICell)
	 */
	@Override
	public List<ParameterValue> getNewParameters(List<ParameterValue> params,
			CellBean cellBean, IOReporter reporter, ICell cell) {

		List<ParameterValue> newList = new ArrayList<ParameterValue>();

		String separator = ""; // default separator
		String concatenation = "";

		for (ParameterValue val : params) {
			// get original separator parameter 
			if (val.getName().equals(SEPARATOR)) {
				separator = val.getValue();
			}

			// get original concatenation parameter
			if (val.getName().equals(CONCATENATION)) {
				concatenation = val.getValue();
			}
		}

		String[] concat = concatenation.split(INTERNALSEPERATOR);
		List<NamedEntityBean> src = cellBean.getSource();
		
		// create list of valid source variables
		Set<String> sourceVars = new HashSet<String>();
		for (NamedEntityBean bean : src) {
			if (bean.getEntity() instanceof PropertyBean) {
				List<ChildContextBean> props = ((PropertyBean) bean.getEntity()).getProperties();
				String[] children = new String[props.size()];
				for (int i = 0; i < props.size(); i++) {
					children[i] = props.get(i).getChildName().getLocalPart();
				}
				sourceVars.add(Joiner.on('.').join(children));
			}
		}
		
		StringBuffer pattern = new StringBuffer();
		boolean first = true;
		for (String thisElement : concat) {
			// append separator between elements
			if (first) {
				first = false;
			}
			else {
				pattern.append(separator);
			}
			
			String[] properties = thisElement.split(String
					.valueOf(DetailedAbout.PROPERTY_DELIMITER));
			String varString = Joiner.on('.').join(properties);
			if (sourceVars.contains(varString)) {
				// thisElement represents a variable
				pattern.append('{');
				pattern.append(varString);
				pattern.append('}');
			}
			else {
				// thisElement is just a string
				pattern.append(thisElement); //TODO any escaping of {?
			}
		}
		// add pattern parameter
		newList.add(new ParameterValue(PARAMETER_PATTERN, pattern.toString()));

		return newList;
	}

}
