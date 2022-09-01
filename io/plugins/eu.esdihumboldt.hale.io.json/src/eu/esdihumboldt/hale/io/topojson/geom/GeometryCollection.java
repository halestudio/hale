package json.topojson.geom;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Vector;

import json.graphic.Colorifier;
import json.graphic.Display;
import json.topojson.topology.Topology;

public class GeometryCollection extends Object{

	public Vector<Object> geometries;
	
	public GeometryCollection(){
		type = "GeometryCollection"; 
		geometries = new Vector<Object	>();
	}

	public void addGeometry(Object iObject){
		geometries.add(iObject);
	}

	@Override
	public java.lang.Object getProperties(){
		
		LinkedList<java.lang.Object> aProperties = new LinkedList<java.lang.Object>();
		
		for (Object iObj:geometries){
			aProperties.add(iObj.properties);
		}
		
		return aProperties;
	}
	
	@Override
	public int findMaxArcIndex() {
		int max = -1;
		for (Object object:geometries) {
			
			int index = object.findMaxArcIndex();
			if (index>max) {
				max = index;
			}
		
		}
		return max;
	}

	@Override
	public void fill(Topology iTopo, Display iDisplay, Colorifier iColorifier) {
		for (Object aObject:geometries){
			aObject.fill(iTopo, iDisplay, iColorifier);
		}
	}
	
}
