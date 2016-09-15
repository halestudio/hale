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

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import org.eclipse.ui.PlatformUI;
import org.jdesktop.swingx.mapviewer.JXMapViewer;
import org.jdesktop.swingx.painter.AbstractPainter;

import de.fhg.igd.eclipse.util.extension.exclusive.ExclusiveExtension.ExclusiveExtensionListener;
import de.fhg.igd.mapviewer.BasicMapKit;
import de.fhg.igd.mapviewer.MapPainter;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.LayoutAugmentation;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.PainterLayout;

/**
 * Layout augmentation painer.
 * 
 * @author Simon Templer
 */
public class LayoutAugmentationPainter extends AbstractPainter<JXMapViewer>implements MapPainter {

	/**
	 * The current layout augmentation
	 */
	private LayoutAugmentation augmentation;

	/**
	 * The painters controlled by the layout
	 */
	private List<PainterProxy> painters;

	private boolean initialized = false;

	private ExclusiveExtensionListener<PainterLayout, PainterLayoutFactory> layoutListener;

	private BasicMapKit mapKit;

	/**
	 * Default constructor.
	 */
	public LayoutAugmentationPainter() {
		super();

		setCacheable(true);
	}

	/**
	 * @see AbstractPainter#doPaint(Graphics2D, Object, int, int)
	 */
	@Override
	public void doPaint(Graphics2D g, JXMapViewer map, int width, int height) {
		init();

		if (augmentation != null) {
			augmentation.paint(g, map, painters, width, height);
		}
	}

	/**
	 * Initialize the painter
	 */
	private void init() {
		if (initialized) {
			return;
		}

		PainterLayoutService pls = PlatformUI.getWorkbench().getService(PainterLayoutService.class);

		// get current configuration
		if (pls.getCurrentDefinition() != null) {
			painters = pls.getCurrentDefinition().getPaintersToLayout();
			augmentation = pls.getCurrent().getAugmentation(painters.size());
		}
		else {
			painters = null;
			augmentation = null;
		}

		pls.addListener(
				layoutListener = new ExclusiveExtensionListener<PainterLayout, PainterLayoutFactory>() {

					@Override
					public void currentObjectChanged(PainterLayout current,
							PainterLayoutFactory definition) {
						if (definition != null) {
							painters = definition.getPaintersToLayout();
							augmentation = current.getAugmentation(painters.size());
						}
						else {
							painters = null;
							augmentation = null;
						}
						clearCache();
						if (mapKit != null) {
							mapKit.refresh();
						}

					}
				});

		initialized = true;
		clearCache();
	}

	/**
	 * @see MapPainter#setMapKit(BasicMapKit)
	 */
	@Override
	public void setMapKit(BasicMapKit mapKit) {
		this.mapKit = mapKit;
	}

	/**
	 * @see MapPainter#getTipText(Point)
	 */
	@Override
	public String getTipText(Point point) {
		return null;
	}

	/**
	 * @see MapPainter#dispose()
	 */
	@Override
	public void dispose() {
		if (layoutListener != null) {
			PainterLayoutService pls = PlatformUI.getWorkbench()
					.getService(PainterLayoutService.class);
			pls.removeListener(layoutListener);
		}
	}

}
