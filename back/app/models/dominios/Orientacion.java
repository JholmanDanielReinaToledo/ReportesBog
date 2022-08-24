package models.dominios;

import io.ebean.Model;
import io.ebean.annotation.NotNull;
import models.SimpleModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "orientacion", schema = "dominios")
public class Orientacion extends Model implements SimpleModel {
    @Id
    @NotNull
    @Column(name = "id")
    public Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "descripcion")
    public String descripcion;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
