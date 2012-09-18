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

package eu.esdihumboldt.cst.doc.functions.internal.toc;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.help.IHelpResource;
import org.eclipse.help.ITopic;
import org.eclipse.help.IUAElement;

import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionExtension;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionExtension;

/**
 * Table of contents for the function reference
 */
public class FunctionReferenceTopic implements ITopic {

	private final ITopic typeFunctions;
	private final ITopic propertyFunctions;

	/**
	 * Default constructor
	 */
	public FunctionReferenceTopic() {
		super();
		// create topics
		typeFunctions = new FunctionsTopic(TypeFunctionExtension.getInstance(), "Type relations",
				null); // "html/type-functions.html");
		propertyFunctions = new FunctionsTopic(PropertyFunctionExtension.getInstance(),
				"Property relations", null); // "html/property-functions.html");
	}

	/**
	 * @see IUAElement#isEnabled(IEvaluationContext)
	 */
	@Override
	public boolean isEnabled(IEvaluationContext context) {
		return true;
	}

	/**
	 * @see IUAElement#getChildren()
	 */
	@Override
	public IUAElement[] getChildren() {
		return getSubtopics();
	}

	/**
	 * @see IHelpResource#getHref()
	 */
	@Override
	public String getHref() {
		// TODO return HREF to main function reference description?
		// return "html/functions.html";
		return null;
	}

	/**
	 * @see IHelpResource#getLabel()
	 */
	@Override
	public String getLabel() {
		return "Functions";
	}

	/**
	 * @see ITopic#getSubtopics()
	 */
	@Override
	public ITopic[] getSubtopics() {
		return new ITopic[] { typeFunctions, propertyFunctions };
	}

}
