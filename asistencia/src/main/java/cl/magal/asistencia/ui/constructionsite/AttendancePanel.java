package cl.magal.asistencia.ui.constructionsite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.velocity.app.VelocityEngine;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.vaadin.dialogs.ConfirmDialog;

import cl.magal.asistencia.entities.AdvancePaymentConfigurations;
import cl.magal.asistencia.entities.AdvancePaymentItem;
import cl.magal.asistencia.entities.Attendance;
import cl.magal.asistencia.entities.Confirmations;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.DateConfigurations;
import cl.magal.asistencia.entities.Overtime;
import cl.magal.asistencia.entities.Salary;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.AttendanceMark;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.services.ConfigurationService;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.services.MailService;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.ui.ListenerFieldFactory;
import cl.magal.asistencia.ui.MagalUI;
import cl.magal.asistencia.ui.components.ColumnCollapsedObservableTable;
import cl.magal.asistencia.ui.components.ColumnCollapsedObservableTable.ColumnCollapsedEvent;
import cl.magal.asistencia.ui.vo.AbsenceVO;
import cl.magal.asistencia.util.Constants;
import cl.magal.asistencia.util.SecurityHelper;
import cl.magal.asistencia.util.Utils;
import cl.magal.asistencia.util.VelocityHelper;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Container.SimpleFilterable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.CellReference;
import com.vaadin.ui.Grid.FooterCell;
import com.vaadin.ui.Grid.FooterRow;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Component
@Scope("prototype")
//public class AttendancePanel extends Panel implements View {
public class AttendancePanel extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3799725098229572967L;

	transient Logger logger = LoggerFactory.getLogger(AttendancePanel.class);

	public static String NAME = "asistencia";

	/** SERVICIOS **/
	@Autowired
	private transient ConstructionSiteService service;
	@Autowired
	private transient LaborerService laborerService;
	@Autowired
	private transient ConfigurationService configurationService;
	@Autowired
	private transient UserService userService;
	@Autowired
	private transient ConfigurationService confService;
	@Autowired
	private transient MailService mailService;
	@Autowired
	private transient VelocityEngine velocityEngine;
	
	AdvancePaymentConfigurations advancepayment;
	/** CONTAINERS **/
	BeanContainer<Long,Attendance> attendanceContainer = new BeanContainer<Long,Attendance>(Attendance.class);
	BeanContainer<Long,Overtime> overtimeContainer = new BeanContainer<Long,Overtime>(Overtime.class);
	BeanContainer<Long,Salary> salaryContainer = new BeanContainer<Long,Salary>(Salary.class);
	BeanItemContainer<AbsenceVO> absenceContainer = new BeanItemContainer<AbsenceVO>(AbsenceVO.class);
	BeanItemContainer<ConstructionSite> constructionContainer = new BeanItemContainer<ConstructionSite>(ConstructionSite.class);

	/** COMPONENTES **/
	ProgressBar progress;
	Label status;
	Grid attendanceGrid, overtimeGrid;
	Window progressDialog;
	InlineDateField attendanceDate;
	Button btnExportSoftland,btnExportSupleSoftland,btnConstructionSiteConfirm,btnCentralConfirm,btnSupleObraConfirm,btnSupleCentralConfirm;
	Table confirmTable;
