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

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.help.IHelpResource;
import org.eclipse.help.ITopic;
import org.eclipse.help.IUAElement;

import eu.esdihumboldt.hale.doc.user.examples.internal.ExamplesConstants;
import eu.esdihumboldt.hale.doc.user.examples.internal.extension.ExampleProject;

/**
 * Topic representing the mapping documentation of a project.
 * 
 * @author Simon Templer
 */
public class MappingDocumentationTopic implements ITopic, ExamplesConstants {

	private final ExampleProject project;

	/**
	 * Create the project topic.
	 * 
	 * @param project the associated project
	 */
	public MappingDocumentationTopic(ExampleProject project) {
		super();
		this.project = project;
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
		return "/" + PLUGIN_ID + "/" + PATH_PREFIX_PROJECT + project.getId()
				+ PATH_SUFFIX_MAPPINGDOC + ".html";
	}

	/**
	 * @see IHelpResource#getLabel()
	 */
	@Override
	public String getLabel() {
		return "Mapping Documentation";
	}

	/**
	 * @see ITopic#getSubtopics()
	 */
	@Override
	public ITopic[] getSubtopics() {
		return NO_TOPICS;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((project == null) ? 0 : project.getId().hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MappingDocumentationTopic other = (MappingDocumentationTopic) obj;
		if (project == null) {
			if (other.project != null)
				return false;
		}
		else if (!project.getId().equals(other.project.getId()))
			return false;
		return true;
	}

}
