package iweb2.ch2.webcrawler.model;

public class KnownUrlEntry {
    
    public static final String STATUS_UNPROCESSED = "unprocessed";
    public static final String STATUS_PROCESSED_SUCCESS = "processed";
    public static final String STATUS_PROCESSED_ERROR = "error";
    
    private String url;
    private String status;
    
    public KnownUrlEntry(String url, String status) {
        this.url = url;
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
