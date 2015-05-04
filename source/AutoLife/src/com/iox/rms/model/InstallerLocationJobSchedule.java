package com.iox.rms.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
public class InstallerLocationJobSchedule implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
    @GeneratedValue
	private Long id;
	
	@Column(unique=true)
	private String jobCode;
	
	@ManyToOne
	private InstallerLocation installer;
	@ManyToOne
	private Customer customer;
	
	@ManyToOne
	private VehicleType vehicleType;
	@ManyToOne
	private VehicleMake vehicleMake;
	private String vehicleModel, vehicleColor, vehicleEngineNo, vehicleChasis;
	@NotNull
	private String vehicleRegNum;
	private BigDecimal vehicleOdometer;
	private String vehicleIMEINo;
	private String unitType, network, simcardNo;
	
	@ManyToOne
	private Product productBooked;
	@Temporal(TemporalType.DATE)
	private Date booked_dt;
	private String paymentType; // PAY-ON-DELIVERY, CARD, BANK-TRANSFER, BANK-DEPOSIT
	private double cost;
	
	@ManyToOne
	private Slot slot;
	@Temporal(TemporalType.TIMESTAMP)
	private Date start_dt, end_dt;
	
	private boolean require_confirmation, confirmed, cancel;
	private boolean paid, checkedIn, completed, refund;
	
	private double refundChargePercent;
	private double refundAmt;
	
	private boolean todayLateNotified, yesterdayLateNotified, twoDaysLateNotified;
	private boolean checkInReminded;
	
	@ManyToOne
	private Agent agent; // the installer
	
	private String installerRemarks, confirmationRemarks;
	
	@ManyToOne
	private User createdBy;
	@Temporal(TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	public InstallerLocationJobSchedule()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getJobCode() {
		return jobCode;
	}

	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}

	public InstallerLocation getInstaller() {
		return installer;
	}

	public void setInstaller(InstallerLocation installer) {
		this.installer = installer;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public VehicleType getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}

	public VehicleMake getVehicleMake() {
		return vehicleMake;
	}

	public void setVehicleMake(VehicleMake vehicleMake) {
		this.vehicleMake = vehicleMake;
	}

	public String getVehicleModel() {
		return vehicleModel;
	}

	public void setVehicleModel(String vehicleModel) {
		this.vehicleModel = vehicleModel;
	}

	public String getVehicleRegNum() {
		return vehicleRegNum;
	}

	public void setVehicleRegNum(String vehicleRegNum) {
		this.vehicleRegNum = vehicleRegNum;
	}

	public Product getProductBooked() {
		return productBooked;
	}

	public void setProductBooked(Product productBooked) {
		this.productBooked = productBooked;
	}

	public Date getBooked_dt() {
		return booked_dt;
	}

	public void setBooked_dt(Date booked_dt) {
		this.booked_dt = booked_dt;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public Slot getSlot() {
		return slot;
	}

	public void setSlot(Slot slot) {
		this.slot = slot;
	}

	public Date getStart_dt() {
		return start_dt;
	}

	public void setStart_dt(Date start_dt) {
		this.start_dt = start_dt;
	}

	public Date getEnd_dt() {
		return end_dt;
	}

	public void setEnd_dt(Date end_dt) {
		this.end_dt = end_dt;
	}

	public boolean isRequire_confirmation() {
		return require_confirmation;
	}

	public void setRequire_confirmation(boolean require_confirmation) {
		this.require_confirmation = require_confirmation;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public boolean isCancel() {
		return cancel;
	}

	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public boolean isCheckedIn() {
		return checkedIn;
	}

	public void setCheckedIn(boolean checkedIn) {
		this.checkedIn = checkedIn;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	public boolean isRefund() {
		return refund;
	}

	public void setRefund(boolean refund) {
		this.refund = refund;
	}

	public double getRefundChargePercent() {
		return refundChargePercent;
	}

	public void setRefundChargePercent(double refundChargePercent) {
		this.refundChargePercent = refundChargePercent;
	}

	public double getRefundAmt() {
		return refundAmt;
	}

	public void setRefundAmt(double refundAmt) {
		this.refundAmt = refundAmt;
	}

	public boolean isTodayLateNotified() {
		return todayLateNotified;
	}

	public void setTodayLateNotified(boolean todayLateNotified) {
		this.todayLateNotified = todayLateNotified;
	}

	public boolean isYesterdayLateNotified() {
		return yesterdayLateNotified;
	}

	public void setYesterdayLateNotified(boolean yesterdayLateNotified) {
		this.yesterdayLateNotified = yesterdayLateNotified;
	}

	public boolean isTwoDaysLateNotified() {
		return twoDaysLateNotified;
	}

	public void setTwoDaysLateNotified(boolean twoDaysLateNotified) {
		this.twoDaysLateNotified = twoDaysLateNotified;
	}

	public boolean isCheckInReminded() {
		return checkInReminded;
	}

	public void setCheckInReminded(boolean checkInReminded) {
		this.checkInReminded = checkInReminded;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public String getInstallerRemarks() {
		return installerRemarks;
	}

	public void setInstallerRemarks(String installerRemarks) {
		this.installerRemarks = installerRemarks;
	}

	public String getConfirmationRemarks() {
		return confirmationRemarks;
	}

	public void setConfirmationRemarks(String confirmationRemarks) {
		this.confirmationRemarks = confirmationRemarks;
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

	public String getVehicleColor() {
		return vehicleColor;
	}

	public void setVehicleColor(String vehicleColor) {
		this.vehicleColor = vehicleColor;
	}

	public String getVehicleEngineNo() {
		return vehicleEngineNo;
	}

	public void setVehicleEngineNo(String vehicleEngineNo) {
		this.vehicleEngineNo = vehicleEngineNo;
	}

	public String getVehicleChasis() {
		return vehicleChasis;
	}

	public void setVehicleChasis(String vehicleChasis) {
		this.vehicleChasis = vehicleChasis;
	}

	public BigDecimal getVehicleOdometer() {
		return vehicleOdometer;
	}

	public void setVehicleOdometer(BigDecimal vehicleOdometer) {
		this.vehicleOdometer = vehicleOdometer;
	}

	public String getVehicleIMEINo() {
		return vehicleIMEINo;
	}

	public void setVehicleIMEINo(String vehicleIMEINo) {
		this.vehicleIMEINo = vehicleIMEINo;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getSimcardNo() {
		return simcardNo;
	}

	public void setSimcardNo(String simcardNo) {
		this.simcardNo = simcardNo;
	}
	
}
