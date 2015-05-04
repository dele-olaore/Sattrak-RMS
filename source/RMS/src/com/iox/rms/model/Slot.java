package com.iox.rms.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
public class Slot implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
    @GeneratedValue
	private Long id;
	@Column(unique=true)
	private String name;
	
	private int amount_of_hours;
	private int start_hour, end_hour;
	
	private boolean sysObject;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	public Slot()
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

	public int getAmount_of_hours() {
		return amount_of_hours;
	}

	public void setAmount_of_hours(int amount_of_hours) {
		this.amount_of_hours = amount_of_hours;
	}

	public int getStart_hour() {
		return start_hour;
	}

	public void setStart_hour(int start_hour) {
		this.start_hour = start_hour;
	}

	public int getEnd_hour() {
		return end_hour;
	}

	public void setEnd_hour(int end_hour) {
		this.end_hour = end_hour;
	}

	public boolean isSysObject() {
		return sysObject;
	}

	public void setSysObject(boolean sysObject) {
		this.sysObject = sysObject;
	}

	public Date getCrt_dt() {
		return crt_dt;
	}

	public void setCrt_dt(Date crt_dt) {
		this.crt_dt = crt_dt;
	}
	
}
