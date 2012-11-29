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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.test.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import eu.esdihumboldt.cst.test.TransformationExample;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.test.TestUtil;

/**
 * Transformation example contained in the CST test bundle.
 * 
 * @author Simon Templer
 */
public class InternalExample implements TransformationExample {

	private Schema sourceSchema;
	private Schema targetSchema;
	private Alignment alignment;
	private InstanceCollection sourceInstances;
	private InstanceCollection targetInstances;
	private final URI sourceSchemaLocation;
	private final URI alignmentLocation;
	private final URI targetSchemaLocation;
	private final URI sourceDataLocation;
	private final URI targetDataLocation;
	private final String targetContainerNamespace;
	private final String targetContainerName;

	/**
	 * Create a transformation example. All provided locations are specific to
	 * this bundle. Relative locations refer to the {@link InternalExample}
	 * class, absolute locations start with a <code>/</code>.
	 * 
	 * @param sourceSchemaLocation the source schema location
	 * @param targetSchemaLocation the target schema location
	 * @param alignmentLocation the alignment location
	 * @param sourceDataLocation the source data location
	 * @param targetDataLocation the target data location
	 * @param targetContainerNamespace the target container namespace
	 * @param targetContainerName the target container name
	 * @throws URISyntaxException if a location was invalid
	 */
	public InternalExample(String sourceSchemaLocation, String targetSchemaLocation,
			String alignmentLocation, String sourceDataLocation, String targetDataLocation,
			String targetContainerNamespace, String targetContainerName) throws URISyntaxException {
		this.sourceSchemaLocation = toLocalURI(sourceSchemaLocation);
		this.targetSchemaLocation = toLocalURI(targetSchemaLocation);
		this.sourceDataLocation = toLocalURI(sourceDataLocation);
		this.targetDataLocation = toLocalURI(targetDataLocation);
		this.alignmentLocation = toLocalURI(alignmentLocation);
		this.targetContainerNamespace = targetContainerNamespace;
		this.targetContainerName = targetContainerName;
	}

	/**
	 * Returns an URI for the given location: <br>
	 * <code>getClass().getResource(location).toURI()</code>
	 * 
	 * @param location the location
	 * @return an URI for the location
	 * @throws URISyntaxException if toURI throws an exception
	 */
	private URI toLocalURI(String location) throws URISyntaxException {
		return InternalExample.class.getResource(location).toURI();
	}

	@Override
	public String getTargetContainerNamespace() {
		return targetContainerNamespace;
	}

	@Override
	public String getTargetContainerName() {
		return targetContainerName;
	}

	@Override
	public Schema getSourceSchema() throws IOProviderConfigurationException, IOException {
		if (sourceSchema == null) {
			sourceSchema = TestUtil.loadSchema(sourceSchemaLocation);
		}
		return sourceSchema;
	}

	@Override
	public Schema getTargetSchema() throws IOProviderConfigurationException, IOException {
		if (targetSchema == null) {
			targetSchema = TestUtil.loadSchema(targetSchemaLocation);
		}
		return targetSchema;
	}

	@Override
	public Alignment getAlignment() throws MarshalException, ValidationException, MappingException,
			IOException, IOProviderConfigurationException {
		if (alignment == null) {
			alignment = TestUtil.loadAlignment(alignmentLocation, getSourceSchema(),
					getTargetSchema());
		}
		return alignment;
	}

	@Override
	public InstanceCollection getSourceInstances() throws IOProviderConfigurationException,
			IOException {
		if (sourceInstances == null) {
			sourceInstances = TestUtil.loadInstances(sourceDataLocation, getSourceSchema());
		}
		return sourceInstances;
	}

	@Override
	public InstanceCollection getTargetInstances() throws IOProviderConfigurationException,
			IOException {
		if (targetInstances == null) {
			targetInstances = TestUtil.loadInstances(targetDataLocation, getTargetSchema());
		}
		return targetInstances;
	}

	@Override
	public LocatableInputSupplier<? extends InputStream> getSourceSchemaInput() {
		return new DefaultInputSupplier(sourceSchemaLocation);
	}

	@Override
	public LocatableInputSupplier<? extends InputStream> getAlignmentInput() {
		return new DefaultInputSupplier(alignmentLocation);
	}

	@Override
	public LocatableInputSupplier<? extends InputStream> getTargetSchemaInput() {
		return new DefaultInputSupplier(targetSchemaLocation);
	}

	@Override
	public LocatableInputSupplier<? extends InputStream> getSourceDataInput() {
		return new DefaultInputSupplier(sourceDataLocation);
	}

	@Override
	public LocatableInputSupplier<? extends InputStream> getTargetDataInput() {
		return new DefaultInputSupplier(targetDataLocation);
	}

}
