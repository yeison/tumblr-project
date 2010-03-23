/**@author Yeison Rodriguez**/
package tumblrinterface;

import java.util.LinkedList;
import java.util.Iterator;

import org.apache.commons.cli.*;

import tumblstats.PostStatistics;

public class CommandLineInterface {


	public static void main(String[] args){
		Options options = new Options();
		BasicParser parser = new BasicParser();

		/* The second parameter is a boolean that specifies whether the option 
		 * requires an argument or not.*/
		options.addOption("h", "help", false, "Print help for this application");
		options.addOption("s", "start", true, "The post offset to start from. The default is 0.");
		options.addOption("n", "num", true, "The number of posts to return. The default is 20, " +
		"and the maximum is 50.");
		options.addOption("t", "type", true, "The type of posts to return. If unspecified or " +
				"empty, all types of posts are returned. Must be one of text, quote, photo, " +
		"link, chat, video, or audio.");
		options.addOption("i", "id", true, "A specific post ID to return. Use instead of start, " +
		"num, or type.");
		options.addOption("f", "filter", true, "Alternate filter to run on the text content. Allowed values:" +
				"\n\t* text - Plain text only. No HTML."+
				"\n\t* none - No post-processing. Output exactly what the author entered. (Note: Some " +
		"authors write in Markdown, which will not be converted to HTML when this option is used.)");
		options.addOption("tag", "tagged", true, "Return posts with this tag in reverse-chronological order " +
		"(newest first). Optionally specify chrono=1 to sort in chronological order (oldest first).");
		options.addOption("s", "search", true, "Search for posts with this query.");
		options.addOption("u", "url", true, "The url of a tumblr blog.  Useful if the blog is not a " +
				"tumblr subdomain.");
		

		CommandLine cl = null;
		try {
			cl = parser.parse(options, args);
		}catch(MissingArgumentException e){
			printHelp(cl, options);
			System.exit(1);
		}catch(ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(args.length == 0){
			printHelp(cl, options);
			System.exit(0);
		}

		String tumblrJson = "";
		try{
			if(cl.hasOption("h") || cl.hasOption("help")){
				printHelp(cl, options);
				System.exit(0);
			}
			else{
				//The cl.iterator returns an iterator over possible options.
				Iterator<Option> optionIterator = cl.iterator();
				LinkedList<String> optionValues = new LinkedList<String>();
				//Transfer the option arguments to the linked list.
				while(optionIterator.hasNext()){
					Option option = optionIterator.next();
					String argument = option.getValue();
					String optString = option.getLongOpt();
					if(argument != null){
						//Linked list will consist of options and arguments.
						optionValues.add(optString);
						optionValues.add(argument);
					}
				}
				TumblrQuery tQuery;
				try{
					String subdomain = cl.getArgs()[0];
					tQuery = new TumblrQuery(subdomain, optionValues);
				}catch(ArrayIndexOutOfBoundsException e){
					tQuery = new TumblrQuery(optionValues);
				}
				//Get the json that corresponds to this query.
				tumblrJson = tQuery.getJson();
			}
		}catch(NullPointerException e){
			printHelp(cl, options);
			System.exit(1);
		}
		
		new PostStatistics(tumblrJson);
		
	}

	static void printHelp(CommandLine cl, Options options){
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("tumblib [OPTION <ARGUMENT>]... <TUMBLR SUB-DOMAIN>" +
				"\nThe subdomain may be that of any existing tumblr blog located" +
				"at http://<sub-domain>.tumblr.com.  If none is provided, " +
				"newsweek is used by default.", options);
	}

}	
