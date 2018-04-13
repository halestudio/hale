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

package eu.esdihumboldt.hale.io.groovy.snippets.impl;

import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.io.groovy.snippets.Snippet;
import eu.esdihumboldt.hale.io.groovy.snippets.SnippetReader;

/**
 * Base class for snippet readers.
 * 
 * @author Simon Templer
 */
public abstract class AbstractSnippetReader extends AbstractImportProvider
		implements SnippetReader {

	private Snippet snippet;

	private String identifier;

	private boolean autoReload = false;

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	public Snippet getSnippet() {
		return snippet;
	}

	/**
	 * @return the configured identifier
	 */
	protected String getIdentifier() {
		return identifier;
	}

	@Override
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the if auto reload is enabled
	 */
	protected boolean isAutoReload() {
		return autoReload;
	}

	@Override
	public void setAutoReload(boolean autoReload) {
		this.autoReload = autoReload;
	}

	/**
	 * Set the result snippet.
	 * 
	 * @param snippet the snippet to set
	 */
	protected void setSnippet(Snippet snippet) {
		this.snippet = snippet;
	}

	@Override
	public void storeConfiguration(Map<String, Value> configuration) {
		// store identifier
		configuration.put(PARAM_IDENTIFIER, Value.of(getIdentifier()));

		// store auto reload setting
		configuration.put(PARAM_AUTO_RELOAD, Value.of(isAutoReload()));

		super.storeConfiguration(configuration);
	}

	@Override
	public void setParameter(String name, Value value) {
		if (name.equals(PARAM_IDENTIFIER)) {
			setIdentifier(value.as(String.class));
		}
		else if (name.equals(PARAM_AUTO_RELOAD)) {
			setAutoReload(value.as(Boolean.class, false));
		}
		else {
			super.setParameter(name, value);
		}
	}

	@Override
	protected String getDefaultTypeName() {
		return "Groovy snippet";
	}

}
