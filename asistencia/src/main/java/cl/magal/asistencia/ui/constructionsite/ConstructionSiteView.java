package cl.magal.asistencia.ui.constructionsite;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.ui.BaseView;
import cl.magal.asistencia.ui.MagalUI;
import ru.xpoft.vaadin.VaadinView;

@VaadinView(value=ConstructionSiteView.NAME,cached=false)
@Scope("prototype")
@Component
public class ConstructionSiteView extends BaseView  implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8616320162970295709L;

	private transient Logger logger = LoggerFactory.getLogger(ConstructionSiteView.class);

	public static final String NAME = "obra";

	@Autowired
	LaborerAndTeamPanel laborerAndTeamPanel;
	@Autowired
	AttendancePanel attendancePanel;

	@Autowired
	private transient ConstructionSiteService service;
	@Autowired
	private transient UserService userService;

	HorizontalLayout root;
	ConstructionSite cs;

	public ConstructionSiteView(){

		setSizeFull();
		root = new HorizontalLayout();
		root.setSizeFull();
		setContent(root);

	}

	Button.ClickListener backListener = new Button.ClickListener() {

		@Override
		public void buttonClick(ClickEvent event) {
			//retrocede si no se está mostrando  la asistencia
			if(root.getComponentIndex(laborerAndTeamPanel) >= 0){
				UI.getCurrent().getNavigator().navigateTo(ConstructionSitesView.NAME);
			}else{
				int indexOf = Page.getCurrent().getUriFragment().indexOf("asistencia");
				Page.getCurrent().setUriFragment(Page.getCurrent().getUriFragment().substring(0, indexOf), false);
				switchPanels();
			}

		}
	};

	@PostConstruct
	private void init(){

		laborerAndTeamPanel.setHasAttendanceButton(true);
		laborerAndTeamPanel.setHasConstructionDetails(false);
		root.addComponent(laborerAndTeamPanel);

		laborerAndTeamPanel.addAttendanceClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				String fragment = "asistencia";
				if(!Page.getCurrent().getUriFragment().endsWith("/"))
					fragment = "/"+fragment;
				Page.getCurrent().setUriFragment(Page.getCurrent().getUriFragment()+fragment, false);
				switchPanels();

			}
		});

		((MagalUI)UI.getCurrent()).getBackButton().addClickListener(backListener);
	}

	private void switchPanels(){
		switchPanels(null);
	}

	private void switchPanels(ViewChangeEvent event) {

		if(root.getComponentIndex(laborerAndTeamPanel) >= 0){
			root.removeComponent(laborerAndTeamPanel);
			root.addComponent(attendancePanel);
			attendancePanel.enter(event);
		}else{
			root.removeComponent(attendancePanel);
			root.addComponent(laborerAndTeamPanel);
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {

		((MagalUI)UI.getCurrent()).setBackVisible(true);
		((MagalUI)UI.getCurrent()).highlightMenuItem(ConstructionSitesView.NAME);

		//obtiene los parametros de url
		if(event.getParameters() != null){
			// split at "/", add each part as a label
			String[] msgs = event.getParameters().split("/");
			//si no trae parametros, entonces avisa y deshabilita la interfaz
			if(msgs == null || msgs.length == 0 ){
				showErrorParam();
				return;
			}
			//si trae parametro verifica que sea un numero valido
			try{

				Long id = Long.valueOf(msgs[0]);

				//verifica los parametros de la url
				if( msgs.length >= 2 ){
					String function = msgs[1];
					reloadData(id);
					if(function.equals("asistencia")){
						switchPanels(event);
					}else{
						showErrorParam();
					}

				}else if( msgs.length >= 1 ){
					//si todo va bien, carga la información de la obra
					reloadData(id);
				}
			}catch(NumberFormatException e){
				showErrorParam();
				return;
			}

		}else{
			showErrorParam();
		}

	}

	private void reloadData(Long id){

		//busca la información de la obra
		cs = service.findConstructionSite(id);
		if(cs == null ){
			showErrorParam();
			return;
		}
		//setea el nombre de la construccion en el titulo
		((MagalUI)UI.getCurrent()).getTitle().setValue("<h1>"+cs.getName()+"</h1>");

		laborerAndTeamPanel.setConstruction( new BeanItem<ConstructionSite>(cs) );
		attendancePanel.setCs(cs);
	}

	private void showErrorParam() {
		Notification.show("Debe seleccionar una obra ",Type.ERROR_MESSAGE);
		//setea el titulo como vacio
		((MagalUI)UI.getCurrent()).getTitle().setValue("");
		root.setEnabled(false);		
	}

}
