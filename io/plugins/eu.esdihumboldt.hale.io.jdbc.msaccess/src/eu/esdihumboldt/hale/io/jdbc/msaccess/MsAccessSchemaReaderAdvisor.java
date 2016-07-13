/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.io.jdbc.msaccess;

import java.util.regex.Pattern;

import eu.esdihumboldt.hale.io.jdbc.extension.JDBCSchemaReaderAdvisor;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

/**
 * TODO Type description
 * 
 * @author Arun
 */
public class MsAccessSchemaReaderAdvisor implements JDBCSchemaReaderAdvisor {

	@Override
	public String adaptPathForNamespace(String path) {
		if (path == null) {
			return null;
		}

		// extract file name from path
		int index = path.lastIndexOf("/");
		String name;
		if (index >= 0 && index + 1 < path.length()) {
			name = path.substring(index + 1).toLowerCase();
		}
		else {
			name = path.toLowerCase();
		}
		// remove extension
		if (name.endsWith(".mdb")) {
			name = name.replace(".mdb", "");
		}

		return name;
	}

	@Override
	public void configureSchemaCrawler(SchemaCrawlerOptions options) {
		options.setTableInclusionRule(new InclusionRule() {

			private static final long serialVersionUID = -1559715487368953641L;

			@Override
			public boolean test(String t) {
				final Pattern uca_metadata_TablePattern = Pattern.compile("\\w*.UCA_METADATA.\\w*");
				boolean isuca_metadata_Table = uca_metadata_TablePattern.matcher(t).matches();
				if (isuca_metadata_Table) {
					return false;
				}

				return true;
			}
		});
	}
}
