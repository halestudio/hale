/*
 * Copyright (c) 2016 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.groovy.helpers

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

import javax.annotation.Nullable

import eu.esdihumboldt.cst.functions.groovy.helper.spec.SpecBuilder
import groovy.transform.CompileStatic

/**
 * Cryptographic helper functions. 
 * 
 * @author Simon Templer
 */
class CryptoHelpers {

	/**
	 * Specification for the sha256 function
	 */
	public static final eu.esdihumboldt.cst.functions.groovy.helper.spec.Specification _sha256_spec = SpecBuilder.newSpec( //
	description: 'Calculate a SHA-256 hash as a hex-encoded string.', //
	result: 'Hex-encoded string representation of the hash. For a null input, null will be returned.') { //
		input('Input to hash, usually a byte array or String. For Strings the UTF-8 encoded bytes will be hashed.') }

	@CompileStatic
	@Nullable
	static String _sha256(def input) {
		if (input == null) {
			return null
		}

		byte[] data
		if (input instanceof String || input instanceof GString) {
			data = input.toString().getBytes(StandardCharsets.UTF_8)
		}
		else {
			// generic conversion to bytes
			data = input as byte[]
		}

		if (data == null) {
			throw new IllegalStateException('Invalid input for hash function ' + input)
		}

		def digest = MessageDigest.getInstance('SHA-256')
		digest.update(data)
		digest.digest().encodeHex()
	}

}
