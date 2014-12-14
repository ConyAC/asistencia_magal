package cl.magal.asistencia.ui.constructionsite;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.FilterTable;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.Team;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.Afp;
import cl.magal.asistencia.entities.enums.Job;
import cl.magal.asistencia.entities.enums.MaritalStatus;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.entities.enums.Status;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.util.SecurityHelper;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Component
@Scope("prototype")
public class LaborerAndTeamPanel extends Panel implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3552532103677168457L;

	Logger logger = LoggerFactory.getLogger(LaborerAndTeamPanel.class);
	
	protected Button editConstructionSite,btnPrint,btnAdd;
	
	/** CONTAINERS **/
	BeanItemContainer<User> userContainer = new BeanItemContainer<User>(User.class);
	BeanItemContainer<Team> teamContainer = new BeanItemContainer<Team>(Team.class);
	BeanItemContainer<Laborer> laborerContainer = new BeanItemContainer<Laborer>(Laborer.class);
	
	BeanItem<ConstructionSite> item;
	
	/** FIELD GROUP **/
	BeanFieldGroup<ConstructionSite> bfg = new BeanFieldGroup<ConstructionSite>(ConstructionSite.class);
	/** LAYOUTS **/
	VerticalLayout detalleLayout;
	HorizontalLayout detailLayout;
	
	/** SERVICES **/
	@Autowired
	transient UserService userService;
	@Autowired
	transient ConstructionSiteService constructionSiteService;
	
	
	Button asistenciaBtn;
	
	boolean hasConstructionDetails;

	public void setHasAttendanceButton(boolean hasAttendanceButton) {
		asistenciaBtn.setVisible(hasAttendanceButton); 
	}


	public boolean isHasConstructionDetails() {
		return hasConstructionDetails;
	}


	public void setHasConstructionDetails(boolean hasConstructionDetails) {
		this.hasConstructionDetails = hasConstructionDetails;
	}


	public LaborerAndTeamPanel() {
		teamContainer.addNestedContainerProperty("leader.firstname");
		//crea el tab con trabajadores y cuadrillas
		TabSheet tab = new TabSheet();
		tab.setSizeFull();
		
		//tab de trabajadores
		tab.addTab(drawLaborer(),"Trabajadores");

		//tab de cuadrillas
		tab.addTab(drawCuadrillas(),"Cuadrillas");
		//rellena el panel de la información de obra
		setContent(tab);
	}
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		Page<User> users = userService.findAllActiveUser(new PageRequest(0, 20));
		userContainer.removeAllItems();
		userContainer.addAll(users.getContent());
	}
	
	@PostConstruct
	public void init(){
		
	}
	
	protected VerticalLayout drawContructionDetail() {
		detalleLayout = new VerticalLayout(); 
		detalleLayout.setSizeFull();
		detalleLayout.setSpacing(true);

		//creando la parte de arriba
		detailLayout = drawTopDetails();
		detalleLayout.addComponent(detailLayout);
		detalleLayout.setExpandRatio(detailLayout, 0.3F);

		//crea el tab con trabajadores y cuadrillas
		TabSheet tab = new TabSheet();
		tab.setSizeFull();

		detalleLayout.addComponent(tab);
		detalleLayout.setExpandRatio(tab, 0.7F);
		//tab de trabajadores
		tab.addTab(drawLaborer(),"Trabajadores");

		//tab de cuadrillas
		tab.addTab(drawCuadrillas(),"Cuadrillas");

		return detalleLayout;
	}
	
	protected HorizontalLayout drawTopDetails() {

		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeFull();
		hl.setSpacing(true);

		hl.addComponent(new HorizontalLayout(){{

			setSpacing(true);
			setSizeFull();

			FormLayout vlInfo =new FormLayout();
			vlInfo.setSizeFull();
			vlInfo.setSpacing(true);

			final TextField nameField = new TextField("Nombre");
			nameField.setNullRepresentation("");
			nameField.setRequired(true);
			nameField.setWidth("100%");
			bfg.bind(nameField, "name");

			vlInfo.addComponent(nameField);
			
			if(SecurityHelper.hastPermission(Permission.EDITAR_OBRA) ){
				//agrega un boton que hace el commit
				editConstructionSite = new Button(null,new Button.ClickListener() {
	
					@Override
					public void buttonClick(ClickEvent event) {
						try {
							bfg.commit();
							constructionSiteService.save(bfg.getItemDataSource().getBean());
						} catch (CommitException e) {
							logger.error("Error al guardar la información la obra",e);
							Notification.show("Error al guardar la información del usuario", Type.ERROR_MESSAGE);
						}
	
					}
				}){{
					setIcon(FontAwesome.SAVE);
				}};
				addComponent(editConstructionSite);
				setComponentAlignment(editConstructionSite, Alignment.TOP_RIGHT);
			}

			TextField addressField = new TextField("Dirección");
			addressField.setWidth("100%");
			addressField.setNullRepresentation("");
			bfg.bind(addressField, "address");
			vlInfo.addComponent(addressField);

			ComboBox statusField = new ComboBox("Estado");
			statusField.setWidth("100%");
			//no permite nulos
			statusField.setNullSelectionAllowed(false);
			for(Status s : Status.values()){
				statusField.addItem(s);
			}

			bfg.bind(statusField, "status");
			vlInfo.addComponent(statusField);

			ComboBox personInChargeField = new ComboBox("Responsable",userContainer);
			personInChargeField.setItemCaptionMode(ItemCaptionMode.PROPERTY);
			personInChargeField.setItemCaptionPropertyId("fullname");
			personInChargeField.setWidth("100%");			
			bfg.bind(personInChargeField, "personInCharge");

			vlInfo.addComponent(personInChargeField);

			addComponent(vlInfo);
			setExpandRatio(vlInfo, 1.0F);
		}});

		//		hl.addComponent(new Image());

		VerticalLayout vlIBotones =new VerticalLayout();
		vlIBotones.setSpacing(true);
		hl.addComponent(vlIBotones);
		hl.setComponentAlignment(vlIBotones, Alignment.MIDDLE_RIGHT );

		final Button asistencia = new Button("Asistencia",FontAwesome.CHECK);
		asistencia.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
//				switchPanels();
				asistencia.setEnabled(true);
			}
		});
		asistencia.setDisableOnClick(true);

		asistencia.setSizeFull();
		vlIBotones.addComponent(asistencia);
