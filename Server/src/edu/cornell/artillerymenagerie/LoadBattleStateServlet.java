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

public class LoadBattleStateServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(NewBattleServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	
    	String battleKeyString = req.getParameter("battleKey");
    	String turn = req.getParameter("turn");
    	try {
    		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    		
    		// Get battle entity
    		Key battleKey = KeyFactory.stringToKey(battleKeyString);
			Entity battle = datastore.get(battleKey);
			
			JSONObject response = new JSONObject();
			
			// Check to see if the player's state is most up to date
			int battleTurn = ((Long) battle.getProperty("turn")).intValue();
			if (Integer.parseInt(turn) == battleTurn) {
				response.put("needUpdate", false);
				resp.getWriter().print(response);
				return;
			}
			else {
				response.put("needUpdate", true);
			}
			
			String battleStateStr = ((Text) battle.getProperty("battleState")).getValue();
			response.put("battleState", battleStateStr);
			
			//System.out.println("Load:" + response);
			resp.getWriter().print(response);
			
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}