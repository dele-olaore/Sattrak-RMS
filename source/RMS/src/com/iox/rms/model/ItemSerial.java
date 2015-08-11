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
import javax.validation.constraints.NotNull;

@Entity
public class ItemSerial implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
    @GeneratedValue
	private Long id;
	@ManyToOne
	private Item item;
	@Column(unique=true)
	private String serialNo;
	private String assignedStatus; // IN-STOCK, DISPOSED, SOLD-TO-CUSTOMER, ASSIGNED-TO-INSTALLER, ASSIGNED-TO-SALESAGENT, ASSIGNED-TO-TRADEPARTNER
	private String status; // WORKING, BAD
	
	@ManyToOne
	private Customer customer; // the customer this item was sold to.
	@ManyToOne
	private InstallerLocation installationPoint; // The installation point this item was supplied to.
	@ManyToOne
	private SalesAgent salesAgent; // The sales agent this item was given to.
	@ManyToOne
	private TradePartner tradePartner; // The trade partner this item was supplied to.
	
	@ManyToOne
	private User user;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updated_dt, crt_dt;
	
	public ItemSerial(){}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getAssignedStatus() {
		return assignedStatus;
	}

	public void setAssignedStatus(String assignedStatus) {
		this.assignedStatus = assignedStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public InstallerLocation getInstallationPoint() {
		return installationPoint;
	}

	public void setInstallationPoint(InstallerLocation installationPoint) {
		this.installationPoint = installationPoint;
	}

	public SalesAgent getSalesAgent() {
		return salesAgent;
	}

	public void setSalesAgent(SalesAgent salesAgent) {
		this.salesAgent = salesAgent;
	}

	public TradePartner getTradePartner() {
		return tradePartner;
	}

	public void setTradePartner(TradePartner tradePartner) {
		this.tradePartner = tradePartner;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getUpdated_dt() {
		return updated_dt;
	}

	public void setUpdated_dt(Date updated_dt) {
		this.updated_dt = updated_dt;
	}

	public Date getCrt_dt() {
		return crt_dt;
	}

	public void setCrt_dt(Date crt_dt) {
		this.crt_dt = crt_dt;
	}
	
}
