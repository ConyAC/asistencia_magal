package cl.magal.asistencia.ui.config;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.VaadinView;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Slider;
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
		
		//agrega cada tab
		ts.addTab(drawFeriados(), "Feriados", FontAwesome.CALENDAR);
		
		ts.addTab(drawMensuales(),"Configuraciones Mensuales",FontAwesome.REBEL);
		
		ts.addTab(drawGlobales(),"Configuraciones",FontAwesome.GLOBE);
		
		ts.addTab(drawAFP(),"Afps y Seguros",FontAwesome.MONEY);
		
	}
	
	private com.vaadin.ui.Component drawAFP() {
		return new VerticalLayout(){
			{
				setMargin(true);
				addComponent(new Table("AFP"){
					{
						int i = 1;
						
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
		return new FormLayout(){
			{
				setSizeFull();
				setSpacing(true);
				setMargin(true);
				addComponent(new TextField("Sueldo Mínimo"));
				addComponent(new Label("Jornal base : #"));
				addComponent(new Label("Gratificación : #"));
				addComponent(new Label("<hr />",ContentMode.HTML));
				addComponent(new TextField("Movilización 1"));
				addComponent(new TextField("Colación"));
				addComponent(new TextField("Impuestos por rango de ganancia"));
				addComponent(new TextField("Asignación Familiar"));
				
			}
		};
	}


	private com.vaadin.ui.Component drawMensuales() {
		return new FormLayout(){
			{
				setSizeFull();
				setSpacing(true);
				setMargin(true);
				addComponent(new DateField("Mes"));
				addComponent(new DateField("Fecha Cierre Anticipo"));
				addComponent(new DateField("Fecha Cierre Anticipo"));
				
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
        
        
        vl.addComponent(calendar);
		return vl;
	}


	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
