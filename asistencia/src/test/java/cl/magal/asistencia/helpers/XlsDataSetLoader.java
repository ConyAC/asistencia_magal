package cl.magal.asistencia.helpers;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.springframework.core.io.Resource;

import com.github.springtestdbunit.dataset.AbstractDataSetLoader;

public class XlsDataSetLoader extends AbstractDataSetLoader {

	protected IDataSet createDataSet(Resource resource) throws Exception {
	    IDataSet xlsDataSet = new XlsDataSet(resource.getFile());
	    ReplacementDataSet result = new ReplacementDataSet(xlsDataSet);
	    result.addReplacementObject("null", null);
	    return result;
    }
}
