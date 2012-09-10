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
		ITileOverlayService tos = (ITileOverlayService) PlatformUI.getWorkbench().getService(
				ITileOverlayService.class);

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
		ITileOverlayService tos = (ITileOverlayService) PlatformUI.getWorkbench().getService(
				ITileOverlayService.class);
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
		ITileOverlayService tos = (ITileOverlayService) PlatformUI.getWorkbench().getService(
				ITileOverlayService.class);
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
		ITileOverlayService tos = (ITileOverlayService) PlatformUI.getWorkbench().getService(
				ITileOverlayService.class);
		TileOverlayFactory def = tos.getFactory(id);
		if (def != null) {
			return def.getDisplayName();
		}
		return null;
	}

}
