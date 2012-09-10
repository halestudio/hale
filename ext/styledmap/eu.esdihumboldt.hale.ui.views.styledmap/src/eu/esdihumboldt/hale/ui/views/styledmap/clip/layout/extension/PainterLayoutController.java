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

package eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension;

import java.util.List;

import org.eclipse.ui.PlatformUI;

import de.cs3d.util.eclipse.extension.exclusive.ExclusiveExtension.ExclusiveExtensionListener;
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

		pls = (PainterLayoutService) PlatformUI.getWorkbench().getService(
				PainterLayoutService.class);

		layoutListener = new ExclusiveExtensionListener<PainterLayout, PainterLayoutFactory>() {

			@Override
			public void currentObjectChanged(PainterLayout current, PainterLayoutFactory definition) {
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
