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
 * 
 * @author Ben M. Faul
 *
 */
@Entity
@Table(name = "recordedbids")
@SuppressWarnings("unused")
public class RecordedBid implements Serializable {

	@Id
	private String id; // the bid request id, also used as the key in the mapstore db.

	private String capKey;
	private String adType; // the ad type
	private Long capTimeout; // the length of this cap in the database. (Not the frequency cap timeout)
	private Long endtime; // the actual time of eviction (Not the time of eviction of the frequency cap

	private String capTimeUnit; // not used
	private String price; // string representation of the bid price.
	private List<FrequencyCap> frequencyCap; // the frequency cap key in the database.

	public Long getEndtime() {
		return endtime;
	}

	public void setEndtime(Long time) {
		endtime = time;
	}

	public String getCapKey() {
		return capKey;
	}

	public void setCapKey(String capKey) {
		this.capKey = capKey;
	}

	public String getAdType() {
		return adType;
	}

	public void setAdType(String adType) {
		this.adType = adType;
	}

	public Long getCapTimeout() {
		return capTimeout;
	}

	public void setCapTimeout(long capTimeout) {
		this.capTimeout = capTimeout;
	}

	public String getCapTimeUnit() {
		return capTimeUnit;
	}

	public void setCapTimeUnit(String capTimeUnit) {
		this.capTimeUnit = capTimeUnit;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<FrequencyCap> getFrequencyCap() {
		return frequencyCap;
	}

	public void setFrequencyCap(List<FrequencyCap> frequencyCap) {
		this.frequencyCap = frequencyCap;
	}

	public String getFrequencyCapAsString() {
		if (frequencyCap == null)
			return "";
		try {
			return DbTools.mapper.writeValueAsString(frequencyCap);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public RecordedBid() {

	}

	public RecordedBid(String id, String capKey, long capTimeout, String capTimeUnit, String price, String adType,
			List<FrequencyCap> frequencyCap, long endtime) {
		this.id = id;
		this.capKey = capKey;
		this.capTimeout = capTimeout;
		this.capTimeUnit = capTimeUnit;
		this.price = price;
		this.adType = adType;

		if (frequencyCap != null) {
			for (int i = 0; i < frequencyCap.size(); i++) {
				try {
					System.out.println("FQ: " + DbTools.mapper.writeValueAsString(frequencyCap.get(i)));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.frequencyCap = frequencyCap;
		}
		this.endtime = endtime;
	}

	public RecordedBid(BidResponse br) {
		price = Double.toString(br.cost);
		adType = br.adtype;
		id = br.oidStr;
		frequencyCap = br.frequencyCap;
	}

}
