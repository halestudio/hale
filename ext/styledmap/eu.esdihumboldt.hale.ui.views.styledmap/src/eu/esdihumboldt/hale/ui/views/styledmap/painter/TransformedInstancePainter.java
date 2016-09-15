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

package eu.esdihumboldt.hale.ui.views.styledmap.painter;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;

/**
 * Painter for transformed instances.
 * 
 * @author Simon Templer
 */
public class TransformedInstancePainter extends AbstractInstancePainter {

	/**
	 * Default constructor
	 */
	public TransformedInstancePainter() {
		super(PlatformUI.getWorkbench().getService(InstanceService.class), DataSet.TRANSFORMED);
	}

}
