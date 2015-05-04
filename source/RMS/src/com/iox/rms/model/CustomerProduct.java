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
public class CustomerProduct implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
    @GeneratedValue
	private Long id;
	
	@ManyToOne
	private Customer customer;
	@ManyToOne
	private Product productBooked;
	
	@ManyToOne
	private CustomerTransaction purchaseTransaction;
	private String purchaseTranRef;
	private double purchasedAmount;
	
	@Temporal(TemporalType.DATE)
	private Date booked_dt;
	
	@ManyToOne
	private InstallerLocationJobSchedule job;
	
	private String status; //PENDING, NOT-INSTALLED, ACTIVE, EXPIRED, CANCELED
	
	@Temporal(TemporalType.DATE)
	private Date last_renew_dt, renewal_due_dt;
	private double lastRenewAmount;
	
	@ManyToOne
	private User createdBy;
	@Temporal(TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	public CustomerProduct()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Product getProductBooked() {
		return productBooked;
	}

	public void setProductBooked(Product productBooked) {
		this.productBooked = productBooked;
	}

	public CustomerTransaction getPurchaseTransaction() {
		return purchaseTransaction;
	}

	public void setPurchaseTransaction(CustomerTransaction purchaseTransaction) {
		this.purchaseTransaction = purchaseTransaction;
	}

	public String getPurchaseTranRef() {
		return purchaseTranRef;
	}

	public void setPurchaseTranRef(String purchaseTranRef) {
		this.purchaseTranRef = purchaseTranRef;
	}

	public double getPurchasedAmount() {
		return purchasedAmount;
	}

	public void setPurchasedAmount(double purchasedAmount) {
		this.purchasedAmount = purchasedAmount;
	}

	public Date getBooked_dt() {
		return booked_dt;
	}

	public void setBooked_dt(Date booked_dt) {
		this.booked_dt = booked_dt;
	}

	public InstallerLocationJobSchedule getJob() {
		return job;
	}

	public void setJob(InstallerLocationJobSchedule job) {
		this.job = job;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLast_renew_dt() {
		return last_renew_dt;
	}

	public void setLast_renew_dt(Date last_renew_dt) {
		this.last_renew_dt = last_renew_dt;
	}

	public Date getRenewal_due_dt() {
		return renewal_due_dt;
	}

	public void setRenewal_due_dt(Date renewal_due_dt) {
		this.renewal_due_dt = renewal_due_dt;
	}

	public double getLastRenewAmount() {
		return lastRenewAmount;
	}

	public void setLastRenewAmount(double lastRenewAmount) {
		this.lastRenewAmount = lastRenewAmount;
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
