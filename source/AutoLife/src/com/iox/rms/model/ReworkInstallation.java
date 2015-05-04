package com.iox.rms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
public class ReworkInstallation implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
    @GeneratedValue
	private Long id;
	
	@Column(unique=true)
	private String jobCode;
	
	@ManyToOne
	private InstallerLocationJobSchedule mainJob;
	
	@Temporal(TemporalType.DATE)
	private Date rework_dt;
	
	private String reasonForRework, installerRemarks;
	
	@ManyToOne
	private Agent installer; // the installer
	
	@ManyToOne
	private User createdBy;
	@Temporal(TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	@Transient
	private List<InstallationReworkDeviceUse> deviceUseList;
	
	public ReworkInstallation()
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

	public InstallerLocationJobSchedule getMainJob() {
		return mainJob;
	}

	public void setMainJob(InstallerLocationJobSchedule mainJob) {
		this.mainJob = mainJob;
	}

	public Date getRework_dt() {
		return rework_dt;
	}

	public void setRework_dt(Date rework_dt) {
		this.rework_dt = rework_dt;
	}

	public String getReasonForRework() {
		return reasonForRework;
	}

	public void setReasonForRework(String reasonForRework) {
		this.reasonForRework = reasonForRework;
	}

	public String getInstallerRemarks() {
		return installerRemarks;
	}

	public void setInstallerRemarks(String installerRemarks) {
		this.installerRemarks = installerRemarks;
	}

	public Agent getInstaller() {
		return installer;
	}

	public void setInstaller(Agent installer) {
		this.installer = installer;
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

	public List<InstallationReworkDeviceUse> getDeviceUseList() {
		if(deviceUseList == null)
			deviceUseList = new ArrayList<InstallationReworkDeviceUse>();
		return deviceUseList;
	}

	public void setDeviceUseList(List<InstallationReworkDeviceUse> deviceUseList) {
		this.deviceUseList = deviceUseList;
	}
	
}
