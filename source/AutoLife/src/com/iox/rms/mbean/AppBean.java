/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iox.rms.mbean;

import com.iox.rms.dao.GeneralDAO;
import com.iox.rms.model.Product;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import javax.persistence.Query;

/**
 *
 * @author oladele
 */
@ManagedBean(name = "appBean", eager = true)
@ApplicationScoped
public class AppBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private List<Product> product1List, product2List, product3List, product5List, product7List;
    
    @SuppressWarnings("unchecked")
	public List<Product> getProduct1List()
    {
        if(product1List == null || (product1List != null && product1List.isEmpty()))
        {
        GeneralDAO gDAO = new GeneralDAO();
        
        ArrayList<Long> idList = new ArrayList<Long>();
        idList.add(61L);
        idList.add(160L);
        idList.add(405L);
        idList.add(201L);
        
        Query q = gDAO.createQuery("Select e from Product e where e.active=:active and e.type.id IN :idList order by e.firstYearPayment");
        q.setParameter("active", true);
        q.setParameter("idList", idList);
        
        Object list = gDAO.search(q, 0);
        if(list != null)
            product1List = (List<Product>)list;
        gDAO.destroy();
        }
        return product1List;
    }

    public List<Product> getProduct2List() {
        return product2List;
    }

    public List<Product> getProduct3List() {
        return product3List;
    }

    public List<Product> getProduct5List() {
        return product5List;
    }
    
    @SuppressWarnings("unchecked")
	public List<Product> getProduct7List()
    {
        if(product7List == null || (product7List != null && product7List.isEmpty()))
        {
        GeneralDAO gDAO = new GeneralDAO();
        
        ArrayList<Long> idList = new ArrayList<Long>();
        idList.add(401L);
        
        Query q = gDAO.createQuery("Select e from Product e where e.active=:active and e.type.id IN :idList order by e.firstYearPayment");
        q.setParameter("active", true);
        q.setParameter("idList", idList);
        
        Object list = gDAO.search(q, 0);
        if(list != null)
            product7List = (List<Product>)list;
        gDAO.destroy();
        }
        return product7List;
    }
}
