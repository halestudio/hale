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

package eu.esdihumboldt.hale.doc.user.examples.internal.toc;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.help.IHelpResource;
import org.eclipse.help.ITopic;
import org.eclipse.help.IUAElement;

import eu.esdihumboldt.hale.doc.user.examples.internal.ExamplesConstants;
import eu.esdihumboldt.hale.doc.user.examples.internal.extension.ExampleProject;
import eu.esdihumboldt.hale.doc.user.examples.internal.extension.ExampleProjectExtension;

/**
 * Table of contents for the function reference
 */
public class ExampleReferenceTopic implements ITopic, ExamplesConstants {
	
	private ITopic[] projectTopics;
	
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
		return "/" + PLUGIN_ID + "/" + PATH_OVERVIEW;
	}

	/**
	 * @see IHelpResource#getLabel()
	 */
	@Override
	public String getLabel() {
		return "Examples";
	}

	/**
	 * @see ITopic#getSubtopics()
	 */
	@Override
	public ITopic[] getSubtopics() {
		if (projectTopics == null) {
			Collection<ITopic> topics = new ArrayList<ITopic>();
			
			// initialize function topics
			for (ExampleProject project : ExampleProjectExtension.getInstance().getElements()) {
				ITopic projectTopic = new ProjectTopic(project);
				topics.add(projectTopic);
			}
			
			if (topics.isEmpty()) {
				projectTopics = NO_TOPICS;
			}
			else {
				projectTopics = topics.toArray(new ITopic[topics.size()]);
			}
		}
		
		return projectTopics;
	}

}