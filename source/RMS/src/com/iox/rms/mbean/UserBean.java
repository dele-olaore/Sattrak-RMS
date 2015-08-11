package com.iox.rms.mbean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.persistence.Query;

import com.dexter.common.util.Emailer;
import com.dexter.common.util.Hasher;
import com.dexter.common.util.SMSGateway;
import com.iox.rms.app.model.AppNotification;
import com.iox.rms.dao.GeneralDAO;
import com.iox.rms.model.Customer;
import com.iox.rms.model.InstallerLocation;
import com.iox.rms.model.InstallerLocationJobSchedule;
import com.iox.rms.model.Notification;
import com.iox.rms.model.Partner;
import com.iox.rms.model.PartnerPersonnel;
import com.iox.rms.model.SalesAgent;
import com.iox.rms.model.TradePartner;
import com.iox.rms.model.User;

@ManagedBean(name = "userBean", eager = true)
@SessionScoped
public class UserBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Partner sessionPartner;
	private User sessionUser;
	private SalesAgent sessionSalesAgent;
	private Customer sessionCustomer;
	private PartnerPersonnel sessionPP;
	private InstallerLocation sessionInstaller;
	private TradePartner sessionTradePartner;
	
	private List<Notification> myUnreadNotifications, myNotifications;
	
	private String function_page, activeTab = "create", activeIMTab = "salesagt";
	
	private String password, new_password, confirm_password;
	
	private BigDecimal todayEarnings, yesterEarnings, thisWeekEarnings, todayCommission, yesterCommission, thisweekCommission;
	private long todayNewCustomers, yesterdayNewCustomers, thisweekNewCustomers;
	private long todayInstallations, yesterdayInstallations, thisweekInstallations;
	
	private long partner_id;
	
	@ManagedProperty("#{appNotifBean}")
	private AppNotificationBean appNotifBean;
	
	public UserBean()
	{}
	
	public String gotoNotificationPage(long notif_id, String notif_url)
	{
		GeneralDAO gDAO = new GeneralDAO();
		Object notif = gDAO.find(Notification.class, notif_id);
		if(notif != null)
		{
			Notification n = (Notification) notif;
			n.setNotified(true);
			gDAO.startTransaction();
			if(gDAO.update(n))
				gDAO.commit();
			
			resetNotifications();
		}
		setFunction_page(notif_url);
		gDAO.destroy();
		return notif_url+"?faces-redirect=true";
	}
	
	public void sendEmail(final String[] to, final String subject, final String message)
	{
		Thread t = new Thread()
		{
			public void run()
			{
				Emailer.sendEmail("rms@sattrakservices.com", to, subject, message);
			}
		};
		t.start();
	}
	
	public void sendEmailWithAttachedment(final String[] to, final String subject, final String message, final String fname, final byte[] data)
	{
		Thread t = new Thread()
		{
			public void run()
			{
				Emailer.sendEmail("rms@sattrakservices.com", to, subject, message, data, fname);
			}
		};
		t.start();
	}
	
	public void sendSMS(final String numbers, final String message)
	{
		Thread t = new Thread()
		{
			public void run()
			{
				SMSGateway.sendSMS("RMS", numbers, message);
			}
		};
		t.start();
	}
	
	public void sendAutoLifeSMS(final String numbers, final String message)
	{
		Thread t = new Thread()
		{
			public void run()
			{
				SMSGateway.sendSMS("AutoLife", numbers, message);
			}
		};
		t.start();
	}
	
	@SuppressWarnings("unused")
	public boolean validatePassword(String password)
	{
		boolean ret = false;
		if(password != null && password.trim().length() > 0)
		{
			int policyMeetCount = 0;
			
			boolean minlength = false, lowercase = false, uppercase = false, digits = false, punctuation = false, specialchar = false;
			if(password.length() >= 8)
				minlength = true;
			
			if(minlength)
			{
				policyMeetCount += 1;
				for(int i=0; i<password.length(); i++)
				{
					char c = password.charAt(i);
					if(Character.isLetter(c) && Character.isLowerCase(c))
					{
						lowercase = true;
						policyMeetCount += 1;
					}
					
					if(Character.isLetter(c) && Character.isUpperCase(c))
					{
						uppercase = true;
						policyMeetCount += 1;
					}
					
					if(Character.isDigit(c))
					{
						digits = true;
						policyMeetCount += 1;
					}
					
					if(Character.isLetter(c) && String.valueOf(c).equals("."))
					{
						punctuation = true;
						policyMeetCount += 1;
					}
					
					if(!Character.isLetter(c) && !Character.isDigit(c) && !Character.isWhitespace(c) && !String.valueOf(c).equals("."))
					{
						specialchar = true;
						policyMeetCount += 1;
					}
					
					if(Character.isWhitespace(c))
					{
						policyMeetCount = 0;
						break;
					}
				}
			}
			
			if(policyMeetCount >= 3)
				ret = true;
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public void changePassword()
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		if(getPassword() != null && getPassword().trim().length() > 0 &&
				getNew_password() != null && getNew_password().trim().length() > 0 &&
				getConfirm_password() != null && getConfirm_password().trim().length() > 0)
		{
			if(getNew_password().equals(getConfirm_password()))
			{
				if(validatePassword(getNew_password()))
				{
					GeneralDAO gDAO = new GeneralDAO();
					
					Hashtable<String, Object> params = new Hashtable<String, Object>();
					params.put("username", getSessionUser().getUsername());
					params.put("password", Hasher.getHashValue(getPassword()));
					User user = null;
					Object u = gDAO.search("User", params);
					if(u != null)
					{
						List<User> list = (List<User>)u;
						for(User e : list)
							user = e;
					}
					
					if(user != null)
					{
						getSessionUser().setPassword(Hasher.getHashValue(getNew_password()));
						gDAO.startTransaction();
						gDAO.update(getSessionUser());
						try
						{
							gDAO.commit();
							an.setType("SUCCESS");
							an.setSubject("Success");
							an.setMessage("Password changed successfully!");
						}
						catch(Exception ex)
						{
							gDAO.rollback();
							getSessionUser().setPassword(Hasher.getHashValue(getPassword()));
							an.setType("ERROR");
							an.setSubject("Error");
							an.setMessage("Message: " + ex.getMessage() + "!");
						}
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Invalid password supplied!");
					}
					gDAO.destroy();
				}
				else
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Your new password does not match the password policy. Please view the help page for more information!");
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("New password fields are not the same!");
			}
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("Please supply all fields!");
		}
		appNotifBean.getAppNotifications().add(an);
	}
	
	public void search()
	{}
	
	public String gotoPage(String page, boolean subFunction)
	{
		if(!subFunction)
			function_page = page;
		
		return page+"?faces-redirect=true";
	}
	
	public Partner getSessionPartner() {
		return sessionPartner;
	}

	public void setSessionPartner(Partner sessionPartner) {
		this.sessionPartner = sessionPartner;
	}

	public User getSessionUser() {
		return sessionUser;
	}

	public void setSessionUser(User sessionUser) {
		this.sessionUser = sessionUser;
	}

	public SalesAgent getSessionSalesAgent() {
		return sessionSalesAgent;
	}

	public void setSessionSalesAgent(SalesAgent sessionSalesAgent) {
		this.sessionSalesAgent = sessionSalesAgent;
	}

	public Customer getSessionCustomer() {
		return sessionCustomer;
	}

	public void setSessionCustomer(Customer sessionCustomer) {
		this.sessionCustomer = sessionCustomer;
	}

	public PartnerPersonnel getSessionPP() {
		return sessionPP;
	}

	public void setSessionPP(PartnerPersonnel sessionPP) {
		this.sessionPP = sessionPP;
	}

	public InstallerLocation getSessionInstaller() {
		return sessionInstaller;
	}

	public void setSessionInstaller(InstallerLocation sessionInstaller) {
		this.sessionInstaller = sessionInstaller;
	}

	public TradePartner getSessionTradePartner() {
		return sessionTradePartner;
	}

	public void setSessionTradePartner(TradePartner sessionTradePartner) {
		this.sessionTradePartner = sessionTradePartner;
	}

	public void resetNotifications()
	{
		setMyUnreadNotifications(null);
		setMyNotifications(null);
	}
	
	public List<Notification> getMyUnreadNotifications() {
		return myUnreadNotifications;
	}

	public void setMyUnreadNotifications(List<Notification> myUnreadNotifications) {
		this.myUnreadNotifications = myUnreadNotifications;
	}

	public List<Notification> getMyNotifications() {
		return myNotifications;
	}

	public void setMyNotifications(List<Notification> myNotifications) {
		this.myNotifications = myNotifications;
	}

	public String getFunction_page() {
		return function_page;
	}

	public void setFunction_page(String function_page) {
		this.function_page = function_page;
	}

	public String getActiveTab() {
		return activeTab;
	}

	public void setActiveTab(String activeTab) {
		this.activeTab = activeTab;
	}

	public String getActiveIMTab() {
		return activeIMTab;
	}

	public void setActiveIMTab(String activeIMTab) {
		this.activeIMTab = activeIMTab;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNew_password() {
		return new_password;
	}

	public void setNew_password(String new_password) {
		this.new_password = new_password;
	}

	public String getConfirm_password() {
		return confirm_password;
	}

	public void setConfirm_password(String confirm_password) {
		this.confirm_password = confirm_password;
	}

	public BigDecimal getTodayEarnings() {
		if(todayEarnings == null)
		{
			todayEarnings = new BigDecimal(0.00);
			GeneralDAO gDAO = new GeneralDAO();
			
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
			c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
			c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
			Date st = c.getTime();
			
			c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
			c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
			c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
			Date end = c.getTime();
			
			Query q = null;
			if(getSessionInstaller() != null)
			{
				q = gDAO.createQuery("Select SUM(e.cost) from InstallerLocationJobSchedule e where e.installer=:installer and (e.booked_dt between :st and :end) and e.paid=true");
				q.setParameter("installer", getSessionInstaller());
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			else if(getSessionSalesAgent() != null || getSessionTradePartner() != null)
			{
				q = gDAO.createQuery("Select SUM(e.cost) from InstallerLocationJobSchedule e where e.createdBy=:createdBy and (e.booked_dt between :st and :end) and e.paid=true");
				q.setParameter("createdBy", getSessionUser());
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			else if(getSessionPartner() != null && !getSessionPartner().isSattrak())
			{
				q = gDAO.createQuery("Select SUM(e.cost) from InstallerLocationJobSchedule e where e.customer.partner=:partner and (e.booked_dt between :st and :end) and e.paid=true");
				q.setParameter("partner", getSessionPartner());
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			else
			{
				q = gDAO.createQuery("Select SUM(e.cost) from InstallerLocationJobSchedule e where e.customer.partner=:partner and (e.booked_dt between :st and :end) and e.paid=true");
				q.setParameter("partner", getPartner());
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			
			Object list = gDAO.search(q, 1);
			if(list != null)
			{
				Double ret = (Double)list;
				if(ret != null)
				{
					todayEarnings = new BigDecimal(ret);
				}
			}
			
			gDAO.destroy();
		}
		return todayEarnings;
	}

	public void setTodayEarnings(BigDecimal todayEarnings) {
		this.todayEarnings = todayEarnings;
	}

	public BigDecimal getYesterEarnings() {
		if(yesterEarnings == null)
		{
			yesterEarnings = new BigDecimal(0.00);
			GeneralDAO gDAO = new GeneralDAO();
			
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, -1);
			c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
			c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
			c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
			Date st = c.getTime();
			
			c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
			c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
			c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
			Date end = c.getTime();
			
			Query q = null;
			if(getSessionInstaller() != null)
			{
				q = gDAO.createQuery("Select SUM(e.cost) from InstallerLocationJobSchedule e where e.installer=:installer and (e.booked_dt between :st and :end) and e.paid=true");
				q.setParameter("installer", getSessionInstaller());
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			else if(getSessionSalesAgent() != null || getSessionTradePartner() != null)
			{
				q = gDAO.createQuery("Select SUM(e.cost) from InstallerLocationJobSchedule e where e.createdBy=:createdBy and (e.booked_dt between :st and :end) and e.paid=true");
				q.setParameter("createdBy", getSessionUser());
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			else if(getSessionPartner() != null && !getSessionPartner().isSattrak())
			{
				q = gDAO.createQuery("Select SUM(e.cost) from InstallerLocationJobSchedule e where e.customer.partner=:partner and (e.booked_dt between :st and :end) and e.paid=true");
				q.setParameter("partner", getSessionPartner());
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			else
			{
				q = gDAO.createQuery("Select SUM(e.cost) from InstallerLocationJobSchedule e where (e.booked_dt between :st and :end) and e.paid=true");
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			
			Object list = gDAO.search(q, 1);
			if(list != null)
			{
				Double ret = (Double)list;
				if(ret != null)
				{
					yesterEarnings = new BigDecimal(ret);
				}
			}
			
			gDAO.destroy();
		}
		return yesterEarnings;
	}

	public void setYesterEarnings(BigDecimal yesterEarnings) {
		this.yesterEarnings = yesterEarnings;
	}

	public BigDecimal getThisWeekEarnings() {
		if(thisWeekEarnings == null)
		{
			thisWeekEarnings = new BigDecimal(0.00);
			GeneralDAO gDAO = new GeneralDAO();
			
			Calendar c = Calendar.getInstance();
			for(int i=0; i<7; i++)
			{
				if(c.get(Calendar.DAY_OF_WEEK) == c.getFirstDayOfWeek())
					break;
				else
					c.add(Calendar.DATE, -1);
			}
			c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
			c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
			c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
			Date st = c.getTime();
			
			c.add(Calendar.DATE, 7);
			c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
			c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
			c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
			Date end = c.getTime();
			
			Query q = null;
			if(getSessionInstaller() != null)
			{
				q = gDAO.createQuery("Select SUM(e.cost) from InstallerLocationJobSchedule e where e.installer=:installer and (e.booked_dt between :st and :end) and e.paid=true");
				q.setParameter("installer", getSessionInstaller());
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			else if(getSessionSalesAgent() != null || getSessionTradePartner() != null)
			{
				q = gDAO.createQuery("Select SUM(e.cost) from InstallerLocationJobSchedule e where e.createdBy=:createdBy and (e.booked_dt between :st and :end) and e.paid=true");
				q.setParameter("createdBy", getSessionUser());
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			else if(getSessionPartner() != null && !getSessionPartner().isSattrak())
			{
				q = gDAO.createQuery("Select SUM(e.cost) from InstallerLocationJobSchedule e where e.customer.partner=:partner and (e.booked_dt between :st and :end) and e.paid=true");
				q.setParameter("partner", getSessionPartner());
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			else
			{
				q = gDAO.createQuery("Select SUM(e.cost) from InstallerLocationJobSchedule e where (e.booked_dt between :st and :end) and e.paid=true");
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			Object list = gDAO.search(q, 1);
			if(list != null)
			{
				Double ret = (Double)list;
				if(ret != null)
				{
					thisWeekEarnings = new BigDecimal(ret);
				}
			}
			
			gDAO.destroy();
		}
		return thisWeekEarnings;
	}

	public void setThisWeekEarnings(BigDecimal thisWeekEarnings) {
		this.thisWeekEarnings = thisWeekEarnings;
	}

	@SuppressWarnings("unchecked")
	public BigDecimal getTodayCommission() {
		if(todayCommission == null)
		{
			todayCommission = new BigDecimal(0.00);
			GeneralDAO gDAO = new GeneralDAO();
			
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
			c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
			c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
			Date st = c.getTime();
			
			c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
			c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
			c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
			Date end = c.getTime();
			
			Query q = null;
			if(getSessionInstaller() != null)
			{
				q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and (e.booked_dt between :st and :end) and e.paid=true and e.cancel=false and e.completed=true");
				q.setParameter("installer", getSessionInstaller());
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			else if(getSessionSalesAgent() != null || getSessionTradePartner() != null)
			{
				q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.createdBy=:createdBy and (e.booked_dt between :st and :end) and e.paid=true and e.cancel=false and e.completed=true");
				q.setParameter("createdBy", getSessionUser());
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			
			Object listObj = gDAO.search(q, 0);
			if(listObj != null)
			{
				List<InstallerLocationJobSchedule> list = (List<InstallerLocationJobSchedule>)listObj;
				for(InstallerLocationJobSchedule e : list)
				{
					q = gDAO.createQuery("Select e.installerCommission from ProductTypeCommission e where e.productType = :productType and e.active=true");
					q.setParameter("productType", e.getProductBooked().getType());
					Object ret = gDAO.search(q, 1);
					if(ret != null)
					{
						Double dbl = (Double)ret;
						todayCommission = todayCommission.add(new BigDecimal(dbl));
					}
				}
			}
			
			gDAO.destroy();
		}
		return todayCommission;
	}

	public void setTodayCommission(BigDecimal todayCommission) {
		this.todayCommission = todayCommission;
	}

	@SuppressWarnings("unchecked")
	public BigDecimal getYesterCommission() {
		if(yesterCommission == null)
		{
			yesterCommission = new BigDecimal(0.00);
			GeneralDAO gDAO = new GeneralDAO();
			
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, -1);
			c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
			c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
			c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
			Date st = c.getTime();
			
			c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
			c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
			c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
			Date end = c.getTime();
			
			Query q = null;
			if(getSessionInstaller() != null)
			{
				q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and (e.booked_dt between :st and :end) and e.paid=true and e.cancel=false and e.completed=true");
				q.setParameter("installer", getSessionInstaller());
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			else if(getSessionSalesAgent() != null || getSessionTradePartner() != null)
			{
				q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.createdBy=:createdBy and (e.booked_dt between :st and :end) and e.paid=true and e.cancel=false and e.completed=true");
				q.setParameter("createdBy", getSessionUser());
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			
			Object listObj = gDAO.search(q, 0);
			if(listObj != null)
			{
				List<InstallerLocationJobSchedule> list = (List<InstallerLocationJobSchedule>)listObj;
				for(InstallerLocationJobSchedule e : list)
				{
					q = gDAO.createQuery("Select e.installerCommission from ProductTypeCommission e where e.productType = :productType and e.active=true");
					q.setParameter("productType", e.getProductBooked().getType());
					Object ret = gDAO.search(q, 1);
					if(ret != null)
					{
						Double dbl = (Double)ret;
						yesterCommission = yesterCommission.add(new BigDecimal(dbl));
					}
				}
			}
			
			gDAO.destroy();
		}
		return yesterCommission;
	}

	public void setYesterCommission(BigDecimal yesterCommission) {
		this.yesterCommission = yesterCommission;
	}

	@SuppressWarnings("unchecked")
	public BigDecimal getThisweekCommission() {
		if(thisweekCommission == null)
		{
			thisweekCommission = new BigDecimal(0.00);
			GeneralDAO gDAO = new GeneralDAO();
			
			Calendar c = Calendar.getInstance();
			for(int i=0; i<7; i++)
			{
				if(c.get(Calendar.DAY_OF_WEEK) == c.getFirstDayOfWeek())
					break;
				else
					c.add(Calendar.DATE, -1);
			}
			c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
			c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
			c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
			Date st = c.getTime();
			
			c.add(Calendar.DATE, 7);
			c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
			c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
			c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
			Date end = c.getTime();
			
			Query q = null;
			if(getSessionInstaller() != null)
			{
				q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and (e.booked_dt between :st and :end) and e.paid=true and e.cancel=false and e.completed=true");
				q.setParameter("installer", getSessionInstaller());
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			else if(getSessionSalesAgent() != null || getSessionTradePartner() != null)
			{
				q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.createdBy=:createdBy and (e.booked_dt between :st and :end) and e.paid=true and e.cancel=false and e.completed=true");
				q.setParameter("createdBy", getSessionUser());
				q.setParameter("st", st);
				q.setParameter("end", end);
			}
			
			Object listObj = gDAO.search(q, 0);
			if(listObj != null)
			{
				List<InstallerLocationJobSchedule> list = (List<InstallerLocationJobSchedule>)listObj;
				for(InstallerLocationJobSchedule e : list)
				{
					q = gDAO.createQuery("Select e.installerCommission from ProductTypeCommission e where e.productType = :productType and e.active=true");
					q.setParameter("productType", e.getProductBooked().getType());
					Object ret = gDAO.search(q, 1);
					if(ret != null)
					{
						Double dbl = (Double)ret;
						thisweekCommission = thisweekCommission.add(new BigDecimal(dbl));
					}
				}
			}
			
			gDAO.destroy();
		}
		return thisweekCommission;
	}

	public void setThisweekCommission(BigDecimal thisweekCommission) {
		this.thisweekCommission = thisweekCommission;
	}

	public long getTodayNewCustomers() {
		todayNewCustomers = 0;
		GeneralDAO gDAO = new GeneralDAO();
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
		Date st = c.getTime();
		
		c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
		Date end = c.getTime();
		
		Query q = null;
		if(getSessionSalesAgent() != null || getSessionTradePartner() != null)
		{
			q = gDAO.createQuery("Select COUNT(e.id) from Customer e where e.createdBy=:createdBy and (e.crt_dt between :st and :end)");
			q.setParameter("createdBy", getSessionUser());
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		else if(getSessionPartner() != null && !getSessionPartner().isSattrak())
		{
			q = gDAO.createQuery("Select COUNT(e.id) from Customer e where e.partner=:partner and (e.crt_dt between :st and :end)");
			q.setParameter("partner", getSessionPartner());
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		else
		{
			q = gDAO.createQuery("Select COUNT(e.id) from Customer e where (e.crt_dt between :st and :end)");
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		
		Object list = gDAO.search(q, 1);
		if(list != null)
		{
			Long ret = (Long)list;
			if(ret != null)
			{
				todayNewCustomers = ret;
			}
		}
		
		gDAO.destroy();
		return todayNewCustomers;
	}

	public void setTodayNewCustomers(long todayNewCustomers) {
		this.todayNewCustomers = todayNewCustomers;
	}

	public long getYesterdayNewCustomers() {
		yesterdayNewCustomers = 0;
		GeneralDAO gDAO = new GeneralDAO();
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
		Date st = c.getTime();
		
		c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
		Date end = c.getTime();
		
		Query q = null;
		if(getSessionSalesAgent() != null || getSessionTradePartner() != null)
		{
			q = gDAO.createQuery("Select COUNT(e.id) from Customer e where e.createdBy=:createdBy and (e.crt_dt between :st and :end)");
			q.setParameter("createdBy", getSessionUser());
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		else if(getSessionPartner() != null && !getSessionPartner().isSattrak())
		{
			q = gDAO.createQuery("Select COUNT(e.id) from Customer e where e.partner=:partner and (e.crt_dt between :st and :end)");
			q.setParameter("partner", getSessionPartner());
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		else
		{
			q = gDAO.createQuery("Select COUNT(e.id) from Customer e where (e.crt_dt between :st and :end)");
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		
		Object list = gDAO.search(q, 1);
		if(list != null)
		{
			Long ret = (Long)list;
			if(ret != null)
			{
				yesterdayNewCustomers = ret;
			}
		}
		
		gDAO.destroy();
		return yesterdayNewCustomers;
	}

	public void setYesterdayNewCustomers(long yesterdayNewCustomers) {
		this.yesterdayNewCustomers = yesterdayNewCustomers;
	}

	public long getThisweekNewCustomers() {
		thisweekNewCustomers = 0;
		GeneralDAO gDAO = new GeneralDAO();
		
		Calendar c = Calendar.getInstance();
		for(int i=0; i<7; i++)
		{
			if(c.get(Calendar.DAY_OF_WEEK) == c.getFirstDayOfWeek())
				break;
			else
				c.add(Calendar.DATE, -1);
		}
		c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
		Date st = c.getTime();
		
		c.add(Calendar.DATE, 7);
		c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
		Date end = c.getTime();
		
		Query q = null;
		if(getSessionSalesAgent() != null || getSessionTradePartner() != null)
		{
			q = gDAO.createQuery("Select COUNT(e.id) from Customer e where e.createdBy=:createdBy and (e.crt_dt between :st and :end)");
			q.setParameter("createdBy", getSessionUser());
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		else if(getSessionPartner() != null && !getSessionPartner().isSattrak())
		{
			q = gDAO.createQuery("Select COUNT(e.id) from Customer e where e.partner=:partner and (e.crt_dt between :st and :end)");
			q.setParameter("partner", getSessionPartner());
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		else
		{
			q = gDAO.createQuery("Select COUNT(e.id) from Customer e where (e.crt_dt between :st and :end)");
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		
		Object list = gDAO.search(q, 1);
		if(list != null)
		{
			Long ret = (Long)list;
			if(ret != null)
			{
				thisweekNewCustomers = ret;
			}
		}
		
		gDAO.destroy();
		return thisweekNewCustomers;
	}

	public void setThisweekNewCustomers(long thisweekNewCustomers) {
		this.thisweekNewCustomers = thisweekNewCustomers;
	}

	public long getTodayInstallations() {
		todayInstallations = 0;
		GeneralDAO gDAO = new GeneralDAO();
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
		Date st = c.getTime();
		
		c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
		Date end = c.getTime();
		
		Query q = null;
		if(getSessionInstaller() != null)
		{
			q = gDAO.createQuery("Select COUNT(e.id) from InstallerLocationJobSchedule e where e.installer=:installer and (e.booked_dt between :st and :end) and e.paid=true and e.cancel=false and e.completed=true");
			q.setParameter("installer", getSessionInstaller());
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		else if(getSessionPartner() != null && !getSessionPartner().isSattrak())
		{
			q = gDAO.createQuery("Select COUNT(e.id) from InstallerLocationJobSchedule e where e.customer.partner=:partner and (e.booked_dt between :st and :end) and e.paid=true and e.cancel=false and e.completed=true");
			q.setParameter("partner", getSessionPartner());
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		else
		{
			q = gDAO.createQuery("Select COUNT(e.id) from InstallerLocationJobSchedule e where (e.booked_dt between :st and :end) and e.paid=true and e.cancel=false and e.completed=true");
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		
		Object list = gDAO.search(q, 1);
		if(list != null)
		{
			Long ret = (Long)list;
			if(ret != null)
			{
				todayInstallations = ret;
			}
		}
		
		gDAO.destroy();
		return todayInstallations;
	}

	public void setTodayInstallations(long todayInstallations) {
		this.todayInstallations = todayInstallations;
	}

	public long getYesterdayInstallations() {
		yesterdayInstallations = 0;
		GeneralDAO gDAO = new GeneralDAO();
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
		Date st = c.getTime();
		
		c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
		Date end = c.getTime();
		
		Query q = null;
		if(getSessionInstaller() != null)
		{
			q = gDAO.createQuery("Select COUNT(e.id) from InstallerLocationJobSchedule e where e.installer=:installer and (e.booked_dt between :st and :end) and e.paid=true and e.cancel=false and e.completed=true");
			q.setParameter("installer", getSessionInstaller());
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		else if(getSessionPartner() != null && !getSessionPartner().isSattrak())
		{
			q = gDAO.createQuery("Select COUNT(e.id) from InstallerLocationJobSchedule e where e.customer.partner=:partner and (e.booked_dt between :st and :end) and e.paid=true and e.cancel=false and e.completed=true");
			q.setParameter("partner", getSessionPartner());
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		else
		{
			q = gDAO.createQuery("Select COUNT(e.id) from InstallerLocationJobSchedule e where (e.booked_dt between :st and :end) and e.paid=true and e.cancel=false and e.completed=true");
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		Object list = gDAO.search(q, 1);
		if(list != null)
		{
			Long ret = (Long)list;
			if(ret != null)
			{
				yesterdayInstallations = ret;
			}
		}
		
		gDAO.destroy();
		return yesterdayInstallations;
	}

	public void setYesterdayInstallations(long yesterdayInstallations) {
		this.yesterdayInstallations = yesterdayInstallations;
	}

	public long getThisweekInstallations() {
		thisweekInstallations = 0;
		GeneralDAO gDAO = new GeneralDAO();
		
		Calendar c = Calendar.getInstance();
		for(int i=0; i<7; i++)
		{
			if(c.get(Calendar.DAY_OF_WEEK) == c.getFirstDayOfWeek())
				break;
			else
				c.add(Calendar.DATE, -1);
		}
		c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
		Date st = c.getTime();
		
		c.add(Calendar.DATE, 7);
		c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
		Date end = c.getTime();
		
		Query q = null;
		if(getSessionInstaller() != null)
		{
			q = gDAO.createQuery("Select COUNT(e.id) from InstallerLocationJobSchedule e where e.installer=:installer and (e.booked_dt between :st and :end) and e.paid=true and e.cancel=false and e.completed=true");
			q.setParameter("installer", getSessionInstaller());
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		else if(getSessionPartner() != null && !getSessionPartner().isSattrak())
		{
			q = gDAO.createQuery("Select COUNT(e.id) from InstallerLocationJobSchedule e where e.customer.partner=:partner and (e.booked_dt between :st and :end) and e.paid=true and e.cancel=false and e.completed=true");
			q.setParameter("partner", getSessionPartner());
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		else
		{
			q = gDAO.createQuery("Select COUNT(e.id) from InstallerLocationJobSchedule e where (e.booked_dt between :st and :end) and e.paid=true and e.cancel=false and e.completed=true");
			q.setParameter("st", st);
			q.setParameter("end", end);
		}
		
		Object list = gDAO.search(q, 1);
		if(list != null)
		{
			Long ret = (Long)list;
			if(ret != null)
			{
				thisweekInstallations = ret;
			}
		}
		
		gDAO.destroy();
		return thisweekInstallations;
	}

	public void setThisweekInstallations(long thisweekInstallations) {
		this.thisweekInstallations = thisweekInstallations;
	}

	public Partner getPartner() {
		Partner partner = null;
		if(!getSessionPartner().isSattrak())
		{
			partner = getSessionPartner();
		}
		else
		{
			if(getPartner_id() > 0)
			{
				partner = (Partner)new GeneralDAO().find(Partner.class, getPartner_id());
			}
		}
		return partner;
	}
	
	public long getPartner_id() {
		return partner_id;
	}

	public void setPartner_id(long partner_id) {
		this.partner_id = partner_id;
	}

	public AppNotificationBean getAppNotifBean() {
		return appNotifBean;
	}

	public void setAppNotifBean(AppNotificationBean appNotifBean) {
		this.appNotifBean = appNotifBean;
	}
	
}
