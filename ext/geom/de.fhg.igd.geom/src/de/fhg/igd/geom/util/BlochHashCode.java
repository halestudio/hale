/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */

package de.fhg.igd.geom.util;

import java.io.Serializable;

/**
 * <p>
 * This class provides a simple and effective hash algorithm taken from Bloch,
 * "Effective Java: Programming Language Guide, p33".
 * </p>
 * <p>
 * Explanation, excerpt from Bloch, "Effective Java: Programming Language Guide,
 * p33
 * </p>
 * <p>
 * A good hash function tends to produce unequal hash codes for unequal objects.
 * This is exactly what is meant by the third provision of the hashCode
 * contract. Ideally, a hash function should distribute any reasonable
 * collection of unequal instances uniformly across all possible hash values.
 * Achieving this ideal can be extremely difficult.
 * </p>
 * <p>
 * Luckily it is not too difficult to achieve a fair approximation. Here is a
 * simple recipe:
 * </p>
 * <p>
 * 1. Store some constant nonzero value, say 17, in an int variable called
 * result. 2. For each significant field f in your object (each field taken into
 * account by the equals method, that is), do the following: a. Compute an int
 * hash code c for the field: i. If the field is a boolean, compute (f ? 0 : 1).
 * ii. If the field is a byte, char, short, or int, compute (int)f. iii. If the
 * field is a long, compute (int)(f ^ (f >>> 32)). iv. If the field is a float
 * compute Float.floatToIntBits(f). v. If the field is a double, compute
 * Double.doubleToLongBits(f), and then hash the resulting long as in step
 * 2.a.iii. vi. If the field is an object reference and this class's equals
 * method compares the field by recursively invoking equals, recursively invoke
 * hashCode on the field. If a more complex comparison is required, compute a
 * "canonical representation" for this field and invoke hashCode on the
 * canonical representation. If the value of the field is null, return 0 (or
 * some other constant, but 0 is traditional). vii. If the field is an array,
 * treat it as if each element were a separate field. That is, compute a hash
 * code for each significant element by applying these rules recursively, and
 * combine these values as described in step 2.b. b. Combine the hash code c
 * computed in step a into result as follows: result = 37*result + c; 3. Return
 * result. 4. When you are done writing the hashCode method, ask yourself
 * whether equal instances have equal hash codes. If not, figure out why and fix
 * the problem.
 * </p>
 * <p>
 * It is acceptable to exclude redundant fields from the hash code computation.
 * In other words, it is acceptable to exclude any field whose value can be
 * computed from fields that are included in the computation. It is required
 * that you exclude any fields that are not used in equality comparisons.
 * Failure to exclude these fields may result in a violation of the second
 * provision of the hashCode contract.
 * </p>
 * <p>
 * A nonzero initial value is used in step 1, so the hash value will be affected
 * by initial fields whose hash value, as computed in step 2.a, is zero. If zero
 * was used as the initial value in step 1, the overall hash value would be
 * unaffected by any such initial fields, which could increase collisions. The
 * value 17 is arbitrary.
 * </p>
 * <p>
 * The multiplication in step 2.b makes the hash value depend on the order of
 * the fields, which results in a much better hash function if the class
 * contains multiple similar fields. For example, if the multiplication were
 * omitted from a String hash function built according to this recipe, all
 * anagrams would have identical hash codes. The multiplier 37 was chosen
 * because it is an odd prime. If it was even and the multiplication overflowed,
 * information Effective Java: Programming Language Guide 34 would be lost
 * because multiplication by two is equivalent to shifting. The advantages of
 * using a prime number are less clear, but it is traditional to use primes for
 * this purpose.
 * </p>
 */
public class BlochHashCode implements Serializable {

	/**
	 * The serial version UID
	 */
	private static final long serialVersionUID = -6432905297232026617L;

	/**
	 * a non-zero positive prime constant
	 */
	public final static int HASH_CONSTANT = 17;

	private volatile int myHash;

	/**
	 * Default constructor
	 */
	public BlochHashCode() {
		myHash = HASH_CONSTANT;
	}

