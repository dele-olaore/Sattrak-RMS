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
public class ProductTypeCommission implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
    @GeneratedValue
	private Long id;
	
	@ManyToOne
	private ProductType productType;
	private double salesAgentCommission, installerCommission, sattrakRunningCostCommission;
	
	private boolean active;
	@ManyToOne
	private Partner partner;
	
	@ManyToOne
	private User createdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	public ProductTypeCommission()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public double getSalesAgentCommission() {
		return salesAgentCommission;
	}

	public void setSalesAgentCommission(double salesAgentCommission) {
		this.salesAgentCommission = salesAgentCommission;
	}

	public double getInstallerCommission() {
		return installerCommission;
	}

	public void setInstallerCommission(double installerCommission) {
		this.installerCommission = installerCommission;
	}

	public double getSattrakRunningCostCommission() {
		return sattrakRunningCostCommission;
	}

	public void setSattrakRunningCostCommission(double sattrakRunningCostCommission) {
		this.sattrakRunningCostCommission = sattrakRunningCostCommission;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
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
	
}
