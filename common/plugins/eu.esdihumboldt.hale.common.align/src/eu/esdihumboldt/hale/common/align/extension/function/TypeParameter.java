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

package eu.esdihumboldt.hale.common.align.extension.function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.condition.TypeCondition;

/**
 * Represents a source or target type as parameter to a {@link TypeFunction}
 * 
 * @author Simon Templer
 */
public final class TypeParameter extends AbstractParameter implements TypeParameterDefinition {

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
	 * @see eu.esdihumboldt.hale.common.align.extension.function.TypeParameterDefinition#getConditions()
	 */
	@Override
	public List<TypeCondition> getConditions() {
		return Collections.unmodifiableList(conditions);
	}

	// TODO conditions, properties?

}
