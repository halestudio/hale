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

package eu.esdihumboldt.hale.io.instancebuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

import org.codehaus.groovy.control.CompilerConfiguration;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceReader;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;

/**
 * Instance reader that runs a Groovy script against an instance builder.
 * 
 * @author Simon Templer
 */
public class InstanceBuilderReader extends AbstractInstanceReader {

	private InstanceCollection instances;

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Run instance builder", ProgressIndicator.UNKNOWN);
		try {
			CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
			compilerConfiguration.setScriptBaseClass(DelegatingScript.class.getName());

			// Configure the GroovyShell and pass the compiler configuration.
			GroovyShell shell = new GroovyShell(getClass().getClassLoader(), new Binding(),
					compilerConfiguration);
			DelegatingScript script;
			try (InputStream in = getSource().getInput();
					InputStreamReader reader = new InputStreamReader(in, getCharset())) {
				script = (DelegatingScript) shell.parse(reader);
			}

			InstanceBuilder builder = new InstanceBuilder();
			builder.setTypes(getSourceSchema()); // apply schema
			script.setDelegate(builder);
			Object res = script.run();

			if (res == null) {
				throw new IllegalStateException("Null returned by script");
			}
			else if (res instanceof InstanceCollection) {
				instances = (InstanceCollection) res;
			}
			else if (res instanceof Instance) {
				instances = new DefaultInstanceCollection(Collections.singleton((Instance) res));
			}
			else {
				throw new IllegalStateException(
						"Unrecognised return type: " + res.getClass().getName());
			}

			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.setSuccess(false);
			reporter.error("Error running instance builder", e);
		} finally {
			progress.end();
		}
		return reporter;
	}

	@Override
	protected String getDefaultTypeName() {
		return "Instance builder script";
	}

	@Override
	public InstanceCollection getInstances() {
		return instances;
	}

}
