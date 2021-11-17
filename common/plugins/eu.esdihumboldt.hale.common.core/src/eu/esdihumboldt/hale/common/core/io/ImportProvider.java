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

package eu.esdihumboldt.hale.common.core.io;

import java.io.IOException;
import java.io.InputStream;

import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Base interface for import providers
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface ImportProvider extends IOProvider {

	/**
	 * The configuration parameter name for the source URI
	 */
	public static final String PARAM_SOURCE = "source";

	/**
	 * The configuration parameter name for the resource identifier
	 */
	public static final String PARAM_RESOURCE_ID = "resourceId";

	/**
	 * Set the import source
	 * 
	 * @param source the source input supplier
	 */
	public void setSource(LocatableInputSupplier<? extends InputStream> source);

	/**
	 * Get the import source
	 * 
	 * @return the source input supplier
	 */
	public LocatableInputSupplier<? extends InputStream> getSource();

	/**
	 * Get the resource identifier. The identifier serves to uniquely identify
	 * the resource represented by the import provider. It is either generated
	 * on execute or loaded from a stored configuration.
	 * 
	 * @return the resource identifier, may be <code>null</code> if the provider
	 *         was not executed yet
	 */
	public String getResourceIdentifier();

	/**
	 * This method executes the I/O provider. It is a special case of
	 * <code>IOProvider.execute(ProgressIndicator progress)</code> and used when
	 * importing multiple schemas or instances. The method is overloaded with
	 * resourceIdentifier because for every file selected by the user a new
	 * resource identifier has to be generated so that every schema
	 * in @link{SchemaServiceImpl} gets a new ID for the selected schema.
	 * 
	 * @param progress the progress indicator, may be <code>null</code>
	 * @param resourceIdentifier identifier of the provided resource. Null if a
	 *            new resource id needs to be generated.
	 * @return the execution report
	 * 
	 * @throws IOProviderConfigurationException if the I/O provider was not
	 *             configured properly
	 * @throws IOException if an I/O operation fails
	 * @throws UnsupportedOperationException if the operation to load multiple
	 *             files is not supported.
	 */
	public IOReport execute(ProgressIndicator progress, String resourceIdentifier)
			throws IOProviderConfigurationException, IOException, UnsupportedOperationException;

}
