package cl.magal.asistencia.ui.config;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.VaadinView;
import cl.magal.asistencia.entities.AdvancePaymentConfigurations;
import cl.magal.asistencia.entities.AdvancePaymentItem;
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
import cl.magal.asistencia.ui.OnValueChangeFieldFactory;
import cl.magal.asistencia.ui.OnValueChangeFieldFactory.OnValueChangeListener;
import cl.magal.asistencia.util.SecurityHelper;

import com.vaadin.data.Container;
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
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
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
	AdvancePaymentConfigurations advancepayment;
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

		ts.addTab(drawAnticipos(),"Tabla de Anticipos",FontAwesome.BEER);

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
				table.setVisibleColumns("from","to","amount");
				table.setColumnHeaders("Desde","Hasta","Monto");
				addComponent(table);

				if(!SecurityHelper.hastPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
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

				if(!SecurityHelper.hastPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
					setEnabled(false);
				}else{
					setEnabled(true);
				}
			}
		};
	}

	private com.vaadin.ui.Component drawAnticipos() {
		
		final FieldGroup fg = new FieldGroup();
		advancepayment = confService.findAdvancePaymentConfigurations();
		if( advancepayment  == null )
			advancepayment  = new AdvancePaymentConfigurations();
		fg.setItemDataSource(new BeanItem<AdvancePaymentConfigurations>(advancepayment));
		
		final Property.ValueChangeListener listener = new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					fg.commit();
					AdvancePaymentConfigurations bean = ((BeanItem<AdvancePaymentConfigurations>)fg.getItemDataSource()).getBean();
					confService.save(bean);
				} catch (Exception e) {
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
						addComponent(supleCode);
						Button add = new Button(null,new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								try{
									
									AdvancePaymentItem advancePaymentItem = new AdvancePaymentItem();
									advancePaymentItem.setSupleCode(Integer.valueOf(supleCode.getValue()));
									//TODO hacer dialogo para crear y validar nuevo item
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
						setComponentAlignment(add, Alignment.BOTTOM_LEFT);
					}
				};
				hl.setWidth("100%");
				addComponent(hl);

				final Table table = new Table("Tabla Anticipo"){
					{
						setSizeFull();
						setPageLength(5);
					}

				};
				
				table.setContainerDataSource(container);
				
				table.addGeneratedColumn("eliminar", new Table.ColumnGenerator() {
					
					@Override
					public Object generateCell(Table source,final Object itemId, Object columnId) {
						return new Button(null,new Button.ClickListener() {
							final AdvancePaymentItem advancePaymentItem = container.getItem(itemId).getBean();
							@Override
							public void buttonClick(ClickEvent event) {
								
								try{
									advancepayment.removeAdvancePaymentItem(advancePaymentItem);
									confService.save(advancepayment);
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
				
				table.setVisibleColumns("supleCode","supleTotalAmount","supleNormalAmount","supleIncreaseAmount","eliminar");
				table.setColumnHeaders("Código Suple","Monto Suple","Normal","Aumento Anticipo","Eliminar");
				table.setEditable(true);
				
				addComponent(table);

				if(!SecurityHelper.hastPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
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
				if(!SecurityHelper.hastPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
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
				if(!SecurityHelper.hastPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
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
								
								fg.setItemDataSource(new BeanItem<DateConfigurations>(config));
							}
						});
						
						Property.ValueChangeListener listener = new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								try {
									fg.commit();
									DateConfigurations bean = ((BeanItem<DateConfigurations>)fg.getItemDataSource()).getBean();
									confService.save(bean);
								} catch (Exception e) {
									Notification.show("Error al guardar");
								}
							}
						};
						addComponent(mes);
//						fg.bind(mes, "date");
						Field advance = fg.buildAndBind("Fecha Cierre Anticipo", "advance");
						advance.addValueChangeListener(listener);
						addComponent(advance);
						Field assistance = fg.buildAndBind("Fecha Cierre Asistencia", "assistance");
						assistance.addValueChangeListener(listener);
						addComponent(assistance);
						Field beginDeal = fg.buildAndBind("Fecha Inicio Trato", "beginDeal");
						beginDeal.addValueChangeListener(listener);
						addComponent(beginDeal);
						Field finishDeal = fg.buildAndBind("Fecha Fin Trato", "finishDeal");
						finishDeal.addValueChangeListener(listener);
						addComponent(finishDeal);
					}
				};
				addComponent(form);
				setComponentAlignment(form, Alignment.MIDDLE_CENTER);
				
				if(!SecurityHelper.hastPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
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
						
						table.addItem(new Object[]{nombre.getValue(),fecha, new Button(null,new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								table.removeItem(fecha.getValue());
							}
						}){
							{
								setIcon(FontAwesome.TRASH_O);
							}
						}}, fecha.getValue());
						}
					}
				 ){
					{
						setIcon(FontAwesome.PLUS);
					}
				});
			}
		};
		hl.setSizeFull();
		vl.addComponent(hl);
		
		vl.addComponent(table);
		
		if(!SecurityHelper.hastPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
			vl.setEnabled(false);
		}else{
			vl.setEnabled(true);
		}

		
		return vl;
	}

	@Override
	public void enter(ViewChangeEvent event) {
			//agrega las obras TODO segun perfil TODO usar paginación
			Page<ConstructionSite> page = service.findAllConstructionSite(new PageRequest(0, 20));
			constructionContainer.removeAllItems();
			constructionContainer.addAll(page.getContent());
	}

}
