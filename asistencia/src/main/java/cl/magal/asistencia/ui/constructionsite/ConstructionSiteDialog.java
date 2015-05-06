package cl.magal.asistencia.ui.constructionsite;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.magal.asistencia.entities.ConstructionCompany;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.entities.enums.Status;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.ui.AbstractWindowEditor;
import cl.magal.asistencia.util.SecurityHelper;
import cl.magal.asistencia.util.Utils;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ConstructionSiteDialog extends AbstractWindowEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3983737958814451979L;

	transient Logger logger = LoggerFactory.getLogger(ConstructionSiteDialog.class);

	BeanItemContainer<User> userContainer;
	BeanItemContainer<ConstructionCompany> constructioncompanyContainer;
	ConstructionSite constructionSite;
	User user;

	UserService userService;
	ConstructionSiteService service;

	public ConstructionSiteDialog(BeanItem<ConstructionSite> item, BeanItemContainer<User> user, BeanItemContainer<ConstructionCompany> constructionCompany, ConstructionSiteService service ){
		super(item);
		if(service == null )
			throw new RuntimeException("Error al crear el dialgo, el servicio de obras no puede ser nulo.");

		userContainer = user;
		this.service = service;
		constructioncompanyContainer = constructionCompany;
		setWidth("70%");

		init();
	}

	public void init(){
		super.init();
	}

	@Override
	protected Component createBody() {

		Panel panel = new Panel();
		panel.setSizeFull();
		panel.setContent(drawObra());

		return panel;
	}
	Table tableSteps;

	protected HorizontalLayout drawObra() {

		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setMargin(true);
		hl.setSpacing(true);

		//datos basicos de obra
		FormLayout fl = new FormLayout();
		fl.setEnabled(SecurityHelper.hasPermission(Permission.EDITAR_OBRA));
		fl.setWidth("100%");
		hl.addComponent(fl);
		hl.setComponentAlignment(fl, Alignment.MIDDLE_CENTER);
		fl.setSpacing(true);

		for (Object propertyId : new String[]{"name","costCenter" ,"code", "address","status"}) {
			if(propertyId.equals("constructionsiteId") || propertyId.equals("deleted"))
				;
			else if(propertyId.equals("status")){
				ComboBox statusField = new ComboBox("Estado");
				statusField.setNullSelectionAllowed(false);
				for(Status s : Status.values()){
					statusField.addItem(s);
				}
				fl.addComponent(statusField);
				bind(statusField, "status");    
				fl.setComponentAlignment(statusField, Alignment.MIDDLE_LEFT);
			}else{        		
				String t = tradProperty(propertyId);
				Field field = buildAndBind(t+" : ", propertyId);
				field.setWidth("100%");
				if(field instanceof TextField){
					((TextField)field).setNullRepresentation("");
				}
				fl.addComponent(field);
				fl.setComponentAlignment(field, Alignment.MIDDLE_LEFT);
			}
		}

		final ComboBox nombre = new ComboBox("Responsable", userContainer);
		nombre.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		nombre.setItemCaptionPropertyId("fullname");
		fl.addComponent(nombre);
		bind(nombre, "personInCharge"); 

		final ComboBox constructora = new ComboBox("Constructora", constructioncompanyContainer);
		constructora.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		constructora.setItemCaptionPropertyId("name");
		fl.addComponent(constructora);
		bind(constructora, "constructionCompany"); 

		// lista de etapas de obra
		VerticalLayout vl = new VerticalLayout();
		hl.addComponent(vl);
		vl.setSpacing(true);

		List<String> steps = (List<String>) getItem().getItemProperty("steps").getValue();
		//agrega tabla con las etapas actuales
		tableSteps = new Table();
		tableSteps.setPageLength(6);
		tableSteps.setWidth("100%");
		tableSteps.addContainerProperty("Etapa", String.class, "");
		tableSteps.addContainerProperty("Eliminar", Button.class, null);
		
		int i = 0;
		for(String step : steps){
			Item item = tableSteps.addItem(i++);
			item.getItemProperty("Etapa").setValue(step);
		}

		tableSteps.addGeneratedColumn("Eliminar", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				return new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						tableSteps.removeItem(itemId);
					}
				}){ {setIcon(FontAwesome.TRASH_O);} };
			}
		});

		tableSteps.setColumnWidth("Eliminar", 100);

		//boton para agregar etapas
		Button btn = new Button(null,FontAwesome.PLUS_CIRCLE);
		btn.setVisible(SecurityHelper.hasPermission(Permission.AGREGAR_ETAPAS_OBRA));
		btn.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				tableSteps.addItem();
			}
		});
		vl.addComponent(btn);
		vl.setComponentAlignment(btn, Alignment.MIDDLE_RIGHT);
		vl.addComponent(tableSteps);
		vl.setExpandRatio(tableSteps, 1.0F);

		tableSteps.setEnabled(SecurityHelper.hasPermission(Permission.AGREGAR_ETAPAS_OBRA));
		tableSteps.setEditable(true);
		
		return hl;
	}

	private String tradProperty(Object propertyId) {
		if(propertyId.equals("costCenter"))
			return "Centro de Costo";
		else if(propertyId.equals("code"))
			return "Nombre en Código";
		else if(propertyId.equals("name"))
			return "Nombre";
		else if(propertyId.equals("address"))
			return "Dirección";
		else
			return propertyId.toString();
	}

	//despues de la validación agrega las etapas
	@Override
	protected boolean preCommit() {
		//antes de guardar recupera la información de los fields
		List<String> steps = (List<String>) getItem().getItemProperty("steps").getValue();
		steps.clear();
		for(Object itemId : tableSteps.getItemIds()){
			String etapa = (String) tableSteps.getItem(itemId).getItemProperty("Etapa").getValue();
			if(!Utils.NotNullOrEmpty(etapa)){
				Notification.show("No se permiten etapas vacias",Type.ERROR_MESSAGE);
				return false;
			}
			steps.add(etapa);
		}
		return true;
	}


}
