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
package eu.esdihumboldt.hale.io.shp.reader.internal;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.geotools.data.DataStore;
import org.geotools.data.FileDataStoreFinder;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceReader;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.shp.ShapefileConstants;
import eu.esdihumboldt.hale.io.shp.internal.Messages;

/**
 * Reads instances from a shapefile.
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public class ShapeInstanceReader extends AbstractInstanceReader implements ShapefileConstants {

	private ShapesInstanceCollection instances;

	/**
	 * Default constructor.
	 */
	public ShapeInstanceReader() {
		super();

		addSupportedParameter(PARAM_TYPENAME);
	}

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin(Messages.getString("ShapeSchemaProvider.1"), ProgressIndicator.UNKNOWN); //$NON-NLS-1$

//		DataStore store = new ShapefileDataStoreFactory().createDataStore(location.toURL());
		DataStore store = FileDataStoreFinder.getDataStore(getSource().getLocation().toURL());

		progress.setCurrentTask("Extracting shape instances");

		String typename = getParameter(PARAM_TYPENAME);
		TypeDefinition defaultType = null;
		if (typename != null && !typename.isEmpty()) {
			try {
				defaultType = getSourceSchema().getType(QName.valueOf(typename));
			} catch (Exception e) {
				// ignore
				// TODO report?
			}
		}
		instances = new ShapesInstanceCollection(store, defaultType, getSourceSchema(),
				getCrsProvider());

		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return ShapefileConstants.DEFAULT_TYPE_NAME;
	}

	/**
	 * @see InstanceReader#getInstances()
	 */
	@Override
	public InstanceCollection getInstances() {
		return instances;
	}

}
