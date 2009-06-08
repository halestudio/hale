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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.renderer.lite.StreamingRenderer;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.StyleService;
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
public class MapView 
	extends ViewPart 
	implements HaleServiceListener {
	
	public static final String ID = "eu.esdihumboldt.hale.rcp.views.map.MapView";
	
	private SplitRenderer renderer;
	private Canvas mapCanvas;
	
	private StyleService styleService = null;
	private InstanceService instanceService = null;

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
		sldALabel.setText("SLD A:  Default");
		
		Label sldBLabel = new Label(sldComposite, SWT.NONE);
		sldBLabel.setText("SLD B:  Default");
		
		// add map area
		Composite mapRenderComposite = new Composite(mapComposite, SWT.BORDER_SOLID);
		layout = new GridLayout();
		layout.numColumns = 1;
		mapRenderComposite.setLayout(layout);
		mapRenderComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// set up the renderer
		this.styleService = (StyleService) this.getSite().getService(
				StyleService.class);
		this.styleService.addListener(this);
		this.instanceService = (InstanceService) this.getSite()
				.getService(InstanceService.class);
		this.instanceService.addListener(this);
		
		// set up Canvas and renderer
		this.renderer = new SplitRenderer(styleService);
		
		this.mapCanvas = new Canvas(mapRenderComposite, SWT.BORDER_SOLID);
		mapCanvas.setBackground(new Color(null, 128, 0, 0));
		mapCanvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.refreshMap();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		
	}
	
	/**
	 * Will retrieve currently available instances from the InstanceService. 
	 * This is only useful if a new instance data set was loaded, since it
	 * effectively resets the whole renderer.
	 * 
	 * @see eu.esdihumboldt.hale.models.HaleServiceListener#update()
	 */
	public void update() {
		this.refreshMap();
	}
	
	protected void refreshMap() {
		FeatureCollection features = this.instanceService.getFeatures(
				DatasetType.reference);
		if (features == null) {
			return;
		}
		this.renderer.setMapArea(features.getBounds());
		Rectangle currentBounds = this.mapCanvas.getBounds();
		this.renderer.setPaintArea(new java.awt.Rectangle(
				currentBounds.x, currentBounds.y, 
				currentBounds.width, currentBounds.height));
		
		this.mapCanvas.setBackgroundImage(new Image(mapCanvas.getDisplay(), 
				AwtSwtImageConversionUtilities.convertToSWT(
						this.renderer.renderFeatures(features))));
		
	}
}