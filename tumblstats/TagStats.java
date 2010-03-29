package tumblstats;

import java.sql.SQLException;
import java.util.HashMap;

import tumblib.Post;

/**
 * Defines the setData method that accumulates statistics on tags.  In
 * particular, the number of times each tag is assigned to a post.
 * @author Yeison Rodriguez
 *
 */
public class TagStats extends Stats{
	
	TagStats(Post[] postArray){
		super(postArray);
		
	}

	@Override
	protected void setData() {
		try{
			//The hashmap will be able to store the occurrence of each tag,
			//where the tag is the key, and frequency is the value.
			HashMap<String, Integer> tagMap = new HashMap<String, Integer>();
			for(int i = 0; i < postArray.length; i++){
				String[] tags = postArray[i].getTags();
				if(tags != null)
					//Accumulate the occurrence of each tag.
					for(int j = 0; j < tags.length; j++){
						if(tagMap.containsKey(tags[j]))
							tagMap.put(tags[j], tagMap.get(tags[j]) + 1);
						else
							tagMap.put(tags[j], 1);
						
						//Prepare a batch to be executed for all tags after
						//exiting the for-loop.
						prep.setString(1, tags[j]);
						prep.setInt(2, tagMap.get(tags[j]));
						prep.addBatch();			

					}
			}

			prep.executeBatch();
		}catch(SQLException e){
			e.printStackTrace();
		}


	}



}
