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
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.Date;
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

				int skipN = 0;
				Boolean skipType = reader.getParameter(CommonSchemaConstants.PARAM_SKIP_N_LINES)
						.as(Boolean.class);

				if (skipType == null) {
					skipN = reader.getParameter(CommonSchemaConstants.PARAM_SKIP_N_LINES)
							.as(Integer.class, 0);
				}
				else if (skipType) {
					skipN = 1;
				}
				else {
					skipN = 0;
				}

				try {
					csvReader = CSVUtil.readFirst(reader);
				} catch (IOException e) {
					log.error("Could not open CSV source", e);
					closed = true;
				}

				if (skipN > 0) {
					try {
						for (int i = 1; i <= skipN; i++) {
							csvReader.readNext();
						}
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

		/**
		 * @param dateString String date
		 * @return Date
		 */
		public String parseDate(String dateString, String dateTime) {
			DateFormat[] dateFormats = { DateFormat.getDateInstance(),
					DateFormat.getDateTimeInstance(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
					new SimpleDateFormat("MM/dd/yy HH:mm:ss"),
					new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"),
					new SimpleDateFormat("MM-dd-yyyy HH:mm:ss"),
					new SimpleDateFormat("MM-dd-yy HH:mm:ss"), new SimpleDateFormat("yyyy-MM-dd"),
					new SimpleDateFormat("dd/MM/yyyy"), new SimpleDateFormat("dd/MMM/yyyy"),
					new SimpleDateFormat("MM/dd/yy"), new SimpleDateFormat("MM/dd/yyyy"),
					new SimpleDateFormat("yyyy/MM/dd"), new SimpleDateFormat("MM-dd-yyyy"),
					new SimpleDateFormat("MM-dd-yy"), new SimpleDateFormat("yy-MM-dd"),
					new SimpleDateFormat("dd-MM-yyyy"), new SimpleDateFormat("yyyy.MM.dd"),
					new SimpleDateFormat("dd.MM.yyyy"), new SimpleDateFormat("MM.dd.yyyy"),
					new SimpleDateFormat("yyyyMMdd"), new SimpleDateFormat("MMMM d, yyyy"),
					new SimpleDateFormat("yy-MM"), new SimpleDateFormat("yyyy-MM"),
					new SimpleDateFormat("MM-yy"), new SimpleDateFormat("MM-yyyy"),
					// Add more date formats as needed
			};

			for (DateFormat dateFormat : dateFormats) {
				try {
					dateFormat.setLenient(false); // Disable lenient parsing
					Date dateCellValue = dateFormat.parse(dateString);

					// Convert java.util.Date to java.time.LocalDateTime
					LocalDateTime localDateTime = dateCellValue.toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDateTime();

					DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTime);

					// Define a DateTimeFormatter with a specific pattern
					if (dateTimeFormatter == null) {
						dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
					}

					// If parsing succeeds, break out of the loop
					// Format LocalDateTime using DateTimeFormatter
					return localDateTime.format(dateTimeFormatter);
				} catch (ParseException e) {
					// Parsing failed with this format, try the next one
				}
			}
			return null;
		}

		private Object convertValue(String part, PropertyDefinition property) {
			if (part == null || part.isEmpty()) {
				// FIXME make this configurable?
				return null;
			}

			try {
				String dateTime = reader.getParameter(CSVUtil.PARAMETER_DATE_FORMAT)
						.as(String.class);
				if (dateTime != null && !part.isEmpty()) {
					part = parseDate(part, dateTime);
				}
			} catch (Exception e) {
				// Handle the exception appropriately, but no need for this
				// trial
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
			String message = MessageFormat.format("Could not find type {0} in source schema",
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
