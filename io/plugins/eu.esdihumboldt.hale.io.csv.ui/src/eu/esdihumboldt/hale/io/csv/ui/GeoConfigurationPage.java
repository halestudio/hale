/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.csv.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVConstants;
import eu.esdihumboldt.hale.ui.io.ImportWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Geometry configuration for the Schema Reader
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class GeoConfigurationPage extends
AbstractConfigurationPage<ImportProvider, IOProviderFactory<ImportProvider>, ImportWizard<ImportProvider, IOProviderFactory<ImportProvider>>>
implements ModifyListener {

	private ComboViewer coordsys;

	/**
	 * default constructor
	 */
	public GeoConfigurationPage() {
		super("CSVGeo");
		
		setTitle("Geometry Settings");
		setDescription("Please select your cooridnate system");
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	@Override
	public void modifyText(ModifyEvent e) {
		//
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(ImportProvider provider) {
		
		String geoString = coordsys.getCombo().getText();
		
		provider.setParameter(CSVConstants.PARAM_GEOMETRY, geoString);
		
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(2, true));
		GridData layoutData = new GridData();
		layoutData.widthHint = 30;

		Collection<String> geoSelection = new ArrayList<String>();
		geoSelection.add("muh");
		geoSelection.add("kuh");

		Label coordsysLabel = new Label(page, SWT.NONE);
		coordsysLabel.setText("Select coordinate system");

		coordsys = new ComboViewer(page, SWT.NONE);
		coordsys.getControl().setLayoutData(GridDataFactory.copyData(layoutData));
		//coordsys.setInput(geoSelection);
		coordsys.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				String sys = coordsys.getCombo().getText();
				
				if(sys.isEmpty()) {
					setPageComplete(false);
					setErrorMessage("You have not selected a valid coordinate system!");
				} else {
					setPageComplete(true);
					setErrorMessage(null);
				}
				
			}
		});
		coordsys.setContentProvider(ArrayContentProvider.getInstance());

		
		page.pack();
		
	}

}
