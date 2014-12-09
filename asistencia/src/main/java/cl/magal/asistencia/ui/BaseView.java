package cl.magal.asistencia.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.Team;
import cl.magal.asistencia.entities.User;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class BaseView extends Panel implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5865528980412390331L;
	
	
	Logger logger = LoggerFactory.getLogger(BaseView.class);

	@Override
	public void enter(ViewChangeEvent event) {

		//llena la lista de usuarios disponibles
//		Page<User> users = userService.findAllActiveUser(new PageRequest(0, 20));
		userContainer.removeAllItems();
//		userContainer.addAll(users.getContent());
	}
	
	protected BaseView(){
		teamContainer.addNestedContainerProperty("leader.firstname");
	}
	
	/** **/
	
	BeanItemContainer<User> userContainer = new BeanItemContainer<User>(User.class);
	BeanItemContainer<Team> teamContainer = new BeanItemContainer<Team>(Team.class);
	BeanItemContainer<Laborer> laborerContainer = new BeanItemContainer<Laborer>(Laborer.class);
	
	VerticalLayout detalleLayout;
	HorizontalLayout detailLayout;
	BeanFieldGroup<ConstructionSite> bfg = new BeanFieldGroup<ConstructionSite>(ConstructionSite.class);

	protected Button editConstructionSite,btnPrint,btnAdd;

	protected void setEnabledDetail(boolean enable,BeanItem<ConstructionSite> item) {
		bfg.setEnabled(enable);
		bfg.setItemDataSource(item);
//		detailLayout.setEnabled(enable);
		
	}
