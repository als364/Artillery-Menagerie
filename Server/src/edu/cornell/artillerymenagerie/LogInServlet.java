package edu.cornell.artillerymenagerie;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.FacebookJsonRestClient;
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

import org.json.JSONArray;

public class LogInServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(NewBattleServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	
    	String userID = req.getParameter("userID");
    	String name = req.getParameter("name");
    	
    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    	
    	Query q = new Query("Player");
    	q.addFilter("userID", Query.FilterOperator.EQUAL, userID);
    	PreparedQuery pq = datastore.prepare(q);
    	
    	int count = 0;
    	for (Entity result : pq.asIterable()) {
    		count++;
    	}
    	
    	JSONObject playerObject = new JSONObject();
    	if (count > 0) {
    		//Key playerKey = KeyFactory.stringToKey(userID);
    		Key playerKey = KeyFactory.createKey("Player", userID);
    		try {
				Entity player = datastore.get(playerKey);
				String userID1 = (String) player.getProperty("userID");
				playerObject.put("userID", userID1);
				String name1 = (String) player.getProperty("name");
				playerObject.put("name", name1);
				Text units = (Text) player.getProperty("units");
				playerObject.put("units", units.getValue());
				Text items = (Text) player.getProperty("items");
				playerObject.put("items", items.getValue());
				Text army = (Text) player.getProperty("army");
				playerObject.put("army", army.getValue());
				int gold = ((Long) player.getProperty("gold")).intValue();
				playerObject.put("gold", gold);
				int rank = ((Long) player.getProperty("rank")).intValue();
				playerObject.put("rank", rank);
				playerObject.put("newAccount", false);
				
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	else {
	    	// Create new battle entity
    		try {
		        Entity player = new Entity("Player", userID);
		        player.setProperty("userID", userID);
		        playerObject.put("userID", userID);
		        
		        FacebookJsonRestClient client = new FacebookJsonRestClient(ArtilleryMenagerie.appID, ArtilleryMenagerie.appSecret);
		        List<Long> temp = new ArrayList<Long>();
		    	temp.add(Long.parseLong(userID));
		    	
		    	Set<ProfileField> c = new HashSet<ProfileField>();
		    	c.add(ProfileField.NAME);
		    	JSONArray info = null;
		    	try {
					info = client.users_getInfo(temp, c);
					System.out.println("UserInfo: " + info);
				} catch (FacebookException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
		        name = info.getJSONObject(0).getString("name");
		        
		        System.out.println("Name: " + name);
		    	
		        player.setProperty("name", name);
		    	playerObject.put("name", name);
		        Date date = new Date();
		        player.setProperty("createDate", date);
				playerObject.put("newAccount", true);
				datastore.put(player);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (org.json.JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	resp.getWriter().print(playerObject);
    }
    
}
        
