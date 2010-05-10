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
import eu.esdihumboldt.hale.rcp.views.table.filter.FeatureSelector;
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
	
	private Image synchImage;
	
	private FeatureSelector secondSelector;

	/**
	 * Default constructor
	 */
	public TransformedTableView() {
		super(new InstanceServiceFeatureSelector(SchemaType.TARGET));
		
		// another selector based on the reference sample service
		secondSelector = new SampleTransformFeatureSelector();
	}

	/**
	 * @see AbstractTableView#provideCustomControls(Composite)
	 */
	@Override
	protected void provideCustomControls(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		parent.setLayout(layout);
		
		final Button synch = new Button(parent, SWT.TOGGLE);
		if (synchImage == null) {
			synchImage = AbstractUIPlugin.imageDescriptorFromPlugin(HALEActivator.PLUGIN_ID, "icons/refresh.gif").createImage(); //$NON-NLS-1$
		}
		synch.setImage(synchImage);
		synch.setToolTipText(Messages.TransformedTableView_SynchToolTipText);
		synch.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				toggleFeatureSelector();
			}
			
		});
	}

	/**
	 * Toggle the feature selector
	 */
	protected void toggleFeatureSelector() {
		FeatureSelector tmp = getFeatureSelector();
		
		setFeatureSelector(secondSelector);
		
		secondSelector = tmp;
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		synchImage.dispose();
		
		super.dispose();
	}

}
