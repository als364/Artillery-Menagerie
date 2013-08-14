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
	static final String serverURL = "http://1.armageddonwaffle.appspot.com/";
	
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
		//URLConnection connection = new URL(serverURL + "findbattle").openConnection();
		//String text = new Scanner(connection.getInputStream()).useDelimiter("\\Z").next();
		String text = doPostRequest(serverURL + "findbattle", "");
		return new JSONArray(text);
	}
	
	public static String newBattle(String player1, String player2) throws IOException, JSONException {
		String parameters = "player1=" + player1 + "&player2" + player2;
		parameters = parameters.replaceAll(" ", "%20");
		//URLConnection connection = new URL(serverURL + "newbattle" + parameters).openConnection();
		//String text = new Scanner(connection.getInputStream()).useDelimiter("\\Z").next();
		String text = doPostRequest(serverURL + "newbattle", parameters);
		return text;
	}
	
	public static String newBattle() throws IOException, JSONException {
		//URLConnection connection = new URL(serverURL + "newbattle").openConnection();
		//String text = new Scanner(connection.getInputStream()).useDelimiter("\\Z").next();
		String text = doPostRequest(serverURL + "newbattle", "");
		return text;
	}
	
	public static JSONObject joinBattle(String battleKey, String player) throws IOException, JSONException {
		String parameters = "battleKey=" + battleKey + "&player=" + player;
		parameters = parameters.replaceAll(" ", "%20");
		//URLConnection connection = new URL(serverURL + "joinbattle" + parameters).openConnection();
		//String text = new Scanner(connection.getInputStream()).useDelimiter("\\Z").next();
		String text = doPostRequest(serverURL + "joinbattle", parameters);
		return new JSONObject(text);
	}
	
	public static String storeBattle(JSONObject state) throws IOException, JSONException {
		String parameters = "battleState=" + state;
		parameters = parameters.replaceAll(" ", "%20");
		//URLConnection connection = new URL(serverURL + "storebattlestate" + parameters).openConnection();
		//String text = new Scanner(connection.getInputStream()).useDelimiter("\\Z").next();
		String text = doPostRequest(serverURL + "storebattlestate", parameters);
		return text;
	}
	
	public static JSONObject loadBattle(String battleKey, int turn) throws IOException, JSONException {
		String parameters = "battleKey=" + battleKey + "&turn=" + turn;
		//URLConnection connection = new URL(serverURL + "loadbattlestate" + parameters).openConnection();
		//String text = new Scanner(connection.getInputStream()).useDelimiter("\\Z").next();
		String text = doPostRequest(serverURL + "loadbattlestate", parameters);
		return new JSONObject(text);
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
