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

package eu.esdihumboldt.hale.ui.instancevalidation.status;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationReport;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.ui.instancevalidation.InstanceValidationUIPlugin;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceAdapter;
import eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationListener;
import eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationService;
import eu.esdihumboldt.hale.ui.views.properties.handler.OpenPropertiesHandler;
import eu.esdihumboldt.hale.ui.views.report.ReportList;

/**
 * Action for instance validation status. On click shows the latest report, and
 * the icon shows the current status.
 * 
 * @author Kai Schwierczek
 */
public class InstanceValidationStatusAction extends Action {

//	private Image noReportBaseImage;
	private ImageDescriptor noReportDescriptor;
	private ImageDescriptor reportOkDescriptor;
	private ImageDescriptor reportWarningsDescriptor;
	private ImageDescriptor reportErrorsDescriptor;

	// the current report
	private InstanceValidationReport report;

	/**
	 * Constructor.
	 */
	public InstanceValidationStatusAction() {
		super("Open instance validation report");

		createImageDescriptors();
		createListeners();

		setDisabledImageDescriptor(noReportDescriptor);
		updateStatus();
	}

	@Override
	public void run() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		try {
			// show report view, select report
			ReportList reportView = (ReportList) window.getActivePage().showView(ReportList.ID);
			reportView.selectReport(report);

			// and show properties view
			OpenPropertiesHandler.unpinAndOpenPropertiesView(window);
		} catch (PartInitException e) {
			// if it's not there we cannot do anything
		}
	}

	/**
	 * Creates needed {@link ImageDescriptor}s.
	 */
	private void createImageDescriptors() {
		// load images
		noReportDescriptor = InstanceValidationUIPlugin
				.getImageDescriptor("icons/instance_validation_disabled.gif");
		Image noReportBaseImage = InstanceValidationUIPlugin.getDefault().getImageRegistry()
				.get(InstanceValidationUIPlugin.IMG_INSTANCE_VALIDATION);

		reportOkDescriptor = new DecorationOverlayIcon(noReportBaseImage,
				InstanceValidationUIPlugin.getImageDescriptor("icons/signed_yes_ovr.gif"),
				IDecoration.BOTTOM_LEFT);
		reportWarningsDescriptor = new DecorationOverlayIcon(noReportBaseImage,
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
						ISharedImages.IMG_DEC_FIELD_WARNING),
				IDecoration.BOTTOM_LEFT);
		reportErrorsDescriptor = new DecorationOverlayIcon(noReportBaseImage,
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
						ISharedImages.IMG_DEC_FIELD_ERROR),
				IDecoration.BOTTOM_LEFT);
	}

	/**
	 * Registers needed listeners.
	 */
	private void createListeners() {
		InstanceService is = PlatformUI.getWorkbench().getService(InstanceService.class);
		is.addListener(new InstanceServiceAdapter() {

			@Override
			public void datasetAboutToChange(DataSet type) {
				report = null;
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						updateStatus();
					}
				});
			}
		});

		final InstanceValidationService ivs = PlatformUI.getWorkbench()
				.getService(InstanceValidationService.class);
		ivs.addListener(new InstanceValidationListener() {

			@Override
			public void instancesValidated(InstanceValidationReport report) {
				InstanceValidationStatusAction.this.report = report;
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						updateStatus();
					}
				});
			}

			@Override
			public void validationEnabledChange() {
				// don't care
			}
		});
	}

	/**
	 * Updates this action according to the current report.
	 */
	private void updateStatus() {
		if (report == null) {
			setToolTipText("Currently there is no validation report available.");
			setImageDescriptor(noReportDescriptor);
			setEnabled(false);
		}
		else {
			ImageDescriptor image;
			String toolTip;
			if (!report.getErrors().isEmpty()) {
				image = reportErrorsDescriptor;
				toolTip = "Instance validation finished with errors.";
			}
			else if (!report.getWarnings().isEmpty()) {
				image = reportWarningsDescriptor;
				toolTip = "Instance validation finished with warnings.";
			}
			else {
				image = reportOkDescriptor;
				toolTip = "Instance validation finished without warnings or errors.";
			}
			setToolTipText(toolTip);
			setImageDescriptor(image);
			setEnabled(true);
		}
	}
}
