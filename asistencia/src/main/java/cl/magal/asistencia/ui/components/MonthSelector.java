package cl.magal.asistencia.ui.components;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class MonthSelector extends CustomComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3874010772284274530L;
	
	BeanItem<MonthSelectorItem> item;

	public MonthSelector() {
		
		item = new BeanItem<MonthSelectorItem>(new MonthSelectorItem(),MonthSelectorItem.class);
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		setHeight("100%");
		setWidth("390px");
		setCompositionRoot(hl);
		
		final TextField tfYear = new TextField();
		tfYear.setConverter(new StringToIntegerConverter() {
		    protected java.text.NumberFormat getFormat(Locale locale) {
		        NumberFormat format = super.getFormat(locale);
		        format.setGroupingUsed(false);
		        return format;
		    }
		});
		
		final ComboBox cbMonth = new ComboBox();
		
		final Button leftYear = new Button(FontAwesome.ANGLE_DOUBLE_LEFT);
		leftYear.addStyleName("link");
		leftYear.addClickListener(new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				int year = item.getBean().getYear();
				tfYear.setValue((year - 1)+"");
				
			}
			
		});
		final Button leftMonth = new Button(FontAwesome.ANGLE_LEFT);
		leftMonth.addStyleName("link");
		leftMonth.addClickListener(new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				int month = item.getBean().getMonth();
				cbMonth.select((month - 1));
			}
			
		});
		
		cbMonth.setWidth("140px");
		cbMonth.setImmediate(true);
		cbMonth.addItem(0);cbMonth.setItemCaption(0,"Enero");
		cbMonth.addItem(1);cbMonth.setItemCaption(1,"Febrero");
		cbMonth.addItem(2);cbMonth.setItemCaption(2,"Marzo");
		cbMonth.addItem(3);cbMonth.setItemCaption(3,"Abril");
		cbMonth.addItem(4);cbMonth.setItemCaption(4,"Mayo");
		cbMonth.addItem(5);cbMonth.setItemCaption(5,"Junio");
		cbMonth.addItem(6);cbMonth.setItemCaption(6,"Julio");
		cbMonth.addItem(7);cbMonth.setItemCaption(7,"Agosto");
		cbMonth.addItem(8);cbMonth.setItemCaption(8,"Septiembre");
		cbMonth.addItem(9);cbMonth.setItemCaption(9,"Octubre");
		cbMonth.addItem(10);cbMonth.setItemCaption(10,"Noviembre");
		cbMonth.addItem(11);cbMonth.setItemCaption(11,"Diciembre");
		
		cbMonth.setPropertyDataSource(item.getItemProperty("month"));
		
		tfYear.setWidth("60px");
		tfYear.setImmediate(true);
		tfYear.setPropertyDataSource(item.getItemProperty("year"));
		
		final Button rightMonth = new Button(FontAwesome.ANGLE_RIGHT);
		rightMonth.addStyleName("link");
		rightMonth.addClickListener(new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				int month = item.getBean().getMonth();
				cbMonth.select((month + 1));
			}
			
		});
		final Button rightYear = new Button(FontAwesome.ANGLE_DOUBLE_RIGHT);
		rightYear.addStyleName("link");
		rightYear.addClickListener(new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				int year = item.getBean().getYear();
				tfYear.setValue((year + 1)+"");
			}
			
		});
		
		hl.addComponent(new HorizontalLayout(){
			{
				addComponent(leftYear);
				addComponent(leftMonth);
			}
		});
		HorizontalLayout hl2 = new HorizontalLayout();
		hl2.addComponent(cbMonth);
		hl2.addComponent(tfYear);
		hl.addComponent(hl2);
		hl.setExpandRatio(hl2, 1.0f);
		
		hl.addComponent(new HorizontalLayout(){
			{
				addComponent(rightMonth);
				addComponent(rightYear);
			}
		});
	}
	
	public String getMonth(){
		MonthSelectorItem bean = item.getBean();
		return String.format("%02d", (bean.month + 1)) + bean.year + "";
	}

}
