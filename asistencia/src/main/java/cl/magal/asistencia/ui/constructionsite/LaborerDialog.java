package cl.magal.asistencia.ui.constructionsite;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.velocity.VelocityEngineUtils;

import cl.magal.asistencia.entities.Absence;
import cl.magal.asistencia.entities.Accident;
import cl.magal.asistencia.entities.AccidentLevel;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.Tool;
import cl.magal.asistencia.entities.Vacation;
import cl.magal.asistencia.entities.enums.AbsenceType;
import cl.magal.asistencia.entities.enums.Afp;
import cl.magal.asistencia.entities.enums.Job;
import cl.magal.asistencia.entities.enums.MaritalStatus;
import cl.magal.asistencia.entities.enums.ToolStatus;
import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.ui.AbstractWindowEditor;
import cl.magal.asistencia.ui.OnValueChangeFieldFactory;
import cl.magal.asistencia.ui.workerfile.vo.HistoryVO;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class LaborerDialog extends AbstractWindowEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8280001172496734091L;
	transient Logger logger = LoggerFactory.getLogger(LaborerDialog.class);

	protected Button btnAddH, btnAddP;
	BeanItemContainer<Tool> toolContainer= new BeanItemContainer<Tool>(Tool.class);
	BeanItemContainer<HistoryVO> historyContainer = new BeanItemContainer<HistoryVO>(HistoryVO.class);

	@Autowired
	transient LaborerService service;
	transient private VelocityEngine velocityEngine;
	
	public LaborerDialog(BeanItem<LaborerConstructionsite> item,LaborerService service ){
		super(item);
		if(service == null )
			throw new RuntimeException("Error al crear el dialgo, el servicio de trabajadores no puede ser nulo.");

		this.velocityEngine = velocityEngine;
		this.service = service;
		setWidth("70%");

		init();
	}

	public void init(){
		super.init();
		LaborerConstructionsite laborer = (LaborerConstructionsite) getItem().getBean();
		List<HistoryVO> history = service.getLaborerHistory(laborer.getLaborer());
		historyContainer.addAll(history);
		historyContainer.addNestedContainerProperty("constructionSite.name");
		Filter filter = new Compare.Equal("constructionSite", laborer.getConstructionsite());
		//filtra la obra en la que se encuentra
		historyContainer.addContainerFilter(new Not(filter));
	}

	@Override
	protected Component createBody() {

		TabSheet tab = new TabSheet();

		//tab de Resumen
		//tab.addTab(drawInfo(),"Resumen"); -> no cuando se está creando.
		//tab de Información
		tab.addTab(drawInfo(),"Información");
		//tab de vacaciones
		tab.addTab(drawVacations(),"Vacaciones");
		//tab de perstamos y herramientas
		tab.addTab(drawPyH(),"Préstamos/Herramientas");
		//tab de accidentes y licencias
		tab.addTab(drawAccidentes(),"Accidentes");
		//tab de accidentes y licencias
		tab.addTab(drawLicencias(),"Licencias");
		//tab de contratos y finiquitos
		tab.addTab(drawCyF(),"Contratos/Finiquitos");
		//tab de histórico
		tab.addTab(drawHistorico(),"Histórico");

		return tab;
	}

	protected VerticalLayout drawPyH() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();

		/*Herramientas*/
		VerticalLayout vh = new VerticalLayout();
		vh.setSizeFull();

		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setSpacing(true);		
		vh.addComponent(hl);

		final TextField herramienta = new TextField("Herramienta");
		hl.addComponent(herramienta);
		final TextField monto_h = new TextField("Monto");
		hl.addComponent(monto_h);
		final DateField fecha_h = new DateField("Fecha");
		hl.addComponent(fecha_h);
		final TextField cuota_h = new TextField("Cuota");
		hl.addComponent(cuota_h);
		final ComboBox estado_h = new ComboBox("Estado");
		estado_h.setNullSelectionAllowed(false);
		for(ToolStatus t : ToolStatus.values()){
			estado_h.addItem(t);
		}
		hl.addComponent(estado_h);

		final Table table_h = new Table("Herramientas"){
			{
				setWidth("100%");				
				addContainerProperty("name", String.class, "");
				addContainerProperty("price", String.class, "");
				addContainerProperty("dateBuy",String.class, "");
				addContainerProperty("fee", String.class, "");
				addContainerProperty("status", String.class, "");
				setVisibleColumns("name","price","dateBuy", "fee", "status");
				setColumnHeaders("Herramienta","Monto","Fecha", "Cuota", "Estado");

				setPageLength(4);
			}
		};	
		vh.addComponent(table_h);

		btnAddH = new Button(null,FontAwesome.PLUS);
		hl.addComponent(btnAddH);
		btnAddH.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Tool t = new Tool();
				t.setName(herramienta.getValue());
				t.setPrice(Integer.valueOf(monto_h.getValue()));
				t.setFee(Integer.valueOf(cuota_h.getValue()));
				t.setStatus((ToolStatus)estado_h.getValue());
				t.setDateBuy(fecha_h.getValue());
				table_h.setContainerDataSource(toolContainer);
				toolContainer.addBean(t);
			}
		});		
		vh.setComponentAlignment(hl, Alignment.TOP_RIGHT);

		/*Préstamo*/
		VerticalLayout vp = new VerticalLayout();
		vp.setSizeFull();

		HorizontalLayout hl2 = new HorizontalLayout();
		hl2.setWidth("100%");
		hl2.setSpacing(true);		
		vp.addComponent(hl2);

		TextField monto_p = new TextField("Monto");
		hl2.addComponent(monto_p);
		DateField fecha_p = new DateField("Fecha");
		hl2.addComponent(fecha_p);
		TextField cuota_p = new TextField("Cuota");
		hl2.addComponent(cuota_p);
		TextField estado_p = new TextField("Estado");
		hl2.addComponent(estado_p);

		btnAddP = new Button(null,FontAwesome.PLUS);
		hl2.addComponent(btnAddP);
		btnAddP.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				//				final ConstructionSite cs = item.getBean();
				//				if(cs == null){
				//					Notification.show("Debe seleccionar una obra",Type.ERROR_MESSAGE);
				//					return;
				//				}
			}
		});
		final Table table_p = new Table("Préstamos"){
			{
				setWidth("100%");				
				addContainerProperty("monto", String.class, "");
				addContainerProperty("fecha", String.class, "");
				addContainerProperty("cuota", String.class, "");
				addContainerProperty("estado", String.class, "");
				setVisibleColumns("monto","fecha", "cuota", "estado");
				setColumnHeaders("Monto","Fecha", "Cuota", "Estado");

				setPageLength(4);
			}
		};
		vp.addComponent(table_p);
		vp.setComponentAlignment(hl2, Alignment.TOP_RIGHT);

		vl.addComponent(vh);
		vl.addComponent(vp);

		return vl;
	}

	protected VerticalLayout drawAyL() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();

		Label l = new Label("En construcción...");
		vl.addComponent(l);
		return vl;
	}

	protected VerticalLayout drawCyF() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();

		Label l = new Label("En construcción...");
		vl.addComponent(l);
		return vl;
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

		table.setContainerDataSource(historyContainer);
		table.setWidth("100%");
		historyContainer.addNestedContainerProperty("constructionSite.name");
		table.setVisibleColumns("constructionSite.name","job","averageWage","reward","numberOfAccidents","endingDate");
		table.setColumnHeaders("Obra","Rol","Jornal Promedio","Premio","N° Accidentes","Fecha Termino");

		vl.addComponent(table);

		return vl;
	}

	private Component drawAccidentes() {
		return new VerticalLayout(){
			{
				final BeanItemContainer<Accident> beanItem = new BeanItemContainer<Accident>(Accident.class);
				List<Accident> accidents = (List<Accident>)getItem().getItemProperty("accidents").getValue();
				if(accidents != null && logger != null )
					logger.debug("accidents {}",accidents);
				beanItem.addAll(accidents );

				setMargin(true);
				setSpacing(true);
				final Table table = new Table();

				addComponent(new GridLayout(3,2){
					{
						setWidth("100%");
						addComponent(new Label(""),0,0);

						addComponent(new Label(""),0,1);

						addComponent(new Button(null,new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								LaborerConstructionsite laborer = (LaborerConstructionsite) getItem().getBean();
								if(laborer == null ) throw new RuntimeException("El trabajador no es válido.");
								Accident accident = new Accident();
								laborer.addAccident(accident);
								beanItem.addBean(accident);

							}
						}){{setIcon(FontAwesome.PLUS);}},2,0);

					}
				});
				table.setPageLength(5);
				table.setWidth("100%");
				table.setContainerDataSource(beanItem);
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
							field = new TextField();
							((TextField)field).setNullRepresentation("");
						}

						else if(  propertyId.equals("fromDate") || propertyId.equals("toDate") ){
							field = new DateField();
						}
						else if(  propertyId.equals("accidentLevel") ){
							field = new ComboBox();
							field.setPropertyDataSource(container.getContainerProperty(itemId, propertyId));
							for(AccidentLevel absenceType : AccidentLevel.values()){
								((ComboBox)field).addItem(absenceType);
							}

						} else {
							return null;
						}

						((AbstractField<?>)field).setImmediate(true);
						return field;
					}
				});

				table.setVisibleColumns("accidentLevel","description","fromDate","toDate","total");
				table.setColumnHeaders("Gravedad","Descripción","Desde","Hasta","Total días");
				table.setEditable(true);				
				addComponent(table);
			}
		};
	}

	private Component drawLicencias() {
		return new VerticalLayout(){
			{
				final BeanItemContainer<Absence> beanItem = new BeanItemContainer<Absence>(Absence.class);
				beanItem.addAll((Collection<? extends Absence>) getItem().getItemProperty("absences").getValue());

				setMargin(true);
				setSpacing(true);
				final Table table = new Table();

				addComponent(new GridLayout(3,2){
					{
						setWidth("100%");
						addComponent(new Label(""),0,0);

						addComponent(new Label(""),0,1);

						addComponent(new Button(null,new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								LaborerConstructionsite laborer = (LaborerConstructionsite) getItem().getBean();
								if(laborer == null ) throw new RuntimeException("El trabajador no es válido.");
								Absence absence = new Absence();
								laborer.addAbsence(absence);
								beanItem.addBean(absence);

							}
						}){{setIcon(FontAwesome.PLUS);}},2,0);

					}
				});
				table.setPageLength(5);
				table.setWidth("100%");
				table.setContainerDataSource(beanItem);
				table.setImmediate(true);
				table.addGeneratedColumn("total", new Table.ColumnGenerator() {

					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {

						final BeanItem<?> item = (BeanItem<?>) source.getItem(itemId);
						final Label label  = new Label(""+ source.getContainerProperty(itemId, columnId).getValue());
						Property.ValueChangeListener listener = new Property.ValueChangeListener() {
							@Override
							public void valueChange(Property.ValueChangeEvent event) {
								label.setValue(((Absence) item.getBean()).getTotal()+"");
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
							field = new TextField();
							((TextField)field).setNullRepresentation("");
						}

						else if(  propertyId.equals("fromDate") || propertyId.equals("toDate") ){
							field = new DateField();
						}
						else if(  propertyId.equals("absenceType") ){
							field = new ComboBox();
							field.setPropertyDataSource(container.getContainerProperty(itemId, propertyId));
							for(AbsenceType absenceType : AbsenceType.values()){
								((ComboBox)field).addItem(absenceType);
							}

						} else {
							return null;
						}

						((AbstractField<?>)field).setImmediate(true);
						return field;
					}
				});

				table.setVisibleColumns("absenceType","description","fromDate","toDate","total");
				table.setColumnHeaders("Tipo","Descripción","Desde","Hasta","Total");
				table.setEditable(true);				
				addComponent(table);
			}
		};
	}
	BeanItemContainer<Vacation> vacationContainer;
	private Component drawVacations() {
		return new VerticalLayout(){
			{
				vacationContainer = new BeanItemContainer<Vacation>(Vacation.class);
				vacationContainer.addAll(new ArrayList<Vacation>((Collection<? extends Vacation>) getItem().getItemProperty("vacations").getValue()));
				
				setMargin(true);
				setSpacing(true);
				Table vacationTable = new Table();

				addComponent(new GridLayout(3,2){
					{
						setWidth("100%");
						addComponent(new Label("Días usados"),0,0);
						addComponent(new Label("3"),1,0);

						addComponent(new Label("Días disponibles"),0,1);
						addComponent(new Label("4"),1,1);

						addComponent(new Button(null,new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								Vacation vacation = new Vacation();
								vacationContainer.addBean(vacation);

							}
						}){{setIcon(FontAwesome.PLUS);}},2,0);

					}
				});
				vacationTable.setPageLength(5);
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
					public Object generateCell(Table source, Object itemId, Object columnId) {
						Button btnPrint = new Button(null,FontAwesome.PRINT);

						btnPrint.addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								
								final Map<String, Object> input = new HashMap<String, Object>();
								input.put("constructionSite", ((LaborerConstructionsite)getItem().getBean()).getConstructionsite());
								input.put("laborer", ((LaborerConstructionsite)getItem().getBean()).getLaborer());
								final String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/vacation_doc.vm", "UTF-8", input);

								StreamResource.StreamSource source2 = new StreamResource.StreamSource() {

									public InputStream getStream() {
										//throw new UnsupportedOperationException("Not supported yet.");
										return new ByteArrayInputStream(body.getBytes());
									}
								};
								StreamResource resource = new StreamResource(source2, "TestReport.html");

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
						
						return btnPrint;
					}

				});

				vacationTable.setVisibleColumns("fromDate","toDate","total","print");
				vacationTable.setColumnHeaders("Desde","Hasta","Total","Imprimir");
				vacationTable.setEditable(true);				
				addComponent(vacationTable);
			}
		};
	}

	protected VerticalLayout drawInfo() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();

		GridLayout detalleObrero = new GridLayout(3,5);
		detalleObrero.setMargin(true);
		detalleObrero.setSpacing(true);
		vl.addComponent(detalleObrero);

		// Loop through the properties, build fields for them and add the fields
		// to this UI
		for (Object propertyId : new String[]{"rut","firstname","secondname","lastname", "secondlastname", "dateBirth", "address", "mobileNumber", "phone", "dateAdmission", "job", "afp", "maritalStatus"}) {
			if(propertyId.equals("laborerId") || propertyId.equals("constructionSites") || propertyId.equals("contractId") || propertyId.equals("teamId"))
				;
			else if(propertyId.equals("afp")){
				ComboBox afpField = new ComboBox("AFP");
				afpField.setNullSelectionAllowed(false);
				for(Afp a : Afp.values()){
					afpField.addItem(a);
				}
				detalleObrero.addComponent(afpField);
				bind(afpField, "laborer.afp");   
				detalleObrero.setComponentAlignment(afpField, Alignment.MIDDLE_CENTER);
			}else if(propertyId.equals("job")){
				ComboBox jobField = new ComboBox("Oficio");
				jobField.setNullSelectionAllowed(false);
				for(Job j : Job.values()){
					jobField.addItem(j);
				}
				detalleObrero.addComponent(jobField);
				bind(jobField, "job");    
				detalleObrero.setComponentAlignment(jobField, Alignment.MIDDLE_CENTER);
			}else if(propertyId.equals("maritalStatus")){
				ComboBox msField = new ComboBox("Estado Civil");
				msField.setNullSelectionAllowed(false);
				for(MaritalStatus ms : MaritalStatus.values()){
					msField.addItem(ms);
				}
				detalleObrero.addComponent(msField);
				bind(msField, "laborer.maritalStatus");   
				detalleObrero.setComponentAlignment(msField, Alignment.MIDDLE_CENTER);
			}else{        		
				String t = tradProperty(propertyId);
				Field<?> field = buildAndBind(t, "laborer."+propertyId);
				if(field instanceof TextField){
					((TextField)field).setNullRepresentation("");
					field.addValidator(new BeanValidator(Laborer.class, (String) propertyId));
				}
				detalleObrero.addComponent(field);
				detalleObrero.setComponentAlignment(field, Alignment.MIDDLE_CENTER);
			}
		}

		detalleObrero.setWidth("100%");

		return vl;
	}


	private Component drawInformation(){

		GridLayout detalleObrero = new GridLayout(2,5);
		detalleObrero.setMargin(true);
		detalleObrero.setSpacing(true);

		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		detalleObrero.addComponent(hl,0,0,1,0);
		detalleObrero.setComponentAlignment(hl, Alignment.TOP_RIGHT);


		//agrega un boton que hace el commit
		//	    Button add = new Button(null,new Button.ClickListener() {
		//
		//	    	@Override
		//	    	public void buttonClick(ClickEvent event) {
		//	    		try {
		//	    			fieldGroup.commit();
		//	    			Laborer laborer = fieldGroup.getItemDataSource().getBean();
		//	    			constructionSiteService.addLaborerToConstructionSite(laborer,cs);				
		//	    			laborerContainer.addBean(laborer);
		//	    			close();
		//	    		} catch (Exception e) {
		//	    			logger.error("Error al guardar la información del obrero");
		//	    			Notification.show("Es necesario agregar todos los campos obligatorios", Type.ERROR_MESSAGE);
		//	    		}
		//
		//	    	}
		//	    }){{
		//	    	setIcon(FontAwesome.SAVE);
		//	    }};
		//	    hl.addComponent(add);
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
				bind(afpField, "afp");    			
			}else if(propertyId.equals("job")){
				ComboBox jobField = new ComboBox("Oficio");
				jobField.setNullSelectionAllowed(false);
				for(Job j : Job.values()){
					jobField.addItem(j);
				}
				detalleObrero.addComponent(jobField);
				bind(jobField, "job");    
			}else if(propertyId.equals("maritalStatus")){
				ComboBox msField = new ComboBox("Estado Civil");
				msField.setNullSelectionAllowed(false);
				for(MaritalStatus ms : MaritalStatus.values()){
					msField.addItem(ms);
				}
				detalleObrero.addComponent(msField);
				bind(msField, "maritalStatus");    
			}else{        		
				String t = tradProperty(propertyId);
				Field field = buildAndBind(t, propertyId);
				if(field instanceof TextField){
					((TextField)field).setNullRepresentation("");
				}
				detalleObrero.addComponent(field);
				detalleObrero.setComponentAlignment(field, Alignment.MIDDLE_CENTER);
			}
		}

		detalleObrero.setWidth("100%");

		return detalleObrero;
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

	//	public static final String EXTENSION = ".pdf";
	//	public String PRESCRIPTION_URL = "vacation_doc.xsl";
	// 
	//	public ByteArrayOutputStream createPDFFile(ByteArrayOutputStream xmlSource, String templateFilePath) throws IOException {
	////		URL url = new File(templateFilePath + PRESCRIPTION_URL).toURI().toURL();
	//		ClassLoader classLoader = getClass().getClassLoader();
	//		URL url = new File(classLoader.getResource(templateFilePath + PRESCRIPTION_URL).getFile()).toURI().toURL();
	//		// creation of transform source
	//		javax.xml.transform.stream.StreamSource transformSource = new javax.xml.transform.stream.StreamSource(url.openStream());
	//		// create an instance of fop factory
	//		FopFactory fopFactory = FopFactory.newInstance();
	//		// a user agent is needed for transformation
	//		FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
	//		// to store output
	//		ByteArrayOutputStream pdfoutStream = new ByteArrayOutputStream();
	//		javax.xml.transform.stream.StreamSource source = new javax.xml.transform.stream.StreamSource(new ByteArrayInputStream(xmlSource.toByteArray()));
	//		Transformer xslfoTransformer;
	//		try {
	//			TransformerFactory transfact = TransformerFactory.newInstance();
	// 
	//			xslfoTransformer = transfact.newTransformer(transformSource);
	//			// Construct fop with desired output format
	//			Fop fop;
	//			try {
	//				fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, pdfoutStream);
	//				// Resulting SAX events (the generated FO)
	//				// must be piped through to FOP
	//				Result res = new SAXResult(fop.getDefaultHandler());
	// 
	//				// Start XSLT transformation and FOP processing
	//				try {
	//					// everything will happen here..
	//					xslfoTransformer.transform(source, res);
	// 
	//					// if you want to save PDF file use the following code
	//					return pdfoutStream;
	// 
	//				} catch (TransformerException e) {
	//					e.printStackTrace();
	//				}
	//			} catch (FOPException e) {
	//				e.printStackTrace();
	//			}
	//		} catch (TransformerConfigurationException e) {
	//			e.printStackTrace();
	//		} catch (TransformerFactoryConfigurationError e) {
	//			e.printStackTrace();
	//		}
	//		return null;
	//	}
	// 
	//	public ByteArrayOutputStream getXMLSource(VacationData data) throws Exception {
	//		JAXBContext context;
	// 
	//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	// 
	//		try {
	//			context = JAXBContext.newInstance(VacationData.class);
	//			Marshaller m = context.createMarshaller();
	//			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	//			m.marshal(data, outStream);
	//		} catch (JAXBException e) {
	// 
	//			e.printStackTrace();
	//		}
	//		return outStream;
	// 
	//	}

	@Override
	protected boolean preCommit() {
		for(Vacation vacation : vacationContainer.getItemIds()){
			LaborerConstructionsite laborer = (LaborerConstructionsite) getItem().getBean();
			laborer.addVacation(vacation);
		}
		getItem().getItemProperty("vacations").setValue(vacationContainer.getItemIds());
		return super.preCommit();
	}
	
	@Override
	protected boolean preDiscard() {
		return super.preDiscard();
	}

}
