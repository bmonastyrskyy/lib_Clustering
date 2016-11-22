package edu.ucdavis.gc.bm.clustering;
/**
 * Metricable interface declares two methods:<br>
 *   distanceTo(Metricable otherObject)<br>
 *   equals(Metricable otherObject)<br>
 *   The objects of classes which implement Metricable interface can be used in clustering algorithms. 
 * @author bohdan
 *
 */
public interface Metricable {
    Double distanceTo(Metricable o);
    @Override
    boolean equals(Object o); 
}
