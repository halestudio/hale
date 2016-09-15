/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */

package de.fhg.igd.mapviewer.view.arecalculation;

import org.eclipse.jface.action.Action;

/**
 * @author <a href="mailto:andreas.burchert@igd.fraunhofer.de">Andreas
 *         Burchert</a>
 */
public class MeasureAction extends Action {

	/**
	 * @see Action
	 * @param checked current active state
	 */
	public MeasureAction(boolean checked) {
		setChecked(checked);
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		AreaCalc.getInstance().setActive(this.isChecked());
	}
}
