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

package eu.esdihumboldt.hale.align.extension.transformation;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractExtension;
import eu.esdihumboldt.hale.align.transformation.function.TypeTransformation;

/**
 * Extension for {@link TypeTransformation}s
 * @author Simon Templer
 */
public class TypeTransformationExtension
		extends AbstractExtension<TypeTransformation<?>, TypeTransformationFactory> {

	private static final String EXTENSION_ID = "eu.esdihumboldt.hale.align.transformation";

	/**
	 * Default constructor
	 */
	public TypeTransformationExtension() {
		super(EXTENSION_ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected TypeTransformationFactory createFactory(
			IConfigurationElement conf) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
