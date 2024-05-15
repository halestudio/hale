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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import com.google.common.base.Joiner;

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.instance.io.InstanceIO;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.io.wfs.capabilities.BBox;
import eu.esdihumboldt.hale.io.wfs.ui.AbstractWFSSource;
import eu.esdihumboldt.hale.io.wfs.ui.KVPUtil;
import eu.esdihumboldt.hale.ui.io.IOWizard;
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
		WFSGetFeatureWizard wizard = new WFSGetFeatureWizard(config, getSchemaSpace());
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
				KVPUtil.addTypeNameParameter(builder, result.getTypeNames(), result.getVersion());
			}

			// BBOX
			if (result.getBbox() != null) {
				BBox bb = result.getBbox();
				List<String> vals = new ArrayList<>(5);
				vals.add(Double.toString(bb.getX1()));
				vals.add(Double.toString(bb.getY1()));
				vals.add(Double.toString(bb.getX2()));
				vals.add(Double.toString(bb.getY2()));
				String crs = result.getBboxCrsUri();
				if (crs != null && !crs.isEmpty()) {
					vals.add(crs);
				}
				else {
					// if no CRS is provided this may be a problem, because
					// default behavior is different for WFS 1.1 and WFS 2.0
					// WFS 1.1: WGS 84
					// WFS 2.0: Service default CRS
				}

				builder.addParameter("BBOX", Joiner.on(',').join(vals));
			}

			// XXX what about other parameters? e.g.
			// FILTER (cannot be used with BBOX)

			// MAXFEATURES (WFS 1) / COUNT (WFS 2)
			if (result.getMaxFeatures() != null) {
				switch (result.getVersion()) {
				case V1_1_0:
					builder.addParameter("MAXFEATURES", String.valueOf(result.getMaxFeatures()));
					break;
				case V2_0_0:
				case V2_0_2:
				default:
					builder.addParameter("COUNT", String.valueOf(result.getMaxFeatures()));
					break;
				}
			}

			if (result.getResolveDepth() != null && !result.getResolveDepth().isEmpty()) {
				builder.addParameter("resolveDepth", result.getResolveDepth());
			}

			try {
				sourceURL.setStringValue(builder.build().toString());
				getPage().setErrorMessage(null);
			} catch (URISyntaxException e) {
				getPage().setErrorMessage(e.getLocalizedMessage());
			}
		}
	}

	/**
	 * @return the schema space of the associated action
	 */
	@Nullable
	protected SchemaSpaceID getSchemaSpace() {
		String actionID = getActionId();
		if (actionID != null) {
			switch (actionID) {
			case InstanceIO.ACTION_LOAD_SOURCE_DATA:
				return SchemaSpaceID.SOURCE;
			}
		}
		return null;
	}

	/**
	 * @return the ID of the I/O action in which context the source is used
	 */
	@Nullable
	protected String getActionId() {
		// get the parent wizard
		IWizard wizard = getPage().getWizard();

		if (wizard instanceof IOWizard<?>) {
			return ((IOWizard<?>) wizard).getActionId();
		}
		return null;
	}

	@Override
	protected String getWFSRequestValue() {
		return "GetFeature";
	}

	@Override
	protected String getCaption() {
		return "WFS GetFeature KVP request";
	}

}
