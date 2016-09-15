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

import java.awt.BorderLayout;

import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;

import de.fhg.igd.mapviewer.BasicMapKit;
import de.fhg.igd.mapviewer.tip.MapTipManager;
import de.fhg.igd.swingrcp.SwingComposite;

/**
 * MapView
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 */
public class MapView extends AbstractMapView implements IPartListener2 {

	private static final String MEMENTO_TOOLS = "tools"; //$NON-NLS-1$

	private static final String MEMENTO_MAP = "map"; //$NON-NLS-1$

	private static final Log log = LogFactory.getLog(MapView.class);

	/**
	 * The ID of this View
	 */
	public static final String ID = "de.fhg.igd.mapviewer.view.MapView"; //$NON-NLS-1$

	private SwingComposite main;

	private ExtendedMapKit mapKit;

	private MapTools mapTools;

	private IMemento initMemento;

	private final MapTipManager mapTips = new MapTipManager();

	/**
	 * The constructor.
	 */
	public MapView() {
	}

	/**
	 * @see ViewPart#init(IViewSite, IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		this.initMemento = memento;

		IPartService partService = site.getService(IPartService.class);
		partService.addPartListener(this);
	}

	/**
	 * @see ViewPart#saveState(IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		mapTools.saveState(memento.createChild(MEMENTO_TOOLS));
		mapKit.saveState(memento.createChild(MEMENTO_MAP));

		super.saveState(memento);
	}

	/**
	 * @see WorkbenchPart#createPartControl(Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		main = new SwingComposite(parent);
		main.getContentPane().setLayout(new BorderLayout());

		mapKit = new ExtendedMapKit(MapView.this);
		main.getContentPane().add(mapKit, BorderLayout.CENTER);

		// actions
		configureActions();

		// restore state
		mapTools.restoreState(
				(initMemento != null) ? (initMemento.getChild(MEMENTO_TOOLS)) : (null));

		// status
		new PositionStatus(mapKit.getMainMap(), getViewSite(), getTitleImage(), epsgProvider);
		new PositionStatus(mapKit.getMiniMap(), getViewSite(), getTitleImage(), epsgProvider);

		// view activator
		ViewActivator activator = new ViewActivator(getViewSite());
		activator.addComponent(mapKit.getMainMap());
		activator.addComponent(mapKit.getMiniMap());
	}

	/**
	 * @see WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				main.getContentPane().requestFocus();
			}
		});
	}

	/**
	 * Configure menu and toolbar actions and register map view extensions
	 */
	public void configureActions() {
		IActionBars bars = getViewSite().getActionBars();

		// tool-bar
		IToolBarManager toolBar = bars.getToolBarManager();
		toolBar.add(mapTools = new MapTools(mapKit));

		// menu
		IMenuManager menu = bars.getMenuManager();
		menu.add(new MapMenu());

		// extensions
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(MapViewExtension.class.getName());

		for (IConfigurationElement element : config) {
			if (element.getName().equals("extra")) { //$NON-NLS-1$
				try {
					MapViewExtension extra = (MapViewExtension) element
							.createExecutableExtension("class"); //$NON-NLS-1$
					extra.setMapView(this);
				} catch (Exception e) {
					log.warn("Error creating map view extension", e); //$NON-NLS-1$
				}
			}
		}

		// add map tips
		mapKit.addCustomPainter(mapTips);
	}

	/**
	 * @return the mapTips
	 */
	public MapTipManager getMapTips() {
		return mapTips;
	}

	/**
	 * @return the map kit
	 */
	public BasicMapKit getMapKit() {
		return mapKit;
	}

	/**
	 * @return the map tools
	 */
	public MapTools getMapTools() {
		return mapTools;
	}

	/**
	 * @see IPartListener2#partVisible(IWorkbenchPartReference)
	 */
	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		// ignore
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
	 * @see IPartListener2#partClosed(IWorkbenchPartReference)
	 */
	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		if (partRef.getPart(false) == this) {
			// do nothing
		}
	}

	/**
	 * @see IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
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
	 * @see IPartListener2#partInputChanged(IWorkbenchPartReference)
	 */
	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
		// ignore
	}

	/**
	 * @see IPartListener2#partOpened(IWorkbenchPartReference)
	 */
	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		if (partRef.getPart(false) == this) {
			// do nothing - expecting restoreState to be called by extensions
		}
	}

	/**
	 * Restore the map state from the view memento.
	 */
	public void restoreState() {
		// restore state (e.g. the first time the view becomes visible)
		final Display display = Display.getCurrent();
		// must be done like this because of mapKit.zoomToPositions -
		// else there is no component width/height set
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						mapKit.restoreState((initMemento != null)
								? (initMemento.getChild(MapView.MEMENTO_MAP)) : (null));
					}
				});
			}
		});
	}

}
