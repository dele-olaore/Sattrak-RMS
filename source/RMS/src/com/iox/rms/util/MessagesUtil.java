package com.iox.rms.util;

import com.iox.rms.model.Customer;
import com.iox.rms.model.CustomerProduct;
import com.iox.rms.model.InstallerLocationJobSchedule;

public class MessagesUtil
{
	public MessagesUtil()
	{}
	
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
	
	public static String getSignUpInstallationPointEmailMessage(final String adminFirstName, String companyName, String username, String password,
			String id)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Welcome ").append(adminFirstName).append("!,</strong></p>");
		sb.append("<p>").append("We are pleased to welcome you to Sattrak's RMS platform. Please see your credentials below...").append("</p>");
		sb.append("<p><table border=0>");
		sb.append("<tr><td>Company</td><td>").append(companyName).append("</td></tr>");
		sb.append("<tr><td>Username</td><td>").append(username).append("</td></tr>");
		sb.append("<tr><td>Password</td><td>").append(password).append("</td></tr>");
		sb.append("<tr><td>ID</td><td>").append(id).append("</td></tr>");
		sb.append("<tr><td>Site URL</td><td>").append("http://sattrakservices.com/rms").append("</td></tr>");
		sb.append("</table></p>");
		sb.append("<p>Please note that above information are case-sensitive.</p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
	}
	
	public static String getSignUpPPEmailMessage(final String firstName, String username, String password)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Welcome ").append(firstName).append("!,</strong></p>");
		sb.append("<p>").append("We are pleased to welcome you to Sattrak's RMS platform. Please see your credentials below...").append("</p>");
		sb.append("<p><table border=0>");
		sb.append("<tr><td>Username</td><td>").append(username).append("</td></tr>");
		sb.append("<tr><td>Password</td><td>").append(password).append("</td></tr>");
		sb.append("<tr><td>Site URL</td><td>").append("http://sattrakservices.com/rms").append("</td></tr>");
		sb.append("</table></p>");
		sb.append("<p>Please note that above information are case-sensitive.</p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
	}
	public static String getSignUpPPEmailMessage(final String firstName, String username, String password, String id)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Welcome ").append(firstName).append("!,</strong></p>");
		sb.append("<p>").append("We are pleased to welcome you to Sattrak's RMS platform. Please see your credentials below...").append("</p>");
		sb.append("<p><table border=0>");
		sb.append("<tr><td>Username</td><td>").append(username).append("</td></tr>");
		sb.append("<tr><td>Password</td><td>").append(password).append("</td></tr>");
		sb.append("<tr><td>ID</td><td>").append(id).append("</td></tr>");
		sb.append("<tr><td>Site URL</td><td>").append("http://sattrakservices.com/rms").append("</td></tr>");
		sb.append("</table></p>");
		sb.append("<p>Please note that above information are case-sensitive.</p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
	}
	public static String getSignUpTPEmailMessage(final String tpName, String username, String password, String id)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Welcome ").append(tpName).append("!,</strong></p>");
		sb.append("<p>").append("We are pleased to welcome you to Sattrak's RMS platform. Please see your credentials below...").append("</p>");
		sb.append("<p><table border=0>");
		sb.append("<tr><td>Username</td><td>").append(username).append("</td></tr>");
		sb.append("<tr><td>Password</td><td>").append(password).append("</td></tr>");
		sb.append("<tr><td>ID</td><td>").append(id).append("</td></tr>");
		sb.append("<tr><td>Site URL</td><td>").append("http://sattrakservices.com/rms").append("</td></tr>");
		sb.append("</table></p>");
		sb.append("<p>Please note that above information are case-sensitive.</p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
	}
	