//	VerticalLayout root;
	Table supleTable;
	ColumnCollapsedObservableTable salaryTable;

	Map<Long, Boolean> ids = new HashMap<Long, Boolean>();
	
	/** ATRIBUTOS **/
	Confirmations confirmations;
	ConstructionSite cs;
	
	final static String[] defaultSalaryTableVisibleTable = new String[]{"laborerConstructionSite.activeContract.jobCode",
		"laborerConstructionSite.laborer.fullname","lastJornalPromedio","jornalPromedio","specialBond","bondMov2","loanBond","sobreTiempo","descHours","loan","tools","totalLiquido"
	};
	final static String[] salaryTableVisibleTable = new String[]{"laborerConstructionSite.activeContract.jobCode",
			"laborerConstructionSite.laborer.fullname","lastJornalPromedio","jornalPromedio","specialBond","bondMov2","loanBond","sobreTiempo","descHours","loan","tools","totalLiquido"
			,"jornalBaseMes","vtrato","valorSabado","vsCorrd","descHoras","bonifImpo","glegal","afecto","sobreAfecto","cargas","asigFamiliar","colacion","mov","mov2","tnoAfecto"
	};

	public Confirmations getConfirmations() {
		if(confirmations == null )
			reloadMonthAttendanceData(getAttendanceDate());
		return confirmations;
	}

	public void setCs(ConstructionSite cs) {
		this.cs = cs;
	}

	private DateTime getAttendanceDate() {
		if(attendanceDate.getValue() == null ){
			attendanceDate.setValue(new DateTime().dayOfMonth().withMinimumValue().toDate()); //agrega el primer día del mes actual
		}
		return new DateTime(attendanceDate.getValue());
	}
	
	/**
	 * obtiene la fecha de cierre del mes seleccionado
	 * @return
	 */
	private DateTime getClosingDate(){
		DateTime dt = getAttendanceDate();
		DateConfigurations dateConfig = configurationService.getDateConfigurationByCsAndMonth(cs,dt);
		//si no se ha seteado la fecha, elije el último día del mes 
		if(dateConfig.getAssistance() == null ){
			dateConfig.setAssistance(dt.withDayOfMonth(dt.dayOfMonth().getMaximumValue()).toDate());
		}
		return new DateTime(dateConfig.getAssistance());
	}

	/**
	 * obtiene la fecha de cierre del mes anterior
	 * @return
	 */
	private DateTime getPastMonthClosingDate(){
		DateTime dt = getAttendanceDate();
		DateConfigurations dateConfig = configurationService.getDateConfigurationByCsAndMonth(cs,dt.minusMonths(1));
		
		if( dateConfig.getAssistance() == null ){
			
			dateConfig.setAssistance(dt.minusMonths(1).withDayOfMonth(dt.minusMonths(1).dayOfMonth().getMaximumValue()).toDate());
		}
		return new DateTime(dateConfig.getAssistance());
	}

	public AttendancePanel(){

	}

	@PostConstruct
	private void init(){
		
		//crea la parte superior de la interfaz de asistencia
		final HorizontalLayout topAsistencia = drawTopAttendance();
		//crea las tabs que contienen la información de asistencia
		final TabSheet detalleAsistencia = drawAttendanceDetail();
		
		setSizeFull();
		setSpacing(true);

		topAsistencia.setSizeFull();
		addComponent(topAsistencia);
		setExpandRatio(topAsistencia, 0.1F);
		detalleAsistencia.setSizeFull();
		addComponent(detalleAsistencia);
		setExpandRatio(detalleAsistencia, 0.9F);

		progressDialog = new Window();
		progressDialog.setModal(true);
		progressDialog.setResizable(false);
		//		progressDialog.setClosable(false);

		//		HorizontalLayout barbar = new HorizontalLayout();
		//		barbar.setMargin(true);
		//		barbar.setSpacing(true);
		//		progressDialog.setContent(barbar);

		// Create the indicator, disabled until progress is started
		//		progress = new ProgressBar(new Float(0.0));
		//		progress.setIndeterminate(true);
		//		progress.setEnabled(false);
		////		barbar.addComponent(progress);
		//		status = new Label(" Cargando asistencia...");
		//		barbar.addComponent(status);

	}

	@Override
	public void enter(ViewChangeEvent event) {

		if( event == null && cs == null ){
			showErrorParam();
			return;
		}

		//obtiene los parametros de url
		else if( event != null && event.getParameters() != null && cs == null ){
			// split at "/", add each part as a label
			String[] msgs = event.getParameters().split("/");
			//si no trae parametros, entonces avisa y deshabilita la interfaz
			if(msgs == null || msgs.length == 0 ){
				showErrorParam();
				return;
			}
			//si trae parametro verifica que sea un numero valido
			try{

				Long id = Long.valueOf(msgs[0]);

				//verifica los parametros de la url
				if( msgs.length >= 1 ){
					//si todo va bien, carga la información de la obra si es necesaria
					cs = service.findConstructionSite(id);
				}else if( msgs.length >= 2 ){

				}else {
					showErrorParam();
				}
			}catch(NumberFormatException e){
				showErrorParam();
				return;
			}

		}

		populateAttendanceGrid();
		configureInterface();
	}

	private void showErrorParam() {
		Notification.show("Debe seleccionar una obra ",Type.ERROR_MESSAGE);
		//setea el titulo como vacio
		((MagalUI)UI.getCurrent()).getTitle().setValue("");
//		root.setEnabled(false);		
		setEnabled(false);
	}

	private void clearGrids(){
		attendanceContainer.removeAllItems();
		overtimeContainer.removeAllItems();
		absenceContainer.removeAllItems();
		salaryContainer.removeAllItems();
	}
	
	TabSheet tab;

	private TabSheet drawAttendanceDetail() {
		logger.debug("PRIMERO");
		tab = new TabSheet();

		Grid attendanceGrid = drawAttendanceGrid();
		tab.addTab(attendanceGrid,"Asistencia");

		Grid overtimeGrid = drawOvertimeGrid();		
		tab.addTab(overtimeGrid,"Horas Extras");
		
		//agrega las confirmaciones solo si se tiene permiso para confirmar central
		Table confirmTable = drawAbsenceConfirmTable();
		tab.addTab(confirmTable,"Licencias y Accidentes");

		VerticalLayout vl = drawSupleLayout();
		tab.addTab(vl,"Suple");		
		
		vl = drawSalaryLayout();
		tab.addTab(vl,"Sueldo");

		return tab;
	}
	
	private void enableOrDisableComponents(){
		//la asistencia de obra se puede editar si
		boolean attendanceEnabled =  
			(
			SecurityHelper.hasConstructionSite(cs) && //tiene asociada la obra
			SecurityHelper.hasPermission(Permission.EDITAR_ASISTENCIA) && //tiene permisos para editar asistencia
			!getConfirmations().isConstructionSiteCheck() //si aún no se confirma ese mes
			) || // O
			SecurityHelper.hasPermission(Permission.DESBLOQUEDAR_ASISTENCIA); //si tiene permisos para cancelar confirmación central (solo lo deberia tener el admin)
		enableAttendance(attendanceEnabled);
		enableSalary(attendanceEnabled);
		//la asistencia de obra se puede editar si
		boolean supleEnabled =  
			(
			SecurityHelper.hasConstructionSite(cs) && //tiene asociada la obra
			SecurityHelper.hasPermission(Permission.EDITAR_ASISTENCIA) && //tiene permisos para editar asistencia
			!getConfirmations().isSupleObraCheck() //si aún no se confirma ese mes
			) || // O
			SecurityHelper.hasPermission(Permission.DESBLOQUEDAR_ASISTENCIA); 
		enableSuple(supleEnabled);
		
	}

	private void setVisibilityOfButtons() {
		//muestra el boton de obra, si tiene el permiso o si tiene permisos de confirmación central y esta confirmado de obra (para que pueda cancelar la confirmación)
		boolean showOnConstructionSite = 
				SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_OBRA )||
				( SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL ) && getConfirmations().isConstructionSiteCheck()) ;
		
		boolean showOnCentral = 
				SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL) ;
		
		btnConstructionSiteConfirm.setVisible(showOnConstructionSite);
		//muestra el boton de confirmación central, si tiene el permiso
		btnCentralConfirm.setVisible(showOnCentral);
		
		//permite exportar a softland si se tiene doble confirmación y se tiene permisos
		
		btnExportSoftland.setVisible(SecurityHelper.hasPermission(Permission.GENERAR_SUELDOS_SOFTLAND));
		btnExportSupleSoftland.setVisible(SecurityHelper.hasPermission(Permission.GENERAR_SUELDOS_SOFTLAND));
		/**
		 * Use el mismo código de arriba, reemplace los valores para considerar los botones del suple que se comportan igual.
		 */
		btnSupleObraConfirm.setVisible(
				SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_OBRA )||
				( SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL ) && getConfirmations().isSupleObraCheck() ));
		btnSupleCentralConfirm.setVisible(showOnCentral);
		
		tab.getTab(2).setVisible(showOnCentral);
		
	}

	private void toogleButtonState(Button btn, boolean confirmations ){
		if(confirmations){

			btn.removeStyleName(Constants.STYLE_CLASS_GREEN_COLOR);
			btn.addStyleName(Constants.STYLE_CLASS_RED_COLOR);
			btn.setCaption(btn.getCaption().indexOf("Cancelar ") >= 0 ? btn.getCaption() : "Cancelar " + btn.getCaption());
			btn.setIcon(FontAwesome.TIMES);

		}else{

			btn.removeStyleName(Constants.STYLE_CLASS_RED_COLOR);
			btn.addStyleName(Constants.STYLE_CLASS_GREEN_COLOR);
			btn.setCaption(btn.getCaption().indexOf("Cancelar ") >= 0 ? btn.getCaption().replace("Cancelar ", ""): btn.getCaption() );
			btn.setIcon(FontAwesome.CHECK);
		}
	}
	
	private void enableOrDisableButtons(){
		//el boton de confirmación obra se activa si
		//se asegura que lo vea
		btnConstructionSiteConfirm.setEnabled(
				(
				getConfirmations().isConstructionSiteCheck() && !getConfirmations().isCentralCheck() // si está confirmado por obra pero no por central 
				&& SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL) //y el usuario tiene permitido confirmar asistencia central (para que el central pueda cancelar la confirmación de obra )
				)||
				!getConfirmations().isConstructionSiteCheck() // si no se ha checkeado la construcción
				);
		
		//boton de confirmación de central
		btnCentralConfirm.setEnabled( 
				(getConfirmations().isConstructionSiteCheck() && !getConfirmations().isCentralCheck()) ||
				(getConfirmations().isConstructionSiteCheck() && getConfirmations().isCentralCheck() && 
						SecurityHelper.hasPermission(Permission.DESBLOQUEDAR_ASISTENCIA) 
		));
		
		//si está confirmado, lo habilita solo si tiene permiso para confirmar asistencia central
		btnSupleObraConfirm.setEnabled((
				getConfirmations().isSupleObraCheck() && !getConfirmations().isSupleCentralCheck() // si está confirmado por obra pero no por central 
				&& SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL) //y el usuario tiene permitido confirmar asistencia central (para que el central pueda cancelar la confirmación de obra )
				)||
				!getConfirmations().isSupleObraCheck());// si no se ha checkeado la construcción);
		
		btnSupleCentralConfirm.setEnabled(
				(getConfirmations().isSupleObraCheck() && !getConfirmations().isSupleCentralCheck()) ||
				(getConfirmations().isSupleObraCheck() && getConfirmations().isSupleCentralCheck() && 
						SecurityHelper.hasPermission(Permission.DESBLOQUEDAR_ASISTENCIA) ) );

		btnExportSoftland.setEnabled(getConfirmations().isCentralCheck()); //sólo se habilita si está la confirmación de central
		btnExportSupleSoftland.setEnabled(getConfirmations().isSupleCentralCheck());
	}
	
	private void enableSalary(boolean state) {
		salaryTable.setEditable(state);
	}

	private void enableAttendance(boolean state) {
		
		//fuerza que ningun item esté siendo editado
		if(attendanceGrid.isEditorActive()){ 
			attendanceGrid.cancelEditor();
		}
		attendanceGrid.setEditorEnabled(state);
		
		//fuerza que ningun item esté siendo editado
		if(overtimeGrid.isEditorActive()) {
			overtimeGrid.cancelEditor();
		}
		overtimeGrid.setEditorEnabled(state);
		confirmTable.setEditable(state);
	}
	
	private void enableSuple(boolean state) {
		supleTable.setEditable(state);
	}

	private void createHeaders(final Grid grid) {

		HeaderRow filterRow = null;

		if(grid.getHeaderRowCount() == 1)
			filterRow = grid.appendHeaderRow();
		else
			filterRow = grid.getHeaderRow(1);
		//si la propiedad comienza con d (dia) o dmp (dia mes pasado), entonces muestra el dia de la semana correspondiente
		final DateTime dt = getAttendanceDate();
		for (final Object pid: grid.getContainerDataSource().getContainerPropertyIds()) {

			HeaderCell cell = filterRow.getCell(pid);

			if(pid.equals("laborerConstructionSite.activeContract.jobCode")||
					pid.equals("laborerConstructionSite.laborer.fullname")){
				
				// Have an input field to use for filter
				TextField filterField = new TextField();
				filterField.setStyleName("no-padding");
				filterField.setWidth("100%");
				filterField.setHeight("90%");

				// Update filter When the filter input is changed
				filterField.addTextChangeListener(new TextChangeListener() {

					@Override
					public void textChange(TextChangeEvent event) {
						// Can't modify filters so need to replace
						((SimpleFilterable) grid.getContainerDataSource()).removeContainerFilters(pid);

						// (Re)create the filter if necessary
						if (! event.getText().isEmpty())
							((Filterable) grid.getContainerDataSource()).addContainerFilter(
									new SimpleStringFilter(pid,
											event.getText(), true, false));
					}
				});
				if(cell != null)
					cell.setComponent(filterField);
			}else {
				Label label = new Label();
				if(grid.getColumn(pid) != null )
					grid.getColumn(pid).setSortable(false);//.setWidth(50);
				//calculo de la semana
				if(((String) pid).startsWith("dmp") || ((String) pid).startsWith("dma")  ){
					//calcula el numero del mes
					int monthDay = Integer.parseInt(((String) pid).replace("dmp","").replace("dma",""));
					DateTime dt2 = dt;
					if ( ((String) pid).startsWith("dmp") )
						dt2 = dt2.minusMonths(1);
					//el número de mes no es un número válido de mes o si es un día a la fecha de cierre del mes pasado, oculta la columna
					if( monthDay > dt2.dayOfMonth().getMaximumValue() || 
						( ((String) pid).startsWith("dmp") && monthDay <= getPastMonthClosingDate().getDayOfMonth())){
						if(grid.getColumn(pid) != null)
							grid.removeColumn(pid);
					}else{ //solo lo setea si el número es mayor a la cantidad de dias del mes
						if(grid.getColumn(pid) == null)
							grid.addColumn(pid);
						label.setValue( dt2.withDayOfMonth(monthDay).dayOfWeek().getAsShortText() );
						grid.getColumn(pid).setHeaderCaption(((String) pid).replace("dmp","").replace("dma","")).setSortable(false);
					}
					if(cell == null){
						cell = filterRow.getCell(pid);
					}

					if(cell != null){
						cell.setComponent(label);
					}
				}

			}

			grid.setCellStyleGenerator(new Grid.CellStyleGenerator() {

				@Override
				public String getStyle(CellReference cellReference) {
					String post = "";
					if( (cellReference.getValue() instanceof AttendanceMark && !AttendanceMark.ATTEND.equals(cellReference.getValue())) ||
							(cellReference.getValue() instanceof Integer && 0 != (Integer)cellReference.getValue()))
						post = " red-color";
					if(cellReference.getValue() instanceof AttendanceMark && !AttendanceMark.ATTEND.equals(cellReference.getValue()))
						post += " bold";
					String pid = (String) cellReference.getPropertyId();
					
					if(pid.equals("laborerConstructionSite.laborer.fullname"))
						return "textalignLeft";

					if( pid.startsWith("dmp") || pid.startsWith("dma") ){
						//calcula el numero del mes
						int monthDay = Integer.parseInt(((String) pid).replace("dmp","").replace("dma",""));
						DateTime dt2 = dt;
						if ( pid.startsWith("dmp") )
							dt2 = dt2.minusMonths(1);
						if(monthDay <= dt2.dayOfMonth().getMaximumValue()){
							//si es el dia actual
							if(dt2.withDayOfMonth(monthDay).toString("dd/MM/yyyy").equals(DateTime.now().toString("dd/MM/yyyy")))
								return "grid-today"+post;
							//	si es mes pasado
							else if(pid.startsWith("dmp")){
								return "grid-pastmonth"+post;
							}else{
								//si es este mes
								return "grid-actualmonth"+post;
							}
						}
					}
					return null;
				}
			});
		}
	}

	/**
	 * "Pestaña Anticipos"
	 * @return
	 */
	private VerticalLayout drawSupleLayout() {
		salaryContainer.addNestedContainerProperty("laborerConstructionSite.activeContract.jobCode");
		salaryContainer.addNestedContainerProperty("laborerConstructionSite.laborer.fullname");
		salaryContainer.addNestedContainerProperty("laborerConstructionSite.supleCode");
		salaryContainer.addNestedContainerProperty("laborerConstructionSite.id");
		salaryContainer.setBeanIdProperty("laborerConstructionSite.id");
		VerticalLayout vl = new VerticalLayout(){
			{
				setSpacing(true);
				setMargin(true);

				supleTable = new Table();
				supleTable.setSizeFull();
				supleTable.setContainerDataSource(salaryContainer);
				
				HorizontalLayout hl = new HorizontalLayout(){
					{
						setSpacing(true);
						
						final Button btnValidate = new Button("Validar Negativos",FontAwesome.CHECK_CIRCLE_O);
						btnValidate.setDisableOnClick(true);
						btnValidate.addClickListener(new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								Object itemId = null;
								if( (itemId = checkHasNegativeOrNull(salaryContainer,"suple")) == null ) {
									Notification.show("La lista de trabajadores no tiene valores negativos en el suple.",Type.HUMANIZED_MESSAGE);
								}else{
									BeanItem<Salary> item = salaryContainer.getItem(itemId);
									Notification.show("El trabajador "+item.getBean().getLaborerConstructionSite().getJobCode()+" tiene un suple negativo."
											,Type.ERROR_MESSAGE);
								}
								btnValidate.setEnabled(true);
							}

						});
						addComponent(btnValidate);
						
						btnSupleObraConfirm = new Button("Confirmación Obra",FontAwesome.CHECK);
						btnSupleObraConfirm.setDisableOnClick(true);						
						addComponent(btnSupleObraConfirm);			
						setComponentAlignment(btnSupleObraConfirm, Alignment.TOP_RIGHT);
						btnSupleObraConfirm.addClickListener(new Button.ClickListener() {					

							@Override
							public void buttonClick(ClickEvent event) {
								//mensaje depende del estado
								final Confirmations confirmations = getConfirmations();
								ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", 
										confirmations.isConstructionSiteCheck() ? 
												"¿Está seguro de cancelar el bloqueo de la información del anticipo? Esto desbloqueará la edición en obra del suple.":
												"¿Está seguro de confirmar el bloqueo de la información del anticipo? Esto bloqueará la edición en obra del suple.",
										"Continuar", "Cancelar", new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											confirmations.setSupleObraCheck(!confirmations.isSupleObraCheck());
											service.save(confirmations);
											configureInterface();

											//si todo sale bien, manda un email a los centrales
											if(confirmations.isSupleObraCheck())
												mailService.sendSupleConfirmationEmail(cs);
										}
										
									}

								});						
							}
						});
						
						btnSupleCentralConfirm = new Button("Confirmación Central",FontAwesome.CHECK);
						btnSupleCentralConfirm.setDisableOnClick(true);						
						addComponent(btnSupleCentralConfirm);
						setComponentAlignment(btnSupleCentralConfirm, Alignment.TOP_RIGHT);
						btnSupleCentralConfirm.addClickListener(new Button.ClickListener() {
							@Override
							public void buttonClick(ClickEvent event) {
								final Confirmations confirmations = getConfirmations();
								ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", 
										confirmations.isCentralCheck() ? 
												"¿Está seguro de cancelar el bloqueo de la información del anticipo? Esto desbloqueará la edición en obra del suple.":
													"¿Está seguro de confirmar el bloqueo de la información del anticipo? Esto bloqueará la edición en obra del suple.",
											"Continuar", "Cancelar", new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											confirmations.setSupleCentralCheck(!confirmations.isSupleCentralCheck());
											service.save(confirmations);
											toogleButtonState(btnSupleCentralConfirm, confirmations.isSupleCentralCheck());
											//actualiza el estado del boton exportar
											configureInterface();
										}
									}
								});
							}
						});
						
						btnExportSupleSoftland = new Button("Exportar a Softland",FontAwesome.FILE_EXCEL_O);
						addComponent(btnExportSupleSoftland);
						setComponentAlignment(btnExportSupleSoftland, Alignment.TOP_RIGHT);
						generateSupleSoftlandFile(btnExportSupleSoftland);
					}					
				};
				
				addComponent(hl);
				setComponentAlignment(hl, Alignment.TOP_RIGHT);
				
				supleTable.addGeneratedColumn("supleSection", new Table.ColumnGenerator() {
					
					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {
						HorizontalLayout hl = new HorizontalLayout();
						hl.setSizeFull();
						
						final BeanItem<Salary> beanItem = salaryContainer.getItem(itemId);
						// recuperar posibles codigos de suple
						final ComboBox cb = new ComboBox();
						for(AdvancePaymentItem item : advancepayment.getAdvancePaymentTable() ){
							cb.addItem(item.getSupleCode());
						}
						cb.setPropertyDataSource(beanItem.getItemProperty("laborerConstructionSite.supleCode"));
						cb.setReadOnly(true);
						
						hl.addComponent(cb);		
						
						return hl;
					}
				});				
				
				supleTable.addGeneratedColumn("supleManual", new Table.ColumnGenerator() {
					
					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {
						HorizontalLayout hl = new HorizontalLayout();
						hl.setSizeFull();
						
						final BeanItem<Salary> beanItem = salaryContainer.getItem(itemId);
						Button btnSuple = new Button(FontAwesome.ARROW_CIRCLE_O_RIGHT);
						btnSuple.addClickListener(new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								logger.debug(" suple value calculated ");
								//obliga a calcular el suple con la tabla
								//define el suple como calculado
								beanItem.getItemProperty("calculatedSuple").setValue(true);
								//obliga a que se recalcule el suple
								beanItem.getItemProperty("forceSuple").getValue();
								//lo pide explicitamente para obligar a recalcular el suple
								double suple = (Double) beanItem.getItemProperty("suple").getValue();
								//recupera monto suple de la tabla
								beanItem.getItemProperty("suple").setValue(suple);
								//guarda el trabajador, para guardar el codigo de suple
								laborerService.save(beanItem.getBean().getLaborerConstructionSite());
								//guarda el salario
								service.save(beanItem.getBean());								
							}							
						});
						hl.addComponent(btnSuple);
						btnSuple.setVisible(!(Boolean) beanItem.getItemProperty("calculatedSuple").getValue());

						if(!ids.isEmpty()){
							Iterator it = ids.keySet().iterator();
							while(it.hasNext()){
							  Long key = (Long) it.next();
								if( ids.get(key) && key == beanItem.getBean().getLaborerConstructionSite().getLaborer().getId() && !beanItem.getItemProperty("calculatedSuple").equals(false))
									btnSuple.setVisible(false);
							}
						}
						return hl;
					}
				});

				supleTable.setVisibleColumns("laborerConstructionSite.activeContract.jobCode","laborerConstructionSite.laborer.fullname","supleSection","supleManual","suple");
				supleTable.setColumnHeaders("Rol","Nombre","Código suple","","Suple");
				supleTable.setEditable(true);
				supleTable.setFooterVisible(true);
				supleTable.setTableFieldFactory(new TableFieldFactory() {

					@Override
					public Field<?> createField(Container container, final Object itemId,Object propertyId, com.vaadin.ui.Component uiContext) {
						if(propertyId.equals("laborerConstructionSite.activeContract.jobCode")||
								propertyId.equals("laborerConstructionSite.laborer.fullname"))
							return null;
						TextField tf = new TextField();
						tf.setNullRepresentation("");
						tf.setImmediate(true);
						if(propertyId.equals("suple")){
							tf.addBlurListener(new FieldEvents.BlurListener() {
								
								@Override
								public void blur(BlurEvent event) {
									BeanItem<Salary> beanItem = salaryContainer.getItem(itemId);
									beanItem.getItemProperty("calculatedSuple").setValue(false);
									//guarda el salario
									service.save(beanItem.getBean());
									supleTable.refreshRowCache();
								}
							});
							tf.addValueChangeListener(new Property.ValueChangeListener() {
								
								@Override
								public void valueChange(ValueChangeEvent event) {
//									logger.debug(" suple value changed ");
//									BeanItem<Salary> beanItem = salaryContainer.getItem(itemId);
//									beanItem.getItemProperty("calculatedSuple").setValue(false);
//									//guarda el salario
//									service.save(beanItem.getBean());
								}
							});
						}
						return tf;
					}
				});


				addComponent(supleTable);
				setExpandRatio(supleTable, 1.0f);
			}
		};
		vl.setSizeFull();
		return vl;
	}
	
	/**
	 * Pestaña calculo sueldo
	 * @return
	 */
	private VerticalLayout drawSalaryLayout() {
		salaryContainer.addNestedContainerProperty("laborerConstructionSite.activeContract.jobCode");
		salaryContainer.addNestedContainerProperty("laborerConstructionSite.laborer.fullname");
		salaryContainer.addNestedContainerProperty("laborerConstructionSite.supleCode");
		VerticalLayout vl = new VerticalLayout(){
			{
				setSpacing(true);
				setMargin(true);
				HorizontalLayout hl = new HorizontalLayout(){
					{

						setSpacing(true);
						
						final Button btnValidate = new Button("Validar Negativos",FontAwesome.CHECK_CIRCLE_O);
						btnValidate.setDisableOnClick(true);
						btnValidate.addClickListener(new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								Object itemId = null;
								if( (itemId = checkHasNegativeOrNull(salaryContainer,"salary","vtrato","valorSabado","vsCorrd","glegal","afecto","colacion","mov","mov2")) == null ) {
									Notification.show("La lista de trabajadores no tiene valores negativos en el sueldo ni en el jornal promedio.",Type.HUMANIZED_MESSAGE);
								}else{
									BeanItem<Salary> item = salaryContainer.getItem(itemId);
									Notification.show("El trabajador "+item.getBean().getLaborerConstructionSite().getJobCode()+" tiene uno de sus valores negativos."
											,Type.ERROR_MESSAGE);
								}
								btnValidate.setEnabled(true);
							}

						});
						addComponent(btnValidate);

						btnConstructionSiteConfirm = new Button("Confirmación Obra",FontAwesome.CHECK);
						btnConstructionSiteConfirm.setDisableOnClick(true);
						addComponent(btnConstructionSiteConfirm);
						setComponentAlignment(btnConstructionSiteConfirm, Alignment.TOP_RIGHT);
						btnConstructionSiteConfirm.addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								//mensaje depende del estado
								final Confirmations confirmations = getConfirmations();
								ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", 
										confirmations.isConstructionSiteCheck() ? 
												"¿Está seguro de cancelar la confirmación de asistencia? Esto desbloqueará la edición en obra de la asistencia del mes.":
													"¿Está seguro de confirmar la asistencia? Esto bloqueará la edición en obra de la asistencia del mes.",
													"Continuar", "Cancelar", new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											confirmations.setConstructionSiteCheck(!confirmations.isConstructionSiteCheck());
											service.save(confirmations);
											configureInterface();
											//si todo sale bien, envia el mail a los centrales
											if(confirmations.isConstructionSiteCheck())
												mailService.sendSalaryConfirmationEmail(cs);
										}
									}

								});

							}
						});

						btnCentralConfirm = new Button("Confirmación Central",FontAwesome.CHECK);
						btnCentralConfirm.setDisableOnClick(true);
						addComponent(btnCentralConfirm);
						setComponentAlignment(btnCentralConfirm, Alignment.TOP_RIGHT);
						btnCentralConfirm.addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								final Confirmations confirmations = getConfirmations();
								ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", 
										confirmations.isCentralCheck() ? 
												"¿Está seguro de cancelar la confirmación de asistencia? Esto desbloqueará la edición en la central de la asistencia del mes.":
													"¿Está seguro de confirmar la asistencia? Esto bloqueará la edición en la central de la asistencia del mes.",
													"Continuar", "Cancelar", new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											confirmations.setCentralCheck(!confirmations.isCentralCheck());
											service.save(confirmations);
											configureInterface();
										}
									}
								});
							}
						});

						btnExportSoftland = new Button("Exportar a Softland",FontAwesome.FILE_EXCEL_O);
