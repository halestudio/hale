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

package eu.esdihumboldt.cst.functions.groovy;

import java.text.MessageFormat;
import java.util.Locale;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;
import eu.esdihumboldt.hale.common.core.io.Text;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Explanation for groovy cells.
 * 
 * @author Kai Schwierczek
 */
public class GroovyExplanation extends AbstractCellExplanation implements GroovyConstants {

	@Override
	protected String getExplanation(Cell cell, boolean html, ServiceProvider services,
			Locale locale) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());

		String script = getScript(cell);

		if (target != null && script != null) {
			if (html)
				script = "<pre><code class=\"language-groovy\">" + script + "</code></pre>";
			String pre = MessageFormat.format(getMessage("pre", locale),
					formatEntity(target, html, true, locale));
			String post = MessageFormat.format(getMessage("post", locale),
					formatEntity(target, html, true, locale));
			if (html) {
				pre = pre.replaceAll("\n", "<br />");
				pre = pre + "<br /><br />";
				post = post.replaceAll("\n", "<br />");
				post = "<br />" + post;
			}
			else {
				pre = pre + "\n\n";
				post = "\n\n" + post;
			}
			return pre + script + post;
		}

		return null;
	}

	/**
	 * Get the script stored in a Groovy function cell.
	 * 
	 * @param cell the cell
	 * @return the Groovy script string or <code>null</code>
	 */
	public static String getScript(Cell cell) {
		Value scriptValue = CellUtil.getFirstParameter(cell, PARAMETER_SCRIPT);
		String script;
		// try retrieving as text
		Text text = scriptValue.as(Text.class);
		if (text != null) {
			script = text.getText();
		}
		else {
			// fall back to string value
			script = scriptValue.as(String.class);
		}
		return script;
	}
}
