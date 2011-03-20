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

package eu.esdihumboldt.hale.rcp.views.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.opengis.feature.Feature;

import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.views.table.filter.InstanceServiceFeatureSelector;


/**
 * Table for viewing reference data
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class ReferenceTableView extends AbstractTableView {
	
	/**
	 * The view id
	 */
	public static final String ID = "eu.esdihumboldt.hale.rcp.views.ReferenceTable"; //$NON-NLS-1$
	
	private Image mapImage;
	
	private Image instanceImage;
	
	private InstanceServiceFeatureSelector instanceSelector;
	
	private MapFeatureSelector mapSelector;

	/**
	 * Default constructor
	 */
	public ReferenceTableView() {
		super(new InstanceServiceFeatureSelector(SchemaType.SOURCE));
		
		instanceSelector = (InstanceServiceFeatureSelector) getFeatureSelector();
		// another selector based on the map selection
		mapSelector = new MapFeatureSelector(SchemaType.SOURCE);
	}

	/**
	 * @see AbstractTableView#onSelectionChange(Iterable)
	 */
	@Override
	protected void onSelectionChange(Iterable<Feature> selection) {
		ReferenceSampleService rss = (ReferenceSampleService) PlatformUI.getWorkbench().getService(ReferenceSampleService.class);
		
		List<Feature> res = new ArrayList<Feature>();
		if (selection != null) {
			for (Feature feature : selection) {
				res.add(feature);
			}
		}
		
		rss.setReferenceFeatures(res);
	}
	
	/**
	 * @see AbstractTableView#provideCustomControls(Composite)
	 */
	@Override
	protected void provideCustomControls(Composite parent) {
		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		parent.setLayout(layout);
		
		final Button instanceButton = new Button(parent, SWT.RADIO);
		if (instanceImage == null) {
			instanceImage = AbstractUIPlugin.imageDescriptorFromPlugin(HALEActivator.PLUGIN_ID, "icons/random.gif").createImage(); //$NON-NLS-1$
		}
		instanceButton.setImage(instanceImage);
		instanceButton.setToolTipText(Messages.ReferenceTableView_0); //$NON-NLS-1$
		instanceButton.setSelection(true);
		instanceButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setFeatureSelector(instanceSelector);
			}
			
		});
		
		final Button mapButton = new Button(parent, SWT.RADIO);
		if (mapImage == null) {
			mapImage = AbstractUIPlugin.imageDescriptorFromPlugin(HALEActivator.PLUGIN_ID, "icons/map.gif").createImage(); //$NON-NLS-1$
		}
		mapButton.setImage(mapImage);
		mapButton.setToolTipText(Messages.ReferenceTableView_1); //$NON-NLS-1$
		mapButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setFeatureSelector(mapSelector);
			}
			
		});
	}

	/**
	 * @see AbstractTableView#dispose()
	 */
	@Override
	public void dispose() {
		if (mapImage != null) {
			mapImage.dispose();
		}
		if (instanceImage != null) {
			instanceImage.dispose();
		}
		
		super.dispose();
	}

}
