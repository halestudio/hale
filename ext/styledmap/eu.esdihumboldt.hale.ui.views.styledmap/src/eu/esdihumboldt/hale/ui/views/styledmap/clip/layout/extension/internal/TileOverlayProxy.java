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

import org.eclipse.ui.PlatformUI;
import org.jdesktop.swingx.mapviewer.TileOverlayPainter;

import de.fhg.igd.mapviewer.view.overlay.ITileOverlayService;
import de.fhg.igd.mapviewer.view.overlay.TileOverlayFactory;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.Clip;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.ClipPainter;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.PainterProxy;

/**
 * Proxy for a tile overlay painter.
 * 
 * @author Simon Templer
 */
public class TileOverlayProxy implements PainterProxy {

	private final String id;

	/**
	 * Create a tile overlay painter proxy.
	 * 
	 * @param id the tile overlay painter ID
	 */
	public TileOverlayProxy(String id) {
		this.id = id;
	}

	/**
	 * @see ClipPainter#setClip(Clip)
	 */
	@Override
	public void setClip(Clip clip) {
		ITileOverlayService tos = PlatformUI.getWorkbench().getService(ITileOverlayService.class);

		for (TileOverlayPainter painter : tos.getActiveObjects()) {
			TileOverlayFactory def = tos.getDefinition(painter);
			if (def.getIdentifier().equals(id)) {
				if (painter instanceof ClipPainter) {
					((ClipPainter) painter).setClip(clip);
				}
				else {
					// TODO warning
				}

				break;
			}
		}
	}

	/**
	 * @see PainterProxy#enable()
	 */
	@Override
	public void enable() {
		ITileOverlayService tos = PlatformUI.getWorkbench().getService(ITileOverlayService.class);
		TileOverlayFactory def = tos.getFactory(id);
		if (def != null) {
			tos.activate(def);
		}
	}

	/**
	 * @see PainterProxy#disable()
	 */
	@Override
	public void disable() {
		ITileOverlayService tos = PlatformUI.getWorkbench().getService(ITileOverlayService.class);
		TileOverlayFactory def = tos.getFactory(id);
		if (def != null) {
			tos.deactivate(def);
		}
	}

	/**
	 * @see PainterProxy#getName()
	 */
	@Override
	public String getName() {
		ITileOverlayService tos = PlatformUI.getWorkbench().getService(ITileOverlayService.class);
		TileOverlayFactory def = tos.getFactory(id);
		if (def != null) {
			return def.getDisplayName();
		}
		return null;
	}

}
