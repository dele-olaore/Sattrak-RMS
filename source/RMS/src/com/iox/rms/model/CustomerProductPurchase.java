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
public class CustomerProductPurchase implements Serializable
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
	private int count;
	@ManyToOne
	private CustomerTransaction purchaseTransaction; // We can have multiple enteries with the same customer transaction id
	
	private double expectedAmount, purchasedAmount, unitAmount;
	
	@ManyToOne
	private User createdBy;
	@Temporal(TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	public CustomerProductPurchase()
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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public CustomerTransaction getPurchaseTransaction() {
		return purchaseTransaction;
	}

	public void setPurchaseTransaction(CustomerTransaction purchaseTransaction) {
		this.purchaseTransaction = purchaseTransaction;
	}

	public double getExpectedAmount() {
		return expectedAmount;
	}

	public void setExpectedAmount(double expectedAmount) {
		this.expectedAmount = expectedAmount;
	}

	public double getPurchasedAmount() {
		return purchasedAmount;
	}

	public void setPurchasedAmount(double purchasedAmount) {
		this.purchasedAmount = purchasedAmount;
	}

	public double getUnitAmount() {
		return unitAmount;
	}

	public void setUnitAmount(double unitAmount) {
		this.unitAmount = unitAmount;
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
