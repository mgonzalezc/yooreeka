package iweb2.ch7.crawling;

import iweb2.ch7.core.NewsDataset;
import iweb2.ch7.core.NewsStory;
import iweb2.ch7.recommendation.NewsRating;
import iweb2.ch7.recommendation.NewsUser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseNewsDataset implements NewsDataset {
    
    private Map<String, NewsUser> newsUsers = new HashMap<String, NewsUser>();
    private List<NewsRating> allRatings = new ArrayList<NewsRating>();

    private String userAndRatingsFilename = null;
    
    BaseNewsDataset() {
        
    }
    
    public void init() {
        loadTopics();
        loadStories();
        
        if( userAndRatingsFilename != null ) {
            loadUsersAndRatings(userAndRatingsFilename);
        }
        else {
            System.out.println("No users and ratings to load.");
        }
        
    }
    
    
    public NewsUser getUser(String userId) {
        return newsUsers.get(userId);
    }
    
    
    public void setUserAndRatingsFilename(String fileName) {
        this.userAndRatingsFilename = fileName;
    }
    
    public void loadUsersAndRatings(String fileName) {

        if( newsUsers.size() > 0 ) {
            System.out.println("Users and ratings already loaded.");
            return;
        }
        
        System.out.println("Loading users and ratings...");
        try {
            File           file = new File(fileName);
            FileReader  fReader = new FileReader(file);
            
            BufferedReader bR = new BufferedReader(fReader); 

            loadUsersAndRatings(bR);
        }
        catch(IOException e) {
            throw new RuntimeException(
                    "Error while loading user and rating data from file: '" +
                    fileName + "'");
        }
    }
    
    public void loadUsersAndRatings(BufferedReader bR) throws IOException {
        
        Map<String, NewsUser> loadedUsers = new HashMap<String, NewsUser>();
        
        String line = null;
        
        while ( (line = bR.readLine()) != null ) {
                
            // skip empty lines
            if( line.trim().length() == 0 ) {
                continue;
            }
            
            // skip comments
            if( line.startsWith("#") ) {
                continue;
            }
            
            String[] data = line.split(",");

            String userId = data[0];
            String userName = data[1];
            // here we are using story title as a unique identifier of the story.
            String storyTitle = data[2];
            int rating = Integer.parseInt(data[3]);

            NewsUser user = loadedUsers.get(userId); 
            if( user == null ) {
                user = new NewsUser();
                user.setId(userId);
                user.setName(userName);
                loadedUsers.put(userId, user);
            }

            NewsStory story = findStoryByUniqueTitle(storyTitle);
            if( story == null ) {
                throw new RuntimeException(
                        "Failed to find story with title: '" + storyTitle + "'");
            }
            NewsRating r = new NewsRating(user.getId(), story.getId(), rating);
            user.addRating(r);
        }

        for(NewsUser u : loadedUsers.values()) {
            // will add users and ratings
            addUser(u);
        }
    }

    private void addUser(NewsUser newsUser) {

        newsUsers.put(newsUser.getId(), newsUser);
         
         List<NewsRating> userRatings = newsUser.getAllRatings();
         for(NewsRating r : userRatings) {
             addNewsRating(r);
         }
    }
    
    private void addNewsRating(NewsRating newsRating) {

        NewsUser newsUser = newsUsers.get(newsRating.getUserId());
        NewsStory newsStory = getStoryById(newsRating.getStoryId());
        assert(newsUser != null);
        assert(newsStory != null);

        allRatings.add(newsRating);
    }
    
    
    private NewsStory findStoryByUniqueTitle(String title) {
        
        NewsStory result = null;
        
        List<NewsStory> stories = findStoriesByTitle(title);
        
        if( stories.size() == 0 ) {
            result = null;
        }
        else if (stories.size() > 1 ) {
            throw new RuntimeException(
                    "Found multiple stories with the same title: '" +
                    title + "', number of stories found: " + stories.size() +
                    ". You can't use this logic for cases when story title is" +
                    " not unique.");
        }
        else {
            result = stories.get(0);
        }
        
        
        return result;
    }
    
    public List<NewsUser> getUsers() {
        return new ArrayList<NewsUser>( newsUsers.values() );
    }
    
}
