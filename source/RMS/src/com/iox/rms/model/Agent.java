package com.iox.rms.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
public class Agent implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
    @GeneratedValue
	private Long id;
	
	@ManyToOne
	private InstallerLocation company;
	private String firstname;
	private String lastname;
	
	private byte[] photo;

	@ManyToOne
	private Rating rating;
	
	@ManyToOne
	private User createdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	private boolean active = true;
	
	public Agent()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InstallerLocation getCompany() {
		return company;
	}

	public void setCompany(InstallerLocation company) {
		this.company = company;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
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
	
}
