package edu.sg.nus.iss.cloudca.emotions.dto;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

/**
 * Responsible to hold the data necessary for Map reduce key
 * @author Ajith Kumar
 */
public class EmotionsDataKey implements WritableComparable<EmotionsDataKey> {
	
	private Text name;
	
	/**
	 * Default Constructor
	 */
	public EmotionsDataKey() {
		this("");
	}
	
	public EmotionsDataKey(String name) {
		this.name = new Text(name);
	}
	
	public void write(DataOutput out) throws IOException {
		name.write(out);

	}

	public void readFields(DataInput in) throws IOException {
		name.readFields(in);
	}

	public int compareTo(EmotionsDataKey o) {
		return name.compareTo(o.getName());
	}

	public Text getName() {
		return name;
	}

	public void setName(Text name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		EmotionsDataKey other = (EmotionsDataKey) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
