/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.ui;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.io.xslt.transformations.property.GenericXPath;
import eu.esdihumboldt.hale.io.xslt.ui.internal.XsltUI;
import eu.esdihumboldt.hale.ui.functions.core.TextSourceListParameterPage;

/**
 * Parameter page for specifying the XPath expression for the
 * {@link GenericXPath} XSL transformation function.
 * 
 * @author Simon Templer
 */
public class XPathParameterPage extends TextSourceListParameterPage {

	/**
	 * Default constructor.
	 */
	public XPathParameterPage() {
		super("XPath");

		setTitle("XPath expression");
		setDescription("Please specify the XPath expression computing the target value.");

		setImageDescriptor(XsltUI.imageDescriptorFromPlugin("eu.esdihumboldt.hale.io.xslt",
				"icons/xsl.png"));
	}

	@Override
	protected String getParameterName() {
		return GenericXPath.PARAM_XPATH;
	}

	@Override
	protected String getSourcePropertyName() {
		return GenericXPath.ENTITY_VAR;
	}

	@Override
	protected Map<EntityDefinition, String> determineDefaultVariableNames(
			List<EntityDefinition> variables) {
		Map<EntityDefinition, String> result = new LinkedHashMap<EntityDefinition, String>();

		// variable names depend on order for GenericXPath
		int number = 1;
		for (EntityDefinition var : variables) {
			result.put(var, "$" + GenericXPath.PREFIX_VAR + number++);
		}

		return result;
	}

}
