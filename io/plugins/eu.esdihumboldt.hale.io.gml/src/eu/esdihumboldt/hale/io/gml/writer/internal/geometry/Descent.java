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

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry;

import java.text.MessageFormat;
import java.util.List;
import java.util.ListIterator;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import eu.esdihumboldt.hale.io.gml.writer.internal.GmlWriterUtil;

/**
 * Represents a descent in the document, must be used to end elements started
 * with
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
			PathElement step = path.getSteps().get(path.getSteps().size() - 1 - i);

			if (!step.isTransient()) {
				writer.writeEndElement();
			}
		}
	}

	/**
	 * Descend the given path
	 * 
	 * @param writer the XML stream writer
	 * @param descendPath the path to descend
	 * @param previousDescent the previous descent, that will be closed or
	 *            partially closed as needed, may be <code>null</code>
	 * @param generateRequiredIDs if required IDs shall be generated for the
	 *            path elements
	 * @return the descent that was opened, it must be closed to close the
	 *         opened elements
	 * @throws XMLStreamException if an error occurs writing the coordinates
	 */
	public static Descent descend(XMLStreamWriter writer, DefinitionPath descendPath,
			Descent previousDescent, boolean generateRequiredIDs) throws XMLStreamException {
		return descend(writer, descendPath, previousDescent, generateRequiredIDs, false);
	}

	/**
	 * Descend the given path
	 * 
	 * @param writer the XML stream writer
	 * @param descendPath the path to descend
	 * @param previousDescent the previous descent, that will be closed or
	 *            partially closed as needed, may be <code>null</code>
	 * @param generateRequiredIDs if required IDs shall be generated for the
	 *            path elements
	 * @param allowFullClose if it is allowed to fully close the previous
	 *            descent regardless of element uniqueness
	 * @return the descent that was opened, it must be closed to close the
	 *         opened elements
	 * @throws XMLStreamException if an error occurs writing the coordinates
	 */
	public static Descent descend(XMLStreamWriter writer, DefinitionPath descendPath,
			Descent previousDescent, boolean generateRequiredIDs, boolean allowFullClose)
			throws XMLStreamException {
		if (descendPath.isEmpty()) {
			if (previousDescent != null) {
				// close previous descent
				previousDescent.close();
			}
			return new Descent(writer, descendPath);
		}

		List<PathElement> stepDown = descendPath.getSteps();
		PathElement downFrom = null;
		PathElement downAfter = null;

		if (previousDescent != null) {
			List<PathElement> previousSteps = previousDescent.getPath().getSteps();

			// find the first non-unique match in both paths
			PathElement firstNonUniqueMatch = null;
			int index = 0;
			for (PathElement step : previousSteps) {
				// check from the beginning how far the paths match
				if (stepDown.size() > index && stepDown.get(index).equals(step)) {
					if (!step.isUnique()) {
						firstNonUniqueMatch = step;
						// found the first non-unique match
						break;
					}
				}
				else {
					// after the first miss no more valid matches can be found
					break;
				}
				index++;
			}

			// close previous descent as needed
			ListIterator<PathElement> itPrev = previousSteps.listIterator(previousSteps.size());
			if (!allowFullClose && firstNonUniqueMatch == null) {
				boolean endedSomething = false;
				while (itPrev.hasPrevious()) {
					PathElement step = itPrev.previous();
					if (stepDown.contains(step)) {
						// step is contained in both paths
						if (step.isUnique()) {
							// step may not be closed, as the next path also
							// wants to enter
							// from the next path all steps before and including
							// this step must be ignored for stepping down
							downAfter = step;
							break;
						}
					}

					// close step
					if (!step.isTransient()) {
						writer.writeEndElement();
						endedSomething = true;
					}
				}

				if (!endedSomething) {
					throw new IllegalStateException(
							MessageFormat
									.format("Previous path ''{0}'' has only unique common elements with path ''{1}'', therefore a sequence of both is not possible",
											previousDescent.getPath().toString(),
											descendPath.toString()));
				}
			}
			else {
				while (itPrev.hasPrevious()) {
					PathElement step = itPrev.previous();

					if (!step.isTransient()) {
						// close step
						writer.writeEndElement();
					}

					if (firstNonUniqueMatch != null && firstNonUniqueMatch.equals(step)) {
						// step after this may not be closed, as the next path
						// also wants to enter
						// from the next path all steps before this step must be
						// ignored for stepping down
						downFrom = step;
						break;
					}
				}
			}
		}

		for (PathElement step : stepDown) {
			if (downFrom != null && downFrom.equals(step)) {
				downFrom = null;
			}

			if (downFrom == null && downAfter == null) {
				if (!step.isTransient()) {
					// start elements
					GmlWriterUtil.writeStartPathElement(writer, step, generateRequiredIDs);
				}
			}

			if (downAfter != null && downAfter.equals(step)) {
				downAfter = null;
			}
		}

		return new Descent(writer, descendPath);
	}

}
