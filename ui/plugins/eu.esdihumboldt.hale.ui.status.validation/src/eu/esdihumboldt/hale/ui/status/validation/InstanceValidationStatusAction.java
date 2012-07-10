package eu.esdihumboldt.hale.ui.status.validation;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instancevalidator.report.InstanceValidationReport;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceAdapter;
import eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationListener;
import eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationService;
import eu.esdihumboldt.hale.ui.views.report.ReportList;

/**
 * Action for instance validation status. On click shows the latest report,
 * and the icon shows the current status.
 * 
 * Must be {@link #dispose()}d!
 *
 * @author Kai Schwierczek
 */
public class InstanceValidationStatusAction extends Action {
	private Image noReportBaseImage;
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
		super();

		createImageDescriptors();
		createListeners();

		setText(null);
		setDisabledImageDescriptor(noReportDescriptor);
		updateStatus();
	}

	@Override
	public void run() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			// show report view, select report
			ReportList reportView = (ReportList) page.showView(ReportList.ID);
			reportView.selectReport(report);

			// and show properties view
			page.showView(IPageLayout.ID_PROP_SHEET);
		} catch (PartInitException e) {
			// if it's not there we cannot do anything
		}
	}

	/**
	 * Disposes of used images.
	 */
	public void dispose() {
		noReportBaseImage.dispose();
	}

	/**
	 * Creates needed {@link ImageDescriptor}s.
	 */
	private void createImageDescriptors() {
		// load images
		noReportDescriptor = InstanceValidationUIPlugin.getImageDescriptor("icons/instance_validation.gif");
		noReportBaseImage = noReportDescriptor.createImage();

		reportOkDescriptor = new DecorationOverlayIcon(noReportBaseImage, InstanceValidationUIPlugin.getImageDescriptor("icons/signed_yes_ovr.gif"), IDecoration.BOTTOM_LEFT);
		reportWarningsDescriptor = new DecorationOverlayIcon(noReportBaseImage, PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_DEC_FIELD_WARNING), IDecoration.BOTTOM_LEFT);
		reportErrorsDescriptor = new DecorationOverlayIcon(noReportBaseImage, PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_DEC_FIELD_ERROR), IDecoration.BOTTOM_LEFT);
	}

	/**
	 * Registers needed listeners.
	 */
	private void createListeners() {
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
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
	
		InstanceValidationService ivs = (InstanceValidationService) PlatformUI.getWorkbench().getService(InstanceValidationService.class);
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
		} else {
			ImageDescriptor image;
			String toolTip;
			if (!report.getErrors().isEmpty()){
				image = reportErrorsDescriptor;
				toolTip = "Instance validation finished with errors.";
			} else if (!report.getWarnings().isEmpty()) {
				image = reportWarningsDescriptor;
				toolTip = "Instance validation finished with warnings.";
			} else {
				image = reportOkDescriptor;
				toolTip = "Instance validation finished without warnings or errors.";
			}
			setToolTipText(toolTip);
			setImageDescriptor(image);
			setEnabled(true);
		}
	}
}
