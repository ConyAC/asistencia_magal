package cl.magal.asistencia.ui.constructionsite;

import java.io.OutputStream;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.VaadinView;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.enums.AttendanceMark;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.ui.BaseView;
import cl.magal.asistencia.ui.MagalUI;

import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Container.SimpleFilterable;
import com.vaadin.data.util.AbstractBeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
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
public class ConstructionSiteView extends BaseView  implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8616320162970295709L;

	private transient Logger logger = LoggerFactory.getLogger(ConstructionSiteView.class);

	public static final String NAME = "obra";

	@Autowired
	LaborerAndTeamPanel laborerAndTeamPanel;

	@Autowired
	private transient ConstructionSiteService service;
	@Autowired
	private transient UserService userService;

	HorizontalLayout root;
//	Panel panelConstruction,
	Panel panelAttendance;
	BeanItemContainer attendanceLaborerContainer;

	public ConstructionSiteView(){

		setSizeFull();

		root = new HorizontalLayout();
		root.setSizeFull();
		setContent(root);

		//crea el panel de la información de obra
//		panelConstruction = new Panel();
//		panelConstruction.setSizeFull();
		
//		//crea el tab con trabajadores y cuadrillas
//		TabSheet tab = new TabSheet();
//		tab.setSizeFull();
//		
//		//tab de trabajadores
//		tab.addTab(drawLaborer(),"Trabajadores");
//
//		//tab de cuadrillas
//		tab.addTab(drawCuadrillas(),"Cuadrillas");
//		//rellena el panel de la información de obra
//		panelConstruction.setContent(tab);

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

				topAsistencia.setSizeFull();
				addComponent(topAsistencia);
				setExpandRatio(topAsistencia, 0.1F);
				detalleAsistencia.setSizeFull();
				addComponent(detalleAsistencia);
				setExpandRatio(detalleAsistencia, 0.9F);
			}
		});


	}
	
	Grid table1;

	private TabSheet drawAttendanceDetail() {
		TabSheet tab = new TabSheet();

		//usando un grid en vez de un table
//		FilterTable table1 = new FilterTable(){
//			{
//				setSizeFull();
//			}
//		};
//		attendanceLaborerContainer = new BeanItemContainer(LaborerConstructionsite.class);
//		attendanceLaborerContainer.addNestedContainerProperty("activeContract.jobCode");
//		GeneratedPropertyContainer gpcontainer = new GeneratedPropertyContainer(attendanceLaborerContainer);
		table1 = new Grid();
		table1.setSizeFull();
		
		Object[] visibleColumns = new Object[ (55 - 25) + 1];
		visibleColumns[0] = "activeContract.jobCode";
		
		table1.addColumn("activeContract.jobCode",Integer.class).setHeaderCaption("Código").setEditable(false);
		table1.addStyleName("grid-attendace");
		table1.setEditorEnabled(true);
		table1.setFrozenColumnCount(1);
		
		for(int i = 25; i < 55 ; i++){
			
			table1.addColumn("day"+i,AttendanceMark.class).setHeaderCaption( ((i%31)+1)+"").setSortable(false);
			visibleColumns[i - 24] = "day"+i; 
		}

		HeaderRow filterRow = table1.appendHeaderRow();
		for (final Object pid: table1.getContainerDataSource()
                .getContainerPropertyIds()) {
		final HeaderCell cell = filterRow.getCell(pid);
		
		// Have an input field to use for filter
		TextField filterField = new TextField();
		filterField.setColumns(8);
		
		// Update filter When the filter input is changed
		filterField.addTextChangeListener(new TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				  // Can't modify filters so need to replace
				((SimpleFilterable) table1.getContainerDataSource()).removeContainerFilters(pid);
				   
				   // (Re)create the filter if necessary
				   if (! event.getText().isEmpty())
					   ((Filterable) table1.getContainerDataSource()).addContainerFilter(
				           new SimpleStringFilter(pid,
				        		   event.getText(), true, false));
			}
		});
		cell.setComponent(filterField);
		}
		
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

	private AttendanceMark randomAttendance() {
		return AttendanceMark.ATTEND;
	}

	private HorizontalLayout drawTopAttendance() {
		return new HorizontalLayout(){
			{
				setWidth("100%");

//				addComponent(new HorizontalLayout(){
//					{
//						setSpacing(true);
//						Button btn = new Button(null, new Button.ClickListener() {
//
//							@Override
//							public void buttonClick(ClickEvent event) {	
//								switchPanels();
//							}
//						} ){
//							{
//								setIcon(FontAwesome.ARROW_CIRCLE_LEFT);
//							}
//						};
//						addComponent(btn);
//						setComponentAlignment(btn, Alignment.MIDDLE_LEFT);
//						Label lb = new Label("<h1>Asistencia Obra XXXX</h1>",ContentMode.HTML);
//						addComponent(lb);
//						setComponentAlignment(lb, Alignment.MIDDLE_LEFT);
//					}
//				});

				addComponent(new HorizontalLayout(){
					{
						addComponent(new FormLayout(){
							{
								addComponent(new InlineDateField("Mes"){
									{
										setResolution(Resolution.MONTH);
									}
								});
							}
						});

						addComponent(new HorizontalLayout(){
							{
								addComponent(new Label("Última Carga Información Reloj: 11/01/2014"));
								addComponent(new Button("Cargar",new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
										final Window window = new Window();
										
										window.center();
										window.setModal(true);
										window.setResizable(false);
										
										window.setContent(new VerticalLayout(){
											{
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
	
	Button.ClickListener backListener = new Button.ClickListener() {

		@Override
		public void buttonClick(ClickEvent event) {
			logger.debug("back");
			UI.getCurrent().getNavigator().navigateTo(ConstructionSitesView.NAME);

		}
	};

	@PostConstruct
	private void init(){
		
		laborerAndTeamPanel.setHasAttendanceButton(true);
		laborerAndTeamPanel.setHasConstructionDetails(false);
		root.addComponent(laborerAndTeamPanel);
		
		laborerAndTeamPanel.addAttendanceClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				switchPanels();
			}
		});
		
		((MagalUI)UI.getCurrent()).getBackButton().addClickListener(backListener);
	}
	
	private void switchPanels() {
		
		if(root.getComponentIndex(laborerAndTeamPanel) >= 0){
			root.removeComponent(laborerAndTeamPanel);
			root.addComponent(panelAttendance);
		}else{
			root.removeComponent(panelAttendance);
			root.addComponent(laborerAndTeamPanel);
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
		((MagalUI)UI.getCurrent()).setBackVisible(true);
		((MagalUI)UI.getCurrent()).highlightMenuItem(ConstructionSitesView.NAME);
		
		//obtiene los parametros de url
		if(event.getParameters() != null){
	          // split at "/", add each part as a label
	          String[] msgs = event.getParameters().split("/");
	          //si no trae parametros, entonces avisa y deshabilita la interfaz
	          if(msgs == null || msgs.length == 0 ){
	        	 showErrorParam();
	        	  return;
	          }
	          //si trae parametro verifica que sea un numero valido
	          try{
	        	  //solo considera el primer parametro
	        	  Long id = Long.valueOf(msgs[0]);
	        	  //si todo va bien, carga la información de la obra
	        	  reloadData(id);
	          }catch(NumberFormatException e){
	        	  showErrorParam();
	        	  return;
	          }
	          
		}else{
			showErrorParam();
		}
		
	}
	ConstructionSite cs;
	
	private void reloadData(Long id){

		//busca la información de la obra
		cs = service.findConstructionSite(id);
		if(cs == null ){
			showErrorParam();
			return;
		}
		//setea el nombre de la construccion en el titulo
		((MagalUI)UI.getCurrent()).getTitle().setValue("<h1>"+cs.getName()+"</h1>");
		
		laborerAndTeamPanel.setConstruction( new BeanItem<ConstructionSite>(cs) );
		List<LaborerConstructionsite> laborers = service.getLaborerActiveByConstruction(cs);
		for(LaborerConstructionsite lc : laborers){
			Object[] row = new Object[ (55  - 25) + 1 ];
			 
			row[0] = lc.getJobCode();
			for(int i = 25; i < 55 ; i++){
				row[ i - 24 ] = randomAttendance();
			}
			table1.addRow(row);
		}
		
		
	}
	

	private void showErrorParam() {
		Notification.show("Debe seleccionar una obra ",Type.ERROR_MESSAGE);
		//setea el titulo como vacio
		((MagalUI)UI.getCurrent()).getTitle().setValue("");
   	  	root.setEnabled(false);		
	}

}
