package instagram;

import java.net.InetAddress;
import java.util.ArrayList;

import org.apache.http.HttpHost;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramDirectShareRequest;
import org.brunocvcunha.instagram4j.requests.InstagramDirectShareRequest.ShareType;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;

public class FollowerSpammer {

	/**
	 * Sends customized messages to followers of a target account
	 * 
	 * @param args
	 * @throws Exception
	 */
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		
		// sign into sending account; edit username and password as needed
		Instagram4j instagram = Instagram4j.builder().username("java.directmessage.testbot").password("newgame1234").build();
		instagram.setup();
		instagram.login();

		// get target account
		InstagramSearchUsernameResult target = instagram.sendRequest(new InstagramSearchUsernameRequest("java.directmessage.testbot"));

		// get list of followers of target with pagination
		ArrayList<InstagramUserSummary> followersList = new ArrayList<InstagramUserSummary>();
		String nextMaxId = null;
		while (true) {
			InstagramGetUserFollowersResult followers = instagram.sendRequest(new InstagramGetUserFollowersRequest(target.getUser().getPk(), nextMaxId));
			followersList.addAll(followers.getUsers());
			nextMaxId = followers.getNext_max_id();
			if (nextMaxId == null) {
				break;
			}
		}

		// loop through followers
		ArrayList recipients = new ArrayList();
		for (InstagramUserSummary user : followersList) {

			// add each individual follower
			recipients.clear();
			recipients.add("" + user.getPk());
			
			// send message
			instagram.sendRequest(InstagramDirectShareRequest.builder().shareType(ShareType.MESSAGE).recipients(recipients).message("" + user.getFull_name() + ", you're awesome and Happy 2020!").build());
			System.err.println("Sent message to " + user.getUsername() + " - user " + (followersList.indexOf(user) + 1) + "/" + followersList.size());
		
			// delay between sends
			Thread.sleep(2000);
		}

		System.out.println("Done :)");
	}
}
