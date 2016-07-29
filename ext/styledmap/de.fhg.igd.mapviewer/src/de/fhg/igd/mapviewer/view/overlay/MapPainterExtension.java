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

import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.mapviewer.MapPainter;

/**
 * {@link MapPainter} extension
 * 
 * @author Simon Templer
 */
public class MapPainterExtension extends AbstractExtension<MapPainter, MapPainterFactory> {

	/**
	 * Default constructor
	 */
	public MapPainterExtension() {
		super(MapPainter.class.getName());
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected MapPainterFactory createFactory(IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("painter")) { //$NON-NLS-1$
			return new MapPainterFactory(conf);
		}

		return null;
	}

}
