package eu.esdihumboldt.hale.rcp.wizards.functions.core.filter;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.rcp.utils.filter.FeatureFilterForm;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizardPage;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Filter wizard page
 * 
 * @author ?, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FilterWizardMainPage extends AbstractSingleCellWizardPage {
	
	/**
	 * The log
	 */
	//private static Logger _log = Logger.getLogger(FilterWizardMainPage.class);
	
	/**
	 * The filter form
	 */
	private FeatureFilterForm filterForm;

	/**
	 * Constructor
	 * 
	 * @param pageName
	 * @param title
	 */
	protected FilterWizardMainPage(String pageName, String title) {
		super(pageName, title, (ImageDescriptor) null);
		setTitle(pageName);
		setDescription("Configure your CQL-Expression to proceed filter operation.");
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);
		
		/*
		 * TODO if this page shall also handle filter editing:
		 * determine if there is already a filter present and
		 * parse the filter expression
		 */
		//Cell cell = getParent().getResultCell();

		// create a composite to hold the widgets
		Composite page = new Composite(parent, SWT.NULL);
		page.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL));
		page.setLayout(new GridLayout(1, false));
		
		
		filterForm = new FeatureFilterForm((TypeDefinition) getParent().getSourceItem().getDefinition(),
				page, SWT.NONE);
		filterForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		setErrorMessage(null); // should not initially have error message
		super.setControl(page);

	}

	/**
	 * 
	 * @return CQL expression based on the pageinput.
	 */
	public String buildCQL() {
		return filterForm.buildCQL();
	}

}
