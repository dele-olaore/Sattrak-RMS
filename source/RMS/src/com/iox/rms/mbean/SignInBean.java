package com.iox.rms.mbean;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.jasypt.util.text.BasicTextEncryptor;

import com.dexter.common.util.Emailer;
import com.dexter.common.util.Hasher;
import com.iox.rms.app.model.AppNotification;
import com.iox.rms.dao.GeneralDAO;
import com.iox.rms.model.Customer;
import com.iox.rms.model.InstallerLocation;
import com.iox.rms.model.PartnerPersonnel;
import com.iox.rms.model.SalesAgent;
import com.iox.rms.model.TradePartner;
import com.iox.rms.model.User;

@ManagedBean(name = "signinBean", eager = true)
@RequestScoped
public class SignInBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String username;
	private String password, kaptchaReceived;
	private long partner_id;
	
	@ManagedProperty("#{userBean}")
	private UserBean userBean;
	
	@ManagedProperty("#{appNotifBean}")
	private AppNotificationBean appNotifBean;
	
	public boolean isValidPasswordResetLink(String encryptedUsername, String encryptedDt)
	{
		appNotifBean.getAppNotifications().clear();
		boolean ret = false;
		if(encryptedUsername != null && encryptedDt != null)
		{
			try
			{
				BasicTextEncryptor bencryptor = new BasicTextEncryptor();
				bencryptor.setPassword("rms");
				username = bencryptor.decrypt(encryptedUsername);
				String dt = bencryptor.decrypt(encryptedDt);
				long dt_exp = Long.parseLong(dt);
				long now = System.currentTimeMillis();
				
				if(encryptedUsername != null && encryptedUsername.trim().length() > 0 && dt_exp >= now)
					ret = true;
				else
				{
					AppNotification an = new AppNotification();
					an.setType("ERROR");
					an.setSubject("Invalid Link");
					an.setMessage("The password reset attributes are either invalid or has expired. Please initiate the password reset process afresh!");
					appNotifBean.getAppNotifications().add(an);
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
		appNotifBean.getAppNotifications().clear();
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
					params.put("partner.id", getPartner_id());
					
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
								
								AppNotification an = new AppNotification();
								an.setType("SUCCESS");
								an.setSubject("Password Reset");
								an.setMessage("Your password was reset successfully. Please go to your email to get your new password!");
								appNotifBean.getAppNotifications().add(an);
								
								StringBuilder sb = new StringBuilder();
								sb.append("<html><body><strong>Hello,</strong>");
								sb.append("<p>Your password has been reset to \"").append(newPassword).append("\"</p>");
								sb.append("<br/><p>Regards<br/>RMS Platform</p>");
								sb.append("</body></html>");
								
								Emailer.sendEmail("rms@sattrakservices.com", new String[]{username}, "Password Reset", sb.toString());
							}
							catch(Exception ex)
							{
								gDAO.rollback();
								AppNotification an = new AppNotification();
								an.setType("ERROR");
								an.setSubject("Failed");
								an.setMessage("Message: " + gDAO.getMessage());
								appNotifBean.getAppNotifications().add(an);
							}
						}
						else
						{
							AppNotification an = new AppNotification();
							an.setType("ERROR");
							an.setSubject("Invalid Account");
							an.setMessage("Your account was not found. Please ensure you entered your valid username/email!");
							appNotifBean.getAppNotifications().add(an);
						}
					}
					else
					{
						AppNotification an = new AppNotification();
						an.setType("ERROR");
						an.setSubject("Invalid Account");
						an.setMessage("Your account was not found. Please ensure you entered your valid username/email!");
						appNotifBean.getAppNotifications().add(an);
					}
					gDAO.destroy();
				}
				else
				{
					AppNotification an = new AppNotification();
					an.setType("ERROR");
					an.setSubject("Unknow Captcha");
					an.setMessage("Please enter the valid secret code!");
					appNotifBean.getAppNotifications().add(an);
				}
			}
			else
			{
				AppNotification an = new AppNotification();
				an.setType("ERROR");
				an.setSubject("Unknow Captcha");
				an.setMessage("Please enter the valid secret code!");
				appNotifBean.getAppNotifications().add(an);
			}
		}
		else
		{
			AppNotification an = new AppNotification();
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("All fields are required!");
			appNotifBean.getAppNotifications().add(an);
		}
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void sendPasswordResetLink()
	{
		appNotifBean.getAppNotifications().clear();
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
					params.put("partner.id", getPartner_id());
					
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
							bencryptor.setPassword("rms");
							String encryptedUsername = bencryptor.encrypt(username);
							String encryptedExpiredLong = bencryptor.encrypt(""+expired);
							
							StringBuilder sb = new StringBuilder();
							sb.append("<html><body><strong>Hello,</strong>");
							sb.append("<p>Please click the link below to reset your password. The link will expire by " + dt.toLocaleString() + ".</p>");
							sb.append("<p><a href=\"http://sattrakservices.com/rms/faces/reset_password.xhtml?u=").append(URLEncoder.encode(encryptedUsername)).append("&dt=").append(URLEncoder.encode(encryptedExpiredLong)).append("\">Reset My Password</a></p>");
							sb.append("<br/><p>If you can not click the above link, please copy and paste this text to your browser: http://sattrakservices.com/rms/faces/reset_password.xhtml?u=").append(URLEncoder.encode(encryptedUsername)).append("&dt=").append(URLEncoder.encode(encryptedExpiredLong)).append("</p><br/><p>Regards<br/>RMS Platform</p>");
							sb.append("</body></html>");
							
							Emailer.sendEmail("rms@sattrakservices.com", new String[]{username}, "Password Reset", sb.toString());
							
							AppNotification an = new AppNotification();
							an.setType("SUCCESS");
							an.setSubject("Password Reset");
							an.setMessage("Instruction on how to reset your password as been sent to your email!");
							appNotifBean.getAppNotifications().add(an);
						}
						else
						{
							AppNotification an = new AppNotification();
							an.setType("ERROR");
							an.setSubject("Invalid Account");
							an.setMessage("Your account was not found. Please ensure you entered your valid username/email!");
							appNotifBean.getAppNotifications().add(an);
						}
					}
					else
					{
						AppNotification an = new AppNotification();
						an.setType("ERROR");
						an.setSubject("Invalid Account");
						an.setMessage("Your account was not found. Please ensure you entered your valid username/email!");
						appNotifBean.getAppNotifications().add(an);
					}
					gDAO.destroy();
				}
				else
				{
					AppNotification an = new AppNotification();
					an.setType("ERROR");
					an.setSubject("Invalid Secret Code");
					an.setMessage("Please enter the valid secret code!");
					appNotifBean.getAppNotifications().add(an);
				}
			}
			else
			{
				AppNotification an = new AppNotification();
				an.setType("ERROR");
				an.setSubject("Unknow Captcha");
				an.setMessage("Please enter the valid secret code!");
				appNotifBean.getAppNotifications().add(an);
			}
		}
		else
		{
			AppNotification an = new AppNotification();
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("All fields are required!");
			appNotifBean.getAppNotifications().add(an);
		}
	}
	
	@SuppressWarnings("unchecked")
	public String signin()
	{
		appNotifBean.getAppNotifications().clear();
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
					if(u.getPartner() != null && u.getPartner().isActive())
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
									{
										userBean.setSessionCustomer(e);
									}
								}
								ret = "dashboard?faces-redirect=true";
							}
							else if(u.getType().equals("INSTALLER"))
							{
								// fetch the installer here
								params = new Hashtable<String, Object>();
								params.put("user", u);
								Object foundObjs = gDAO.search("InstallerLocation", params);
								if(foundObjs != null)
								{
									List<InstallerLocation> list = (List<InstallerLocation>)foundObjs;
									for(InstallerLocation e : list)
									{
										userBean.setSessionInstaller(e);
									}
								}
								ret = "dashboard?faces-redirect=true";
							}
							else if(u.getType().equals("SALES-AGENT"))
							{
								// fetch the installer here
								params = new Hashtable<String, Object>();
								params.put("user", u);
								Object foundObjs = gDAO.search("SalesAgent", params);
								if(foundObjs != null)
								{
									List<SalesAgent> list = (List<SalesAgent>)foundObjs;
									for(SalesAgent e : list)
									{
										userBean.setSessionSalesAgent(e);
									}
								}
								ret = "dashboard?faces-redirect=true";
							}
							else if(u.getType().equals("TRADE-PARTNER"))
							{
								// fetch the installer here
								params = new Hashtable<String, Object>();
								params.put("user", u);
								Object foundObjs = gDAO.search("TradePartner", params);
								if(foundObjs != null)
								{
									List<TradePartner> list = (List<TradePartner>)foundObjs;
									for(TradePartner e : list)
									{
										userBean.setSessionTradePartner(e);
									}
								}
								ret = "dashboard?faces-redirect=true";
							}
							//else if(u.getType().equals("PERSONNEL") || u.getType().equalsIgnoreCase("FINANCIAL REPORTS") ||
							//		u.getType().equals("ADMINISTRATOR") || u.getType().equals("CONTROL ROOM"))
							else
							{
								// fetch the installer here
								params = new Hashtable<String, Object>();
								params.put("user", u);
								Object foundObjs = gDAO.search("PartnerPersonnel", params);
								if(foundObjs != null)
								{
									List<PartnerPersonnel> list = (List<PartnerPersonnel>)foundObjs;
									for(PartnerPersonnel e : list)
									{
										userBean.setSessionPP(e);
									}
								}
								
								if(userBean.getSessionPP() != null)
									ret = "dashboard?faces-redirect=true";
								else
								{
									AppNotification an = new AppNotification();
									an.setType("ERROR");
									an.setSubject("Authentication Failed");
									an.setMessage("Could not determine your account type. Please contact the system administrator!");
									appNotifBean.getAppNotifications().add(an);
								}
							}
							
							if(ret.indexOf("dashboard") >= 0)
							{
								AppNotification an = new AppNotification();
								an.setType("SUCCESS");
								an.setSubject("Authentication Success");
								an.setMessage("Welcome " + u.getUsername() + "!");
								appNotifBean.getAppNotifications().add(an);
							}
						}
						else
						{
							AppNotification an = new AppNotification();
							an.setType("ERROR");
							an.setSubject("Authentication Failed");
							an.setMessage("Your account is in-active!");
							appNotifBean.getAppNotifications().add(an);
						}
					}
					else
					{
						AppNotification an = new AppNotification();
						an.setType("ERROR");
						an.setSubject("Authentication Failed");
						an.setMessage("Your partner account is in-active!");
						appNotifBean.getAppNotifications().add(an);
					}
				}
				else
				{
					AppNotification an = new AppNotification();
					an.setType("ERROR");
					an.setSubject("Authentication Failed");
					an.setMessage("Invalid password!");
					appNotifBean.getAppNotifications().add(an);
				}
			}
			else
			{
				AppNotification an = new AppNotification();
				an.setType("ERROR");
				an.setSubject("Authentication Failed");
				an.setMessage("Invalid username!");
				appNotifBean.getAppNotifications().add(an);
			}
		}
		else
		{
			AppNotification an = new AppNotification();
			an.setType("ERROR");
			an.setSubject("Authentication Failed");
			an.setMessage("Invalid username!");
			appNotifBean.getAppNotifications().add(an);
		}
		gDAO.destroy();
		
		return ret;
	}
	
	public void redirector(String url)
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try
		{
			ec.redirect(url);
		}
		catch(IOException ex)
		{}
	}
	
	public void forwarder(String url)
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		try
		{
			fc.getExternalContext().dispatch(url);
		}
		catch (Exception ex)
		{}
	}
	
	public String signout()
	{
		appNotifBean.getAppNotifications().clear();
		String ret = "signin?faces-redirect=true";
		
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		
		AppNotification an = new AppNotification();
		an.setType("SUCCESS");
		an.setSubject("Sign Out");
		an.setMessage("You have successfully signed out!");
		appNotifBean.getAppNotifications().add(an);
		
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

	public String getKaptchaReceived() {
		return kaptchaReceived;
	}

	public void setKaptchaReceived(String kaptchaReceived) {
		this.kaptchaReceived = kaptchaReceived;
	}

	public long getPartner_id() {
		partner_id = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("partner_id"));
		return partner_id;
	}

	public void setPartner_id(long partner_id) {
		this.partner_id = partner_id;
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
	
	private String generatePassword()
	{
		String pwd = "";
		for(int i=0; i<8; i++)
		{
			pwd += ""+new Random().nextInt(10);
		}
		return pwd;
	}
}
