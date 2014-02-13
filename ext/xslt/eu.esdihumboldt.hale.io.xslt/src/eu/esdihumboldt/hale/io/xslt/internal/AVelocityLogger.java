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

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Logging facade for Velocity.
 * 
 * @author Simon Templer
 */
public class AVelocityLogger implements LogChute {

	private static final ALogger log = ALoggerFactory.getMaskingLogger(AVelocityLogger.class, null);

	@Override
	public void init(RuntimeServices rs) throws Exception {
		// ignore
	}

	@Override
	public void log(int level, String message) {
		switch (level) {
		case TRACE_ID:
			log.trace(message);
			break;
		case DEBUG_ID:
			log.debug(message);
			break;
		case INFO_ID:
			log.info(message);
			break;
		case WARN_ID:
			log.warn(message);
			break;
		case ERROR_ID:
		default:
			log.error(message);
			break;
		}
	}

	@Override
	public void log(int level, String message, Throwable t) {
		switch (level) {
		case TRACE_ID:
			log.trace(message, t);
			break;
		case DEBUG_ID:
			log.debug(message, t);
			break;
		case INFO_ID:
			log.info(message, t);
			break;
		case WARN_ID:
			log.warn(message, t);
			break;
		case ERROR_ID:
		default:
			log.error(message, t);
			break;
		}
	}

	@Override
	public boolean isLevelEnabled(int level) {
		switch (level) {
		case TRACE_ID:
			return log.isTraceEnabled();
		case DEBUG_ID:
			return log.isDebugEnabled();
		case INFO_ID:
			return log.isInfoEnabled();
		case WARN_ID:
			return log.isWarnEnabled();
		case ERROR_ID:
		default:
			return log.isErrorEnabled();
		}
	}

}
