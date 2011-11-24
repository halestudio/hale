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

package eu.esdihumboldt.cst.doc.functions;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.help.IHelpResource;
import org.eclipse.help.ITopic;
import org.eclipse.help.IUAElement;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunctionExtension;

/**
 * Function reference topic for a set of functions
 * @author Simon Templer
 */
public class FunctionsTopic implements ITopic {

	private final AbstractFunctionExtension<?> functions;
	private final String label;
	
	private ITopic[] functionTopics;

	/**
	 * Create the topic for the given function extension
	 * @param functions the function extension
	 * @param label the topic label
	 */
	public FunctionsTopic(AbstractFunctionExtension<?> functions, String label) {
		this.functions = functions;
		this.label = label;
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
		//TODO description for this type of functions?!
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see IHelpResource#getLabel()
	 */
	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * @see ITopic#getSubtopics()
	 */
	@Override
	public ITopic[] getSubtopics() {
		if (functionTopics == null) {
			Collection<ITopic> topics = new ArrayList<ITopic>();
			
			// initialize function topics
			for (AbstractFunction<?> function : functions.getElements()) {
				//TODO topic per category?!
				ITopic functionTopic = new FunctionTopic(function);
				topics.add(functionTopic);
			}
			
			functionTopics = topics.toArray(new ITopic[topics.size()]);
		}
		
		return functionTopics;
	}

}
