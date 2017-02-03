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

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
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

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Resource;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.headless.transform.ExportJob;
import eu.esdihumboldt.hale.common.headless.transform.ValidationJob;
import eu.esdihumboldt.hale.common.instance.io.InstanceIO;
import eu.esdihumboldt.hale.common.instance.io.InstanceValidator;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.DefaultReportHandler;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.io.instance.InstanceExportWizard;
import eu.esdihumboldt.hale.ui.io.instance.InstanceImportWizard;
import eu.esdihumboldt.hale.ui.service.project.ProjectResourcesUtil;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.util.io.ThreadProgressMonitor;
import eu.esdihumboldt.hale.ui.util.wizard.HaleWizardDialog;

/**
 * Page for selection of source data files for the {@link TransformDataWizard}.
 * 
 * @author Kai Schwierczek
 */
public class TransformDataWizardSourcePage extends WizardPage {

	private final InternalInstanceExportWizard exportWizard;
	private final List<InstanceCollection> sourceCollections = new ArrayList<InstanceCollection>();
	private ExportJob exportJob;
	private ValidationJob validationJob;

	private final TransformationSinkProxy targetSink;
	private final boolean useProjectData;

	/**
	 * Creates the transform data wizard page for selecting source data files.
	 * The pages next page is the starting page of the export wizard. For that
	 * it needs the target instance collection.
	 * 
	 * @param container the wizard container
	 * @param targetSink the target sink
	 * @param useProjectData <code>true</code> if the source data registered in
	 *            the project should be used, <code>false</code> if the user
	 *            specify different data
	 */
	public TransformDataWizardSourcePage(IWizardContainer container,
			TransformationSinkProxy targetSink, boolean useProjectData) {
		super("sourceSelection");
		this.targetSink = targetSink;
		this.useProjectData = useProjectData;
		if (useProjectData) {
			setTitle("Source data");
			setDescription(
					"The data sources that are imported into the project and will be transformed");
		}
		else {
			setTitle("Source instance selection");
			setDescription("Add all source data files which you want to transform.");
		}
		setPageComplete(false);

		exportWizard = new InternalInstanceExportWizard();
		exportWizard.setContainer(container);
		exportWizard.setAdvisor(new TransformDataExportAdvisor(targetSink.getInstanceCollection()),
				null);
		// add pages after setting the advisor, because it creates new config
		// pages
		exportWizard.addPages();
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		content.setLayout(GridLayoutFactory.swtDefaults().create());
		final ListViewer listViewer = new ListViewer(content);
		listViewer.getControl()
				.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		if (!useProjectData) {
			Button addButton = new Button(content, SWT.PUSH);
			addButton.setText("Add source file");
			addButton.setLayoutData(
					GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());
			addButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					InstanceImportWizard importWizard = new InstanceImportWizard();

					TransformDataImportAdvisor advisor = new TransformDataImportAdvisor();
					// specifying null as actionId results in no call to
					// ProjectService.rememberIO
					importWizard.setAdvisor(advisor, null);

					if (new HaleWizardDialog(getShell(), importWizard).open() == Dialog.OK) {
						if (advisor.getInstances() != null) {
							sourceCollections.add(advisor.getInstances());
							listViewer.add(advisor.getLocation());
							getContainer().updateButtons();
						}
					}
				}
			});
		}
		else {
			// initialize project source data
			IRunnableWithProgress op = new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Prepare data sources", IProgressMonitor.UNKNOWN);

					ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);

					final List<URI> locations = new ArrayList<>();
					for (Resource resource : ps.getResources()) {
						if (InstanceIO.ACTION_LOAD_SOURCE_DATA.equals(resource.getActionId())) {
							// resource is source data

							IOConfiguration conf = resource.copyConfiguration(true);

							TransformDataImportAdvisor advisor = new TransformDataImportAdvisor();
							ProjectResourcesUtil.executeConfiguration(conf, advisor, false, null);

							if (advisor.getInstances() != null) {
								sourceCollections.add(advisor.getInstances());
								locations.add(advisor.getLocation());
							}
						}
					}

					PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

						@Override
						public void run() {
							for (URI location : locations) {
								listViewer.add(location);
							}
						}
					});

					monitor.done();
				}
			};
			try {
				ThreadProgressMonitor.runWithProgressDialog(op, false);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

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
	public ExportJob getExportJob() {
		return exportJob;
	}

	/**
	 * Returns the validation job.
	 * 
	 * @return the validation job, may be null
	 */
	public ValidationJob getValidationJob() {
		return validationJob;
	}

	/**
	 * InstanceExportWizard with custom performFinish behavior.
	 */
	private class InternalInstanceExportWizard extends InstanceExportWizard {

		@Override
		protected IOReport execute(final IOProvider provider, final IOReporter defaultReporter) {
			// this may get called twice: once for the export, and afterwards
			// another time for the validation
			if (exportJob == null) {
				InstanceWriter writer = (InstanceWriter) provider;

				// init sink
				try {
					targetSink.init(!writer.isPassthrough());
					SchemaService ss = PlatformUI.getWorkbench().getService(SchemaService.class);
					targetSink.setTypes(ss.getSchemas(SchemaSpaceID.TARGET));
				} catch (Exception e) {
					throw new IllegalStateException("Failed to initialize target sink", e);
				}

				// create export job
				exportJob = new ExportJob(targetSink, writer, getAdvisor(),
						DefaultReportHandler.getInstance());
			}
			else if (validationJob == null) {
				final InstanceValidator validator = (InstanceValidator) provider;
				validationJob = new ValidationJob(Arrays.asList(validator),
						DefaultReportHandler.getInstance(), null, HaleUI.getServiceProvider()) {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						/*
						 * Reconfigure validator because when it was created the
						 * writer was not executed yet (and the validation
						 * schemas thus not updated if applicable)
						 */
						// set schemas
						List<? extends Locatable> schemas = getProvider().getValidationSchemas();
						validator.setSchemas(schemas.toArray(new Locatable[schemas.size()]));

						return super.run(monitor);
					}

				};
			}
			else
				throw new IllegalStateException("Unknown calls to export wizard's execute.");

			return null;
		}
	}
}
