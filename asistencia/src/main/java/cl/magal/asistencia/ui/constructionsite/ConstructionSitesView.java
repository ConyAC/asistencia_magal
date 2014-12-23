package cl.magal.asistencia.ui.constructionsite;

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
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.ui.BaseView;
import cl.magal.asistencia.ui.MagalUI;
import cl.magal.asistencia.util.SecurityHelper;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
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
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@VaadinView(value=ConstructionSitesView.NAME,cached=false)
@Scope("prototype")
@Component
public class ConstructionSitesView extends BaseView implements View {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1455505684312459398L;

	private transient Logger logger = LoggerFactory.getLogger(ConstructionSitesView.class);

	public static final String NAME = "obras";

	BeanFieldGroup<ConstructionSite> bfg = new BeanFieldGroup<ConstructionSite>(ConstructionSite.class);
	BeanItemContainer<ConstructionSite> constructionContainer = new BeanItemContainer<ConstructionSite>(ConstructionSite.class);
	
	FilterTable table;
	VerticalLayout detalleLayout;

	@Autowired
	private transient ConstructionSiteService service;
	@Autowired
	private transient UserService userService;

	HorizontalLayout root,detailLayout;
	Panel panelConstructions;


	public ConstructionSitesView(){

		logger.debug("obras");

		setSizeFull();

		root = new HorizontalLayout();
		root.setSizeFull();
		setContent(root);

		//crea el panel de la información de obra
		panelConstructions = new Panel();
		panelConstructions.setSizeFull();
		
		//dibula la sección de las obras
		final VerticalLayout obras = drawObras();

		//rellena el panel de la información de obra
		panelConstructions.setContent(new HorizontalLayout(){
			{
				setSizeFull();
				setSpacing(true);
				setMargin(true);

				addComponent(obras);
				setExpandRatio(obras, 1F);
			}
		});

		root.addComponent(panelConstructions);


	}

	@PostConstruct
	private void init(){
		
	}

	private VerticalLayout drawObras() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);

		//agrega solo si tiene los permisos
		if( SecurityHelper.hastPermission(Permission.CREAR_OBRA,Permission.ELIMINAR_OBRA)){
		
			//botones agrega y eliminar
			HorizontalLayout hl = new HorizontalLayout();
			hl.setSpacing(true);
	
			vl.addComponent(hl);
			vl.setComponentAlignment(hl, Alignment.BOTTOM_RIGHT);
			Button agregaObra = new Button(null,FontAwesome.PLUS);
			//agregando obras dummy
			agregaObra.addClickListener(new Button.ClickListener() {
	
				/**
				 * 
				 */
				private static final long serialVersionUID = 3844920778615955739L;
	
				@Override
				public void buttonClick(ClickEvent event) {
					ConstructionSite obra = new ConstructionSite();
					obra.setName("Nueva Obra");
					service.save(obra);
					BeanItem<ConstructionSite> item = constructionContainer.addBean(obra);
//					setConstruction(item);
				}
			});
			hl.addComponent(agregaObra);
			Button borrarObra = new Button(null,FontAwesome.TRASH_O);
			borrarObra.addClickListener(new Button.ClickListener() {
	
				@Override
				public void buttonClick(ClickEvent event) {
					ConstructionSite cs = (ConstructionSite) table.getValue();
					if(cs == null){
						Notification.show("Debe seleccionar una obra para eliminar");
						return;
					}
					service.deleteCS(cs.getConstructionsiteId());
					constructionContainer.removeItem(cs);
//					setConstruction(null);
	
				}
			});
			hl.addComponent(borrarObra);
		}

		//la tabla con su buscador buscador
		vl.addComponent(drawConstructionTable());

		return vl;
	}

	private FilterTable drawConstructionTable() {
		table =  new FilterTable();
		table.setContainerDataSource(constructionContainer);
		table.setSizeFull();
		table.setFilterBarVisible(true);
		table.addGeneratedColumn("actions", new CustomTable.ColumnGenerator() {
			
			@Override
			public Object generateCell(CustomTable source, Object itemId,
					Object columnId) {
				return new Button(null,FontAwesome.TRASH_O);
			}
		});
		
		table.setVisibleColumns("name","status","actions");
		table.setColumnHeaders("Nombre","Estado","Acciones");
		table.setSelectable(true);


		table.addItemClickListener(new ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {
//				setConstruction((BeanItem<ConstructionSite>)event.getItem());
				BeanItem<ConstructionSite> bean = (BeanItem<ConstructionSite>)event.getItem();
				UI.getCurrent().getNavigator().navigateTo(ConstructionSiteView.NAME+"/"+bean.getBean().getConstructionsiteId());
			}
		});
		return table;
	}
//	
//	private VerticalLayout drawContructionDetail() {
//		detalleLayout = new VerticalLayout(); 
//		detalleLayout.setSizeFull();
//		detalleLayout.setSpacing(true);
//
//		//creando la parte de arriba
//		detailLayout = drawTopDetails();
//		detalleLayout.addComponent(detailLayout);
//		detalleLayout.setExpandRatio(detailLayout, 0.3F);
//
//		//crea el tab con trabajadores y cuadrillas
//		TabSheet tab = new TabSheet();
//		tab.setSizeFull();
//
//		detalleLayout.addComponent(tab);
//		detalleLayout.setExpandRatio(tab, 0.7F);
//		//tab de trabajadores
//		tab.addTab(drawLaborer(),"Trabajadores");
//
//		//tab de cuadrillas
//		tab.addTab(drawCuadrillas(),"Cuadrillas");
//
//		return detalleLayout;
//	}

	@Override
	public void enter(ViewChangeEvent event) {
		
		((MagalUI)UI.getCurrent()).setBackVisible(false);
		((MagalUI)UI.getCurrent()).highlightMenuItem(NAME);
		
		reloadData();
	}

	private void reloadData(){
		//agrega las obras TODO segun perfil TODO usar paginación
		Page<ConstructionSite> page = service.findAllConstructionSite(new PageRequest(0, 20));
		constructionContainer.removeAllItems();
		constructionContainer.addAll(page.getContent());

	}

}
