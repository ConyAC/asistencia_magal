package cl.magal.asistencia.ui.config;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.VaadinView;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.DateField;
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

	public static final String NAME = "configuraciones";


	public ConfigView(){


		//define tab para poner cada tipo de configuración
		TabSheet ts = new TabSheet();
		ts.setSizeFull();
		addComponent(ts);

		ts.addTab(drawMensuales(),"Fechas Mensuales",FontAwesome.REBEL);

		ts.addTab(drawGlobales(),"Configuraciones",FontAwesome.GLOBE);

		ts.addTab(drawAFP(),"Tabla de Afps y Seguros",FontAwesome.MONEY);

		ts.addTab(drawAnticipos(),"Table de Anticipos",FontAwesome.MONEY);
		//agrega cada tab
		ts.addTab(drawFeriados(), "Feriados", FontAwesome.CALENDAR);
		

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
			}
		};
	}

	private com.vaadin.ui.Component drawAFP() {
		return new VerticalLayout(){
			{
				setMargin(true);
				addComponent(new Table("Tabla AFP"){
					{
						int i = 1;
						setSizeFull();
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

					}
				});
			}
		};
	}

	private com.vaadin.ui.Component drawGlobales() {
		return new VerticalLayout(){
			{
				setSizeFull();
				setSpacing(true);
				setMargin(true);
				FormLayout form = new FormLayout(){
					{
						setSizeUndefined();
						addComponent(new TextField("Sueldo Mínimo"));
						addComponent(new Label("Jornal base : #"));
						addComponent(new Label("Gratificación : #"));
						addComponent(new Label("<hr />",ContentMode.HTML){
							{
								setWidth("100%");
							}
						});
						addComponent(new TextField("Movilización 1"));
						addComponent(new TextField("Colación"));
						addComponent(new TextField("Impuestos por rango de ganancia"));
						addComponent(new TextField("Asignación Familiar"));

					}
				};
				addComponent(form);
				setComponentAlignment(form, Alignment.MIDDLE_CENTER);
			}
		};
	}


	private com.vaadin.ui.Component drawMensuales() {
		return new HorizontalLayout(){
			{
				setSizeFull();
				setSpacing(true);
				setMargin(true);
				
//				setComponentAlignment(mes, Alignment.MIDDLE_CENTER);
				FormLayout form = new FormLayout(){
					{
						setSizeUndefined();
						InlineDateField  mes = new InlineDateField("Mes");
						mes.setResolution(Resolution.MONTH);
						mes.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								
								Notification.show("event "+event.getProperty().getValue());
							}
						});
						addComponent(mes);
						addComponent(new DateField("Fecha Cierre Anticipo"));
						addComponent(new DateField("Fecha Cierre Asistencia"));
						addComponent(new DateField("Fecha Inicio Trato"));
						addComponent(new DateField("Fecha Fin Trato"));
					}
				};
				addComponent(form);
				setComponentAlignment(form, Alignment.MIDDLE_CENTER);
			}
		};

	}


	private com.vaadin.ui.Component drawFeriados() {
		VerticalLayout vl = new VerticalLayout();
		
		vl.setMargin(true);
		
		Calendar calendar = new Calendar();
		calendar.setLocale(Locale.getDefault());
		calendar.setImmediate(true);
		calendar.setSizeFull();

		GregorianCalendar gregorianCalendar = new GregorianCalendar(calendar.getLocale());

		//        final int rollAmount = gregorianCalendar
		//                .get(GregorianCalendar.DAY_OF_MONTH) - 1;
		gregorianCalendar.set(GregorianCalendar.DAY_OF_MONTH, 1);
		//        resetTime(false);
		Date currentMonthsFirstDate = gregorianCalendar.getTime();
		calendar.setStartDate(currentMonthsFirstDate);
		gregorianCalendar.add(GregorianCalendar.MONTH, 1);
		gregorianCalendar.add(GregorianCalendar.DATE, -1);
		calendar.setEndDate(gregorianCalendar.getTime());
//
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeFull();
		hl.addComponent(calendar);
		VerticalLayout form = new VerticalLayout(){
			{
				addComponent(new TextField("Nombre Feriado"));
			}
		};
		hl.addComponent(form);
		hl.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
		hl.setExpandRatio(form, 1.0F);
		
		vl.addComponent(hl);
		
		return vl;
	}


	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
