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

package eu.esdihumboldt.hale.io.wfs;

import java.io.IOException;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.instance.io.util.GeoInstanceWriterDecorator;
import eu.esdihumboldt.hale.io.gml.writer.XmlWrapper;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;

/**
 * TODO Type description
 * 
 * @param <T> the XML/GML writer type
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public abstract class AbstractWFSWriter<T extends StreamGmlWriter> extends
		GeoInstanceWriterDecorator<T> {

	/**
	 * Name of the parameter specifying the WFS version.
	 */
	public static final String PARAM_WFS_VERSION = "wfsVersion";

	/**
	 * @param internalProvider
	 * @param wfsTransaction
	 */
	public AbstractWFSWriter(T internalProvider) {
		super(internalProvider);
	}

	/**
	 * Set the WFS version.
	 * 
	 * @param version the WFS version
	 */
	public void setWFSVersion(WFSVersion version) {
		setParameter(PARAM_WFS_VERSION, Value.of(version.versionString));
	}

	/**
	 * @return the WFS version to use
	 */
	public WFSVersion getWFSVersion() {
		String versionString = getParameter(PARAM_WFS_VERSION).as(String.class);
		if (versionString == null) {
			return null;
		}
		else
			return WFSVersion.fromString(versionString, null);
	}

	@Override
	public IOReport execute(ProgressIndicator progress) throws IOProviderConfigurationException,
			IOException {
		internalProvider.setDocumentWrapper(createTransaction());

		return super.execute(progress);
	}

	/**
	 * @return
	 */
	protected abstract XmlWrapper createTransaction();

}
