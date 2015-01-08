package cl.magal.asistencia.ui.constructionsite;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.ui.AbstractWindowEditor;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

public class AddLaborerDialog extends AbstractWindowEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3250481772094615264L;
	
	Logger logger = LoggerFactory.getLogger(AddLaborerDialog.class);
	
	transient LaborerService laborerService;
	BeanItemContainer<Laborer> laborers = new BeanItemContainer<Laborer>(Laborer.class);
	protected AddLaborerDialog(BeanItem<?> item,LaborerService laborerService) {
		super(item);
		this.laborerService= laborerService;
		init();
	}
	
	public void init(){
		laborers.addAll( this.laborerService.getAllLaborerExceptThisConstruction(((BeanItem<LaborerConstructionsite>)getItem()).getBean().getConstructionsite()));
		logger.debug("laborers {}",laborers);
		super.init();
	}

	@Override
	protected Component createBody() {
		return new HorizontalLayout(){
			{
				setSizeFull();
				addComponent(new ComboBox("Trabajador:",laborers){
					{
						setItemCaptionMode(ItemCaptionMode.PROPERTY);
						setItemCaptionPropertyId("rut");
						setNewItemsAllowed(true);
						addValidator(new BeanValidator(Laborer.class, "rut"));
					}
				});
				Button btn = new Button(null,FontAwesome.PLUS);
				addComponent(btn);
			}
		};
	}

}
