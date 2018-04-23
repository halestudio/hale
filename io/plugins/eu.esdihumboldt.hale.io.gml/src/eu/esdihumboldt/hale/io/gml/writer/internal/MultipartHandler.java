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

package eu.esdihumboldt.hale.io.gml.writer.internal;

import java.net.URI;

import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;

/**
 * Interface for handlers that control writing instances to multiple XML/GML
 * files.
 * 
 * @author Florian Esser
 */
public interface MultipartHandler {

	/**
	 * Build the target file name for the given part
	 * 
	 * @param part Part
	 * @param originalTarget Output target originally provided
	 * @return The modified output target for the given part
	 */
	String getTargetFilename(InstanceCollection part, URI originalTarget);

	/**
	 * Extension point to provide a stream writer with special capabilities.
	 * Returns the provided writer unless overridden by the handler
	 * implementation.
	 * 
	 * @param writer Original writer
	 * @param target Output target
	 * @return The decorated writer
	 */
	default PrefixAwareStreamWriter getDecoratedWriter(PrefixAwareStreamWriter writer, URI target) {
		return writer;
	}

}