//						btnExportSoftland.setDisableOnClick(true);
						addComponent(btnExportSoftland);
						setComponentAlignment(btnExportSoftland, Alignment.TOP_RIGHT);

						generateSoftlandFile(btnExportSoftland);
						
					}
				};

				addComponent(hl);
				setComponentAlignment(hl, Alignment.TOP_RIGHT);
				salaryTable = new ColumnCollapsedObservableTable();
//				salaryTable.setWidth("100%");
				salaryTable.setSizeFull();
				salaryTable.setContainerDataSource(salaryContainer);
				salaryTable.setColumnCollapsingAllowed(true);
				
				salaryTable.addGeneratedColumn("totalLiquido", new Table.ColumnGenerator(){

					@Override
					public Object generateCell(final Table source, final Object itemId,final Object columnId) {
						final BeanItem<Salary> item = (BeanItem<Salary>) salaryContainer.getItem(itemId);
						final Label label  = new Label("<b>"+Utils.formatInteger((Integer) salaryContainer.getContainerProperty(itemId, columnId).getValue())+"</b>"+
								"  ("+Utils.formatInteger((Integer) salaryContainer.getContainerProperty(itemId, "roundSalary").getValue())+")");
						label.setContentMode(ContentMode.HTML);
						for(final String pid : new String[]{"jornalPromedio","suple","descHours","bondMov2","specialBond","loanBond"})
							((ValueChangeNotifier)item.getItemProperty(pid)).addValueChangeListener(new Property.ValueChangeListener() {
								
								@Override
								public void valueChange(ValueChangeEvent event) {
									if("jornalPromedio".equals(pid)){
									}else if("suple".equals(pid)){
									}
									Object result = salaryContainer.getItem(itemId).getItemProperty("forceSalary").getValue();
									label.setValue( "<b>"+Utils.formatInteger((Integer) salaryContainer.getContainerProperty(itemId, columnId).getValue())+"</b>"+
											"  ("+Utils.formatInteger((Integer) salaryContainer.getContainerProperty(itemId, "roundSalary").getValue())+")");
									Utils.notifyPropertyValueChanged(item,"jornalBaseMes","vtrato","valorSabado","vsCorrd","sobreTiempo","descHoras","bonifImpo","glegal","afecto","sobreAfecto","cargas","asigFamiliar","colacion","mov","mov2","tnoAfecto");
									createTableFooter(salaryTable);
								}
							});
						return label;
					}
					
				});
				
				salaryTable.setVisibleColumns(salaryTableVisibleTable);
				
				salaryTable.setColumnHeaders("Rol","Nombre","Último<br />Jornal Prom","Jornal Prom","Bono Imp.","Bono no Imp.","Bono Prest.", "Sobre Tiempo","H Desc","V Cuota<br />Prestamo","V Cuota<br />Herramienta","Total Líquido<br />(A Pagar)"
						,"Jornal Base", " V Trato", "Valor Sábado" , "V S Corrd", "Desc Horas","Bonif Imp","G Legal","Afecto","Sobre Afecto","Cargas","A Familiar","Colación","Mov","Movi 2","T No Afecto"
						);
				
				salaryTable.setColumnWidth("jornalPromedio", 100);
				
				salaryTable.setColumnCollapsed("jornalBaseMes", true);
				salaryTable.setColumnCollapsed("vtrato", true);
				salaryTable.setColumnCollapsed("valorSabado", true);
				salaryTable.setColumnCollapsed("vsCorrd", true);
