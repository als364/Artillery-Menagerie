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

public class ResumeBattleServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(NewBattleServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	
    	String battleKeyString = req.getParameter("battleKey");
    	String player = req.getParameter("player");
    	try {
    		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    		
    		// Get battle entity
    		Key battleKey = KeyFactory.stringToKey(battleKeyString);
			Entity battle = datastore.get(battleKey);
			
			JSONObject response = new JSONObject();
			
			response.put("battleKey", battleKeyString);

			String player1 = (String) battle.getProperty("player1");
        	String player2 = (String) battle.getProperty("player2");
        	int playerNum = -1;
        	if (player.equals(player1)) {
        		playerNum = 0;
        	}
        	else if (player.equals(player2)) {
        		playerNum = 1;
        	}
        	
        	response.put("playerNumber", playerNum);
        	
        	if (((Long)battle.getProperty("turn")).intValue() > 0) {
        		response.put("isFirstTurn", false);
        	}
        	else {
        		response.put("isFirstTurn", true);
        	}
        	
        	// Get Armies
        	
        	int startingPlayerNumber = 1;
			int endingPlayerNumber = 2;
			boolean complete = true;
			for (int i = startingPlayerNumber; i <= endingPlayerNumber; i++) {
				String tempPlayer = (String) battle.getProperty("player" + i);
				if (tempPlayer == null) {
					complete = false;
					break;
				}
			}
			if (complete) {
				JSONArray armies = new JSONArray();
				for (int i = startingPlayerNumber; i <= endingPlayerNumber; i++) {
					Text t = (Text) battle.getProperty("army" + i);
					armies.put(new JSONObject(t.getValue()));
				}
				response.put("armies", armies);
				response.put("complete", complete);
			}
			response.put("complete", complete);
			
			resp.getWriter().print(response);
			
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}