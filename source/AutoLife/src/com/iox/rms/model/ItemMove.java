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
public class ItemMove implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
    @GeneratedValue
	private Long id;
	
	@ManyToOne
	private Item item;
	private String moveType; // SUPPLY, DISTRIBUTION, RETURNED, DAMAGED
	
	private long count;
	private String remarks;
	@Temporal(TemporalType.TIMESTAMP)
	private Date move_dt;
	
	private long before_balance, new_balance;
	
	@ManyToOne
	private InstallerLocation installer;
	@ManyToOne
	private SalesAgent salesAgent;
	@ManyToOne
	private TradePartner tradePartner;
	
	@ManyToOne
	private User createdBy;
	@Temporal(TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	public ItemMove()
	{}

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

	public String getMoveType() {
		return moveType;
	}

	public void setMoveType(String moveType) {
		this.moveType = moveType;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getMove_dt() {
		return move_dt;
	}

	public void setMove_dt(Date move_dt) {
		this.move_dt = move_dt;
	}

	public long getBefore_balance() {
		return before_balance;
	}

	public void setBefore_balance(long before_balance) {
		this.before_balance = before_balance;
	}

	public long getNew_balance() {
		return new_balance;
	}

	public void setNew_balance(long new_balance) {
		this.new_balance = new_balance;
	}

	public InstallerLocation getInstaller() {
		return installer;
	}

	public void setInstaller(InstallerLocation installer) {
		this.installer = installer;
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
