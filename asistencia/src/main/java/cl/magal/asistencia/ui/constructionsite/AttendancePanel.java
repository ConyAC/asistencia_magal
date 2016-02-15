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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.velocity.app.VelocityEngine;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.vaadin.dialogs.ConfirmDialog;

import cl.magal.asistencia.entities.AdvancePaymentConfigurations;
import cl.magal.asistencia.entities.AdvancePaymentItem;
import cl.magal.asistencia.entities.AfpAndInsuranceConfigurations;
import cl.magal.asistencia.entities.Attendance;
import cl.magal.asistencia.entities.Confirmations;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Contract;
import cl.magal.asistencia.entities.CostAccount;
import cl.magal.asistencia.entities.DateConfigurations;
import cl.magal.asistencia.entities.FamilyAllowanceConfigurations;
import cl.magal.asistencia.entities.HistoricalSalary;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.Overtime;
import cl.magal.asistencia.entities.Salary;
import cl.magal.asistencia.entities.TaxationConfigurations;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.WageConfigurations;
import cl.magal.asistencia.entities.enums.AttendanceMark;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.repositories.CostAccountRepository;
import cl.magal.asistencia.repositories.LaborerConstructionsiteRepository;
import cl.magal.asistencia.services.ConfigurationService;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.services.MailService;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.services.bo.SalaryCalculator.ProjectedAttendanceNotDefined;
import cl.magal.asistencia.ui.ListenerFieldFactory;
import cl.magal.asistencia.ui.MagalUI;
import cl.magal.asistencia.ui.components.ColumnCollapsedObservableTable;
import cl.magal.asistencia.ui.components.ColumnCollapsedObservableTable.ColumnCollapsedEvent;
import cl.magal.asistencia.ui.vo.AbsenceVO;
import cl.magal.asistencia.util.Constants;
import cl.magal.asistencia.util.OnDemandFileDownloader;
import cl.magal.asistencia.util.OnDemandFileDownloader.OnDemandStreamResource;
import cl.magal.asistencia.util.SecurityHelper;
import cl.magal.asistencia.util.Utils;
import cl.magal.asistencia.util.VelocityHelper;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Container.SimpleFilterable;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.data.Validator.InvalidValueException;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.CellReference;
import com.vaadin.ui.Grid.CommitErrorEvent;
import com.vaadin.ui.Grid.EditorErrorHandler;
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
	private transient ConstructionSiteService constructionSiteService;
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
	@Autowired
	LaborerConstructionsiteRepository labcsRepo;
	@Autowired
	private transient CostAccountRepository costService;

	AdvancePaymentConfigurations advancepayment;
	/** CONTAINERS **/
	BeanContainer<Long,Attendance> attendanceContainer = new BeanContainer<Long,Attendance>(Attendance.class);
	BeanContainer<Long,Overtime> overtimeContainer = new BeanContainer<Long,Overtime>(Overtime.class);
	BeanContainer<Long,Salary> salaryContainer = new BeanContainer<Long,Salary>(Salary.class);
	BeanItemContainer<AbsenceVO> absenceContainer = new BeanItemContainer<AbsenceVO>(AbsenceVO.class);
	BeanItemContainer<ConstructionSite> constructionContainer = new BeanItemContainer<ConstructionSite>(ConstructionSite.class);
	BeanItemContainer<CostAccount> costContainer = new BeanItemContainer<CostAccount>(CostAccount.class);

	/** COMPONENTES **/
	ProgressBar progress;
	Label status;
	Grid attendanceGrid, overtimeGrid;
	Window progressDialog;
	InlineDateField attendanceDate;
	Button btnExportSoftland,btnExportSupleSoftland,btnConstructionSiteConfirm,btnCentralConfirm,btnSupleObraConfirm,btnSupleCentralConfirm;
	Table confirmTable;
	TabSheet tab;
	//	VerticalLayout root;
	Table supleTable;
	ColumnCollapsedObservableTable salaryTable;

	/** ATRIBUTOS **/
	Confirmations confirmations;
	ConstructionSite cs;

	final static String[] defaultSalaryTableVisibleTable = new String[]{"laborerConstructionSite.activeContract.jobCode",
		"laborerConstructionSite.laborer.fullname","lastJornalPromedio","jornalPromedio","specialBond","bondMov2","loanBond","sobreTiempo","descHours","loan","tools","totalLiquido"
	};
	final static String[] salaryTableVisibleTable = new String[]{
		"laborerConstructionSite.activeContract.jobCode",//1
		"laborerConstructionSite.laborer.fullname",//2
		"costAccount",
		"lastJornalPromedio",//3
		"jornalPromedio",//4
		"specialBond",//5
		"bondMov2",//6
		"loanBond",//7
		"sobreTiempo",//8
		"descHours",//9
		"loan",//10
		"tools",//11
		"totalLiquido",//12
		"salaryCalculator.diaTrab",//13
		"salaryCalculator.sab",//14
		"salaryCalculator.sep",//15
		"salaryCalculator.dps",//16
		"salaryCalculator.dpd",//17
		"salaryCalculator.col",//18
		"salaryCalculator.mov",//19
		"jornalBaseMes",//20
		"vtrato",//21
		"valorSabado",//22
		"vsCorrd",//23
		"descHoras","bonifImpo","glegal","afecto","sobreAfecto","cargas","asigFamiliar","colacion","mov","mov2","tnoAfecto"
	};
	
	final static String[] historicalSalaryTableVisibleTable = new String[]{"laborerConstructionSite.activeContract.jobCode",
		"laborerConstructionSite.laborer.fullname","costAccount.code","lastJornalPromedio","jornalPromedio","specialBond","bondMov2","loanBond","sobreTiempo","descHours","loan","tools","totalLiquido",
		"diaTrab","sab","sep","dps","dpd","col","mov"
		,"jornalBaseMes","vtrato","valorSabado","vsCorrd","descHoras","bonifImpo","glegal","afecto","sobreAfecto","cargas","asigFamiliar","colacion","mov2","tnoAfecto"
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
			attendanceDate.setValue(new DateTime(DateTimeZone.UTC).dayOfMonth().withMinimumValue().toDate()); //agrega el primer día del mes actual
		}
		return new DateTime(attendanceDate.getValue(),DateTimeZone.UTC);
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
	 * 
	 * obtiene las configuraciones del mes seleccionado
	 * @return
	 */
	private AfpAndInsuranceConfigurations getAfpAndInsuranceConfigurations(){
		AfpAndInsuranceConfigurations dateConfig = configurationService.findAfpAndInsuranceConfiguration();
		return dateConfig;
	}
	
	private List<TaxationConfigurations> getTaxationConfigurations(){
		List<TaxationConfigurations> dateConfig = configurationService.findTaxationConfigurations();
		return dateConfig;
	}
	
	private List<FamilyAllowanceConfigurations> getFamilyAllowanceConfigurations(){
		List<FamilyAllowanceConfigurations> dateConfig = configurationService.findFamylyAllowanceConfigurations();
		return dateConfig;
	}
	
	private List<CostAccount> getCostByConstructionSite(){
		List<CostAccount> cost = (List<CostAccount>) costService.findByConstructionSite(cs);
		return cost;
	}
	

	DateConfigurations dateConfig = null;
	/**
	 * 
	 * obtiene las configuraciones del mes seleccionado
	 * @return
	 */
	private DateConfigurations getDateConfigurations(){
		DateTime dt = getAttendanceDate();
		if(dateConfig == null)
			dateConfig = configurationService.getDateConfigurationByCsAndMonth(cs,dt);
		return dateConfig;
	}

	DateConfigurations dateConfigPastMonth = null;

	private DateConfigurations getDateConfigurationsPastMonth(){
		DateTime dt = getAttendanceDate();
		if(dateConfigPastMonth == null)
			dateConfigPastMonth = configurationService.getDateConfigurationByCsAndMonth(cs,dt.minusMonths(1));
		return dateConfigPastMonth;
	}

	/**
	 * 
	 * obtiene las configuraciones del mes seleccionado
	 * @return
	 */
	private WageConfigurations getWageConfigurations(){
		WageConfigurations dateConfig = configurationService.findWageConfigurations();
		return dateConfig;
	}

	/**
	 * obtiene la fecha de cierre del mes anterior
	 * @return
	 */
	private DateTime getPastMonthClosingDate(){
		DateTime dt = getAttendanceDate();
		DateConfigurations dateConfig = getDateConfigurationsPastMonth();

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
			logger.debug("recuperando parametros de la url");
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
					cs = constructionSiteService.findConstructionSite(id);
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

		boolean isAttendanceGrid = grid.equals(attendanceGrid);

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

				//TODO si hay que cambiar la fecha de sobretiempo, aqui se deberia discriminar
				//				int maxDay = getPastMonthClosingDate().getDayOfMonth();
				//calculo de la semana
				if(((String) pid).startsWith("dmp") || ((String) pid).startsWith("dma")  ){
					//calcula el numero del mes
					int monthDay = Integer.parseInt(((String) pid).replace("dmp","").replace("dma",""));
					//crea una fecha con el año y mes seleccionado y con el dia recuperado del property actual
					DateTime dt2 = dt;
					try{
						//si el property es dmp(dia mes pasado, entonces retrocede un mes
						if ( ((String) pid).startsWith("dmp") )
							dt2 = dt2.minusMonths(1);
						dt2 = dt2.withDayOfMonth(monthDay);
					}catch(Exception e){ //si no es un día válido del mes válida lo ocula
						dt2 = null;
						if(grid.getColumn(pid) != null)
							grid.removeColumn(pid);
					}
					if(dt2 != null ){ //continua solo si el día es válido

						//si la grid es de asistencia, calcula el rango con el dia de cierre del mes pasado más 1 y el ultimo dia del mes seleccionado
						DateTime startDate,endDate;
						if(isAttendanceGrid){
							startDate = getPastMonthClosingDate().plusDays(1);
							endDate = getAttendanceDate().withDayOfMonth(getAttendanceDate().dayOfMonth().getMaximumValue());
						}else{//si la grid es de sobretiempos, calcula el rango con el dia de inicio del trato y el fin de trato
							startDate = new DateTime( getDateConfigurations().getBeginDeal());
							endDate = new DateTime( getDateConfigurations().getFinishDeal());
						}
						//define el rango
						Interval interval = new Interval(startDate, endDate.plusDays(1)); //le suma un dia, dado que el contains del interval es exclusivo para el final
						//si la fecha que representa el property está dentro del rango, se preocupa de mostrarla y definir sus header y subheader
						if(interval.contains(dt2)){
							if(grid.getColumn(pid) == null)
								grid.addColumn(pid);
							label.setValue( dt2.dayOfWeek().getAsShortText() );
							grid.getColumn(pid).setHeaderCaption(((String) pid).replace("dmp","").replace("dma","")).setSortable(false);
						}else{ //si no la contiene, la oculta
							if(grid.getColumn(pid) != null)
								grid.removeColumn(pid);
						}
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
						if(AttendanceMark.FILLER.equals(cellReference.getValue()))
							post = " r-color";
						else
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
						
						final Button btnGuardar = new Button("Calcular y Guardar",FontAwesome.FLOPPY_O);
						btnGuardar.setDisableOnClick(true);
						btnGuardar.addClickListener(new Button.ClickListener() {
							@Override
							public void buttonClick(ClickEvent event) {
								
								try{
									List<Salary> salaries = new ArrayList<Salary>(salaryContainer.size());
									for(Long itemId : salaryContainer.getItemIds()){
										Salary salary = salaryContainer.getItem(itemId).getBean();
										salaries.add(salary);
									}
									constructionSiteService.saveSalaries(salaries);
									supleTable.refreshRowCache();
									createTableFooter(supleTable);
								}finally{
									btnGuardar.setEnabled(true);
								}
							}
						});
						addComponent(btnGuardar);

						final Button btnValidate = new Button("Validar Negativos",FontAwesome.CHECK_CIRCLE_O);
						btnValidate.setDisableOnClick(true);
						btnValidate.addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								List<Object> itemIds = null;
								if( (itemIds = checkHasNegativeOrNull(salaryContainer,"suple")).size() == 0 ) {
									Notification.show("La lista de trabajadores no tiene valores negativos en el suple.",Type.HUMANIZED_MESSAGE);
								}else{
									//crea el mensaje
									StringBuilder sb = new StringBuilder("Hay "+itemIds.size()+" trabajadores con valores negativos y son:\n");
									int i = 0;
									for(Object itemId : itemIds ){
										BeanItem<Salary> item = salaryContainer.getItem(itemId);
										sb.append(item.getBean().getLaborerConstructionSite().getJobCode());

										i++;
										if( i < itemIds.size() )
											sb.append(",");
										if( i % 8 == 0) //para mostrar de a grupo de 8 maximo
											sb.append("\n");
									}


									Notification.show(sb.toString(),Type.ERROR_MESSAGE);
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
											constructionSiteService.save(confirmations);
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
											constructionSiteService.save(confirmations);
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
					public Object generateCell(Table source,final Object itemId, Object columnId) {
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
						
						final Button btnSuple = new Button(FontAwesome.ARROW_CIRCLE_O_RIGHT);
						btnSuple.setData(false);
						btnSuple.addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								//obliga a calcular el suple con la tabla
								//define el suple como calculado

								synchronized(btnSuple){
									
									beanItem.getItemProperty("calculatedSuple").setValue(true);
									//obliga a que se recalcule el suple
									beanItem.getItemProperty("forceSuple").getValue();
									//lo pide explicitamente para obligar a recalcular el suple
									double suple = (Double) beanItem.getItemProperty("suple").getValue();
									btnSuple.setData(true);
									Boolean b = (Boolean) btnSuple.getData();
									logger.debug("suple calculado {} {}",b,itemId);
									//recupera monto suple de la tabla
									beanItem.getItemProperty("suple").setValue(suple);
								}
								//guarda el trabajador, para guardar el codigo de suple
//								laborerService.save(beanItem.getBean().getLaborerConstructionSite());
								//guarda el salario
//								constructionSiteService.save(beanItem.getBean());
								
								btnSuple.setVisible(false);
								
								
							}							
						});
						btnSuple.setVisible(!(Boolean) beanItem.getItemProperty("calculatedSuple").getValue());

						((ValueChangeNotifier)beanItem.getItemProperty("suple")).addValueChangeListener(new Property.ValueChangeListener() {

							@Override
							public void valueChange(ValueChangeEvent event) {
								//si el valor es distinto al suple que le corresponde, entonces lo marca como no calculado
								// y muestra el botón para volverlo al por defecto

								//primero intenta transformarlo
								//								Double value = (Double) event.getProperty().getValue();	
								//								//luego busca el valor que le corresponderia
								//								Integer supleCode = (Integer) beanItem.getItemProperty("laborerConstructionSite.supleCode").getValue();
								//								if(supleCode == null ) // si aún no se define el codigo de suple, se retorna
								//									return;
								//								Double supleAmount = null;
								//								for(AdvancePaymentItem item : advancepayment.getAdvancePaymentTable() ){
								//									if(item.getSupleCode() == supleCode );
								//										supleAmount = item.getSupleTotalAmount(); 
								//								}
								//								
								//								//si no son iguales los valores
								//								boolean sameValues = supleAmount != null && value != null && supleAmount.doubleValue() == value.doubleValue();
								synchronized(btnSuple){
									Boolean b = (Boolean) btnSuple.getData();
									boolean sameValues = false;
									beanItem.getItemProperty("calculatedSuple").setValue(b);
									btnSuple.setVisible(!b);
									btnSuple.setData(false);
								}
								//independiente de lo que pase, guarda el nuevo suple
//								constructionSiteService.save(beanItem.getBean());

//								createTableFooter(supleTable);
							}
						});

						//						if(!ids.isEmpty()){
						//							Iterator it = ids.keySet().iterator();
						//							while(it.hasNext()){
						//								Long key = (Long) it.next();
						//								if( ids.get(key) && 
						//										key == beanItem.getBean().getLaborerConstructionSite().getLaborer().getId() && 
						//										!beanItem.getItemProperty("calculatedSuple").equals(false))
						//									btnSuple.setVisible(false);
						//							}
						//						}						
						hl.addComponent(btnSuple);

						return hl;
					}
				});

				supleTable.setVisibleColumns("laborerConstructionSite.activeContract.jobCode","laborerConstructionSite.laborer.fullname","supleSection","suple");
				supleTable.setColumnHeaders("Rol","Nombre","Código suple","Suple");
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
//							tf.addValueChangeListener(new Property.ValueChangeListener() {
//
//								@Override
//								public void valueChange(ValueChangeEvent event) {
//									//									logger.debug(" suple value changed ");
//									//									BeanItem<Salary> beanItem = salaryContainer.getItem(itemId);
//									//									beanItem.getItemProperty("calculatedSuple").setValue(false);
//									//									//guarda el salario
//									//									service.save(beanItem.getBean());
//								}
//							});
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
		salaryContainer.addNestedContainerProperty("salaryCalculator.diaTrab");
		salaryContainer.addNestedContainerProperty("salaryCalculator.sab");
		salaryContainer.addNestedContainerProperty("salaryCalculator.sep");
		salaryContainer.addNestedContainerProperty("salaryCalculator.dps");
		salaryContainer.addNestedContainerProperty("salaryCalculator.dpd");
		salaryContainer.addNestedContainerProperty("salaryCalculator.col");
		salaryContainer.addNestedContainerProperty("salaryCalculator.mov");
		salaryContainer.addNestedContainerProperty("costAccount.code");

		VerticalLayout vl = new VerticalLayout(){
			{
				setSpacing(true);
				setMargin(true);
				HorizontalLayout hl = new HorizontalLayout(){
					{

						setSpacing(true);
						
						final Button btnGuardar = new Button("Calcular y Guardar",FontAwesome.FLOPPY_O);
						btnGuardar.setDisableOnClick(true);
						btnGuardar.addClickListener(new Button.ClickListener() {
							@Override
							public void buttonClick(ClickEvent event) {
								salaryTable.refreshRowCache();
								createTableFooter(salaryTable);
								List<Salary> salaries = new ArrayList<Salary>(salaryContainer.size());
								for(Long itemId : salaryContainer.getItemIds()){
									Salary salary = salaryContainer.getItem(itemId).getBean();
											
									try {
										
										salary.getSalaryCalculator().getAjusteMesAnterior(true);
									
									}catch(ProjectedAttendanceNotDefined e){
										Notification.show("Existe asistencia proyectada o real no definida, por lo que no se puede calcular el sueldo.",Type.ERROR_MESSAGE);
										btnGuardar.setEnabled(true);
										return;
									}
									salaries.add(salary);
								}
								constructionSiteService.saveSalaries(salaries);
								btnGuardar.setEnabled(true);
							}
						});
						addComponent(btnGuardar);

						final Button btnValidate = new Button("Validar Negativos",FontAwesome.CHECK_CIRCLE_O);
						btnValidate.setDisableOnClick(true);
						btnValidate.addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								List<Object> itemIds = null;
								if( (itemIds = checkHasNegativeOrNull(salaryContainer,"salary","vtrato","valorSabado","vsCorrd","glegal","afecto","colacion","mov","mov2")).size() == 0 ) {
									Notification.show("La lista de trabajadores no tiene valores negativos en el sueldo ni en el jornal promedio.",Type.HUMANIZED_MESSAGE);
								}else{
									//crea el mensaje
									StringBuilder sb = new StringBuilder("Hay "+itemIds.size()+" trabajadores con valores negativos y son:\n");
									int i = 0;
									for(Object itemId : itemIds ){
										BeanItem<Salary> item = salaryContainer.getItem(itemId);
										sb.append(item.getBean().getLaborerConstructionSite().getJobCode());

										i++;
										if( i < itemIds.size() )
											sb.append(",");
										if( i % 8 == 0) //para mostrar de a grupo de 8 maximo
											sb.append("\n");
									}


									Notification.show(sb.toString(),Type.ERROR_MESSAGE);
								}
								btnValidate.setEnabled(true);
							}

						});
						addComponent(btnValidate);
						
						final Button btnValidarCostos = new Button("Validar Cta. Costos",FontAwesome.CHECK_CIRCLE_O);
						btnValidarCostos.setDisableOnClick(true);
						btnValidarCostos.addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {								
								List<Object> itemIds = new LinkedList<Object>();
								for(Object itemId : salaryContainer.getItemIds()){
										Property property = salaryContainer.getContainerProperty(itemId, "costAccount");
										if( property == null || property.getValue() == null )
											itemIds.add(itemId);
								}								
								
								if( itemIds.size() == 0 )
									Notification.show("Todos los trabajadores tienen asignada una cuenta de costos.",Type.HUMANIZED_MESSAGE);
								else{
									//crea el mensaje
									StringBuilder sb = new StringBuilder("Hay "+itemIds.size()+" trabajadores sin cuenta de costos:\n");
									int i = 0;
									for(Object itemId : itemIds ){
										BeanItem<Salary> item = salaryContainer.getItem(itemId);
										sb.append(item.getBean().getLaborerConstructionSite().getJobCode());

										i++;
										if( i < itemIds.size() )
											sb.append(",");
										if( i % 8 == 0) //para mostrar de a grupo de 8 maximo
											sb.append("\n");
									}
									Notification.show(sb.toString(),Type.ERROR_MESSAGE);
								}
								btnValidarCostos.setEnabled(true);
							}
						});
						addComponent(btnValidarCostos);

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
											constructionSiteService.save(confirmations);
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
											//copia los salarios y los pasa a históricos
											List<HistoricalSalary> historicals = new ArrayList<HistoricalSalary>(salaryContainer.size());
											for(Long itemId : salaryContainer.getItemIds()){
												BeanItem<Salary> beanItem = salaryContainer.getItem(itemId);
												HistoricalSalary historicalSalary = new HistoricalSalary(beanItem.getBean());
												historicals.add(historicalSalary);
											}
											//guarda los historicos
											constructionSiteService.saveHistoricalSalaries(historicals);
											//marca la confirmación sólo si no hubo problemas
											constructionSiteService.save(confirmations);
											
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
				salaryTable.setColumnReorderingAllowed(true);

				configureSalaryTable();

				salaryTable.addColumnCollapsedListener(new ColumnCollapsedObservableTable.ColumnCollapsedListener() {

					@Override
					public void colapseColumn(ColumnCollapsedEvent event) {
						User user = SecurityHelper.getCredentials();
						String propertyId = (String) event.getPropertyId();
						//agrega la columna al usuario si es que se setea como no colapsada
						if(!event.isCollapsed() && !user.getSalaryColumns().contains(propertyId) ){
							user.getSalaryColumns().add(propertyId);
							user.setPassword(null);
							userService.saveUser(user);
						}else if(event.isCollapsed() && user.getSalaryColumns().contains(propertyId) ){ //si no lo quita
							user.getSalaryColumns().remove(propertyId);
							user.setPassword(null);
							userService.saveUser(user);
						}

					}
				});

				salaryTable.setTableFieldFactory(new TableFieldFactory() {

					@Override
					public Field<?> createField(Container container, final Object itemId,Object propertyId, com.vaadin.ui.Component uiContext) {
						if(propertyId.equals("laborerConstructionSite.activeContract.jobCode")||
								propertyId.equals("laborerConstructionSite.laborer.fullname") ||
								propertyId.equals("totalLiquido") ||
								propertyId.equals("lastJornalPromedio") ||
								propertyId.equals("suple") )
							return null;
						if(propertyId.equals("jornalPromedio")||
								propertyId.equals("descHours")||
								propertyId.equals("specialBond")||
//								propertyId.equals("loanBond")||
								propertyId.equals("bondMov2")){

							TextField tf = new TextField();
							tf.setWidth("100%");
							tf.setNullRepresentation("");
							tf.setImmediate(true);
//							tf.addValueChangeListener(new ValueChangeListener() {
//
//								@Override
//								public void valueChange(ValueChangeEvent event) {
//									//
//									if(!loadingData){
////										BeanItem<Salary> beanItem = salaryContainer.getItem(itemId);
//										//guarda el salario
////										logger.debug("Guardando salary");
////										constructionSiteService.save(beanItem.getBean());
//									}
//								}
//							});
							return tf;
						}
						if(propertyId.equals("costAccount")){
							costContainer.addAll(getCostByConstructionSite());							
							ComboBox cbCost = new ComboBox(null,costContainer);
							cbCost.setItemCaptionPropertyId("code");
							cbCost.setImmediate(true);
							cbCost.setWidth("130px");
							return cbCost;
						}
						return null;
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
	
	boolean loadingData = false;

	/**
	 * Permite verificar si un contenedor tiene valores nulos o menores a 0 para las propiedades dadas. 
	 * @param container
	 * @param propertyIds
	 * @return El primer itemId del item que tiene una propiedad nula o menor a 0
	 */
	private List<Object> checkHasNegativeOrNull(BeanContainer<Long, Salary> container,String... propertyIds ) {
		List<Object> itemdIds = new LinkedList<Object>();
		for(Object itemId : container.getItemIds())
			for(String propertyId : propertyIds){
				Property property = container.getContainerProperty(itemId, propertyId);
				if( property == null || 
						property.getValue() == null || 
						((Number)property.getValue()).intValue() < 0 )
					itemdIds.add(itemId);
			}
		return itemdIds;
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
						propertyId.equals("forceSalary")||
						propertyId.equals("forceSuple")||
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
					propertyId.equals("forceSalary")||
					propertyId.equals("forceSuple")||
					propertyId.equals("laborerConstructionSite.laborer.fullname"))
				continue;
			table.setColumnFooter(propertyId, Utils.formatInteger( counts[i] ) );
			i++;
		}	

		//ordena las tablas por rol
		table.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});
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
						constructionSiteService.confirmAbsence(absence);
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

		BeanFieldGroup<Overtime> binder = new BeanFieldGroup<Overtime>(Overtime.class);
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
				constructionSiteService.save(overtime);
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
				//si el día está fuera del rango del contrato, no puede ser otra marca que R
				checkR(attendance);

				BeanItem<Salary> salaryItem = salaryContainer.getItem(attendance.getLaborerConstructionSite().getId());
				if(salaryItem == null ){
					return;
				}
				Property<Attendance> prop = salaryItem.getItemProperty("attendance");
				prop.setValue(attendance);

				//obliga al suple a ser calculado
				salaryItem.getItemProperty("calculatedSuple").setValue(true);

				salaryItem.getItemProperty("forceSalary").getValue();
				salaryItem.getItemProperty("forceSuple").getValue();

				//guarda el valor
				constructionSiteService.save(salaryItem.getBean());

				disabledHours(attendanceGrid);

			}

			/**
			 * Permite verificar si no se han seleccionado marcas distinas de R fuera de la fecha de contrato
			 * @param attendance
			 * @throws CommitException
			 */
			private void checkR(Attendance attendance) throws CommitException {
				LaborerConstructionsite lc = attendance.getLaborerConstructionSite();
				Contract contract = lc.getActiveContract();
				DateTime date = getAttendanceDate().withTime(0, 0, 0, 0);
				int current = date.dayOfMonth().getMinimumValue();
				date = date.withDayOfMonth(current);
				//mientras la fecha de inicio sea mayor a la fecha recorrida
				while( Utils.isDateAfter( lc.getActiveContract().getStartDate(), date.toDate()) && current <= date.dayOfMonth().getMaximumValue() )
				{
					logger.debug("1 date {}",date);
					if( !Utils.isAttendanceMarkEmptyOrFilled ( attendance.getMarksAsList().get( current - 1) ) )
						throw new  CommitException("El día "+Utils.date2String(date.toDate())+" está fuera del rango del contrato ("+Utils.date2String(contract.getStartDate())+"-"+Utils.date2String(contract.getTerminationDate())+") , no puede tener una marca distinta a R o vacio.");;
					current++;
					if(current <= date.dayOfMonth().getMaximumValue())
						date = date.withDayOfMonth(current);
				}
				//rellena con R, todo lo que este fuera de la fecha final de contrato
				if( lc.getActiveContract().getTerminationDate() != null){
					current = date.dayOfMonth().getMaximumValue();
					date = date.withDayOfMonth(current);
					while( Utils.isDateBefore(lc.getActiveContract().getTerminationDate(),date.toDate()) && current >= date.dayOfMonth().getMinimumValue() ){
						logger.debug("2 date {}",date);
						if( !Utils.isAttendanceMarkEmptyOrFilled (attendance.getMarksAsList().get( current - 1) ))
							throw new  CommitException("El día "+Utils.date2String(date.toDate())+" está fuera del rango del contrato ("+Utils.date2String(contract.getStartDate())+"-"+Utils.date2String(contract.getTerminationDate())+") , no puede tener una marca distinta a R o vacio.");;
						current-- ;
						if(current >= date.dayOfMonth().getMinimumValue())
							date = date.withDayOfMonth(current);
					}
				}
				
				//hace lo mismo para el mes pasado
				DateTime date2 = getPastMonthClosingDate().plusDays(1);
				//rellena con R, todo lo que este fuera de la fecha inicial 
				current = date2.dayOfMonth().get();
//				date2 = date2.withDayOfMonth(current);
				date2 = date2.withDayOfMonth(current);
				logger.debug("3 date2 {}",date2);
				//mientras la fecha de inicio sea mayor a la fecha recorrida 
				while(Utils.isDateAfter(lc.getActiveContract().getStartDate(),date2.toDate()) && current <= date2.dayOfMonth().getMaximumValue() )
				{
					if( !Utils.isAttendanceMarkEmptyOrFilled (attendance.getLastMarksAsList().get( current - 1) ))
						throw new  CommitException("El día "+Utils.date2String(date2.toDate())+" está fuera del rango del contrato ("+Utils.date2String(contract.getStartDate())+"-"+Utils.date2String(contract.getTerminationDate())+") , no puede tener una marca distinta a R o vacio.");;
					current++;
					if(current <= date2.dayOfMonth().getMaximumValue())
						date2 = date2.withDayOfMonth(current);
				}
				//rellena con R, todo lo que este fuera de la fecha final de contrato
				if( lc.getActiveContract().getTerminationDate() != null){
					current = date2.dayOfMonth().getMaximumValue();
					date2 = date2.withDayOfMonth(current);
					logger.debug("4");
					while( Utils.isDateBefore(lc.getActiveContract().getTerminationDate(), date2.toDate()) && current >= date2.dayOfMonth().getMinimumValue() ){
						if( !Utils.isAttendanceMarkEmptyOrFilled (attendance.getLastMarksAsList().get( current - 1) ))
							throw new  CommitException("El día "+Utils.date2String(date2.toDate())+" está fuera del rango del contrato ("+Utils.date2String(contract.getStartDate())+"-"+Utils.date2String(contract.getTerminationDate())+") , no puede tener una marca distinta a R o vacio.");;
						current-- ;
						if(current >= date2.dayOfMonth().getMinimumValue())
							date2 = date2.withDayOfMonth(current);
					}
				}
			}
		});
		
		attendanceGrid.setEditorErrorHandler(new EditorErrorHandler() {
			
			@Override
			public void commitError(CommitErrorEvent event) {
				List<InvalidValueException> invalidValues = new ArrayList<InvalidValueException>(event.getCause().getInvalidFields().values());
				
				String message = "";
				if(!invalidValues.isEmpty())
					message = invalidValues.get(0).getMessage();
				else {
					Exception e = event.getCause();
					if( e.getCause() != null ){
						message = e.getCause().getMessage();
					}else {
						message = e.getMessage();
					}
				}
				
				Notification.show(message,Type.ERROR_MESSAGE);
				event.setUserErrorMessage(message);
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
				constructionSiteService.save(attedance);
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
			dateConfig = null;
			dateConfigPastMonth = null;
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
										final BeanItemContainer<AdvancePaymentItem> container = new BeanItemContainer<AdvancePaymentItem>(AdvancePaymentItem.class);
										final Window window = new Window();
										final FieldGroup fg = new FieldGroup();

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
																				Field permissionDiscount = fg.buildAndBind("Descuento por Falla", "failureDiscount");
																				((TextField)permissionDiscount).setNullRepresentation("");
																				permissionDiscount.addValueChangeListener(listener);

																				Field failureDiscount = fg.buildAndBind("Descuento Adicional por Falla", "permissionDiscount");
																				((TextField)failureDiscount).setNullRepresentation("");
																				failureDiscount.addValueChangeListener(listener);

																				addComponent(permissionDiscount);
																				addComponent(failureDiscount);
																			}
																		});

																		container.removeAllItems();
																		container.addAll(advancepayment.getAdvancePaymentTable());

																		HorizontalLayout hl = new HorizontalLayout(){
																			{
																				setSpacing(true);
																				final TextField supleCode = new TextField("Código Suple");
																				supleCode.setImmediate(true);
																				addComponent(new FormLayout(supleCode));
																				Button add = new Button(null,new Button.ClickListener() {

																					@Override
																					public void buttonClick(ClickEvent event) {
																						AdvancePaymentItem advancePaymentItem = new AdvancePaymentItem();	
																						AdvancePaymentConfigurations apItem = confService.findAdvancePaymentConfigurationsByCS(cs);
																						boolean check = false;	
																						try{
																							advancePaymentItem.setSupleCode(Integer.valueOf(supleCode.getValue()));																							
																							for(AdvancePaymentItem i : apItem.getAdvancePaymentTable()){
																								if(i.getSupleCode() == advancePaymentItem.getSupleCode())
																									check = true;
																							}

																							if(!check){
																								advancepayment.setConstructionSite(cs);
																								advancepayment.addAdvancePaymentItem(advancePaymentItem);
																								confService.save(advancepayment);
																								container.addBean(advancePaymentItem);
																							}else{
																								Notification.show("El código ingresado ya existe, ingrese uno diferente.",Type.ERROR_MESSAGE);
																								return;
																							}
																						}catch(Exception e){
																							advancepayment.removeAdvancePaymentItem(advancePaymentItem);
																							Notification.show("El código ingresado ya existe, ingrese uno diferente.",Type.ERROR_MESSAGE);
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
																						List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActive(cs);
																						boolean verify = false;
																						for(LaborerConstructionsite lcsItem : lcs){
																							if(lcsItem.getSupleCode() == advancePaymentItem.getSupleCode()){
																								verify = true;
																							}
																						}

																						// No se permitirá eliminar un suple que esta siendo utilizado por algún trabajador de la obra.
																						if(verify){
																							Notification.show("El suple seleccionado no puede ser eliminado ya que está siendo utilizado.", Type.ERROR_MESSAGE);
																						}else{
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
														try {
															fg.commit();
														} catch (CommitException e) {
															Notification.show("No pudo realizarse el commit", Type.ERROR_MESSAGE);
															logger.error("CommitException", e);
															return;
														}

														AdvancePaymentConfigurations apc = ((BeanItem<AdvancePaymentConfigurations>) fg.getItemDataSource()).getBean();
														apc.setAdvancePaymentTable(container.getItemIds());												

														for(Object itemId : salaryContainer.getItemIds()){															
															BeanItem<Salary> salaryItem = salaryContainer.getItem(itemId);			
															Salary salary = salaryItem.getBean();

															salary.setAdvancePaymentConfiguration(apc);														
														}

														supleTable.refreshRowCache();
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
														fg.discard();
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
								OnDemandStreamResource myResource = new OnDemandFileDownloader.OnDemandStreamResource() {

									public InputStream getStream() {
										if(salaryContainer.size() == 0 ){
											logger.debug("Aún no se calculan los sueldos.");
											Notification.show("Aún no se calculan los sueldos.");
											return null;
										}
											

										AfpAndInsuranceConfigurations afpTable = getAfpAndInsuranceConfigurations();
										List<TaxationConfigurations> taxationConfig = getTaxationConfigurations();
										List<FamilyAllowanceConfigurations> familyConfig = getFamilyAllowanceConfigurations();
										List<CostAccount> costo = getCostByConstructionSite();

										//verifica que se tengan bien las configuraciones del mes
										logger.debug("TERCERO");
										DateConfigurations dc = getDateConfigurations();
										if(!validDateConfiguration(dc))
											return null;

										XSSFWorkbook wb = null;

										//1. Open the file
										try {
											wb = new XSSFWorkbook(new FileInputStream(new File(AttendancePanel.class.getResource("/templates/asistencia/planilla_asistencia.xlsx").toURI())));
											//setea la configuración de la primera pestaña
											XSSFSheet sheet0 = wb.getSheetAt(0);
											//celda B1 Centro de costo
											Row row1 = sheet0.getRow(0);
											row1.getCell(1).setCellValue(cs.getCostCenter());
											//celda B2 nombre del clave para el doc
											Row row2 = sheet0.getRow(1);
											row2.getCell(1).setCellValue(cs.getCode());
											//celda B3 fecha (primer dia del mes)
											Row row3 = sheet0.getRow(2);
											//											row3.getCell(1).setCellValue(getAttendanceDate().withDayOfMonth(1).toString("dd/MM/yyyy"));
											row3.getCell(1).setCellValue(getAttendanceDate().withDayOfMonth(1).toDate());
											//celda B13 cuenta la cantidad de sabados del mes
											Row row13 = sheet0.getRow(12);
											row13.getCell(1).setCellValue(Utils.countSat(getAttendanceDate()));
											//celda B14 cuenta la cantidad dias de lunes a viernes trabajados
											int holidays = constructionSiteService.countHolidaysMonthOnLaborerDays(getAttendanceDate());
											Row row14 = sheet0.getRow(13);
											row14.getCell(1).setCellValue(Utils.countLaborerDays(getAttendanceDate())-holidays);
											int holidays2 = constructionSiteService.countHolidaysMonth(getAttendanceDate());
											//celda B15 cuenta los domingos y festivos
											Row row15 = sheet0.getRow(14);
											row15.getCell(1).setCellValue(Utils.countSun(getAttendanceDate())+holidays2);
											//celda B16 inicio trato
											Row row16 = sheet0.getRow(15);
											row16.getCell(1).setCellValue(new DateTime(dc.getBeginDeal()).toDate());
											//celda B17 fin trato
											Row row17 = sheet0.getRow(16);
											row17.getCell(1).setCellValue(new DateTime(dc.getFinishDeal()).toDate());
											//celda B18 dia de cierre de anticipo
											Row row18 = sheet0.getRow(17);
											row18.getCell(1).setCellValue(new DateTime(dc.getAdvance()).getDayOfMonth());
											//celda B19 uf del mes
											Row row19 = sheet0.getRow(18);
											row19.getCell(1).setCellValue(dc.getUf());

											WageConfigurations wageConfiguration = getWageConfigurations();

											//celda C23 sueldo minimo
											Row row23 = sheet0.getRow(22);
											row23.getCell(2).setCellValue(getWageConfigurations().getMinimumWage());

											//celda B25 colacion
											Row row25 = sheet0.getRow(24);
											row25.getCell(1).setCellValue(wageConfiguration.getCollation());
											//celda B26 movilización
											Row row26 = sheet0.getRow(25);
											row26.getCell(1).setCellValue(wageConfiguration.getMobilization());
											//celda B27 movilización2
											Row row27 = sheet0.getRow(26);
											row27.getCell(1).setCellValue(Utils.getMov2ConstructionSite(wageConfiguration.getMobilizations2(), cs));																		
								
											//Importar datos de Impuestos
									        XSSFRow row41, rowE;
									        int t_imp = 0;
									        for (int i=40; i<= (40 + (taxationConfig.size() * 2) ); i += 2 ){
									            row41 = sheet0.createRow(i);									            
							                	if(t_imp < taxationConfig.size()){
							                		row41.createCell(1).setCellValue(taxationConfig.get(t_imp).getFrom());
							                		row41.createCell(2).setCellValue((taxationConfig.get(t_imp).getFactor() ));
							                		row41.createCell(4).setCellValue(taxationConfig.get(t_imp).getReduction());						                		
							                	}
							                	t_imp++;
									        }
									        
									        int t_imp_d = 0;
									        for (int i=41; i<= (41 + (taxationConfig.size() * 2) ); i += 2 ){
									        	rowE = sheet0.createRow(i);									            
							                	if(t_imp_d < taxationConfig.size()){
							                		rowE.createCell(1).setCellValue(taxationConfig.get(t_imp_d).getTo());		
							                		rowE.createCell(2).setCellValue(taxationConfig.get(t_imp_d).getFactor() );
							                		rowE.createCell(4).setCellValue(taxationConfig.get(t_imp_d).getReduction());		
							                	}
							                	t_imp_d++;
									        }
									        
									        //Importar datos Asignación Familiar
									        XSSFRow row70, rowAF;
									        int t_af = 0;
									        for (int i=70; i<= (70 + (familyConfig.size() * 2) ); i += 2 ){
									            row70 = sheet0.createRow(i);
							                	if(t_af < familyConfig.size()){
							                		row70.createCell(1).setCellValue(familyConfig.get(t_af).getFrom());
							                		row70.createCell(2).setCellValue(familyConfig.get(t_af).getAmount());
							                	}
									            t_af++;
									        }
									        
									        int t_af_d = 0;
									        for (int i=71; i<= (71 + (familyConfig.size() * 2) ); i += 2 ){
									        	rowAF = sheet0.createRow(i);
							                	if(t_af_d < familyConfig.size()){
							                		rowAF.createCell(1).setCellValue(familyConfig.get(t_af_d).getTo());
							                		rowAF.createCell(2).setCellValue(familyConfig.get(t_af_d).getAmount());
							                	}
							                	t_af_d++;
									        }
									        
											//Importar datos de AFP
									        XSSFRow row101;
									        int t = 0;
									        for (int i=101; i<= (101 + afpTable.getAfpTable().size()); i++){
									            row101 = sheet0.createRow(i);
							                	if(t < afpTable.getAfpTable().size()){
							                		row101.createCell(1).setCellValue(afpTable.getAfpTable().get(t).getName());							                		
							                		row101.createCell(2).setCellValue((afpTable.getAfpTable().get(t).getRate()/100));							                		
							                		row101.createCell(3).setCellValue((afpTable.getAfpTable().get(t).getAfpAndInsuranceConfigurations().getSis()/100));
								                	}
									            t++;
									        }
									        
											//pone la asistencia en la segunda pestaña
											wb.setSheetName(1,getAttendanceDate().toString("MMMM yyyy").toUpperCase());
											XSSFSheet sheet = wb.getSheetAt(1);
											int i = 11;
											costContainer.addAll(getCostByConstructionSite());
											for(Object itemId : attendanceContainer.getItemIds()){

												BeanItem<Attendance> attendanceItem = attendanceContainer.getItem(itemId);
												BeanItem<Salary> salaryItem = salaryContainer.getItem(itemId);
												BeanItem<Overtime> overtimeItem = overtimeContainer.getItem(itemId);

												Salary salary = salaryItem.getBean();
												Attendance attendance = attendanceItem.getBean();
												Overtime overtime = overtimeItem.getBean();
												
												BeanItem<CostAccount> costItem = costContainer.getItem(salaryItem.getBean().getCostAccount());
												Row row = sheet.getRow(i++);

												//información de cuenta de costos										
												row.getCell(133).setCellValue(costItem.getBean().getCode());
												
												//información del trabajador
												row.getCell(0).setCellValue(attendance.getLaborerConstructionSite().getJobCode());
												row.getCell(1).setCellValue(attendance.getLaborerConstructionSite().getLaborer().getFullname());
												
//												int minDay = Utils.calcularDiaInicial(attendance,0,false);
//												int maxDay = Utils.calcularDiaFinal(attendance,getAttendanceDate().dayOfMonth().getMaximumValue(),false) + 1;
//												int minDay = 0;
												int maxDay = getAttendanceDate().dayOfMonth().getMaximumValue() ;//+ 1;
												//asistencia
												int j = 3;
												for(AttendanceMark mark : attendance.getMarksAsList()){
													
													if(j == 17){
														//setea el suple ingresado manualmente
														row.getCell(j).setCellValue(salary.getSuple());
														j++;
													}
													
													//continua hasta que no haya ingresado por contrato
//													if( j - 3 < minDay ){
//														j++;
//														continue;
//													}
													
													if(mark == null)
														mark = AttendanceMark.EMPTY;
													row.getCell(j).setCellValue(mark.toString());
													//ingresa hasta el fin de mes solamente 
													if(j - 3 == maxDay )
														break;
													j++;
												}
												//jornal promedio
												row.getCell(44).setCellValue(salary.getJornalPromedio());
												//horas desc TODO
												row.getCell(46).setCellValue(salary.getDescHours());
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
												int k = 60;
												List<Double> overtimeLastMonth = overtime.getLastMonthOvertimeAsList();
												LocalDateTime beginingDeal  = new LocalDateTime(dc.getBeginDeal());
												LocalDateTime finishingDeal  = new LocalDateTime(dc.getFinishDeal());
												//solo agrega el mes anterior, si la fecha de inicio es en el mes anterior a la fecha de fin de trato
												if(beginingDeal.getMonthOfYear() == finishingDeal.getMonthOfYear() - 1 ){
													int beginDeal = beginingDeal.getDayOfMonth();
													int maxLasMonthDeal = beginingDeal.dayOfMonth().getMaximumValue();
													for(int l =  beginDeal - 1 ; l < maxLasMonthDeal ;l++ ){
														 if(overtimeLastMonth.get(l) != null )
															 row.getCell(k).setCellValue(overtimeLastMonth.get(l));
														 k++;													
													}
												}
												
												//sobre tiempo 75 103
												k = 75;
												List<Double> overtimeMonth = overtime.getOvertimeAsList();
												int finishDeal = finishingDeal.getDayOfMonth();
												for(int l =  0 ; l < finishDeal ;l++ ){
													if(overtimeMonth.get(l) != null )
														row.getCell(k).setCellValue(overtimeMonth.get(l));
													k++;
												}

												//loc mov2 105
												row.getCell(105).setCellValue(salary.getBondMov2());
												//bono especial 108
												row.getCell(108).setCellValue(salary.getSpecialBond());
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
												row.getCell(156).setCellValue(salary.getLaborerConstructionSite().getLaborer().getAfp().getName());
												Double rate = Utils.getAfpRate(afpTable.getAfpTable(), salary.getLaborerConstructionSite().getLaborer().getAfp());
												if(rate != 0 )
													row.getCell(157).setCellValue("Activo");
												else
													row.getCell(157).setCellValue("Pensionado");
												//copia el % de afp asociado
												row.getCell(158).setCellValue(rate);

											}
											
											//Modificar valores de centro de costo
											XSSFRow rowS19;
									        int cc = 0;
									        for (int k = 8; k <= costo.size(); k++){
									        	rowS19 = sheet.getRow(k);
							                	if(cc < costo.size()){
							                		rowS19.getCell(18).setCellValue(costo.get(cc).getCode());
							                	}
									            cc++;
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

									private boolean validDateConfiguration(DateConfigurations dc) {
										if(dc == null ){
											Notification.show("No se puede generar el Excel si no se han definido las configuraciones del mes");
											return false;
										}

										if(dc.getAdvance() == null ){
											Notification.show("No se puede generar el Excel si no se ha definido la fecha de fin del adelanto del mes");
											return false;
										}

										if(dc.getAssistance() == null ){
											Notification.show("No se puede generar el Excel si no se ha definido la fecha de fin de asistencia del mes");
											return false;
										}

										if(dc.getBeginDeal() == null ){
											Notification.show("No se puede generar el Excel si no se ha definido la fecha de inicio de trato del mes");
											return false;
										}

										if(dc.getFinishDeal() == null ){
											Notification.show("No se puede generar el Excel si no se ha definido la fecha de fin de trato del mes");
											return false;
										}

										if(dc.getUf() == null ){
											Notification.show("No se puede generar el Excel si no se han definido la uf del mes");
											return false;
										}

										return true;
									}

									public String getFilename() {
										return  cs.getCode()+ "_Asistencia_"+getAttendanceDate().toString("MMMM_yyyy").toUpperCase()+".xlsx";
									};
								};
								//								OnDemandStreamResource resource = new OnDemandFileDownloader.OnDemandStreamResource(myResource){
								//									public String getFilename() {
								//										return  cs.getCode()+ "_Asistencia_"+getAttendanceDate().toString("MMMM_yyyy")+".xlsx";
								//									};
								//								}; //TODO mes_año_codobra_ant/liq.txt
								OnDemandFileDownloader fileDownloader = new OnDemandFileDownloader(myResource);
								fileDownloader.extend(btnExportAttendance);
							}});
					}
				});



			}
		};
	}


	private void generateSoftlandFile(final Button btnExportSoftland) {

		//recupera la lista de sueldos
		//		StreamResource.StreamSource myResource = new StreamResource.StreamSource() {
		OnDemandStreamResource myResource = new OnDemandFileDownloader.OnDemandStreamResource() {

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

			@Override
			public String getFilename() {
				return getAttendanceDate().toString("YYYY_M_").toUpperCase()+cs.getCode()+"_LIQ.txt";
			}
		};
		//		StreamResource resource = new StreamResource(myResource, getAttendanceDate().toString("YYYY_M_")+cs.getCode()+"_LIQ.txt"); 

		OnDemandFileDownloader fileDownloader = new OnDemandFileDownloader(myResource);
		fileDownloader.extend(btnExportSoftland);
	}

	private void generateSupleSoftlandFile(final Button btnExportSoftland) {


		OnDemandStreamResource myResource = new OnDemandFileDownloader.OnDemandStreamResource() {

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

			@Override
			public String getFilename() {
				return getAttendanceDate().toString("YYYY_M_").toUpperCase()+cs.getCode()+"_ANT.txt";
			}
		};
		//		StreamResource resource = new StreamResource(myResource, getAttendanceDate().toString("YYYY_M_")+cs.getCode()+"_ANT.txt"); 

		OnDemandFileDownloader fileDownloader = new OnDemandFileDownloader(myResource);
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
		if(getConfirmations().isCentralCheck()) //solo deja confirmar una vez
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
		
		logger.debug("configureInterface();");

		reloadMonthAttendanceData(dt);
		configureInterface();

		reloadMonthGridData(dt);

		logger.debug("createHeaders(attendanceGrid);");
		createHeaders(attendanceGrid);
		logger.debug("setAttendanceOrder();");
		setAttendanceOrder();

		logger.debug("createHeaders(attendanceGrid);");
		createHeaders(overtimeGrid);
		logger.debug("setOvertimeOrders();");
		setOvertimeOrders();

		logger.debug("createGridFooters(attendanceGrid);");
		createGridFooters(attendanceGrid);

		logger.debug("createGridFooters(overtimeGrid);");
		createGridFooters(overtimeGrid);

		logger.debug("createTableFooter(salaryTable);");
		createTableFooter(salaryTable);

		logger.debug("createTableFooter(supleTable);");
		createTableFooter(supleTable);

		logger.debug("disabledHours(attendanceGrid);");
		disabledHours(attendanceGrid);

		logger.debug("SecurityHelper.getCredentials();");
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
		logger.debug("salaryTableVisibleTable");
		for(String column : salaryTableVisibleTable ){
			salaryTable.setColumnCollapsed(column, !user.getSalaryColumns().contains(column));
		}
		
		loadingData = false;
		
		// Enable polling and set frequency to 1 seconds
		//		UI.getCurrent().setPollInterval(1000);
	}
	
	private void configureSalaryTable(){
		
		//crea la columna, solo si no existe
		if(salaryTable.getColumnGenerator("totalLiquido") == null )
			salaryTable.addGeneratedColumn("totalLiquido", new Table.ColumnGenerator(){
	
				@Override
				public Object generateCell(final Table source, final Object itemId,final Object columnId) {
					
					if(((BeanContainer)source.getContainerDataSource()).getBeanType() == Salary.class){
					
						final BeanItem<Salary> item = (BeanItem<Salary>) salaryContainer.getItem(itemId);
						salaryContainer.getItem(itemId).getItemProperty("forceSalary").getValue();
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
									salaryContainer.getItem(itemId).getItemProperty("forceSalary").getValue();
									//forza actualizar el item
									label.setValue( "<b>"+Utils.formatInteger((Integer) salaryContainer.getContainerProperty(itemId, columnId).getValue())+"</b>"+
											"  ("+Utils.formatInteger((Integer) salaryContainer.getContainerProperty(itemId, "roundSalary").getValue())+")");
									
								}
							});
						return label;
					}else {
						return ((BeanItem<HistoricalSalary>) source.getContainerDataSource().getItem(itemId)).getItemProperty(columnId).getValue();
					}
				}
	
			});

		salaryTable.setVisibleColumns(salaryTableVisibleTable);

		salaryTable.setColumnHeaders(
				"Rol",//1
				"Nombre",//2
				"Cta. Costos",
				"Último<br />Jornal Prom",//3
				"Jornal Prom",//4
				"Bono Imp.",//5
				"Bono No Imp.",//6
				"Bono Prest.",//7
				"Sobretpo",//8
				"H Desc",//9
				"V Cuota<br />Prestamo",//10
				"V Cuota<br />Herramienta",//11
				"Total Líquido<br />(A Pagar)",//12
				"Día<br />Trab",//13
				"Sab",//14
				"Sep",//15
				"DPS",//16
				"DPD",//17
				"Col",//18
				"Mov",//19
				"Jornal Base",//20
				" V Trato",//21
				"Valor Sábado" ,//22
				"V S Corrida",//23
				"Desc Horas","Total<br />Bonos<br />Imponibles","G Legal","Afecto","Sobre Afecto","Cargas","A Familiar","Colación","Mov","Movi 2","T No Afecto"
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
		salaryTable.setColumnCollapsed("salaryCalculator.diaTrab",true);
		salaryTable.setColumnCollapsed("salaryCalculator.sab",true);
		salaryTable.setColumnCollapsed("salaryCalculator.sep",true);
		salaryTable.setColumnCollapsed("salaryCalculator.dps",true);
		salaryTable.setColumnCollapsed("salaryCalculator.dpd",true);
		salaryTable.setColumnCollapsed("salaryCalculator.col",true);
		salaryTable.setColumnCollapsed("salaryCalculator.mov",true);
		
		salaryTable.setEditable(true);
	}

	private void reloadMonthGridData(DateTime dt) {

		loadingData = true;
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

			List<Overtime> overtime = constructionSiteService.getOvertimeByConstruction(cs,dt);
			overtimeContainer.addAll(overtime);
			overtimeContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});

			List<Attendance> attendance = constructionSiteService.getAttendanceByConstruction(cs,dt);
			attendanceContainer.addAll(attendance);
			attendanceContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});

			List<AbsenceVO> absences = constructionSiteService.getAbsencesByConstructionAndMonth(cs,dt);
			absenceContainer.addAll(absences);
			absenceContainer.sort(new String[]{"laborerConstructionsite.activeContract.jobCode"},new boolean[]{ true });

			//dependiendo de si está o no confirmada por el central, carga los sueldos historicos u los otros
			if( !getConfirmations().isCentralCheck() ){
				List<Salary> salaries = constructionSiteService.getSalariesByConstructionAndMonth(cs,dt);
				salaryContainer.addAll(salaries);
				salaryContainer.sort(new String[]{"laborerConstructionsite.activeContract.jobCode"},new boolean[]{ true });
				salaryTable.setContainerDataSource(salaryContainer);
				configureSalaryTable();
			}else{
				List<HistoricalSalary> salaries = constructionSiteService.getHistoricalSalariesByConstructionAndMonth(cs,dt);
				BeanContainer<Long,HistoricalSalary> historicalSalaryContainer = new BeanContainer<Long,HistoricalSalary>(HistoricalSalary.class);
				historicalSalaryContainer.addNestedContainerProperty("laborerConstructionSite.activeContract.jobCode");
				historicalSalaryContainer.addNestedContainerProperty("laborerConstructionSite.laborer.fullname");
				historicalSalaryContainer.addNestedContainerProperty("laborerConstructionSite.supleCode");
				historicalSalaryContainer.addNestedContainerProperty("laborerConstructionSite.id");
				historicalSalaryContainer.addNestedContainerProperty("costAccount.code");
				historicalSalaryContainer.setBeanIdProperty("laborerConstructionSite.id");
				
				historicalSalaryContainer.addAll(salaries);
				historicalSalaryContainer.sort(new String[]{"laborerConstructionsite.activeContract.jobCode"},new boolean[]{ true });
				salaryTable.setContainerDataSource(historicalSalaryContainer);
				salaryTable.setVisibleColumns(historicalSalaryTableVisibleTable);

				salaryTable.setColumnHeaders("Rol","Nombre","Cta. Costos","Último<br />Jornal Prom","Jornal Prom","Bono Imp.","Bono No Imp.","Bono Prest.", "Sobretpo","H Desc","V Cuota<br />Prestamo",
						"V Cuota<br />Herramienta","Total Líquido<br />(A Pagar)",
						"Día<br />Trab","Sab","Sep","DPS","DPD","Col","Mov"
						,"Jornal Base", " V Trato", "Valor Sábado" , "V S Corrida", "Desc Horas","Total<br />Bonos<br />Imponibles","G Legal","Afecto","Sobre Afecto","Cargas","A Familiar","Colación","Movi 2","T No Afecto"
						);
			}
			salaryTable.refreshRowCache();

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
		confirmations = constructionSiteService.getConfirmationsByConstructionsiteAndMonth(cs,dt);
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

	//Permite bloquear el ingreso de horas extras en caso de que un obrero registre vacaciones, accidente y/o licencia.
	private void disabledHours(Grid grid){
		for(Object itemId : grid.getContainerDataSource().getItemIds()){
			BeanItem attendanceItem = (BeanItem) grid.getContainerDataSource().getItem(itemId);
			BeanItem<Overtime> overtimeItem = overtimeContainer.getItem(itemId);				

			for(Object propertyId : grid.getContainerDataSource().getContainerPropertyIds()){
				if( attendanceItem.getBean() instanceof Attendance ){
					if( attendanceItem.getItemProperty(propertyId).getValue() instanceof AttendanceMark &&
							( (AttendanceMark)attendanceItem.getItemProperty(propertyId).getValue()  == AttendanceMark.VACATION ||
							(AttendanceMark)attendanceItem.getItemProperty(propertyId).getValue()  == AttendanceMark.ACCIDENT ||
							(AttendanceMark)attendanceItem.getItemProperty(propertyId).getValue()  == AttendanceMark.SICK
									)){

						overtimeItem.getItemProperty(propertyId).setReadOnly(true);
					}
				}
			}
		}
	}
}
