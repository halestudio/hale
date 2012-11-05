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

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.wicketstuff.html5.markup.html.Progress;

/**
 * Progress tag based on a {@link eu.esdihumboldt.hale.server.progress.Progress}
 * information object.
 * 
 * @author Simon Templer
 */
public class JobProgress extends Progress {

	private static final long serialVersionUID = 6656206928952111540L;

	private final IModel<eu.esdihumboldt.hale.server.progress.Progress> progress;

	/**
	 * Create a progress tag based on the given model.
	 * 
	 * @param id the component ID
	 * @param progress the progress model
	 */
	public JobProgress(String id,
			final IModel<eu.esdihumboldt.hale.server.progress.Progress> progress) {
		super(id, new LoadableDetachableModel<Integer>() {

			private static final long serialVersionUID = -3776978863287041995L;

			@Override
			protected Integer load() {
				return progress.getObject().getWorked();
			}

		}, new LoadableDetachableModel<Integer>() {

			private static final long serialVersionUID = -1034357292783054041L;

			@Override
			protected Integer load() {
				return progress.getObject().getTotalWork();
			}

		});
		this.progress = progress;
	}

	/**
	 * @see org.wicketstuff.html5.markup.html.Progress#isDeterminate()
	 */
	@Override
	public boolean isDeterminate() {
		eu.esdihumboldt.hale.server.progress.Progress p = progress.getObject();
		return p != null && !p.isIndeterminate() && p.getTotalWork() > 0;
	}

}
