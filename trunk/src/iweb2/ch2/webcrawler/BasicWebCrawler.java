package iweb2.ch2.webcrawler;

import iweb2.ch2.webcrawler.db.DocumentIdSequence;
import iweb2.ch2.webcrawler.db.FetchedDocsDB;
import iweb2.ch2.webcrawler.db.KnownUrlDB;
import iweb2.ch2.webcrawler.db.ProcessedDocsDB;
import iweb2.ch2.webcrawler.model.FetchedDocument;
import iweb2.ch2.webcrawler.model.KnownUrlEntry;
import iweb2.ch2.webcrawler.model.Outlink;
import iweb2.ch2.webcrawler.model.ProcessedDocument;
import iweb2.ch2.webcrawler.parser.common.DocumentParser;
import iweb2.ch2.webcrawler.parser.common.DocumentParserFactory;
import iweb2.ch2.webcrawler.transport.common.Transport;
import iweb2.ch2.webcrawler.transport.file.FileTransport;
import iweb2.ch2.webcrawler.transport.http.HTTPTransport;
import iweb2.ch2.webcrawler.utils.UrlGroup;
import iweb2.ch2.webcrawler.utils.UrlUtils;

import java.util.List;

public class BasicWebCrawler {
    
    private CrawlData crawlData;

    private URLFilter urlFilter;
    
    public BasicWebCrawler(CrawlData crawlData) {
        this.crawlData = crawlData;
    }
    
    public void addSeedUrls(List<String> seedUrls) {
        KnownUrlDB knownUrlsDB = crawlData.getKnownUrlsDB(); 
        for(String url : seedUrls) {
            knownUrlsDB.addNewUrl(url);
        }
    }
    
    public void fetchAndProcess(int maxCrawls, int maxDocs) {
        
        crawlData.delete();
        crawlData.init(); 
        
        int totalCrawlCount = 0;
        DocumentIdSequence docIdSequence = new DocumentIdSequence();
        while( totalCrawlCount < maxCrawls ) {
            totalCrawlCount++;
            // create a list of url that we are about to fetch
            List<String> urls = selectURLsForNextCrawl(maxDocs); 
            if( urls.size() == 0 ) {
            	System.out.println("There are no unprocessed urls.");
                break;
            }

            /* for each crawl iteration create a separate group of documents */
            String currentGroupId = docIdSequence.createNextGroupId();
            fetchPages(urls, crawlData.getFetchedDocsDB(), docIdSequence);
            
            // process downloaded data
            processPages(currentGroupId, crawlData.getProcessedDocsDB(), crawlData.getFetchedDocsDB());
            
            // get processed doc, get links, add links to all-known-urls.dat
            processLinks(currentGroupId, crawlData.getProcessedDocsDB());

//            logger.debug("after crawl: " + totalCrawlCount + 
//                    ", url total: " + crawlData.getKnownUrlsDB().findAllKnownUrls().size() + 
//                    ", unprocessed urls: " + crawlData.getKnownUrlsDB().findUnprocessedUrls().size());            
        }
        
//        logger.debug("after crawl: " + totalCrawlCount + 
//                ", url total: " + crawlData.getKnownUrlsDB().findAllKnownUrls().size() + 
//                ", unprocessed urls: " + crawlData.getKnownUrlsDB().findUnprocessedUrls().size());            
    }
    
    
    
    
    /*
     * Attempts to process all docs that were fetched before.
     */
    public void processFetchedData() {
        // keep fetched data. delete everything else
        crawlData.getProcessedDocsDB().delete();
        crawlData.getPageLinkDB().delete();
        crawlData.getKnownUrlsDB().delete();
        crawlData.init(); // load whatever is left

        FetchedDocsDB fetchedDocsDB = crawlData.getFetchedDocsDB();
        ProcessedDocsDB processedDocsDB = crawlData.getProcessedDocsDB();
        
        // iterate through every directory with fetched/unprocessed documents and process them
        for(String groupId : fetchedDocsDB.getAllGroupIds()) {
            processPages(groupId, processedDocsDB, fetchedDocsDB);
            // get processed doc, get links, add links to all-known-urls.dat
            processLinks(groupId, processedDocsDB);
        }
    }
    
    private Transport getTransport(String protocol) {
        if( "http".equalsIgnoreCase(protocol) ) {
            return new HTTPTransport();
        }
        else if ( "file".equalsIgnoreCase(protocol) ) {
            return new FileTransport();
        }
        else {
            throw new RuntimeException("Unsupported protocol: '" + protocol + "'.");
        }
    }
    
