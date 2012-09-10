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

package eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.internal;

import java.util.Collections;
import java.util.List;

import de.cs3d.util.eclipse.extension.AbstractObjectFactory;
import de.cs3d.util.eclipse.extension.ExtensionObjectDefinition;
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
