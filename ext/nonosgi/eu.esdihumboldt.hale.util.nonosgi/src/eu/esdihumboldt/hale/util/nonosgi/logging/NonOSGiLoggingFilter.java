/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.util.nonosgi.logging;

import org.slf4j.Marker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Logging filter that suppresses messages that appear in context of an
 * environment w/o OSGi but can be ignored (in context of hale).
 * 
 * @author Simon Templer
 */
public class NonOSGiLoggingFilter extends TurboFilter {

	@Override
	public FilterReply decide(Marker marker, Logger logger, Level level, String format,
			Object[] params, Throwable t) {
		/*
		 *  Filter error on org.eclipse.xsd content type appearing since upgrade
		 *  to Eclipse Photon.
		 *  Hiding this because org.eclipse.xsd is not currently used in hale.
		 */
		if ("eu.esdihumboldt.hale.util.nonosgi.contenttype.ContentType".equals(logger.getName())) {
			if ("Could not create content describer for org.eclipse.xsd. Content type has been disabled."
					.equals(format)) {
				return FilterReply.DENY;
			}
		}

		// neutral on anything else
		return FilterReply.NEUTRAL;
	}

}
