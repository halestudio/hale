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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.help.IHelpResource;
import org.eclipse.help.ITopic;
import org.eclipse.help.IUAElement;

import eu.esdihumboldt.cst.doc.functions.FunctionReferenceConstants;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunctionExtension;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;

/**
 * Function reference topic for a set of functions
 * 
 * @author Simon Templer
 */
public class FunctionsTopic implements ITopic, FunctionReferenceConstants {

	private final AbstractFunctionExtension<?> functions;
	private final String label;
	private final String href;

	private ITopic[] functionTopics;

	/**
	 * Create the topic for the given function extension
	 * 
	 * @param functions the function extension
	 * @param label the topic label
	 * @param href the reference to the topic content, may be <code>null</code>
	 */
	public FunctionsTopic(AbstractFunctionExtension<?> functions, String label, String href) {
		this.functions = functions;
		this.label = label;
		this.href = href;
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
		return href;
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
			for (FunctionDefinition<?> function : functions.getElements()) {
				// TODO topic per category?!
				ITopic functionTopic = new FunctionTopic(function);
				topics.add(functionTopic);
			}

			if (topics.isEmpty()) {
				functionTopics = NO_TOPICS;
			}
			else {
				functionTopics = topics.toArray(new ITopic[topics.size()]);
			}
		}

		return functionTopics;
	}

}
