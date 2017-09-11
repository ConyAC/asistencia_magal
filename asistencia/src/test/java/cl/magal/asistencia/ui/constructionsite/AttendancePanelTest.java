package cl.magal.asistencia.ui.constructionsite;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vaadin.data.Property;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.shared.ui.ui.PageState;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.ui.MagalUI;
import cl.magal.asistencia.util.SecurityHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class AttendancePanelTest {

	@Autowired
	AttendancePanel panel;
	@Autowired
	UserService userService;

	@Test
	public void test() {
		Long userId = 1L;
		// busca al usuario
		User user = userService.findUser(userId);
		// verifica que no sea null
		assertNotNull(user);
		assertTrue(!user.getCs().isEmpty());

		// hace un fake del login
		fakeLogin(user);

		ConstructionSite cs = new ConstructionSite();
		cs.setId(10L);

		panel.setCs(cs);
		
		String uriFragment = "asistencia/052017";

		MagalUI ui = new MagalUI();
		Page page = new Page(ui, new PageState());
		VaadinRequest request = new VaadinServletRequest(null, null);
		request.getParameter("");
		page.init(null);
		UI.setCurrent(ui);
		Page.getCurrent().setLocation("");
		Page.getCurrent().setUriFragment(uriFragment,false);

		// test
		ViewChangeEvent event = new ViewChangeEvent(new Navigator(UI.getCurrent(), new VerticalLayout()), null, null,
				null, "asistencia/052017");
		panel.enter(event);
	}
	
	@Test
	public void test2(){
		
		Long userId = 1L;
		// busca al usuario
		User user = userService.findUser(userId);
		// verifica que no sea null
		assertNotNull(user);
		assertTrue(!user.getCs().isEmpty());

		// hace un fake del login
		fakeLogin(user);

		ConstructionSite cs = new ConstructionSite();
		cs.setId(10L);

		panel.setCs(cs);
		
		System.out.println("inicio \n\n");
		
		long inicio = System.currentTimeMillis() / 1000;
		
		panel.populateAttendanceGrid();
		
		long fin = System.currentTimeMillis() / 1000 ;
		
		long periodo = fin - inicio;
		
		System.out.println("inicio "+inicio+" fin "+ fin + " periodo "+periodo);
		
		Grid attendanceGrid = panel.attendanceGrid;
		//imprime el contenido
		for ( final Object rid : attendanceGrid.getContainerDataSource().getItemIds()){
			for (final Object pid: attendanceGrid.getContainerDataSource().getContainerPropertyIds()) {
				Property pp = attendanceGrid.getContainerDataSource().getContainerProperty(rid, pid);
				System.out.print(" " + pp.getValue()+";");
			}
			System.out.println("");
		}
	}

	private void fakeLogin(User user) {
		SecurityHelper.setUser(user);
	}

}
