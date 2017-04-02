package cl.magal.asistencia.ui.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;

public class LazyTabSheet extends TabSheet {

/**
	 * 
	 */
	private static final long serialVersionUID = 1503312918825101587L;


public LazyTabSheet() {
    addSelectedTabChangeListener(new LazyTabChangeListener());
}


public static abstract class LazyTab extends CustomComponent {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1385715434636997560L;


	public LazyTab() {
        this(false);
    }


    public LazyTab(boolean eager) {
        if (eager) {
            refresh();
        }
    }


    public abstract Component build();


    public final void refresh() {
        setCompositionRoot(build());
    }
}


private static class LazyTabChangeListener implements SelectedTabChangeListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8896998017433423422L;

	@Override
    public void selectedTabChange(SelectedTabChangeEvent event) {
        Component selectedTab = event.getTabSheet().getSelectedTab();
        if (selectedTab instanceof LazyTab) {
            ((LazyTab) selectedTab).refresh();
        }
    }
}

}