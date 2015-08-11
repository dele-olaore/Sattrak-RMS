package com.iox.rms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
public class Product implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
    @GeneratedValue
	private Long id;
	@ManyToOne
	private ProductType type;
	private String productName;
	private String details;
	private String moreDetails;
	
	private double firstYearPayment, yearlySubscription, leaseModule;
	
	private boolean specialProduct, requiresRenewal, active;
	
	private byte[] photo;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	@Transient
	private boolean selected;
	@Transient
	private List<ProductItem> items;
	
	@Transient
	private int count = 1;
	@Transient
	private double sellingAmount = 0.0;
	
	public Product()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProductType getType() {
		return type;
	}

	public void setType(ProductType type) {
		this.type = type;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getMoreDetails() {
		return moreDetails;
	}

	public void setMoreDetails(String moreDetails) {
		this.moreDetails = moreDetails;
	}

	public double getFirstYearPayment() {
		return firstYearPayment;
	}

	public void setFirstYearPayment(double firstYearPayment) {
		this.firstYearPayment = firstYearPayment;
	}

	public double getYearlySubscription() {
		return yearlySubscription;
	}

	public void setYearlySubscription(double yearlySubscription) {
		this.yearlySubscription = yearlySubscription;
	}

	public double getLeaseModule() {
		return leaseModule;
	}

	public void setLeaseModule(double leaseModule) {
		this.leaseModule = leaseModule;
	}

	public boolean isSpecialProduct() {
		return specialProduct;
	}

	public void setSpecialProduct(boolean specialProduct) {
		this.specialProduct = specialProduct;
	}

	public boolean isRequiresRenewal() {
		return requiresRenewal;
	}

	public void setRequiresRenewal(boolean requiresRenewal) {
		this.requiresRenewal = requiresRenewal;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public Date getCrt_dt() {
		return crt_dt;
	}

	public void setCrt_dt(Date crt_dt) {
		this.crt_dt = crt_dt;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public List<ProductItem> getItems() {
		if(items == null)
			items = new ArrayList<ProductItem>();
		return items;
	}

	public void setItems(List<ProductItem> items) {
		this.items = items;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public double getSellingAmount() {
		return sellingAmount;
	}

	public void setSellingAmount(double sellingAmount) {
		this.sellingAmount = sellingAmount;
	}
	
}
