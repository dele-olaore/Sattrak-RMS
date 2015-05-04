package com.iox.pp.mbean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.json.JSONObject;

import com.dexter.common.util.Crypto;
import com.iox.pp.dao.GeneralDAO;
import com.iox.pp.model.PaymentTransaction;

/**
*
* @author oladele
*/
@ManagedBean(name = "paymentBean", eager = true)
@SessionScoped
public class PaymentBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String externalAppCode, transactionRef, productName;
	private String customerName, customerEmail, customerPhone;
	private Date tranInitDate;
	private BigDecimal amount;
	
	private String paymentGateway;
	
	public PaymentBean()
	{}
	
	public String gotoPaymentGateway()
	{
		String ret = "index?faces-redirect=true";
		
		if(getPaymentGateway() != null && getPaymentGateway().equals("Zenith GlobalPay"))
		{
			ret = "connector?faces-redirect=true";
		}
		System.out.println("ret: " + ret);
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public boolean parseRequest(String encryptedReq)
	{
		boolean ret = false;
		PaymentTransaction pt = null;
		
		if(transactionRef != null)
			encryptedReq = transactionRef;
		
		if(encryptedReq != null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			
			Query q = gDAO.createQuery("Select e from PaymentTransaction e where e.transRef=:transRef");
			q.setParameter("transRef", encryptedReq);
			
			Object listObj = gDAO.search(q, 0);
			if(listObj != null)
			{
				List<PaymentTransaction> list = (List<PaymentTransaction>)listObj;
				for(PaymentTransaction e : list)
					pt = e;
			}
			gDAO.destroy();
			
			if(pt != null)
			{
				ret = true;
				setExternalAppCode(pt.getAppId());
				setTransactionRef(pt.getTransRef());
				setProductName(pt.getProductName());
				setCustomerName(pt.getCustomerName());
				setCustomerEmail(pt.getCustomerEmail());
				setCustomerPhone(pt.getCustomerPhone());
				setTranInitDate(pt.getTranDate());
				setAmount(new BigDecimal(pt.getAmount()));
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: ", "Could not identify the transaction!");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			/*try {
				String decryptedReq = Crypto.decrypt256(Crypto.VANSO_KEY, encryptedJSON);
				JSONObject req = new JSONObject(decryptedReq);
				
				setExternalAppCode(req.getString("externalAppCode"));
				setTransactionRef(req.getString("transactionRef"));
				setCustomerName(req.getString("customerName"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		
		return ret;
	}

	public String getExternalAppCode() {
		return externalAppCode;
	}

	public void setExternalAppCode(String externalAppCode) {
		this.externalAppCode = externalAppCode;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public Date getTranInitDate() {
		return tranInitDate;
	}

	public void setTranInitDate(Date tranInitDate) {
		this.tranInitDate = tranInitDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPaymentGateway() {
		return paymentGateway;
	}

	public void setPaymentGateway(String paymentGateway) {
		this.paymentGateway = paymentGateway;
	}
	
	/*public void logout() throws ServletException, IOException
	{
	    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
	    ((HttpServletRequest)ec.getRequest()).logout();
	    ec.invalidateSession();
	    ec.redirect("http://example.com/anothercontext/logout");
	}*/
	
}