//				salaryTable.setColumnCollapsed("sobreTiempo", true);
				salaryTable.setColumnCollapsed("descHoras", true);
				salaryTable.setColumnCollapsed("bonifImpo", true);
				salaryTable.setColumnCollapsed("glegal", true);
				salaryTable.setColumnCollapsed("afecto", true);
				salaryTable.setColumnCollapsed("sobreAfecto", true);
				salaryTable.setColumnCollapsed("cargas", true);
				salaryTable.setColumnCollapsed("asigFamiliar", true);
				salaryTable.setColumnCollapsed("colacion", true);
				salaryTable.setColumnCollapsed("mov", true);
				salaryTable.setColumnCollapsed("mov2", true);
				salaryTable.setColumnCollapsed("tnoAfecto", true);
				
				salaryTable.addColumnCollapsedListener(new ColumnCollapsedObservableTable.ColumnCollapsedListener() {
					
					@Override
					public void colapseColumn(ColumnCollapsedEvent event) {
						User user = SecurityHelper.getCredentials();
						//agrega la columna al usuario si es que se setea como no colapsada
						if(!event.isCollapsed()){
							user.getSalaryColumns().add((String) event.getPropertyId());
						}else{ //si no lo quita
							user.getSalaryColumns().remove((String) event.getPropertyId());
						}
						user.setPassword(null);
						userService.saveUser(user);
					}
				});
				
				salaryTable.setEditable(true);
				salaryTable.setTableFieldFactory(new TableFieldFactory() {

					@Override
					public Field<?> createField(Container container, final Object itemId,Object propertyId, com.vaadin.ui.Component uiContext) {
						if(propertyId.equals("laborerConstructionSite.activeContract.jobCode")||
								propertyId.equals("laborerConstructionSite.laborer.fullname") ||
								propertyId.equals("totalLiquido") ||
								propertyId.equals("lastJornalPromedio") ||
								propertyId.equals("suple") )
							return null;
						TextField tf = new TextField();
						tf.setWidth("100%");
						tf.setNullRepresentation("");
						tf.setImmediate(true);
						if(propertyId.equals("jornalPromedio")||
						   propertyId.equals("descHours")||
						   propertyId.equals("specialBond")||
						   propertyId.equals("loanBond")||
						   propertyId.equals("bondMov2")){
							tf.addBlurListener(new FieldEvents.BlurListener() {
								
								@Override
								public void blur(BlurEvent event) {
									BeanItem<Salary> beanItem = salaryContainer.getItem(itemId);
									//guarda el salario
									service.save(beanItem.getBean());
								}
							});
						}
						return tf;
					}
				});

				Panel p = new Panel(salaryTable);
				p.getContent().setSizeFull();
				addComponent(salaryTable);
				setExpandRatio(salaryTable, 1.0f);
				
				salaryTable.setFooterVisible(true);
			}
		};
		vl.setSizeFull();
		return vl;
	}
	
	/**
	 * Permite verificar si un contenedor tiene valores nulos o menores a 0 para las propiedades dadas. 
	 * @param container
	 * @param propertyIds
	 * @return El primer itemId del item que tiene una propiedad nula o menor a 0
	 */
	private Object checkHasNegativeOrNull(BeanContainer container,String... propertyIds ) {
		for(Object itemId : container.getItemIds())
			for(String propertyId : propertyIds){
				Property property = container.getContainerProperty(itemId, propertyId);
				if( property == null || 
					property.getValue() == null || 
					((Number)property.getValue()).intValue() < 0 )
					return itemId;
			}
		return null;
	}
	
	/**
	 * Permite crear el footer de la tabla dada, sumando las propiedades de las columnas de tipo double o integer.
	 * Ignora las columnas de jobCode o fullname si las tuviera.
	 * @param table
	 */
	private void createTableFooter(Table table){
		int[] counts = new int[table.getContainerPropertyIds().size()];
		int i = 0;
		for(Object itemId : table.getContainerDataSource().getItemIds()){
			i = 0;
			for(Object propertyId : table.getContainerPropertyIds()){
				if(propertyId.equals("laborerConstructionSite.activeContract.jobCode")||
				propertyId.equals("laborerConstructionSite.laborer.fullname"))
					continue;
				if( table.getContainerProperty(itemId, propertyId).getValue() instanceof Double )
					counts[i] +=  (Double)table.getContainerProperty(itemId, propertyId).getValue();
				else if( table.getContainerProperty(itemId, propertyId).getValue() instanceof Integer )
					counts[i] +=  (Integer)table.getContainerProperty(itemId, propertyId).getValue();
				i++;
			}
		}
		i = 0;
		for(Object propertyId : table.getContainerPropertyIds()){
			if(propertyId.equals("laborerConstructionSite.activeContract.jobCode")||
					propertyId.equals("laborerConstructionSite.laborer.fullname"))
						continue;
			table.setColumnFooter(propertyId, Utils.formatInteger( counts[i] ) );
			i++;
		}	
	}

	/**
	 * Tabla que contiene un listado de todas las posibles ausencias en el sistema (enfermedad, vacaciones, accidentes, etc) y las lista dando la posibilidad
	 * de modificar sus fechas de inicio/final o marcarla como confirmada en el caso que corresponda.
	 * @return
	 */
	private Table drawAbsenceConfirmTable() {
		
		absenceContainer.addNestedContainerProperty("laborerConstructionsite.activeContract.jobCode");
		absenceContainer.addNestedContainerProperty("laborerConstructionsite.laborer.fullname");
		confirmTable = new Table();
		confirmTable.setContainerDataSource(absenceContainer);
		confirmTable.setSizeFull();
		confirmTable.setEditable(true);
		
		confirmTable.addGeneratedColumn("laborerConstructionsite.activeContract.jobCode", new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				return source.getContainerProperty(itemId, columnId).getValue();
			}
		});
		
		confirmTable.addGeneratedColumn("laborerConstructionsite.laborer.fullname", new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				return source.getContainerProperty(itemId, columnId).getValue();
			}
		});
		
		confirmTable.addGeneratedColumn("type", new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				return source.getContainerProperty(itemId, columnId).getValue();
			}
		});
		
		confirmTable.addGeneratedColumn("description", new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				return source.getContainerProperty(itemId, columnId).getValue();
			}
		});
		
		confirmTable.addGeneratedColumn("confirm", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				final AbsenceVO absence = ((BeanItem<AbsenceVO>) source.getItem(itemId)).getBean(); 
				final Button btn = new Button();
				
				toogleButtonState(btn, absence);
				
				btn.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						absence.setConfirmed(!absence.isConfirmed());
						toogleButtonState(btn,absence);
						service.confirmAbsence(absence);
					}
				});
				
				return btn;
			}

			private void toogleButtonState(Button btn, AbsenceVO absence) {
				if(!absence.isConfirmed()){
					btn.removeStyleName(Constants.STYLE_CLASS_RED_COLOR);
					btn.addStyleName(Constants.STYLE_CLASS_GREEN_COLOR);
					btn.setCaption( "Confirmar" );
					btn.setIcon(FontAwesome.CHECK_CIRCLE_O);
				}else{
					btn.removeStyleName(Constants.STYLE_CLASS_GREEN_COLOR);
					btn.addStyleName(Constants.STYLE_CLASS_RED_COLOR);
					btn.setCaption( "Cancelar Confirmación" );
					btn.setIcon(FontAwesome.TIMES_CIRCLE_O);
				}
			}
		});
		
		confirmTable.setVisibleColumns("laborerConstructionsite.activeContract.jobCode","laborerConstructionsite.laborer.fullname","type","description","fromDate","toDate","confirm");
		confirmTable.setColumnHeaders("Rol","Nombre","Tipo","Descripción","Fecha inicio","Fecha Fin","Acción");
		return confirmTable;
	}

	private Grid drawOvertimeGrid() {
		
		overtimeContainer.addNestedContainerProperty("laborerConstructionSite.activeContract.jobCode");
		overtimeContainer.addNestedContainerProperty("laborerConstructionSite.laborer.fullname");
		overtimeContainer.addNestedContainerProperty("laborerConstructionSite.id");
		overtimeContainer.setBeanIdProperty("laborerConstructionSite.id");
		
		overtimeGrid = new Grid(overtimeContainer);
		overtimeGrid.setSelectionMode(SelectionMode.SINGLE);
		//		overtimeGrid.setSizeFull();
		overtimeGrid.setHeight("100%");
		overtimeGrid.setWidth("100%");
		//overtimeGrid.setEditorFieldGroup(new BeanFieldGroup<Overtime>(Overtime.class));
		
		BeanFieldGroup binder = new BeanFieldGroup<Overtime>(Overtime.class);
		binder.setFieldFactory(new EnhancedFieldGroupFieldFactory());
		overtimeGrid.setEditorFieldGroup(binder);
		//overtimeGrid.setEditorFieldGroup(new BeanFieldGroup<EnhancedFieldGroupFieldFactory>(EnhancedFieldGroupFieldFactory.class));
		overtimeGrid.getEditorFieldGroup().addCommitHandler(new CommitHandler() {

			@Override
			public void preCommit(CommitEvent commitEvent) throws CommitException {

			}

			@Override
			public void postCommit(CommitEvent commitEvent) throws CommitException {
				//guarda el elmento
				Overtime overtime = ((BeanItem<Overtime>) commitEvent.getFieldBinder().getItemDataSource()).getBean();
				service.save(overtime);
				overtimeContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});
				
				//por cada variable
				createGridFooters(overtimeGrid);
			}
		});

		overtimeGrid.addStyleName("grid-attendace");
		overtimeGrid.setEditorEnabled(true);
		overtimeGrid.setFrozenColumnCount(3);
		if(overtimeGrid.getColumn("laborerConstructionSite") != null )
			overtimeGrid.removeColumn("laborerConstructionSite");
		if(overtimeGrid.getColumn("laborerConstructionSite.id") != null )
			overtimeGrid.removeColumn("laborerConstructionSite.id");
		if(overtimeGrid.getColumn("date") != null )
			overtimeGrid.removeColumn("date");
		if(overtimeGrid.getColumn("overtimeAsList") != null )
			overtimeGrid.removeColumn("overtimeAsList");
		if(overtimeGrid.getColumn("id") != null )
			overtimeGrid.removeColumn("id");
		if(overtimeGrid.getColumn("lastMonthOvertimeAsList") != null )
			overtimeGrid.removeColumn("lastMonthOvertimeAsList");

		overtimeContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});
		overtimeGrid.getColumn("laborerConstructionSite.activeContract.jobCode").setHeaderCaption("Rol").setEditorField(new TextField(){{setReadOnly(true);}}).setWidth(50);
		overtimeGrid.getColumn("laborerConstructionSite.laborer.fullname").setHeaderCaption("Nombre").setEditorField(new TextField(){{setReadOnly(true);}});
		overtimeGrid.getColumn("total").setHeaderCaption("Total");
         
		createHeaders(overtimeGrid);
		setOvertimeOrders();
		
		return overtimeGrid;
	}

	private void setOvertimeOrders() {
		String[] s = new String[]{ "laborerConstructionSite.activeContract.jobCode","laborerConstructionSite.laborer.fullname","total",
				"dmp1","dmp2","dmp3","dmp4","dmp5","dmp6","dmp7","dmp8","dmp9","dmp10","dmp11","dmp12","dmp13","dmp14","dmp15","dmp16"
				,"dmp17","dmp18","dmp19","dmp20","dmp21","dmp22","dmp23","dmp24","dmp25","dmp26","dmp27","dmp28","dmp29","dmp30","dmp31",
				"dma1","dma2","dma3","dma4","dma5","dma6","dma7","dma8","dma9","dma10","dma11","dma12","dma13","dma14","dma15","dma16"
				,"dma17","dma18","dma19","dma20","dma21","dma22","dma23","dma24","dma25","dma26","dma27","dma28","dma29","dma30","dma31"};
		List<String> sList = new ArrayList<String>(s.length);
		for(String ss : s){
			if(overtimeGrid.getColumn(ss) != null)
				sList.add(ss);
		}
		overtimeGrid.setColumnOrder(sList.toArray(new String[sList.size()]));		
	}

	private Grid drawAttendanceGrid() {
		logger.debug("SEGUNDO");
		attendanceContainer.addNestedContainerProperty("laborerConstructionSite.activeContract.jobCode");
		attendanceContainer.addNestedContainerProperty("laborerConstructionSite.laborer.fullname");
		attendanceContainer.addNestedContainerProperty("laborerConstructionSite.id");
		attendanceContainer.setBeanIdProperty("laborerConstructionSite.id");
		attendanceGrid = new Grid(attendanceContainer);
		attendanceGrid.setSelectionMode(SelectionMode.SINGLE);
		attendanceGrid.setSizeFull();
		BeanFieldGroup<Attendance> bfg = new BeanFieldGroup<Attendance>(Attendance.class);
		bfg.addCommitHandler(new CommitHandler() {
			
			@Override
			public void preCommit(CommitEvent commitEvent) throws CommitException {
			}
			
			@Override
			public void postCommit(CommitEvent commitEvent) throws CommitException {
				BeanItem<Attendance> item = (BeanItem<Attendance>) commitEvent.getFieldBinder().getItemDataSource();
				Attendance attendance = item.getBean(); 
				
				Item salaryItem = salaryContainer.getItem(attendance.getLaborerConstructionSite().getId());
				if(salaryItem == null )
					return;
				Property prop = salaryItem.getItemProperty("attendance");
				prop.setValue(attendance);
				
				salaryItem.getItemProperty("forceSalary").getValue();
				salaryItem.getItemProperty("forceSuple").getValue();
				
				ids.put(attendance.getLaborerConstructionSite().getLaborer().getId(), true);
			}
		});
		attendanceGrid.setEditorFieldGroup(bfg);
		attendanceGrid.setEditorFieldFactory(new DefaultFieldGroupFieldFactory(){
			@Override
			public <T extends Field> T createField(Class<?> type, Class<T> fieldType) {

				if (type.isAssignableFrom(AttendanceMark.class) && fieldType.isAssignableFrom(ComboBox.class)) {
					ComboBox cb = new ComboBox();
					cb.setImmediate(true);
					for(AttendanceMark a : AttendanceMark.values()){
						cb.addItem(a);
					}
					return (T) cb;
				}
				return super.createField(type, fieldType);
			}
		});
		attendanceGrid.getEditorFieldGroup().addCommitHandler(new CommitHandler() {

			@Override
			public void preCommit(CommitEvent commitEvent) throws CommitException {

			}

			@Override
			public void postCommit(CommitEvent commitEvent) throws CommitException {
				//guarda el elmento
				Attendance attedance = ((BeanItem<Attendance>) commitEvent.getFieldBinder().getItemDataSource()).getBean();
				service.save(attedance);
				attendanceContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});
				//por cada variable
				createGridFooters(attendanceGrid);
			}
		});

		attendanceGrid.addStyleName("grid-attendace");
		attendanceGrid.setEditorEnabled(true);
		attendanceGrid.setFrozenColumnCount(2);
		if(attendanceGrid.getColumn("laborerConstructionSite") != null )
			attendanceGrid.removeColumn("laborerConstructionSite");
		if(attendanceGrid.getColumn("laborerConstructionSite.id") != null )
			attendanceGrid.removeColumn("laborerConstructionSite.id");
		if(attendanceGrid.getColumn("id") != null )
			attendanceGrid.removeColumn("id");
		if(attendanceGrid.getColumn("date") != null )
			attendanceGrid.removeColumn("date");
		if(attendanceGrid.getColumn("marksAsList") != null )
			attendanceGrid.removeColumn("marksAsList");
		if(attendanceGrid.getColumn("lastMarksAsList") != null )
			attendanceGrid.removeColumn("lastMarksAsList");

		attendanceContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});
		attendanceGrid.getColumn("laborerConstructionSite.activeContract.jobCode").setHeaderCaption("Rol").setWidth(50).setEditorField(new TextField(){{setReadOnly(true);}});
		attendanceGrid.getColumn("laborerConstructionSite.laborer.fullname").setHeaderCaption("Nombre").setWidth(120).setEditorField(new TextField(){{setReadOnly(true);}});

		createHeaders(attendanceGrid);
		setAttendanceOrder();
		
		return attendanceGrid;
	}
	
	private void createGridFooters(Grid grid){
		FooterRow footer = null;
		if( grid.getFooterRowCount() == 1 )
			footer = grid.getFooterRow(0);
		else
			footer = grid.appendFooterRow();
		//por cada variable
		int[] counts = new int[grid.getContainerDataSource().getContainerPropertyIds().size()];
		for(Object attendanceId : grid.getContainerDataSource().getItemIds() ) {
			BeanItem attendanceItem = (BeanItem) grid.getContainerDataSource().getItem(attendanceId);
			int i = 0;
			for(Object propertyId : grid.getContainerDataSource().getContainerPropertyIds() ){
				if(propertyId.equals("laborerConstructionSite.activeContract.jobCode")||
						propertyId.equals("laborerConstructionSite.laborer.fullname"))
							continue;
				//cuenta las X
				if( attendanceItem.getBean() instanceof Attendance ){
					if( attendanceItem.getItemProperty(propertyId).getValue() instanceof AttendanceMark &&
						( (AttendanceMark)attendanceItem.getItemProperty(propertyId).getValue()  == AttendanceMark.ATTEND ||
						  (AttendanceMark)attendanceItem.getItemProperty(propertyId).getValue()  == AttendanceMark.SATURDAY ||
						  (AttendanceMark)attendanceItem.getItemProperty(propertyId).getValue()  == AttendanceMark.SUNDAY
						))
						counts[i] ++;
				}else if( attendanceItem.getBean() instanceof Overtime ){
					if( attendanceItem.getItemProperty(propertyId).getValue() instanceof Integer  )
						counts[i] += (Integer)attendanceItem.getItemProperty(propertyId).getValue();
				}
				i++;
			}
		}
		int i = 0;
		final DateTime dt = getAttendanceDate();
		for(Object propertyId : grid.getContainerDataSource().getContainerPropertyIds() ){
			if(propertyId.equals("laborerConstructionSite.activeContract.jobCode")||
					propertyId.equals("laborerConstructionSite.laborer.fullname"))
						continue;
			FooterCell cell = footer.getCell(propertyId);
			if(cell != null ) {
				cell.setText(counts[i]+"");
//				//calculo de la semana
//				if(((String) propertyId).startsWith("dmp") || ((String) propertyId).startsWith("dma")  ){
//					//calcula el numero del mes
//					int monthDay = Integer.parseInt(((String) propertyId).replace("dmp","").replace("dma",""));
//					DateTime dt2 = dt;
//					if ( ((String) propertyId).startsWith("dmp") )
//						dt2 = dt2.minusMonths(1);
//					dt2.withDayOfMonth(monthDay);
//					//el número de mes no es un número válido de mes o si es un día a la fecha de cierre del mes pasado, oculta la columna
//					if(dt2.getDayOfWeek() == DateTimeConstants.SATURDAY || dt2.getDayOfWeek() == DateTimeConstants.SUNDAY ){
						cell.setStyleName("red-color bold");
//					}else{ //solo lo setea si el número es mayor a la cantidad de dias del mes
//						cell.setStyleName("");
//					}
//				}
				
			}
			i++;
		}
	}
	String[] attendanceOrder = new String[]{"laborerConstructionSite.activeContract.jobCode","laborerConstructionSite.laborer.fullname",
			"dmp1","dmp2","dmp3","dmp4","dmp5","dmp6","dmp7","dmp8","dmp9","dmp10","dmp11","dmp12","dmp13","dmp14","dmp15","dmp16"
			,"dmp17","dmp18","dmp19","dmp20","dmp21","dmp22","dmp23","dmp24","dmp25","dmp26","dmp27","dmp28","dmp29","dmp30","dmp31",
			"dma1","dma2","dma3","dma4","dma5","dma6","dma7","dma8","dma9","dma10","dma11","dma12","dma13","dma14","dma15","dma16"
			,"dma17","dma18","dma19","dma20","dma21","dma22","dma23","dma24","dma25","dma26","dma27","dma28","dma29","dma30","dma31"};

	private void setAttendanceOrder() {
		
		List<String> sList = new ArrayList<String>(attendanceOrder.length);
		for(String ss : attendanceOrder){
			if(attendanceGrid.getColumn(ss) != null)
				sList.add(ss);
		}
		attendanceGrid.setColumnOrder(sList.toArray(new String[sList.size()]));		
	}

	Property.ValueChangeListener listener = new Property.ValueChangeListener() {

		@Override
		public void valueChange(ValueChangeEvent event) {
			attendanceDate.removeValueChangeListener(listener);
			populateAttendanceGrid();
			attendanceDate.addValueChangeListener(listener);
		}
	};

	@SuppressWarnings("serial")
	private HorizontalLayout drawTopAttendance() {
		return new HorizontalLayout(){
			{
				setWidth("100%");
				addComponent(new HorizontalLayout(){
					{
						setSpacing(true);
						addComponent(new FormLayout(){
							{
								attendanceDate  =  new InlineDateField("Mes");
								attendanceDate.setResolution(Resolution.MONTH);
								attendanceDate.setValue(new Date());
								attendanceDate.setImmediate(true);
								attendanceDate.addValueChangeListener(listener);
								addComponent(attendanceDate);
							}
						});

						addComponent(new HorizontalLayout(){
							{
								addComponent(new Label("Última Carga Información Reloj: "));
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
						
						//Suple
						addComponent(new HorizontalLayout(){
							{
								addComponent(new Button("Configurar Suple", new Button.ClickListener() {

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
														setWidth("980px");
														setHeight("610px");
														setSpacing(true);

														final FieldGroup fg = new FieldGroup();
														
														if( advancepayment  == null ){
															advancepayment = confService.findAdvancePaymentConfigurationsByCS(cs);
															advancepayment  = new AdvancePaymentConfigurations();
														}
														
														fg.setItemDataSource(new BeanItem<AdvancePaymentConfigurations>(advancepayment));
														
														final Property.ValueChangeListener listener = new Property.ValueChangeListener() {
															
															@Override
															public void valueChange(ValueChangeEvent event) {
																try {
																	fg.commit();
																	AdvancePaymentConfigurations bean = ((BeanItem<AdvancePaymentConfigurations>)fg.getItemDataSource()).getBean();
																	confService.save(bean);
																} catch (Exception e) {
																	logger.error("Error al guardar las propiedades de suple",e);
																	Notification.show("Error al guardar");
																}
															}
														};														
														
														addComponent( new VerticalLayout(){
															{			
																setSpacing(true);																
																addComponent(new Panel("Configurar Suple", new VerticalLayout(){
																	{
																		setMargin(true);
																		addComponent(new FormLayout(){
																			{
																				Field permissionDiscount = fg.buildAndBind("Descuento por Permiso", "permissionDiscount");
																				((TextField)permissionDiscount).setNullRepresentation("");
																				permissionDiscount.addValueChangeListener(listener);
																				
																				Field failureDiscount = fg.buildAndBind("Descuento por Falla", "failureDiscount");
																				((TextField)failureDiscount).setNullRepresentation("");
																				failureDiscount.addValueChangeListener(listener);
																				
																				addComponent(permissionDiscount);
																				addComponent(failureDiscount);
																			}
																		});
																		
																		
																		final BeanItemContainer<AdvancePaymentItem> container = new BeanItemContainer<AdvancePaymentItem>(AdvancePaymentItem.class,advancepayment.getAdvancePaymentTable());
																		
																		HorizontalLayout hl = new HorizontalLayout(){
																			{
																				setSpacing(true);
																				final TextField supleCode = new TextField("Código Suple");
																				addComponent(new FormLayout(supleCode));
																				Button add = new Button(null,new Button.ClickListener() {
																					
																					@Override
																					public void buttonClick(ClickEvent event) {
																						try{
																							
																							AdvancePaymentItem advancePaymentItem = new AdvancePaymentItem();
																							advancePaymentItem.setSupleCode(Integer.valueOf(supleCode.getValue()));
																							advancepayment.setConstructionSite(cs);
																							advancepayment.addAdvancePaymentItem(advancePaymentItem);
																							confService.save(advancepayment);
																							
																							container.addBean(advancePaymentItem);
																						}catch(Exception e){
																							Notification.show("Error al agregar el nuevo suple",Type.ERROR_MESSAGE);
																							logger.error("Error al agregar el nuevo suple",e);
																						}
																					}
																			}){
																					{
																						setIcon(FontAwesome.PLUS);
																					}
																				};
																				addComponent(add);
																				setComponentAlignment(add, Alignment.MIDDLE_CENTER);
																			}
																		};
																		addComponent(hl);

																		final Table table = new Table("Tabla Anticipo"){
																			{
																				setSizeFull();
																				setPageLength(5);
																			}
																		};
																		
																		table.setContainerDataSource(container);		
																		
																		table.setTableFieldFactory(new ListenerFieldFactory(listener));
																		table.addGeneratedColumn("eliminar", new Table.ColumnGenerator() {
																			
																			@Override
																			public Object generateCell(Table source,final Object itemId, Object columnId) {
																				return new Button(null,new Button.ClickListener() {
																					
																					final AdvancePaymentItem advancePaymentItem = container.getItem(itemId).getBean();
																					@Override
																					public void buttonClick(ClickEvent event) {
																						ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar el suple seleccionado?",
																								"Eliminar", "Cancelar", new ConfirmDialog.Listener() {

																							public void onClose(ConfirmDialog dialog) {
																								if (dialog.isConfirmed()) {
																									try{
																										advancepayment.removeAdvancePaymentItem(advancePaymentItem);
																										confService.save(advancepayment);
																										container.removeItem(itemId);
																									}catch(Exception e){
																										Notification.show("Error al quitar elemento",Type.ERROR_MESSAGE);
																										logger.error("Error al eliminar un suple",e);
																									}
																								}
																							}
																						});
																					}
																				}){
																					{setIcon(FontAwesome.TRASH_O);}
																				};
																			}
																		});
																		
																		table.addGeneratedColumn("supleTotalAmount", new Table.ColumnGenerator() {

																			@Override
																			public Object generateCell(Table source, Object itemId, Object columnId) {

																				final BeanItem<?> item = (BeanItem<?>) source.getItem(itemId);
																				Double value = (Double) source.getContainerProperty(itemId, columnId).getValue();																				
																				final Label label  = new Label(""+value.intValue());
																				Property.ValueChangeListener listener = new Property.ValueChangeListener() {
																					@Override
																					public void valueChange(Property.ValueChangeEvent event) {
																						label.setValue(((AdvancePaymentItem) item.getBean()).getSupleTotalAmount().intValue()+"");
																					}
																				};
																				for (String pid: new String[]{"supleIncreaseAmount", "supleNormalAmount"})
																					((ValueChangeNotifier)item.getItemProperty(pid)).addValueChangeListener(listener);

																				return label; 
																			}
																		});
																		
																		table.setVisibleColumns("supleCode","supleTotalAmount","supleNormalAmount","supleIncreaseAmount","eliminar");
																		table.setColumnHeaders("Código Suple","Monto Suple","Normal","Aumento Anticipo","Eliminar");
																		table.setEditable(true);
																		
																		addComponent(table);

																		if(!SecurityHelper.hasPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
																			setEnabled(false);
																		}else{
																			setEnabled(true);
																		}
																	}
																}));															
															}
														});
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
										setIcon(FontAwesome.DOLLAR);
									}
								});
							}
						});
						
						//Exportar a excel
						addComponent(new HorizontalLayout(){
							{
								
								Button btnExportAttendance = new Button("Exportar a Excel",FontAwesome.FILE_ARCHIVE_O);
								addComponent(btnExportAttendance);
								//recupera la lista de sueldos
								StreamResource.StreamSource myResource = new StreamResource.StreamSource() {

									public InputStream getStream() {
										XSSFWorkbook wb = null;

										//1. Open the file
										try {
											wb = new XSSFWorkbook(new FileInputStream(new File(AttendancePanel.class.getResource("/templates/asistencia/planilla_asistencia.xlsx").toURI())));
											//pone la asistencia en la segunda pestaña
											XSSFSheet sheet = wb.getSheetAt(1);
											int i = 11;
											for(Object itemId : attendanceContainer.getItemIds()){
												
												BeanItem<Attendance> attendanceItem = attendanceContainer.getItem(itemId);
												BeanItem<Salary> salaryItem = salaryContainer.getItem(itemId);
												BeanItem<Overtime> overtimeItem = overtimeContainer.getItem(itemId);
												
												Salary salary = salaryItem.getBean();
												Attendance attendance = attendanceItem.getBean();
												Overtime overtime = overtimeItem.getBean();
												
												Row row = sheet.getRow(i++);
												
												//información del trabajador
												row.getCell(0).setCellValue(attendance.getLaborerConstructionSite().getJobCode());
												row.getCell(1).setCellValue(attendance.getLaborerConstructionSite().getLaborer().getFullname());
												//asistencia
												int j = 3;
												for(AttendanceMark mark : attendance.getMarksAsList()){
													if(j == 17)
														j++;
													row.getCell(j).setCellValue(mark.toString());
													j++;
												}
												//jornal promedio
												row.getCell(44).setCellValue(salary.getJornalPromedio());
												//horas desc TODO
												row.getCell(46).setCellValue(salary.getDescHoras());
												//herramientas TODO
												row.getCell(48).setCellValue(salary.getTools());
												//prestamos TODO
												row.getCell(49).setCellValue(salary.getLoan());
												//información del trabajador y su contrato
												row.getCell(52).setCellValue(salary.getLaborerConstructionSite().getLaborer().getRut());
												row.getCell(53).setCellValue(attendance.getLaborerConstructionSite().getActiveContract().getStartDate());
												if(attendance.getLaborerConstructionSite().getActiveContract().getTerminationDate() != null )
													row.getCell(54).setCellValue(attendance.getLaborerConstructionSite().getActiveContract().getTerminationDate());
												//codigo suple 
												row.getCell(57).setCellValue(salary.getLaborerConstructionSite().getSupleCode());
												//ajuste sobre tiempo 60 74
												
												//sobre tiempo 75 103
												int k = 75;
												for(Integer o : overtime.getOvertimeAsList()){
													if(o != null )
														row.getCell(k).setCellValue(o);
													k++;
												}
												
												//loc mov2 105
												row.getCell(105).setCellValue(salary.getBondMov2());
												//bono especial 108
												row.getCell(108).setCellValue(salary.getBonifImpo());
												//ajuste mes anterior 117 a 130
												k = 117;
												for(AttendanceMark m : salary.getAjusteMesAnterior()){
													if(m != null)
														row.getCell(k).setCellValue(m.toString());
													k++;
												}
												// TODO salud isapre , monto 152 153
												row.getCell(152).setCellValue(salary.getLaborerConstructionSite().getLaborer().getIsapre().toString());
//												row.getCell(153).setCellValue(salary.getLaborerConstructionSite().getLaborer().getIsapre().toString());
												// TODO afp y espacio?= 156 157
												row.getCell(156).setCellValue(salary.getLaborerConstructionSite().getLaborer().getAfp().toString());
//												row.getCell(157).setCellValue(salary.getLaborerConstructionSite().getLaborer().getIsapre().toString()); 
												
											}
											try { HSSFFormulaEvaluator.evaluateAllFormulaCells(wb); }catch(Exception e){}
											
											ByteArrayOutputStream outputStream =  new ByteArrayOutputStream();
											wb.write(outputStream);
											InputStream stream =  new ByteArrayInputStream(outputStream.toByteArray());
											return stream;
										}catch(Exception e){
											logger.error("Error al generarl el excel",e);
											Notification.show("Error al generar el archivo excel.");
										}
										return null;
										
									}
								};
								StreamResource resource = new StreamResource(myResource, "pruebaAsistencia.xlsx"); //TODO mes_año_codobra_ant/liq.txt
								 FileDownloader fileDownloader = new FileDownloader(resource);
							     fileDownloader.extend(btnExportAttendance);
							}});
					}
				});



			}
		};
	}
	

	private void generateSoftlandFile(final Button btnExportSoftland) {
		
		//recupera la lista de sueldos
		StreamResource.StreamSource myResource = new StreamResource.StreamSource() {

			public InputStream getStream() {
				//recupera la lista de sueldos
				List<Salary> salaries = new ArrayList<Salary>(salaryContainer.size());
				for(Object itemId : salaryContainer.getItemIds()){
					salaries.add(salaryContainer.getItem(itemId).getBean());
				}
				
				final Map<String, Object> input = new HashMap<String, Object>();
				input.put("salaries", salaries);
				VelocityHelper.addTools(input);
				
				final StringBuilder sb = new StringBuilder();

				// contrato
				sb.append( VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/export/salary.vm", "UTF-8", input));
				
				InputStream stream =  new ByteArrayInputStream(sb.toString().getBytes());
//				btnExportSoftland.setEnabled(true);
				return stream;
			}
		};
		StreamResource resource = new StreamResource(myResource, "pruebaSalary.txt"); //TODO mes_año_codobra_ant/liq.txt
		
        FileDownloader fileDownloader = new FileDownloader(resource);
        fileDownloader.extend(btnExportSoftland);
	}
	
	private void generateSupleSoftlandFile(final Button btnExportSoftland) {
		
		
		StreamResource.StreamSource myResource = new StreamResource.StreamSource() {

			public InputStream getStream() {
				//recupera la lista de sueldos
				List<Salary> salaries = new ArrayList<Salary>(salaryContainer.size());
				for(Object itemId : salaryContainer.getItemIds()){
					salaries.add(salaryContainer.getItem(itemId).getBean());
				}
				
				final Map<String, Object> input = new HashMap<String, Object>();
				input.put("salaries", salaries);
				VelocityHelper.addTools(input);
				
				final StringBuilder sb = new StringBuilder();

				// contrato
				sb.append( VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/export/suple.vm", "UTF-8", input));
				
				InputStream stream =  new ByteArrayInputStream(sb.toString().getBytes());
//				btnExportSoftland.setEnabled(true);
				return stream;
			}
		};
		StreamResource resource = new StreamResource(myResource, "pruebaSuple.txt"); //mes_año_codobra_ant/liq.txt
		
        FileDownloader fileDownloader = new FileDownloader(resource);
        fileDownloader.extend(btnExportSoftland);
        
	}

	/**
	 * Muestra o no los botones según el perfil del usuario
	 */
	private void configureInterface() {	

		//se preocupa de deshabilitar o habilitar los componentes
		enableOrDisableComponents();
		//se preocupa de mostrar o esconder los botones
		setVisibilityOfButtons();
		//define los estados de los botones
		toogleButtonState(btnConstructionSiteConfirm,getConfirmations().isConstructionSiteCheck());
		toogleButtonState(btnCentralConfirm,getConfirmations().isCentralCheck());
		toogleButtonState(btnSupleObraConfirm, confirmations.isSupleObraCheck());
		toogleButtonState(btnSupleCentralConfirm, confirmations.isSupleCentralCheck());
		
		//se preocupa de deshabilitar o habilitar los botones de confirmación
		enableOrDisableButtons();
	}


	private void populateAttendanceGrid(){

		//		UI.getCurrent().addWindow(progressDialog);

		//		final WorkThread thread = new WorkThread();
		//		thread.start();
		DateTime dt = getAttendanceDate();
		
		
		createHeaders(attendanceGrid);
		setAttendanceOrder();
		
		createHeaders(overtimeGrid);
		setOvertimeOrders();
		
		//		DateTime attendanceDate = dt.withDayOfMonth(dt.dayOfMonth().getMinimumValue());
		//		DateTime date2 = attendanceDate.withDayOfMonth( attendanceDate.dayOfMonth().getMaximumValue() );
		reloadMonthGridData(dt);

		reloadMonthAttendanceData(dt);
		
		configureInterface();
		
		createGridFooters(attendanceGrid);
		
		createGridFooters(overtimeGrid);
		
		createTableFooter(salaryTable);
		
		createTableFooter(supleTable);
		
		//recupera las columnas de la tabla de sueldos seleccionadas del usuario
		User user = SecurityHelper.getCredentials();
		//si no tiene ninguna columna seleccionada, le agrega las por defecto
		if( user.getSalaryColumns().isEmpty() ){
			for(String s : defaultSalaryTableVisibleTable){
				user.getSalaryColumns().add(s);
			}
			user.setPassword(null);
			userService.saveUser(user);
		}
		for(String column : salaryTableVisibleTable ){
			salaryTable.setColumnCollapsed(column, !user.getSalaryColumns().contains(column));
		}

		// Enable polling and set frequency to 1 seconds
		//		UI.getCurrent().setPollInterval(1000);
	}

	private void reloadMonthGridData(DateTime dt) {

		if(cs == null ){
//			throw new RuntimeException("No se ha seteado la obra en la vista de asistencia.");
			
		}
		//cuenta la cantidad de dias entre ambas fechas
		//		int days = Days.daysBetween(initialDate, lastDate).getDays() + 1;

		//		logger.debug("se van a generar las filas para {} dias",days);

		clearGrids();
		try{
			
			advancepayment = confService.findAdvancePaymentConfigurationsByCS(cs);
			if(advancepayment ==  null )
				advancepayment  = new AdvancePaymentConfigurations();

			List<Overtime> overtime = service.getOvertimeByConstruction(cs,dt);
			overtimeContainer.addAll(overtime);
			overtimeContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});

			List<Attendance> attendance = service.getAttendanceByConstruction(cs,dt);
			attendanceContainer.addAll(attendance);
			attendanceContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});

			List<AbsenceVO> absences = service.getAbsencesByConstructionAndMonth(cs,dt);
			absenceContainer.addAll(absences);
			absenceContainer.sort(new String[]{"laborerConstructionsite.activeContract.jobCode"},new boolean[]{ true });

			List<Salary> salaries = service.getSalariesByConstructionAndMonth(cs,dt);
			salaryContainer.addAll(salaries);
			salaryContainer.sort(new String[]{"laborerConstructionsite.activeContract.jobCode"},new boolean[]{ true });

		}catch(Exception e){
			logger.error("Error al calcular los sueldos",e);
			String mensaje = "Error al calcular los sueldos.";
			if( e.getMessage() != null )
				mensaje = e.getMessage();
			Notification.show(mensaje,Type.ERROR_MESSAGE);
		}
	}
	/**
	 * Según la fecha y la obra, verifica cual es el estado de confirmación de cada una
	 */
	private void reloadMonthAttendanceData(DateTime dt){
		//obteniendo fechas de confirmación
		logger.debug("obteniendo fecha de confirmación");
		confirmations = service.getConfirmationsByConstructionsiteAndMonth(cs,dt);
		logger.debug("end");
	}

	/**
	 * Clase para hacer un trabajo en segundo plano
	 * @author Pablo Carreño
	 *
	 */
	class WorkThread extends Thread {

		@Override
		public void run() {

			try{
				//dependiendo de la fecha seleccionada llena la información de la tabla
				DateTime dt = getAttendanceDate();
				DateTime attendanceDate = dt.withDayOfMonth(dt.dayOfMonth().getMinimumValue());
				DateTime date2 = attendanceDate.withDayOfMonth( attendanceDate.dayOfMonth().getMaximumValue() );

				getUI().getSession().getLockInstance().lock();

				//				configAttendanceColumns(attendanceDate,date2);
				reloadMonthGridData(dt);
				getUI().getSession().getLockInstance().unlock();

			}catch(Exception e){
				logger.error("Error al obtener la asistencia del mes " ,e);
				UI.getCurrent().access(new Runnable() {
					@Override
					public void run() {
						Notification.show("Error al obtener la asistencia del mes",Type.ERROR_MESSAGE);
					}
				});
			}

			// Update the UI thread-safely
			UI.getCurrent().access(new Runnable() {
				@Override
				public void run() {
					// Stop polling
					UI.getCurrent().setPollInterval(-1);
					progressDialog.close();

				}
			});
		}

	}

}
