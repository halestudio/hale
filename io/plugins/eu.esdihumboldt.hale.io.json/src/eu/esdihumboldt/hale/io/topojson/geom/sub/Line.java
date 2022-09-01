package json.topojson.geom.sub;

import json.geojson.objects.Bounding;
import json.topojson.algorithm.ArcMap;

public class Line extends Entity {

	public Line(Position[] iPoints, Bounding iBound) {
		super(iPoints,iBound);
	}

	@Override
	public void cut(ArcMap iMap) {
		
		// _pattern has been computed previously
		// by join algorithm;
		// first last points must be set by the algorithm
		_pattern[0]=1;
		_pattern[_pattern.length-1]=1;
		
		int count=0;
		
		while (count<_pattern.length){
			count++;
		}
		
	}

	@Override
	public Entity clone() {
		
		Position[] aP = new Position[_points.length];
		for (int i=0; i<_points.length; i++){
			aP[i] = (Position) _points[i].clone();
		}
		
		Bounding aBnd = _bound.clone();
		int[] aPat = _pattern.clone();
		
		Entity aEnt = new Ring(aP,aBnd);
		aEnt._pattern = aPat;
		aEnt._indexes = _indexes.clone();
		
		// not cloning the ref
		aEnt._ref = _ref;
		
		return aEnt;
		
	}
		

}
