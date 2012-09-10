/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.io.instance.crs;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;

/**
 * Provides default CRS definitions by offering a dialog to the user.
 * 
 * @author Simon Templer
 */
public class DialogCRSProvider implements CRSProvider {

	private CRSDefinition crsDef;

	private boolean shown = false;

	/**
	 * @see CRSProvider#getCRS(PropertyDefinition)
	 */
	@Override
	public CRSDefinition getCRS(PropertyDefinition property) {
		// TODO extend dialog to allow selecting a CRS per property definition
		// XXX for now always reports the same CRS definition
		if (crsDef == null && !shown) {
			shown = true;

			Display display = PlatformUI.getWorkbench().getDisplay();
			final AtomicReference<CRSDefinition> result = new AtomicReference<CRSDefinition>();
			display.syncExec(new Runnable() {

				@Override
				public void run() {
					SelectCRSDialog dialog = new SelectCRSDialog(Display.getCurrent()
							.getActiveShell(), null);

					dialog.open();
					result.set(dialog.getValue());
				}
			});
			crsDef = result.get();
		}

		return crsDef;
	}

}
