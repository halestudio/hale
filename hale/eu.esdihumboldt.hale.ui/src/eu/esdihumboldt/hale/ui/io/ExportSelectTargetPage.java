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

package eu.esdihumboldt.hale.ui.io;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.core.io.HaleIO;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.ui.HaleSharedImages;
import eu.esdihumboldt.hale.ui.HaleUIPlugin;
import eu.esdihumboldt.hale.ui.HaleWizardPage;

/**
 * Wizard page that allows selecting a target file
 * @param <W> the concrete I/O wizard type
 * @param <P> the {@link IOProvider} type used in the wizard
 * @param <T> the {@link IOProviderFactory} type used in the wizard
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ExportSelectTargetPage<P extends IOProvider, T extends IOProviderFactory<P>, 
	W extends ExportWizard<P, T>> extends IOWizardPage<P, T, W> {
	
	SaveFileFieldEditor targetFile;

	/**
	 * Default constructor
	 */
	public ExportSelectTargetPage() {
		super("export.selTarget");
		setTitle("Select destination file");
		setDescription("Please select a destination file for the export");
		setImageDescriptor(HaleUIPlugin.getDefault().getImageRegistry().getDescriptor(
				HaleSharedImages.IMG_EXPORT_WIZARD));
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(3, false));
		
		targetFile = new SaveFileFieldEditor("targetFile", "save to", true, page);
		targetFile.setEmptyStringAllowed(false);
	}
	
	/**
	 * @see HaleWizardPage#onShowPage()
	 */
	@Override
	protected void onShowPage() {
		super.onShowPage();
		
		// update file editor with possibly changed file extensions
		targetFile.setFileExtensions(HaleIO.getFileExtensions(
				getWizard().getProviderFactory().getSupportedTypes()));
	}

	/**
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(P provider) {
		File file = new File(targetFile.getStringValue());
		getWizard().setTargetFile(file);
		return true;
	}

}
