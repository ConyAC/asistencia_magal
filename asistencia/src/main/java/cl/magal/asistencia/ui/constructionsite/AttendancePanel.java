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
import org.vaadin.dialogs.ConfirmDialog;

import cl.magal.asistencia.entities.AdvancePaymentConfigurations;
import cl.magal.asistencia.entities.AdvancePaymentItem;
import cl.magal.asistencia.entities.Attendance;
import cl.magal.asistencia.entities.Confirmations;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.DateConfigurations;
import cl.magal.asistencia.entities.ExtraParams;
import cl.magal.asistencia.entities.Overtime;
import cl.magal.asistencia.entities.Salary;
import cl.magal.asistencia.entities.enums.AttendanceMark;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.services.ConfigurationService;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.ui.ListenerFieldFactory;
import cl.magal.asistencia.ui.MagalUI;
import cl.magal.asistencia.ui.vo.AbsenceVO;
import cl.magal.asistencia.util.Constants;
import cl.magal.asistencia.util.SecurityHelper;
import cl.magal.asistencia.util.Utils;

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
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
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
	private transient LaborerService laborerService;
	@Autowired
	private transient ConfigurationService configurationService;
	@Autowired
	private transient UserService userService;
	@Autowired
	private transient ConfigurationService confService;
	
	AdvancePaymentConfigurations advancepayment;
	/** CONTAINERS **/
	BeanItemContainer<ExtraParams> extraParamContainer = new BeanItemContainer<ExtraParams>(ExtraParams.class);
	BeanContainer<Long,Attendance> attendanceContainer = new BeanContainer<Long,Attendance>(Attendance.class);
	BeanItemContainer<Overtime> overtimeContainer = new BeanItemContainer<Overtime>(Overtime.class);
	BeanContainer<Long,Salary> salaryContainer = new BeanContainer<Long,Salary>(Salary.class);
	BeanItemContainer<AbsenceVO> absenceContainer = new BeanItemContainer<AbsenceVO>(AbsenceVO.class);
	BeanItemContainer<ConstructionSite> constructionContainer = new BeanItemContainer<ConstructionSite>(ConstructionSite.class);

	/** COMPONENTES **/
	ProgressBar progress;
	Label status;
	Grid attendanceGrid, overtimeGrid,extraGrid;
	Window progressDialog;
	InlineDateField attendanceDate;
	Button btnExportSoftland,btnConstructionSiteConfirm,btnCentralConfirm,btnGenerateSalary,btnSupleObraConfirm,btnSupleCentralConfirm;
	Table confirmTable;
	VerticalLayout root;
	Table salaryTable;

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
		
		setSizeFull();

		//crea la parte superior de la interfaz de asistencia
		final HorizontalLayout topAsistencia = drawTopAttendance();
		//crea las tabs que contienen la información de asistencia
		final TabSheet detalleAsistencia = drawAttendanceDetail();

		root = new VerticalLayout(){
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
		};
		setContent(root);

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
		root.setEnabled(false);		
	}

	private void clearGrids(){
		attendanceContainer.removeAllItems();
		overtimeContainer.removeAllItems();
		absenceContainer.removeAllItems();
		salaryContainer.removeAllItems();
		extraParamContainer.removeAllItems();
	}

	private TabSheet drawAttendanceDetail() {
		
		TabSheet tab = new TabSheet();

		Grid attendanceGrid = drawAttendanceGrid();
		tab.addTab(attendanceGrid,"Asistencia");

		Grid overtimeGrid = drawOvertimeGrid();		
		tab.addTab(overtimeGrid,"Horas Extras");
		
		Grid extraParamsTable = drawExtraParamsGrid();		
		tab.addTab(extraParamsTable,"Parámetros Extra");

		Table confirmTable = drawAbsenceConfirmTable();
		tab.addTab(confirmTable,"Confirmar Licencias y Accidentes");

		VerticalLayout vl = drawSupleLayout();
		tab.addTab(vl,"Suple");		
		
		vl = drawSalaryLayout();
		tab.addTab(vl,"Sueldo");

		return tab;
	}

	private void enableAttendance(boolean state) {
		attendanceGrid.setEnabled(state);
		overtimeGrid.setEnabled(state);
		extraGrid.setEnabled(state);
		confirmTable.setEnabled(state);
//		btnGenerateSalary.setEnabled(state);
	}
	
	private void enableSuple(boolean state) {
		salaryTable.setEnabled(state);
	}

	private Grid drawExtraParamsGrid() {
		
		extraParamContainer.addNestedContainerProperty("laborerConstructionSite.activeContract.jobCode");
		
		GeneratedPropertyContainer gpcontainer =
			    new GeneratedPropertyContainer(extraParamContainer);
		
		gpcontainer.addGeneratedProperty("jobCode",new PropertyValueGenerator<Integer>() {
			    @Override
			    public Integer getValue(Item item, Object itemId,Object propertyId) {
			        int born = (Integer)item.getItemProperty("laborerConstructionSite.activeContract.jobCode").getValue();
			        return born;
			    }

			    @Override
			    public Class<Integer> getType() {
			        return Integer.class;
			    }
			});
		
		extraGrid = new Grid(extraParamContainer);
		extraGrid.setSelectionMode(SelectionMode.SINGLE);
		extraGrid.setSizeFull();
		extraGrid.setEditorFieldGroup(new BeanFieldGroup<ExtraParams>(ExtraParams.class));
		extraGrid.getEditorFieldGroup().addCommitHandler(new CommitHandler() {

			@Override
			public void preCommit(CommitEvent commitEvent) throws CommitException {

			}

			@Override
			public void postCommit(CommitEvent commitEvent) throws CommitException {
				//guarda el elmento
				ExtraParams attedance = ((BeanItem<ExtraParams>) commitEvent.getFieldBinder().getItemDataSource()).getBean();
				service.save(attedance);
				extraParamContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});
			}
		});

		extraGrid.addStyleName("grid-attendace");
		extraGrid.setEditorEnabled(true);
		extraGrid.setFrozenColumnCount(1);
		if(extraGrid.getColumn("laborerConstructionSite") != null )
			extraGrid.removeColumn("laborerConstructionSite");
		if(extraGrid.getColumn("date") != null )
			extraGrid.removeColumn("date");
		if(extraGrid.getColumn("id") != null )
			extraGrid.removeColumn("id");
		
		extraGrid.setColumnOrder("laborerConstructionSite.activeContract.jobCode","bondMov2","km","specialBond","overtimeHours");

		extraParamContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});
		extraGrid.getColumn("laborerConstructionSite.activeContract.jobCode").setHeaderCaption("Oficio").setEditable(false).setWidth(100);
		extraGrid.getColumn("bondMov2").setHeaderCaption("Bono Cargo a Locomocion 2 (No Imponible)");
		extraGrid.getColumn("km").setHeaderCaption("Km");
		extraGrid.getColumn("specialBond").setHeaderCaption("Bono Imponible Especial");
		extraGrid.getColumn("overtimeHours").setHeaderCaption("Horas Sobretiempo");
		
		createHeaders(extraGrid);
		return extraGrid;
	}

	private void createHeaders(final Grid grid) {
		HeaderRow filterRow =  grid.appendHeaderRow();
		//si la propiedad comienza con d (dia) o dmp (dia mes pasado), entonces muestra el dia de la semana correspondiente
		final DateTime dt = getAttendanceDate();
		for (final Object pid: grid.getContainerDataSource().getContainerPropertyIds()) {

			final HeaderCell cell = filterRow.getCell(pid);

			if(pid.equals("laborerConstructionSite.activeContract.jobCode")){
				// Have an input field to use for filter
				TextField filterField = new TextField();
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
					if(monthDay > dt2.dayOfMonth().getMaximumValue() || 
					   ( ((String) pid).startsWith("dmp") && monthDay <= getPastMonthClosingDate().getDayOfMonth())){
						grid.removeColumn(pid);
					}else{ //solo lo setea si el número es mayor a la cantidad de dias del mes
						label.setValue( dt2.withDayOfMonth(monthDay).dayOfWeek().getAsShortText() );
						grid.getColumn(pid).setHeaderCaption(((String) pid).replace("dmp","").replace("dma","")).setSortable(false);
						
					}
					
				}
				
				if(cell != null)
					cell.setComponent(label);
			}
			
			grid.setCellStyleGenerator(new Grid.CellStyleGenerator() {
				
				@Override
				public String getStyle(CellReference cellReference) {
					String post = "";
					if( (cellReference.getValue() instanceof AttendanceMark && !AttendanceMark.ATTEND.equals(cellReference.getValue())) ||
						(cellReference.getValue() instanceof Integer && 0 != (Integer)cellReference.getValue()))
							post = " red-color";
					String pid = (String) cellReference.getPropertyId();
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
		salaryContainer.addNestedContainerProperty("laborerConstructionSite.supleCode");
		salaryContainer.addNestedContainerProperty("laborerConstructionSite.id");
		salaryContainer.setBeanIdProperty("laborerConstructionSite.id");
		VerticalLayout vl = new VerticalLayout(){
			{
				setSpacing(true);

				salaryTable = new Table();
				salaryTable.setWidth("100%");
				salaryTable.setContainerDataSource(salaryContainer);
				
				HorizontalLayout hl = new HorizontalLayout(){
					{
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
											toogleButtonState(btnSupleObraConfirm, confirmations.isSupleObraCheck());
											btnSupleCentralConfirm.setEnabled(confirmations.isSupleObraCheck());
											//si tiene confirmación de obra y no tiene permisos de confirmar central o si tiene ambos checheados, bloqueda la interfaz
											enableSuple(!(
													(confirmations.isSupleObraCheck() && !SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL)) || 
													(confirmations.isSupleObraCheck() && confirmations.isSupleCentralCheck())||
													(!confirmations.isSupleObraCheck() && !SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_OBRA))
													));
											
											if( !confirmations.isSupleObraCheck() && !SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_OBRA)){
												btnSupleObraConfirm.setVisible(false);
											}else if( !confirmations.isSupleObraCheck() && SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL)){
												btnSupleObraConfirm.setVisible(true);
												btnSupleObraConfirm.setEnabled(true);
											}
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
					}					
				};
				
				addComponent(hl);
				setComponentAlignment(hl, Alignment.TOP_RIGHT);
				
				salaryTable.addGeneratedColumn("laborerConstructionSite.supleCode", new Table.ColumnGenerator() {
					
					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {
						HorizontalLayout hl = new HorizontalLayout();
						hl.setSizeFull();
						
						final BeanItem<Salary> beanItem = salaryContainer.getItem(itemId);
						//TODO recuperar posibles codigos de suple
						final ComboBox cb = new ComboBox();
						cb.setImmediate(true);
//						cb.addItem(1);
//						cb.addItem(2);
//						cb.addItem(3);
//						cb.addItem(4);
//						cb.addItem(5);
						for(AdvancePaymentItem item : advancepayment.getAdvancePaymentTable() ){
							cb.addItem(item.getSupleCode());
						}
						cb.setPropertyDataSource(beanItem.getItemProperty("laborerConstructionSite.supleCode"));
						
						hl.addComponent(cb);
						
						hl.addComponent(new Button(null, new Button.ClickListener() {
							
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
						}){
							{
								setIcon(FontAwesome.ARROW_CIRCLE_O_RIGHT);
							}
						});
						
						return hl;
					}
				});

				salaryTable.setVisibleColumns("laborerConstructionSite.activeContract.jobCode","laborerConstructionSite.supleCode","suple");
				salaryTable.setColumnHeaders("Oficio","Código suple","Suple");
				salaryTable.setEditable(true);
				salaryTable.setTableFieldFactory(new TableFieldFactory() {

					@Override
					public Field<?> createField(Container container, final Object itemId,Object propertyId, com.vaadin.ui.Component uiContext) {
						if(propertyId.equals("laborerConstructionSite.activeContract.jobCode") )
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


				addComponent(salaryTable);
				setExpandRatio(salaryTable, 1.0f);
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
		salaryContainer.addNestedContainerProperty("laborerConstructionSite.supleCode");
		VerticalLayout vl = new VerticalLayout(){
			{
				setSpacing(true);
				
				HorizontalLayout hl = new HorizontalLayout(){
					{

						setSpacing(true);

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
											toogleButtonState(btnConstructionSiteConfirm, confirmations.isConstructionSiteCheck());
											btnCentralConfirm.setEnabled(confirmations.isConstructionSiteCheck());
											//si tiene confirmación de obra y no tiene permisos de confirmar central o si tiene ambos checheados, bloqueda la interfaz
											enableAttendance(!(
													(confirmations.isConstructionSiteCheck() && !SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL)) || 
													(confirmations.isConstructionSiteCheck() && confirmations.isCentralCheck())||
													(!confirmations.isConstructionSiteCheck() && !SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_OBRA))
													));

											if( !confirmations.isConstructionSiteCheck() && !SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_OBRA)){
												btnConstructionSiteConfirm.setVisible(false);
											}else if( !confirmations.isConstructionSiteCheck() && SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL)){
												btnConstructionSiteConfirm.setVisible(true);
												btnConstructionSiteConfirm.setEnabled(true);
											}
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
											toogleButtonState(btnCentralConfirm, confirmations.isCentralCheck());
											//actualiza el estado del boton exportar
											btnExportSoftland.setEnabled(confirmations.isCentralCheck());
											configureInterface();
										}
									}
								});
							}
						});

						btnExportSoftland = new Button("Exportar a Softland",FontAwesome.FILE_EXCEL_O);
						btnExportSoftland.setDisableOnClick(true);
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

