package models.general;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Model;
import io.ebean.annotation.NotNull;
import models.ArchivoModel;
import models.SimpleModel;
import org.postgis.Geometry;
import org.postgis.Point;
import utilities.json.geoJson.deserializers.CustomPointDeserializer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "direccion", schema = "sisdep_general")
public class Direccion extends Model implements ArchivoModel {

    @Id
    @NotNull
    @Column(name = "id")
    public Long id;

    @Column(name = "cruce_desde")
    public Long cruceDesde;

    @Column(name = "numero_desde")
    public Long numeroDesde;

    @Column(name = "letra_desde")
    public String letraDesde;

    @Column(name = "orientacion_desde")
    public Long orientacionDesde;

    @Column(name = "cruce_hasta")
    public Long cruceHasta;

    @Column(name = "numero_hasta")
    public Long numeroHasta;

    @Column(name = "letra_hasta")
    public String letraHasta;

    @Column(name = "orientacion_hasta")
    public Long orientacionHasta;

    @Column(name = "numero")
    public Long numero;

    @Column(name = "complemento")
    public String complemento;

    @Column(name = "id_municipio")
    public Long idMunicipio;

    @Column(name = "id_barrio")
    public Long idBarrio;

    @Column(name = "id_comuna")
    public Long idComuna;

    @Column(name = "localizacion")
    @JsonDeserialize(using = CustomPointDeserializer.class)
    public Point localizacion;

    @Column(name = "ip")
    public String ip;

    @Column(name = "id_usuario_edita")
    public Long idUsuarioEdita;

    @Column(name = "fecha_edita")
    public LocalDate fechaEdita;

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public Long getIdUsuarioEdita() {
        return idUsuarioEdita;
    }

    @Override
    public void setIdUsuarioEdita(Long idUsuarioEdita) {
        this.idUsuarioEdita = idUsuarioEdita;
    }

    @Override
    public LocalDate getFechaEdita() {
        return fechaEdita;
    }

    @Override
    public void setFechaEdita(LocalDate fechaEdita) {
        this.fechaEdita = fechaEdita;
    }


    public Long getCruceDesde() {
        return cruceDesde;
    }

    public void setCruceDesde(Long cruceDesde) {
        this.cruceDesde = cruceDesde;
    }

    public Long getNumeroDesde() {
        return numeroDesde;
    }

    public void setNumeroDesde(Long numeroDesde) {
        this.numeroDesde = numeroDesde;
    }

    public String getLetraDesde() {
        return letraDesde;
    }

    public void setLetraDesde(String letraDesde) {
        this.letraDesde = letraDesde;
    }

    public Long getOrientacionDesde() {
        return orientacionDesde;
    }

    public void setOrientacionDesde(Long orientacionDesde) {
        this.orientacionDesde = orientacionDesde;
    }

    public Long getCruceHasta() {
        return cruceHasta;
    }

    public void setCruceHasta(Long cruceHasta) {
        this.cruceHasta = cruceHasta;
    }

    public Long getNumeroHasta() {
        return numeroHasta;
    }

    public void setNumeroHasta(Long numeroHasta) {
        this.numeroHasta = numeroHasta;
    }

    public String getLetraHasta() {
        return letraHasta;
    }

    public void setLetraHasta(String letraHasta) {
        this.letraHasta = letraHasta;
    }

    public Long getOrientacionHasta() {
        return orientacionHasta;
    }

    public void setOrientacionHasta(Long orientacionHasta) {
        this.orientacionHasta = orientacionHasta;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public Long getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(Long idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public Long getIdBarrio() {
        return idBarrio;
    }

    public void setIdBarrio(Long idBarrio) {
        this.idBarrio = idBarrio;
    }

    public Long getIdComuna() {
        return idComuna;
    }

    public void setIdComuna(Long idComuna) {
        this.idComuna = idComuna;
    }

    public Point getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(Point localizacion) {
        this.localizacion = localizacion;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Direccion{" +
          "id=" + id +
          ", cruceDesde=" + cruceDesde +
          ", numeroDesde=" + numeroDesde +
          ", letraDesde='" + letraDesde + '\'' +
          ", orientacionDesde=" + orientacionDesde +
          ", cruceHasta=" + cruceHasta +
          ", numeroHasta=" + numeroHasta +
          ", letraHasta='" + letraHasta + '\'' +
          ", orientacionHasta=" + orientacionHasta +
          ", numero=" + numero +
          ", complemento='" + complemento + '\'' +
          ", idMunicipio=" + idMunicipio +
          ", idBarrio=" + idBarrio +
          ", idComuna=" + idComuna +
          ", localizacion=" + localizacion +
          '}';
    }
}
