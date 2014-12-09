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

package eu.esdihumboldt.hale.ui.autocorrelation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.autocorrelation.AutoCorrelation;
import eu.esdihumboldt.hale.common.autocorrelation.AutoCorrelationComparatorImpl;
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
	private boolean useSuperType;
	private boolean useStructuralRename;
	private int mode;
	private AutoCorrelationComparatorImpl comparator;

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

		source = typePage.getSourceTypes();
		target = typePage.getTargetTypes();

		ignoreNamespace = parameterPage.getIgnoreNamespace();
		useSuperType = parameterPage.getUseSuperType();
		useStructuralRename = parameterPage.getUseStructuralRename();
		mode = parameterPage.getMode();

		comparator = new AutoCorrelationComparatorImpl();

		IRunnableWithProgress op;

		switch (mode) {
		case 0:
			op = retypeAndRename();
			break;
		case 1:
			op = retypeOnly();
			break;
		case 2:
			// rename only, based on "PropertyEntitySelections"
			return false;
			// TODO missing property relation
		default:
			return false;
		}

		try {
			ThreadProgressMonitor.runWithProgressDialog(op, false);
		} catch (Throwable e) {
			log.error("Error on auto correlation.", e);
		}
		// XXX save page configuration ???

		return true;
	}

	/**
	 * Creates an IRunnableWithProgress Object which is configured to create
	 * Retype and Rename cells, based on Type Matching.
	 * 
	 * @return The Progress Runnable to perform the retypeAndRename auto
	 *         correlation mapping
	 */
	private IRunnableWithProgress retypeAndRename() {

		return new IRunnableWithProgress() {

			final AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException {
				try {
					// step #2 create/find pairs
					monitor.beginTask("Finding pairs between " + source.size() + " source and "
							+ target.size() + " target Types.", IProgressMonitor.UNKNOWN);

					Collection<Pair<TypeEntityDefinition, TypeEntityDefinition>> typePairs = Collections
							.emptyList();
					Map<Pair<TypeEntityDefinition, TypeEntityDefinition>, Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>>> propertyPairs = Collections
							.emptyMap();

					typePairs = AutoCorrelation.retype(source, target, ignoreNamespace, comparator);

					if (!typePairs.isEmpty())
						propertyPairs = AutoCorrelation.rename(typePairs, 3, ignoreNamespace,
								useSuperType, useStructuralRename, comparator);

					// ### no matches -> no cells ###
					if (typePairs.size() <= 0 || typePairs.isEmpty()) {
						log.info("No matching types were found.");
						return;
					}

					// step #3 create cells for type pairs (retype only)
					monitor.beginTask("Creating cells for " + typePairs.size() + " pairs.",
							IProgressMonitor.UNKNOWN);

					Collection<MutableCell> cells = CellCreationHelper
							.createRetypeCellsWithoutDoubles(typePairs, ignoreNamespace, false);

					Collection<MutableCell> propCells = new ArrayList<MutableCell>();
					for (Pair<TypeEntityDefinition, TypeEntityDefinition> typePair : propertyPairs
							.keySet()) {
						propCells.addAll(CellCreationHelper.createRenameCellsWithoutDoubles(
								propertyPairs.get(typePair), ignoreNamespace, useStructuralRename));
					}

					// ### no cells -> only dublicates -> no new cells ###
					if (cells.size() <= 0 || cells.isEmpty()) {
						log.info("All found matches are already part of this alignement. No new type cell(s) could be created.");
					}
					// log info if some types were already mapped
					else if (typePairs.size() != cells.size()) {
						log.info("Already mapped types were ignored: " + typePairs.size()
								+ " pair(s) found, but " + cells.size() + " type cell(s) created.");
					}
					// ### no cells -> only dublicates -> no new cells ###
					if (propCells.size() <= 0 || propCells.isEmpty()) {
						log.info("All found matches are already part of this alignement. No new property cell(s) could be created.");
					}
//					// log info if some types were already mapped
//					else if (propertyPairs.values().size() != propCells.size()) {
//						log.info("Already mapped properties were ignored: "
//								+ propertyPairs.values().size() + " pair(s) found, but "
//								+ propCells.size() + " property cell(s) created.");
//					}

					// step #4 add all cells through AlignmentService
					monitor.beginTask(
							"Construct alignment by adding " + cells.size() + propCells.size()
									+ " cell(s) to alignment.", cells.size() + propCells.size());

					DefaultAlignment alignment = new DefaultAlignment();

					// prestep add cell to an constructed default
					// alignment
					for (MutableCell cell : cells) {
						alignment.addCell(cell);
						monitor.worked(1);
					}

					for (MutableCell cell : propCells) {
						alignment.addCell(cell);
						monitor.worked(1);
					}

					monitor.beginTask("Integrating constructed alignment.",
							IProgressMonitor.UNKNOWN);

					as.addOrUpdateAlignment(alignment);

				} finally {

					monitor.done();
				}

			}

		};
	}

	/**
	 * Creates an IRunnableWithProgress Object which is configured to create
	 * Retype cells, based on Type Matching.
	 * 
	 * @return The Progress Runnable to perform the retypeAndRename auto
	 *         correlation mapping
	 */
	private IRunnableWithProgress retypeOnly() {

		return new IRunnableWithProgress() {

			final AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException {
				try {
					// step #2 create/find pairs
					monitor.beginTask("Finding pairs between " + source.size() + " source and "
							+ target.size() + " target Types.", IProgressMonitor.UNKNOWN);

					Collection<Pair<TypeEntityDefinition, TypeEntityDefinition>> typePairs = Collections
							.emptyList();

					typePairs = AutoCorrelation.retype(source, target, ignoreNamespace, comparator);

					// ### no matches -> no cells ###
					if (typePairs.size() <= 0 || typePairs.isEmpty()) {
						log.info("No matching types were found.");
						return;
					}

					// step #3 create cells for type pairs (retype only)
					monitor.beginTask("Creating cells for " + typePairs.size() + " pairs.",
							IProgressMonitor.UNKNOWN);

					Collection<MutableCell> cells = CellCreationHelper
							.createRetypeCellsWithoutDoubles(typePairs, ignoreNamespace, false);

					// ### no cells -> only dublicates -> no new cells ###
					if (cells.size() <= 0 || cells.isEmpty()) {
						log.info("All found matches are already part of this alignement. No new type cell(s) could be created.");
					}
					// log info if some types were already mapped
					else if (typePairs.size() != cells.size()) {
						log.info("Already mapped types were ignored: " + typePairs.size()
								+ " pair(s) found, but " + cells.size() + " cell(s) created.");
					}

					// step #4 add all cells through AlignmentService
					monitor.beginTask("Construct alignment by adding " + cells.size()
							+ " cell(s) to alignment.", cells.size());

					DefaultAlignment alignment = new DefaultAlignment();

					// prestep add cell to an constructed default
					// alignment
					for (MutableCell cell : cells) {
						alignment.addCell(cell);
						monitor.worked(1);
					}

					monitor.beginTask("Integrating constructed alignment.",
							IProgressMonitor.UNKNOWN);

					as.addOrUpdateAlignment(alignment);

				} finally {

					monitor.done();
				}

			}

		};
	}

}
