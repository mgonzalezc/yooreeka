package iweb2.ch3.collaborative.similarity.triangular;

import iweb2.ch3.collaborative.similarity.util.RatingCountMatrix;

import java.util.Hashtable;

/**
 * Defines a similarity matrix, which uses a <code>Hashtable</code>. 
 * The <code>Hashtable</code> store the upper triangular part of the similarity matrix.
 * 
 * Note: If the similarity matrix is <b>not symmetric</b> then this is not an appropriate representation.  
 * For example, in the case of user-oriented methods you might want the similarity matrix to reflect the
 * assymetry between the tastes of various individuals. Person A may like person B and considers himself
 * similar to person B. However, person B may not feel the same way.
 * 
 */
public interface UpperTriangularSimilarityMatrix extends java.io.Serializable {

    /**
     * Returns an upper triangular matrix of similarities. For user-oriented methods it represents
     * similarities between users and for item-oriented methods the matrix represents similarities 
     * between items.
     * 
     * @return similarity matrix
     */
    public abstract Hashtable<Integer, double[]> getSimilarityMatrix();

    /**
     * Returns similarity value between two objects identified by their IDs.
     * 
     * @param idX
     * @param idY
     * @return
     */
    public abstract double getValue(Integer idX, Integer idY);
    
    /**
     * Similarity matrix id. 
     * @return
     */
    public abstract String getId();
    
    public abstract RatingCountMatrix getRatingCountMatrix(Integer idX, Integer idY);
    
    public abstract boolean isRatingCountMatrixAvailable();
    
    public void print();
}