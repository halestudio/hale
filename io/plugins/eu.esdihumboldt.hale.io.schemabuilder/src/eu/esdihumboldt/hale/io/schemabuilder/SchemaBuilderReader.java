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

package eu.esdihumboldt.hale.io.schemabuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.codehaus.groovy.control.CompilerConfiguration;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder;
import eu.esdihumboldt.hale.common.schema.io.impl.AbstractSchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;

/**
 * Schema reader that runs a Groovy script against a schema builder.
 * 
 * @author Simon Templer
 */
public class SchemaBuilderReader extends AbstractSchemaReader {

	private Schema schema;

	@Override
	public Schema getSchema() {
		return schema;
	}

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Run schema builder", ProgressIndicator.UNKNOWN);
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

			SchemaBuilder builder = new SchemaBuilder();
			script.setDelegate(builder);
			Object res = script.run();

			if (res == null) {
				throw new IllegalStateException("Null returned by script");
			}
			else if (res instanceof Schema) {
				schema = (Schema) res;
			}
			else if (res instanceof TypeIndex) {
				DefaultSchema s = new DefaultSchema(null, getSource().getLocation());
				for (TypeDefinition type : ((TypeIndex) res).getTypes()) {
					s.addType(type);
				}
				schema = s;
			}
			else if (res instanceof TypeDefinition) {
				DefaultSchema s = new DefaultSchema(null, getSource().getLocation());
				s.addType((TypeDefinition) res);
				schema = s;
			}
			else {
				throw new IllegalStateException(
						"Unrecognised return type: " + res.getClass().getName());
			}

			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.setSuccess(false);
			reporter.error("Error running schema builder", e);
		} finally {
			progress.end();
		}
		return reporter;
	}

	@Override
	protected String getDefaultTypeName() {
		return "Schema builder script";
	}

}