//		Button vacaciones = new Button("Carga Masiva Vacaciones",FontAwesome.UPLOAD);
//		vacaciones.setSizeFull();
//		vlIBotones.addComponent(vacaciones);

		Button configuraciones = new Button("Configuraciones Obra",FontAwesome.GEARS);
		configuraciones.setSizeFull();
		vlIBotones.addComponent(configuraciones);
		vlIBotones.setWidth("300px");

		return hl;
	}
	
	protected VerticalLayout drawCuadrillas() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		vl.setSpacing(true);
		vl.setMargin(true);

		Button btnAdd = new Button(null,FontAwesome.PLUS);
		vl.addComponent(btnAdd);
		vl.setComponentAlignment(btnAdd, Alignment.TOP_RIGHT);

		btnAdd.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				ConstructionSite cs = item.getBean();
				if(cs == null){
					Notification.show("Debe seleccionar una obra",Type.ERROR_MESSAGE);
					return;
				}

				Team team = new Team();
				team.setName("Cuadrilla 1");
				team.setDate(new Date());
				team.setStatus(Status.ACTIVE);
				team.setLeader(laborerContainer.firstItemId());
				constructionSiteService.addTeamToConstructionSite(team,cs);

				teamContainer.addBean(team);

			}
		});
		
		FilterTable table =  new FilterTable(){

			@Override
            protected String formatPropertyValue(Object rowId, Object colId,
            		Property<?> property) {
                Object v = property.getValue();
                if (v instanceof Date) {
                    Date dateValue = (Date) v;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                    return sdf.format(dateValue);
                }
                return super.formatPropertyValue(rowId, colId, property);
            }
		};
		
		table.setContainerDataSource(teamContainer);
		table.setSizeFull();
		table.setFilterBarVisible(true);
		table.setVisibleColumns("name","leader.firstname","date","status");
		table.setColumnHeaders("Nombre","Responsable","Fecha","Estado");
		
		table.setSelectable(true);

		vl.addComponent(table);
		vl.setExpandRatio(table,1.0F);

		return vl;
	}
	
	protected VerticalLayout test1() {
		return null;
	}
	
	protected VerticalLayout test2() {
		return null;
	}
	
	protected VerticalLayout drawLaborer() {

		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();

		//boton para agregar trabajadores e imprimir
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setSpacing(true);
		
		asistenciaBtn = new Button("Asistencia",FontAwesome.CHECK);
		asistenciaBtn.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
//				switchPanels();
				asistenciaBtn.setEnabled(true);
			}
		});
		asistenciaBtn.setDisableOnClick(true);

		asistenciaBtn.setWidth("200px");
		hl.addComponent(asistenciaBtn);
		hl.setComponentAlignment(asistenciaBtn, Alignment.TOP_CENTER);
		hl.setExpandRatio(asistenciaBtn, 1.0F);
		
		vl.addComponent(hl);

		btnPrint = new Button(null,FontAwesome.PRINT);
		hl.addComponent(btnPrint);
		hl.setComponentAlignment(btnPrint, Alignment.TOP_RIGHT);

		btnPrint.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show("Imprimiendo");

			}
		});

		btnAdd = new Button(null,FontAwesome.PLUS);
		hl.addComponent(btnAdd);
		hl.setComponentAlignment(btnAdd, Alignment.TOP_RIGHT);

		btnAdd.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				final ConstructionSite cs = item.getBean();
				if(cs == null){
					Notification.show("Debe seleccionar una obra",Type.ERROR_MESSAGE);
					return;
				}
				
				final Window window = new Window();
				window.setWidth("70%");
				
				window.setModal(true);
				window.center();
				
				GridLayout detalleObrero = new GridLayout(2,5);
				detalleObrero.setMargin(true);
				detalleObrero.setSpacing(true);
				
				Panel panel = new Panel("Ficha Trabajador");
				panel.setContent(detalleObrero);
				window.setContent(panel);				
				
				HorizontalLayout hl = new HorizontalLayout();
				TabSheet tab = new TabSheet();
				tab.setSizeFull();

				hl.addComponent(tab);
				
				//tab de trabajadores
				tab.addTab(drawCuadrillas(),"fgfgf");
				//tab de cuadrillas
				tab.addTab(drawCuadrillas(),"sdsdsx");
				
				hl.setSpacing(true);
				detalleObrero.addComponent(hl,0,0,1,0);
				detalleObrero.setComponentAlignment(hl, Alignment.TOP_LEFT);
				
				
				final BeanFieldGroup<Laborer> fieldGroup = new BeanFieldGroup<Laborer>(Laborer.class);
		        fieldGroup.setItemDataSource(new BeanItem<Laborer>(new Laborer()));

		        //agrega un boton que hace el commit
		        Button add = new Button(null,new Button.ClickListener() {

		        	@Override
		        	public void buttonClick(ClickEvent event) {
		        		try {
		        			fieldGroup.commit();
		        			Laborer laborer = fieldGroup.getItemDataSource().getBean();
		        			constructionSiteService.addLaborerToConstructionSite(laborer,cs);				
		        			laborerContainer.addBean(laborer);
		        			window.close();
		        		} catch (Exception e) {
		        			logger.error("Error al guardar la información del obrero");
		        			Notification.show("Es necesario agregar todos los campos obligatorios", Type.ERROR_MESSAGE);
		        		}

		        	}
		        }){{
		        	setIcon(FontAwesome.SAVE);
		        }};
		        hl.addComponent(add);
		        //detalleObrero.addComponent(add);
		        //detalleObrero.setComponentAlignment(add, Alignment.TOP_RIGHT);
		        
				//boton para imprimir
				Button btnPrint = new Button(null,new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						Notification.show("Imprimiendo");
						
					}
				}){{
					setIcon(FontAwesome.PRINT);
				}};
				 hl.addComponent(btnPrint);
				//detalleObrero.addComponent(btnPrint);
				//detalleObrero.setComponentAlignment(btnPrint, Alignment.TOP_LEFT);        
		        // Loop through the properties, build fields for them and add the fields
		        // to this UI
				 for (Object propertyId : new String[]{"rut","firstname","secondname","lastname", "secondlastname", "dateBirth", "address", "mobileNumber", "phone", "dateAdmission"}) {
		        	if(propertyId.equals("laborerId") || propertyId.equals("constructionSites") || propertyId.equals("contractId") || propertyId.equals("teamId"))
		        		;
		        	else if(propertyId.equals("afp")){
		        		ComboBox afpField = new ComboBox("AFP");
		        		afpField.setNullSelectionAllowed(false);
		    			for(Afp a : Afp.values()){
		    				afpField.addItem(a);
		    			}
		    			detalleObrero.addComponent(afpField);
		    			fieldGroup.bind(afpField, "afp");    			
		        	}else if(propertyId.equals("job")){
		        		ComboBox jobField = new ComboBox("Oficio");
		        		jobField.setNullSelectionAllowed(false);
		    			for(Job j : Job.values()){
		    				jobField.addItem(j);
		    			}
		    			detalleObrero.addComponent(jobField);
		    			fieldGroup.bind(jobField, "job");    
		        	}else if(propertyId.equals("maritalStatus")){
		        		ComboBox msField = new ComboBox("Estado Civil");
		        		msField.setNullSelectionAllowed(false);
		    			for(MaritalStatus ms : MaritalStatus.values()){
		    				msField.addItem(ms);
		    			}
		    			detalleObrero.addComponent(msField);
		    			fieldGroup.bind(msField, "maritalStatus");    
		        	}else{        		
		        		String t = tradProperty(propertyId);
		        		Field field = fieldGroup.buildAndBind(t, propertyId);
		        		if(field instanceof TextField){
		        			((TextField)field).setNullRepresentation("");
		        		}
		        		detalleObrero.addComponent(field);
		        		detalleObrero.setComponentAlignment(field, Alignment.MIDDLE_CENTER);
		        	}
		        }
		        
		        detalleObrero.setWidth("100%");
		        
		        UI.getCurrent().addWindow(window);

			}
		});

		FilterTable table =  new FilterTable();
		table.setContainerDataSource(laborerContainer);
		table.setSizeFull();
		table.setFilterBarVisible(true);
		//TODO estado
		table.setVisibleColumns("job","firstname","laborerId"); //FIXME laborerId
		table.setColumnHeaders("Cod","Nombre","Estado");
		table.setSelectable(true);

		vl.addComponent(table);
		vl.setExpandRatio(table,1.0F);

		return vl;
	}
	
	private String tradProperty(Object propertyId) {
		if(propertyId.equals("rut"))
			return "RUT";
		else if(propertyId.equals("firstname"))
			return "Primer Nombre";
		else if(propertyId.equals("secondname"))
			return "Segundo Nombre";
		else if(propertyId.equals("lastname"))
			return "Primer Apellido";
		else if(propertyId.equals("secondlastname"))
			return "Segundo Apellido";
		else if(propertyId.equals("dateBirth"))
			return "Fecha de Nacimiento";
		else if(propertyId.equals("address"))
			return "Direcciòn";
		else if(propertyId.equals("mobileNumber"))
			return "Teléfono móvil";
		else if(propertyId.equals("phone"))
			return "Teléfono fijo";
		else if(propertyId.equals("dateAdmission"))
			return "Fecha de Admisión";
		else
			return propertyId.toString();
	}
	
	public void setConstruction(BeanItem<ConstructionSite> item){
		if(item == null ){
			
			setEnabledDetail(false,new BeanItem<ConstructionSite>(new ConstructionSite()));
			return;
		}
		
		this.item = item;
		
		if( SecurityHelper.hastPermission(Permission.CREAR_OBRA) || SecurityHelper.hasConstructionSite(item.getBean())){
			if( editConstructionSite != null )
				editConstructionSite.setEnabled(true);
			btnPrint.setEnabled(true);
			btnAdd.setEnabled(true);;
		}else{
			if( editConstructionSite != null )
				editConstructionSite.setEnabled(false);
			btnPrint.setEnabled(false);
			btnAdd.setEnabled(false);
		}
		
		setEnabledDetail(true,item);
		List<Laborer> laborers = constructionSiteService.getLaborerByConstruction(item.getBean());
		laborerContainer.removeAllItems();
		laborerContainer.addAll(laborers);
		
		List<Team> teams = constructionSiteService.getTeamsByConstruction(item.getBean());
		teamContainer.removeAllItems();
		teamContainer.addAll(teams);
	}
	
	protected void setEnabledDetail(boolean enable,BeanItem<ConstructionSite> item) {
		bfg.setEnabled(enable);
		bfg.setItemDataSource(item);
		if(detailLayout != null)
			detailLayout.setEnabled(enable);
		
	}

}
