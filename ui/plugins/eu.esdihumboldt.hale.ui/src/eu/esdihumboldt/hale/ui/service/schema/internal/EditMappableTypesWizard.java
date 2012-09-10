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
		SchemaService schemaService = (SchemaService) PlatformUI.getWorkbench().getService(
				SchemaService.class);
		schemaService.toggleMappable(spaceID, page.getSelectedTypes());
		return true;
	}
}
