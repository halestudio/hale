/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.csv.reader.internal;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;

import org.springframework.core.convert.ConversionService;

import au.com.bytecode.opencsv.CSVReader;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceCollection2;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.PseudoInstanceReference;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.io.csv.reader.CommonSchemaConstants;

/**
 * Instance collection based on a CSV file/stream.
 * 
 * @author Simon Templer
 */
public class CSVInstanceCollection implements InstanceCollection, InstanceCollection2 {

	private static final ALogger log = ALoggerFactory.getLogger(CSVInstanceCollection.class);

	/**
	 * CSV instance iterator.
	 */
	public class CSVIterator implements ResourceIterator<Instance> {

		@SuppressWarnings("unused")
		private int currentLine = -1;

		private boolean closed = false;

		private CSVReader csvReader;

		private String[] nextItem = null;

		@Override
		public boolean hasNext() {
			if (closed) {
				return false;
			}

			proceedToNext();

			return nextItem != null;
		}

		private void proceedToNext() {
			if (closed) {
				return;
			}

			// initialize reader if necessary
			if (csvReader == null) {
				boolean skipFirst = reader.getParameter(CommonSchemaConstants.PARAM_SKIP_FIRST_LINE)
						.as(Boolean.class, false);

				try {
					csvReader = CSVUtil.readFirst(reader);
				} catch (IOException e) {
					log.error("Could not open CSV source", e);
					closed = true;
				}

				if (skipFirst) {
					try {
						csvReader.readNext();
					} catch (IOException e) {
						// close on error
						close(e);
					}
					currentLine++;
				}
			}

			if (nextItem == null) {
				// item was consumed or first item
				try {
					nextItem = csvReader.readNext();
				} catch (IOException e) {
					// close on error
					close(e);
				}
				currentLine++;
			}
		}

		private void close(IOException e) {
			closed = true;
			log.error("Error accessing CSV source", e);
		}

		@Override
		public Instance next() {
			proceedToNext();

			if (nextItem == null) {
				throw new NoSuchElementException();
			}

			MutableInstance instance = new DefaultInstance(type, null);
			try {
				// build instance
				PropertyDefinition[] propAr = type.getChildren()
						.toArray(new PropertyDefinition[type.getChildren().size()]);

				int index = 0;
				for (String part : nextItem) {
					if (index >= propAr.length) {
						// break if line has more columns than the specified
						// type
						log.warn("More data columns encountered than defined in the schema");
						break;
					}
					PropertyDefinition property = propAr[index];

					Object value = convertValue(part, property);

					instance.addProperty(property.getName(), value);
					index++;
				}
			} finally {
				nextItem = null;
			}

			return instance;
		}

		private Object convertValue(String part, PropertyDefinition property) {
			if (part == null || part.isEmpty()) {
				// FIXME make this configurable?
				return null;
			}

			Binding binding = property.getPropertyType().getConstraint(Binding.class);
			try {
				if (!binding.getBinding().equals(String.class)) {

					if (Number.class.isAssignableFrom(binding.getBinding())
							&& decimalPoint != '.') {
						// number binding and we don't have the
						// default decimal point

						// TODO more sophisticated behavior?
						// what about thousands separator char?

						part = part.replace(decimalPoint, '.');
					}

					ConversionService conversionService = HalePlatform
							.getService(ConversionService.class);
					if (conversionService.canConvert(String.class, binding.getBinding())) {
						return conversionService.convert(part, binding.getBinding());
					}
					else {
						throw new IllegalStateException("Conversion not possible!");
					}
				}
			} catch (Exception e) {
				log.error(MessageFormat.format("Cannot convert property value to {0}",
						binding.getBinding().getSimpleName()), e);
			}

			return part;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void close() {
			closed = true;
			if (csvReader != null) {
				try {
					csvReader.close();
				} catch (IOException e) {
					log.debug("Error closing CSV reader", e);
				}
			}
		}

	}

	/**
	 * The original CSV instance reader.
	 */
	protected final CSVInstanceReader reader;

	/**
	 * The schema type of instances read.
	 */
	protected final TypeDefinition type;

	/**
	 * The character used as a decimal point.
	 */
	protected final char decimalPoint;

	private Boolean empty;

	/**
	 * Create a CSV instance collection based on the given CSV instance reader
	 * (because we make use of its configuration).
	 * 
	 * @param csvInstanceReader the CSV instance reader
	 */
	public CSVInstanceCollection(CSVInstanceReader csvInstanceReader) {
		this.reader = csvInstanceReader;

		// Decimal point
		decimalPoint = CSVUtil.getDecimal(reader);

		// Schema type
		String typeName = reader.getParameter(CommonSchemaConstants.PARAM_TYPENAME)
				.as(String.class);
		type = reader.getSourceSchema().getType(QName.valueOf(typeName));
		if (type == null) {
			String message = MessageFormat.format("Could not find type {1} in source schema",
					typeName);
			// can't really continue w/o type
			throw new IllegalStateException(message);
		}
	}

	@Override
	public InstanceReference getReference(Instance instance) {
		// TODO reference by line?
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
		return new CSVIterator();
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
		if (empty != null) {
			return empty;
		}

		try (ResourceIterator<Instance> it = iterator()) {
			empty = !it.hasNext();
		}
		return empty;
	}

	@Override
	public InstanceCollection select(Filter filter) {
		return FilteredInstanceCollection.applyFilter(this, filter);
	}

	@Override
	public boolean supportsFanout() {
		return true;
	}

	@Override
	public Map<TypeDefinition, InstanceCollection> fanout() {
		return Collections.<TypeDefinition, InstanceCollection> singletonMap(type, this);
	}

}