//				btnGenerateSalary = new Button("Generar Sueldo y Anticipo",FontAwesome.GEARS);
//				btnGenerateSalary.setDisableOnClick(true);
//				btnGenerateSalary.addClickListener(new Button.ClickListener() {
//
//					@Override
//					public void buttonClick(ClickEvent event) {
//
//						//se pide confirmación, pues este proceso reemplazará lo que exista en base de datos para la fecha
//						ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "El siguiente proceso reemplazará la información de sueldo que se tenga guardada. ¿Está seguro de que desea continuar con el proceso?",
//								"Continuar", "Cancelar", new ConfirmDialog.Listener() {
//							public void onClose(ConfirmDialog dialog) {
//								if (dialog.isConfirmed()) {
//
//									try{
//										//comienza el procesamiento de los sueldos y se guarda el resultado en base de datos
//										// recupera los resultados  este procesamiento puede tardar un tiempo
//										List<Salary> salaries = service.calculateSalaries(cs,getAttendanceDate());
//										logger.debug("sueldos calculados {} ",salaries);
//										//limpia
//										salaryContainer.removeAllItems();
//										salaryContainer.addAll(salaries);
//									}catch(Exception e){
//										logger.error("Error al calcular los sueldos",e);
//										String mensaje = "Error al calcular los sueldos.";
//										if( e.getMessage() != null )
//											mensaje = e.getMessage();
//										Notification.show(mensaje,Type.ERROR_MESSAGE);
//									}
//								}
//							}
//						});
//						btnGenerateSalary.setEnabled(true);
//					}
//				});
				final Table salaryTable = new Table();
				salaryTable.setWidth("100%");
				salaryTable.setContainerDataSource(salaryContainer);
				
				salaryTable.addGeneratedColumn("totalLiquido", new Table.ColumnGenerator(){

					@Override
					public Object generateCell(final Table source, final Object itemId,final Object columnId) {
						final BeanItem<Salary> item = (BeanItem<Salary>) salaryContainer.getItem(itemId);
						final Label label  = new Label("<b>"+Utils.formatInteger((Integer) salaryContainer.getContainerProperty(itemId, "roundSalary").getValue())+"</b>"+
								"  ("+Utils.formatInteger((Integer) salaryContainer.getContainerProperty(itemId, columnId).getValue())+")");
						label.setContentMode(ContentMode.HTML);
//						Property.ValueChangeListener listener = ;
						for(final String pid : new String[]{"jornalPromedio","suple"})
							((ValueChangeNotifier)item.getItemProperty(pid)).addValueChangeListener(new Property.ValueChangeListener() {
								
								@Override
								public void valueChange(ValueChangeEvent event) {
									if("jornalPromedio".equals(pid)){
									}else if("suple".equals(pid)){
									}
									Object result = salaryContainer.getItem(itemId).getItemProperty("forceSalary").getValue();
									logger.debug("salary == null {}, {}",result,itemId);
									label.setValue( "<b>"+Utils.formatInteger((Integer) salaryContainer.getContainerProperty(itemId, "roundSalary").getValue())+"</b>"+
											"  ("+Utils.formatInteger((Integer) salaryContainer.getContainerProperty(itemId, columnId).getValue())+")");
									
								}
							});
						return label;
					}
					
				});
				
				salaryTable.setVisibleColumns("laborerConstructionSite.activeContract.jobCode","jornalPromedio","suple","totalLiquido");
				salaryTable.setColumnHeaders("Oficio","Jornal Promedio","Suple","A Pagar (Tot Liquido)");
				salaryTable.setEditable(true);
				salaryTable.setTableFieldFactory(new TableFieldFactory() {

					@Override
					public Field<?> createField(Container container, final Object itemId,Object propertyId, com.vaadin.ui.Component uiContext) {
						if(propertyId.equals("laborerConstructionSite.activeContract.jobCode")||
								propertyId.equals("totalLiquido") ||
								propertyId.equals("suple") )
							return null;
						TextField tf = new TextField();
						tf.setNullRepresentation("");
						tf.setImmediate(true);
						if(propertyId.equals("jornalPromedio")){
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

				addComponent(salaryTable);
				setExpandRatio(salaryTable, 1.0f);
			}
		};
		vl.setSizeFull();
		return vl;
	}

	private Table drawAbsenceConfirmTable() {
		
		absenceContainer.addNestedContainerProperty("laborerConstructionsite.activeContract.jobCode");
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
		return confirmTable;
	}

	private Grid drawOvertimeGrid() {
		
		overtimeContainer.addNestedContainerProperty("laborerConstructionSite.activeContract.jobCode");
		overtimeGrid = new Grid(overtimeContainer);
		overtimeGrid.setSelectionMode(SelectionMode.SINGLE);
		//		overtimeGrid.setSizeFull();
		overtimeGrid.setHeight("100%");
		overtimeGrid.setWidth("100%");
		overtimeGrid.setEditorFieldGroup(new BeanFieldGroup<Overtime>(Overtime.class));
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
			}
		});

		overtimeGrid.addStyleName("grid-attendace");
		overtimeGrid.setEditorEnabled(true);
		overtimeGrid.setFrozenColumnCount(1);
		if(overtimeGrid.getColumn("laborerConstructionSite") != null )
			overtimeGrid.removeColumn("laborerConstructionSite");
		if(overtimeGrid.getColumn("date") != null )
			overtimeGrid.removeColumn("date");
		if(overtimeGrid.getColumn("overtimeAsList") != null )
			overtimeGrid.removeColumn("overtimeAsList");
		if(overtimeGrid.getColumn("id") != null )
			overtimeGrid.removeColumn("id");
		if(overtimeGrid.getColumn("lastMonthOvertimeAsList") != null )
			overtimeGrid.removeColumn("lastMonthOvertimeAsList");

		overtimeGrid.setColumnOrder("laborerConstructionSite.activeContract.jobCode",
				"dmp1","dmp2","dmp3","dmp4","dmp5","dmp6","dmp7","dmp8","dmp9","dmp10","dmp11","dmp12","dmp13","dmp14","dmp15","dmp16"
				,"dmp17","dmp18","dmp19","dmp20","dmp21","dmp22","dmp23","dmp24","dmp25","dmp26","dmp27","dmp28","dmp29","dmp30","dmp31",
				"dma1","dma2","dma3","dma4","dma5","dma6","dma7","dma8","dma9","dma10","dma11","dma12","dma13","dma14","dma15","dma16"
				,"dma17","dma18","dma19","dma20","dma21","dma22","dma23","dma24","dma25","dma26","dma27","dma28","dma29","dma30","dma31");

		overtimeContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});
		overtimeGrid.getColumn("laborerConstructionSite.activeContract.jobCode").setHeaderCaption("Oficio").setEditorField(new TextField(){{setReadOnly(true);}}).setWidth(100);

		createHeaders(overtimeGrid);

		return overtimeGrid;
	}

	private Grid drawAttendanceGrid() {
		
		attendanceContainer.addNestedContainerProperty("laborerConstructionSite.activeContract.jobCode");
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
				salaryContainer.getItem(attendance.getLaborerConstructionSite().getId()).getItemProperty("attendance").setValue(attendance);
				
				salaryContainer.getItem(attendance.getLaborerConstructionSite().getId()).getItemProperty("forceSalary").getValue();
				salaryContainer.getItem(attendance.getLaborerConstructionSite().getId()).getItemProperty("forceSuple").getValue();
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
			}
		});

		attendanceGrid.addStyleName("grid-attendace");
		attendanceGrid.setEditorEnabled(true);
		attendanceGrid.setFrozenColumnCount(1);
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

		attendanceGrid.setColumnOrder("laborerConstructionSite.activeContract.jobCode",
				"dmp1","dmp2","dmp3","dmp4","dmp5","dmp6","dmp7","dmp8","dmp9","dmp10","dmp11","dmp12","dmp13","dmp14","dmp15","dmp16"
				,"dmp17","dmp18","dmp19","dmp20","dmp21","dmp22","dmp23","dmp24","dmp25","dmp26","dmp27","dmp28","dmp29","dmp30","dmp31",
				"dma1","dma2","dma3","dma4","dma5","dma6","dma7","dma8","dma9","dma10","dma11","dma12","dma13","dma14","dma15","dma16"
				,"dma17","dma18","dma19","dma20","dma21","dma22","dma23","dma24","dma25","dma26","dma27","dma28","dma29","dma30","dma31");

		attendanceContainer.sort(new Object[]{"laborerConstructionSite.activeContract.jobCode"}, new boolean[]{true});
		attendanceGrid.getColumn("laborerConstructionSite.activeContract.jobCode").setHeaderCaption("Oficio").setEditorField(new TextField(){{setReadOnly(true);}}).setWidth(100);

		createHeaders(attendanceGrid);
		
		return attendanceGrid;
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
					}
				});

