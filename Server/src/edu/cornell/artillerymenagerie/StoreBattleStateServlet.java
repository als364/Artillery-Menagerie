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

public class StoreBattleStateServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(NewBattleServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	String battleStateString = req.getParameter("battleState");
    	try {
    		
    		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    		
    		JSONObject battleStateJSON = new JSONObject(battleStateString);
    		
    		//System.out.println("Store:" + battleStateJSON);
    		
    		// Get battle entity
    		String battleKeyString = battleStateJSON.getString("battleKey");
        	Key battleKey = KeyFactory.stringToKey(battleKeyString);
        	Entity battle = datastore.get(battleKey);
            
        	if (battleStateJSON.getInt("turn") <= ((Long) battle.getProperty("turn")).intValue()) {
        		resp.getWriter().print("failed");
        		return;
        	}
        	
        	// Update the battle state
            battle.setProperty("finished", (boolean) battleStateJSON.getBoolean("finished"));
            if ((boolean) battleStateJSON.getBoolean("finished")) {
            	battle.setProperty("state", "finished");
            }
            battle.setProperty("turn", battleStateJSON.getInt("turn"));
            Date date = new Date();
            battle.setProperty("updateDate", date);
            Text t = new Text(battleStateString);
            battle.setProperty("battleState", t);
            datastore.put(battle);
            
            resp.getWriter().print("done");
            return;
            
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	resp.getWriter().print("failed");
    }
}
