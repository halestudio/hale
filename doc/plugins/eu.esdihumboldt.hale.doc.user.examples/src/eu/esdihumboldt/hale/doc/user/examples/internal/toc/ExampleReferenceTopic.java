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

package eu.esdihumboldt.hale.doc.user.examples.internal.toc;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.help.IHelpResource;
import org.eclipse.help.ITopic;
import org.eclipse.help.IUAElement;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import de.fhg.igd.slf4jplus.ATransaction;

import eu.esdihumboldt.hale.doc.user.examples.internal.ExamplesConstants;
import eu.esdihumboldt.hale.doc.user.examples.internal.extension.ExampleProject;
import eu.esdihumboldt.hale.doc.user.examples.internal.extension.ExampleProjectExtension;

/**
 * Table of contents for the function reference
 */
public class ExampleReferenceTopic implements ITopic, ExamplesConstants {

	private static final ALogger log = ALoggerFactory.getLogger(ExampleReferenceTopic.class);

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
			ATransaction trans = log.begin("Initializing example project topics");
			try {
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
			} finally {
				trans.end();
			}
		}

		return projectTopics;
	}

}
