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

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.core.io.ExportProvider;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.util.SaveFileFieldEditor;

/**
 * Wizard page that allows selecting a target file
 * @param <W> the concrete I/O wizard type
 * @param <P> the {@link IOProvider} type used in the wizard
 * @param <T> the {@link IOProviderFactory} type used in the wizard
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ExportSelectTargetPage<P extends ExportProvider, T extends IOProviderFactory<P>, 
	W extends ExportWizard<P, T>> extends IOWizardPage<P, T, W> {
	
	SaveFileFieldEditor targetFile;

	/**
	 * Default constructor
	 */
	public ExportSelectTargetPage() {
		super("export.selTarget");
		setTitle("Export destination");
		setDescription("Please select a destination file for the export");
//		setImageDescriptor(HaleUIPlugin.getDefault().getImageRegistry().getDescriptor(
//				HaleSharedImages.IMG_EXPORT_WIZARD));
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(3, false));
		
		targetFile = new SaveFileFieldEditor("targetFile", "Target file:", true,
				FileFieldEditor.VALIDATE_ON_KEY_STROKE, page);
		targetFile.setEmptyStringAllowed(false);
		targetFile.setPage(this);
		targetFile.setPropertyChangeListener(new IPropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					updateState();
				}
			}
		});
		
		updateState();
	}
	
	private void updateState() {
		setPageComplete(targetFile.isValid());
	}

	/**
	 * @see HaleWizardPage#onShowPage()
	 */
	@Override
	protected void onShowPage() {
		super.onShowPage();
		
		// update file editor with possibly changed file extensions
		targetFile.setContentTypes(getWizard().getProviderFactory().getSupportedTypes());
	}

	/**
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(P provider) {
		File file = new File(targetFile.getStringValue());
		provider.setTarget(new FileIOSupplier(file));
		return true;
	}

}
