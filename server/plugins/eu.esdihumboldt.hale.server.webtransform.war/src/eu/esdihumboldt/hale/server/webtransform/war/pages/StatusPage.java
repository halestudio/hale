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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.eclipse.core.runtime.jobs.Job;
import org.joda.time.format.DateTimeFormat;

import eu.esdihumboldt.hale.common.headless.WorkspaceService;
import eu.esdihumboldt.hale.common.headless.transform.AbstractTransformationJob;
import eu.esdihumboldt.hale.common.headless.transform.TransformationWorkspace;
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

		final String workspaceId = getPageParameters().get(PARAMETER_WORKSPACE).toOptionalString();
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

		final IModel<TransformationWorkspace> workspace = new LoadableDetachableModel<TransformationWorkspace>() {

			private static final long serialVersionUID = 2600444242247550094L;

			@Override
			protected TransformationWorkspace load() {
				return new TransformationWorkspace(workspaceId);
			}
		};

		// job panel
		final Serializable family = AbstractTransformationJob.createFamily(workspaceId);
		final JobPanel jobs = new JobPanel("jobs", family, true);
		add(jobs);

		// status
		final Label status = new Label("status", new LoadableDetachableModel<String>() {

			private static final long serialVersionUID = -4351763182104835300L;

			@Override
			protected String load() {
				if (workspace.getObject().isTransformationFinished()) {
					if (workspace.getObject().isTransformationSuccessful()) {
						return "Transformation completed.";
					}
					else {
						return "Transformation failed.";
					}
				}
				else {
					if (Job.getJobManager().find(family).length > 0) {
						return "Transformation is running:";
					}
					else {
						return "No transformation running.";
					}
				}
			}
		});
		status.setOutputMarkupId(true);
		add(status);

		// result
		final WebMarkupContainer result = new WebMarkupContainer("result");
		result.setOutputMarkupId(true);
		add(result);

		final WebMarkupContainer update = new WebMarkupContainer("update") {

			private static final long serialVersionUID = -2591480922683644827L;

			@Override
			public boolean isVisible() {
				return workspace.getObject().isTransformationFinished();
			}

		};
		result.add(update);

		// result : report
		File reportFile = workspace.getObject().getReportFile();
		DownloadLink report = new DownloadLink("log", reportFile, reportFile.getName());
		update.add(report);

		// result : file list
		IModel<? extends List<File>> resultFilesModel = new LoadableDetachableModel<List<File>>() {

			private static final long serialVersionUID = -7971872898614031331L;

			@Override
			protected List<File> load() {
				return Arrays.asList(workspace.getObject().getTargetFolder().listFiles());
			}

		};
		final ListView<File> fileList = new ListView<File>("file", resultFilesModel) {

			private static final long serialVersionUID = -8045643864251639540L;

			@Override
			protected void populateItem(ListItem<File> item) {
				// download link
				DownloadLink download = new DownloadLink("download", item.getModelObject(), item
						.getModelObject().getName());
				item.add(download);

				// file name
				download.add(new Label("name", item.getModelObject().getName()));
			}

		};
		update.add(fileList);

		// leaseEnd
		String leaseEnd;
		try {
			leaseEnd = workspaces.getLeaseEnd(workspaceId)
					.toString(DateTimeFormat.mediumDateTime());
		} catch (IOException e) {
			leaseEnd = "unknown";
		}
		add(new Label("leaseEnd", leaseEnd));

		// timer
		add(new AbstractAjaxTimerBehavior(Duration.milliseconds(1500)) {

			private static final long serialVersionUID = -3726349470723024150L;

			@Override
			protected void onTimer(AjaxRequestTarget target) {
				if (workspace.getObject().isTransformationFinished()) {
					// update status and result
					target.add(status);
					target.add(result);

					// stop timers
					stop(target);
					jobs.getTimer().stopOnNextUpdate();
				}
			}
		});
	}

}
