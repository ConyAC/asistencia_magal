package cl.magal.asistencia.ui.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import cl.magal.asistencia.entities.Bank;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.DateConfigurations;
import cl.magal.asistencia.entities.FamilyAllowanceConfigurations;
import cl.magal.asistencia.entities.Holiday;
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
	BeanItemContainer<Holiday> holidayContainer = new BeanItemContainer<Holiday>(Holiday.class);
	BeanItemContainer<Bank> bankContainer = new BeanItemContainer<Bank>(Bank.class);
	BeanItem<Holiday> item;
	Integer limit = 10;
	
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

		setSizeFull();
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
		
		ts.addTab(drawBancos(),"Bancos",FontAwesome.DOLLAR);
		

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
				
				HorizontalLayout hl = new HorizontalLayout();
				hl.setWidth("100%");
				addComponent(hl);

				final TextField desde = new TextField("Desde");
				hl.addComponent(desde);
				final TextField hasta = new TextField("Hasta");
				hl.addComponent(hasta);
				final TextField monto = new TextField("Monto");
				hl.addComponent(monto);
				
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
				
				Button btnAdd = new Button(null,FontAwesome.PLUS);
				hl.addComponent(btnAdd);
				btnAdd.addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						try{
							if(desde.getValue() == "" || hasta.getValue() == "" || monto.getValue() == ""){
								Notification.show("Debe ingresar todos los valores del nuevo rango de asignación familiar.",Type.ERROR_MESSAGE);
								return;	
							}else if(container.size() > limit){
								Notification.show("Se ha alcanzado el límite máximo de creación de rango de asignación familiar.",Type.ERROR_MESSAGE);
								return;
							}else{
								FamilyAllowanceConfigurations fac = new FamilyAllowanceConfigurations();
								fac.setFrom(Double.parseDouble(desde.getValue()));
								fac.setTo(Double.parseDouble(hasta.getValue()));
								fac.setAmount(Double.parseDouble(monto.getValue()));							
								confService.save(fac);
								container.addBean(fac);			
								
								desde.setValue("");
								hasta.setValue("");
								monto.setValue("");
							}
						}catch(Exception e){
							Notification.show("Error al añadir el rango de asignación familiar.",Type.ERROR_MESSAGE);
							logger.error("Error al añadir el elemento",e);
						}
					}
				});		
				
				setComponentAlignment(hl, Alignment.TOP_RIGHT);				
				
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
				
				HorizontalLayout hl = new HorizontalLayout();
				hl.setWidth("100%");
				addComponent(hl);

				final TextField desde = new TextField("Desde");
				hl.addComponent(desde);
				final TextField hasta = new TextField("Hasta");
				hl.addComponent(hasta);
				final TextField factor = new TextField("Factor");
				hl.addComponent(factor);
				final TextField rebaja = new TextField("Rebaja");
				hl.addComponent(rebaja);
				final TextField exento = new TextField("Exento");
				hl.addComponent(exento);
				
				table.setContainerDataSource(container);
				table.addGeneratedColumn("delete", new Table.ColumnGenerator() {
					
					@Override
					public Object generateCell(Table source, final Object itemId, Object columnId) {
						return new Button(null,new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar el rango de impuesto seleccionado?",
										"Eliminar", "Cancelar", new ConfirmDialog.Listener() {

									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											
											TaxationConfigurations tc = ((BeanItem<TaxationConfigurations>)container.getItem(itemId)).getBean();
											confService.delete(tc);
											container.removeItem(itemId);
										}
									}
								});
							}
						}){{setIcon(FontAwesome.TRASH_O);}};
					}
				});
				
				table.setVisibleColumns("from","to","factor","reduction","exempt","delete");
				table.setColumnHeaders("Desde","Hasta","Factor","Rebaja","Exento","Quitar");
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
				Button btnAdd = new Button(null,FontAwesome.PLUS);
				hl.addComponent(btnAdd);
				btnAdd.addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						try{
							if(desde.getValue() == "" || hasta.getValue() == "" || factor.getValue() == "" || exento.getValue() == "" || rebaja.getValue() == ""){
								Notification.show("Debe ingresar todos los valores para generar el rango de impuesto.",Type.ERROR_MESSAGE);
								return;
							}else if(container.size() > limit){
								Notification.show("Se ha alcanzado el límite máximo de creación de rango de impuestos de segunda categoría.",Type.ERROR_MESSAGE);
								return;
							}else{
								TaxationConfigurations tc = new TaxationConfigurations();
								tc.setFrom(Double.parseDouble(desde.getValue()));
								tc.setTo(Double.parseDouble(hasta.getValue()));
								tc.setExempt(Double.parseDouble(exento.getValue()));
								tc.setReduction(Double.parseDouble(rebaja.getValue()));
								tc.setFactor(Double.parseDouble(factor.getValue()));
								confService.save(tc);
								container.addBean(tc);
								
								desde.setValue("");
								hasta.setValue("");
								factor.setValue("");
								rebaja.setValue("");
								exento.setValue("");
							}
						}catch(Exception e){
							Notification.show("Error al añadir el rango de impuestos de segunda categoria.",Type.ERROR_MESSAGE);
							logger.error("Error al añadir elemento",e);
						}
					}
				});		
				
				setComponentAlignment(hl, Alignment.TOP_RIGHT);
				
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
		
		afpAndInsurance = confService.findAfpAndInsuranceConfiguration();
		if( afpAndInsurance  == null )
			afpAndInsurance  = new AfpAndInsuranceConfigurations();
		
		final FieldGroup fg = new FieldGroup();
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
						setMargin(false);
						setSpacing(false);
						Field sis = fg.buildAndBind("SIS", "sis");
						((TextField)sis).setNullRepresentation("");
						sis.addValueChangeListener(listener);
						
						addComponent(sis);
					}
				});
							
				final BeanItemContainer<AfpItem> container = new BeanItemContainer<AfpItem>(AfpItem.class, afpAndInsurance.getAfpTable());
				
				final Table table = new Table(){
					{
						setWidth("100%");
						setPageLength(6);
					}
				};

				HorizontalLayout hl = new HorizontalLayout();
				hl.setWidth("100%");
