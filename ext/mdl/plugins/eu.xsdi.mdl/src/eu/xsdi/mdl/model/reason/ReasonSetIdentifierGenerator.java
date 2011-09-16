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


/**
 * A Generator that can be used to manage ReasonSet identifiers. Note that it 
 * resets after generating 676 identifiers (let's hope you never need a set that 
 * needs that many identifiers, my current use cases don't require more than 
 * ~4, after all you will need only one ID per level of nesting of 
 * {@link ReasonSet}s). 
 * You should use a {@link ReasonSetIdentifierGenerator} per
 * {@link ReasonSet} you are creating.
 * 
 * @author Thorsten Reitz, thor@xsdi.eu
 * @version $Id$ 
 * @since 0.1.0
 */
public class ReasonSetIdentifierGenerator {
	
	private long id = 0;
	
	/**
	 * Default constructor for a {@link ReasonSetIdentifierGenerator}.
	 */
	public ReasonSetIdentifierGenerator() {
		
	}
	
	/**
	 * @return a letter indicating the next possible ReasonSetIdentifier. The 
	 * first 26 identifiers that are returned are a...z, the generator 
	 * will continue with aa to az, and finally za to zz.
	 */
	public synchronized String next() {
		String result = "";
		if (id > 25) {
			result += (char)((id / 26) + 96);
		}
		result += (char)((id % 26) + 97);
		id++;
		if (id > 675) {
			this.reset();
		}
		return result;
	}
	
	/**
	 * Resets the {@link ReasonSetIdentifierGenerator}, so it will start with 
	 * 'a' again.
	 */
	public synchronized void reset() {
		id = 0;
	}

}
