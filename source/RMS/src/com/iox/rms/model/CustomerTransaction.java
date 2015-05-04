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
public class CustomerTransaction implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
    @GeneratedValue
	private Long id;
	
	@ManyToOne
	private Customer customer;
	@ManyToOne
	private Product product;
	@Column(unique=true)
	private String tranRef;
	private double amount;
	@Temporal(TemporalType.TIMESTAMP)
	private Date tranInitDate;
	private String payFor; // PURCHASE, RENEWAL
	private String payMode; // Pay Online, Pay at Bank
	private String confirmationInfo;
	@Temporal(TemporalType.TIMESTAMP)
	private Date payConfirmDate;
	private String status; // PENDING, PAID, FAILED, CANCELLED, REFUNDED
	@ManyToOne
	private User confirmedBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	public CustomerTransaction()
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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getTranRef() {
		return tranRef;
	}

	public void setTranRef(String tranRef) {
		this.tranRef = tranRef;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getTranInitDate() {
		return tranInitDate;
	}

	public void setTranInitDate(Date tranInitDate) {
		this.tranInitDate = tranInitDate;
	}

	public String getPayFor() {
		return payFor;
	}

	public void setPayFor(String payFor) {
		this.payFor = payFor;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	public String getConfirmationInfo() {
		return confirmationInfo;
	}

	public void setConfirmationInfo(String confirmationInfo) {
		this.confirmationInfo = confirmationInfo;
	}

	public Date getPayConfirmDate() {
		return payConfirmDate;
	}

	public void setPayConfirmDate(Date payConfirmDate) {
		this.payConfirmDate = payConfirmDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public User getConfirmedBy() {
		return confirmedBy;
	}

	public void setConfirmedBy(User confirmedBy) {
		this.confirmedBy = confirmedBy;
	}

	public Date getCrt_dt() {
		return crt_dt;
	}

	public void setCrt_dt(Date crt_dt) {
		this.crt_dt = crt_dt;
	}
	
}
