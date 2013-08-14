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

public class GetArmiesServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(NewBattleServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	
    	String playerNumber = req.getParameter("playerNumber");
    	String battleKeyString = req.getParameter("battleKey");
    	
    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
    	JSONObject armyInfo = new JSONObject();
    	
    	
		// Get battle entity
		Key battleKey = KeyFactory.stringToKey(battleKeyString);
		Entity battle;
		try {
			battle = datastore.get(battleKey);
			int startingPlayerNumber = 1;
			int endingPlayerNumber = 2;
			boolean complete = true;
			for (int i = startingPlayerNumber; i <= endingPlayerNumber; i++) {
				String player = (String) battle.getProperty("player" + i);
				if (player == null) {
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
				armyInfo.put("armies", armies);
				armyInfo.put("complete", complete);
			}
			armyInfo.put("complete", complete);
			
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		resp.getWriter().print(armyInfo);
    }
}
