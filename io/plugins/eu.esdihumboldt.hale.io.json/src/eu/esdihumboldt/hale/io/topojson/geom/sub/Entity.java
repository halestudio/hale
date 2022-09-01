package json.topojson.geom.sub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import json.geojson.objects.Bounding;
import json.topojson.algorithm.ArcMap;


public abstract class Entity {

	Position[] _points;
	Bounding _bound;
	int[] _points_hash;
	int[] _pattern;
	public int[] _indexes;
	ArcMap _ref;
	
	public Entity(Position[] iPoints, Bounding iBound){
		_points = iPoints;
		_points_hash = new int[iPoints.length];
		for (int i=0; i<iPoints.length; i++){
			_points_hash[i] = iPoints[i].hashCode();
		}
		_bound = iBound;
		_pattern = new int[_points.length];
	}
	
	public abstract Entity clone();
	
	public void rebuildIndexes(ArcMap iMap){
		_ref = iMap;
		for (int i=0; i<_indexes.length; i++){
			_indexes[i] = iMap._rebuild.get(_indexes[i]);
		}
	}
	
	/**
	 * Return Arcs indexes in ArcMap
	 * @param iMap
	 * @return
	 */
	public abstract void cut(ArcMap iMap);
	
	public int[] getIndexes(){
		return _indexes;
	}
	
	protected int[] vectorToInt(Vector<Integer> aAllIndexes){
		int[] aIndexes = new int[aAllIndexes.size()];
		for (int i=0; i<aAllIndexes.size(); i++){
			aIndexes[i] = aAllIndexes.get(i);
		}
		return aIndexes;
	}
	
	public static HashSet<Position> join(List<Entity> iEntities){
		
		HashSet<Position> aJoinPoints = new HashSet<Position>();
		
		int count=0;
		int match_bound = 0;
		int count_all_points=0;
		
		HashMap<Bounding,Vector<Entity>> aAllPoints = new HashMap<Bounding,Vector<Entity>>();
		for (Entity aEntity:iEntities) {
			Vector<Entity> aVE = aAllPoints.get(aEntity._bound);
			if (aVE==null) {
				aVE = new Vector<Entity>();
				aAllPoints.put(aEntity._bound,aVE);
			} 
			aVE.add(aEntity);
			
			count_all_points+=aEntity._points.length;
		}
			
		for (Entity aEntity:iEntities) {
		
			for (Entry<Bounding,Vector<Entity>> aBoundSet : aAllPoints.entrySet()) {
				
				if (aEntity._bound!=aBoundSet.getKey()) {
					
					if (aEntity._bound.partlyIn(aBoundSet.getKey(),2.0)) {
						
						match_bound++;
						
						int[] pattern = new int[aEntity._points.length];
						
						int num=0;
						for (Position aP:aEntity._points) {
							for (Entity aEnt:aBoundSet.getValue()) {
								for (int val:aEnt._points_hash) {
									if (val==aP.hashCode()) {
										pattern[num] = 1;
									}
								}
							}
							num++;		
						}	
						
						int pos = 0;
						
						for (num=0;num<pattern.length;num++) {
							if ((pattern[num]==1) && (pos==0)) {
								pos = 1;
								aJoinPoints.add(aEntity._points[num]);
								aEntity._pattern[num] = 1;
							} else if ((pattern[num]==0) && (pos==1)) {
								pos = 0;
								aJoinPoints.add(aEntity._points[num-1]);
								aEntity._pattern[num-1] = 1;
							}
						}
						
					}
				} 
				
			}
			
			
			
			count++;
			//System.out.println("Match bound:"+match_bound+" found:"+aJoinPoints.size()+"/"+count_all_points+" "+count+"/"+iEntities.size());
		}
		
		return aJoinPoints;
			
	}
	
}
