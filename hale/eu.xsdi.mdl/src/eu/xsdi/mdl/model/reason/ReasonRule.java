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

import java.util.Set;

import eu.xsdi.mdl.model.Mismatch;


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
public class ReasonRule {
	
	protected ReasonSet set1;
	protected ReasonSet set2;
	
	/**
	 * The default constructor.
	 * @param rs1 the first {@link ReasonSet}. May not be null.
	 * @param rs2 the second {@link ReasonSet}. May not be null.
	 */
	public ReasonRule(ReasonSet rs1, ReasonSet rs2) {
		if (rs1 == null || rs2 == null) {
			throw new RuntimeException("The two ReasonSets of a ReasonRule " +
					"must be defined.");
		}
		this.set1 = rs1;
		this.set2 = rs2;
	}
	
	/**
	 * Tests whether two given Sets, with the respective {@link ReasonSet}s 
	 * applied, are equal. When they are not, a Mismatch can be indicated.
	 * @param objects1 the first set of objects, e.g. SchemaElements or 
	 * Features. Has to be of the same type as the second set.
	 * @param objects2 the second set of objects, e.g. SchemaElements or 
	 * Features. Has to be of the same type as the first set.
	 * @return true if the {@link Set}s objects1 and objects2, with the respective 
	 * {@link ReasonSet}s applied, are equal.
	 */
	public boolean testEquality(Set<Object> objects1, Set<Object> objects2) {
		Set<Object> s1 = this.set1.apply(objects1);
		Set<Object> s2 = this.set2.apply(objects2);
		return s1.containsAll(s2);
	}
	
	/**
	 * Get the Set of objects that are represented in both objects1 and 
	 * objects2, with the respective {@link ReasonSet}s applied.
	 * @param objects1 the first set of objects, e.g. SchemaElements or 
	 * Features. Has to be of the same type as the second set.
	 * @param objects2 the second set of objects, e.g. SchemaElements or 
	 * Features. Has to be of the same type as the first set.
	 * @return the Set of objects that are represented in both objects1 and 
	 * objects2, with the respective {@link ReasonSet}s applied.
	 */
	public Set<Object> getIntersection(Set<Object> objects1, Set<Object> objects2) {
		// TODO
		return null;
	}
	
	/**
	 * Get the union of the Sets objects1 and 
	 * objects2, with the respective {@link ReasonSet}s applied.
	 * @param objects1 the first set of objects, e.g. SchemaElements or 
	 * Features. Has to be of the same type as the second set.
	 * @param objects2 the second set of objects, e.g. SchemaElements or 
	 * Features. Has to be of the same type as the first set.
	 * @return A {@link Set} representing the union of the Sets objects1 and 
	 * objects2, with the respective {@link ReasonSet}s applied.
	 */
	public Set<Object> getUnion(Set<Object> objects1, Set<Object> objects2) {
		// TODO
		return null;
	}
	
	/**
	 * Get the difference of the Sets objects1 and 
	 * objects2, with the respective {@link ReasonSet}s applied. This operation 
	 * will return the inverse Set to {@link #getIntersection(Set, Set)}.
	 * @param objects1 the first set of objects, e.g. SchemaElements or 
	 * Features. Has to be of the same type as the second set.
	 * @param objects2 the second set of objects, e.g. SchemaElements or 
	 * Features. Has to be of the same type as the first set.
	 * @return A {@link Set} representing the difference of the Sets objects1 and 
	 * objects2, with the respective {@link ReasonSet}s applied.
	 */
	public Set<Object> getDifference(Set<Object> objects1, Set<Object> objects2) {
		// TODO
		return null;
	}

	/**
	 * @return the set1
	 */
	public ReasonSet getSet1() {
		return set1;
	}

	/**
	 * @return the set2
	 */
	public ReasonSet getSet2() {
		return set2;
	}

}
