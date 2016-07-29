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
package de.fhg.igd.mapviewer.view.overlay;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import de.fhg.igd.mapviewer.MapPainter;

/**
 * Factory for a {@link MapPainter}
 * 
 * @author Simon Templer
 */
public class MapPainterFactory extends AbstractConfigurationFactory<MapPainter> {

	/**
	 * Constructor
	 * 
	 * @param conf the configuration element
	 */
	protected MapPainterFactory(IConfigurationElement conf) {
		super(conf, "class"); //$NON-NLS-1$
	}

	/**
	 * @see ExtensionObjectDefinition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return conf.getAttribute("name"); //$NON-NLS-1$
	}

	/**
	 * @see ExtensionObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return getTypeName();
	}

	/**
	 * @see ExtensionObjectFactory#dispose(Object)
	 */
	@Override
	public void dispose(MapPainter instance) {
		instance.dispose();
	}

}
