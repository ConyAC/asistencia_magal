package cl.magal.asistencia.util;

import java.util.Map;

import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.EscapeTool;
import org.apache.velocity.tools.generic.NumberTool;

public class VelocityHelper {

	public static void addTools(Map<String, Object> input) {
		input.put("tools", new DateTool());
		input.put("numberTools", new NumberTool());
		input.put("esc", new EscapeTool());
	}

}
