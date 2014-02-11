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
import java.net.URI;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import eu.esdihumboldt.hale.common.align.io.EntityResolver;
import eu.esdihumboldt.hale.common.align.io.impl.internal.AlignmentToJaxb;
import eu.esdihumboldt.hale.common.align.io.impl.internal.JaxbToAlignment;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AlignmentType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ObjectFactory;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.util.io.PathUpdate;

/**
 * Save or load an alignment
 * 
 * @author Simon Templer
 */
public class JaxbAlignmentIO {

	/**
	 * The JAXB context path for alignments.
	 */
	public static final String ALIGNMENT_CONTEXT = "eu.esdihumboldt.hale.common.align.io.impl.internal.generated";

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
	 * @param updater the path updater to use for base alignments
	 * @param resolver the entity resolver, <code>null</code> to use the default
	 *            resolver
	 * @return the alignment
	 * @throws JAXBException if reading the alignment failed
	 * @throws IOException if loading of base alignments failed
	 */
	public static MutableAlignment load(InputStream in, IOReporter reporter, TypeIndex sourceTypes,
			TypeIndex targetTypes, PathUpdate updater, EntityResolver resolver)
			throws JAXBException, IOException {
		AlignmentType genAlignment = JaxbToAlignment.load(in, reporter);
		// convert to alignment
		return new JaxbToAlignment(genAlignment, reporter, sourceTypes, targetTypes, updater,
				resolver).convert();
	}

	/**
	 * Adds the given base alignment to the given alignment.
	 * 
	 * @param alignment the alignment to add a base alignment to
	 * @param newBase URI of the new base alignment
	 * @param projectLocation the project location or <code>null</code>
	 * @param sourceTypes the source types to use for resolving definition
	 *            references
	 * @param targetTypes the target types to use for resolving definition
	 *            references
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @throws IOException if adding the base alignment failed
	 */
	public static void addBaseAlignment(MutableAlignment alignment, URI newBase,
			URI projectLocation, TypeIndex sourceTypes, TypeIndex targetTypes, IOReporter reporter)
			throws IOException {
		JaxbToAlignment.addBaseAlignment(alignment, newBase, projectLocation, sourceTypes,
				targetTypes, reporter);
	}

	/**
	 * Save a default alignment to an output stream.
	 * 
	 * @param alignment the alignment to save
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @param out the output stream
	 * @param pathUpdate to update relative paths in case of a path change
	 * @throws Exception if converting or writing the alignment fails
	 */
	public static void save(Alignment alignment, IOReporter reporter, OutputStream out,
			PathUpdate pathUpdate) throws Exception {
		AlignmentType align = new AlignmentToJaxb(alignment, reporter, pathUpdate).convert();

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
