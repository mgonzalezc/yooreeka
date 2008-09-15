package iweb2.ch2.shell;

import iweb2.ch2.webcrawler.BasicWebCrawler;
import iweb2.ch2.webcrawler.CrawlData;
import iweb2.ch2.webcrawler.URLFilter;
import iweb2.ch2.webcrawler.URLNormalizer;

import java.util.ArrayList;
import java.util.List;

public class FetchAndProcessCrawler {

	public static final int DEFAULT_NUMBER_OF_CRAWLS = 3;
	public static final int DEFAULT_NUMBER_OF_DOCS_PER_CRAWL = 100;
	
	//INSTANCE VARIABLES
	// A reference to the crawled data
	CrawlData crawlData;
	
	// The location where we will store the fetched data
	String rootDir;

	// total number of iterations
    int maxNumberOfCrawls = DEFAULT_NUMBER_OF_CRAWLS;
    
    // max number of pages that will be fetched within every crawl/iteration.
    int maxNumberOfDocsPerCrawl = DEFAULT_NUMBER_OF_DOCS_PER_CRAWL; 
        
    List<String> seedUrls;

    public FetchAndProcessCrawler(String arg1, int arg2, int arg3) {
    	
    	rootDir = arg1;

    	if ( rootDir == null || rootDir.trim().length() == 0) {
    		
    		String prefix = System.getProperty("iweb2.home");
    		if (prefix == null) {
    			prefix = "..";
    		}

    		rootDir = System.getProperty("iweb2.home")+System.getProperty("file.separator")+"data";
    	}
    	
    	rootDir = rootDir+System.getProperty("file.separator")+"crawl-" + System.currentTimeMillis();
    	
    	maxNumberOfCrawls = arg2;
    	
    	maxNumberOfDocsPerCrawl = arg3;
    	
    	seedUrls = new ArrayList<String>();
    }
    
    public void run() {
    	        
    	crawlData = new CrawlData(rootDir);

        BasicWebCrawler webCrawler = new BasicWebCrawler(crawlData);
        webCrawler.addSeedUrls(getSeedUrls());

        /* configure url filter to accept only file:// urls */
        URLFilter urlFilter = new URLFilter();
        urlFilter.setAllowFileUrls(true);
        urlFilter.setAllowHttpUrls(false);
        webCrawler.setURLFilter(urlFilter);
        
    	long t0 = System.currentTimeMillis();

        /* run crawl */
        webCrawler.fetchAndProcess(maxNumberOfCrawls, maxNumberOfDocsPerCrawl);

    	System.out.println("Timer (s): [Crawler fetched data] --> " + (System.currentTimeMillis()-t0)*0.001);
    	t0 = System.currentTimeMillis();
    	
        /* process the collected data */
        webCrawler.processFetchedData();
        
        System.out.println("Timer (s): [Crawler processed data] --> " + (System.currentTimeMillis()-t0)*0.001);
    	
    }
    
    public List<String> getSeedUrls() { 

    	return seedUrls;
    }

    public void addUrl(String val) {
    	URLNormalizer urlNormalizer = new URLNormalizer();
    	seedUrls.add(urlNormalizer.normalizeUrl(val));
    }

    public void setAllUrls() {
    	
    	setDefaultUrls();
    	
    	// Include the spam pages ... all of them!
    	addUrl("file:///c:/iWeb2/data/ch02/spam-01.html");
    	addUrl("file:///c:/iWeb2/data/ch02/spam-biz-01.html");
    	addUrl("file:///c:/iWeb2/data/ch02/spam-biz-02.html");
    	addUrl("file:///c:/iWeb2/data/ch02/spam-biz-03.html");    	
    }
    
    public void setDefaultUrls() {
    	
    	addUrl("file:///c:/iWeb2/data/ch02/biz-01.html");
    	addUrl("file:///c:/iWeb2/data/ch02/biz-02.html");
    	addUrl("file:///c:/iWeb2/data/ch02/biz-03.html");
    	addUrl("file:///c:/iWeb2/data/ch02/biz-04.html");
    	addUrl("file:///c:/iWeb2/data/ch02/biz-05.html");
    	addUrl("file:///c:/iWeb2/data/ch02/biz-06.html");
    	addUrl("file:///c:/iWeb2/data/ch02/biz-07.html");
    	
    	addUrl("file:///c:/iWeb2/data/ch02/sport-01.html");
    	addUrl("file:///c:/iWeb2/data/ch02/sport-02.html");
    	addUrl("file:///c:/iWeb2/data/ch02/sport-03.html");

    	addUrl("file:///c:/iWeb2/data/ch02/usa-01.html");
    	addUrl("file:///c:/iWeb2/data/ch02/usa-02.html");
    	addUrl("file:///c:/iWeb2/data/ch02/usa-03.html");
    	addUrl("file:///c:/iWeb2/data/ch02/usa-04.html");

    	addUrl("file:///c:/iWeb2/data/ch02/world-01.html");
    	addUrl("file:///c:/iWeb2/data/ch02/world-02.html");
    	addUrl("file:///c:/iWeb2/data/ch02/world-03.html");
    	addUrl("file:///c:/iWeb2/data/ch02/world-04.html");
    	addUrl("file:///c:/iWeb2/data/ch02/world-05.html");
	}
    
