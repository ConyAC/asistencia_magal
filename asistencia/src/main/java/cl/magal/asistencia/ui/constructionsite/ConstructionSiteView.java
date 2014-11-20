package cl.magal.asistencia.ui.constructionsite;

import java.util.List;

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
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.enums.Status;
import cl.magal.asistencia.services.ConstructionSiteHelper;
import cl.magal.asistencia.services.LaborerHelper;
import cl.magal.asistencia.services.ObrasService;

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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@VaadinView(value=ConstructionSiteView.NAME,cached=false)
@Scope("prototype")
@Component
public class ConstructionSiteView extends Panel  implements View {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8616320162970295709L;
	
	private transient Logger logger = LoggerFactory.getLogger(ConstructionSiteView.class);
	
	public static final String NAME = "obras";
	
	BeanFieldGroup<ConstructionSite> bfg = new BeanFieldGroup<ConstructionSite>(ConstructionSite.class);
	BeanItemContainer<ConstructionSite> constructionContainer = new BeanItemContainer<ConstructionSite>(ConstructionSite.class);
	BeanItemContainer<Laborer> laborerContainer = new BeanItemContainer<Laborer>(Laborer.class);
	
	FilterTable table;
	
	@Autowired
	private transient ObrasService service;
	
	
	public ConstructionSiteView(){
		
		logger.debug("obras");
		
		setSizeFull();
		
		HorizontalLayout root = new HorizontalLayout();
		root.setSizeFull();
		root.setSpacing(true);
		root.setMargin(true);
		
		setContent(root);
		
		//dibula la sección de las obras
		VerticalLayout obras = drawObras();
		
		root.addComponent(obras);
		root.setExpandRatio(obras, 0.2F);
		
		//dibuja la sección de detalles
		VerticalLayout detalleObra = drawContructionDetail();
		
		root.addComponent(detalleObra);
		root.setExpandRatio(detalleObra, 0.8F);
		
	}

	@PostConstruct
	private void init(){
	}
	
