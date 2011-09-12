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

import java.util.List;
import java.util.Set;

import eu.esdihumboldt.commons.goml.align.Entity;

/**
 * The {@link ReasonSet} describes a Set of properties of either a schema or
 * the instances associated with the schema. The structure is loosely 
 * aligned to MathML's way of expressing Sets.
 *  
 * @author Thorsten Reitz, thor@xsdi.eu
 * @version $Id$ 
 * @since 0.1.0
 */
public class ReasonSet {
	
	private ReasonSetIdentifierGenerator rsig;
	
	/**
	 * The identifier is used as this {@link ReasonSet}'s name.
	 */
	private String identifier;
	
	/**
	 * The domain identifies the Set form which this {@link ReasonSet} is 
	 * built. In MDL, domains are identified by fully qualified {@link Entity}
	 * names, i.e. the value in here is equal to {@link Entity#getAbout()}'s
	 * value.
	 */
	private String domain;
	
	/**
	 * A List of conditions that can be used as filtering expressions on the
	 * {@link ReasonSet} identified by the domain.
	 */
	private List<ReasonCondition> conditions;
	
	/**
	 * Subsets can be used to "go down" the object graph from which a set 
	 * is to be taken. As an example, the parent Set can be a part of all
	 * Features of a given FeatureType, while the subset can then limit 
	 * down the ReasonSet to the attributes fulfilling a certain condition
	 * of those features.
	 */
	private ReasonSet subSet;
	
	// Constructors ------------------------------------------------------------
	
	/**
	 * Constructs an empty {@link ReasonSet}.
	 */
	public ReasonSet() {
		super();
		this.rsig = new ReasonSetIdentifierGenerator();
	}
	
	/**
	 * Constructs a {@link ReasonSet} 
	 * @param domain the namespace URI of the basic Set from which to build 
	 * this {@link ReasonSet}.
	 */
	public ReasonSet(String domain) {
		this(null, domain);
	}
	
	/**
	 * Constructs a {@link ReasonSet} 
	 * @param identifier the identifier to use for this set. If null, a 
	 * random one will be assigned.
	 * @param domain the namespace URI of the basic Set from which to build 
	 * this {@link ReasonSet}.
	 */
	public ReasonSet(String identifier, String domain) {
		this();
		if (identifier == null || identifier.equals("")) {
			this.identifier = this.rsig.next();
		}
		this.domain = domain;
	}
	
	// Operations --------------------------------------------------------------
	
	/**
	 * @param objects the objects to build the {@link ReasonSet} from.
	 * @return the {@link Set} of Objects that correspond to this 
	 * {@link ReasonSet}, when applied to the given input objects {@link Set}.
	 */
	public Set<Object> apply(Set<Object> objects) {
		// TODO
		return null;
	}
	
	// Getters/Setters ---------------------------------------------------------

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
	 * @return the conditions
	 */
	public List<ReasonCondition> getConditions() {
		return conditions;
	}

	/**
	 * @param conditions the conditions to set
	 */
	public void setConditions(List<ReasonCondition> conditions) {
		this.conditions = conditions;
	}

	/**
	 * @return the subSet
	 */
	public ReasonSet getSubSet() {
		return subSet;
	}

	/**
	 * @param subSet the subSet to set
	 */
	public void setSubSet(ReasonSet subSet) {
		this.subSet = subSet;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	

}
