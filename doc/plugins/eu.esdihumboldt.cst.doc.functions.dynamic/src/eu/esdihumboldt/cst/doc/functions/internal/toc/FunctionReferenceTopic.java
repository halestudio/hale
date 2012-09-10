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