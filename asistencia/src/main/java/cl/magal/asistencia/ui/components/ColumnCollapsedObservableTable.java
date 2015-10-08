package cl.magal.asistencia.ui.components;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

public class ColumnCollapsedObservableTable extends Table {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2276339488154479878L;
	private List<ColumnCollapsedListener> collapseListeners = new ArrayList<ColumnCollapsedListener>();

    @Override
    public void setColumnCollapsed(Object propertyId, boolean collapsed)
            throws IllegalStateException {

        super.setColumnCollapsed(propertyId, collapsed);
        fireColumnCollapsedEvent(new ColumnCollapsedEvent(this, propertyId, collapsed));
    }

    @Override
    public String formatPropertyValue(Object rowId, Object colId, Property property) {
        // Format by property type
        if (property.getType() == Double.class) {
        	NumberFormat df = NumberFormat.getInstance();
            return df.format(Math.round((Double) property.getValue()));
        }

        return super.formatPropertyValue(rowId, colId, property);
    }
    
    public void addColumnCollapsedListener(ColumnCollapsedListener l) {
    	collapseListeners.add(l);
    }

    public void removeColumnCollapsedListener(ColumnCollapsedListener l) {
    	collapseListeners.remove(l);
    }

    private void fireColumnCollapsedEvent(ColumnCollapsedEvent event) {
    	for(ColumnCollapsedListener listener : collapseListeners){
    		listener.colapseColumn(event);
    	}
    } 
    
    public class ColumnCollapsedEvent extends Component.Event {

    	/**
		 * 
		 */
		private static final long serialVersionUID = -1509986492818812776L;
		Object propertyId; boolean collapsed;
		public ColumnCollapsedEvent(Component source,Object propertyId, boolean collapsed) {
			super(source);
			this.propertyId = propertyId;
			this.collapsed = collapsed;
		}
		public Object getPropertyId() {
			return propertyId;
		}
		public boolean isCollapsed() {
			return collapsed;
		}
		
    }
    
    public interface ColumnCollapsedListener extends Serializable {
		public void colapseColumn(ColumnCollapsedEvent event);
	}
}
