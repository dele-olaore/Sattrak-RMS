/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iox.rms.mbean;

import com.dexter.common.util.Emailer;
import com.dexter.common.util.Hasher;
import com.iox.rms.dao.GeneralDAO;
import com.iox.rms.model.Customer;
import com.iox.rms.model.User;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.faces.bean.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 *
 * @author oladele
 */
@ManagedBean(name = "signinBean", eager = true)
@RequestScoped
public class LoginBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String username;
    private String password;
    private long partner_id;
    private String kaptchaReceived;
    
    @ManagedProperty("#{userBean}")
    private UserBean userBean;
    
    public boolean isValidPasswordResetLink(String encryptedUsername, String encryptedDt)
	{
		boolean ret = false;
		if(encryptedUsername != null && encryptedDt != null)
		{
			try
			{
				BasicTextEncryptor bencryptor = new BasicTextEncryptor();
				bencryptor.setPassword("AutoLifeSattrak");
				username = bencryptor.decrypt(encryptedUsername);
				String dt = bencryptor.decrypt(encryptedDt);
				long dt_exp = Long.parseLong(dt);
				long now = System.currentTimeMillis();
				
				if(encryptedUsername != null && encryptedUsername.trim().length() > 0 && dt_exp >= now)
					ret = true;
				else
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Link: ", "The password reset attributes are either invalid or has expired. Please initiate the password reset process afresh!");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}
			catch(Exception ex)
			{}
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public void sendPasswordReset()
	{
		FacesContext context = FacesContext.getCurrentInstance();
		if(kaptchaReceived != null && kaptchaReceived.trim().length() > 0 && username != null && username.trim().length() > 0)
		{
			ExternalContext ext = context.getExternalContext();
			Map<String, Object> session = ext.getSessionMap();
			Object kaptchaObj = session.get(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
			if(kaptchaObj != null)
			{
				String kaptchaExpected = kaptchaObj.toString();
				if(kaptchaReceived.equalsIgnoreCase(kaptchaExpected))
				{
					GeneralDAO gDAO = new GeneralDAO();
					
					Hashtable<String, Object> params = new Hashtable<String, Object>();
					params.put("username", getUsername());
					
					Object foundUsers = gDAO.search("User", params);
					if(foundUsers != null)
					{
						User u = null;
						List<User> users = (List<User>)foundUsers;
						if(users != null && users.size() > 0)
						{
							for(User e : users)
								u = e;
						}
						
						if(u != null)
						{
							String newPassword = generatePassword();
							
							u.setPassword(Hasher.getHashValue(newPassword));
							gDAO.startTransaction();
							gDAO.update(u);
							try
							{
								gDAO.commit();
								
								FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Password Reset: ", "Your password was reset successfully. Please go to your email to get your new password!");
			                    FacesContext.getCurrentInstance().addMessage(null, msg);
								
								StringBuilder sb = new StringBuilder();
								sb.append("<html><body><strong>Hello,</strong>");
								sb.append("<p>Your password has been reset to \"").append(newPassword).append("\"</p>");
								sb.append("<br/><p>Regards<br/>AutoLife Platform</p>");
								sb.append("</body></html>");
								
								Emailer.sendEmail("rms@sattrakservices.com", new String[]{username}, "Password Reset", sb.toString());
							}
							catch(Exception ex)
							{
								gDAO.rollback();
								FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "Message: " + gDAO.getMessage());
			                    FacesContext.getCurrentInstance().addMessage(null, msg);
							}
						}
						else
						{
							FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Account: ", "Your account was not found. Please ensure you entered your valid username/email!");
		                    FacesContext.getCurrentInstance().addMessage(null, msg);
						}
					}
					else
					{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Account: ", "Your account was not found. Please ensure you entered your valid username/email!");
	                    FacesContext.getCurrentInstance().addMessage(null, msg);
					}
					gDAO.destroy();
				}
				else
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unknow Captcha: ", "Please enter the valid secret code!");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unknow Captcha: ", "Please enter the valid secret code!");
                FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "All fields are required!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void sendPasswordResetLink()
	{
		FacesContext context = FacesContext.getCurrentInstance();
		if(kaptchaReceived != null && kaptchaReceived.trim().length() > 0 && username != null && username.trim().length() > 0)
		{
			ExternalContext ext = context.getExternalContext();
			Map<String, Object> session = ext.getSessionMap();
			Object kaptchaObj = session.get(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
			if(kaptchaObj != null)
			{
				String kaptchaExpected = kaptchaObj.toString();
				if(kaptchaReceived.equalsIgnoreCase(kaptchaExpected))
				{
					GeneralDAO gDAO = new GeneralDAO();
					
					Hashtable<String, Object> params = new Hashtable<String, Object>();
					params.put("username", getUsername());
					
					Object foundUsers = gDAO.search("User", params);
					if(foundUsers != null)
					{
						User u = null;
						List<User> users = (List<User>)foundUsers;
						if(users != null && users.size() > 0)
						{
							for(User e : users)
								u = e;
						}
						
						if(u != null)
						{
							long now = System.currentTimeMillis();
							long oneday = 1000L*60L*60L*24L;
							long expired = now+oneday;
							
							Date dt = new Date(expired);
							
							// TODO: Send the password reset link to the user's email
							BasicTextEncryptor bencryptor = new BasicTextEncryptor();
							bencryptor.setPassword("AutoLifeSattrak");
							String encryptedUsername = bencryptor.encrypt(username);
							String encryptedExpiredLong = bencryptor.encrypt(""+expired);
							
							StringBuilder sb = new StringBuilder();
							sb.append("<html><body><strong>Hello,</strong>");
							sb.append("<p>Please click the link below to reset your password. The link will expire by " + dt.toLocaleString() + ".</p>");
							sb.append("<p><a href=\"http://sattrakservices.com/AutoLife/faces/resetPassword.xhtml?u=").append(URLEncoder.encode(encryptedUsername)).append("&dt=").append(URLEncoder.encode(encryptedExpiredLong)).append("\">Reset My Password</a></p>");
							sb.append("<br/><p>If you can not click the above link, please copy and paste this text to your browser: http://sattrakservices.com/AutoLife/faces/resetPassword.xhtml?u=").append(URLEncoder.encode(encryptedUsername)).append("&dt=").append(URLEncoder.encode(encryptedExpiredLong)).append("</p><br/><p>Regards<br/>AutoLife Platform</p>");
							sb.append("</body></html>");
							
							Emailer.sendEmail("rms@sattrakservices.com", new String[]{username}, "Password Reset", sb.toString());
							
							FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Password Reset: ", "Instruction on how to reset your password as been sent to your email!");
				            FacesContext.getCurrentInstance().addMessage(null, msg);
						}
						else
						{
							FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Account: ", "Your account was not found. Please ensure you entered your valid username/email!");
				            FacesContext.getCurrentInstance().addMessage(null, msg);
						}
					}
					else
					{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Account: ", "Your account was not found. Please ensure you entered your valid username/email!");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}
					gDAO.destroy();
				}
				else
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Secret Code: ", "Please enter the valid secret code!");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unknow Captcha: ", "Please enter the valid secret code!");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "All fields are required!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
    
    @SuppressWarnings("unchecked")
	public String signin()
    {
        String ret = "signin";
	
        GeneralDAO gDAO = new GeneralDAO();
        
        Hashtable<String, Object> params = new Hashtable<String, Object>();
        params.put("username", getUsername());
        //params.put("partner.id", getPartner_id());
        
        Object foundUsers = gDAO.search("User", params);
        if(foundUsers != null)
        {
            User u = null;
            List<User> users = (List<User>)foundUsers;
            if(users != null && users.size() > 0)
            {
                for(User e : users)
                    u = e;
            }

            if(u != null)
            {
                if(u.getPassword().equals(Hasher.getHashValue(getPassword())))
                {
                    if(u.isActive())
                    {
                        userBean.setSessionPartner(u.getPartner());
                        userBean.setSessionUser(u);
                        if(u.getType().equals("CUSTOMER"))
                        {
                            // fetch the customer here
                            params = new Hashtable<String, Object>();
                            params.put("user", u);
                            Object foundObjs = gDAO.search("Customer", params);
                            if(foundObjs != null)
                            {
                                List<Customer> list = (List<Customer>)foundObjs;
                                for(Customer e : list)
                                    userBean.setSessionCustomer(e);
                            }
                            if(userBean.getSessionCustomer() != null)
                                ret = "profile?faces-redirect=true";
                            else
                            {
                                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: ", "Unrecognized user account type!");
                                FacesContext.getCurrentInstance().addMessage(null, msg);
                            }
                        }
                        else
                        {
                            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: ", "Unrecognized user account type!");
                            FacesContext.getCurrentInstance().addMessage(null, msg);
                        }
                    }
                    else
                    {
                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: ", "User account disabled!");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                    }
                }
                else
                {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: ", "Authentication failed!");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            }
            else
            {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: ", "User does not exist.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        else
        {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: ", "User does not exist.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        gDAO.destroy();
        
        return ret;
    }
    
    private String generatePassword()
	{
		String pwd = "";
		for(int i=0; i<8; i++)
		{
			pwd += ""+new Random().nextInt(10);
		}
		return pwd;
	}
    
    /**
    * 
    * @param url
    */
    public void redirector(String url)
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try 
        {
            ec.redirect(url);
        } 
        catch(IOException ex)
        {
            //Logger.getLogger(getClass().getName(), null).log(Level.SEVERE, null, ex);
        }
    }

    /*
    * 
    */
    public void forwarder(String url) 
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        try
        {
            fc.getExternalContext().dispatch(url);
        } 
        catch (Exception ex) 
        {
            //Logger.getLogger(getClass().getName(), null).log(Level.SEVERE, null, ex);
        }
    }
	
    public String logout()
    {
        String ret = "index?faces-redirect=true";

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        try {
        	((HttpServletRequest)ec.getRequest()).logout();
        } catch(Exception ig){}
	    ec.invalidateSession();

        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success: ", "Log out successful.");
        FacesContext.getCurrentInstance().addMessage(null, msg);

        return ret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getPartner_id() {
        //partner_id = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("partner_id"));
        return partner_id;
    }

    public void setPartner_id(long partner_id) {
        this.partner_id = partner_id;
    }

    public String getKaptchaReceived() {
		return kaptchaReceived;
	}

	public void setKaptchaReceived(String kaptchaReceived) {
		this.kaptchaReceived = kaptchaReceived;
	}

	public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
    
}
