package json.topojson.geom;

import java.awt.Color;

import json.graphic.Colorifier;
import json.graphic.Display;
import json.topojson.topology.Topology;

public abstract class Object {

	Integer id;
	protected String type;
	protected java.lang.Object properties;

	public void setId(int iId){
		id = iId;
	}
	
	public void setProperties(java.lang.Object iProperties){
		properties = iProperties;
	}
	
	public java.lang.Object getProperties(){
		return properties;
	}
	
	public abstract int findMaxArcIndex();
	
	public abstract void fill(Topology iTopo, Display iDisplay, Colorifier iColorifier );
	
	
	
}
