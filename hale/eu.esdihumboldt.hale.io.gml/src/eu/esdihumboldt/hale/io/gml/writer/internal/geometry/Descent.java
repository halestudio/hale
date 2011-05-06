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

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import eu.esdihumboldt.hale.io.gml.writer.internal.GmlWriterUtil;

/**
 * Represents a descent in the document, must be used to end elements 
 * started with 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class Descent {
	
	private final XMLStreamWriter writer;
	
	private final DefinitionPath path;

	/**
	 * Constructor
	 * 
	 * @param writer the XMl stream writer
	 * @param path the descent path
	 */
	private Descent(XMLStreamWriter writer, DefinitionPath path) {
		super();
		this.path = path;
		this.writer = writer;
	}

	/**
	 * @return the path
	 */
	public DefinitionPath getPath() {
		return path;
	}

	/**
	 * Close the descent
	 * 
	 * @throws XMLStreamException if an error occurs closing the elements
	 */
	public void close() throws XMLStreamException {
		if (path.isEmpty()) {
			return;
		}
		
		for (int i = 0; i < path.getSteps().size(); i++) {
			writer.writeEndElement();
		}
	}
	
	/**
	 * Descend the given path
	 * 
	 * @param writer the XML stream writer 
	 * @param descendPath the path to descend
	 * @param generateRequiredIDs if required IDs shall be generated for the 
	 *   path elements
	 * @return the descent that was opened, it must be closed to close the
	 *   opened elements
	 * @throws XMLStreamException if an error occurs writing the coordinates
	 */
	public static Descent descend(XMLStreamWriter writer, 
			DefinitionPath descendPath, boolean generateRequiredIDs) throws XMLStreamException {
		if (descendPath.isEmpty()) {
			return new Descent(writer, descendPath);
		}
		
		for (PathElement step : descendPath.getSteps()) {
			// start elements
			GmlWriterUtil.writeStartPathElement(writer, step, generateRequiredIDs);
		}
		
		return new Descent(writer, descendPath); 
	}
	
}