	/**
	 * Adds a value
	 * 
	 * @param val the value to add
	 */
	public void add(int val) {
		myHash = addFieldToHash(myHash, hashCode(val));
	}

	/**
	 * Adds a value
	 * 
	 * @param val the value to add
	 */
	public void add(char val) {
		myHash = addFieldToHash(myHash, hashCode(val));
	}

	/**
	 * Adds a value
	 * 
	 * @param val the value to add
	 */
	public void add(float val) {
		myHash = addFieldToHash(myHash, hashCode(val));
	}

	/**
	 * Adds a value
	 * 
	 * @param val the value to add
	 */
	public void add(short val) {
		myHash = addFieldToHash(myHash, hashCode(val));
	}

	/**
	 * Adds a value
	 * 
	 * @param val the value to add
	 */
	public void add(double val) {
		myHash = addFieldToHash(myHash, hashCode(val));
	}

	/**
	 * Adds a value
	 * 
	 * @param val the value to add
	 */
	public void add(boolean val) {
		myHash = addFieldToHash(myHash, hashCode(val));
	}

	/**
	 * Adds a value
	 * 
	 * @param val the value to add
	 */
	public void add(long val) {
		myHash = addFieldToHash(myHash, hashCode(val));
	}

	/**
	 * Adds an object
	 * 
	 * @param object the object to add
	 */
	public void add(Object object) {
		myHash = addFieldToHash(myHash, hashCode(object));
	}

	/**
	 * Resets the hash
	 */
	public void reset() {
		this.myHash = HASH_CONSTANT;
	}

	/**
	 * Adds a hash value to the current hash
	 * 
	 * @param hashToAdd the hash code that shall be added
	 */
	public void addHash(int hashToAdd) {
		this.myHash = BlochHashCode.addHashToHash(this.myHash, hashToAdd);
	}

	/**
	 * @return the current hash
	 */
	public int getHash() {
		return this.myHash;
	}

	/**
	 * @return a hash code for the given field (see explanation above)
	 * @param field the field to calculate the hash code for
	 */
	public static int hashCode(int field) {
		return field;
	}

	/**
	 * @return a hash code for the given field (see explanation above)
	 * @param field the field to calculate the hash code for
	 */
	public static int hashCode(char field) {
		return field;
	}

	/**
	 * @return a hash code for the given field (see explanation above)
	 * @param field the field to calculate the hash code for
	 */
	public static int hashCode(String field) {
		return field.hashCode();
	}

	/**
	 * @return a hash code for the given field (see explanation above)
	 * @param field the field to calculate the hash code for
	 */
	public static int hashCode(byte field) {
		return field;
	}

	/**
	 * @return a hash code for the given field (see explanation above)
	 * @param field the field to calculate the hash code for
	 */
	public static int hashCode(short field) {
		return field;
	}

	/**
	 * @return a hash code for the given field (see explanation above)
	 * @param field the field to calculate the hash code for
	 */
	public static int hashCode(double field) {
		return hashCode(Double.doubleToLongBits(field));
	}

	/**
	 * @return a hash code for the given field (see explanation above)
	 * @param field the field to calculate the hash code for
	 */
	public static int hashCode(long field) {
		return (int) (field ^ (field >>> 32));
	}

	/**
	 * @return a hash code for the given field (see explanation above)
	 * @param field the field to calculate the hash code for
	 */
	public static int hashCode(boolean field) {
		return (field ? 0 : 1);
	}

	/**
	 * @return a hash code for the given field (see explanation above)
	 * @param field the field to calculate the hash code for
	 */
	public static int hashCode(float field) {
		return Float.floatToIntBits(field);
	}

	/**
	 * @return a hash code for the given field (see explanation above)
	 * @param field the field to calculate the hash code for
	 */
	public static int hashCode(Object field) {
		return field == null ? 0 : field.hashCode();
	}

