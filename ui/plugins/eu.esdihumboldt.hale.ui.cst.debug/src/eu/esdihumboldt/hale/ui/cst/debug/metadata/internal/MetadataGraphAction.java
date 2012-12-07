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

package eu.esdihumboldt.hale.ui.cst.debug.metadata.internal;

import java.io.IOException;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataAction;

/**
 * Metadata extension point Action for displaying a graph
 * 
 * @author Sebastian Reinhardt
 */
public class MetadataGraphAction implements MetadataAction {

	/**
	 * @throws IOException my throw an exception if the dialog produces an error
	 * @see eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataAction#execute(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void execute(Object key, Object value) throws IOException {

		GraphMLDialog dialog = new GraphMLDialog(PlatformUI.getWorkbench().getDisplay()
				.getActiveShell(), value.toString());

		dialog.open();

	}
}
