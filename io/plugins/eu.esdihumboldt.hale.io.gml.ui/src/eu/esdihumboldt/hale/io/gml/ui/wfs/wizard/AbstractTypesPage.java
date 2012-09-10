/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.io.gml.ui.wfs.wizard;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.io.gml.ui.wfs.wizard.capabilities.GetCapabilititiesRetriever;

/**
 * 
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @param <T> the WFS configuration type
 */
public abstract class AbstractTypesPage<T extends WfsConfiguration> extends AbstractWfsPage<T> {

	private final CapabilitiesPage capsPage;

	private String lastUrl = null;

	/**
	 * Constructor
	 * 
	 * @param configuration the WFS configuration
	 * @param capsPage the capabilities page
	 * @param pageName the page name
	 */
	public AbstractTypesPage(T configuration, CapabilitiesPage capsPage, String pageName) {
		super(configuration, pageName);

		this.capsPage = capsPage;
	}

	/**
	 * @see AbstractWfsPage#onShowPage()
	 */
	@Override
	protected void onShowPage() {
		final String url = capsPage.getCapabilitiesURL();

		if (lastUrl == null || !lastUrl.equals(url)) {
			final Display display = Display.getCurrent();

			try {
				getContainer().run(true, true, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException,
							InterruptedException {
						try {
							final List<FeatureType> types = GetCapabilititiesRetriever
									.readFeatureTypes(url, monitor);
							display.asyncExec(new Runnable() {

								@Override
								public void run() {
									update(types);
									lastUrl = url;
								}

							});
						} catch (IOException e) {
							setErrorMessage("Error getting feature types: " + e.getLocalizedMessage()); //$NON-NLS-1$
						}
					}
				});
			} catch (Throwable e) {
				setErrorMessage("Error retrieving feature types: " + e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}

	/**
	 * @see AbstractWfsPage#updateConfiguration(WfsConfiguration)
	 */
	@Override
	public boolean updateConfiguration(WfsConfiguration configuration) {
		List<FeatureType> selection = getSelection();

		if (selection == null || selection.isEmpty()) {
			return false;
		}
		else {
			configuration.setFeatureTypes(selection);
			return true;
		}
	}

	/**
	 * Get the selected feature types
	 * 
	 * @return the selected feature types
	 */
	protected abstract List<FeatureType> getSelection();

	/**
	 * Update the page with the given feature types
	 * 
	 * @param types the list of feature types
	 */
	protected abstract void update(List<FeatureType> types);
}
