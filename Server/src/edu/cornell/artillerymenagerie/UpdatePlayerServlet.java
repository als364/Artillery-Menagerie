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

public class UpdatePlayerServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(NewBattleServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	
    	String resourcesString = req.getParameter("resources");
    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			JSONObject resources = new JSONObject(resourcesString);
			//Key playerKey = KeyFactory.stringToKey(resources.getString("userID"));
			Key playerKey = KeyFactory.createKey("Player", resources.getString("userID"));
			Entity player = datastore.get(playerKey);
			Text t = new Text(resources.getJSONArray("units").toString());
			player.setProperty("units", t);
			t = new Text(resources.getJSONArray("items").toString());
			player.setProperty("items", t);
			t = new Text(resources.getJSONArray("army").toString());
			player.setProperty("army", t);
			player.setProperty("gold", resources.getInt("gold"));
			player.setProperty("rank", resources.getInt("rank"));
			
			datastore.put(player);
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