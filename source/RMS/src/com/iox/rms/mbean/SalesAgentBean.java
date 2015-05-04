package com.iox.rms.mbean;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.persistence.Query;

import com.iox.rms.dao.GeneralDAO;
import com.iox.rms.model.InstallerLocationJobSchedule;

@ManagedBean(name = "salesAgentBean")
@SessionScoped
public class SalesAgentBean implements Serializable
{
	private static final long serialVersionUID = 1L;

	private List<InstallerLocationJobSchedule> mybookings;
	
	@ManagedProperty("#{appNotifBean}")
	private AppNotificationBean appNotifBean;
	@ManagedProperty("#{userBean}")
	private UserBean userBean;
	
	public SalesAgentBean()
	{}

	@SuppressWarnings("unchecked")
	public List<InstallerLocationJobSchedule> getMybookings() {
		GeneralDAO gDAO = new GeneralDAO();
		Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.createdBy = :me order by e.crt_dt desc");
		q.setParameter("me", userBean.getSessionUser());
		
		Object list = gDAO.search(q, 0);
		if(list != null)
			mybookings = (List<InstallerLocationJobSchedule>)list;
		gDAO.destroy();
		
		return mybookings;
	}

	public void setMybookings(List<InstallerLocationJobSchedule> mybookings) {
		this.mybookings = mybookings;
	}

	public AppNotificationBean getAppNotifBean() {
		return appNotifBean;
	}

	public void setAppNotifBean(AppNotificationBean appNotifBean) {
		this.appNotifBean = appNotifBean;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	
}
