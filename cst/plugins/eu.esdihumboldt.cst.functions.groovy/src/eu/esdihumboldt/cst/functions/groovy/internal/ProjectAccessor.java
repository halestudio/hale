/*
 * Copyright (c) 2016 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.groovy.internal;

import java.util.Date;

import javax.annotation.Nullable;

import org.osgi.framework.Version;

import eu.esdihumboldt.hale.common.align.transformation.function.ExecutionContext;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoService;

/**
 * Accessor for project information in Groovy scripts.
 * 
 * @author Simon Templer
 */
public class ProjectAccessor implements ProjectInfo {

	private final ProjectInfoService projectInfo;

	private final GroovyProjectVariables variables;

	/**
	 * Constructor.
	 * 
	 * @param projectInfo the project information service, if available
	 * @param cellLog cell log to report messages to
	 * @param executionContext the transformation execution context
	 */
	public ProjectAccessor(@Nullable ProjectInfoService projectInfo,
			TransformationLogWrapper cellLog, ExecutionContext executionContext) {
		this.projectInfo = projectInfo;
		this.variables = new GroovyProjectVariables(projectInfo, cellLog, executionContext);
	}

	@SuppressWarnings("javadoc")
	public GroovyProjectVariables getVariables() {
		return variables;
	}

	@SuppressWarnings("javadoc")
	public GroovyProjectVariables getVars() {
		return variables;
	}

	@Override
	public String getName() {
		if (projectInfo != null) {
			return projectInfo.getProjectInfo().getName();
		}
		return null;
	}

	@Override
	public String getAuthor() {
		if (projectInfo != null) {
			return projectInfo.getProjectInfo().getAuthor();
		}
		return null;
	}

	@Override
	public String getDescription() {
		if (projectInfo != null) {
			return projectInfo.getProjectInfo().getDescription();
		}
		return null;
	}

	@Override
	public Version getHaleVersion() {
		if (projectInfo != null) {
			return projectInfo.getProjectInfo().getHaleVersion();
		}
		return null;
	}

	@Override
	public Date getCreated() {
		if (projectInfo != null) {
			return projectInfo.getProjectInfo().getCreated();
		}
		return null;
	}

	@Override
	public Date getModified() {
		if (projectInfo != null) {
			return projectInfo.getProjectInfo().getModified();
		}
		return null;
	}

	@Override
	public Value getProperty(String name) {
		if (projectInfo != null) {
			return projectInfo.getProperty(name);
		}
		return Value.NULL;
	}

}
