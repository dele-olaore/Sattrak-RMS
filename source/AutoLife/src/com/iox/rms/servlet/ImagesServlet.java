package com.iox.rms.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iox.rms.dao.GeneralDAO;
import com.iox.rms.model.Agent;
import com.iox.rms.model.InstallerLocation;
import com.iox.rms.model.Product;

/**
 * Servlet implementation class ImagesServlet
 */
@WebServlet(description = "Servlet to render images", urlPatterns = { "/imageservlet/*" })
public class ImagesServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    
    //private static final String PERSISTENCE_UNIT_NAME = "AutoLifePU";
    
    final Logger logger = Logger.getLogger("AutoLife-ImagesServlet");
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImagesServlet()
    {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @SuppressWarnings("deprecation")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	GeneralDAO gDAO = new GeneralDAO();
    	
        try
        {
                String details = String.valueOf(request.getPathInfo().substring(1)); // Gets string that goes after "/imageservlet/".

                String r_id = "", photo="";
                if(details.indexOf(":")>=0)
                {
                        r_id = details.split(":")[0];
                        photo = details.split(":")[1];
                }
                else if(details.indexOf("-")>=0)
                {
                        r_id = details.split("-")[0];
                        photo = details.split("-")[1];
                }

                Long id = 0L;
                try
                {
                        try
                        {
                                id = Long.parseLong(r_id);
                        }
                        catch(Exception ig)
                        {}

                        byte[] data = null;

                        if(photo.equalsIgnoreCase("installer"))
                        {
                        	try
        					{
        						Object obj = gDAO.find(InstallerLocation.class, id);
        						gDAO.destroy();
        						if(obj != null)
        						{
        							InstallerLocation e = (InstallerLocation)obj;
                                if(e != null && e.getPhoto() != null)
                                {
                                        data = e.getPhoto();
                                }
        						}
        					} catch(Exception ex){}
                        }
                        else if(photo.equalsIgnoreCase("agent"))
                        {
                        	try
        					{
        						Object obj = gDAO.find(Agent.class, id);
        						gDAO.destroy();
        						if(obj != null)
        						{
        							Agent e = (Agent)obj;
                                if(e != null && e.getPhoto() != null)
                                        data = e.getPhoto();
        						}
        					} catch(Exception ex){}
                        }
                        else if(photo.equalsIgnoreCase("product"))
                        {
                        	try
        					{
        						Object obj = gDAO.find(Product.class, id);
        						gDAO.destroy();
        						if(obj != null)
        						{
        							Product e = (Product)obj;
                                if(e != null && e.getPhoto() != null)
                                        data = e.getPhoto();
        						}
        					} catch(Exception ex){}
                        }

                        if(data != null)
                        {
                                response.setHeader("Content-Type", getServletContext().getMimeType("image/jpeg"));
                        response.setHeader("Content-Disposition", "inline; filename=\"photo\"");

                        BufferedInputStream input = null;
                        BufferedOutputStream output = null;

                        try
                        {
                                input = new BufferedInputStream(new ByteArrayInputStream(data)); // Creates buffered input stream.

                            output = new BufferedOutputStream(response.getOutputStream());
                            byte[] buffer = new byte[8192];
                            for (int length = 0; (length = input.read(buffer)) > 0;) {
                                output.write(buffer, 0, length);
                            }
                        }
                        finally
                        {
                            if (output != null) try { output.close(); } catch (IOException logOrIgnore) {}
                            if (input != null) try { input.close(); } catch (IOException logOrIgnore) {}
                        }
                        }
                        else
                        {
                                File defaultIcon = null;
                                if(photo.equalsIgnoreCase("partner"))
                                {
                                        defaultIcon = new File(request.getRealPath("/resources/images/satraklogo.jpg"));
                                        response.setHeader("Content-Type", getServletContext().getMimeType("image/jpg"));
                                response.setHeader("Content-Disposition", "inline; filename=\"logo\"");
                                }
                                else if(photo.equalsIgnoreCase("product") || photo.equalsIgnoreCase("installer") || photo.equalsIgnoreCase("agent"))
                                {
                                        defaultIcon = new File(request.getRealPath("/images/media1.jpg"));
                                        response.setHeader("Content-Type", getServletContext().getMimeType("image/jpg"));
                                response.setHeader("Content-Disposition", "inline; filename=\"product-img\"");
                                }
                                else if(photo.equalsIgnoreCase("advert"))
                                {
                                        // TODO: Maybe we should put a default advert here
                                }
                                else
                                {
                                        defaultIcon = new File(request.getRealPath("/images/media1.jpg"));
                                        response.setHeader("Content-Type", getServletContext().getMimeType("image/jpg"));
                                response.setHeader("Content-Disposition", "inline; filename=\"photo\"");
                                }

                        BufferedInputStream input = null;
                        BufferedOutputStream output = null;

                        try
                        {
                                input = new BufferedInputStream(new FileInputStream(defaultIcon)); // Creates buffered input stream.

                            output = new BufferedOutputStream(response.getOutputStream());
                            byte[] buffer = new byte[8192];
                            for (int length = 0; (length = input.read(buffer)) > 0;) {
                                output.write(buffer, 0, length);
                            }
                        }
                        finally
                        {
                            if (output != null) try { output.close(); } catch (IOException logOrIgnore) {}
                            if (input != null) try { input.close(); } catch (IOException logOrIgnore) {}
                        }
                        }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
        }
        catch(Exception e){}
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
