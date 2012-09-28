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

package eu.esdihumboldt.hale.ui.transformation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.ui.io.instance.InstanceExportWizard;
import eu.esdihumboldt.hale.ui.io.instance.InstanceImportWizard;
import eu.esdihumboldt.hale.ui.io.util.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.ui.service.report.ReportService;
import eu.esdihumboldt.hale.ui.util.wizard.HaleWizardDialog;

/**
 * Page for selection of source data files for the {@link TransformDataWizard}.
 * 
 * @author Kai Schwierczek
 */
public class TransformDataWizardSourcePage extends WizardPage {

	private static final ALogger log = ALoggerFactory
			.getLogger(TransformDataWizardSourcePage.class);

	private final InternalInstanceExportWizard exportWizard;
	private final List<InstanceCollection> sourceCollections = new ArrayList<InstanceCollection>();
	private Job exportJob;
	private Job validationJob;

	private final TransformDataInstanceSink targetSink;

	/**
	 * Creates the transform data wizard page for selecting source data files.
	 * The pages next page is the starting page of the export wizard. For that
	 * it needs the target instance collection.
	 * 
	 * @param container the wizard container
	 * @param targetSink the target sink
	 */
	public TransformDataWizardSourcePage(IWizardContainer container,
			TransformDataInstanceSink targetSink) {
		super("sourceSelection");
		this.targetSink = targetSink;
		setTitle("Source instance selection");
		setDescription("Add all source data files which you want to transform.");
		setPageComplete(false);

		exportWizard = new InternalInstanceExportWizard();
		exportWizard.setContainer(container);
		exportWizard.addPages();
		exportWizard.setAdvisor(new TransformDataExportAdvisor(targetSink.getInstanceCollection()),
				null);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		content.setLayout(GridLayoutFactory.swtDefaults().create());
		final ListViewer listViewer = new ListViewer(content);
		listViewer.getControl().setLayoutData(
				GridDataFactory.fillDefaults().grab(true, true).create());

		Button addButton = new Button(content, SWT.PUSH);
		addButton.setText("Add source file");
		addButton.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());
		addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				InstanceImportWizard importWizard = new InstanceImportWizard();

				TransformDataImportAdvisor advisor = new TransformDataImportAdvisor();
				// specifying null as actionId results in no call to
				// ProjectService.rememberIO
				importWizard.setAdvisor(advisor, null);

				new HaleWizardDialog(getShell(), importWizard).open();

				if (advisor.getInstances() != null) {
					sourceCollections.add(advisor.getInstances());
					listViewer.add(advisor.getLocation());
					getContainer().updateButtons();
				}
			}
		});

		setControl(content);
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	@Override
	public IWizardPage getNextPage() {
		return exportWizard.getStartingPage();
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {
		return !sourceCollections.isEmpty();
	}

	/**
	 * Returns all selected source instance collections.
	 * 
	 * @return all selected source instance collections
	 */
	public List<InstanceCollection> getSourceInstances() {
		return sourceCollections;
	}

	/**
	 * Returns the export job.
	 * 
	 * @return the export job
	 */
	public Job getExportJob() {
		return exportJob;
	}

	/**
	 * Returns the validation job.
	 * 
	 * @return the validation job, may be null
	 */
	public Job getValidationJob() {
		return validationJob;
	}

	/**
	 * InstanceExportWizard with custom performFinish behavior.
	 */
	private class InternalInstanceExportWizard extends InstanceExportWizard {

		/**
		 * @see eu.esdihumboldt.hale.ui.io.IOWizard#execute(eu.esdihumboldt.hale.common.core.io.IOProvider,
		 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
		 */
		@Override
		protected IOReport execute(final IOProvider provider, final IOReporter defaultReporter) {
			// this may get called twice: once for the export, and afterwards
			// another time for the validation
			if (exportJob == null) {
				exportJob = new Job("Export") {

					/**
					 * @see org.eclipse.core.runtime.jobs.Job#canceling()
					 */
					@Override
					protected void canceling() {
						// must cancel sink, otherwise it may be blocking
						targetSink.done(true);
					}

					/**
					 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
					 */
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						defaultReporter.setSuccess(false);
						IOReport report = defaultReporter;
						try {
							ATransaction trans = log.begin(defaultReporter.getTaskName());
							try {
								IOReport result = provider.execute(new ProgressMonitorIndicator(
										monitor));
								if (result != null) {
									report = result;
								}
								else {
									defaultReporter.setSuccess(true);
								}
							} catch (Throwable e) {
								defaultReporter
										.error(new IOMessageImpl(e.getLocalizedMessage(), e));
							} finally {
								trans.end();
							}
						} catch (Throwable e) {
							defaultReporter.error(new IOMessageImpl(e.getLocalizedMessage(), e));
						}

						if (monitor.isCanceled())
							return Status.CANCEL_STATUS;

						// add report to report service
						ReportService repService = (ReportService) PlatformUI.getWorkbench()
								.getService(ReportService.class);
						repService.addReport(report);

						// show message to user
						if (report.isSuccess()) {
							// no message, we rely on the report being
							// shown/processed

							// let advisor handle results
							getAdvisor().handleResults(getProvider());
						}
						else {
							// error message
							log.userError(report.getSummary());
						}

						// XXX return something depending on report success?
						return Status.OK_STATUS;
					}
				};
			}
			else if (validationJob == null) {
				validationJob = new Job("Validation") {

					/**
					 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
					 */
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						defaultReporter.setSuccess(false);
						IOReport report = defaultReporter;
						try {
							ATransaction trans = log.begin(defaultReporter.getTaskName());
							try {
								IOReport result = provider.execute(new ProgressMonitorIndicator(
										monitor));
								if (result != null) {
									report = result;
								}
								else {
									defaultReporter.setSuccess(true);
								}
							} catch (Throwable e) {
								defaultReporter
										.error(new IOMessageImpl(e.getLocalizedMessage(), e));
							} finally {
								trans.end();
							}
						} catch (Throwable e) {
							defaultReporter.error(new IOMessageImpl(e.getLocalizedMessage(), e));
						}

						if (monitor.isCanceled())
							return Status.CANCEL_STATUS;

						// add report to report service
						ReportService repService = (ReportService) PlatformUI.getWorkbench()
								.getService(ReportService.class);
						repService.addReport(report);

						// show message to user
						if (report.isSuccess()) {
							// info message
							log.userInfo(report.getSummary());
						}
						else {
							// error message
							log.userError(report.getSummary());
						}

						// XXX return something depending on report success?
						return Status.OK_STATUS;
					}
				};
			}
			else
				throw new IllegalStateException("Unknown calls to export wizard's execute.");

			return null;
		}
	}
}
