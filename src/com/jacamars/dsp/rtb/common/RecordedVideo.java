package com.jacamars.dsp.rtb.common;

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
@Table(name = "video")
@SuppressWarnings("unused")
public class RecordedVideo implements Serializable {
	
	@Id
	private String id;
	private String name;
	private Long endtime;

	public Long getEndtime() {
		return endtime;
	}
	
	public void setEndtime(Long time) {
		endtime = time;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	
	public RecordedVideo() {
		
	}
	
	public RecordedVideo(String id, String name, long endtime) {
		this.id = id;
		this.name = name;
		this.endtime = endtime;
	}

}
