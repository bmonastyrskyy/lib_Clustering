package edu.ucdavis.gc.bm.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class HierarchicalClustering extends BaseClustering {

	/**
	 * list of sets of counting numbers of every cluster
	 */
	private List<Set<Integer>> clusters;
	/**
	 * map of distances between clusters
	 */
	private Double[][] mapDistances_clusts;
	/**
	 * return clusters of indexes
	 */
	public List<Set<Integer>> getClusters() {
		return clusters;
	}
	/**
	 * return the biggest cluster
	 */
	public Set<Integer> getBiggestCluster(){
		Set<Integer> result = new TreeSet<Integer>();
		int size  = 0;
		if (result.isEmpty()){
			result.addAll(clusters.get(0));
			size = result.size();
		}
		for(Set<Integer> cl : clusters){
			if (cl.size() > size){
				result.clear();
				result.addAll(cl);
				size = result.size();
			}
		}
		return result;
	}
	/**
	 * Linkage parameter determines how to calculate distance between clusters
	 * 0 - average distance (default);
	 * 1 - single: min distance between elements of the clusters
	 * 2 - complete: max distance between elements of the clusters 
	 */
	private int linkage = 0;
	/**
	 * set parameter linkage which determines how to calculate distance between clusters
	 *  0 - average distance (default);
	 *  1 - single: min distance between elements of the clusters
	 *  2 - complete: max distance between elements of the clusters
	 * @param linkage
	 */
	public void setFDistBetweenClusters(int linkage){
		if (linkage == 2){
			this.linkage = linkage;
			return;
		}
		if (linkage == 1){
			this.linkage = linkage;
			return;
		}
		linkage = 0;
	}

	public HierarchicalClustering(List<? extends Metricable> elements) {
		super(elements);
		this.mapDistances_clusts = initiateMapDistances();
		this.initiateClusters();
	}
	
	public HierarchicalClustering(Double[][] mapDistances_clusts) {
		super(mapDistances_clusts);
		this.mapDistances_clusts = initiateMapDistances();
		this.initiateClusters();
	}
	
	
	@Override
	public void process(double cutOff_dist){
		while(true){
			int[] a = selectMin();
			int ii = a[0];
			int jj = a[1];
			if(this.mapDistances_clusts[ii][jj] > cutOff_dist || this.clusters.size() == 1){
				break;
			}
			this.recalculateClusters(ii, jj);
			this.mapDistances_clusts = this.recalculateMapDistances(ii, jj);
		}
	}
	
	@Override
	public void process(int numberClusters){
		if(numberClusters > super.getElements().size()){
			numberClusters = super.getElements().size();
		}
		while (this.clusters.size() > numberClusters) {
			// int [] a = selectMax();
			int[] a = selectMin();
			int ii = a[0];
			int jj = a[1];
			this.recalculateClusters(ii, jj);
			this.mapDistances_clusts = this.recalculateMapDistances(ii, jj);
		}
	}
	
	@SuppressWarnings("unused")
	private int[] selectMax() {
		int[] result = new int[2];
		Double max = Double.MIN_VALUE;
		for (int i = 1; i < mapDistances_clusts.length; i++) {
			for (int j = 0; j < i; j++) {
				if (mapDistances_clusts[i][j] > max) {
					result[0] = i;
					result[1] = j;
					max = mapDistances_clusts[i][j];
				}
			}
		}
		return result;
	}
	
	/**
	 * select i,j of minimal element of the mapDistances matrix
	 * @return
	 */
	private int[] selectMin() {
		int[] result = new int[2];
		Double min = Double.MAX_VALUE;
		for (int i = 1; i < mapDistances_clusts.length; i++) {
			for (int j = 0; j < i; j++) {
				if (mapDistances_clusts[i][j] < min) {
					result[0] = i;
					result[1] = j;
					min = mapDistances_clusts[i][j];
				}
			}
		}
		return result;
	}

	private Double[][] initiateMapDistances() {
		Double[][] result = new Double[super.getMapDistances_elems().length][super
				.getMapDistances_elems().length];
		for (int i = 0; i < super.getMapDistances_elems().length; i++) {
			for (int j = 0; j < super.getMapDistances_elems().length; j++) {
				result[i][j] = super.getMapDistances_elems()[i][j];
			}
		}
		return result;
	}

	/**
	 * the method recalculates mapDistances after clusters ii and jj having been
	 * joined
	 */
	private Double[][] recalculateMapDistances(int ii, int jj) {
		Double[][] result = new Double[this.clusters.size()][this.clusters
				.size()];
		int min = (ii > jj ? jj : ii); // min (ii,jj)
		int max = (ii > jj ? ii : jj); // max (ii,jj)
		for (int i = 0; i < clusters.size(); i++) {
			int old_i = 0;
			if (i < max) {
				old_i = i;
			} else {
				old_i = i + 1;
			}
			for (int j = 0; j < clusters.size(); j++) {
				int old_j = 0;
				if (j < max) {
					old_j = j;
				} else {
					old_j = j + 1;
				}
				if (j == min || i == min) {
					result[i][j] = distanceBetweenClusters(
							this.clusters.get(i), this.clusters.get(j),linkage);
					result[j][i] = result[i][j];
				} else {
					result[i][j] = mapDistances_clusts[old_i][old_j];
					result[j][i] = result[j][i];
				}
			}
		}
		return result;
	}

	/**
	 * the initial clusters are just the elements of the input set: every
	 * cluster just contains one element
	 */
	private void initiateClusters() {
		if (null == this.clusters) {
			this.clusters = new ArrayList<Set<Integer>>();
			for (int i = 0; i < super.getMapDistances_elems().length; i++) {
				Set<Integer> cluster = new TreeSet<Integer>();
				cluster.add(i);
				clusters.add(cluster);
			}
		}
	}

	/**
	 * join i-th and j-th clusters into one cluster with index = min(i,j) 
	 */
	private void recalculateClusters(int i, int j) {
		if (null == this.clusters) {
			initiateClusters();
		}
		int min = (i > j ? j : i);
		int max = (i > j ? i : j);
		this.clusters.get(min).addAll(this.clusters.get(max)); // join i and j
																// clusters
		this.clusters.remove(max); // remove max(i,j) cluster
	}

	/**
	 * The method calculates the distance between clusters 
	 */
	private Double distanceBetweenClusters(Set<Integer> cluster1,
			Set<Integer> cluster2, int linkage) {
		Double result = 0.0;
		switch (linkage) {
		case 0: { // average linkage
				int count = 0;
				for (int i1 : cluster1) {
					for (int i2 : cluster2) {
						if (i1 != i2) {// int [] a = selectMax();
							if (i1 > i2) {
								result += super.getMapDistances_elems()[i1][i2];
							} else {
								result += super.getMapDistances_elems()[i2][i1];
							}
							count++;
						}
					}
				}
				return (double) result / count;
			}
		case 1: { // single linkage: min distances between members of clusters
			Double min_dist = Double.MAX_VALUE; 
			for (int i1 : cluster1) {
				for (int i2 : cluster2) {
					if (i1 != i2) {
						if (i1 > i2) {
							if( super.getMapDistances_elems()[i1][i2] < min_dist){
								min_dist = super.getMapDistances_elems()[i1][i2];
							}
						} else {
							if( super.getMapDistances_elems()[i2][i1] < min_dist){
								min_dist = super.getMapDistances_elems()[i2][i1];
							}
						}
					}
				}
			}
			return min_dist;
			}
		case 2: { // complete linkage: max distances between members of clusters
			Double max_dist = Double.MIN_VALUE;
			for (int i1 : cluster1) {
				for (int i2 : cluster2) {
					if (i1 != i2) {
						if (i1 > i2) {
							if( super.getMapDistances_elems()[i1][i2] > max_dist){
								max_dist = super.getMapDistances_elems()[i1][i2];
							}
						} else {
							if( super.getMapDistances_elems()[i2][i1] > max_dist){
								max_dist = super.getMapDistances_elems()[i2][i1];
							}
						}
					}
				}
			}
			return  max_dist;
			}
		default: return null;
		}
	}

}
