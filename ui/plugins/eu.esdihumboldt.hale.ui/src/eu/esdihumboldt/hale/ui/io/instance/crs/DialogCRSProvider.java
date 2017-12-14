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

package eu.esdihumboldt.hale.ui.io.instance.crs;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.xml.namespace.QName;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Provides default CRS definitions by offering a dialog to the user.
 * 
 * @author Simon Templer
 */
public class DialogCRSProvider implements CRSProvider {

	private CRSDefinition crsDef;

	private boolean shown = false;

	/**
	 * @see CRSProvider#getCRS(TypeDefinition, List)
	 */
	@Override
	public CRSDefinition getCRS(TypeDefinition parentType, List<QName> propertyPath) {
		return getCRS(parentType, propertyPath, null);
	}

	/**
	 * @see CRSProvider#getCRS(TypeDefinition, List, CRSDefinition)
	 */
	@Override
	public CRSDefinition getCRS(TypeDefinition parentType, List<QName> propertyPath,
			final CRSDefinition defaultCrs) {
		// TODO extend dialog to allow selecting a CRS per property definition
		// XXX for now always reports the same CRS definition
		if (crsDef == null && !shown) {
			shown = true;

			Display display = PlatformUI.getWorkbench().getDisplay();
			final AtomicReference<CRSDefinition> result = new AtomicReference<CRSDefinition>();
			display.syncExec(new Runnable() {

				@Override
				public void run() {
					SelectCRSDialog dialog = new SelectCRSDialog(
							Display.getCurrent().getActiveShell(), defaultCrs);

					dialog.open();
					result.set(dialog.getValue());
				}
			});
			crsDef = result.get();
		}

		return crsDef;
	}

}