    public void setUrls(String val) {
    	
    	this.seedUrls.clear();
    	
    	if (val.equalsIgnoreCase("biz")) {
    		
        	addUrl("file:///c:/iWeb2/data/ch02/biz-01.html");
        	addUrl("file:///c:/iWeb2/data/ch02/biz-02.html");
        	addUrl("file:///c:/iWeb2/data/ch02/biz-03.html");
        	addUrl("file:///c:/iWeb2/data/ch02/biz-04.html");
        	addUrl("file:///c:/iWeb2/data/ch02/biz-05.html");
        	addUrl("file:///c:/iWeb2/data/ch02/biz-06.html");
        	addUrl("file:///c:/iWeb2/data/ch02/biz-07.html");
        	
    	} else if (val.equalsIgnoreCase("sport")) {
    		
        	addUrl("file:///c:/iWeb2/data/ch02/sport-01.html");
        	addUrl("file:///c:/iWeb2/data/ch02/sport-02.html");
        	addUrl("file:///c:/iWeb2/data/ch02/sport-03.html");

    	} else if (val.equalsIgnoreCase("usa")) {
    		
        	addUrl("file:///c:/iWeb2/data/ch02/usa-01.html");
        	addUrl("file:///c:/iWeb2/data/ch02/usa-02.html");
        	addUrl("file:///c:/iWeb2/data/ch02/usa-03.html");
        	addUrl("file:///c:/iWeb2/data/ch02/usa-04.html");

    	} else if (val.equalsIgnoreCase("world")) {
    		
        	addUrl("file:///c:/iWeb2/data/ch02/world-01.html");
        	addUrl("file:///c:/iWeb2/data/ch02/world-02.html");
        	addUrl("file:///c:/iWeb2/data/ch02/world-03.html");
        	addUrl("file:///c:/iWeb2/data/ch02/world-04.html");
        	addUrl("file:///c:/iWeb2/data/ch02/world-05.html");
    	} else if (val.equalsIgnoreCase("biz-docs")) {
            
            addUrl("file:///c:/iWeb2/data/ch02/biz-01.doc");
            addUrl("file:///c:/iWeb2/data/ch02/biz-02.doc");
            addUrl("file:///c:/iWeb2/data/ch02/biz-03.doc");
            addUrl("file:///c:/iWeb2/data/ch02/biz-04.doc");
            addUrl("file:///c:/iWeb2/data/ch02/biz-05.doc");
            addUrl("file:///c:/iWeb2/data/ch02/biz-06.doc");
            addUrl("file:///c:/iWeb2/data/ch02/biz-07.doc");
            
        } else if (val.equalsIgnoreCase("sport-docs")) {
            
            addUrl("file:///c:/iWeb2/data/ch02/sport-01.doc");
            addUrl("file:///c:/iWeb2/data/ch02/sport-02.doc");
            addUrl("file:///c:/iWeb2/data/ch02/sport-03.doc");

        } else if (val.equalsIgnoreCase("usa-docs")) {
            
            addUrl("file:///c:/iWeb2/data/ch02/usa-01.doc");
            addUrl("file:///c:/iWeb2/data/ch02/usa-02.doc");
            addUrl("file:///c:/iWeb2/data/ch02/usa-03.doc");
            addUrl("file:///c:/iWeb2/data/ch02/usa-04.doc");

        } else if (val.equalsIgnoreCase("world-docs")) {
            
            addUrl("file:///c:/iWeb2/data/ch02/world-01.doc");
            addUrl("file:///c:/iWeb2/data/ch02/world-02.doc");
            addUrl("file:///c:/iWeb2/data/ch02/world-03.doc");
            addUrl("file:///c:/iWeb2/data/ch02/world-04.doc");
            addUrl("file:///c:/iWeb2/data/ch02/world-05.doc");
        }    	
    	else {
    	    throw new IllegalArgumentException("Unknown value: '" + val + "'");
    	}
        	
    }
    
    public void addDocSpam() {
    	
    	addUrl("file:///c:/iWeb2/data/ch02/spam-biz-01.doc");
    	addUrl("file:///c:/iWeb2/data/ch02/spam-biz-02.doc");
    	addUrl("file:///c:/iWeb2/data/ch02/spam-biz-03.doc");    	
    }
    
	/**
	 * @return the rootDir
	 */
	public String getRootDir() {
		return rootDir;
	}

	/**
	 * @param rootDir the rootDir to set
	 */
	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	/**
	 * @return the maxNumberOfCrawls
	 */
	public int getMaxNumberOfCrawls() {
		return maxNumberOfCrawls;
	}

	/**
	 * @param maxNumberOfCrawls the maxNumberOfCrawls to set
	 */
	public void setMaxNumberOfCrawls(int maxNumberOfCrawls) {
		this.maxNumberOfCrawls = maxNumberOfCrawls;
	}

	/**
	 * @return the maxNumberOfDocsPerCrawl
	 */
	public int getMaxNumberOfDocsPerCrawl() {
		return maxNumberOfDocsPerCrawl;
	}

	/**
	 * @param maxNumberOfDocsPerCrawl the maxNumberOfDocsPerCrawl to set
	 */
	public void setMaxNumberOfDocsPerCrawl(int maxNumberOfDocsPerCrawl) {
		this.maxNumberOfDocsPerCrawl = maxNumberOfDocsPerCrawl;
	}

	/**
	 * @return the crawlData
	 */
	public CrawlData getCrawlData() {
		return crawlData;
	}
    
}
