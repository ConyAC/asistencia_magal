package cl.magal.asistencia.ui.constructionsite;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.Job;
import cl.magal.asistencia.entities.enums.Status;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.ui.AbstractWindowEditor;
import cl.magal.asistencia.ui.MagalUI;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;

public class ConstructionSiteDialog extends AbstractWindowEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3983737958814451979L;
	
	transient Logger logger = LoggerFactory.getLogger(ConstructionSiteDialog.class);

	BeanItemContainer<User> userContainer;
	ConstructionSite constructionSite;
	User user;
	
	UserService userService;
	ConstructionSiteService service;
	private VelocityEngine velocityEngine;

	public ConstructionSiteDialog(BeanItem<ConstructionSite> item, BeanItemContainer<User> user, ConstructionSiteService service , VelocityEngine velocityEngine){
		super(item);
		if(service == null )
			throw new RuntimeException("Error al crear el dialgo, el servicio de obras no puede ser nulo.");

		userContainer = user;
		this.velocityEngine = velocityEngine;
		this.service = service;
		setWidth("70%");

		init();
	}

	public void init(){
		super.init();
	}

	@Override
	protected Component createBody() {

		TabSheet tab = new TabSheet();
		tab.addTab(drawObra(),"Crear Obra");

		return tab;
	}
	
	protected VerticalLayout drawObra() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();
			
		for (Object propertyId : new String[]{"name", "code", "address","status"}) {
        	if(propertyId.equals("constructionsiteId") || propertyId.equals("deleted"))
        		;
        	else if(propertyId.equals("status")){
			ComboBox statusField = new ComboBox("Estado");
			statusField.setNullSelectionAllowed(false);
			for(Status s : Status.values()){
				statusField.addItem(s);
			}
			vl.addComponent(statusField);
			bind(statusField, "status");    
			vl.setComponentAlignment(statusField, Alignment.MIDDLE_LEFT);
        	}else{        		
        		String t = tradProperty(propertyId);
        		Field field = buildAndBind(t, propertyId);
        		if(field instanceof TextField){
        			((TextField)field).setNullRepresentation("");
        		}
        		vl.addComponent(field);
        		vl.setComponentAlignment(field, Alignment.MIDDLE_LEFT);
        	}
        }
		 
		HorizontalLayout resp = new HorizontalLayout();				
		final ComboBox nombre = new ComboBox("Responsable", userContainer);
		nombre.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		nombre.setItemCaptionPropertyId("fullname");
		resp.addComponent(nombre);
		vl.addComponent(resp);
		bind(nombre, "personInCharge"); 
		
		return vl;
	}

	private String tradProperty(Object propertyId) {
		if(propertyId.equals("code"))
			return "Código";
		else if(propertyId.equals("name"))
			return "Nombre";
		else if(propertyId.equals("address"))
			return "Dirección";
		else
			return propertyId.toString();
	}
	
}
