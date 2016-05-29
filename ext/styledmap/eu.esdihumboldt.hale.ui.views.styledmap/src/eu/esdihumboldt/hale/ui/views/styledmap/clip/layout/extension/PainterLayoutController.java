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

package eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension;

import java.util.List;

import org.eclipse.ui.PlatformUI;

import de.fhg.igd.eclipse.util.extension.exclusive.ExclusiveExtension.ExclusiveExtensionListener;
import de.fhg.igd.mapviewer.BasicMapKit;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.Clip;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.PainterLayout;

/**
 * TODO Type description
 * 
 * @author Simon Templer
 */
public class PainterLayoutController {

	private final PainterLayoutService pls;

	private final ExclusiveExtensionListener<PainterLayout, PainterLayoutFactory> layoutListener;

	private final BasicMapKit mapKit;

	/**
	 * Default constructor
	 * 
	 * @param mapKit the map kit
	 */
	public PainterLayoutController(BasicMapKit mapKit) {
		super();

		this.mapKit = mapKit;

		pls = PlatformUI.getWorkbench().getService(PainterLayoutService.class);

		layoutListener = new ExclusiveExtensionListener<PainterLayout, PainterLayoutFactory>() {

			@Override
			public void currentObjectChanged(PainterLayout current,
					PainterLayoutFactory definition) {
				applyLayout(definition, current);
			}
		};
	}

	/**
	 * Enable the painter layout controller.
	 */
	public void enable() {
		// apply current layout
		applyLayout(pls.getCurrentDefinition(), pls.getCurrent());

		// add listeners
		pls.addListener(layoutListener);
	}

	/**
	 * Disable the painter layout controller.
	 */
	public void disable() {
		// remove listeners
		pls.removeListener(layoutListener);
	}

	/**
	 * Apply the current layout.
	 * 
	 * @param currentDefinition the current definition
	 * @param current the current painter layout
	 */
	private void applyLayout(PainterLayoutFactory currentDefinition, PainterLayout current) {
		List<PainterProxy> painters = currentDefinition.getPaintersToLayout();
		List<Clip> clips = current.createClips(painters.size());

		// apply clips
		for (int i = 0; i < painters.size(); i++) {
			PainterProxy painter = painters.get(i);

			if (i >= clips.size()) {
				// no clip for proxy specified, disable the painter
				// XXX instead do nothing?
				painter.disable();
			}
			else {
				Clip clip = clips.get(i);
				painter.enable();
				painter.setClip(clip); // XXX is this working even if the
										// painter is not enabled (in time)?
			}
		}

		mapKit.refresh();
	}

}
