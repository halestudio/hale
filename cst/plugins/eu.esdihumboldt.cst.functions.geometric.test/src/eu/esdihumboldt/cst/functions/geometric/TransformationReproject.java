package eu.esdihumboldt.cst.functions.geometric;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import eu.esdihumboldt.cst.test.TransformationExample;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.PseudoInstanceReference;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.test.TestUtil;

public class TransformationReproject implements TransformationExample{

	private String sourceData;
	private String sourceSchema;
	private String targetSchema;
	private String alignment;

	public TransformationReproject(TestDataConfiguration configuration) {
		this.sourceData = configuration.getSourceData();
		this.sourceSchema = configuration.getSourceSchema();
		this.targetSchema = configuration.getTargetSchema();
		this.alignment = configuration.getAlignment();
	}

	private URI toLocalURI(String location) throws URISyntaxException {
		return this.getClass().getResource(location).toURI();
	}

	@Override
	public Schema getSourceSchema() throws Exception {
		return TestUtil.loadSchema(toLocalURI(sourceSchema));
	}

	@Override
	public Schema getTargetSchema() throws Exception {
		return TestUtil.loadSchema(toLocalURI(targetSchema));
	}

	@Override
	public Alignment getAlignment() throws Exception {
		return TestUtil.loadAlignment(toLocalURI(alignment), getSourceSchema(), getTargetSchema());
	}

	@Override
	public String getTargetContainerNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTargetContainerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InstanceCollection getSourceInstances() throws Exception {
		return new TestInstanceCollection(this.sourceSchema, this.sourceData);
	}

	@Override
	public InstanceCollection getTargetInstances() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocatableInputSupplier<? extends InputStream> getSourceSchemaInput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocatableInputSupplier<? extends InputStream> getAlignmentInput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocatableInputSupplier<? extends InputStream> getTargetSchemaInput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocatableInputSupplier<? extends InputStream> getSourceDataInput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocatableInputSupplier<? extends InputStream> getTargetDataInput() {
		// TODO Auto-generated method stub
		return null;
	}


	private class TestInstanceCollection implements InstanceCollection{

		private String schema;
		private String data;

		public TestInstanceCollection(String schema, String data) {
			this.schema = schema;
			this.data = data;
		}
		
		@Override
		public InstanceReference getReference(Instance instance) {
			return new PseudoInstanceReference(instance);
		}

		@Override
		public Instance getInstance(InstanceReference reference) {
			if (reference instanceof PseudoInstanceReference) {
				return ((PseudoInstanceReference) reference).getInstance();
			}
			return null;
		}

		@Override
		public ResourceIterator<Instance> iterator() {
			try {
				return new TestInstanceIterator(schema, data);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public boolean hasSize() {
			return false;
		}

		@Override
		public int size() {
			return UNKNOWN_SIZE;
		}

		@Override
		public boolean isEmpty() {
			ResourceIterator<Instance> it = iterator();
			try {
				return !it.hasNext();
			} finally {
				it.close();
			}
		}

		@Override
		public InstanceCollection select(Filter filter) {
			return FilteredInstanceCollection.applyFilter(this, filter);
		}

	}


	private class TestInstanceIterator implements InstanceIterator {

		private final ResourceIterator<Instance> currentIterator;

		public TestInstanceIterator(String schema, String data) throws Exception {
			super();
			InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
					getClass().getResource(schema).toURI(),
					getClass().getResource(data).toURI());   
			this.currentIterator = instances.iterator();
		}

		@Override
		public boolean hasNext() {
			return currentIterator.hasNext();
		}

		@Override
		public Instance next() {
			Instance instance = currentIterator.next();

			if (instance != null) {
				return instance;
			}
			else {
				throw new IllegalStateException();
			}
		}

		@Override
		public void close() {
			this.currentIterator.close();
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub

		}

		@Override
		public TypeDefinition typePeek() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean supportsTypePeek() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void skip() {
			// TODO Auto-generated method stub

		}
	}
}
