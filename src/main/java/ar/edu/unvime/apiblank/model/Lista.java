package ar.edu.unvime.apiblank.model;

/** Entidad de dominio: una lista que agrupa favoritos (por ejemplo "Regalos") y pertenece a un usuario. */
public class Lista {

    private Long id;
    private String nombre;
    private Long usuarioId;

    public Lista(Long id, String nombre, Long usuarioId) {
        this.id = id;
        this.nombre = nombre;
        this.usuarioId = usuarioId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}