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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.util.time.Duration;

/**
 * Ajax timer beahvior that can be marked to stop the time when it is next
 * updated.
 * 
 * @author Simon Templer
 */
public class StoppableAjaxSelfUpdatingTimer extends AjaxSelfUpdatingTimerBehavior {

	private static final long serialVersionUID = 748548472586335843L;

	private boolean stopOnNextUpdate = false;

	/**
	 * @see AjaxSelfUpdatingTimerBehavior#AjaxSelfUpdatingTimerBehavior(Duration)
	 */
	public StoppableAjaxSelfUpdatingTimer(Duration updateInterval) {
		super(updateInterval);
	}

	/**
	 * @see AjaxSelfUpdatingTimerBehavior#onPostProcessTarget(AjaxRequestTarget)
	 */
	@Override
	protected void onPostProcessTarget(AjaxRequestTarget target) {
		super.onPostProcessTarget(target);

		if (!isStopped() && stopOnNextUpdate) {
			stop(target);
			stopOnNextUpdate = false;
		}
	}

	/**
	 * Set the timer to stop when it is next updated.
	 */
	public void stopOnNextUpdate() {
		this.stopOnNextUpdate = true;
	}

}
