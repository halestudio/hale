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
package eu.esdihumboldt.hale.rcp.views;

import org.eclipse.swt.graphics.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.models.impl.InstanceServiceFactory;


/**
 * The MapView render and display a Map from geodataset. The MapView is divided 
 * diagonal into two parts. One part for a Map with the look of SLD A and the
 * User Data Model. The second part with the look of SLD B and the INSPIRE Data
 * Model.
 * 
 * @author Thorsten Reitz, Christian Jauss
 * @version $Id$
 */
public class MapView extends ViewPart {
	
	public static final String ID = "eu.esdihumboldt.hale.rcp.views.MapView";

	public void createPartControl(Composite _parent) {
		Composite mapComposite = new Composite(_parent, SWT.BEGINNING);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		mapComposite.setLayout(layout);
		
		// add SLD area
		Composite sldComposite = new Composite(mapComposite, SWT.BEGINNING);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		sldComposite.setLayout(layout);
		
		Label sldALabel = new Label(sldComposite, SWT.NONE);
		sldALabel.setText("SLD A:");
		
		Label sldBLabel = new Label(sldComposite, SWT.NONE);
		sldBLabel.setText("SLD B:");
		
		// add map area
		Composite mapRenderComposite = new Composite(mapComposite, SWT.BORDER_SOLID);
		layout = new GridLayout();
		layout.numColumns = 1;
		mapRenderComposite.setLayout(layout);
		mapRenderComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		final Canvas mapCanvas = new Canvas(mapRenderComposite, SWT.BORDER_SOLID);
		mapCanvas.setBackground(new Color(null, 128, 0, 0));
		mapCanvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		this.refreshMap();
		
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		
	}
	
	public void refreshMap() {
		InstanceServiceFactory.getInstance().getAllFeatures(
				DatasetType.reference);
	}
}