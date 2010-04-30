package iweb2.ch2.clicks;

import iweb2.ch5.ontology.core.BaseConcept;
import iweb2.ch5.ontology.core.BaseInstance;
import iweb2.ch5.ontology.core.StringAttribute;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * This class captures a user click.
 * 
 * @author babis
 *
 */
public class UserClick  extends BaseInstance {

	UserQuery userQuery;
	String url;
	
	public UserClick() {
		super();
	}
	
	public UserClick(UserQuery uQ, String url) {
		
		super();

		userQuery = uQ;
		this.setConcept(new BaseConcept(url));
		
    	attributes = new StringAttribute[userQuery.getQueryTerms().length+1];
        
        attributes[0] = new StringAttribute("UserName",userQuery.getUid());
        
        int j=1;
        for (String s : uQ.getQueryTerms()) {
        	attributes[j] = new StringAttribute("QueryTerm_"+j,s);
        	j++;
        }
	}
	
	@Override
    public UserClick[] load(BufferedReader bR) throws IOException {
        
        ArrayList<UserClick> userClicks = new ArrayList<UserClick>();
        
        String line;
        boolean hasMoreLines = true;
        
        while (hasMoreLines) {
            
            line=bR.readLine();
            
            if (line == null) {
            
                hasMoreLines = false;
            
            } else {
                
                String[] data = line.split(",");

                UserQuery uQ = new UserQuery(data[0],data[1]);
                
            	UserClick userClick = new UserClick(uQ,data[2].substring(1, data[2].length()-1));
                
            	userClick.print();
            	
            	userClicks.add(userClick);
            }
        }
        
        return userClicks.toArray(new UserClick[userClicks.size()]);
    }

	
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUrl() == null) ? 0 : getUrl().hashCode());
        result = prime * result
                + ((userQuery == null) ? 0 : userQuery.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final UserClick other = (UserClick) obj;
        if (getUrl() == null) {
            if (other.getUrl() != null)
                return false;
        } else if (!getUrl().equals(other.getUrl()))
            return false;
        if (userQuery == null) {
            if (other.userQuery != null)
                return false;
        } else if (!userQuery.equals(other.userQuery))
            return false;
        return true;
    }


    /**
     * The concept of a user click is its URL
     * 
	 * @return the url
	 */
	public String getUrl() {
		return getConcept().getName();
	}
	

	/**
	 * @return the userQuery
	 */
	public UserQuery getUserQuery() {
		return userQuery;
	}
	
	
}
