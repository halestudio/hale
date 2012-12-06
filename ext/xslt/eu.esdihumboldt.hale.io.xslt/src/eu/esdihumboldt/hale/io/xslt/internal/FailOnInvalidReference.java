/*
 * Copyright (c) 2012 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.internal;

import org.apache.velocity.app.event.InvalidReferenceEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.util.introspection.Info;

/**
 * Velocity event handler that ensures that template merging fails if an invalid
 * reference is encountered.
 * 
 * @author Simon Templer
 */
public final class FailOnInvalidReference implements InvalidReferenceEventHandler {

	private void report(Info info, String reference) {
		throw new ParseErrorException("Error while merging template - invalid reference: "
				+ reference, info, reference);
	}

	@Override
	public boolean invalidSetMethod(Context context, String leftreference, String rightreference,
			Info info) {
		report(info, leftreference + "." + rightreference);
		return false;
	}

	@Override
	public Object invalidMethod(Context context, String reference, Object object, String method,
			Info info) {
		report(info, reference);
		return null;
	}

	@Override
	public Object invalidGetMethod(Context context, String reference, Object object,
			String property, Info info) {
		report(info, reference);
		return null;
	}
}