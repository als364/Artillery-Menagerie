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
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetBattleListServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(NewBattleServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	
    	String type = req.getParameter("type");
    	String player = req.getParameter("player");
    	
    	System.out.println("BattleList: " + type +",  " + player);
    	
    	String state = null;
    	if (type.equals("sent") || type.equals("received") || type.equals("random")) {
    		state = "requested";
    	}
    	else if (type.equals("ongoing")){
    		state = "started";
    	}
    	
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        JSONArray array = new JSONArray();
        
        if (type.equals("sent") || type.equals("ongoing")) {
	        Query q = new Query("Battle");
	        q.addFilter("state", Query.FilterOperator.EQUAL, state);
	        q.addFilter("player1", Query.FilterOperator.EQUAL, player);
	        
	        PreparedQuery pq = datastore.prepare(q);
	        addResultToArray(pq, array, datastore);
        }
        
        if (type.equals("received") || type.equals("ongoing")) {
	        Query q = new Query("Battle");
	        q.addFilter("state", Query.FilterOperator.EQUAL, state);
	        q.addFilter("player2", Query.FilterOperator.EQUAL, player);
	        
	        PreparedQuery pq = datastore.prepare(q);
	        addResultToArray(pq, array, datastore);
        }
        
        if (type.equals("random")) {
	        Query q = new Query("Battle");
	        q.addFilter("state", Query.FilterOperator.EQUAL, state);
	        q.addFilter("player2", Query.FilterOperator.EQUAL, "???");
	        
	        PreparedQuery pq = datastore.prepare(q);
	        addResultToArray(pq, array, datastore);
        }
        
        //System.out.println(array);
        resp.getWriter().print(array);
    }
    
    public void addResultToArray(PreparedQuery pq, JSONArray array, DatastoreService datastore) {
    	JSONObject object;
        Key playerKey;
        Entity playerEntity;
        String player2UserID;
        try {
	        for (Entity result : pq.asIterable()) {
	        	object = new JSONObject();
				object.put("battleKey", KeyFactory.keyToString(result.getKey()));
				object.put("startDate", (Date) result.getProperty("startDate"));
				object.put("turn", ((Long) result.getProperty("turn")).intValue());
				playerKey = KeyFactory.createKey("Player", (String)result.getProperty("player1"));
				playerEntity = datastore.get(playerKey);
				object.put("player1Name", (String)playerEntity.getProperty("name"));
				object.put("player1UserID", (String)playerEntity.getProperty("userID"));
				player2UserID = (String)result.getProperty("player2");
				if (player2UserID == null || player2UserID.equals("???")) {
					object.put("player2Name", "???");
					object.put("player2UserID", "???");
				}
				else {
					playerKey = KeyFactory.createKey("Player", player2UserID);
					playerEntity = datastore.get(playerKey);
					object.put("player2Name", (String)playerEntity.getProperty("name"));
					object.put("player2UserID", (String)playerEntity.getProperty("userID"));
				}
	        	array.put(object);
	        }
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
   