package cl.magal.asistencia.ui.constructionsite;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cl.magal.asistencia.entities.Attendance;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.enums.AttendanceMark;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.UserService;

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

	@Autowired
	private transient ConstructionSiteService service;
	@Autowired
	private transient UserService userService;

	BeanItemContainer<Attendance> attendanceLaborerContainer = new BeanItemContainer<Attendance>(Attendance.class);
	ConstructionSite cs;

	public void setCs(ConstructionSite cs) {
		this.cs = cs;
	}

	InlineDateField attendanceDate;

	private DateTime getAttendanceDate() {
		if(attendanceDate.getValue() == null ){
			attendanceDate.setValue(new Date());
		}
		return new DateTime(attendanceDate.getValue());
	}

	ProgressBar progress;
	Label status;
	Grid table1;
	Window progressDialog;

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

	private void clearAttendanceTable(){
		attendanceLaborerContainer.removeAllItems();
		//		table1.removeAllColumns();
	}

	private void configAttendanceColumns(DateTime initialDate, DateTime lastDate){

		//cuenta la cantidad de dias entre ambas fechas
		int days = Days.daysBetween(initialDate, lastDate).getDays() + 1;
		logger.debug("cantidad de dias {}",days);
		//oculta las columnas que no se usarán
		for(int i = 29 ; i <= 31 ; i++){
			String propertyId = "d"+i;
			//si el dia es menor o igual la cantidad de dias, lo agrega si no está
			if( i <= days && table1.getColumn(propertyId) == null ){
				logger.debug("agregando la columna {} ",i);
				table1.addColumn(propertyId);//.setSortable(false).setWidth(50);
			}else if ( i > days && table1.getColumn(propertyId) != null ){ // si no , la quita
				logger.debug("quitando la columna {} ",i);
				table1.removeColumn(propertyId);
			}else{
				logger.debug("la columna {} se deja como está",i);
			}
		}

//		logger.debug("se van a generar {} columnas",days);
//		
//		table1.addStyleName("grid-attendace");
//		table1.setEditorEnabled(true);
//		table1.setFrozenColumnCount(1);
//		if(table1.getColumn("laborerConstructionSite") != null )
//			table1.removeColumn("laborerConstructionSite");
//		if(table1.getColumn("attendanceId") != null )
//			table1.removeColumn("attendanceId");
//		if(table1.getColumn("date") != null )
//			table1.removeColumn("date");
//		

		//si las columnas no están cargadas, las carga
//		if( table1.getColumns().size() == 0 ){

			//crea 31 columnas y luego oculta las ultimas según sea necesario
//			Object[] visibleColumns = new Object[ days + 1];
//			visibleColumns[0] = "activeContract.jobCode";
//
//			if( table1.getColumn("activeContract.jobCode") == null ){
//				table1.addColumn("activeContract.jobCode",Integer.class).setHeaderCaption("Código").setEditable(false).setWidth(100);
//			}
//			table1.addStyleName("grid-attendace");
//			table1.setEditorEnabled(true);
//			table1.setFrozenColumnCount(1);
//
//			for(int i = 1 ; i <= days ; i++){
//				String propertyId = "day"+i;
//				if( table1.getColumn(propertyId) == null ){
//					table1.addColumn("day"+i,AttendanceMark.class).setHeaderCaption( initialDate.plusDays(i - 1).getDayOfMonth()+"" ).setSortable(false).setWidth(50);
//					visibleColumns[i] = "day"+i; 
//				}
//			}
		
//			table1.getColumn("laborerConstructionSite.activeContract.jobCode").setHeaderCas
//		}
//		//oculta las columnas que no se usarán
//		for(int i = 29 ; i <= 31 ; i++){
//			String propertyId = "day"+i;
//			//si el dia es menor o igual la cantidad de dias, lo agrega si no está
//			if( i <= days && table1.getColumn(propertyId) == null ){
//				logger.debug("agregando la columna {} ",i);
//				table1.addColumn(propertyId,AttendanceMark.class).setHeaderCaption( initialDate.plusDays(i - 1).getDayOfMonth()+"" ).setSortable(false).setWidth(50);
//			}else if ( i > days && table1.getColumn(propertyId) != null ){ // si no , la quita
//				logger.debug("quitando la columna {} ",i);
//				table1.removeColumn(propertyId);
//			}else{
//				logger.debug("la columna {} se deja como está",i);
//			}
//		}

	}

	private TabSheet drawAttendanceDetail() {
		TabSheet tab = new TabSheet();

		attendanceLaborerContainer.addNestedContainerProperty("laborerConstructionSite.activeContract.jobCode");
		table1 = new Grid(attendanceLaborerContainer);
		table1.setSelectionMode(SelectionMode.SINGLE);
		table1.setSizeFull();
		table1.setEditorFieldGroup(new BeanFieldGroup<Attendance>(Attendance.class));
		table1.setEditorFieldFactory(new DefaultFieldGroupFieldFactory(){
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
		table1.getEditorFieldGroup().addCommitHandler(new CommitHandler() {
			
			@Override
			public void preCommit(CommitEvent commitEvent) throws CommitException {
				
			}
			
			@Override
			public void postCommit(CommitEvent commitEvent) throws CommitException {
				//guarda el elmento
				Attendance attedance = ((BeanItem<Attendance>) commitEvent.getFieldBinder().getItemDataSource()).getBean();
				service.save(attedance);
				attendanceLaborerContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});
			}
		});
		
		table1.addStyleName("grid-attendace");
		table1.setEditorEnabled(true);
		table1.setFrozenColumnCount(1);
		if(table1.getColumn("laborerConstructionSite") != null )
			table1.removeColumn("laborerConstructionSite");
		if(table1.getColumn("attendanceId") != null )
			table1.removeColumn("attendanceId");
		if(table1.getColumn("date") != null )
			table1.removeColumn("date");
		
		table1.setColumnOrder("laborerConstructionSite.activeContract.jobCode","d1","d2","d3","d4","d5","d6","d7","d8","d9","d10","d11","d12","d13","d14","d15","d16"
				,"d17","d18","d19","d20","d21","d22","d23","d24","d25","d26","d27","d28","d29","d30","d31");
		
		attendanceLaborerContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});
		table1.getColumn("laborerConstructionSite.activeContract.jobCode").setHeaderCaption("Código").setEditable(false).setWidth(100);
		
		HeaderRow filterRow =  table1.appendHeaderRow();
		
		for (final Object pid: table1.getContainerDataSource().getContainerPropertyIds()) {
			
			final HeaderCell cell = filterRow.getCell(pid);
	
			// Have an input field to use for filter
			TextField filterField = new TextField();
			if(pid.equals("laborerConstructionSite.activeContract.jobCode")) filterField.setWidth("100%");
			else {
				filterField.setWidth("50px");
				if(table1.getColumn(pid) != null )
					table1.getColumn(pid).setHeaderCaption(((String) pid).replace("d","")).setSortable(false);//.setWidth(50);
			}
	
			filterField.setHeight("90%");
	
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
			if(cell != null)
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
								logger.debug("creando inlinedate1");
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
				Button btn = new Button("Exportar",FontAwesome.FILE_EXCEL_O);
				addComponent(btn);
				setComponentAlignment(btn, Alignment.TOP_RIGHT);

			}
		};
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
	}

	private void populateAttendanceGrid(){
		
//		UI.getCurrent().addWindow(progressDialog);

//		final WorkThread thread = new WorkThread();
//		thread.start();
		DateTime dt = getAttendanceDate();
//		DateTime attendanceDate = dt.withDayOfMonth(dt.dayOfMonth().getMinimumValue());
//		DateTime date2 = attendanceDate.withDayOfMonth( attendanceDate.dayOfMonth().getMaximumValue() );
		reloadAttendance(dt);

		// Enable polling and set frequency to 1 seconds
//		UI.getCurrent().setPollInterval(1000);
	}

	private void reloadAttendance(DateTime dt) {

		if(cs == null )
			throw new RuntimeException("No se ha seteado la obra en la vista de asistencia.");

		//cuenta la cantidad de dias entre ambas fechas
//		int days = Days.daysBetween(initialDate, lastDate).getDays() + 1;

//		logger.debug("se van a generar las filas para {} dias",days);

		clearAttendanceTable();
		
		List<Attendance> attendance = service.getAttendanceByConstruction(cs,dt);
		logger.debug("lista de asistencia para el mes dado {} ",attendance);
		attendanceLaborerContainer.addAll(attendance);
		attendanceLaborerContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});
//		for(LaborerConstructionsite lc : laborers){
//			Attendance row = new Attendance();
//
//			row.setAttendanceId((long) days++);
//			row.setLaborerConstructionSite( lc );
//			row.setD1( randomAttendance() );
//			row.setD2( randomAttendance() );
//			row.setD3( randomAttendance() );
//			row.setD4( randomAttendance() );
//			row.setD5( randomAttendance() );
//			
//			attendanceLaborerContainer.addBean(row);
//		}
	}

	// A thread to do some work
	class WorkThread extends Thread {

		@Override
		public void run() {

			try{
				//dependiendo de la fecha seleccionada llena la información de la tabla
				DateTime dt = getAttendanceDate();
				DateTime attendanceDate = dt.withDayOfMonth(dt.dayOfMonth().getMinimumValue());
				DateTime date2 = attendanceDate.withDayOfMonth( attendanceDate.dayOfMonth().getMaximumValue() );

				getUI().getSession().getLockInstance().lock();
				
				configAttendanceColumns(attendanceDate,date2);
				reloadAttendance(dt);
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
