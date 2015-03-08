package cl.magal.asistencia.ui.constructionsite;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cl.magal.asistencia.entities.Attendance;
import cl.magal.asistencia.entities.Confirmations;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Overtime;
import cl.magal.asistencia.entities.enums.AttendanceMark;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.ui.vo.AbsenceVO;
import cl.magal.asistencia.util.Constants;
import cl.magal.asistencia.util.SecurityHelper;

import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Container.SimpleFilterable;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Component
@Scope("prototype")
public class AttendancePanel extends Panel implements View {

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
	private transient UserService userService;

	/** CONTAINERS **/
	BeanItemContainer<Attendance> attendanceContainer = new BeanItemContainer<Attendance>(Attendance.class);
	BeanItemContainer<Overtime> overtimeContainer = new BeanItemContainer<Overtime>(Overtime.class);
	BeanItemContainer<AbsenceVO> absenceContainer = new BeanItemContainer<AbsenceVO>(AbsenceVO.class);

	/** COMPONENTES **/
	ProgressBar progress;
	Label status;
	Grid attendanceGrid, overtimeGrid;
	Window progressDialog;
	InlineDateField attendanceDate;
	Button btnExportSoftland,btnConstructionSiteConfirm,btnCentralConfirm;

	/** ATRIBUTOS **/
	Confirmations confirmations;
	ConstructionSite cs;

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

