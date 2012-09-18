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

package eu.esdihumboldt.hale.ui.common.definition.viewer;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.common.definition.DefinitionImages;

/**
 * Basic label provider for {@link Definition}s and {@link EntityDefinition}s
 * 
 * @author Simon Templer
 */
public class DefinitionLabelProvider extends LabelProvider {

	private final DefinitionImages images = new DefinitionImages();

	private final boolean longNames;

	/**
	 * Create a label provider that will use short names for
	 * {@link EntityDefinition}s.
	 */
	public DefinitionLabelProvider() {
		this(false);
	}

	/**
	 * Create a label provider for {@link Definition}s and
	 * {@link EntityDefinition}.
	 * 
	 * @param longNames if for {@link EntityDefinition}s long names shall be
	 *            used
	 */
	public DefinitionLabelProvider(boolean longNames) {
		this(longNames, false);
	}

	/**
	 * Create a label provider for {@link Definition}s and
	 * {@link EntityDefinition}.
	 * 
	 * @param longNames if for {@link EntityDefinition}s long names shall be
	 *            used
	 * @param suppressMandatory if the mandatory overlay for properties shall be
	 *            suppressed (defaults to <code>false</code>)
	 */
	public DefinitionLabelProvider(boolean longNames, boolean suppressMandatory) {
		super();

		this.longNames = longNames;
		images.setSuppressMandatory(suppressMandatory);
	}

	/**
	 * @see LabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof EntityDefinition) {
			EntityDefinition entityDef = (EntityDefinition) element;
			element = entityDef.getDefinition();

			List<ChildContext> path = entityDef.getPropertyPath();
			if (path != null && !path.isEmpty()) {
				if (!longNames) {
					path = Collections.singletonList(path.get(path.size() - 1));
				}

				StringBuffer name = new StringBuffer();
				boolean first = true;
				for (ChildContext context : path) {
					if (first) {
						first = false;
					}
					else {
						name.append('.');
					}
					boolean defContext = context.getContextName() == null
							&& context.getIndex() == null && context.getCondition() == null;
					if (!defContext) {
						name.append('(');
					}
					name.append(getText(context.getChild()));
					if (!defContext) {
						name.append(')');
					}
				}
				return name.toString();
			}
			else {
				if (entityDef.getFilter() != null) {
					return "(" + getText(element) + ")";
				}
			}
		}

		if (element instanceof Definition<?>) {
			return ((Definition<?>) element).getDisplayName();
		}

		return super.getText(element);
	}

	/**
	 * Returns an adjusted image depending on the type of the object passed in.
	 * 
	 * @return an Image
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof EntityDefinition) {
			return images.getImage((EntityDefinition) element);
		}

		if (element instanceof Definition<?>) {
			return images.getImage((Definition<?>) element);
		}

		return super.getImage(element);
	}

	/**
	 * @see BaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		images.dispose();

		super.dispose();
	}

}
