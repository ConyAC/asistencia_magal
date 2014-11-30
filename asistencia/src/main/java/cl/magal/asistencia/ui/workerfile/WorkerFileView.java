package cl.magal.asistencia.ui.workerfile;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.FilterTable;

import ru.xpoft.vaadin.VaadinView;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.services.LaborerService;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;

@VaadinView(value=WorkerFileView.NAME)
@Scope("prototype")
@Component
public class WorkerFileView extends HorizontalLayout implements View {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7438608047589376668L;
	
	private transient Logger logger = LoggerFactory.getLogger(WorkerFileView.class);
	
	public static final String NAME = "fichas";
	
	BeanItemContainer<Laborer> laborerContainer = new BeanItemContainer<Laborer>(Laborer.class);
	
	@Autowired
	private transient LaborerService service;

	VerticalLayout detalleObrero;
	FilterTable tableObrero;
	
	public WorkerFileView(){
		
		setSizeFull();
		VerticalLayout obreros = drawObreros();		
		addComponent(obreros);
		setExpandRatio(obreros, 0.2F);
		
		detalleObrero = drawDetalleObrero();		
		addComponent(detalleObrero);
		setExpandRatio(detalleObrero, 0.8F);
	}
	
	private VerticalLayout drawDetalleObrero() {
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		vl.addComponent(new Label("Seleccione un obrero para ver su información"));
		
		return vl;
	}
	
	private void setLaborer(BeanItem<Laborer> laborerItem){
		
		//obtiene el vertical Layout
		detalleObrero.removeAllComponents();
		if(laborerItem == null){
			detalleObrero.addComponent(new Label("Seleccione un obrero para ver su información"));
			return;
		}
		
		final BeanFieldGroup<Laborer> fieldGroup = new BeanFieldGroup<Laborer>(Laborer.class);
        fieldGroup.setItemDataSource(laborerItem);

        //agrega un boton que hace el commit
        Button add = new Button(null,new Button.ClickListener() {

        	@Override
        	public void buttonClick(ClickEvent event) {
        		try {
        			fieldGroup.commit();
        			service.saveLaborer(fieldGroup.getItemDataSource().getBean());
        		} catch (CommitException e) {
        			logger.error("Error al guardar la información del obrero");
        			Notification.show("Error al guardar la información del obrero", Type.ERROR_MESSAGE);
        		}

        	}
        }){{
        	setIcon(FontAwesome.SAVE);
        }};
        detalleObrero.addComponent(add);
        detalleObrero.setComponentAlignment(add, Alignment.TOP_RIGHT);
        
        // Loop through the properties, build fields for them and add the fields
        // to this UI
        for (Object propertyId : fieldGroup.getUnboundPropertyIds()) {
        	if(propertyId.equals("laborerId"))
        		;
        	else
        		detalleObrero.addComponent(fieldGroup.buildAndBind(propertyId));        	
        }
	}
	
	private FilterTable drawTablaObreros() {
		tableObrero =  new FilterTable();
		laborerContainer.addNestedContainerProperty("rut");
		tableObrero.setContainerDataSource(laborerContainer);
		tableObrero.setSizeFull();
		tableObrero.setFilterBarVisible(true);
		tableObrero.setVisibleColumns("rut","firstname");
		tableObrero.setSelectable(true);
		
		tableObrero.addItemClickListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				setLaborer((BeanItem<Laborer>)event.getItem());
			}
		});
		
		return tableObrero;
	}
	
	private VerticalLayout drawObreros() {
		
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		
		//la tabla con su buscador buscador
		vl.addComponent(drawTablaObreros());
		//botones agrega y eliminar
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		vl.addComponent(hl);
		vl.setComponentAlignment(hl, Alignment.BOTTOM_CENTER );
		Button agregaObrero = new Button(null,FontAwesome.PLUS);
		//agregando obras dummy
		agregaObrero.addClickListener(new Button.ClickListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -5582668940084219150L;			

			@Override
			public void buttonClick(ClickEvent event) {
				
				Laborer l = new Laborer();
				l.setFirstname("Jesse");
				l.setLastname("Ward");
				l.setRut("1111111-1");
				
				service.saveLaborer(l);
				laborerContainer.addBean(l);				
			}
		});
		
		hl.addComponent(agregaObrero);
		Button borrarObrero = new Button(null,FontAwesome.TRASH_O);
		borrarObrero.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				//recupera el elemento seleccionado
				Laborer l = (Laborer) tableObrero.getValue();
				if(l == null){
					Notification.show("Debe seleccionar un obrero para eliminarlo");
					return;
				}
				//TODO dialogo de confirmación
				service.deleteLaborer(l.getLaborerId());
				laborerContainer.removeItem(l);
				
				setLaborer(null);
			}
		});
		hl.addComponent(borrarObrero);
		
		return vl;
	}
	
	@PostConstruct
	private void init(){
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		List<Laborer> l = service.findAllLaborer();
		laborerContainer.removeAllItems();
		laborerContainer.addAll(l);
		

	}
}
