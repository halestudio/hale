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

package eu.esdihumboldt.hale.ui.views.data;

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

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.service.instance.sample.InstanceSampleService;
import eu.esdihumboldt.hale.ui.views.data.internal.DataViewPlugin;
import eu.esdihumboldt.hale.ui.views.data.internal.Messages;
import eu.esdihumboldt.hale.ui.views.data.internal.filter.InstanceServiceSelector;
import eu.esdihumboldt.hale.ui.views.data.internal.filter.WindowSelectionSelector;
import eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart;


/**
 * Table for viewing reference data
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class SourceDataView extends AbstractDataView {
	
	/**
	 * The view id
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.data.source"; //$NON-NLS-1$
	
	private Image mapImage;
	
	private Image instanceImage;
	
	private InstanceServiceSelector instanceSelector;
	
	private WindowSelectionSelector mapSelector;

	/**
	 * Default constructor
	 */
	public SourceDataView() {
		super(new InstanceServiceSelector(SchemaSpaceID.SOURCE), ID + ".viewer");
		
		instanceSelector = (InstanceServiceSelector) getDefaultInstanceSelector();
		// another selector based on the map selection
		mapSelector = new WindowSelectionSelector(DataSet.SOURCE);
	}
	
	/**
	 * @see PropertiesViewPart#getViewContext()
	 */
	@Override
	protected String getViewContext() {
		return "eu.esdihumboldt.hale.doc.user.source_data";
	}

	/**
	 * @see AbstractDataView#onSelectionChange(Iterable)
	 */
	@Override
	protected void onSelectionChange(Iterable<Instance> selection) {
		InstanceSampleService rss = (InstanceSampleService) PlatformUI.getWorkbench().getService(InstanceSampleService.class);
		
		List<Instance> res = new ArrayList<Instance>();
		if (selection != null) {
			for (Instance instance : selection) {
				res.add(instance);
			}
		}
		
		rss.setReferenceInstances(res);
	}
	
	/**
	 * @see AbstractDataView#provideCustomControls(Composite)
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
			instanceImage = DataViewPlugin.getImageDescriptor("icons/random.gif").createImage(); //$NON-NLS-1$
		}
		instanceButton.setImage(instanceImage);
		instanceButton.setToolTipText(Messages.ReferenceTableView_0); //$NON-NLS-1$
		instanceButton.setSelection(true);
		instanceButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setInstanceSelector(instanceSelector);
			}
			
		});
		
		final Button mapButton = new Button(parent, SWT.RADIO);
		if (mapImage == null) {
			mapImage = DataViewPlugin.getImageDescriptor("icons/map.gif").createImage(); //$NON-NLS-1$
		}
		mapButton.setImage(mapImage);
		mapButton.setToolTipText(Messages.ReferenceTableView_1); //$NON-NLS-1$
		mapButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setInstanceSelector(mapSelector);
			}
			
		});
	}

	/**
	 * @see AbstractDataView#dispose()
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
