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
package de.fhg.igd.mapviewer.server.wms.overlay;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.jdesktop.swingx.mapviewer.TileOverlayPainter;

import de.fhg.igd.eclipse.util.extension.AbstractObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.mapviewer.AbstractTileOverlayPainter;
import de.fhg.igd.mapviewer.server.wms.Messages;
import de.fhg.igd.mapviewer.server.wms.WMSConfiguration;
import de.fhg.igd.mapviewer.server.wms.wizard.WMSConfigurationWizard;
import de.fhg.igd.mapviewer.view.overlay.TileOverlayFactory;
import de.fhg.igd.mapviewer.view.overlay.TileOverlayFactoryCollection;

/**
 * Collection of {@link WMSTileOverlay} factories
 * 
 * @author Simon Templer
 */
public class WMSTileOverlayCollection implements TileOverlayFactoryCollection {

	/**
	 * Factory for {@link WMSTileOverlay}s
	 */
	public class OverlayFactory extends AbstractObjectFactory<TileOverlayPainter>
			implements TileOverlayFactory {

		private final String name;

		/**
		 * Constructor
		 * 
		 * @param name the WMS overlay name
		 */
		public OverlayFactory(String name) {
			super();

			this.name = name;
		}

		/**
		 * @see ExtensionObjectFactory#createExtensionObject()
		 */
		@Override
		public TileOverlayPainter createExtensionObject() throws Exception {
			WMSTileOverlay result = new WMSTileOverlay(name);
			result.setPriority(priority);
			return result;
		}

		/**
		 * @see ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return name;
		}

		/**
		 * @see ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return getTypeName() + ":" + getDisplayName(); //$NON-NLS-1$
		}

		/**
		 * @see ExtensionObjectDefinition#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return WMSTileOverlay.class.getName();
		}

		/**
		 * @see AbstractObjectFactory#allowConfigure()
		 */
		@Override
		public boolean allowConfigure() {
			return true;
		}

		/**
		 * @see AbstractObjectFactory#configure()
		 */
		@Override
		public boolean configure() {
			try {
				WMSTileOverlay server = (WMSTileOverlay) createExtensionObject();
				return WMSTileOverlayCollection.this.configure(server, true);
			} catch (Exception e) {
				return false;
			}
		}

		/**
		 * @see ExtensionObjectFactory#dispose(Object)
		 */
		@Override
		public void dispose(TileOverlayPainter instance) {
			instance.dispose();
		}

		/**
		 * @see TileOverlayFactory#showInMiniMap()
		 */
		@Override
		public boolean showInMiniMap() {
			return true;
		}

	}

	private static final Log log = LogFactory.getLog(WMSTileOverlayCollection.class);

	private int priority = AbstractTileOverlayPainter.DEF_PRIORITY;

	/**
	 * @see ExtensionObjectFactoryCollection#addNew()
	 */
	@Override
	public TileOverlayFactory addNew() {
		WMSTileOverlay overlay = new WMSTileOverlay();

		if (configure(overlay, false)) {
			return new OverlayFactory(overlay.getConfiguration().getName());
		}
		else {
			return null;
		}
	}

	/**
	 * Configure the given overlay
	 * 
	 * @param overlay the overlay
	 * @param overwrite if the configuration may be overridden
	 * @return if the configuration was saved
	 */
	private boolean configure(WMSTileOverlay overlay, boolean overwrite) {
		WMSConfiguration configuration = overlay.getConfiguration();

		try {
			final Display display = PlatformUI.getWorkbench().getDisplay();

			WMSConfigurationWizard<WMSConfiguration> wizard = new WMSConfigurationWizard<WMSConfiguration>(
					configuration, true, false);
			WizardDialog dialog = new WizardDialog(display.getActiveShell(), wizard);
			if (dialog.open() == WizardDialog.OK) {
				configuration.save(overwrite);
				return true;
			}
			else {
				return false;
			}
		} catch (Exception e) {
			log.error("Error creating WMS overlay", e); //$NON-NLS-1$
		}

		return false;
	}

	/**
	 * @see ExtensionObjectFactoryCollection#allowAddNew()
	 */
	@Override
	public boolean allowAddNew() {
		return true;
	}

	/**
	 * @see ExtensionObjectFactoryCollection#allowRemove()
	 */
	@Override
	public boolean allowRemove() {
		return true;
	}

	/**
	 * @see ExtensionObjectFactoryCollection#getFactories()
	 */
	@Override
	public List<TileOverlayFactory> getFactories() {
		List<TileOverlayFactory> results = new LinkedList<TileOverlayFactory>();

		for (String name : WMSTileOverlay.getConfigurationNames()) {
			results.add(new OverlayFactory(name));
		}

		return results;
	}

	/**
	 * @see ExtensionObjectFactoryCollection#getName()
	 */
	@Override
	public String getName() {
		return Messages.WMSTileOverlayCollection_1;
	}

	/**
	 * @see ExtensionObjectFactoryCollection#remove(ExtensionObjectFactory)
	 */
	@Override
	public boolean remove(TileOverlayFactory factory) {
		return WMSTileOverlay.removeConfiguration(factory.getDisplayName());
	}

	/**
	 * @see TileOverlayFactoryCollection#setPriority(int)
	 */
	@Override
	public void setPriority(int priority) {
		this.priority = priority;
	}

}
