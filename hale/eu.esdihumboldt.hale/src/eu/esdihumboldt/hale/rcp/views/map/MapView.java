/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import eu.esdihumboldt.hale.models.StyleService;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.views.map.style.DropdownAction;
import eu.esdihumboldt.hale.rcp.views.map.style.LoadStylesAction;
import eu.esdihumboldt.hale.rcp.views.map.style.SaveStylesAction;
import eu.esdihumboldt.hale.rcp.views.map.style.StyleDropdown;


/**
 * The MapView renders and display a Map from geodataset. The MapView uses a 
 * {@link FeatureTilePainter}, which offers the opportunity of overlaying reference 
 * and transformed data in multiple ways. 
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class MapView extends ViewPart {
	
	/**
	 * The view ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.rcp.views.map.MapView"; //$NON-NLS-1$
	
	/**
	 * The canvas to paint the map on
	 */
	private Canvas mapCanvas;
	
	/**
	 * The map painter
	 */
	private FeatureTilePainter painter;
	
	private PositionStatus status;
	
	private Image invertImage;
	
	/**
	 * @see WorkbenchPart#createPartControl(Composite)
	 */
	@Override
	public void createPartControl(Composite _parent) {
		Composite mapComposite = new Composite(_parent, SWT.BEGINNING);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		mapComposite.setLayout(layout);
		
		// add SLD area
		Composite sldComposite = new Composite(mapComposite, SWT.BEGINNING);
		layout = new GridLayout();
		layout.numColumns = 5;
		layout.makeColumnsEqualWidth = false;
		sldComposite.setLayout(layout);
		sldComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		
		ToolBar tools = new ToolBar(sldComposite, SWT.FLAT | SWT.WRAP);
		IToolBarManager tm = new ToolBarManager(tools);
		DropdownAction styles = new StyleDropdown();
		styles.addItem(new Separator());
		Action backgroundAction = new Action(Messages.MapView_ChangeBackgroundText, IAction.AS_PUSH_BUTTON) {

			@Override
			public void run() {
				StyleService ss = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
				ColorDialog dialog = new ColorDialog(Display.getCurrent().getActiveShell());
				dialog.setRGB(ss.getBackground());
				RGB color = dialog.open();
				if (color != null) {
					ss.setBackground(color);
				}
			}
			
		};
		styles.addItem(new ActionContributionItem(backgroundAction));
		styles.addItem(new Separator());
		styles.addItem(new ActionContributionItem(new LoadStylesAction()));
		styles.addItem(new ActionContributionItem(new SaveStylesAction()));
		
		tm.add(styles);
		tm.update(false);
		
		// create the split style combo box
		Combo splitCombo = new Combo(sldComposite, SWT.READ_ONLY);
		ComboViewer splitView = new ComboViewer(splitCombo);
		splitView.add(SplitStyle.values());
		
		// add map area
		Composite mapRenderComposite = new Composite(mapComposite, SWT.BORDER_SOLID);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		mapRenderComposite.setLayout(layout);
		mapRenderComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// set up canvas
		this.mapCanvas = new Canvas(mapRenderComposite, SWT.DOUBLE_BUFFERED | SWT.BORDER);
		//mapCanvas.setBackground(new Color(null, 128, 0, 0));
		mapCanvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// create the painter
		painter = new FeatureTilePainter(mapCanvas);
		
		getSite().setSelectionProvider(painter.getSelectionProvider());
		
		// create position status
		status = new PositionStatus(painter, mapCanvas, getViewSite(), getTitleImage());
		
		// add zoom buttons
		tm.add(painter.getZoomOutAction());
		tm.add(painter.getZoomInAction());
		tm.update(false);
		
		splitView.setSelection(new StructuredSelection(painter.getSplitStyle()));
		splitView.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				
				if (selection instanceof StructuredSelection) {
					StructuredSelection ss = (StructuredSelection) selection;
					
					if (!selection.isEmpty()) {
						painter.setSplitStyle((SplitStyle) ss.getFirstElement());
					}
				}
			}
			
		});
		
		// invert button
		final Button invert = new Button(sldComposite, SWT.TOGGLE);
		if (invertImage == null) {
			invertImage = AbstractUIPlugin.imageDescriptorFromPlugin(HALEActivator.PLUGIN_ID, "icons/invert.gif").createImage(); //$NON-NLS-1$
		}
		invert.setImage(invertImage);
		invert.setToolTipText("Invert split");
		invert.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				painter.setInvertSplit(invert.getSelection());
			}
			
		});
	}

	/**
	 * @see WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// set focus to the canvas for it to receive mouse wheel events
		if (mapCanvas != null) {
			mapCanvas.setFocus();
		}
	}
	
	/**
	 * @return the painter
	 */
	public FeatureTilePainter getPainter() {
		return painter;
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (invertImage != null) {
			invertImage.dispose();
		}
		
		if (painter != null) {
			painter.dispose();
		}
		
		if (status != null) {
			status.dispose();
		}
		
		super.dispose();
	}

}