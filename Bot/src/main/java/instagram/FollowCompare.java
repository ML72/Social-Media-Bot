package instagram;


import java.util.ArrayList;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowingRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;

public class FollowCompare {

	public static ArrayList<InstagramUserSummary> followersList = new ArrayList<InstagramUserSummary>();
	public static ArrayList<InstagramUserSummary> followingList = new ArrayList<InstagramUserSummary>();
	private static ArrayList<Long> followingIDs = new ArrayList<Long>();
	private static ArrayList<Long> followerIDs = new ArrayList<Long>();
	public static ArrayList<InstagramUserSummary> ghostFollowers = new ArrayList<InstagramUserSummary>();
	public static ArrayList<InstagramUserSummary> unFollowers = new ArrayList<InstagramUserSummary>();
	public static ArrayList<InstagramUserSummary> mutualFollowers = new ArrayList<InstagramUserSummary>();
	
	
	
	public static void main(String[] args) throws Exception {

		String username = "java.directmessage.bot";
		String password = "newgame1234";
		String targetUsername = "hansennansen";
		
		
		
		// setup
		Instagram4j instagram = login(username, password);
		
		// obtain target
		InstagramSearchUsernameResult target = instagram.sendRequest(new InstagramSearchUsernameRequest(targetUsername));
		
		//initializeFollowerFollowingRepeatedly(username, password, target, 5);
		initializeFollowerFollowingRepeatedly(instagram, target, 5);
		initializeGhostUnfollowersLists(instagram);
		
		
		// display data :)
		System.err.println(followingList.size() + " being followed by target user");
		System.err.println(followersList.size() + " following target user");
				

		printList(ghostFollowers, "ghost followers (list below): ");
		
		Thread.sleep(1000);
		
		printList(unFollowers, "unfollowers (list below): ");
		
		Thread.sleep(1000);
		
		printList(mutualFollowers, "mutual followers (list below): ");
		
	}
	

	public static void printList(ArrayList<InstagramUserSummary> toPrint, String header) throws Exception {
		System.err.println(header + toPrint.size());
		Thread.sleep(500);
		for(InstagramUserSummary user : toPrint) {
			System.out.println(user.getUsername() + "  -  " + user.getFull_name() + "  -  " + user.getPk());
		}
	}
	
	public static Instagram4j login(String username, String password) throws Exception {
		Instagram4j instagram = Instagram4j.builder().username(username).password(password).build();
		instagram.setup();
		instagram.login();
		return instagram;
	}
	
	public static void initializeFollowerFollowingRepeatedly(Instagram4j instagram, InstagramSearchUsernameResult target, int repetitions) throws Exception {
		
		if(repetitions < 1) throw new IllegalArgumentException("Can't run below 1 repetitions!");
		
		// run repetition 1
		initializeFollowerFollowingLists(instagram, target);
		ArrayList<InstagramUserSummary> followersBaselist = (ArrayList<InstagramUserSummary>) followersList.clone();
		ArrayList<InstagramUserSummary> followingBaselist = (ArrayList<InstagramUserSummary>) followingList.clone();
		ArrayList<Long> followerIDsBaselist = (ArrayList<Long>) followerIDs.clone();
		ArrayList<Long> followingIDsBaselist = (ArrayList<Long>) followingIDs.clone();
		System.err.println("Finished 1 run of initialization");
		
		for(int i = 2; i <= repetitions; i++) {
			
			// get new data
			initializeFollowerFollowingLists(instagram, target);
			
			// merge lists
			for(Long ID : followingIDsBaselist) {
				if(!followingIDs.contains(ID)) {
					followingList.add(followingBaselist.get(followingIDsBaselist.indexOf(ID)));
					followingIDs.add(ID);
					System.err.println("Merged " + followingBaselist.get(followingIDsBaselist.indexOf(ID)).getUsername());
				}
			}
			for(Long ID : followerIDsBaselist) {
				if(!followerIDs.contains(ID)) {
					followersList.add(followersBaselist.get(followerIDsBaselist.indexOf(ID)));
					followerIDs.add(ID);
					System.err.println("Merged " + followersBaselist.get(followerIDsBaselist.indexOf(ID)).getUsername());
				}
			}
			
			// re-clone lists
			followersBaselist = (ArrayList<InstagramUserSummary>) followersList.clone();
			followingBaselist = (ArrayList<InstagramUserSummary>) followingList.clone();
			followerIDsBaselist = (ArrayList<Long>) followerIDs.clone();
			followingIDsBaselist = (ArrayList<Long>) followingIDs.clone();
			
			System.err.println("Finished " + i + " runs of initialization");
		}
		
		removeDuplicatesFollowerFollowing();
		
	}
	
