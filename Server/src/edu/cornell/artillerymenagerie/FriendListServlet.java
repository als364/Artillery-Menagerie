package edu.cornell.artillerymenagerie;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.FacebookJsonRestClient;
import com.google.code.facebookapi.FacebookParam;
import com.google.code.facebookapi.ProfileField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


public class FriendListServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(NewBattleServlet.class.getName());
    
    private static final String FACEBOOK_USER_CLIENT = "facebook.user.client";
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	
    	String userID = req.getParameter("userID");
    	
    	FacebookJsonRestClient client = new FacebookJsonRestClient(ArtilleryMenagerie.appID, ArtilleryMenagerie.appSecret);
    	
    	JSONArray response = null;
    	try {
			response = client.friends_get(Long.parseLong(userID));
		} catch (FacebookException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//System.out.println("FriendList1: "+ response);
        
    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    	
    	JSONArray friendArray = new JSONArray();
    	try {
	    	for (int i = 0; i < response.length(); i++) {
	    		Query q = new Query("Player");
	        	q.addFilter("userID", Query.FilterOperator.EQUAL, Long.toString(response.getLong(i)));
				PreparedQuery pq = datastore.prepare(q);
				for (Entity result : pq.asIterable()) {
		        	JSONObject friend = new JSONObject();
					friend.put("userID", result.getProperty("userID"));
					friend.put("name", result.getProperty("name"));
		        	friendArray.put(friend);
		        }
	    	}
    	} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	resp.getWriter().print(friendArray);
    }
}
        
