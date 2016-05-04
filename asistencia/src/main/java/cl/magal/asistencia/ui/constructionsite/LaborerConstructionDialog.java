
package cl.magal.asistencia.ui.constructionsite;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.velocity.app.VelocityEngine;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.vaadin.dialogs.ConfirmDialog;

import cl.magal.asistencia.entities.Accident;
import cl.magal.asistencia.entities.AdvancePaymentConfigurations;
import cl.magal.asistencia.entities.AdvancePaymentItem;
import cl.magal.asistencia.entities.Annexed;
import cl.magal.asistencia.entities.Contract;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.License;
import cl.magal.asistencia.entities.Loan;
import cl.magal.asistencia.entities.ProgressiveVacation;
import cl.magal.asistencia.entities.Speciality;
import cl.magal.asistencia.entities.Tool;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.Vacation;
import cl.magal.asistencia.entities.WithdrawalSettlement;
import cl.magal.asistencia.entities.enums.AccidentLevel;
import cl.magal.asistencia.entities.enums.LicenseType;
import cl.magal.asistencia.entities.enums.LoanToolStatus;
import cl.magal.asistencia.entities.enums.MaritalStatus;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.services.ConfigurationService;
import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.ui.AbstractWindowEditor;
import cl.magal.asistencia.ui.MagalUI;
import cl.magal.asistencia.ui.OnValueChangeFieldFactory;
import cl.magal.asistencia.ui.UndefinedWidthLabel;
import cl.magal.asistencia.ui.vo.HistoryVO;
import cl.magal.asistencia.util.Constants;
import cl.magal.asistencia.util.SecurityHelper;
import cl.magal.asistencia.util.Utils;
import cl.magal.asistencia.util.VelocityHelper;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class LaborerConstructionDialog extends AbstractWindowEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8280001172496734091L;
	transient Logger logger = LoggerFactory.getLogger(LaborerConstructionDialog.class);

	protected Button btnAddH, btnAddP;
	BeanItemContainer<HistoryVO> historyContainer = new BeanItemContainer<HistoryVO>(HistoryVO.class);
	BeanItemContainer<LaborerConstructionsite> constructionContainer = new BeanItemContainer<LaborerConstructionsite>(LaborerConstructionsite.class);
	BeanItemContainer<User> itemUser = new BeanItemContainer<User>(User.class);

	transient ConfigurationService configurationService;
	transient UserService serviceUser;
	transient LaborerService service;
	transient private VelocityEngine velocityEngine;
	LaborerConstructionsite laborerConstructionSite;

	private Validator validator;

	boolean readOnly = false;
	
	private String fullpath;

	public LaborerConstructionDialog(BeanItem<LaborerConstructionsite> item,boolean readOnly){
		super(item);
		this.readOnly = readOnly;
		init();
	}

	public LaborerConstructionDialog(BeanItem<LaborerConstructionsite> item){
		super(item);
		init();
	}

	public void init(){
		
		fullpath = (String) ((MagalUI)UI.getCurrent()).getSpringBean("uploaded_images_path");
		this.configurationService = (ConfigurationService) ((MagalUI)UI.getCurrent()).getSpringBean(Constants.CONFIGURATION_SERVICE_BEAN);
		this.velocityEngine = (VelocityEngine) ((MagalUI)UI.getCurrent()).getSpringBean(Constants.VELOCITY_ENGINE_BEAN);
		this.service = (LaborerService) ((MagalUI)UI.getCurrent()).getSpringBean(Constants.LABORER_SERVICE_BEAN);
		this.configurationService = (ConfigurationService) ((MagalUI)UI.getCurrent()).getSpringBean(Constants.CONFIGURATION_SERVICE_BEAN);
		this.validator = (Validator) ((MagalUI)UI.getCurrent()).getSpringBean(Constants.BEANVALIDATOR_BEAN);
		if(service == null )
			throw new RuntimeException("Error al crear el dialgo, el servicio de trabajadores no puede ser nulo.");

		getBtnGuardar().setVisible(!readOnly);
		if(readOnly)
			getBtnCancelar().setCaption("Cerrar");

		super.init();
		LaborerConstructionsite laborerConstructionsite = (LaborerConstructionsite) getItem().getBean();
		List<HistoryVO> history = service.getLaborerHistory(laborerConstructionsite.getLaborer());
		historyContainer.addAll(history);
		historyContainer.addNestedContainerProperty("constructionSite.name");
		//		Filter filter = new Compare.Equal("constructionSite", laborerConstructionsite.getConstructionsite());
		//		//filtra la obra en la que se encuentra
		//		historyContainer.addContainerFilter(new Not(filter));
	}

	TabSheet tab;
	@Override
	protected Component createBody() {

		tab = new TabSheet();
		tab.setSizeFull();

		//tab de Resumen
		if(!readOnly)
			tab.addTab(drawSummary(),"Resumen");
		//tab de Información
		if(!readOnly)
			tab.addTab(drawInformation(),"Información");
		//tab de vacaciones
		tab.addTab(drawVacations(),"Vacaciones");
		//tab de prestamos y herramientas
		tab.addTab(drawPyH(),"Préstamos/Herramientas");
		//tab de retiros de finiquito
		tab.addTab(drawW(),"Retiro de finiquito");
		//tab de accidentes y licencias
		tab.addTab(drawAccidents(),"Accidentes");
		//tab de accidentes y licencias
		tab.addTab(drawLicencias(),"Licencias");
		//tab de contratos y finiquitos
		tab.addTab(drawCyF(),"Contratos/Finiquitos");
		//tab de histórico
		if(!readOnly)
			tab.addTab(drawHistorico(),"Histórico");

		return tab;
	}

	private Component drawInformation() {

		GridLayout gl  = new GridLayout(3,9);
		gl.setWidth("100%");
		gl.setSpacing(true);

		gl.addComponent(new Label("<h2>Información de Obra : </h2>",ContentMode.HTML),0,0,2,0);

		final DateField startDate = new DateField("Fecha Inicial",getItem().getItemProperty("rewardStartDate"));
		startDate.setImmediate(true);
		final DateField endDate = new DateField("Fecha Final",getItem().getItemProperty("rewardEndDate"));
		endDate.setImmediate(true);

		// codigo por asignar
		final ComboBox cbSupleCode = new ComboBox("Código Suple");
		cbSupleCode.setRequired(true);
		AdvancePaymentConfigurations supleConfigurations = configurationService.getSupleTableByCs(((BeanItem<LaborerConstructionsite>) getItem()).getBean().getConstructionsite());
		Map<Integer, AdvancePaymentItem> paymentTable = supleConfigurations.getMapTable();
		for(Integer key : paymentTable.keySet()){
			cbSupleCode.addItem(key);
		}
		cbSupleCode.select(getItem().getItemProperty("supleCode").getValue());		
		cbSupleCode.addValueChangeListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				getItem().getItemProperty("supleCode").setValue(cbSupleCode.getValue());
			}
		});
				
		VerticalLayout vl = new VerticalLayout(){
			{
				setSpacing(true);
				TextField tfReward = new TextField("Premio: ",getItem().getItemProperty("reward")){{
					setNullRepresentation("");
					addValidator(new BeanValidator(LaborerConstructionsite.class,"reward"));
				}};
				addComponent(tfReward);
				setComponentAlignment(tfReward, Alignment.MIDDLE_CENTER);
				CheckBox checkbox = new CheckBox("Aplicar premio según fechas de contrato",getItem().getItemProperty("useDefaultDates"));
				startDate.setVisible(!checkbox.getValue());
				endDate.setVisible(!checkbox.getValue());
				checkbox.addValueChangeListener(new Property.ValueChangeListener() {

					@Override
					public void valueChange(ValueChangeEvent event) {
						boolean ischecked = (Boolean) event.getProperty().getValue();
						startDate.setVisible(!ischecked);
						endDate.setVisible(!ischecked);
						if(ischecked){
							startDate.setValue(null);
							endDate.setValue(null);
						}
					}
				});
				addComponent(checkbox);

			}
		};

		gl.addComponent( vl,0,1);
		gl.setComponentAlignment(vl, Alignment.MIDDLE_CENTER);
		gl.addComponent(startDate,1,1);
		gl.setComponentAlignment(startDate, Alignment.TOP_CENTER);
		gl.addComponent(endDate,2,1);
		gl.setComponentAlignment(endDate, Alignment.TOP_CENTER);
		gl.addComponent(cbSupleCode,0,2);
		gl.setComponentAlignment(cbSupleCode, Alignment.TOP_CENTER);
		//si el usuario tiene permisos para cambiar el rol
		if(SecurityHelper.hasPermission(Permission.AGREGAR_CUENTAS_COSTO)){
			final TextField jobCode = new TextField("Codigo en Obra",getItem().getItemProperty("activeContract.jobCode"));
			gl.addComponent(jobCode,0,3);
			gl.setComponentAlignment(jobCode, Alignment.TOP_CENTER);
		}

		gl.addComponent(new LaborerBaseInformation(getBinder(),"laborer",true),0,4,2,4);

		return gl;
	}

	protected Component drawSummary() {	

		GridLayout gl = new GridLayout(3,10);
		gl.setSpacing(true);
		gl.setMargin(true);
		gl.setWidth("100%");

		gl.addComponent( new HorizontalLayout(){
			{
				try{

					String photoFileName = (String) getItem().getItemProperty("laborer.photo").getValue();
					if(photoFileName != null && !photoFileName.trim().isEmpty() ){
						// Image as a file resource
						FileResource resource = new FileResource(new File(fullpath + getItem().getItemProperty("laborer.photo").getValue()));

						Embedded image = new Embedded("", resource);
						image.setWidth("350");
						image.setHeight("400");
						addComponent(image);       
						setComponentAlignment(image, Alignment.TOP_LEFT);
					}
				}catch(Exception e){ logger.error("Error",e); /*FIXME falla silenciosamente*/ }

				setSpacing(true);

				GridLayout gl = new GridLayout(2,10);
				addComponent(gl);
				setComponentAlignment(gl, Alignment.MIDDLE_CENTER);
				//gl.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);

				gl.addComponent(new Label("Rol :   "));gl.addComponent(new Label(getItem().getItemProperty("activeContract.jobCode")));
				gl.addComponent(new Label("Oficio :   "));gl.addComponent(new Label(getItem().getItemProperty("activeContract.job")));
				Speciality speciality = (Speciality)getItem().getItemProperty("activeContract.speciality").getValue();
				gl.addComponent(new Label("Especialidad :   "));gl.addComponent(new Label( speciality != null ? speciality.getName(): ""));
				gl.addComponent(new Label("Nombre :   "));gl.addComponent(new Label(getItem().getItemProperty("laborer.fullname")));
				gl.addComponent(new Label("Rut :   "));gl.addComponent(new Label(getItem().getItemProperty("laborer.rut")));
				gl.addComponent(new Label("Fecha Nacimiento :   "));gl.addComponent(new Label( getItem().getItemProperty("laborer.dateBirthString") ));
				String marital = MaritalStatus.CASADO.toString();
				try{
					marital = ((MaritalStatus)getItem().getItemProperty("laborer.maritalStatus").getValue()).toString();
				}catch(Exception e){
					logger.error("Error",e);
				}

				gl.addComponent(new Label("Estado Civil : "));gl.addComponent(new Label(marital));
				gl.addComponent(new Label("Dirección : "));gl.addComponent(new Label(getItem().getItemProperty("laborer.address")));

				if(getItem().getItemProperty("laborer.mobileNumber").getValue() != null || getItem().getItemProperty("laborer.phone").getValue() != null){
					gl.addComponent(new Label("Teléfonos : "));
					gl.addComponent(new Label(getItem().getItemProperty("laborer.mobileNumber").getValue() +" - "+getItem().getItemProperty("laborer.phone").getValue()));
				}

				if(getItem().getItemProperty("activeContract.startDate").getValue() != null){
					gl.addComponent(new Label("Fecha Ingreso : "));
					gl.addComponent(new Label(getItem().getItemProperty("activeContract.startDateString")));
				}
				gl.addComponent(new Label("Premio : "));gl.addComponent(new Label(getItem().getItemProperty("reward").getValue()+""));

				if(getItem().getItemProperty("laborer.dependents").getValue() != null){
					gl.addComponent(new Label("Cargas : "));gl.addComponent(new Label(getItem().getItemProperty("laborer.dependents").getValue()+""));
				}
				setSpacing(true);
				//				POR AHORA OCULTAR ESTE BOTON HASTA QUE SE DEFINA BIEN QUE HARA Y SI VA
				//				Button btnPrint = new Button(null,new Button.ClickListener() {
				//
				//					@Override
				//					public void buttonClick(ClickEvent event) {
				//						Notification.show("Imprimiendo");
				//					}
				//				}){{setIcon(FontAwesome.PRINT); setDescription("Imprimir");}};
				//				addComponent(btnPrint);

				if( SecurityHelper.hasPermission(Permission.BLOQUEAR_OBRERO)){
					Button bloquear = new Button(null,FontAwesome.LOCK);					
					bloquear.addClickListener(new Button.ClickListener() {
						public void buttonClick(ClickEvent event) {								

							LaborerBlockDialog lbWindow = new LaborerBlockDialog(getItem(), service, velocityEngine);
							lbWindow.setCaption("Bloquear Trabajador");
							lbWindow.addListener(new AbstractWindowEditor.EditorSavedListener() {							

								@Override
								public void editorSaved(EditorSavedEvent event) {
									try {										
										LaborerConstructionsite lc = ((BeanItem<LaborerConstructionsite>) event.getSavedItem()).getBean();
										lc.setPersonBlock((User) VaadinSession.getCurrent().getAttribute(Constants.SESSION_USUARIO));
										lc.setBlock(true);
										service.save(lc);
										constructionContainer.addBean(lc);
									} catch (Exception e) {
										logger.error("Error al guardar la información de la obra",e);
										Notification.show("Es necesario agregar todos los campos obligatorios", Type.ERROR_MESSAGE);
									}

								}
							});

							UI.getCurrent().addWindow(lbWindow);
						}
					});		
					bloquear.setData(constructionContainer);
					bloquear.setDescription("Bloquear");
					addComponent(bloquear);
				}

				if( SecurityHelper.hasPermission(Permission.CONFIRMAR_OBREROS)){
					Button acceptObrero = new Button(null,FontAwesome.CHECK);					
					acceptObrero.addClickListener(new Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							LaborerConstructionsite laborer = (LaborerConstructionsite) getItem().getBean();
							if(laborer == null ) 
								throw new RuntimeException("El trabajador no es válido.");
							laborer.setConfirmed(true);
							service.save(laborer);
							Notification.show("Trabajador confirmado.");
						}
					});		
					acceptObrero.setDescription("Confirmar");
					addComponent(acceptObrero);
				}
			}
		},0,0,1,0);

		return gl;
	}

	BeanItemContainer<Tool> beanItemTool;
	BeanItemContainer<Loan> beanItemLoan;
	protected VerticalLayout drawPyH() {

		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setWidth("100%");

		/********** Herramientas **********/
		VerticalLayout vh = new VerticalLayout();
		vh.setWidth("100%");

		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setSpacing(true);		
		vh.addComponent(hl);

		beanItemTool = new BeanItemContainer<Tool>(Tool.class);
		beanItemTool.addNestedContainerProperty("status.description");
		List<Tool> tools = (List<Tool>)getItem().getItemProperty("tool").getValue();
		beanItemTool.addAll(tools);

		if(!readOnly){
			btnAddH = new Button(null,FontAwesome.PLUS);
			hl.addComponent(btnAddH);
			hl.setComponentAlignment(btnAddH, Alignment.MIDDLE_RIGHT);

			btnAddH.addClickListener(new Button.ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					LaborerConstructionsite laborer = (LaborerConstructionsite) getItem().getBean();
					if(laborer == null ) throw new RuntimeException("El trabajador no es válido.");
					Tool tool = new Tool();
					tool.setStatus(LoanToolStatus.EN_DEUDA);
					laborer.addTool(tool);
					beanItemTool.addBean(tool);
				}
			});	
		}

		final Table tableTool = new Table("Herramientas");
		tableTool.setPageLength(3);
		tableTool.setWidth("100%");
		tableTool.setContainerDataSource(beanItemTool);
		tableTool.setImmediate(true);
		tableTool.setTableFieldFactory(new DefaultFieldFactory(){

			public Field<?> createField(final Container container,
					final Object itemId, Object propertyId, com.vaadin.ui.Component uiContext) {
				Field<?> field = null; 
				if( propertyId.equals("name") || propertyId.equals("price") || propertyId.equals("fee")){
					field = new TextField();
					((TextField)field).setNullRepresentation("");
					((TextField)field).setConversionError("Número inválido");
					((TextField)field).setImmediate(true);
				}
				else  if( propertyId.equals("status") ){
					field = new TextField();
					((TextField)field).setValue("En deuda");
					((TextField)field).setImmediate(true);
					field.setEnabled(false);
				}
//				else if(  propertyId.equals("dateBuy") ){
//					field = new DateField();
//					((DateField)field).setImmediate(true);
//				}

				return field;
			}
		});

		tableTool.addGeneratedColumn("selected", new Table.ColumnGenerator() {

			@Override
			public Component generateCell(Table source, final Object itemId, Object columnId) {
				BeanItem<Tool> toolBean = beanItemTool.getItem(itemId);
				final CheckBox tcb = new CheckBox("",toolBean.getItemProperty("postponed"));
				tcb.setImmediate(true);
				Date firstDayOfCurrentMonth = new DateTime().dayOfMonth().withMinimumValue().toDate();
				if(  Utils.containsMonth(toolBean.getBean().getDatePostponed(), firstDayOfCurrentMonth) ){                
					tcb.setValue(true);
				}else{
					tcb.setValue(false);
				}
				return tcb;

			}
		});

		tableTool.addGeneratedColumn("eliminar", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				return new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar la herramienta seleccionada?",
								"Eliminar", "Cancelar", new ConfirmDialog.Listener() {

							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									beanItemTool.removeItem(itemId);
								}
							}
						});
					}
				}){{setIcon(FontAwesome.TRASH_O);}};
			}
		});

		tableTool.addGeneratedColumn("dateBuy", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				BeanItem<Tool> toolBean = beanItemTool.getItem(itemId);
				Date value = (Date) source.getContainerProperty(itemId, columnId).getValue();	
				
				final DateField fecha  = new DateField("Fecha"+value);		
				fecha.setPropertyDataSource(toolBean.getItemProperty("dateBuy"));
				fecha.addValueChangeListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						DateTime dateTime = new DateTime((Date)event.getProperty().getValue());
						Integer month = dateTime.getMonthOfYear();
						
						DateTime today = new DateTime();
						Integer month_today = today.getMonthOfYear();
						
						if(!month_today.equals(month)){
							Notification.show("Solo puede seleccionar un día del mes actual.", Type.ERROR_MESSAGE);
							
							return;
						}					
					}
				});

				return fecha; 
			}
		});

		
		tableTool.setVisibleColumns("name","price","dateBuy","fee","selected","status.description", "eliminar");
		tableTool.setColumnHeaders("Herramienta","Monto","Fecha","Cuota","Postergar pago","Estado", "Acciones");
		tableTool.setEditable(true);				
		vh.addComponent(tableTool);
		vh.setComponentAlignment(hl, Alignment.TOP_RIGHT);

		/********** Préstamo **********/
		VerticalLayout vp = new VerticalLayout();
		vp.setSizeFull();

		HorizontalLayout hl2 = new HorizontalLayout();
		hl2.setWidth("100%");
		hl2.setSpacing(true);		
		vp.addComponent(hl2);

		beanItemLoan = new BeanItemContainer<Loan>(Loan.class);
		beanItemLoan.addNestedContainerProperty("status.description");
		List<Loan> loans = (List<Loan>)getItem().getItemProperty("loan").getValue();
		logger.debug("cargando loans {}",loans);
		beanItemLoan.addAll(loans);
		logger.debug("items {}",beanItemLoan.getItemIds());

		if(!readOnly){
			btnAddP = new Button(null,FontAwesome.PLUS);
			hl2.addComponent(btnAddP);
			hl2.setComponentAlignment(btnAddP, Alignment.MIDDLE_RIGHT);

			btnAddP.addClickListener(new Button.ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					LaborerConstructionsite laborer = (LaborerConstructionsite) getItem().getBean();
					if(laborer == null ) throw new RuntimeException("El trabajador no es válido.");
					Loan loan = new Loan();
					loan.setStatus(LoanToolStatus.EN_DEUDA);
					laborer.addLoan(loan);
					beanItemLoan.addBean(loan);
				}
			});
		}

		final Table tableLoan = new Table("Préstamos");
		tableLoan.setPageLength(3);
		tableLoan.setWidth("100%");
		tableLoan.setContainerDataSource(beanItemLoan);
		tableLoan.setImmediate(true);		
		tableLoan.setTableFieldFactory(new DefaultFieldFactory(){

			public Field<?> createField(final Container container,
					final Object itemId,Object propertyId,com.vaadin.ui.Component uiContext) {
				Field<?> field = null; 
				if( propertyId.equals("price") || propertyId.equals("fee")){
					field = new TextField();
					((TextField)field).setNullRepresentation("");
					((TextField)field).setConversionError("Número inválido");
				}
				else  if( propertyId.equals("status") ){
					field = new TextField();
					((TextField)field).setNullRepresentation("En deuda");
					field.setEnabled(false);
				}
				else if(  propertyId.equals("dateBuy") ){
					field = new DateField();
				}
				else {
					return null;
				}
				return field;
			}
		});

		tableLoan.addGeneratedColumn("selected", new Table.ColumnGenerator() {

			@Override
			public Component generateCell(Table source, Object itemId, Object columnId) {
				BeanItem<Loan> loanBean = beanItemLoan.getItem(itemId);
				final CheckBox lcb = new CheckBox("", loanBean.getItemProperty("postponed"));
				lcb.setImmediate(true);
				Date firstDayOfCurrentMonth = new DateTime().dayOfMonth().withMinimumValue().toDate();
				if(  Utils.containsMonth(loanBean.getBean().getDatePostponed(), firstDayOfCurrentMonth) ){                
					lcb.setValue(true);
				}else{
					lcb.setValue(false);
				}
				return lcb;
			}
		});

		tableLoan.addGeneratedColumn("eliminar", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				return new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar el préstamo seleccionado?",
								"Eliminar", "Cancelar", new ConfirmDialog.Listener() {

							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									beanItemLoan.removeItem(itemId);
								}
							}
						});
					}
				}){{setIcon(FontAwesome.TRASH_O);}};
			}
		});

		tableLoan.addGeneratedColumn("dateBuy", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				BeanItem<Loan> loanBean = beanItemLoan.getItem(itemId);
				Date value = (Date) source.getContainerProperty(itemId, columnId).getValue();	
				
				final DateField fecha  = new DateField("Fecha"+value);		
				fecha.setPropertyDataSource(loanBean.getItemProperty("dateBuy"));
				fecha.addValueChangeListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						DateTime dateTime = new DateTime((Date)event.getProperty().getValue());
						Integer month = dateTime.getMonthOfYear();
						
						DateTime today = new DateTime();
						Integer month_today = today.getMonthOfYear();
						
						if(!month_today.equals(month)){
							Notification.show("Solo puede seleccionar un día del mes actual.", Type.ERROR_MESSAGE);
							
							return;
						}					
					}
				});

				return fecha; 
			}
		});

		tableLoan.setVisibleColumns("price","dateBuy", "fee", "selected","status.description", "eliminar");
		tableLoan.setColumnHeaders("Monto","Fecha", "Cuota", "Postergar pago", "Estado", "Eliminar");
		tableLoan.setEditable(true);		
		vp.addComponent(tableLoan);
		vp.setComponentAlignment(hl2, Alignment.TOP_RIGHT);

		vl.addComponent(vh);
		vl.addComponent(vp);

		return vl;
	}
	
	BeanItemContainer<WithdrawalSettlement> beanItemWithdrawalSettlement;
	protected VerticalLayout drawW() {

		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setWidth("100%");

		/********** Herramientas **********/
		VerticalLayout vh = new VerticalLayout();
		vh.setWidth("100%");

		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setSpacing(true);		
		vh.addComponent(hl);

		beanItemWithdrawalSettlement = new BeanItemContainer<WithdrawalSettlement>(WithdrawalSettlement.class);
		List<WithdrawalSettlement> tools = (List<WithdrawalSettlement>)getItem().getItemProperty("withdrawalSettlements").getValue();
		beanItemWithdrawalSettlement.addAll(tools);

		if(!readOnly){
			Button btnAddH = new Button(null,FontAwesome.PLUS);
			hl.addComponent(btnAddH);
			hl.setComponentAlignment(btnAddH, Alignment.MIDDLE_RIGHT);

			btnAddH.addClickListener(new Button.ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					LaborerConstructionsite laborer = (LaborerConstructionsite) getItem().getBean();
					if(laborer == null ) throw new RuntimeException("El trabajador no es válido.");
					WithdrawalSettlement tool = new WithdrawalSettlement();
					laborer.addWithdrawalSettlement(tool);
					beanItemWithdrawalSettlement.addBean(tool);
				}
			});	
		}

		final Table tableTool = new Table("Retiros");
		tableTool.setPageLength(3);
		tableTool.setWidth("100%");
		tableTool.setContainerDataSource(beanItemWithdrawalSettlement);
		tableTool.setImmediate(true);
		tableTool.setTableFieldFactory(new DefaultFieldFactory(){

			public Field<?> createField(final Container container,
					final Object itemId, Object propertyId, com.vaadin.ui.Component uiContext) {
				Field<?> field = null; 
				if( propertyId.equals("price") ){
					field = new TextField();
					((TextField)field).setNullRepresentation("");
					((TextField)field).setImmediate(true);
				}

				return field;
			}
		});

		tableTool.addGeneratedColumn("eliminar", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				return new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar el retiro seleccionado?",
								"Eliminar", "Cancelar", new ConfirmDialog.Listener() {

							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									beanItemWithdrawalSettlement.removeItem(itemId);
								}
							}
						});
					}
				}){{setIcon(FontAwesome.TRASH_O);}};
			}
		});

		tableTool.setVisibleColumns("price","eliminar");
		tableTool.setColumnHeaders("Monto","Acciones");
		tableTool.setEditable(true);				
		vh.addComponent(tableTool);
		vh.setComponentAlignment(hl, Alignment.TOP_RIGHT);

		vl.addComponent(vh);
		return vl;
	}

	protected Component drawCyF() {

		final Contract activeContract = ((LaborerConstructionsite)getItem().getBean()).getActiveContract();

		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setWidth("100%");

		final GridLayout gl = new GridLayout(3,10);
		gl.setSpacing(true);
		gl.setWidth("100%");
		gl.setSizeFull();

		gl.setColumnExpandRatio(0, 0.2F);
		gl.setColumnExpandRatio(1, 0.2F);
		gl.setColumnExpandRatio(2, 1.0F);

		vl.addComponent(gl);

		final BeanItemContainer<Annexed> beanContainerAnnexeds = new BeanItemContainer<Annexed>(Annexed.class); 

		int fila = 0, columna = 0;
		gl.addComponent(new UndefinedWidthLabel("<h1>Contrato</h1>",ContentMode.HTML),columna++,fila);
		gl.addComponent( new HorizontalLayout(){
			{
				setSpacing(true);
				setWidth("100%");

				Button btnPrint = new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {

						final Map<String, Object> input = new HashMap<String, Object>();
						input.put("laborerConstructions", new LaborerConstructionsite[] {(LaborerConstructionsite)getItem().getBean()});
						String jornalBase = Utils.getDecimalFormatSinDecimal().format(configurationService.findWageConfigurations().getMinimumWage() / 30);
						input.put("jornalBase", jornalBase);
						VelocityHelper.addTools(input);

						final StringBuilder sb = new StringBuilder();

						// contrato
						sb.append( VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/temporary_work_contract_doc.vm", "UTF-8", input));
						// pacto horas extras
						sb.append( VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/covenant_overtime.vm", "UTF-8", input) );
						// acuse recibo
						sb.append( VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/acknowledgment_of_receipt.vm", "UTF-8", input) );

						StreamResource.StreamSource source2 = new StreamResource.StreamSource() {

							public InputStream getStream() {
								return new ByteArrayInputStream(sb.toString().getBytes());
							}
						};
						StreamResource resource = new StreamResource(source2, "Contrato"+((LaborerConstructionsite)getItem().getBean()).getJobCode()+".html");

						Window window = new Window();
						window.setResizable(true);
						window.setWidth("60%");
						window.setHeight("60%");
						window.center();
						window.setModal(true);
						BrowserFrame e = new BrowserFrame();
						e.setSizeFull();

						// Here we create a new StreamResource which downloads our StreamSource,
						// which is our pdf.
						// Set the right mime type
						//						        resource.setMIMEType("application/pdf");
						resource.setMIMEType("text/html");

						e.setSource(resource);
						window.setContent(e);
						UI.getCurrent().addWindow(window);
					}
				}){{setIcon(FontAwesome.PRINT); setDescription("Imprimir");}};
				addComponent(btnPrint);
				setComponentAlignment(btnPrint, Alignment.MIDDLE_CENTER);

			}
		},2,fila++);

		lbStep = new UndefinedWidthLabel();
		lbJob = new UndefinedWidthLabel();
		lbJobCode = new UndefinedWidthLabel();
		lbStarting = new UndefinedWidthLabel();
		lbEnding = new UndefinedWidthLabel();
		lbSettlement = new UndefinedWidthLabel();
		lbStatus = new UndefinedWidthLabel();
		columna = 0;

		gl.addComponent(new UndefinedWidthLabel("Etapa : "),columna++,fila);gl.addComponent(lbStep,columna--,fila++);
		gl.addComponent(new UndefinedWidthLabel("Oficio : "),columna++,fila);gl.addComponent(lbJob,columna--,fila++);
		gl.addComponent(new UndefinedWidthLabel("Rol : "),columna++,fila);gl.addComponent(lbJobCode,columna--,fila++);
		gl.addComponent(new UndefinedWidthLabel("Fecha Inicio : "),columna++,fila);gl.addComponent(lbStarting,columna--,fila++);
		gl.addComponent(new UndefinedWidthLabel("Fecha Termino : "),columna++,fila);gl.addComponent(lbEnding,columna--,fila++);

		final Button btnFinishContract = new Button(null,FontAwesome.TIMES);
		btnFinishContract.setDescription("Término de Contrato");
		btnFinishContract.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				//permite imprimir la carta de renuncia
				final Window w = new Window("Cartas de renuncias");
				w.center();
				w.setModal(true);

				w.setContent(new VerticalLayout(){
					{

						setSpacing(true);
						setMargin(true);
						final OptionGroup og = new OptionGroup("Tipo de Término",
								Arrays.asList("Voluntaria",
										"Término de Contrato",
										"Ausencia Reiterada"));
						addComponent(og);

						addComponent(new HorizontalLayout(){
							{
								// boton aceptar
								addComponent(new Button("Aceptar",new Button.ClickListener() {

									@Override
									public void buttonClick(ClickEvent event) {

										if( og.getValue() == null ){
											Notification.show("Debe seleccionar una causa de término.",Type.WARNING_MESSAGE);
											return;
										}

										final Map<String, Object> input = new HashMap<String, Object>();
										input.put("laborerConstructions", new LaborerConstructionsite[] {(LaborerConstructionsite)getItem().getBean()});
										VelocityHelper.addTools(input);

										final StringBuilder sb = new StringBuilder();
										if(((String) og.getValue()).compareTo("Voluntaria") == 0){
											sb.append( VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/voluntary_resignation_letter.vm", "UTF-8", input) );
										}else if(((String) og.getValue()).compareTo("Término de Contrato") == 0){
											sb.append( VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/dismissal_letter_for_completion_of_work.vm", "UTF-8", input) );
										}else if(((String) og.getValue()).compareTo("Ausencia Reiterada") == 0){
											sb.append( VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/dismissal_letter_for_absence.vm", "UTF-8", input) );
										}

										StreamResource.StreamSource source2 = new StreamResource.StreamSource() {

											public InputStream getStream() {
												//throw new UnsupportedOperationException("Not supported yet.");
												return new ByteArrayInputStream(sb.toString().getBytes());
											}
										};
										StreamResource resource = new StreamResource(source2, (String) og.getValue());

										BrowserFrame e = new BrowserFrame();
										e.setSizeFull();

										// Here we create a new StreamResource which downloads our StreamSource,
										// which is our pdf.
										// Set the right mime type
										//						        resource.setMIMEType("application/pdf");
										resource.setMIMEType("text/html");

										e.setSource(resource);
										w.setContent(e);
										w.center();
										w.setWidth("60%");
										w.setHeight("60%");

										activeContract.setTerminationDate(new Date());
										activeContract.setFinished(true);
										setContractGl(activeContract);
									}
								}){ {setIcon(FontAwesome.CHECK_CIRCLE_O);} } );

								// boton aceptar
								addComponent(new Button("Cancelar",new Button.ClickListener() {

									@Override
									public void buttonClick(ClickEvent event) {
										w.close();
									}
								}){{addStyleName("link");}});
							}
						});
					}
				});

				UI.getCurrent().addWindow(w);
			}
		});
		gl.addComponent(new UndefinedWidthLabel("Estado : "),columna++,fila);gl.addComponent(lbStatus,columna++,fila);gl.addComponent(btnFinishContract,columna++,fila++);
		columna = 0;

		final Button btnSettlement = new Button(null,FontAwesome.FILE_TEXT);
		btnSettlement.setDescription("Cálcular Finiquito");
		btnSettlement.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				//solo puede calcular el finiquito, si el contrato no está activo
				if(!activeContract.isFinished()){
					Notification.show("El contrato debe estár terminado para calcular el finiquito",Type.HUMANIZED_MESSAGE);
					return;
				}

				final Window w = new Window("Finiquito");
				w.center();
				w.setModal(true);
				
				w.setWidth("60%");
				w.setHeight("60%");
				
				w.setContent(new VerticalLayout(){
					{

						setSpacing(true);
						setMargin(true);
						final TextField tfFailureDiscount = new TextField("Descuento por Falla",getItem().getItemProperty("failureDiscount"));
						tfFailureDiscount.setImmediate(true);
						addComponent(tfFailureDiscount);
						
						final TextField tfOtherDiscount = new TextField("Otros descuentos",getItem().getItemProperty("othersDiscount"));
						tfOtherDiscount.setImmediate(true);
						addComponent(tfOtherDiscount);

						addComponent(new HorizontalLayout(){
							{
								// boton aceptar
								addComponent(new Button("Aceptar",new Button.ClickListener() {

									@Override
									public void buttonClick(ClickEvent event) {

										if( !Utils.NotNullOrEmpty(tfFailureDiscount.getValue()) || 
											!Utils.NotNullOrEmpty(tfOtherDiscount.getValue()) ){
											Notification.show("Debe ingresar los montos de descuentos.",Type.WARNING_MESSAGE);
											return;
										}

										//agrega el finiquito
										LaborerConstructionsite lc = (LaborerConstructionsite)getItem().getBean();
										//deja desactivo tanto el contrato como el laborer constructionsite
										activeContract.setActive(false);
										lc.setActive(false);
										
										//setea un finiquito calculado
										final Map<String, Object> input = new HashMap<String, Object>();
										input.put("laborerConstructions", new LaborerConstructionsite[] { lc });
										VelocityHelper.addTools(input);
										

										activeContract.setSettlement(calculateSettlement(lc,input));
										setContractGl(activeContract);

										//muestra el finiquito
										final StringBuilder sb = new StringBuilder();
										sb.append( VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/settler.vm", "UTF-8", input) );
										
										StreamResource.StreamSource source2 = new StreamResource.StreamSource() {

											public InputStream getStream() {
												//throw new UnsupportedOperationException("Not supported yet.");
												return new ByteArrayInputStream(sb.toString().getBytes());
											}
										};
										StreamResource resource = new StreamResource(source2, "Finiquito.txt");

										BrowserFrame e = new BrowserFrame();
										e.setSizeFull();

										// Here we create a new StreamResource which downloads our StreamSource,
										// which is our pdf.
										// Set the right mime type
										//						        resource.setMIMEType("application/pdf");
										resource.setMIMEType("text/html");

										e.setSource(resource);
										w.setContent(e);
									}
								}){ {setIcon(FontAwesome.CHECK_CIRCLE_O);} } );

								// boton aceptar
								addComponent(new Button("Cancelar",new Button.ClickListener() {

									@Override
									public void buttonClick(ClickEvent event) {
										w.close();
									}
								}){{addStyleName("link");}});
							}
						});
					}
				});
				
				UI.getCurrent().addWindow(w);

			}
		});

		gl.addComponent(new UndefinedWidthLabel("Finiquito : "),columna++,fila);gl.addComponent(lbSettlement,columna++,fila);gl.addComponent(btnSettlement,columna++,fila++);
		setContractGl(activeContract);

		GridLayout gl2 = new GridLayout(2,10);
		gl2.setSpacing(true);
		gl2.setSizeFull();
		vl.addComponent(gl2);

		columna = 0; fila = 0; 

		gl2.addComponent(new Label("<hr />",ContentMode.HTML),columna++,fila,columna--,fila++);

		beanContainerAnnexeds.addAll((Collection<? extends Annexed>) activeContract.getAnnexeds());

		gl2.addComponent(new Label("<h1>Anexos</h1>",ContentMode.HTML),columna++,fila);
		if(!readOnly)
			gl2.addComponent( new HorizontalLayout(){
				{
					setWidth("100%");
					//					Button AgregarAnexo = new Button(null,new Button.ClickListener() {
					//
					//						@Override
					//						public void buttonClick(ClickEvent event) {
					//
					//							AddAnnexedContractDialog userWindow = new AddAnnexedContractDialog(new BeanItem<Contract>(((LaborerConstructionsite)getItem().getBean()).getActiveContract()));
					//							userWindow.addListener(new AbstractWindowEditor.EditorSavedListener() {
					//
					//								@Override
					//								public void editorSaved(EditorSavedEvent event) {
					//									try {
					//										//TODO definir si guardará o no el estado del laborer en esta etapa
					//										BeanItem<Contract>	newBeanItem = (BeanItem<Contract>) event.getSavedItem();
					//
					//										beanContainerAnnexeds.removeAllItems();
					//										beanContainerAnnexeds.addAll((Collection<? extends Annexed>) newBeanItem.getItemProperty("annexeds").getValue());
					//
					//									} catch (Exception e) {
					//										logger.error("Error al guardar la información del obrero",e);
					//										Notification.show("Es necesario agregar todos los campos obligatorios", Type.ERROR_MESSAGE);
					//									}
					//								}
					//							});
					//
					//							UI.getCurrent().addWindow(userWindow);
					//						}
					//					}){{setIcon(FontAwesome.PLUS);}};
					//					addComponent(AgregarAnexo);
					//					setComponentAlignment(AgregarAnexo, Alignment.MIDDLE_CENTER);
				}
			},columna--,fila++);
		else{ columna--;fila++;}


		Table annexedTable = new Table(null,beanContainerAnnexeds){
			{
				setWidth("100%");
				setPageLength(5);
			}
		};
		annexedTable.addGeneratedColumn("print", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				return new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {

						final Map<String, Object> input = new HashMap<String, Object>();
						input.put("laborerConstructions", new LaborerConstructionsite[] {(LaborerConstructionsite)getItem().getBean()});
						VelocityHelper.addTools(input);

						final String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/annex_contract_doc.vm", "UTF-8", input);

						StreamResource.StreamSource source2 = new StreamResource.StreamSource() {

							public InputStream getStream() {
								return new ByteArrayInputStream(body.getBytes());
							}
						};
						StreamResource resource = new StreamResource(source2, "Anexo"+((LaborerConstructionsite)getItem().getBean()).getJobCode()+".html");

						Window window = new Window();
						window.setResizable(true);
						window.setWidth("60%");
						window.setHeight("60%");
						window.center();
						window.setModal(true);
						BrowserFrame e = new BrowserFrame();
						e.setSizeFull();

						// Here we create a new StreamResource which downloads our StreamSource,
						// which is our pdf.
						// Set the right mime type
						//						        resource.setMIMEType("application/pdf");
						resource.setMIMEType("text/html");

						e.setSource(resource);
						window.setContent(e);
						UI.getCurrent().addWindow(window);
					}
				}){ { setIcon(FontAwesome.PRINT);}};
			}
		});

		annexedTable.setVisibleColumns("step","startDate","terminationDate"//,"print"
				);
		annexedTable.setColumnHeaders("Etapa","Fecha de inicio","Fecha de termino"//,"Imprimir"
				);

		gl2.addComponent(annexedTable,columna++,fila,columna--,fila++);
		return vl;
	}

	/**
	 * Calcula el finiquito
	 * Mes por Año = 30 * Promedio Jornal * codigo (1) * cantidad (0) +
	 * Desahucio = 30 * Promedio Jornal * codigo (1) * cantidad (0) +
	 * Calculo Vacaciones = Años Duracion Contrato * 12 *1.75 * Promedio Jornal +
	 * Vacaciones Efectivas = -1 *  vacaciones tomadas * Promedio Jornal * 1.4
	 *
	 * @param lc
	 * @param input
	 * @return
	 */
	protected double calculateSettlement(LaborerConstructionsite lc,Map<String, Object> input) {

		//TODO
		List<Double> last3JornalPromedio = service.getJornalPromedioLastThreeMonth(lc,lc.getActiveContract().getTerminationDate());
		input.put("ultimosJornales", last3JornalPromedio );
		DateTime dt = new DateTime(lc.getActiveContract().getTerminationDate());
		input.put("firstmonth", dt.toDate() );
		input.put("secondmonth", dt.minusMonths(1).toDate() );
		input.put("threemonth", dt.minusMonths(2).toDate() );
		
		double JornalPromedio = Utils.avg( last3JornalPromedio ) ;
		input.put("promedioJornales", JornalPromedio);
		
		logger.debug("JornalPromedio {}",JornalPromedio);
		
		Period period = new Period(new DateTime(lc.getActiveContract().getStartDate()), new DateTime(lc.getActiveContract().getTerminationDate()));
		//TODO
		double AnoDuracionContrato = period.getYears() + (period.getMonths() - (period.getYears() * 12)) / 12;
		input.put("duracionContrato", AnoDuracionContrato);
		
		logger.debug("AnoDuracionContrato {}",AnoDuracionContrato);
		double vacacionesTotales = AnoDuracionContrato * 12 * 1.75;
		input.put("vacacionesTotales", vacacionesTotales);
		
		double vacacionesTomadas = calcularUsadas(vacationContainer.getItemIds());
		input.put("vacacionesTomadas", vacacionesTomadas);
		logger.debug("vacacionesTomadas {}",vacacionesTomadas);
		
		double totalPremio = lc.getReward() * period.getMonths();
		input.put("totalPremio", totalPremio );
		
		double Vacaciones = vacacionesTotales * JornalPromedio;
		input.put("Vacaciones", Vacaciones);
		double VacacionesEfectivas = -1 * vacacionesTomadas * JornalPromedio * 1.4;
		input.put("VacacionesEfectivas", VacacionesEfectivas);
		
		//retiro de finiquito
		double retiros = -1 * Utils.sum(lc.getWithdrawalSettlements());
		input.put("retiros", retiros);
		
		logger.debug("Total Premio + Vacaciones + VacacionesEfectivas + retiros = {} + {} + {} + {}",totalPremio , Vacaciones , VacacionesEfectivas,retiros);
		long total =  Math.round( totalPremio + Vacaciones + VacacionesEfectivas + retiros - lc.getFailureDiscount() - lc.getOthersDiscount() );
		input.put("total", total);
		return total;
	}

	Label lbJob ,lbJobCode,lbStep ,lbStarting, lbEnding,lbSettlement,lbStatus;
	BeanItemContainer<Vacation> vacationContainer;
	BeanItemContainer<License> absenceContainer;BeanItemContainer<Accident> accidentContainer;
	BeanItemContainer<ProgressiveVacation> progressiveVacationContainer;

	private void setContractGl(final Contract activeContract) {

		lbStep.setValue(activeContract.getStep());
		lbJob.setValue(activeContract.getJob().toString());
		lbJobCode.setValue(activeContract.getJobCode()+"");
		lbStarting.setValue(Utils.date2String( activeContract.getStartDate()));
		lbEnding.setValue(Utils.date2String( activeContract.getTerminationDate()));
		lbStatus.setValue(!activeContract.isFinished() ? "ACTIVO" : "TERMINADO");
		lbSettlement.setValue(activeContract.getSettlement() != null ? activeContract.getSettlement()+"":"");

	}

	protected VerticalLayout drawHistorico() {

		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();

		Table table = new Table();
		table.addGeneratedColumn("endingDate", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				Property endingDateProp = source.getItem(itemId).getItemProperty(columnId);
				if(endingDateProp.getValue() == null)
					return "Sin fecha de termino";
				else 
					return endingDateProp.getValue();
			}
		});
		table.setCellStyleGenerator(new Table.CellStyleGenerator() {

			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				if(propertyId == null ){ //estilo para la fila
					//si es la activa, la marca
					Item item = source.getItem(itemId);
					boolean isActive = (Boolean) item.getItemProperty("active").getValue();
					return isActive ? "active-row":null;
				}
				return null;
			}
		});

		table.setContainerDataSource(historyContainer);
		table.setWidth("100%");
		historyContainer.addNestedContainerProperty("constructionSite.name");
		table.setVisibleColumns("constructionSite.name","job","averageWage","reward","numberOfAccidents","endingDate");
		table.setColumnHeaders("Obra","Rol","Jornal Promedio","Premio","N° Accidentes","Fecha Termino");

		vl.addComponent(table);

		return vl;
	}

	private Component drawAccidents() {
		return new VerticalLayout(){
			{
				accidentContainer = new BeanItemContainer<Accident>(Accident.class);
				List<Accident> accidents = (List<Accident>)getItem().getItemProperty("accidents").getValue();
				if(accidents != null && logger != null )
					logger.debug("accidents {}",accidents);
				accidentContainer.addAll(accidents );

				setMargin(true);
				setSpacing(true);
				final Table table = new Table();
				if(!readOnly)
					addComponent(new GridLayout(3,2){
						{
							setWidth("100%");
							addComponent(new Label(""),0,0);
							addComponent(new Label(""),0,1);

							Button btnAddAccident = new Button(null,new Button.ClickListener() {

								@Override
								public void buttonClick(ClickEvent event) {
									LaborerConstructionsite laborer = (LaborerConstructionsite) getItem().getBean();
									if(laborer == null ) throw new RuntimeException("El trabajador no es válido.");
									Accident accident = new Accident();
									laborer.addAccident(accident);
									accidentContainer.addBean(accident);

								}
							}){{setIcon(FontAwesome.PLUS);}};
							addComponent(btnAddAccident,2,0);
							setComponentAlignment(btnAddAccident, Alignment.MIDDLE_RIGHT);

						}
					});
				table.setPageLength(5);
				table.setWidth("100%");
				table.setContainerDataSource(accidentContainer);
				table.setImmediate(true);

				table.addGeneratedColumn("total", new Table.ColumnGenerator() {

					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {

						final BeanItem<?> item = (BeanItem<?>) source.getItem(itemId);
						final Label label  = new Label(""+ source.getContainerProperty(itemId, columnId).getValue());
						Property.ValueChangeListener listener = new Property.ValueChangeListener() {
							@Override
							public void valueChange(Property.ValueChangeEvent event) {
								label.setValue(((Accident) item.getBean()).getTotal()+"");
							}
						};
						for (String pid: new String[]{"fromDate", "toDate"})
							((ValueChangeNotifier)item.getItemProperty(pid)).addValueChangeListener(listener);

						return label; 
					}
				});

				table.setTableFieldFactory(new DefaultFieldFactory(){

					public Field<?> createField(final Container container,
							final Object itemId,Object propertyId,com.vaadin.ui.Component uiContext) {
						Field<?> field = null; 
						if( propertyId.equals("description") ){
							field = new TextArea();
							field.setWidth("100%");	
							((TextArea)field).setNullRepresentation("");
							((TextArea)field).setImmediate(true);
						}

						else if(  propertyId.equals("fromDate") || propertyId.equals("toDate") ){
							field = new DateField();
							((DateField)field).setImmediate(true);
						}
						else if(  propertyId.equals("accidentLevel") ){
							field = new ComboBox();
							field.setPropertyDataSource(container.getContainerProperty(itemId, propertyId));
							for(AccidentLevel absenceType : AccidentLevel.values()){
								((ComboBox)field).addItem(absenceType);
							}
							((ComboBox)field).setImmediate(true);

						} else {
							return null;
						}
						return field;
					}
				});

				table.addGeneratedColumn("actions", new Table.ColumnGenerator() {

					@Override
					public Object generateCell(Table source, final Object itemId, Object columnId) {
						return new Button(null,new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar el accidente seleccionado?",
										"Eliminar", "Cancelar", new ConfirmDialog.Listener() {

									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											accidentContainer.removeItem(itemId);
										}
									}
								});
							}
						}){{setIcon(FontAwesome.TRASH_O);}};
					}
				});

				table.setVisibleColumns("accidentLevel","description","fromDate","toDate","total","actions");
				table.setColumnHeaders("Gravedad","Descripción","Desde","Hasta","Total días","Eliminar");
				table.setEditable(!readOnly);	
				table.setColumnWidth("actions", 100);
				addComponent(table);
			}
		};
	}

	private Component drawLicencias() {
		return new VerticalLayout(){
			{

				absenceContainer = new BeanItemContainer<License>(License.class);
				absenceContainer.addAll((Collection<? extends License>) getItem().getItemProperty("absences").getValue());

				setMargin(true);
				setSpacing(true);
				final Table table = new Table();
				table.setReadOnly(readOnly);
				if(!readOnly)
					addComponent(new GridLayout(3,2){
						{
							setWidth("100%");
							addComponent(new Label(""),0,0);

							addComponent(new Label(""),0,1);

							Button btnAddLicense = new Button(null,new Button.ClickListener() {

								@Override
								public void buttonClick(ClickEvent event) {
									LaborerConstructionsite laborer = (LaborerConstructionsite) getItem().getBean();
									if(laborer == null ) throw new RuntimeException("El trabajador no es válido.");
									License absence = new License();
									laborer.addAbsence(absence);
									absenceContainer.addBean(absence);

								}
							}){{setIcon(FontAwesome.PLUS);}};
							addComponent(btnAddLicense,2,0);
							setComponentAlignment(btnAddLicense, Alignment.MIDDLE_RIGHT);

						}
					});
				table.setPageLength(5);
				table.setWidth("100%");
				table.setContainerDataSource(absenceContainer);
				table.setImmediate(true);
				table.addGeneratedColumn("total", new Table.ColumnGenerator() {

					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {

						final BeanItem<?> item = (BeanItem<?>) source.getItem(itemId);
						final Label label  = new Label(""+ source.getContainerProperty(itemId, columnId).getValue());
						Property.ValueChangeListener listener = new Property.ValueChangeListener() {
							@Override
							public void valueChange(Property.ValueChangeEvent event) {
								label.setValue(((License) item.getBean()).getTotal()+"");
							}
						};
						for (String pid: new String[]{"fromDate", "toDate"})
							((ValueChangeNotifier)item.getItemProperty(pid)).addValueChangeListener(listener);

						return label; 
					}
				});

				table.setTableFieldFactory(new DefaultFieldFactory(){

					public Field<?> createField(final Container container,
							final Object itemId,Object propertyId,com.vaadin.ui.Component uiContext) {
						Field<?> field = null; 
						if( propertyId.equals("description") ){
							field = new TextArea();
							field.setWidth("100%");	
							((TextArea)field).setNullRepresentation("");
							((TextArea)field).setImmediate(true);
						}

						else if(  propertyId.equals("fromDate") || propertyId.equals("toDate") ){
							field = new DateField();
							((DateField)field).setImmediate(true);
						}
						else if(  propertyId.equals("licenseType") ){
							field = new ComboBox();
							field.setPropertyDataSource(container.getContainerProperty(itemId, propertyId));
							for(LicenseType absenceType : LicenseType.values()){
								((ComboBox)field).addItem(absenceType);
							}
							((ComboBox)field).setImmediate(true);

						} else {
							return null;
						}

						return field;
					}
				});

				table.addGeneratedColumn("actions", new Table.ColumnGenerator() {

					@Override
					public Object generateCell(Table source, final Object itemId, Object columnId) {
						return new Button(null,new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar la licencia seleccionada?",
										"Eliminar", "Cancelar", new ConfirmDialog.Listener() {

									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											absenceContainer.removeItem(itemId);
										}
									}
								});
							}
						}){{setIcon(FontAwesome.TRASH_O);}};
					}
				});

				table.setVisibleColumns("licenseType","description","fromDate","toDate","total","actions");
				table.setColumnHeaders("Tipo","Descripción","Desde","Hasta","Total","Eliminar");
				table.setColumnWidth("actions", 100);
				table.setEditable(!readOnly);				
				addComponent(table);
			}
		};
	}
	private int calcularUsadas(List<Vacation> vacations){
		if(vacations == null || vacations.isEmpty() ){
			return 0;
		}else{
			int total = 0;
			for(Vacation v : vacations){
				total += v.getTotal();
			}
			return total;
		}
	}

	private int calcularDisponibles(Contract contract,List<Vacation> vacations) {
		//calcula las vacaciones totales
		Date startDate = contract.getStartDate();
		int totalDays = Days.daysBetween(new DateTime(startDate), new DateTime(new Date())).getDays()/30;
		return (int) (( totalDays*1.25 ) - calcularUsadas(vacations));
	}

	private Component drawVacations() {
		return new VerticalLayout(){
			{

				vacationContainer = new BeanItemContainer<Vacation>(Vacation.class);
				vacationContainer.addAll(new ArrayList<Vacation>((Collection<? extends Vacation>) getItem().getItemProperty("vacations").getValue()));

				setMargin(true);
				setSpacing(true);
				Table vacationTable = new Table();

				final Label totalUsadas = new Label();
				final Label totalDisponibles = new Label();

				totalUsadas.setValue( calcularUsadas(vacationContainer.getItemIds()) +"");
				final Contract activeContract = ((LaborerConstructionsite) getItem().getBean()).getActiveContract();
				totalDisponibles.setValue(calcularDisponibles(activeContract,vacationContainer.getItemIds()) +"");

				addComponent(new GridLayout(3,2){
					{
						setWidth("100%");
						addComponent(new Label("Días usados"),0,0);
						addComponent(totalUsadas,1,0);

						addComponent(new Label("Días disponibles"),0,1);
						addComponent(totalDisponibles,1,1);

						if(!readOnly){
							Button addVacation = new Button(null,new Button.ClickListener() {

								@Override
								public void buttonClick(ClickEvent event) {
									Vacation vacation = new Vacation();
									vacationContainer.addBean(vacation);
									totalUsadas.setValue( calcularUsadas(vacationContainer.getItemIds()) +"");
									totalDisponibles.setValue(calcularDisponibles(activeContract,vacationContainer.getItemIds()) +"");

								}
							}){{setIcon(FontAwesome.PLUS);}};
							addComponent(addVacation,2,0);
							setComponentAlignment(addVacation, Alignment.MIDDLE_RIGHT);
						}

					}
				});

				vacationTable.setPageLength(5);
				vacationTable.setReadOnly(readOnly);
				vacationTable.setWidth("100%");
				vacationTable.setContainerDataSource(vacationContainer);
				vacationTable.addGeneratedColumn("total", new Table.ColumnGenerator() {

					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {

						final BeanItem<?> item = (BeanItem<?>) source.getItem(itemId);
						final Label label  = new Label(""+ source.getContainerProperty(itemId, columnId).getValue());
						Property.ValueChangeListener listener = new Property.ValueChangeListener() {
							@Override
							public void valueChange(Property.ValueChangeEvent event) {
								totalUsadas.setValue( calcularUsadas(vacationContainer.getItemIds()) +"");
								totalDisponibles.setValue(calcularDisponibles(activeContract,vacationContainer.getItemIds()) +"");
								label.setValue(((Vacation) item.getBean()).getTotal()+"");
							}
						};
						for (String pid: new String[]{"fromDate", "toDate"})
							((ValueChangeNotifier)item.getItemProperty(pid)).addValueChangeListener(listener);

						return label; 
					}
				});

				vacationTable.setTableFieldFactory(new OnValueChangeFieldFactory(2));

				vacationTable.addGeneratedColumn("print", new Table.ColumnGenerator() {

					@Override
					public Object generateCell(Table source, final Object itemId, Object columnId) {

						HorizontalLayout hl = new HorizontalLayout();
						hl.setSpacing(true);

						Button btnPrint = new Button(null,FontAwesome.PRINT);

						btnPrint.addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {

								final Map<String, Object> input = new HashMap<String, Object>();
								input.put("laborerConstructions", new LaborerConstructionsite[] {(LaborerConstructionsite)getItem().getBean()});
								VelocityHelper.addTools(input);

								final String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/vacation_doc.vm", "UTF-8", input);

								StreamResource.StreamSource source2 = new StreamResource.StreamSource() {

									public InputStream getStream() {
										//throw new UnsupportedOperationException("Not supported yet.");
										return new ByteArrayInputStream(body.getBytes());
									}
								};
								StreamResource resource = new StreamResource(source2, "vacaciones.html");

								Window window = new Window();
								window.setResizable(true);
								window.setWidth("60%");
								window.setHeight("60%");
								window.center();
								BrowserFrame e = new BrowserFrame();
								e.setSizeFull();

								// Here we create a new StreamResource which downloads our StreamSource,
								// which is our pdf.
								// Set the right mime type
								//						        resource.setMIMEType("application/pdf");
								resource.setMIMEType("text/html");

								e.setSource(resource);
								window.setContent(e);
								UI.getCurrent().addWindow(window);

							}

						});

						hl.addComponent(btnPrint);

						hl.addComponent(new Button(null,new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar la vacación seleccionada?",
										"Eliminar", "Cancelar", new ConfirmDialog.Listener() {

									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											vacationContainer.removeItem(itemId);
										}
									}
								});
							}
						}){{setIcon(FontAwesome.TRASH_O);}});

						return hl;
					}

				});

				vacationTable.setVisibleColumns("fromDate","toDate","total","print");
				vacationTable.setColumnHeaders("Desde","Hasta","Total","Acciones");
				vacationTable.setEditable(!readOnly);
				vacationTable.setColumnWidth("print", 130);

				addComponent(vacationTable);

				progressiveVacationContainer = new BeanItemContainer<ProgressiveVacation>(ProgressiveVacation.class);
				progressiveVacationContainer.addAll(new ArrayList<ProgressiveVacation>((Collection<? extends ProgressiveVacation>) getItem().getItemProperty("progressiveVacation").getValue()));


				if(!readOnly){
					Button addVacation = new Button(null,new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							ProgressiveVacation vacation = new ProgressiveVacation();
							vacation.setLaborerConstructionSite(laborerConstructionSite);
							progressiveVacationContainer.addBean(vacation);

						}
					}){{setIcon(FontAwesome.PLUS);}};
					addComponent(addVacation);
					setComponentAlignment(addVacation, Alignment.MIDDLE_RIGHT);
				}

				Table vacationProgressiveTable = new Table("Vacaciones Progresivas");
				vacationProgressiveTable.setPageLength(2);
				vacationProgressiveTable.setReadOnly(readOnly);
				vacationProgressiveTable.setWidth("100%");
				vacationProgressiveTable.setContainerDataSource(progressiveVacationContainer);

				vacationProgressiveTable.addGeneratedColumn("total", new Table.ColumnGenerator() {

					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {

						final BeanItem<?> item = (BeanItem<?>) source.getItem(itemId);
						final Label label  = new Label(""+ source.getContainerProperty(itemId, columnId).getValue());
						Property.ValueChangeListener listener = new Property.ValueChangeListener() {
							@Override
							public void valueChange(Property.ValueChangeEvent event) {
								label.setValue(((ProgressiveVacation) item.getBean()).getTotal()+"");
							}
						};
						for (String pid: new String[]{"fromDate", "toDate"})
							((ValueChangeNotifier)item.getItemProperty(pid)).addValueChangeListener(listener);

						return label; 
					}
				});

				vacationProgressiveTable.setTableFieldFactory(new OnValueChangeFieldFactory(2));

				vacationProgressiveTable.addGeneratedColumn("print", new Table.ColumnGenerator() {

					@Override
					public Object generateCell(Table source, final Object itemId, Object columnId) {

						HorizontalLayout hl = new HorizontalLayout();
						hl.setSpacing(true);

						Button btnPrint = new Button(null,FontAwesome.PRINT);

						btnPrint.addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {

								final Map<String, Object> input = new HashMap<String, Object>();
								input.put("laborerConstructions", new LaborerConstructionsite[] {(LaborerConstructionsite)getItem().getBean()});
								VelocityHelper.addTools(input);

								final String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/progressive_vacation_doc.vm", "UTF-8", input);

								StreamResource.StreamSource source2 = new StreamResource.StreamSource() {

									public InputStream getStream() {
										//throw new UnsupportedOperationException("Not supported yet.");
										return new ByteArrayInputStream(body.getBytes());
									}
								};
								StreamResource resource = new StreamResource(source2, "vacaciones_progressivas.html");

								Window window = new Window();
								window.setResizable(true);
								window.setWidth("60%");
								window.setHeight("60%");
								window.center();
								BrowserFrame e = new BrowserFrame();
								e.setSizeFull();

								resource.setMIMEType("text/html");

								e.setSource(resource);
								window.setContent(e);
								UI.getCurrent().addWindow(window);

							}

						});

						hl.addComponent(btnPrint);

						hl.addComponent(new Button(null,new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar la vacación seleccionada?",
										"Eliminar", "Cancelar", new ConfirmDialog.Listener() {

									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											progressiveVacationContainer.removeItem(itemId);
										}
									}
								});
							}
						}){{setIcon(FontAwesome.TRASH_O);}});

						return hl;
					}

				});

				vacationProgressiveTable.setVisibleColumns("fromDate","toDate","total","print");
				vacationProgressiveTable.setColumnHeaders("Desde","Hasta","Total","Acciones");
				vacationProgressiveTable.setEditable(!readOnly);
				vacationProgressiveTable.setColumnWidth("print", 130);

				addComponent(vacationProgressiveTable);
			}
		};
	}

	@Override
	protected boolean preCommit() {
		LaborerConstructionsite laborer = (LaborerConstructionsite) getItem().getBean();
		Date firstDayOfCurrentMonth;
		//vacaciones
		List<Vacation> vacations = new ArrayList<Vacation>(vacationContainer.getItemIds().size());
		for(Vacation vacation : vacationContainer.getItemIds()){
			// valida las vacaciones
			Set<ConstraintViolation<Vacation>> constraintViolations = validator.validate(vacation);
			if(constraintViolations.size() > 0 ){
				Notification.show("Una vacación es inválida \""+ constraintViolations.iterator().next().getMessage()+"\"",Type.ERROR_MESSAGE);
				tab.setSelectedTab(2);
				return false;
			}
			vacations.add(vacation);
			vacation.setLaborerConstructionSite(laborer);
		}
		getItem().getItemProperty("vacations").setValue(vacations); 
		
		//vacaciones progresivas
		List<ProgressiveVacation> vacationps = new ArrayList<ProgressiveVacation>(progressiveVacationContainer.getItemIds().size());
		for(ProgressiveVacation vacation : progressiveVacationContainer.getItemIds()){
			// valida las vacaciones
			Set<ConstraintViolation<ProgressiveVacation>> constraintViolations = validator.validate(vacation);
			if(constraintViolations.size() > 0 ){
				Notification.show("Una vacación progresiva es inválida \""+ constraintViolations.iterator().next().getMessage()+"\"",Type.ERROR_MESSAGE);
				tab.setSelectedTab(2);
				return false;
			}
			vacationps.add(vacation);
			vacation.setLaborerConstructionSite(laborer);
		}
		getItem().getItemProperty("progressiveVacation").setValue(vacationps); // no se si esto es necesario

		//accidentes
		List<Accident> accidents = new ArrayList<Accident>(accidentContainer.getItemIds().size());
		for(Accident accident : accidentContainer.getItemIds()){
			// valida los accidentes
			Set<ConstraintViolation<Accident>> constraintViolations = validator.validate(accident);
			if(constraintViolations.size() > 0 ){
				Notification.show("Un accidente es inválido \""+ constraintViolations.iterator().next().getMessage()+"\"",Type.ERROR_MESSAGE);
				tab.setSelectedTab(4);
				return false;
			}
			accidents.add(accident);
			accident.setLaborerConstructionSite(laborer);
		}
		getItem().getItemProperty("accidents").setValue(accidents);

		//licencias
		List<License> absences = new ArrayList<License>(absenceContainer.getItemIds().size());
		for(License absence : absenceContainer.getItemIds()){
			// valida las licencias
			Set<ConstraintViolation<License>> constraintViolations = validator.validate(absence);
			if(constraintViolations.size() > 0 ){
				Notification.show("Una licencia es inválida \""+ constraintViolations.iterator().next().getMessage()+"\"",Type.ERROR_MESSAGE);
				tab.setSelectedTab(5);
				return false;
			}
			absences.add(absence);
			absence.setLaborerConstructionSite(laborer);
		}
		getItem().getItemProperty("absences").setValue(absences); 

		List<Tool> tools = new ArrayList<Tool>(beanItemTool.getItemIds().size());
		for(Tool t : beanItemTool.getItemIds()){
			// valida las herramientas
			Set<ConstraintViolation<Tool>> constraintViolations = validator.validate(t);
			if(constraintViolations.size() > 0 ){
				Notification.show("Una herramienta es inválida \""+ constraintViolations.iterator().next().getMessage()+"\"",Type.ERROR_MESSAGE);
				tab.setSelectedTab(3);
				return false;
			}
			firstDayOfCurrentMonth = new DateTime().dayOfMonth().withMinimumValue().toDate();
			//si está marcado como pospuesto, verifica que exista la fecha, si no la tiene la agrega
			if(t.isPostponed()){
				if(!Utils.containsMonth(t.getDatePostponed(), firstDayOfCurrentMonth)){
					// agrega el primero del mes actual
					if(firstDayOfCurrentMonth != null)
						t.getDatePostponed().add(firstDayOfCurrentMonth);
				}
			} else {
				// si no está pospuesto y tiene la fecha actual, la quita
				if(Utils.containsMonth(t.getDatePostponed(), firstDayOfCurrentMonth)){
					if(firstDayOfCurrentMonth != null)
						t.getDatePostponed().remove(firstDayOfCurrentMonth);
				}
			}                
			tools.add(t);
			t.setLaborerConstructionSite(laborer);
		}
		getItem().getItemProperty("tool").setValue(tools);

		//limpiando lista de prestamos
		List<Loan> loans = new ArrayList<Loan>(beanItemLoan.getItemIds().size());
		for(Loan l : beanItemLoan.getItemIds()){
			// valida los préstamos
			Set<ConstraintViolation<Loan>> constraintViolations = validator.validate(l);
			if(constraintViolations.size() > 0 ){
				Notification.show("Un préstamo es inválido \""+ constraintViolations.iterator().next().getMessage()+"\"",Type.ERROR_MESSAGE);
				tab.setSelectedTab(3);
				return false;
			}
			firstDayOfCurrentMonth = new DateTime().dayOfMonth().withMinimumValue().toDate();
			//si está marcado como pospuesto, verifica que exista la fecha, si no la tiene la agrega
			if(l.isPostponed()){
				if(!Utils.containsMonth(l.getDatePostponed(), firstDayOfCurrentMonth)){
					// agrega el primero del mes actual
					if(firstDayOfCurrentMonth != null)
						l.getDatePostponed().add(firstDayOfCurrentMonth);
				}
			} else {
				// si no está pospuesto y tiene la fecha actual, la quita
				if(Utils.containsMonth(l.getDatePostponed(), firstDayOfCurrentMonth)){
					if(firstDayOfCurrentMonth != null)
						l.getDatePostponed().remove(firstDayOfCurrentMonth);
				}
			}                
			loans.add(l);
			l.setLaborerConstructionSite(laborer);
		}
		getItem().getItemProperty("loan").setValue(loans);
		
		//retiros
		List<WithdrawalSettlement> withdrawalsettlements = new ArrayList<WithdrawalSettlement>(beanItemWithdrawalSettlement.getItemIds().size());
		for(WithdrawalSettlement l : beanItemWithdrawalSettlement.getItemIds()){
			// valida los préstamos
			Set<ConstraintViolation<WithdrawalSettlement>> constraintViolations = validator.validate(l);
			if(constraintViolations.size() > 0 ){
				Notification.show("Una retención es inválida \""+ constraintViolations.iterator().next().getMessage()+"\"",Type.ERROR_MESSAGE);
				tab.setSelectedTab(4);
				return false;
			}
			withdrawalsettlements.add(l);
			l.setLaborerConstructionSite(laborer);
		}
		getItem().getItemProperty("withdrawalSettlements").setValue(withdrawalsettlements);

		return super.preCommit();
	}

	@Override
	protected boolean preDiscard() {
		return super.preDiscard();
	}
}
