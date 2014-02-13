/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.extension.annotation;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.ExtensionUtil;
import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.AnnotationDescriptor;

/**
 * Extension for cell annotations.
 * 
 * @author Simon Templer
 */
public class AnnotationExtension extends IdentifiableExtension<DefinedAnnotation> {

	private static final ALogger log = ALoggerFactory.getLogger(AnnotationExtension.class);

	/**
	 * The extension identifier.
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.hale.align.annotation";

	private static AnnotationExtension instance;

	/**
	 * Get the extension instance.
	 * 
	 * @return the extension singleton
	 */
	public static AnnotationExtension getInstance() {
		synchronized (AnnotationExtension.class) {
			if (instance == null)
				instance = new AnnotationExtension();
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	protected AnnotationExtension() {
		super(EXTENSION_ID);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected DefinedAnnotation create(String id, IConfigurationElement conf) {
		try {
			return new DefinedAnnotation(id,
					(Class<AnnotationDescriptor<?>>) ExtensionUtil.loadClass(conf, "descriptor"));
		} catch (Exception e) {
			log.error("Could not load annotation descriptor for type identifier " + id, e);
			return null;
		}
	}

	@Override
	protected String getIdAttributeName() {
		return "type";
	}

}
