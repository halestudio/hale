/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.wfs.ui.getfeature;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.io.wfs.ui.AbstractWFSSource;
import eu.esdihumboldt.hale.io.wfs.ui.describefeature.WFSDescribeFeatureSource;
import eu.esdihumboldt.hale.ui.util.io.URIFieldEditor;
import eu.esdihumboldt.hale.ui.util.wizard.HaleWizardDialog;

/**
 * Source for loading a schema from a WFS.
 * 
 * @author Simon Templer
 */
public class WFSGetFeatureSource extends AbstractWFSSource<ImportProvider> {

	@Override
	protected void determineSource(URIFieldEditor sourceURL) {
		WFSGetFeatureConfig config = new WFSGetFeatureConfig();
		WFSGetFeatureWizard wizard = new WFSGetFeatureWizard(config);
		HaleWizardDialog dialog = new HaleWizardDialog(Display.getCurrent().getActiveShell(),
				wizard);

		if (dialog.open() == WizardDialog.OK) {
			WFSGetFeatureConfig result = wizard.getConfiguration();

			// create URL
			URIBuilder builder = new URIBuilder(result.getGetFeatureUri());
			// add fixed parameters
			builder.addParameter("SERVICE", "WFS");
			builder.addParameter("VERSION", result.getVersion().toString());
			builder.addParameter("REQUEST", "GetFeature");
			// specify type names
			if (!result.getTypeNames().isEmpty()) {
				WFSDescribeFeatureSource.addTypeNameParameter(builder, result.getTypeNames());
			}

			// XXX what about other parameters? e.g.
			// BBOX
			// FILTER
			// MAXFEATURES

			try {
				sourceURL.setStringValue(builder.build().toASCIIString());
				getPage().setErrorMessage(null);
			} catch (URISyntaxException e) {
				getPage().setErrorMessage(e.getLocalizedMessage());
			}
		}
	}

	@Override
	protected String getCaption() {
		return "WFS GetFeature KVP request";
	}

}
