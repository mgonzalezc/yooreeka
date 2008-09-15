package iweb2.ch2.ranking;

import iweb2.ch2.webcrawler.CrawlData;
import iweb2.ch2.webcrawler.db.PageLinkDB;
import iweb2.util.gui.GraphGui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Rank {
    
	/**
	 * This is the percentage of time that a random surfer 
	 * follows the structure of the web.
	 */
	private double alpha = 0.8;
	
	/** This is the error tolerance for convergence */
	private double epsilon = 0.001;
	
	double[] pR;
	
	public Rank() {
	}
	
	public abstract PageRankMatrixH getH();
	
	public void build() throws Exception {
	    
        // check the results
		// getH().print();
        
        findPageRank(alpha, epsilon);        
    }

	public void findPageRank(double alpha, double epsilon) {
		
		// A counter for our iterations
		int k = 0; 
		
		// auxiliary variable
		PageRankMatrixH matrixH = getH();
		
		// The H matrix has size nxn and the PageRank vector has size n
		int n = matrixH.getSize();
		
		//auxiliary variable
		double inv_n = (double)1/n;
		
		// This is the actual nxn matrix of double values
		double[][] H = matrixH.getMatrix();
		
		// A dummy variable that holds our error -- 
		// arbitrarily set to an initial value of 1
		double error = 1;
		
		// This holds the values of the PageRank vector
        pR = new double[n];
        
        // This is a copy of the PageRank vector from the previous iteration
        // The only reason that we need this is for evaluating the error	
        double[] tmpPR = new double[n];

        // Set the initial values (ad hoc)
        for (int i=0; i < n; i++) {
        	pR[i] = inv_n;
        }

        /*
         * Book Section 2.3 -- Altering the H matrix: Dangling nodes
         */
        double[][] dNodes= getDanglingNodeMatrix();
    	
/** 
 * TODO: 2.5 -- Altering the G matrix: Teleportation (Book Section 2.3)
 * 
 *       The following code defines the contribution of the dangling nodes,
 *       i.e. jumping randomly on a page that is not connected with the one 
 *       that our surfer is currently viewing
 *       
 *       Notice that it is the same for all pages. An interesting variation
 *       of the algorithm would introduce a "teleportation" contribution that
 *       relates the probability of an arbitrary transition to the degree of
 *       interest that a user has for the content of a page. 
 *       
 *       Exercise: Could that be done? If so, how? What problems can you see
 *       with that variation?  
 */    	
    	double tNodes=(1 - alpha) * inv_n;

    	//Replace the H matrix with the G matrix
    	for (int i=0; i < n; i++) {
    		
        	for (int j=0; j < n; j++) {
        		        		
        		H[i][j] = alpha*H[i][j] + dNodes[i][j] + tNodes;
        	}
    	}

    	// Iterate until convergence.
    	// We have found the PageRank values if our error is smaller than epsilon 
    	while ( error >= epsilon) {
        	
            // Make a copy of the PageRank vector before we update it
        	for (int i=0; i < n; i++) {
            	tmpPR[i] = pR[i];
            }
        	         	
        	double dummy =0;
        	// Now we get the next point in the iteration
        	for (int i=0; i < n; i++) {
        	
        		dummy =0;
        		
        		for (int j=0; j < n; j++) {
            		
            		dummy += pR[j]*H[j][i];
            	}
        		
        		pR[i] = dummy;
        	}

        	// Get the error, so that we can check convergence
        	error = norm(pR,tmpPR);
        	
        	// Display the progress
        	System.out.println("\n Iteration: "+k +",   PageRank convergence error: "+error);
        	for(int i = 0; i < n; i++) {
        	    System.out.println("Index: " + i + " -->  PageRank: " + pR[i]);
        	}
        	//increase the value of the counter by one
        	k++;
        }
        
        // Report the final values

    	List<RelevanceScore> allRankings = new ArrayList<RelevanceScore>();
        for (int i=0; i < n; i++) {
            String url = matrixH.getIndexMapping().getValue(i);
            RelevanceScore r = new RelevanceScore(url, pR[i]);
            allRankings.add(r);
        }
        RelevanceScore.sort(allRankings);
        System.out.println("\n______________  Calculation Results  _______________\n");
        for (RelevanceScore r : allRankings) {
            System.out.printf("Page URL: %-42s  -->  Rank: %.15f\n", r.getId(), r.getScore());
        }
        System.out.println("\n____________________________________________________\n");        
	}
	
	private double norm(double[] a, double[] b) {
		
		double norm = 0;
		
		int n = a.length;
		
		for (int i=0; i < n; i++) {
			norm += Math.abs(a[i]-b[i]);
		}
		return norm;
	}

	/** 
	 * TODO: 2.4 -- Altering the G matrix: Dangling nodes (Book Section 2.3)
	 * 
	 *       The following code defines the contribution of the dangling nodes,
	 *       i.e. nodes that do not link to any other node.
	 *       
	 *       Notice that the 1/n contribution is arbitrary. Given that we have
	 *       no other information about the random surfer's habits or preferences,
	 *       the 1/n value is fair. However, an interesting variation would take
	 *       into account some statistics related to the number of visits a page
	 *       gets.
	 *       
	 *       Exercise: Change the algorithm, so that a dangling node's contribution
	 *       depends on some page visit statistic. You can practice with a 
	 *       small set of pages and examine the effect on the ranking of the pages.
	 */
	private double[][] getDanglingNodeMatrix() {
		
		PageRankMatrixH matrixH = getH();
		
		int n = matrixH.getSize();
		
		double inv_n = (double)1/n;
		
		// The dangling node vector
		int[] dangling = matrixH.getDangling();
		
		double[][] dNodes = new double[n][n];
	   	
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				
				if (dangling[i] == 0) {
					dNodes[i][j] = 0;
				} else {
					dNodes[i][j] = alpha * inv_n;
				}
			}
		}

		return dNodes;
	}
	    	
	
	
	
	/**
	 * @return the alpha
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	/**
	 * @return the epsilon
	 */
	public double getEpsilon() {
		return epsilon;
	}

	/**
	 * @param epsilon the epsilon to set
	 */
	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	/**
	 * @return the pR
	 */
	public double getPageRank(String url) {
		
		int i = getH().getIndexMapping().getIndex(url);
		
		return pR[i];
	}
	
	
    /**
     * Shows page graph with corresponding page rank values.
     */
    public void visualize(CrawlData c) {
         
         
         GraphGui gui = new GraphGui();
         //String[] allDocs = getDocNames();
         
         PageRankMatrixH H = getH();
         int n = H.getSize();
         List<String> graphUrls = new ArrayList<String>(); 
         for(int i = 0; i < n; i++ ) {
             String pageUrl = H.getIndexMapping().getValue(i);
             graphUrls.add(pageUrl);
             //double[] xy = getCoordinates(i);
             double[] xy = new double[] {10, 10 };
             double pageRank = getPageRank(pageUrl);
             String extraText = String.valueOf(pageRank);
             gui.addNode(pageUrl, extraText, xy[0], xy[1]);
         }
         
         // add edges between nodes
         PageLinkDB linksDB = c.getPageLinkDB();
         for(String sourceDocUrl : graphUrls) {
             Set<String> targetNodeUrls = linksDB.getOutlinks(sourceDocUrl); 
             for(String targetNodeUrl : targetNodeUrls) {
                 gui.addEdge(sourceDocUrl, targetNodeUrl);                
             }
         }
         gui.showGraph();
    }

   /*
    * Contains hard-coded coordinates for graph nodes. 
    */
   private static Map<String, double[]> coordinates = 
       new HashMap<String, double[]>();
   
   static {
       coordinates.put("Doc1", new double[] {10, 10});
       coordinates.put("Doc2", new double[] {10, 100});
       coordinates.put("Doc3", new double[] {200, 10});        
       coordinates.put("Doc4", new double[] {200, 100});        
   }
	
}
