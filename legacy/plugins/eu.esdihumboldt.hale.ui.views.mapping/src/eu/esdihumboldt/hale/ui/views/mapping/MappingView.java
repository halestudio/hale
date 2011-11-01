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
package eu.esdihumboldt.hale.ui.views.mapping;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;


/**
 * View for viewing, removing and editing alignment cells
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class MappingView extends ViewPart {

	/**
	 * The view ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.mapping"; //$NON-NLS-1$
	
	private CellSelector cellSelector;

	/**
	 * @see WorkbenchPart#createPartControl(Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
		
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(2, false));
		
		cellSelector = new CellSelector(page, selectionService);
		cellSelector.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		CellDetails details = new CellDetails(page);
		details.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		cellSelector.addSelectionChangedListener(details);
		
		getSite().setSelectionProvider(cellSelector);
	}

	/**
	 * @see WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// do nothing
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (cellSelector != null) {
			cellSelector.dispose();
		}
	}

}
