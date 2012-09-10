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
import java.util.Collection;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.util.SaveFileFieldEditor;

/**
 * Wizard page that allows selecting a target file
 * 
 * @param <W> the concrete I/O wizard type
 * @param <P> the {@link IOProvider} type used in the wizard
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ExportSelectTargetPage<P extends ExportProvider, W extends ExportWizard<P>> extends
		IOWizardPage<P, W> {

	private static final ALogger log = ALoggerFactory.getLogger(ExportSelectTargetPage.class);

	/**
	 * The file field editor for the target file
	 */
	private SaveFileFieldEditor targetFile;

	/**
	 * Default constructor
	 */
	public ExportSelectTargetPage() {
		super("export.selTarget");
		setTitle("Export destination");
		setDescription("Please select a destination file for the export");
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
				else if (event.getProperty().equals(FieldEditor.VALUE)) {
					updateContentType();
				}
			}
		});

		updateState();
	}

	/**
	 * Update the content type
	 */
	private void updateContentType() {
		IContentType contentType = null;

		if (getWizard().getProviderFactory() != null && targetFile.isValid()) {
			Collection<IContentType> types = getWizard().getProviderFactory().getSupportedTypes();
			if (types != null && !types.isEmpty()) {
				if (types.size() == 1) {
					// if only one content type is possible for the export we
					// can assume that it is used
					contentType = types.iterator().next();
				}
				else {
					Collection<IContentType> filteredTypes = HaleIO.findContentTypesFor(types,
							null, targetFile.getStringValue());
					if (!filteredTypes.isEmpty()) {
						contentType = filteredTypes.iterator().next();
					}
				}
			}
			else {
				// no supported content types!
				log.error("Export provider {0} doesn't support any content types", getWizard()
						.getProviderFactory().getDisplayName());
			}
		}

		getWizard().setContentType(contentType);
		if (contentType != null) {
			setMessage(contentType.getName(), DialogPage.INFORMATION);
		}
		else {
			setMessage(null);
		}
	}

	private void updateState() {
		updateContentType();

		setPageComplete(targetFile.isValid());
	}

	/**
	 * @see HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

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

	/**
	 * Get the target file name
	 * 
	 * @return the target file name
	 */
	public String getTargetFileName() {
		return targetFile.getStringValue();
	}

}
