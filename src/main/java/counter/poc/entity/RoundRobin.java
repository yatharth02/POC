package counter.poc.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoundRobin implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int slotSize;
	
	private String psp1Id;
	
	private int rrbn1Num;
	
	private String psp1IdFallback;
	
	private String psp2Id;
	
	private int rrbn2Num;
	
	private String psp2IdFallback;
	
	private String psp3Id;
	
	private int rrbn3Num;
	
	private String psp3IdFallback;
	
	private String psp4Id;
	
	private int rrbn4Num;
	
	private String psp4IdFallback;
	
	private String startDate;
	
	private String endDate;

}
