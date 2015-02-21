package cl.magal.asistencia.ui.constructionsite;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.FilterTable;

import ru.xpoft.vaadin.VaadinView;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.ui.BaseView;
import cl.magal.asistencia.ui.MagalUI;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
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

	private TabSheet drawAttendanceDetail() {
		TabSheet tab = new TabSheet();


		FilterTable table1 = new FilterTable(){
			{
				setSizeFull();
				setEditable(true);
			}
		};
		
		Object[] visibleColumns = new Object[ (55 - 25) + 1];
		visibleColumns[0] = "activeContract.jobCode";
		
		table1.addGeneratedColumn("activeContract.jobCode", new FilterTable.ColumnGenerator() {
			
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				return source.getContainerProperty(itemId, columnId).getValue();
			}

		});
		table1.setColumnHeader("activeContract.jobCode", "Código");
		table1.setColumnWidth("activeContract.jobCode", 50);
		table1.setBuffered(true);
		for(int i = 25; i < 55 ; i++){
			table1.addGeneratedColumn("day"+i, new FilterTable.ColumnGenerator() {
				
				@Override
				public Object generateCell(CustomTable source, Object itemId, Object columnId) {
//					ComboBox cb = new ComboBox(null);
//					cb.setWidth("100%");
//					cb.addItems(new Object[]{"X","LL","S"});
//					cb.setValue("X");
//					return cb;
					final Label lb = new Label( randomAttendance()+"");
					final CssLayout csLy = new CssLayout(lb);
					csLy.setSizeFull();
					csLy.addLayoutClickListener(new com.vaadin.event.LayoutEvents.LayoutClickListener() {
						
						@Override
						public void layoutClick(LayoutClickEvent event) {
							//al recibir un click, quita el label y pone el combobox con las opciones
							csLy.removeComponent(lb);
							final ComboBox cb = new ComboBox(null,Arrays.asList("X","LL","P"));
							cb.setImmediate(false);
							cb.setValue(lb.getValue());
							cb.setWidth("100%");
							cb.addBlurListener(new com.vaadin.event.FieldEvents.BlurListener() {
								
								@Override
								public void blur(BlurEvent event) {
									//al perder el focus repone el label con el valor correspondientes
									lb.setValue((String) cb.getValue());
									csLy.removeComponent(cb);
									csLy.addComponent(lb);
								}
							});
							csLy.addComponent(cb);
						}
					});
					return csLy;
				}

			});
			table1.setColumnHeader("day"+i, ((i%31)+1)+"");
			table1.setColumnWidth("day"+i, 50);
			visibleColumns[i - 24] = "day"+i; 
		}
		
		attendanceLaborerContainer = new BeanItemContainer(LaborerConstructionsite.class);
		attendanceLaborerContainer.addNestedContainerProperty("activeContract.jobCode");
		table1.setContainerDataSource(attendanceLaborerContainer);
		table1.setVisibleColumns(visibleColumns);

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
		attendanceLaborerContainer.removeAllItems();
		attendanceLaborerContainer.addAll(laborers);
		
	}
	

	private void showErrorParam() {
		Notification.show("Debe seleccionar una obra ",Type.ERROR_MESSAGE);
		//setea el titulo como vacio
		((MagalUI)UI.getCurrent()).getTitle().setValue("");
   	  	root.setEnabled(false);		
	}

}
