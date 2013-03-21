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

package eu.esdihumboldt.hale.ui.function.generic;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import de.cs3d.util.eclipse.extension.FactoryFilter;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.ui.function.AbstractFunctionWizard;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.function.extension.ParameterPageExtension;
import eu.esdihumboldt.hale.ui.function.extension.ParameterPageFactory;
import eu.esdihumboldt.hale.ui.function.generic.pages.EntitiesPage;
import eu.esdihumboldt.hale.ui.function.generic.pages.FunctionWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.pages.GenericParameterPage;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.util.wizard.TitleImageWizard;

/**
 * Generic function wizard
 * 
 * @param <T> the function type
 * @param <P> the field definition type
 * @author Simon Templer
 */
public abstract class AbstractGenericFunctionWizard<P extends AbstractParameter, T extends AbstractFunction<P>>
		extends AbstractFunctionWizard implements TitleImageWizard {

	private static final ALogger log = ALoggerFactory
			.getLogger(AbstractGenericFunctionWizard.class);

	private final String functionId;
	private MutableCell resultCell;

	private EntitiesPage<T, P, ?> entitiesPage;

	private List<ParameterPage> parameterPages;

	private Image functionImage;

	/**
	 * Create a generic function wizard for a certain function based on a schema
	 * selection
	 * 
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
		// and when editing a cell to populate its copy with the same
		// configuration
		entitiesPage = createEntitiesPage(getInitSelection(), getInitCell());

		// create parameter pages
		if (!getFunction().getDefinedParameters().isEmpty())
			parameterPages = createParameterPages(getInitCell());
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
		resultCell.setPriority(cell.getPriority());
		// the cell configuration will be duplicated or changed by the wizard
		// afterwards the old cell is replaced by the new cell in the alignment
	}

	/**
	 * Create the entities page
	 * 
	 * @param initSelection the initial selection, may be <code>null</code>
	 * @param initCell the initial cell, may be <code>null</code>
	 * @return the entities page
	 */
	protected abstract EntitiesPage<T, P, ?> createEntitiesPage(SchemaSelection initSelection,
			Cell initCell);

	/**
	 * Create the page for configuring the function parameters.
	 * 
	 * @param initialCell the initial cell, may be <code>null</code>
	 * @return the parameter configuration page or <code>null</code>
	 */
	protected List<ParameterPage> createParameterPages(Cell initialCell) {
		LinkedList<ParameterPage> parameterPages = new LinkedList<ParameterPage>();
		// create copy of function parameter set
		Set<FunctionParameter> functionParameters = new LinkedHashSet<FunctionParameter>();
		for (FunctionParameter param : getFunction().getDefinedParameters())
			functionParameters.add(param);
		// get initial values
		ListMultimap<String, ParameterValue> initialValues = initialCell == null ? null
				: initialCell.getTransformationParameters();
		if (initialValues != null)
			initialValues = Multimaps.unmodifiableListMultimap(initialValues);
		// get available parameter pages
		List<ParameterPageFactory> paramPageFactories = ParameterPageExtension.getInstance()
				.getFactories(new FactoryFilter<ParameterPage, ParameterPageFactory>() {

					@Override
					public boolean acceptFactory(ParameterPageFactory factory) {
						return factory.getFunctionId().equals(getFunctionId());
					}

					@Override
					public boolean acceptCollection(
							ExtensionObjectFactoryCollection<ParameterPage, ParameterPageFactory> collection) {
						return true;
					}
				});
		// use available parameter pages (first come first serve)
		for (ParameterPageFactory paramPageFactory : paramPageFactories) {
			Set<FunctionParameter> pageFunctionParameters = new HashSet<FunctionParameter>();
			for (FunctionParameter fp : paramPageFactory.getAssociatedParameters())
				if (functionParameters.contains(fp))
					pageFunctionParameters.add(fp);
			if (!pageFunctionParameters.isEmpty()) {
				ParameterPage paramPage;
				try {
					paramPage = paramPageFactory.createExtensionObject();
				} catch (Exception e) {
					log.error(
							"Could not creating parameter page " + paramPageFactory.getIdentifier(),
							e);
					continue;
				}
				functionParameters.removeAll(pageFunctionParameters);
				parameterPages.add(paramPage);
				paramPage.setParameter(pageFunctionParameters, initialValues);
			}
		}
		// use generic parameter page for remaining parameters
		if (!functionParameters.isEmpty()) {
			ParameterPage generic = new GenericParameterPage();
			generic.setParameter(functionParameters, initialValues);
			parameterPages.add(generic);
		}

		return parameterPages;
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

		if (parameterPages != null) {
			for (ParameterPage parameterPage : parameterPages)
				addPage(parameterPage);
		}
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
	 * 
	 * @return the function identifier
	 */
	public String getFunctionId() {
		return functionId;
	}

	/**
	 * Get the function
	 * 
	 * @return the function
	 */
	public abstract T getFunction();

	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		resultCell.setTransformationParameters(parameters);
		// configure cell with all pages
		for (IWizardPage page : getPages())
			if (page instanceof FunctionWizardPage)
				((FunctionWizardPage) page).configureCell(resultCell);
			else if (page instanceof ParameterPage)
				parameters.putAll(((ParameterPage) page).getConfiguration());

		return true;
	}

	/**
	 * Returns the cell that would be created if the wizard would be finished
	 * now.
	 * 
	 * @return the cell
	 */
	public Cell getUnfinishedCell() {
		MutableCell current = new DefaultCell();
		current.setTransformationIdentifier(getFunctionId());
		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		current.setTransformationParameters(parameters);
		for (IWizardPage page : getPages()) {
			// stop at first uncompleted page
			if (!page.isPageComplete())
				break;
			if (page instanceof FunctionWizardPage)
				((FunctionWizardPage) page).configureCell(current);
			else if (page instanceof ParameterPage)
				parameters.putAll(((ParameterPage) page).getConfiguration());
		}
		return current;
	}

	/**
	 * @see TitleImageWizard#getTitleImage()
	 */
	@Override
	public Image getTitleImage() {
		if (functionImage == null) {
			try {
				functionImage = ImageDescriptor.createFromURL(getFunction().getIconURL())
						.createImage();
			} catch (Exception e) {
				// ignore
			}
		}

		return functionImage;
	}

	/**
	 * @see Wizard#dispose()
	 */
	@Override
	public void dispose() {
		if (functionImage != null) {
			functionImage.dispose();
		}

		super.dispose();
	}

}
