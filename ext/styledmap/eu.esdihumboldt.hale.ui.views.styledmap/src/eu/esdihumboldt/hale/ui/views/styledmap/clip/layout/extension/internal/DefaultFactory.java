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

package eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.internal;

import java.util.Collections;
import java.util.List;

import de.fhg.igd.eclipse.util.extension.AbstractObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.PainterLayout;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.PainterLayoutFactory;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.PainterProxy;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.impl.NoLayout;

/**
 * Default painter layout factory. Used as fall back in
 * {@link PainterLayoutManager}.
 * 
 * @author Simon Templer
 */
public class DefaultFactory extends AbstractObjectFactory<PainterLayout> implements
		PainterLayoutFactory {

	@Override
	public PainterLayout createExtensionObject() throws Exception {
		return new NoLayout();
	}

	@Override
	public void dispose(PainterLayout instance) {
		// TODO ?
	}

	@Override
	public String getIdentifier() {
		return "default";
	}

	/**
	 * @see ExtensionObjectDefinition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Default";
	}

	/**
	 * @see ExtensionObjectDefinition#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return NoLayout.class.getName();
	}

	/**
	 * @see PainterLayoutFactory#getPaintersToLayout()
	 */
	@Override
	public List<PainterProxy> getPaintersToLayout() {
		// no painters to layout
		return Collections.emptyList();
	}

}
