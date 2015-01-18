package cl.magal.asistencia.ui.constructionsite;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tepi.filtertable.FilterTable;

import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.Team;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class AddTeamDialog extends AbstractWindowEditor {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3992470917312354308L;
	transient List<Laborer> laborers;
	transient Logger logger = LoggerFactory.getLogger(AddTeamDialog.class);

	transient LaborerService laborerService;

	public AddTeamDialog(BeanItem<?> item) {
		super(item);
		init();
	}
	
	public void init(){
		
		setWidth("50%");
		setHeight("300px");
		laborerService = (LaborerService) ((MagalUI)UI.getCurrent()).getSpringBean(Constants.LABORER_SERVICE_BEAN);
		super.init();
	}

	@Override
	protected Component createBody() {
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setSpacing(true);
		hl.setMargin(true);
		
		VerticalLayout detalleCuadrilla = new VerticalLayout();
		detalleCuadrilla.setSpacing(true);
		hl.addComponent(detalleCuadrilla);
		hl.setExpandRatio(detalleCuadrilla, .3F);
		
		TextField tf = (TextField) buildAndBind("Nombre Cuadrilla", "name");
		tf.setNullRepresentation("");
		detalleCuadrilla.addComponent(tf);
		
		laborers = laborerService.getAllLaborer(((Team)getItem().getBean()).getConstructionSite());
		ComboBox cbLeader = new ComboBox("Responsable",new BeanItemContainer<Laborer>(Laborer.class,laborers));
		cbLeader.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbLeader.setItemCaptionPropertyId("fullname");
		bind(cbLeader, "leader");
		detalleCuadrilla.addComponent(cbLeader);
		
		//Seleccionar Obreros
		final FilterTable laborersTeamTable =  new FilterTable();
		laborersTeamTable.setWidth("100%");
		laborersTeamTable.setContainerDataSource(new BeanItemContainer<Laborer>(Laborer.class));
		bind(laborersTeamTable, "laborers");

		VerticalLayout laborerCuadrilla = new VerticalLayout();
		laborerCuadrilla.setSpacing(true);
		hl.addComponent(laborerCuadrilla);
		hl.setExpandRatio(laborerCuadrilla, .7F);
		
		final ComboBox cb = new ComboBox("Rut trabajador:",new BeanItemContainer<Laborer>(Laborer.class,laborers));
		cb.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cb.setItemCaptionPropertyId("rut");
		
		//agrega un trabajador a la cuadrilla
		final Button add = new Button(null,new Button.ClickListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					((BeanItemContainer<Laborer>) laborersTeamTable.getContainerDataSource()).addBean((Laborer) cb.getValue());
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
		laborersTeamTable.setColumnHeaders("Nombre", "Acciones");
		laborersTeamTable.setSelectable(true);
		return hl;
	}

}
