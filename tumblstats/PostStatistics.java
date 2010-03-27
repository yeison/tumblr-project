package tumblstats;

import java.util.ArrayList;

import tumblib.Post;


public class PostStatistics {


	public PostStatistics(ArrayList<Post> postList){
		Post[] postArray = {};
		postArray = postList.toArray(postArray);

		new TagStats("tags", "tag", postArray).createTable(false);
		System.out.println();
		new TypeStats("types", "type", postArray).createTable(false);
		System.out.println();
		new TimeStats("hour", "hour", postArray).createTable(false);
		System.out.println();
	}

}