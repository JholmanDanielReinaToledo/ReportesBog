package utilities.security;

public enum Modulo {
  //ToDo, los IDs depende de cómo quede en productivo.

solo_lectura(0),
admin_dominios_admin(1),
admin_dominios_aprovechamiento(2),
admin_dominios_bodegas(3),
admin_dominios_social(4),
admin_dominios_operativa(5),
admin_dominios_pev(6),
admin_dominios_regulaciones(7),
admin_usuarios_grupos(8),
regulaciones_autorizacion(9),
regulaciones_modulos(10),
regulaciones_recuperacion(11),
archivo(12),
bodegas_retenciones(13),
bodegas_descongestion(14),
operativa_asig_tareas(15),
operativa_incautaciones(16),
operativa_territorios(17),
operativa_recuperaciones(18),
social_registro_ventero(19),
social_ofertas(20),
social_asig_visitas(21),
vehiculos(22),
pev_asig_visitas(23),
pev_soli_publi(24),
pev_registro_persona(25),
pev_regulaciones_pev(26),
pev_pasacalles(27),
pev_direc_publicidad(28),
pev_soli_pasacalles(29),
personas(30),
evidencia_misional(31),
otros_radicados(32),
social_registro_estudio(33),
operativa_sensibilizacion(34);

  private Integer idModulo;

  Modulo(Integer id) {
    this.idModulo = id;
  }

  public Long getId() {
    return idModulo.longValue();
  }
}
