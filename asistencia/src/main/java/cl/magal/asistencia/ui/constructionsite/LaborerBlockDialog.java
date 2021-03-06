package cl.magal.asistencia.ui.constructionsite;


import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.ui.AbstractWindowEditor;
import cl.magal.asistencia.util.Constants;

import com.vaadin.data.util.BeanItem;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LaborerBlockDialog extends AbstractWindowEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3983737958814451979L;
	
	transient Logger logger = LoggerFactory.getLogger(LaborerBlockDialog.class);

	LaborerService lab;
	private VelocityEngine velocityEngine;

	public LaborerBlockDialog(BeanItem<?> item, LaborerService lab, VelocityEngine velocityEngine){
		super(item);

		this.velocityEngine = velocityEngine;
		this.lab = lab;
		setWidth("50%");
		setHeight("50%");
		
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
		
		User user = (User) VaadinSession.getCurrent().getAttribute(Constants.SESSION_USUARIO);		
		vl.addComponent(new Label("Responsable: "+user.getFullname()));
		
		for (Object propertyId : new String[]{"comment"}) {
        	if(propertyId.equals("constructionsiteId") || propertyId.equals("deleted"))
        		;        
        	else if(propertyId.equals("comment")){
				TextArea comment = new TextArea("Comentario");
				comment.setNullRepresentation("");
				comment.setWidth("500");
				comment.setHeight("100");
				vl.addComponent(comment);
				bind(comment, "comment"); 
				vl.setComponentAlignment(comment, Alignment.MIDDLE_LEFT);
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
