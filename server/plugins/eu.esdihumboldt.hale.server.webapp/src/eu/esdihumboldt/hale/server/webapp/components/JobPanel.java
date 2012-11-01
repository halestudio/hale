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

package eu.esdihumboldt.hale.server.webapp.components;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.time.Duration;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.wicketstuff.html5.markup.html.Progress;

/**
 * Panel showing job status.
 * 
 * @author Simon Templer
 */
public class JobPanel extends Panel {

	private static final long serialVersionUID = 1567693599697840713L;

	/**
	 * Create a job panel.
	 * 
	 * @param id the component ID
	 * @param jobFamily the job family, may be <code>null</code> if all jobs
	 *            should be shown
	 * 
	 * @see IJobManager#find(Object)
	 */
	public JobPanel(String id, final Serializable jobFamily) {
		super(id);

		setOutputMarkupId(true);

		// update panel
		add(new AjaxSelfUpdatingTimerBehavior(Duration.milliseconds(1500)));

		// job list
		final IModel<? extends List<Job>> jobModel = new LoadableDetachableModel<List<Job>>() {

			private static final long serialVersionUID = 7277175702043541004L;

			@Override
			protected List<Job> load() {
				return Arrays.asList(Job.getJobManager().find(jobFamily));
			}

		};

		final ListView<Job> jobList = new ListView<Job>("jobs", jobModel) {

			private static final long serialVersionUID = -6740090246572869212L;

			/**
			 * @see ListView#populateItem(ListItem)
			 */
			@Override
			protected void populateItem(ListItem<Job> item) {
				final boolean odd = item.getIndex() % 2 == 1;
				if (odd) {
					item.add(AttributeModifier.replace("class", "odd"));
				}

				final Job job = item.getModelObject();

				// status
				String status;
				switch (job.getState()) {
				case Job.WAITING:
					status = "waiting";
					break;
				case Job.SLEEPING:
					status = "sleeping";
					break;
				case Job.RUNNING:
					status = "running";
					break;
				case Job.NONE:
				default:
					status = "unknown";
				}
				item.add(new Label("status", status));

				// name
				item.add(new Label("name", job.getName()));

				// progress
//				ProgressBar progress = new ProgressBar("progress", model);
				Progress progress = new Progress("progress");
				item.add(progress);
			}

		};
		add(jobList);

		add(new WebMarkupContainer("nojobs") {

			private static final long serialVersionUID = -7752350858497246457L;

			@Override
			public boolean isVisible() {
				return jobModel.getObject().isEmpty();
			}

		});
	}

}
