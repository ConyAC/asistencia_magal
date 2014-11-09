package cl.magal.asistencia.ui.obras;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.FilterTable;

import ru.xpoft.vaadin.VaadinView;
import cl.magal.asistencia.entities.Obra;
import cl.magal.asistencia.services.ObrasService;
import cl.magal.asistencia.util.Utils;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

@VaadinView(value=ObrasView.NAME,cached=false)
@Scope("prototype")
@Component
public class ObrasView extends HorizontalLayout implements View {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8616320162970295709L;
	
	private transient Logger logger = LoggerFactory.getLogger(ObrasView.class);
	
	public static final String NAME = "obras";
	
	BeanItemContainer<Obra> container = new BeanItemContainer<Obra>(Obra.class);
	
	@Autowired
	private transient ObrasService service;
	
	
	public ObrasView(){
		
		logger.debug("obras");
		
		setSizeFull();
		
		//dibula la sección de las obras
		VerticalLayout obras = drawObras();
		
		addComponent(obras);
		setExpandRatio(obras, 0.2F);
		
		//dibuja la sección de detalles
		VerticalLayout detalleObra = drawDetalleObra();
		
		addComponent(detalleObra);
		setExpandRatio(detalleObra, 0.8F);
		
	}

	@PostConstruct
	private void init(){
	}
	
	private VerticalLayout drawObras() {
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		
		//la tabla con su buscador buscador
		vl.addComponent(drawTablaObras());
		//botones agrega y eliminar
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		vl.addComponent(hl);
		vl.setComponentAlignment(hl, Alignment.BOTTOM_CENTER );
		Button agregaObra = new Button(null,FontAwesome.PLUS);
		//agregando obras dummy
		agregaObra.addClickListener(new Button.ClickListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 3844920778615955739L;

			@Override
			public void buttonClick(ClickEvent event) {
				Obra obra = new Obra();
				obra.setNombre("Obra"+Utils.random());
				
				obra.setDireccion("Dire");
				service.saveObra(obra);
				recargarDatos();
			}
		});
		hl.addComponent(agregaObra);
		Button borrarObra = new Button(null,FontAwesome.TRASH_O);
		hl.addComponent(borrarObra);
		
		return vl;
	}
	
	private FilterTable drawTablaObras() {
		FilterTable table =  new FilterTable();
		table.setContainerDataSource(container);
		table.setSizeFull();
		table.setFilterBarVisible(true);
		table.setVisibleColumns("id","nombre");
		table.setSelectable(true);
		return table;
	}

	private VerticalLayout drawDetalleObra() {
		VerticalLayout vl = new VerticalLayout(); 
		vl.setSizeFull();
		vl.setMargin(true);
		
		//creando la parte de arriba
		HorizontalLayout hl = drawTopDetalles();
		vl.addComponent(hl);
		vl.setExpandRatio(hl, 0.2F);
		
		//crea el tab con trabajadores y cuadrillas
		TabSheet tab = new TabSheet();
		tab.setSizeFull();
		vl.addComponent(tab);
		vl.setExpandRatio(tab, 0.8F);
		//tab de trabajadores
		tab.addTab(drawTrabajadores(),"Trabajadores");
		
		//tab de cuadrillas
		tab.addTab(drawCuadrillas(),"Cuadrillas");
		
		return vl;
	}

	private VerticalLayout drawCuadrillas() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		
		FilterTable table =  new FilterTable();
		table.setContainerDataSource(container);
		table.setSizeFull();
		table.setFilterBarVisible(true);
		table.setVisibleColumns("id","nombre");
		table.setSelectable(true);
		
		vl.addComponent(table);
		
		return vl;
	}

	private VerticalLayout drawTrabajadores() {
		VerticalLayout vl = new VerticalLayout();
		
		vl.setSizeFull();
		
		FilterTable table =  new FilterTable();
		table.setContainerDataSource(container);
		table.setSizeFull();
		table.setFilterBarVisible(true);
		table.setVisibleColumns("id","nombre");
		table.setSelectable(true);
		
		vl.addComponent(table);
		
		return vl;
	}

	private HorizontalLayout drawTopDetalles() {
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeFull();
		hl.setSpacing(true);
		VerticalLayout vlInfo =new VerticalLayout();
		vlInfo.setSpacing(true);
		hl.addComponent(vlInfo);
		
		vlInfo.addComponent(new Label("Obra1"));
		vlInfo.addComponent(new Label("Dirección de la Obra"));
		vlInfo.addComponent(new Label("Estado Obra"));
		
		hl.addComponent(new Image());
		
		VerticalLayout vlIBotones =new VerticalLayout();
		vlIBotones.setSpacing(true);
		hl.addComponent(vlIBotones);
		hl.setComponentAlignment(vlIBotones, Alignment.TOP_RIGHT );
		
		Button asistencia = new Button("Asistencia",FontAwesome.CHECK);
		asistencia.setSizeFull();
		vlIBotones.addComponent(asistencia);
		Button vacaciones = new Button("Carga Masiva Vacaciones",FontAwesome.UPLOAD);
		vacaciones.setSizeFull();
		vlIBotones.addComponent(vacaciones);
		
		Button configuraciones = new Button("Configuraciones",FontAwesome.GEARS);
		configuraciones.setSizeFull();
		vlIBotones.addComponent(configuraciones);
		
		return hl;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		recargarDatos();
	}
	
	private void recargarDatos(){
		Page<Obra> page = service.findAllObra(new PageRequest(0, 20));
		container.removeAllItems();
		container.addAll(page.getContent());
	}

}