	public static void initializeFollowerFollowingLists(Instagram4j instagram, InstagramSearchUsernameResult target) throws Exception {
		
		// clear data
		followersList.clear();
		followingList.clear();
		followingIDs.clear();
		followerIDs.clear();
		ghostFollowers.clear();
		unFollowers.clear();
		mutualFollowers.clear();
		
		// MY FOLLOWER COUNT
		int followerCount = target.getUser().getFollower_count();
		
		
		// get followers - people who follow target
		String nextMaxId = null;
		while (true) {
			System.err.println(nextMaxId + " is nextmaxid for followers");
			InstagramGetUserFollowersResult followers = instagram.sendRequest(new InstagramGetUserFollowersRequest(target.getUser().getPk(), nextMaxId));			
			followersList.addAll(followers.getUsers());
			nextMaxId = followers.getNext_max_id();
			if (nextMaxId == null) {
				break;
			}
		}
		// get following - people who target follows
		nextMaxId = null;
		while (true) {
			System.err.println(nextMaxId + " is nextmaxid for following");
			InstagramGetUserFollowersResult following = instagram.sendRequest(new InstagramGetUserFollowingRequest(target.getUser().getPk(), nextMaxId));
			followingList.addAll(following.getUsers());
			nextMaxId = following.getNext_max_id();
			if (nextMaxId == null) {
				break;
			}
		}
		
		System.out.println("Configured following and follower lists");
		
		// convert follower and following list into ID lists
		for(InstagramUserSummary user : followingList) {
			followingIDs.add(user.getPk());
		}
		for(InstagramUserSummary user : followersList) {
			followerIDs.add(user.getPk());
		}
		
		System.out.println("IDs configured for following and follower lists");
		Thread.sleep(500);
	}
	
	
	public static void initializeGhostUnfollowersLists(Instagram4j instagram) throws Exception {
		
		// mutual followers
		for(Long ID : followerIDs) {
			if(followingIDs.contains(ID)) {
				mutualFollowers.add(followersList.get(followerIDs.indexOf(ID)));
			}
		}
		ArrayList<Long> mutualIDs = new ArrayList<Long>();
		for(InstagramUserSummary user : mutualFollowers) {
			mutualIDs.add(user.getPk());
		}
		
		System.out.println("Configured mutual follower list");
		Thread.sleep(1000);
		
		// following target but not followed by target (ghost followers)
		for(Long ID : followerIDs) {
			if(!mutualIDs.contains(ID)) {
				ghostFollowers.add(followersList.get(followerIDs.indexOf(ID)));
			}
		}
		// followed by target but not following target (un followers)
		for(Long ID : followingIDs) {
			if(!mutualIDs.contains(ID)) {
				unFollowers.add(followingList.get(followingIDs.indexOf(ID)));
			}
		}
		
		System.out.println("Configured ghost and un follower lists");
		Thread.sleep(1000);
	}

	public static void removeDuplicatesFollowerFollowing() {

		// get new lists for following
		ArrayList<Long> newFollowingIDs = new ArrayList<Long>();
		ArrayList<InstagramUserSummary> newFollowingList = new ArrayList<InstagramUserSummary>();
		for (Long ID : followingIDs) {
			if (!newFollowingIDs.contains(ID)) {
				newFollowingIDs.add(ID);
				newFollowingList.add(followingList.get(followingIDs.indexOf(ID)));
			} else {
				System.err.println("Duplicate of " + followingList.get(followingIDs.indexOf(ID)).getUsername());
			}
		}
		
		// get new lists for followers
		ArrayList<Long> newFollowerIDs = new ArrayList<Long>();
		ArrayList<InstagramUserSummary> newFollowersList = new ArrayList<InstagramUserSummary>();
		for (Long ID : followerIDs) {
			if (!newFollowerIDs.contains(ID)) {
				newFollowerIDs.add(ID);
				newFollowersList.add(followersList.get(followerIDs.indexOf(ID)));
			} else {
				System.err.println("Duplicate of " + followersList.get(followerIDs.indexOf(ID)).getUsername());
			}
		}
		
		// put back into official lists
		followingIDs = (ArrayList<Long>) newFollowingIDs.clone();
		followingList = (ArrayList<InstagramUserSummary>) newFollowingList.clone();
		followerIDs = (ArrayList<Long>) newFollowerIDs.clone();
		followersList = (ArrayList<InstagramUserSummary>) newFollowersList.clone();
	}
}
