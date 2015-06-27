package edu.sg.nus.iss.cloudca.emotions.dto;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class PoliticalDataKey implements WritableComparable<PoliticalDataKey>{
	
	private String politicalViewKey = "";
	
	public PoliticalDataKey() {
		this("");
	}
	
	public PoliticalDataKey(String key) {
		this.politicalViewKey = key;
	}
	

	public void write(DataOutput out) throws IOException {
		out.writeUTF(politicalViewKey);
		
	}

	public void readFields(DataInput in) throws IOException {
		this.politicalViewKey = in.readUTF();
		
	}

	public int compareTo(PoliticalDataKey o) {
		return this.politicalViewKey.compareTo(o.getPoliticalViewKey());
	}
	
	public String getPoliticalViewKey() {
		return politicalViewKey;
	}

	public void setPoliticalViewKey(String politicalViewKey) {
		this.politicalViewKey = politicalViewKey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((politicalViewKey == null) ? 0 : politicalViewKey.hashCode());
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
		PoliticalDataKey other = (PoliticalDataKey) obj;
		if (politicalViewKey == null) {
			if (other.politicalViewKey != null)
				return false;
		} else if (!politicalViewKey.equals(other.politicalViewKey))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PoliticalDataKey [politicalViewKey=" + politicalViewKey + "]";
	}
	
}
