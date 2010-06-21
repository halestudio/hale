/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.wizards.functions.core.inspire.geographicname;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.corefunctions.inspire.GeographicalNameFunction;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * This is the {@link FunctionWizardFactory} for the
 * {@link GeographicalNameFunction}.
 * 
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$
 */
public class GeographicNameFunctionWizardFactory implements
		FunctionWizardFactory {

	/**
	 * @see FunctionWizardFactory#createWizard(AlignmentInfo)
	 */
	@Override
	public FunctionWizard createWizard(AlignmentInfo selection) {

		return new GeographicNameFunctionWizard(selection);
	}

	/**
	 * @see FunctionWizardFactory#supports(AlignmentInfo)
	 */
	@Override
	public boolean supports(AlignmentInfo selection) {

		// must be at least one source item and exactly one target item
		if (selection.getSourceItemCount() < 1
				|| selection.getTargetItemCount() != 1) {
			return false;
		}

		SchemaItem target = selection.getFirstTargetItem();
		if (!target.isAttribute()
				|| !isGeographicalNameAttribute((AttributeDefinition) target.getDefinition())) {
			return false;
		}

		// source items must be properties of the type STRING_ATTRIBUTE
		for (SchemaItem source : selection.getSourceItems()) {
			if (!source.isAttribute()
					|| !source.getType()
							.equals(TreeObjectType.STRING_ATTRIBUTE)) {
				return false;
			}
		}

		ICell cell = selection.getAlignment(selection.getSourceItems(),
				selection.getTargetItems());
		if (cell != null) {
			// only allow editing matching transformation
			try {
				return cell.getEntity1().getTransformation().getService()
						.getLocation().equals(
								GeographicalNameFunction.class.getName());
			} catch (NullPointerException e) {
				return false;
			}
		}

		return true;

	}

	/**
	 * Determines if the given attribute definition represents a geographical
	 *   name attribute
	 * 
	 * @param definition the attribute definition
	 * 
	 * @return if the definition represents a geographical name attribute
	 */
	private boolean isGeographicalNameAttribute(AttributeDefinition definition) {
		TypeDefinition type = definition.getAttributeType();
		
		for (AttributeDefinition attr : type.getAttributes()) {
			/*
			 * TODO improve "detection"
			 * - tested w/ INSPIRE 3 HydroPhysicalWaters, ProtectedSitesFull
			 * - must be kept consistent w/ function implementation
			 */
			if (attr.getName().equals("GeographicalName")
					&& attr.getAttributeType().getName().getLocalPart().equals("GeographicalNameType")) {
				return true;
			}
		}
		
		return false;
	}

}
