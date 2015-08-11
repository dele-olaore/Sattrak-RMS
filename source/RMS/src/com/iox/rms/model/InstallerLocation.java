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
public class InstallerLocation implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
    @GeneratedValue
	private Long id;
	
	@Column(unique=true)
	private String uniqueID;
	
	private String companyName;
	private byte[] photo;
	
	private int maxMultiSlotInstallation;
	
	private String adminFirstname, adminLastname;
	private String phoneNo, email;
	private String address;
	
	private double lat, lng;
	
	@ManyToOne
	private Country country;
	@ManyToOne
	private State state;
	@ManyToOne
	private LGA lga;
	
	@ManyToOne
	private User user;
	
	private String bankName, accountName, accountNo;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	private boolean active = true;
	
	@Transient
	private boolean selected;
	
	public InstallerLocation()
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public int getMaxMultiSlotInstallation() {
		return maxMultiSlotInstallation;
	}

	public void setMaxMultiSlotInstallation(int maxMultiSlotInstallation) {
		this.maxMultiSlotInstallation = maxMultiSlotInstallation;
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

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
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
