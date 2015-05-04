/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iox.rms.mbean;

import com.dexter.common.util.Emailer;
import com.dexter.common.util.Hasher;
import com.dexter.common.util.SMSGateway;
import com.iox.rms.dao.GeneralDAO;
import com.iox.rms.model.Country;
import com.iox.rms.model.Customer;
import com.iox.rms.model.LGA;
import com.iox.rms.model.Notification;
import com.iox.rms.model.Partner;
import com.iox.rms.model.Role;
import com.iox.rms.model.State;
import com.iox.rms.model.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import javax.faces.bean.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

/**
 *
 * @author oladele
 */
@ManagedBean(name = "regBean", eager = true)
@SessionScoped
public class RegisterBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Customer customer;
    private User cusUser;
    
    @ManagedProperty("#{userBean}")
    private UserBean userBean;
    
    private String confirm_password;
    private long ctry_id, state_id, lga_id, partner_id;
    private boolean agreed;
    
    private List<Country> countries;
    private List<State> states;
    private List<LGA> lgas;
    
    @SuppressWarnings("unchecked")
	public String register()
    {
    	String ret = "register-user";
        if(isAgreed())
        {
	        if(getCustomer().getFirstname() != null && getCustomer().getFirstname().trim().length() > 0 &&
	            getCustomer().getLastname() != null && getCustomer().getFirstname().trim().length() > 0 &&
	            getCusUser().getUsername() != null && getCusUser().getUsername().trim().length() > 0 &&
	            getCusUser().getPassword() != null && getCusUser().getPassword().trim().length() > 0)
	        {
	            if(getCusUser().getPassword().equals(getConfirm_password()))
	            {
	                if(validatePassword(getCusUser().getPassword()))
	                {
	                    if(getCusUser().getUsername().indexOf("@") > 0 && getCusUser().getUsername().indexOf(".") > 0)
	                    {
	                        getCusUser().setPassword(Hasher.getHashValue(getConfirm_password()));
	                        GeneralDAO gDAO = new GeneralDAO();
							
	                        Object country = gDAO.find(Country.class, getCtry_id());
	                        if(country != null)
	                            getCustomer().setCountry((Country)country);
	                        Object state = gDAO.find(State.class, getState_id());
	                        if(state != null)
	                            getCustomer().setState((State)state);
	                        Object lga = gDAO.find(LGA.class, getLga_id());
	                        if(lga != null)
	                            getCustomer().setLga((LGA)lga);
	                        
	                        Partner p = null;
	                        Object pobj = gDAO.find(Partner.class, getPartner_id());
	                        if(pobj != null)
	                            p = (Partner)pobj;
				
	                        //getCustomer().setCreatedBy(userBean.getSessionUser());
	                        getCustomer().setPartner(p);
	                        getCustomer().setCrt_dt(new Date());
	                        
	                        Hashtable<String, Object> params = new Hashtable<String, Object>();
	                        params.put("name", "CUSTOMER");
	                        Object rolesObj = gDAO.search("Role", params);
	                        if(rolesObj != null)
	                        {
	                            List<Role> roles = (List<Role>)rolesObj;
	                            for(Role e : roles)
	                                getCusUser().setRole(e);
	                            getCusUser().setActive(true);
	                            //getCusUser().setCreatedBy(userBean.getSessionUser());
	                            getCusUser().setCrt_dt(new Date());
	                            getCusUser().setPartner(p);
	                            getCusUser().setType("CUSTOMER");
	
	                            gDAO.startTransaction();
	                            gDAO.save(getCusUser());
	                            
	                            getCusUser().setCreatedBy(getCusUser());
	                            gDAO.update(getCusUser());
	
	                            getCustomer().setUser(getCusUser());
	                            getCustomer().setCreatedBy(getCusUser());
	                            gDAO.save(getCustomer());
	                            
	                            getCustomer().setUniqueID(generateCustomerReference(getCustomer().getId()));
	                            gDAO.update(getCustomer());
	                            try
	                            {
	                                Notification n = new Notification();
	                                n.setCrt_dt(new Date());
	                                n.setNotified(false);
	                                n.setSubject("Welcome");
	                                n.setPage_url("dashboard");
	                                n.setUser(getCusUser());
	                                n.setMessage("Welcome to RMS!!!");
	
	                                gDAO.save(n);
	
	                                gDAO.commit();
	
	                                sendEmail(new String[]{getCusUser().getUsername()}, "Welcome to RMS", getSignUpEmailMessage(getCustomer().getFirstname(), getCusUser().getUsername(), getConfirm_password(), getCustomer().getUniqueID()));
	                                sendSMS(getCustomer().getPhoneNo(), getSignUpSMSMessage(getCustomer()));
	
	                                setCustomer(null);
	                                setCusUser(null);
	                                
	                                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success: ", "Account registered successfully! You can now proceed to login!");
	                                FacesContext.getCurrentInstance().addMessage(null, msg);
	                                
	                                ret = "register?faces-redirect=true";
	                            }
	                            catch(Exception ex)
	                            {
	                                gDAO.rollback();
	                                String message = ex.getMessage();
	                                if(message.indexOf("MySQLIntegrityConstraintViolationException")>=0)
	                                    message = "Account already exists. Please confirm you have not registered before with this email address!";
	                                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: ", "Message: " + message + "!");
	                                FacesContext.getCurrentInstance().addMessage(null, msg);
	                            }
	                        }
	                        else
	                        {
	                            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "Could not get the customer role!");
	                            FacesContext.getCurrentInstance().addMessage(null, msg);
	                        }
	                        gDAO.destroy();
	                    }
	                    else
	                    {
	                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "Username is not in a valid email format!");
	                        FacesContext.getCurrentInstance().addMessage(null, msg);
	                    }
	                }
	                else
	                {
	                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "Password does not match the password policy. Please view the help page for more information!");
	                    FacesContext.getCurrentInstance().addMessage(null, msg);
	                }
	            }
	            else
	            {
	                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "Password fields were not the same!");
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
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "Please agree to the terms and conditions!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        return ret;
    }

    public Customer getCustomer() {
        if(customer == null)
            customer = new Customer();
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public User getCusUser() {
        if(cusUser == null)
            cusUser = new User();
        return cusUser;
    }

    public void setCusUser(User cusUser) {
        this.cusUser = cusUser;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public String getConfirm_password() {
        return confirm_password;
    }

    public void setConfirm_password(String confirm_password) {
        this.confirm_password = confirm_password;
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

    public long getPartner_id() {
        partner_id = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("partner_id"));
        return partner_id;
    }

    public void setPartner_id(long partner_id) {
        this.partner_id = partner_id;
    }

    public boolean isAgreed() {
        return agreed;
    }

    public void setAgreed(boolean agreed) {
        this.agreed = agreed;
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
    
    private String generateCustomerReference(long cusID)
    {
        return "CUS-" + getPartner_id() + "-" + cusID;
    }
    
    public static String getSignUpEmailMessage(final String cusFirstName, String username, String password, String uniqueId)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("<html><body>");
        sb.append("<p><strong>Welcome ").append(cusFirstName).append("!,</strong></p>");
        sb.append("<p>").append("We are pleased to welcome you to our AutoLife platform.").append("</p>");
        sb.append("<p>Your Unique ID is ").append(uniqueId).append("</p>");
        sb.append("<p>Your authentication credentials are below: -</p>");
        sb.append("<p>Username: ").append(username).append("<br/>").append("Password: ").append(password).append("</p><br/>");
        sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
        sb.append("</body></html>");
        return sb.toString();
    }
    
    public static String getSignUpSMSMessage(final Customer cus)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(cus.getFirstname()).append(", welcome to our AutoLife platform. Your Unique ID is ").append(cus.getUniqueID()).append(". Your authentication credentials has been sent to your registered email address.");
        return sb.toString();
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
}
