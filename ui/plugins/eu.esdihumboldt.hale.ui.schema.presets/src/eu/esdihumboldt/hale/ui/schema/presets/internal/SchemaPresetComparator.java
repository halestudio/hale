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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.schema.presets.internal;

import java.util.Objects;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import eu.esdihumboldt.hale.ui.schema.presets.extension.SchemaPreset;

/**
 * Comparator for {@link SchemaPreset}s.
 * 
 * @author Simon Templer
 */
public class SchemaPresetComparator extends ViewerComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof SchemaPreset && e2 instanceof SchemaPreset) {
			return compare((SchemaPreset) e1, (SchemaPreset) e2);
		}

		return super.compare(viewer, e1, e2);
	}

	private int compare(SchemaPreset o1, SchemaPreset o2) {
		// first, test the name
		int result = o1.getName().compareToIgnoreCase(o2.getName());

		if (result == 0) {
			// then the version
			if (Objects.equals(o1.getVersion(), o2.getVersion())) {
				result = 0;
			}
			else if (o1.getVersion() == null) {
				result = -1;
			}
			else if (o2.getVersion() == null) {
				result = 1;
			}
			else {
				result = o1.getVersion().compareToIgnoreCase(o2.getVersion());
			}
		}

		if (result == 0) {
			// then the tag
			if (Objects.equals(o1.getTag(), o2.getTag())) {
				result = 0;
			}
			else if (o1.getTag() == null) {
				result = -1;
			}
			else if (o2.getTag() == null) {
				result = 1;
			}
			else {
				result = o1.getTag().compareToIgnoreCase(o2.getTag());
			}
		}

		return result;
	}

}
