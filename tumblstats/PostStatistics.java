package tumblstats;

import java.util.ArrayList;

import tumblib.Post;

/**
 * That Post Statistics class calls the functions that accumulate and print 
 * statistics about posts.  It passes the postList to those functions.
 * @author Yeison Rodriguez
 *
 */

public class PostStatistics {
	boolean allStats = false;
	boolean allPosts = false;
	Post[] postArray = {};

	/**
	 * Creates an instance of PostStatistics, which may be used to print
	 * statistical data about posts.
	 * @param postList An ArrayList containg objects of type Post.
	 */
	public PostStatistics(ArrayList<Post> postList){
		postArray = postList.toArray(postArray);		
	}
	
	/**
	 * Prints the content of one post, and different summary statistics.
	 */
	public void printSummary(){
		System.out.println("Total posts retrieved: " + Post.totalCount);
		System.out.println("Printing stats...");
		new TypeStats(postArray).createTable("types", "type");
		System.out.println();
		if(allStats)
			showAllStats();
		System.out.print("Post id: " + postArray[0].getId());
		System.out.println(postArray[0].getContent() + "\n");
		if(allPosts)
			showAllPosts();
	}
	
	/**
	 * Collects and prints tag stats and time stats.
	 * @param postArray The array of posts that will be analyzed.
	 */
	public void showAllStats(){
		new TimeStats(postArray).createTable("hour", "hour");
		System.out.println();
		new TagStats(postArray).createTable("tags", "tags");
		System.out.println();
	}
	
	/**
	 * Prints the post id and the contents of every post in postArray.
	 * @param postArray The array of posts to print the contents of.
	 */
	public void showAllPosts(){
		for(int i = 1; i < postArray.length; i++){
			System.out.print("Post id: " + postArray[i].getId());
			System.out.println(postArray[i].getContent() + "\n");
		}
	}
	
	/**
	 * Sets whether or not all stats should be printed.
	 * @param bool true or false
	 */
	public void setAllStats(boolean bool){
		allStats = bool;
	}
	
	/**
	 * Sets whether or not all posts should be printed.
	 * @param bool true or false
	 */
	public void setAllPosts(boolean bool){
		allPosts = bool;
	}

}