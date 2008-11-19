package iweb2.ch7.recommendation;

import iweb2.ch3.collaborative.model.Dataset;
import iweb2.ch3.collaborative.model.Item;
import iweb2.ch3.collaborative.model.Rating;
import iweb2.ch3.collaborative.model.User;
import iweb2.ch4.utils.ObjectToIndexMapping;
import iweb2.ch7.core.NewsDataset;
import iweb2.ch7.core.NewsStory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatasetAdapter implements Dataset {

    private NewsDataset newsDataset;
    
    private Map<Integer, User> users = new HashMap<Integer, User>();

    private ObjectToIndexMapping<NewsUser> userMapping = 
        new ObjectToIndexMapping<NewsUser>();

    private Map<Integer, Item> items = new HashMap<Integer, Item>();
    
    private ObjectToIndexMapping<NewsStory> storyMapping = 
        new ObjectToIndexMapping<NewsStory>();
    
    private List<Rating> allRatings = new ArrayList<Rating>();
    
    
    public DatasetAdapter(NewsDataset newsDataset) {
        this.newsDataset = newsDataset;
        load(this.newsDataset);
    }
    
    private void load(NewsDataset newsDataset) {
        // convert NewsStory into Item
        for(NewsStory story : newsDataset.getStories()) {
            loadItem(story);
        }
        
        // convert NewsUser and NewsRating into User and Rating
        for(NewsUser newsUser : newsDataset.getUsers() ) {
            loadUser(newsUser);
        }
    }
    
    private void loadRating(NewsRating newsRating) {
        NewsUser newsUser = newsDataset.getUser(newsRating.getUserId());
        NewsStory newsStory = newsDataset.getStoryById(newsRating.getStoryId());
        
        assert(newsUser != null);
        assert(newsStory != null);
  
        int userId = userMapping.getIndex(newsUser);
        int itemId = storyMapping.getIndex(newsStory);

        newsUser.addRating(newsRating);        
        Rating r = new Rating(userId, itemId, newsRating.getRating());
        
        //int userId = r.getUserId();
        User user = users.get(userId);
        //int itemId = r.getItemId();
        Item item = items.get(itemId);
        
        user.addRating(r);
        item.addUserRating(r);
        
        allRatings.add(r);
    }
    
    // createUser
    private void loadUser(NewsUser newsUser) {

       // add new User if needed
        Integer userId = userMapping.getIndex(newsUser);
        User user = users.get(userId);
        if( user == null ) {
            user = new User(userId, newsUser.getName());
            users.put(userId, user);
        }

        
        List<NewsRating> userRatings = newsUser.getAllRatings();
        for(NewsRating r : userRatings) {
            loadRating(r);
        }
    }
    
    private void loadItem(NewsStory newsStory) {
        Integer itemId = storyMapping.getIndex(newsStory);
        Item item = items.get(itemId);
        if( item == null ) {
            item = new Item(itemId, newsStory.getTitle());
            items.put(itemId, item);
        }
    }
    
    
    public Item getItemForStoryId(String storyId) {
        NewsStory newsStory = newsDataset.getStoryById(storyId);
        Integer itemId = storyMapping.getIndex(newsStory);
        return items.get(itemId); 
    }

    public User getUserForNewsUserId(String userId) {
        NewsUser newsUser = newsDataset.getUser(userId);
        Integer id = userMapping.getIndex(newsUser);
        return users.get(id); 
    }
    
        
    public NewsStory getNewsStoryForItemId(Integer itemId) {
        return storyMapping.getObject(itemId); 
    }

    public NewsUser getUserForNewsUserId(Integer userId) {
        return userMapping.getObject(userId); 
    }

    // ------------------------------------------------
    
    public String[] getAllTerms() {
        throw new UnsupportedOperationException("not implemented.");
    }

    public double getAverageItemRating(int itemId) {
        return getItem(itemId).getAverageRating();
    }

    public double getAverageUserRating(int userId) {
        return getUser(userId).getAverageRating();
    }

    public Item getItem(Integer itemId) {
        return items.get(itemId);
    }

    public int getItemCount() {
        return items.size();
    }

    public Collection<Item> getItems() {
        return items.values();
    }

    public String getName() {
        return newsDataset.getDatasetName();
    }

    public Collection<Rating> getRatings() {
        return allRatings;
    }

    public int getRatingsCount() {
        return allRatings.size();
    }

    public User getUser(Integer userId) {
        return users.get(userId);
    }

    public int getUserCount() {
        return users.size();
    }

    public Collection<User> getUsers() {
        return users.values();
    }

    public boolean isIdMappingRequired() {
        return true;
    }
}
