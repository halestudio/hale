/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.schema.model.constraint.factory.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;

/**
 * Extension point for {@link ValueConstraintFactory} aliases.
 * 
 * @author Simon Templer
 */
public class AliasExtension extends IdentifiableExtension<Alias> {

	/**
	 * The extension instance.
	 */
	public static final AliasExtension INSTANCE = new AliasExtension();

	/**
	 * Default constructor.
	 */
	private AliasExtension() {
		super(ValueConstraintExtension.ID);
	}

	@Override
	protected Alias create(String id, IConfigurationElement conf) {
		if ("alias".equals(conf.getName())) {
			return new Alias(id, conf);
		}
		else {
			return null;
		}
	}

	@Override
	protected String getIdAttributeName() {
		return "id";
	}

}
