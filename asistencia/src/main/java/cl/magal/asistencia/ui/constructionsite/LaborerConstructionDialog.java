package cl.magal.asistencia.ui.constructionsite;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.velocity.VelocityEngineUtils;

import cl.magal.asistencia.entities.Absence;
import cl.magal.asistencia.entities.Accident;
import cl.magal.asistencia.entities.Annexed;
import cl.magal.asistencia.entities.Contract;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.Tool;
import cl.magal.asistencia.entities.Vacation;
import cl.magal.asistencia.entities.enums.AbsenceType;
import cl.magal.asistencia.entities.enums.AccidentLevel;
import cl.magal.asistencia.entities.enums.ToolStatus;
import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.ui.AbstractWindowEditor;
import cl.magal.asistencia.ui.OnValueChangeFieldFactory;
import cl.magal.asistencia.ui.workerfile.vo.HistoryVO;
import cl.magal.asistencia.util.Utils;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
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
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
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
	BeanItemContainer<Tool> toolContainer= new BeanItemContainer<Tool>(Tool.class);
	BeanItemContainer<HistoryVO> historyContainer = new BeanItemContainer<HistoryVO>(HistoryVO.class);

	transient LaborerService service;
	transient private VelocityEngine velocityEngine;

	boolean readOnly = false;

	public LaborerConstructionDialog(BeanItem<LaborerConstructionsite> item,LaborerService service ,VelocityEngine velocityEngine,boolean readOnly){
		super(item);
		this.readOnly = readOnly;
		init(service,velocityEngine);
	}

	public LaborerConstructionDialog(BeanItem<LaborerConstructionsite> item,LaborerService service ,VelocityEngine velocityEngine){
		super(item);
		init(service,velocityEngine);
	}

	public void init(LaborerService service ,VelocityEngine velocityEngine){

		if(service == null )
			throw new RuntimeException("Error al crear el dialgo, el servicio de trabajadores no puede ser nulo.");

		this.velocityEngine = velocityEngine;
		this.service = service;

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

	@Override
	protected Component createBody() {

		TabSheet tab = new TabSheet();
		tab.setSizeFull();

		//tab de Resumen
		//tab.addTab(drawInfo(),"Resumen"); -> no cuando se está creando.
		//tab de Información
		if(!readOnly)
			tab.addTab(new LaborerBaseInformation(getBinder(),"laborer"),"Información");
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
		if(!readOnly)
			tab.addTab(drawHistorico(),"Histórico");

		return tab;
	}

	protected VerticalLayout drawPyH() {

		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setWidth("100%");

		/*Herramientas*/
		VerticalLayout vh = new VerticalLayout();
		vh.setSizeFull();

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

		if(!readOnly){

			HorizontalLayout hl = new HorizontalLayout();
			hl.setWidth("100%");
			hl.setSpacing(true);		
			vh.addComponent(hl);
			vh.setComponentAlignment(hl, Alignment.TOP_RIGHT);

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
		}

		table_h.setReadOnly(readOnly);
		vh.addComponent(table_h);

		/*Préstamo*/
		VerticalLayout vp = new VerticalLayout();
		vp.setSizeFull();

		if(!readOnly){

			HorizontalLayout hl2 = new HorizontalLayout();
			hl2.setWidth("100%");
			hl2.setSpacing(true);		
			vp.addComponent(hl2);
			vp.setComponentAlignment(hl2, Alignment.TOP_RIGHT);

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
		}

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
		table_p.setReadOnly(readOnly);
		vp.addComponent(table_p);

		vl.addComponent(vh);
		vl.addComponent(vp);

		return vl;
	}

	protected Component drawCyF() {

		final Contract activeContract = ((LaborerConstructionsite)getItem().getBean()).getActiveContract();

		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setWidth("100%");

		final GridLayout gl = new GridLayout(2,10);
		gl.setSpacing(true);
		gl.setSizeFull();
		vl.addComponent(gl);
		
		final BeanItemContainer<Annexed> beanContainerAnnexeds = new BeanItemContainer<Annexed>(Annexed.class); 

		int fila = 0, columna = 0;
		gl.addComponent(new Label("<h1>Contrato</h1>",ContentMode.HTML),columna++,fila);
		gl.addComponent( new HorizontalLayout(){
			{
				setSpacing(true);

				Button btnPrint = new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						final Map<String, Object> input = new HashMap<String, Object>();
						input.put("constructionSite", ((LaborerConstructionsite)getItem().getBean()).getConstructionsite());
						input.put("laborer", ((LaborerConstructionsite)getItem().getBean()).getLaborer());
						input.put("contract", activeContract);
						input.put("tools", new DateTool());
						final String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/temporary_work_contract_doc.vm", "UTF-8", input);

						StreamResource.StreamSource source2 = new StreamResource.StreamSource() {

							public InputStream getStream() {
								//throw new UnsupportedOperationException("Not supported yet.");
								return new ByteArrayInputStream(body.getBytes());
							}
						};
						StreamResource resource = new StreamResource(source2, "Contrato"+((LaborerConstructionsite)getItem().getBean()).getJobCode()+".html");

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
				}){{setIcon(FontAwesome.PRINT); setDescription("Imprimir");}};
				addComponent(btnPrint);

				//				Button btnEdit = new Button(null,FontAwesome.PENCIL);
				if(!readOnly){
					final Button btnChangeJob = new Button(null,FontAwesome.CHILD);
					final Button btnSettlement = new Button(null,FontAwesome.FILE_TEXT);

					btnSettlement.setDescription("Finiquitar");
					//				btnEdit.setDescription("Editar");
					btnChangeJob.setDescription("Cambiar Oficio");

					//				btnEdit.addClickListener(new Button.ClickListener() {
					//
					//					@Override
					//					public void buttonClick(ClickEvent event) {
					//						
					//					}
					//				});
					//				addComponent(btnEdit);

					btnChangeJob.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {

							AddLaborerContractDialog userWindow = new AddLaborerContractDialog(getItem(),service,false);

							userWindow.addListener(new AbstractWindowEditor.EditorSavedListener() {

								@Override
								public void editorSaved(EditorSavedEvent event) {
									try {
										//TODO definir si guardará o no el estado del laborer en esta etapa
										LaborerConstructionsite laborer = ((BeanItem<LaborerConstructionsite>) event.getSavedItem()).getBean();
										setContractGl(laborer.getActiveContract());
										beanContainerAnnexeds.removeAllItems();
										
										replaceComponent(btnChangeJob,btnSettlement);
									} catch (Exception e) {
										logger.error("Error al guardar la información del obrero",e);
										Notification.show("Es necesario agregar todos los campos obligatorios", Type.ERROR_MESSAGE);
									}
								}
							});

							UI.getCurrent().addWindow(userWindow);
						}
					});

					btnSettlement.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							//TODO calcular finiquito
							//setea un finiquito
							activeContract.setSettlement(100000);
							//se asegura de marcar inactivos todos los contratos
							replaceComponent(btnSettlement, btnChangeJob);
						}
					});

					if(activeContract.getSettlement() == null ){
						addComponent(btnSettlement);
					}else{
						addComponent(btnChangeJob);
					}
				}
			}
		},columna--,fila++);

		lbStep = new Label(){{ setImmediate(true);}};
		lbJob = new Label(){{setImmediate(true);}};
		lbJobCode = new Label(){{setImmediate(true);}};
		lbStarting = new Label(){{setImmediate(true);}};
		lbEnding = new Label(){{ setImmediate(true);}};
		gl.addComponent(new Label("Etapa"),columna++,fila);gl.addComponent(lbStep,columna--,fila++);
		gl.addComponent(new Label("Oficio"),columna++,fila);gl.addComponent(lbJob,columna--,fila++);
		gl.addComponent(new Label("Código"),columna++,fila);gl.addComponent(lbJobCode,columna--,fila++);
		gl.addComponent(new Label("Fecha Inicio"),columna++,fila);gl.addComponent(lbStarting,columna--,fila++);
		gl.addComponent(new Label("Fecha Termino"),columna++,fila);gl.addComponent(lbEnding,columna--,fila++);
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
					Button AgregarAnexo = new Button(null,new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {

							AddAnnexedContractDialog userWindow = new AddAnnexedContractDialog(new BeanItem<Contract>(((LaborerConstructionsite)getItem().getBean()).getActiveContract()));
							userWindow.addListener(new AbstractWindowEditor.EditorSavedListener() {

								@Override
								public void editorSaved(EditorSavedEvent event) {
									try {
										//TODO definir si guardará o no el estado del laborer en esta etapa
										BeanItem<Contract>	newBeanItem = (BeanItem<Contract>) event.getSavedItem();

										beanContainerAnnexeds.removeAllItems();
										beanContainerAnnexeds.addAll((Collection<? extends Annexed>) newBeanItem.getItemProperty("annexeds").getValue());

									} catch (Exception e) {
										logger.error("Error al guardar la información del obrero",e);
										Notification.show("Es necesario agregar todos los campos obligatorios", Type.ERROR_MESSAGE);
									}
								}
							});

							UI.getCurrent().addWindow(userWindow);
						}
					}){{setIcon(FontAwesome.PLUS);}};
					addComponent(AgregarAnexo);
				}
			},columna--,fila++);
		else{ columna--;fila++;}


		Table annexedTable = new Table(null,beanContainerAnnexeds){
			{
				setWidth("100%");
			}
		};
		annexedTable.addGeneratedColumn("print", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				return new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {

						final Map<String, Object> input = new HashMap<String, Object>();
						input.put("constructionSite", ((LaborerConstructionsite)getItem().getBean()).getConstructionsite());
						input.put("laborer", ((LaborerConstructionsite)getItem().getBean()).getLaborer());
						input.put("contract", ((LaborerConstructionsite)getItem().getBean()).getActiveContract());
						input.put("annexed", (beanContainerAnnexeds.getItem(itemId).getBean()));
						input.put("tools", new DateTool());
						final String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/annex_contract_doc.vm", "UTF-8", input);

						StreamResource.StreamSource source2 = new StreamResource.StreamSource() {

							public InputStream getStream() {
								return new ByteArrayInputStream(body.getBytes());
							}
						};
						StreamResource resource = new StreamResource(source2, "Contrato"+((LaborerConstructionsite)getItem().getBean()).getJobCode()+".html");

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
				}){ { setIcon(FontAwesome.PRINT);}};
			}
		});

		annexedTable.setVisibleColumns("step","startDate","terminationDate","print");
		annexedTable.setColumnHeaders("Etapa","Fecha de inicio","Fecha de termino","Imprimir");

		gl2.addComponent(annexedTable,columna++,fila,columna--,fila++);
		return vl;
	}

	Label lbStep ,lbJob ,lbJobCode ,lbStarting, lbEnding;

	private void setContractGl(final Contract activeContract) {

		lbStep.setValue(activeContract.getStep());
		lbJob.setValue(activeContract.getJob().toString());
		lbJobCode.setValue(activeContract.getJobCode()+"");
		lbStarting.setValue(Utils.date2String( activeContract.getStartDate()));
		lbEnding.setValue(Utils.date2String( activeContract.getTerminationDate()));

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
				if(!readOnly)
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
				table.setEditable(!readOnly);		
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
				table.setReadOnly(readOnly);
				if(!readOnly)
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
				table.setEditable(!readOnly);				
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

						if(!readOnly)
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
				vacationTable.setEditable(!readOnly);				
				addComponent(vacationTable);
			}
		};
	}

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
