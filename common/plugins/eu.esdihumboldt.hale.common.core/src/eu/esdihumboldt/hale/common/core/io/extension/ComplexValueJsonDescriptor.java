/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core.io.extension;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.common.core.io.ComplexValueJson;

/**
 * Descriptor for extensions providing augmented JSON converters for existing
 * complex value definitions.
 * 
 * @author Simon Templer
 */
@Immutable
public class ComplexValueJsonDescriptor implements Identifiable {

	private final String id;

	private final Class<ComplexValueJson<?, ?>> converterClass;

	/**
	 * Create a new descriptor.
	 * 
	 * @param id the complex value identifier
	 * @param converterClass the JSON converter class for the complex value
	 */
	public ComplexValueJsonDescriptor(@Nonnull String id,
			@Nonnull Class<ComplexValueJson<?, ?>> converterClass) {
		super();
		this.id = id;
		this.converterClass = converterClass;
	}

	/**
	 * @return the converter class
	 */
	public Class<ComplexValueJson<?, ?>> getConverterClass() {
		return converterClass;
	}

	@Override
	public String getId() {
		return id;
	}

}