	private VerticalLayout drawObras() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		
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
				//FIXME SOLO PARA TEST
				ConstructionSite obra = ConstructionSiteHelper.newConstrutionSite();
				service.save(obra);
				constructionContainer.addBean(obra);
			}
		});
		hl.addComponent(agregaObra);
		Button borrarObra = new Button(null,FontAwesome.TRASH_O);
		hl.addComponent(borrarObra);

		//la tabla con su buscador buscador
		vl.addComponent(drawConstructionTable());
		
		return vl;
	}
	
	private FilterTable drawConstructionTable() {
	    table =  new FilterTable();
		table.setContainerDataSource(constructionContainer);
		table.setSizeFull();
		table.setFilterBarVisible(true);
		table.setVisibleColumns("name");
		table.setColumnHeaders("Nombre");
		table.setSelectable(true);
		
		table.addItemClickListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				setConstruction((BeanItem<ConstructionSite>)event.getItem());
			}
		});
		return table;
	}

	private VerticalLayout drawContructionDetail() {
		VerticalLayout vl = new VerticalLayout(); 
		vl.setSizeFull();
		vl.setSpacing(true);
		
		//creando la parte de arriba
		HorizontalLayout hl = drawTopDetails();
		vl.addComponent(hl);
		vl.setExpandRatio(hl, 0.2F);
		
		//crea el tab con trabajadores y cuadrillas
		TabSheet tab = new TabSheet();
		tab.setSizeFull();
		
		vl.addComponent(tab);
		vl.setExpandRatio(tab, 0.8F);
		//tab de trabajadores
		tab.addTab(drawLaborer(),"Trabajadores");
		
		//tab de cuadrillas
		tab.addTab(drawCuadrillas(),"Cuadrillas");
		
		return vl;
	}

	private VerticalLayout drawCuadrillas() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		vl.setSpacing(true);
		vl.setMargin(true);
		
		FilterTable table =  new FilterTable();
		table.setContainerDataSource(constructionContainer);
		table.setSizeFull();
		table.setFilterBarVisible(true);
		table.setVisibleColumns("constructionsiteId","name");
		table.setSelectable(true);
		
		vl.addComponent(table);
		
		return vl;
	}

	private VerticalLayout drawLaborer() {
		
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();
		
		//boton para agregar trabajadores e imprimir
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		
		vl.addComponent(hl);
		vl.setComponentAlignment(hl, Alignment.TOP_RIGHT);
		
		Button btnPrint = new Button(null,FontAwesome.PRINT);
		hl.addComponent(btnPrint);
		
		btnPrint.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show("Imprimiendo");
				
			}
		});
		
		Button btnAdd = new Button(null,FontAwesome.PLUS);
		hl.addComponent(btnAdd);
		
		btnAdd.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				ConstructionSite cs = (ConstructionSite) table.getValue();
				if(cs == null){
					Notification.show("Debe seleccionar una obra",Type.ERROR_MESSAGE);
					return;
				}
				
				Laborer laborer = LaborerHelper.newLaborer();
				service.addLaborer(laborer,cs);
				
				laborerContainer.addBean(laborer);
				
			}
		});
		
		FilterTable table =  new FilterTable();
		table.setContainerDataSource(laborerContainer);
		table.setSizeFull();
		table.setFilterBarVisible(true);
		//TODO estado
		table.setVisibleColumns("job","firstname","laborerId"); //FIXME laborerId
		table.setColumnHeaders("Cod","Nombre","Estado");
		table.setSelectable(true);
		
		vl.addComponent(table);
		vl.setExpandRatio(table,1.0F);
		
		return vl;
	}
	
	private void setConstruction(BeanItem<ConstructionSite> item){
		if(item == null )
			return;
		
		bfg.setItemDataSource(item);
		List<Laborer> laborers = service.getLaborerByConstruction(item.getBean());
		laborerContainer.removeAllItems();
		laborerContainer.addAll(laborers);
	}

	private HorizontalLayout drawTopDetails() {
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeFull();
		hl.setSpacing(true);
		VerticalLayout vlInfo =new VerticalLayout();
		vlInfo.setSpacing(true);
		hl.addComponent(vlInfo);
		
		final TextField nameField = new TextField();
		bfg.bind(nameField, "name");
		vlInfo.addComponent(new HorizontalLayout(){{
			addComponent(nameField);
			 //agrega un boton que hace el commit
	        Button add = new Button(null,new Button.ClickListener() {

	        	@Override
	        	public void buttonClick(ClickEvent event) {
	        		try {
	        			bfg.commit();
	        			service.save(bfg.getItemDataSource().getBean());
	        		} catch (CommitException e) {
	        			logger.error("Error al guardar la información la obra",e);
	        			Notification.show("Error al guardar la información del usuario", Type.ERROR_MESSAGE);
	        		}

	        	}
	        }){{
	        	setIcon(FontAwesome.SAVE);
	        }};
	        addComponent(add);
	        setComponentAlignment(add, Alignment.TOP_RIGHT);
		}});
		
		TextField addressField = new TextField();
		bfg.bind(addressField, "address");
		vlInfo.addComponent(addressField);
		
		ComboBox statusField = new ComboBox();
		for(Status s : Status.values()){
			statusField.addItem(s);
		}
		
		bfg.bind(statusField, "status");
		
		vlInfo.addComponent(statusField);
		
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
		
		Button configuraciones = new Button("Configuraciones Obra",FontAwesome.GEARS);
		configuraciones.setSizeFull();
		vlIBotones.addComponent(configuraciones);
		
		return hl;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		reloadData();
	}
	
	private void reloadData(){
		//agrega las obras TODO segun perfil TODO usar paginación
		Page<ConstructionSite> page = service.findAllConstructionSite(new PageRequest(0, 20));
		constructionContainer.removeAllItems();
		constructionContainer.addAll(page.getContent());
		//agrea los trabajadores asociados a la obra TODO segun la obra seleccionada
		//si no es vacia
		if(!page.getContent().isEmpty()){
			ConstructionSite fisrt = page.getContent().get(0);
			Page<Laborer> laborerPage = service.findLaborerByConstruction(fisrt);
		}
	}

}