	/**
	 * Convenience method for adding a new hash to a given hash code.
	 * 
	 * @param currentHash the current hash code
	 * @param hashToAdd the hash code that shall be added to the current hash
	 * @return the new hash
	 */
	public static int addHashToHash(int currentHash, int hashToAdd) {
		return 37 * currentHash + hashToAdd;
	}

	/**
	 * Convenience method for adding a field to a given hash code.
	 * 
	 * @param currentHash the current hash code
	 * @param fieldToAdd the field whose hash code shall be added to the current
	 *            hash
	 * @return the new hash including the field's hash
	 */
	public static int addFieldToHash(int currentHash, int fieldToAdd) {
		return 37 * currentHash + hashCode(fieldToAdd);
	}

	/**
	 * Convenience method for adding a field to a given hash code.
	 * 
	 * @param currentHash the current hash code
	 * @param fieldToAdd the field whose hash code shall be added to the current
	 *            hash
	 * @return the new hash including the field's hash
	 */
	public static int addFieldToHash(int currentHash, char fieldToAdd) {
		return 37 * currentHash + hashCode(fieldToAdd);
	}

	/**
	 * Convenience method for adding a field to a given hash code.
	 * 
	 * @param currentHash the current hash code
	 * @param fieldToAdd the field whose hash code shall be added to the current
	 *            hash
	 * @return the new hash including the field's hash
	 */
	public static int addFieldToHash(int currentHash, byte fieldToAdd) {
		return 37 * currentHash + hashCode(fieldToAdd);
	}

	/**
	 * Convenience method for adding a field to a given hash code.
	 * 
	 * @param currentHash the current hash code
	 * @param fieldToAdd the field whose hash code shall be added to the current
	 *            hash
	 * @return the new hash including the field's hash
	 */
	public static int addFieldToHash(int currentHash, short fieldToAdd) {
		return 37 * currentHash + hashCode(fieldToAdd);
	}

	/**
	 * Convenience method for adding a field to a given hash code.
	 * 
	 * @param currentHash the current hash code
	 * @param fieldToAdd the field whose hash code shall be added to the current
	 *            hash
	 * @return the new hash including the field's hash
	 */
	public static int addFieldToHash(int currentHash, double fieldToAdd) {
		return 37 * currentHash + hashCode(fieldToAdd);
	}

	/**
	 * Convenience method for adding a field to a given hash code.
	 * 
	 * @param currentHash the current hash code
	 * @param fieldToAdd the field whose hash code shall be added to the current
	 *            hash
	 * @return the new hash including the field's hash
	 */
	public static int addFieldToHash(int currentHash, float fieldToAdd) {
		return 37 * currentHash + hashCode(fieldToAdd);
	}

	/**
	 * Convenience method for adding a field to a given hash code.
	 * 
	 * @param currentHash the current hash code
	 * @param fieldToAdd the field whose hash code shall be added to the current
	 *            hash
	 * @return the new hash including the field's hash
	 */
	public static int addFieldToHash(int currentHash, long fieldToAdd) {
		return 37 * currentHash + hashCode(fieldToAdd);
	}

	/**
	 * Convenience method for adding a field to a given hash code.
	 * 
	 * @param currentHash the current hash code
	 * @param fieldToAdd the field whose hash code shall be added to the current
	 *            hash
	 * @return the new hash including the field's hash
	 */
	public static int addFieldToHash(int currentHash, boolean fieldToAdd) {
		return 37 * currentHash + hashCode(fieldToAdd);
	}

	/**
	 * Convenience method for adding a field to a given hash code.
	 * 
	 * @param currentHash the current hash code
	 * @param fieldToAdd the field whose hash code shall be added to the current
	 *            hash
	 * @return the new hash including the field's hash
	 */
	public static int addFieldToHash(int currentHash, Object fieldToAdd) {
		return 37 * currentHash + hashCode(fieldToAdd);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof BlochHashCode && ((BlochHashCode) o).getHash() == this.myHash ? true
				: false;
	}

	@Override
	public int hashCode() {
		return this.getHash();
	}

	@Override
	public String toString() {
		return "[BlochHashCode with hash value= " + this.myHash + "]";
	}
}
