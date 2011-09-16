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

package eu.esdihumboldt.cst.transformer;

import java.util.List;

import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.filter.Filter;

import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.omwg.Restriction;
import eu.esdihumboldt.specification.cst.align.IEntity;

/**
 * Utility methods for filters on entities
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class FilterUtils {
	
	/**
	 * Get the restrictions for an entity. For {@link FeatureClass}es the
	 * attributeValueConditions will be returned, for {@link Property}s the
	 * attributeValueConditions of a domainRestriction
	 * 
	 * @param entity the entity (either {@link FeatureClass} or {@link Property})
	 * 
	 * @return the entity restrictions
	 */
	public static List<Restriction> getRestrictions(IEntity entity) {
		if (entity instanceof FeatureClass) {
			return ((FeatureClass) entity).getAttributeValueCondition();
		}
		else if (entity instanceof Property) {
			Property property = (Property) entity;
			List<FeatureClass> domainRestrictions = property.getDomainRestriction();
			if (domainRestrictions != null) {
				for (FeatureClass fc : domainRestrictions) {
					List<Restriction> restrictions = fc.getAttributeValueCondition();
					if (restrictions != null && !restrictions.isEmpty()) {
						return restrictions;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Get the filter for the given entity
	 * 
	 * @param entity the entity (either {@link FeatureClass} or {@link Property})
	 * @return the filter or <code>null</code> if none is defined
	 */
	public static Filter getFilter(IEntity entity) {
		List<Restriction> avclist = getRestrictions(entity);
		String cql = null;
		if (avclist != null 
				&& avclist.size() > 0 
				&& avclist.get(0) != null) {
			cql = avclist.get(0).getCqlStr();
		}
		
		if (cql == null) {
			return null;
		}
		else {
			try {
				return CQL.toFilter(cql);
			} catch (CQLException e) {
				throw new RuntimeException("Filter could not be parsed from CQL string", e); //$NON-NLS-1$
			}
		}
	}

}
