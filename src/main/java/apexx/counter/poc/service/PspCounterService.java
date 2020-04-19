package apexx.counter.poc.service;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import apexx.counter.poc.model.PspCounter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PspCounterService {
	
	@Autowired
	private PspService ps;
	
	private RedisTemplate<String, PspCounter> redisTemplate;

	private HashOperations<String, Object, Object> hashOperations;


    public PspCounterService(RedisTemplate<String, PspCounter> redisTemplate) {
        this.redisTemplate = redisTemplate;

        hashOperations = redisTemplate.opsForHash();
    }
	
	public String processAuthorisation(String[] pspIdList) {
		
		log.info("PspController|authorisation|PspCounterService|processAuthorisation");
		
		int loopCounter = 0;
		String[] tempList = pspIdList.clone();
		
		Gson gson = new Gson();
		ObjectMapper Obj = new ObjectMapper(); 
		
		while (loopCounter>=0) {
			
			log.info("counter "+ loopCounter);
			
			if(!tempList[loopCounter].equals(null)) {
				
				int roundRobinCounterAll = ps.getById(1).getCounterAll();
				int pspRedisCounterAll = 0;
				
				System.out.println(redisTemplate.opsForHash().entries("PspCounter"));
				
				for(String data : pspIdList) {
					
					String jsonStr = (String) hashOperations.get("PspCounter", data);
					PspCounter pspCounter = gson.fromJson(jsonStr, PspCounter.class);
					pspRedisCounterAll += pspCounter.getReqCount();
				}
				
				if(roundRobinCounterAll <=  pspRedisCounterAll) {
					ps.saveAll();
				}
				
				System.out.println(redisTemplate.opsForHash().entries("PspCounter"));
				
				int listCounter=0;
				switch(loopCounter) {
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
				}
				
				String str = (String) hashOperations.get("PspCounter", tempList[loopCounter]);
				int pspRedisCounter = gson.fromJson(str, PspCounter.class).getReqCount();
				
				if(listCounter > pspRedisCounter) {
				try {
					String jsonUpdatedStr = Obj.writeValueAsString(new PspCounter(tempList[loopCounter],pspRedisCounter+1));
					hashOperations.put("PspCounter", tempList[loopCounter],jsonUpdatedStr);
					
					log.info(tempList[loopCounter]);
					boolean response = externalPSP(tempList[loopCounter]);
					if(response) {
						return "PSP passed ".concat(tempList[loopCounter]);
					}
					
					//List<String> list = new ArrayList<>(Arrays.asList(tempList));
					//list.remove(tempList[loopCounter]);
					//tempList = list.toArray(new String[0]);
					//loopCounter --;
				}
		        catch (IOException e) {e.printStackTrace();}
				}
			}
			
			loopCounter = loopCounter >= tempList.length-1 ? -1 : loopCounter+1;
		}
		return "fail";
	}
	
	public boolean externalPSP(String pspName) {
		if(LocalDateTime.now().getMinute()%2==0) {
			return true;
		}
		return false;
	}

}
