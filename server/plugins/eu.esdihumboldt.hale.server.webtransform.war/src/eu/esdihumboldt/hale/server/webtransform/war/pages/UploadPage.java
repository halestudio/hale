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

import java.util.Collection;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import eu.esdihumboldt.hale.common.headless.EnvironmentService;
import eu.esdihumboldt.hale.common.headless.TransformationEnvironment;
import eu.esdihumboldt.hale.server.webapp.pages.BasePage;
import eu.esdihumboldt.hale.server.webapp.util.PageDescription;

/**
 * Page for uploading data for transformation.
 * 
 * @author Simon Templer
 */
@PageDescription(parent = TransformationsPage.class, title = "Upload")
public class UploadPage extends BasePage {

	private static final long serialVersionUID = -2188490515045737872L;

	/**
	 * Name of the parameter specifying the project identifier.
	 */
	public static final String PARAMETER_PROJECT = "project";

	@SpringBean
	private EnvironmentService environmentService;

	/**
	 * Default constructor.
	 */
	public UploadPage() {
		Collection<TransformationEnvironment> envs = environmentService.getEnvironments();
		if (envs.size() == 1) {
			setResponsePage(UploadPage.class,
					new PageParameters().add(PARAMETER_PROJECT, envs.iterator().next().getId()));
		}
		else {
			setResponsePage(TransformationsPage.class);
		}
	}

	/**
	 * Create an upload page with the given page parameters.
	 * 
	 * @param parameters the page parameters
	 */
	public UploadPage(PageParameters parameters) {
		super(parameters);
	}

	/**
	 * @see BasePage#addControls(boolean)
	 */
	@Override
	protected void addControls(boolean loggedIn) {
		super.addControls(loggedIn);

		String projectId = getPageParameters().get(PARAMETER_PROJECT).toOptionalString();
		if (projectId != null) {
			if (environmentService.getEnvironment(projectId) == null) {
				throw new IllegalStateException("Project with identifer " + projectId
						+ " not available.");
			}

			add(new UploadAndTransForm("action", projectId));
		}
		else {
			throw new IllegalStateException("No transformation project specified.");
		}
	}

}
