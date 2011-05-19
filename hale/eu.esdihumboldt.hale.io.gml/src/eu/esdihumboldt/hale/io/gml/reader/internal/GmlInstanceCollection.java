/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.gml.reader.internal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.geotools.feature.NameImpl;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.DefinitionUtil;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

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
	public class InstanceIterator implements ResourceIterator<Instance> {

		private final InputStream in;
		
		private final XMLStreamReader reader;
		
		private Map<Name, Definition> allowedTypes;
		
		private TypeDefinition nextType;

		/**
		 * Default constructor
		 */
		public InstanceIterator() {
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

		private void proceedToNext() throws XMLStreamException {
			if (nextType != null) {
				return;
			}
			
			if (allowedTypes == null) {
				initAllowedTypes();
			}
			
			while (nextType == null) {
				int event = reader.nextTag();
				if (event == XMLStreamConstants.START_ELEMENT) {
					// check element and try to determine associated type
					Definition def = allowedTypes.get(new NameImpl(
							reader.getNamespaceURI(), 
							reader.getLocalName()));
					
					// also check for xsi:type
					if (reader.getAttributeCount() > 0) {
						String xsiType = null;
						for (int i = 0; i < reader.getAttributeCount() && xsiType == null; i++) {
							if (reader.getAttributeNamespace(i).equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)
									&& reader.getAttributeLocalName(i).equals("type")) {
								// found xsi:type
								xsiType = reader.getAttributeValue(i);
							}
						}
						
						if (xsiType != null) {
							String[] parts = xsiType.split(":");
							if (parts != null && parts.length > 1) {
								String prefix = parts[0];
								String type = parts[1]; //XXX also other eventual parts?
								
								String ns = reader.getNamespaceURI(prefix);
								
								// override with xsi:type
								def = allowedTypes.get(new NameImpl(ns, type));
							}
						}
					}
					
					if (def != null) {
						nextType = DefinitionUtil.getType(def);
					}
				}
			}
		}

		private void initAllowedTypes() {
			allowedTypes = new HashMap<Name, Definition>();
			
			for (Definition def : sourceSchema.getTypes().keySet()) {
				Name name;
				if (def instanceof SchemaElement) {
					name = ((SchemaElement) def).getElementName();
				}
				else if (def instanceof TypeDefinition) {
					name = ((TypeDefinition) def).getName();
				}
				else {
					continue;
				}
				allowedTypes.put(name, def);
			}
		}

		/**
		 * @see Iterator#next()
		 */
		@Override
		public synchronized Instance next() {
			if (reader.getEventType() != XMLStreamConstants.START_ELEMENT 
					|| nextType == null) {
				throw new IllegalStateException();
			}
			
			try {
				return StreamGmlInstance.parseInstance(reader, nextType);
			} catch (XMLStreamException e) {
				throw new IllegalStateException();
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
		public synchronized void skip() {
			if (reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
				throw new IllegalStateException();
			}
			
			try {
				// close elements
				int open = 1;
				while (open > 0 && reader.hasNext()) {
					int event = reader.nextTag();
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
		 * @see ResourceIterator#dispose()
		 */
		@Override
		public synchronized void dispose() {
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

	private final ContentType contentType; //FIXME content type necessary?
	private final Schema sourceSchema;
	private final LocatableInputSupplier<? extends InputStream> source;

	/**
	 * Create an XMl/GML instance collection based on the given source
	 * 
	 * @param source the source
	 * @param sourceSchema the source schema
	 * @param contentType the content type
	 */
	public GmlInstanceCollection(
			LocatableInputSupplier<? extends InputStream> source,
			Schema sourceSchema, ContentType contentType) {
		this.source = source;
		this.sourceSchema = sourceSchema;
		this.contentType = contentType;
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
	public ResourceIterator<Instance> iterator() {
		return new InstanceIterator();
	}

}
