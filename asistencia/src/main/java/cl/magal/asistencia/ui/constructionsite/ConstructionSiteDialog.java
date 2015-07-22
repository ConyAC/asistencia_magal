package cl.magal.asistencia.ui.constructionsite;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.magal.asistencia.entities.ConstructionCompany;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Speciality;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.Job;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.entities.enums.Status;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.ui.AbstractWindowEditor;
import cl.magal.asistencia.util.Constants;
import cl.magal.asistencia.util.SecurityHelper;
import cl.magal.asistencia.util.Utils;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
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
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
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
	BeanItemContainer<Speciality> specialitiesContainer = new BeanItemContainer<Speciality>(Speciality.class);
	User user;

	UserService userService;
	ConstructionSiteService service;
	
	Table tableSteps;

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

		for (Object propertyId : new String[]{"name","costCenter" ,"code", "address","commune","status"}) {
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
			}else if(propertyId.equals("commune")){
				ComboBox statusField = new ComboBox("Comuna");
				statusField.setNullSelectionAllowed(false);
				for(String ms : Constants.COMUNAS){
					statusField.addItem(ms);
				}
				fl.addComponent(statusField);
				bind(statusField, "commune");    
				fl.setComponentAlignment(statusField, Alignment.MIDDLE_LEFT);
			}else {        		
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
		VerticalLayout vl1 = drawStepTable();
		VerticalLayout vl2 = drawSpecialityTable();
		
		TabSheet tb = new TabSheet();
		tb.addTab(vl1,"Etapas");
		tb.addTab(vl2,"Especialidades");
		
		hl.addComponent(tb);
		return hl;
	}
	
	/**
	 * Tabla de especialidades de la obra
	 * @return
	 */
	private VerticalLayout drawSpecialityTable(){
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		vl.setSpacing(true);

		specialitiesContainer.addAll(service.findSpecialitiesByConstructionSite((ConstructionSite) getItem().getBean()));
		//agrega tabla con las etapas actuales
		final Table tableSpecialities = new Table(null,specialitiesContainer);
		tableSpecialities.setEditable(true);
//		tableSpecialities.setImmediate(true);
		tableSpecialities.setPageLength(6);
		tableSpecialities.setWidth("100%");
		tableSpecialities.setTableFieldFactory(new TableFieldFactory() {
			
			@Override
			public Field<?> createField(Container container, Object itemId,
					Object propertyId, Component uiContext) {
				if(propertyId.equals("name"))
					return new TextField(){{setImmediate(true);}};
				return null;
			}
		});
		
		tableSpecialities.addGeneratedColumn("delete", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				return new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						//confirma que no se esté usando
						Speciality speciality = specialitiesContainer.getItem(itemId).getBean();;
						if(service.checkSpecialityInUse((ConstructionSite) getItem().getBean(),speciality)){
							Notification.show("No se puede borrar la especialidad dado que está asignada a trabajadores de la obra");
							return;
						}
						service.removeSpeciality(speciality);
						specialitiesContainer.removeItem(itemId);
					}
				}){ {setIcon(FontAwesome.TRASH_O);} };
			}
		});

		tableSpecialities.setColumnWidth("delete", 100);
		tableSpecialities.setColumnHeader("delete", "");
		tableSpecialities.setColumnHeader("name", "Nombre Especialidad");
		
		tableSpecialities.setVisibleColumns("name","delete");

		//comboBox para filtrar por oficio
		final ComboBox comboBox = new ComboBox("Oficios");
		comboBox.setWidth("100%");
		comboBox.setNullSelectionAllowed(false);
		for(Job job : Job.values()){
			comboBox.addItem(job);
		}
		//cada vez que se modifique el valor, se debe filtrar el contenedor
		comboBox.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				specialitiesContainer.removeAllContainerFilters();
				Filter filter = new Compare.Equal("job", event.getProperty().getValue());
				specialitiesContainer.addContainerFilter(filter);
			}
		});
		comboBox.select(Job.values()[0]);
		
		//boton para agregar etapas
		Button btn = new Button(null,FontAwesome.PLUS_CIRCLE);
		btn.setVisible(SecurityHelper.hasPermission(Permission.AGREGAR_ETAPAS_OBRA));
		btn.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				
				Job job = (Job) comboBox.getValue();
				
				Speciality speciality = new Speciality();
				String defaultName = "Especialidad"; 
				speciality.setName(defaultName + " 1");
				speciality.setConstructionSite((ConstructionSite) getItem().getBean());
				speciality.setJob(job);
				//verifica si ya existe uno así en el contenedor
				int i = 2;
				while(Utils.containsContainer(specialitiesContainer,"name",speciality.getName())){
//					Notification.show("Ya existe una especialidad con el mismo nombre para el oficio seleccionado.",Type.WARNING_MESSAGE);
//					return;
					speciality.setName(defaultName+" "+(i++));
				}
				service.save(speciality);
				specialitiesContainer.addBean(speciality);
			}
		});
		
		HorizontalLayout hl = new HorizontalLayout();
		FormLayout fm = new FormLayout(comboBox) ;
		fm.setMargin(false);
		hl.setWidth("100%");
		hl.addComponent(fm);
		hl.setComponentAlignment(fm, Alignment.MIDDLE_LEFT);	
		hl.addComponent(btn);
		hl.setComponentAlignment(btn, Alignment.BOTTOM_RIGHT);	
		
		vl.addComponent(hl);
		
		vl.addComponent(tableSpecialities);
		vl.setExpandRatio(tableSpecialities, 1.0F);

		tableSpecialities.setEnabled(SecurityHelper.hasPermission(Permission.AGREGAR_ETAPAS_OBRA));
		tableSpecialities.setEditable(true);
		return vl;
	}
	
	/**
	 * Tabla de etapas de la construcción
	 * @return
	 */
	private VerticalLayout drawStepTable(){
		VerticalLayout vl = new VerticalLayout();vl.setMargin(true);
		vl.setSpacing(true);

		List<String> steps = (List<String>) getItem().getItemProperty("steps").getValue();
		//agrega tabla con las etapas actuales
		tableSteps = new Table();
		tableSteps.setPageLength(6);
		tableSteps.setWidth("100%");
		tableSteps.addContainerProperty("Etapa", String.class, "");
		tableSteps.addContainerProperty("delete", Button.class, null);
		
		int i = 0;
		for(String step : steps){
			Item item = tableSteps.addItem(i++);
			item.getItemProperty("Etapa").setValue(step);
		}

		tableSteps.addGeneratedColumn("delete", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				return new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						//confirma que no se esté usando
						String step = (String) tableSteps.getItem(itemId).getItemProperty("Etapa").getValue();
						if(service.checkStepInUse((ConstructionSite) getItem().getBean(),step)){
							Notification.show("No se puede borrar la etapa dado que está asignada a trabajadores de la obra");
							return;
						}
						tableSteps.removeItem(itemId);
					}
				}){ {setIcon(FontAwesome.TRASH_O);} };
			}
		});

		tableSteps.setColumnWidth("delete", 100);
		tableSteps.setColumnHeader("delete", "");

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
		return vl;
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
		List<String> steps = new LinkedList<String>();
		for(Object itemId : tableSteps.getItemIds()){
			String etapa = (String) tableSteps.getItem(itemId).getItemProperty("Etapa").getValue();
			if(!Utils.NotNullOrEmpty(etapa)){
				Notification.show("No se permiten etapas vacias",Type.ERROR_MESSAGE);
				return false;
			}
			steps.add(etapa);
		}
		//quita los filtros para recorrerlos todos
		List<Filter> filters = new ArrayList<Filter>(specialitiesContainer.getContainerFilters());
		specialitiesContainer.removeAllContainerFilters();
		for(Object itemId : specialitiesContainer.getItemIds()){
			Item item = specialitiesContainer.getItem(itemId);
			String etapa = (String)item.getItemProperty("name").getValue();
			Job job = (Job)item.getItemProperty("job").getValue();
			//verfica que no sea nulo
			if(!Utils.NotNullOrEmpty(etapa)){
				Notification.show("No se permiten especialidades vacias",Type.ERROR_MESSAGE);
				//recupera los filtros
				for(Filter filter : filters){
					specialitiesContainer.addContainerFilter(filter);
				}
				return false;
			}
			//verifica que ya no exista el mismo nombre
			if(Utils.containsTwoContainer(specialitiesContainer,new String[]{"name","job"},new Object[]{ etapa , job})){
				Notification.show("No se permiten especialidades con nombres repetidos (\""+etapa+"\")",Type.ERROR_MESSAGE);
				//recupera los filtros
				for(Filter filter : filters){
					specialitiesContainer.addContainerFilter(filter);
				}
				return false;
			}
		}
		service.save(specialitiesContainer.getItemIds());
		//si todo va bien, setea la nueva lista
		getItem().getItemProperty("steps").setValue(steps);
		return true;
	}


}
