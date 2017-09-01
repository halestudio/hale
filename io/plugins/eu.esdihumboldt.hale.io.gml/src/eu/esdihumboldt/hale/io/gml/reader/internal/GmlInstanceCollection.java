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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
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
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.io.gml.reader.internal.instance.StreamGmlHelper;
import eu.esdihumboldt.hale.io.gml.reader.internal.instance.StreamGmlInstance;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;
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
		private Map<QName, TypeDefinition> allElements;

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
		 * Variable for tracking current type to be able to determine types
		 * based on internal nested elements.
		 * 
		 * Uses a linked list to allow null items.
		 */
		private final Deque<TypeDefinition> typeStack = new LinkedList<>();

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

			if (allElements == null) {
				initAllowedTypes();
			}

			while (nextType == null && reader.hasNext()) {
				int event = reader.next();
				if (event == XMLStreamConstants.START_ELEMENT) {
					// check element and try to determine associated type
					QName elementName = new QName(reader.getNamespaceURI(), reader.getLocalName());
					TypeDefinition def = findType(elementName);

					// also check for xsi:type
					if (reader.getAttributeCount() > 0) {
						String xsiType = null;
						for (int i = 0; i < reader.getAttributeCount() && xsiType == null; i++) {
							String ns = reader.getAttributeNamespace(i);
							if (ns != null && ns.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)
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
								def = findTypeByName(new QName(ns, type));
							}
						}
					}

					if (def == null) {
						// try to determine based on current type stack parent
						// type
						TypeDefinition parentType = typeStack.peek();
						if (parentType != null) {
							def = findType(parentType, elementName);
						}
					}
					typeStack.push(def);

					if (!rootEncountered) {
						rootEncountered = true;

						processExceptionReport();

						if (ignoreRoot) {
							// skip to next element, never create a root
							// instance
							continue;
						}
					}

					if (def != null && isAllowedType(def)) {
						nextType = def;
					}
				}
				else if (event == XMLStreamConstants.END_ELEMENT) {
					typeStack.pop();
				}
			}
		}

		/**
		 * Determine if the given type is a type allowed to be used for parsing
		 * instances.
		 * 
		 * @param def the type definition
		 * @return if the type is allowed for parsing instances
		 */
		private boolean isAllowedType(TypeDefinition def) {
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
				return ignoreMappingRelevant
						|| def.getConstraint(MappingRelevantFlag.class).isEnabled();
			}
			else {
				return false;
			}
		}

		/**
		 * Find the type for a given name.
		 * 
		 * @param name the type or element name
		 * @return the found type or <code>null</code>
		 */
		private TypeDefinition findType(QName name) {
			TypeDefinition result = allElements.get(name);

			if (result == null && ignoreNamespaces) {
				// also allow a local name match
				for (Entry<QName, TypeDefinition> entry : allElements.entrySet()) {
					TypeDefinition type = entry.getValue();
					if (entry.getKey().getLocalPart().equals(name.getLocalPart())
							|| type.getName().getLocalPart().equals(name.getLocalPart())) {
						result = type;
						log.info(MessageFormat.format(
								"Using type with differing namespace - {0} replaced with {1}",
								name.toString(), entry.getKey().toString()));
						break;
					}
				}
			}

			return result;
		}

		/**
		 * Find type by its type name.
		 * 
		 * @param name the type name
		 * @return the type definition or <code>null</code>
		 */
		private TypeDefinition findTypeByName(QName name) {
			TypeDefinition result = sourceSchema.getType(name);

			if (result == null && ignoreNamespaces) {
				// also allow a local name match

				// first try mapping relevant types
				for (TypeDefinition type : sourceSchema.getMappingRelevantTypes()) {
					if (type.getName().getLocalPart().equals(name.getLocalPart())) {
						result = type;
						log.info(MessageFormat.format(
								"Using type with differing namespace - {0} replaced with {1}",
								name.toString(), type.getName().toString()));
						break;
					}
				}

				// then try all types
				if (result == null) {
					for (TypeDefinition type : sourceSchema.getTypes()) {
						if (type.getName().getLocalPart().equals(name.getLocalPart())) {
							result = type;
							log.info(MessageFormat.format(
									"Using type with differing namespace - {0} replaced with {1}",
									name.toString(), type.getName().toString()));
							break;
						}
					}
				}
			}

			return result;
		}

		private TypeDefinition findType(TypeDefinition parentType, QName elementName) {
			if (parentType == null) {
				return null;
			}

			// try direct property
			ChildDefinition<?> child = parentType.getChild(elementName);
			if (child != null && child.asProperty() != null) {
				return child.asProperty().getPropertyType();
			}

			// try children nested in groups
			Collection<? extends PropertyDefinition> allProperties = DefinitionUtil
					.getAllProperties(parentType);
			for (PropertyDefinition property : allProperties) {
				if (!property.getConstraint(XmlAttributeFlag.class).isEnabled()
						&& property.getName().equals(elementName)) {
					return property.getPropertyType();
				}
			}

			if (ignoreNamespaces) {
				// also allow a local name match
				for (PropertyDefinition property : allProperties) {
					if (!property.getConstraint(XmlAttributeFlag.class).isEnabled() && property
							.getName().getLocalPart().equals(elementName.getLocalPart())) {
						log.debug(MessageFormat.format(
								"Descending property with differing namespace - {0} replaced with {1}",
								elementName.toString(), property.getName().toString()));
						return property.getPropertyType();
					}
				}
			}

			return null;
		}

		/**
		 * Process an eventual exception / error report document. This method is
		 * called at the START_ELEMENT event of the root element.
		 * 
		 * @throws XMLStreamException if an error occurs parsing the document
		 */
		private void processExceptionReport() throws XMLStreamException {
			/*
			 * This code is very specific, handling certain cases of error
			 * documents that may be encountered.
			 */

			QName elementName = reader.getName();

			if ("ExceptionReport".equals(elementName.getLocalPart())
					&& elementName.getNamespaceURI().startsWith("http://www.opengis.net/ows")) {
				// OWS Exception Report (e.g. WFS 1.1, WFS 2)
				StringBuilder message = new StringBuilder("Document is a OGC OWS Exception Report");

				// try to extract error information
				List<String> errors = new ArrayList<>();
				while (reader.hasNext()) {
					int event = reader.next();
					if (event == XMLStreamConstants.START_ELEMENT) {
						if ("Exception".equals(reader.getLocalName())) {
							// an individual exception
							String code = null;
							String locator = null;
							for (int i = 0; i < reader.getAttributeCount(); i++) {
								String name = reader.getAttributeLocalName(i);
								if (name.equals("exceptionCode")) {
									code = reader.getAttributeValue(i);
								}
								else if (name.equals("locator")) {
									locator = reader.getAttributeValue(i);
								}
							}

							// try getting the exception test
							int level = 1;
							String text = null;
							while (reader.hasNext() && level >= 1) {
								int insideEvent = reader.next();
								if (insideEvent == XMLStreamConstants.END_ELEMENT) {
									level--;
								}
								else if (insideEvent == XMLStreamConstants.START_ELEMENT) {
									if (text == null
											&& "ExceptionText".equals(reader.getLocalName())) {
										try {
											text = reader.getElementText();
										} catch (XMLStreamException e) {
											// ignore
										}
									}
									else {
										level++;
									}
								}
							}

							if (text != null) {
								errors.add(buildErrorString(text, code, locator));
							}
						}
					}
				}

				if (!errors.isEmpty()) {
					message.append(" with the following exceptions reported:");
					boolean first = true;
					for (String error : errors) {
						if (first) {
							first = false;
						}
						else {
							message.append(',');
						}
						message.append('\n');
						message.append(error);
					}
				}

				throw new IllegalStateException(message.toString());
			}

			if ("ServiceExceptionReport".equals(elementName.getLocalPart())) {
				// WFS 1.0.0 Exception Report

				/*
				 * Seems to be returned even by some WFS 1.1/WFS 2 services and
				 * without proper namespace definition.
				 */
				StringBuilder message = new StringBuilder(
						"Document is a OGC ServiceExceptionReport");

				// try to extract error information
				List<String> errors = new ArrayList<>();
				while (reader.hasNext()) {
					int event = reader.next();
					if (event == XMLStreamConstants.START_ELEMENT) {
						if ("ServiceException".equals(reader.getLocalName())) {
							// an individual exception
							String code = null;
							String locator = null;
							for (int i = 0; i < reader.getAttributeCount(); i++) {
								String name = reader.getAttributeLocalName(i);
								if (name.equals("code")) {
									code = reader.getAttributeValue(i);
								}
								else if (name.equals("locator")) {
									locator = reader.getAttributeValue(i);
								}
							}

							String text = null;
							try {
								text = reader.getElementText();
							} catch (XMLStreamException e) {
								// ignore
							}

							if (text != null) {
								errors.add(buildErrorString(text, code, locator));
							}
						}
					}
				}

				if (!errors.isEmpty()) {
					message.append(" with the following exceptions reported:");
					boolean first = true;
					for (String error : errors) {
						if (first) {
							first = false;
						}
						else {
							message.append(',');
						}
						message.append('\n');
						message.append(error);
					}
				}

				throw new IllegalStateException(message.toString());
			}
		}

		private String buildErrorString(String text, @Nullable String code,
				@Nullable String locator) {
			StringBuilder error = new StringBuilder(text);
			if (code != null || locator != null) {
				error.append(" [");
				if (code != null) {
					error.append("code=");
					error.append(code);
					if (locator != null)
						error.append(", ");
				}
				if (locator != null) {
					error.append("locator=");
					error.append(locator);
				}
				error.append(']');
			}
			return error.toString();
		}

		private void initAllowedTypes() {
			allElements = new HashMap<QName, TypeDefinition>();

			for (TypeDefinition def : sourceSchema.getTypes()) {
				Collection<? extends XmlElement> elements = def.getConstraint(XmlElements.class)
						.getElements();
				if (!elements.isEmpty()) {
					// use element name
					// XXX MappableFlag also for elements?
					for (XmlElement element : elements) {
						allElements.put(element.getName(), def);
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
				return StreamGmlHelper.parseInstance(reader, nextType, elementIndex++, strict, null,
						crsProvider, nextType, null, false, ignoreNamespaces, ioProvider);
			} catch (XMLStreamException e) {
				throw new IllegalStateException(e);
			} finally {
				nextType = null;
				typeStack.pop(); // parseInstance consumes END_ELEMENT
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

	private final ALogger log = ALoggerFactory.getLogger(GmlInstanceCollection.class);

	private final TypeIndex sourceSchema;
	private final LocatableInputSupplier<? extends InputStream> source;
	private final boolean restrictToFeatures;
	private final boolean ignoreRoot;
	private final boolean strict;

	private boolean emptyInitialized = false;
	private boolean empty = false;
	private final CRSProvider crsProvider;
	private final boolean ignoreNamespaces;
	private final IOProvider ioProvider;

	private final boolean ignoreMappingRelevant;

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
	 * @param ignoreNamespaces if parsing of the XML instances should allow
	 *            types and properties with namespaces that differ from those
	 *            defined in the schema
	 * @param crsProvider CRS provider in case no CRS is specified, may be
	 *            <code>null</code>
	 * @param provider the I/O provider to get values
	 */
	public GmlInstanceCollection(LocatableInputSupplier<? extends InputStream> source,
			TypeIndex sourceSchema, boolean restrictToFeatures, boolean ignoreRoot, boolean strict,
			boolean ignoreNamespaces, CRSProvider crsProvider, IOProvider provider) {
		this.source = source;
		this.sourceSchema = sourceSchema;
		this.restrictToFeatures = restrictToFeatures;
		this.ignoreRoot = ignoreRoot;
		this.strict = strict;
		this.ignoreNamespaces = ignoreNamespaces;
		this.crsProvider = crsProvider;
		this.ioProvider = provider;

		// extract additional settings from I/O provider

		this.ignoreMappingRelevant = provider
				.getParameter(StreamGmlReader.PARAM_IGNORE_MAPPING_RELEVANT)
				.as(Boolean.class, false);

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

	/*
	 * TODO optimize retrieval of multiple references
	 */

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
