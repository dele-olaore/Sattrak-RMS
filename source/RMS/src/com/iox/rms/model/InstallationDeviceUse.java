package com.iox.rms.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
public class InstallationDeviceUse implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
    @GeneratedValue
	private Long id;
	
	@ManyToOne
	private InstallerLocationJobSchedule installation;
	@ManyToOne
	private Item item;
	private long count;
	private String serial_numbers;
	
	@ManyToOne
	private User createdBy;
	@Temporal(TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	@Transient
	private long item_id;
	@Transient
	private int item_count;
	
	public InstallationDeviceUse()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InstallerLocationJobSchedule getInstallation() {
		return installation;
	}

	public void setInstallation(InstallerLocationJobSchedule installation) {
		this.installation = installation;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getSerial_numbers() {
		return serial_numbers;
	}

	public void setSerial_numbers(String serial_numbers) {
		this.serial_numbers = serial_numbers;
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

	public long getItem_id() {
		return item_id;
	}

	public void setItem_id(long item_id) {
		this.item_id = item_id;
	}

	public int getItem_count() {
		return item_count;
	}

	public void setItem_count(int item_count) {
		this.item_count = item_count;
	}
	
}
