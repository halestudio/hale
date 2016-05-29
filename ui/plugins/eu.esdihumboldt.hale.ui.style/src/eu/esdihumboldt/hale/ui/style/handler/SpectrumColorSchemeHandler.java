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

package eu.esdihumboldt.hale.ui.style.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;
import org.geotools.styling.Style;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.service.style.StyleService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.style.StyleHelper;

/**
 * Assigns a color spectrum to the schema types.
 * 
 * @author Simon Templer
 */
public class SpectrumColorSchemeHandler extends AbstractHandler {

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// collect all types
		SetMultimap<DataSet, TypeDefinition> types = HashMultimap.create();
		SchemaService schemas = PlatformUI.getWorkbench().getService(SchemaService.class);
		for (TypeDefinition type : schemas.getSchemas(SchemaSpaceID.SOURCE)
				.getMappingRelevantTypes()) {
			types.put(DataSet.SOURCE, type);
		}
		for (TypeDefinition type : schemas.getSchemas(SchemaSpaceID.TARGET)
				.getMappingRelevantTypes()) {
			types.put(DataSet.TRANSFORMED, type);
		}

		Style style = StyleHelper.getSpectrumStyles(types);

		StyleService styleService = PlatformUI.getWorkbench().getService(StyleService.class);
		styleService.addStyles(style);

		return null;
	}
}
