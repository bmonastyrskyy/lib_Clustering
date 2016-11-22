package edu.ucdavis.gc.bm.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AddRedundancyClustering extends BaseClustering{

	/**
	 * start clusters - non-redundant clusters 
	 */
	private List<Set<Integer>> start_clusters;
	
	/**
	 * final clusters - redundant clusters
	 */
	private List<Set<Integer>> final_clusters;

	public AddRedundancyClustering(List<Set<Integer>> start_clusters, List<Metricable> elements){
		super(elements);
		this.start_clusters = start_clusters;
	}
	/**
	 * The method doesn't alter start clusters.
	 */
	@Override
	public void process(int nC) {
		// do nothing 
	}

	/**
	 * The method starting from the non-redundant clusters adds to them elements <br>
	 * from the other clusters which are closer then a given cutOff.
	 * @param cutoff   
	 */
	@Override
	public void process(double cutOff) {
		List<Set<Integer>> tmp = new ArrayList<Set<Integer>>();
		for(Set<Integer> start_cluster : start_clusters){
			//System.out.println(++count);
			tmp.add(processPerCluster(start_cluster, cutOff));
		}
		this.final_clusters = this.filterClusters(tmp);
	}
	
	private List<Set<Integer>> filterClusters(List<Set<Integer>> tmp){
		Set<Integer> indexesToRemove = new TreeSet<Integer>();
		for(int i = 1; i < tmp.size(); i++){
			for(int j = 0; j < i; j++){
				if(tmp.get(i).containsAll(tmp.get(j)) &&
						tmp.get(j).containsAll(tmp.get(i))){
					indexesToRemove.add(i);
				}
			}
		}
		List<Set<Integer>> result = new ArrayList<Set<Integer>>();
		for(int i = 0; i < tmp.size(); i++){
			if(! indexesToRemove.contains(i)){
				result.add(tmp.get(i));
			}
		}
		return result;
	}
	
	private Set<Integer>  processPerCluster(Set<Integer> start_cluster, double cutOff){
		Set<Integer> result = new TreeSet<Integer>();
		result.addAll(start_cluster);
		if(calScoreCluster(result) > cutOff){
			return result;
		}
		List<Integer> i_s =  rankIndexes(start_cluster);
		for(int i : i_s){
			result.add(i);
			if(calScoreCluster(result) > cutOff){
				result.remove(i);
				return result;
			}
		}
		return result;
	}
	
	private Double calScoreCluster(Set<Integer> cluster){
		double result = 0.0;
		int count = 0;
		for(int i : cluster){
			for(int j : cluster){
				if(i <= j){
					continue;
				}
				result += super.getMapDistances_elems()[i][j];
				count++;
			}
		}
		return (double)result/count;
	}
	
	/**
	 * The method returns the List of indexes of elements sorted by distance to the cluster  
	 * @param start_cluster
	 * @return
	 */
	private List<Integer> rankIndexes(Set<Integer> start_cluster){
		HashMap<Integer,Double> hashDist = new HashMap<Integer,Double>();
		for (int i = 0; i < super.getElements().size(); i++){
			if(start_cluster.contains(i)){
				continue;
			}
			hashDist.put(i, this.calcDistanesCluster_elem(start_cluster, i, 0));
		}
		
		return getIndexesofHashSortedByValue(hashDist);
	}
	
	/**
	 * calculate distance between cluster and i-th element, which doesn't belong to cluster;
	 * linkage = 0 - average: distance between cluster and element is calculated as average all distances between the element and all members of the cluster;
	 * flag = 1 - single: distance between cluster and element is calculated as min of all distances between the element and all members of the cluster;
	 * flag = 2 - complete: distance between cluster and element is calculated as max of all distances between the element and all members of the cluster;
	 * @param cluster
	 * @param i
	 * @param linkage
	 * @return
	 */
	private Double calcDistanesCluster_elem(Set<Integer> cluster, int i, int linkage){
		double result = 0.0;
		switch (linkage) {
		case 0: {
				int count = 0;
				for (int i1 : cluster) {
					
						if (i1 != i) {
							if (i1 > i) {
								result += super.getMapDistances_elems()[i1][i];
							} else {
								result += super.getMapDistances_elems()[i][i1];
							}
							count++;
						}
					
				}
				return (double) result / count;
			}
		case 1: { // min distances between members of clusters
			Double min_dist = Double.MAX_VALUE;
			for (int i1 : cluster) {
				
					if (i1 != i) {
						if (i1 > i) {
							if( super.getMapDistances_elems()[i1][i] < min_dist){
								min_dist = super.getMapDistances_elems()[i1][i];
							}
						} else {
							if( super.getMapDistances_elems()[i][i1] < min_dist){
								min_dist = super.getMapDistances_elems()[i][i1];
							}
						}
					}
				
			}
			return (double) min_dist;
			}
		case 2: { // max distances between members of clusters
			Double max_dist = Double.MIN_VALUE;
			for (int i1 : cluster) {

				if (i1 != i) {
						if (i1 > i) {
							if( super.getMapDistances_elems()[i1][i] > max_dist){
								max_dist = super.getMapDistances_elems()[i1][i];
							}
						} else {
							if( super.getMapDistances_elems()[i][i1] > max_dist){
								max_dist = super.getMapDistances_elems()[i][i1];
							}
						}
					}
				}
			
			return (double) max_dist;
			}
		default: return null;
		}
	}
	
	private List<Integer> getIndexesofHashSortedByValue(HashMap<Integer,Double> hash){
		HashMap<Double,Set<Integer>> reverseHash = new HashMap<Double,Set<Integer>>();
		for(int key : hash.keySet()){
			Double value = hash.get(key);
			if(reverseHash.containsKey(value)){
				reverseHash.get(value).add(key);
			}else{
				Set<Integer> tmp = new TreeSet<Integer>();
				tmp.add(key);
				reverseHash.put(value, tmp);
			}
		}
		List<Integer> result = new ArrayList<Integer>();
		Set<Double> sortedValues = new TreeSet<Double>();
		sortedValues.addAll(reverseHash.keySet());
		for(double key : sortedValues){
			for(int index : reverseHash.get(key)){
				result.add(index);
			}
		}
		//System.out.println(result);
		return result;
	}

	@Override
	public List<Set<Integer>> getClusters() {
		// TODO Auto-generated method stub
		return this.final_clusters;
	}
}
