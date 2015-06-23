package edu.sg.nus.iss.cloudca.emotions.dto;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

/**
 * Responsible to hold the data necessary for Map reduce Value
 * 
 * @author Ajith Kumar
 * 
 */
public class EmotionsDataValue implements WritableComparable<EmotionsDataValue> {
	private Text feedData;
	private Text userName;
	private IntWritable likeCount;
	private Text geoLocation;
	private Text celebrity;
	private ArrayWritable hashTag = new ArrayWritable(Text.class);

	private DoubleWritable indicoValue;
	private Text happinessIndexText;

	public EmotionsDataValue() {
		this("","",0,"","",new String[]{});
	}

	public EmotionsDataValue(String feed, String userName, int likeCount, String location, String celebrity, String[] hashTag) {
		this.feedData = new Text(feed);
		this.userName = new Text(userName);
		this.likeCount = new IntWritable(likeCount);
		this.geoLocation = new Text(location);
		this.celebrity = new Text(celebrity);
		if(hashTag != null){
			Text[] arr = new Text[hashTag.length];
			for(int i = 0; i < hashTag.length; i++){
				arr[i] = new Text(hashTag[i]);
//				arr[i].set();
			}
			this.hashTag.set(arr);
		}
	}

	public void write(DataOutput out) throws IOException {
		feedData.write(out);
		indicoValue.write(out);
		happinessIndexText.write(out);
	}

	public void readFields(DataInput in) throws IOException {
		feedData.readFields(in);
		indicoValue.readFields(in);
		happinessIndexText.readFields(in);
	}

	public int compareTo(EmotionsDataValue o) {
		return indicoValue.compareTo(o.getIndicoValue());
	}

	public Text getFeedData() {
		return feedData;
	}

	public void setFeedData(Text feedData) {
		this.feedData = feedData;
	}

	public DoubleWritable getIndicoValue() {
		return indicoValue;
	}

	public void setIndicoValue(DoubleWritable indicoValue) {
		this.indicoValue = indicoValue;
	}

	public Text getHappinessIndexText() {
		return happinessIndexText;
	}

	public void setHappinessIndexText(Text happinessIndexText) {
		this.happinessIndexText = happinessIndexText;
	}

	public Text getUserName() {
		return userName;
	}

	public void setUserName(Text userName) {
		this.userName = userName;
	}

	public IntWritable getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(IntWritable likeCount) {
		this.likeCount = likeCount;
	}

	public Text getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(Text geoLocation) {
		this.geoLocation = geoLocation;
	}

	public Text getCelebrity() {
		return celebrity;
	}

	public void setCelebrity(Text celebrity) {
		this.celebrity = celebrity;
	}

	public ArrayWritable getHashTag() {
		return hashTag;
	}

	public void setHashTag(ArrayWritable hashTag) {
		this.hashTag = hashTag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((indicoValue == null) ? 0 : indicoValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmotionsDataValue other = (EmotionsDataValue) obj;
		if (indicoValue == null) {
			if (other.indicoValue != null)
				return false;
		} else if (!indicoValue.equals(other.indicoValue))
			return false;
		return true;
	}

	

}
