package edu.sg.nus.iss.cloudca.emotions.dto;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class PoliticalDataValue implements WritableComparable<PoliticalDataValue>{
	
	private String feedData = "";
	private String userName = "";
	private Integer likeCount = 0;
	private String date = "";
	private Double politicalValue = 0d;
	private String politicalString = "";
	
	public PoliticalDataValue() {
		this("","",0,"");
	}

	public PoliticalDataValue(String feed, String name, int likes, String date) {
		this.feedData = feed;
		this.userName = name;
		this.likeCount = likes;
		this.date = date;
	}
	
	public PoliticalDataValue(PoliticalDataValue value){
		this(value.getFeedData(), value.getUserName(), value.getLikeCount(), value.getDate());
		this.politicalString = value.getPoliticalString();
		this.politicalValue = value.getPoliticalValue();
	}
	
	public void write(DataOutput out) throws IOException {
		out.writeUTF(feedData);
		out.writeUTF(userName);
		out.writeInt(likeCount);
		out.writeUTF(date);
		out.writeDouble(politicalValue);
		out.writeUTF(politicalString);
	}
	
	public void readFields(DataInput in) throws IOException {
		this.feedData = in.readUTF();
		this.userName = in.readUTF();
		this.likeCount = in.readInt();
		this.date = in.readUTF();
		this.politicalValue = in.readDouble();
		this.politicalString = in.readUTF();
	}

	public int compareTo(PoliticalDataValue o) {
		return this.politicalValue.compareTo(o.getPoliticalValue());
	}
	
	public String getFeedData() {
		return feedData;
	}

	public void setFeedData(String feedData) {
		this.feedData = feedData;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Double getPoliticalValue() {
		return politicalValue;
	}

	public void setPoliticalValue(Double politicalValue) {
		this.politicalValue = politicalValue;
	}

	public String getPoliticalString() {
		return politicalString;
	}

	public void setPoliticalString(String politicalString) {
		this.politicalString = politicalString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((politicalValue == null) ? 0 : politicalValue.hashCode());
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
		PoliticalDataValue other = (PoliticalDataValue) obj;
		if (politicalValue == null) {
			if (other.politicalValue != null)
				return false;
		} else if (!politicalValue.equals(other.politicalValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PoliticalDataValue [feedData=" + feedData + ", userName="
				+ userName + ", likeCount=" + likeCount + ", date=" + date
				+ ", politicalValue=" + politicalValue + ", politicalString="
				+ politicalString + "]";
	}
	
}
