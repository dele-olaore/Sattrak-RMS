package com.iox.rms.mbean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.dexter.common.util.Hasher;
import com.iox.rms.app.model.AppNotification;
import com.iox.rms.dao.GeneralDAO;
import com.iox.rms.model.Agent;
import com.iox.rms.model.AppConfiguration;
import com.iox.rms.model.AppResource;
import com.iox.rms.model.Country;
import com.iox.rms.model.Customer;
import com.iox.rms.model.CustomerProduct;
import com.iox.rms.model.CustomerTransaction;
import com.iox.rms.model.InstallationDeviceUse;
import com.iox.rms.model.InstallationReworkDeviceUse;
import com.iox.rms.model.InstallerLocation;
import com.iox.rms.model.InstallerLocationItem;
import com.iox.rms.model.InstallerLocationJobSchedule;
import com.iox.rms.model.Item;
import com.iox.rms.model.ItemManufacturer;
import com.iox.rms.model.ItemMove;
import com.iox.rms.model.ItemType;
import com.iox.rms.model.LGA;
import com.iox.rms.model.Notification;
import com.iox.rms.model.Partner;
import com.iox.rms.model.PartnerPersonnel;
import com.iox.rms.model.Product;
import com.iox.rms.model.ProductItem;
import com.iox.rms.model.ProductType;
import com.iox.rms.model.ProductTypeCommission;
import com.iox.rms.model.Rating;
import com.iox.rms.model.ReworkInstallation;
import com.iox.rms.model.Role;
import com.iox.rms.model.SalesAgent;
import com.iox.rms.model.SalesAgentItem;
import com.iox.rms.model.Slot;
import com.iox.rms.model.State;
import com.iox.rms.model.TradePartner;
import com.iox.rms.model.TradePartnerItem;
import com.iox.rms.model.User;
import com.iox.rms.model.VehicleMake;
import com.iox.rms.model.VehicleType;
import com.iox.rms.util.MessagesUtil;
import com.itextpdf.text.Chunk;
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

