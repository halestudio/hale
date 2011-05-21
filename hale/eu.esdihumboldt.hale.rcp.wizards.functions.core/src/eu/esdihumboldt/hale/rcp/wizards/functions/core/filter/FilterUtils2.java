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

package eu.esdihumboldt.hale.rcp.wizards.functions.core.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.omwg.Restriction;
import eu.esdihumboldt.cst.transformer.EntityUtils;
import eu.esdihumboldt.cst.transformer.FilterUtils;
import eu.esdihumboldt.hale.ui.model.schema.SchemaItem;
import eu.esdihumboldt.specification.cst.align.IEntity;

/**
 * Utility methods for filters on entities
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class FilterUtils2 extends FilterUtils {
	
	/**
	 * Add a restriction to the entity. For {@link FeatureClass}es it will be 
	 * added to the attributeValueConditions, for {@link Property}s the
	 * restriction will be added to the attributeValueConditions of a
	 * domainRestriction that refers to the encompassing type.
	 * 
	 * @param r the restriction
	 * @param entity the entity (either {@link FeatureClass} or {@link Property})
	 * @param item the schema item that represents the entity
	 */
	public static void addRestriction(Restriction r, IEntity entity,
			SchemaItem item) {
		if (entity instanceof FeatureClass) {
			FeatureClass fc = (FeatureClass) entity;
			if (fc.getAttributeValueCondition() == null) {
				fc.setAttributeValueCondition(new ArrayList<Restriction>());
			}
			fc.getAttributeValueCondition().add(r);
		}
		else if (entity instanceof Property) {
			Property p = (Property) entity;
			List<FeatureClass> domainRestrictions = p.getDomainRestriction();
			FeatureClass fc = null;
			if (domainRestrictions == null || domainRestrictions.isEmpty()) {
				// create domain restriction
				if (domainRestrictions == null) {
					domainRestrictions = new ArrayList<FeatureClass>();
					p.setDomainRestriction(domainRestrictions);
				}
			}
			else {
				FeatureClass reference = getParentFeatureClass(item);
				Iterator<FeatureClass> it = domainRestrictions.iterator();
				while (fc == null && it.hasNext()) {
					FeatureClass domain = it.next();
					if (EntityUtils.entitiesMatch(reference, domain)) {
						fc = domain;
					}
				}
			}
			
			if (fc == null) {
				fc = getParentFeatureClass(item);
				domainRestrictions.add(fc);
			}
			
			addRestriction(r, fc, null);
		}
	}

	/**
	 * Get the parent feature class entity for the given schema item
	 * 
	 * @param item the schema item (must be either an attribute or a type)
	 * 
	 * @return the parent feature class entity
	 */
	public static FeatureClass getParentFeatureClass(SchemaItem item) {
		return (FeatureClass) getParentTypeItem(item).getEntity();
	}
	
	/**
	 * Get the parent feature class entity for the given schema item
	 * 
	 * @param item the schema item (must be either an attribute or a type)
	 * 
	 * @return the parent feature class entity
	 */
	public static SchemaItem getParentTypeItem(SchemaItem item) {
		while (item.isAttribute() && item.getParent() != null) {
			item = item.getParent();
		}
		
		if (item.isType()) {
			return item;
		}
		else {
			throw new RuntimeException("No parent type found"); //$NON-NLS-1$
		}
	}

}
