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

import eu.esdihumboldt.cst.doc.functions.FunctionReferenceConstants;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;

/**
 * Topic representing a function.
 * @author Simon Templer
 */
public class FunctionTopic implements ITopic, FunctionReferenceConstants {

	private final AbstractFunction<?> function;

	/**
	 * Create the function topic.
	 * @param function the associated function
	 */
	public FunctionTopic(AbstractFunction<?> function) {
		super();
		this.function = function;
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
		return FUNCTION_TOPIC_PATH + function.getId();
	}

	/**
	 * @see IHelpResource#getLabel()
	 */
	@Override
	public String getLabel() {
		return function.getDisplayName();
	}

	/**
	 * @see ITopic#getSubtopics()
	 */
	@Override
	public ITopic[] getSubtopics() {
		return NO_TOPICS;
	}

}
