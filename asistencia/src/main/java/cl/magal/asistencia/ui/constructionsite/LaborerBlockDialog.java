package cl.magal.asistencia.ui.constructionsite;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.Status;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.ui.AbstractWindowEditor;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;

public class LaborerBlockDialog extends AbstractWindowEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3983737958814451979L;
	
	transient Logger logger = LoggerFactory.getLogger(LaborerBlockDialog.class);

	BeanItemContainer<User> userContainer;
	ConstructionSite constructionSite;
	User user;
	
	UserService userService;
	ConstructionSiteService service;
	private VelocityEngine velocityEngine;

	public LaborerBlockDialog(BeanItem<LaborerConstructionsite> item,  User user, VelocityEngine velocityEngine){
		super(item);

		this.velocityEngine = velocityEngine;
		this.service = service;
		setWidth("70%");
		user = user;
		
		init();
	}

	public void init(){
		super.init();
	}

	@Override
	protected Component createBody() {

		Panel panel = new Panel();
		panel.setContent(drawBlock());

		return panel;
	}
	
	protected VerticalLayout drawBlock() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();
		
		vl.addComponent(new Label("Responsable"));vl.addComponent(new Label(getItem().getItemProperty("user.fullname")));
		
		for (Object propertyId : new String[]{"comment"}) {
        	if(propertyId.equals("constructionsiteId") || propertyId.equals("deleted"))
        		;        
        	else if(  propertyId.equals("comment")){
				TextArea ta = new TextArea("Comentario");
				ta.setWidth("400");
				ta.setHeight("100");
				vl.addComponent(ta);
				vl.setComponentAlignment(ta, Alignment.MIDDLE_LEFT);
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
		 
		return vl;
	}

	private String tradProperty(Object propertyId) {
		if(propertyId.equals("comment"))
			return "Comentario";
		else
			return propertyId.toString();
	}
	
}
