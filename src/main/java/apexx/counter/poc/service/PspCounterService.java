package apexx.counter.poc.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import apexx.counter.poc.model.PspCounter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PspCounterService {
	
	@Autowired
	private PspService ps;
	
	private Gson gson = new Gson();
	
	private ObjectMapper obj = new ObjectMapper();
	
	private RedisTemplate<String, PspCounter> redisTemplate;

	private HashOperations<String, Object, Object> hashOperations;


    public PspCounterService(RedisTemplate<String, PspCounter> redisTemplate) {
        this.redisTemplate = redisTemplate;

        hashOperations = redisTemplate.opsForHash();
    }
    
    private static final String PSP_COUNTER = "PspCounter";
	
	public String processAuthorisation(String[] pspIdList) throws JsonProcessingException {
		
		log.info("PspController|authorisation|PspCounterService|processAuthorisation");
		
		int loopCounter = 0;
		String[] tempList = pspIdList.clone();
		
		while (loopCounter>=0) {
			
			log.info("counter {}", loopCounter);
			
			if(tempList[loopCounter] != null) {
				
				int roundRobinCounterAll = ps.getById(1).getCounterAll();
				
				log.info("PSP redis list {}", redisTemplate.opsForHash().entries(PSP_COUNTER));
				
				//calculate the sum of Redis counter for specific PSPs
				int pspRedisCounterAll = calculatePspRedisCounter(pspIdList);
				
				if(roundRobinCounterAll <=  pspRedisCounterAll) {
					ps.resetAll();
				}
				
				log.info("PSP redis list {}", redisTemplate.opsForHash().entries(PSP_COUNTER));
				
				//fetch PSP from DB or redis cache
				int listCounter = fetchPspCounter(loopCounter);
				
				String str = (String) hashOperations.get(PSP_COUNTER, tempList[loopCounter]);
				int pspRedisCounter = gson.fromJson(str, PspCounter.class).getReqCount();
				
				return (callPsp(tempList, loopCounter, pspRedisCounter, listCounter));
			}
			
			loopCounter = loopCounter >= tempList.length-1 ? -1 : loopCounter+1;
		}
		return null;
	}
	
	public boolean externalPSP() {
		return(LocalDateTime.now().getMinute()%2==0);
	}
	
	public int fetchPspCounter(int index ) {
		int listCounter=0;
		switch(index) {
			case 0: 
				listCounter = ps.getById(1).getCounter1(); 
	            break; 
	        case 1: 
	        	listCounter = ps.getById(1).getCounter2(); 
	            break; 
	        case 2: 
	        	listCounter = ps.getById(1).getCounter3(); 
	            break; 
	        case 3: 
	        	listCounter = ps.getById(1).getCounter4(); 
	            break; 
			default:
				listCounter=0;
		}
		return listCounter;
	}
	
	public int calculatePspRedisCounter(String[] pspIdList) {
		int pspRedisCounterAll = 0;
		for(String data : pspIdList) {
			
			String jsonStr = (String) hashOperations.get(PSP_COUNTER, data);
			PspCounter pspCounter = gson.fromJson(jsonStr, PspCounter.class);
			pspRedisCounterAll += pspCounter.getReqCount();
		}
		return pspRedisCounterAll;
	}
	
	public String callPsp(String[] tempList, int loopCounter, int pspRedisCounter, int listCounter) throws JsonProcessingException{
		if(listCounter > pspRedisCounter) {
			String jsonUpdatedStr = obj.writeValueAsString(new PspCounter(tempList[loopCounter],pspRedisCounter+1));
			hashOperations.put(PSP_COUNTER, tempList[loopCounter],jsonUpdatedStr);
			
			log.info("Current PSP is {}",tempList[loopCounter]);
			boolean response = externalPSP();
			if(response) {
				return "PSP passed ".concat(tempList[loopCounter]);
			}
			//List<String> list = new ArrayList<>(Arrays.asList(tempList));
			//list.remove(tempList[loopCounter]);
			//tempList = list.toArray(new String[0]);
			//loopCounter --;
		}
		return null;
	}
	
}
