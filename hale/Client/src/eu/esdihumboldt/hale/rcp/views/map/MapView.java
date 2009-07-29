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

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;


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
	
	public static final String ID = "eu.esdihumboldt.hale.rcp.views.map.MapView";
	
	/**
	 * The canvas to paint the map on
	 */
	private Canvas mapCanvas;
	
	/**
	 * The map painter
	 */
	private FeatureTilePainter painter;
	
	/**
	 * @see WorkbenchPart#createPartControl(Composite)
	 */
	public void createPartControl(Composite _parent) {
		Composite mapComposite = new Composite(_parent, SWT.BEGINNING);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		mapComposite.setLayout(layout);
		
		// add SLD area
		Composite sldComposite = new Composite(mapComposite, SWT.BEGINNING);
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = false;
		sldComposite.setLayout(layout);
		sldComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		
		Label sldALabel = new Label(sldComposite, SWT.NONE);
		sldALabel.setText("SLD A:  Default  ");
		
		Label sldBLabel = new Label(sldComposite, SWT.NONE);
		sldBLabel.setText("SLD B:  Default  ");
		
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
	}

	/**
	 * @see WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// set focus to the canvas for it to receive mouse wheel events
		mapCanvas.setFocus();
	}

}