package counter.poc.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Counter implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private long count;
	
	public void reset() {
		count = 0;
	}
	
	public void increment() {
		count++;
	}

}
