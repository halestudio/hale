/*
 * Copyright (c) 2016 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.convert.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;

/**
 * Converter extension.
 * 
 * @author Simon Templer
 */
public class ConverterExtension extends IdentifiableExtension<ConverterInfo> {

	private static final String ID = "eu.esdihumboldt.hale.converters";

	private static ConverterExtension instance;

	/**
	 * Get the extension instance.
	 * 
	 * @return the extension singleton
	 */
	public static ConverterExtension getInstance() {
		synchronized (ConverterExtension.class) {
			if (instance == null)
				instance = new ConverterExtension();
		}
		return instance;
	}

	/**
	 * Default constructor.
	 */
	public ConverterExtension() {
		super(ID);
	}

	@Override
	protected String getIdAttributeName() {
		return "class";
	}

	@Override
	protected ConverterInfo create(String elementId, IConfigurationElement element) {
		return new ConverterInfo(elementId, element);
	}

}
