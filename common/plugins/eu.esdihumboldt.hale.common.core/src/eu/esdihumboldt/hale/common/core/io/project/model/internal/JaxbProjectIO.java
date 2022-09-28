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

package eu.esdihumboldt.hale.common.core.io.project.model.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.stream.StreamSource;

import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.generated.ObjectFactory;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.generated.ProjectType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

/**
 * Save or load a project
 * 
 * @author Simon Templer
 */
public class JaxbProjectIO {

	/**
	 * The JAXB context.
	 */
	public static final String PROJECT_CONTEXT = "eu.esdihumboldt.hale.common.core.io.project.model.internal.generated";

	/**
	 * Load a project from an input stream.
	 * 
	 * @param in the input stream
	 * @return the alignment
	 * @throws JAXBException if reading the alignment failed
	 */
	public static Project load(InputStream in) throws JAXBException {
		JAXBContext jc;
		JAXBElement<ProjectType> root = null;
		jc = JAXBContext.newInstance(PROJECT_CONTEXT, ProjectType.class.getClassLoader());
		Unmarshaller u = jc.createUnmarshaller();

		// it will debug problems while unmarshalling
		// don't set the event handler to prevent errors on unexpected elements
//		u.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());

		try {
			root = u.unmarshal(new StreamSource(in), ProjectType.class);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// ignore
			}
		}

		ProjectType genProject = root.getValue();

		// convert to project
		return JaxbToProject.convert(genProject);
	}

	/**
	 * Save a project to an output stream.
	 * 
	 * @param project the project to save
	 * @param out the output stream
	 * @throws Exception if converting or writing the alignment fails
	 */
	public static void save(Project project, OutputStream out) throws Exception {
		ProjectType projType = ProjectToJaxb.convert(project);

		JAXBContext jc = JAXBContext.newInstance(PROJECT_CONTEXT,
				ObjectFactory.class.getClassLoader());
		Marshaller m = jc.createMarshaller();

		// Indent output
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		// set encoding
		m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		// Specify the schema location
//		m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
//				"http://knowledgeweb.semanticweb.org/heterogeneity/alignment align.xsd");

		ObjectFactory of = new ObjectFactory();
		try {
			m.marshal(of.createHaleProject(projType), out);
		} finally {
			out.flush();
			out.close();
		}
	}
}