	public static String getSignUpEmailMessage(final String cusFirstName, String username, String password, String uniqueId)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Welcome ").append(cusFirstName).append("!,</strong></p>");
		sb.append("<p>").append("We are pleased to welcome you to our RMS platform.").append("</p>");
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
        sb.append("Dear ").append(cus.getFirstname()).append(", welcome to our RMS platform. Your Unique ID is ").append(cus.getUniqueID()).append(". Your authentication credentials has been sent to your registered email address.");
        return sb.toString();
	}
	
	public static String getJobScheduleEmailMessage(final String cusFirstName, final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Dear ").append(cusFirstName).append(",</strong></p>");
		sb.append("<p>").append("You have successfully placed a booking on our site with the below details: -").append("</p>");
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
	public static String getJobScheduleSMSMessage(final String cusFirstName, final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Dear ").append(cusFirstName).append(", you have successfully placed a booking with Job Code: ").append(job.getJobCode()).append(" on the RMS platform. More details has been sent to your email.");
		return sb.toString();
	}
	public static String getJobRescheduleSMSMessage(final String cusFirstName, final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Dear ").append(cusFirstName).append(", your booking with Job Code: ").append(job.getJobCode()).append(" has been rescheduled on the RMS platform. More details has been sent to your email.");
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
	
	public static String getJobScheduleSupportEmailMessage(final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Hello,</strong></p>");
		sb.append("<p>").append("An installation schedule as been placed on RMS with below details: -").append("</p>");
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
	
	public static String getJobScheduleCheckInEmailMessage(final String cusFirstName, final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Dear ").append(cusFirstName).append(",</strong></p>");
		sb.append("<p>").append("Your installation with Job Code: ").append(job.getJobCode()).append(" has been checked-in.").append("</p>");
		sb.append("<p>For enquires, please send mail to support@sattrakservices.com. Please include the Job Code above in your email.</p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
	}
	public static String getJobScheduleCheckInSMSMessage(final String cusFirstName, final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Dear ").append(cusFirstName).append(", your installation with Job Code: ").append(job.getJobCode()).append(" has been checked-in.");
		return sb.toString();
	}
	
	public static String getJobScheduleReminderEmailMessage(final String cusFirstName, final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Dear ").append(cusFirstName).append(",</strong></p>");
		sb.append("<p>").append("This is to remind you of your scheduled installation with below details:- ").append("</p>");
		sb.append("<p><table border=0>");
		sb.append("<tr><td>Job Code</td><td>").append(job.getJobCode()).append("</td></tr>");
		sb.append("<tr><td>Installation Date and Time</td><td>").append(job.getBooked_dt()).append(" ").append(job.getSlot().getName()).append("</td></tr>");
		sb.append("<tr><td>Product</td><td>").append(job.getProductBooked().getDetails()).append("</td></tr>");
		sb.append("<tr><td>Installation Point</td><td>").append(job.getInstaller().getCompanyName()).append("</td></tr>");
		sb.append("<tr><td>Installation Point Address</td><td>").append(job.getInstaller().getAddress()).append("</td></tr>");
		sb.append("</table></p>");
		sb.append("<p>Please remember the above details and ensure you are at the installation point at least 30 minutes before your booked time.</p>");
		sb.append("<p>For enquires, please send mail to support@sattrakservices.com. Please include the Job Code above in your email.</p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
	}
	
	public static String getJobScheduleInstallerReminderEmailMessage(final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Hello,</strong></p>");
		sb.append("<p>").append("This is to remind you of the scheduled installation placed with your company as the installation point. See installation details below: -").append("</p>");
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
	
	public static String getJobScheduleSupportReminderEmailMessage(final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Hello,</strong></p>");
		sb.append("<p>").append("This is to notify you that a scheduled installation with below details is yet to check-in and will be due to start soon. Please find a way to reach the customer:- ").append("</p>");
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
	
	public static String getJobScheduleOverdueEmailMessage(final String cusFirstName, final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Dear ").append(cusFirstName).append(",</strong></p>");
		sb.append("<p>").append("This is to notify you that you are over due for your installation with below details:- ").append("</p>");
		sb.append("<p><table border=0>");
		sb.append("<tr><td>Job Code</td><td>").append(job.getJobCode()).append("</td></tr>");
		sb.append("<tr><td>Installation Date and Time</td><td>").append(job.getBooked_dt()).append(" ").append(job.getSlot().getName()).append("</td></tr>");
		sb.append("<tr><td>Product</td><td>").append(job.getProductBooked().getDetails()).append("</td></tr>");
		sb.append("<tr><td>Installation Point</td><td>").append(job.getInstaller().getCompanyName()).append("</td></tr>");
		sb.append("<tr><td>Installation Point Address</td><td>").append(job.getInstaller().getAddress()).append("</td></tr>");
		sb.append("</table></p>");
		sb.append("<p>Please note that if you are not available after 3 days of your installation date, your schedule will be automatically cancelled.</p>");
		sb.append("<p>For enquires, please send mail to support@sattrakservices.com. Please include the Job Code above in your email.</p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
	}
	
	public static String getJobScheduleOverdueSupportEmailMessage(final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Hello,</strong></p>");
		sb.append("<p>").append("This is to notify you that the scheduled installation with below details is over due. Please find a way to reach the customer.").append("</p>");
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
	
	public static String getJobScheduleCancelEmailMessage(final String cusFirstName, final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Dear ").append(cusFirstName).append(",</strong></p>");
		sb.append("<p>").append("This is to notify you that your installation schedule with Job Code: ").append(job.getJobCode()).append(" has been cancelled.").append("</p>");
		sb.append("<p>For enquires, please send mail to support@sattrakservices.com. Please include the Job Code above in your email.</p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
	}
	public static String getJobScheduleCancelSMSMessage(final String cusFirstName, final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Dear ").append(cusFirstName).append(", your installation schedule with Job Code: ").append(job.getJobCode()).append(" has been cancelled.");
		return sb.toString();
	}
	
	public static String getJobScheduleCancelInstallerEmailMessage(final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Hello,</strong></p>");
		sb.append("<p>").append("This is to notify you that the scheduled installation job with details below and having your company as the installation point has been Cancelled.").append("</p>");
		sb.append("<p><table border=0>");
		sb.append("<tr><td>Job Code</td><td>").append(job.getJobCode()).append("</td></tr>");
		sb.append("<tr><td>Supposed Installation Date and Time</td><td>").append(job.getBooked_dt()).append(" ").append(job.getSlot().getName()).append("</td></tr>");
		sb.append("<tr><td>Product</td><td>").append(job.getProductBooked().getDetails()).append("</td></tr>");
		sb.append("<tr><td>Customer</td><td>").append(job.getCustomer().getFirstname()).append(" ").append(job.getCustomer().getLastname()).append("</td></tr>");
		sb.append("</table></p>");
		sb.append("<p>For enquires, please send mail to support@sattrakservices.com. Please include the Job Code above in your email.</p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
	}
	
	public static String getJobScheduleCompletedEmailMessage(final String cusFirstName, final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Dear ").append(cusFirstName).append(",</strong></p>");
		sb.append("<p>").append("This is to notify you that your installation schedule with Job Code: ").append(job.getJobCode()).append(" has been completed.").append("</p>");
		sb.append("<p>For enquires, please send mail to support@sattrakservices.com. Please include the Job Code above in your email.</p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
	}
	public static String getJobScheduleCompletedSMSMessage(final String cusFirstName, final InstallerLocationJobSchedule job)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Dear ").append(cusFirstName).append(", your installation schedule with Job Code: ").append(job.getJobCode()).append(" has been completed.");
		return sb.toString();
	}
	
	public static String getProductRenewalReminderEmailMessage(final String cusFirstName, final CustomerProduct cp)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Dear ").append(cusFirstName).append(",</strong></p>");
		sb.append("<p>").append("This is to notify you that your installation with Job Code: ").append(cp.getJob().getJobCode()).append(" will be due for renewal on ").append(cp.getRenewal_due_dt()).append(".</p>");
		sb.append("<p>For enquires, please send mail to support@sattrakservices.com. Please include the Job Code above in your email.</p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
	}
	
	public static String getProductRenewedEmailMessage(final String cusFirstName, final CustomerProduct cp)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<p><strong>Dear ").append(cusFirstName).append(",</strong></p>");
		sb.append("<p>").append("This is to notify you that your installation with Job Code: ").append(cp.getJob().getJobCode()).append(" has been renewed. Your next renewal will be due on ").append(cp.getRenewal_due_dt()).append(".</p>");
		sb.append("<p>For enquires, please send mail to support@sattrakservices.com. Please include the Job Code above in your email.</p>");
		sb.append("<p>Best Regards,<br/>Sattrak Telematics Limited<br/><img src=\"http://sattrakservices.com/rms/images/sattrak-logo.png\" style=\"width:300px;height:70px\" /></p>");
		sb.append("</body></html>");
		return sb.toString();
	}
}
