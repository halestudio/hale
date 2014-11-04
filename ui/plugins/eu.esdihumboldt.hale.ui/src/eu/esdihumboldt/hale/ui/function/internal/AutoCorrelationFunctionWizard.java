/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.function.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.util.io.ThreadProgressMonitor;
import eu.esdihumboldt.util.Pair;

/**
 * Wizard for creating an automatic mapping.
 * 
 * @author Yasmina Kammeyer
 */
public class AutoCorrelationFunctionWizard extends Wizard {

	private static final ALogger log = ALoggerFactory
			.getLogger(AutoCorrelationFunctionWizard.class);
	/**
	 * Page to set the source types
	 */
	protected AutoCorrelationTypesPage typePage;

	/**
	 * Page to set and select the needed Parameter
	 */
	protected AutoCorrelationParameterPage parameterPage;
	private Collection<TypeDefinition> source;
	private Collection<TypeDefinition> target;
	private boolean ignoreNamespace;
	private int mode;
	private Alignment alignment;

	/**
	 * Default Constructor
	 */
	public AutoCorrelationFunctionWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public String getWindowTitle() {
		return "Auto Correlation";
	}

	@Override
	public void addPages() {
		typePage = new AutoCorrelationTypesPage("Types");
		parameterPage = new AutoCorrelationParameterPage("Parameter");

		addPage(typePage);
		addPage(parameterPage);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {

		// get SchemaSelection (source and target files) from page
		// fill with parameters from page

		// SchemaSelection typeSelection = new
		// DefaultSchemaSelection(typePage.getSourceTypes(),
		// typePage.getTargetTypes(),
		// DefaultSchemaSelection.SchemaStructuredMode.ALL);
		// step #1 Collect arguments from pages
//		Collection<TypeDefinition> source = typePage.getSourceTypes();
//		Collection<TypeDefinition> target = typePage.getTargetTypes();
//
//		boolean ignoreNamespace = parameterPage.getIgnoreNamespace();
//		int mode = parameterPage.getMode();
		source = typePage.getSourceTypes();
		target = typePage.getTargetTypes();

		ignoreNamespace = parameterPage.getIgnoreNamespace();
		mode = parameterPage.getMode();

		final AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
				AlignmentService.class);

		alignment = as.getAlignment();

		IRunnableWithProgress op = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException {

				// step #2 create/find pairs
				monitor.beginTask("Finding pairs between " + source.size() + " source and "
						+ target.size() + " target Types.", IProgressMonitor.UNKNOWN);
				Collection<Pair<TypeEntityDefinition, TypeEntityDefinition>> typePairs = Collections
						.emptyList();
				switch (mode) {
				case 0:
					typePairs = AutoCorrelation.retype(source, target, ignoreNamespace);
					// TODO missing property relation
					break;
				case 1:
					typePairs = AutoCorrelation.retype(source, target, ignoreNamespace);
					break;
				case 2:
					//
					return;
					// TODO missing property relation
				default:
					break;
				}

				// step #3 create cells for type pairs (retype only)
				monitor.beginTask("Creating cells for " + typePairs.size() + " pairs.",
						IProgressMonitor.UNKNOWN);
				Collection<MutableCell> cells = CellCreationHelper
						.createTypeCellRetypeCollectionWithoutDoubles(typePairs, ignoreNamespace,
								false, alignment);

				// log info if some types were already mapped
				if (typePairs.size() != cells.size()) {
					log.info("Already mapped types were ignored: " + typePairs.size()
							+ " pair(s) found but " + cells.size() + " cell(s) created.");
				}

				// step #4 add all cells through AlignmentService
				monitor.beginTask("Adding " + cells.size() + " cells to alignment.", cells.size());

				for (MutableCell cell : cells) {
					as.addCell(cell);
					monitor.worked(1);
				}
				monitor.done();
			}
		};

		try {
			ThreadProgressMonitor.runWithProgressDialog(op, false);
		} catch (Throwable e) {
			log.error("Error on auto correlation.", e);
		}
		// save page configuration ???

		return true;
	}

}
