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

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CancelBattleServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(NewBattleServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	
    	String battleKeyString = req.getParameter("battleKey");
    	String player = req.getParameter("player");
    	
    	System.out.println("Cancel Battle: " + player);
    	
    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		// Get battle entity
		Key battleKey = KeyFactory.stringToKey(battleKeyString);
		try {
			Entity battle = datastore.get(battleKey);
			String player1 = (String) battle.getProperty("player1");
			String player2 = (String) battle.getProperty("player2");
			//System.out.println("Players: " + player1 + ", " + player2);
			if (player != null) {
				if (player1 != null && player.equals(player1)) {
					battle.setProperty("state", "canceled");
					datastore.put(battle);
					resp.getWriter().print("done");
					return;
				}
				if (player2 != null && player.equals(player2)) {
					battle.setProperty("state", "canceled");
					datastore.put(battle);
					resp.getWriter().print("done");
					return;
				}
			}
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resp.getWriter().print("failed");
    }
}
        
