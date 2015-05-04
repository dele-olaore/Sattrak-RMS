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
public class AppConfiguration implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
    @GeneratedValue
	private Long id;
	
	private String customerSupportEmail, customerSupportMobiles;
	private int checkInReminderMinute, cancelDueDays;
	private double refundChargePercent;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date crt_dt, last_update_dt;
	
	public AppConfiguration()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCustomerSupportEmail() {
		return customerSupportEmail;
	}

	public void setCustomerSupportEmail(String customerSupportEmail) {
		this.customerSupportEmail = customerSupportEmail;
	}

	public String getCustomerSupportMobiles() {
		return customerSupportMobiles;
	}

	public void setCustomerSupportMobiles(String customerSupportMobiles) {
		this.customerSupportMobiles = customerSupportMobiles;
	}

	public int getCheckInReminderMinute() {
		return checkInReminderMinute;
	}

	public void setCheckInReminderMinute(int checkInReminderMinute) {
		this.checkInReminderMinute = checkInReminderMinute;
	}

	public int getCancelDueDays() {
		return cancelDueDays;
	}

	public void setCancelDueDays(int cancelDueDays) {
		this.cancelDueDays = cancelDueDays;
	}

	public double getRefundChargePercent() {
		return refundChargePercent;
	}

	public void setRefundChargePercent(double refundChargePercent) {
		this.refundChargePercent = refundChargePercent;
	}

	public Date getCrt_dt() {
		return crt_dt;
	}

	public void setCrt_dt(Date crt_dt) {
		this.crt_dt = crt_dt;
	}

	public Date getLast_update_dt() {
		return last_update_dt;
	}

	public void setLast_update_dt(Date last_update_dt) {
		this.last_update_dt = last_update_dt;
	}
}
