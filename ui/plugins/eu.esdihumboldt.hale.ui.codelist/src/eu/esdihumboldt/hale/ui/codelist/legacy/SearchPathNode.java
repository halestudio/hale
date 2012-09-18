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

package eu.esdihumboldt.hale.ui.codelist.legacy;

import java.io.File;
import java.io.FilenameFilter;

import eu.esdihumboldt.hale.ui.util.tree.DefaultTreeNode;

/**
 * Tree node representing a search path
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class SearchPathNode extends DefaultTreeNode {

	private final String searchPath;

	/**
	 * Create a new search path node
	 * 
	 * @param searchPath the search path
	 */
	public SearchPathNode(String searchPath) {
		super(searchPath);

		this.searchPath = searchPath;

		// try to load path code lists
		File fPath = new File(searchPath);

		File[] candidates = fPath.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".xml"); //$NON-NLS-1$
			}
		});

		if (candidates == null) {
//			log.warn("No potential code list files found in " + searchPath);
		}
		else {
			// FIXME disabled
//			for (File candidate : candidates) {
//				try {
//					CodeList codeList = new XmlCodeList(new FileInputStream(candidate), candidate.toURI());
//					addChild(new CodeListNode(codeList)); //new DefaultTreeNode(codeList.getIdentifier()));
//				} catch (Throwable e) {
//					// ignore
//				}
//			}
		}
	}

	/**
	 * @return the searchPath
	 */
	public String getSearchPath() {
		return searchPath;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((searchPath == null) ? 0 : searchPath.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchPathNode other = (SearchPathNode) obj;
		if (searchPath == null) {
			if (other.searchPath != null)
				return false;
		}
		else if (!searchPath.equals(other.searchPath))
			return false;
		return true;
	}

}
