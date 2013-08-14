package edu.cornell.artillerymenagerie;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
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

public class NewBattleServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(NewBattleServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	
    	String player1 = req.getParameter("player1");
    	String player2 = req.getParameter("player2");
    	
    	System.out.println(player1 + ", " +player2);
    	
    	// Create new battle entity
        Entity battle = new Entity("Battle");
        if (player1 != null) {
        	battle.setProperty("player1", player1);
        }
        else {
        	battle.setProperty("player1", "???");
        }
        
        if (player2 != null) {
        	battle.setProperty("player2", player2);
        }
        else {
        	battle.setProperty("player2", "???");
        }
        Date date = new Date();
        battle.setProperty("startDate", date);
        battle.setProperty("updateDate", date);
        battle.setProperty("finished", false);
        Text t = new Text("");
        battle.setProperty("battleState", t);
        battle.setProperty("turn", -1);
        
        battle.setProperty("state", "requested");
        battle.setProperty("army1", null);
        battle.setProperty("army2", null);
        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(battle);
        
        resp.getWriter().print(KeyFactory.keyToString(battle.getKey()));
    }
}
        
