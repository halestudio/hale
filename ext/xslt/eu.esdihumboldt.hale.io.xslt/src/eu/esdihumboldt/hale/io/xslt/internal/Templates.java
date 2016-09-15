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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

/**
 * The main XSLT templates.
 * 
 * @author Simon Templer
 */
public class Templates extends ResourceLoader {

	/**
	 * The root template containing the
	 */
	public static final String ROOT = "transform.xsl";

	/**
	 * Copy the main templates to the given template folder.
	 * 
	 * @param templateFolder the template folder
	 * @throws IOException if copying the templates fails
	 */
	public static void copyTemplates(File templateFolder) throws IOException {
		copyTemplate(templateFolder, ROOT);
	}

	/**
	 * Copy a template to the given target folder.
	 * 
	 * @param targetFolder the target folder
	 * @param templateFileName the template file name, must lie next to the
	 *            {@link Templates} class
	 * @throws IOException if copying the template fails
	 */
	private static void copyTemplate(final File targetFolder, final String templateFileName)
			throws IOException {
		new ByteSource() {

			@Override
			public InputStream openStream() throws IOException {
				return Templates.class.getResourceAsStream(templateFileName);
			}

		}.copyTo(Files.asByteSink(new File(targetFolder, templateFileName)));
	}

	@Override
	public InputStream getResourceStream(String source) throws ResourceNotFoundException {
		try {
			return Templates.class.getResourceAsStream(source);
		} catch (Exception e) {
			throw new ResourceNotFoundException(e);
		}
	}

	@Override
	public boolean isSourceModified(Resource resource) {
		return false;
	}

	@Override
	public long getLastModified(Resource resource) {
		return 0;
	}

	@Override
	public void init(ExtendedProperties configuration) {
		// do nothing
	}

}
