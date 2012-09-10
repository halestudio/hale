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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.condition.TypeCondition;

/**
 * Represents a source or target type as parameter to a {@link TypeFunction}
 * 
 * @author Simon Templer
 */
public final class TypeParameter extends AbstractParameter {

	private static final ALogger log = ALoggerFactory.getLogger(TypeParameter.class);

	private final List<TypeCondition> conditions;

	/**
	 * @see AbstractParameter#AbstractParameter(IConfigurationElement)
	 */
	public TypeParameter(IConfigurationElement conf) {
		super(conf);

		conditions = createConditions(conf);
	}

	private static List<TypeCondition> createConditions(IConfigurationElement conf) {
		List<TypeCondition> result = new ArrayList<TypeCondition>();

		IConfigurationElement[] children = conf.getChildren();
		if (children != null) {
			for (IConfigurationElement child : children) {
				String name = child.getName();
				if (name.equals("typeCondition")) {
					try {
						TypeCondition condition = (TypeCondition) child
								.createExecutableExtension("class");
						result.add(condition);
					} catch (CoreException e) {
						log.error("Error creating property condition from extension", e);
					}
				}
				else {
					// ignore
//					log.error("Unrecognized property condition");
				}
			}
		}

		return result;
	}

	/**
	 * Get the property conditions
	 * 
	 * @return the property conditions
	 */
	public List<TypeCondition> getConditions() {
		return Collections.unmodifiableList(conditions);
	}

	// TODO conditions, properties?

}
