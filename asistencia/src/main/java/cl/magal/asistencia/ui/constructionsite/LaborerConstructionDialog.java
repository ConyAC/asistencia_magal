package cl.magal.asistencia.ui.constructionsite;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.vaadin.dialogs.ConfirmDialog;

import cl.magal.asistencia.entities.Absence;
import cl.magal.asistencia.entities.Accident;
import cl.magal.asistencia.entities.Annexed;
import cl.magal.asistencia.entities.Contract;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.Loan;
import cl.magal.asistencia.entities.Tool;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.Vacation;
import cl.magal.asistencia.entities.enums.AbsenceType;
import cl.magal.asistencia.entities.enums.AccidentLevel;
import cl.magal.asistencia.entities.enums.LoanStatus;
import cl.magal.asistencia.entities.enums.MaritalStatus;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.entities.enums.ToolStatus;
import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.ui.AbstractWindowEditor;
import cl.magal.asistencia.ui.OnValueChangeFieldFactory;
import cl.magal.asistencia.ui.workerfile.vo.HistoryVO;
import cl.magal.asistencia.util.Constants;
import cl.magal.asistencia.util.SecurityHelper;
import cl.magal.asistencia.util.Utils;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.BeanValidator;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
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
		tab.addTab(drawSummary(),"Resumen");
		//tab de Información
		if(!readOnly)
			tab.addTab(new LaborerBaseInformation(getBinder(),"laborer",true),"Información");
		//tab de vacaciones
		tab.addTab(drawVacations(),"Vacaciones");
		//tab de perstamos y herramientas
		tab.addTab(drawPyH(),"Préstamos/Herramientas");
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

	protected Component drawSummary() {	
		
		GridLayout gl = new GridLayout(2,10);
		gl.setSpacing(true);
		gl.setMargin(true);
		gl.setWidth("100%");
		
		gl.addComponent( new HorizontalLayout(){
			{
				try{
				// Find the application directory
				String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
				// Image as a file resource
				FileResource resource = new FileResource(new File(basepath + "/WEB-INF/images/" + getItem().getItemProperty("laborer.photo").getValue()));

				Embedded image = new Embedded("", resource);
				image.setWidth("350");
				image.setHeight("400");
				addComponent(image);       
				setComponentAlignment(image, Alignment.TOP_LEFT);
				}catch(Exception e){ /*FIXME falla silenciosamente*/ }
				
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
				
				if( SecurityHelper.hastPermission(Permission.BLOQUEAR_OBRERO)){
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
				
				if( SecurityHelper.hastPermission(Permission.CONFIRMAR_OBREROS)){
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

		gl.addComponent(new Label("Trabajador"));gl.addComponent(new Label(getItem().getItemProperty("laborer.fullname")));
		gl.addComponent(new Label("Rut"));gl.addComponent(new Label(getItem().getItemProperty("laborer.rut")));
		gl.addComponent(new Label("Premio"));gl.addComponent(new TextField(getItem().getItemProperty("reward")){{
			setNullRepresentation("");
			addValidator(new BeanValidator(LaborerConstructionsite.class,"reward"));
			setReadOnly(readOnly);
		}});
		gl.addComponent(new Label("Fecha de Nacimiento"));gl.addComponent(new Label(getItem().getItemProperty("laborer.dateBirth")));
		String marital = MaritalStatus.CASADO.toString();
		try{
			marital = ((MaritalStatus)getItem().getItemProperty("laborer.maritalStatus").getValue()).toString();
		}catch(Exception e){
			//FIXME 
		}
		
		gl.addComponent(new Label("Estado Civil"));gl.addComponent(new Label(marital));
		gl.addComponent(new Label("Dirección"));gl.addComponent(new Label(getItem().getItemProperty("laborer.address")));
		if(getItem().getItemProperty("laborer.mobileNumber") != null)
			gl.addComponent(new Label("Celular"));gl.addComponent(new Label(getItem().getItemProperty("laborer.mobileNumber")));
		gl.addComponent(new Label("Teléfono"));gl.addComponent(new Label(getItem().getItemProperty("laborer.phone")));
		gl.addComponent(new Label("Fecha de Admisión")); gl.addComponent(new Label(getItem().getItemProperty("laborer.dateAdmission")));
				
		return gl;
	}
	
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

		final BeanItemContainer<Tool> beanItemTool = new BeanItemContainer<Tool>(Tool.class);
		List<Tool> tools = (List<Tool>)getItem().getItemProperty("tool").getValue();
		beanItemTool.addAll(tools);

		if(!readOnly){
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
		beanItemLoan.addAll(loans);

		if(!readOnly){
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
						input.put("laborerConstructions", new LaborerConstructionsite[] {(LaborerConstructionsite)getItem().getBean()});
						input.put("tools", new DateTool());
						final String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/temporary_work_contract_doc.vm", "UTF-8", input);

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

				//				Button btnEdit = new Button(null,FontAwesome.PENCIL);
				if(!readOnly){
					
					final HorizontalLayout hl = this;
					
					final Button btnChangeJob = new Button(null,FontAwesome.CHILD);
					final Button btnFinishContract = new Button(null,FontAwesome.TIMES);
					final Button btnSettlement = new Button(null,FontAwesome.FILE_TEXT);

					//				btnEdit.setDescription("Editar");
					btnChangeJob.setDescription("Cambiar Oficio");
					btnFinishContract.setDescription("Término de Contrato");
					btnSettlement.setDescription("Cálcular Finiquito");

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
										
										hl.replaceComponent(btnChangeJob,btnFinishContract);
									} catch (Exception e) {
										logger.error("Error al guardar la información del obrero",e);
										Notification.show("Es necesario agregar todos los campos obligatorios", Type.ERROR_MESSAGE);
									}
								}
							});

							UI.getCurrent().addWindow(userWindow);
						}
					});

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
													input.put("tools", new DateTool());
													
													final StringBuilder sb = new StringBuilder();
													if(((String) og.getValue()).compareTo("Voluntaria") == 0){
														sb.append( VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/voluntary_resignation_letter.vm", "UTF-8", input) );
													}else
														if(((String) og.getValue()).compareTo("Término de Contrato") == 0){
														sb.append( VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/dismissal_letter_for_completion_of_work.vm", "UTF-8", input) );
													}else
														if(((String) og.getValue()).compareTo("Ausencia Reiterada") == 0){
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
													
													activeContract.setFinished(true);
													//se asegura de marcar inactivos todos los contratos
													hl.replaceComponent(btnFinishContract,btnSettlement );
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
					
					btnSettlement.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {

							//TODO calcular finiquito
							//setea un finiquito
							activeContract.setSettlement(100000);
							//se asegura de marcar inactivos todos los contratos
							hl.replaceComponent(btnSettlement, btnChangeJob);
						}
					});

					if(activeContract == null ){
						addComponent(btnChangeJob);
					}else if(!activeContract.isFinished()){
						addComponent(btnFinishContract);
					}else if(activeContract.getSettlement() == null ){
						addComponent(btnSettlement);
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
						input.put("tools", new DateTool());
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

		annexedTable.setVisibleColumns("step","startDate","terminationDate","print");
		annexedTable.setColumnHeaders("Etapa","Fecha de inicio","Fecha de termino","Imprimir");

		gl2.addComponent(annexedTable,columna++,fila,columna--,fila++);
		return vl;
	}

	Label lbStep ,lbJob ,lbJobCode ,lbStarting, lbEnding;
	BeanItemContainer<Vacation> vacationContainer;BeanItemContainer<Absence> absenceContainer;BeanItemContainer<Accident> accidentContainer;

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

							addComponent(new Button(null,new Button.ClickListener() {

								@Override
								public void buttonClick(ClickEvent event) {
									LaborerConstructionsite laborer = (LaborerConstructionsite) getItem().getBean();
									if(laborer == null ) throw new RuntimeException("El trabajador no es válido.");
									Accident accident = new Accident();
									laborer.addAccident(accident);
									accidentContainer.addBean(accident);

								}
							}){{setIcon(FontAwesome.PLUS);}},2,0);

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

				absenceContainer = new BeanItemContainer<Absence>(Absence.class);
				absenceContainer.addAll((Collection<? extends Absence>) getItem().getItemProperty("absences").getValue());

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
									absenceContainer.addBean(absence);

								}
							}){{setIcon(FontAwesome.PLUS);}},2,0);

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

				table.setVisibleColumns("absenceType","description","fromDate","toDate","total","actions");
				table.setColumnHeaders("Tipo","Descripción","Desde","Hasta","Total","Eliminar");
				table.setColumnWidth("actions", 100);
				table.setEditable(!readOnly);				
				addComponent(table);
			}
		};
	}
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
					public Object generateCell(Table source, final Object itemId, Object columnId) {
						
						HorizontalLayout hl = new HorizontalLayout();
						hl.setSpacing(true);
						
						Button btnPrint = new Button(null,FontAwesome.PRINT);

						btnPrint.addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {

								final Map<String, Object> input = new HashMap<String, Object>();
								input.put("laborerConstructions", new LaborerConstructionsite[] {(LaborerConstructionsite)getItem().getBean()});
								input.put("tools", new DateTool());
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
			}
		};
	}

	@Override
	protected boolean preCommit() {
        LaborerConstructionsite laborer = (LaborerConstructionsite) getItem().getBean();
        //vacaciones
        laborer.getVacations().clear();
		for(Vacation vacation : vacationContainer.getItemIds()){
			laborer.addVacation(vacation);
		}
		getItem().getItemProperty("vacations").setValue(laborer.getVacations()); // no se si esto es necesario
		
		//accidentes
        laborer.getAccidents().clear();
		for(Accident accident : accidentContainer.getItemIds()){
			laborer.addAccident(accident);
		}
		getItem().getItemProperty("accidents").setValue(laborer.getAccidents()); // no se si esto es necesario
		
		//licencias
        laborer.getAbsences().clear();
		for(Absence absence : absenceContainer.getItemIds()){
			laborer.addAbsence(absence);
		}
		getItem().getItemProperty("absences").setValue(laborer.getAbsences()); // no se si esto es necesario
		
		return super.preCommit();
	}

	@Override
	protected boolean preDiscard() {
		return super.preDiscard();
	}
}
