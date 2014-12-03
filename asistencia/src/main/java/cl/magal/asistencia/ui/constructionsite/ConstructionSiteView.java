package cl.magal.asistencia.ui.constructionsite;

import java.io.OutputStream;
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

import ru.xpoft.vaadin.VaadinView;
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

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@VaadinView(value=ConstructionSiteView.NAME,cached=false)
@Scope("prototype")
@Component
public class ConstructionSiteView extends Panel  implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8616320162970295709L;

	private transient Logger logger = LoggerFactory.getLogger(ConstructionSiteView.class);

	public static final String NAME = "obras";

	BeanFieldGroup<ConstructionSite> bfg = new BeanFieldGroup<ConstructionSite>(ConstructionSite.class);
	BeanItemContainer<ConstructionSite> constructionContainer = new BeanItemContainer<ConstructionSite>(ConstructionSite.class);
	BeanItemContainer<Laborer> laborerContainer = new BeanItemContainer<Laborer>(Laborer.class);
	BeanItemContainer<User> userContainer = new BeanItemContainer<User>(User.class);
	BeanItemContainer<Team> teamContainer = new BeanItemContainer<Team>(Team.class);

	FilterTable table;
	VerticalLayout detalleLayout;

	@Autowired
	private transient ConstructionSiteService service;
	@Autowired
	private transient UserService userService;

	HorizontalLayout root,detailLayout;
	Panel panelConstruction,panelAttendance;


	public ConstructionSiteView(){

		logger.debug("obras");

		setSizeFull();
		
		teamContainer.addNestedContainerProperty("leader.firstname");

		root = new HorizontalLayout();
		root.setSizeFull();
		setContent(root);

		//crea el panel de la información de obra
		panelConstruction = new Panel();
		panelConstruction.setSizeFull();

		//dibula la sección de las obras
		final VerticalLayout obras = drawObras();

		//dibuja la sección de detalles
		final VerticalLayout detalleObra = drawContructionDetail();
		//rellena el panel de la información de obra
		panelConstruction.setContent(new HorizontalLayout(){
			{
				setSizeFull();
				setSpacing(true);
				setMargin(true);

				addComponent(obras);
				setExpandRatio(obras, 0.2F);
				addComponent(detalleObra);
				setExpandRatio(detalleObra, 0.8F);
			}
		});

		root.addComponent(panelConstruction);

		//crea el panel de asistencia para intercambiarlo
		panelAttendance = new Panel();
		panelAttendance.setSizeFull();

		//crea la parte superior de la interfaz de asistencia
		final HorizontalLayout topAsistencia = drawTopAttendance();
		//crea las tabs que contienen la información de asistencia
		final TabSheet detalleAsistencia = drawAttendanceDetail();

		panelAttendance.setContent(new VerticalLayout(){
			{
				setSizeFull();
				setSpacing(true);
				setMargin(true);

				addComponent(topAsistencia);
				setExpandRatio(topAsistencia, 0.1F);
				addComponent(detalleAsistencia);
				setExpandRatio(detalleAsistencia, 0.9F);
			}
		});


	}

	private TabSheet drawAttendanceDetail() {
		TabSheet tab = new TabSheet();

		final Object[][] dummies = new Object[28][26];
		int j = 0;
		for(Object[] array  : dummies){
			for (int i = 0; i < array.length; i++) {
				if(i == 0)
					array[0] = j + 1;
				else if( i == 1)
					array[1] = "Trabajador "+(j + 1);
				else
					array[i] = randomAttendance();
			}
			j++;
		}


		Table table1 = new Table(){
			{
				setSizeFull();
				setEditable(true);
				addContainerProperty("number", Integer.class, 0);
				setColumnHeader("number", "#");
				setColumnWidth("number", 30);
				addGeneratedColumn("number", new ColumnGenerator() {

					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {
						return source.getItem(itemId).getItemProperty(columnId).getValue();
					}
				});

				addContainerProperty("firstname", String.class, "");
				setColumnHeader("firstname", "Nombre");
				setColumnWidth("firstname", 100);
				addGeneratedColumn("firstname", new ColumnGenerator() {

					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {
						return source.getItem(itemId).getItemProperty(columnId).getValue();
					}
				});

				for(int i = 25; i < 55 ; i++){ 
					addContainerProperty("day"+i, String.class, "");
					setColumnHeader("day"+i, ((i%31)+1)+"");
					setColumnWidth("day"+i, 50);
				}


			}
		};
		
		for(int i = 0 ; i < 2 ; i++){
			table1.addItem(new Object[]{0,"e3da","ddasda"});
		}

//		table1.setTableFieldFactory(new ImmediateFieldFactory());

		tab.addTab(table1,"Asistencia");


		tab.addTab(new Table(){
			{
				setSizeFull();
				setEditable(true);
				addContainerProperty("number", Integer.class, 0);
				setColumnHeader("number", "#");
				setColumnWidth("number", 30);
				addGeneratedColumn("number", new ColumnGenerator() {

					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {
						return source.getItem(itemId).getItemProperty(columnId).getValue();
					}
				});

				addContainerProperty("firstname", String.class, "");
				setColumnHeader("firstname", "Nombre");
				setColumnWidth("firstname", 100);
				addGeneratedColumn("firstname", new ColumnGenerator() {

					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {
						return source.getItem(itemId).getItemProperty(columnId).getValue();
					}
				});

				for(int i = 25; i < 55 ; i++){ 
					addContainerProperty("day"+i, String.class, "");
					setColumnHeader("day"+i, ((i%31)+1)+"");
					setColumnWidth("day"+i, 50);
				}

			}
		},"Horas Extras");

		tab.addTab(new Table(){
			{
				setSizeFull();
				setEditable(true);
			}
		},"Cálculos");

		return tab;
	}

	public class ImmediateFieldFactory extends DefaultFieldFactory {
		public Field createField(Container container,
				Object itemId,
				Object propertyId,
				com.vaadin.ui.Component uiContext) {
			Field field = null;
			if(propertyId.equals("number") || propertyId.equals("firstname") )
				field = super.createField(container, itemId,
						propertyId, uiContext);
			else {
				field = new ComboBox(null);
				for(String s : new String[]{"X","LL","P","E","S","D"}){
					((ComboBox)field).addItem(s);
				}
			}

			return field;
		}
	}

	private Object randomAttendance() {
		return "X";
	}

	private HorizontalLayout drawTopAttendance() {
		return new HorizontalLayout(){
			{
				setWidth("100%");

				addComponent(new HorizontalLayout(){
					{
						setSpacing(true);
						Button btn = new Button(null, new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {	
								switchPanels();
							}
						} ){
							{
								setIcon(FontAwesome.ARROW_CIRCLE_LEFT);
							}
						};
						addComponent(btn);
						setComponentAlignment(btn, Alignment.MIDDLE_LEFT);
						Label lb = new Label("<h1>Asistencia Obra XXXX</h1>",ContentMode.HTML);
						addComponent(lb);
						setComponentAlignment(lb, Alignment.MIDDLE_LEFT);
					}
				});

				addComponent(new VerticalLayout(){
					{
						setSizeFull();
						addComponent(new HorizontalLayout(){
							{
								setSizeFull();
								setSpacing(true);
								addComponent(new FormLayout(){
									{
										addComponent(new InlineDateField("Mes"){
											{
												setResolution(Resolution.MONTH);
											}
										});
									}
								});
//								addComponent(new Button("Configuraciones",FontAwesome.GEARS));

							}
						});
						addComponent(new HorizontalLayout(){
							{
								setSpacing(true);
								addComponent(new Label("Última Carga Información Reloj: 11/01/2014"));
								addComponent(new Button("Cargar",new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
										final Window window = new Window();
										
										window.center();
										window.setModal(true);
										window.setResizable(false);
										
										
//										window.setHeight("50%");
										
										window.setContent(new VerticalLayout(){
											{
//												setSizeFull();
//												setSpacing(true);
												setMargin(true);
												VerticalLayout form = new VerticalLayout(){
													{
														setWidth("700px");
														setMargin(true);
														setSpacing(true);
														addComponent(new Label("Última Carga Información Reloj: 11/01/2014"));
														addComponent(new Upload("Información Reloj: ",new Upload.Receiver() {
															
															@Override
															public OutputStream receiveUpload(String filename, String mimeType) {
																
																return null;
															}
														}));
														addComponent(new Label("Última Carga vacaciones: 12/01/2014"));
														addComponent(new Upload("Carga Vacaciones: ",new Upload.Receiver() {
															
															@Override
															public OutputStream receiveUpload(String filename, String mimeType) {
																
																return null;
															}
														}));
														
													}
												};
												
												addComponent(form);
												setExpandRatio(form, 1.0F);
													
												HorizontalLayout footer = new HorizontalLayout();
												footer.setHeight("60px");
												footer.setSpacing(true);
														
												Button btnGuardar = new Button("Aceptar");
												btnGuardar.addClickListener(new Button.ClickListener() {
													
													@Override
													public void buttonClick(ClickEvent event) {
														window.close();
														
													}
												});
												btnGuardar.addStyleName("default");
												footer.addComponent(btnGuardar);
												footer.setComponentAlignment(btnGuardar, Alignment.MIDDLE_RIGHT);

												Button btnCancelar = new Button("Cancelar");
												btnCancelar.addClickListener(new Button.ClickListener() {
													
													@Override
													public void buttonClick(ClickEvent event) {
														window.close();
														
													}
												});
												btnCancelar.addStyleName("link");
												footer.addComponent(btnCancelar);
												footer.setComponentAlignment(btnCancelar, Alignment.MIDDLE_RIGHT);

												addComponent(new Label("<hr />", ContentMode.HTML));
												addComponent(footer);
												setComponentAlignment(footer, Alignment.MIDDLE_RIGHT);
												
											}
										});
										
										UI.getCurrent().addWindow(window);
									}
								}){
									{
										setWidth("100%");
										setIcon(FontAwesome.UPLOAD);
									}
								});
							}
						});
					}
				});
				Button btn = new Button("Exportar",FontAwesome.FILE_EXCEL_O);
				addComponent(btn);
				setComponentAlignment(btn, Alignment.TOP_RIGHT);

			}
		};
	}

	@PostConstruct
	private void init(){
	}

	private VerticalLayout drawObras() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);

		//agrega solo si tiene los permisos
		
		if( hastPermission(Permission.CREAR_OBRA,Permission.ELIMINAR_OBRA)){
		
			//botones agrega y eliminar
			HorizontalLayout hl = new HorizontalLayout();
			hl.setSpacing(true);
	
			vl.addComponent(hl);
			vl.setComponentAlignment(hl, Alignment.BOTTOM_CENTER );
			Button agregaObra = new Button(null,FontAwesome.PLUS);
			//agregando obras dummy
			agregaObra.addClickListener(new Button.ClickListener() {
	
				/**
				 * 
				 */
				private static final long serialVersionUID = 3844920778615955739L;
	
				@Override
				public void buttonClick(ClickEvent event) {
					ConstructionSite obra = new ConstructionSite();
					obra.setName("Nueva Obra");
					service.save(obra);
					BeanItem<ConstructionSite> item = constructionContainer.addBean(obra);
					setConstruction(item);
				}
			});
			hl.addComponent(agregaObra);
			Button borrarObra = new Button(null,FontAwesome.TRASH_O);
			borrarObra.addClickListener(new Button.ClickListener() {
	
				@Override
				public void buttonClick(ClickEvent event) {
					ConstructionSite cs = (ConstructionSite) table.getValue();
					if(cs == null){
						Notification.show("Debe seleccionar una obra para eliminar");
						return;
					}
					service.deleteCS(cs.getConstructionsiteId());
					constructionContainer.removeItem(cs);
					setConstruction(null);
	
				}
			});
			hl.addComponent(borrarObra);
		}

		//la tabla con su buscador buscador
		vl.addComponent(drawConstructionTable());

		return vl;
	}
	
	private boolean hasConstructionSite(ConstructionSite cs){
		
		User usuario = (User) VaadinSession.getCurrent().getAttribute(
				"usuario");
		if(usuario.getCs()!= null && usuario.getCs().contains(cs) ){
			return true;
		}
		
		return false;
	}

	private boolean hastPermission(Permission... permissions) {
		if(permissions == null)
			return true;
		
		User usuario = (User) VaadinSession.getCurrent().getAttribute(
				"usuario");
		if(usuario.getRole() == null || usuario.getRole().getPermission() == null ){
			return false;
		}
		for(Permission p : permissions){
			if(!usuario.getRole().getPermission().contains(p))
				return false;
		}
		
		return true;
	}

	private FilterTable drawConstructionTable() {
		table =  new FilterTable();
		table.setContainerDataSource(constructionContainer);
		table.setSizeFull();
		table.setFilterBarVisible(true);
		table.setVisibleColumns("name");
		table.setColumnHeaders("Nombre");
		table.setSelectable(true);

		table.addItemClickListener(new ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {
				setConstruction((BeanItem<ConstructionSite>)event.getItem());
			}
		});
		return table;
	}

	private VerticalLayout drawContructionDetail() {
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

	private VerticalLayout drawCuadrillas() {
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
				ConstructionSite cs = (ConstructionSite) table.getValue();
				if(cs == null){
					Notification.show("Debe seleccionar una obra",Type.ERROR_MESSAGE);
					return;
				}

				Team team = new Team();
				team.setName("Cuadrilla 1");
				team.setDate(new Date());
				team.setStatus(Status.ACTIVE);
				team.setLeader(laborerContainer.firstItemId());
				service.addTeamToConstructionSite(team,cs);

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
	
	private VerticalLayout drawLaborer() {

		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();

		//boton para agregar trabajadores e imprimir
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);

		vl.addComponent(hl);
		vl.setComponentAlignment(hl, Alignment.TOP_RIGHT);

		btnPrint = new Button(null,FontAwesome.PRINT);
		hl.addComponent(btnPrint);

		btnPrint.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show("Imprimiendo");

			}
		});

		btnAdd = new Button(null,FontAwesome.PLUS);
		hl.addComponent(btnAdd);

		btnAdd.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				final ConstructionSite cs = (ConstructionSite) table.getValue();
				if(cs == null){
					Notification.show("Debe seleccionar una obra",Type.ERROR_MESSAGE);
					return;
				}
				
				final Window window = new Window();
				window.setWidth("70%");
				
				window.setModal(true);
				window.center();
				
