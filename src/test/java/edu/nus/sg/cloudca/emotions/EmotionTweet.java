package edu.nus.sg.cloudca.emotions;

import java.util.Arrays;

/**
 * 
 * @author Jagadeesh Potturi
 */
public class EmotionTweet {
	private String product;
	private String tweet;
	private int likesCount;
	private String user;
	private String[] hashtags;
	private String geolocation;
	private String celebrity;
	private long date;
	
	public EmotionTweet() {
		// TODO Auto-generated constructor stub
	}

	public EmotionTweet(String product, String tweet, int likesCount,
			String user, String[] hashtags, String geolocation,
			String celebrity, long date) {
		this.product = product;
		this.tweet = tweet;
		this.likesCount = likesCount;
		this.user = user;
		this.hashtags = hashtags;
		this.geolocation = geolocation;
		this.celebrity = celebrity;
		this.date = date;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public int getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(int likesCount) {
		this.likesCount = likesCount;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String[] getHashtags() {
		return hashtags;
	}

	public void setHashtags(String[] hashtags) {
		this.hashtags = hashtags;
	}

	public String getGeolocation() {
		return geolocation;
	}

	public void setGeolocation(String geolocation) {
		this.geolocation = geolocation;
	}

	public String getCelebrity() {
		return celebrity;
	}

	public void setCelebrity(String celebrity) {
		this.celebrity = celebrity;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "EmotionTweet [product=" + product + ", tweet=" + tweet
				+ ", likesCount=" + likesCount + ", user=" + user
				+ ", hashtags=" + Arrays.toString(hashtags) + ", geolocation="
				+ geolocation + ", celebrity=" + celebrity + ", date=" + date
				+ "]";
	}
	
}
