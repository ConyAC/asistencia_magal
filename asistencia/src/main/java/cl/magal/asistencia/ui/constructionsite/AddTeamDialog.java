package cl.magal.asistencia.ui.constructionsite;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tepi.filtertable.FilterTable;

import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.Team;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.ui.AbstractWindowEditor;
import cl.magal.asistencia.ui.MagalUI;
import cl.magal.asistencia.util.Constants;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class AddTeamDialog extends AbstractWindowEditor {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3992470917312354308L;
	transient List<LaborerConstructionsite> laborersConstructionsite;
	transient Logger logger = LoggerFactory.getLogger(AddTeamDialog.class);

	transient LaborerService laborerService;
	transient ConstructionSiteService constructionSiteService;
	BeanItemContainer<Laborer> laborerTeamContainer;
	ComboBox cbLeader;

	public AddTeamDialog(BeanItem<?> item) {
		super(item);
		init();
	}
	
	public void init(){
		
		setWidth("50%");
		laborerService = (LaborerService) ((MagalUI)UI.getCurrent()).getSpringBean(Constants.LABORER_SERVICE_BEAN);
		constructionSiteService = (ConstructionSiteService) ((MagalUI)UI.getCurrent()).getSpringBean(Constants.CONSTRUCTIONSITE_SERVICE_BEAN);
		super.init();
	}

	@Override
	protected Component createBody() {
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
//		hl.setSizeFull();
		hl.setSpacing(true);
		hl.setMargin(true);
		
		VerticalLayout detalleCuadrilla = new VerticalLayout();
		detalleCuadrilla.setSpacing(true);
		hl.addComponent(detalleCuadrilla);
		hl.setExpandRatio(detalleCuadrilla, .3F);
		
		TextField tf = (TextField) buildAndBind("Nombre Cuadrilla", "name");
		tf.setNullRepresentation("");
		detalleCuadrilla.addComponent(tf);
		
//		laborersConstructionsite = laborerService.getAllLaborer(((Team)getItem().getBean()).getConstructionSite());
		laborersConstructionsite = constructionSiteService.getLaborerByConstruction(((Team)getItem().getBean()).getConstructionSite());
		cbLeader = new ComboBox("Responsable",new BeanItemContainer<LaborerConstructionsite>(LaborerConstructionsite.class,laborersConstructionsite));
		cbLeader.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbLeader.setItemCaptionPropertyId("jobCode");
		
		Laborer leader = (Laborer) getItem().getItemProperty("leader").getValue();
		//pre selecciona el lider si es distinto de null		
		if(leader != null)
			for(Object itemId : cbLeader.getItemIds()){
				LaborerConstructionsite lc = ((LaborerConstructionsite)itemId);
				if(lc.getLaborer().getLaborerId() == leader.getLaborerId() ){
					cbLeader.setValue(lc);
					break;
				}
			}
//		bind(cbLeader, "leader");
		detalleCuadrilla.addComponent(cbLeader);
		
		//Seleccionar Obreros
		
		List<Laborer> laborersTeam = (List<Laborer>) getItem().getItemProperty("laborers").getValue();
		laborerTeamContainer =  new BeanItemContainer<Laborer>(Laborer.class,laborersTeam);
		
		final FilterTable laborersTeamTable =  new FilterTable();
		laborersTeamTable.setPageLength(6);
		laborersTeamTable.setWidth("100%");
		laborersTeamTable.setContainerDataSource(laborerTeamContainer);

		VerticalLayout laborerCuadrilla = new VerticalLayout();
		laborerCuadrilla.setSpacing(true);
		hl.addComponent(laborerCuadrilla);
		hl.setExpandRatio(laborerCuadrilla, .7F);
		
		
		final ComboBox cb = new ComboBox("Código trabajador:",new BeanItemContainer<LaborerConstructionsite>(LaborerConstructionsite.class,laborersConstructionsite));
		cb.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cb.setItemCaptionPropertyId("jobCode");
		
		//agrega un trabajador a la cuadrilla
		final Button add = new Button(null,new Button.ClickListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					LaborerConstructionsite laborerConstructionsite = (LaborerConstructionsite) cb.getValue();
					if(laborerTeamContainer.containsId(laborerConstructionsite.getLaborer())){
						Notification.show("El trabajador ya está agregado a la cuadrilla",Type.ERROR_MESSAGE);
						return;
					}
					
					laborerTeamContainer.addBean(laborerConstructionsite.getLaborer());
				} catch (Exception e) {
					logger.error("Error al guardar la información de la cuadrilla",e);
					Notification.show("Es necesario agregar todos los campos obligatorios", Type.ERROR_MESSAGE);
				}
			}
		}){/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		{
			setIcon(FontAwesome.PLUS_CIRCLE);
		}};

		//agrega el combobox y el boton
		laborerCuadrilla.addComponent(new HorizontalLayout(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				addComponent(cb);
				addComponent(add);
				setComponentAlignment(add, Alignment.BOTTOM_LEFT);
			}
		});
		
		laborerCuadrilla.addComponent(laborersTeamTable);

		laborersTeamTable.setSizeFull();
		laborersTeamTable.setFilterBarVisible(true);
		laborersTeamTable.addGeneratedColumn("my_select", new CustomTable.ColumnGenerator() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(CustomTable source, final Object itemId,Object columnId) {
				
				return new Button(null,new Button.ClickListener() {
					
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {
						laborersTeamTable.getContainerDataSource().removeItem(itemId);
					}
				}){/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

				{setIcon(FontAwesome.TRASH_O);}};
			}
		});

		laborersTeamTable.setVisibleColumns("fullname","my_select");
		laborersTeamTable.setColumnHeaders("Nombre", "Eliminar");
		laborersTeamTable.setColumnWidth("my_select", 100);
		laborersTeamTable.setSelectable(true);
		return new Panel(hl){{setSizeFull();}};
	}
	
	//despues de la validación agrega las etapas
	@Override
	protected boolean preCommit() {
		//antes de guardar recupera la información de los fields
		List<Laborer> laborersTeam = (List<Laborer>) getItem().getItemProperty("laborers").getValue();
		laborersTeam.clear();
		for(Laborer lab : laborerTeamContainer.getItemIds()){
			if(lab == null){
				Notification.show("No se permiten etapas vacias",Type.ERROR_MESSAGE);
				return false;
			}
			laborersTeam.add(lab);
		}
		//agrega el valore seleccionado para el lider
//		if(cbLeader.getValue() != null)
		LaborerConstructionsite leaderConstruction =  (LaborerConstructionsite) cbLeader.getValue();
		Laborer leader = null;
		if( leaderConstruction != null )
			leader = leaderConstruction.getLaborer();
		getItem().getItemProperty("leader").setValue(leader);
		
		return true;
	}

}
