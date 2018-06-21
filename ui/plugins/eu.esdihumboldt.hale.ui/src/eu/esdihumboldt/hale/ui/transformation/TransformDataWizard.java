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

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.headless.transform.DefaultTransformationSettings;
import eu.esdihumboldt.hale.common.headless.transform.Transformation;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.MultiInstanceCollection;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.DefaultReportHandler;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Wizard for selecting source data files and a target file for a complete
 * transformation run.
 * 
 * @author Kai Schwierczek
 */
public class TransformDataWizard extends Wizard {

	private final TransformationSinkProxy targetSink;
	private TransformDataWizardSourcePage sourceSelectionPage;

	private final boolean useProjectData;

	/**
	 * Default constructor.
	 * 
	 * @param useProjectData <code>true</code> if the source data registered in
	 *            the project should be used, <code>false</code> if the user
	 *            specify different data
	 */
	public TransformDataWizard(boolean useProjectData) {
		super();

		setWindowTitle("Transform data wizard");
		setForcePreviousAndNextButtons(true);

		targetSink = new TransformationSinkProxy();
		this.useProjectData = useProjectData;
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		sourceSelectionPage = new TransformDataWizardSourcePage(getContainer(), targetSink,
				useProjectData);
		addPage(sourceSelectionPage);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		InstanceCollection rawSources = new MultiInstanceCollection(
				sourceSelectionPage.getSourceInstances());

		// Create a copy of the current alignment to be independent and run
		// everything in a job.
		AlignmentService alignmentService = PlatformUI.getWorkbench()
				.getService(AlignmentService.class);
		Alignment alignment = new DefaultAlignment(alignmentService.getAlignment());

		// schema service for getting source schema
		SchemaService ss = PlatformUI.getWorkbench().getService(SchemaService.class);

		Transformation.transform(rawSources, targetSink, sourceSelectionPage.getExportJob(),
				sourceSelectionPage.getValidationJob(), alignment,
				ss.getSchemas(SchemaSpaceID.SOURCE), DefaultReportHandler.getInstance(),
				HaleUI.getServiceProvider(), null, new DefaultTransformationSettings());

		return true;
	}
}
