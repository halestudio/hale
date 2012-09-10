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

package eu.esdihumboldt.hale.common.align.extension.function;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension;

/**
 * Extension for {@link TypeFunction}s
 * 
 * @author Simon Templer
 */
public class TypeFunctionExtension extends AbstractFunctionExtension<TypeFunction> {

	/**
	 * Property function extension point
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.hale.align.function";

	private static TypeFunctionExtension instance;

	/**
	 * Get the extension instance
	 * 
	 * @return the extension
	 */
	public static TypeFunctionExtension getInstance() {
		if (instance == null) {
			instance = new TypeFunctionExtension();
		}

		return instance;
	}

	/**
	 * Default constructor
	 */
	protected TypeFunctionExtension() {
		super(EXTENSION_ID);
	}

	/**
	 * @see IdentifiableExtension#getIdAttributeName()
	 */
	@Override
	protected String getIdAttributeName() {
		return "identifier";
	}

	/**
	 * @see AbstractFunctionExtension#doCreate(String, IConfigurationElement)
	 */
	@Override
	protected TypeFunction doCreate(String elementId, IConfigurationElement element) {
		if (element.getName().equals("typeFunction")) {
			return new TypeFunction(element);
		}

		return null;
	}

}
