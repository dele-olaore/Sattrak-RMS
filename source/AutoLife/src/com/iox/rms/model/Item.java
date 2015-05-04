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
public class Item implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
    @GeneratedValue
	private Long id;
	private String name;
	private String model;
	private String description;
	@ManyToOne
	private ItemType type;
	@ManyToOne
	private ItemManufacturer manufacturer;
	
	private long stocklevel;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	public Item()
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

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ItemType getType() {
		return type;
	}

	public void setType(ItemType type) {
		this.type = type;
	}

	public ItemManufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(ItemManufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	public long getStocklevel() {
		return stocklevel;
	}

	public void setStocklevel(long stocklevel) {
		this.stocklevel = stocklevel;
	}

	public Date getCrt_dt() {
		return crt_dt;
	}

	public void setCrt_dt(Date crt_dt) {
		this.crt_dt = crt_dt;
	}
	
}
