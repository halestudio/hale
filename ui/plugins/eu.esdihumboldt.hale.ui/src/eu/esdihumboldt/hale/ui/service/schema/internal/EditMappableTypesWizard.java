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

package eu.esdihumboldt.hale.ui.service.schema.internal;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Wizard to edit which types are mappable.
 * 
 * @author Kai Schwierczek
 */
public class EditMappableTypesWizard extends Wizard {

	private final EditMappableTypesPage page;
	private final SchemaSpaceID spaceID;

	/**
	 * Creates a new wizard to edit which types in the given index are mappable.
	 * 
	 * @param spaceID the schema space of which the types are
	 * @param typeIndex the type index to change
	 */
	public EditMappableTypesWizard(SchemaSpaceID spaceID, TypeIndex typeIndex) {
		page = new EditMappableTypesPage(spaceID, typeIndex);
		this.spaceID = spaceID;
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		addPage(page);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		SchemaService schemaService = PlatformUI.getWorkbench().getService(SchemaService.class);
		schemaService.toggleMappable(spaceID, page.getSelectedTypes());
		return true;
	}
}
