package cl.magal.asistencia.ui.config;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolationException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.vaadin.dialogs.ConfirmDialog;

import ru.xpoft.vaadin.VaadinView;
import cl.magal.asistencia.entities.AfpAndInsuranceConfigurations;
import cl.magal.asistencia.entities.AfpItem;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.DateConfigurations;
import cl.magal.asistencia.entities.FamilyAllowanceConfigurations;
import cl.magal.asistencia.entities.Mobilization2;
import cl.magal.asistencia.entities.TaxationConfigurations;
import cl.magal.asistencia.entities.WageConfigurations;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.services.ConfigurationService;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.ui.ListenerFieldFactory;
import cl.magal.asistencia.ui.MagalUI;
import cl.magal.asistencia.ui.OnValueChangeFieldFactory;
import cl.magal.asistencia.ui.OnValueChangeFieldFactory.OnValueChangeListener;
import cl.magal.asistencia.util.SecurityHelper;
import cl.magal.asistencia.util.Utils;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@VaadinView(value=ConfigView.NAME)
@Scope("prototype")
@Component
public class ConfigView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8538300514577423280L;
	
	transient Logger logger = LoggerFactory.getLogger(ConfigView.class);

	public static final String NAME = "configuraciones";
	BeanItemContainer<ConstructionSite> constructionContainer = new BeanItemContainer<ConstructionSite>(ConstructionSite.class);

	@Autowired
	private transient ConstructionSiteService service;
	@Autowired
	private transient ConfigurationService confService;
	//atributos globales
	WageConfigurations wageconfiguration;
	AfpAndInsuranceConfigurations afpAndInsurance;
	
	public ConfigView(){}
	
	@PostConstruct
	public void init(){


		//define tab para poner cada tipo de configuración
		TabSheet ts = new TabSheet();
		ts.setSizeFull();
		addComponent(ts);

		ts.addTab(drawMensuales(),"Fechas Mensuales",FontAwesome.REBEL);

		ts.addTab(drawGlobales(),"Bonos y Sueldos",FontAwesome.GLOBE);

		ts.addTab(drawAFP(),"Tabla de Afp y Seguros",FontAwesome.CROSSHAIRS);
		
		ts.addTab(drawImpuestos(),"Tabla de Impuesto",FontAwesome.MONEY);
		
		ts.addTab(drawAsignacion(),"Tabla de Asignación Familiar",FontAwesome.GROUP);
		//agrega cada tab
		ts.addTab(drawFeriados(), "Feriados", FontAwesome.CALENDAR);
		

	}

	private com.vaadin.ui.Component drawAsignacion() {
		return new VerticalLayout(){
			{
				setMargin(true);
				setSpacing(true);

				List<FamilyAllowanceConfigurations> taxes = confService.findFamylyAllowanceConfigurations();
				final BeanItemContainer<FamilyAllowanceConfigurations> container = new BeanItemContainer<FamilyAllowanceConfigurations>(FamilyAllowanceConfigurations.class,taxes);

				Table table = new Table("Asignación Familiar"){
					{
						setWidth("100%");
						setHeightUndefined();
						setPageLength(8);

					}
				};
				table.setEditable(true);

				table.addGeneratedColumn("delete", new Table.ColumnGenerator() {

					@Override
					public Object generateCell(Table source, final Object itemId, Object columnId) {

						return new Button(null,new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {

								ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar el item de asignación familiar?",
										"Continuar", "Cancelar", new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {

											try{
												FamilyAllowanceConfigurations family = ((BeanItem<FamilyAllowanceConfigurations>)container.getItem(itemId)).getBean();
												confService.delete(family);
												container.removeItem(itemId);
											}catch(Exception e){
												logger.error("Error al eliminar la asignación",e);
												String mensaje = "Error al eliminar la asignación";
												Notification.show(mensaje,Type.ERROR_MESSAGE);
											}
										}
									}
								});
							}
						}){
							{
								setIcon(FontAwesome.TRASH_O);
							}
						};
					}
				});
				
				OnValueChangeListener listener = new OnValueChangeListener(){

					@Override
					public void onValueChange(Object itemId) {
						FamilyAllowanceConfigurations family = ((BeanItem<FamilyAllowanceConfigurations>)container.getItem(itemId)).getBean();
						confService.save(family);
					}
					
				};
				OnValueChangeFieldFactory factory = new OnValueChangeFieldFactory(2);
				factory.addListener(listener);
				table.setTableFieldFactory(factory);
				table.setContainerDataSource(container);
				table.setVisibleColumns("from","to","amount","delete");
				table.setColumnHeaders("Desde","Hasta","Monto","Quitar");
				addComponent(table);

				if(!SecurityHelper.hasPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
					setEnabled(false);
				}else{
					setEnabled(true);
				}
			}
		};
	}

	private com.vaadin.ui.Component drawImpuestos() {
		
		return new VerticalLayout(){
			{
				setMargin(true);
				setSpacing(true);
				
				List<TaxationConfigurations> taxes = confService.findTaxationConfigurations();
				final BeanItemContainer<TaxationConfigurations> container = new BeanItemContainer<TaxationConfigurations>(TaxationConfigurations.class,taxes);

				Table table = new Table("Impuesto 2° Categoria"){
					{
						setWidth("100%");
						setHeightUndefined();
						setPageLength(8);

					}
				};
				table.setContainerDataSource(container);
				table.setVisibleColumns("from","to","factor","reduction","exempt");
				table.setColumnHeaders("Desde","Hasta","Factor","Rebaja","Exento");
				table.setEditable(true);
				
				OnValueChangeListener listener = new OnValueChangeListener(){

					@Override
					public void onValueChange(Object itemId) {
						TaxationConfigurations tax = ((BeanItem<TaxationConfigurations>)container.getItem(itemId)).getBean();
						confService.save(tax);
					}
					
				};
				OnValueChangeFieldFactory factory = new OnValueChangeFieldFactory(1);
				factory.addListener(listener);
				
				table.setTableFieldFactory(factory);
				addComponent(table);

				if(!SecurityHelper.hasPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
					setEnabled(false);
				}else{
					setEnabled(true);
				}
			}
		};
	}

	private com.vaadin.ui.Component drawAFP() {
		
		final FieldGroup fg = new FieldGroup();
		afpAndInsurance = confService.findAfpAndInsuranceConfiguration();
		if( afpAndInsurance  == null )
			afpAndInsurance  = new AfpAndInsuranceConfigurations();
		fg.setItemDataSource(new BeanItem<AfpAndInsuranceConfigurations>(afpAndInsurance));
		
		final Property.ValueChangeListener listener = new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					fg.commit();
					AfpAndInsuranceConfigurations bean = ((BeanItem<AfpAndInsuranceConfigurations>)fg.getItemDataSource()).getBean();
					confService.save(bean);
				} catch (Exception e) {
					logger.error("Error al guardar las propiedades de afp",e);
					Notification.show("Error al guardar");
				}
			}
		};
		
		return new VerticalLayout(){
			{
				setMargin(true);
				setSpacing(true);
				
				addComponent(new FormLayout(){
					{
						Field sis = fg.buildAndBind("SIS", "sis");
						((TextField)sis).setNullRepresentation("");
						sis.addValueChangeListener(listener);
						
						addComponent(sis);
					}
				});
				
				final BeanItemContainer<AfpItem> container = new BeanItemContainer<AfpItem>(AfpItem.class,afpAndInsurance.getAfpTable());
				container.addNestedContainerProperty("afp.description");
				
				final Table table = new Table("Tabla AFP"){
					{
						setWidth("100%");
						setPageLength(6);
					}
				};
				
				
				//agrega el listener a los field creados
				table.setTableFieldFactory(new ListenerFieldFactory(listener));
				
				table.setContainerDataSource(container);
				
				table.setVisibleColumns("afp.description","rate");
				table.setColumnHeaders("Afp","Tasa");
				table.setEditable(true);
				
				addComponent(table);
				if(!SecurityHelper.hasPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
					setEnabled(false);
				}else{
					setEnabled(true);
				}
				
			}
		};
	}
	
	private com.vaadin.ui.Component drawGlobales() {
		return new VerticalLayout(){
			{
				setSizeFull();
				setSpacing(true);
				setMargin(true);
				
				final FieldGroup fg = new FieldGroup();
				wageconfiguration = confService.findWageConfigurations();
				if( wageconfiguration  == null )
					wageconfiguration  = new WageConfigurations();
				fg.setItemDataSource(new BeanItem<WageConfigurations>(wageconfiguration));
				
				FormLayout form = new FormLayout(){
					{
						setSizeUndefined();
						
						addComponent(new Label("<hr />",ContentMode.HTML){
							{
								setWidth("100%");
							}
						});
						
						Property.ValueChangeListener listener = new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								try {
									fg.commit();
									WageConfigurations bean = ((BeanItem<WageConfigurations>)fg.getItemDataSource()).getBean();
									confService.save(bean);
								} catch (Exception e) {
									logger.error("Error al guardar las propiedades de sueldo",e);
									Notification.show("Error al guardar");
								}
							}
						};
						
						Field advance = fg.buildAndBind("Sueldo Mínimo", "minimumWage");
						((TextField)advance).setNullRepresentation("");
						advance.addValueChangeListener(listener);
						
						addComponent(advance);
						addComponent(new Label("Jornal base : #"));
						addComponent(new Label("Gratificación : #"));
						
						addComponent(new Label("<hr />",ContentMode.HTML){
							{
								setWidth("100%");
							}
						});
						
						Field collation = fg.buildAndBind("Colación", "collation");
						((TextField)collation).setNullRepresentation("");
						collation.addValueChangeListener(listener);
						addComponent(collation);
						Field mobilization = fg.buildAndBind("Movilización 1", "mobilization");
						((TextField)mobilization).setNullRepresentation("");
						mobilization.addValueChangeListener(listener);
						addComponent(mobilization);
						
						addComponent(new Label("<hr />",ContentMode.HTML){
							{
								setWidth("100%");
							}
						});
						
						final Table table = new Table("Movilización 2"){
							{
								setWidth("100%");
								setPageLength(3);
							}
						};
						
						final BeanItemContainer<Mobilization2> container = new BeanItemContainer<Mobilization2>(Mobilization2.class,wageconfiguration.getMobilizations2());
						container.addNestedContainerProperty("constructionSite.name");
						table.setContainerDataSource(container);
						
						table.addGeneratedColumn("eliminar", new Table.ColumnGenerator() {
							
							@Override
							public Object generateCell(Table source,final Object itemId, Object columnId) {
								return new Button(null,new Button.ClickListener() {
									final Mobilization2 mob = container.getItem(itemId).getBean();
									@Override
									public void buttonClick(ClickEvent event) {
										
										try{
											wageconfiguration.removeMobilizations2(mob);
											confService.save(wageconfiguration);
											container.removeItem(itemId);
										}catch(Exception e){
											Notification.show("Error al quitar elemento",Type.ERROR_MESSAGE);
											logger.error("Error al quitar una mobilización 2",e);
										}
									}
								}){
									{setIcon(FontAwesome.TRASH_O);}
								};
							}
						});
						
						table.setVisibleColumns("constructionSite.name","amount","eliminar");
						table.setColumnHeaders("Obra","Monto","Eliminar");
						
						
						HorizontalLayout hl = new HorizontalLayout(){
							{
								setSpacing(true);
								final ComboBox nombre = new ComboBox("Obra",constructionContainer);
								nombre.setItemCaptionMode(ItemCaptionMode.PROPERTY);
								nombre.setItemCaptionPropertyId("name");
								addComponent(nombre);
								final TextField monto = new TextField("Monto");
								addComponent(monto);
								addComponent(new Button(null,new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
										try{
											
											ConstructionSite cs = (ConstructionSite) nombre.getValue();
											Mobilization2 mob = new Mobilization2();
											mob.setAmount(Double.valueOf(monto.getValue()));
											mob.setConstructionSite(cs);
											wageconfiguration.addMobilizations2(mob);
											confService.save(wageconfiguration);
											
											container.addBean(mob);
										}catch(Exception e){
											Notification.show("Error al agregar la mobilización 2",Type.ERROR_MESSAGE);
											logger.error("Error al agregar la mobilización 2",e);
										}
									}
							}){
									{
										setIcon(FontAwesome.PLUS);
									}
								});
							}
						};
						hl.setWidth("100%");
						addComponent(hl);
						
						addComponent(table);

					}
				};
				addComponent(form);
				setComponentAlignment(form, Alignment.MIDDLE_CENTER);
				if(!SecurityHelper.hasPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
					setEnabled(false);
				}else{
					setEnabled(true);
				}
			}
		};
	}

	private com.vaadin.ui.Component drawMensuales() {
		return new HorizontalLayout(){
			{
				setSizeFull();
				setSpacing(true);
				setMargin(true);
				
				final FieldGroup fg = new FieldGroup();
				fg.setItemDataSource(new BeanItem<DateConfigurations>(new DateConfigurations()));
//				setComponentAlignment(mes, Alignment.MIDDLE_CENTER);
				FormLayout form = new FormLayout(){
					{
						setSizeUndefined();
						InlineDateField  mes = new InlineDateField("Mes");
						mes.setResolution(Resolution.MONTH);
						
						Property.ValueChangeListener listener = new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								try {
									fg.commit();
									DateConfigurations bean = ((BeanItem<DateConfigurations>)fg.getItemDataSource()).getBean();
									confService.save(bean);
								} catch (Exception e) {
									logger.error("Error al guardar las propiedades de fecha ",e);
									if(e instanceof ConstraintViolationException ){
										logger.error("Error de constraint "+Utils.printConstraintMessages(((ConstraintViolationException) e).getConstraintViolations()),e);
										Notification.show("Error al validar los datos:\n"+Utils.printConstraintMessages(((ConstraintViolationException) e).getConstraintViolations()), Type.ERROR_MESSAGE);
									}else{
										Notification.show("Error al guardar");
									}
								}
							}
						};
						addComponent(mes);
//						fg.bind(mes, "date");
						final DateField advance = (DateField) fg.buildAndBind("Fecha Cierre Anticipo", "advance");
						advance.addValueChangeListener(listener);
						addComponent(advance);
						final DateField assistance = (DateField) fg.buildAndBind("Fecha Cierre Asistencia", "assistance");
						assistance.addValueChangeListener(listener);
						addComponent(assistance);
						final DateField beginDeal = (DateField) fg.buildAndBind("Fecha Inicio Trato", "beginDeal");
						beginDeal.addValueChangeListener(listener);
						addComponent(beginDeal);
						final DateField finishDeal = (DateField) fg.buildAndBind("Fecha Fin Trato", "finishDeal");
						finishDeal.addValueChangeListener(listener);
						addComponent(finishDeal);
						
						Field oil = fg.buildAndBind("Petroleo", "oil");
						((TextField)oil).setNullRepresentation("");
						oil.addValueChangeListener(listener);
						addComponent(oil);
						
						Field benzine = fg.buildAndBind("Bencina", "benzine");
						((TextField)benzine).setNullRepresentation("");
						benzine.addValueChangeListener(listener);
						addComponent(benzine);

						Field uf = fg.buildAndBind("UF Mes", "uf");
						((TextField)uf).setNullRepresentation("");
						uf.addValueChangeListener(listener);
						addComponent(uf);
						
						mes.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								
								//busca la configuración del mes
								Date date = (Date)event.getProperty().getValue();
								DateConfigurations config = confService.findDateConfigurationsByDate(date);
								
								if(config == null ){
									config = new DateConfigurations();
									config.setDate(date);
								}
								
								advance.setRangeStart(null);advance.setRangeEnd(null);
								assistance.setRangeEnd(null);assistance.setRangeStart(null);
//								beginDeal.setRangeEnd(null);beginDeal.setRangeStart(null);
								finishDeal.setRangeEnd(null);finishDeal.setRangeStart(null);
								
								fg.setItemDataSource(new BeanItem<DateConfigurations>(config));
								
								//calcula el inicio y final del mes
								Date current = config.getDate();
								Date endDate = new DateTime(current).dayOfMonth().withMaximumValue().toDate();
								Date startDate = new DateTime(current).withDayOfMonth(1).toDate();
								advance.setRangeStart(startDate);advance.setRangeEnd(endDate);
								assistance.setRangeEnd(endDate);assistance.setRangeStart(startDate);
//								beginDeal.setRangeEnd(endDate);beginDeal.setRangeStart(startDate);
								finishDeal.setRangeEnd(endDate);finishDeal.setRangeStart(startDate);
							}
						});
					}
				};
				addComponent(form);
				setComponentAlignment(form, Alignment.MIDDLE_CENTER);
				
				if(!SecurityHelper.hasPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
					setEnabled(false);
				}else{
					setEnabled(true);
				}
			}
		};

	}
	
	private com.vaadin.ui.Component drawFeriados() {
		VerticalLayout vl = new VerticalLayout();
		
		vl.setMargin(true);
		
//		Calendar calendar = new Calendar();
//		calendar.setLocale(Locale.getDefault());
//		calendar.setImmediate(true);
//		calendar.setSizeFull();
//
//		GregorianCalendar gregorianCalendar = new GregorianCalendar(calendar.getLocale());
//
//		//        final int rollAmount = gregorianCalendar
//		//                .get(GregorianCalendar.DAY_OF_MONTH) - 1;
//		gregorianCalendar.set(GregorianCalendar.DAY_OF_MONTH, 1);
//		//        resetTime(false);
//		Date currentMonthsFirstDate = gregorianCalendar.getTime();
//		calendar.setStartDate(currentMonthsFirstDate);
//		gregorianCalendar.add(GregorianCalendar.MONTH, 1);
//		gregorianCalendar.add(GregorianCalendar.DATE, -1);
//		calendar.setEndDate(gregorianCalendar.getTime());
////
//		HorizontalLayout hl = new HorizontalLayout();
//		hl.setSizeFull();
//		hl.addComponent(calendar);
//		VerticalLayout form = new VerticalLayout(){
//			{
//				addComponent(new TextField("Nombre Feriado"));
//			}
//		};
//		hl.addComponent(form);
//		hl.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
//		hl.setExpandRatio(form, 1.0F);
		
		setMargin(true);
		
		final Table table = new Table("Feriados"){
			{
				int i = 1;
				setWidth("100%");
				
				addContainerProperty("nombre", String.class, "");
				addContainerProperty("fecha", DateField.class, new DateField());
				addContainerProperty("eliminar", Button.class, new Button(null,FontAwesome.TRASH_O));
				setVisibleColumns("nombre","fecha","eliminar");
				setColumnHeaders("Nombre","Fecha","Eliminar");

				addItem(new Object[]{"Feriado 1",new DateField(null,new Date()),new Button(null,FontAwesome.TRASH_O)}, i++);
				addItem(new Object[]{"Feriado 2",new DateField(null,new Date()),new Button(null,FontAwesome.TRASH_O)}, i++);
				addItem(new Object[]{"Feriado 3",new DateField(null,new Date()),new Button(null,FontAwesome.TRASH_O)}, i++);

				setPageLength(6);
			}
		};
		
		HorizontalLayout hl = new HorizontalLayout(){
			{
				final TextField nombre = new TextField("Nombre Feriado");
				addComponent(nombre);
				final DateField fecha = new DateField("Fecha");
				addComponent(fecha);
				addComponent(new Button(null,new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						final DateField df = new DateField();
						df.setValue(fecha.getValue());
						Button btn = new Button(null,FontAwesome.TRASH_O);
						final Object itemId = table.addItem(new Object[]{nombre.getValue(),df, btn }, fecha.getValue());
						btn.addClickListener(new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								table.removeItem(itemId);
							}
						});
					}
				}){
					{
						setIcon(FontAwesome.PLUS);
					}
				});
			}
		};
		hl.setSizeFull();
		vl.addComponent(hl);
		
		vl.addComponent(table);
		
		if(!SecurityHelper.hasPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
			vl.setEnabled(false);
		}else{
			vl.setEnabled(true);
		}

		
		return vl;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
			((MagalUI)UI.getCurrent()).setBackVisible(false);
			((MagalUI)UI.getCurrent()).highlightMenuItem(NAME);
			((MagalUI)UI.getCurrent()).getTitle().setValue("<h1>Configuraciones</h1>");
			
			//agrega las obras TODO segun perfil TODO usar paginación
			Page<ConstructionSite> page = service.findAllConstructionSite(new PageRequest(0, 20));
			constructionContainer.removeAllItems();
			constructionContainer.addAll(page.getContent());
	}

}
