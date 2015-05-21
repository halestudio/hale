/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.function.custom;

import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomFunction;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationFunction;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.AbstractFunctionWizard;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.util.wizard.HaleWizardDialog;

/**
 * Base class for custom function wizards
 * 
 * @param <T> the transformation function type
 * @param <C> the custom function type
 * @author Simon Templer
 */
public abstract class AbstractGenericCustomFunctionWizard<C extends CustomFunction<T>, T extends TransformationFunction<?>>
		extends AbstractCustomFunctionWizard<T> {

	private static final ALogger log = ALoggerFactory
			.getLogger(AbstractGenericCustomFunctionWizard.class);

	private C customFunction;

//	private EntitiesPage<T, P, ?> entitiesPage;

//	private List<ParameterPage> parameterPages;

	/**
	 * Create a generic function wizard for a certain function based on a schema
	 * selection
	 * 
	 * @param selection the schema selection, may be <code>null</code>
	 * @param functionId the function identifier
	 */
	public AbstractGenericCustomFunctionWizard(SchemaSelection selection) {
		super(selection);

		setHelpAvailable(true);
	}

//	/**
//	 * @see AbstractFunctionWizard#AbstractFunctionWizard(Cell)
//	 */
//	public AbstractGenericCustomFunctionWizard(Cell cell) {
//		super(cell);
//
//		setHelpAvailable(true);
//		this.functionId = cell.getTransformationIdentifier();
//	}

	/**
	 * @see AbstractFunctionWizard#init()
	 */
	@Override
	public void init() {
		super.init();

//		setWindowTitle(getFunction().getDisplayName());

		// create the entities page
		// it is needed for creating a new cell to allow assigning the entities
		// and when editing a cell to populate its copy with the same
		// configuration
//		entitiesPage = createEntitiesPage(getInitSelection(), getInitCell());

		// create parameter page

		// create editor page
	}

	/**
	 * @see AbstractFunctionWizard#init(SchemaSelection)
	 */
	@Override
	protected void init(SchemaSelection selection) {
		// create a new function
		customFunction = createCustomFunction();
	}

//	/**
//	 * @see AbstractFunctionWizard#init(Cell)
//	 */
//	@Override
//	protected void init(Cell cell) {
//		// create a new cell even if a cell is already present
//		resultCell = new DefaultCell(cell);
//		// copy ID
//		resultCell.setId(cell.getId());
//		// XXX necessary to reset those?
//		resultCell.setSource(null);
//		resultCell.setTarget(null);
//		resultCell.setTransformationParameters(null);
//		// the cell configuration will be duplicated or changed by the wizard
//		// afterwards the old cell is replaced by the new cell in the alignment
//	}

//	/**
//	 * Create the entities page
//	 * 
//	 * @param initSelection the initial selection, may be <code>null</code>
//	 * @param initCell the initial cell, may be <code>null</code>
//	 * @return the entities page
//	 */
//	protected abstract EntitiesPage<T, P, ?> createEntitiesPage(SchemaSelection initSelection,
//			Cell initCell);

	/**
	 * Create a new custom function.
	 * 
	 * @return the function definition
	 */
	protected abstract C createCustomFunction();

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);

		// disable help button, let function wizards do their work afterwards
		// At all other points the buttons aren't created yet.
		if (getContainer() instanceof HaleWizardDialog)
			((HaleWizardDialog) getContainer()).setHelpButtonEnabled(false);
	}

	/**
	 * @see Wizard#setContainer(IWizardContainer)
	 */
	@Override
	public void setContainer(IWizardContainer wizardContainer) {
		super.setContainer(wizardContainer);

		if (wizardContainer instanceof HaleWizardDialog) {
			final HaleWizardDialog container = (HaleWizardDialog) wizardContainer;
			// has to be pageChanging, because closing the tray in page changed
			// leads to an exception
			((HaleWizardDialog) wizardContainer)
					.addPageChangingListener(new IPageChangingListener() {

						@Override
						public void handlePageChanging(PageChangingEvent event) {
							boolean helpAvailable = false;
							if (event.getTargetPage() instanceof HaleWizardPage<?>) {
								HaleWizardPage<?> page = (HaleWizardPage<?>) event.getTargetPage();
								helpAvailable = page.getHelpContext() != null;
							}

							container.setHelpButtonEnabled(helpAvailable);

							if (container.getTray() != null)
								container.closeTray();
						}
					});
		}
	}

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();

//		if (entitiesPage != null) {
//			addPage(entitiesPage);
//		}
//
//		if (parameterPages != null) {
//			for (ParameterPage parameterPage : parameterPages)
//				addPage(parameterPage);
//		}
	}

	/**
	 * @see FunctionWizard#getResult()
	 */
	@Override
	public C getResult() {
		return customFunction;
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
//		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
//		resultCell.setTransformationParameters(parameters);
//		// configure cell with all pages
//		for (IWizardPage page : getPages())
//			if (page instanceof FunctionWizardPage)
//				((FunctionWizardPage) page).configureCell(resultCell);
//			else if (page instanceof ParameterPage)
//				parameters.putAll(((ParameterPage) page).getConfiguration());

		return true;
	}

//	/**
//	 * Returns the cell that would be created if the wizard would be finished
//	 * now.
//	 * 
//	 * @return the cell
//	 */
//	public Cell getUnfinishedCell() {
//		MutableCell current = new DefaultCell();
//		current.setTransformationIdentifier(getFunctionId());
//		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
//		current.setTransformationParameters(parameters);
//		for (IWizardPage page : getPages()) {
//			// stop at first uncompleted page
//			if (!page.isPageComplete())
//				break;
//			if (page instanceof FunctionWizardPage)
//				((FunctionWizardPage) page).configureCell(current);
//			else if (page instanceof ParameterPage)
//				parameters.putAll(((ParameterPage) page).getConfiguration());
//		}
//		return current;
//	}

}
