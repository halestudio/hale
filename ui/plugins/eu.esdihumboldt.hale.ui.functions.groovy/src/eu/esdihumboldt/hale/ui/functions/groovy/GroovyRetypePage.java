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

package eu.esdihumboldt.hale.ui.functions.groovy;

import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.google.common.collect.ImmutableList;

import eu.esdihumboldt.hale.ui.util.groovy.SimpleGroovySourceViewerConfiguration;

/**
 * Configuration page for the Groovy Retype script.
 * 
 * @author Simon Templer
 */
public class GroovyRetypePage extends GroovyScriptPage {

	/**
	 * Default constructor.
	 */
	public GroovyRetypePage() {
		super();
		setTitle("Convert instances script");
		setDescription("Specify a Groovy script to build a target instance from a source instance");
	}

	@Override
	protected SourceViewerConfiguration createConfiguration() {
		return new SimpleGroovySourceViewerConfiguration(colorManager, ImmutableList.of(
				BINDING_BUILDER, BINDING_SOURCE, BINDING_TARGET));
	}

}