//				hl.setSpacing(true);		
				addComponent(hl);

				final TextField nombre = new TextField("Nombre AFP");
				hl.addComponent(nombre);
				final TextField tasa = new TextField("Tasa");
				hl.addComponent(tasa);
				
				//agrega el listener a los field creados
				table.setTableFieldFactory(new ListenerFieldFactory(listener));				
				table.setContainerDataSource(container);				
				table.addGeneratedColumn("delete", new Table.ColumnGenerator() {
					
					@Override
					public Object generateCell(Table source, final Object itemId, Object columnId) {
						return new Button(null,new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar la AFP seleccionada?",
										"Eliminar", "Cancelar", new ConfirmDialog.Listener() {

									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											AfpItem afp = ((BeanItem<AfpItem>)container.getItem(itemId)).getBean();
											confService.delete(afp);
											container.removeItem(itemId);
										}
									}
								});
							}
						}){{setIcon(FontAwesome.TRASH_O);}};
					}
				});
				
				table.setVisibleColumns("name","rate","delete");
				table.setColumnHeaders("Afp","Tasa","Eliminar");
				table.setEditable(true);

				Button btnAdd = new Button(null,FontAwesome.PLUS);
				hl.addComponent(btnAdd);
				btnAdd.addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						try{
							if(nombre.getValue() == "" || tasa.getValue() == null){
								Notification.show("Debe ingresar tanto el nombre como la tasa de la nueva AFP.",Type.ERROR_MESSAGE);
								return;
							}else{
								AfpItem a = new AfpItem();
								a.setAfpAndInsuranceConfigurations(afpAndInsurance);
								a.setName(nombre.getValue());
								a.setRate( Utils.getDecimalFormat().parse(tasa.getValue()).doubleValue());
								confService.save(a);
								container.addBean(a);
								
								nombre.setValue("");
								tasa.setValue("");						
							}
						}catch(Exception e){
							Notification.show("Error al añadir la afp.",Type.ERROR_MESSAGE);
							logger.error("Error al añadir elemento",e);
						}
					}
				});		
				
				setComponentAlignment(hl, Alignment.TOP_RIGHT);
				
				addComponent(table);
				setExpandRatio(table, 1.0F);
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
				setSpacing(true);
				setMargin(true);
				
				final FieldGroup fg = new FieldGroup();
				wageconfiguration = confService.findWageConfigurations();
				if( wageconfiguration  == null )
					wageconfiguration  = new WageConfigurations();
				fg.setItemDataSource(new BeanItem<WageConfigurations>(wageconfiguration));
				
				FormLayout form = new FormLayout(){
					{
						
//						addComponent(new Label("<hr style='width:100%'/>",ContentMode.HTML));
						
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
						
						String grat = "#",jb = "#";
						if(wageconfiguration.getMinimumWage() != null){
							Double sueldoMinimo = wageconfiguration.getMinimumWage();
							jb = Utils.getDecimalFormatSinDecimal().format(sueldoMinimo/30);
							grat = Utils.getDecimalFormatSinDecimal().format(4.75*sueldoMinimo/12);
						}
						
						final Label jornaBase = new Label("Jornal base : "+ jb);
						final Label gratificacion = new Label("Gratificación : "+grat);
						
						Field advance = fg.buildAndBind("Sueldo Mínimo", "minimumWage");
						((TextField)advance).setNullRepresentation("");
						advance.addValueChangeListener(listener);
						advance.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								Double sueldoMinimo = 0D;
								try{
									sueldoMinimo = Utils.getDecimalFormatSinDecimal().parse((String) event.getProperty().getValue()).doubleValue();
								}catch(Exception e){
									logger.error("Error al transformar el sueldo mínimo",e);
									return;
								}
								double grat = 4.75*sueldoMinimo/12;
								double jb = sueldoMinimo/30;
								jornaBase.setValue("Jornal base : "+Utils.getDecimalFormatSinDecimal().format( jb ));
								gratificacion.setValue("Gratificación : "+Utils.getDecimalFormatSinDecimal().format( grat ));
							}
						});
						
						addComponent(advance);
						addComponent(jornaBase);
						addComponent(gratificacion);
						
						Field maxImponibleFactor = fg.buildAndBind("Máxima Imponible", "maxImponibleFactor");
						((TextField)maxImponibleFactor).setNullRepresentation("");
						addComponent(maxImponibleFactor);
						maxImponibleFactor.addValueChangeListener(listener);
						
						addComponent(new Label("<hr style='width:100%'/>",ContentMode.HTML){
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
								beginDeal.setRangeEnd(null);beginDeal.setRangeStart(null);
								finishDeal.setRangeEnd(null);finishDeal.setRangeStart(null);
								
								fg.setItemDataSource(new BeanItem<DateConfigurations>(config));
								
								//calcula el inicio y final del mes
								Date current = config.getDate();
								DateTime endDatetime = new DateTime(current).dayOfMonth().withMaximumValue(); 
								Date endDate = endDatetime.toDate();
								DateTime startDatetime = new DateTime(current).withDayOfMonth(1); 
								Date startDate = startDatetime.toDate();
								
								advance.setRangeStart(startDate);advance.setRangeEnd(endDate);
								assistance.setRangeEnd(endDate);assistance.setRangeStart(startDate);
								//el principio del trato puede ser desde el primero del mes anterior hasta el primero del mes actual
								beginDeal.setRangeEnd(startDate);beginDeal.setRangeStart(startDatetime.minusMonths(1).toDate());
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
	
	
	protected VerticalLayout drawFeriados() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();
		
		List<Holiday> h = service.findAllHoliday();
		holidayContainer = new BeanItemContainer<Holiday>(Holiday.class, h);

		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setSpacing(true);		
		vl.addComponent(hl);

		final TextField nombre = new TextField("Nombre Feriado");
		hl.addComponent(nombre);
		final DateField fecha = new DateField("Fecha");
		hl.addComponent(fecha);

		final Table table = new Table(){
			{
				setSizeFull();
				setContainerDataSource(holidayContainer);
				setTableFieldFactory(new DefaultFieldFactory(){

					public Field<?> createField(final Container container,
							final Object itemId,Object propertyId,com.vaadin.ui.Component uiContext) {
						Field<?> field = null; 
						if( propertyId.equals("name")){
							field = new TextField();
							((TextField)field).setNullRepresentation("");
						}
						else if(  propertyId.equals("date") ){
							field = new DateField();
						}
						else {
							return null;
						}
						return field;
					}
				});
				
				addGeneratedColumn("delete", new Table.ColumnGenerator() {
					
					@Override
					public Object generateCell(Table source, final Object itemId, Object columnId) {
						return new Button(null,new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar de la asistencia el feriado seleccionado?",
										"Eliminar", "Cancelar", new ConfirmDialog.Listener() {

									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											Holiday holiday = ((BeanItem<Holiday>)holidayContainer.getItem(itemId)).getBean();
											service.resetHoliday(new DateTime(holiday.getDate()));
											service.delete(holiday);																			
											holidayContainer.removeItem(itemId);
										}
									}
								});
							}
						}){{setIcon(FontAwesome.TRASH_O);}};
					}
				});
				
				setVisibleColumns("name","date","delete");
				setColumnHeaders("Nombre","Fecha","Eliminar");
				setPageLength(4);
			}
			
			@Override
		    protected String formatPropertyValue(Object rowId,
		            Object colId, Property property) {
		        // Format by property type
		        if (property.getType() == Date.class) {
		        	DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
		        	String d = formatter.format(property.getValue());
		        	return d;		        	
		        }

		        return super.formatPropertyValue(rowId, colId, property);
		    }
		};	
		
		vl.addComponent(table);
		vl.setExpandRatio(table, 1.0F);

		Button btnAdd = new Button(null,FontAwesome.PLUS);
		hl.addComponent(btnAdd);
		btnAdd.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try{
					if(nombre.getValue() == "" || fecha.getValue() == null){
						Notification.show("Debe ingresar tanto el nombre como la fecha del nuevo feriado.",Type.ERROR_MESSAGE);
						return;
					}else if(service.findExistingDate(fecha.getValue()) != null){
						logger.debug("DDD: "+service.findExistingDate(fecha.getValue()).toString());
						Notification.show("La fecha ya ha sido registrada.",Type.ERROR_MESSAGE);
						return;
					}else{
						Holiday h = new Holiday();
						h.setName(nombre.getValue());
						h.setDate(fecha.getValue());
						service.save(h);
						holidayContainer.addBean(h);
						
						nombre.setValue("");
						fecha.setValue(null);						
					}
				}catch(Exception e){
					Notification.show("Error al quitar elemento",Type.ERROR_MESSAGE);
					logger.error("Error al quitar elemento",e);
				}
			}
		});		
		
		vl.setComponentAlignment(hl, Alignment.TOP_RIGHT);

		return vl;
	}
	
	
	protected VerticalLayout drawBancos() {
		
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		
		List<Bank> b = confService.findBank();
		bankContainer = new BeanItemContainer<Bank>(Bank.class, b);

		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setSpacing(true);		
		vl.addComponent(hl);

		final TextField nombre = new TextField("Nombre Banco");
		hl.addComponent(nombre);

		final Table table = new Table(){
			{
				setWidth("100%");
				setContainerDataSource(bankContainer);
				setTableFieldFactory(new DefaultFieldFactory(){

					public Field<?> createField(final Container container,
							final Object itemId,Object propertyId,com.vaadin.ui.Component uiContext) {
						Field<?> field = null; 
						if( propertyId.equals("name")){
							field = new TextField();
							((TextField)field).setNullRepresentation("");
						}
						else {
							return null;
						}
						return field;
					}
				});
				
				addGeneratedColumn("delete", new Table.ColumnGenerator() {
					
					@Override
					public Object generateCell(Table source, final Object itemId, Object columnId) {
						return new Button(null,new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar el banco seleccionado?",
										"Eliminar", "Cancelar", new ConfirmDialog.Listener() {

									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											Bank banco = ((BeanItem<Bank>)bankContainer.getItem(itemId)).getBean();
											confService.delete(banco);																			
											bankContainer.removeItem(itemId);
										}
									}
								});
							}
						}){{setIcon(FontAwesome.TRASH_O);}};
					}
				});
				
				setVisibleColumns("name","delete");
				setColumnHeaders("Nombre","Eliminar");
				setPageLength(6);
			}
			
			@Override
		    protected String formatPropertyValue(Object rowId,
		            Object colId, Property property) {
		        // Format by property type
		        if (property.getType() == Date.class) {
		        	DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
		        	String d = formatter.format(property.getValue());
		        	return d;		        	
		        }

		        return super.formatPropertyValue(rowId, colId, property);
		    }
		};	
		
		vl.addComponent(table);
		vl.setExpandRatio(table, 1.0F);

		Button btnAdd = new Button(null,FontAwesome.PLUS);
		hl.addComponent(btnAdd);
		btnAdd.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try{
					if(nombre.getValue() == ""){
						Notification.show("Debe ingresar el nombre del nuevo banco.",Type.ERROR_MESSAGE);
						return;
					}else{
						Bank b = new Bank();
						b.setName(nombre.getValue());
						confService.save(b);
						bankContainer.addBean(b);
						
						nombre.setValue("");
					}
				}catch(Exception e){
					Notification.show("Error al quitar elemento.",Type.ERROR_MESSAGE);
					logger.error("Error al quitar elemento",e);
				}
			}
		});		
		
		vl.setComponentAlignment(hl, Alignment.TOP_RIGHT);

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
