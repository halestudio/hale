/*
 * LICENSE: This program is being made available under the LGPL 3.0 license.
 * For more information on the license, please read the following:
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * For additional information on the Model behind Mismatches, please refer to
 * the following publication(s):
 * Thorsten Reitz (2010): A Mismatch Description Language for Conceptual Schema 
 * Mapping and Its Cartographic Representation, Geographic Information Science,
 * http://www.springerlink.com/content/um2082120r51232u/
 */
package eu.xsdi.mdl.model.reason;

import java.util.Collection;
import java.util.Iterator;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;

import eu.xsdi.mdl.model.Mismatch;
import eu.xsdi.mdl.model.Reason.EntityCharacteristic;

/**
 * The {@link ReasonRule} provides a formal definition of the identified Reason
 * of a {@link Mismatch}. It uses the class {@link ReasonSet} to express
 * the mismatching characteristic in the concrete case (i.e. it is not used to
 * express the general rule).
 * 
 * @author Thorsten Reitz, thor@xsdi.eu
 * @version $Id$ 
 * @since 0.1.0
 */
public class ReasonCondition {
	
	protected String domain;
	
	protected String filter;
	
	protected EntityCharacteristic characteristic;
	
	// Constructors ............................................................
	
	/**
	 * @param domain
	 */
	public ReasonCondition(String domain) {
		super();
		this.domain = domain;
	}
	
	/**
	 * @param domain the URL of the domain, such as the qualified name of a 
	 * property.
	 * @param filter an optional filter value (TODO: add Filter Operator?).
	 * @param characteristic an optional {@link EntityCharacteristic} to 
	 * evaluate.
	 */
	public ReasonCondition(String domain, String filter, EntityCharacteristic characteristic) {
		this(domain);
		this.characteristic = characteristic;
		this.filter = filter;
	}
	
	// Operations ..............................................................
	
	/**
	 * @param o the object which to check whether it fulfills this {@link ReasonCondition}.
	 * @return true if the {@link Object} o fulfills this {@link ReasonCondition}.
	 */
	public boolean evaluate(Object o) {
		if (o instanceof Feature) {
			Feature f = (Feature) o;
			Collection<Property> domainProperties = f.getProperties(this.domain);
			Iterator<Property> ip = domainProperties.iterator();
			if (ip.hasNext()) {
				Property p = ip.next();
				if (this.filter != null && !this.filter.equals("")) {
					return p.getValue().toString().equals(this.filter);
				}
				if (this.characteristic != null) {
					if (this.characteristic.equals(
							EntityCharacteristic.AttributeCardinalityConstraint)) {
						// TODO
					}
					else if (this.characteristic.equals(
							EntityCharacteristic.AttributeConcreteType)) {
						// TODO
					}
				}
			}
			return true;
		}
		else if (o instanceof FeatureType) {
			// TODO
		}
		else if (o instanceof Property) {
			// TODO
		}
		return false;
	}

	// Getters/Setters .........................................................
	
	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @param domain the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}

	/**
	 * @return the characteristic
	 */
	public EntityCharacteristic getCharacteristic() {
		return characteristic;
	}

	/**
	 * @param characteristic the characteristic to set
	 */
	public void setCharacteristic(EntityCharacteristic characteristic) {
		this.characteristic = characteristic;
	}

}
