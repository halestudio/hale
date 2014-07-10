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

package eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.AbstractObjectDefinition;
import de.fhg.igd.eclipse.util.extension.AbstractObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.PainterLayout;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.PainterLayoutFactory;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.PainterProxy;

/**
 * {@link PainterLayout} extension.
 * 
 * @author Simon Templer
 */
public class PainterLayoutExtension extends AbstractExtension<PainterLayout, PainterLayoutFactory> {

	/**
	 * {@link PainterLayout} factory based on an {@link IConfigurationElement}.
	 */
	public static class ConfigurationPainterLayoutFactory extends
			AbstractConfigurationFactory<PainterLayout> implements PainterLayoutFactory {

		/**
		 * Create a {@link PainterLayout} factory based on the given
		 * configuration element.
		 * 
		 * @param conf the configuration element
		 */
		protected ConfigurationPainterLayoutFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		/**
		 * @see ExtensionObjectFactory#dispose(Object)
		 */
		@Override
		public void dispose(PainterLayout instance) {
			// TODO ?
		}

		/**
		 * @see ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		/**
		 * @see ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return conf.getAttribute("name");
		}

		/**
		 * @see AbstractObjectFactory#getIconURL()
		 */
		@Override
		public URL getIconURL() {
			return getIconURL("icon");
		}

		/**
		 * @see PainterLayoutFactory#getPaintersToLayout()
		 */
		@Override
		public List<PainterProxy> getPaintersToLayout() {
			List<PainterProxy> proxies = new ArrayList<PainterProxy>();
			for (IConfigurationElement child : conf.getChildren()) {
				if (child.getName().equals("tileoverlay")) {
					proxies.add(new TileOverlayProxy(child.getAttribute("ref")));
				}
				else {
					// not supported
				}
			}
			return proxies;
		}

		/**
		 * @see AbstractObjectDefinition#getPriority()
		 */
		@Override
		public int getPriority() {
			String order = conf.getAttribute("order");

			if (order != null) {
				try {
					return Integer.parseInt(order);
				} catch (NumberFormatException e) {
					// ignore, use default
				}
			}

			return super.getPriority();
		}

	}

	/**
	 * The extension point ID
	 */
	private static final String ID = "eu.esdihumboldt.hale.ui.views.styledmap.layout";

	/**
	 * Default constructor
	 */
	public PainterLayoutExtension() {
		super(ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected PainterLayoutFactory createFactory(IConfigurationElement conf) throws Exception {
		return new ConfigurationPainterLayoutFactory(conf);
	}

}
