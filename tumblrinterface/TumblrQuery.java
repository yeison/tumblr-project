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
/**
 * The TumblrQuery class allows one to construct a Java Object based on a particular
 * query to the tumblr API's JSON end-point.  This query is defined as a URL, 
 * and consists of the location of a tumblr blog, and one or more optional GET 
 * parameters listed at {@link http://www.tumblr.com/docs/en/api}.  The 
 * location may be a subdomain of tumblr.com, or the complete url of the tumblr 
 * blog.
 * 
 * The TumblrQuery object takes the JSON that is returned by the tumblr api, and
 * converts it into Post objects.  An ArrayList of these post objects may be 
 * accessed by using the getPosts() function of TumblrQuery. 
 * @author Yeison Rodriguez
 * 
 */
public class TumblrQuery {

	private URL url;
	private String urlString;
	private String jsonString;
	ArrayList<Post> postList =  new ArrayList<Post>();
	
	/**
	 * Instantiates a default TumblrQuery object, which queries newsweek's
	 * tumblr blog.
	 */
	public TumblrQuery(){
		this("newsweek");
	}
	
	/**
	 * Queries newsweek's tumblr blog using the given GET parameters. 
	 * @param getParameters A linked list containing the GET parameters defined 
	 * at {@link http://www.tumblr.com/docs/en/api}, and the values for the 
	 * parameters.  The list should start with a GET parameter and alternate 
	 * between parameter and value.*/
	public TumblrQuery(LinkedList<String> getParameters){
		this("newsweek", getParameters);
	}
	
	/**
	 * Queries the tumblr blog of the given subdomain.  Uses no GET parameters,
	 * and so returns 20 posts.
	 * @param subdomain The subdomain of a tumblr url.  Such as "newsweek" in 
	 * the case of http://newsweek.tumblr.com.*/
	public TumblrQuery(String subdomain){
		this(subdomain, new LinkedList<String>());
	}

	/**
	 * Queries the tumblr blog of the given subdomain using the GET parameters
	 * given.
	 * @param subdomain The subdomain of a tumblr url. Such as "newsweek" in 
	 * the case of http://newsweek.tumblr.com.
	 * @param getParameters A linked list containing the GET parameters defined 
	 * at {@link http://www.tumblr.com/docs/en/api}, and the values for the 
	 * parameters.  The list should start with a GET parameter and alternate 
	 * between parameter and value.*/
	public TumblrQuery(String subdomain, LinkedList<String> getParameters){
		setUrlString("http://" + subdomain + ".tumblr.com");
		appendToUrlString(getParameters);
		setUrl(urlString);
		generateJson();
		generatePosts();
	}
	
	/**
	 * In case the blog does not exist at a tumblr subdomain, the following 
	 * constructor may be used to query a tumblr blog at the given url.
	 * @param url In proper url format, the address of a tumblr blog.
	 * @param getParameters A linked list containing the GET parameters defined 
	 * at {@link http://www.tumblr.com/docs/en/api}, and the values for the 
	 * parameters.  The list should start with a GET parameter and alternate 
	 * between parameter and value.*/
	public TumblrQuery(URL url, LinkedList<String> getParameters){
		setUrlString(url.toString());
		appendToUrlString(getParameters);
		setUrl(urlString);
		generateJson();
		generatePosts();
	}
	/**
	 * Returns the URL that was used to query the tumblr api.
	 * @return The URL corresponding to the tumblr query.*/
	public URL getUrl() {
		return url;
	}

	/**
	 * @param url the url to set*/
	private void setUrl(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the URL that was used to query the tumblr api, as a String.
	 * @return The URL corresponding to the tumblr query as a String.*/
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
	
	/** @param getParameters A linked list containing the GET parameters defined 
	 * at {@link http://www.tumblr.com/docs/en/api}, and the values for the 
	 * parameters.  The list should start with a GET parameter and alternate 
	 * between parameter and value.*/
	private void appendToUrlString(LinkedList<String> getParameters){
		//Start string with ?
		String addendum = "?";
		//Add the parameter and its value, seperate different parameters with &.
		while(!getParameters.isEmpty()){
			addendum += getParameters.removeFirst();
			addendum += "=" + getParameters.removeFirst() + "&";
		}
		
		urlString += addendum;
	}
	/**
	 * Use the url constructed by this TumblrQuery to query the tumblr api.
	 * The tumblr api will return a Json string that is parsed for its post
	 * information.
	 */
	private void generateJson(){
		InputStream in = null;
		try {
			in = this.url.openStream();
		}catch (FileNotFoundException e){
			e.printStackTrace();
			System.err.println("  Please verify that the subdomain or url " +
					"provided exists.");
			System.err.println("  Make sure that the url provided does not " +
					"include a trailing slash.");
			
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Please verify that the subdomain or url " +
					"provided exists.");
		} 
		Reader reader = new InputStreamReader(in);
		BufferedReader bufferedReader = new BufferedReader(reader);
		
		try {
			jsonString = bufferedReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Format the tumblr jsonString to be proper json syntax.
		jsonString = jsonString.replace("var tumblr_api_read = ", "");
		if(jsonString.endsWith(");"))
			jsonString = jsonString.substring(0, jsonString.length()-2);
		
	}
	
	/**
	 * Get the Json that defines the posts returned by the tumblr api.
	 * @return The Json that corresponds to this Tumblr query, as a String.*/
	public String getJson(){
		return jsonString;
	}
	
	/**
	 * From the Json string of posts, create Post objects and save them in the
	 * postList.
	 */
	private void generatePosts(){
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
		Iterator<JsonElement> postIterator 
								= jObject.getAsJsonArray("posts").iterator();

		while(postIterator.hasNext()){
			jElement = postIterator.next();
			postList.add(gson.fromJson(jElement, Post.class));
		}
		
	}
	
	/**
	 * Gives the user a list of Post objects containing the posts of this
	 * particular query.
	 * @return An ArrayList of Post objects containing the posts of this Tumblr
	 * query.*/
	public ArrayList<Post> getPosts(){
		return postList;
	}
	
	/**
	 * Joins together two ArrayLists of post objects.
	 * @param otherPostList Another ArrayList of posts from one ore more 
	 * different Tumblr queries, that will be joined with the posts from this 
	 * query.
	 * @return An ArrayList of Post objects containing the posts of this query
	 * and the posts from the otherPostList. 
	 */
	public ArrayList<Post> joinPosts(ArrayList<Post> otherPostList){
		ArrayList<Post> newPostList = new ArrayList<Post>(postList);
		newPostList.addAll(otherPostList);
		return newPostList;
	}
	
}
