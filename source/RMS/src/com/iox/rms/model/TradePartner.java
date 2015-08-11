package com.iox.rms.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
public class TradePartner implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
    @GeneratedValue
	private Long id;
	
	@Column(unique=true)
	private String uniqueID;
	
	@ManyToOne
	private User user;
	
	private String tradePartnerName;
	private String adminFirstname;
	private String adminLastname;
	
	@ManyToOne
	private Country country;
	@ManyToOne
	private State state;
	@ManyToOne
	private LGA lga;
	private String address, phoneNo;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	private boolean active = true;
	
	@Transient
	private boolean selected;
	
	public TradePartner()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTradePartnerName() {
		return tradePartnerName;
	}

	public void setTradePartnerName(String tradePartnerName) {
		this.tradePartnerName = tradePartnerName;
	}

	public String getAdminFirstname() {
		return adminFirstname;
	}

	public void setAdminFirstname(String adminFirstname) {
		this.adminFirstname = adminFirstname;
	}

	public String getAdminLastname() {
		return adminLastname;
	}

	public void setAdminLastname(String adminLastname) {
		this.adminLastname = adminLastname;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public LGA getLga() {
		return lga;
	}

	public void setLga(LGA lga) {
		this.lga = lga;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public Date getCrt_dt() {
		return crt_dt;
	}

	public void setCrt_dt(Date crt_dt) {
		this.crt_dt = crt_dt;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
}
