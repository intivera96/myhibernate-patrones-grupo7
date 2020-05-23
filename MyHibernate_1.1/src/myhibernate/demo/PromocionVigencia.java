package myhibernate.demo;



import java.util.Date;

import myhibernate.ann.Column;
import myhibernate.ann.Entity;
import myhibernate.ann.Id;
import myhibernate.ann.JoinColumn;
import myhibernate.ann.ManyToOne;
import myhibernate.ann.Table;

@Entity
@Table(name = "promocion_vigencia")
public class PromocionVigencia {

	@Id
	@Column(name = "id_promocion_vigencia")
	private int idPromocionVigencia;
	
	@ManyToOne
	@JoinColumn(name = "id_promocion")
	private Promocion promocion;
	
	@Column(name = "fecha_inicio")
	private Date fechaInicio;
	
	@Column(name = "fecha_fin")
	private Date fechaFin;

	public int getIdPromocionVigencia() {
		return idPromocionVigencia;
	}

	public void setIdPromocionVigencia(int idPromocionVigencia) {
		this.idPromocionVigencia = idPromocionVigencia;
	}

	public Promocion getPromocion() {
		return promocion;
	}

	public void setPromocion(Promocion idPromocion) {
		this.promocion = idPromocion;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = new Date(fechaInicio.getTime());
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = new Date(fechaFin.getTime());
	}
	
}
