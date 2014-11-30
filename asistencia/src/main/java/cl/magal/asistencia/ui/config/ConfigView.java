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
import com.vaadin.ui.Calendar;
import com.vaadin.ui.TabSheet;
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
		
	}
	
	
	private com.vaadin.ui.Component drawFeriados() {
		VerticalLayout vl = new VerticalLayout();
		
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
