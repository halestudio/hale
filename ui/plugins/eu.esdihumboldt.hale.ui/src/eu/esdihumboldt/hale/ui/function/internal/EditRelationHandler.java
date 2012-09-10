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

package eu.esdihumboldt.hale.ui.function.internal;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import de.cs3d.util.eclipse.extension.FactoryFilter;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunction;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardExtension;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardFactory;
import eu.esdihumboldt.hale.ui.function.generic.GenericPropertyFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.GenericTypeFunctionWizard;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Handler for editing an existing {@link Cell}.
 * 
 * @author Simon Templer
 */
public class EditRelationHandler extends AbstractHandler {

	private static final ALogger log = ALoggerFactory.getLogger(EditRelationHandler.class);

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			Object selected = ((IStructuredSelection) selection).getFirstElement();
			if (selected instanceof Cell) {
				final Cell originalCell = (Cell) selected;

				FunctionWizard wizard = null;
				List<FunctionWizardDescriptor<?>> factories = FunctionWizardExtension
						.getInstance()
						.getFactories(
								new FactoryFilter<FunctionWizardFactory, FunctionWizardDescriptor<?>>() {

									@Override
									public boolean acceptFactory(FunctionWizardDescriptor<?> factory) {
										return factory.getFunctionId().equals(
												originalCell.getTransformationIdentifier());
									}

									@Override
									public boolean acceptCollection(
											ExtensionObjectFactoryCollection<FunctionWizardFactory, FunctionWizardDescriptor<?>> collection) {
										return true;
									}
								});

				if (!factories.isEmpty()) {
					// create registered wizard
					FunctionWizardDescriptor<?> fwd = factories.get(0);
					wizard = fwd.createEditWizard(originalCell);
				}

				if (wizard == null) {
					AbstractFunction<?> function = FunctionUtil.getFunction(originalCell
							.getTransformationIdentifier());
					if (function == null) {
						log.userError(MessageFormat.format(
								"Function with identifier ''{0}'' is unknown.",
								originalCell.getTransformationIdentifier()));
						return null;
					}
					// create generic wizard
					if (function instanceof TypeFunction) {
						wizard = new GenericTypeFunctionWizard(originalCell);
					}
					else {
						wizard = new GenericPropertyFunctionWizard(originalCell);
					}
				}

				// initialize wizard
				wizard.init();

				WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);

				if (dialog.open() == WizardDialog.OK) {
					MutableCell cell = wizard.getResult();

					AlignmentService alignmentService = (AlignmentService) PlatformUI
							.getWorkbench().getService(AlignmentService.class);
					// remove the original cell
					// and add the new cell
					alignmentService.replaceCell(originalCell, cell);
				}
			}
		}

		return null;
	}

}
