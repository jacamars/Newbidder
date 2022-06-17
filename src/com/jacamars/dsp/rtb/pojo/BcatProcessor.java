package com.jacamars.dsp.rtb.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Blocked categories processing
 * @author Ben M. Faul
 *
 */
public class BcatProcessor {

	/**
	 * Create the cat field when cat and blocked fields are present.
	 * @param categories List<String>. List of categories the creative fits.
	 * @param blocked List<String>. List of categories being blocked.
	 * @param response StringBuilder. The JSON string being constructed.
	 */
	public static void process(List<String> categories, List<String> blocked, StringBuilder response) {
		if (blocked != null && (categories != null && categories.size() != 0)) {
			response.append(",\"cat\":[");
			for (int i=0;i<categories.size()-1;i++) {
				response.append("\""+categories.get(i)+"\",");
			}
			response.append("\"" + categories.get(categories.size()-1)+"\"]");
		}  
	}
	
	/**
	 * Given a set of categories, expand it if necessary. IAB1-3 for example turns into IAB1, IAB1-3. This way blocking IAB-1-4 will
	 * not be blocked, but a bcat of IAB1 will block all in that category.
	 * @param categories List<String>. The categories.
	 */
	public static void expandCategories(List<String> categories) {
		if (categories == null)
			return;
		
		List<String> cat = new ArrayList();
		
		categories.forEach(c->{
			int k = c.indexOf("-");
			if (k > -1) {
				String ss = c.substring(0,k);
				if (!cat.contains(ss))
					cat.add(ss);
			}
			if (!cat.contains(c))
				cat.add(c);
			
		});
		categories.clear();
		categories.addAll(cat);

	}
	
	/**
	 * Does the categories list contain a blocked element.
	 * @param categories List<String>. The creative categories.
	 * @param blocked List<String>. The blocked categories.
	 * @return boolean. Returns true if blocked.
	 */
	public static boolean isBlocked(List<String>categories,List<String>blocked) {
		Set<String> result = blocked.stream()
				  .distinct()
				  .filter(categories::contains)
				  .collect(Collectors.toSet());
		if (result.size()>0)
			return true;
		return false;
	}
}
