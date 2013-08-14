package edu.cornell.artillerymenagerie;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.FacebookParam;
import com.google.code.facebookapi.FacebookXmlRestClient;
import com.google.code.facebookapi.ProfileField;


public class Game extends HttpServlet
{

    //facebook give it!
    String apiKey = "your api key";
    String secretKey = "your secret key";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, FacebookException
    {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        //facebook login mechanism give you by http parameter the session key
        //needed for client api request.
        //String sessionKey = request.getParameter(FacebookParam.SESSION_KEY.toString());

        //initialize a facebook xml client (you can choose different client version: xml, jaxb or json)
        //the init is done by apiKey, secretKey and session key previosly requested
        //FacebookXmlRestClient client = new FacebookXmlRestClient(apiKey, secretKey, sessionKey);
            
        //This code line obtain the user logged id
        //Long uid = client.users_getLoggedInUser();

        //print user info.
        //out.println(printUserInfo(uid, client, sessionKey));
        
        out.print(printIndexPage("abc"));
     
    }
    
 public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	 resp.setContentType("text/html;charset=UTF-8");
     PrintWriter out = resp.getWriter();
	 	try {
			out.print(printIndexPage("abc"));
		} catch (FacebookException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private String printUserInfo(Long uid, FacebookXmlRestClient client, String sessionKey) throws FacebookException
    {
        StringBuffer ret = new StringBuffer();
        //init array parameter
        ArrayList<Long> uids = new ArrayList<Long>(1);
        uids.add(uid);
        //init field parameter - we choose all profile infos.
        List<ProfileField> fields = Arrays.asList(ProfileField.values());
        //init the client in order to make the xml request
        client = new FacebookXmlRestClient(apiKey, secretKey, sessionKey);
        //get the xml document containing the infos
        Document userInfoDoc = client.users_getInfo(uids, fields);
        //for each info append it to returned string buffer
        for (ProfileField pfield : fields)
        {
            ret.append(pfield.fieldName()).append(" <b>").append(userInfoDoc.getElementsByTagName(pfield.fieldName()).item(0).getTextContent()).append("</b>");
            ret.append("</br>");
        }
        return ret.toString();
    }
    
    private String printIndexPage(String userID) throws FacebookException
    {
        
        String indexPage = "<html><head><title>AppletLoader</title></head><body>\n" +
        		"<applet code=\"org.lwjgl.util.applet.AppletLoader\" archive=\"lwjgl_util_applet.jar, lzma.jar\" codebase=\".\" width=\"680\" height=\"540\">\n" +
        		"<param name=\"al_title\" value=\"appletloadertest\">\n" +
        		"<param name=\"al_main\" value=\"org.newdawn.slick.AppletGameContainer\">\n" +
        		"<param name=\"game\" value=\"edu.cornell.slicktest.ClientEngine\">\n" +
        		"<param name=\"al_jars\" value=\"mygame.jar, slf4j-api-1.6.3.jar, jbox2d-library-2.1.2.1-SNAPSHOT.jar, jbox2d-library-2.1.2.1-SNAPSHOT-javadoc.jar, jbox2d-library-2.1.2.1-SNAPSHOT-sources.jar, lwjgl_applet.jar.pack.lzma, lwjgl.jar.pack.lzma, phys2d.jar, jinput.jar.pack.lzma, lwjgl_util.jar.pack.lzma, slick.jar, jbox2d.jar, jogg-0.0.7.jar, jorbis-0.0.15.jar\">\n" +
        		"<param name=\"al_linux\" value=\"linux_natives.jar.lzma\">\n" +
        		"<param name=\"al_mac\" value=\"macosx_natives.jar.lzma\">\n" +
        		"<param name=\"al_solaris\" value=\"solaris_natives.jar.lzma\">\n" +
        		"<param name=\"al_debug\" value=\"true\">\n" +
        		"<param name=\"separate_jvm\" value=\"true\">\n" +
        		"<param name=\"userID\" value=\"" + userID + "\">\n" +
        		"</applet></body></html>";
        
        return indexPage;
    }
}