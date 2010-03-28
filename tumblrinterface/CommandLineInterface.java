/**@author Yeison Rodriguez**/
package tumblrinterface;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.cli.*;

import tumblib.Post;
import tumblstats.PostStatistics;

/**
 * The CommandLine interface class contains the main method, and parses
 * the user's input.  It utilizes Apache Common's CLI library to define the
 * options on the command line that should be considered.  In addition,
 * the user may pass a tumblr.com subdomain, or the URL of a tumblr blog to
 * query for posts.  If no arguments are given at the command line, the program
 * will print a usage message.   
 * 
 * @author Yeison Rodriguez
 */
public class CommandLineInterface {


	public static void main(String[] args){
		Options options = new Options();
		BasicParser parser = new BasicParser();

		/* The second parameter is a boolean that specifies whether the option 
		 * requires an argument or not.*/
		options.addOption("h", "help", false, "Print help for this application");
		options.addOption("st", "start", true, "The post offset to start from. " +
				"The default is 0.");
		options.addOption("n", "num", true, "The number of posts to return. " +
				"The default is 20, and the maximum is 50.");
		options.addOption("t", "type", true, "The type of posts to return. If " +
				"unspecified or empty, all types of posts are returned. Must be" +
				" one of text, quote, photo, link, chat, video, or audio.");
		options.addOption("i", "id", true, "A specific post ID to return. Use " +
				"instead of start, num, or type.");
		options.addOption("f", "filter", true, "Alternate filter to run on the " +
				"text content. Allowed values:" +
				"\n\t* text - Plain text only. No HTML."+
				"\n\t* none - No post-processing. Output exactly what the " +
				"author entered. (Note: Some authors write in Markdown, which " +
				"will not be converted to HTML when this option is used.)");
		options.addOption("tag", "tagged", true, "Return posts with this tag " +
				"in reverse-chronological order (newest first). Optionally " +
				"specify chrono=1 to sort in chronological order (oldest first).");
		options.addOption("s", "search", true, "Search for posts with this query.");
		options.addOption("u", "url", true, "The url of a tumblr blog. Useful " +
				"if the blog is not a tumblr subdomain.  Cannot be used in " +
				"conjunction with subdomains.  Num must be less than or equal " +
				"to 50");


		//Create a commandline object.  We can parse input from this object.
		CommandLine cl = null;
		try {
			//Pass the options we will consider.
			cl = parser.parse(options, args);
		}catch(MissingArgumentException e){
			printHelp(cl, options);
			System.exit(1);
		}catch(ParseException e) {
			e.printStackTrace();
		}

		//If no arguments, print help/usage
		if(args.length == 0){
			printHelp(cl, options);
			System.exit(0);
		}

		ArrayList<Post> postList = new ArrayList<Post>();
		try{
			if(cl.hasOption("h") || cl.hasOption("help")){
				printHelp(cl, options);
				System.exit(0);
			}
			else{
				//The cl.iterator returns an iterator over possible options.
				Iterator<Option> optionIterator = cl.iterator();
				//Linked list will consist of options and arguments.
				LinkedList<String> optionValues = new LinkedList<String>();
				URL url = null;
				//Transfer the option arguments to the linked list.
				if(cl.hasOption("u") || cl.hasOption("url")){
					try {
						url = new URL(cl.getOptionValue("url"));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
				//start is the post number to begin with, 0 being the newest.
				int start = 0;
				if(cl.hasOption("st") || cl.hasOption("start"))
					start = new Integer(cl.getOptionValue("start"));
				//num is the number of posts to retrieve from tumblr.
				int num = 0;
				if(cl.hasOption ("n") || cl.hasOption("num"))
					num = new Integer(cl.getOptionValue("num"));
				while(optionIterator.hasNext()){
					Option option = optionIterator.next();
					String argument = option.getValue();
					String optString = option.getLongOpt();
					if(argument != null && optString != "url" && 
							num <=50 && optString != "start"){
						//Linked list will alternate between option and arg.
						optionValues.add(optString);
						optionValues.add(argument);
					}
				}
				

				String[] subdomains = {};
				if(url == null)
					subdomains = cl.getArgs();
				for(int i = 0; i < subdomains.length; i++){
					postList.addAll(queryRange(start, num, optionValues, subdomains[i]));
				}
				if(subdomains.length == 0){
					if(url != null)
						postList = new TumblrQuery(url, optionValues).getPosts();
					else
						postList.addAll(queryRange(start, num, optionValues, "newsweek"));
				}
				
			}
		}catch(NullPointerException e){
			e.printStackTrace();
			printHelp(cl, options);
			System.exit(1);
		}

		new PostStatistics(postList);

	}

	//This method prints the usage info for the program.
	static void printHelp(CommandLine cl, Options options){
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("tumblib [OPTION <ARGUMENT>]... [TUMBLR SUB-DOMAINS]..." +
				"\nThe subdomains may be that of any existing tumblr blog " +
				"located at http://<sub-domain>.tumblr.com.  If none are " +
				"provided, newsweek is used by default.", options);
	}
	
	static ArrayList<Post> queryRange(int start, int num, LinkedList<String>optionValues, String subdomain){
		TumblrQuery tQuery;
		ArrayList<Post> postList = new ArrayList<Post>(); 
		LinkedList<String> tempOptionValues = new LinkedList<String>();
		//Tumblr only allows a query on 50 posts at a time.  Lets change
		//that.
		for(int i = 0 ; i < num/50; i++){
			tempOptionValues.add("num");
			tempOptionValues.add("50");
			//Shift the start of the range to be queried by 50.
			int rangeStart = i*50 + start;
			tempOptionValues.add("start");
			tempOptionValues.add(new Integer(rangeStart).toString());
			tempOptionValues.addAll(optionValues);
			tQuery = new TumblrQuery(subdomain, tempOptionValues);
			//Join the new range with the previous range.
			postList = tQuery.joinPosts(postList);
			//Remove the last rangeStart used.
			
		}
		if(num > 50){
			int remainder = num%50;
			int quotient = num/50;
			tempOptionValues.add("num");
			tempOptionValues.add(new Integer(remainder).toString());
			tempOptionValues.add("start");
			tempOptionValues.add(new Integer(quotient*50).toString());
			tempOptionValues.addAll(optionValues);
			postList = 
				new TumblrQuery(subdomain, tempOptionValues).joinPosts(postList);
		}
		else
			return new TumblrQuery(subdomain, tempOptionValues).getPosts();
		
		return postList;
	}

}	
