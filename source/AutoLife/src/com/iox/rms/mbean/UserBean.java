/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iox.rms.mbean;

import com.dexter.common.util.Crypto;
import com.dexter.common.util.Emailer;
import com.dexter.common.util.SMSGateway;
import com.iox.pp.model.PaymentTransaction;
import com.iox.rms.dao.GeneralDAO;
import com.iox.rms.model.AppConfiguration;
import com.iox.rms.model.Country;
import com.iox.rms.model.Customer;
import com.iox.rms.model.CustomerProduct;
import com.iox.rms.model.CustomerTransaction;
import com.iox.rms.model.InstallerLocation;
import com.iox.rms.model.InstallerLocationJobSchedule;
import com.iox.rms.model.LGA;
import com.iox.rms.model.Notification;
import com.iox.rms.model.Partner;
import com.iox.rms.model.Product;
import com.iox.rms.model.ProductType;
import com.iox.rms.model.Slot;
import com.iox.rms.model.State;
import com.iox.rms.model.User;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.json.JSONObject;

/**
 *
 * @author oladele
 */
@ManagedBean(name = "userBean", eager = true)
@SessionScoped
public class UserBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Partner sessionPartner;
    private User sessionUser;
    private Customer sessionCustomer;
    
    private long ctry_id, state_id, lga_id;
    private List<Country> countries;
    private List<State> states;
    private List<LGA> lgas;
    
    private List<CustomerProduct> myProducts;
    private List<InstallerLocationJobSchedule> mySchedules_cus;
    private InstallerLocationJobSchedule selectedInstallation;
    
    private long ptype_id, product_id;
    private List<ProductType> ptypes;
    private List<Product> products;
    
    private Product productToBuy;
    private String productToBuyRef;
    private String payType;
    
    private InstallerLocation selInstallationPoint;
    private List<InstallerLocation> installersByLGA;
    private List<Slot> slots;
    private List<CustomerProduct> uninstalledPurchases;
	private long cp_id, installation_point_id, slot_id;
	private Date installation_dt, start_dt, end_dt;
	
	private List<CustomerTransaction> ctList;
	
	@SuppressWarnings("unchecked")
	public void searchPaymentHistory()
	{
		setCtList(null);
		if(getStart_dt() != null && getEnd_dt() != null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			
			Query q = gDAO.createQuery("Select e from CustomerTransaction e where (e.tranInitDate between :stdt and :eddt) order by e.tranInitDate desc");
			q.setParameter("stdt", getStart_dt());
			q.setParameter("eddt", getEnd_dt());
			
			Object listObj = gDAO.search(q, 0);
			if(listObj != null)
				setCtList((List<CustomerTransaction>)listObj);
			gDAO.destroy();
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "Please supply search dates!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
    
    public String initBuyProduct(long pid)
    {
    	String ret = "buyProduct";
    	
    	GeneralDAO gDAO = new GeneralDAO();
    	Object p = gDAO.find(Product.class, pid);
    	if(p != null)
    	{
    		setProductToBuy((Product)p);
    		
    		String tranRef = "";
    		try
    		{
    			tranRef = sessionCustomer.getUniqueID().substring(sessionCustomer.getUniqueID().lastIndexOf("-")+1) + "-" + System.currentTimeMillis();
    		}
    		catch(Exception ex)
    		{
    			tranRef = sessionCustomer.getUniqueID() + "-" + System.currentTimeMillis();
    		}
    		setProductToBuyRef(tranRef);
    		
    		ret = "payProduct?faces-redirect=true";
    	}
    	else
    	{
    		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "Unknown product selected!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
    	}
    	
    	gDAO.destroy();
    	
    	return ret;
    }
    
    public String buyProduct()
    {
    	String ret = "payProduct";
    	
        if(getProductToBuy() != null && getProductToBuyRef() != null && getPayType() != null)
        {
        	GeneralDAO gDAO = new GeneralDAO();
    		
    		CustomerTransaction ct = new CustomerTransaction();
    		ct.setAmount(getProductToBuy().getFirstYearPayment());
    		ct.setCrt_dt(new Date());
    		ct.setCustomer(getSessionCustomer());
    		ct.setPayMode(getPayType());
    		ct.setPayFor("PURCHASE");
    		ct.setProduct(getProductToBuy());
    		ct.setStatus("PENDING");
    		ct.setTranInitDate(new Date());
    		ct.setTranRef(getProductToBuyRef());
    		
    		CustomerProduct cp = new CustomerProduct();
    		cp.setBooked_dt(new Date());
    		cp.setCreatedBy(getSessionUser());
    		cp.setCrt_dt(new Date());
    		cp.setCustomer(getSessionCustomer());
    		cp.setProductBooked(getProductToBuy());
    		cp.setPurchasedAmount(getProductToBuy().getFirstYearPayment());
    		cp.setPurchaseTranRef(getProductToBuyRef());
    		cp.setStatus("PENDING");
    		
    		gDAO.startTransaction();
    		gDAO.save(ct);
    		
    		cp.setPurchaseTransaction(ct);
    		gDAO.save(cp);
    		
    		try
    		{
    			//Create the purchase here, but make it pending so that a sattrak personnel (or RMS automated platform) will have to mark it as paid when the bank sends in the alert of the customer depositing for this payment.
    			gDAO.commit();
    			gDAO.destroy();
    			
	        	if(getPayType().equals("Pay at Bank"))
	        	{
            		//Send email and sms to the customer on how and which account to pay to, also include the product details and amount, in the email, also ask the customer to notify sattrak via email after making the payment at the bank
        			
        			// Attach the invoice to this email
        			byte[] data = generateInvoiceForCustomerPurchase(cp);
					if(data != null)
						sendEmailWithAttachedment(new String[]{getSessionUser().getUsername()}, "How to pay", getHowToPayMessage(getProductToBuyRef(), cp), "invoice-"+cp.getPurchaseTranRef()+".pdf", data);
					else
						sendEmail(new String[]{getSessionUser().getUsername()}, "How to pay", getHowToPayMessage(getProductToBuyRef(), cp));
        			sendSMS(getSessionCustomer().getPhoneNo(), "Dear " + getSessionCustomer().getFirstname() + ", we have sent you an email on how to make payment for your transaction(" + getProductToBuyRef() + "). Please notify us after making your payment. Thank you.");
        			
        			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success: ", "Payment saved! We have sent you steps on how to complete your payment!");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    
                    ret = "paymentSuccess?faces-redirect=true";
	        		
	        	}
	        	else if(getPayType().equals("Pay Online"))
	        	{
	        		PaymentTransaction pt = new PaymentTransaction();
	        		
	        		pt.setAmount(getProductToBuy().getFirstYearPayment());
	        		pt.setAppId("AUTOLIFE");
	        		pt.setCrt_dt(new Date());
	        		pt.setCustomerEmail(getSessionUser().getUsername());
	        		pt.setCustomerName(getSessionCustomer().getFirstname() + " " + getSessionCustomer().getLastname());
	        		pt.setCustomerPhone(getSessionCustomer().getPhoneNo());
	        		pt.setStatus("PENDING");
	        		pt.setTranDate(new Date());
	        		pt.setTransRef(getProductToBuyRef());
	        		pt.setProductName(getProductToBuy().getProductName());
	        		
	        		com.iox.pp.dao.GeneralDAO ppGDAO = new com.iox.pp.dao.GeneralDAO();
	        		ppGDAO.startTransaction();
	        		ppGDAO.save(pt);
	        		
	        		//JSONObject req = new JSONObject();
	        		try
	        		{
	        			/*req.put("externalAppCode", );
	        			req.put("transactionRef", );
	        			req.put("customerName", );
	        			req.put("customerEmail", );
	        			req.put("customerPhone", );
	        			req.put("tranInitDate", new Date());
	        			req.put("amount", );
	        			
	        			String reqStr = req.toString();
	        			String reqStrEncrypted = Crypto.encrypt256(Crypto.IOX_KEY, reqStr);*/
	        			
	        			ppGDAO.commit();
	        			ppGDAO.destroy();
	        			
	        			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
	        		    ec.redirect("http://sattrakservices.com/PaymentPortal/faces/index.xhtml?req=" + pt.getTransRef()); //reqStrEncrypted
	        		}
	        		catch(Exception ex)
	        		{
	        			ex.printStackTrace();
	        			
	        			try{
	        				ppGDAO.destroy();
	        			} catch(Exception ig){}
	        			
	        			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: ", "Failed to initiate navigation to online payment portal! Please try again or contact support if error persists.");
	                    FacesContext.getCurrentInstance().addMessage(null, msg);
	                    
	                    GeneralDAO gDAO2 = new GeneralDAO();
	                    try
	                    {
		                    ct.setStatus("FAILED");
		                    ct.setConfirmationInfo("Failed to initiate navigation to online payment portal");
		                    cp.setStatus("FAILED");
		                    
		                    gDAO2.startTransaction();
		                    gDAO2.update(ct);
		                    gDAO2.update(cp);
		                    gDAO2.commit();
	                    }
	                    catch(Exception ig){}
	                    gDAO2.destroy();
	        		}
	        	}
	        	else
	        	{
	        		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "Un-supported payment option!");
	                FacesContext.getCurrentInstance().addMessage(null, msg);
	        	}
    		}
    		catch(Exception ex)
    		{
    			gDAO.rollback();
    			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "Could not save payment: Message: " + ex.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
    		}
    		
    		try{
    		gDAO.destroy(); // just incase
    		} catch(Exception ig){}
        }
        else
        {
        	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "Please select a valid product, and payment option!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        
        return ret;
    }
    
    public String initEdit(String page)
    {
    	return page+"?faces-redirect=true";
    }
    
    @SuppressWarnings("unchecked")
	public void rescheduleInstallation()
    {
    	if(getSelectedInstallation() != null && getSelInstallationPoint() != null && getInstallation_dt() != null && 
				getSlot_id() > 0)
		{
			GeneralDAO gDAO = new GeneralDAO();
			
			getSelectedInstallation().setInstaller(getSelInstallationPoint());
			getSelectedInstallation().setBooked_dt(getInstallation_dt());
			
			Object slot = gDAO.find(Slot.class, getSlot_id());
			if(slot != null)
			{
				getSelectedInstallation().setSlot((Slot)slot);
				Calendar c = Calendar.getInstance();
				c.setTime(getSelectedInstallation().getBooked_dt());
				c.set(Calendar.HOUR_OF_DAY, getSelectedInstallation().getSlot().getStart_hour());
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				getSelectedInstallation().setStart_dt(c.getTime());
				c.add(Calendar.HOUR_OF_DAY, getSelectedInstallation().getSlot().getAmount_of_hours());
				getSelectedInstallation().setEnd_dt(c.getTime());
			}
			
			Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and e.cancel=:cancel and e.booked_dt=:booked_dt and e.slot=:slot");
			q.setParameter("installer", getSelectedInstallation().getInstaller());
			q.setParameter("booked_dt", getSelectedInstallation().getBooked_dt());
			q.setParameter("slot", getSelectedInstallation().getSlot());
			q.setParameter("cancel", false);
			
			boolean slot_taken = false;
			int slot_used_size = 0;
			Object qret = gDAO.search(q, 0);
			if(qret != null)
			{
				List<InstallerLocationJobSchedule> list = (List<InstallerLocationJobSchedule>)qret;
				slot_used_size = list.size();
				if(slot_used_size > 0)
					slot_taken = true;
			}
			if(!slot_taken || getSelInstallationPoint().getMaxMultiSlotInstallation() == 0 || getSelInstallationPoint().getMaxMultiSlotInstallation() > slot_used_size)
			{
				gDAO.startTransaction();
				gDAO.update(getSelectedInstallation());
				try
				{
					gDAO.commit();
					
					sendEmail(new String[]{getSelectedInstallation().getCustomer().getUser().getUsername()}, "Installation Reschedule on RMS", getJobRescheduleEmailMessage(getSelectedInstallation().getCustomer().getFirstname(), getSelectedInstallation()));
					sendSMS(getSelectedInstallation().getCustomer().getPhoneNo(), getJobRescheduleSMSMessage(getSelectedInstallation().getCustomer().getFirstname(), getSelectedInstallation()));
					
					sendEmail(new String[]{getSelectedInstallation().getInstaller().getUser().getUsername()}, "Installation Reschedule on RMS", getJobRescheduleInstallerEmailMessage(getSelectedInstallation()));
					
					// send notification to support
					AppConfiguration currectAppConfig = getCurrentAppConfig();
					if(currectAppConfig != null && currectAppConfig.getCustomerSupportEmail() != null && currectAppConfig.getCustomerSupportEmail().trim().length() > 0)
					{
						String[] supportEmails = currectAppConfig.getCustomerSupportEmail().split(",");
						sendEmail(supportEmails, "Installation Reschedule on RMS", getJobRescheduleSupportEmailMessage(getSelectedInstallation()));
					}
					
					setSlot_id(0);
					setInstallation_dt(null);
					setSelInstallationPoint(null);
					setSlots(null);
					setSelectedInstallation(null);
					setMySchedules_cus(null);
					
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success: ", "Booking updated successfully!");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				catch(Exception ex)
				{
					gDAO.rollback();
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: ", "Message: " + ex.getMessage() + "!");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}
			else
			{
				setSlots(null);
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "The slot you requested is filled! Please select a different slot!");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			gDAO.destroy();
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: ", "Please input the valid details for this edit!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
    }
    
    @SuppressWarnings("unchecked")
	public void bookInstallation()
    {
    	GeneralDAO gDAO = new GeneralDAO();
		CustomerProduct cp = null;
		if(getCp_id() > 0)
		{
			Object cpObj = gDAO.find(CustomerProduct.class, getCp_id());
			if(cpObj != null)
				cp = (CustomerProduct)cpObj;
		}
		if(cp != null)
		{
			InstallerLocationJobSchedule booking = new InstallerLocationJobSchedule();
			booking.setCustomer(cp.getCustomer());
			booking.setBooked_dt(getInstallation_dt());
			
			Object slot = gDAO.find(Slot.class, getSlot_id());
			if(slot != null)
			{
				booking.setSlot((Slot)slot);
				Calendar c = Calendar.getInstance();
				c.setTime(booking.getBooked_dt());
				c.set(Calendar.HOUR_OF_DAY, booking.getSlot().getStart_hour());
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				booking.setStart_dt(c.getTime());
				c.add(Calendar.HOUR_OF_DAY, booking.getSlot().getAmount_of_hours());
				booking.setEnd_dt(c.getTime());
			}
			
			if(getSelInstallationPoint() != null && booking.getCustomer() != null && booking.getSlot() != null && 
					booking.getBooked_dt() != null)
			{
				Calendar todaymax = Calendar.getInstance();
				todaymax.set(Calendar.HOUR_OF_DAY, todaymax.getMaximum(Calendar.HOUR_OF_DAY));
				todaymax.set(Calendar.MINUTE, todaymax.getMaximum(Calendar.MINUTE));
				todaymax.set(Calendar.SECOND, todaymax.getMaximum(Calendar.SECOND));
				todaymax.set(Calendar.MILLISECOND, todaymax.getMaximum(Calendar.MILLISECOND));
				
				if(booking.getBooked_dt().after(todaymax.getTime()))
				{
					booking.setJobCode(generateReference());
					booking.setPaid(true);
					booking.setPaymentType(cp.getPurchaseTransaction().getPayMode());
					booking.setCheckedIn(false);
					booking.setCompleted(false);
					booking.setCost(cp.getPurchasedAmount());
					booking.setCreatedBy(sessionUser);
					booking.setCrt_dt(new Date());
					booking.setInstaller(getSelInstallationPoint());
					booking.setProductBooked(cp.getProductBooked());
					// this booking will require customer service to confirm it if the product is a special product
					booking.setRequire_confirmation(cp.getProductBooked().isSpecialProduct());
					
					Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and e.cancel=:cancel and e.booked_dt=:booked_dt and e.slot=:slot");
					q.setParameter("installer", booking.getInstaller());
					q.setParameter("booked_dt", booking.getBooked_dt());
					q.setParameter("slot", booking.getSlot());
					q.setParameter("cancel", false);
					
					boolean slot_taken = false;
					int slot_used_size = 0;
					Object qret = gDAO.search(q, 0);
					if(qret != null)
					{
						List<InstallerLocationJobSchedule> list = (List<InstallerLocationJobSchedule>)qret;
						slot_used_size = list.size();
						if(slot_used_size > 0)
							slot_taken = true;
					}
					if(!slot_taken || getSelInstallationPoint().getMaxMultiSlotInstallation() == 0 || getSelInstallationPoint().getMaxMultiSlotInstallation() > slot_used_size)
					{
						gDAO.startTransaction();
						gDAO.save(booking);
						
						cp.setJob(booking);
						cp.setStatus("BOOKED");
						gDAO.update(cp);
						
						try {
							Notification n = new Notification();
							n.setCrt_dt(new Date());
							n.setNotified(false);
							n.setSubject("Installation Scheduled");
							n.setPage_url("dashboard");
							n.setUser(booking.getCustomer().getUser());
							n.setMessage("Installation with Job Code: " + booking.getJobCode() + " has been scheduled for you.");
							
							gDAO.save(n);
							
							gDAO.commit();
							
							sendEmail(new String[]{booking.getCustomer().getUser().getUsername()}, "Installation Schedule on AutoLife", getJobScheduleEmailMessage(booking.getCustomer().getFirstname(), booking));
							sendSMS(booking.getCustomer().getPhoneNo(), getJobScheduleSMSMessage(booking.getCustomer().getFirstname(), booking));
							
							sendEmail(new String[]{booking.getInstaller().getUser().getUsername()}, "Installation Schedule on AutoLife", getJobScheduleInstallerEmailMessage(booking));
							
							// send notification to support
							AppConfiguration currectAppConfig = getCurrentAppConfig();
							if(currectAppConfig != null && currectAppConfig.getCustomerSupportEmail() != null && currectAppConfig.getCustomerSupportEmail().trim().length() > 0)
							{
								String[] supportEmails = currectAppConfig.getCustomerSupportEmail().split(",");
								sendEmail(supportEmails, "Installation Schedule on RMS", getJobScheduleSupportEmailMessage(booking));
							}
							
							setSlots(null);
							setSelInstallationPoint(null);
							setCp_id(0);setInstallation_point_id(0);setSlot_id(0);
							setInstallation_dt(null);setMySchedules_cus(null);
							
							FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success: ", "Purchase installation booked successfully!");
				            FacesContext.getCurrentInstance().addMessage(null, msg);
						} catch(Exception ex){
							gDAO.rollback();
							FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "Message: " + ex.getMessage() + "!");
				            FacesContext.getCurrentInstance().addMessage(null, msg);
						}
					}
					else
					{
						setSlots(null);
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "The slot you requested is filled! Please select a different slot!");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}
				else
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "Your book date must be from tomorrow upwards!");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "All fields with the '*' sign are required!");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "Could not identify the purchase!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		gDAO.destroy();
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

    public Customer getSessionCustomer() {
        return sessionCustomer;
    }

    public void setSessionCustomer(Customer sessionCustomer) {
        this.sessionCustomer = sessionCustomer;
    }

    public long getCtry_id() {
        return ctry_id;
    }

    public void setCtry_id(long ctry_id) {
        this.ctry_id = ctry_id;
    }

    public long getState_id() {
        return state_id;
    }

    public void setState_id(long state_id) {
        this.state_id = state_id;
    }

    public long getLga_id() {
        return lga_id;
    }

    public void setLga_id(long lga_id) {
        this.lga_id = lga_id;
    }
    
    @SuppressWarnings("unchecked")
    public List<Country> getCountries() {
        if(countries == null)
        {
            GeneralDAO gDAO = new GeneralDAO();
            Object all = gDAO.findAll("Country");
            if(all != null)
                countries = (List<Country>)all;
            gDAO.destroy();
        }
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    @SuppressWarnings("unchecked")
    public List<State> getStates() {
        if(states == null)
        {
            GeneralDAO gDAO = new GeneralDAO();
            Object all = gDAO.findAll("State");
            if(all != null)
                states = (List<State>)all;
            gDAO.destroy();
        }
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }
    
    @SuppressWarnings("unchecked")
	public void onStateChange()
    {
        if(state_id > 0L)
        {
            GeneralDAO gDAO = new GeneralDAO();

            Hashtable<String, Object> params = new Hashtable<String, Object>();
            params.put("state.id", getState_id());

            Object ret = gDAO.search("LGA", params);
            if(ret != null)
                lgas = (List<LGA>)ret;
            gDAO.destroy();
        }
        else
            lgas = new ArrayList<LGA>();
    }

    public List<LGA> getLgas() {
        return lgas;
    }

    public void setLgas(List<LGA> lgas) {
        this.lgas = lgas;
    }

	@SuppressWarnings("unchecked")
	public List<CustomerProduct> getMyProducts() {
		if(myProducts == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from CustomerProduct e where e.customer=:customer and (e.status=:active or e.status=:expired) order by e.booked_dt desc");
			q.setParameter("customer", getSessionCustomer());
			q.setParameter("active", "ACTIVE");
			q.setParameter("expired", "EXPIRED");
			
			Object list = gDAO.search(q, 0);
			if(list != null)
				myProducts = (List<CustomerProduct>)list;
			gDAO.destroy();
		}
		return myProducts;
	}

	public void setMyProducts(List<CustomerProduct> myProducts) {
		this.myProducts = myProducts;
	}

	@SuppressWarnings("unchecked")
	public List<InstallerLocationJobSchedule> getMySchedules_cus() {
		if(mySchedules_cus == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.customer=:customer and e.cancel=:cancel and e.completed=:completed order by e.booked_dt desc");
			q.setParameter("customer", getSessionCustomer());
			q.setParameter("cancel", false);
			q.setParameter("completed", false);
			
			Object list = gDAO.search(q, 0);
			if(list != null)
				mySchedules_cus = (List<InstallerLocationJobSchedule>)list;
			gDAO.destroy();
		}
		return mySchedules_cus;
	}

	public void setMySchedules_cus(
			List<InstallerLocationJobSchedule> mySchedules_cus) {
		this.mySchedules_cus = mySchedules_cus;
	}
    
	public InstallerLocationJobSchedule getSelectedInstallation() {
		return selectedInstallation;
	}

	public void setSelectedInstallation(
			InstallerLocationJobSchedule selectedInstallation) {
		this.selectedInstallation = selectedInstallation;
	}

	@SuppressWarnings("unchecked")
	public List<ProductType> getPtypes() {
		if(ptypes == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Object all = gDAO.findAll("ProductType");
			if(all != null)
				ptypes = (List<ProductType>)all;
			gDAO.destroy();
		}
		return ptypes;
	}

	public void setPtypes(List<ProductType> ptypes) {
		this.ptypes = ptypes;
	}
	
	@SuppressWarnings("unchecked")
	public List<Product> getProducts() {
		boolean research = false;
		if(products == null || (products != null && products.size() == 0))
			research = true;
		else if(products != null && products.size() > 0)
		{
			if(getPtype_id() > 0)
			{
				for(int i=0; i<products.size(); i++)
				{
					if(products.get(i).getType() != null && products.get(i).getType().getId().longValue() == getPtype_id());
					else
					{
						research = true;
						break;
					}
				}
			}
		}
		if(research)
		{
			GeneralDAO gDAO = new GeneralDAO();
			
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			if(getPtype_id() > 0)
				params.put("type.id", getPtype_id());
			
			params.put("active", true);
			
			Object ret = gDAO.search("Product", params);
			if(ret != null)
			{
				products = (List<Product>)ret;
			}
			gDAO.destroy();
		}
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public long getPtype_id() {
		return ptype_id;
	}

	public void setPtype_id(long ptype_id) {
		this.ptype_id = ptype_id;
	}

	public long getProduct_id() {
		return product_id;
	}

	public void setProduct_id(long product_id) {
		this.product_id = product_id;
	}

	public Product getProductToBuy() {
		return productToBuy;
	}

	public void setProductToBuy(Product productToBuy) {
		this.productToBuy = productToBuy;
	}

	public String getProductToBuyRef() {
		return productToBuyRef;
	}

	public void setProductToBuyRef(String productToBuyRef) {
		this.productToBuyRef = productToBuyRef;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}
	
	public InstallerLocation getSelInstallationPoint() {
		return selInstallationPoint;
	}

	public void setSelInstallationPoint(InstallerLocation selInstallationPoint) {
		this.selInstallationPoint = selInstallationPoint;
	}

	@SuppressWarnings("unchecked")
	public List<InstallerLocation> getInstallersByLGA() {
		boolean research = false;
		if(installersByLGA == null || (installersByLGA != null && installersByLGA.size() == 0))
			research = true;
		else if(installersByLGA != null && installersByLGA.size() > 0)
		{
			if(getLga_id() > 0)
				for(InstallerLocation e : installersByLGA)
				{
					if(e.getLga() != null && e.getLga().getId().longValue() == getLga_id());
					else
					{
						research = true;
						break;
					}
				}
		}
		if(research)
		{
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("lga.id", getLga_id());
			
			GeneralDAO gDAO = new GeneralDAO();
			Object obj = gDAO.search("InstallerLocation", params);
			if(obj != null)
				installersByLGA = (List<InstallerLocation>)obj;
			gDAO.destroy();
		}
		return installersByLGA;
	}

	public void setInstallersByLGA(List<InstallerLocation> installersByLGA) {
		this.installersByLGA = installersByLGA;
	}
	
	@SuppressWarnings("unchecked")
	public List<Slot> getSlots() {
		boolean research = true; // false;
		/*if((slots == null || (slots != null && slots.size() == 0)) && getInstallation_dt() != null)
			research = true;*/
		if(research)
		{
			List<Slot> allSlots = null;
			GeneralDAO gDAO = new GeneralDAO();
			Object all = gDAO.findAll("Slot");
			if(all != null)
				allSlots = (List<Slot>)all;
			
			if(allSlots != null && getSelInstallationPoint() != null)
			{
				Hashtable<String, Object> params = new Hashtable<String, Object>();
				params.put("booked_dt", getInstallation_dt());
				params.put("installer", getSelInstallationPoint());
				params.put("cancel", false);
				Object obj = gDAO.search("InstallerLocationJobSchedule", params);
				if(obj != null)
				{
					slots = new ArrayList<Slot>();
					List<InstallerLocationJobSchedule> bookings = (List<InstallerLocationJobSchedule>)obj;
					for(Slot e : allSlots)
					{
						boolean exists = false;
						int slot_used_size = 0;
						for(InstallerLocationJobSchedule bk : bookings)
						{
							if(bk.getSlot().getId().longValue() == e.getId().longValue())
							{
								exists = true;
								slot_used_size += 1;
							}
						}
						if(!exists || getSelInstallationPoint().getMaxMultiSlotInstallation() == 0 || getSelInstallationPoint().getMaxMultiSlotInstallation() > slot_used_size)
							slots.add(e);
					}
				}
			}
			gDAO.destroy();
		}
		return slots;
	}

	public void setSlots(List<Slot> slots) {
		this.slots = slots;
	}
	
	@SuppressWarnings("unchecked")
	public List<CustomerProduct> getUninstalledPurchases() {
		GeneralDAO gDAO = new GeneralDAO();
		Query q = gDAO.createQuery("Select e from CustomerProduct e where e.status=:status and e.customer=:customer order by e.productBooked desc");
		q.setParameter("status", "NOT-INSTALLED");
		q.setParameter("customer", getSessionCustomer());
		
		Object obj = gDAO.search(q, 0);
		if(obj != null)
			uninstalledPurchases = (List<CustomerProduct>)obj;
		gDAO.destroy();
		return uninstalledPurchases;
	}

	public void setUninstalledPurchases(List<CustomerProduct> uninstalledPurchases) {
		this.uninstalledPurchases = uninstalledPurchases;
	}

	public long getCp_id() {
		return cp_id;
	}

	public void setCp_id(long cp_id) {
		this.cp_id = cp_id;
	}

	public long getInstallation_point_id() {
		return installation_point_id;
	}

	public void setInstallation_point_id(long installation_point_id) {
		this.installation_point_id = installation_point_id;
		selInstallationPoint = null;
		if(installation_point_id > 0)
		{
			if(selInstallationPoint != null && selInstallationPoint.getId().longValue() == installation_point_id);
			else
			{
				GeneralDAO gDAO = new GeneralDAO();
				Object installer = gDAO.find(InstallerLocation.class, installation_point_id);
				if(installer != null)
					selInstallationPoint = (InstallerLocation)installer;
				gDAO.destroy();
			}
		}
	}

	public long getSlot_id() {
		return slot_id;
	}

	public void setSlot_id(long slot_id) {
		this.slot_id = slot_id;
	}

	public Date getInstallation_dt() {
		return installation_dt;
	}

	public void setInstallation_dt(Date installation_dt) {
		this.installation_dt = installation_dt;
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

	public List<CustomerTransaction> getCtList() {
		return ctList;
	}

	public void setCtList(List<CustomerTransaction> ctList) {
		this.ctList = ctList;
	}

	public static String getHowToPayMessage(final String tranRef, final CustomerProduct cp)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("<html><body>");
        sb.append("<p><strong>Dear ").append(cp.getCustomer().getFirstname()).append("!,</strong></p>");
        sb.append("<p>Thank you for your interest in our product. Please follow the steps below to make your payment: -</p>");
        sb.append("<p><ol><li>Confirm that the product details below is the product you want to purchase: -<br/>");
        sb.append("Product: ").append(cp.getProductBooked().getDetails()).append("<br/>Amount: N ").append(cp.getPurchasedAmount()).append("</li>");
        sb.append("<li>Make a deposit of the above amount in any of <strong>Zenith Bank</strong> branches nationwide into our account with below details: -<br />Transaction Ref: ").append(tranRef).append("<br/ >Account Name: Sattrak Telematics Plc<br/>Account Number: 1234567890</li>");
        sb.append("<li>For faster response and confirmation of your payment, please send a notification of your deposit to our support team at support@sattrakservices.com</li>");
        sb.append("<li>You will be notified on confirmation of your payment.<li></ol></p>");
        sb.append("<p>Please contact us for further assistance</p>");
        sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
        sb.append("</body></html>");
        
        return sb.toString();
    }
	
	public static String getJobScheduleEmailMessage(final String cusFirstName, final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Dear ").append(cusFirstName).append(",</strong></p>");
		sb.append("<p>").append("You have successfully placed a booking on our AutoLife site with the below details: -").append("</p>");
		sb.append("<p><table border=0>");
		sb.append("<tr><td>Job Code</td><td>").append(job.getJobCode()).append("</td></tr>");
		sb.append("<tr><td>Installation Date and Time</td><td>").append(job.getBooked_dt()).append(" ").append(job.getSlot().getName()).append("</td></tr>");
		sb.append("<tr><td>Product</td><td>").append(job.getProductBooked().getDetails()).append("</td></tr>");
		sb.append("<tr><td>Installation Point</td><td>").append(job.getInstaller().getCompanyName()).append("</td></tr>");
		sb.append("<tr><td>Installation Point Address</td><td>").append(job.getInstaller().getAddress()).append("</td></tr>");
		sb.append("</table></p>");
		sb.append("<p>Please remember the above details and ensure you are at the installation point at least 15 minutes before your booked time.</p>");
		sb.append("<p>For enquires, please send mail to support@sattrakservices.com. Please include the Job Code above in your email.</p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
	}
	
	public static String getJobScheduleSMSMessage(final String cusFirstName, final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Dear ").append(cusFirstName).append(", you have successfully placed a booking with Job Code: ").append(job.getJobCode()).append(" on the AutoLife platform. More details has been sent to your email.");
		return sb.toString();
	}
	
	public static String getJobScheduleSupportEmailMessage(final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Hello,</strong></p>");
		sb.append("<p>").append("An installation schedule as been placed on AutoLife with below details: -").append("</p>");
		sb.append("<p><table border=0>");
		sb.append("<tr><td>Job Code</td><td>").append(job.getJobCode()).append("</td></tr>");
		sb.append("<tr><td>Customer</td><td>").append(job.getCustomer().getFirstname()).append(" ").append(job.getCustomer().getLastname()).append("</td></tr>");
		sb.append("<tr><td>Installation Date and Time</td><td>").append(job.getBooked_dt()).append(" ").append(job.getSlot().getName()).append("</td></tr>");
		sb.append("<tr><td>Product</td><td>").append(job.getProductBooked().getDetails()).append("</td></tr>");
		sb.append("<tr><td>Installation Point</td><td>").append(job.getInstaller().getCompanyName()).append("</td></tr>");
		sb.append("<tr><td>Installation Point Address</td><td>").append(job.getInstaller().getAddress()).append("</td></tr>");
		sb.append("</table></p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
	}
	
	public static String getJobScheduleInstallerEmailMessage(final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Hello,</strong></p>");
		sb.append("<p>").append("An installation schedule as been placed with your company as the installation point. See below details: -").append("</p>");
		sb.append("<p><table border=0>");
		sb.append("<tr><td>Job Code</td><td>").append(job.getJobCode()).append("</td></tr>");
		sb.append("<tr><td>Installation Date and Time</td><td>").append(job.getBooked_dt()).append(" ").append(job.getSlot().getName()).append("</td></tr>");
		sb.append("<tr><td>Product</td><td>").append(job.getProductBooked().getDetails()).append("</td></tr>");
		sb.append("<tr><td>Customer</td><td>").append(job.getCustomer().getFirstname()).append(" ").append(job.getCustomer().getLastname()).append("</td></tr>");
		sb.append("</table></p>");
		sb.append("<p>For enquires, please send mail to support@sattrakservices.com. Please include the Job Code above in your email.</p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
	}
	
	public static String getJobRescheduleEmailMessage(final String cusFirstName, final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Dear ").append(cusFirstName).append(",</strong></p>");
		sb.append("<p>").append("Your booking with Job Code: ").append(job.getJobCode()).append(" has been rescheduled with the below details: -").append("</p>");
		sb.append("<p><table border=0>");
		sb.append("<tr><td>Job Code</td><td>").append(job.getJobCode()).append("</td></tr>");
		sb.append("<tr><td>Installation Date and Time</td><td>").append(job.getBooked_dt()).append(" ").append(job.getSlot().getName()).append("</td></tr>");
		sb.append("<tr><td>Product</td><td>").append(job.getProductBooked().getDetails()).append("</td></tr>");
		sb.append("<tr><td>Installation Point</td><td>").append(job.getInstaller().getCompanyName()).append("</td></tr>");
		sb.append("<tr><td>Installation Point Address</td><td>").append(job.getInstaller().getAddress()).append("</td></tr>");
		sb.append("</table></p>");
		sb.append("<p>Please remember the above details and ensure you are at the installation point at least 15 minutes before your booked time.</p>");
		sb.append("<p>For enquires, please send mail to support@sattrakservices.com. Please include the Job Code above in your email.</p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
	}
	
	public static String getJobRescheduleSMSMessage(final String cusFirstName, final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Dear ").append(cusFirstName).append(", your booking with Job Code: ").append(job.getJobCode()).append(" has been rescheduled on the RMS platform. More details has been sent to your email.");
		return sb.toString();
	}
	
	public static String getJobRescheduleInstallerEmailMessage(final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Hello,</strong></p>");
		sb.append("<p>").append("An installation has been rescheduled with your company as the installation point. See below details: -").append("</p>");
		sb.append("<p><table border=0>");
		sb.append("<tr><td>Job Code</td><td>").append(job.getJobCode()).append("</td></tr>");
		sb.append("<tr><td>Installation Date and Time</td><td>").append(job.getBooked_dt()).append(" ").append(job.getSlot().getName()).append("</td></tr>");
		sb.append("<tr><td>Product</td><td>").append(job.getProductBooked().getDetails()).append("</td></tr>");
		sb.append("<tr><td>Customer</td><td>").append(job.getCustomer().getFirstname()).append(" ").append(job.getCustomer().getLastname()).append("</td></tr>");
		sb.append("</table></p>");
		sb.append("<p>For enquires, please send mail to support@sattrakservices.com. Please include the Job Code above in your email.</p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
	}
	
	public static String getJobRescheduleSupportEmailMessage(final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Hello,</strong></p>");
		sb.append("<p>").append("An installation has been rescheduled on RMS with below details: -").append("</p>");
		sb.append("<p><table border=0>");
		sb.append("<tr><td>Job Code</td><td>").append(job.getJobCode()).append("</td></tr>");
		sb.append("<tr><td>Customer</td><td>").append(job.getCustomer().getFirstname()).append(" ").append(job.getCustomer().getLastname()).append("</td></tr>");
		sb.append("<tr><td>Installation Date and Time</td><td>").append(job.getBooked_dt()).append(" ").append(job.getSlot().getName()).append("</td></tr>");
		sb.append("<tr><td>Product</td><td>").append(job.getProductBooked().getDetails()).append("</td></tr>");
		sb.append("<tr><td>Installation Point</td><td>").append(job.getInstaller().getCompanyName()).append("</td></tr>");
		sb.append("<tr><td>Installation Point Address</td><td>").append(job.getInstaller().getAddress()).append("</td></tr>");
		sb.append("</table></p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
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
    
    public void sendSMS(final String numbers, final String message)
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
    
    public String getRandomNumber()
	{
		return ""+new Random().nextInt(10000);
	}
    
    private String generateReference()
	{
        return "PRTN" + (sessionUser.getPartner() != null ? sessionUser.getPartner().getId().longValue() : "") + "-" + System.currentTimeMillis() + (new SecureRandom().nextInt(999) + 1);
    }
    
    @SuppressWarnings("deprecation")
	private byte[] generateInvoiceForCustomerPurchase(CustomerProduct cp)
	{
		byte[] data = null;
		
		if(cp != null)
		{
			Document document = new Document();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try
			{
				PdfWriter writer = PdfWriter.getInstance(document, baos);
				writer.setPageEvent(new HeaderFooter());
				writer.setBoxSize("footer", new Rectangle(36, 54, 559, 788));
				if (!document.isOpen())
				{
					document.open();
				}
				document.setPageSize(PageSize.A4);
				document.addAuthor("AutoLife");
				document.addCreationDate();
				document.addCreator("AutoLife");
				document.addSubject("Invoice");
				document.addTitle("Purchase Invoice");
				
				PdfPTable headerTable = new PdfPTable(3);
				
				ServletContext servletContext = (ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext();
				String logo = servletContext.getRealPath("") + File.separator + "images" + File.separator + "sattrak-logo.png";
				PdfPCell c = new PdfPCell(Image.getInstance(logo));
				c.setBorder(0);
				c.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				headerTable.addCell(c);
				
				BaseFont helvetica = null;
				try
				{
					helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
				}
				catch (Exception e)
				{}
				Font font = new Font(helvetica, 16, Font.NORMAL|Font.BOLD);
				c = new PdfPCell(new Paragraph("INVOICE", font));
				c.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				c.setBorder(0);
				headerTable.addCell(c);
				
				font = new Font(helvetica, 10, Font.NORMAL|Font.BOLD);
				c = new PdfPCell(new Paragraph("TRANSACTION REF. NO.: " + cp.getPurchaseTranRef(), font));
				c.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
				c.setBorder(0);
				headerTable.addCell(c);
				
				document.add(headerTable);
				
				font = new Font(helvetica, 12, Font.NORMAL|Font.BOLD);
				Paragraph p = new Paragraph("DETAILS", font);
				p.setAlignment(Paragraph.ALIGN_CENTER);
				document.add(p);
				
				PdfPTable pdfTable = new PdfPTable(3);
				
				font = new Font(helvetica, 8, Font.BOLDITALIC);
				pdfTable.addCell(new Paragraph("INITIATED DATE", font));
				pdfTable.addCell(new Paragraph("PRODUCT", font));
				pdfTable.addCell(new Paragraph("AMOUNT", font));
				font = new Font(helvetica, 8, Font.NORMAL);
				pdfTable.addCell(new Paragraph(cp.getPurchaseTransaction().getTranInitDate().toLocaleString(), font));
				pdfTable.addCell(new Paragraph(cp.getProductBooked().getDetails(), font));
				pdfTable.addCell(new Paragraph(""+cp.getPurchasedAmount(), font));
				document.add(pdfTable);
				
				document.close();
				
				data = baos.toByteArray();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		return data;
	}
    
    static class HeaderFooter extends PdfPageEventHelper
	{
		public void onEndPage(PdfWriter writer, Document document)
		{
			Rectangle rect = writer.getBoxSize("footer");
			BaseFont bf_times;
			try
			{
				bf_times = BaseFont.createFont(BaseFont.TIMES_ROMAN, "Cp1252", false);
				Font font = new Font(bf_times, 9);
				ColumnText.showTextAligned(writer.getDirectContent(),
					    Element.ALIGN_RIGHT, new Phrase(String.format("%d", writer.getPageNumber()), font),  rect.getRight(),
					    rect.getBottom() - 18, 0);
				ColumnText.showTextAligned(writer.getDirectContent(),
						Element.ALIGN_LEFT, new Phrase("Copyright " + Calendar.getInstance().get(Calendar.YEAR), font), 
						rect.getLeft(), rect.getBottom() - 18, 0);
			}
			catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
    
    @SuppressWarnings("unchecked")
	public AppConfiguration getCurrentAppConfig()
	{
		GeneralDAO gDAO = new GeneralDAO();
		AppConfiguration appConfig = null;
		Object appConfigListObj = gDAO.findAll("AppConfiguration");
		if(appConfigListObj != null)
		{
			List<AppConfiguration> appConfigList = (List<AppConfiguration>)appConfigListObj;
			if(appConfigList != null && appConfigList.size() > 0)
			for(AppConfiguration e : appConfigList)
				appConfig = e;
		}
		gDAO.destroy();
		
		return appConfig;
	}
}
