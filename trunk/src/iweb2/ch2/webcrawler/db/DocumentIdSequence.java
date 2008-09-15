package iweb2.ch2.webcrawler.db;

public class DocumentIdSequence {
    private static final String groupIdPrefix = "g";
    private static final String itemIdPrefix = "doc";
    private static final String delimiter = ":";
    
    private int groupSequence = 0;
    private int itemSequence = 0;
    
    public DocumentIdSequence() {
    }

    public DocumentIdSequence(String documentId) {
        String groupId = getGroupId(documentId);
        this.groupSequence = getGroupSequence(groupId); 
        String itemId = getItemId(documentId);
        this.itemSequence = getItemSequence(itemId);
    }
    
    
    public String createNextDocumentId() {
        itemSequence++;
        String itemId = itemIdPrefix + String.valueOf(itemSequence);
        return groupIdPrefix + String.valueOf(groupSequence) + delimiter + itemId; 
    }
    
    public String createNextDocumentId(boolean createNewGroup) {
        if( createNewGroup ) {
            createNextGroupId();
        }
        return createNextDocumentId();
    }
    
    public String createNextGroupId() {
        groupSequence++;
        itemSequence = 0;
        return groupIdPrefix + groupSequence;
    }
    
    public static String getGroupId(String documentId) {
        return documentId.substring(0, documentId.indexOf(delimiter));        
    }

    public static String getItemId(String documentId) {
        return documentId.substring(documentId.indexOf(delimiter) +delimiter.length());        
    }
    
    public static int getGroupSequence(String groupId) {
        return Integer.valueOf(groupId.substring(groupIdPrefix.length()));        
    }
    
    public static int getItemSequence(String itemId) {
        return Integer.valueOf(itemId.substring(itemIdPrefix.length()));        
    }
    
    public static String toDocumentId(String groupId, String itemId) {
        return groupId + ":" + itemId;
    }
}
