package cl.magal.asistencia.ui.constructionsite;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.velocity.VelocityEngineUtils;

import cl.magal.asistencia.entities.Absence;
import cl.magal.asistencia.entities.Accident;
import cl.magal.asistencia.entities.Annexed;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Contract;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.Loan;
import cl.magal.asistencia.entities.Tool;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.Vacation;
import cl.magal.asistencia.entities.enums.AbsenceType;
import cl.magal.asistencia.entities.enums.AccidentLevel;
import cl.magal.asistencia.entities.enums.Job;
import cl.magal.asistencia.entities.enums.LoanStatus;
import cl.magal.asistencia.entities.enums.MaritalStatus;
import cl.magal.asistencia.entities.enums.ToolStatus;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.ui.AbstractWindowEditor;
import cl.magal.asistencia.ui.OnValueChangeFieldFactory;
import cl.magal.asistencia.ui.AbstractWindowEditor.EditorSavedEvent;
import cl.magal.asistencia.ui.workerfile.vo.HistoryVO;
import cl.magal.asistencia.util.Constants;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Not;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
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
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
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
	BeanItemContainer<HistoryVO> historyContainer = new BeanItemContainer<HistoryVO>(HistoryVO.class);
	BeanItemContainer<LaborerConstructionsite> constructionContainer = new BeanItemContainer<LaborerConstructionsite>(LaborerConstructionsite.class);
	BeanItemContainer<User> itemUser = new BeanItemContainer<User>(User.class);
	
	transient UserService serviceUser;
	transient LaborerService service;
	transient private VelocityEngine velocityEngine;
	
	public LaborerConstructionDialog(BeanItem<LaborerConstructionsite> item,LaborerService service ,VelocityEngine velocityEngine){
		super(item);
		if(service == null )
			throw new RuntimeException("Error al crear el dialgo, el servicio de trabajadores no puede ser nulo.");

		this.velocityEngine = velocityEngine;
		this.service = service;

		init();
	}

	public void init(){
		super.init();
		LaborerConstructionsite laborerConstructionsite = (LaborerConstructionsite) getItem().getBean();
		List<HistoryVO> history = service.getLaborerHistory(laborerConstructionsite.getLaborer());
		historyContainer.addAll(history);
		historyContainer.addNestedContainerProperty("constructionSite.name");
		Filter filter = new Compare.Equal("constructionSite", laborerConstructionsite.getConstructionsite());
		//filtra la obra en la que se encuentra
		historyContainer.addContainerFilter(new Not(filter));
	}

	@Override
	protected Component createBody() {

		TabSheet tab = new TabSheet();

		//tab de Resumen
		tab.addTab(drawSummary(),"Resumen");
		//tab de Información
		tab.addTab(new LaborerBaseInformation(getBinder(),"laborer", true),"Información");
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

	protected Component drawSummary() {	
		
		GridLayout gl = new GridLayout(2,10);
		gl.setSpacing(true);
		gl.setMargin(true);
		gl.setSizeFull();

		// Find the application directory
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		// Image as a file resource
		FileResource resource = new FileResource(new File(basepath + "/WEB-INF/images/" + getItem().getItemProperty("laborer.photo").getValue()));

		// Show the image in the application
		HorizontalLayout t = new HorizontalLayout();
		t.setWidth("100%");
		t.setSpacing(true);		
		gl.addComponent(t);
		
		Embedded image = new Embedded("", resource);
		image.setWidth("350");
		image.setHeight("400");
		t.addComponent(image);       
		t.setComponentAlignment(image, Alignment.TOP_LEFT);
		
		gl.addComponent( new HorizontalLayout(){
			{
				setSpacing(true);

				Button btnPrint = new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						Notification.show("Imprimiendo");
					}
				}){{setIcon(FontAwesome.PRINT); setDescription("Imprimir");}};
				addComponent(btnPrint);
				
				Button bloquear = new Button(null,FontAwesome.LOCK);					
				bloquear.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {								

						LaborerConstructionsite cs = new LaborerConstructionsite();
						BeanItem<LaborerConstructionsite> csItem = new BeanItem<LaborerConstructionsite>(cs);						
						LaborerBlockDialog lbWindow = new LaborerBlockDialog(csItem, velocityEngine);
						
						lbWindow.setCaption("Bloquear Trabajador");
						lbWindow.addListener(new AbstractWindowEditor.EditorSavedListener() {
							
							@Override
							public void editorSaved(EditorSavedEvent event) {
								try {
									LaborerConstructionsite lc = (LaborerConstructionsite) getItem().getBean();
									if(lc == null ) 
										throw new RuntimeException("El trabajador no es válido.");
									lc.setPersonBlock((User) VaadinSession.getCurrent().getAttribute(Constants.SESSION_USUARIO));
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
				addComponent(bloquear);
				
				Button acceptObrero = new Button(null,FontAwesome.CHECK);					
				acceptObrero.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						LaborerConstructionsite laborer = (LaborerConstructionsite) getItem().getBean();
						if(laborer == null ) 
							throw new RuntimeException("El trabajador no es válido.");
						laborer.setConfirmed(true);
						Notification.show("Trabajador aceptado.");
					}
				});		
				addComponent(acceptObrero);
			}
		});

		//if(getItem().getItemProperty("laborer.photo") != null)
			//gl.addComponent(new Label("Fecha de Admisión"));gl.addComponent(new Label(getItem().getItemProperty("laborer.photo")));
		gl.addComponent(new Label("Trabajador"));gl.addComponent(new Label(getItem().getItemProperty("laborer.fullname")));
		gl.addComponent(new Label("Rut"));gl.addComponent(new Label(getItem().getItemProperty("laborer.rut")));
		gl.addComponent(new Label("Fecha de Nacimiento"));gl.addComponent(new Label(getItem().getItemProperty("laborer.dateBirth")));
		gl.addComponent(new Label("Estado Civil"));gl.addComponent(new Label(((MaritalStatus)getItem().getItemProperty("laborer.maritalStatus").getValue()).toString()));
		gl.addComponent(new Label("Dirección"));gl.addComponent(new Label(getItem().getItemProperty("laborer.address")));
		if(getItem().getItemProperty("laborer.mobileNumber") != null)
			gl.addComponent(new Label("Celular"));gl.addComponent(new Label(getItem().getItemProperty("laborer.mobileNumber")));
		gl.addComponent(new Label("Teléfono"));gl.addComponent(new Label(getItem().getItemProperty("laborer.phone")));
		if(getItem().getItemProperty("laborer.dateAdmission").getValue() != null)
			gl.addComponent(new Label("Fecha de Admisión")); gl.addComponent(new Label(getItem().getItemProperty("laborer.dateAdmission")));
				
		return gl;
	}
	
	protected VerticalLayout drawPyH() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();

		/********** Herramientas **********/
		VerticalLayout vh = new VerticalLayout();
		vh.setSizeFull();

		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setSpacing(true);		
		vh.addComponent(hl);

		final BeanItemContainer<Tool> beanItemTool = new BeanItemContainer<Tool>(Tool.class);
		List<Tool> tools = (List<Tool>)getItem().getItemProperty("tool").getValue();
		if(tools != null && logger != null )
			logger.debug("tools {}",tools);
		beanItemTool.addAll(tools);

		btnAddH = new Button(null,FontAwesome.PLUS);
		hl.addComponent(btnAddH);
		
		btnAddH.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				
				LaborerConstructionsite laborer = (LaborerConstructionsite) getItem().getBean();
				if(laborer == null ) throw new RuntimeException("El trabajador no es válido.");
				Tool tool = new Tool();
				laborer.addTool(tool);
				beanItemTool.addBean(tool);
			}
		});		
		
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
				}
				else  if( propertyId.equals("status") ){
					field = new ComboBox();
					field.setPropertyDataSource(container.getContainerProperty(itemId, propertyId));
					for(ToolStatus ts : ToolStatus.values()){
						((ComboBox)field).addItem(ts);
					}
				}
				else if(  propertyId.equals("dateBuy") ){
					field = new DateField();
				}
				else {
					return null;
				}
				((AbstractField<?>)field).setImmediate(true);
				return field;
			}
		});
		
		tableTool.setVisibleColumns("name","price","dateBuy","fee","status");
		tableTool.setColumnHeaders("Herramienta","Monto","Fecha","Cuota","Estado");
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

		final BeanItemContainer<Loan> beanItemLoan = new BeanItemContainer<Loan>(Loan.class);
		List<Loan> loans = (List<Loan>)getItem().getItemProperty("loan").getValue();
		if(loans != null && logger != null )
			logger.debug("loans {}",loans);
		beanItemLoan.addAll(loans);

		btnAddP = new Button(null,FontAwesome.PLUS);
		hl2.addComponent(btnAddP);
		btnAddP.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				LaborerConstructionsite laborer = (LaborerConstructionsite) getItem().getBean();
				if(laborer == null ) throw new RuntimeException("El trabajador no es válido.");
				Loan loan = new Loan();
				laborer.addLoan(loan);
				beanItemLoan.addBean(loan);
			}
		});
		
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
				}
				else  if( propertyId.equals("status") ){
					field = new ComboBox();
					field.setPropertyDataSource(container.getContainerProperty(itemId, propertyId));
					for(LoanStatus ls : LoanStatus.values()){
						((ComboBox)field).addItem(ls);
					}
				}
				else if(  propertyId.equals("dateBuy") ){
					field = new DateField();
				}
				else {
					return null;
				}
				((AbstractField<?>)field).setImmediate(true);
				return field;
			}
		});
		
		tableLoan.setVisibleColumns("price","dateBuy", "fee", "status");
		tableLoan.setColumnHeaders("Monto","Fecha", "Cuota", "Estado");
		tableLoan.setEditable(true);		
		vp.addComponent(tableLoan);
		vp.setComponentAlignment(hl2, Alignment.TOP_RIGHT);

		vl.addComponent(vh);
		vl.addComponent(vp);

		return vl;
	}

	protected Component drawCyF() {
		
		final Contract activeContract = ((LaborerConstructionsite)getItem().getBean()).getActiveContract();
		
		GridLayout gl = new GridLayout(2,10);
		gl.setSpacing(true);
		gl.setMargin(true);
		gl.setSizeFull();

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
				
				Button btnEdit = new Button(null,FontAwesome.PENCIL);
				final Button btnChangeJob = new Button(null,FontAwesome.CHILD);
				final Button btnSettlement = new Button(null,FontAwesome.FILE_TEXT);
				
				btnSettlement.setDescription("Finiquitar");
				btnEdit.setDescription("Editar");
				btnChangeJob.setDescription("Cambiar Oficio");
				
				btnEdit.addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						
					}
				});
				addComponent(btnEdit);
				
				btnChangeJob.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						AddLaborerContractDialog userWindow = new AddLaborerContractDialog(getItem(),service,false);
						
						userWindow.addListener(new AbstractWindowEditor.EditorSavedListener() {
							
							@Override
							public void editorSaved(EditorSavedEvent event) {
								try {
									//TODO definir si guardará o no el estado del laborer en esta etapa
									//LaborerConstructionsite laborer = ((BeanItem<LaborerConstructionsite>) event.getSavedItem()).getBean();
									//service.save(laborer);
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
						for(Contract contract : ((LaborerConstructionsite)getItem().getBean()).getContracts())
							contract.setActive(false);
						
						replaceComponent(btnSettlement, btnChangeJob);
					}
				});
				
				if(activeContract.getSettlement() == null ){
					addComponent(btnSettlement);
				}else{
					addComponent(btnChangeJob);
				}
			}
		},columna--,fila++);

		gl.addComponent(new Label("Etapa"),columna++,fila);gl.addComponent(new Label(getItem().getItemProperty("activeContract.step")),columna--,fila++);
		gl.addComponent(new Label("Oficio"),columna++,fila);gl.addComponent(new Label( ((Job)getItem().getItemProperty("activeContract.job").getValue()).toString()),columna--,fila++);
		gl.addComponent(new Label("Código"),columna++,fila);gl.addComponent(new Label(getItem().getItemProperty("activeContract.jobCode")),columna--,fila++);
		gl.addComponent(new Label("Fecha Inicio"),columna++,fila);gl.addComponent(new Label(getItem().getItemProperty("activeContract.startDate")),columna--,fila++);
		gl.addComponent(new Label("Fecha Termino"),columna++,fila);gl.addComponent(new Label(getItem().getItemProperty("activeContract.terminationDate")),columna--,fila++);
		
		gl.addComponent(new Label("<hr />",ContentMode.HTML),columna++,fila,columna--,fila++);

		final BeanItemContainer<Annexed> beanContainerAnnexeds = new BeanItemContainer<Annexed>(Annexed.class); 
		gl.addComponent(new Label("<h1>Anexos</h1>",ContentMode.HTML),columna++,fila);
		gl.addComponent( new HorizontalLayout(){
			{
				Button AgregarAnexo = new Button(null,new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						Annexed annexed = new Annexed();
						annexed.setStartDate(new Date());
						annexed.setStep("Nueva etapa");
						beanContainerAnnexeds.addBean(annexed);
						
					}
				}){{setIcon(FontAwesome.PLUS);}};
				addComponent(AgregarAnexo);
			}
		},columna--,fila++);
		

		Table table = new Table(null,beanContainerAnnexeds){
			{
				setWidth("100%");
				setHeight("200px");
				
			}
		};
		table.addGeneratedColumn("print", new Table.ColumnGenerator() {
			
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
				}){ { setIcon(FontAwesome.PRINT);}};
			}
		});
		
		gl.addComponent(table,columna++,fila,columna--,fila++);
		return gl;
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

