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

package eu.esdihumboldt.cst.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.test.TestUtil;

/**
 * Transformation example default implementation.
 * 
 * @author Simon Templer
 */
public class TransformationExampleImpl implements TransformationExample {

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
	 * Create a transformation example.
	 * 
	 * @param sourceSchemaLocation the source schema location
	 * @param targetSchemaLocation the target schema location
	 * @param alignmentLocation the alignment location
	 * @param sourceDataLocation the source data location
	 * @param targetDataLocation the target data location
	 * @param targetContainerNamespace the target container namespace
	 * @param targetContainerName the target container name
	 */
	public TransformationExampleImpl(URI sourceSchemaLocation, URI targetSchemaLocation,
			URI alignmentLocation, URI sourceDataLocation, @Nullable URI targetDataLocation,
			@Nullable String targetContainerNamespace, @Nullable String targetContainerName) {
		this.sourceSchemaLocation = sourceSchemaLocation;
		this.targetSchemaLocation = targetSchemaLocation;
		this.sourceDataLocation = sourceDataLocation;
		this.targetDataLocation = targetDataLocation;
		this.alignmentLocation = alignmentLocation;
		this.targetContainerNamespace = targetContainerNamespace;
		this.targetContainerName = targetContainerName;
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
	public Alignment getAlignment() throws Exception {
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
		if (targetDataLocation == null) {
			throw new IllegalStateException("Example has no target data");
		}
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
		if (targetDataLocation == null) {
			throw new IllegalStateException("Example has no target data");
		}
		return new DefaultInputSupplier(targetDataLocation);
	}

}
