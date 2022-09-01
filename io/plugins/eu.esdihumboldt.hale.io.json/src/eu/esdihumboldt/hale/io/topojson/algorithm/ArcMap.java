package json.topojson.algorithm;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

import json.graphic.Display;
import json.topojson.topology.Arc;

public class ArcMap {
	
	TreeMap<Integer,TreeMap<Integer,Vector<Arc>>> _listArcs;
	public Vector<Arc> _arcs;
    int _shared;
    public HashMap<Integer,Integer> _rebuild;
	
	public ArcMap(){
		_listArcs = new TreeMap<Integer,TreeMap<Integer,Vector<Arc>>>();
		_arcs = new Vector<Arc>();
		_shared = 0;
	}
	
	public int getSize(){
		return _arcs.size();
	}
	
	public int getShared(){
		return _shared;
	}
	
	public Integer findArc(int iFHash, int iLHash, Arc iArc){
		TreeMap<Integer,Vector<Arc>> _subList = _listArcs.get(iFHash);
		if (_subList!=null) {
			Vector<Arc> aV = _subList.get(iLHash);
			if (aV!=null) {
				for (Arc aArc:aV) {
					int res = aArc.equals_reverse(iArc);
					if (res!=0) {
						_shared++;
						aArc.setShared();
						return res*(aArc.getId()+(res<0?1:0));
					}
				}
			}
		}
		return null;
	}
	
	public Integer findArc(Arc iArc){
		Integer res = findArc(iArc.firstHash(), iArc.lastHash(),iArc);
		if (res==null) {
			return findArc(iArc.lastHash(), iArc.firstHash(),iArc);
		} 
		return res;
	}
	
	public int addArc(Arc iArc){
		
		TreeMap<Integer,Vector<Arc>> _subList = _listArcs.get(iArc.firstHash());
		if (_subList==null) {
			_subList = new TreeMap<Integer,Vector<Arc>>();
			_listArcs.put(iArc.firstHash(),_subList);
		}
		Vector<Arc> aV = _subList.get(iArc.lastHash());
		if (aV==null) {
			aV = new Vector<Arc>();
			_subList.put(iArc.lastHash(), aV);
		}
		aV.add(iArc);
		
		iArc.setId(_arcs.size());
		_arcs.add(iArc);
		
		return iArc.getId();
	}

	public void draw(Display iDisp) {	
		for (Arc aArc:_arcs) {
			aArc.draw(iDisp);
		}
	}	
	
	public ArcMap rebuild(Integer[] iArcsToKeep) {
		
		HashMap<Integer,Integer> aRebuildIndexes = new HashMap<Integer,Integer>();
		ArcMap aMap = new ArcMap();
		
		for (int i=0; i<iArcsToKeep.length; i++){
			
			int iIndex = iArcsToKeep[i]<0?-iArcsToKeep[i]-1:iArcsToKeep[i] ;
			int iNewId = aMap.addArc(_arcs.get(iIndex));
			aRebuildIndexes.put(iArcsToKeep[i],iArcsToKeep[i]<0?-(iNewId+1):iNewId);
		}
		
		aMap._rebuild = aRebuildIndexes;
		
		return aMap;
	}
	
}
