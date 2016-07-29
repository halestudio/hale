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

package eu.esdihumboldt.hale.ui.views.styledmap;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.jdesktop.swingx.mapviewer.TileOverlayPainter;

import de.fhg.igd.eclipse.util.extension.selective.SelectiveExtension.SelectiveExtensionListener;
import de.fhg.igd.mapviewer.view.MapView;
import de.fhg.igd.mapviewer.view.MapViewExtension;
import de.fhg.igd.mapviewer.view.overlay.ITileOverlayService;
import de.fhg.igd.mapviewer.view.overlay.TileOverlayFactory;
import eu.esdihumboldt.hale.ui.common.service.style.StyleService;
import eu.esdihumboldt.hale.ui.geometry.service.GeometrySchemaService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.PainterLayoutController;
import eu.esdihumboldt.hale.ui.views.styledmap.painter.AbstractInstancePainter;

/**
 * Map view extension for the styled instance map.
 * 
 * @author Simon Templer
 */
public class StyledMapExtra implements MapViewExtension, IPartListener2 {

	private MapView mapView;

	private PainterLayoutController layoutController;

	/**
	 * @see MapViewExtension#setMapView(MapView)
	 */
	@Override
	public void setMapView(MapView mapView) {
		this.mapView = mapView;

		layoutController = new PainterLayoutController(mapView.getMapKit());

		/*
		 * Listen for activated/deactivated instance painters
		 * 
		 * - remove listeners for deactivated painters and clear the waypoints -
		 * update activated listeners and add the corresponding listeners
		 */
		ITileOverlayService overlayService = PlatformUI.getWorkbench()
				.getService(ITileOverlayService.class);
		overlayService.addListener(
				new SelectiveExtensionListener<TileOverlayPainter, TileOverlayFactory>() {

					@Override
					public void deactivated(TileOverlayPainter object,
							TileOverlayFactory definition) {
						if (object instanceof AbstractInstancePainter) {
							AbstractInstancePainter painter = (AbstractInstancePainter) object;

							// get services
							ISelectionService selection = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getSelectionService();
							InstanceService instances = PlatformUI.getWorkbench()
									.getService(InstanceService.class);
							StyleService styles = PlatformUI.getWorkbench()
									.getService(StyleService.class);
							GeometrySchemaService geometries = PlatformUI.getWorkbench()
									.getService(GeometrySchemaService.class);

							// remove listeners
							selection.removeSelectionListener(painter);
							instances.removeListener(painter);
							styles.removeListener(painter.getStyleListener());
							geometries.removeListener(painter.getGeometryListener());

							// clear way-points
							painter.clearWaypoints();
						}
					}

					@Override
					public void activated(TileOverlayPainter object,
							TileOverlayFactory definition) {
						if (object instanceof AbstractInstancePainter) {
							AbstractInstancePainter painter = (AbstractInstancePainter) object;

							// get services
							ISelectionService selection = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getSelectionService();
							InstanceService instances = PlatformUI.getWorkbench()
									.getService(InstanceService.class);
							StyleService styles = PlatformUI.getWorkbench()
									.getService(StyleService.class);
							GeometrySchemaService geometries = PlatformUI.getWorkbench()
									.getService(GeometrySchemaService.class);

							// update
							painter.update(selection.getSelection());

							// add listeners
							selection.addSelectionListener(painter);
							instances.addListener(painter);
							styles.addListener(painter.getStyleListener());
							geometries.addListener(painter.getGeometryListener());
						}
					}
				});

		IPartService partService = mapView.getSite().getService(IPartService.class);
		partService.addPartListener(this);

		// map tips
		mapView.getMapTips().addMapTip(new InstanceMapTip(mapView.getMapKit()), 5);
	}

	/**
	 * @see IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		if (partRef.getPart(false) == mapView) {
			layoutController.disable();

			// get services
			ISelectionService selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getSelectionService();
			InstanceService instances = PlatformUI.getWorkbench().getService(InstanceService.class);
			StyleService styles = PlatformUI.getWorkbench().getService(StyleService.class);
			GeometrySchemaService geometries = PlatformUI.getWorkbench()
					.getService(GeometrySchemaService.class);

			// remove listeners
			disableScenePainterListeners(selection, instances, styles, geometries);
		}
	}

	/**
	 * @see IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		if (partRef.getPart(false) == mapView) {
			mapView.restoreState();

			// get services
			ISelectionService selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getSelectionService();
			InstanceService instances = PlatformUI.getWorkbench().getService(InstanceService.class);
			StyleService styles = PlatformUI.getWorkbench().getService(StyleService.class);
			GeometrySchemaService geometries = PlatformUI.getWorkbench()
					.getService(GeometrySchemaService.class);

			// update
			updateScenePainters(selection);

			// add listeners
			enableScenePainterListeners(selection, instances, styles, geometries);

			layoutController.enable();
		}
	}

	/**
	 * Add the instance painters as listeners.
	 * 
	 * @param selection the selection service
	 * @param instances the instance service
	 * @param styles the style service
	 * @param geometries the geometry schema service
	 */
	private void enableScenePainterListeners(ISelectionService selection, InstanceService instances,
			StyleService styles, GeometrySchemaService geometries) {
		for (AbstractInstancePainter painter : mapView.getMapKit()
				.getTilePainters(AbstractInstancePainter.class)) {
			selection.addSelectionListener(painter);
			instances.addListener(painter);
			styles.addListener(painter.getStyleListener());
			geometries.addListener(painter.getGeometryListener());
		}
	}

	/**
	 * Update the instance painters.
	 * 
	 * @param selection the selection service
	 */
	private void updateScenePainters(ISelectionService selection) {
		for (AbstractInstancePainter painter : mapView.getMapKit()
				.getTilePainters(AbstractInstancePainter.class)) {
			painter.update(selection.getSelection());
		}
	}

	/**
	 * Remove the instance painters as listeners.
	 * 
	 * @param selection the selection service
	 * @param instances the instance service
	 * @param styles the style service
	 * @param geometries the geometry schema service
	 */
	private void disableScenePainterListeners(ISelectionService selection,
			InstanceService instances, StyleService styles, GeometrySchemaService geometries) {
		for (AbstractInstancePainter painter : mapView.getMapKit()
				.getTilePainters(AbstractInstancePainter.class)) {
			selection.removeSelectionListener(painter);
			instances.removeListener(painter);
			styles.removeListener(painter.getStyleListener());
			geometries.removeListener(painter.getGeometryListener());

			painter.clearWaypoints();
		}
	}

	/**
	 * @see IPartListener2#partActivated(IWorkbenchPartReference)
	 */
	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		// ignore
	}

	/**
	 * @see IPartListener2#partBroughtToTop(IWorkbenchPartReference)
	 */
	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		// ignore
	}

	/**
	 * @see IPartListener2#partHidden(IWorkbenchPartReference)
	 */
	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
		// ignore
	}

	/**
	 * @see IPartListener2#partVisible(IWorkbenchPartReference)
	 */
	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		// ignore
	}

	/**
	 * @see IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
		// ignore
	}

	/**
	 * @see IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
		// ignore
	}

}
