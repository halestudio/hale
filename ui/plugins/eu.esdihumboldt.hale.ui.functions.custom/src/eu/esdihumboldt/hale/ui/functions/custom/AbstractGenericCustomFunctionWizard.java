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

package eu.esdihumboldt.hale.ui.functions.custom;

import javax.annotation.Nullable;

import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomFunction;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationFunction;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.AbstractFunctionWizard;
import eu.esdihumboldt.hale.ui.functions.custom.pages.CustomFunctionWizardPage;
import eu.esdihumboldt.hale.ui.util.wizard.HaleWizardDialog;

/**
 * Base class for custom function wizards
 * 
 * @param <T> the transformation function type
 * @param <F> the function type
 * @param <C> the custom function type
 * @author Simon Templer
 */
public abstract class AbstractGenericCustomFunctionWizard<C extends CustomFunction<F, T>, F extends FunctionDefinition<?>, T extends TransformationFunction<?>>
		extends AbstractCustomFunctionWizard<C> {

//	private static final ALogger log = ALoggerFactory
//			.getLogger(AbstractGenericCustomFunctionWizard.class);

	private C customFunction;

//	private EntitiesPage<T, P, ?> entitiesPage;

//	private List<ParameterPage> parameterPages;

	/**
	 * Create a custom function wizard from scratch or based on an existing
	 * custom function.
	 * 
	 * @param function the custom function or <code>null</code>
	 */
	public AbstractGenericCustomFunctionWizard(C function) {
		super(function);

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

	@Override
	protected void init(C function) {
		// create a new function
		customFunction = createCustomFunction(function);
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
	 * Create a custom function.
	 * 
	 * @param org an existing custom function to copy or <code>null</code>
	 * 
	 * @return the function definition
	 */
	protected abstract C createCustomFunction(@Nullable C org);

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
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		for (IWizardPage page : getPages()) {
			if (page instanceof CustomFunctionWizardPage) {
				((CustomFunctionWizardPage) page).apply();
			}
		}

		return true;
	}

	/**
	 * Returns the cell that would be created if the wizard would be finished
	 * now.
	 * 
	 * @return the cell
	 */
	public C getUnfinishedFunction() {
		if (getResultFunction() == null) {
			return null;
		}

		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				for (IWizardPage page : getPages()) {
					// stop at first uncompleted page
					if (!page.isPageComplete())
						break;
					if (page instanceof CustomFunctionWizardPage)
						((CustomFunctionWizardPage) page).apply();
				}
			}
		});
		return getResultFunction();
	}

	@Override
	public C getResultFunction() {
		return customFunction;
	}

}
