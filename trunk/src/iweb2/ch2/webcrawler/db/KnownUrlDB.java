package iweb2.ch2.webcrawler.db;

import iweb2.ch2.webcrawler.model.KnownUrlEntry;
import iweb2.ch2.webcrawler.utils.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnownUrlDB {
    
    private static final String DB_FILENAME = "knownurlsdb.dat";    
    
    private Map<String, KnownUrlEntry> processedURLs = 
        new HashMap<String, KnownUrlEntry>();

    private Map<String, KnownUrlEntry> unprocessedURLs = 
        new HashMap<String, KnownUrlEntry>();
    
    private File rootDir = null;
    private File dbFile = null;

    
    public KnownUrlDB(File f) {
        this.rootDir = f;

    }

    public void delete() {
        FileUtils.deleteDir(rootDir);
    }
    
    public void init() {
        rootDir.mkdirs();
        
        this.dbFile = new File(rootDir, DB_FILENAME);
        try {
            // creates a new file if the file doesn't exist
            dbFile.createNewFile();
        }
        catch(IOException e) {
            throw new RuntimeException("Can't create db file: '" + dbFile.getAbsolutePath() + "'.", e);
        }
        
        load();        
    }
    
    public void save() {
        try {
            OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(dbFile), "UTF-8");
            BufferedWriter bw = new BufferedWriter(w);
            for(KnownUrlEntry r : unprocessedURLs.values() ) {
                writeRecord(bw, r);
            }
            for(KnownUrlEntry r : processedURLs.values() ) {
                writeRecord(bw, r);
            }
            bw.flush();
            bw.close();
        }
        catch(IOException e) {
            throw new RuntimeException("Failed to save data: ", e);
        }
    }
    
    private void writeRecord(BufferedWriter w, KnownUrlEntry r) throws IOException {
        w.write(r.getStatus() + "|" + r.getUrl());
        w.newLine();
    }
    
    private void load() {
        try {
            InputStreamReader r = new InputStreamReader(new FileInputStream(dbFile), "UTF-8");
            BufferedReader br = new BufferedReader(r);
            String line = null;
            while( (line = br.readLine()) != null ) {
                int delimiterIndex = line.indexOf("|");
                String status = line.substring(0, delimiterIndex);
                String url = line.substring(delimiterIndex + "|".length());
                addNewUrl(url, status);
            }
            br.close();
        }
        catch(IOException e) {
            throw new RuntimeException("Failed to load data: ", e);
        }
    }
    
    public boolean inUnprocessedUrl(String url) {
        return unprocessedURLs.containsKey(url);
    }
    
    public boolean inProcessedUrl(String url) {
        return processedURLs.containsKey(url);
    }
    
    public boolean isSuccessfullyProcessed(String url) {
        KnownUrlEntry r = processedURLs.get(url);
        if( r != null && 
                KnownUrlEntry.STATUS_PROCESSED_SUCCESS
                    .equalsIgnoreCase(r.getStatus()) ) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public boolean isKnownUrl(String url) {
        return processedURLs.containsKey(url) || 
               unprocessedURLs.containsKey(url);
    }
    
    public boolean addNewUrl(String url) {
        return addNewUrl(url, KnownUrlEntry.STATUS_UNPROCESSED);
    }

    public boolean addNewUrl(String url, String status) {
        boolean isAdded =false;
        
        if( isKnownUrl(url) == false ) {
            KnownUrlEntry r = new KnownUrlEntry(url, status);
            if( KnownUrlEntry.STATUS_PROCESSED_SUCCESS.equalsIgnoreCase(status) ||
                KnownUrlEntry.STATUS_PROCESSED_ERROR.equalsIgnoreCase(status) ) {
                processedURLs.put(url, r);
            }
            else if( KnownUrlEntry.STATUS_UNPROCESSED.equalsIgnoreCase(status) ) {
                unprocessedURLs.put(url, r);                
            }
            else {
                throw new RuntimeException("Unsupported status value: '" + status + "'.");
            }
            isAdded = true;
        }
        else {
            isAdded = false;
        }
        return isAdded;
    }
    
    public void updateUrlStatus(String url, String status) {
        if( KnownUrlEntry.STATUS_PROCESSED_SUCCESS.equalsIgnoreCase(status) ||
            KnownUrlEntry.STATUS_PROCESSED_ERROR.equalsIgnoreCase(status) ) {
            KnownUrlEntry r = unprocessedURLs.remove(url);
            if( r != null ) {
                r.setStatus(status);
            }
            else {
                r = new KnownUrlEntry(url, status);
            }
            processedURLs.put(url, r);            
        }
        else if( KnownUrlEntry.STATUS_UNPROCESSED.equalsIgnoreCase(status) ) {
            KnownUrlEntry r = processedURLs.remove(url);
            if( r != null ) {
                r.setStatus(status);
            }
            else {
                r = new KnownUrlEntry(url, status);
            }
            unprocessedURLs.put(url, r);
        }
    }
    

    public List<String> findAllKnownUrls() {
        List<String> allUrls = new ArrayList<String>();
        allUrls.addAll(unprocessedURLs.keySet());
        allUrls.addAll(processedURLs.keySet());
        return allUrls;
    }
    
    public int getTotalUrlCount() {
        return unprocessedURLs.size() + processedURLs.size();
    }

    public List<String> findProcessedUrls(String status) {
        ArrayList<String> selectedUrls = new ArrayList<String>();
        for(Map.Entry<String, KnownUrlEntry> mapEntry : processedURLs.entrySet() ) {
            KnownUrlEntry urlEntry = mapEntry.getValue();
            if( status.equalsIgnoreCase(urlEntry.getStatus()) ) {
                selectedUrls.add(urlEntry.getUrl());
            }
        }
        return selectedUrls;
    }
    
    public List<String> findUnprocessedUrls() {
        return new ArrayList<String>(unprocessedURLs.keySet());
    }
    
    public List<String> findUnprocessedUrls(int maxDocs) {
        List<String> urls = new ArrayList<String>();
        
        for(String key : unprocessedURLs.keySet() ) {
            if( urls.size() >= maxDocs ) {
                break;
            }
            urls.add(key);
        }
        
        return urls;
    }
    
}
