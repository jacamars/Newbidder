package com.jacamars.dsp.rtb.shared;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jacamars.dsp.rtb.pojo.BidResponse;
import com.jacamars.dsp.rtb.tools.DbTools;

/**
 * A class that contains the recorded bits of a bid
 * @author Ben M. Faul
 *
 */
@Entity
@Table(name = "misc")
@SuppressWarnings("unused")
public class RecordedMisc implements Serializable {
	
	@Id
	private String id;
	private Object value;
	private Long endtime;

	public Long getEndtime() {
		return endtime;
	}
	
	public void setEndtime(Long time) {
		endtime = time;
	}
	
	public Object getValue() {
		return value;
	}
	
	public String getValueAsString() {
		try {
			return DbTools.mapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	
	public RecordedMisc() {
		
	}
	
	public RecordedMisc(String id, Object value, long endtime) {
		this.id = id;
		this.value = value;
		this.endtime = endtime;
	}

}