    public void fetchPages(List<String> urls, FetchedDocsDB fetchedDocsDB, DocumentIdSequence docIdSequence) {
        List<UrlGroup> urlGroups = UrlUtils.groupByProtocolAndHost(urls);
        for( UrlGroup urlGroup : urlGroups ) {
            Transport t = getTransport(urlGroup.getProtocol());
            try {
                t.init();
                for(String url : urlGroup.getUrls() ) {
                    try {
                        FetchedDocument doc = t.fetch(url);
                        doc.setDocumentId(docIdSequence.createNextDocumentId());
                        fetchedDocsDB.saveDocument(doc);
                        pause();
                    }
                    catch(Exception e) {
                    	System.out.println("Failed to fetch document from url: '" + url + "'.\n"+
                    			e.getMessage());
                        crawlData.getKnownUrlsDB().updateUrlStatus(url, KnownUrlEntry.STATUS_PROCESSED_ERROR);
                    }
                }
            }
            finally {
                t.clear();                
            }
        }
    }

    public void pause() {
        long PAUSE = 250;
        try {
            Thread.sleep(PAUSE);
        }
        catch(InterruptedException e) {
            // do nothing
        }
    }
        

    public void processPages(String groupId, ProcessedDocsDB parsedDocsService, FetchedDocsDB fetchedDocsDB) {
        DocumentFilter docFilter = new DocumentFilter();        
        List<String> docIds = fetchedDocsDB.getDocumentIds(groupId);

        for(String id : docIds) {
            FetchedDocument doc = null;
            try {
                doc = fetchedDocsDB.getDocument(id);
                String url = doc.getDocumentURL();
                
                if( docFilter.duplicateContentExists(doc) == false ) {                                
                    DocumentParser docParser = DocumentParserFactory
                        .getInstance().getDocumentParser(doc.getContentType());
                    ProcessedDocument parsedDoc = docParser.parse(doc);
                    parsedDocsService.saveDocument(parsedDoc);
                    crawlData.getKnownUrlsDB().updateUrlStatus(url, KnownUrlEntry.STATUS_PROCESSED_SUCCESS);                    
                }
                else {
                    crawlData.getKnownUrlsDB().updateUrlStatus(url, KnownUrlEntry.STATUS_PROCESSED_SUCCESS);                    
                }
            }
            catch(Exception e) {
                
            	if( doc != null  ) {
                
                	System.out.println("ERROR:\n");
                	System.out.println("Unexpected exception while processing: '" +	id + "', ");
                	System.out.println("   URL='" + doc.getDocumentURL() + "'\n");
                	System.out.println("Exception message: "+e.getMessage());
                
                } else {
                	System.out.println("ERROR:\n");
                	System.out.println("Unexpected exception while processing: '" +	id + "', ");
                	System.out.println("Exception message: "+e.getMessage());
                }
            }
        }
    }

    public void processLinks(String groupId, ProcessedDocsDB parsedDocs) {
        URLNormalizer urlNormalizer = new URLNormalizer();
        if( urlFilter == null ) {
            urlFilter = new URLFilter();
            urlFilter.setAllowFileUrls(true);
            urlFilter.setAllowHttpUrls(false);
            System.out.println("Using default URLFilter configuration that only accepts 'file://' urls");
        }
        
        List<String> docIds = parsedDocs.getDocumentIds(groupId);
        for(String documentId : docIds) {
            ProcessedDocument doc = parsedDocs.loadDocument(documentId);
            // register url without any outlinks first
            crawlData.getPageLinkDB().addLink(doc.getDocumentURL());
            List<Outlink> outlinks = doc.getOutlinks();
            for(Outlink outlink : outlinks) {
                String url = outlink.getLinkUrl();
                String normalizedUrl = urlNormalizer.normalizeUrl(url);
                if( urlFilter.accept(normalizedUrl) ) {
                    crawlData.getKnownUrlsDB().addNewUrl(url); 
                    crawlData.getPageLinkDB().addLink(doc.getDocumentURL(), url);
                }
            }
        }
        crawlData.getKnownUrlsDB().save();
        crawlData.getPageLinkDB().save();
    }

    public List<String> selectURLsForNextCrawl(int maxDocs) {
        return crawlData.getKnownUrlsDB().findUnprocessedUrls(maxDocs);
    }
    
    public URLFilter getURLFilter() {
        return urlFilter;
    }
    
    public void setURLFilter(URLFilter urlFilter) {
        this.urlFilter = urlFilter;
    }
    
}
