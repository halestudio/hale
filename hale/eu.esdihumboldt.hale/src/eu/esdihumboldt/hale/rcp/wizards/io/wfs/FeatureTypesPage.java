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

package eu.esdihumboldt.hale.rcp.wizards.io.wfs;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.rcp.wizards.io.FeatureTypeList;
import eu.esdihumboldt.hale.rcp.wizards.io.FeatureTypeList.TypeSelectionListener;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FeatureTypesPage extends AbstractTypesPage<WfsConfiguration> {
	
	private FeatureTypeList list;

	/**
	 * Constructor
	 * 
	 * @param configuration the WFS configuration 
	 * @param capsPage the capabilities page
	 */
	public FeatureTypesPage(WfsConfiguration configuration, CapabilitiesPage capsPage) {
		super(configuration, capsPage, "Feature types");
		
		setTitle("Feature type selection");
		setMessage("Select the namespace and feature types for the import");
	}

	/**
	 * @see AbstractTypesPage#update(List)
	 */
	@Override
	protected void update(List<FeatureType> types) {
		list.setFeatureTypes(types);
	}

	/**
	 * @see AbstractWfsPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(1, false));
		
		list = new FeatureTypeList(page);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		list.addTypeSelectionListener(new TypeSelectionListener() {
			
			@Override
			public void selectionChanged() {
				FeatureTypesPage.this.update();
			}
			
		});
		
		setControl(page);
		
		//update();
	}

	/**
	 * @see AbstractWfsPage#updateConfiguration(WfsConfiguration)
	 */
	@Override
	public boolean updateConfiguration(WfsConfiguration configuration) {
		List<FeatureType> selection = list.getSelection();
		
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		else {
			configuration.setFeatureTypes(selection);
			return true;
		}
	}
	
	private void update() {
		List<FeatureType> selection = list.getSelection();
		
		setPageComplete(selection != null && !selection.isEmpty());
	}

}
