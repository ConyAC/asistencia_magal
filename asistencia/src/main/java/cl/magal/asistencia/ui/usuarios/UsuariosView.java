package cl.magal.asistencia.ui.usuarios;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.FilterTable;

import ru.xpoft.vaadin.VaadinView;
import cl.magal.asistencia.util.Utils;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@VaadinView(value=UsuariosView.NAME)
@Scope("prototype")
@Component
public class UsuariosView extends HorizontalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8538300514577423280L;
	
	public static final String NAME = "usuarios";
	
	Container container = new IndexedContainer();
	
	public UsuariosView(){
		//solo para test
		container.addContainerProperty("rol", String.class, null);
		container.addContainerProperty("nombre", String.class, null);
		
		setSizeFull();
		
		//dibula la sección de las obras
		VerticalLayout usuarios = drawUsuarios();
		
		addComponent(usuarios);
		setExpandRatio(usuarios, 0.2F);
		
		//dibuja la sección de detalles
		VerticalLayout detalleUsuario = drawDetalleUsuario();
		
		addComponent(detalleUsuario);
		setExpandRatio(detalleUsuario, 0.8F);
	}
	
	private VerticalLayout drawDetalleUsuario() {
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		vl.addComponent(new Label("Seleccione un usuario para ver su información"));
		
		return vl;
	}

	private FilterTable drawTablaUsuarios() {
		FilterTable table =  new FilterTable();
		table.setContainerDataSource(container);
		table.setSizeFull();
		table.setFilterBarVisible(true);
		table.setVisibleColumns("rol","nombre");
		table.setSelectable(true);
		return table;
	}

	private VerticalLayout drawUsuarios() {
		
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		
		//la tabla con su buscador buscador
		vl.addComponent(drawTablaUsuarios());
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
				
				Object itemId = container.addItem();
				Item item = container.getItem(itemId);
				item.getItemProperty("rol").setValue("Adm. Central");
				item.getItemProperty("nombre").setValue("Usuario "+Utils.random());
				
			}
		});
		hl.addComponent(agregaObra);
		Button borrarObra = new Button(null,FontAwesome.TRASH_O);
		hl.addComponent(borrarObra);
		
		return vl;
	}

	@PostConstruct
	private void init(){
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
