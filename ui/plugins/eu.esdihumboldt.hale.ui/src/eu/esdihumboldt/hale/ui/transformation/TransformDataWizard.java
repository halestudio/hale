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

package eu.esdihumboldt.hale.ui.transformation;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.align.transformation.service.TransformationService;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.MultiInstanceCollection;
import eu.esdihumboldt.hale.ui.io.util.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Wizard for selecting source data files and a target file
 * for a complete transformation run.
 *
 * @author Kai Schwierczek
 */
public class TransformDataWizard extends Wizard {
	private final TransformDataInstanceSink targetSink;
	private TransformDataWizardSourcePage sourceSelectionPage;
	/**
	 * Default constructor.
	 */
	public TransformDataWizard() {
		super();

		setWindowTitle("Transform data wizard");
		setForcePreviousAndNextButtons(true);

		// TODO create collection which gets fed by an instance sink
		targetSink = new TransformDataInstanceSink();
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		sourceSelectionPage = new TransformDataWizardSourcePage(getContainer(),
				targetSink.getInstanceCollection());
		addPage(sourceSelectionPage);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final InstanceCollection sources = new MultiInstanceCollection(sourceSelectionPage.getSourceInstances());
		// TODO write sources to temporary database for performance
		// if it contains any type mapping which needs references,
		// i. e. Join or Merge.
		// Maybe do it the other way around and do NOT create a temporary database if 
		// only Retype is used.
		final TransformationService transformationService = (TransformationService) PlatformUI.getWorkbench().getService(TransformationService.class);

		// TODO run as job, associated with job from export provider
		try {
			getContainer().run(true, transformationService.isCancelable(), new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					AlignmentService alignmentService = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
					// copy the alignment to be independent
					transformationService.transform(new DefaultAlignment(alignmentService.getAlignment()),
							sources, targetSink, new ProgressMonitorIndicator(monitor));
				}
			});
		} catch (Exception e) {
			// TODO cleanup/cancel of import/export
			e.printStackTrace();
			return false; // XXX really return false here?
		}

		return true;
	}
}
