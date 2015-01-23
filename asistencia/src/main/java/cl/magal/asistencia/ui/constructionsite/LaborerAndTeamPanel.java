package cl.magal.asistencia.ui.constructionsite;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolationException;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.Team;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.entities.enums.Status;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.services.TeamService;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.ui.AbstractWindowEditor;
import cl.magal.asistencia.ui.AbstractWindowEditor.EditorSavedEvent;
import cl.magal.asistencia.util.SecurityHelper;
import cl.magal.asistencia.util.Utils;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Component
@Scope("prototype")
public class LaborerAndTeamPanel extends Panel implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3552532103677168457L;

	transient Logger logger = LoggerFactory.getLogger(LaborerAndTeamPanel.class);

	protected Button editConstructionSite,btnPrint,btnAdd;
	protected Label block, confirmed;

	/** CONTAINERS **/
	BeanItemContainer<User> userContainer = new BeanItemContainer<User>(User.class);
	BeanItemContainer<Team> teamContainer = new BeanItemContainer<Team>(Team.class);
	BeanItemContainer<LaborerConstructionsite> laborerConstructionContainer = new BeanItemContainer<LaborerConstructionsite>(LaborerConstructionsite.class);

	BeanItem<ConstructionSite> item;

	/** FIELD GROUP **/
	BeanFieldGroup<ConstructionSite> bfg = new BeanFieldGroup<ConstructionSite>(ConstructionSite.class);
	/** LAYOUTS **/
	VerticalLayout detalleLayout;
	HorizontalLayout detailLayout;
	private TwinColSelect tcsLaborer;

	/** SERVICES **/
	@Autowired
	transient UserService userService;
	@Autowired
	transient ConstructionSiteService constructionSiteService;
	@Autowired
	transient TeamService teamService;
	@Autowired
	transient LaborerService laborerService;
	@Autowired
	transient private VelocityEngine velocityEngine;

	Button asistenciaBtn;

	boolean hasConstructionDetails;

	public void setHasAttendanceButton(boolean hasAttendanceButton) {
		asistenciaBtn.setVisible(hasAttendanceButton); 
	}


	public boolean isHasConstructionDetails() {
		return hasConstructionDetails;
	}


	public void setHasConstructionDetails(boolean hasConstructionDetails) {
		this.hasConstructionDetails = hasConstructionDetails;
	}


	public LaborerAndTeamPanel() {
		teamContainer.addNestedContainerProperty("leader.firstname");
		laborerConstructionContainer.addNestedContainerBean("laborer");
		laborerConstructionContainer.addNestedContainerBean("activeContract");
		//crea el tab con trabajadores y cuadrillas
		TabSheet tab = new TabSheet();
		tab.setSizeFull();

		//tab de trabajadores
		tab.addTab(drawLaborer(),"Trabajadores");

		//tab de cuadrillas
		tab.addTab(drawTeam(),"Cuadrillas");
		//rellena el panel de la información de obra
		setContent(tab);
	}


	@Override
	public void enter(ViewChangeEvent event) {
		Page<User> users = userService.findAllActiveUser(new PageRequest(0, 20));
		userContainer.removeAllItems();
		userContainer.addAll(users.getContent());
	}

	@PostConstruct
	public void init(){

	}

	protected VerticalLayout drawContructionDetail() {
		detalleLayout = new VerticalLayout(); 
		detalleLayout.setSizeFull();
		detalleLayout.setSpacing(true);

		//creando la parte de arriba
		detailLayout = drawTopDetails();
		detalleLayout.addComponent(detailLayout);
		detalleLayout.setExpandRatio(detailLayout, 0.3F);

		//crea el tab con trabajadores y cuadrillas
		TabSheet tab = new TabSheet();
		tab.setSizeFull();

		detalleLayout.addComponent(tab);
		detalleLayout.setExpandRatio(tab, 0.7F);
		//tab de trabajadores
		tab.addTab(drawLaborer(),"Trabajadores");

		//tab de cuadrillas
		tab.addTab(drawCuadrillas(),"Cuadrillas");

		return detalleLayout;
	}

	protected HorizontalLayout drawTopDetails() {

		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeFull();
		hl.setSpacing(true);

		hl.addComponent(new HorizontalLayout(){{

			setSpacing(true);
			setSizeFull();

			FormLayout vlInfo =new FormLayout();
			vlInfo.setSizeFull();
			vlInfo.setSpacing(true);

			final TextField nameField = new TextField("Nombre");
			nameField.setNullRepresentation("");
			nameField.setRequired(true);
			nameField.setWidth("100%");
			bfg.bind(nameField, "name");

			vlInfo.addComponent(nameField);

			if(SecurityHelper.hastPermission(Permission.EDITAR_OBRA) ){
				//agrega un boton que hace el commit
				editConstructionSite = new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						try {
							bfg.commit();
							constructionSiteService.save(bfg.getItemDataSource().getBean());
						} catch (CommitException e) {
							logger.error("Error al guardar la información la obra",e);
							Notification.show("Error al guardar la información del usuario", Type.ERROR_MESSAGE);
						}

					}
				}){{
					setIcon(FontAwesome.SAVE);
				}};
				addComponent(editConstructionSite);
				setComponentAlignment(editConstructionSite, Alignment.TOP_RIGHT);
			}

			TextField addressField = new TextField("Dirección");
			addressField.setWidth("100%");
			addressField.setNullRepresentation("");
			bfg.bind(addressField, "address");
			vlInfo.addComponent(addressField);

			ComboBox statusField = new ComboBox("Estado");
			statusField.setWidth("100%");
			//no permite nulos
			statusField.setNullSelectionAllowed(false);
			for(Status s : Status.values()){
				statusField.addItem(s);
			}

			bfg.bind(statusField, "status");
			vlInfo.addComponent(statusField);

			ComboBox personInChargeField = new ComboBox("Responsable",userContainer);
			personInChargeField.setItemCaptionMode(ItemCaptionMode.PROPERTY);
			personInChargeField.setItemCaptionPropertyId("fullname");
			personInChargeField.setWidth("100%");			
			bfg.bind(personInChargeField, "personInCharge");

			vlInfo.addComponent(personInChargeField);

			addComponent(vlInfo);
			setExpandRatio(vlInfo, 1.0F);
		}});

		//		hl.addComponent(new Image());

		VerticalLayout vlIBotones =new VerticalLayout();
		vlIBotones.setSpacing(true);
		hl.addComponent(vlIBotones);
		hl.setComponentAlignment(vlIBotones, Alignment.MIDDLE_RIGHT );

		final Button asistencia = new Button("Asistencia",FontAwesome.CHECK);
		asistencia.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				//				switchPanels();
				asistencia.setEnabled(true);
			}
		});
		asistencia.setDisableOnClick(true);

		asistencia.setSizeFull();
		vlIBotones.addComponent(asistencia);
		//		Button vacaciones = new Button("Carga Masiva Vacaciones",FontAwesome.UPLOAD);
		//		vacaciones.setSizeFull();
		//		vlIBotones.addComponent(vacaciones);

		Button configuraciones = new Button("Configuraciones Obra",FontAwesome.GEARS);
		configuraciones.setSizeFull();
		vlIBotones.addComponent(configuraciones);
		vlIBotones.setWidth("300px");

		return hl;
	}

	protected VerticalLayout drawCuadrillas() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		vl.setSpacing(true);
		vl.setMargin(true);

		Button btnAdd = new Button(null,FontAwesome.PLUS);
		vl.addComponent(btnAdd);
		vl.setComponentAlignment(btnAdd, Alignment.TOP_RIGHT);

		btnAdd.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				ConstructionSite cs = item.getBean();
				if(cs == null){
					Notification.show("Debe seleccionar una obra",Type.ERROR_MESSAGE);
					return;
				}

				Team team = new Team();
				team.setName("Cuadrilla 1");
				team.setDate(new Date());
				team.setStatus(Status.ACTIVE);
				team.setLeader(laborerConstructionContainer.firstItemId().getLaborer());
				constructionSiteService.addTeamToConstructionSite(team,cs);

				teamContainer.addBean(team);

			}
		});

		FilterTable table =  new FilterTable(){

			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				Object v = property.getValue();
				if (v instanceof Date) {
					Date dateValue = (Date) v;
					SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
					return sdf.format(dateValue);
				}
				return super.formatPropertyValue(rowId, colId, property);
			}
		};

		table.setContainerDataSource(teamContainer);
		table.setSizeFull();
		table.setFilterBarVisible(true);
		table.setVisibleColumns("name","leader.firstname","date","status");
		table.setColumnHeaders("Nombre","Responsable","Fecha","Estado");

		table.setSelectable(true);

		vl.addComponent(table);
		vl.setExpandRatio(table,1.0F);

		return vl;
	}

	protected VerticalLayout test1() {
		return null;
	}

	protected VerticalLayout test2() {
		return null;
	}

	/**
	 * Lista de trabajadores seleccionados
	 */
	final Set<Object> selectedItemIds = new HashSet<Object>();

	protected VerticalLayout drawLaborer() {

		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();

		//boton para agregar trabajadores e imprimir
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setSpacing(true);

		asistenciaBtn = new Button("Asistencia",FontAwesome.CHECK);
		asistenciaBtn.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				//				switchPanels();
				asistenciaBtn.setEnabled(true);
			}
		});
		asistenciaBtn.setDisableOnClick(true);

		asistenciaBtn.setWidth("200px");
		hl.addComponent(asistenciaBtn);
		hl.setComponentAlignment(asistenciaBtn, Alignment.TOP_LEFT);

		vl.addComponent(hl);

		HorizontalLayout hl2 = new HorizontalLayout();
		hl2.setSpacing(true);

		block = new Label("Bloqueado");
		block.addStyleName("laborer-block");
		hl2.addComponent(block);

		confirmed = new Label("No Confirmado");
		confirmed.addStyleName("laborer-confirmed");
		hl2.addComponent(confirmed);

		btnPrint = new Button(null,FontAwesome.PRINT);
		hl2.addComponent(btnPrint);

		hl.addComponent(hl2);
		hl.setComponentAlignment(hl2, Alignment.TOP_RIGHT);

		btnPrint.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if(selectedItemIds.isEmpty()){
					Notification.show("Para imprimir masivamente, primero debe seleccionar uno más trabajadores");
					return;
				}

				final ObjectProperty contracts = new ObjectProperty(false, Boolean.class);
				final ObjectProperty vacations = new ObjectProperty(false, Boolean.class);
				final ObjectProperty anexxeds = new ObjectProperty(false, Boolean.class);

				final Window w = new Window("Impresión masiva");
				w.center();
				w.setModal(true);

				w.setContent(new HorizontalLayout(){
					{
						setSpacing(true);
						setMargin(true);
						addComponent(new CheckBox("Contrato"){{setPropertyDataSource(contracts);}});
						addComponent(new CheckBox("Anexos"){{setPropertyDataSource(anexxeds);}});
						addComponent(new CheckBox("Últimas Vacaciones"){{setPropertyDataSource(vacations);}});

						addComponent(new Button(null,new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {

								final Map<String, Object> input = new HashMap<String, Object>();
								input.put("laborerConstructions", selectedItemIds);
								input.put("tools", new DateTool());

								final StringBuilder sb = new StringBuilder();
								if((Boolean) contracts.getValue()){
									sb.append( VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/temporary_work_contract_doc.vm", "UTF-8", input) );
								}
								if((Boolean) anexxeds.getValue()){
									sb.append( VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/annex_contract_doc.vm", "UTF-8", input) );
								}
								if((Boolean) vacations.getValue()){
									sb.append( VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/vacation_doc.vm", "UTF-8", input) );
								}

								StreamResource.StreamSource source2 = new StreamResource.StreamSource() {

									public InputStream getStream() {
										//throw new UnsupportedOperationException("Not supported yet.");
										return new ByteArrayInputStream(sb.toString().getBytes());
									}
								};
								StreamResource resource = new StreamResource(source2, "Documentos Masivos.html");

								BrowserFrame e = new BrowserFrame();
								e.setSizeFull();

								// Here we create a new StreamResource which downloads our StreamSource,
								// which is our pdf.
								// Set the right mime type
								//resource.setMIMEType("application/pdf");
								resource.setMIMEType("text/html");

								e.setSource(resource);
								w.setContent(e);
								w.center();
								w.setWidth("60%");
								w.setHeight("60%");
							}
						}){ {setIcon(FontAwesome.PRINT);} } );
					}
				});

				UI.getCurrent().addWindow(w);


				final Map<String, Object> input = new HashMap<String, Object>();
				input.put("laborerConstructions", selectedItemIds);
				input.put("tools", new DateTool());
				final String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/annex_contract_doc.vm", "UTF-8", input);

				StreamResource.StreamSource source2 = new StreamResource.StreamSource() {

					public InputStream getStream() {
						//throw new UnsupportedOperationException("Not supported yet.");
						return new ByteArrayInputStream(body.getBytes());
					}
				};
				StreamResource resource = new StreamResource(source2, "Contratos Masivos.html");

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
				//				UI.getCurrent().addWindow(window);


				//				for(Object lc : selectedItemIds){
				//					Notification.show("Imprimiendo "+((LaborerConstructionsite) lc).getLaborer().getFullname());
				//				}

			}
		});

		//agrega solo si tiene los permisos //siempre se debe crear pues solo se deshabilita si no se tiene permiso
		btnAdd = new Button(null,FontAwesome.PLUS);
		hl2.addComponent(btnAdd);

		ShortcutListener enter = new ShortcutListener("Entrar",
				KeyCode.ENTER, new int[]{ModifierKey.CTRL }) {
			@Override
			public void handleAction(Object sender, Object target) {
				btnAdd.click();
			}

		};

		btnAdd.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				final ConstructionSite cs = item.getBean();
				if(cs == null){
					Notification.show("Debe seleccionar una obra",Type.ERROR_MESSAGE);
					return;
				}

				LaborerConstructionsite laborer = new LaborerConstructionsite();
				laborer.setConstructionsite(item.getBean());

				BeanItem<LaborerConstructionsite> laborerItem = new BeanItem<LaborerConstructionsite>(laborer);
				AddLaborerContractDialog userWindow = new AddLaborerContractDialog(laborerItem,laborerService,true);

				userWindow.addListener(new AbstractWindowEditor.EditorSavedListener() {

					@Override
					public void editorSaved(EditorSavedEvent event) {
						try {
							LaborerConstructionsite laborer = ((BeanItem<LaborerConstructionsite>) event.getSavedItem()).getBean();
							laborerService.save(laborer);				
							laborerConstructionContainer.addBean(laborer);
						} catch (Exception e) {
							logger.error("Error al guardar la información del obrero",e);
							Notification.show("Es necesario agregar todos los campos obligatorios", Type.ERROR_MESSAGE);
						}

					}
				});

				UI.getCurrent().addWindow(userWindow);
			}
		});

		btnAdd.addShortcutListener(enter);

		final FilterTable table =  new FilterTable();

		table.addGeneratedColumn("actions", new CustomTable.ColumnGenerator() {

			@Override
			public Object generateCell(CustomTable source, final Object itemId,Object columnId) {
				final BeanItem<LaborerConstructionsite> laborerConstruction = (BeanItem<LaborerConstructionsite>) laborerConstructionContainer.getItem(itemId);
				return new Button(null,new Button.ClickListener(){
					@Override
					public void buttonClick(ClickEvent event) {
						laborerService.remove(laborerConstruction.getBean());
						laborerConstructionContainer.removeItem(itemId);
					}
				}){

					{ setIcon(FontAwesome.TRASH_O);}
				};
			}
		});


		table.addGeneratedColumn("selected", new CustomTable.ColumnGenerator() {

			@Override
			public Object generateCell(CustomTable source, final Object itemId,Object columnId) {
				boolean selected = selectedItemIds.contains(itemId);
				/* When the chekboc value changes, add/remove the itemId from the selectedItemIds set */
				final CheckBox cb = new CheckBox("", selected);
				cb.addValueChangeListener(new Property.ValueChangeListener() {
					@Override
					public void valueChange(Property.ValueChangeEvent event) {
						if(selectedItemIds.contains(itemId)){
							selectedItemIds.remove(itemId);
						} else {
							selectedItemIds.add(itemId);
						}
					}
				});
				return cb;
			}
		});

		table.setContainerDataSource(laborerConstructionContainer);
		table.setSizeFull();
		table.setFilterBarVisible(true);

		//		table.addGeneratedColumn("confirmed", new CustomTable.ColumnGenerator() {
		//
		//			@Override
		//			public Object generateCell(CustomTable source, Object itemId,Object columnId) {
		//				if((Boolean)source.getContainerProperty(itemId, columnId).getValue()){
		//					return "Si";
		//				}else
		//					return "No";
		//			}
		//		});

		//TODO estado
		//		table.setVisibleColumns("laborer.job","laborer.firstname","laborer.laborerId"); //FIXME laborerId
		table.setVisibleColumns("selected","activeContract.jobCode","laborer.fullname","activeContract.step","actions"); //FIXME laborerId
		table.setColumnHeaders("","Cod","Nombre","Etapa","Acciones");
		table.setColumnWidth("selected", 40);

		table.setSelectable(true);

		table.addItemClickListener(new ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {

				final BeanItem<LaborerConstructionsite> beanItem = (BeanItem<LaborerConstructionsite>) event.getItem();
				logger.debug("laborer constructionsite click item {} rut {} ",beanItem.getBean(),beanItem.getBean().getLaborer().getRut());
				LaborerConstructionDialog userWindow = new LaborerConstructionDialog(beanItem,laborerService,velocityEngine);

				userWindow.addListener(new AbstractWindowEditor.EditorSavedListener() {

					@Override
					public void editorSaved(EditorSavedEvent event) {
						final ConstructionSite cs = item.getBean();
						if(cs == null){
							Notification.show("Debe seleccionar una obra",Type.ERROR_MESSAGE);
							//							return false;
							return ;
						}
						try {
							//			    			LaborerConstructionsite laborer = ((BeanItem<LaborerConstructionsite>) event.getSavedItem()).getBean();
							LaborerConstructionsite laborer = beanItem.getBean();
							logger.debug("laborer constructionsite {}, rut {} postcommit ",laborer,laborer.getLaborer().getRut());
							laborerService.save(laborer);	
							table.refreshRowCache();
							//			    			return true;
							return;
						} catch (TransactionSystemException e) {

							Throwable e1 = e.getCause() != null ? e.getCause().getCause() : e;
							if( e1 == null ){
								logger.error("Error al intentar guardar el trabajador",e);
								Notification.show("Error al intentar guardar el trabajador", Type.ERROR_MESSAGE);
								return;
							}
							if(e1 instanceof ConstraintViolationException ){
								logger.error("Error de constraint "+Utils.printConstraintMessages(((ConstraintViolationException) e1).getConstraintViolations()),e);
								Notification.show("Error al validar los datos:\n"+Utils.printConstraintMessages(((ConstraintViolationException) e1).getConstraintViolations()), Type.ERROR_MESSAGE);
							}else{
								logger.error("Error al intentar guardar el trabajador",e);
								logger.error("Error al intentar guardar el trabajador",e1);
								Notification.show("Error al intentar guardar el trabajador", Type.ERROR_MESSAGE);
							}

							//			    			return false;
							return;
						}catch (Exception e){

							if(e instanceof ConstraintViolationException )
								logger.error("Error de constraint "+Utils.printConstraintMessages(((ConstraintViolationException) e).getConstraintViolations()),e);
							else
								logger.error("Error al guardar la información del obrero",e);
							Notification.show("Ocurrió un error al intentar guardar el trabajador", Type.ERROR_MESSAGE);
							//			    			return false;
							return;
						}
					}
				});

				UI.getCurrent().addWindow(userWindow);

			}
		});

		table.setCellStyleGenerator(new FilterTable.CellStyleGenerator() {
			@Override
			public String getStyle(CustomTable source, Object itemId,
					Object propertyId) {

				if (!(Boolean) table.getItem(itemId).getItemProperty("confirmed").getValue()) {
					return "pending-laborer";
				}else if((Boolean) table.getItem(itemId).getItemProperty("block").getValue()){
					return "block-laborer";
				}else
					return "";				
			}
		});

		table.refreshRowCache();
		vl.addComponent(table);
		vl.setExpandRatio(table,1.0F);

		return vl;
	}

	protected VerticalLayout drawTeam() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();

		Button btnAdd = new Button(null,FontAwesome.PLUS);
		vl.addComponent(btnAdd);
		vl.setComponentAlignment(btnAdd, Alignment.TOP_RIGHT);
		btnAdd.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				final ConstructionSite cs = item.getBean();
				if(cs == null){
					Notification.show("Debe seleccionar una obra",Type.ERROR_MESSAGE);
					return;
				}

				Team team = new Team();
				team.setConstructionSite(item.getBean());

				BeanItem<Team> teamItem = new BeanItem<Team>(team);
				AddTeamDialog teamWindow = new AddTeamDialog(teamItem);

				teamWindow.addListener(new AbstractWindowEditor.EditorSavedListener() {

					@Override
					public void editorSaved(EditorSavedEvent event) {
						try {
							Team team = ((BeanItem<Team>) event.getSavedItem()).getBean();
							constructionSiteService.save(team);				
							teamContainer.addBean(team);
						} catch (Exception e) {
							logger.error("Error al guardar la información del obrero",e);
							Notification.show("Es necesario agregar todos los campos obligatorios", Type.ERROR_MESSAGE);
						}

					}
				});

				UI.getCurrent().addWindow(teamWindow);

			}
		});		

		final FilterTable table =  new FilterTable();
		table.setContainerDataSource(teamContainer);
		table.setSizeFull();
		table.setFilterBarVisible(true);
		
		table.addGeneratedColumn("actions", new CustomTable.ColumnGenerator() {

			@Override
			public Object generateCell(CustomTable source, final Object itemId,Object columnId) {
				BeanItem<Team> item = teamContainer.getItem(itemId);
				final Team team = item.getBean();
				return new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar la cuadrilla seleccionada?",
								"Eliminar", "Cancelar", new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									constructionSiteService.delete(team);
									teamContainer.removeItem(itemId);
								}
							}
						});		
					}
				}){ { setIcon(FontAwesome.TRASH_O); } };
			}
		});

		table.setVisibleColumns("name","leader.firstname","date","status","actions");
		table.setColumnHeaders("Nombre","Responsable","Fecha","Estado", "Acciones");
		table.setSelectable(true);
		table.addItemClickListener(new ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {
				final BeanItem<Team> beanItem = (BeanItem<Team>) event.getItem();
				AddTeamDialog userWindow = new AddTeamDialog(beanItem);

				userWindow.addListener(new AbstractWindowEditor.EditorSavedListener() {

					@Override
					public void editorSaved(EditorSavedEvent event) {
						final ConstructionSite cs = item.getBean();
						if(cs == null){
							Notification.show("Debe seleccionar una obra",Type.ERROR_MESSAGE);
							return ;
						}
						try {
							Team team = ((BeanItem<Team>) event.getSavedItem()).getBean();
							constructionSiteService.save(team);				
							teamContainer.addBean(team);
							table.refreshRowCache();
						} catch (Exception e) {
							logger.error("Error al guardar la información del obrero",e);
							Notification.show("Es necesario agregar todos los campos obligatorios", Type.ERROR_MESSAGE);
						}
					}
				});

				UI.getCurrent().addWindow(userWindow);	
			}
		});

		vl.addComponent(table);
		vl.setExpandRatio(table,1.0F);

		return vl;
	}

	public void setConstruction(BeanItem<ConstructionSite> item){
		if(item == null ){

			setEnabledDetail(false,new BeanItem<ConstructionSite>(new ConstructionSite()));
			return;
		}

		this.item = item;

		if( SecurityHelper.hastPermission(Permission.CREAR_OBRA) || SecurityHelper.hasConstructionSite(item.getBean())){
			if( editConstructionSite != null )
				editConstructionSite.setEnabled(true);
			btnPrint.setEnabled(true);
			btnAdd.setEnabled(true);
		}else{
			if( editConstructionSite != null )
				editConstructionSite.setEnabled(false);
			btnPrint.setEnabled(false);
			btnAdd.setEnabled(false);
		}

		setEnabledDetail(true,item);
		List<LaborerConstructionsite> laborers = constructionSiteService.getLaborerByConstruction(item.getBean());
		laborerConstructionContainer.removeAllItems();
		laborerConstructionContainer.addAll(laborers);

		List<Team> teams = constructionSiteService.getTeamsByConstruction(item.getBean());
		teamContainer.removeAllItems();
		teamContainer.addAll(teams);
	}

	protected void setEnabledDetail(boolean enable,BeanItem<ConstructionSite> item) {
		bfg.setEnabled(enable);
		bfg.setItemDataSource(item);
		if(detailLayout != null)
			detailLayout.setEnabled(enable);

	}
}
