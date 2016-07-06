/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.groovy;

import java.util.Locale;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.functions.explanations.CreateExplanation;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Explanation for {@link GroovyCreate}
 * 
 * @author Simon Templer
 */
public class GroovyCreateExplanation extends CreateExplanation {

	@Override
	protected Class<?> getDefaultMessageClass() {
		return CreateExplanation.class;
	}

	@Override
	protected String getExplanation(Cell cell, boolean html, ServiceProvider services,
			Locale locale) {
		String superExplanation = super.getExplanation(cell, html, services, locale);

		String script = GroovyExplanation.getScript(cell);
		if (script != null) {
			String explanation = getMessage("main", locale, GroovyJoinExplanation.class) + "\n\n";

			if (html) {
				explanation = explanation.replaceAll("\n", "<br />");
				script = "<pre><code class=\"language-groovy\">" + script + "</code></pre>";
			}

			explanation = explanation + script;

			if (superExplanation == null) {
				return explanation;
			}
			else if (html) {
				return superExplanation + "<br />" + explanation;
			}
			else {
				return superExplanation + "\n" + explanation;
			}
		}
		else {
			return superExplanation;
		}

	}

}
