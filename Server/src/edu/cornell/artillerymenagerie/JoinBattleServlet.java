package edu.cornell.artillerymenagerie;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JoinBattleServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(NewBattleServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	String player = req.getParameter("player");
    	String battleKeyString = req.getParameter("battleKey");
    	String army = req.getParameter("army");
    	
    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		// Get battle entity
		Key battleKey = KeyFactory.stringToKey(battleKeyString);
		Entity battle = null;
		try {
			battle = datastore.get(battleKey);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject response = new JSONObject();
		try {
			if (battle == null) {
				response.put("battleFound", false);
				resp.getWriter().print(response);
				return;
			}
			response.put("battleFound", true);
	        if ((Boolean)battle.getProperty("finished")) {
				response.put("joined", false);
	        }
	        else {
	        	String player1 = (String) battle.getProperty("player1");
	        	String player2 = (String) battle.getProperty("player2");
	        	if (player1 == null || player1.equals("???")) {
	        		response.put("joined", true);
	        		response.put("number", 0);
	        		battle.setProperty("player1", player);
	        		battle.setProperty("army1", new Text(army));
	        	}
	        	else if (player1.equals(player)) {
	        		response.put("joined", true);
	        		response.put("number", 0);
	        		battle.setProperty("army1", new Text(army));
	        	}
	        	else if (player2 == null || player2.equals("???")) {
	        		response.put("joined", true);
	        		response.put("number", 1);
	        		battle.setProperty("player2", player);
	        		battle.setProperty("army2", new Text(army));
	        	}
	        	else if (player2.equals(player)) {
	        		response.put("joined", true);
	        		response.put("number", 1);
	        		battle.setProperty("army2", new Text(army));
	        	}
	        	else {
	        		response.put("joined", false);
	        	}
	        	
	        	if (battle.getProperty("army1") != null && battle.getProperty("army2") != null) {
	        		battle.setProperty("state", "started");
	        	}
	        	
	        	datastore.put(battle);
	        }
	        
	        
	        //System.out.println(response);
	        
	        resp.getWriter().print(response);
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
        