//				HorizontalLayout hl = new HorizontalLayout(){
//					{
//
//						setSpacing(true);
//
//						btnConstructionSiteConfirm = new Button("Confirmación Obra",FontAwesome.CHECK);
//						btnConstructionSiteConfirm.setDisableOnClick(true);
//						addComponent(btnConstructionSiteConfirm);
//						setComponentAlignment(btnConstructionSiteConfirm, Alignment.TOP_RIGHT);
//						btnConstructionSiteConfirm.addClickListener(new Button.ClickListener() {
//
//							@Override
//							public void buttonClick(ClickEvent event) {
//								//mensaje depende del estado
//								final Confirmations confirmations = getConfirmations();
//								ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", 
//										confirmations.isConstructionSiteCheck() ? 
//												"¿Está seguro de cancelar la confirmación de asistencia? Esto desbloqueará la edición en obra de la asistencia del mes.":
//													"¿Está seguro de confirmar la asistencia? Esto bloqueará la edición en obra de la asistencia del mes.",
//													"Continuar", "Cancelar", new ConfirmDialog.Listener() {
//									public void onClose(ConfirmDialog dialog) {
//										if (dialog.isConfirmed()) {
//											confirmations.setConstructionSiteCheck(!confirmations.isConstructionSiteCheck());
//											service.save(confirmations);
//											toogleButtonState(btnConstructionSiteConfirm, confirmations.isConstructionSiteCheck());
//											btnCentralConfirm.setEnabled(confirmations.isConstructionSiteCheck());
//											//si tiene confirmación de obra y no tiene permisos de confirmar central o si tiene ambos checheados, bloqueda la interfaz
//											enableAttendance(!(
//													(confirmations.isConstructionSiteCheck() && !SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL)) || 
//													(confirmations.isConstructionSiteCheck() && confirmations.isCentralCheck())||
//													(!confirmations.isConstructionSiteCheck() && !SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_OBRA))
//													));
//
//											if( !confirmations.isConstructionSiteCheck() && !SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_OBRA)){
//												btnConstructionSiteConfirm.setVisible(false);
//											}else if( !confirmations.isConstructionSiteCheck() && SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL)){
//												btnConstructionSiteConfirm.setVisible(true);
//												btnConstructionSiteConfirm.setEnabled(true);
//											}
//										}
//									}
//
//								});
//
//							}
//						});
//
//						btnCentralConfirm = new Button("Confirmación Central",FontAwesome.CHECK);
//						btnCentralConfirm.setDisableOnClick(true);
//						addComponent(btnCentralConfirm);
//						setComponentAlignment(btnCentralConfirm, Alignment.TOP_RIGHT);
//						btnCentralConfirm.addClickListener(new Button.ClickListener() {
//
//							@Override
//							public void buttonClick(ClickEvent event) {
//								final Confirmations confirmations = getConfirmations();
//								ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", 
//										confirmations.isCentralCheck() ? 
//												"¿Está seguro de cancelar la confirmación de asistencia? Esto desbloqueará la edición en la central de la asistencia del mes.":
//													"¿Está seguro de confirmar la asistencia? Esto bloqueará la edición en la central de la asistencia del mes.",
//													"Continuar", "Cancelar", new ConfirmDialog.Listener() {
//									public void onClose(ConfirmDialog dialog) {
//										if (dialog.isConfirmed()) {
//											confirmations.setCentralCheck(!confirmations.isCentralCheck());
//											service.save(confirmations);
//											toogleButtonState(btnCentralConfirm, confirmations.isCentralCheck());
//											//actualiza el estado del boton exportar
//											btnExportSoftland.setEnabled(confirmations.isCentralCheck());
//											configureInterface();
//										}
//									}
//								});
//							}
//						});
//
//						btnExportSoftland = new Button("Exportar a Softland",FontAwesome.FILE_EXCEL_O);
//						btnExportSoftland.setDisableOnClick(true);
//						addComponent(btnExportSoftland);
//						setComponentAlignment(btnExportSoftland, Alignment.TOP_RIGHT);
//
//						btnExportSoftland.addClickListener(new Button.ClickListener() {
//
//							@Override
//							public void buttonClick(ClickEvent event) {
//								generateSoftlandFile();
//							}
//
//						});
//					}
//				};
//
//				addComponent(hl);
//				setComponentAlignment(hl, Alignment.TOP_RIGHT);


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
		//si está confirmado, lo habilita solo si tiene permiso para confirmar asistencia central
		if(confirmations.isConstructionSiteCheck()){
			//se asegura que lo vea
			btnConstructionSiteConfirm.setVisible(true);
			btnConstructionSiteConfirm.setEnabled( !confirmations.isCentralCheck() && SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL));
		}else{
			btnConstructionSiteConfirm.setEnabled(true);
		}
		//deshabilita si tiene confirmación de obra y no tiene permisos de confirmar central, si tiene ambos checheados, bloqueda la interfaz o si no tiene confirmacion de obra y no tiene permiso de obra 
		enableAttendance(!(
				(confirmations.isConstructionSiteCheck() && !SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL)) || 
				(confirmations.isConstructionSiteCheck() && confirmations.isCentralCheck()) ||
				(!confirmations.isConstructionSiteCheck() && !SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_OBRA))
				));

		btnCentralConfirm.setVisible(SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL));
		toogleButtonState(btnCentralConfirm,confirmations.isCentralCheck());
		btnCentralConfirm.setEnabled( (confirmations.isConstructionSiteCheck() && !confirmations.isCentralCheck()) ||
				(confirmations.isConstructionSiteCheck() && confirmations.isCentralCheck() && SecurityHelper.hasPermission(Permission.DESBLOQUEDAR_ASISTENCIA) ));
		
		
		/**
		 * Use el mismo código de arriba, reemplace los valores para considerar los botones del suple que se comportan igual.
		 */
		btnSupleObraConfirm.setVisible(SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_OBRA));
		toogleButtonState(btnSupleObraConfirm, confirmations.isSupleObraCheck());
		//si está confirmado, lo habilita solo si tiene permiso para confirmar asistencia central
		if(confirmations.isSupleObraCheck()){
			//se asegura que lo vea
			btnSupleObraConfirm.setVisible(true);
			btnSupleObraConfirm.setEnabled( !confirmations.isSupleCentralCheck() && SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL));
		}else{
			btnSupleObraConfirm.setEnabled(true);
		}
		//deshabilita si tiene confirmación de obra y no tiene permisos de confirmar central, si tiene ambos checheados, bloqueda la interfaz o si no tiene confirmacion de obra y no tiene permiso de obra 		
		enableSuple(!(
				(confirmations.isSupleObraCheck() && !SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL)) || 
				(confirmations.isSupleObraCheck() && confirmations.isSupleCentralCheck()) ||
				(!confirmations.isSupleObraCheck() && !SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_OBRA))
				));

		btnSupleCentralConfirm.setVisible(SecurityHelper.hasPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL));
		toogleButtonState(btnSupleCentralConfirm, confirmations.isSupleCentralCheck());
		btnSupleCentralConfirm.setEnabled( (confirmations.isSupleObraCheck() && !confirmations.isSupleCentralCheck()) ||
				(confirmations.isSupleObraCheck() && confirmations.isSupleCentralCheck() && SecurityHelper.hasPermission(Permission.DESBLOQUEDAR_ASISTENCIA) ));
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

	private void populateAttendanceGrid(){

		//		UI.getCurrent().addWindow(progressDialog);

		//		final WorkThread thread = new WorkThread();
		//		thread.start();
		DateTime dt = getAttendanceDate();
		//		DateTime attendanceDate = dt.withDayOfMonth(dt.dayOfMonth().getMinimumValue());
		//		DateTime date2 = attendanceDate.withDayOfMonth( attendanceDate.dayOfMonth().getMaximumValue() );
		reloadMonthGridData(dt);

		reloadMonthAttendanceData(dt);
		
		configureInterface();

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

			List<ExtraParams> params = service.getExtraParamsByConstructionAndMonth(cs,dt);
			//limpia
			extraParamContainer.addAll(params);
			extraParamContainer.sort(new String[]{"laborerConstructionsite.activeContract.jobCode"},new boolean[]{ true });

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
