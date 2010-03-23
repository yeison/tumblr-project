package tumblrinterface;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

public class TumblrQuery {

	private URL url;
	private String urlString;
	
	
	public TumblrQuery(){
		setUrl("http://newsweek.tumblr.com/api/read/json");
	}
	
	/**
	 * @param getParameters A linked list containing the GET parameters defined at 
	 * http://www.tumblr.com/docs/en/api, and the values for the parameters.  The list
	 * should start with a GET parameter and alternate between parameter and value.*/
	public TumblrQuery(LinkedList<String> getParameters){
		this("newsweek");
		appendToUrlString(getParameters);
		setUrl(urlString);
	}
	
	/**
	 * @param subdomain The subdomain of a tumblr url. Such as "newsweek" in the case
	 * of http://newsweek.tumblr.com.*/
	public TumblrQuery(String subdomain){
		setUrlString("http://" + subdomain + ".tumblr.com/api/read/json");
		setUrl(urlString);
	}

	/**
	 * @param subdomain The subdomain of a tumblr url. Such as "newsweek" in the case
	 * of http://newsweek.tumblr.com.
	 * @param getParameters A linked list containing the GET parameters defined at 
	 * {@link http://www.tumblr.com/docs/en/api}, and the values for the parameters.  The list
	 * should start with a GET parameter and alternate between parameter and value.*/
	public TumblrQuery(String subdomain, LinkedList<String> getParameters){
		this(subdomain);
		appendToUrlString(getParameters);
		setUrl(urlString);
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
		this.urlString = urlString;
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
	
	/**@return The JSON located at the url*/
	public String getJson(){
		InputStream in = null;
		try {
			in = this.url.openStream();
		}catch (FileNotFoundException e){
			e.printStackTrace();
			System.err.println("Please verify that the subdomain or url provided exists.");
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Please verify that the subdomain or url provided exists.");
		} 
		Reader reader = new InputStreamReader(in);
		BufferedReader bufferedReader = new BufferedReader(reader);
		String jsonString = null;
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
		
		return jsonString;
	}
	
}
