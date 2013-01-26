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

package eu.esdihumboldt.hale.common.align.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import eu.esdihumboldt.hale.common.align.io.impl.internal.AlignmentToJaxb;
import eu.esdihumboldt.hale.common.align.io.impl.internal.JaxbToAlignment;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AlignmentType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ObjectFactory;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Save or load an alignment
 * 
 * @author Simon Templer
 */
public class JaxbAlignmentIO {

	private static final String ALIGNMENT_CONTEXT = "eu.esdihumboldt.hale.common.align.io.impl.internal.generated";

	/**
	 * Load a default alignment from an input stream.
	 * 
	 * @param in the input stream
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @param sourceTypes the source types to use for resolving definition
	 *            references
	 * @param targetTypes the target types to use for resolving definition
	 *            references
	 * @return the alignment
	 * @throws JAXBException if reading the alignment failed
	 */
	public static MutableAlignment load(InputStream in, IOReporter reporter, TypeIndex sourceTypes,
			TypeIndex targetTypes) throws JAXBException {
		JAXBContext jc;
		JAXBElement<AlignmentType> root = null;
		jc = JAXBContext.newInstance(ALIGNMENT_CONTEXT);
		Unmarshaller u = jc.createUnmarshaller();

		// it will debug problems while unmarshalling
		u.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());

		try {
			root = u.unmarshal(new StreamSource(in), AlignmentType.class);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// ignore
			}
		}

		AlignmentType genAlignment = root.getValue();

		// convert to alignment
		return new JaxbToAlignment(genAlignment, reporter, sourceTypes, targetTypes).convert();
	}

	/**
	 * Save a default alignment to an output stream.
	 * 
	 * @param alignment the alignment to save
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @param out the output stream
	 * @throws Exception if converting or writing the alignment fails
	 */
	public static void save(Alignment alignment, IOReporter reporter, OutputStream out)
			throws Exception {
		// TODO convert to alignment type
		AlignmentType align = new AlignmentToJaxb(alignment, reporter).convert();

		JAXBContext jc = JAXBContext.newInstance(ALIGNMENT_CONTEXT);
		Marshaller m = jc.createMarshaller();

		// Indent output
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		// Specify the schema location
//		m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
//				"http://knowledgeweb.semanticweb.org/heterogeneity/alignment align.xsd");

		ObjectFactory of = new ObjectFactory();
		try {
			m.marshal(of.createAlignment(align), out);
		} finally {
			out.flush();
			out.close();
		}
	}
}
