package tumblrinterface;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import tumblib.Post;
import tumblib.PostDeserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TumblrQuery {

	private URL url;
	private String urlString;
	private String jsonString;
	ArrayList<Post> postList =  new ArrayList<Post>();
	
	public TumblrQuery(){
		this("newsweek");
	}
	
	/**
	 * @param getParameters A linked list containing the GET parameters defined at 
	 * http://www.tumblr.com/docs/en/api, and the values for the parameters.  The list
	 * should start with a GET parameter and alternate between parameter and value.*/
	public TumblrQuery(LinkedList<String> getParameters){
		this("newsweek", getParameters);
	}
	
	/**
	 * @param subdomain The subdomain of a tumblr url. Such as "newsweek" in the case
	 * of http://newsweek.tumblr.com.*/
	public TumblrQuery(String subdomain){
		this(subdomain, new LinkedList());
	}

	/**
	 * @param subdomain The subdomain of a tumblr url. Such as "newsweek" in the case
	 * of http://newsweek.tumblr.com.
	 * @param getParameters A linked list containing the GET parameters defined at 
	 * {@link http://www.tumblr.com/docs/en/api}, and the values for the parameters.  The list
	 * should start with a GET parameter and alternate between parameter and value.*/
	public TumblrQuery(String subdomain, LinkedList<String> getParameters){
		setUrlString("http://" + subdomain + ".tumblr.com");
		appendToUrlString(getParameters);
		setUrl(urlString);
		setJson();
		setPosts();
	}
	
	public TumblrQuery(URL url, LinkedList<String> getParameters){
		setUrlString(url.toString());
		appendToUrlString(getParameters);
		setUrl(urlString);
		setJson();
		setPosts();
	}
	/**
	 * @return the url*/
	public URL getUrl() {
		return url;
	}

	/**
	 * @param url the url to set*/
	void setUrl(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the urlString*/
	public String getUrlString() {
		return urlString;
	}

	/**
	 * @param urlString the urlString to set*/
	void setUrlString(String urlString) {
		if(urlString.endsWith("/"))
			urlString = urlString.substring(0, urlString.length() - 1);
		this.urlString = urlString + "/api/read/json";
	}
	
	/** @param getParameters A linked list containing the GET parameters defined at 
	 * {@link http://www.tumblr.com/docs/en/api}, and the values for the parameters.  The list
	 * should start with a GET parameter and alternate between parameter and value.*/
	public void appendToUrlString(LinkedList<String> getParameters){
		//Start string with ?
		String addendum = "?";
		//Add the parameter and its value, seperate different parameters with &.
		while(!getParameters.isEmpty()){
			addendum += getParameters.removeFirst();
			addendum += "=" + getParameters.removeFirst() + "&";
		}
		
		urlString += addendum;
	}
	
	/**@return The Json that corresponds to this query.*/
	public void setJson(){
		InputStream in = null;
		try {
			in = this.url.openStream();
		}catch (FileNotFoundException e){
			e.printStackTrace();
			System.err.println("  Please verify that the subdomain or url provided exists.");
			System.err.println("  Make sure that the url provided does not include a trailing slash.");
			
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Please verify that the subdomain or url provided exists.");
		} 
		Reader reader = new InputStreamReader(in);
		BufferedReader bufferedReader = new BufferedReader(reader);
		
		try {
			jsonString = bufferedReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Format the tumblr jsonString to be proper json syntax.
		jsonString = jsonString.replace("var tumblr_api_read = ", "");
		if(jsonString.endsWith(");"))
			jsonString = jsonString.substring(0, jsonString.length()-2);
		
	}
	
	public String getJson(){
		return jsonString;
	}
		
	public void setPosts(){
		String jsonString = getJson();
		Gson gson = new GsonBuilder().
		registerTypeAdapter(Post.class, new PostDeserializer()).
		create();

		//Parse the JsonString and return a JsonElement (supertype).
		JsonParser jParser = new JsonParser();
		JsonElement jElement = jParser.parse(jsonString);

		//The JsonElement should be a JsonObject (the tumblog is a JSON objects).
		JsonObject jObject = jElement.getAsJsonObject();
		//From the tumblog extract the JSON-format posts in an Iterator.
		Iterator<JsonElement> postIterator = jObject.getAsJsonArray("posts").iterator();

		while(postIterator.hasNext()){
			jElement = postIterator.next();
			postList.add(gson.fromJson(jElement, Post.class));
		}
		
	}
	
	public ArrayList<Post> getPosts(){
		return postList;
	}
	
	public ArrayList<Post> joinPosts(ArrayList<Post> otherPostList){
		ArrayList<Post> newPostList = new ArrayList<Post>(postList);
		newPostList.addAll(otherPostList);
		return newPostList;
	}
	
}