	public AttendancePanel(){

		setSizeFull();

		//crea la parte superior de la interfaz de asistencia
		final HorizontalLayout topAsistencia = drawTopAttendance();
		//crea las tabs que contienen la información de asistencia
		final TabSheet detalleAsistencia = drawAttendanceDetail();

		setContent(new VerticalLayout(){
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

	@PostConstruct
	private void init(){

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
		populateAttendanceGrid();
		configureInterface();
	}


	private void clearGrids(){
		attendanceContainer.removeAllItems();
		overtimeContainer.removeAllItems();
		absenceContainer.removeAllItems();
	}

	private TabSheet drawAttendanceDetail() {
		TabSheet tab = new TabSheet();

		attendanceContainer.addNestedContainerProperty("laborerConstructionSite.activeContract.jobCode");
		attendanceGrid = new Grid(attendanceContainer);
		attendanceGrid.setSelectionMode(SelectionMode.SINGLE);
		attendanceGrid.setSizeFull();
		attendanceGrid.setEditorFieldGroup(new BeanFieldGroup<Attendance>(Attendance.class));
		attendanceGrid.setEditorFieldFactory(new DefaultFieldGroupFieldFactory(){
			@Override
			public <T extends Field> T createField(Class<?> type, Class<T> fieldType) {

				if (type.isAssignableFrom(AttendanceMark.class) && fieldType.isAssignableFrom(ComboBox.class)) {
					ComboBox cb = new ComboBox();
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
			}
		});

		attendanceGrid.addStyleName("grid-attendace");
		attendanceGrid.setEditorEnabled(true);
		attendanceGrid.setFrozenColumnCount(1);
		if(attendanceGrid.getColumn("laborerConstructionSite") != null )
			attendanceGrid.removeColumn("laborerConstructionSite");
		if(attendanceGrid.getColumn("attendanceId") != null )
			attendanceGrid.removeColumn("attendanceId");
		if(attendanceGrid.getColumn("date") != null )
			attendanceGrid.removeColumn("date");

		attendanceGrid.setColumnOrder("laborerConstructionSite.activeContract.jobCode","d1","d2","d3","d4","d5","d6","d7","d8","d9","d10","d11","d12","d13","d14","d15","d16"
				,"d17","d18","d19","d20","d21","d22","d23","d24","d25","d26","d27","d28","d29","d30","d31");

		attendanceContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});
		attendanceGrid.getColumn("laborerConstructionSite.activeContract.jobCode").setHeaderCaption("Oficio").setEditable(false).setWidth(100);

		HeaderRow filterRow =  attendanceGrid.appendHeaderRow();
		for (final Object pid: attendanceGrid.getContainerDataSource().getContainerPropertyIds()) {

			final HeaderCell cell = filterRow.getCell(pid);

			// Have an input field to use for filter
			TextField filterField = new TextField();
			if(pid.equals("laborerConstructionSite.activeContract.jobCode")) filterField.setWidth("100%");
			else {
				filterField.setWidth("50px");
				if(attendanceGrid.getColumn(pid) != null )
					attendanceGrid.getColumn(pid).setHeaderCaption(((String) pid).replace("d","")).setSortable(false);//.setWidth(50);
			}

			filterField.setHeight("90%");

			// Update filter When the filter input is changed
			filterField.addTextChangeListener(new TextChangeListener() {

				@Override
				public void textChange(TextChangeEvent event) {
					// Can't modify filters so need to replace
					((SimpleFilterable) attendanceGrid.getContainerDataSource()).removeContainerFilters(pid);

					// (Re)create the filter if necessary
					if (! event.getText().isEmpty())
						((Filterable) attendanceGrid.getContainerDataSource()).addContainerFilter(
								new SimpleStringFilter(pid,
										event.getText(), true, false));
				}
			});
			if(cell != null)
				cell.setComponent(filterField);
		}

		tab.addTab(attendanceGrid,"Asistencia");

		overtimeContainer.addNestedContainerProperty("laborerConstructionSite.activeContract.jobCode");
		overtimeGrid = new Grid(overtimeContainer);
		overtimeGrid.setSelectionMode(SelectionMode.SINGLE);
		overtimeGrid.setSizeFull();
		overtimeGrid.setEditorFieldGroup(new BeanFieldGroup<Overtime>(Overtime.class));
		overtimeGrid.getEditorFieldGroup().addCommitHandler(new CommitHandler() {

			@Override
			public void preCommit(CommitEvent commitEvent) throws CommitException {

			}

			@Override
			public void postCommit(CommitEvent commitEvent) throws CommitException {
				//guarda el elmento
				Overtime attedance = ((BeanItem<Overtime>) commitEvent.getFieldBinder().getItemDataSource()).getBean();
				service.save(attedance);
				overtimeContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});
			}
		});

		overtimeGrid.addStyleName("grid-attendace");
		overtimeGrid.setEditorEnabled(true);
		overtimeGrid.setFrozenColumnCount(1);
		if(overtimeGrid.getColumn("laborerConstructionSite") != null )
			overtimeGrid.removeColumn("laborerConstructionSite");
		if(overtimeGrid.getColumn("attendanceId") != null )
			overtimeGrid.removeColumn("attendanceId");
		if(overtimeGrid.getColumn("date") != null )
			overtimeGrid.removeColumn("date");

		overtimeGrid.setColumnOrder("laborerConstructionSite.activeContract.jobCode","d1","d2","d3","d4","d5","d6","d7","d8","d9","d10","d11","d12","d13","d14","d15","d16"
				,"d17","d18","d19","d20","d21","d22","d23","d24","d25","d26","d27","d28","d29","d30","d31");

		overtimeContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});
		overtimeGrid.getColumn("laborerConstructionSite.activeContract.jobCode").setHeaderCaption("Oficio").setEditable(false).setWidth(100);

		filterRow =  overtimeGrid.appendHeaderRow();
		for (final Object pid: overtimeGrid.getContainerDataSource().getContainerPropertyIds()) {

			final HeaderCell cell = filterRow.getCell(pid);

			// Have an input field to use for filter
			TextField filterField = new TextField();
			if(pid.equals("laborerConstructionSite.activeContract.jobCode")) filterField.setWidth("100%");
			else {
				filterField.setWidth("50px");
				if(overtimeGrid.getColumn(pid) != null )
					overtimeGrid.getColumn(pid).setHeaderCaption(((String) pid).replace("d","")).setSortable(false);//.setWidth(50);
			}

			filterField.setHeight("90%");

			// Update filter When the filter input is changed
			filterField.addTextChangeListener(new TextChangeListener() {

				@Override
				public void textChange(TextChangeEvent event) {
					// Can't modify filters so need to replace
					((SimpleFilterable) overtimeGrid.getContainerDataSource()).removeContainerFilters(pid);

					// (Re)create the filter if necessary
					if (! event.getText().isEmpty())
						((Filterable) overtimeGrid.getContainerDataSource()).addContainerFilter(
								new SimpleStringFilter(pid,
										event.getText(), true, false));
				}
			});
			if(cell != null)
				cell.setComponent(filterField);
		}

		tab.addTab(overtimeGrid,"Horas Extras");

		absenceContainer.addNestedContainerProperty("laborerConstructionsite.activeContract.jobCode");
		Table confirmTable = new Table();
		confirmTable.setContainerDataSource(absenceContainer);
		confirmTable.setSizeFull();
		confirmTable.setEditable(true);
		
		confirmTable.addGeneratedColumn("laborerConstructionsite.activeContract.jobCode", new Table.ColumnGenerator() {
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
		
		confirmTable.setVisibleColumns("laborerConstructionsite.activeContract.jobCode","type","description","fromDate","toDate","confirm");
		confirmTable.setColumnHeaders("Oficio","Tipo","Descripción","Fecha inicio","Fecha Fin","Acción");

		tab.addTab(confirmTable,"Confirmar Licencias y Accidentes");

		Table salaryTable = new Table();
		tab.addTab(salaryTable,"Cálculos");

		return tab;
	}

	Property.ValueChangeListener listener = new Property.ValueChangeListener() {

		@Override
		public void valueChange(ValueChangeEvent event) {
			attendanceDate.removeValueChangeListener(listener);
			populateAttendanceGrid();
			attendanceDate.addValueChangeListener(listener);
		}
	};

	private HorizontalLayout drawTopAttendance() {
		return new HorizontalLayout(){
			{
				setWidth("100%");

				addComponent(new HorizontalLayout(){
					{
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
					}
				});

				HorizontalLayout hl = new HorizontalLayout(){
					{

						setSpacing(true);

						btnConstructionSiteConfirm = new Button("Confirmación Obra",FontAwesome.CHECK);					
						addComponent(btnConstructionSiteConfirm);
						setComponentAlignment(btnConstructionSiteConfirm, Alignment.TOP_RIGHT);
						btnConstructionSiteConfirm.addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								Confirmations confirmations = getConfirmations();
								confirmations.setConstructionSiteCheck(!confirmations.isConstructionSiteCheck());
								service.save(confirmations);
								toogleButtonState(btnConstructionSiteConfirm, confirmations.isConstructionSiteCheck());
							}
						});

						btnCentralConfirm = new Button("Confirmación Central",FontAwesome.CHECK);
						addComponent(btnCentralConfirm);
						setComponentAlignment(btnCentralConfirm, Alignment.TOP_RIGHT);
						btnCentralConfirm.addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								Confirmations confirmations = getConfirmations();
								confirmations.setCentralCheck(!confirmations.isCentralCheck());
								service.save(confirmations);
								toogleButtonState(btnCentralConfirm, confirmations.isCentralCheck());
								//actualiza el estado del boton exportar
								btnExportSoftland.setEnabled(confirmations.isCentralCheck());
							}
						});

						btnExportSoftland = new Button("Exportar a Softland",FontAwesome.FILE_EXCEL_O);
						addComponent(btnExportSoftland);
						setComponentAlignment(btnExportSoftland, Alignment.TOP_RIGHT);

						btnExportSoftland.addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								generateSoftlandFile();
							}

						});
					}
				};

				addComponent(hl);
				setComponentAlignment(hl, Alignment.TOP_RIGHT);


			}
		};
	}

	private void generateSoftlandFile() {
		Notification.show("Descagando archivo de softland...");
	}

	/**
	 * Muestra o no los botones según el perfil del usuario
	 */
	private void configureInterface() {

		Confirmations confirmations = getConfirmations();

		//permite exportar a softland si se tiene doble confirmación y se tiene permisos
		btnExportSoftland.setVisible(SecurityHelper.hasPermission(Permission.GENERAR_SUELDOS_SOFTLAND));
		btnExportSoftland.setEnabled(confirmations.isCentralCheck()); //sólo se habilita si está la confirmación de central

		btnConstructionSiteConfirm.setVisible(SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_OBRA));
		toogleButtonState(btnConstructionSiteConfirm,confirmations.isConstructionSiteCheck());

		btnCentralConfirm.setVisible(SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL));
		toogleButtonState(btnCentralConfirm,confirmations.isCentralCheck());
	}

	private void toogleButtonState(Button btn, boolean confirmations ){
		if(confirmations){

			btn.removeStyleName(Constants.STYLE_CLASS_GREEN_COLOR);
			btn.addStyleName(Constants.STYLE_CLASS_RED_COLOR);
			btn.setCaption("Cancelar Confirmación Obra");
			btn.setIcon(FontAwesome.TIMES);

		}else{

			btn.removeStyleName(Constants.STYLE_CLASS_RED_COLOR);
			btn.addStyleName(Constants.STYLE_CLASS_GREEN_COLOR);
			btn.setCaption("Confirmación Obra");
			btn.setIcon(FontAwesome.CHECK);
		}
	}

	private void populateAttendanceGrid(){

		//		UI.getCurrent().addWindow(progressDialog);

		//		final WorkThread thread = new WorkThread();
		//		thread.start();
		DateTime dt = getAttendanceDate();
		//		DateTime attendanceDate = dt.withDayOfMonth(dt.dayOfMonth().getMinimumValue());
		//		DateTime date2 = attendanceDate.withDayOfMonth( attendanceDate.dayOfMonth().getMaximumValue() );
		reloadMonthGridData(dt);

		reloadMonthAttendanceData(dt);

		// Enable polling and set frequency to 1 seconds
		//		UI.getCurrent().setPollInterval(1000);
	}

	private void reloadMonthGridData(DateTime dt) {

		if(cs == null )
			throw new RuntimeException("No se ha seteado la obra en la vista de asistencia.");

		//cuenta la cantidad de dias entre ambas fechas
		//		int days = Days.daysBetween(initialDate, lastDate).getDays() + 1;

		//		logger.debug("se van a generar las filas para {} dias",days);

		clearGrids();

		List<Overtime> overtime = service.getOvertimeByConstruction(cs,dt);
		overtimeContainer.addAll(overtime);
		overtimeContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});

		List<Attendance> attendance = service.getAttendanceByConstruction(cs,dt);
		attendanceContainer.addAll(attendance);
		attendanceContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});
		
		List<AbsenceVO> absences = service.getAbsencesByConstructionAndMonth(cs,dt);
		absenceContainer.removeAllItems();
		absenceContainer.addAll(absences);
		absenceContainer.sort(new String[]{"laborerConstructionsite.activeContract.jobCode"},new boolean[]{ true });
	}
	/**
	 * Según la fecha y la obra, verifica cual es el estado de confirmación de cada una
	 */
	private void reloadMonthAttendanceData(DateTime dt){
		confirmations = service.getConfirmationsByConstructionsiteAndMonth(cs,dt);
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


	//	private void configAttendanceColumns(DateTime initialDate, DateTime lastDate){
	//
	//		//cuenta la cantidad de dias entre ambas fechas
	//		int days = Days.daysBetween(initialDate, lastDate).getDays() + 1;
	//		logger.debug("cantidad de dias {}",days);
	//		//oculta las columnas que no se usarán
	//		for(int i = 29 ; i <= 31 ; i++){
	//			String propertyId = "d"+i;
	//			//si el dia es menor o igual la cantidad de dias, lo agrega si no está
	//			if( i <= days && attendanceGrid.getColumn(propertyId) == null ){
	//				logger.debug("agregando la columna {} ",i);
	//				attendanceGrid.addColumn(propertyId);//.setSortable(false).setWidth(50);
	//			}else if ( i > days && attendanceGrid.getColumn(propertyId) != null ){ // si no , la quita
	//				logger.debug("quitando la columna {} ",i);
	//				attendanceGrid.removeColumn(propertyId);
	//			}else{
	//				logger.debug("la columna {} se deja como está",i);
	//			}
	//		}
	//
	////		logger.debug("se van a generar {} columnas",days);
	////		
	////		table1.addStyleName("grid-attendace");
	////		table1.setEditorEnabled(true);
	////		table1.setFrozenColumnCount(1);
	////		if(table1.getColumn("laborerConstructionSite") != null )
	////			table1.removeColumn("laborerConstructionSite");
	////		if(table1.getColumn("attendanceId") != null )
	////			table1.removeColumn("attendanceId");
	////		if(table1.getColumn("date") != null )
	////			table1.removeColumn("date");
	////		
	//
	//		//si las columnas no están cargadas, las carga
	////		if( table1.getColumns().size() == 0 ){
	//
	//			//crea 31 columnas y luego oculta las ultimas según sea necesario
	////			Object[] visibleColumns = new Object[ days + 1];
	////			visibleColumns[0] = "activeContract.jobCode";
	////
	////			if( table1.getColumn("activeContract.jobCode") == null ){
	////				table1.addColumn("activeContract.jobCode",Integer.class).setHeaderCaption("Código").setEditable(false).setWidth(100);
	////			}
	////			table1.addStyleName("grid-attendace");
	////			table1.setEditorEnabled(true);
	////			table1.setFrozenColumnCount(1);
	////
	////			for(int i = 1 ; i <= days ; i++){
	////				String propertyId = "day"+i;
	////				if( table1.getColumn(propertyId) == null ){
	////					table1.addColumn("day"+i,AttendanceMark.class).setHeaderCaption( initialDate.plusDays(i - 1).getDayOfMonth()+"" ).setSortable(false).setWidth(50);
	////					visibleColumns[i] = "day"+i; 
	////				}
	////			}
	//		
	////			table1.getColumn("laborerConstructionSite.activeContract.jobCode").setHeaderCas
	////		}
	////		//oculta las columnas que no se usarán
	////		for(int i = 29 ; i <= 31 ; i++){
	////			String propertyId = "day"+i;
	////			//si el dia es menor o igual la cantidad de dias, lo agrega si no está
	////			if( i <= days && table1.getColumn(propertyId) == null ){
	////				logger.debug("agregando la columna {} ",i);
	////				table1.addColumn(propertyId,AttendanceMark.class).setHeaderCaption( initialDate.plusDays(i - 1).getDayOfMonth()+"" ).setSortable(false).setWidth(50);
	////			}else if ( i > days && table1.getColumn(propertyId) != null ){ // si no , la quita
	////				logger.debug("quitando la columna {} ",i);
	////				table1.removeColumn(propertyId);
	////			}else{
	////				logger.debug("la columna {} se deja como está",i);
	////			}
	////		}
	//
	//	}

}
