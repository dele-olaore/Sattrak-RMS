package com.iox.rms.mbean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.persistence.Query;

import com.dexter.common.util.Emailer;
import com.dexter.common.util.Hasher;
import com.dexter.common.util.SMSGateway;
import com.iox.rms.dao.GeneralDAO;
import com.iox.rms.model.AppConfiguration;
import com.iox.rms.model.CustomerProduct;
import com.iox.rms.model.InstallerLocationJobSchedule;
import com.iox.rms.model.Partner;
import com.iox.rms.model.PartnerPersonnel;
import com.iox.rms.model.Role;
import com.iox.rms.model.Slot;
import com.iox.rms.model.User;
import com.iox.rms.util.MessagesUtil;

@ManagedBean(name = "setupBean", eager=true)
@ApplicationScoped
public class SetupBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	Timer cancelTimer, todayLateTimer, checkinReminderTimer, renewalReminderTimer;
	TimerTask cancelTask, todayLateTask, checkInReminderTask, renewalReminderTask;
	
	public SetupBean()
	{
		if(!checkIfSysSetup())
			setupSys();
		
		cancelTimer = new Timer();
		cancelTask = new TimerTask()
		{
			@Override
			public void run() {
				checkJobsToBeCancelled();
				
				checkYesterdayLateJobs();
				
				checkTwoDaysLateJobs();
			}
		};
		
		todayLateTimer = new Timer();
		todayLateTask = new TimerTask()
		{	
			@Override
			public void run() {
				checkTodayLateJobs();
			}
		};
		
		checkinReminderTimer = new Timer();
		checkInReminderTask = new TimerTask()
		{
			@Override
			public void run() {
				checkInRemindJobs();
			}
		};
		
		renewalReminderTimer = new Timer();
		renewalReminderTask = new TimerTask()
		{
			@Override
			public void run() {
				checkRenewals();
			}
		};
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, 5); // 5 minutes past the hour
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
		//TODO: Un-comment below
		cancelTimer.scheduleAtFixedRate(cancelTask, c.getTime(), 1000*60*60*24);
		
		c = Calendar.getInstance();
		c.set(Calendar.MINUTE, 10); // hourly but at every 10 minutes past the hour
		//TODO: Un-comment below
		todayLateTimer.scheduleAtFixedRate(todayLateTask, c.getTime(), 1000*60*60*1); // every hour
		
		AppConfiguration appConfig = getAppConfig();
		c = Calendar.getInstance();
		c.set(Calendar.MINUTE, (appConfig != null && appConfig.getCheckInReminderMinute() > 0) ? appConfig.getCheckInReminderMinute() : 30);
		//TODO: Un-comment below
		checkinReminderTimer.scheduleAtFixedRate(checkInReminderTask, c.getTime(), 1000*60*60*1); // every hour
		
		c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 10);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		//TODO: Un-comment below
		renewalReminderTimer.scheduleAtFixedRate(renewalReminderTask, c.getTime(), 1000*60*60*24);
	}
	
	@PreDestroy
	public void destroy()
	{
		try
		{
			if(cancelTimer != null)
				cancelTimer.cancel();
			
			cancelTimer = null;
		}catch(Exception ex){}
		
		try
		{
			if(todayLateTimer != null)
				todayLateTimer.cancel();
			
			todayLateTimer = null;
		}catch(Exception ex){}
		
		try
		{
			if(checkinReminderTimer != null)
				checkinReminderTimer.cancel();
			
			checkinReminderTimer = null;
		}catch(Exception ex){}
		
		try
		{
			if(renewalReminderTimer != null)
				renewalReminderTimer.cancel();
			
			renewalReminderTimer = null;
		}catch(Exception ex){}
	}
	
	private void setupSys()
	{
		System.out.println("Begin System Setup");
		GeneralDAO gDAO = new GeneralDAO("rms2");
		
		gDAO.startTransaction();
		
		Role r1 = new Role();
		r1.setCrt_dt(new Date());
		r1.setSysObject(true);
		r1.setName("SATTRAK ADMINISTRATOR");
		r1.setDescription("This is the administrative role for Sattrak's Administrator");
		gDAO.save(r1);
		
		Role r2 = new Role();
		r2.setCrt_dt(new Date());
		r2.setSysObject(true);
		r2.setName("PARTNER ADMINISTRATOR");
		r2.setDescription("This is the administrative role for every partner on the platform");
		gDAO.save(r2);
		
		Role r3 = new Role();
		r3.setCrt_dt(new Date());
		r3.setSysObject(true);
		r3.setName("SALES-AGENT");
		r3.setDescription("This is the role for every sales agent on the platform");
		gDAO.save(r3);
		
		Role r4 = new Role();
		r4.setCrt_dt(new Date());
		r4.setSysObject(true);
		r4.setName("CUSTOMER");
		r4.setDescription("This is the role for every customer on the platform");
		gDAO.save(r4);
		
		Role r5 = new Role();
		r5.setCrt_dt(new Date());
		r5.setSysObject(true);
		r5.setName("INSTALLER");
		r5.setDescription("This is the role for every installer on the platform");
		gDAO.save(r5);
		
		Role r6 = new Role();
		r6.setCrt_dt(new Date());
		r6.setSysObject(true);
		r6.setName("PERSONNEL");
		r6.setDescription("This is the role for every personnel on the platform");
		gDAO.save(r6);
		
		Role r7 = new Role();
		r7.setCrt_dt(new Date());
		r7.setSysObject(true);
		r7.setName("FINANCIAL REPORTS");
		r7.setDescription("This is the role for the financial department on the platform");
		gDAO.save(r7);
		
		Role r8 = new Role();
		r8.setCrt_dt(new Date());
		r8.setSysObject(true);
		r8.setName("CONTROL ROOM");
		r8.setDescription("This is the role for the control/technical department on the platform");
		gDAO.save(r8);
		
		Role r9 = new Role();
		r9.setCrt_dt(new Date());
		r9.setSysObject(true);
		r9.setName("SALES EXECUTIVE");
		r9.setDescription("This is the role for the control/technical department on the platform");
		gDAO.save(r9);
		
		Partner sattrak = new Partner();
		sattrak.setContact_email("support@sattrakservices.com");
		sattrak.setContact_firstname("Banjo");
		sattrak.setContact_lastname("Olajide");
		sattrak.setContact_phoneno("");
		sattrak.setCrt_dt(new Date());
		sattrak.setName("Sattrak Telematics Limited");
		sattrak.setSattrak(true);
		gDAO.save(sattrak);
		
		User sattrakAdmin = new User();
		sattrakAdmin.setCrt_dt(new Date());
		sattrakAdmin.setPartner(sattrak);
		sattrakAdmin.setActive(true);
		sattrakAdmin.setPassword(Hasher.getHashValue("password"));
		sattrakAdmin.setRole(r1);
		sattrakAdmin.setType("PERSONNEL");
		sattrakAdmin.setUsername("administrator");
		gDAO.save(sattrakAdmin);
		
		PartnerPersonnel pp = new PartnerPersonnel();
		pp.setCrt_dt(new Date());
		pp.setFirstname("Banjo");
		pp.setLastname("Olajide");
		pp.setPartner(sattrak);
		pp.setUser(sattrakAdmin);
		gDAO.save(pp);
		
		AppConfiguration appConfig = new AppConfiguration();
		appConfig.setCancelDueDays(3);
		appConfig.setRefundChargePercent(10);
		appConfig.setCheckInReminderMinute(30);
		appConfig.setCrt_dt(new Date());
		appConfig.setCustomerSupportEmail("support@sattrakservices.com");
		appConfig.setLast_update_dt(new Date());
		
		gDAO.save(appConfig);
		
		Slot s = new Slot();
		s.setAmount_of_hours(1);
		s.setCrt_dt(new Date());
		s.setEnd_hour(9);
		s.setName("8AM - 9AM");
		s.setStart_hour(8);
		s.setSysObject(true);
		gDAO.save(s);
		
		s = new Slot();
		s.setAmount_of_hours(1);
		s.setCrt_dt(new Date());
		s.setEnd_hour(10);
		s.setName("9AM - 10AM");
		s.setStart_hour(9);
		s.setSysObject(true);
		gDAO.save(s);
		
		s = new Slot();
		s.setAmount_of_hours(1);
		s.setCrt_dt(new Date());
		s.setEnd_hour(11);
		s.setName("10AM - 11AM");
		s.setStart_hour(10);
		s.setSysObject(true);
		gDAO.save(s);
		
		s = new Slot();
		s.setAmount_of_hours(1);
		s.setCrt_dt(new Date());
		s.setEnd_hour(12);
		s.setName("11AM - 12NOON");
		s.setStart_hour(11);
		s.setSysObject(true);
		gDAO.save(s);
		
		s = new Slot();
		s.setAmount_of_hours(1);
		s.setCrt_dt(new Date());
		s.setEnd_hour(13);
		s.setName("12NOON - 1PM");
		s.setStart_hour(12);
		s.setSysObject(true);
		gDAO.save(s);
		
		s = new Slot();
		s.setAmount_of_hours(1);
		s.setCrt_dt(new Date());
		s.setEnd_hour(14);
		s.setName("1PM - 2PM");
		s.setStart_hour(13);
		s.setSysObject(true);
		gDAO.save(s);
		
		s = new Slot();
		s.setAmount_of_hours(1);
		s.setCrt_dt(new Date());
		s.setEnd_hour(15);
		s.setName("2PM - 3PM");
		s.setStart_hour(14);
		s.setSysObject(true);
		gDAO.save(s);
		
		s = new Slot();
		s.setAmount_of_hours(1);
		s.setCrt_dt(new Date());
		s.setEnd_hour(16);
		s.setName("3PM - 4PM");
		s.setStart_hour(15);
		s.setSysObject(true);
		gDAO.save(s);
		
		s = new Slot();
		s.setAmount_of_hours(1);
		s.setCrt_dt(new Date());
		s.setEnd_hour(17);
		s.setName("4PM - 5PM");
		s.setStart_hour(16);
		s.setSysObject(true);
		gDAO.save(s);
		
		gDAO.commit();
		gDAO.destroy();
		System.out.println("System Setup Complete!");
	}
	
	private boolean checkIfSysSetup()
	{
		boolean ret = false;
		GeneralDAO gDAO = new GeneralDAO("rms2");
		
		try
		{
			Object allPartners = gDAO.findAll("Partner");
			if(allPartners != null)
			{
				@SuppressWarnings("unchecked")
				List<Partner> list = (List<Partner>)allPartners;
				if(list != null && list.size() > 0)
					ret = true;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		gDAO.destroy();
		
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	private AppConfiguration getAppConfig()
	{
		GeneralDAO gDAO = new GeneralDAO("rms2");
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
	
	@SuppressWarnings("unchecked")
	private void checkInRemindJobs()
	{
		System.out.println("Checking for job schedules to remind...");
		AppConfiguration appConfig = getAppConfig();
		
		GeneralDAO gDAO = new GeneralDAO("rms2");
		
		Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.checkedIn=:checkedIn and e.paid=:paid and e.completed=:completed and e.cancel=:cancel and e.checkInReminded=:checkInReminded and (e.start_dt between :st_dt and :end_dt)");
		q.setParameter("checkedIn", false);
		q.setParameter("paid", false);
		q.setParameter("completed", false);
		q.setParameter("cancel", false);
		q.setParameter("checkInReminded", false);
		
		Calendar c = Calendar.getInstance();
		
		c.set(Calendar.MINUTE, (appConfig != null && appConfig.getCheckInReminderMinute() > 0) ? appConfig.getCheckInReminderMinute() : 30); // 30 minutes to check in
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
		Date st = c.getTime();
		q.setParameter("st_dt", st);
		
		c.add(Calendar.HOUR_OF_DAY, 1);
		Date end = c.getTime();
		q.setParameter("end_dt", end);
		
		Object listObj = gDAO.search(q, 0);
		if(listObj != null)
		{
			List<InstallerLocationJobSchedule> list = (List<InstallerLocationJobSchedule>)listObj;
			gDAO.startTransaction();
			for(InstallerLocationJobSchedule e : list)
			{
				e.setCheckInReminded(true);
				gDAO.update(e);
				sendEmail(new String[]{e.getCustomer().getUser().getUsername()}, "Installation Reminder on RMS", MessagesUtil.getJobScheduleReminderEmailMessage(e.getCustomer().getFirstname(), e));
				sendEmail(new String[]{e.getInstaller().getUser().getUsername()}, "Installation Reminder on RMS", MessagesUtil.getJobScheduleInstallerReminderEmailMessage(e));
				
				// send notification to support
				AppConfiguration currectAppConfig = getAppConfig();
				if(currectAppConfig != null && currectAppConfig.getCustomerSupportEmail() != null && currectAppConfig.getCustomerSupportEmail().trim().length() > 0)
				{
					String[] supportEmails = currectAppConfig.getCustomerSupportEmail().split(",");
					sendEmail(supportEmails, "Installation Reminder on RMS", MessagesUtil.getJobScheduleSupportReminderEmailMessage(e));
				}
			}
			try
			{
				gDAO.commit();
			}catch(Exception ex){}
		}
		
		gDAO.destroy();
		System.out.println("Finished reminding for job schedules.");
	}
	
	@SuppressWarnings("unchecked")
	private void checkTodayLateJobs()
	{
		System.out.println("Checking for today's late job schedules to remind...");
		GeneralDAO gDAO = new GeneralDAO("rms2");
		
		Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.checkedIn=:checkedIn and e.paid=:paid and e.completed=:completed and e.cancel=:cancel and e.todayLateNotified=:todayLateNotified and e.start_dt < :dt and (e.booked_dt between :st_dt and :end_dt)");
		q.setParameter("checkedIn", false);
		q.setParameter("paid", false);
		q.setParameter("completed", false);
		q.setParameter("cancel", false);
		q.setParameter("todayLateNotified", false);
		
		Calendar c = Calendar.getInstance();
		Date dt = c.getTime();
		q.setParameter("dt", dt);
		
		c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
		Date st = c.getTime();
		q.setParameter("st_dt", st);
		
		c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
		Date end = c.getTime();
		q.setParameter("end_dt", end);
		
		Object listObj = gDAO.search(q, 0);
		if(listObj != null)
		{
			List<InstallerLocationJobSchedule> list = (List<InstallerLocationJobSchedule>)listObj;
			gDAO.startTransaction();
			for(InstallerLocationJobSchedule e : list)
			{
				e.setTodayLateNotified(true);
				gDAO.update(e);
				sendEmail(new String[]{e.getCustomer().getUser().getUsername()}, "Installation Due on RMS", MessagesUtil.getJobScheduleOverdueEmailMessage(e.getCustomer().getFirstname(), e));
				
				// send notification to support
				AppConfiguration currectAppConfig = getAppConfig();
				if(currectAppConfig != null && currectAppConfig.getCustomerSupportEmail() != null && currectAppConfig.getCustomerSupportEmail().trim().length() > 0)
				{
					String[] supportEmails = currectAppConfig.getCustomerSupportEmail().split(",");
					sendEmail(supportEmails, "Installation Due on RMS", MessagesUtil.getJobScheduleOverdueSupportEmailMessage(e));
				}
			}
			try
			{
				gDAO.commit();
			}catch(Exception ex){}
		}
		
		gDAO.destroy();
		System.out.println("Finished reminding for today's late job schedules.");
	}
	
	@SuppressWarnings("unchecked")
	private void checkYesterdayLateJobs()
	{
		System.out.println("Checking for yesterday's late job schedules to remind...");
		GeneralDAO gDAO = new GeneralDAO("rms2");
		
		Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.checkedIn=:checkedIn and e.paid=:paid and e.completed=:completed and e.cancel=:cancel and e.yesterdayLateNotified=:yesterdayLateNotified and (e.booked_dt between :st_dt and :end_dt)");
		q.setParameter("checkedIn", false);
		q.setParameter("paid", false);
		q.setParameter("completed", false);
		q.setParameter("cancel", false);
		q.setParameter("yesterdayLateNotified", false);
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
		Date st = c.getTime();
		q.setParameter("st_dt", st);
		
		c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
		Date end = c.getTime();
		q.setParameter("end_dt", end);
		
		Object listObj = gDAO.search(q, 0);
		if(listObj != null)
		{
			List<InstallerLocationJobSchedule> list = (List<InstallerLocationJobSchedule>)listObj;
			gDAO.startTransaction();
			for(InstallerLocationJobSchedule e : list)
			{
				e.setYesterdayLateNotified(true);
				gDAO.update(e);
				sendEmail(new String[]{e.getCustomer().getUser().getUsername()}, "Installation Due on RMS", MessagesUtil.getJobScheduleOverdueEmailMessage(e.getCustomer().getFirstname(), e));
			}
			try
			{
				gDAO.commit();
			}catch(Exception ex){}
		}
		
		gDAO.destroy();
		System.out.println("Finished reminding for yesterday's late job schedules.");
	}
	
	@SuppressWarnings("unchecked")
	private void checkTwoDaysLateJobs()
	{
		System.out.println("Checking for 2-days late job schedules to remind...");
		GeneralDAO gDAO = new GeneralDAO("rms2");
		
		Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.checkedIn=:checkedIn and e.paid=:paid and e.completed=:completed and e.cancel=:cancel and e.twoDaysLateNotified=:twoDaysLateNotified and (e.booked_dt between :st_dt and :end_dt)");
		q.setParameter("checkedIn", false);
		q.setParameter("paid", false);
		q.setParameter("completed", false);
		q.setParameter("cancel", false);
		q.setParameter("twoDaysLateNotified", false);
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -2);
		c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
		Date st = c.getTime();
		q.setParameter("st_dt", st);
		
		c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
		Date end = c.getTime();
		q.setParameter("end_dt", end);
		
		Object listObj = gDAO.search(q, 0);
		if(listObj != null)
		{
			List<InstallerLocationJobSchedule> list = (List<InstallerLocationJobSchedule>)listObj;
			gDAO.startTransaction();
			for(InstallerLocationJobSchedule e : list)
			{
				e.setTwoDaysLateNotified(true);
				gDAO.update(e);
				sendEmail(new String[]{e.getCustomer().getUser().getUsername()}, "Installation Due on RMS", MessagesUtil.getJobScheduleOverdueEmailMessage(e.getCustomer().getFirstname(), e));
			}
			try
			{
				gDAO.commit();
			}catch(Exception ex){}
		}
		
		gDAO.destroy();
		System.out.println("Finished reminding for 2-days late job schedules.");
	}
	
	@SuppressWarnings("unchecked")
	private void checkJobsToBeCancelled()
	{
		System.out.println("Checking for job schedules to be cancelled...");
		AppConfiguration appConfig = getAppConfig();
		GeneralDAO gDAO = new GeneralDAO("rms2");
		
		Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.checkedIn=:checkedIn and e.paid=:paid and e.cancel=:cancel and (e.booked_dt between :st and :et)");
		q.setParameter("checkedIn", false);
		q.setParameter("paid", false);
		q.setParameter("cancel", false);
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, (appConfig != null && appConfig.getCancelDueDays()>0) ? -appConfig.getCancelDueDays() : -3);
		
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
		
		q.setParameter("st", st);
		q.setParameter("et", end);
		
		Object listObj = gDAO.search(q, 0);
		if(listObj != null)
		{
			List<InstallerLocationJobSchedule> list = (List<InstallerLocationJobSchedule>)listObj;
			gDAO.startTransaction();
			for(InstallerLocationJobSchedule e : list)
			{
				e.setCancel(true);
				gDAO.update(e);
				sendEmail(new String[]{e.getCustomer().getUser().getUsername()}, "Installation Cancelled on RMS", MessagesUtil.getJobScheduleCancelEmailMessage(e.getCustomer().getFirstname(), e));
				sendEmail(new String[]{e.getInstaller().getUser().getUsername()}, "Installation Cancelled on RMS", MessagesUtil.getJobScheduleCancelInstallerEmailMessage(e));
			}
			try
			{
				gDAO.commit();
			}catch(Exception ex){}
		}
		
		gDAO.destroy();
		System.out.println("Finished cancelling job schedules.");
	}
	
	@SuppressWarnings("unchecked")
	private void checkRenewals()
	{
		System.out.println("Checking for product renewals to be alerted...");
		GeneralDAO gDAO = new GeneralDAO("rms2");
		
		Query q = gDAO.createQuery("Select e from CustomerProduct e where e.status=:status and ((e.renewal_due_dt between :st and :et) or (e.renewal_due_dt between :st2 and :et2) or (e.renewal_due_dt between :st3 and :et3))");
		q.setParameter("status", "ACTIVE");
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 1);
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
		q.setParameter("st", st);
		q.setParameter("et", end);
		
		c = Calendar.getInstance();
		c.add(Calendar.DATE, 7);
		c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
		st = c.getTime();
		c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
		end = c.getTime();
		q.setParameter("st2", st);
		q.setParameter("et2", end);
		
		c = Calendar.getInstance();
		c.add(Calendar.DATE, 3);
		c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
		st = c.getTime();
		c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
		end = c.getTime();
		q.setParameter("st3", st);
		q.setParameter("et3", end);
		
		Object listObj = gDAO.search(q, 0);
		if(listObj != null)
		{
			List<CustomerProduct> list = (List<CustomerProduct>)listObj;
			for(CustomerProduct e : list)
			{
				sendEmail(new String[]{e.getCustomer().getUser().getUsername()}, "Product Renewal Due Reminder", MessagesUtil.getProductRenewalReminderEmailMessage(e.getCustomer().getFirstname(), e));
			}
		}
		
		gDAO.destroy();
		System.out.println("Finished products renewals check.");
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
				SMSGateway.sendSMS("RMS", numbers, message);
			}
		};
		t.start();
	}
}
