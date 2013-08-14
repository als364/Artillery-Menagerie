package edu.cornell.slicktest;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerConnection {
	
	//static final String serverURL = "http://localhost:8888/";
	static final String serverURL = "http://armageddonwaffle.appspot.com/";
	//static final String serverURL = "http://artillerywaffle.appspot.com/";
	
	public static String getBattle(int n) throws IOException, JSONException {
		
		JSONArray battles = findBattle();
		String battleKey;
		if (battles.length() == n) {
			battleKey = newBattle();
		}
		else {
			battleKey = battles.getString(n);
		}
		return battleKey;
	}
	
	public static String getBattle() throws IOException, JSONException {
		return getBattle(0);
	}
	
	public static JSONArray findBattle() throws IOException, JSONException {
		String text = doPostRequest(serverURL + "findbattle", "");
		return new JSONArray(text);
	}
	
	public static JSONArray getBattleList(String type, String player) throws IOException, JSONException {
		String parameters = "type=" + type + "&player=" + player;
		String text = doPostRequest(serverURL + "getbattlelist", parameters);
		return new JSONArray(text);
	}
	
	public static String cancelBattle(String player, String battleKey) throws IOException {
		String parameters = "battleKey=" + battleKey + "&player=" + player;
		String text = doPostRequest(serverURL + "cancelbattle", parameters);
		return text;
	}
	
	public static JSONArray friendList(String userID) throws IOException, JSONException {
		String parameters = "userID=" + userID;
		String text = doPostRequest(serverURL + "friendlist", parameters);
		return new JSONArray(text);
	}
	
	public static String newBattle(String player1, String player2) throws IOException, JSONException {
		String parameters = "player1=" + player1 + "&player2=" + player2;
		System.out.println(parameters);
		//parameters = parameters.replaceAll(" ", "%20");
		String text = doPostRequest(serverURL + "newbattle", parameters);
		return text;
	}
	
	public static String newBattle(String player1) throws IOException, JSONException {
		String parameters = "player1=" + player1;
		System.out.println(parameters);
		String text = doPostRequest(serverURL + "newbattle", parameters);
		return text;
	}
	
	public static String newBattle() throws IOException, JSONException {
		String text = doPostRequest(serverURL + "newbattle", "");
		return text;
	}
	
	public static JSONObject joinBattle(String battleKey, String player, String army) throws IOException, JSONException {
		String parameters = "battleKey=" + battleKey + "&player=" + player + "&army=" + army;
		//parameters = parameters.replaceAll(" ", "%20");
		String text = doPostRequest(serverURL + "joinbattle", parameters);
		return new JSONObject(text);
	}
	
	public static JSONObject resumeBattle(String battleKey, String player) throws IOException, JSONException {
		String parameters = "battleKey=" + battleKey + "&player=" + player;
		String text = doPostRequest(serverURL + "resumebattle", parameters);
		return new JSONObject(text);
	}
	
	public static JSONObject getArmies(String battleKey, int playerNumber) throws IOException, JSONException {
		String parameters = "battleKey=" + battleKey + "&playerNumber=" + playerNumber;
		String text = doPostRequest(serverURL + "getarmies", parameters);
		return new JSONObject(text);
	}
	
	public static String storeBattle(JSONObject state) throws IOException, JSONException {
		String parameters = "battleState=" + state;
		//parameters = parameters.replaceAll(" ", "%20");
		String text = doPostRequest(serverURL + "storebattlestate", parameters);
		return text;
	}
	
	public static JSONObject loadBattle(String battleKey, int turn) throws IOException, JSONException {
		String parameters = "battleKey=" + battleKey + "&turn=" + turn;
		String text = doPostRequest(serverURL + "loadbattlestate", parameters);
		return new JSONObject(text);
	}
	
	public static String newUnitID(String player) throws IOException, JSONException {
		String parameters = "player=" + player;
		//parameters = parameters.replaceAll(" ", "%20");
		String text = doPostRequest(serverURL + "newunitid", parameters);
		return text;
	}
	
	public static String newItemID(String player) throws IOException, JSONException {
		String parameters = "player=" + player;
		//parameters = parameters.replaceAll(" ", "%20");
		String text = doPostRequest(serverURL + "newitemid", parameters);
		return text;
	}
	
	public static JSONObject logIn(String userID, String playerName) throws IOException, JSONException {
		String parameters = "userID=" + userID + "&name=" + playerName;
		String text = doPostRequest(serverURL + "login", parameters);
		return new JSONObject(text);
	}
	
	public static String updatePlayer(JSONObject resources) throws IOException, JSONException {
		String parameters = "resources=" + resources;
		String text = doPostRequest(serverURL + "updateplayer", parameters);
		return text;
	}
	
	private static String doPostRequest(String urlStr, String data) throws IOException {
		
		// Send data
	    URL url = new URL(urlStr);
	    URLConnection conn = url.openConnection();
	    conn.setDoOutput(true);
	    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	    wr.write(data);
	    wr.flush();

	    // Get the response
	    String text = new Scanner(conn.getInputStream()).useDelimiter("\\Z").next();
	    wr.close();
	    return text;
	}
}
