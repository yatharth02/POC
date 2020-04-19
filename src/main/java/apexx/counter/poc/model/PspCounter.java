package apexx.counter.poc.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PspCounter implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String pspName;
	
	private int reqCount;
	
}