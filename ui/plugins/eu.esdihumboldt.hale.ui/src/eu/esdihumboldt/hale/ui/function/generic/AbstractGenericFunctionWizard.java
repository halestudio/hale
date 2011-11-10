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

package eu.esdihumboldt.hale.ui.function.generic;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.ui.function.AbstractFunctionWizard;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.EntitiesPage;
import eu.esdihumboldt.hale.ui.function.generic.pages.FunctionWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;

/**
 * Generic function wizard
 * @param <T> the function type
 * @param <P> the field definition type
 * @author Simon Templer
 */
public abstract class AbstractGenericFunctionWizard<P extends AbstractParameter, T extends AbstractFunction<P>> extends AbstractFunctionWizard {
	
	private final String functionId;
	private MutableCell resultCell;
	
	private EntitiesPage<T, P, ?> entitiesPage;
	
	private FunctionWizardPage parameterPage;
	
	private Set<FunctionWizardPage> cellPages = new LinkedHashSet<FunctionWizardPage>();

	/**
	 * Create a generic function wizard for a certain function based on a 
	 * schema selection
	 * @param selection the schema selection, may be <code>null</code>
	 * @param functionId the function identifier
	 */
	public AbstractGenericFunctionWizard(SchemaSelection selection, String functionId) {
		super(selection);
		
		this.functionId = functionId;
	}
	
	/**
	 * @see AbstractFunctionWizard#AbstractFunctionWizard(Cell)
	 */
	public AbstractGenericFunctionWizard(Cell cell) {
		super(cell);
		
		this.functionId = cell.getTransformationIdentifier();
	}

	/**
	 * @see AbstractFunctionWizard#init()
	 */
	@Override
	public void init() {
		super.init();
		
		setWindowTitle(getFunction().getDisplayName());
		
		// create the entities page
		// it is needed for creating a new cell to allow assigning the entities
		// and when editing a cell to populate its copy with the same configuration
		entitiesPage = createEntitiesPage(getInitSelection(), getInitCell());
		
		if (!getFunction().getDefinedParameters().isEmpty()) {
			// create the parameter page
			parameterPage = createParameterPage(getInitCell());
		}
	}

	/**
	 * @see AbstractFunctionWizard#init(SchemaSelection)
	 */
	@Override
	protected void init(SchemaSelection selection) {
		// create a new cell
		resultCell = new DefaultCell();
		resultCell.setTransformationIdentifier(getFunctionId());
	}

	/**
	 * @see AbstractFunctionWizard#init(Cell)
	 */
	@Override
	protected void init(Cell cell) {
		// create a new cell even if a cell is already present
		resultCell = new DefaultCell();
		resultCell.setTransformationIdentifier(getFunctionId());
		// the cell configuration will be duplicated or changed by the wizard
		// afterwards the old cell is replaced by the new cell in the alignment
	}

	/**
	 * Create the entities page
	 * @param initSelection the initial selection, may be <code>null</code>
	 * @param initCell the initial cell, may be <code>null</code>
	 * @return the entities page
	 */
	protected abstract EntitiesPage<T, P, ?> createEntitiesPage(SchemaSelection initSelection,
			Cell initCell);
	
	/**
	 * Create the page for configuring the function parameters.
	 * @param initialCell the initial cell, may be <code>null</code>
	 * @return the parameter configuration page or <code>null</code>
	 */
	protected FunctionWizardPage createParameterPage(Cell initialCell) {
		return new ParameterPage(initialCell);
	}

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		
		if (entitiesPage != null) {
			addPage(entitiesPage);
		}
		
		if (parameterPage != null) {
			addPage(parameterPage);
		}
	}

	/**
	 * @see Wizard#addPage(IWizardPage)
	 */
	@Override
	public void addPage(IWizardPage page) {
		// track FunctionWizardPages
		if (page instanceof FunctionWizardPage) {
			cellPages.add((FunctionWizardPage) page);
		}
		
		super.addPage(page);
	}

	/**
	 * @see FunctionWizard#getResult()
	 */
	@Override
	public MutableCell getResult() {
		return resultCell;
	}
	
	/**
	 * Get the function identifier
	 * @return the function identifier
	 */
	public String getFunctionId() {
		return functionId;
	}
	
	/**
	 * Get the function
	 * @return the function
	 */
	public abstract T getFunction();

	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// configure cell with all pages
		for (FunctionWizardPage page : cellPages) {
			page.configureCell(resultCell);
		}
		
		return true;
	}

}
