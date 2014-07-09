/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.io.source.internal;

import org.eclipse.core.runtime.content.IContentType;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.ui.io.ImportSource;

/**
 * Interface for {@link ImportSource} factories provided by the
 * {@link ImportSourceExtension}.
 * 
 * @author Simon Templer
 */
public interface ImportSourceFactory extends ExtensionObjectFactory<ImportSource<?>> {

	/**
	 * Get the I/O provider factory type supported by the import source.
	 * 
	 * @return the I/O provider factory type
	 */
	public Class<? extends ImportProvider> getProviderType();

	/**
	 * Get the source description.
	 * 
	 * @return the description or <code>null</code>
	 */
	public String getDescription();

	/**
	 * Get the content type supported by the source. If <code>null</code> any
	 * content type is supported.
	 * 
	 * @return the supported content type or <code>null</code>
	 */
	public IContentType getContentType();

}
