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
 *     1Spatial PLC <http://www.1spatial.com>
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package com.onespatial.jrc.tns.oml_to_rif.schema;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class that represents a GML attribute path and provides support for comparing
 * two paths.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public class GmlAttributePath extends ArrayList<GmlAttribute> implements
        Comparable<GmlAttributePath>
{

    /**
     * Required for serialization of this class.
     */
    private static final long serialVersionUID = 806073544978386706L;

    /**
     * @see Comparable#compareTo(Object) which this overrides.
     * @param otherPath
     *            {@link GmlAttributePath} the path to compare with
     * @return int
     */
    @Override
    public int compareTo(GmlAttributePath otherPath)
    {
        int result = 0;
        Iterator<GmlAttribute> mine = iterator();
        Iterator<GmlAttribute> other = otherPath.iterator();

        while (mine.hasNext() && other.hasNext() && result == 0)
        {
            result = mine.next().compareTo(other.next());
        }

        if (result == 0)
        {
            if (mine.hasNext())
            {
                result = -1;
            }
            else
            {
                result = 1;
            }
        }

        return result;
    }

}
