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
package de.fhg.igd.mapviewer.server.wms;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import de.fhg.igd.eclipse.util.extension.AbstractObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.mapviewer.server.MapServer;
import de.fhg.igd.mapviewer.server.MapServerFactory;
import de.fhg.igd.mapviewer.server.MapServerFactoryCollection;
import de.fhg.igd.mapviewer.server.wms.wizard.WMSConfigurationWizard;
import de.fhg.igd.mapviewer.server.wms.wizard.WMSTileConfigurationWizard;

/**
 * WMSMapServerFactory
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class WMSMapServerFactory implements MapServerFactoryCollection {

	/**
	 * WMS server factory
	 */
	public class WMSFactory extends AbstractObjectFactory<MapServer>implements MapServerFactory {

		private final String name;

		/**
		 * Constructor
		 * 
		 * @param name the WMS map name
		 */
		public WMSFactory(String name) {
			super();

			this.name = name;
		}

		/**
		 * @see ExtensionObjectFactory#createExtensionObject()
		 */
		@Override
		public MapServer createExtensionObject() throws Exception {
			WMSMapServer server = new WMSMapServer();
			if (server.load(name)) {
				return server;
			}
			else {
				throw new IllegalArgumentException("Loading configuration " + name + " failed"); //$NON-NLS-1$ //$NON-NLS-2$
			}
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
			return WMSMapServerFactory.class.getName();
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
				WMSMapServer server = (WMSMapServer) createExtensionObject();
				return WMSMapServerFactory.this.configure(server, true);
			} catch (Exception e) {
				return false;
			}
		}

		/**
		 * @see ExtensionObjectFactory#dispose(java.lang.Object)
		 */
		@Override
		public void dispose(MapServer instance) {
			instance.cleanup();
		}

	}

	private static final Log log = LogFactory.getLog(WMSMapServerFactory.class);

	/**
	 * @see ExtensionObjectFactoryCollection#addNew()
	 */
	@Override
	public MapServerFactory addNew() {
		MapServer server = createNewServer();
		if (server != null) {
			return new WMSFactory(server.getName());
		}
		else {
			return null;
		}
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
	public List<MapServerFactory> getFactories() {
		List<MapServerFactory> results = new LinkedList<MapServerFactory>();

		for (String name : WMSMapServer.getConfigurationNames()) {
			results.add(new WMSFactory(name));
		}

		return results;
	}

	/**
	 * @see ExtensionObjectFactoryCollection#getName()
	 */
	@Override
	public String getName() {
		return Messages.WMSMapServerFactory_3;
	}

	/**
	 * @see ExtensionObjectFactoryCollection#remove(ExtensionObjectFactory)
	 */
	@Override
	public boolean remove(MapServerFactory factory) {
		return WMSMapServer.removeConfiguration(factory.getDisplayName());
	}

	/**
	 * Creates a new WMS map server
	 * 
	 * @return the map server or <code>null</code>
	 */
	private MapServer createNewServer() {
		WMSMapServer server = new WMSMapServer();

		if (configure(server, false)) {
			return server;
		}
		else {
			return null;
		}
	}

	/**
	 * Configure the given WMS map server
	 * 
	 * @param server the WMS map server
	 * @param overwrite if old settings may be overridden
	 * @return if the configuration was saved
	 */
	private boolean configure(WMSMapServer server, boolean overwrite) {
		try {
			final Display display = PlatformUI.getWorkbench().getDisplay();

			WMSConfigurationWizard<WMSTileConfiguration> wizard = new WMSTileConfigurationWizard(
					server, true);
			WizardDialog dialog = new WizardDialog(display.getActiveShell(), wizard);
			if (dialog.open() == WizardDialog.OK) {
				server.save(overwrite);
				return true;
			}
			else
				return false;
		} catch (Exception e) {
			log.error("Error configuring wms map server", e); //$NON-NLS-1$
			return false;
		}
	}

}