@ManagedBean(name = "appBean", eager = true)
@SessionScoped
public class AppBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String salesAgentUniqueId, customerUniqueId, customerPhoneNo, paymentType;
	
	private Partner newPartner, selPartner;
	private List<Partner> partners;
	
	private ItemManufacturer itmManufacturer, selItmManufacturer;
	private List<ItemManufacturer> itmManufacturers;
	
	private ItemType itmType, selItmType;
	private List<ItemType> itmTypes;
	
	private long itmManu_id, itmType_id;
	private Item itm, selItm;
	private List<Item> items;
	
	private ProductType ptype, selPtype;
	private List<ProductType> ptypes;
	
	private long ptype_id;
	private List<ProductItem> productItemList, editProductItemList;
	private Product product, selProduct;
	private List<Product> products;
	private Part product_photo;
	
	private long ctry_id, state_id, lga_id;
	private String confirm_password, dobstr;
	private List<Country> countries;
	private List<State> states;
	private List<LGA> lgas, lgas2;
	private User cusUser;
	private Customer customer, selCustomer;
	private List<Customer> customers;
	
	private User salesAgentUser;
	private long salesAgent_id;
	private SalesAgent salesAgent, selSalesAgent;
	private List<SalesAgent> salesAgents, salesAgentsWithStock;
	
	private User tpUser;
	private long tradePartner_id;
	private TradePartner tradePartner, selTradePartner;
	private List<TradePartner> tradePartners;
	
	private long rating_id;
	private Rating rating, selRating;
	private List<Rating> ratings;
	
	private User installerUser;
	private InstallerLocation installer, selInstaller, selEditInstaller;
	private List<InstallerLocation> installers, installersByLGA, installersByLGA2;
	private Agent agent, selAgent;
	private List<Agent> installerAgents, myAgents, allAgents;
	private Part installer_photo, agent_photo, completeJobCard;
	
	private User ppUser;
	private PartnerPersonnel pp, selPP;
	private List<PartnerPersonnel> ppList;
	
	private String itmMove_dt_str;
	private long itm_id, product_id, installer_id, edit_installer_id;
	private ItemMove itmMove, selItmMove;
	private List<ItemMove> itmMoveList;
	
	private List<ProductTypeCommission> ptypeCommissionList;
	
	private long customer_id, vtype_id, vmake_id, slot_id;
	private double total_cost;
	private List<Slot> slots, edit_slots;
	private List<VehicleType> vtypes;
	private List<VehicleMake> vmakes;
	private String book_dt_str, rework_dt_str;
	private InstallerLocationJobSchedule booking;
	
	private long installer_lga_id, installer_state_id, partner_id;
	private boolean agreed;
	
	private String start_dt_str, end_dt_str;
	
	private List<InstallationDeviceUse> installationDeviceUseList;
	private List<InstallationReworkDeviceUse> installationReworkDeviceUseList;
	
	private InstallerLocationJobSchedule installation, selectedInstallation, selNotCheckInJob, selCheckedInJob;
	private List<InstallerLocationJobSchedule> myPendingJobs, myNotCheckInJobs, myCheckedInJobs, jobsPendingConfirmation, mySchedules_cus, checkedInSchedules;
	private List<InstallerLocationJobSchedule> myUpcomingJobs, myInprogressJobs, myOverdueJobs, myCompletedJobs, myCancelledJobs;
	private String myPendingJobsDate_st_str, myPendingJobsDate_end_str;
	private long not_checkedin_installation_id, checkedin_installation_id, item_id, job_installer_id;
	private int item_used_count, item_return_count;
	
	private String return_reason, return_remarks;
	
	private List<CustomerProduct> myProducts;
	
	private List<InstallerLocationJobSchedule> searchedInstallation;
	private BigDecimal expectedEarnings, actualEarnings;
	private long totalCount, totalPendingCheckIn, totalCheckInPaid, totalCompleted, totalCanceled, totalRConfirm, totalConfirm, totalRefund;
	
	private List<String[]> assetStockByType, assetStockByModel;
	
	private AppConfiguration appConfig;
	
	private AppResource appResource, selAppResource;
	private Part appResourceFile;
	private List<AppResource> appResources;
	
	private String jobCode;
	
	private InstallerLocationJobSchedule jobRefund;
	
	private List<InstallerLocationJobSchedule> totalSubscriptionReport;
	private List<CustomerProduct> totalRenewalReport, upcomingRenewalDueReport, overdueRenewalReport;
	private List<String[]> salesAgentSummaryReport, installationPointSummaryReport, totalSalesReport, salesAgentCommissionReport, installationPointCommissionReport;
	
	private String moveType;
	private List<ItemMove> deviceMoveReport;
	
	private Part customers_excel;
	
	private ReworkInstallation reworkInstallation;
	private InstallationReworkDeviceUse reworkDeviceUse;
	
	private List<CustomerTransaction> pendingBankDeposits;
	private CustomerTransaction selBankDeposit;
	private List<CustomerProduct> uninstalledPurchases;
	private long cp_id;
	
	@ManagedProperty("#{appNotifBean}")
	private AppNotificationBean appNotifBean;
	@ManagedProperty("#{userBean}")
	private UserBean userBean;
	
	public AppBean()
	{}
	
	@SuppressWarnings("unchecked")
	public String confirmBankDeposit()
	{
		String ret = "confirmPaymentAtBank?faces-redirect=true";
		
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		if(getSelBankDeposit() != null)
		{
			getSelBankDeposit().setConfirmedBy(userBean.getSessionUser());
			getSelBankDeposit().setPayConfirmDate(new Date());
			getSelBankDeposit().setStatus("PAID");
			
			GeneralDAO gDAO = new GeneralDAO();
			CustomerProduct cp = null;
			Query q = gDAO.createQuery("Select e from CustomerProduct e where e.purchaseTransaction=:purchaseTransaction");
			q.setParameter("purchaseTransaction", getSelBankDeposit());
			Object cpObj = gDAO.search(q, 0);
			if(cpObj != null)
			{
				List<CustomerProduct> cpList = (List<CustomerProduct>)cpObj;
				for(CustomerProduct e : cpList)
					cp = e;
			}
			
			if(cp != null)
			{
				gDAO.startTransaction();
				gDAO.update(getSelBankDeposit());
				
				cp.setStatus("NOT-INSTALLED");
				gDAO.update(cp);
				
				try
				{
					gDAO.commit();
					setPendingBankDeposits(null);
					setSelBankDeposit(null);
					
					userBean.sendAutoLifeSMS(cp.getCustomer().getPhoneNo(), "Dear " + cp.getCustomer().getFirstname() + ", your payment for transaction (" + cp.getPurchaseTranRef() + ") has been confirmed. You can now proceed to book your product installation. Thank you.");
					
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage("Bank deposit payment confirmed successfully! Installation can now be booked for this purchase!");
				}
				catch(Exception ex)
				{
					gDAO.rollback();
					an.setType("ERROR");
					an.setSubject("Error");
					an.setMessage("Message: " + ex.getMessage() + "!");
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("Could not identify the customer product purchase attached to this bank deposit!");
			}
			
			gDAO.destroy();
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("Please select a valid pending bank deposit!");
		}
		appNotifBean.getAppNotifications().add(an);
		
		return ret;
	}
	
	public String initBankPayConfirm()
	{
		return "confirmPaymentAtBankFinal?faces-redirect=true";
	}
	
	public void cancelBankPay()
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		if(getSelBankDeposit() != null)
		{
			getSelBankDeposit().setConfirmedBy(userBean.getSessionUser());
			getSelBankDeposit().setPayConfirmDate(new Date());
			getSelBankDeposit().setStatus("CANCELLED");
			
			GeneralDAO gDAO = new GeneralDAO();
			gDAO.startTransaction();
			gDAO.update(getSelBankDeposit());
			
			try
			{
				gDAO.commit();
				setPendingBankDeposits(null);
				setSelBankDeposit(null);
				an.setType("SUCCESS");
				an.setSubject("Success");
				an.setMessage("Bank deposit payment cancelled successfully!");
			}
			catch(Exception ex)
			{
				gDAO.rollback();
				an.setType("ERROR");
				an.setSubject("Error");
				an.setMessage("Message: " + ex.getMessage() + "!");
			}
			
			gDAO.destroy();
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("Please select a valid pending bank deposit!");
		}
		appNotifBean.getAppNotifications().add(an);
	}
	
	public void addJobReworkDeviceUse()
	{
		if(getItem_id() > 0 && getItem_used_count() > 0)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Item item = null;
			Object itm = gDAO.find(Item.class, getItem_id());
			if(itm != null)
				item = (Item)itm;
			
			boolean exists = false;
			for(InstallationReworkDeviceUse e : getInstallationReworkDeviceUseList())
			{
				if(e.getItem_id() == getItem_id())
				{
					exists = true;
					break;
				}
			}
			
			if(item != null && !exists)
			{
				InstallationReworkDeviceUse e = new InstallationReworkDeviceUse();
				e.setCount(getItem_used_count());
				e.setItem(item);
				e.setCreatedBy(userBean.getSessionUser());
				e.setCrt_dt(new Date());
				e.setItem_id(getItem_id());
				e.setItem_count(getItem_used_count());
				
				getInstallationReworkDeviceUseList().add(e);
			}
			gDAO.destroy();
		}
	}
	public void removeJobReworkDeviceUse(long item_id)
	{
		for(int i=0; i<getInstallationReworkDeviceUseList().size(); i++)
		{
			InstallationReworkDeviceUse e = getInstallationReworkDeviceUseList().get(i);
			if(e.getItem_id() == item_id)
			{
				getInstallationReworkDeviceUseList().remove(i);
				break;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void reworkInstallation()
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		if(getReworkInstallation().getReasonForRework() != null && getReworkInstallation().getReasonForRework().trim().length() > 0 &&
				getReworkInstallation().getInstallerRemarks() != null && getReworkInstallation().getInstallerRemarks().trim().length() > 0)
		{
			getReworkInstallation().setCreatedBy(userBean.getSessionUser());
			getReworkInstallation().setCrt_dt(new Date());
			getReworkInstallation().setJobCode(generateReworkReference());
			
			if(rework_dt_str != null)
			{
				try
				{
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					Date dt = sdf.parse(rework_dt_str);
					getReworkInstallation().setRework_dt(dt);
				}
				catch(Exception ig)
				{
					getReworkInstallation().setRework_dt(new Date());
				}
			}
			
			GeneralDAO gDAO = new GeneralDAO();
			
			getReworkInstallation().setMainJob(getSelectedInstallation());
			if(getJob_installer_id() > 0)
			{
				Object agentObj = gDAO.find(Agent.class, getJob_installer_id());
				if(agentObj != null)
					getReworkInstallation().setInstaller((Agent)agentObj);
			}
			
			gDAO.startTransaction();
			
			gDAO.save(getReworkInstallation());
			if(getInstallationReworkDeviceUseList().size() > 0)
			{
				for(InstallationReworkDeviceUse e : getInstallationReworkDeviceUseList())
				{
					if(e.getItem_id() > 0 && e.getItem_count() > 0)
					{
						Item item = e.getItem();
						
						if(item != null)
						{
							e.setInstallation(getReworkInstallation());
							gDAO.save(e);
							
							InstallerLocationItem ili = null;
							Hashtable<String, Object> params = new Hashtable<String, Object>();
							params.put("installer", getReworkInstallation().getMainJob().getInstaller());
							params.put("item", item);
							
							Object iliObj = gDAO.search("InstallerLocationItem", params);
							if(iliObj != null)
							{
								List<InstallerLocationItem> list = (List<InstallerLocationItem>)iliObj;
								for(InstallerLocationItem ilie : list)
									ili = ilie;
							}
							
							if(ili == null)
							{
								ili = new InstallerLocationItem();
								ili.setCrt_dt(new Date());
								ili.setCreatedBy(userBean.getSessionUser());
								ili.setInstaller(getReworkInstallation().getMainJob().getInstaller());
								ili.setItem(item);
								ili.setCount(-e.getItem_count());
								gDAO.save(ili);
							}
							else
							{
								ili.setCount(ili.getCount() - e.getItem_count());
								gDAO.update(ili);
							}
						}
					}
				}
			}
			
			try
			{
				Notification n = new Notification();
				n.setCrt_dt(new Date());
				n.setNotified(false);
				n.setSubject("Installation Reworked");
				n.setPage_url("dashboard");
				n.setUser(getReworkInstallation().getMainJob().getCustomer().getUser());
				n.setMessage("Your installation with Job Code: " + getReworkInstallation().getMainJob().getJobCode() + " has been reworked.");
				
				gDAO.save(n);
				
				gDAO.commit();
				
				userBean.sendEmail(new String[]{getReworkInstallation().getMainJob().getCustomer().getUser().getUsername()}, "Installation Reworked on RMS", MessagesUtil.getJobScheduleCompletedEmailMessage(getReworkInstallation().getMainJob().getCustomer().getFirstname(), getReworkInstallation().getMainJob()));
				userBean.sendSMS(getReworkInstallation().getMainJob().getCustomer().getPhoneNo(), MessagesUtil.getJobScheduleCompletedSMSMessage(getReworkInstallation().getMainJob().getCustomer().getFirstname(), getReworkInstallation().getMainJob()));
				
				setReworkInstallation(null);
				setInstallationReworkDeviceUseList(null);
				setSelectedInstallation(null);
				setRework_dt_str(null);
				an.setType("SUCCESS");
				an.setSubject("Success");
				an.setMessage("Job reworked successfully!");
			}
			catch(Exception ex)
			{
				gDAO.rollback();
				an.setType("ERROR");
				an.setSubject("Error");
				an.setMessage("Message: " + ex.getMessage() + "!");
			}
			gDAO.destroy();
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("Please select a valid previously completed job!");
		}
		appNotifBean.getAppNotifications().add(an);
	}
	
	@SuppressWarnings("unchecked")
	public void batchLoadCustomers()
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		if(getCustomers_excel() != null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			try
			{
				Role role = null;
				Object rolesObj = gDAO.findAll("Role");
				if(rolesObj != null)
				{
					List<Role> roles = (List<Role>)rolesObj;
					for(Role e : roles)
					{
						if(e.getName().equalsIgnoreCase("CUSTOMER"))
						{
							role = e;
							break;
						}
					}
				}
				//Get the workbook instance for XLS file
				HSSFWorkbook workbook = new HSSFWorkbook(getCustomers_excel().getInputStream());
				//Get first sheet from the workbook
				HSSFSheet sheet = workbook.getSheetAt(0);
				//Get iterator to all the rows in current sheet starting from row 2
				Iterator<Row> rowIterator = sheet.iterator();
				int pos = 1;
				gDAO.startTransaction();
				boolean ret = false;
				while(rowIterator.hasNext())
				{
					Row row = rowIterator.next();
					String title = "", firstname = "", lastname = "", date_of_birth="", address="", country="", state="", lga="", phoneno="";
					String username="", password="";
					if(pos > 1)
					{
						//Get iterator to all cells of current row
						Iterator<Cell> cellIterator = row.cellIterator();
						while(cellIterator.hasNext())
						{
							Cell cell = cellIterator.next();
							String val = "";
							switch(cell.getCellType())
							{
								case Cell.CELL_TYPE_BLANK:
									val = "";
									break;
								case Cell.CELL_TYPE_BOOLEAN:
									val = ""+cell.getBooleanCellValue();
									break;
								case Cell.CELL_TYPE_ERROR:
									val = "";
									break;
								case Cell.CELL_TYPE_NUMERIC:
									val = ""+cell.getNumericCellValue();
									break;
								case Cell.CELL_TYPE_STRING:
									val = cell.getStringCellValue();
									break;
								default:
								{
									try
									{
									val = cell.getStringCellValue();
									} catch(Exception ex){}
									break;
								}
							}
							switch(cell.getColumnIndex())
							{
							case 0:
								title = val;
								break;
							case 1:
								firstname = val;
								break;
							case 2:
								lastname = val;
								break;
							case 3:
								date_of_birth = val;
								break;
							case 4:
								address = val;
								break;
							case 5:
								country = val;
								break;
							case 6:
								state = val;
								break;
							case 7:
								lga = val;
								break;
							case 8:
								phoneno = val;
								break;
							case 9:
								username = val;
								break;
							case 10:
								password = val;
								break;
							}
						}
						User cusUser = new User();
						cusUser.setActive(true);
						cusUser.setCreatedBy(userBean.getSessionUser());
						cusUser.setCrt_dt(new Date());
						cusUser.setPartner(getPartner());
						cusUser.setPassword(password);
						cusUser.setRole(role);
						cusUser.setType("CUSTOMER");
						cusUser.setUsername(username);
						
						Customer cus = new Customer();
						cus.setPartner(getPartner());
						cus.setCreatedBy(userBean.getSessionUser());
						cus.setCrt_dt(new Date());
						cus.setTitle(title);
						cus.setFirstname(firstname);
						cus.setLastname(lastname);
						cus.setAddress(address);
						cus.setPhoneNo(phoneno);
						
						if(date_of_birth != null && date_of_birth.trim().length() > 0)
						{
							try
							{
								SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
								Date dt = sdf.parse(date_of_birth);
								cus.setDob(dt);
							}catch(Exception ex){ex.printStackTrace();}
						}
						
						if(country != null && country.trim().length() > 0)
						{
							Object listObj = gDAO.findAll("Country");
							if(listObj != null)
							{
								List<Country> list = (List<Country>)listObj;
								for(Country e : list)
								{
									if(e.getName().equalsIgnoreCase(country))
									{
										cus.setCountry(e);
										break;
									}
								}
							}
						}
						
						if(state != null && state.trim().length() > 0)
						{
							Object listObj = gDAO.findAll("State");
							if(listObj != null)
							{
								List<State> list = (List<State>)listObj;
								for(State e : list)
								{
									if(e.getName().equalsIgnoreCase(state))
									{
										cus.setState(e);
										break;
									}
								}
							}
						}
						
						if(lga != null && lga.trim().length() > 0)
						{
							Object listObj = gDAO.findAll("LGA");
							if(listObj != null)
							{
								List<LGA> list = (List<LGA>)listObj;
								for(LGA e : list)
								{
									if(e.getName().equalsIgnoreCase(lga))
									{
										cus.setLga(e);
										break;
									}
								}
							}
						}
						
						ret = gDAO.save(cusUser);
						if(ret)
						{
							cus.setUser(cusUser);
							ret = gDAO.save(cus);
							if(!ret)
								break;
						}
						else
							break;
					}
				}
				
				if(ret)
				{
					gDAO.commit();
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage("Customers loaded successfully!");
				}
				else
				{
					gDAO.rollback();
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Failed to save customers: " + gDAO.getMessage());
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				an.setType("ERROR");
				an.setSubject("Error");
				an.setMessage("Severe error occured. " + ex.getMessage());
			}
			gDAO.destroy();
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("No document uploaded!");
		}
		appNotifBean.getAppNotifications().add(an);
	}
	
	public void refundJob()
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		if(getJobRefund() != null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			
			try
			{
				AppConfiguration appConfig = getCurrentAppConfig();
				BigDecimal chargPercent = new BigDecimal(appConfig.getRefundChargePercent());
				chargPercent = chargPercent.setScale(2, RoundingMode.HALF_UP);
				BigDecimal total = new BigDecimal(getJobRefund().getCost());
				total = total.setScale(2, RoundingMode.HALF_UP);
				
				BigDecimal amount = total.multiply(chargPercent);
				amount = amount.divide(new BigDecimal(100.00));
				amount = amount.setScale(2, RoundingMode.HALF_UP);
				
				getJobRefund().setRefundAmt(amount.doubleValue());
				getJobRefund().setRefundChargePercent(chargPercent.doubleValue());
				getJobRefund().setRefund(true);
			}
			catch(Exception ex)
			{
				an.setType("ERROR");
				an.setSubject("Error");
				an.setMessage("Message: " + ex.getMessage() + "!");
			}
			
			if(getJobRefund().isRefund())
			{
				gDAO.startTransaction();
				gDAO.update(getJobRefund());
				try
				{
					gDAO.commit();
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage("Job successfully marked as refunded! Pay back customer: NGN" + (getJobRefund().getCost()-getJobRefund().getRefundAmt()));
				}
				catch(Exception ex)
				{
					gDAO.rollback();
					an.setType("ERROR");
					an.setSubject("Error");
					an.setMessage("Message: " + ex.getMessage() + "!");
				}
			}
			gDAO.destroy();
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("No job to refund!");
		}
		appNotifBean.getAppNotifications().add(an);
	}
	
	public void loadJobForRefund()
	{
		setJobRefund(null);
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		if(getJobCode() != null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.completed=:completed and e.refund=:refund and e.jobCode=:jobCode");
			q.setParameter("jobCode", getJobCode());
			q.setParameter("completed", true);
			q.setParameter("refund", false);
			Object obj = gDAO.search(q, 1);
			if(obj != null)
			{
				setJobRefund((InstallerLocationJobSchedule)obj);
				an.setType("SUCCESS");
				an.setSubject("Success");
				an.setMessage("Job found!");
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("Job not found! The job might have been refunded, not completed or does not exist. Please check and also ensure you entered the correct job code!");
			}
			gDAO.destroy();
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("Please supply a job code!");
		}
		appNotifBean.getAppNotifications().add(an);
	}
	
	public void saveAppConfig()
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		if(getAppConfig() != null && getAppConfig().getId() != null)
		{
			getAppConfig().setLast_update_dt(new Date());
			GeneralDAO gDAO = new GeneralDAO();
			gDAO.startTransaction();
			gDAO.update(getAppConfig());
			try
			{
				gDAO.commit();
				an.setType("SUCCESS");
				an.setSubject("Success");
				an.setMessage("Updated successfully!");
			}
			catch(Exception ex)
			{
				gDAO.rollback();
				an.setType("ERROR");
				an.setSubject("Error");
				an.setMessage("Message: " + ex.getMessage() + "!");
			}
			gDAO.destroy();
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("The currently loaded Application Configuration is invalid. Please logout and login again to reload the configuration then try again!");
		}
		appNotifBean.getAppNotifications().add(an);
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
	
	public void addEditItemToProduct()
	{
		if(getItem_id() > 0 && getItem_used_count() > 0)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Item item = null;
			Object itm = gDAO.find(Item.class, getItem_id());
			if(itm != null)
				item = (Item)itm;
			
			boolean exists = false;
			for(ProductItem e : getEditProductItemList())
			{
				if(e.getItem().getId().longValue() == getItem_id())
				{
					exists = true;
					break;
				}
			}
			
			if(item != null && !exists)
			{
				ProductItem e = new ProductItem();
				e.setItem_count(getItem_used_count());
				e.setItem(item);
				e.setCrt_dt(new Date());
				e.setProduct(getSelProduct());
				
				getEditProductItemList().add(e);
			}
			gDAO.destroy();
		}
	}
	public void removeEditItemFromProduct(long item_id)
	{
		for(int i=0; i<getEditProductItemList().size(); i++)
		{
			ProductItem e = getEditProductItemList().get(i);
			if(e.getItem().getId().longValue() == item_id)
			{
				getEditProductItemList().remove(i);
				break;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public String initEdit(String page)
	{
		if(page.equalsIgnoreCase("edit-salesagent"))
		{
			setCtry_id(getSelSalesAgent().getCountry()!= null ? getSelSalesAgent().getCountry().getId() : 0L);
			setState_id(getSelSalesAgent().getState()!= null ? getSelSalesAgent().getState().getId() : 0L);
			setLga_id(getSelSalesAgent().getLga()!= null ? getSelSalesAgent().getLga().getId() : 0L);
		}
		else if(page.equalsIgnoreCase("edit-tradepartner"))
		{
			setCtry_id(getSelTradePartner().getCountry()!= null ? getSelSalesAgent().getCountry().getId() : 0L);
			setState_id(getSelSalesAgent().getState()!= null ? getSelSalesAgent().getState().getId() : 0L);
			setLga_id(getSelSalesAgent().getLga()!= null ? getSelSalesAgent().getLga().getId() : 0L);
		}
		else if(page.equalsIgnoreCase("edit-agent"))
		{
			setCtry_id(getSelEditInstaller().getCountry()!= null ? getSelEditInstaller().getCountry().getId() : 0L);
			setState_id(getSelEditInstaller().getState()!= null ? getSelEditInstaller().getState().getId() : 0L);
			setLga_id(getSelEditInstaller().getLga()!= null ? getSelEditInstaller().getLga().getId() : 0L);
		}
		else if(page.equalsIgnoreCase("edit-customer"))
		{
			String dtstr = "";
			if(getSelCustomer().getDob() != null)
			{
				String mm = ""+(getSelCustomer().getDob().getMonth()+1);
				if(mm.trim().length() == 1)
					mm = "0"+mm;
				String dd = ""+(getSelCustomer().getDob().getDate());
				if(dd.trim().length() == 1)
					dd = "0"+dd;
				String yy = ""+(getSelCustomer().getDob().getYear()+1900);
				dtstr = mm + "/" + dd + "/" + yy;
			}
			setDobstr(dtstr);
			setCtry_id(getSelCustomer().getCountry()!= null ? getSelCustomer().getCountry().getId() : 0L);
			setState_id(getSelCustomer().getState()!= null ? getSelCustomer().getState().getId() : 0L);
			setLga_id(getSelCustomer().getLga()!= null ? getSelCustomer().getLga().getId() : 0L);
		}
		else if(page.equalsIgnoreCase("edit-product"))
		{
			setEditProductItemList(getSelProduct().getItems());
		}
		return page+"?faces-redirect=true";
	}
	
	public void update(Object entity)
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		boolean validated = true;
		GeneralDAO gDAO = new GeneralDAO();
		if(entity instanceof Item)
		{
			if(getSelItm().getName().trim().length() > 0 && getSelItm().getDescription().trim().length() > 0 &&
					getSelItm().getModel().trim().length() > 0 && getItmType_id() > 0 && getItmManu_id() > 0)
			{
				Object itmType = gDAO.find(ItemType.class, getItmType_id());
				if(itmType != null)
					getSelItm().setType((ItemType)itmType);
				
				Object itmManu = gDAO.find(ItemManufacturer.class, getItmManu_id());
				if(itmManu != null)
					getSelItm().setManufacturer((ItemManufacturer)itmManu);
				
				entity = getSelItm();
			}
			else
				validated = false;
		}
		else if(entity instanceof Product)
		{
			if(getPtype_id() > 0)
			{
				Object ptype = gDAO.find(ProductType.class, getPtype_id());
				if(ptype != null)
					getSelProduct().setType((ProductType)ptype);
			}
			
			if(getProduct_photo() != null)
			{
				if(getProduct_photo().getContentType().indexOf("image")>=0)
				{
					try
					{
						InputStream in = getProduct_photo().getInputStream();
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						int read;
						byte[] input = new byte[4096];
						while(-1 != ( read = in.read( input ) ) ) {
							buffer.write( input, 0, read );
						}
						in.close();
						getSelProduct().setPhoto(buffer.toByteArray());
					}
					catch(Exception ex)
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Could not upload the selected photo!");
						appNotifBean.getAppNotifications().add(an);
						an = new AppNotification();
					}
				}
				else
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("File uploaded not an image!");
					appNotifBean.getAppNotifications().add(an);
					an = new AppNotification();
				}
			}
			
			entity = getSelProduct();
		}
		else if(entity instanceof Agent)
		{
			if(getRating_id() > 0)
			{
				Object rating = gDAO.find(Rating.class, getRating_id());
				if(rating != null)
					getSelAgent().setRating((Rating)rating);
				
				entity = getSelAgent();
			}
		}
		else if(entity instanceof InstallerLocation)
		{
			if(getCtry_id() > 0)
			{
				Object ctry = gDAO.find(Country.class, getCtry_id());
				if(ctry != null)
					getSelEditInstaller().setCountry((Country)ctry);
			}
			if(getState_id() > 0)
			{
				Object state = gDAO.find(State.class, getState_id());
				if(state != null)
					getSelEditInstaller().setState((State)state);
			}
			if(getLga_id() > 0)
			{
				Object lga = gDAO.find(LGA.class, getLga_id());
				if(lga != null)
					getSelEditInstaller().setLga((LGA)lga);
			}
			
			if(getSelEditInstaller().getUniqueID() == null)
			{
				getSelEditInstaller().setUniqueID(generateAgentReference(getSelEditInstaller().getId()));
			}
			
			entity = getSelEditInstaller();
		}
		else if(entity instanceof SalesAgent)
		{
			if(getCtry_id() > 0)
			{
				Object ctry = gDAO.find(Country.class, getCtry_id());
				if(ctry != null)
					getSelSalesAgent().setCountry((Country)ctry);
			}
			if(getState_id() > 0)
			{
				Object state = gDAO.find(State.class, getState_id());
				if(state != null)
					getSelSalesAgent().setState((State)state);
			}
			if(getLga_id() > 0)
			{
				Object lga = gDAO.find(LGA.class, getLga_id());
				if(lga != null)
					getSelSalesAgent().setLga((LGA)lga);
			}
			if(getSelSalesAgent().getUniqueID() == null)
			{
				getSelSalesAgent().setUniqueID(generateAgentReference(getSelSalesAgent().getId()));
			}
			
			entity = getSelSalesAgent();
		}
		else if(entity instanceof TradePartner)
		{
			if(getCtry_id() > 0)
			{
				Object ctry = gDAO.find(Country.class, getCtry_id());
				if(ctry != null)
					getSelTradePartner().setCountry((Country)ctry);
			}
			if(getState_id() > 0)
			{
				Object state = gDAO.find(State.class, getState_id());
				if(state != null)
					getSelTradePartner().setState((State)state);
			}
			if(getLga_id() > 0)
			{
				Object lga = gDAO.find(LGA.class, getLga_id());
				if(lga != null)
					getSelTradePartner().setLga((LGA)lga);
			}
			if(getSelTradePartner().getUniqueID() == null)
			{
				getSelTradePartner().setUniqueID(generateTPReference(getSelTradePartner().getId()));
			}
			
			entity = getSelTradePartner();
		}
		else if(entity instanceof Customer)
		{
			if(getCtry_id() > 0)
			{
				Object ctry = gDAO.find(Country.class, getCtry_id());
				if(ctry != null)
					getSelCustomer().setCountry((Country)ctry);
			}
			if(getState_id() > 0)
			{
				Object state = gDAO.find(State.class, getState_id());
				if(state != null)
					getSelCustomer().setState((State)state);
			}
			if(getLga_id() > 0)
			{
				Object lga = gDAO.find(LGA.class, getLga_id());
				if(lga != null)
					getSelCustomer().setLga((LGA)lga);
			}
			
			if(getDobstr() != null && getDobstr().trim().length() > 0)
			{
				try
				{
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					Date dt = sdf.parse(getDobstr());
					getSelCustomer().setDob(dt);
				}catch(Exception ex){ex.printStackTrace();}
			}
			
			entity = getSelCustomer();
		}
		
		if(validated)
		{
			try
			{
				gDAO.startTransaction();
				gDAO.update(entity);
				
				// here we handle changes to the items modified for this product
				if(entity instanceof Product)
				{
					for(ProductItem pi1 : getEditProductItemList())
					{
						boolean exists = false, update = false;
						for(ProductItem pi2 : getSelProduct().getItems())
						{
							if(pi1.getItem().getId().longValue() == pi2.getItem().getId().longValue())
							{
								exists = true;
								if(pi1.getItem_count() != pi2.getItem_count())
									update = true;
							}
						}
						if(!exists)
						{
							pi1.setProduct(getSelProduct());
							gDAO.save(pi1);
						}
						else if(exists && update)
							gDAO.update(pi1);
					}
					for(ProductItem pi1 : getSelProduct().getItems())
					{
						boolean remove = true;
						for(ProductItem pi2 : getEditProductItemList())
						{
							if(pi1.getItem().getId().longValue() == pi2.getItem().getId().longValue())
							{
								remove = false;
							}
						}
						if(remove)
							gDAO.remove(pi1);
					}
				}
				
				gDAO.commit();
				an.setType("SUCCESS");
				an.setSubject("Success");
				an.setMessage("Updated successfully!");
			}
			catch(Exception ex)
			{
				gDAO.rollback();
				an.setType("ERROR");
				an.setSubject("Error");
				an.setMessage("Message: " + ex.getMessage() + "!");
			}
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("All field(s) with the '*' sign are required!");
		}
		gDAO.destroy();
		appNotifBean.getAppNotifications().add(an);
	}
	
	@SuppressWarnings("unchecked")
	public void cancelSchedule()
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		if(getSelectedInstallation() != null)
		{
			getSelectedInstallation().setCancel(true);
			getSelectedInstallation().setCompleted(true);
			GeneralDAO gDAO = new GeneralDAO();
			gDAO.startTransaction();
			gDAO.update(getSelectedInstallation());
			
			CustomerProduct cp = null;
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("job", getSelectedInstallation());
			Object cpObj = gDAO.search("CustomerProduct", params);
			if(cpObj != null)
			{
				List<CustomerProduct> list = (List<CustomerProduct>)cpObj;
				for(CustomerProduct e : list)
					cp = e;
			}
			
			if(cp != null)
			{
				cp.setStatus("NOT-INSTALLED");
				cp.setJob(null);
				gDAO.update(cp);
				
				try
				{
					Notification n = new Notification();
					n.setCrt_dt(new Date());
					n.setNotified(false);
					n.setSubject("Installation Cancelled");
					n.setPage_url("dashboard");
					n.setUser(getSelectedInstallation().getCustomer().getUser());
					n.setMessage("Your installation with Job Code: " + getSelectedInstallation().getJobCode() + " has been cancelled.");
					
					gDAO.save(n);
					
					gDAO.commit();
					
					userBean.sendEmail(new String[]{getSelectedInstallation().getCustomer().getUser().getUsername()}, "Installation Cancelled on RMS", MessagesUtil.getJobScheduleCancelEmailMessage(getSelectedInstallation().getCustomer().getFirstname(), getSelectedInstallation()));
					userBean.sendSMS(getSelectedInstallation().getCustomer().getPhoneNo(), MessagesUtil.getJobScheduleCancelSMSMessage(getSelectedInstallation().getCustomer().getFirstname(), getSelectedInstallation()));
					
					setSelectedInstallation(null);
					searchSchedules();
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage("Booking canceled successfully!");
				}
				catch(Exception ex)
				{
					gDAO.rollback();
					an.setType("ERROR");
					an.setSubject("Error");
					an.setMessage("Message: " + ex.getMessage() + "!");
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("No corresponding customer-product mapping found for the selected installation!");
			}
			gDAO.destroy();
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("Please select a valid booking to cancel!");
		}
		appNotifBean.getAppNotifications().add(an);
	}
	
	@SuppressWarnings("unchecked")
	public void updateSchedule()
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		if(getSelectedInstallation() != null && getSelEditInstaller() != null && getBook_dt_str() != null && getBooking().getBooked_dt() != null &&
				getSlot_id() > 0)
		{
			GeneralDAO gDAO = new GeneralDAO();
			
			getSelectedInstallation().setInstaller(getSelEditInstaller());
			getSelectedInstallation().setBooked_dt(getBooking().getBooked_dt());
			
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
			if(!slot_taken || getSelInstaller().getMaxMultiSlotInstallation() == 0 || getSelInstaller().getMaxMultiSlotInstallation() > slot_used_size)
			{
				gDAO.startTransaction();
				gDAO.update(getSelectedInstallation());
				try
				{
					gDAO.commit();
					
					userBean.sendEmail(new String[]{getSelectedInstallation().getCustomer().getUser().getUsername()}, "Installation Reschedule on RMS", MessagesUtil.getJobRescheduleEmailMessage(getSelectedInstallation().getCustomer().getFirstname(), getSelectedInstallation()));
					userBean.sendSMS(getSelectedInstallation().getCustomer().getPhoneNo(), MessagesUtil.getJobRescheduleSMSMessage(getSelectedInstallation().getCustomer().getFirstname(), getSelectedInstallation()));
					
					userBean.sendEmail(new String[]{getSelectedInstallation().getInstaller().getUser().getUsername()}, "Installation Reschedule on RMS", MessagesUtil.getJobRescheduleInstallerEmailMessage(getSelectedInstallation()));
					
					// send notification to support
					AppConfiguration currectAppConfig = getCurrentAppConfig();
					if(currectAppConfig != null && currectAppConfig.getCustomerSupportEmail() != null && currectAppConfig.getCustomerSupportEmail().trim().length() > 0)
					{
						String[] supportEmails = currectAppConfig.getCustomerSupportEmail().split(",");
						userBean.sendEmail(supportEmails, "Installation Reschedule on RMS", MessagesUtil.getJobRescheduleSupportEmailMessage(getSelectedInstallation()));
					}
					
					setSlot_id(0);
					setBook_dt_str(null);
					setEdit_installer_id(0);
					setEdit_slots(null);
					setSelectedInstallation(null);
					setBooking(null);
					searchSchedules();
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage("Booking updated successfully!");
				}
				catch(Exception ex)
				{
					gDAO.rollback();
					an.setType("ERROR");
					an.setSubject("Error");
					an.setMessage("Message: " + ex.getMessage() + "!");
				}
			}
			else
			{
				setEdit_slots(null);
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("The slot you requested is filled! Please select a different slot!");
			}
			gDAO.destroy();
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("Please input the valid details for this edit!");
		}
		appNotifBean.getAppNotifications().add(an);
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
	
	public void downloadJobCard() throws IOException
	{
		if(getSelectedInstallation() != null)
		{
			Document document = new Document();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			boolean error = false;
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
				document.addAuthor("RMS");
				document.addCreationDate();
				document.addCreator("RMS");
				document.addSubject("JobCard");
				document.addTitle("Job Card");
				
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
				c = new PdfPCell(new Paragraph("JOB CARD", font));
				c.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				c.setBorder(0);
				headerTable.addCell(c);
				
				font = new Font(helvetica, 10, Font.NORMAL|Font.BOLD);
				c = new PdfPCell(new Paragraph("JOB CODE: " + getSelectedInstallation().getJobCode(), font));
				c.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
				c.setBorder(0);
				headerTable.addCell(c);
				
				document.add(headerTable);
				
				font = new Font(helvetica, 12, Font.NORMAL|Font.BOLD);
				Paragraph p = new Paragraph("CLIENT'S DATA", font);
				p.setAlignment(Paragraph.ALIGN_CENTER);
				document.add(p);
				
				PdfPTable pdfTable = new PdfPTable(2);
				
				font = new Font(helvetica, 8, Font.BOLDITALIC);
				pdfTable.addCell(new Paragraph("COMPANY/CLIENT", font));
				pdfTable.addCell(new Paragraph("EMAIL ADDRESS", font));
				font = new Font(helvetica, 8, Font.NORMAL);
				pdfTable.addCell(new Paragraph(getSelectedInstallation().getCustomer().getFirstname() + " " + getSelectedInstallation().getCustomer().getLastname(), font));
				pdfTable.addCell(new Paragraph(getSelectedInstallation().getCustomer().getUser().getUsername(), font));
				document.add(pdfTable);
				
				document.add(Chunk.NEWLINE);document.add(Chunk.NEWLINE);
				
				pdfTable = new PdfPTable(2);
				
				font = new Font(helvetica, 8, Font.BOLDITALIC);
				pdfTable.addCell(new Paragraph("TEL NO", font));
				pdfTable.addCell(new Paragraph("ADDRESS", font));
				font = new Font(helvetica, 8, Font.NORMAL);
				pdfTable.addCell(new Paragraph(getSelectedInstallation().getCustomer().getPhoneNo(), font));
				pdfTable.addCell(new Paragraph(getSelectedInstallation().getCustomer().getAddress(), font));
				document.add(pdfTable);
				
				document.add(Chunk.NEWLINE);document.add(Chunk.NEWLINE);
				
				font = new Font(helvetica, 12, Font.NORMAL|Font.BOLD);
				p = new Paragraph("VEHICLE PARTICULARS", font);
				p.setAlignment(Paragraph.ALIGN_CENTER);
				document.add(p);
				
				document.add(Chunk.NEWLINE);document.add(Chunk.NEWLINE);
				
				pdfTable = new PdfPTable(3);
				
				font = new Font(helvetica, 8, Font.BOLDITALIC);
				pdfTable.addCell(new Paragraph("BRAND/MAKE", font));
				pdfTable.addCell(new Paragraph("COLOR", font));
				pdfTable.addCell(new Paragraph("ODOMETER", font));
				font = new Font(helvetica, 8, Font.NORMAL);
				pdfTable.addCell(new Paragraph(getSelectedInstallation().getVehicleMake() != null ? getSelectedInstallation().getVehicleMake().getName() : "N/A", font));
				pdfTable.addCell(new Paragraph(getSelectedInstallation().getVehicleColor(), font));
				pdfTable.addCell(new Paragraph(getSelectedInstallation().getVehicleOdometer().toPlainString(), font));
				
				font = new Font(helvetica, 8, Font.BOLDITALIC);
				pdfTable.addCell(new Paragraph("MODEL/YEAR", font));
				pdfTable.addCell(new Paragraph("IMEI NO", font));
				pdfTable.addCell(new Paragraph("REGISTRATION NO", font));
				font = new Font(helvetica, 8, Font.NORMAL);
				pdfTable.addCell(new Paragraph(getSelectedInstallation().getVehicleModel(), font));
				pdfTable.addCell(new Paragraph(getSelectedInstallation().getVehicleIMEINo(), font));
				pdfTable.addCell(new Paragraph(getSelectedInstallation().getVehicleRegNum(), font));
				
				font = new Font(helvetica, 8, Font.BOLDITALIC);
				pdfTable.addCell(new Paragraph("ENGINE NO", font));
				pdfTable.addCell(new Paragraph("CHASIS", font));
				pdfTable.addCell(new Paragraph("UNIT TYPE", font));
				font = new Font(helvetica, 8, Font.NORMAL);
				pdfTable.addCell(new Paragraph(getSelectedInstallation().getVehicleEngineNo(), font));
				pdfTable.addCell(new Paragraph(getSelectedInstallation().getVehicleChasis(), font));
				pdfTable.addCell(new Paragraph(getSelectedInstallation().getUnitType(), font));
				
				font = new Font(helvetica, 8, Font.BOLDITALIC);
				pdfTable.addCell(new Paragraph("NETWORK", font));
				pdfTable.addCell(new Paragraph("SIMCARD NO", font));
				pdfTable.addCell(new Paragraph("", font));
				font = new Font(helvetica, 8, Font.NORMAL);
				pdfTable.addCell(new Paragraph(getSelectedInstallation().getNetwork(), font));
				pdfTable.addCell(new Paragraph(getSelectedInstallation().getSimcardNo(), font));
				pdfTable.addCell(new Paragraph("", font));
				
				document.add(pdfTable);
				
				document.add(Chunk.NEWLINE);document.add(Chunk.NEWLINE);
				
				font = new Font(helvetica, 12, Font.NORMAL|Font.BOLD);
				p = new Paragraph("REMARKS", font);
				p.setAlignment(Paragraph.ALIGN_LEFT);
				document.add(p);
				
				font = new Font(helvetica, 10, Font.NORMAL);
				p = new Paragraph(getSelectedInstallation().getInstallerRemarks(), font);
				p.setAlignment(Paragraph.ALIGN_LEFT);
				document.add(p);
				
				document.close();
			}
			catch(Exception ex)
			{
				error = true;
				// Put message to display on page here
			}
			
			if(!error)
			{
				FacesContext fc = FacesContext.getCurrentInstance();
				ExternalContext ec = fc.getExternalContext();
				
				ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
				ec.setResponseContentType("application/pdf"); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
				ec.setResponseContentLength(baos.size()); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
				ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + getSelectedInstallation().getJobCode() + ".pdf\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.
				ec.setResponseHeader("Expires", "0");
				ec.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
				ec.setResponseHeader("Pragma", "public");
				
				OutputStream output = ec.getResponseOutputStream();
				// Now you can write the InputStream of the file to the above OutputStream the usual way.
				baos.writeTo(output);
				ec.responseFlushBuffer();
				
				fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void searchSchedules()
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		setCheckedInSchedules(null);
		if((getStart_dt_str() != null && getStart_dt_str().trim().length() > 0 &&
				getEnd_dt_str() != null && getEnd_dt_str().trim().length() > 0) ||
				getJobCode() != null && getJobCode().trim().length() > 0)
		{
			GeneralDAO gDAO = new GeneralDAO();
			
			String qry = "Select e from InstallerLocationJobSchedule e where e.customer.partner=:partner and e.checkedIn=:checkedIn and e.completed=:completed";
			
			if(getStart_dt_str() != null && getStart_dt_str().trim().length() > 0 &&
				getEnd_dt_str() != null && getEnd_dt_str().trim().length() > 0)
			{
				qry += " and e.booked_dt between :st_dt and :end_dt";
			}
			
			if(getInstaller_id() > 0)
			{
				qry += " and e.installer=:installer";
			}
			
			if(getJobCode() != null && getJobCode().trim().length() > 0)
			{
				qry += " and e.jobCode like :jobCode";
			}
			
			qry += " order by e.booked_dt, e.slot.start_hour";
			
			Query q = gDAO.createQuery(qry);
			q.setParameter("partner", getPartner());
			q.setParameter("checkedIn", false);
			q.setParameter("completed", false);
			
			
			Date st_dt = new Date(), end_dt = new Date();
			boolean error = false;
			if(getStart_dt_str() != null && getStart_dt_str().trim().length() > 0 &&
					getEnd_dt_str() != null && getEnd_dt_str().trim().length() > 0)
			{
				try
				{
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					st_dt = sdf.parse(getStart_dt_str());
					end_dt = sdf.parse(getEnd_dt_str());
				}
				catch(Exception ex)
				{
					//ex.printStackTrace();
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("One of the supplied date values is in an invalid format!");
					error = true;
				}
			}
			Object installer = null;
			if(!error)
			{
				if(getInstaller_id() > 0)
				{
					installer = gDAO.find(InstallerLocation.class, getInstaller_id());
					if(installer == null)
					{
						error = true;
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Could not recognize the selected Agent!");
					}
				}
			}
			
			if(!error)
			{
				if(getStart_dt_str() != null && getStart_dt_str().trim().length() > 0 &&
						getEnd_dt_str() != null && getEnd_dt_str().trim().length() > 0)
				{
					q.setParameter("st_dt", st_dt);
					q.setParameter("end_dt", end_dt);
				}
				
				if(getInstaller_id() > 0)
					q.setParameter("installer", (InstallerLocation)installer);
				
				if(getJobCode() != null && getJobCode().trim().length() > 0)
					q.setParameter("jobCode", "%"+getJobCode()+"%");
				
				Object list = gDAO.search(q, 0);
				if(list != null)
				{
					checkedInSchedules = (List<InstallerLocationJobSchedule>)list;
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage(checkedInSchedules.size() + " record(s) found!");
				}
				else
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("No record found!");
				}
			}
			gDAO.destroy();
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("Please select a valid date range at least!");
		}
		appNotifBean.getAppNotifications().add(an);
	}
	
	@SuppressWarnings("unchecked")
	public void returnDevices()
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		if(getItem_id() > 0 && getItem_return_count() > 0)
		{
			ItemMove im = new ItemMove();
			
			GeneralDAO gDAO = new GeneralDAO();
			
			Object itm = gDAO.find(Item.class, getItem_id());
			if(itm != null)
				im.setItem((Item)itm);
			
			if(im.getItem() != null)
			{
				im.setCreatedBy(userBean.getSessionUser());
				im.setCrt_dt(new Date());
				im.setInstaller(userBean.getSessionInstaller());
				im.setCount(getItem_return_count());
				im.setMove_dt(new Date());
				im.setMoveType(getReturn_reason());
				im.setRemarks(getReturn_remarks());
				
				InstallerLocationItem ili = null;
				boolean iliExists = false;
				Hashtable<String, Object> params = new Hashtable<String, Object>();
				params.put("installer", im.getInstaller());
				params.put("item", im.getItem());
				
				Object iliObj = gDAO.search("InstallerLocationItem", params);
				if(iliObj != null)
				{
					List<InstallerLocationItem> list = (List<InstallerLocationItem>)iliObj;
					for(InstallerLocationItem e : list)
						ili = e;
				}
				
				if(ili == null)
				{
					ili = new InstallerLocationItem();
					ili.setCrt_dt(new Date());
					ili.setCreatedBy(userBean.getSessionUser());
					ili.setInstaller(im.getInstaller());
					ili.setItem(im.getItem());
					ili.setCount(-im.getCount());
					im.setBefore_balance(0);
					im.setNew_balance(-im.getCount());
				}
				else
				{
					iliExists = true;
					im.setBefore_balance(ili.getCount());
					ili.setCount(ili.getCount() - im.getCount());
					im.setNew_balance(ili.getCount());
				}
				
				gDAO.startTransaction();
				gDAO.save(im);
				
				if(ili != null && iliExists)
					gDAO.update(ili);
				else if(ili != null && !iliExists)
					gDAO.save(ili);
				try
				{
					gDAO.commit();
					setItem_id(0);setReturn_reason(null);setReturn_remarks(null);
					setItem_return_count(0);
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage("Device(s) returned successfully!");
				}
				catch(Exception ex)
				{
					gDAO.rollback();
					an.setType("ERROR");
					an.setSubject("Error");
					an.setMessage("Message: " + ex.getMessage() + "!");
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("Can't detect the device selected!");
			}
			gDAO.destroy();
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("Please select a valid device and input the count to return!");
		}
		appNotifBean.getAppNotifications().add(an);
	}
	
	public void confirmJob()
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		if(getSelectedInstallation() != null && getSelectedInstallation().getConfirmationRemarks() != null && getSelectedInstallation().getConfirmationRemarks().trim().length() > 0)
		{
			if(getSelectedInstallation().isRequire_confirmation())
			{
				getSelectedInstallation().setConfirmed(true);
				GeneralDAO gDAO = new GeneralDAO();
				gDAO.startTransaction();
				gDAO.update(getSelectedInstallation());
				try
				{
					gDAO.commit();
					setSelectedInstallation(null);
					setJobsPendingConfirmation(null);
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage("Job confirmed successfully!");
				}
				catch(Exception ex)
				{
					gDAO.rollback();
					an.setType("ERROR");
					an.setSubject("Error");
					an.setMessage("Message: " + ex.getMessage() + "!");
				}
				gDAO.destroy();
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("The job you are trying to confirm does not require confirmation!");
			}
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("Please select a valid completed job and input your remarks!");
		}
		appNotifBean.getAppNotifications().add(an);
	}
	
	public void addJobDeviceUse()
	{
		if(getItem_id() > 0 && getItem_used_count() > 0)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Item item = null;
			Object itm = gDAO.find(Item.class, getItem_id());
			if(itm != null)
				item = (Item)itm;
			
			boolean exists = false;
			for(InstallationDeviceUse e : getInstallationDeviceUseList())
			{
				if(e.getItem_id() == getItem_id())
				{
					exists = true;
					break;
				}
			}
			
			if(item != null && !exists)
			{
				InstallationDeviceUse e = new InstallationDeviceUse();
				e.setCount(getItem_used_count());
				e.setItem(item);
				e.setCreatedBy(userBean.getSessionUser());
				e.setCrt_dt(new Date());
				e.setItem_id(getItem_id());
				e.setItem_count(getItem_used_count());
				
				getInstallationDeviceUseList().add(e);
			}
			gDAO.destroy();
		}
	}
	public void removeJobDeviceUse(long item_id)
	{
		for(int i=0; i<getInstallationDeviceUseList().size(); i++)
		{
			InstallationDeviceUse e = getInstallationDeviceUseList().get(i);
			if(e.getItem_id() == item_id)
			{
				getInstallationDeviceUseList().remove(i);
				break;
			}
		}
	}
	
	public void addItemToProduct()
	{
		userBean.setActiveTab("create");
		if(getItem_id() > 0 && getItem_used_count() > 0)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Item item = null;
			Object itm = gDAO.find(Item.class, getItem_id());
			if(itm != null)
				item = (Item)itm;
			
			boolean exists = false;
			for(ProductItem e : getProductItemList())
			{
				if(e.getItem().getId().longValue() == getItem_id())
				{
					exists = true;
					break;
				}
			}
			
			if(item != null && !exists)
			{
				ProductItem e = new ProductItem();
				e.setItem_count(getItem_used_count());
				e.setItem(item);
				e.setCrt_dt(new Date());
				
				getProductItemList().add(e);
			}
			gDAO.destroy();
		}
	}
	public void removeItemFromProduct(long item_id)
	{
		userBean.setActiveTab("create");
		for(int i=0; i<getProductItemList().size(); i++)
		{
			ProductItem e = getProductItemList().get(i);
			if(e.getItem().getId().longValue() == item_id)
			{
				getProductItemList().remove(i);
				break;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void completeJob()
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		if(selCheckedInJob != null)
		{
			selCheckedInJob.setCompleted(true);
			GeneralDAO gDAO = new GeneralDAO();
			
			gDAO.startTransaction();
			if(getInstallationDeviceUseList().size() > 0)
			{
				for(InstallationDeviceUse e : getInstallationDeviceUseList())
				{
					if(e.getItem_id() > 0 && e.getItem_count() > 0)
					{
						Item item = e.getItem();
						
						if(item != null)
						{
							e.setInstallation(selCheckedInJob);
							gDAO.save(e);
							
							InstallerLocationItem ili = null;
							Hashtable<String, Object> params = new Hashtable<String, Object>();
							params.put("installer", selCheckedInJob.getInstaller());
							params.put("item", item);
							
							Object iliObj = gDAO.search("InstallerLocationItem", params);
							if(iliObj != null)
							{
								List<InstallerLocationItem> list = (List<InstallerLocationItem>)iliObj;
								for(InstallerLocationItem ilie : list)
									ili = ilie;
							}
							
							if(ili == null)
							{
								ili = new InstallerLocationItem();
								ili.setCrt_dt(new Date());
								ili.setCreatedBy(userBean.getSessionUser());
								ili.setInstaller(selCheckedInJob.getInstaller());
								ili.setItem(item);
								ili.setCount(-e.getItem_count());
								gDAO.save(ili);
							}
							else
							{
								ili.setCount(ili.getCount() - e.getItem_count());
								gDAO.update(ili);
							}
						}
					}
				}
			}
			
			if(getJob_installer_id() > 0)
			{
				Object agentObj = gDAO.find(Agent.class, getJob_installer_id());
				if(agentObj != null)
					selCheckedInJob.setAgent((Agent)agentObj);
			}
			gDAO.update(selCheckedInJob);
			
			CustomerProduct cp = null;
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("job", selCheckedInJob);
			Object cpObj = gDAO.search("CustomerProduct", params);
			if(cpObj != null)
			{
				List<CustomerProduct> list = (List<CustomerProduct>)cpObj;
				for(CustomerProduct e : list)
					cp = e;
			}
			
			if(cp != null)
			{
				cp.setStatus("ACTIVE");
				if(cp.getProductBooked() != null && cp.getProductBooked().isRequiresRenewal())
				{
					Calendar c = Calendar.getInstance();
					c.add(Calendar.YEAR, 1);
					cp.setRenewal_due_dt(c.getTime());
				}
				gDAO.update(cp);
				
				try
				{
					Notification n = new Notification();
					n.setCrt_dt(new Date());
					n.setNotified(false);
					n.setSubject("Installation Completed");
					n.setPage_url("dashboard");
					n.setUser(selCheckedInJob.getCustomer().getUser());
					n.setMessage("Your installation with Job Code: " + selCheckedInJob.getJobCode() + " has been completed.");
					
					gDAO.save(n);
					
					gDAO.commit();
					
					if(getCompleteJobCard() != null)
					{
						if(getCompleteJobCard().getContentType().indexOf("image")>=0)
						{
							try
							{
								InputStream in = getCompleteJobCard().getInputStream();
								//TODO: Write the file to disk here for future download, don't save to db, would be too large and affect performance
								File folder = new File("c:/files/jobcards");
								//String filename = FilenameUtils.getBaseName(getCompleteJobCard().getName()); 
								//String extension = FilenameUtils.getExtension(getCompleteJobCard().getName());
								File file = new File(folder, selCheckedInJob.getJobCode() + ".img");
								//File file = File.createTempFile(filename + "-", "." + extension, folder);
								Files.copy(in, file.toPath());
								in.close();
							}
							catch(Exception ex)
							{
								ex.printStackTrace();
							}
						}
					}
					
					userBean.sendEmail(new String[]{selCheckedInJob.getCustomer().getUser().getUsername()}, "Installation Completed on RMS", MessagesUtil.getJobScheduleCompletedEmailMessage(selCheckedInJob.getCustomer().getFirstname(), selCheckedInJob));
					userBean.sendSMS(selCheckedInJob.getCustomer().getPhoneNo(), MessagesUtil.getJobScheduleCompletedSMSMessage(selCheckedInJob.getCustomer().getFirstname(), selCheckedInJob));
					
					setSelCheckedInJob(null);
					setCheckedin_installation_id(0);
					setJob_installer_id(0);
					resetMyJobs();
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage("Job completed successfully!");
				}
				catch(Exception ex)
				{
					gDAO.rollback();
					an.setType("ERROR");
					an.setSubject("Error");
					an.setMessage("Message: " + ex.getMessage() + "!");
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("No corresponding customer-product mapping found for the selected installation!");
			}
			gDAO.destroy();
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("Please select a valid checked-in job!");
		}
		appNotifBean.getAppNotifications().add(an);
	}
	
	public void checkInJob()
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		if(selNotCheckInJob != null)
		{
			selNotCheckInJob.setCheckedIn(true);
			selNotCheckInJob.setPaid(true);
			
			GeneralDAO gDAO = new GeneralDAO();
			
			Object vtype = gDAO.find(VehicleType.class, getVtype_id());
			if(vtype != null)
				selNotCheckInJob.setVehicleType((VehicleType)vtype);
			
			Object vmake = gDAO.find(VehicleMake.class, getVmake_id());
			if(vmake != null)
				selNotCheckInJob.setVehicleMake((VehicleMake)vmake);
			
			gDAO.startTransaction();
			gDAO.update(selNotCheckInJob);
			try
			{
				Notification n = new Notification();
				n.setCrt_dt(new Date());
				n.setNotified(false);
				n.setSubject("Installation Checked-In");
				n.setPage_url("dashboard");
				n.setUser(selNotCheckInJob.getCustomer().getUser());
				n.setMessage("Your installation with Job Code: " + selNotCheckInJob.getJobCode() + " has been checked-in.");
				
				gDAO.save(n);
				
				gDAO.commit();
				
				userBean.sendEmail(new String[]{selNotCheckInJob.getCustomer().getUser().getUsername()}, "Installation Checked-In on RMS", MessagesUtil.getJobScheduleCheckInEmailMessage(selNotCheckInJob.getCustomer().getFirstname(), selNotCheckInJob));
				userBean.sendSMS(selNotCheckInJob.getCustomer().getPhoneNo(), MessagesUtil.getJobScheduleCheckInSMSMessage(selNotCheckInJob.getCustomer().getFirstname(), selNotCheckInJob));
				
				setSelNotCheckInJob(null);
				setNot_checkedin_installation_id(0);
				setVtype_id(0);setVmake_id(0);
				
				resetMyJobs();
				
				an.setType("SUCCESS");
				an.setSubject("Success");
				an.setMessage("Job Checked-In successfully!");
			}
			catch(Exception ex)
			{
				gDAO.rollback();
				an.setType("ERROR");
				an.setSubject("Error");
				an.setMessage("Message: " + ex.getMessage() + "!");
			}
			gDAO.destroy();
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("Please select a valid not-checked-in job!");
		}
		appNotifBean.getAppNotifications().add(an);
	}
	
	@SuppressWarnings("unchecked")
	public void signup()
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		if(isAgreed())
		{
			if(getCustomer().getFirstname() != null && getCustomer().getFirstname().trim().length() > 0 &&
					getCustomer().getLastname() != null && getCustomer().getFirstname().trim().length() > 0 &&
					getCusUser().getUsername() != null && getCusUser().getUsername().trim().length() > 0 &&
					getCusUser().getPassword() != null && getCusUser().getPassword().trim().length() > 0)
			{
				if(getCusUser().getPassword().equals(getConfirm_password()))
				{
					if(userBean.validatePassword(getCusUser().getPassword()))
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
						
						if(getDobstr() != null && getDobstr().trim().length() > 0)
						{
							try
							{
								SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
								Date dt = sdf.parse(getDobstr());
								getCustomer().setDob(dt);
							}catch(Exception ex){ex.printStackTrace();}
						}
						
						Hashtable<String, Object> params = new Hashtable<String, Object>();
						params.put("name", "CUSTOMER");
						Object rolesObj = gDAO.search("Role", params);
						if(rolesObj != null)
						{
							List<Role> roles = (List<Role>)rolesObj;
							for(Role e : roles)
								getCusUser().setRole(e);
							
							Partner partner = null;
							Object partnerObj = gDAO.find(Partner.class, getPartner_id());
							if(partnerObj != null)
								partner = (Partner)partnerObj;
							
							getCusUser().setActive(true);
							getCusUser().setCreatedBy(null);
							getCusUser().setCrt_dt(new Date());
							getCusUser().setPartner(partner);
							getCusUser().setType("CUSTOMER");
							
							gDAO.startTransaction();
							gDAO.save(getCusUser());
							
							getCustomer().setCreatedBy(getCusUser());
							getCustomer().setPartner(partner);
							getCustomer().setCrt_dt(new Date());
							
							getCustomer().setUser(getCusUser());
							gDAO.save(getCustomer());
							
							getCustomer().setUniqueID(generateCustomerReference(getCustomer().getId()));
							gDAO.update(getCustomer());
							
							boolean commit = true;
							// submit booking here
							Product selProduct = null;
							for(Product p : getProducts())
							{
								if(p.isSelected())
								{
									selProduct = p;
									break;
								}
							}
							getBooking().setCustomer(getCustomer());
							
							Object slot = gDAO.find(Slot.class, getSlot_id());
							if(slot != null)
							{
								getBooking().setSlot((Slot)slot);
								Calendar c = Calendar.getInstance();
								c.setTime(getBooking().getBooked_dt());
								c.set(Calendar.HOUR_OF_DAY, getBooking().getSlot().getStart_hour());
								c.set(Calendar.MINUTE, 0);
								c.set(Calendar.SECOND, 0);
								c.set(Calendar.MILLISECOND, 0);
								getBooking().setStart_dt(c.getTime());
								c.add(Calendar.HOUR_OF_DAY, getBooking().getSlot().getAmount_of_hours());
								getBooking().setEnd_dt(c.getTime());
							}
							
							if(selProduct != null && getSelInstaller() != null && getBooking().getPaymentType() != null && getBooking().getPaymentType().trim().length() > 0 &&
									getBooking().getCustomer() != null && getBooking().getSlot() != null && getBooking().getBooked_dt() != null)
							{
								Calendar todaymax = Calendar.getInstance();
								todaymax.set(Calendar.HOUR_OF_DAY, todaymax.getMaximum(Calendar.HOUR_OF_DAY));
								todaymax.set(Calendar.MINUTE, todaymax.getMaximum(Calendar.MINUTE));
								todaymax.set(Calendar.SECOND, todaymax.getMaximum(Calendar.SECOND));
								todaymax.set(Calendar.MILLISECOND, todaymax.getMaximum(Calendar.MILLISECOND));
								
								if(getBooking().getBooked_dt().after(todaymax.getTime()))
								{
									String jobCode = "PRTN" + (partner != null ? partner.getId().longValue() : "") + "-" + System.currentTimeMillis() + (new SecureRandom().nextInt(999) + 1);
									getBooking().setJobCode(jobCode);
									
									getBooking().setCheckedIn(false);
									getBooking().setCompleted(false);
									getBooking().setCost(getTotal_cost());
									getBooking().setCreatedBy(getCusUser());
									getBooking().setCrt_dt(new Date());
									getBooking().setInstaller(getSelInstaller());
									getBooking().setProductBooked(selProduct);
									// this booking will require customer service to confirm it if the product is a special product
									getBooking().setRequire_confirmation(selProduct.isSpecialProduct());
									
									Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and e.cancel=:cancel and e.booked_dt=:booked_dt and e.slot=:slot");
									q.setParameter("installer", getBooking().getInstaller());
									q.setParameter("booked_dt", getBooking().getBooked_dt());
									q.setParameter("slot", getBooking().getSlot());
									q.setParameter("cancel", false);
									
									boolean slot_taken = false;
									Object qret = gDAO.search(q, 0);
									if(qret != null)
									{
										List<InstallerLocationJobSchedule> list = (List<InstallerLocationJobSchedule>)qret;
										if(list.size() > 0)
											slot_taken = true;
									}
									if(!slot_taken)
									{
										gDAO.save(getBooking());
									}
									else
									{
										commit = false;
										setSlots(null);
										an.setType("ERROR");
										an.setSubject("Failed");
										an.setMessage("The slot you requested has been taken! Please select a different slot!");
									}
								}
								else
								{
									commit = false;
									an.setType("ERROR");
									an.setSubject("Failed");
									an.setMessage("Your book date must be from tomorrow upwards!");
								}
							}
							else
							{
								commit = false;
								an.setType("ERROR");
								an.setSubject("Failed");
								an.setMessage("All fields with the '*' sign are required!");
							}
							
							if(commit)
							{
								Notification n = new Notification();
								n.setCrt_dt(new Date());
								n.setNotified(false);
								n.setSubject("Welcome");
								n.setPage_url("dashboard");
								n.setUser(getCusUser());
								n.setMessage("Welcome and thank you for purchasing one of our products!!!");
								
								gDAO.save(n);
								
								try
								{
									gDAO.commit();
									
									userBean.sendEmail(new String[]{getCusUser().getUsername()}, "Welcome to RMS", MessagesUtil.getSignUpEmailMessage(getCustomer().getFirstname(), getCusUser().getUsername(), getConfirm_password(), getCustomer().getUniqueID()));
									userBean.sendSMS(getCustomer().getPhoneNo(), MessagesUtil.getSignUpSMSMessage(getCustomer()));
									
									userBean.sendEmail(new String[]{getCusUser().getUsername()}, "Installation Schedule on RMS", MessagesUtil.getJobScheduleEmailMessage(getCustomer().getFirstname(), getBooking()));
									userBean.sendSMS(getCustomer().getPhoneNo(), MessagesUtil.getJobScheduleSMSMessage(getCustomer().getFirstname(), getBooking()));
									
									userBean.sendEmail(new String[]{getBooking().getInstaller().getUser().getUsername()}, "Installation Schedule on RMS", MessagesUtil.getJobScheduleInstallerEmailMessage(getBooking()));
									
									// send notification to support
									AppConfiguration currectAppConfig = getCurrentAppConfig();
									if(currectAppConfig != null && currectAppConfig.getCustomerSupportEmail() != null && currectAppConfig.getCustomerSupportEmail().trim().length() > 0)
									{
										String[] supportEmails = currectAppConfig.getCustomerSupportEmail().split(",");
										userBean.sendEmail(supportEmails, "Installation Schedule on RMS", MessagesUtil.getJobScheduleSupportEmailMessage(getBooking()));
									}
									
									setCustomer(null);setCustomers(null);setSelCustomer(null);setCusUser(null);
									setBooking(null);setSlots(null);setProducts(null);
									setSelInstaller(null);setSelProduct(null);
									setCustomer_id(0);setSlot_id(0);setTotal_cost(0);setPtype_id(0);
									setBook_dt_str(null);
									an.setType("SUCCESS");
									an.setSubject("Success");
									an.setMessage("Your account is setup and your product booking placed successfully!");
								} catch(Exception ex){
									gDAO.rollback();
									an.setType("ERROR");
									an.setSubject("Error");
									an.setMessage("Message: " + ex.getMessage() + "!");
								}
							}
							else
							{
								gDAO.rollback();
							}
							appNotifBean.getAppNotifications().add(an);
						}
						else
						{
							an.setType("ERROR");
							an.setSubject("Failed");
							an.setMessage("Could not get the customer role!");
							appNotifBean.getAppNotifications().add(an);
						}
						gDAO.destroy();
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Username is not in a valid email format!");
						appNotifBean.getAppNotifications().add(an);
					}
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Password does not match the password policy. Please view the help page for more information!");
						appNotifBean.getAppNotifications().add(an);
					}
				}
				else
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Password fields were not the same!");
					appNotifBean.getAppNotifications().add(an);
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("All fields with the '*' sign are required!");
				appNotifBean.getAppNotifications().add(an);
			}
		}
		else
		{
			an.setType("ERROR");
			an.setSubject("Failed");
			an.setMessage("Please agree to the Terms and Condition");
			appNotifBean.getAppNotifications().add(an);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void report(int i)
	{
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		switch(i)
		{
		case 1: // installation report
		{
			setSearchedInstallation(null);
			setExpectedEarnings(null);setActualEarnings(null);
			totalCount=0;totalPendingCheckIn=0;totalCheckInPaid=0;totalCompleted=0;totalCanceled=0;totalRConfirm=0;totalConfirm=0;
			
			if(getInstaller_id() > 0 && getStart_dt_str() != null && getStart_dt_str().trim().length() > 0 &&
					getEnd_dt_str() != null && getEnd_dt_str().trim().length() > 0)
			{
				Date st = null, et = null;
				try
				{
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					st = sdf.parse(getStart_dt_str());
					et = sdf.parse(getEnd_dt_str());
				}
				catch(Exception ex)
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Invalid date formats detected!");
					appNotifBean.getAppNotifications().add(an);
				}
				
				if(st != null && et != null)
				{
					GeneralDAO gDAO = new GeneralDAO();
					Object installer = gDAO.find(InstallerLocation.class, getInstaller_id());
					if(installer != null)
					{
						InstallerLocation il = (InstallerLocation)installer;
						Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and e.customer.partner=:partner and (e.booked_dt between :st and :et) order by e.booked_dt desc");
						q.setParameter("installer", il);
						q.setParameter("partner", getPartner());
						q.setParameter("st", st);
						q.setParameter("et", et);
						
						Object list = gDAO.search(q, 0);
						if(list != null)
						{
							setSearchedInstallation((List<InstallerLocationJobSchedule>)list);
							an.setType("SUCCESS");
							an.setSubject("Success");
							an.setMessage(getSearchedInstallation().size() + " record(s) found!");
							appNotifBean.getAppNotifications().add(an);
							
							for(InstallerLocationJobSchedule e : getSearchedInstallation())
							{
								totalCount += 1;
								setExpectedEarnings(getExpectedEarnings().add(new BigDecimal(e.getCost())));
								if(!e.isCancel() && e.isPaid() && !e.isRefund())
									setActualEarnings(getActualEarnings().add(new BigDecimal(e.getCost())));
								
								if(!e.isCancel() && !e.isCheckedIn() && !e.isPaid())
									totalPendingCheckIn += 1;
								
								if(!e.isCancel() && e.isCheckedIn() && e.isPaid())
									totalCheckInPaid += 1;
								
								if(!e.isCancel() && e.isCompleted())
								{
									totalCompleted += 1;
									if(e.isRequire_confirmation())
									{
										totalRConfirm += 1;
										if(e.isConfirmed())
											totalConfirm += 1;
									}
									if(e.isRefund())
										totalRefund += 1;
								}
								
								if(e.isCancel())
									totalCanceled += 1;
							}
							// Expected Earnings | Actual Earnings | Total Count | Total Check-In/Paid | Total Completed | Total Canceled | Total Refunded
						}
						else
						{
							an.setType("ERROR");
							an.setSubject("Failed");
							an.setMessage("No result!");
							appNotifBean.getAppNotifications().add(an);
						}
					}
					gDAO.destroy();
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("Please select an Agent and input a date range!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 2: // Total Service/Product subscription report
		{
			setTotalSubscriptionReport(null);
			
			if(getStart_dt_str() != null && getStart_dt_str().trim().length() > 0 &&
					getEnd_dt_str() != null && getEnd_dt_str().trim().length() > 0)
			{
				Date st = null, et = null;
				try
				{
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					st = sdf.parse(getStart_dt_str());
					et = sdf.parse(getEnd_dt_str());
				}
				catch(Exception ex)
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Invalid date formats detected!");
					appNotifBean.getAppNotifications().add(an);
				}
				
				if(st != null && et != null)
				{
					GeneralDAO gDAO = new GeneralDAO();
					Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.customer.partner=:partner and (e.booked_dt between :st and :et) order by e.booked_dt desc");
					q.setParameter("partner", getPartner());
					q.setParameter("st", st);
					q.setParameter("et", et);
					Object list = gDAO.search(q, 0);
					if(list != null)
					{
						setTotalSubscriptionReport((List<InstallerLocationJobSchedule>)list);
						an.setType("SUCCESS");
						an.setSubject("Success");
						an.setMessage(getTotalSubscriptionReport().size() + " record(s) found!");
						appNotifBean.getAppNotifications().add(an);
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("No result!");
						appNotifBean.getAppNotifications().add(an);
					}
					gDAO.destroy();
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("Please input a date range!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 3: // Total renewal subscription  report
		{
			setTotalRenewalReport(null);
			
			if(getStart_dt_str() != null && getStart_dt_str().trim().length() > 0 &&
					getEnd_dt_str() != null && getEnd_dt_str().trim().length() > 0)
			{
				Date st = null, et = null;
				try
				{
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					st = sdf.parse(getStart_dt_str());
					et = sdf.parse(getEnd_dt_str());
				}
				catch(Exception ex)
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Invalid date formats detected!");
					appNotifBean.getAppNotifications().add(an);
				}
				
				if(st != null && et != null)
				{
					GeneralDAO gDAO = new GeneralDAO();
					Query q = gDAO.createQuery("Select e from CustomerProduct e where e.customer.partner=:partner and (e.last_renew_dt between :st and :et) order by e.last_renew_dt desc");
					q.setParameter("partner", getPartner());
					q.setParameter("st", st);
					q.setParameter("et", et);
					Object list = gDAO.search(q, 0);
					if(list != null)
					{
						setTotalRenewalReport((List<CustomerProduct>)list);
						an.setType("SUCCESS");
						an.setSubject("Success");
						an.setMessage(getTotalRenewalReport().size() + " record(s) found!");
						appNotifBean.getAppNotifications().add(an);
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("No result!");
						appNotifBean.getAppNotifications().add(an);
					}
					gDAO.destroy();
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("Please input a date range!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 4: // Sales Agent sales summary report
		{
			setSalesAgentSummaryReport(null);
			
			if(getStart_dt_str() != null && getStart_dt_str().trim().length() > 0 &&
					getEnd_dt_str() != null && getEnd_dt_str().trim().length() > 0)
			{
				Date st = null, et = null;
				try
				{
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					st = sdf.parse(getStart_dt_str());
					et = sdf.parse(getEnd_dt_str());
				}
				catch(Exception ex)
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Invalid date formats detected!");
					appNotifBean.getAppNotifications().add(an);
				}
				
				if(st != null && et != null)
				{
					GeneralDAO gDAO = new GeneralDAO();
					Hashtable<String, Object> params = new Hashtable<String, Object>();
					params.put("user.partner", getPartner());
					Object allSalesAgentObj = gDAO.search("SalesAgent", params);
					if(allSalesAgentObj != null)
					{
						setSalesAgentSummaryReport(new ArrayList<String[]>());
						List<SalesAgent> allSalesAgentList = (List<SalesAgent>)allSalesAgentObj;
						for(SalesAgent e : allSalesAgentList)
						{
							Query q = gDAO.createQuery("Select e from CustomerProduct e where e.createdBy=:salesAgent and (e.booked_dt between :st and :et) order by e.booked_dt desc");
							q.setParameter("salesAgent", e.getUser());
							q.setParameter("st", st);
							q.setParameter("et", et);
							Object list = gDAO.search(q, 0);
							if(list != null)
							{
								String[] rec = new String[5];
								List<CustomerProduct> jList = (List<CustomerProduct>)list;
								long totalCount=0, paidCount=0, completedCount=0, canceledCount=0;
								BigDecimal totalSum=new BigDecimal(0.00), paidSum=new BigDecimal(0.00), completedSum=new BigDecimal(0.00), canceledSum=new BigDecimal(0.00);
								
								for(CustomerProduct j : jList)
								{
									totalCount+=1;totalSum = totalSum.add(new BigDecimal(j.getPurchasedAmount()));
									if(j.getPurchaseTransaction() != null && j.getPurchaseTransaction().getStatus().equalsIgnoreCase("PAID"))
									{
										paidCount += 1;
										paidSum = paidSum.add(new BigDecimal(j.getPurchasedAmount()));
									}
									if(j.getJob() != null && j.getJob().isCompleted())
									{
										completedCount+=1;
										completedSum = completedSum.add(new BigDecimal(j.getPurchasedAmount()));
									}
									if(j.getJob() != null && j.getJob().isCancel())
									{
										canceledCount += 1;
										canceledSum = canceledSum.add(new BigDecimal(j.getPurchasedAmount()));
									}
								}
								rec[0] = e.getFirstname() + " " + e.getLastname();
								rec[1] = totalCount + "/" + totalSum.toPlainString();
								rec[2] = paidCount + "/" + paidSum.toPlainString();
								rec[3] = completedCount + "/" + completedSum.toPlainString();
								rec[4] = canceledCount + "/" + canceledSum.toPlainString();
								
								getSalesAgentSummaryReport().add(rec);
							}
						}
						
						if(getSalesAgentSummaryReport().size() > 0)
						{
							an.setType("SUCCESS");
							an.setSubject("Success");
							an.setMessage(getSalesAgentSummaryReport().size() + " record(s) found!");
							appNotifBean.getAppNotifications().add(an);
						}
						else
						{
							an.setType("ERROR");
							an.setSubject("Failed");
							an.setMessage("No result!");
							appNotifBean.getAppNotifications().add(an);
						}
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("No sales agents found!");
						appNotifBean.getAppNotifications().add(an);
					}
					gDAO.destroy();
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("Please input a date range!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 5: // Installation Agent Job summary report
		{
			setInstallationPointSummaryReport(null);
			
			if(getStart_dt_str() != null && getStart_dt_str().trim().length() > 0 &&
					getEnd_dt_str() != null && getEnd_dt_str().trim().length() > 0)
			{
				Date st = null, et = null;
				try
				{
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					st = sdf.parse(getStart_dt_str());
					et = sdf.parse(getEnd_dt_str());
				}
				catch(Exception ex)
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Invalid date formats detected!");
					appNotifBean.getAppNotifications().add(an);
				}
				
				if(st != null && et != null)
				{
					GeneralDAO gDAO = new GeneralDAO();
					Object allInstallationPointObj = gDAO.findAll("InstallerLocation");
					if(allInstallationPointObj != null)
					{
						setInstallationPointSummaryReport(new ArrayList<String[]>());
						List<InstallerLocation> allInstallationPointList = (List<InstallerLocation>)allInstallationPointObj;
						for(InstallerLocation e : allInstallationPointList)
						{
							Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and (e.booked_dt between :st and :et) order by e.booked_dt desc");
							q.setParameter("installer", e);
							q.setParameter("st", st);
							q.setParameter("et", et);
							Object list = gDAO.search(q, 0);
							if(list != null)
							{
								String[] rec = new String[4];
								List<InstallerLocationJobSchedule> jList = (List<InstallerLocationJobSchedule>)list;
								long totalCount=0, paidCount=0, completedCount=0;
								BigDecimal totalSum=new BigDecimal(0.00), paidSum=new BigDecimal(0.00), completedSum=new BigDecimal(0.00);
								
								for(InstallerLocationJobSchedule j : jList)
								{
									totalCount+=1;totalSum = totalSum.add(new BigDecimal(j.getCost()));
									if(j.isPaid())
									{
										paidCount += 1;
										paidSum = paidSum.add(new BigDecimal(j.getCost()));
									}
									if(j.isCompleted())
									{
										completedCount+=1;
										completedSum = completedSum.add(new BigDecimal(j.getCost()));
									}
								}
								rec[0] = e.getCompanyName();
								rec[1] = totalCount + "/" + totalSum.toPlainString();
								rec[2] = paidCount + "/" + paidSum.toPlainString();
								rec[3] = completedCount + "/" + completedSum.toPlainString();
								
								getInstallationPointSummaryReport().add(rec);
							}
						}
						
						if(getInstallationPointSummaryReport().size() > 0)
						{
							an.setType("SUCCESS");
							an.setSubject("Success");
							an.setMessage(getInstallationPointSummaryReport().size() + " record(s) found!");
							appNotifBean.getAppNotifications().add(an);
						}
						else
						{
							an.setType("ERROR");
							an.setSubject("Failed");
							an.setMessage("No result!");
							appNotifBean.getAppNotifications().add(an);
						}
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("No sales agents found!");
						appNotifBean.getAppNotifications().add(an);
					}
					gDAO.destroy();
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("Please input a date range!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 6: // Sales Agent Commission Payable
		{
			setSalesAgentCommissionReport(null);
			
			if(getStart_dt_str() != null && getStart_dt_str().trim().length() > 0 &&
					getEnd_dt_str() != null && getEnd_dt_str().trim().length() > 0)
			{
				Date st = null, et = null;
				try
				{
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					st = sdf.parse(getStart_dt_str());
					et = sdf.parse(getEnd_dt_str());
				}
				catch(Exception ex)
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Invalid date formats detected!");
					appNotifBean.getAppNotifications().add(an);
				}
				
				if(st != null && et != null)
				{
					GeneralDAO gDAO = new GeneralDAO();
					Hashtable<String, Object> params = new Hashtable<String, Object>();
					params.put("user.partner", userBean.getSessionPartner());
					Object allSalesAgentObj = gDAO.search("SalesAgent", params);
					if(allSalesAgentObj != null)
					{
						setSalesAgentCommissionReport(new ArrayList<String[]>());
						List<SalesAgent> allSalesAgentList = (List<SalesAgent>)allSalesAgentObj;
						for(SalesAgent e : allSalesAgentList)
						{
							Query q = gDAO.createQuery("Select e from CustomerProduct e where e.createdBy=:salesAgent and (e.booked_dt between :st and :et) order by e.booked_dt desc");
							q.setParameter("salesAgent", e.getUser());
							q.setParameter("st", st);
							q.setParameter("et", et);
							Object list = gDAO.search(q, 0);
							if(list != null)
							{
								String[] rec = new String[4];
								
								List<CustomerProduct> jList = (List<CustomerProduct>)list;
								long totalCount=0, paidCount=0;
								BigDecimal totalSum=new BigDecimal(0.00), paidSum=new BigDecimal(0.00), commissionSum=new BigDecimal(0.00);
								
								for(CustomerProduct j : jList)
								{
									ProductTypeCommission commission = null;
									for(ProductTypeCommission c : getPtypeCommissionList())
										commission = c;
									
									totalCount+=1;totalSum = totalSum.add(new BigDecimal(j.getPurchasedAmount()));
									if((j.getPurchaseTransaction() != null && j.getPurchaseTransaction().getStatus().equalsIgnoreCase("PAID")) && 
										(j.getJob() != null && !j.getJob().isCancel() && !j.getJob().isRefund()))
									{
										paidCount += 1;
										paidSum = paidSum.add(new BigDecimal(j.getPurchasedAmount()));
										
										if(commission != null && commission.getSalesAgentCommission() > 0)
										{
											BigDecimal cc = new BigDecimal(commission.getSalesAgentCommission()).multiply(new BigDecimal(j.getPurchasedAmount()));
											cc = cc.divide(new BigDecimal(100.00));
											cc = cc.setScale(2, RoundingMode.HALF_UP);
											commissionSum = commissionSum.add(cc);
										}
									}
								}
								rec[0] = e.getFirstname() + " " + e.getLastname();
								rec[1] = totalCount + "/" + totalSum.toPlainString();
								rec[2] = paidCount + "/" + paidSum.toPlainString();
								rec[3] = commissionSum.toPlainString();
								
								getSalesAgentCommissionReport().add(rec);
							}
						}
						
						if(getSalesAgentCommissionReport().size() > 0)
						{
							an.setType("SUCCESS");
							an.setSubject("Success");
							an.setMessage(getSalesAgentCommissionReport().size() + " record(s) found!");
							appNotifBean.getAppNotifications().add(an);
						}
						else
						{
							an.setType("ERROR");
							an.setSubject("Failed");
							an.setMessage("No result!");
							appNotifBean.getAppNotifications().add(an);
						}
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("No sales agents found!");
						appNotifBean.getAppNotifications().add(an);
					}
					gDAO.destroy();
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("Please input a date range!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 7: // Installation Agent Commission Payable
		{
			setInstallationPointCommissionReport(null);
			
			if(getStart_dt_str() != null && getStart_dt_str().trim().length() > 0 &&
					getEnd_dt_str() != null && getEnd_dt_str().trim().length() > 0)
			{
				Date st = null, et = null;
				try
				{
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					st = sdf.parse(getStart_dt_str());
					et = sdf.parse(getEnd_dt_str());
				}
				catch(Exception ex)
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Invalid date formats detected!");
					appNotifBean.getAppNotifications().add(an);
				}
				
				if(st != null && et != null)
				{
					GeneralDAO gDAO = new GeneralDAO();
					Object allAgentObj = gDAO.findAll("InstallerLocation");
					if(allAgentObj != null)
					{
						setInstallationPointCommissionReport(new ArrayList<String[]>());
						List<InstallerLocation> allAgentList = (List<InstallerLocation>)allAgentObj;
						for(InstallerLocation e : allAgentList)
						{
							Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and e.customer.partner=:partner and (e.booked_dt between :st and :et) order by e.booked_dt desc");
							q.setParameter("installer", e);
							q.setParameter("partner", userBean.getSessionPartner());
							q.setParameter("st", st);
							q.setParameter("et", et);
							Object list = gDAO.search(q, 0);
							if(list != null)
							{
								String[] rec = new String[4];
								
								List<InstallerLocationJobSchedule> jList = (List<InstallerLocationJobSchedule>)list;
								long totalCount=0, paidCount=0;
								BigDecimal totalSum=new BigDecimal(0.00), paidSum=new BigDecimal(0.00), commissionSum=new BigDecimal(0.00);
								
								for(InstallerLocationJobSchedule j : jList)
								{
									ProductTypeCommission commission = null;
									for(ProductTypeCommission c : getPtypeCommissionList())
										commission = c;
									
									totalCount+=1;totalSum = totalSum.add(new BigDecimal(j.getCost()));
									if(j.isPaid() && j.isCompleted() && !j.isCancel() && !j.isRefund())
									{
										paidCount += 1;
										paidSum = paidSum.add(new BigDecimal(j.getCost()));
										
										if(commission != null && commission.getInstallerCommission() > 0)
										{
											BigDecimal cc = new BigDecimal(commission.getInstallerCommission()).multiply(new BigDecimal(j.getCost()));
											cc = cc.divide(new BigDecimal(100.00));
											cc = cc.setScale(2, RoundingMode.HALF_UP);
											commissionSum = commissionSum.add(cc);
										}
									}
								}
								rec[0] = e.getCompanyName();
								rec[1] = totalCount + "/" + totalSum.toPlainString();
								rec[2] = paidCount + "/" + paidSum.toPlainString();
								rec[3] = commissionSum.toPlainString();
								
								getInstallationPointCommissionReport().add(rec);
							}
						}
						
						if(getInstallationPointCommissionReport().size() > 0)
						{
							an.setType("SUCCESS");
							an.setSubject("Success");
							an.setMessage(getInstallationPointCommissionReport().size() + " record(s) found!");
							appNotifBean.getAppNotifications().add(an);
						}
						else
						{
							an.setType("ERROR");
							an.setSubject("Failed");
							an.setMessage("No result!");
							appNotifBean.getAppNotifications().add(an);
						}
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("No sales agents found!");
						appNotifBean.getAppNotifications().add(an);
					}
					gDAO.destroy();
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("Please input a date range!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 8: // Device movement report
		{
			setDeviceMoveReport(null);
			
			if(getStart_dt_str() != null && getStart_dt_str().trim().length() > 0 &&
					getEnd_dt_str() != null && getEnd_dt_str().trim().length() > 0)
			{
				Date st = null, et = null;
				try
				{
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					st = sdf.parse(getStart_dt_str());
					et = sdf.parse(getEnd_dt_str());
				}
				catch(Exception ex)
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Invalid date formats detected!");
					appNotifBean.getAppNotifications().add(an);
				}
				
				if(st != null && et != null)
				{
					GeneralDAO gDAO = new GeneralDAO();
					Query q = gDAO.createQuery("Select e from ItemMove e where (e.move_dt between :st and :et) and e.moveType = :moveType");
					q.setParameter("st", st);
					q.setParameter("et", et);
					q.setParameter("moveType", getMoveType());
					Object listObj = gDAO.search(q, 0);
					if(listObj != null)
					{
						setDeviceMoveReport((List<ItemMove>)listObj);
					}
					gDAO.destroy();
					
					if(getDeviceMoveReport() != null && getDeviceMoveReport().size() > 0)
					{
						an.setType("SUCCESS");
						an.setSubject("Success");
						an.setMessage(getDeviceMoveReport().size() + " record(s) found!");
						appNotifBean.getAppNotifications().add(an);
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("No record found!");
						appNotifBean.getAppNotifications().add(an);
					}
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("Please input a date range!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void save(int i)
	{
		if(userBean != null)
			userBean.setActiveTab("create");
		appNotifBean.getAppNotifications().clear();
		AppNotification an = new AppNotification();
		switch(i)
		{
		case -1: // quick links buy product for existing customer
		{
			Product selProduct = null;
			for(Product p : getProducts())
			{
				if(p.isSelected())
				{
					selProduct = p;
					break;
				}
			}
			Customer cus = null;
			GeneralDAO gDAO = new GeneralDAO();
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("uniqueID", getCustomerUniqueId());
			params.put("phoneNo", getCustomerPhoneNo());
			Object cusObj = gDAO.search("Customer", params);
			if(cusObj != null)
			{
				List<Customer> cusList = (List<Customer>)cusObj;
				for(Customer e : cusList)
					cus = e;
			}
			
			
			SalesAgent sa = null;
			TradePartner tp = null;
			if(getSalesAgentUniqueId() != null && getSalesAgentUniqueId().startsWith("AGT"))
			{
				if(userBean.getSessionSalesAgent() != null)
				{
					sa = userBean.getSessionSalesAgent();
				}
				else
				{
					params = new Hashtable<String, Object>();
					params.put("uniqueID", getSalesAgentUniqueId());
					Object saObj = gDAO.search("SalesAgent", params);
					if(saObj != null)
					{
						List<SalesAgent> saList = (List<SalesAgent>)saObj;
						for(SalesAgent e : saList)
							sa = e;
					}
				}
			}
			else if(getSalesAgentUniqueId() != null && getSalesAgentUniqueId().startsWith("TP"))
			{
				if(userBean.getSessionTradePartner() != null)
				{
					tp = userBean.getSessionTradePartner();
				}
				else
				{
					params = new Hashtable<String, Object>();
					params.put("uniqueID", getSalesAgentUniqueId());
					Object saObj = gDAO.search("TradePartner", params);
					if(saObj != null)
					{
						List<TradePartner> saList = (List<TradePartner>)saObj;
						for(TradePartner e : saList)
							tp = e;
					}
				}
			}
			
			List<SalesAgentItem> sails = new ArrayList<SalesAgentItem>();
			List<TradePartnerItem> tpils = new ArrayList<TradePartnerItem>();
			boolean stockAvailable = true;
			//confirm that the salesagent has stock for the items in the product it's about to sell, that's if the salesagent is marked to hold its own stock
			if(sa != null && sa.isStockDevice())
			{
				for(ProductItem pi : selProduct.getItems())
				{
					params = new Hashtable<String, Object>();
					params.put("salesAgent", sa);
					params.put("item", pi.getItem());
					Object lsobj = gDAO.search("SalesAgentItem", params);
					if(lsobj != null)
					{
						List<SalesAgentItem> ls = (List<SalesAgentItem>)lsobj;
						for(SalesAgentItem e : ls)
						{
							if(e.getCount() < pi.getItem_count())
							{
								stockAvailable = false;
								break;
							}
							else
							{
								e.setCount(e.getCount()-pi.getItem_count());
								sails.add(e);
							}
						}
					}
					if(!stockAvailable)
						break;
				}
			}
			//confirm that the tradepartner has stock for the items in the product it's about to sell.
			if(tp != null)
			{
				for(ProductItem pi : selProduct.getItems())
				{
					params = new Hashtable<String, Object>();
					params.put("tradePartner", tp);
					params.put("item", pi.getItem());
					Object lsobj = gDAO.search("TradePartnerItem", params);
					if(lsobj != null)
					{
						List<TradePartnerItem> ls = (List<TradePartnerItem>)lsobj;
						for(TradePartnerItem e : ls)
						{
							if(e.getCount() < pi.getItem_count())
							{
								stockAvailable = false;
								break;
							}
							else
							{
								e.setCount(e.getCount()-pi.getItem_count());
								tpils.add(e);
							}
						}
					}
					if(!stockAvailable)
						break;
				}
			}
			
			if(stockAvailable)
			{
				if(selProduct != null && (sa != null || tp != null) && cus != null)
				{
					gDAO.startTransaction();
					String tranRef = "";
		    		try
		    		{
		    			tranRef = cus.getUniqueID().substring(cus.getUniqueID().lastIndexOf("-")+1) + "-" + System.currentTimeMillis();
		    		}
		    		catch(Exception ex)
		    		{
		    			tranRef = cus.getUniqueID() + "-" + System.currentTimeMillis();
		    		}
					
					CustomerTransaction ct = new CustomerTransaction();
					ct.setAmount(selProduct.getFirstYearPayment());
					ct.setCrt_dt(new Date());
					ct.setCustomer(cus);
					ct.setPayFor("PURCHASE");
					ct.setPayMode("Pay at Bank");
					ct.setProduct(selProduct);
					ct.setStatus("PENDING");
					ct.setTranInitDate(new Date());
					ct.setTranRef(tranRef);
					gDAO.save(ct);
					
					CustomerProduct cp = new CustomerProduct();
					cp.setBooked_dt(new Date());
					cp.setCreatedBy((sa != null ? sa.getUser() : (tp != null ? tp.getUser() : null)));
					cp.setCrt_dt(new Date());
					cp.setCustomer(cus);
					cp.setProductBooked(selProduct);
					cp.setPurchasedAmount(selProduct.getFirstYearPayment());
					cp.setPurchaseTranRef(tranRef);
					cp.setPurchaseTransaction(ct);
					cp.setStatus("PENDING");
					gDAO.save(cp);
					
					if(sails != null && sails.size()>0)
					{
						for(SalesAgentItem e : sails)
							gDAO.update(e);
					}
					if(tpils != null && tpils.size()>0)
					{
						for(TradePartnerItem e : tpils)
							gDAO.update(e);
					}
					
					try
					{
						gDAO.commit();
						
						//Send invoice along with this email to the customer
						byte[] data = generateInvoiceForCustomerPurchase(cp);
						if(data != null)
						{
							if(sa != null)
								userBean.sendEmailWithAttachedment(new String[]{cus.getUser().getUsername(), sa.getUser().getUsername()}, "How to pay", MessagesUtil.getHowToPayMessage(tranRef, cp), "invoice-"+cp.getPurchaseTranRef()+".pdf", data);
							else if(tp != null)
								userBean.sendEmailWithAttachedment(new String[]{cus.getUser().getUsername(), tp.getUser().getUsername()}, "How to pay", MessagesUtil.getHowToPayMessage(tranRef, cp), "invoice-"+cp.getPurchaseTranRef()+".pdf", data);
						}
						else
						{
							if(sa != null)
								userBean.sendEmail(new String[]{cus.getUser().getUsername(), sa.getUser().getUsername()}, "How to pay", MessagesUtil.getHowToPayMessage(tranRef, cp));
							else if(tp != null)
								userBean.sendEmail(new String[]{cus.getUser().getUsername(), tp.getUser().getUsername()}, "How to pay", MessagesUtil.getHowToPayMessage(tranRef, cp));
						}
						userBean.sendSMS(cus.getPhoneNo(), "Dear " + cus.getFirstname() + ", we have sent you an email on how to make payment for your transaction(" + tranRef + "). Please notify us after making your payment. Thank you.");
	        			
						setCustomerUniqueId(null);
						setCustomerPhoneNo(null);
						setSalesAgentUniqueId(null);
						an.setType("SUCCESS");
						an.setSubject("Success");
						an.setMessage("Product purchase submitted successfully!");
					} catch(Exception ex){
						gDAO.rollback();
						an.setType("ERROR");
						an.setSubject("Error");
						an.setMessage("Message: " + ex.getMessage() + "!");
					}
					
					appNotifBean.getAppNotifications().add(an);
				}
				else
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Could not identify either the sales agent or the customer! Also make sure you select a product!");
					appNotifBean.getAppNotifications().add(an);
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("You do not have enough stock of items to sell the selected product!");
				appNotifBean.getAppNotifications().add(an);
			}
			
			gDAO.destroy();
			break;
		}
		case 0: // quick links register and buy product for new customer
		{
			Product selProduct = null;
			for(Product p : getProducts())
			{
				if(p.isSelected())
				{
					selProduct = p;
					break;
				}
			}
			if(getCustomer().getFirstname() != null && getCustomer().getFirstname().trim().length() > 0 &&
					getCustomer().getLastname() != null && getCustomer().getFirstname().trim().length() > 0 &&
					getCusUser().getUsername() != null && getCusUser().getUsername().trim().length() > 0 &&
					getCusUser().getPassword() != null && getCusUser().getPassword().trim().length() > 0 && 
					selProduct != null)
			{
				if(getCusUser().getPassword().equals(getConfirm_password()))
				{
					if(userBean.validatePassword(getCusUser().getPassword()))
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
						
						if(getDobstr() != null && getDobstr().trim().length() > 0)
						{
							try
							{
								SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
								Date dt = sdf.parse(getDobstr());
								getCustomer().setDob(dt);
							}catch(Exception ex){ex.printStackTrace();}
						}
						
						SalesAgent sa = null;
						TradePartner tp = null;
						if(userBean.getSessionSalesAgent() != null || userBean.getSessionTradePartner() != null)
						{
							getCustomer().setCreatedBy(userBean.getSessionUser());
							getCustomer().setPartner(userBean.getSessionPartner());
							
							sa = userBean.getSessionSalesAgent();
							tp = userBean.getSessionTradePartner();
						}
						else
						{
							if(getSalesAgentUniqueId() != null && getSalesAgentUniqueId().startsWith("AGT"))
							{
								Hashtable<String, Object> params = new Hashtable<String, Object>();
								params.put("uniqueID", getSalesAgentUniqueId());
								Object saObj = gDAO.search("SalesAgent", params);
								if(saObj != null)
								{
									List<SalesAgent> saList = (List<SalesAgent>)saObj;
									for(SalesAgent e : saList)
										sa = e;
								}
								if(sa != null)
								{
									getCustomer().setCreatedBy(sa.getUser());
									getCustomer().setPartner(sa.getUser().getPartner());
								}
							}
							else if(getSalesAgentUniqueId() != null && getSalesAgentUniqueId().startsWith("TP"))
							{
								Hashtable<String, Object> params = new Hashtable<String, Object>();
								params.put("uniqueID", getSalesAgentUniqueId());
								Object saObj = gDAO.search("TradePartner", params);
								if(saObj != null)
								{
									List<TradePartner> saList = (List<TradePartner>)saObj;
									for(TradePartner e : saList)
										tp = e;
								}
								if(tp != null)
								{
									getCustomer().setCreatedBy(tp.getUser());
									getCustomer().setPartner(tp.getUser().getPartner());
								}
							}
						}
						
						List<SalesAgentItem> sails = new ArrayList<SalesAgentItem>();
						List<TradePartnerItem> tpils = new ArrayList<TradePartnerItem>();
						boolean stockAvailable = true;
						//confirm that the salesagent has stock for the items in the product it's about to sell, that's if the salesagent is marked to hold its own stock
						if(sa != null && sa.isStockDevice())
						{
							for(ProductItem pi : selProduct.getItems())
							{
								Hashtable<String, Object> params = new Hashtable<String, Object>();
								params.put("salesAgent", sa);
								params.put("item", pi.getItem());
								Object lsobj = gDAO.search("SalesAgentItem", params);
								if(lsobj != null)
								{
									List<SalesAgentItem> ls = (List<SalesAgentItem>)lsobj;
									for(SalesAgentItem e : ls)
									{
										if(e.getCount() < pi.getItem_count())
										{
											stockAvailable = false;
											break;
										}
										else
										{
											e.setCount(e.getCount()-pi.getItem_count());
											sails.add(e);
										}
									}
								}
								if(!stockAvailable)
									break;
							}
						}
						//confirm that the tradepartner has stock for the items in the product it's about to sell.
						if(tp != null)
						{
							for(ProductItem pi : selProduct.getItems())
							{
								Hashtable<String, Object> params = new Hashtable<String, Object>();
								params.put("tradePartner", tp);
								params.put("item", pi.getItem());
								Object lsobj = gDAO.search("TradePartnerItem", params);
								if(lsobj != null)
								{
									List<TradePartnerItem> ls = (List<TradePartnerItem>)lsobj;
									for(TradePartnerItem e : ls)
									{
										if(e.getCount() < pi.getItem_count())
										{
											stockAvailable = false;
											break;
										}
										else
										{
											e.setCount(e.getCount()-pi.getItem_count());
											tpils.add(e);
										}
									}
								}
								if(!stockAvailable)
									break;
							}
						}
						
						if(stockAvailable)
						{
							if(getCustomer().getCreatedBy() != null)
							{
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
									getCusUser().setCreatedBy(getCustomer().getCreatedBy());
									getCusUser().setCrt_dt(new Date());
									getCusUser().setPartner(getCustomer().getPartner());
									getCusUser().setType("CUSTOMER");
									
									gDAO.startTransaction();
									gDAO.save(getCusUser());
									
									getCustomer().setUser(getCusUser());
									gDAO.save(getCustomer());
									
									getCustomer().setUniqueID(generateCustomerReference(getCustomer().getId()));
									gDAO.update(getCustomer());
									
									String tranRef = "";
						    		try
						    		{
						    			tranRef = getCustomer().getUniqueID().substring(getCustomer().getUniqueID().lastIndexOf("-")+1) + "-" + System.currentTimeMillis();
						    		}
						    		catch(Exception ex)
						    		{
						    			tranRef = getCustomer().getUniqueID() + "-" + System.currentTimeMillis();
						    		}
									
									CustomerTransaction ct = new CustomerTransaction();
									ct.setAmount(selProduct.getFirstYearPayment());
									ct.setCrt_dt(new Date());
									ct.setCustomer(getCustomer());
									ct.setPayFor("PURCHASE");
									ct.setPayMode("Pay at Bank");
									ct.setProduct(selProduct);
									ct.setStatus("PENDING");
									ct.setTranInitDate(new Date());
									ct.setTranRef(tranRef);
									gDAO.save(ct);
									
									CustomerProduct cp = new CustomerProduct();
									cp.setBooked_dt(new Date());
									cp.setCreatedBy(getCustomer().getCreatedBy());
									cp.setCrt_dt(new Date());
									cp.setCustomer(getCustomer());
									cp.setProductBooked(selProduct);
									cp.setPurchasedAmount(selProduct.getFirstYearPayment());
									cp.setPurchaseTranRef(tranRef);
									cp.setPurchaseTransaction(ct);
									cp.setStatus("PENDING");
									gDAO.save(cp);
									
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
										
										userBean.sendEmail(new String[]{getCusUser().getUsername()}, "Welcome to RMS", MessagesUtil.getSignUpEmailMessage(getCustomer().getFirstname(), getCusUser().getUsername(), getConfirm_password(), getCustomer().getUniqueID()));
										userBean.sendSMS(getCustomer().getPhoneNo(), MessagesUtil.getSignUpSMSMessage(getCustomer()));
										
										//Send invoice attached with the below email to customer
										byte[] data = generateInvoiceForCustomerPurchase(cp);
										if(data != null)
											userBean.sendEmailWithAttachedment(new String[]{getCusUser().getUsername(), getCustomer().getCreatedBy().getUsername()}, "How to pay", MessagesUtil.getHowToPayMessage(tranRef, cp), "invoice-"+cp.getPurchaseTranRef()+".pdf", data);
										else
											userBean.sendEmail(new String[]{getCusUser().getUsername(), getCustomer().getCreatedBy().getUsername()}, "How to pay", MessagesUtil.getHowToPayMessage(tranRef, cp));
										userBean.sendSMS(getCustomer().getPhoneNo(), "Dear " + getCustomer().getFirstname() + ", we have sent you an email on how to make payment for your transaction(" + tranRef + "). Please notify us after making your payment. Thank you.");
					        			
										setCustomer(null);
										setCustomers(null);
										setSelCustomer(null);
										setCusUser(null);
										an.setType("SUCCESS");
										an.setSubject("Success");
										an.setMessage("Customer registered and product purchase submitted successfully!");
									} catch(Exception ex){
										gDAO.rollback();
										an.setType("ERROR");
										an.setSubject("Error");
										an.setMessage("Message: " + ex.getMessage() + "!");
									}
									
									appNotifBean.getAppNotifications().add(an);
								}
								else
								{
									an.setType("ERROR");
									an.setSubject("Failed");
									an.setMessage("Could not get the customer role!");
									appNotifBean.getAppNotifications().add(an);
								}
							}
							else
							{
								an.setType("ERROR");
								an.setSubject("Failed");
								an.setMessage("Could not identify the sales agent!");
								appNotifBean.getAppNotifications().add(an);
							}
						}
						else
						{
							an.setType("ERROR");
							an.setSubject("Failed");
							an.setMessage("You do not have enough stock of items to sell the selected product!");
							appNotifBean.getAppNotifications().add(an);
						}
						gDAO.destroy();
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Username is not in a valid email format!");
						appNotifBean.getAppNotifications().add(an);
					}
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Password does not match the password policy. Please view the help page for more information!");
						appNotifBean.getAppNotifications().add(an);
					}
				}
				else
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Password fields were not the same!");
					appNotifBean.getAppNotifications().add(an);
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("All fields with the '*' sign are required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 1: // items manufacturer
		{
			if(getItmManufacturer().getName() != null && getItmManufacturer().getName().trim().length() > 0)
			{
				getItmManufacturer().setCrt_dt(new Date());
				GeneralDAO gDAO = new GeneralDAO();
				gDAO.startTransaction();
				gDAO.save(getItmManufacturer());
				try {
					gDAO.commit();
					setItmManufacturers(null);
					setItmManufacturer(null);
					setSelItmManufacturer(null);
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage("Manufacturer created successfully!");
				} catch(Exception ex){
					gDAO.rollback();
					an.setType("ERROR");
					an.setSubject("Error");
					an.setMessage("Message: " + ex.getMessage() + "!");
				}
				gDAO.destroy();
				appNotifBean.getAppNotifications().add(an);
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("The manufacturer name is required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 2: // item types
		{
			if(getItmType().getName() != null && getItmType().getName().trim().length() > 0)
			{
				getItmType().setCrt_dt(new Date());
				GeneralDAO gDAO = new GeneralDAO();
				gDAO.startTransaction();
				gDAO.save(getItmType());
				try {
					gDAO.commit();
					setItmTypes(null);
					setItmType(null);
					setSelItmType(null);
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage("Device Type created successfully!");
				} catch(Exception ex){
					gDAO.rollback();
					an.setType("ERROR");
					an.setSubject("Error");
					an.setMessage("Message: " + ex.getMessage() + "!");
				}
				gDAO.destroy();
				
				appNotifBean.getAppNotifications().add(an);
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("The device type name is required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 3: // item
		{
			if(getItm().getName() != null && getItm().getName().trim().length() > 0 &&
					getItm().getModel() != null && getItm().getModel().trim().length() > 0 &&
					getItmManu_id() > 0 && getItmType_id() > 0)
			{
				GeneralDAO gDAO = new GeneralDAO();
				
				Object manuObj = gDAO.find(ItemManufacturer.class, getItmManu_id());
				if(manuObj != null)
					getItm().setManufacturer((ItemManufacturer)manuObj);
				
				Object typeObj = gDAO.find(ItemType.class, getItmType_id());
				if(typeObj != null)
					getItm().setType((ItemType)typeObj);
				
				getItm().setCrt_dt(new Date());
				gDAO.startTransaction();
				gDAO.save(getItm());
				try {
					gDAO.commit();
					setItm(null);
					setItems(null);
					setSelItm(null);
					setItmManu_id(0);
					setItmType_id(0);
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage("Device created successfully!");
				} catch(Exception ex){
					gDAO.rollback();
					an.setType("ERROR");
					an.setSubject("Error");
					an.setMessage("Message: " + ex.getMessage() + "!");
				}
				gDAO.destroy();
				
				appNotifBean.getAppNotifications().add(an);
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("All fields with the '*' sign are required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 4: // product type
		{
			if(getPtype().getName() != null && getPtype().getName().trim().length() > 0)
			{
				GeneralDAO gDAO = new GeneralDAO();
				
				getPtype().setCrt_dt(new Date());
				gDAO.startTransaction();
				gDAO.save(getPtype());
				try {
					gDAO.commit();
					setPtype(null);
					setPtypes(null);
					setSelPtype(null);
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage("Product type created successfully!");
				} catch(Exception ex){
					gDAO.rollback();
					an.setType("ERROR");
					an.setSubject("Error");
					an.setMessage("Message: " + ex.getMessage() + "!");
				}
				gDAO.destroy();
				
				appNotifBean.getAppNotifications().add(an);
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("Product type name is required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 5: // product
		{
			if(getProduct().getProductName() != null && getProduct().getProductName().trim().length() > 0 && 
					getProduct().getDetails() != null && getProduct().getDetails().trim().length() > 0 &&
					getPtype_id() > 0)
			{
				GeneralDAO gDAO = new GeneralDAO();
				
				Object ptype = gDAO.find(ProductType.class, getPtype_id());
				if(ptype != null)
					getProduct().setType((ProductType)ptype);
				
				getProduct().setActive(true);
				
				if(getProduct_photo() != null)
				{
					if(getProduct_photo().getContentType().indexOf("image")>=0)
					{
						try
						{
							InputStream in = getProduct_photo().getInputStream();
							ByteArrayOutputStream buffer = new ByteArrayOutputStream();
							int read;
							byte[] input = new byte[4096];
							while(-1 != ( read = in.read( input ) ) ) {
								buffer.write( input, 0, read );
							}
							in.close();
							getProduct().setPhoto(buffer.toByteArray());
						}
						catch(Exception ex)
						{
							an.setType("ERROR");
							an.setSubject("Failed");
							an.setMessage("Could not upload the selected photo!");
							appNotifBean.getAppNotifications().add(an);
							an = new AppNotification();
						}
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("File uploaded not an image!");
						appNotifBean.getAppNotifications().add(an);
						an = new AppNotification();
					}
				}
				
				getProduct().setCrt_dt(new Date());
				gDAO.startTransaction();
				gDAO.save(getProduct());
				
				for(ProductItem pi : getProductItemList())
				{
					pi.setProduct(getProduct());
					gDAO.save(pi);
				}
				
				try {
					gDAO.commit();
					setProduct(null);
					setProducts(null);
					setSelProduct(null);
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage("Product created successfully!");
				} catch(Exception ex){
					ex.printStackTrace();
					try
					{
						gDAO.rollback();
					}
					catch(Exception e){}
					an.setType("ERROR");
					an.setSubject("Error");
					an.setMessage("Message: " + ex.getMessage() + "!");
				}
				gDAO.destroy();
				
				appNotifBean.getAppNotifications().add(an);
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("All fields with the '*' sign are required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 6: // customer
		{
			if(getCustomer().getFirstname() != null && getCustomer().getFirstname().trim().length() > 0 &&
					getCustomer().getLastname() != null && getCustomer().getFirstname().trim().length() > 0 &&
					getCusUser().getUsername() != null && getCusUser().getUsername().trim().length() > 0 &&
					getCusUser().getPassword() != null && getCusUser().getPassword().trim().length() > 0)
			{
				if(getCusUser().getPassword().equals(getConfirm_password()))
				{
					if(userBean.validatePassword(getCusUser().getPassword()))
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
						
						if(getDobstr() != null && getDobstr().trim().length() > 0)
						{
							try
							{
								SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
								Date dt = sdf.parse(getDobstr());
								getCustomer().setDob(dt);
							}catch(Exception ex){ex.printStackTrace();}
						}
						
						getCustomer().setCreatedBy(userBean.getSessionUser());
						getCustomer().setPartner(getPartner());
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
							getCusUser().setCreatedBy(userBean.getSessionUser());
							getCusUser().setCrt_dt(new Date());
							getCusUser().setPartner(getPartner());
							getCusUser().setType("CUSTOMER");
							
							gDAO.startTransaction();
							gDAO.save(getCusUser());
							
							getCustomer().setUser(getCusUser());
							gDAO.save(getCustomer());
							
							getCustomer().setUniqueID(generateCustomerReference(getCustomer().getId()));
							gDAO.update(getCustomer());
							try {
								
								Notification n = new Notification();
								n.setCrt_dt(new Date());
								n.setNotified(false);
								n.setSubject("Welcome");
								n.setPage_url("dashboard");
								n.setUser(getCusUser());
								n.setMessage("Welcome to RMS!!!");
								
								gDAO.save(n);
								
								gDAO.commit();
								
								userBean.sendEmail(new String[]{getCusUser().getUsername()}, "Welcome to RMS", MessagesUtil.getSignUpEmailMessage(getCustomer().getFirstname(), getCusUser().getUsername(), getConfirm_password(), getCustomer().getUniqueID()));
								userBean.sendSMS(getCustomer().getPhoneNo(), MessagesUtil.getSignUpSMSMessage(getCustomer()));
								
								setCustomer(null);
								setCustomers(null);
								setSelCustomer(null);
								setCusUser(null);
								an.setType("SUCCESS");
								an.setSubject("Success");
								an.setMessage("Customer created successfully!");
							} catch(Exception ex){
								gDAO.rollback();
								an.setType("ERROR");
								an.setSubject("Error");
								an.setMessage("Message: " + ex.getMessage() + "!");
							}
							
							appNotifBean.getAppNotifications().add(an);
						}
						else
						{
							an.setType("ERROR");
							an.setSubject("Failed");
							an.setMessage("Could not get the customer role!");
							appNotifBean.getAppNotifications().add(an);
						}
						gDAO.destroy();
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Username is not in a valid email format!");
						appNotifBean.getAppNotifications().add(an);
					}
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Password does not match the password policy. Please view the help page for more information!");
						appNotifBean.getAppNotifications().add(an);
					}
				}
				else
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Password fields were not the same!");
					appNotifBean.getAppNotifications().add(an);
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("All fields with the '*' sign are required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 7: // installation point
		{
			if(getInstaller().getAdminFirstname() != null && getInstaller().getAdminFirstname().trim().length() > 0 &&
					getInstaller().getAdminLastname() != null && getInstaller().getAdminLastname().trim().length() > 0 &&
					getInstaller().getCompanyName() != null && getInstaller().getCompanyName().trim().length() > 0 &&
					getInstaller().getAddress() != null && getInstaller().getAddress().trim().length() > 0 &&
					getLga_id() > 0 && getState_id() > 0 &&
					getInstallerUser().getPassword() != null && getInstallerUser().getPassword().trim().length() > 0 &&
					getInstallerUser().getUsername() != null && getInstallerUser().getUsername().trim().length() > 0)
			{
				if(getInstallerUser().getPassword().equals(getConfirm_password()))
				{
					if(userBean.validatePassword(getInstallerUser().getPassword()))
					{
					if(getInstallerUser().getUsername().indexOf("@") > 0 && getInstallerUser().getUsername().indexOf(".") > 0)
					{
						getInstallerUser().setPassword(Hasher.getHashValue(getConfirm_password()));
						GeneralDAO gDAO = new GeneralDAO();
						
						Object country = gDAO.find(Country.class, getCtry_id());
						if(country != null)
							getInstaller().setCountry((Country)country);
						Object state = gDAO.find(State.class, getState_id());
						if(state != null)
							getInstaller().setState((State)state);
						Object lga = gDAO.find(LGA.class, getLga_id());
						if(lga != null)
							getInstaller().setLga((LGA)lga);
						
						if(getInstaller_photo() != null)
						{
							if(getInstaller_photo().getContentType().indexOf("image")>=0)
							{
								try
								{
									InputStream in = getInstaller_photo().getInputStream();
									ByteArrayOutputStream buffer = new ByteArrayOutputStream();
									int read;
									byte[] input = new byte[4096];
									while(-1 != ( read = in.read( input ) ) ) {
										buffer.write( input, 0, read );
									}
									in.close();
									getInstaller().setPhoto(buffer.toByteArray());
								}
								catch(Exception ex)
								{
									an.setType("ERROR");
									an.setSubject("Failed");
									an.setMessage("Could not upload the selected photo!");
									appNotifBean.getAppNotifications().add(an);
									an = new AppNotification();
								}
							}
							else
							{
								an.setType("ERROR");
								an.setSubject("Failed");
								an.setMessage("File uploaded not an image!");
								appNotifBean.getAppNotifications().add(an);
								an = new AppNotification();
							}
						}
						
						getInstaller().setCrt_dt(new Date());
						
						Hashtable<String, Object> params = new Hashtable<String, Object>();
						params.put("name", "INSTALLER");
						Object rolesObj = gDAO.search("Role", params);
						if(rolesObj != null)
						{
							List<Role> roles = (List<Role>)rolesObj;
							for(Role e : roles)
								getInstallerUser().setRole(e);
							getInstallerUser().setActive(true);
							getInstallerUser().setCreatedBy(userBean.getSessionUser());
							getInstallerUser().setCrt_dt(new Date());
							getInstallerUser().setPartner(userBean.getSessionPartner());
							getInstallerUser().setType("INSTALLER");
							
							gDAO.startTransaction();
							gDAO.save(getInstallerUser());
							
							getInstaller().setUser(getInstallerUser());
							gDAO.save(getInstaller());
							
							getInstaller().setUniqueID(generateAgentReference(getInstaller().getId()));
							gDAO.update(getInstaller());
							try {
								gDAO.commit();
								
								userBean.sendEmail(new String[]{getInstallerUser().getUsername()}, 
										"Welcome to RMS", 
										MessagesUtil.getSignUpInstallationPointEmailMessage(getInstaller().getAdminFirstname(), 
												getInstaller().getCompanyName(), getInstallerUser().getUsername(), getConfirm_password(), getInstaller().getUniqueID()));
								
								setInstaller(null);
								setInstallers(null);
								setSelInstaller(null);
								setInstallerUser(null);
								an.setType("SUCCESS");
								an.setSubject("Success");
								an.setMessage("Agent created successfully!");
							} catch(Exception ex){
								gDAO.rollback();
								an.setType("ERROR");
								an.setSubject("Error");
								an.setMessage("Message: " + ex.getMessage() + "!");
							}
							appNotifBean.getAppNotifications().add(an);
						}
						else
						{
							an.setType("ERROR");
							an.setSubject("Failed");
							an.setMessage("Could not get the agent role!");
							appNotifBean.getAppNotifications().add(an);
						}
						gDAO.destroy();
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Username is not in a valid email format!");
						appNotifBean.getAppNotifications().add(an);
					}
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Password does not match the password policy. Please view the help page for more information!");
						appNotifBean.getAppNotifications().add(an);
					}
				}
				else
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Password fields were not the same!");
					appNotifBean.getAppNotifications().add(an);
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("All fields with the '*' sign are required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 8: // partner personnel
		{
			if(getPp().getFirstname() != null && getPp().getFirstname().trim().length() > 0 &&
					getPp().getLastname() != null && getPp().getLastname().trim().length() > 0 &&
					getPpUser().getPassword() != null && getPpUser().getPassword().trim().length() > 0 &&
					getPpUser().getUsername() != null && getPpUser().getUsername().trim().length() > 0)
			{
				if(getPpUser().getPassword().equals(getConfirm_password()))
				{
					if(userBean.validatePassword(getPpUser().getPassword()))
					{
					getPpUser().setPassword(Hasher.getHashValue(getConfirm_password()));
					GeneralDAO gDAO = new GeneralDAO();
					
					getPp().setCreatedBy(userBean.getSessionUser());
					getPp().setPartner(getPartner());
					getPp().setCrt_dt(new Date());
					
					Hashtable<String, Object> params = new Hashtable<String, Object>();
					params.put("name", getPpUser().getType()); // FINANCIAL REPORTS, PERSONNEL, SATTRAK ADMINISTRATOR
					Object rolesObj = gDAO.search("Role", params);
					if(rolesObj != null)
					{
						List<Role> roles = (List<Role>)rolesObj;
						for(Role e : roles)
							getPpUser().setRole(e);
						getPpUser().setActive(true);
						getPpUser().setCreatedBy(userBean.getSessionUser());
						getPpUser().setCrt_dt(new Date());
						getPpUser().setPartner(getPartner());
						
						gDAO.startTransaction();
						gDAO.save(getPpUser());
						
						getPp().setUser(getPpUser());
						gDAO.save(getPp());
						try {
							gDAO.commit();
							
							userBean.sendEmail(new String[]{getPpUser().getUsername()}, 
									"Welcome to RMS", 
									MessagesUtil.getSignUpPPEmailMessage(getPp().getFirstname(), 
											getPpUser().getUsername(), getConfirm_password()));
							
							setPartner_id(0);
							setPp(null);
							setPpList(null);
							setSelPP(null);
							setPpUser(null);
							an.setType("SUCCESS");
							an.setSubject("Success");
							an.setMessage("Personnel created successfully!");
						} catch(Exception ex){
							gDAO.rollback();
							an.setType("ERROR");
							an.setSubject("Error");
							an.setMessage("Message: " + ex.getMessage() + "!");
						}
						appNotifBean.getAppNotifications().add(an);
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Could not get the role for the personnel type!");
						appNotifBean.getAppNotifications().add(an);
					}
					gDAO.destroy();
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Password does not match the password policy. Please view the help page for more information!");
						appNotifBean.getAppNotifications().add(an);
					}
				}
				else
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Password fields were not the same!");
					appNotifBean.getAppNotifications().add(an);
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("All fields with the '*' sign are required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 9: // item move
		{
			userBean.setActiveIMTab("installer");
			if(getItmMove().getCount() > 0 &&
					getItmMove().getMoveType() != null && getItmMove().getMoveType().trim().length() > 0 &&
					getItm_id() > 0 && getInstaller_id() > 0)
			{
				if(getItmMove_dt_str() != null && getItmMove_dt_str().trim().length() > 0)
				{
					try
					{
						SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
						Date dt = sdf.parse(getItmMove_dt_str());
						getItmMove().setMove_dt(dt);
					}catch(Exception ex){}
				}
				
				boolean iliexists = false;
				InstallerLocationItem ili = null;
				
				GeneralDAO gDAO = new GeneralDAO();
				
				Object installer = gDAO.find(InstallerLocation.class, getInstaller_id());
				if(installer != null)
					getItmMove().setInstaller((InstallerLocation)installer);
				
				Object item = gDAO.find(Item.class, getItm_id());
				if(item != null)
				{
					getItmMove().setItem((Item)item);
					getItmMove().setBefore_balance(getItmMove().getItem().getStocklevel());
					
					if(getItmMove().getInstaller() != null)
					{
						Hashtable<String, Object> params = new Hashtable<String, Object>();
						params.put("installer", getItmMove().getInstaller());
						params.put("item", getItmMove().getItem());
						Object iliObj = gDAO.search("InstallerLocationItem", params);
						if(iliObj != null)
						{
							List<InstallerLocationItem> iliList = (List<InstallerLocationItem>)iliObj;
							for(InstallerLocationItem e : iliList)
							{
								ili = e;
								iliexists = true;
							}
						}
						if(!iliexists)
						{
							ili = new InstallerLocationItem();
							ili.setInstaller(getItmMove().getInstaller());
							ili.setItem(getItmMove().getItem());
							ili.setCreatedBy(userBean.getSessionUser());
							ili.setCrt_dt(new Date());
						}
					}
					
					if(getItmMove().getMoveType().equalsIgnoreCase("SUPPLY"))
					{
						getItmMove().setNew_balance(getItmMove().getBefore_balance() + getItmMove().getCount());
						getItmMove().getItem().setStocklevel(getItmMove().getNew_balance());
					}
					else if(getItmMove().getMoveType().equalsIgnoreCase("DISTRIBUTION"))
					{
						getItmMove().setNew_balance(getItmMove().getBefore_balance() - getItmMove().getCount());
						getItmMove().getItem().setStocklevel(getItmMove().getNew_balance());
						
						if(ili != null)
							ili.setCount(ili.getCount() + getItmMove().getCount());
					}
					else if(getItmMove().getMoveType().equalsIgnoreCase("RETURNED"))
					{
						getItmMove().setNew_balance(getItmMove().getBefore_balance() + getItmMove().getCount());
						getItmMove().getItem().setStocklevel(getItmMove().getNew_balance());
						
						if(ili != null)
							ili.setCount(ili.getCount() - getItmMove().getCount());
					}
					else if(getItmMove().getMoveType().equalsIgnoreCase("DAMAGED"))
					{
						getItmMove().setNew_balance(getItmMove().getBefore_balance());
						
						if(ili != null)
							ili.setCount(ili.getCount() - getItmMove().getCount());
					}
				}
				
				getItmMove().setCreatedBy(userBean.getSessionUser());
				getItmMove().setCrt_dt(new Date());
				
				if(getItmMove().getNew_balance() >= 0)
				{
					gDAO.startTransaction();
					gDAO.save(getItmMove());
					gDAO.update(getItmMove().getItem());
					
					if(ili != null)
					{
						if(!iliexists)
							gDAO.save(ili);
						else
							gDAO.update(ili);
					}
					try {
						gDAO.commit();
						setItmMove(null);
						setInstaller_id(0);
						setItm_id(0);
						an.setType("SUCCESS");
						an.setSubject("Success");
						an.setMessage("Item movement created and stock level updated successfully!");
					} catch(Exception ex){
						gDAO.rollback();
						an.setType("ERROR");
						an.setSubject("Error");
						an.setMessage("Message: " + ex.getMessage() + "!");
					}
					
					appNotifBean.getAppNotifications().add(an);
				}
				else
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("New item stock level after this movement will be negative. This is not allowed!");
					appNotifBean.getAppNotifications().add(an);
				}
				gDAO.destroy();
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("All fields with the '*' sign are required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 92: // item move
		{
			userBean.setActiveIMTab("salesagt");
			if(getItmMove().getCount() > 0 &&
					getItmMove().getMoveType() != null && getItmMove().getMoveType().trim().length() > 0 &&
					getItm_id() > 0 && getSalesAgent_id() > 0)
			{
				if(getItmMove_dt_str() != null && getItmMove_dt_str().trim().length() > 0)
				{
					try
					{
						SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
						Date dt = sdf.parse(getItmMove_dt_str());
						getItmMove().setMove_dt(dt);
					}catch(Exception ex){}
				}
				
				boolean iliexists = false;
				SalesAgentItem ili = null;
				
				GeneralDAO gDAO = new GeneralDAO();
				
				Object installer = gDAO.find(SalesAgent.class, getSalesAgent_id());
				if(installer != null)
					getItmMove().setSalesAgent((SalesAgent)installer);
				
				Object item = gDAO.find(Item.class, getItm_id());
				if(item != null)
				{
					getItmMove().setItem((Item)item);
					getItmMove().setBefore_balance(getItmMove().getItem().getStocklevel());
					
					if(getItmMove().getSalesAgent() != null)
					{
						Hashtable<String, Object> params = new Hashtable<String, Object>();
						params.put("salesAgent", getItmMove().getSalesAgent());
						params.put("item", getItmMove().getItem());
						Object iliObj = gDAO.search("SalesAgentItem", params);
						if(iliObj != null)
						{
							List<SalesAgentItem> iliList = (List<SalesAgentItem>)iliObj;
							for(SalesAgentItem e : iliList)
							{
								ili = e;
								iliexists = true;
							}
						}
						if(!iliexists)
						{
							ili = new SalesAgentItem();
							ili.setSalesAgent(getItmMove().getSalesAgent());
							ili.setItem(getItmMove().getItem());
							ili.setCreatedBy(userBean.getSessionUser());
							ili.setCrt_dt(new Date());
						}
					}
					
					if(getItmMove().getMoveType().equalsIgnoreCase("SUPPLY"))
					{
						getItmMove().setNew_balance(getItmMove().getBefore_balance() + getItmMove().getCount());
						getItmMove().getItem().setStocklevel(getItmMove().getNew_balance());
					}
					else if(getItmMove().getMoveType().equalsIgnoreCase("DISTRIBUTION"))
					{
						getItmMove().setNew_balance(getItmMove().getBefore_balance() - getItmMove().getCount());
						getItmMove().getItem().setStocklevel(getItmMove().getNew_balance());
						
						if(ili != null)
							ili.setCount(ili.getCount() + getItmMove().getCount());
					}
					else if(getItmMove().getMoveType().equalsIgnoreCase("RETURNED"))
					{
						getItmMove().setNew_balance(getItmMove().getBefore_balance() + getItmMove().getCount());
						getItmMove().getItem().setStocklevel(getItmMove().getNew_balance());
						
						if(ili != null)
							ili.setCount(ili.getCount() - getItmMove().getCount());
					}
					else if(getItmMove().getMoveType().equalsIgnoreCase("DAMAGED"))
					{
						getItmMove().setNew_balance(getItmMove().getBefore_balance());
						
						if(ili != null)
							ili.setCount(ili.getCount() - getItmMove().getCount());
					}
				}
				
				getItmMove().setCreatedBy(userBean.getSessionUser());
				getItmMove().setCrt_dt(new Date());
				
				if(getItmMove().getNew_balance() >= 0)
				{
					gDAO.startTransaction();
					gDAO.save(getItmMove());
					gDAO.update(getItmMove().getItem());
					
					if(ili != null)
					{
						if(!iliexists)
							gDAO.save(ili);
						else
							gDAO.update(ili);
					}
					try {
						gDAO.commit();
						setItmMove(null);
						setSalesAgent_id(0);
						setItm_id(0);
						an.setType("SUCCESS");
						an.setSubject("Success");
						an.setMessage("Item movement created and stock level updated successfully!");
					} catch(Exception ex){
						gDAO.rollback();
						an.setType("ERROR");
						an.setSubject("Error");
						an.setMessage("Message: " + ex.getMessage() + "!");
					}
					
					appNotifBean.getAppNotifications().add(an);
				}
				else
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("New item stock level after this movement will be negative. This is not allowed!");
					appNotifBean.getAppNotifications().add(an);
				}
				gDAO.destroy();
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("All fields with the '*' sign are required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 93: // item move
		{
			userBean.setActiveIMTab("tradeprt");
			if(getItmMove().getCount() > 0 &&
					getItmMove().getMoveType() != null && getItmMove().getMoveType().trim().length() > 0 &&
					getItm_id() > 0 && getTradePartner_id() > 0)
			{
				if(getItmMove_dt_str() != null && getItmMove_dt_str().trim().length() > 0)
				{
					try
					{
						SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
						Date dt = sdf.parse(getItmMove_dt_str());
						getItmMove().setMove_dt(dt);
					}catch(Exception ex){}
				}
				
				boolean iliexists = false;
				TradePartnerItem ili = null;
				
				GeneralDAO gDAO = new GeneralDAO();
				
				Object installer = gDAO.find(TradePartner.class, getTradePartner_id());
				if(installer != null)
					getItmMove().setTradePartner((TradePartner)installer);
				
				Object item = gDAO.find(Item.class, getItm_id());
				if(item != null)
				{
					getItmMove().setItem((Item)item);
					getItmMove().setBefore_balance(getItmMove().getItem().getStocklevel());
					
					if(getItmMove().getSalesAgent() != null)
					{
						Hashtable<String, Object> params = new Hashtable<String, Object>();
						params.put("tradePartner", getItmMove().getTradePartner());
						params.put("item", getItmMove().getItem());
						Object iliObj = gDAO.search("TradePartnerItem", params);
						if(iliObj != null)
						{
							List<TradePartnerItem> iliList = (List<TradePartnerItem>)iliObj;
							for(TradePartnerItem e : iliList)
							{
								ili = e;
								iliexists = true;
							}
						}
						if(!iliexists)
						{
							ili = new TradePartnerItem();
							ili.setTradePartner(getItmMove().getTradePartner());
							ili.setItem(getItmMove().getItem());
							ili.setCreatedBy(userBean.getSessionUser());
							ili.setCrt_dt(new Date());
						}
					}
					
					if(getItmMove().getMoveType().equalsIgnoreCase("SUPPLY"))
					{
						getItmMove().setNew_balance(getItmMove().getBefore_balance() + getItmMove().getCount());
						getItmMove().getItem().setStocklevel(getItmMove().getNew_balance());
					}
					else if(getItmMove().getMoveType().equalsIgnoreCase("DISTRIBUTION"))
					{
						getItmMove().setNew_balance(getItmMove().getBefore_balance() - getItmMove().getCount());
						getItmMove().getItem().setStocklevel(getItmMove().getNew_balance());
						
						if(ili != null)
							ili.setCount(ili.getCount() + getItmMove().getCount());
					}
					else if(getItmMove().getMoveType().equalsIgnoreCase("RETURNED"))
					{
						getItmMove().setNew_balance(getItmMove().getBefore_balance() + getItmMove().getCount());
						getItmMove().getItem().setStocklevel(getItmMove().getNew_balance());
						
						if(ili != null)
							ili.setCount(ili.getCount() - getItmMove().getCount());
					}
					else if(getItmMove().getMoveType().equalsIgnoreCase("DAMAGED"))
					{
						getItmMove().setNew_balance(getItmMove().getBefore_balance());
						
						if(ili != null)
							ili.setCount(ili.getCount() - getItmMove().getCount());
					}
				}
				
				getItmMove().setCreatedBy(userBean.getSessionUser());
				getItmMove().setCrt_dt(new Date());
				
				if(getItmMove().getNew_balance() >= 0)
				{
					gDAO.startTransaction();
					gDAO.save(getItmMove());
					gDAO.update(getItmMove().getItem());
					
					if(ili != null)
					{
						if(!iliexists)
							gDAO.save(ili);
						else
							gDAO.update(ili);
					}
					try {
						gDAO.commit();
						setItmMove(null);
						setTradePartner_id(0);
						setItm_id(0);
						an.setType("SUCCESS");
						an.setSubject("Success");
						an.setMessage("Item movement created and stock level updated successfully!");
					} catch(Exception ex){
						gDAO.rollback();
						an.setType("ERROR");
						an.setSubject("Error");
						an.setMessage("Message: " + ex.getMessage() + "!");
					}
					
					appNotifBean.getAppNotifications().add(an);
				}
				else
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("New item stock level after this movement will be negative. This is not allowed!");
					appNotifBean.getAppNotifications().add(an);
				}
				gDAO.destroy();
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("All fields with the '*' sign are required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 10: // commission settings
		{
			if(getPtypeCommissionList() != null && getPtypeCommissionList().size() > 0)
			{
				GeneralDAO gDAO = new GeneralDAO();
				gDAO.startTransaction();
				for(ProductTypeCommission ptc : getPtypeCommissionList())
				{
					if(ptc.getId() != null)
						gDAO.update(ptc);
					else
					{
						ptc.setActive(true);
						ptc.setCreatedBy(userBean.getSessionUser());
						ptc.setCrt_dt(new Date());
						
						gDAO.save(ptc);
					}
				}
				try {
					gDAO.commit();
					setPtypeCommissionList(null);
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage("Product type commissions set/updated successfully!");
				} catch(Exception ex){
					gDAO.rollback();
					an.setType("ERROR");
					an.setSubject("Error");
					an.setMessage("Message: " + ex.getMessage() + "!");
				}
				gDAO.destroy();
				
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 112: // buy product
		{
			Product selProduct = null;
			for(Product p : getProducts())
			{
				if(p.isSelected())
				{
					selProduct = p;
					break;
				}
			}
			GeneralDAO gDAO = new GeneralDAO();
			Customer cus = null;
			Object customer = gDAO.find(Customer.class, getCustomer_id());
			if(customer != null)
				cus = (Customer)customer;
			
			if(selProduct != null && userBean.getSessionUser() != null && cus != null && getPaymentType() != null)
			{
				gDAO.startTransaction();
				String tranRef = "";
	    		try
	    		{
	    			tranRef = cus.getUniqueID().substring(cus.getUniqueID().lastIndexOf("-")+1) + "-" + System.currentTimeMillis();
	    		}
	    		catch(Exception ex)
	    		{
	    			tranRef = cus.getUniqueID() + "-" + System.currentTimeMillis();
	    		}
				
				CustomerTransaction ct = new CustomerTransaction();
				CustomerProduct cp = new CustomerProduct();
				
				ct.setAmount(selProduct.getFirstYearPayment());
				ct.setCrt_dt(new Date());
				ct.setCustomer(cus);
				ct.setPayFor("PURCHASE");
				ct.setPayMode(getPaymentType());
				ct.setProduct(selProduct);
				ct.setTranInitDate(new Date());
				ct.setTranRef(tranRef);
				
				cp.setBooked_dt(new Date());
				cp.setCreatedBy(userBean.getSessionUser());
				cp.setCrt_dt(new Date());
				cp.setCustomer(cus);
				cp.setProductBooked(selProduct);
				cp.setPurchasedAmount(selProduct.getFirstYearPayment());
				cp.setPurchaseTranRef(tranRef);
				
				if(getPaymentType().equals("Pay at Bank"))
				{
					ct.setStatus("PENDING");
					cp.setStatus("PENDING");
				}
				else if(getPaymentType().equals("Pay Cash"))
				{
					ct.setStatus("PAID");
					ct.setConfirmedBy(userBean.getSessionUser());
					ct.setConfirmationInfo("Cash payment");
					ct.setPayConfirmDate(new Date());
					
					cp.setStatus("NOT-INSTALLED");
				}
				gDAO.save(ct);
				
				cp.setPurchaseTransaction(ct);
				gDAO.save(cp);
				
				try
				{
					gDAO.commit();
					
					if(getPaymentType().equals("Pay at Bank"))
					{
						// Send mail to customer with invoice attached
						byte[] data = generateInvoiceForCustomerPurchase(cp);
						if(data != null)
							userBean.sendEmailWithAttachedment(new String[]{cus.getUser().getUsername(), userBean.getSessionUser().getUsername()}, "How to pay", MessagesUtil.getHowToPayMessage(tranRef, cp), "invoice-"+cp.getPurchaseTranRef()+".pdf", data);
						else
							userBean.sendEmail(new String[]{cus.getUser().getUsername(), userBean.getSessionUser().getUsername()}, "How to pay", MessagesUtil.getHowToPayMessage(tranRef, cp));
						userBean.sendAutoLifeSMS(cus.getPhoneNo(), "Dear " + cus.getFirstname() + ", we have sent you an email on how to make payment for your transaction(" + tranRef + "). Please notify us after making your payment. Thank you.");
					}
					else if(getPaymentType().equals("Pay Cash"))
					{
						userBean.sendAutoLifeSMS(cus.getPhoneNo(), "Dear " + cus.getFirstname() + ", your payment for transaction (" + tranRef + ") has been confirmed. You can now proceed to book your product installation. Thank you.");
					}
					
					setProducts(null);
					setCustomer_id(0L);
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage("Product purchase submitted successfully!");
				} catch(Exception ex){
					gDAO.rollback();
					an.setType("ERROR");
					an.setSubject("Error");
					an.setMessage("Message: " + ex.getMessage() + "!");
				}
				
				appNotifBean.getAppNotifications().add(an);
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("Could not identify either the sales agent or the customer! Also make sure you select a product!");
				appNotifBean.getAppNotifications().add(an);
			}
			
			gDAO.destroy();
			break;
		}
		case 11: // product booking
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
				getBooking().setCustomer(cp.getCustomer());
				
				Object slot = gDAO.find(Slot.class, getSlot_id());
				if(slot != null)
				{
					getBooking().setSlot((Slot)slot);
					Calendar c = Calendar.getInstance();
					c.setTime(getBooking().getBooked_dt());
					c.set(Calendar.HOUR_OF_DAY, getBooking().getSlot().getStart_hour());
					c.set(Calendar.MINUTE, 0);
					c.set(Calendar.SECOND, 0);
					c.set(Calendar.MILLISECOND, 0);
					getBooking().setStart_dt(c.getTime());
					c.add(Calendar.HOUR_OF_DAY, getBooking().getSlot().getAmount_of_hours());
					getBooking().setEnd_dt(c.getTime());
				}
				
				if(getSelInstaller() != null && getBooking().getCustomer() != null && getBooking().getSlot() != null && 
						getBooking().getBooked_dt() != null)
				{
					Calendar todaymax = Calendar.getInstance();
					todaymax.set(Calendar.HOUR_OF_DAY, todaymax.getMaximum(Calendar.HOUR_OF_DAY));
					todaymax.set(Calendar.MINUTE, todaymax.getMaximum(Calendar.MINUTE));
					todaymax.set(Calendar.SECOND, todaymax.getMaximum(Calendar.SECOND));
					todaymax.set(Calendar.MILLISECOND, todaymax.getMaximum(Calendar.MILLISECOND));
					
					if(getBooking().getBooked_dt().after(todaymax.getTime()))
					{
						getBooking().setJobCode(generateReference());
						getBooking().setPaid(true);
						getBooking().setPaymentType("Pay at Bank");
						getBooking().setCheckedIn(false);
						getBooking().setCompleted(false);
						getBooking().setCost(cp.getPurchasedAmount());
						getBooking().setCreatedBy(userBean.getSessionUser());
						getBooking().setCrt_dt(new Date());
						getBooking().setInstaller(getSelInstaller());
						getBooking().setProductBooked(cp.getProductBooked());
						// this booking will require customer service to confirm it if the product is a special product
						getBooking().setRequire_confirmation(cp.getProductBooked().isSpecialProduct());
						
						Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and e.cancel=:cancel and e.booked_dt=:booked_dt and e.slot=:slot");
						q.setParameter("installer", getBooking().getInstaller());
						q.setParameter("booked_dt", getBooking().getBooked_dt());
						q.setParameter("slot", getBooking().getSlot());
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
						if(!slot_taken || getSelInstaller().getMaxMultiSlotInstallation() == 0 || getSelInstaller().getMaxMultiSlotInstallation() > slot_used_size)
						{
							gDAO.startTransaction();
							gDAO.save(getBooking());
							try {
								Notification n = new Notification();
								n.setCrt_dt(new Date());
								n.setNotified(false);
								n.setSubject("Installation Scheduled");
								n.setPage_url("dashboard");
								n.setUser(getBooking().getCustomer().getUser());
								n.setMessage("Installation with Job Code: " + getBooking().getJobCode() + " has been scheduled for you.");
								
								gDAO.save(n);
								
								gDAO.commit();
								
								userBean.sendEmail(new String[]{getBooking().getCustomer().getUser().getUsername()}, "Installation Schedule on RMS", MessagesUtil.getJobScheduleEmailMessage(getBooking().getCustomer().getFirstname(), getBooking()));
								userBean.sendSMS(getBooking().getCustomer().getPhoneNo(), MessagesUtil.getJobScheduleSMSMessage(getBooking().getCustomer().getFirstname(), getBooking()));
								
								userBean.sendEmail(new String[]{getBooking().getInstaller().getUser().getUsername()}, "Installation Schedule on RMS", MessagesUtil.getJobScheduleInstallerEmailMessage(getBooking()));
								
								// send notification to support
								AppConfiguration currectAppConfig = getCurrentAppConfig();
								if(currectAppConfig != null && currectAppConfig.getCustomerSupportEmail() != null && currectAppConfig.getCustomerSupportEmail().trim().length() > 0)
								{
									String[] supportEmails = currectAppConfig.getCustomerSupportEmail().split(",");
									userBean.sendEmail(supportEmails, "Installation Schedule on RMS", MessagesUtil.getJobScheduleSupportEmailMessage(getBooking()));
								}
								
								setBooking(null);setSlots(null);
								setSelCustomer(null);setSelInstaller(null);
								setCp_id(0);setSlot_id(0);
								setBook_dt_str(null);setMySchedules_cus(null);
								an.setType("SUCCESS");
								an.setSubject("Success");
								an.setMessage("Purchase installation booked successfully!");
							} catch(Exception ex){
								gDAO.rollback();
								an.setType("ERROR");
								an.setSubject("Error");
								an.setMessage("Message: " + ex.getMessage() + "!");
							}
							appNotifBean.getAppNotifications().add(an);
						}
						else
						{
							setSlots(null);
							an.setType("ERROR");
							an.setSubject("Failed");
							an.setMessage("The slot you requested is filled! Please select a different slot!");
							appNotifBean.getAppNotifications().add(an);
						}
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Your book date must be from tomorrow upwards!");
						appNotifBean.getAppNotifications().add(an);
					}
				}
				else
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("All fields with the '*' sign are required!");
					appNotifBean.getAppNotifications().add(an);
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("Could not identify the purchase!");
				appNotifBean.getAppNotifications().add(an);
			}
			gDAO.destroy();
			break;
		}
		case 12: // installation point installer agent
		{
			if(getAgent().getFirstname() != null && getAgent().getFirstname().trim().length() > 0 &&
					getAgent().getLastname() != null && getAgent().getLastname().trim().length() > 0 &&
					getInstaller_id() > 0)
			{
				GeneralDAO gDAO = new GeneralDAO();
				
				Object installer = gDAO.find(InstallerLocation.class, getInstaller_id());
				if(installer != null)
					getAgent().setCompany((InstallerLocation)installer);
				
				if(getAgent_photo() != null)
				{
					if(getAgent_photo().getContentType().indexOf("image")>=0)
					{
						try
						{
							InputStream in = getAgent_photo().getInputStream();
							ByteArrayOutputStream buffer = new ByteArrayOutputStream();
							int read;
							byte[] input = new byte[4096];
							while(-1 != ( read = in.read( input ) ) ) {
								buffer.write( input, 0, read );
							}
							in.close();
							getAgent().setPhoto(buffer.toByteArray());
						}
						catch(Exception ex)
						{
							an.setType("ERROR");
							an.setSubject("Failed");
							an.setMessage("Could not upload the selected photo!");
							appNotifBean.getAppNotifications().add(an);
							an = new AppNotification();
						}
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("File uploaded not an image!");
						appNotifBean.getAppNotifications().add(an);
						an = new AppNotification();
					}
				}
				
				getAgent().setCreatedBy(userBean.getSessionUser());
				getAgent().setCrt_dt(new Date());
				
				if(getRating_id() > 0)
				{
					Object rating = gDAO.find(Rating.class, getRating_id());
					if(rating != null)
						getAgent().setRating((Rating)rating);
				}
				
				if(getAgent().getCompany() != null)
				{
					gDAO.startTransaction();
					gDAO.save(getAgent());
					try {
						gDAO.commit();
						setAgent(null);
						setSelAgent(null);
						setInstallerAgents(null);
						setInstaller_id(0);
						an.setType("SUCCESS");
						an.setSubject("Success");
						an.setMessage("Installer created successfully!");
					} catch(Exception ex){
						gDAO.rollback();
						an.setType("ERROR");
						an.setSubject("Error");
						an.setMessage("Message: " + ex.getMessage() + "!");
					}
					appNotifBean.getAppNotifications().add(an);
				}
				else
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Could not get the agent this installer belongs to!");
					appNotifBean.getAppNotifications().add(an);
				}
				gDAO.destroy();
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("All fields with the '*' sign are required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 13: // sales agent
		{
			if(getSalesAgent().getFirstname() != null && getSalesAgent().getFirstname().trim().length() > 0 &&
					getSalesAgent().getLastname() != null && getSalesAgent().getLastname().trim().length() > 0 &&
					getSalesAgent().getAddress() != null && getSalesAgent().getAddress().trim().length() > 0 &&
					getLga_id() > 0 && getState_id() > 0 &&
					getSalesAgentUser().getPassword() != null && getSalesAgentUser().getPassword().trim().length() > 0 &&
							getSalesAgentUser().getUsername() != null && getSalesAgentUser().getUsername().trim().length() > 0)
			{
				if(getSalesAgentUser().getPassword().equals(getConfirm_password()))
				{
					if(userBean.validatePassword(getSalesAgentUser().getPassword()))
					{
					if(getSalesAgentUser().getUsername().indexOf("@") > 0 && getSalesAgentUser().getUsername().indexOf(".") > 0)
					{
						getSalesAgentUser().setPassword(Hasher.getHashValue(getConfirm_password()));
						GeneralDAO gDAO = new GeneralDAO();
						
						Object country = gDAO.find(Country.class, getCtry_id());
						if(country != null)
							getSalesAgent().setCountry((Country)country);
						Object state = gDAO.find(State.class, getState_id());
						if(state != null)
							getSalesAgent().setState((State)state);
						Object lga = gDAO.find(LGA.class, getLga_id());
						if(lga != null)
							getSalesAgent().setLga((LGA)lga);
						
						getSalesAgent().setCrt_dt(new Date());
						
						Hashtable<String, Object> params = new Hashtable<String, Object>();
						params.put("name", "SALES-AGENT");
						Object rolesObj = gDAO.search("Role", params);
						if(rolesObj != null)
						{
							List<Role> roles = (List<Role>)rolesObj;
							for(Role e : roles)
								getSalesAgentUser().setRole(e);
							getSalesAgentUser().setActive(true);
							getSalesAgentUser().setCreatedBy(userBean.getSessionUser());
							getSalesAgentUser().setCrt_dt(new Date());
							getSalesAgentUser().setPartner(getPartner());
							getSalesAgentUser().setType("SALES-AGENT");
							
							gDAO.startTransaction();
							gDAO.save(getSalesAgentUser());
							
							getSalesAgent().setUser(getSalesAgentUser());
							gDAO.save(getSalesAgent());
							
							getSalesAgent().setUniqueID(generateAgentReference(getSalesAgent().getId()));
							gDAO.update(getSalesAgent());
							try {
								gDAO.commit();
								
								userBean.sendEmail(new String[]{getSalesAgentUser().getUsername()}, 
										"Welcome to RMS", 
										MessagesUtil.getSignUpPPEmailMessage(getSalesAgent().getFirstname(), 
												getSalesAgentUser().getUsername(), getConfirm_password(), getSalesAgent().getUniqueID()));
								
								setSalesAgent(null);
								setSalesAgents(null);
								setSelSalesAgent(null);
								setSalesAgentUser(null);
								an.setType("SUCCESS");
								an.setSubject("Success");
								an.setMessage("Sales-Agent created successfully!");
							} catch(Exception ex){
								gDAO.rollback();
								an.setType("ERROR");
								an.setSubject("Error");
								an.setMessage("Message: " + ex.getMessage() + "!");
							}
							
							appNotifBean.getAppNotifications().add(an);
						}
						else
						{
							an.setType("ERROR");
							an.setSubject("Failed");
							an.setMessage("Could not get the sales agent role!");
							appNotifBean.getAppNotifications().add(an);
						}
						gDAO.destroy();
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Username is not in a valid email format!");
						appNotifBean.getAppNotifications().add(an);
					}
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Password does not match the password policy. Please view the help page for more information!");
						appNotifBean.getAppNotifications().add(an);
					}
				}
				else
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Password fields were not the same!");
					appNotifBean.getAppNotifications().add(an);
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("All fields with the '*' sign are required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 14: // ratings
		{
			if(getRating().getRatingDescription() != null && getRating().getRatingDescription().trim().length() > 0 &&
					getRating().getRatingValue() > 0)
			{
				getRating().setCreatedBy(userBean.getSessionUser());
				getRating().setCrt_dt(new Date());
				
				GeneralDAO gDAO = new GeneralDAO();
				gDAO.startTransaction();
				gDAO.save(getRating());
				
				try {
					gDAO.commit();
					setRating(null);
					setRatings(null);
					setSelRating(null);
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage("Rating created successfully!");
				} catch(Exception ex){
					gDAO.rollback();
					an.setType("ERROR");
					an.setSubject("Error");
					an.setMessage("Message: " + ex.getMessage() + "!");
				}
				gDAO.destroy();
				
				appNotifBean.getAppNotifications().add(an);
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("All fields with the '*' sign are required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 15: // resources
		{
			if(getAppResource().getResourceType() != null && getAppResource().getResourceType().trim().length() > 0 &&
					getAppResource().getResourceName() != null && getAppResource().getResourceName().trim().length() > 0 &&
					getAppResourceFile() != null && getAppResourceFile().getSize() > 0)
			{
				getAppResource().setFilesize(getAppResourceFile().getSize());
				getAppResource().setResourceFileName(getFilename(getAppResourceFile()));
				getAppResource().setCrt_dt(new Date());
				boolean uploaded = false;
				try
				{
					FacesContext fc = FacesContext.getCurrentInstance();
					ExternalContext ec = fc.getExternalContext();
					String realPath = ec.getRealPath("/resources");
					getAppResourceFile().write(realPath + "/" + getAppResource().getResourceFileName());
					uploaded = true;
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					an.setType("ERROR");
					an.setSubject("Error");
					an.setMessage("Message: " + ex.getMessage() + "!");
					appNotifBean.getAppNotifications().add(an);
				}
				if(uploaded)
				{
					GeneralDAO gDAO = new GeneralDAO();
					gDAO.startTransaction();
					gDAO.save(getAppResource());
					try
					{
						gDAO.commit();
						setAppResource(null);
						setSelAppResource(null);
						setAppResources(null);
						an.setType("SUCCESS");
						an.setSubject("Success");
						an.setMessage("Resource created successfully!");
						appNotifBean.getAppNotifications().add(an);
					}
					catch(Exception ex)
					{
						gDAO.rollback();
						an.setType("ERROR");
						an.setSubject("Error");
						an.setMessage("Message: " + ex.getMessage() + "!");
						appNotifBean.getAppNotifications().add(an);
					}
					gDAO.destroy();
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("All fields with the '*' sign are required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 16: // trade partner
		{
			if(getTradePartner().getTradePartnerName() != null && getTradePartner().getTradePartnerName().trim().length() > 0 && 
					getTradePartner().getAdminFirstname() != null && getTradePartner().getAdminFirstname().trim().length() > 0 &&
					getTradePartner().getAdminLastname() != null && getTradePartner().getAdminLastname().trim().length() > 0 &&
					getTradePartner().getAddress() != null && getTradePartner().getAddress().trim().length() > 0 &&
					getLga_id() > 0 && getState_id() > 0 &&
					getTpUser().getPassword() != null && getTpUser().getPassword().trim().length() > 0 &&
							getTpUser().getUsername() != null && getTpUser().getUsername().trim().length() > 0)
			{
				if(getTpUser().getPassword().equals(getConfirm_password()))
				{
					if(userBean.validatePassword(getTpUser().getPassword()))
					{
					if(getTpUser().getUsername().indexOf("@") > 0 && getTpUser().getUsername().indexOf(".") > 0)
					{
						getTpUser().setPassword(Hasher.getHashValue(getConfirm_password()));
						GeneralDAO gDAO = new GeneralDAO();
						
						Object country = gDAO.find(Country.class, getCtry_id());
						if(country != null)
							getTradePartner().setCountry((Country)country);
						Object state = gDAO.find(State.class, getState_id());
						if(state != null)
							getTradePartner().setState((State)state);
						Object lga = gDAO.find(LGA.class, getLga_id());
						if(lga != null)
							getTradePartner().setLga((LGA)lga);
						
						getTradePartner().setCrt_dt(new Date());
						
						Hashtable<String, Object> params = new Hashtable<String, Object>();
						params.put("name", "TRADE-PARTNER");
						Object rolesObj = gDAO.search("Role", params);
						if(rolesObj != null)
						{
							List<Role> roles = (List<Role>)rolesObj;
							for(Role e : roles)
								getTpUser().setRole(e);
							getTpUser().setActive(true);
							getTpUser().setCreatedBy(userBean.getSessionUser());
							getTpUser().setCrt_dt(new Date());
							getTpUser().setPartner(getPartner());
							getTpUser().setType("SALES-AGENT");
							
							gDAO.startTransaction();
							gDAO.save(getTpUser());
							
							getTradePartner().setUser(getTpUser());
							gDAO.save(getTradePartner());
							
							getTradePartner().setUniqueID(generateTPReference(getTradePartner().getId()));
							gDAO.update(getTradePartner());
							try {
								gDAO.commit();
								
								userBean.sendEmail(new String[]{getTpUser().getUsername()}, 
										"Welcome to RMS", 
										MessagesUtil.getSignUpTPEmailMessage(getTradePartner().getTradePartnerName(), 
												getTpUser().getUsername(), getConfirm_password(), getTradePartner().getUniqueID()));
								
								setTradePartner(null);
								setTradePartners(null);
								setSelTradePartner(null);
								setTpUser(null);
								an.setType("SUCCESS");
								an.setSubject("Success");
								an.setMessage("Trade partner created successfully!");
							} catch(Exception ex){
								gDAO.rollback();
								an.setType("ERROR");
								an.setSubject("Error");
								an.setMessage("Message: " + ex.getMessage() + "!");
							}
							
							appNotifBean.getAppNotifications().add(an);
						}
						else
						{
							an.setType("ERROR");
							an.setSubject("Failed");
							an.setMessage("Could not get the trade partner role!");
							appNotifBean.getAppNotifications().add(an);
						}
						gDAO.destroy();
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Username is not in a valid email format!");
						appNotifBean.getAppNotifications().add(an);
					}
					}
					else
					{
						an.setType("ERROR");
						an.setSubject("Failed");
						an.setMessage("Password does not match the password policy. Please view the help page for more information!");
						appNotifBean.getAppNotifications().add(an);
					}
				}
				else
				{
					an.setType("ERROR");
					an.setSubject("Failed");
					an.setMessage("Password fields were not the same!");
					appNotifBean.getAppNotifications().add(an);
				}
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("All fields with the '*' sign are required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		case 17: // partner
		{
			if(getNewPartner().getName() != null && getNewPartner().getName().trim().length() > 0 &&
					getNewPartner().getContact_firstname() != null && getNewPartner().getContact_firstname().trim().length() > 0 &&
					getNewPartner().getContact_lastname() != null && getNewPartner().getContact_lastname().trim().length() > 0 &&
					getNewPartner().getContact_email() != null && getNewPartner().getContact_email().trim().length() > 0 &&
					getNewPartner().getContact_phoneno() != null && getNewPartner().getContact_phoneno().trim().length() > 0)
			{
				getNewPartner().setActive(true);
				getNewPartner().setSattrak(false);
				getNewPartner().setCrt_dt(new Date());
				
				GeneralDAO gDAO = new GeneralDAO();
				gDAO.startTransaction();
				gDAO.save(getNewPartner());
				
				try {
					gDAO.commit();
					setNewPartner(null);
					setPartners(null);
					setSelPartner(null);
					an.setType("SUCCESS");
					an.setSubject("Success");
					an.setMessage("Partner created successfully!");
				} catch(Exception ex){
					gDAO.rollback();
					an.setType("ERROR");
					an.setSubject("Error");
					an.setMessage("Message: " + ex.getMessage() + "!");
				}
				gDAO.destroy();
				
				appNotifBean.getAppNotifications().add(an);
			}
			else
			{
				an.setType("ERROR");
				an.setSubject("Failed");
				an.setMessage("All fields with the '*' sign are required!");
				appNotifBean.getAppNotifications().add(an);
			}
			break;
		}
		}
	}
	
	private static String getFilename(Part part)
	{
		for (String cd : part.getHeader("content-disposition").split(";"))
		{
			if(cd.trim().startsWith("filename"))
			{
				String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
			}
		}
		return null;
	}

	public String getSalesAgentUniqueId() {
		return salesAgentUniqueId;
	}

	public void setSalesAgentUniqueId(String salesAgentUniqueId) {
		this.salesAgentUniqueId = salesAgentUniqueId;
	}

	public String getCustomerUniqueId() {
		return customerUniqueId;
	}

	public void setCustomerUniqueId(String customerUniqueId) {
		this.customerUniqueId = customerUniqueId;
	}

	public String getCustomerPhoneNo() {
		return customerPhoneNo;
	}

	public void setCustomerPhoneNo(String customerPhoneNo) {
		this.customerPhoneNo = customerPhoneNo;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public Partner getNewPartner() {
		if(newPartner == null)
			newPartner = new Partner();
		return newPartner;
	}

	public void setNewPartner(Partner newPartner) {
		this.newPartner = newPartner;
	}

	public Partner getSelPartner() {
		return selPartner;
	}

	public void setSelPartner(Partner selPartner) {
		this.selPartner = selPartner;
	}

	@SuppressWarnings("unchecked")
	public List<Partner> getPartners() {
		if(partners == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Object all = gDAO.findAll("Partner");
			if(all != null)
				partners = (List<Partner>)all;
			gDAO.destroy();
		}
		return partners;
	}

	public void setPartners(List<Partner> partners) {
		this.partners = partners;
	}

	public ItemManufacturer getItmManufacturer() {
		if(itmManufacturer == null)
			itmManufacturer = new ItemManufacturer();
		return itmManufacturer;
	}

	public void setItmManufacturer(ItemManufacturer itmManufacturer) {
		this.itmManufacturer = itmManufacturer;
	}

	public ItemManufacturer getSelItmManufacturer() {
		return selItmManufacturer;
	}

	public void setSelItmManufacturer(ItemManufacturer selItmManufacturer) {
		this.selItmManufacturer = selItmManufacturer;
	}

	@SuppressWarnings("unchecked")
	public List<ItemManufacturer> getItmManufacturers() {
		if(itmManufacturers == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Object all = gDAO.findAll("ItemManufacturer");
			if(all != null)
				itmManufacturers = (List<ItemManufacturer>)all;
			gDAO.destroy();
		}
		return itmManufacturers;
	}

	public void setItmManufacturers(List<ItemManufacturer> itmManufacturers) {
		this.itmManufacturers = itmManufacturers;
	}

	public ItemType getItmType() {
		if(itmType == null)
			itmType = new ItemType();
		return itmType;
	}

	public void setItmType(ItemType itmType) {
		this.itmType = itmType;
	}

	public ItemType getSelItmType() {
		return selItmType;
	}

	public void setSelItmType(ItemType selItmType) {
		this.selItmType = selItmType;
	}

	@SuppressWarnings("unchecked")
	public List<ItemType> getItmTypes() {
		if(itmTypes == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Object all = gDAO.findAll("ItemType");
			if(all != null)
				itmTypes = (List<ItemType>)all;
			gDAO.destroy();
		}
		return itmTypes;
	}

	public void setItmTypes(List<ItemType> itmTypes) {
		this.itmTypes = itmTypes;
	}

	public long getItmManu_id() {
		return itmManu_id;
	}

	public void setItmManu_id(long itmManu_id) {
		this.itmManu_id = itmManu_id;
	}

	public long getItmType_id() {
		return itmType_id;
	}

	public void setItmType_id(long itmType_id) {
		this.itmType_id = itmType_id;
	}

	public Item getItm() {
		if(itm == null)
			itm = new Item();
		return itm;
	}

	public void setItm(Item itm) {
		this.itm = itm;
	}

	public Item getSelItm() {
		return selItm;
	}

	public void setSelItm(Item selItm) {
		this.selItm = selItm;
	}

	public void resetItems()
	{
		setItems(null);
	}
	
	@SuppressWarnings("unchecked")
	public List<Item> getItems() {
		boolean research = false;
		if(items == null || (items != null && items.size() == 0))
			research = true;
		else if(items != null && items.size() > 0)
		{
			if(getItmManu_id() > 0)
			{
				for(int i=0; i<items.size(); i++)
				{
					if(items.get(i).getManufacturer() != null && items.get(i).getManufacturer().getId().longValue() == getItmManu_id());
					else
					{
						research = true;
						break;
					}
				}
			}
			if(!research && getItmType_id() > 0)
			{
				for(int i=0; i<items.size(); i++)
				{
					if(items.get(i).getType() != null && items.get(i).getType().getId().longValue() == getItmType_id());
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
			if(getItmManu_id() > 0)
				params.put("manufacturer.id", getItmManu_id());
			if(getItmType_id() > 0)
				params.put("type.id", getItmType_id());
			
			Object ret = gDAO.search("Item", params);
			if(ret != null)
				items = (List<Item>)ret;
			gDAO.destroy();
		}
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public ProductType getPtype() {
		if(ptype == null)
			ptype = new ProductType();
		return ptype;
	}

	public void setPtype(ProductType ptype) {
		this.ptype = ptype;
	}

	public ProductType getSelPtype() {
		return selPtype;
	}

	public void setSelPtype(ProductType selPtype) {
		this.selPtype = selPtype;
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

	public long getPtype_id() {
		return ptype_id;
	}

	public void setPtype_id(long ptype_id) {
		this.ptype_id = ptype_id;
	}

	public List<ProductItem> getProductItemList() {
		if(productItemList == null)
			productItemList = new ArrayList<ProductItem>();
		return productItemList;
	}

	public void setProductItemList(List<ProductItem> productItemList) {
		this.productItemList = productItemList;
	}

	public List<ProductItem> getEditProductItemList() {
		if(editProductItemList == null)
			editProductItemList = new ArrayList<ProductItem>();
		return editProductItemList;
	}

	public void setEditProductItemList(List<ProductItem> editProductItemList) {
		this.editProductItemList = editProductItemList;
	}

	public Product getProduct() {
		if(product == null)
			product = new Product();
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Product getSelProduct() {
		return selProduct;
	}

	public void setSelProduct(Product selProduct) {
		this.selProduct = selProduct;
	}

	public void selectProduct(long id)
	{
		if(products != null)
		{
			for(Product p : products)
			{
				if(p.getId().longValue() == id)
					p.setSelected(true);
				else
					p.setSelected(false);
			}
		}
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
				for(Product p : products)
				{
					params = new Hashtable<String, Object>();
					params.put("product", p);
					Object lsret = gDAO.search("ProductItem", params);
					if(lsret != null) p.setItems((List<ProductItem>)lsret);
				}
			}
			gDAO.destroy();
		}
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public Part getProduct_photo() {
		return product_photo;
	}

	public void setProduct_photo(Part product_photo) {
		this.product_photo = product_photo;
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

	public String getConfirm_password() {
		return confirm_password;
	}

	public void setConfirm_password(String confirm_password) {
		this.confirm_password = confirm_password;
	}

	public String getDobstr() {
		return dobstr;
	}

	public void setDobstr(String dobstr) {
		this.dobstr = dobstr;
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
	public List<LGA> getLgas() {
		boolean research = true;
		if(lgas != null && lgas.size()>0)
		{
			if(lgas.get(0).getState().getId().longValue() == getState_id())
				research = false;
		}
		if(research)
		{
			if(getState_id() > 0)
			{
				GeneralDAO gDAO = new GeneralDAO();
				
				Hashtable<String, Object> params = new Hashtable<String, Object>();
				params.put("state.id", getState_id());
				
				Object ret = gDAO.search("LGA", params);
				if(ret != null)
					lgas = (List<LGA>)ret;
				gDAO.destroy();
			}
		}
		return lgas;
	}

	public void setLgas(List<LGA> lgas) {
		this.lgas = lgas;
	}

	@SuppressWarnings("unchecked")
	public List<LGA> getLgas2() {
		boolean research = true;
		if(lgas2 != null && lgas2.size()>0)
		{
			if(lgas2.get(0).getState().getId().longValue() == getInstaller_state_id())
				research = false;
		}
		if(research)
		{
			if(getInstaller_state_id() > 0)
			{
				GeneralDAO gDAO = new GeneralDAO();
				
				Hashtable<String, Object> params = new Hashtable<String, Object>();
				params.put("state.id", getInstaller_state_id());
				
				Object ret = gDAO.search("LGA", params);
				if(ret != null)
					lgas2 = (List<LGA>)ret;
				gDAO.destroy();
			}
		}
		return lgas2;
	}

	public void setLgas2(List<LGA> lgas2) {
		this.lgas2 = lgas2;
	}

	public User getCusUser() {
		if(cusUser == null)
			cusUser = new User();
		return cusUser;
	}

	public void setCusUser(User cusUser) {
		this.cusUser = cusUser;
	}

	public Customer getCustomer() {
		if(customer == null)
			customer = new Customer();
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Customer getSelCustomer() {
		return selCustomer;
	}

	public void setSelCustomer(Customer selCustomer) {
		this.selCustomer = selCustomer;
	}

	public User getInstallerUser() {
		if(installerUser == null)
			installerUser = new User();
		return installerUser;
	}

	public void setInstallerUser(User installerUser) {
		this.installerUser = installerUser;
	}

	@SuppressWarnings("unchecked")
	public List<Customer> getCustomers() {
		if(customers == null)
		{
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("partner", getPartner());
			
			if(userBean.getSessionSalesAgent() != null)
			{
				params.put("createdBy", userBean.getSessionUser());
			}
			
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

	public long getSalesAgent_id() {
		return salesAgent_id;
	}

	public void setSalesAgent_id(long salesAgent_id) {
		this.salesAgent_id = salesAgent_id;
	}

	public User getSalesAgentUser() {
		if(salesAgentUser == null)
			salesAgentUser = new User();
		return salesAgentUser;
	}

	public void setSalesAgentUser(User salesAgentUser) {
		this.salesAgentUser = salesAgentUser;
	}

	public SalesAgent getSalesAgent() {
		if(salesAgent == null)
			salesAgent = new SalesAgent();
		return salesAgent;
	}

	public void setSalesAgent(SalesAgent salesAgent) {
		this.salesAgent = salesAgent;
	}

	public SalesAgent getSelSalesAgent() {
		return selSalesAgent;
	}

	public void setSelSalesAgent(SalesAgent selSalesAgent) {
		this.selSalesAgent = selSalesAgent;
	}

	@SuppressWarnings("unchecked")
	public List<SalesAgent> getSalesAgents() {
		if(salesAgents == null)
		{
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("user.partner", getPartner());
			
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
	public List<SalesAgent> getSalesAgentsWithStock() {
		if(salesAgentsWithStock == null)
		{
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("user.partner", getPartner());
			params.put("stockDevice", true);
			
			GeneralDAO gDAO = new GeneralDAO();
			Object all = gDAO.search("SalesAgent", params);
			if(all != null)
				salesAgentsWithStock = (List<SalesAgent>)all;
			gDAO.destroy();
		}
		return salesAgentsWithStock;
	}

	public void setSalesAgentsWithStock(List<SalesAgent> salesAgentsWithStock) {
		this.salesAgentsWithStock = salesAgentsWithStock;
	}

	public User getTpUser() {
		if(tpUser == null)
			tpUser = new User();
		return tpUser;
	}

	public void setTpUser(User tpUser) {
		this.tpUser = tpUser;
	}

	public long getTradePartner_id() {
		return tradePartner_id;
	}

	public void setTradePartner_id(long tradePartner_id) {
		this.tradePartner_id = tradePartner_id;
	}

	public TradePartner getTradePartner() {
		if(tradePartner == null)
			tradePartner = new TradePartner();
		return tradePartner;
	}

	public void setTradePartner(TradePartner tradePartner) {
		this.tradePartner = tradePartner;
	}

	public TradePartner getSelTradePartner() {
		return selTradePartner;
	}

	public void setSelTradePartner(TradePartner selTradePartner) {
		this.selTradePartner = selTradePartner;
	}

	@SuppressWarnings("unchecked")
	public List<TradePartner> getTradePartners() {
		if(tradePartners == null)
		{
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("user.partner", getPartner());
			
			GeneralDAO gDAO = new GeneralDAO();
			Object all = gDAO.search("TradePartner", params);
			if(all != null)
				tradePartners = (List<TradePartner>)all;
			gDAO.destroy();
		}
		return tradePartners;
	}

	public void setTradePartners(List<TradePartner> tradePartners) {
		this.tradePartners = tradePartners;
	}

	public long getRating_id() {
		return rating_id;
	}

	public void setRating_id(long rating_id) {
		this.rating_id = rating_id;
	}

	public Rating getRating() {
		if(rating == null)
			rating = new Rating();
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public Rating getSelRating() {
		return selRating;
	}

	public void setSelRating(Rating selRating) {
		this.selRating = selRating;
	}

	@SuppressWarnings("unchecked")
	public List<Rating> getRatings() {
		if(ratings == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from Rating e order by e.ratingValue desc");
			Object list = gDAO.search(q, 0);
			if(list != null)
				ratings = (List<Rating>)list;
			gDAO.destroy();
		}
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

	public InstallerLocation getInstaller() {
		if(installer == null)
			installer = new InstallerLocation();
		return installer;
	}

	public void setInstaller(InstallerLocation installer) {
		this.installer = installer;
	}

	public InstallerLocation getSelInstaller() {
		return selInstaller;
	}

	public void setSelInstaller(InstallerLocation selInstaller) {
		this.selInstaller = selInstaller;
	}

	public InstallerLocation getSelEditInstaller() {
		return selEditInstaller;
	}

	public void setSelEditInstaller(InstallerLocation selEditInstaller) {
		this.selEditInstaller = selEditInstaller;
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
	public List<InstallerLocation> getInstallersByLGA2() {
		boolean research = false;
		if(installersByLGA2 == null || (installersByLGA2 != null && installersByLGA2.size() == 0))
			research = true;
		else if(installersByLGA2 != null && installersByLGA2.size() > 0)
		{
			if(getInstaller_lga_id() > 0)
				for(InstallerLocation e : installersByLGA2)
				{
					if(e.getLga() != null && e.getLga().getId().longValue() == getInstaller_lga_id());
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
			params.put("lga.id", getInstaller_lga_id());
			
			GeneralDAO gDAO = new GeneralDAO();
			Object obj = gDAO.search("InstallerLocation", params);
			if(obj != null)
				installersByLGA2 = (List<InstallerLocation>)obj;
			gDAO.destroy();
		}
		return installersByLGA2;
	}

	public void setInstallersByLGA2(List<InstallerLocation> installersByLGA2) {
		this.installersByLGA2 = installersByLGA2;
	}

	public Agent getAgent() {
		if(agent == null)
			agent = new Agent();
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Agent getSelAgent() {
		return selAgent;
	}

	public void setSelAgent(Agent selAgent) {
		this.selAgent = selAgent;
	}

	public void resetInstallerAgents() {
		setInstallerAgents(null);
	}
	
	@SuppressWarnings("unchecked")
	public List<Agent> getInstallerAgents() {
		boolean research = false;
		if(installerAgents == null || (installerAgents != null && installerAgents.size() == 0))
			research = true;
		else if(installerAgents != null && installerAgents.size() > 0)
		{
			if(installerAgents.get(0).getCompany().getId().longValue() == getInstaller_id());
			else
				research = true;
		}
		if(research)
		{
			installerAgents = null;
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("company.id", getInstaller_id());
			GeneralDAO gDAO = new GeneralDAO();
			Object list = gDAO.search("Agent", params);
			if(list != null)
				installerAgents = (List<Agent>)list;
			gDAO.destroy();
		}
		return installerAgents;
	}

	public void setInstallerAgents(List<Agent> installerAgents) {
		this.installerAgents = installerAgents;
	}
	
	public void resetMyAgents()
	{
		setMyAgents(null);
	}

	@SuppressWarnings("unchecked")
	public List<Agent> getMyAgents() {
		if(myAgents == null && userBean.getSessionInstaller() != null)
		{
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("company", userBean.getSessionInstaller());
			GeneralDAO gDAO = new GeneralDAO();
			Object list = gDAO.search("Agent", params);
			if(list != null)
				myAgents = (List<Agent>)list;
			gDAO.destroy();
		}
		return myAgents;
	}

	public void setMyAgents(List<Agent> myAgents) {
		this.myAgents = myAgents;
	}

	@SuppressWarnings("unchecked")
	public List<Agent> getAllAgents() {
		GeneralDAO gDAO = new GeneralDAO();
		Object list = gDAO.findAll("Agent");
		if(list != null)
			allAgents = (List<Agent>)list;
		gDAO.destroy();
		return allAgents;
	}

	public void setAllAgents(List<Agent> allAgents) {
		this.allAgents = allAgents;
	}

	public Part getInstaller_photo() {
		return installer_photo;
	}

	public void setInstaller_photo(Part installer_photo) {
		this.installer_photo = installer_photo;
	}

	public Part getAgent_photo() {
		return agent_photo;
	}

	public void setAgent_photo(Part agent_photo) {
		this.agent_photo = agent_photo;
	}

	public Part getCompleteJobCard() {
		return completeJobCard;
	}

	public void setCompleteJobCard(Part completeJobCard) {
		this.completeJobCard = completeJobCard;
	}

	public User getPpUser() {
		if(ppUser == null)
			ppUser = new User();
		return ppUser;
	}

	public void setPpUser(User ppUser) {
		this.ppUser = ppUser;
	}

	public PartnerPersonnel getPp() {
		if(pp == null)
			pp = new PartnerPersonnel();
		return pp;
	}

	public void setPp(PartnerPersonnel pp) {
		this.pp = pp;
	}

	public PartnerPersonnel getSelPP() {
		return selPP;
	}

	public void setSelPP(PartnerPersonnel selPP) {
		this.selPP = selPP;
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

	public String getItmMove_dt_str() {
		return itmMove_dt_str;
	}

	public void setItmMove_dt_str(String itmMove_dt_str) {
		this.itmMove_dt_str = itmMove_dt_str;
	}

	public long getItm_id() {
		return itm_id;
	}

	public void setItm_id(long itm_id) {
		this.itm_id = itm_id;
	}

	public long getProduct_id() {
		return product_id;
	}

	public void setProduct_id(long product_id) {
		this.product_id = product_id;
	}

	public long getInstaller_id() {
		return installer_id;
	}

	public void setInstaller_id(long installer_id) {
		this.installer_id = installer_id;
		selInstaller = null;
		if(installer_id > 0)
		{
			if(selInstaller != null && selInstaller.getId().longValue() == installer_id);
			else
			{
				GeneralDAO gDAO = new GeneralDAO();
				Object installer = gDAO.find(InstallerLocation.class, installer_id);
				if(installer != null)
					selInstaller = (InstallerLocation)installer;
				gDAO.destroy();
			}
		}
	}

	public long getEdit_installer_id() {
		return edit_installer_id;
	}

	public void setEdit_installer_id(long edit_installer_id) {
		this.edit_installer_id = edit_installer_id;
		selEditInstaller = null;
		if(edit_installer_id > 0)
		{
			if(selEditInstaller != null && selEditInstaller.getId().longValue() == edit_installer_id);
			else
			{
				GeneralDAO gDAO = new GeneralDAO();
				Object installer = gDAO.find(InstallerLocation.class, edit_installer_id);
				if(installer != null)
					selEditInstaller = (InstallerLocation)installer;
				gDAO.destroy();
			}
		}
	}

	public ItemMove getItmMove() {
		if(itmMove == null)
			itmMove = new ItemMove();
		return itmMove;
	}

	public void setItmMove(ItemMove itmMove) {
		this.itmMove = itmMove;
	}

	public ItemMove getSelItmMove() {
		return selItmMove;
	}

	public void setSelItmMove(ItemMove selItmMove) {
		this.selItmMove = selItmMove;
	}

	public List<ItemMove> getItmMoveList() {
		return itmMoveList;
	}

	public void setItmMoveList(List<ItemMove> itmMoveList) {
		this.itmMoveList = itmMoveList;
	}

	@SuppressWarnings("unchecked")
	public List<ProductTypeCommission> getPtypeCommissionList() {
		if(ptypeCommissionList == null)
		{
			ptypeCommissionList = new ArrayList<ProductTypeCommission>();
			List<ProductType> ptList = getPtypes();
			GeneralDAO gDAO = new GeneralDAO();
			for(ProductType pt : ptList)
			{
				ProductTypeCommission ptc = null;
				Hashtable<String, Object> params = new Hashtable<String, Object>();
				params.put("partner", userBean.getSessionPartner()); // don't let sattrak admin change commission of our partners, its their own business
				params.put("productType", pt);
				Object ptcObj = gDAO.search("ProductTypeCommission", params);
				if(ptcObj != null)
				{
					List<ProductTypeCommission> ptcList = (List<ProductTypeCommission>)ptcObj;
					for(ProductTypeCommission e : ptcList)
						ptc = e;
				}
				if(ptc != null)
					ptypeCommissionList.add(ptc);
				else
				{
					ptc = new ProductTypeCommission();
					ptc.setProductType(pt);
					ptc.setPartner(userBean.getSessionPartner());
					ptypeCommissionList.add(ptc);
				}
			}
			gDAO.destroy();
		}
		return ptypeCommissionList;
	}

	public void setPtypeCommissionList(
			List<ProductTypeCommission> ptypeCommissionList) {
		this.ptypeCommissionList = ptypeCommissionList;
	}

	public long getVtype_id() {
		return vtype_id;
	}

	public void setVtype_id(long vtype_id) {
		this.vtype_id = vtype_id;
	}

	public long getVmake_id() {
		return vmake_id;
	}

	public void setVmake_id(long vmake_id) {
		this.vmake_id = vmake_id;
	}

	public long getSlot_id() {
		return slot_id;
	}

	public void setSlot_id(long slot_id) {
		this.slot_id = slot_id;
	}

	public double getTotal_cost() {
		total_cost = 0;
		if(getProducts() != null)
		{
			for(Product p : getProducts())
			{
				if(p.isSelected())
				{
					total_cost += p.getFirstYearPayment();
					break; // just the first selected product
				}
			}
		}
		return total_cost;
	}

	public void setTotal_cost(double total_cost) {
		this.total_cost = total_cost;
	}

	@SuppressWarnings("unchecked")
	public List<Slot> getSlots() {
		boolean research = true; // false;
		/*if((slots == null || (slots != null && slots.size() == 0)) && getBooking().getBooked_dt() != null)
			research = true;*/
		if(research)
		{
			List<Slot> allSlots = null;
			GeneralDAO gDAO = new GeneralDAO();
			Object all = gDAO.findAll("Slot");
			if(all != null)
				allSlots = (List<Slot>)all;
			
			if(allSlots != null && getSelInstaller() != null)
			{
				Hashtable<String, Object> params = new Hashtable<String, Object>();
				params.put("booked_dt", getBooking().getBooked_dt());
				params.put("installer", getSelInstaller());
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
						if(!exists || getSelInstaller().getMaxMultiSlotInstallation() == 0 || getSelInstaller().getMaxMultiSlotInstallation() > slot_used_size)
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
	public List<Slot> getEdit_slots() {
		boolean research = false;
		if((edit_slots == null || (edit_slots != null && edit_slots.size() == 0)) && getBooking().getBooked_dt() != null)
			research = true;
		if(research)
		{
			List<Slot> allSlots = null;
			GeneralDAO gDAO = new GeneralDAO();
			Object all = gDAO.findAll("Slot");
			if(all != null)
				allSlots = (List<Slot>)all;
			
			if(allSlots != null && getSelEditInstaller() != null)
			{
				Hashtable<String, Object> params = new Hashtable<String, Object>();
				params.put("booked_dt", getBooking().getBooked_dt());
				params.put("installer", getSelEditInstaller());
				params.put("cancel", false);
				Object obj = gDAO.search("InstallerLocationJobSchedule", params);
				if(obj != null)
				{
					edit_slots = new ArrayList<Slot>();
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
						if(!exists || getSelEditInstaller().getMaxMultiSlotInstallation() == 0 || getSelEditInstaller().getMaxMultiSlotInstallation() > slot_used_size)
							edit_slots.add(e);
					}
				}
			}
			gDAO.destroy();
		}
		return edit_slots;
	}

	public void setEdit_slots(List<Slot> edit_slots) {
		this.edit_slots = edit_slots;
	}

	@SuppressWarnings("unchecked")
	public List<VehicleType> getVtypes() {
		if(vtypes == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Object all = gDAO.findAll("VehicleType");
			if(all != null)
				vtypes = (List<VehicleType>)all;
			gDAO.destroy();
		}
		return vtypes;
	}

	public void setVtypes(List<VehicleType> vtypes) {
		this.vtypes = vtypes;
	}

	@SuppressWarnings("unchecked")
	public List<VehicleMake> getVmakes() {
		if(vmakes == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Object all = gDAO.findAll("VehicleMake");
			if(all != null)
				vmakes = (List<VehicleMake>)all;
			gDAO.destroy();
		}
		return vmakes;
	}

	public void setVmakes(List<VehicleMake> vmakes) {
		this.vmakes = vmakes;
	}

	public String getBook_dt_str() {
		return book_dt_str;
	}

	public void setBook_dt_str(String book_dt_str) {
		this.book_dt_str = book_dt_str;
		if(book_dt_str != null)
		{
			try
			{
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Date dt = sdf.parse(book_dt_str);
				
				if(getBooking().getBooked_dt() != null)
				{
					if(!dt.equals(getBooking().getBooked_dt()))
					{
						getBooking().setBooked_dt(dt);
						setSlots(null);
						setEdit_slots(null);
					}
				}
				else
				{
					getBooking().setBooked_dt(dt);
				}
			}catch(Exception ex){}
		}
		else
			getBooking().setBooked_dt(null);
	}

	public String getRework_dt_str() {
		return rework_dt_str;
	}

	public void setRework_dt_str(String rework_dt_str) {
		this.rework_dt_str = rework_dt_str;
	}

	public InstallerLocationJobSchedule getBooking() {
		if(booking == null)
			booking = new InstallerLocationJobSchedule();
		return booking;
	}

	public void setBooking(InstallerLocationJobSchedule booking) {
		this.booking = booking;
	}

	public String getStart_dt_str() {
		return start_dt_str;
	}

	public void setStart_dt_str(String start_dt_str) {
		this.start_dt_str = start_dt_str;
	}

	public String getEnd_dt_str() {
		return end_dt_str;
	}

	public void setEnd_dt_str(String end_dt_str) {
		this.end_dt_str = end_dt_str;
	}

	public List<InstallationDeviceUse> getInstallationDeviceUseList() {
		if(installationDeviceUseList == null)
			installationDeviceUseList = new ArrayList<InstallationDeviceUse>();
		return installationDeviceUseList;
	}

	public void setInstallationDeviceUseList(
			List<InstallationDeviceUse> installationDeviceUseList) {
		this.installationDeviceUseList = installationDeviceUseList;
	}

	public List<InstallationReworkDeviceUse> getInstallationReworkDeviceUseList() {
		if(installationReworkDeviceUseList == null)
			installationReworkDeviceUseList = new ArrayList<InstallationReworkDeviceUse>();
		return installationReworkDeviceUseList;
	}

	public void setInstallationReworkDeviceUseList(
			List<InstallationReworkDeviceUse> installationReworkDeviceUseList) {
		this.installationReworkDeviceUseList = installationReworkDeviceUseList;
	}

	public InstallerLocationJobSchedule getInstallation() {
		if(installation == null)
			installation = new InstallerLocationJobSchedule();
		return installation;
	}

	public void setInstallation(InstallerLocationJobSchedule installation) {
		this.installation = installation;
	}

	public InstallerLocationJobSchedule getSelectedInstallation() {
		return selectedInstallation;
	}

	public void setSelectedInstallation(
			InstallerLocationJobSchedule selectedInstallation) {
		this.selectedInstallation = selectedInstallation;
	}

	public InstallerLocationJobSchedule getSelNotCheckInJob() {
		return selNotCheckInJob;
	}

	public void setSelNotCheckInJob(InstallerLocationJobSchedule selNotCheckInJob) {
		this.selNotCheckInJob = selNotCheckInJob;
	}
	
	private void resetMyJobs()
	{
		setMyNotCheckInJobs(null);
		setMyUpcomingJobs(null);
		setMyInprogressJobs(null);
		setMyCancelledJobs(null);
		setMyCheckedInJobs(null);
		setMyOverdueJobs(null);
		setMyPendingJobs(null);
		setMyCompletedJobs(null);
	}

	public void resetMyPendingJobs()
	{
		setMyPendingJobs(null);
	}
	
	@SuppressWarnings("unchecked")
	public List<InstallerLocationJobSchedule> getMyPendingJobs() {
		if(myPendingJobs == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and e.completed=:completed and e.booked_dt between :st_dt and :end_dt order by e.booked_dt, e.slot.start_hour");
			q.setParameter("installer", userBean.getSessionInstaller());
			q.setParameter("completed", false);
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
			cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
			Date st_dt = cal.getTime();
			
			cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
			cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
			Date end_dt = cal.getTime();
			
			if(getMyPendingJobsDate_st_str() != null && getMyPendingJobsDate_st_str().trim().length() > 0 &&
					getMyPendingJobsDate_end_str() != null && getMyPendingJobsDate_end_str().trim().length() > 0)
			{
				try
				{
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					st_dt = sdf.parse(getMyPendingJobsDate_st_str());
					end_dt = sdf.parse(getMyPendingJobsDate_end_str());
				} catch(Exception ex){ ex.printStackTrace(); }
			}
			q.setParameter("st_dt", st_dt);
			q.setParameter("end_dt", end_dt);
			
			Object list = gDAO.search(q, 0);
			if(list != null)
				myPendingJobs = (List<InstallerLocationJobSchedule>)list;
			gDAO.destroy();
		}
		return myPendingJobs;
	}

	public void setMyPendingJobs(List<InstallerLocationJobSchedule> myPendingJobs) {
		this.myPendingJobs = myPendingJobs;
	}

	public void resetMyNotCheckInJobs()
	{
		setMyNotCheckInJobs(null);
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public List<InstallerLocationJobSchedule> getMyNotCheckInJobs() {
		if(myNotCheckInJobs == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and e.checkedIn=:checkedIn and e.paid=:paid and e.cancel=:cancel order by e.booked_dt, e.slot.start_hour"); // and e.booked_dt between :st_dt and :end_dt
			q.setParameter("installer", userBean.getSessionInstaller());
			q.setParameter("checkedIn", false);
			q.setParameter("paid", true);
			q.setParameter("cancel", false);
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
			cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
			Date st_dt = cal.getTime();
			
			cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
			cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
			Date end_dt = cal.getTime();
			
			//q.setParameter("st_dt", st_dt);
			//q.setParameter("end_dt", end_dt);
			
			Object list = gDAO.search(q, 0);
			if(list != null)
				myNotCheckInJobs = (List<InstallerLocationJobSchedule>)list;
			gDAO.destroy();
		}
		return myNotCheckInJobs;
	}

	public void setMyNotCheckInJobs(
			List<InstallerLocationJobSchedule> myNotCheckInJobs) {
		this.myNotCheckInJobs = myNotCheckInJobs;
	}
	
	public void resetMyCheckedInJobs()
	{
		setMyCheckedInJobs(null);
	}

	@SuppressWarnings("unchecked")
	public List<InstallerLocationJobSchedule> getMyCheckedInJobs() {
		if(myCheckedInJobs == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and e.checkedIn=:checkedIn and e.paid=:paid and e.completed=:completed order by e.booked_dt, e.slot.start_hour");
			q.setParameter("installer", userBean.getSessionInstaller());
			q.setParameter("checkedIn", true);
			q.setParameter("paid", true);
			q.setParameter("completed", false);
			
			Object list = gDAO.search(q, 0);
			if(list != null)
				myCheckedInJobs = (List<InstallerLocationJobSchedule>)list;
			gDAO.destroy();
		}
		return myCheckedInJobs;
	}

	public void setMyCheckedInJobs(
			List<InstallerLocationJobSchedule> myCheckedInJobs) {
		this.myCheckedInJobs = myCheckedInJobs;
	}

	@SuppressWarnings("unchecked")
	public List<InstallerLocationJobSchedule> getJobsPendingConfirmation() {
		if(jobsPendingConfirmation == null)
		{
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("require_confirmation", true);
			params.put("completed", true);
			params.put("cancel", false);
			params.put("confirmed", false);
			
			GeneralDAO gDAO = new GeneralDAO();
			Object list = gDAO.search("InstallerLocationJobSchedule", params);
			if(list != null)
				jobsPendingConfirmation = (List<InstallerLocationJobSchedule>)list;
			gDAO.destroy();
		}
		return jobsPendingConfirmation;
	}

	public void setJobsPendingConfirmation(
			List<InstallerLocationJobSchedule> jobsPendingConfirmation) {
		this.jobsPendingConfirmation = jobsPendingConfirmation;
	}

	@SuppressWarnings("unchecked")
	public List<InstallerLocationJobSchedule> getMySchedules_cus() {
		if(mySchedules_cus == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.customer=:customer and e.cancel=:cancel order by e.booked_dt desc");
			q.setParameter("customer", userBean.getSessionCustomer());
			q.setParameter("cancel", false);
			
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

	public List<InstallerLocationJobSchedule> getCheckedInSchedules() {
		return checkedInSchedules;
	}

	public void setCheckedInSchedules(
			List<InstallerLocationJobSchedule> checkedInSchedules) {
		this.checkedInSchedules = checkedInSchedules;
	}

	@SuppressWarnings("unchecked")
	public List<InstallerLocationJobSchedule> getMyUpcomingJobs() {
		if(myUpcomingJobs == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			
			Query q = null;
			if(userBean.getSessionInstaller() != null)
			{
				q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and e.checkedIn=:checkedIn and e.paid=:paid and e.cancel=:cancel and e.booked_dt >= :dt order by e.booked_dt, e.slot.start_hour"); // and e.booked_dt between :st_dt and :end_dt
				q.setParameter("installer", userBean.getSessionInstaller());
			}
			else
			{
				q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.customer.partner=:partner and e.checkedIn=:checkedIn and e.paid=:paid and e.cancel=:cancel and e.booked_dt >= :dt order by e.booked_dt, e.slot.start_hour"); // and e.booked_dt between :st_dt and :end_dt
				q.setParameter("partner", getPartner());
			}
			
			q.setParameter("checkedIn", false);
			q.setParameter("paid", true);
			q.setParameter("cancel", false);
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
			cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
			Date dt = cal.getTime();
			q.setParameter("dt", dt);
			
			Object list = gDAO.search(q, 0);
			if(list != null)
				myUpcomingJobs = (List<InstallerLocationJobSchedule>)list;
			gDAO.destroy();
		}
		return myUpcomingJobs;
	}

	public void setMyUpcomingJobs(List<InstallerLocationJobSchedule> myUpcomingJobs) {
		this.myUpcomingJobs = myUpcomingJobs;
	}

	@SuppressWarnings("unchecked")
	public List<InstallerLocationJobSchedule> getMyInprogressJobs() {
		if(myInprogressJobs == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			
			Query q = null;
			if(userBean.getSessionInstaller() != null)
			{
				q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and e.checkedIn=:checkedIn and e.paid=:paid and e.cancel=:cancel and e.completed=:completed order by e.booked_dt, e.slot.start_hour"); // and e.booked_dt between :st_dt and :end_dt
				q.setParameter("installer", userBean.getSessionInstaller());
			}
			else
			{
				q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.customer.partner=:partner and e.checkedIn=:checkedIn and e.paid=:paid and e.cancel=:cancel and e.completed=:completed order by e.booked_dt, e.slot.start_hour"); // and e.booked_dt between :st_dt and :end_dt
				q.setParameter("partner", getPartner());
			}
			
			q.setParameter("checkedIn", true);
			q.setParameter("paid", true);
			q.setParameter("cancel", false);
			q.setParameter("completed", false);
			
			Object list = gDAO.search(q, 0);
			if(list != null)
				myInprogressJobs = (List<InstallerLocationJobSchedule>)list;
			gDAO.destroy();
		}
		return myInprogressJobs;
	}

	public void setMyInprogressJobs(
			List<InstallerLocationJobSchedule> myInprogressJobs) {
		this.myInprogressJobs = myInprogressJobs;
	}

	@SuppressWarnings("unchecked")
	public List<InstallerLocationJobSchedule> getMyOverdueJobs() {
		if(myOverdueJobs == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			
			Query q = null;
			if(userBean.getSessionInstaller() != null)
			{
				q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and e.checkedIn=:checkedIn and e.paid=:paid and e.cancel=:cancel and e.booked_dt < :dt order by e.booked_dt, e.slot.start_hour"); // and e.booked_dt between :st_dt and :end_dt
				q.setParameter("installer", userBean.getSessionInstaller());
			}
			else
			{
				q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.customer.partner=:partner and e.checkedIn=:checkedIn and e.paid=:paid and e.cancel=:cancel and e.booked_dt < :dt order by e.booked_dt, e.slot.start_hour"); // and e.booked_dt between :st_dt and :end_dt
				q.setParameter("partner", getPartner());
			}
			
			q.setParameter("checkedIn", false);
			q.setParameter("paid", true);
			q.setParameter("cancel", false);
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
			cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
			Date dt = cal.getTime();
			q.setParameter("dt", dt);
			
			Object list = gDAO.search(q, 0);
			if(list != null)
				myOverdueJobs = (List<InstallerLocationJobSchedule>)list;
			gDAO.destroy();
		}
		return myOverdueJobs;
	}

	public void setMyOverdueJobs(List<InstallerLocationJobSchedule> myOverdueJobs) {
		this.myOverdueJobs = myOverdueJobs;
	}

	@SuppressWarnings("unchecked")
	public List<InstallerLocationJobSchedule> getMyCompletedJobs() {
		if(myCompletedJobs == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			
			Query q = null;
			if(userBean.getSessionInstaller() != null)
			{
				q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and e.checkedIn=:checkedIn and e.paid=:paid and e.cancel=:cancel and e.completed=:completed order by e.booked_dt, e.slot.start_hour"); // and e.booked_dt between :st_dt and :end_dt
				q.setParameter("installer", userBean.getSessionInstaller());
			}
			else
			{
				q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.customer.partner=:partner and e.checkedIn=:checkedIn and e.paid=:paid and e.cancel=:cancel and e.completed=:completed order by e.booked_dt, e.slot.start_hour"); // and e.booked_dt between :st_dt and :end_dt
				q.setParameter("partner", getPartner());
			}

			q.setParameter("checkedIn", true);
			q.setParameter("paid", true);
			q.setParameter("cancel", false);
			q.setParameter("completed", true);
			
			Object list = gDAO.search(q, 0);
			if(list != null)
				myCompletedJobs = (List<InstallerLocationJobSchedule>)list;
			gDAO.destroy();
		}
		return myCompletedJobs;
	}

	public void setMyCompletedJobs(
			List<InstallerLocationJobSchedule> myCompletedJobs) {
		this.myCompletedJobs = myCompletedJobs;
	}

	@SuppressWarnings("unchecked")
	public List<InstallerLocationJobSchedule> getMyCancelledJobs() {
		if(myCancelledJobs == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			
			Query q = null;
			if(userBean.getSessionInstaller() != null)
			{
				q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.installer=:installer and e.cancel=:cancel order by e.booked_dt, e.slot.start_hour"); // and e.booked_dt between :st_dt and :end_dt
				q.setParameter("installer", userBean.getSessionInstaller());
			}
			else
			{
				q = gDAO.createQuery("Select e from InstallerLocationJobSchedule e where e.customer.partner=:partner and e.cancel=:cancel order by e.booked_dt, e.slot.start_hour"); // and e.booked_dt between :st_dt and :end_dt
				q.setParameter("partner", getPartner());
			}
			
			q.setParameter("cancel", true);
			
			Object list = gDAO.search(q, 0);
			if(list != null)
				myCancelledJobs = (List<InstallerLocationJobSchedule>)list;
			gDAO.destroy();
		}
		return myCancelledJobs;
	}

	public void setMyCancelledJobs(
			List<InstallerLocationJobSchedule> myCancelledJobs) {
		this.myCancelledJobs = myCancelledJobs;
	}

	public String getMyPendingJobsDate_st_str() {
		return myPendingJobsDate_st_str;
	}

	public void setMyPendingJobsDate_st_str(String myPendingJobsDate_st_str) {
		this.myPendingJobsDate_st_str = myPendingJobsDate_st_str;
	}

	public String getMyPendingJobsDate_end_str() {
		return myPendingJobsDate_end_str;
	}

	public void setMyPendingJobsDate_end_str(String myPendingJobsDate_end_str) {
		this.myPendingJobsDate_end_str = myPendingJobsDate_end_str;
	}

	public long getNot_checkedin_installation_id() {
		return not_checkedin_installation_id;
	}

	public void setNot_checkedin_installation_id(long not_checkedin_installation_id) {
		this.not_checkedin_installation_id = not_checkedin_installation_id;
		if(not_checkedin_installation_id > 0)
		{
			boolean search = true;
			if(selNotCheckInJob != null)
			{
				if(selNotCheckInJob.getId().longValue() == not_checkedin_installation_id)
					search = false;
			}
			if(search)
			{
				for(InstallerLocationJobSchedule e : getMyNotCheckInJobs())
				{
					if(e.getId().longValue() == not_checkedin_installation_id)
					{
						selNotCheckInJob = e;
						break;
					}
				}
			}
		}
		else if(selNotCheckInJob != null)
		{
			selNotCheckInJob = null;
		}
	}

	public long getCheckedin_installation_id() {
		return checkedin_installation_id;
	}

	public void setCheckedin_installation_id(long checkedin_installation_id) {
		this.checkedin_installation_id = checkedin_installation_id;
		if(checkedin_installation_id > 0)
		{
			boolean search = true;
			if(selCheckedInJob != null)
			{
				if(selCheckedInJob.getId().longValue() == checkedin_installation_id)
					search = false;
			}
			if(search)
			{
				for(InstallerLocationJobSchedule e : getMyCheckedInJobs())
				{
					if(e.getId().longValue() == checkedin_installation_id)
					{
						selCheckedInJob = e;
						break;
					}
				}
			}
		}
		else if(selCheckedInJob != null)
		{
			selCheckedInJob = null;
		}
	}

	public long getItem_id() {
		return item_id;
	}

	public void setItem_id(long item_id) {
		this.item_id = item_id;
	}

	public long getJob_installer_id() {
		return job_installer_id;
	}

	public void setJob_installer_id(long job_installer_id) {
		this.job_installer_id = job_installer_id;
	}

	public int getItem_used_count() {
		return item_used_count;
	}

	public void setItem_used_count(int item_used_count) {
		this.item_used_count = item_used_count;
	}

	public int getItem_return_count() {
		return item_return_count;
	}

	public void setItem_return_count(int item_return_count) {
		this.item_return_count = item_return_count;
	}

	public String getReturn_reason() {
		return return_reason;
	}

	public void setReturn_reason(String return_reason) {
		this.return_reason = return_reason;
	}

	public String getReturn_remarks() {
		return return_remarks;
	}

	public void setReturn_remarks(String return_remarks) {
		this.return_remarks = return_remarks;
	}

	@SuppressWarnings("unchecked")
	public List<CustomerProduct> getMyProducts() {
		if(myProducts == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from CustomerProduct e where e.customer=:customer and (e.status=:active or e.status=:expired) order by e.booked_dt desc");
			q.setParameter("customer", userBean.getSessionCustomer());
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

	public List<InstallerLocationJobSchedule> getSearchedInstallation() {
		return searchedInstallation;
	}

	public void setSearchedInstallation(
			List<InstallerLocationJobSchedule> searchedInstallation) {
		this.searchedInstallation = searchedInstallation;
	}

	public BigDecimal getExpectedEarnings() {
		if(expectedEarnings == null)
			expectedEarnings = new BigDecimal(0.00);
		return expectedEarnings;
	}

	public void setExpectedEarnings(BigDecimal expectedEarnings) {
		this.expectedEarnings = expectedEarnings;
	}

	public BigDecimal getActualEarnings() {
		if(actualEarnings == null)
			actualEarnings = new BigDecimal(0.00);
		return actualEarnings;
	}

	public void setActualEarnings(BigDecimal actualEarnings) {
		this.actualEarnings = actualEarnings;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public long getTotalPendingCheckIn() {
		return totalPendingCheckIn;
	}

	public void setTotalPendingCheckIn(long totalPendingCheckIn) {
		this.totalPendingCheckIn = totalPendingCheckIn;
	}

	public long getTotalCheckInPaid() {
		return totalCheckInPaid;
	}

	public void setTotalCheckInPaid(long totalCheckInPaid) {
		this.totalCheckInPaid = totalCheckInPaid;
	}

	public long getTotalCompleted() {
		return totalCompleted;
	}

	public void setTotalCompleted(long totalCompleted) {
		this.totalCompleted = totalCompleted;
	}

	public long getTotalCanceled() {
		return totalCanceled;
	}

	public void setTotalCanceled(long totalCanceled) {
		this.totalCanceled = totalCanceled;
	}

	public long getTotalRConfirm() {
		return totalRConfirm;
	}

	public void setTotalRConfirm(long totalRConfirm) {
		this.totalRConfirm = totalRConfirm;
	}

	public long getTotalConfirm() {
		return totalConfirm;
	}

	public void setTotalConfirm(long totalConfirm) {
		this.totalConfirm = totalConfirm;
	}

	public long getTotalRefund() {
		return totalRefund;
	}

	public void setTotalRefund(long totalRefund) {
		this.totalRefund = totalRefund;
	}

	@SuppressWarnings("unchecked")
	public List<String[]> getAssetStockByType() {
		GeneralDAO gDAO = new GeneralDAO();
		
		if(userBean.getSessionInstaller() != null)
		{
			Object itemsTypes = gDAO.findAll("ItemType");
			if(itemsTypes != null)
			{
				assetStockByType = new ArrayList<String[]>();
				List<ItemType> itemsTypesList = (List<ItemType>)itemsTypes;
				for(ItemType e : itemsTypesList)
				{
					String[] rec = new String[2];
					rec[0] = e.getName();
					long total = 0;
					Query q = gDAO.createQuery("Select SUM(e.count) from InstallerLocationItem e where e.installer=:installer and e.item.type=:type");
					q.setParameter("installer", userBean.getSessionInstaller());
					q.setParameter("type", e);
					
					Object list = gDAO.search(q, 1);
					if(list != null)
					{
						Long ret = (Long)list;
						if(ret != null)
						{
							total = ret;
						}
					}
					rec[1] = ""+total;
					assetStockByType.add(rec);
				}
			}
		}
		else
		{
			Object itemsTypes = gDAO.findAll("ItemType");
			if(itemsTypes != null)
			{
				assetStockByType = new ArrayList<String[]>();
				List<ItemType> itemsTypesList = (List<ItemType>)itemsTypes;
				for(ItemType e : itemsTypesList)
				{
					String[] rec = new String[2];
					rec[0] = e.getName();
					long total = 0;
					Query q = gDAO.createQuery("Select SUM(e.stocklevel) from Item e where e.type=:type");
					q.setParameter("type", e);
					
					Object list = gDAO.search(q, 1);
					if(list != null)
					{
						Long ret = (Long)list;
						if(ret != null)
						{
							total = ret;
						}
					}
					rec[1] = ""+total;
					assetStockByType.add(rec);
				}
			}
		}
		gDAO.destroy();
		return assetStockByType;
	}

	public void setAssetStockByType(List<String[]> assetStockByType) {
		this.assetStockByType = assetStockByType;
	}

	@SuppressWarnings("unchecked")
	public List<String[]> getAssetStockByModel() {
		GeneralDAO gDAO = new GeneralDAO();
		if(userBean.getSessionInstaller() == null)
		{
			Query q = gDAO.createQuery("Select DISTINCT(e.model) from Item e");
			Object list = gDAO.search(q, 0);
			if(list != null)
			{
				assetStockByModel = new ArrayList<String[]>();
				List<String> ret = (List<String>)list;
				for(String model : ret)
				{
					String[] rec = new String[2];
					rec[0] = model;
					long total = 0;
					q = gDAO.createQuery("Select SUM(e.stocklevel) from Item e where e.model=:model");
					q.setParameter("model", model);
					
					Object retObj = gDAO.search(q, 1);
					if(retObj != null)
					{
						Long lng = (Long)retObj;
						if(lng != null)
						{
							total = lng;
						}
					}
					rec[1] = ""+total;
					assetStockByModel.add(rec);
				}
			}
		}
		else
		{
			Query q = gDAO.createQuery("Select DISTINCT(e.model) from Item e");
			Object list = gDAO.search(q, 0);
			if(list != null)
			{
				assetStockByModel = new ArrayList<String[]>();
				List<String> ret = (List<String>)list;
				for(String model : ret)
				{
					String[] rec = new String[2];
					rec[0] = model;
					long total = 0;
					q = gDAO.createQuery("Select SUM(e.count) from InstallerLocationItem e where e.installer=:installer and e.item.model=:model");
					q.setParameter("installer", userBean.getSessionInstaller());
					q.setParameter("model", model);
					
					Object retObj = gDAO.search(q, 1);
					if(retObj != null)
					{
						Long lng = (Long)retObj;
						if(lng != null)
						{
							total = lng;
						}
					}
					rec[1] = ""+total;
					assetStockByModel.add(rec);
				}
			}
		}
		gDAO.destroy();
		return assetStockByModel;
	}

	public void setAssetStockByModel(List<String[]> assetStockByModel) {
		this.assetStockByModel = assetStockByModel;
	}

	public AppConfiguration getAppConfig() {
		if(appConfig == null)
		{
			appConfig = getCurrentAppConfig();
			if(appConfig == null)
				appConfig = new AppConfiguration();
		}
		return appConfig;
	}

	public void setAppConfig(AppConfiguration appConfig) {
		this.appConfig = appConfig;
	}

	public AppResource getAppResource() {
		if(appResource == null)
			appResource = new AppResource();
		return appResource;
	}

	public void setAppResource(AppResource appResource) {
		this.appResource = appResource;
	}

	public AppResource getSelAppResource() {
		return selAppResource;
	}

	public void setSelAppResource(AppResource selAppResource) {
		this.selAppResource = selAppResource;
	}

	public Part getAppResourceFile() {
		return appResourceFile;
	}

	public void setAppResourceFile(Part appResourceFile) {
		this.appResourceFile = appResourceFile;
	}

	@SuppressWarnings("unchecked")
	public List<AppResource> getAppResources() {
		if(appResources == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			
			Object all = gDAO.findAll("AppResource");
			if(all != null)
				appResources = (List<AppResource>)all;
			
			gDAO.destroy();
		}
		return appResources;
	}

	public void setAppResources(List<AppResource> appResources) {
		this.appResources = appResources;
	}

	public String getJobCode() {
		return jobCode;
	}

	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}

	public InstallerLocationJobSchedule getJobRefund() {
		return jobRefund;
	}

	public void setJobRefund(InstallerLocationJobSchedule jobRefund) {
		this.jobRefund = jobRefund;
	}

	public List<InstallerLocationJobSchedule> getTotalSubscriptionReport() {
		return totalSubscriptionReport;
	}

	public void setTotalSubscriptionReport(
			List<InstallerLocationJobSchedule> totalSubscriptionReport) {
		this.totalSubscriptionReport = totalSubscriptionReport;
	}

	public List<CustomerProduct> getTotalRenewalReport() {
		return totalRenewalReport;
	}

	public void setTotalRenewalReport(List<CustomerProduct> totalRenewalReport) {
		this.totalRenewalReport = totalRenewalReport;
	}

	@SuppressWarnings("unchecked")
	public List<CustomerProduct> getUpcomingRenewalDueReport() {
		Date st = null, et = null;
		Calendar c = Calendar.getInstance();
		st = c.getTime();
		c.add(Calendar.MONTH, 2);
		et = c.getTime();
		
		GeneralDAO gDAO = new GeneralDAO();
		Query q = gDAO.createQuery("Select e from CustomerProduct e where e.customer.partner=:partner and (e.renewal_due_dt between :st and :et) order by e.renewal_due_dt desc");
		q.setParameter("partner", getPartner());
		q.setParameter("st", st);
		q.setParameter("et", et);
		Object list = gDAO.search(q, 0);
		if(list != null)
		{
			setUpcomingRenewalDueReport((List<CustomerProduct>)list);
		}
		gDAO.destroy();
		return upcomingRenewalDueReport;
	}

	public void setUpcomingRenewalDueReport(
			List<CustomerProduct> upcomingRenewalDueReport) {
		this.upcomingRenewalDueReport = upcomingRenewalDueReport;
	}

	@SuppressWarnings("unchecked")
	public List<CustomerProduct> getOverdueRenewalReport() {
		Date st = null, et = null;
		Calendar c = Calendar.getInstance();
		et = c.getTime();
		c.add(Calendar.MONTH, -2);
		st = c.getTime();
		
		GeneralDAO gDAO = new GeneralDAO();
		Query q = gDAO.createQuery("Select e from CustomerProduct e where e.customer.partner=:partner and (e.renewal_due_dt between :st and :et) order by e.renewal_due_dt desc");
		q.setParameter("partner", getPartner());
		q.setParameter("st", st);
		q.setParameter("et", et);
		Object list = gDAO.search(q, 0);
		if(list != null)
		{
			setOverdueRenewalReport((List<CustomerProduct>)list);
		}
		gDAO.destroy();
		return overdueRenewalReport;
	}

	public void setOverdueRenewalReport(List<CustomerProduct> overdueRenewalReport) {
		this.overdueRenewalReport = overdueRenewalReport;
	}

	public List<String[]> getSalesAgentSummaryReport() {
		return salesAgentSummaryReport;
	}

	public void setSalesAgentSummaryReport(List<String[]> salesAgentSummaryReport) {
		this.salesAgentSummaryReport = salesAgentSummaryReport;
	}

	public List<String[]> getInstallationPointSummaryReport() {
		return installationPointSummaryReport;
	}

	public void setInstallationPointSummaryReport(
			List<String[]> installationPointSummaryReport) {
		this.installationPointSummaryReport = installationPointSummaryReport;
	}

	@SuppressWarnings("unchecked")
	public List<String[]> getTotalSalesReport() {
		GeneralDAO gDAO = new GeneralDAO();
		Object productsObj = gDAO.findAll("Product");
		if(productsObj != null)
		{
			totalSalesReport = new ArrayList<String[]>();
			List<Product> products = (List<Product>)productsObj;
			for(Product p : products)
			{
				String[] e = new String[3];
				e[0] = p.getProductName();
				e[1] = "0";
				e[2] = "0";
				Query q = gDAO.createQuery("Select COUNT(e.id), SUM(e.amount) from CustomerTransaction e where e.customer.partner=:partner and e.product=:product and e.status=:status");
				q.setParameter("partner", userBean.getSessionPartner());
				q.setParameter("product", p);
				q.setParameter("status", "PAID");
				
				Object[] obj = (Object[])gDAO.search(q, 1);
				if(obj != null && obj.length > 0)
				{
					e[1] = (obj[0] != null) ? obj[0].toString() : "0";
					e[2] = (obj[1] != null) ? obj[1].toString() : "0";
				}
				totalSalesReport.add(e);
			}
		}
		gDAO.destroy();
		return totalSalesReport;
	}

	public void setTotalSalesReport(List<String[]> totalSalesReport) {
		this.totalSalesReport = totalSalesReport;
	}

	public List<String[]> getSalesAgentCommissionReport() {
		return salesAgentCommissionReport;
	}

	public void setSalesAgentCommissionReport(
			List<String[]> salesAgentCommissionReport) {
		this.salesAgentCommissionReport = salesAgentCommissionReport;
	}

	public List<String[]> getInstallationPointCommissionReport() {
		return installationPointCommissionReport;
	}

	public void setInstallationPointCommissionReport(
			List<String[]> installationPointCommissionReport) {
		this.installationPointCommissionReport = installationPointCommissionReport;
	}

	public String getMoveType() {
		return moveType;
	}

	public void setMoveType(String moveType) {
		this.moveType = moveType;
	}

	public List<ItemMove> getDeviceMoveReport() {
		return deviceMoveReport;
	}

	public void setDeviceMoveReport(List<ItemMove> deviceMoveReport) {
		this.deviceMoveReport = deviceMoveReport;
	}

	public Part getCustomers_excel() {
		return customers_excel;
	}

	public void setCustomers_excel(Part customers_excel) {
		this.customers_excel = customers_excel;
	}

	public ReworkInstallation getReworkInstallation() {
		if(reworkInstallation == null)
			reworkInstallation = new ReworkInstallation();
		return reworkInstallation;
	}

	public void setReworkInstallation(ReworkInstallation reworkInstallation) {
		this.reworkInstallation = reworkInstallation;
	}

	public InstallationReworkDeviceUse getReworkDeviceUse() {
		if(reworkDeviceUse == null)
			reworkDeviceUse = new InstallationReworkDeviceUse();
		return reworkDeviceUse;
	}

	public void setReworkDeviceUse(InstallationReworkDeviceUse reworkDeviceUse) {
		this.reworkDeviceUse = reworkDeviceUse;
	}

	@SuppressWarnings("unchecked")
	public List<CustomerTransaction> getPendingBankDeposits() {
		GeneralDAO gDAO = new GeneralDAO();
		Query q = gDAO.createQuery("Select e from CustomerTransaction e where e.status=:status order by e.tranInitDate desc");
		q.setParameter("status", "PENDING");
		Object obj = gDAO.search(q, 0);
		if(obj != null)
			pendingBankDeposits = (List<CustomerTransaction>)obj;
		gDAO.destroy();
		
		return pendingBankDeposits;
	}

	public void setPendingBankDeposits(List<CustomerTransaction> pendingBankDeposits) {
		this.pendingBankDeposits = pendingBankDeposits;
	}

	public CustomerTransaction getSelBankDeposit() {
		return selBankDeposit;
	}

	public void setSelBankDeposit(CustomerTransaction selBankDeposit) {
		this.selBankDeposit = selBankDeposit;
	}

	@SuppressWarnings("unchecked")
	public List<CustomerProduct> getUninstalledPurchases() {
		GeneralDAO gDAO = new GeneralDAO();
		Query q = gDAO.createQuery("Select e from CustomerProduct e where e.status=:status and e.customer.partner=:partner order by e.productBooked desc");
		q.setParameter("partner", getPartner());
		q.setParameter("status", "NOT-INSTALLED");
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

	public InstallerLocationJobSchedule getSelCheckedInJob() {
		return selCheckedInJob;
	}

	public void setSelCheckedInJob(InstallerLocationJobSchedule selCheckedInJob) {
		this.selCheckedInJob = selCheckedInJob;
	}

	public long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(long customer_id) {
		this.customer_id = customer_id;
	}

	public long getInstaller_lga_id() {
		return installer_lga_id;
	}

	public void setInstaller_lga_id(long installer_lga_id) {
		this.installer_lga_id = installer_lga_id;
	}

	public long getInstaller_state_id() {
		return installer_state_id;
	}

	public void setInstaller_state_id(long installer_state_id) {
		this.installer_state_id = installer_state_id;
	}

	public long getPartner_id() {
		//partner_id = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("partner_id"));
		return partner_id;
	}

	public void setPartner_id(long partner_id) {
		this.partner_id = partner_id;
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

	public boolean isAgreed() {
		return agreed;
	}

	public void setAgreed(boolean agreed) {
		this.agreed = agreed;
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
	
	private String generateReworkReference()
	{
        return "PRTN-RWK" + (userBean.getSessionUser().getPartner() != null ? userBean.getSessionUser().getPartner().getId().longValue() : "") + "-" + System.currentTimeMillis() + (new SecureRandom().nextInt(999) + 1);
    }
	
	private String generateReference()
	{
        return "PRTN" + (userBean.getSessionUser().getPartner() != null ? userBean.getSessionUser().getPartner().getId().longValue() : "") + "-" + System.currentTimeMillis() + (new SecureRandom().nextInt(999) + 1);
    }
	
	private String generateAgentReference(long agentID)
	{
		return "AGT-" + (userBean.getSessionUser().getPartner() != null ? userBean.getSessionUser().getPartner().getId().longValue() : "") + "-" + agentID;
	}
	
	private String generateTPReference(long tpID)
	{
		return "TP-" + (userBean.getSessionPartner() != null ? userBean.getSessionPartner().getId().longValue() : "") + "-" + tpID;
	}
	
	private String generateCustomerReference(long cusID)
	{
		return "CUS-" + (userBean.getSessionUser().getPartner() != null ? userBean.getSessionUser().getPartner().getId().longValue() : "") + "-" + cusID;
	}
	
	public String getRandomNumber()
	{
		return ""+new Random().nextInt(10000);
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
}