//	
//	
//	protected HorizontalLayout drawTopDetails() {
//
//		HorizontalLayout hl = new HorizontalLayout();
//		hl.setSizeFull();
//		hl.setSpacing(true);
//
//		hl.addComponent(new HorizontalLayout(){{
//
//			setSpacing(true);
//			setSizeFull();
//
//			FormLayout vlInfo =new FormLayout();
//			vlInfo.setSizeFull();
//			vlInfo.setSpacing(true);
//
//			final TextField nameField = new TextField("Nombre");
//			nameField.setNullRepresentation("");
//			nameField.setRequired(true);
//			nameField.setWidth("100%");
//			bfg.bind(nameField, "name");
//
//			vlInfo.addComponent(nameField);
//			
//			if(SecurityHelper.hastPermission(Permission.EDITAR_OBRA) ){
//				//agrega un boton que hace el commit
//				editConstructionSite = new Button(null,new Button.ClickListener() {
//	
//					@Override
//					public void buttonClick(ClickEvent event) {
//						try {
//							bfg.commit();
////							service.save(bfg.getItemDataSource().getBean());
//						} catch (CommitException e) {
////							logger.error("Error al guardar la información la obra",e);
//							Notification.show("Error al guardar la información del usuario", Type.ERROR_MESSAGE);
//						}
//	
//					}
//				}){{
//					setIcon(FontAwesome.SAVE);
//				}};
//				addComponent(editConstructionSite);
//				setComponentAlignment(editConstructionSite, Alignment.TOP_RIGHT);
//			}
//
//			TextField addressField = new TextField("Dirección");
//			addressField.setWidth("100%");
//			addressField.setNullRepresentation("");
//			bfg.bind(addressField, "address");
//			vlInfo.addComponent(addressField);
//
//			ComboBox statusField = new ComboBox("Estado");
//			statusField.setWidth("100%");
//			//no permite nulos
//			statusField.setNullSelectionAllowed(false);
//			for(Status s : Status.values()){
//				statusField.addItem(s);
//			}
//
//			bfg.bind(statusField, "status");
//			vlInfo.addComponent(statusField);
//
//			ComboBox personInChargeField = new ComboBox("Responsable",userContainer);
//			personInChargeField.setItemCaptionMode(ItemCaptionMode.PROPERTY);
//			personInChargeField.setItemCaptionPropertyId("fullname");
//			personInChargeField.setWidth("100%");			
//			bfg.bind(personInChargeField, "personInCharge");
//
//			vlInfo.addComponent(personInChargeField);
//
//			addComponent(vlInfo);
//			setExpandRatio(vlInfo, 1.0F);
//		}});
//
//		//		hl.addComponent(new Image());
//
//		VerticalLayout vlIBotones =new VerticalLayout();
//		vlIBotones.setSpacing(true);
//		hl.addComponent(vlIBotones);
//		hl.setComponentAlignment(vlIBotones, Alignment.MIDDLE_RIGHT );
//
//		final Button asistencia = new Button("Asistencia",FontAwesome.CHECK);
//		asistencia.addClickListener(new Button.ClickListener() {
//
//			@Override
//			public void buttonClick(ClickEvent event) {
////				switchPanels();
//				asistencia.setEnabled(true);
//			}
//		});
//		asistencia.setDisableOnClick(true);
//
//		asistencia.setSizeFull();
//		vlIBotones.addComponent(asistencia);
////		Button vacaciones = new Button("Carga Masiva Vacaciones",FontAwesome.UPLOAD);
////		vacaciones.setSizeFull();
////		vlIBotones.addComponent(vacaciones);
//
//		Button configuraciones = new Button("Configuraciones Obra",FontAwesome.GEARS);
//		configuraciones.setSizeFull();
//		vlIBotones.addComponent(configuraciones);
//		vlIBotones.setWidth("300px");
//
//		return hl;
//	}
//
//	protected VerticalLayout drawContructionDetail() {
//		detalleLayout = new VerticalLayout(); 
//		detalleLayout.setSizeFull();
//		detalleLayout.setSpacing(true);
//
//		//creando la parte de arriba
//		detailLayout = drawTopDetails();
//		detalleLayout.addComponent(detailLayout);
//		detalleLayout.setExpandRatio(detailLayout, 0.3F);
//
//		//crea el tab con trabajadores y cuadrillas
//		TabSheet tab = new TabSheet();
//		tab.setSizeFull();
//
//		detalleLayout.addComponent(tab);
//		detalleLayout.setExpandRatio(tab, 0.7F);
//		//tab de trabajadores
//		tab.addTab(drawLaborer(),"Trabajadores");
//
//		//tab de cuadrillas
//		tab.addTab(drawCuadrillas(),"Cuadrillas");
//
//		return detalleLayout;
//	}
//	
//	protected VerticalLayout drawCuadrillas() {
//		VerticalLayout vl = new VerticalLayout();
//		vl.setSizeFull();
//		vl.setSpacing(true);
//		vl.setMargin(true);
//
//		Button btnAdd = new Button(null,FontAwesome.PLUS);
//		vl.addComponent(btnAdd);
//		vl.setComponentAlignment(btnAdd, Alignment.TOP_RIGHT);
//
//		btnAdd.addClickListener(new Button.ClickListener() {
//
//			@Override
//			public void buttonClick(ClickEvent event) {
////				ConstructionSite cs = (ConstructionSite) table.getValue();
////				if(cs == null){
////					Notification.show("Debe seleccionar una obra",Type.ERROR_MESSAGE);
////					return;
////				}
////
////				Team team = new Team();
////				team.setName("Cuadrilla 1");
////				team.setDate(new Date());
////				team.setStatus(Status.ACTIVE);
////				team.setLeader(laborerContainer.firstItemId());
////				service.addTeamToConstructionSite(team,cs);
////
////				teamContainer.addBean(team);
//
//			}
//		});
//		
//		FilterTable table =  new FilterTable(){
//
//			@Override
//            protected String formatPropertyValue(Object rowId, Object colId,
//            		Property<?> property) {
//                Object v = property.getValue();
//                if (v instanceof Date) {
//                    Date dateValue = (Date) v;
//                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
//                    return sdf.format(dateValue);
//                }
//                return super.formatPropertyValue(rowId, colId, property);
//            }
//		};
//		
//		table.setContainerDataSource(teamContainer);
//		table.setSizeFull();
//		table.setFilterBarVisible(true);
//		table.setVisibleColumns("name","leader.firstname","date","status");
//		table.setColumnHeaders("Nombre","Responsable","Fecha","Estado");
//		
//		table.setSelectable(true);
//
//		vl.addComponent(table);
//		vl.setExpandRatio(table,1.0F);
//
//		return vl;
//	}
//	
//	
//	
//	
//	protected VerticalLayout drawLaborer() {
//
//		VerticalLayout vl = new VerticalLayout();
//		vl.setSpacing(true);
//		vl.setMargin(true);
//		vl.setSizeFull();
//
//		//boton para agregar trabajadores e imprimir
//		HorizontalLayout hl = new HorizontalLayout();
//		hl.setWidth("100%");
//		hl.setSpacing(true);
//		
//		final Button asistencia = new Button("Asistencia",FontAwesome.CHECK);
//		asistencia.addClickListener(new Button.ClickListener() {
//
//			@Override
//			public void buttonClick(ClickEvent event) {
////				switchPanels();
//				asistencia.setEnabled(true);
//			}
//		});
//		asistencia.setDisableOnClick(true);
//
//		asistencia.setWidth("200px");
//		hl.addComponent(asistencia);
//		hl.setComponentAlignment(asistencia, Alignment.TOP_CENTER);
//		hl.setExpandRatio(asistencia, 1.0F);
//
//		vl.addComponent(hl);
//
//		btnPrint = new Button(null,FontAwesome.PRINT);
//		hl.addComponent(btnPrint);
//		hl.setComponentAlignment(btnPrint, Alignment.TOP_RIGHT);
//
//		btnPrint.addClickListener(new Button.ClickListener() {
//
//			@Override
//			public void buttonClick(ClickEvent event) {
//				Notification.show("Imprimiendo");
//
//			}
//		});
//
//		btnAdd = new Button(null,FontAwesome.PLUS);
//		hl.addComponent(btnAdd);
//		hl.setComponentAlignment(btnAdd, Alignment.TOP_RIGHT);
//
//		btnAdd.addClickListener(new Button.ClickListener() {
//
//			@Override
//			public void buttonClick(ClickEvent event) {
////				final ConstructionSite cs = (ConstructionSite) table.getValue();
////				if(cs == null){
////					Notification.show("Debe seleccionar una obra",Type.ERROR_MESSAGE);
////					return;
////				}
//				
//				final Window window = new Window();
//				window.setWidth("70%");
//				
//				window.setModal(true);
//				window.center();
//				
////				VerticalLayout detalleObrero = new VerticalLayout();
//				GridLayout detalleObrero = new GridLayout(2,5);
//				detalleObrero.setMargin(true);
//				detalleObrero.setSpacing(true);
//				
//				
//				window.setContent(new Panel(detalleObrero));
//				
//				HorizontalLayout hl = new HorizontalLayout();
//				hl.setSpacing(true);
//				detalleObrero.addComponent(hl,0,0,1,0);
//				detalleObrero.setComponentAlignment(hl, Alignment.TOP_RIGHT);
//				
//				
//				final BeanFieldGroup<Laborer> fieldGroup = new BeanFieldGroup<Laborer>(Laborer.class);
//		        fieldGroup.setItemDataSource(new BeanItem<Laborer>(new Laborer()));
//
//		        //agrega un boton que hace el commit
//		        Button add = new Button(null,new Button.ClickListener() {
//
//		        	@Override
//		        	public void buttonClick(ClickEvent event) {
//		        		try {
//		        			fieldGroup.commit();
//		        			Laborer laborer = fieldGroup.getItemDataSource().getBean();
////		        			service.addLaborerToConstructionSite(laborer,cs);				
////		        			laborerContainer.addBean(laborer);
//		        			window.close();
//		        		} catch (Exception e) {
//		        			logger.error("Error al guardar la información del obrero");
//		        			Notification.show("Es necesario agregar todos los campos obligatorios", Type.ERROR_MESSAGE);
//		        		}
//
//		        	}
//		        }){{
//		        	setIcon(FontAwesome.SAVE);
//		        }};
//		        hl.addComponent(add);
//		        //detalleObrero.addComponent(add);
//		        //detalleObrero.setComponentAlignment(add, Alignment.TOP_RIGHT);
//		        
//				//boton para imprimir
//				Button btnPrint = new Button(null,new Button.ClickListener() {
//					
//					@Override
//					public void buttonClick(ClickEvent event) {
//						Notification.show("Imprimiendo");
//						
//					}
//				}){{
//					setIcon(FontAwesome.PRINT);
//				}};
//				 hl.addComponent(btnPrint);
//				//detalleObrero.addComponent(btnPrint);
//				//detalleObrero.setComponentAlignment(btnPrint, Alignment.TOP_LEFT);        
//		        // Loop through the properties, build fields for them and add the fields
//		        // to this UI
//				 for (Object propertyId : new String[]{"rut","firstname","secondname","lastname", "secondlastname", "dateBirth", "address", "mobileNumber", "phone", "dateAdmission"}) {
//		        	if(propertyId.equals("laborerId") || propertyId.equals("constructionSites") || propertyId.equals("contractId") || propertyId.equals("teamId"))
//		        		;
//		        	else if(propertyId.equals("afp")){
//		        		ComboBox afpField = new ComboBox("AFP");
//		        		afpField.setNullSelectionAllowed(false);
//		    			for(Afp a : Afp.values()){
//		    				afpField.addItem(a);
//		    			}
//		    			detalleObrero.addComponent(afpField);
//		    			fieldGroup.bind(afpField, "afp");    			
//		        	}else if(propertyId.equals("job")){
//		        		ComboBox jobField = new ComboBox("Oficio");
//		        		jobField.setNullSelectionAllowed(false);
//		    			for(Job j : Job.values()){
//		    				jobField.addItem(j);
//		    			}
//		    			detalleObrero.addComponent(jobField);
//		    			fieldGroup.bind(jobField, "job");    
//		        	}else if(propertyId.equals("maritalStatus")){
//		        		ComboBox msField = new ComboBox("Estado Civil");
//		        		msField.setNullSelectionAllowed(false);
//		    			for(MaritalStatus ms : MaritalStatus.values()){
//		    				msField.addItem(ms);
//		    			}
//		    			detalleObrero.addComponent(msField);
//		    			fieldGroup.bind(msField, "maritalStatus");    
//		        	}else{        		
//		        		String t = tradProperty(propertyId);
//		        		Field field = fieldGroup.buildAndBind(t, propertyId);
//		        		if(field instanceof TextField){
//		        			((TextField)field).setNullRepresentation("");
//		        		}
//		        		detalleObrero.addComponent(field);
//		        		detalleObrero.setComponentAlignment(field, Alignment.MIDDLE_CENTER);
//		        	}
//		        }
//		        
//		        detalleObrero.setWidth("100%");
//		        
//		        UI.getCurrent().addWindow(window);
//
//			}
//		});
//
//		FilterTable table =  new FilterTable();
//		table.setContainerDataSource(laborerContainer);
//		table.setSizeFull();
//		table.setFilterBarVisible(true);
//		//TODO estado
//		table.setVisibleColumns("job","firstname","laborerId"); //FIXME laborerId
//		table.setColumnHeaders("Cod","Nombre","Estado");
//		table.setSelectable(true);
//
//		vl.addComponent(table);
//		vl.setExpandRatio(table,1.0F);
//
//		return vl;
//	}
//
//	private String tradProperty(Object propertyId) {
//		if(propertyId.equals("rut"))
//			return "RUT";
//		else if(propertyId.equals("firstname"))
//			return "Primer Nombre";
//		else if(propertyId.equals("secondname"))
//			return "Segundo Nombre";
//		else if(propertyId.equals("lastname"))
//			return "Primer Apellido";
//		else if(propertyId.equals("secondlastname"))
//			return "Segundo Apellido";
//		else if(propertyId.equals("dateBirth"))
//			return "Fecha de Nacimiento";
//		else if(propertyId.equals("address"))
//			return "Direcciòn";
//		else if(propertyId.equals("mobileNumber"))
//			return "Teléfono móvil";
//		else if(propertyId.equals("phone"))
//			return "Teléfono fijo";
//		else if(propertyId.equals("dateAdmission"))
//			return "Fecha de Admisión";
//		else
//			return propertyId.toString();
//	}

}
