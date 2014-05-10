/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.inspire.apps.templates;

import java.io.File;

import org.eclipse.equinox.app.IApplicationContext;

import eu.esdihumboldt.hale.common.app.AbstractApplication;

/**
 * Application that generates a mapping of default values.
 * 
 * @author Simon Templer
 */
public class GenerateTemplatesApplication extends AbstractApplication<GenerateTemplatesContext> {

	@Override
	protected Object run(GenerateTemplatesContext executionContext, IApplicationContext appContext) {
		new GenerateTemplates(executionContext).generate();

		return EXIT_OK;
	}

	@Override
	protected void processParameter(String param, String value,
			GenerateTemplatesContext executionContext) throws Exception {
		switch (param) {
		case "-target":
			executionContext.setTargetDir(new File(value));
			break;
		}
	}

	@Override
	protected void processFlag(String arg, GenerateTemplatesContext executionContext) {
		switch (arg) {
		case "-explicit":
			executionContext.setExplicit(true);
		}
	}

	@Override
	protected GenerateTemplatesContext createExecutionContext() {
		return new GenerateTemplatesContext();
	}
}
