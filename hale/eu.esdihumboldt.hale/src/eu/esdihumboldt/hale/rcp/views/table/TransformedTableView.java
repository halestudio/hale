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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.views.table.filter.InstanceServiceFeatureSelector;

/**
 * Table for viewing transformed data
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TransformedTableView extends AbstractTableView {
	
	/**
	 * The view id
	 */
	public static final String ID = "eu.esdihumboldt.hale.rcp.views.TransformedTable"; //$NON-NLS-1$
	
	private Image instanceImage;
	
	private Image sampleImage;
	
	private Image mapImage;
	
	private InstanceServiceFeatureSelector instanceSelector;
	
	private SampleTransformFeatureSelector sampleSelector;
	
	private MapFeatureSelector mapSelector;

	/**
	 * Default constructor
	 */
	public TransformedTableView() {
		super(new InstanceServiceFeatureSelector(SchemaType.TARGET));
		
		instanceSelector = (InstanceServiceFeatureSelector) getFeatureSelector();
		// another selector based on the reference sample service
		sampleSelector = new SampleTransformFeatureSelector();
		// selector base on the map selection
		mapSelector = new MapFeatureSelector(SchemaType.TARGET);
	}

	/**
	 * @see AbstractTableView#provideCustomControls(Composite)
	 */
	@Override
	protected void provideCustomControls(Composite parent) {
		GridLayout layout = new GridLayout(2, true);
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
		instanceButton.setToolTipText("Random transformed instances");
		instanceButton.setSelection(true);
		instanceButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setFeatureSelector(instanceSelector);
			}
			
		});
		
		final Button sampleButton = new Button(parent, SWT.RADIO);
		if (sampleImage == null) {
			sampleImage = AbstractUIPlugin.imageDescriptorFromPlugin(HALEActivator.PLUGIN_ID, "icons/table.gif").createImage(); //$NON-NLS-1$
		}
		sampleButton.setImage(sampleImage);
		sampleButton.setToolTipText(Messages.TransformedTableView_SynchToolTipText);
		sampleButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setFeatureSelector(sampleSelector);
			}
			
		});
		
		final Button mapButton = new Button(parent, SWT.RADIO);
		if (mapImage == null) {
			mapImage = AbstractUIPlugin.imageDescriptorFromPlugin(HALEActivator.PLUGIN_ID, "icons/map.gif").createImage(); //$NON-NLS-1$
		}
		mapButton.setImage(mapImage);
		mapButton.setToolTipText("Synchronize with map selection");
		mapButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setFeatureSelector(mapSelector);
			}
			
		});
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		instanceImage.dispose();
		sampleImage.dispose();
		mapImage.dispose();
		
		super.dispose();
	}

}
