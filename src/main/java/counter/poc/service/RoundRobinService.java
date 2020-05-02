package counter.poc.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import counter.poc.entity.Counter;
import counter.poc.entity.OrgRequestCounter;
import counter.poc.entity.RoundRobin;
import counter.poc.entity.WrapperRoundRobin;
import counter.poc.repo.RoundRobinRepo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoundRobinService {
	
	@Autowired
	private RoundRobinRepo roundRobinRepo;
	
	private RedisTemplate<String, OrgRequestCounter> redisTemplate;

	private HashOperations<String, Object, Object> hashOperations;
	
	private static final String PSP_COUNTER_ORG1 = "PspCounter Org1";
	
	private ObjectMapper obj = new ObjectMapper();

    public RoundRobinService(RedisTemplate<String, OrgRequestCounter> redisTemplate) {
        this.redisTemplate = redisTemplate;

        hashOperations = redisTemplate.opsForHash();
    }

	@Cacheable(key="#orgId", value="RoundRobin")
	public List<WrapperRoundRobin> fetch(String orgId) {
		
		log.info("CounterPocController|fetch|RoundRobinService|fetch");
		
		WrapperRoundRobin wrr = new WrapperRoundRobin();
		wrr.setOrgId(orgId);
		RoundRobin roundRobin1 = new RoundRobin(100,"psp1",40,"psp4","psp2",20,"psp4","psp3",15,"psp4","psp4",25,"psp1",LocalDate.now().toString(),LocalDate.now().plusDays(1).toString());
		wrr.setRoundRobin(roundRobin1);
		wrr.setId(1);
		roundRobinRepo.save(wrr);

		RoundRobin roundRobin2 = new RoundRobin(200,"psp1",140,"psp4","psp2",20,"psp4","psp3",15,"psp4","psp4",25,"psp1",LocalDate.now().plusDays(2).toString(),LocalDate.now().plusDays(4).toString());
		wrr.setRoundRobin(roundRobin2);
		wrr.setId(2);
		roundRobinRepo.save(wrr);
		
		return roundRobinRepo.findByOrgId(orgId);
	}
	
	public String resetAll() throws JsonProcessingException {
		log.info("CounterPocController|resetAllPspCounter|RoundRobinService|resetAll");
		
		Map<String, Counter> pspCounterMap = new HashMap<>();
		pspCounterMap.put("psp1", new Counter(0));
		pspCounterMap.put("psp2", new Counter(0));
		pspCounterMap.put("psp3", new Counter(0));
		pspCounterMap.put("psp4", new Counter(0));
		
		OrgRequestCounter orgRequestCounter = new OrgRequestCounter("orgId1", new Counter(0), pspCounterMap);
		 
		String jsonStr = "";
		
		jsonStr = obj.writeValueAsString(orgRequestCounter);
        hashOperations.put(PSP_COUNTER_ORG1, orgRequestCounter.getOrgId(),jsonStr);
            
		log.info("PSP redis list {}", redisTemplate.opsForHash().entries(PSP_COUNTER_ORG1));
		return redisTemplate.opsForHash().entries(PSP_COUNTER_ORG1).toString();
	}
}