//				VerticalLayout detalleObrero = new VerticalLayout();
				GridLayout detalleObrero = new GridLayout(2,5);
				detalleObrero.setMargin(true);
				detalleObrero.setSpacing(true);
				
				
				window.setContent(new Panel(detalleObrero));
				
				HorizontalLayout hl = new HorizontalLayout();
				hl.setSpacing(true);
				detalleObrero.addComponent(hl,0,0,1,0);
				detalleObrero.setComponentAlignment(hl, Alignment.TOP_RIGHT);
				
				
				final BeanFieldGroup<Laborer> fieldGroup = new BeanFieldGroup<Laborer>(Laborer.class);
		        fieldGroup.setItemDataSource(new BeanItem<Laborer>(new Laborer()));

		        //agrega un boton que hace el commit
		        Button add = new Button(null,new Button.ClickListener() {

		        	@Override
		        	public void buttonClick(ClickEvent event) {
		        		try {
		        			fieldGroup.commit();
		        			Laborer laborer = fieldGroup.getItemDataSource().getBean();
		        			service.addLaborerToConstructionSite(laborer,cs);				
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

	private void setConstruction(BeanItem<ConstructionSite> item){
		if(item == null ){
			
			setEnabledDetail(false,new BeanItem<ConstructionSite>(new ConstructionSite()));
			return;
		}
		
		if( hastPermission(Permission.CREAR_OBRA) || hasConstructionSite(item.getBean())){
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
		List<Laborer> laborers = service.getLaborerByConstruction(item.getBean());
		laborerContainer.removeAllItems();
		laborerContainer.addAll(laborers);
		
		List<Team> teams = service.getTeamsByConstruction(item.getBean());
		teamContainer.removeAllItems();
		teamContainer.addAll(teams);
	}

	private void setEnabledDetail(boolean enable,BeanItem<ConstructionSite> item) {
		bfg.setEnabled(enable);
		bfg.setItemDataSource(item);
		detailLayout.setEnabled(enable);
		
	}
	
	Button editConstructionSite,btnPrint,btnAdd;

	private HorizontalLayout drawTopDetails() {

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
			
			if(hastPermission(Permission.EDITAR_OBRA) ){
				//agrega un boton que hace el commit
				editConstructionSite = new Button(null,new Button.ClickListener() {
	
					@Override
					public void buttonClick(ClickEvent event) {
						try {
							bfg.commit();
							service.save(bfg.getItemDataSource().getBean());
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
				switchPanels();
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
	
	private void switchPanels() {
		
		if(root.getComponentIndex(panelConstruction) >= 0){
			root.removeComponent(panelConstruction);
			root.addComponent(panelAttendance);
		}else{
			root.removeComponent(panelAttendance);
			root.addComponent(panelConstruction);
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		reloadData();
	}

	private void reloadData(){
		//agrega las obras TODO segun perfil TODO usar paginación
		Page<ConstructionSite> page = service.findAllConstructionSite(new PageRequest(0, 20));
		constructionContainer.removeAllItems();
		constructionContainer.addAll(page.getContent());

		//si tiene al menos un elemento selecciona el primero
		setConstruction( constructionContainer.getItem( constructionContainer.firstItemId() ));
		table.select(constructionContainer.firstItemId());

		//llena la lista de usuarios disponibles
		Page<User> users = userService.findAllActiveUser(new PageRequest(0, 20));
		userContainer.removeAllItems();
		userContainer.addAll(users.getContent());

		//agrea los trabajadores asociados a la obra TODO segun la obra seleccionada
		//si no es vacia
		if(!page.getContent().isEmpty()){
			ConstructionSite fisrt = page.getContent().get(0);
			Page<Laborer> laborerPage = service.findLaborerByConstruction(fisrt);
		}
	}

}
