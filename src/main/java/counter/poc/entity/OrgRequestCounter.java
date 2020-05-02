package counter.poc.entity;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrgRequestCounter implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String orgId;
	
	private Counter totalCount;
	
	private Map<String,Counter> pspCounterMap; 
	
	public void reset() {
		totalCount.reset();
		for(String pspId : pspCounterMap.keySet() ){
		     pspCounterMap.get(pspId).reset();
		}
	}
	
	public void increment(String pspId) {
		pspCounterMap.get(pspId).increment();
		totalCount.increment();
	}

}
