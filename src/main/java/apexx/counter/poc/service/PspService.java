package apexx.counter.poc.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import apexx.counter.poc.model.PSP;
import apexx.counter.poc.model.PspCounter;
import apexx.counter.poc.repo.PspRepoInterface;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PspService {
	
	@Autowired
	private PspRepoInterface ps;
	
	private RedisTemplate<String, PspCounter> redisTemplate;

	private HashOperations<String, Object, Object> hashOperations;


    public PspService(RedisTemplate<String, PspCounter> redisTemplate) {
        this.redisTemplate = redisTemplate;

        hashOperations = redisTemplate.opsForHash();
    }
		
	@Cacheable(key="#id", value="PSP")
    public PSP getById(int id) {
		log.info("PspController|findById|PspService|getById");
		return ps.findById(id);
    }
	
	public void resetAll() {
		log.info("PspController|resetAllPspCounter|PspService|resetAll");
		
		List<PspCounter> pspCounter = new ArrayList<>();
		pspCounter.add(new PspCounter("psp11",0));
		pspCounter.add(new PspCounter("psp22",0));
		pspCounter.add(new PspCounter("psp33",0));
		pspCounter.add(new PspCounter("psp44",0));
		
		ObjectMapper Obj = new ObjectMapper(); 
		String jsonStr = "";
		
		try { 
			for(PspCounter data : pspCounter){
	            jsonStr = Obj.writeValueAsString(data);
	            hashOperations.put("PspCounter", data.getPspName(),jsonStr);
			}
        } 
        catch (IOException e) { 
            e.printStackTrace(); 
        }
	}

}
