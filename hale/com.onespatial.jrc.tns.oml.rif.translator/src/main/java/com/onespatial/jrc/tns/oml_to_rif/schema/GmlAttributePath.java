/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.schema;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class that represents a GML attribute path and provides support for comparing
 * two paths.
 * 
 * @author simonp
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
