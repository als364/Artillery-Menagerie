package edu.cornell.artillerymenagerie;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

public class NewUnitIDServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(NewBattleServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	
    	String player = req.getParameter("player");
    	
    	// Create new unit entity
        Entity unit = new Entity("UnitID");
        unit.setProperty("player", player);
        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(unit);
        
        resp.getWriter().print(KeyFactory.keyToString(unit.getKey()));
    }
}
