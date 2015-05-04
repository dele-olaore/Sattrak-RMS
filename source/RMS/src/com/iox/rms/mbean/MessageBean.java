package com.iox.rms.mbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import com.iox.rms.app.model.AppNotification;
import com.iox.rms.dao.GeneralDAO;
import com.iox.rms.model.Customer;
import com.iox.rms.model.InstallerLocation;
import com.iox.rms.model.Partner;
import com.iox.rms.model.PartnerPersonnel;
import com.iox.rms.model.SalesAgent;

@ManagedBean(name = "messageBean", eager = true)
@SessionScoped
public class MessageBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String sendToCategory = "ALL";; // This is the category of the user the message should be sent to, eg. ALL, INSTALLATION_AGENT, CUSTOMER, SALES_AGENT, CUSTOMER_SERVICE, SYSTEM ADMINISTRATOR
	private String emailSubject, emailContent;
	private String smsContent;
	
	private List<Customer> customers;
	private List<SalesAgent> salesAgents;
	private List<InstallerLocation> installers;
	private List<PartnerPersonnel> ppList;
	
	private int totalrecipientCount, totalrecipientSMSCount;
	private long partner_id;
	
	private String activeTab = "create";
	
	@ManagedProperty("#{userBean}")
	private UserBean userBean;
	
	@ManagedProperty("#{appNotifBean}")
	private AppNotificationBean appNotifBean;
	
	public MessageBean()
	{}
	
	public void checkAllSMSRecipients()
	{
		for(SalesAgent e : getSalesAgents())
		{
			e.setSelected(true);
		}
		for(InstallerLocation e : getInstallers())
		{
			e.setSelected(true);
		}
	}
	
	public void uncheckAllSMSRecipients()
	{
		for(SalesAgent e : getSalesAgents())
		{
			e.setSelected(false);
		}
		for(InstallerLocation e : getInstallers())
		{
			e.setSelected(false);
		}
	}
	
	public void checkAllEmailRecipients()
	{
		for(Customer e : getCustomers())
		{
			e.setSelected(true);
		}
		for(SalesAgent e : getSalesAgents())
		{
			e.setSelected(true);
		}
		for(InstallerLocation e : getInstallers())
		{
			e.setSelected(true);
		}
		for(PartnerPersonnel e : getPpList())
		{
			e.setSelected(true);
		}
	}
	
	public void uncheckAllEmailRecipients()
	{
		for(Customer e : getCustomers())
		{
			e.setSelected(false);
		}
		for(SalesAgent e : getSalesAgents())
		{
			e.setSelected(false);
		}
		for(InstallerLocation e : getInstallers())
		{
			e.setSelected(false);
		}
		for(PartnerPersonnel e : getPpList())
		{
			e.setSelected(false);
		}
	}
	
	public void sendEmailMessage()
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		//System.out.println("Send Email Message");
		if(getEmailSubject() != null && getEmailSubject().trim().length() > 0 &&
				getEmailContent() != null && getEmailContent().trim().length() > 0)
		{
			if(getTotalrecipientCount() > 0)
			{
				ArrayList<String> toList = new ArrayList<String>();
				if(getCustomers() != null)
				{
					for(Customer e : getCustomers())
					{
						if(e.isSelected())
						{
							toList.add(e.getUser().getUsername());
						}
					}
				}
				if(getSalesAgents() != null)
				{
					for(SalesAgent e : getSalesAgents())
					{
						if(e.isSelected())
						{
							toList.add(e.getUser().getUsername());
						}
					}
				}
				if(getInstallers() != null)
				{
					for(InstallerLocation e : getInstallers())
					{
						if(e.isSelected())
						{
							toList.add(e.getUser().getUsername());
						}
					}
				}
				if(getPpList() != null)
				{
					for(PartnerPersonnel e : getPpList())
					{
						if(e.isSelected())
						{
							toList.add(e.getUser().getUsername());
						}
					}
				}
				String[] to = new String[toList.size()];
				to = toList.toArray(to);
				
				userBean.sendEmail(to, getEmailSubject(), getEmailContent());
				an.setType("SUCCESS");
				an.setSubject("Success");
				an.setMessage("Email sent successfully!");
				
				setEmailContent(null);
				setCustomers(null);
				setSalesAgents(null);
				setInstallers(null);
				setPpList(null);
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("Please select at least one recipient!");
			}
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("Please input the subject and content of the email!");
		}
		appNotifBean.getAppNotifications().add(an);
	}
	
	public void sendSMSMessage()
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		//System.out.println("Send SMS Message");
		if(getSmsContent() != null && getSmsContent().trim().length() > 0)
		{
			if(getTotalrecipientSMSCount() > 0)
			{
				ArrayList<String> toList = new ArrayList<String>();
				if(getSalesAgents() != null)
				{
					for(SalesAgent e : getSalesAgents())
					{
						if(e.isSelected())
						{
							toList.add(e.getPhoneNo());
						}
					}
				}
				if(getInstallers() != null)
				{
					for(InstallerLocation e : getInstallers())
					{
						if(e.isSelected())
						{
							toList.add(e.getPhoneNo());
						}
					}
				}
				
				String[] to = new String[toList.size()];
				to = toList.toArray(to);
				
				String numbers = "";
				for(int i=0; i<to.length; i++)
				{
					String e = to[i];
					numbers += e;
					if(i < (to.length-1))
						numbers += ",";
				}
				
				userBean.sendSMS(numbers, getSmsContent());
				an.setType("SUCCESS");
				an.setSubject("Success");
				an.setMessage("SMS sent successfully!");
				
				setSmsContent(null);
				setSalesAgents(null);
				setInstallers(null);
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("Please select at least one recipient!");
			}
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("Please input the content of the SMS!");
		}
		appNotifBean.getAppNotifications().add(an);
	}
	
	public void resetLists()
	{
		setCustomers(null);
		setInstallers(null);
		setPpList(null);
		setSalesAgents(null);
	}
	
	public void sortEmailRecipients()
	{
		resetLists();
	}
	
	public void sortSMSRecipients()
	{
		resetLists();
	}

	public String getSendToCategory() {
		return sendToCategory;
	}

	public void setSendToCategory(String sendToCategory) {
		this.sendToCategory = sendToCategory;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailContent() {
		return emailContent;
	}

	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}

	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}

	@SuppressWarnings("unchecked")
	public List<Customer> getCustomers() {
		if(customers == null)
		{
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("partner", getPartner());
			
			GeneralDAO gDAO = new GeneralDAO();
			Object cusObj = gDAO.search("Customer", params);
			if(cusObj != null)
				customers = (List<Customer>)cusObj;
			gDAO.destroy();
		}
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	@SuppressWarnings("unchecked")
	public List<SalesAgent> getSalesAgents() {
		if(salesAgents == null)
		{
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("partner", getPartner());
			
			GeneralDAO gDAO = new GeneralDAO();
			Object all = gDAO.search("SalesAgent", params);
			if(all != null)
				salesAgents = (List<SalesAgent>)all;
			gDAO.destroy();
		}
		return salesAgents;
	}

	public void setSalesAgents(List<SalesAgent> salesAgents) {
		this.salesAgents = salesAgents;
	}

	@SuppressWarnings("unchecked")
	public List<InstallerLocation> getInstallers() {
		if(installers == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Object all = gDAO.findAll("InstallerLocation");
			if(all != null)
				installers = (List<InstallerLocation>)all;
			gDAO.destroy();
		}
		return installers;
	}

	public void setInstallers(List<InstallerLocation> installers) {
		this.installers = installers;
	}

	@SuppressWarnings("unchecked")
	public List<PartnerPersonnel> getPpList() {
		if(ppList == null)
		{
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("partner", getPartner());
			
			GeneralDAO gDAO = new GeneralDAO();
			Object cusObj = gDAO.search("PartnerPersonnel", params);
			if(cusObj != null)
				ppList = (List<PartnerPersonnel>)cusObj;
			gDAO.destroy();
		}
		return ppList;
	}

	public void setPpList(List<PartnerPersonnel> ppList) {
		this.ppList = ppList;
	}

	public int getTotalrecipientCount() {
		totalrecipientCount = 0;
		
		if(getCustomers() != null)
		{
			for(Customer e : getCustomers())
			{
				if(e.isSelected())
					totalrecipientCount += 1;
			}
		}
		if(getSalesAgents() != null)
		{
			for(SalesAgent e : getSalesAgents())
			{
				if(e.isSelected())
					totalrecipientCount += 1;
			}
		}
		if(getInstallers() != null)
		{
			for(InstallerLocation e : getInstallers())
			{
				if(e.isSelected())
					totalrecipientCount += 1;
			}
		}
		if(getPpList() != null)
		{
			for(PartnerPersonnel e : getPpList())
			{
				if(e.isSelected())
					totalrecipientCount += 1;
			}
		}
		
		return totalrecipientCount;
	}

	public void setTotalrecipientCount(int totalrecipientCount) {
		this.totalrecipientCount = totalrecipientCount;
	}

	public int getTotalrecipientSMSCount() {
		totalrecipientSMSCount = 0;
		if(getSalesAgents() != null)
		{
			for(SalesAgent e : getSalesAgents())
			{
				if(e.isSelected())
					totalrecipientSMSCount += 1;
			}
		}
		if(getInstallers() != null)
		{
			for(InstallerLocation e : getInstallers())
			{
				if(e.isSelected())
					totalrecipientSMSCount += 1;
			}
		}
		return totalrecipientSMSCount;
	}

	public void setTotalrecipientSMSCount(int totalrecipientSMSCount) {
		this.totalrecipientSMSCount = totalrecipientSMSCount;
	}

	public long getPartner_id() {
		return partner_id;
	}

	public void setPartner_id(long partner_id) {
		this.partner_id = partner_id;
	}

	public String getActiveTab() {
		return activeTab;
	}

	public void setActiveTab(String activeTab) {
		this.activeTab = activeTab;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public AppNotificationBean getAppNotifBean() {
		return appNotifBean;
	}

	public void setAppNotifBean(AppNotificationBean appNotifBean) {
		this.appNotifBean = appNotifBean;
	}
	
	public Partner getPartner() {
		Partner partner = null;
		if(!userBean.getSessionPartner().isSattrak())
		{
			partner = userBean.getSessionPartner();
		}
		else
		{
			if(getPartner_id() > 0)
			{
				partner = (Partner)new GeneralDAO().find(Partner.class, getPartner_id());
			}
			else
				partner = new Partner();
		}
		return partner;
	}
}
