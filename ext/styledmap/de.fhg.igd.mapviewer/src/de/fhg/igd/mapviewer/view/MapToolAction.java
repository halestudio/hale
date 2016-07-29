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
package de.fhg.igd.mapviewer.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

import de.fhg.igd.mapviewer.BasicMapKit;
import de.fhg.igd.mapviewer.MapTool;
import de.fhg.igd.mapviewer.tools.AbstractMapTool;
import de.fhg.igd.mapviewer.tools.Activator;
import de.fhg.igd.mapviewer.view.arecalculation.AreaCalc;

/**
 * MapToolAction
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class MapToolAction extends Action implements Activator {

	private static final Log log = LogFactory.getLog(MapToolAction.class);

	private final AbstractMapTool tool;

	private final BasicMapKit mapKit;

	/**
	 * Creates an action that activates the given tool
	 * 
	 * @param tool the map tool
	 * @param mapKit the map kit
	 * @param checked if the map tool shall be activated initially
	 */
	public MapToolAction(AbstractMapTool tool, BasicMapKit mapKit, boolean checked) {
		super(tool.getName(), Action.AS_RADIO_BUTTON);

		tool.setMapKit(mapKit);
		tool.setActivator(this);

		// set icon
		if (tool.getIconURL() != null) {
			try {
				setImageDescriptor(ImageDescriptor.createFromURL(tool.getIconURL()));
			} catch (Exception e) {
				log.warn("Error creating action icon", e); //$NON-NLS-1$
			}
		}

		// set tool tip
		setToolTipText(tool.getDescription());

		this.tool = tool;
		this.mapKit = mapKit;

		if (checked) {
			setChecked(true);
			mapKit.setMapTool(tool);
		}
	}

	/**
	 * @see Activator#activate()
	 */
	@Override
	public void activate() {
		AreaCalc.getInstance().setArea("");
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				setChecked(true);
			}

		});
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		// if (isChecked()) { XXX not working in combination w/
		// HTMLToolTipProvider
		mapKit.setMapTool(tool);
		// }
	}

	/**
	 * @return the {@link MapTool}
	 */
	public MapTool getTool() {
		return tool;
	}

}
