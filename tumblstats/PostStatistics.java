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


	public PostStatistics(ArrayList<Post> postList){
		Post[] postArray = {};
		postArray = postList.toArray(postArray);

		System.out.println("Total posts retrieved: " + Post.totalCount);
		System.out.println("Printing stats...");
		new TypeStats(postArray).createTable("types", "type");
		System.out.println();
		new TimeStats(postArray).createTable("hour", "hour");
		System.out.println();
	}

}