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

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import org.eclipse.ui.PlatformUI;
import org.jdesktop.swingx.mapviewer.JXMapViewer;
import org.jdesktop.swingx.painter.Painter;

import de.cs3d.util.eclipse.extension.exclusive.ExclusiveExtension.ExclusiveExtensionListener;
import de.fhg.igd.mapviewer.BasicMapKit;
import de.fhg.igd.mapviewer.MapPainter;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.LayoutAugmentation;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.PainterLayout;

/**
 * TODO Type description
 * @author Simon Templer
 */
public class LayoutAugmentationPainter implements MapPainter {
	
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

	/**
	 * @see Painter#paint(Graphics2D, Object, int, int)
	 */
	@Override
	public void paint(Graphics2D g, JXMapViewer map, int width, int height) {
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
		
		PainterLayoutService pls = (PainterLayoutService) PlatformUI.getWorkbench().getService(PainterLayoutService.class);
		
		// get current configuration
		if (pls.getCurrentDefinition() != null) {
			painters = pls.getCurrentDefinition().getPaintersToLayout();
			augmentation = pls.getCurrent().getAugmentation(painters.size());
		}
		else {
			painters = null;
			augmentation = null;
		}
		
		pls.addListener(layoutListener = new ExclusiveExtensionListener<PainterLayout, PainterLayoutFactory>() {
			
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
			}
		});
		
		initialized = true;
	}

	/**
	 * @see MapPainter#setMapKit(BasicMapKit)
	 */
	@Override
	public void setMapKit(BasicMapKit mapKit) {
		// ignore
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
			PainterLayoutService pls = (PainterLayoutService) PlatformUI.getWorkbench().getService(PainterLayoutService.class);
			pls.removeListener(layoutListener);
		}
	}

}
