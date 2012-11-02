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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.webtransform.war.pages;

import java.io.FileNotFoundException;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import eu.esdihumboldt.hale.common.headless.WorkspaceService;
import eu.esdihumboldt.hale.common.headless.transform.AbstractTransformationJob;
import eu.esdihumboldt.hale.server.webapp.components.JobPanel;
import eu.esdihumboldt.hale.server.webapp.pages.BasePage;
import eu.esdihumboldt.hale.server.webapp.util.PageDescription;

/**
 * Page showing the status of a transformation.
 * 
 * @author Simon Templer
 */
@PageDescription(parent = TransformationsPage.class, title = "Status")
public class StatusPage extends BasePage {

	private static final long serialVersionUID = -2711157942139206024L;

	/**
	 * Name of the parameter specifying the workspace identifier.
	 */
	public static final String PARAMETER_WORKSPACE = "workspace";

	@SpringBean
	private WorkspaceService workspaces;

	/**
	 * Create a status page with the given page parameters.
	 * 
	 * @param parameters the page parameters
	 */
	public StatusPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected void addControls(boolean loggedIn) {
		super.addControls(loggedIn);

		String workspaceId = getPageParameters().get(PARAMETER_WORKSPACE).toOptionalString();
		if (workspaceId == null || workspaceId.isEmpty()) {
			throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND,
					"Workspace ID not specified.");
		}

		try {
			workspaces.getWorkspaceFolder(workspaceId);
		} catch (FileNotFoundException e) {
			throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND,
					"Workspace does not exist.");
		}

		add(new JobPanel("jobs", AbstractTransformationJob.createFamily(workspaceId)));
	}

}
