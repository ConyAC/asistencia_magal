package cl.magal.asistencia.ui.config;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.VaadinView;
import cl.magal.asistencia.entities.Configurations;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.DateConfigurations;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.services.ConfigurationService;
import cl.magal.asistencia.services.ConstructionSiteService;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
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
	
	Logger logger = LoggerFactory.getLogger(ConfigView.class);

	public static final String NAME = "configuraciones";
	BeanItemContainer<ConstructionSite> constructionContainer = new BeanItemContainer<ConstructionSite>(ConstructionSite.class);

	@Autowired
	private transient ConstructionSiteService service;
	@Autowired
	private transient ConfigurationService confService;
	
	
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

				addComponent(new Table("Asignación Familiar"){
					{
						int i = 1;
						setWidth("100%");
						setHeightUndefined();
						addContainerProperty("desde", String.class, "");
						addContainerProperty("hasta", String.class, "");
						addContainerProperty("mont", TextField.class, new TextField());
						
						setVisibleColumns("desde","hasta","mont");
						setColumnHeaders("Desde","Hasta","Monto");

						addItem(new Object[]{"0","7744",new TextField(null,"0")}, i++);
						addItem(new Object[]{"8100,64","7744",new TextField(null,"202516")}, i++);
						addItem(new Object[]{"8100,68","5221",new TextField(null,"202517")}, i++);
						addItem(new Object[]{"12696,28","5221",new TextField(null,"317407")}, i++);
						addItem(new Object[]{"12696,32","4650",new TextField(null,"317408")}, i++);
						addItem(new Object[]{"19801,92","4650",new TextField(null,"495048")}, i++);
						addItem(new Object[]{"19801,96","0",new TextField(null,"495049")}, i++);
						addItem(new Object[]{"1000000","0",new TextField(null,"25000000")}, i++);

					}
				});

				if(!hastPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
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

				addComponent(new Table("Impuesto 2° Categoria"){
					{
						int i = 1;
						setWidth("100%");
						setHeightUndefined();
						addContainerProperty("desde", String.class, "");
						addContainerProperty("hasta", String.class, "");
						addContainerProperty("factor", TextField.class, new TextField());
						addContainerProperty("rebaja", TextField.class, new TextField());
						addContainerProperty("exento", TextField.class, new TextField());
						
						setVisibleColumns("desde","hasta","factor","rebaja","exento");
						setColumnHeaders("Desde","Hasta","Factor","Rebaja","Exento");

						addItem(new Object[]{"0","$541147,5",new TextField(null,"0"),new TextField(null,"0"),new TextField(null,"0")}, i++);
						addItem(new Object[]{"$541147,51","$1202550",new TextField(null,"0,04"),new TextField(null,"$21645,9"),new TextField(null,"2,2%")}, i++);
						addItem(new Object[]{"$1202550,01","$2004250",new TextField(null,"11,27%"),new TextField(null,"$69747,9"),new TextField(null,"4,52%")}, i++);
						addItem(new Object[]{"$2004250,01","$2805950",new TextField(null,"10,77%"),new TextField(null,"$179981,65"),new TextField(null,"7,09%")}, i++);
						addItem(new Object[]{"$2805950,01","$3607650",new TextField(null,"12,36%"),new TextField(null,"$446546,9"),new TextField(null,"10,62%")}, i++);
						addItem(new Object[]{"$3607650,01","$4810200",new TextField(null,"11,54%"),new TextField(null,"$713513"),new TextField(null,"15,57%")}, i++);
						addItem(new Object[]{"$4810200,01","$6012750",new TextField(null,"11,54%"),new TextField(null,"$958833,2"),new TextField(null,"19,55%")}, i++);
						addItem(new Object[]{"$6012750,01","más",new TextField(null,"11,54%"),new TextField(null,"$1229406,95"),new TextField(null,"19,55%")}, i++);

					}
				});

				if(!hastPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
					setEnabled(false);
				}else{
					setEnabled(true);
				}
			}
		};
	}

	private com.vaadin.ui.Component drawAnticipos() {
		return new VerticalLayout(){
			{
				setMargin(true);
				setSpacing(true);

				addComponent(new FormLayout(){
					{
						addComponent(new TextField("Descuento por Permiso","15000"));
						addComponent(new TextField("Descto Adicional por Falla ","5000"));
					}
				});

				addComponent(new Table("Tabla Anticipo"){
					{
						setSizeFull();
						addContainerProperty("suple_cod", String.class, "");
						addContainerProperty("suple_monto", String.class, "");
						addContainerProperty("suple_monto_normal", TextField.class, new TextField());
						addContainerProperty("suple_monto_aumento", TextField.class, new TextField());
						setVisibleColumns("suple_cod","suple_monto","suple_monto_normal","suple_monto_aumento");
						setColumnHeaders("Código Suple","Monto Suple","Normal","Aumento Anticipo");

						int i = 1;

						addItem(new Object[]{"1","105000",new TextField(null,"105000"),new TextField(null,"0")}, i++);
						addItem(new Object[]{"2","140000",new TextField(null,"140000"),new TextField(null,"0")}, i++);
						addItem(new Object[]{"3","130000",new TextField(null,"130000"),new TextField(null,"0")}, i++);
						addItem(new Object[]{"4","250000",new TextField(null,"250000"),new TextField(null,"0")}, i++);
						addItem(new Object[]{"5","115000",new TextField(null,"115000"),new TextField(null,"0")}, i++);
					}

				});

				if(!hastPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
					setEnabled(false);
				}else{
					setEnabled(true);
				}
			}
		};
	}

	private com.vaadin.ui.Component drawAFP() {
		return new VerticalLayout(){
			{
				setMargin(true);
				addComponent(new Table("Tabla AFP y Seguro SIS"){
					{
						int i = 1;
						setWidth("100%");
						
						addContainerProperty("afp", String.class, "");
						addContainerProperty("tasa", TextField.class, new TextField());
						addContainerProperty("sis", TextField.class, new TextField());
						setVisibleColumns("afp","tasa","sis");
						setColumnHeaders("AFP","Tasa","SIS");

						addItem(new Object[]{"Capital",new TextField(null,"11,44%"),new TextField(null,"1,26%")}, i++);
						addItem(new Object[]{"Cuprum",new TextField(null,"11,47%"),new TextField(null,"1,26%")}, i++);
						addItem(new Object[]{"Habitat",new TextField(null,"11,27%"),new TextField(null,"1,26%")}, i++);
						addItem(new Object[]{"Modelo",new TextField(null,"10,77%"),new TextField(null,"1,26%")}, i++);
						addItem(new Object[]{"Plan Vital",new TextField(null,"12,36%"),new TextField(null,"1,26%")}, i++);
						addItem(new Object[]{"Provida",new TextField(null,"11,54%"),new TextField(null,"1,26%")}, i++);

						setPageLength(6);
					}
				});
				if(!hastPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
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
				Configurations configuration = confService.findConfigurations();
				if( configuration  == null )
					configuration  = new Configurations();
				fg.setItemDataSource(new BeanItem<Configurations>(configuration));
				
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
									Configurations bean = ((BeanItem<Configurations>)fg.getItemDataSource()).getBean();
									confService.save(bean);
								} catch (Exception e) {
									Notification.show("Error al guardar");
								}
							}
						};
						
						Field advance = fg.buildAndBind("Sueldo Mínimo", "minWage");
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
								int i = 1;
								setWidth("100%");
								
								addContainerProperty("obra.name", ConstructionSite.class, "");
								addContainerProperty("monto", TextField.class, new TextField());
								addContainerProperty("eliminar", Button.class, new Button(null,FontAwesome.TRASH_O));
								setVisibleColumns("obra.name","monto","eliminar");
								setColumnHeaders("Obra","Monto","Eliminar");

								
								setPageLength(3);
							}
						};
						
						HorizontalLayout hl = new HorizontalLayout(){
							{
								setSpacing(true);
								final ComboBox nombre = new ComboBox("Obra",constructionContainer);
								nombre.setItemCaptionMode(ItemCaptionMode.PROPERTY);
								nombre.setItemCaptionPropertyId("name");
								addComponent(nombre);
								final TextField fecha = new TextField("Monto");
								addComponent(fecha);
								addComponent(new Button(null,new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
										
										table.addItem(new Object[]{nombre.getValue(),new TextField(null,fecha.getValue()), new Button(null,new Button.ClickListener() {
											
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
						hl.setWidth("100%");
						addComponent(hl);
						
						addComponent(table);

					}
				};
				addComponent(form);
				setComponentAlignment(form, Alignment.MIDDLE_CENTER);
				if(!hastPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
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
				
				if(!hastPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
					setEnabled(false);
				}else{
					setEnabled(true);
				}
			}
		};

	}
	
		private boolean hastPermission(Permission... permissions) {
		if(permissions == null)
			return true;
		
		User usuario = (User) VaadinSession.getCurrent().getAttribute(
				"usuario");
		if(usuario.getRole() == null || usuario.getRole().getPermission() == null ){
			return false;
		}
		for(Permission p : permissions){
			if(!usuario.getRole().getPermission().contains(p))
				return false;
		}
		
		return true;
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
		
		if(!hastPermission(Permission.DEFINIR_VARIABLE_GLOBAL)){
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
