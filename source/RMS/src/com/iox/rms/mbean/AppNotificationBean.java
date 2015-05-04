package com.iox.rms.mbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.iox.rms.app.model.AppNotification;

@ManagedBean(name = "appNotifBean", eager = true)
@SessionScoped
public class AppNotificationBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private List<AppNotification> appNotifications = new ArrayList<AppNotification>();
	
	public AppNotificationBean()
	{}
	
	public boolean clear(String type)
	{
		boolean ret = false;
		for(int i=0; i<appNotifications.size(); i++)
		{
			AppNotification e = appNotifications.get(i);
			if(e.getType().equalsIgnoreCase(type) && !e.isShowed())
			{
				e.setShowed(true);
				ret = true;
				break;
			}
		}
		return ret;
	}

	public List<AppNotification> getAppNotifications() {
		return appNotifications;
	}

	public void setAppNotifications(List<AppNotification> appNotifications) {
		this.appNotifications = appNotifications;
	}
	
}
