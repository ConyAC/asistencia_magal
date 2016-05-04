package cl.magal.asistencia.entities.enums;

public enum Permission {

	CREAR_OBRA(1, "Crear Obra"),
	EDITAR_OBRA(2, "Editar Obra"),
	ELIMINAR_OBRA(3, "Eliminar Obra"),
	VISUALIZAR_OTRAS_OBRAS(4, "Visualizar Obras"),
	ASIGNAR_OBRA(5, "Asignar Obrar"),
	CREAR_USUARIO(6, "Crear Usuario"),
	EDITAR_USUARIO(7, "Editar Usuario"),
	ELIMINAR_USUARIO(8, "Eliminar Usuario"),
	VISUALIZAR_USUARIO(9, "Visualizar Usuario"),
	CREAR_OBRERO(10, "Crear Obrero"),
	EDITAR_OBRERO(11, "Editar Obrero"),
	ELIMINAR_OBRERO(12, "Eliminar Obrero"),
	VISUALIZAR_OBRERO(13, "Visualizar Obrero"),
	VISUALIZAR_CUADRILLA(14, "Visualizar Cuadrilla"),
	DEFINIR_VARIABLE_GLOBAL(15, "Definir Variable Global"),
	EDITAR_VARIABLE_GLOBAL(16, "Editar Variable Global"),
	ELIMINAR_VARIABLE_GLOBAL(17, "Eliminar Variable Global"),
	VISUALIZAR_VARIABLE_GLOBAL(18, "Visualizar Variable Global"),
	VISUALIZAR_ASISTENCIA(19, "Visualizar Asistencia"),
	VISUALIZAR_CARGA_MASIVA_VACACIONES(20, "Visualizar Carga Masiva"),
	EDITAR_ASISTENCIA(21, "Editar Asistencia"),
	IMPRIMIR_OBREROS(22, "Imprimir Obreros"),
	EDITAR_OBRA_NOASIGNADAS(24, "Editar Obra No Asignada"), 
	CONFIRMAR_OBREROS(25,"Confirmar Obreros"),
	BLOQUEAR_OBRERO(26, "Bloquear Obrero"),
	AGREGAR_ETAPAS_OBRA(27,"Agregar Etapas a Obra"),
	CONFIRMAR_ASISTENCIA_OBRA(27,"Confirmar Asistencia en Obra"),
	CONFIRMAR_ASISTENCIA_CENTRAL(28,"Confirmar Asistencia desde Central"),
	GENERAR_SUELDOS_SOFTLAND(29,"Generar Sueldos en Formato Softland"), 
	DESBLOQUEDAR_ASISTENCIA(30,"Desbloquear asistencia/sueldo mes"),
	AGREGAR_CUENTAS_COSTO(31,"Agregar Cuentas de Costo"),
	CAMBIAR_ROL(32,"Cambiar Rol de Trabajadores");
	
	int i;
	String description;
	
	private Permission(int i, String description) {
		this.i = i;
		this.description = description;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static Permission getPermission(int i){
		for(Permission e : Permission.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("Permission invalid");
	}

	@Override
	public String toString(){
		return description;
	}
}
