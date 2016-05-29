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

package eu.esdihumboldt.hale.ui.function.contribution;

import org.eclipse.ui.PlatformUI;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityService;
import eu.esdihumboldt.hale.ui.function.contribution.internal.AbstractWizardAction;
import eu.esdihumboldt.hale.ui.function.contribution.internal.SchemaSelectionWizardAction;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.selection.SchemaSelectionHelper;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Function wizard contribution based on {@link SchemaSelection}
 * 
 * @author Simon Templer
 */
public class SchemaSelectionFunctionContribution extends AbstractFunctionWizardContribution {

	private static final ALogger log = ALoggerFactory
			.getLogger(SchemaSelectionFunctionContribution.class);

	private final SchemaSelectionFunctionMatcher matcher;

	/**
	 * Default constructor.
	 */
	public SchemaSelectionFunctionContribution() {
		this(new SchemaSelectionFunctionMatcher());
	}

	/**
	 * Constructor.
	 * 
	 * @param matcher a custom function matcher
	 */
	public SchemaSelectionFunctionContribution(SchemaSelectionFunctionMatcher matcher) {
		super();
		this.matcher = matcher;
	}

	/**
	 * @see AbstractFunctionWizardContribution#createWizardAction(FunctionWizardDescriptor,
	 *      AlignmentService)
	 */
	@Override
	protected AbstractWizardAction<?> createWizardAction(FunctionWizardDescriptor<?> descriptor,
			AlignmentService alignmentService) {
		return new SchemaSelectionWizardAction(this, descriptor, alignmentService);
	}

	/**
	 * @see AbstractFunctionWizardContribution#isActive(FunctionWizardDescriptor)
	 */
	@Override
	public boolean isActive(FunctionWizardDescriptor<?> descriptor) {
		FunctionDefinition<?> function = descriptor.getFunction();
		// rule out functions not supported by the compatibility mode
		try {
			if (!PlatformUI.getWorkbench().getService(CompatibilityService.class).getCurrent()
					.supportsFunction(function.getId(), HaleUI.getServiceProvider())) {
				return false;
			}
		} catch (NullPointerException npe) {
			// ignore any NPEs in this nicely phrased condition
		}

		try {
			return matcher.matchFunction(function, getSelection());
		} catch (IllegalStateException e) {
			log.error("Unsupported function deactivated");
			return false;
		}
	}

	/**
	 * Get the schema selection
	 * 
	 * @return the schema selection to use
	 */
	public SchemaSelection getSelection() {
//		ISelectionService selectionService = PlatformUI.getWorkbench()
//			.getActiveWorkbenchWindow().getSelectionService();
//		ISelection selection = selectionService.getSelection();

//		if (selection instanceof SchemaSelection) {
//			return (SchemaSelection) selection;
//		}
//		else {
		return SchemaSelectionHelper.getSchemaSelection();
//		}
	}

}
