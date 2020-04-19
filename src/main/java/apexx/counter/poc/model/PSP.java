package apexx.counter.poc.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PSP")
public class PSP implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

	
	@Column(name = "COUNTER_ALL")
	private Integer counterAll;
	
	@Column(name = "PSP1")
	private String psp1;
	
	@Column(name = "COUNTER1")
	private Integer counter1;
	
	@Column(name = "PSP2")
	private String psp2;
	
	@Column(name = "COUNTER2")
	private Integer counter2;
	
	@Column(name = "psp3")
	private String psp3;
	
	@Column(name = "COUNTER3")
	private Integer counter3;
	
	@Column(name = "psp4")
	private String psp4;
	
	@Column(name = "COUNTER4")
	private Integer counter4;

}