//	private Component drawInformation(){
//
//		GridLayout detalleObrero = new GridLayout(2,5);
//		detalleObrero.setMargin(true);
//		detalleObrero.setSpacing(true);
//
//		HorizontalLayout hl = new HorizontalLayout();
//		hl.setSpacing(true);
//		detalleObrero.addComponent(hl,0,0,1,0);
//		detalleObrero.setComponentAlignment(hl, Alignment.TOP_RIGHT);
//
//
//		//agrega un boton que hace el commit
//		//	    Button add = new Button(null,new Button.ClickListener() {
//		//
//		//	    	@Override
//		//	    	public void buttonClick(ClickEvent event) {
//		//	    		try {
//		//	    			fieldGroup.commit();
//		//	    			Laborer laborer = fieldGroup.getItemDataSource().getBean();
//		//	    			constructionSiteService.addLaborerToConstructionSite(laborer,cs);				
//		//	    			laborerContainer.addBean(laborer);
//		//	    			close();
//		//	    		} catch (Exception e) {
//		//	    			logger.error("Error al guardar la información del obrero");
//		//	    			Notification.show("Es necesario agregar todos los campos obligatorios", Type.ERROR_MESSAGE);
//		//	    		}
//		//
//		//	    	}
//		//	    }){{
//		//	    	setIcon(FontAwesome.SAVE);
//		//	    }};
//		//	    hl.addComponent(add);
//		//detalleObrero.addComponent(add);
//		//detalleObrero.setComponentAlignment(add, Alignment.TOP_RIGHT);
//
//		//boton para imprimir
//		Button btnPrint = new Button(null,new Button.ClickListener() {
//
//			@Override
//			public void buttonClick(ClickEvent event) {
//				Notification.show("Imprimiendo");
//
//			}
//		}){{
//			setIcon(FontAwesome.PRINT);
//		}};
//		hl.addComponent(btnPrint);
//		//detalleObrero.addComponent(btnPrint);
//		//detalleObrero.setComponentAlignment(btnPrint, Alignment.TOP_LEFT);        
//		// Loop through the properties, build fields for them and add the fields
//		// to this UI
//		for (Object propertyId : new String[]{"rut","firstname","secondname","lastname", "secondlastname", "dateBirth", "address", "mobileNumber", "phone", "dateAdmission"}) {
//			if(propertyId.equals("laborerId") || propertyId.equals("constructionSites") || propertyId.equals("contractId") || propertyId.equals("teamId"))
//				;
//			else if(propertyId.equals("afp")){
//				ComboBox afpField = new ComboBox("AFP");
//				afpField.setNullSelectionAllowed(false);
//				for(Afp a : Afp.values()){
//					afpField.addItem(a);
//				}
//				detalleObrero.addComponent(afpField);
//				bind(afpField, "afp");    			
//			}else if(propertyId.equals("job")){
//				ComboBox jobField = new ComboBox("Oficio");
//				jobField.setNullSelectionAllowed(false);
//				for(Job j : Job.values()){
//					jobField.addItem(j);
//				}
//				detalleObrero.addComponent(jobField);
//				bind(jobField, "job");    
//			}else if(propertyId.equals("maritalStatus")){
//				ComboBox msField = new ComboBox("Estado Civil");
//				msField.setNullSelectionAllowed(false);
//				for(MaritalStatus ms : MaritalStatus.values()){
//					msField.addItem(ms);
//				}
//				detalleObrero.addComponent(msField);
//				bind(msField, "maritalStatus");    
//			}else{        		
//				String t = tradProperty(propertyId);
//				Field field = buildAndBind(t, propertyId);
//				if(field instanceof TextField){
//					((TextField)field).setNullRepresentation("");
//				}
//				detalleObrero.addComponent(field);
//				detalleObrero.setComponentAlignment(field, Alignment.MIDDLE_CENTER);
//			}
//		}
//
//		detalleObrero.setWidth("100%");
//
//		return detalleObrero;
//	}

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
