package edu.ucdavis.gc.bm.clustering;

public  class Pair {
	
	private Metricable first;
	
	private Metricable second;
	
	private Double distance;
	
	public void setFirst(Metricable first){
		this.first = first;
	}
	
	public void setSecond(Metricable second){
		this.second = second;
	}
	
	public Metricable getFirst(){
		return this.first;
	}
	
	public Metricable getSecond(){
		return this.second;
	}
	
	public void setDistance(Metricable first, Metricable second){
		this.first = first;
		this.second = second;
		this.distance = first.distanceTo(second);
	}
	
	public Double getDistance(){
		return this.distance;
	}
	
	@Override
	public boolean equals(Object obj){
		Pair anotherPair = (Pair) obj;
		if(this.first.equals(anotherPair.getFirst()) && this.second.equals(anotherPair.getSecond()) 
				|| this.first.equals(anotherPair.getSecond()) && this.second.equals(anotherPair.getFirst())){
			return true;
		}else{
			return false;
		}
	}
	
}
