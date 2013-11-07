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

package eu.esdihumboldt.hale.io.gml.reader.internal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.InstanceResolver;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.IndexInstanceReference;
import eu.esdihumboldt.hale.common.schema.Classification;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.io.gml.reader.internal.instance.StreamGmlHelper;
import eu.esdihumboldt.hale.io.gml.reader.internal.instance.StreamGmlInstance;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

/**
 * Instance collection based on an XML/GML input stream
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class GmlInstanceCollection implements InstanceCollection {

	/**
	 * Iterates over {@link Instance}s in an XML/GML stream
	 */
	public class GmlInstanceIterator implements InstanceIterator {

		private final InputStream in;

		private final XMLStreamReader reader;

		/**
		 * Element names associated with type definitions
		 */
		private Map<QName, TypeDefinition> allowedTypes;

		private TypeDefinition nextType;

		/**
		 * The index in the stream for the element returned next with
		 * {@link #next()}
		 */
		private int elementIndex = 0;

		/**
		 * States if the root element has been encountered yet.
		 */
		private boolean rootEncountered = false;

		/**
		 * Default constructor
		 */
		public GmlInstanceIterator() {
			super();

			nextType = null;

			try {
				in = new BufferedInputStream(source.getInput());
				reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
			} catch (Throwable e) {
				throw new IllegalStateException("Could not open instance input", e);
			}
		}

		/**
		 * @see Iterator#hasNext()
		 */
		@Override
		public synchronized boolean hasNext() {
			try {
				proceedToNext();
			} catch (XMLStreamException e) {
				throw new IllegalStateException("Failed to proceed to next instance", e);
			}

			return nextType != null;
		}

		@Override
		public TypeDefinition typePeek() {
			if (hasNext()) {
				return nextType;
			}
			return null;
		}

		@Override
		public boolean supportsTypePeek() {
			return true;
		}

		private void proceedToNext() throws XMLStreamException {
			if (nextType != null) {
				return;
			}

			if (allowedTypes == null) {
				initAllowedTypes();
			}

			while (nextType == null && reader.hasNext()) {
				int event = reader.next();
				if (event == XMLStreamConstants.START_ELEMENT) {
					if (!rootEncountered) {
						rootEncountered = true;
						if (ignoreRoot) {
							// skip to next element, don't create a root
							// instance
							continue;
						}
					}

					// check element and try to determine associated type
					QName elementName = new QName(reader.getNamespaceURI(), reader.getLocalName());
					TypeDefinition def = allowedTypes.get(elementName);

					// also check for xsi:type
					if (reader.getAttributeCount() > 0) {
						String xsiType = null;
						for (int i = 0; i < reader.getAttributeCount() && xsiType == null; i++) {
							String ns = reader.getAttributeNamespace(i);
							if (ns != null
									&& ns.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)
									&& reader.getAttributeLocalName(i).equals("type")) {
								// found xsi:type
								xsiType = reader.getAttributeValue(i);
							}
						}

						if (xsiType != null) {
							String[] parts = xsiType.split(":");
							if (parts != null && parts.length > 1) {
								String prefix = parts[0];
								String type = parts[1]; // XXX also other
														// eventual parts?

								String ns = reader.getNamespaceURI(prefix);

								// override with xsi:type
								def = allowedTypes.get(new QName(ns, type));
							}
						}
					}

					if (def != null) {
						nextType = def;
					}
				}
			}
		}

		private void initAllowedTypes() {
			allowedTypes = new HashMap<QName, TypeDefinition>();

			for (TypeDefinition def : sourceSchema.getMappingRelevantTypes()) {
				boolean accept;
				if (restrictToFeatures) {
					// accept only feature types
					Classification clazz = Classification.getClassification(def);
					switch (clazz) {
					case CONCRETE_FT:
						accept = true;
						break;
					default:
						accept = false;
					}
				}
				else {
					// accept all mappable types
					accept = true;
				}

				if (accept) {
					Collection<? extends XmlElement> elements = def
							.getConstraint(XmlElements.class).getElements();
					if (!elements.isEmpty()) {
						// use element name
						// XXX MappableFlag also for elements?
						for (XmlElement element : elements) {
							allowedTypes.put(element.getName(), def);
						}
					}
					else {
						allowedTypes.put(def.getName(), def);
					}
				}
			}
		}

		/**
		 * @see Iterator#next()
		 */
		@Override
		public synchronized Instance next() {
			if (nextType == null) {
				try {
					proceedToNext();
				} catch (XMLStreamException e) {
					throw new IllegalStateException(e);
				}
			}

			if (reader.getEventType() != XMLStreamConstants.START_ELEMENT || nextType == null) {
				throw new IllegalStateException();
			}

			try {
				return StreamGmlHelper.parseInstance(reader, nextType, elementIndex++, strict,
						null, crsProvider, nextType, null, false);
			} catch (XMLStreamException e) {
				throw new IllegalStateException(e);
			} finally {
				nextType = null;
			}
		}

		/**
		 * Get the type of the next instance. Must be called after
		 * {@link #hasNext()} but before {@link #next()} or {@link #skip()}
		 * 
		 * @return the type of the next instance
		 */
		public synchronized TypeDefinition nextType() {
			return nextType;
		}

		/**
		 * Skip the next object. Can be used instead of {@link #next()}
		 */
		@Override
		public synchronized void skip() {
			if (nextType == null) {
				try {
					proceedToNext();
				} catch (XMLStreamException e) {
					throw new IllegalStateException(e);
				}
			}

			if (reader.getEventType() != XMLStreamConstants.START_ELEMENT || nextType == null) {
				throw new IllegalStateException();
			}

			try {
				// close elements
				int open = 1;
				while (open > 0 && reader.hasNext()) {
					int event = reader.next();
					switch (event) {
					case XMLStreamConstants.START_ELEMENT:
						open++;
						break;
					case XMLStreamConstants.END_ELEMENT:
						open--;
						break;
					}
				}
			} catch (XMLStreamException e) {
				throw new IllegalStateException(e);
			} finally {
				nextType = null;
			}
		}

		/**
		 * @see Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see ResourceIterator#close()
		 */
		@Override
		public synchronized void close() {
			try {
				reader.close();
			} catch (XMLStreamException e) {
				// ignore
			}
			try {
				in.close();
			} catch (IOException e) {
				// ignore
			}
			nextType = null;
		}

	}

	private final TypeIndex sourceSchema;
	private final LocatableInputSupplier<? extends InputStream> source;
	private final boolean restrictToFeatures;
	private final boolean ignoreRoot;
	private final boolean strict;

	private boolean emptyInitialized = false;
	private boolean empty = false;
	private final CRSProvider crsProvider;

	/**
	 * Create an XMl/GML instance collection based on the given source.
	 * 
	 * @param source the source
	 * @param sourceSchema the source schema
	 * @param restrictToFeatures if only instances that are GML features shall
	 *            be loaded
	 * @param ignoreRoot if the root element should be ignored for creating
	 *            instances even if it is recognized as an allowed instance type
	 * @param strict if associating elements with properties should be done
	 *            strictly according to the schema, otherwise a fall-back is
	 *            used trying to populate values also on invalid property paths
	 * @param crsProvider CRS provider in case no CRS is specified, may be
	 *            <code>null</code>
	 */
	public GmlInstanceCollection(LocatableInputSupplier<? extends InputStream> source,
			TypeIndex sourceSchema, boolean restrictToFeatures, boolean ignoreRoot, boolean strict,
			CRSProvider crsProvider) {
		this.source = source;
		this.sourceSchema = sourceSchema;
		this.restrictToFeatures = restrictToFeatures;
		this.ignoreRoot = ignoreRoot;
		this.strict = strict;
		this.crsProvider = crsProvider;
	}

	/**
	 * @see InstanceCollection#hasSize()
	 */
	@Override
	public boolean hasSize() {
		return false;
	}

	/**
	 * @see InstanceCollection#size()
	 */
	@Override
	public int size() {
		return UNKNOWN_SIZE;
	}

	/**
	 * @see Iterable#iterator()
	 */
	@Override
	public GmlInstanceIterator iterator() {
		return new GmlInstanceIterator();
	}

	/**
	 * @see InstanceCollection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		if (!emptyInitialized) {
			ResourceIterator<Instance> it = iterator();
			try {
				empty = !it.hasNext();
			} finally {
				it.close();
			}

			emptyInitialized = true;
		}

		return empty;
	}

	/**
	 * @see InstanceCollection#select(Filter)
	 */
	@Override
	public InstanceCollection select(Filter filter) {
		return FilteredInstanceCollection.applyFilter(this, filter);
	}

	/**
	 * @see InstanceResolver#getReference(Instance)
	 */
	@Override
	public InstanceReference getReference(Instance instance) {
		if (instance instanceof StreamGmlInstance) {
			return new IndexInstanceReference(instance.getDataSet(),
					((StreamGmlInstance) instance).getIndexInStream());
		}

		throw new IllegalArgumentException(
				"Reference can only be determined based on a StreamGmlInstance");
	}

	/**
	 * @see InstanceResolver#getInstance(InstanceReference)
	 */
	@Override
	public Instance getInstance(InstanceReference reference) {
		IndexInstanceReference ref = (IndexInstanceReference) reference;

		GmlInstanceIterator it = iterator();
		try {
			for (int i = 0; i < ref.getIndex(); i++) {
				// skip all instances before the referenced instance
				it.skip();
			}
			return it.next(); // return the referenced instance
		} finally {
			it.close();
		}
	}

}
