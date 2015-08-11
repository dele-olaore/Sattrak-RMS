package com.iox.rms.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
public class Partner implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
    @GeneratedValue
	private Long id;
	private String name;
	
	private String contact_firstname, contact_lastname, contact_phoneno, contact_email;
	
	private boolean active = true;
	private boolean sattrak;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	public Partner()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContact_firstname() {
		return contact_firstname;
	}

	public void setContact_firstname(String contact_firstname) {
		this.contact_firstname = contact_firstname;
	}

	public String getContact_lastname() {
		return contact_lastname;
	}

	public void setContact_lastname(String contact_lastname) {
		this.contact_lastname = contact_lastname;
	}

	public String getContact_phoneno() {
		return contact_phoneno;
	}

	public void setContact_phoneno(String contact_phoneno) {
		this.contact_phoneno = contact_phoneno;
	}

	public String getContact_email() {
		return contact_email;
	}

	public void setContact_email(String contact_email) {
		this.contact_email = contact_email;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isSattrak() {
		return sattrak;
	}

	public void setSattrak(boolean sattrak) {
		this.sattrak = sattrak;
	}

	public Date getCrt_dt() {
		return crt_dt;
	}

	public void setCrt_dt(Date crt_dt) {
		this.crt_dt = crt_dt;
	}
	
}
