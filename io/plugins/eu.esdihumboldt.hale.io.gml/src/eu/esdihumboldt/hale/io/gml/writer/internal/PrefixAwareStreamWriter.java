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

import javax.annotation.Nullable;
import javax.xml.stream.XMLStreamWriter;

/**
 * Interface extending the {@link XMLStreamWriter} interface with a possibility
 * to find out which namespace was set for a prefix.
 * 
 * @author Simon Templer
 */
public interface PrefixAwareStreamWriter extends XMLStreamWriter {

	/**
	 * Get the namespace associated with the given prefix using
	 * {@link XMLStreamWriter#setPrefix(String, String)}.
	 * 
	 * @param prefix the prefix
	 * @return the namespace or <code>null</code>
	 */
	@Nullable
	String getNamespace(String prefix);

}
