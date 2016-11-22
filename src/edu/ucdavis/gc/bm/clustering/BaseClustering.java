package edu.ucdavis.gc.bm.clustering;

import java.util.List;
import java.util.Set;

public abstract class BaseClustering {

	private List<? extends Metricable> elements;

	private Double[][] mapDistances_elems;

	public BaseClustering (){
		
	}
	
	public BaseClustering(List<? extends Metricable> elements){
		setElements(elements);
		setMapDistances();
	}
	
	public BaseClustering(Double[][] mapDistances_elems){
		this.mapDistances_elems = mapDistances_elems; 
	}

	private void setElements(List<? extends Metricable> elements) {
		this.elements = elements;
	}

	private  void setMapDistances() {
		if (null == mapDistances_elems) {
			mapDistances_elems = new Double[elements.size()][elements.size()];
		}
		for (int i = 0; i < elements.size() ; i++) {
			for (int j = 0; j <= i; j++) {				
				mapDistances_elems[i][j] = elements.get(i).distanceTo(elements.get(j));
				mapDistances_elems[j][i] =  mapDistances_elems[i][j];
			}
		}
	}
	
	public Double[][] getMapDistances_elems(){
		return this.mapDistances_elems;
	}
	
	
	public List<? extends Metricable> getElements(){
		return this.elements;
	}
	
	
	/**
	 * The method creates nC clusters;
	 * if nC > m=size(setOfElements); creates m clusters, i.e. every element represents its own cluster
	 * @param nC
	 */
	public abstract void process(int nC);
	/**
	 * The method creates clusters for which the distance between them are less than cutOff_dist
	 * @param cutOff_dist
	 */
	public abstract void process(double cutOff_dist);
	/**
	 * The getter method, returns the list of clusters. 
	 * @return
	 */
	public abstract List<Set<Integer>>  getClusters();
	
}
