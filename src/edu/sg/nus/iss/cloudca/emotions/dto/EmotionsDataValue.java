package edu.sg.nus.iss.cloudca.emotions.dto;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

/**
 * Responsible to hold the data necessary for Map reduce Value
 * @author Ajith Kumar
 *
 */
public class EmotionsDataValue implements WritableComparable<EmotionsDataValue>{
	private Text feedData;
	private LongWritable indicoValue;
	private Text happinessIndexText;
	
	public EmotionsDataValue() {
		// TODO Auto-generated constructor stub
	}
	
	public EmotionsDataValue(String feed, Long indicoVal, String indexText) {
		this.feedData = new Text(feed);
		this.indicoValue = new LongWritable(indicoVal);
		this.happinessIndexText = new Text(indexText);
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

	public LongWritable getIndicoValue() {
		return indicoValue;
	}

	public void setIndicoValue(LongWritable indicoValue) {
		this.indicoValue = indicoValue;
	}

	public Text getHappinessIndexText() {
		return happinessIndexText;
	}

	public void setHappinessIndexText(Text happinessIndexText) {
		this.happinessIndexText = happinessIndexText;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((feedData == null) ? 0 : feedData.hashCode());
		result = prime
				* result
				+ ((happinessIndexText == null) ? 0 : happinessIndexText
						.hashCode());
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
		if (feedData == null) {
			if (other.feedData != null)
				return false;
		} else if (!feedData.equals(other.feedData))
			return false;
		if (happinessIndexText == null) {
			if (other.happinessIndexText != null)
				return false;
		} else if (!happinessIndexText.equals(other.happinessIndexText))
			return false;
		if (indicoValue == null) {
			if (other.indicoValue != null)
				return false;
		} else if (!indicoValue.equals(other.indicoValue))
			return false;
		return true;
	}

}
