package counter.poc.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import counter.poc.entity.OrgRequestCounter;
import counter.poc.entity.WrapperRoundRobin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PspCounterService {
	
	@Autowired
	private RoundRobinService roundRobinService;
	
	private ObjectMapper obj = new ObjectMapper();
	
	private RedisTemplate<String, OrgRequestCounter> redisTemplate;

	private HashOperations<String, Object, Object> hashOperations;


    public PspCounterService(RedisTemplate<String, OrgRequestCounter> redisTemplate) {
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }
    
    private static final String PSP_COUNTER_ORG1 = "PspCounter Org1";

	public String processAuthorisation(List<String> pspIdList) throws JsonProcessingException {
		
		log.info("PspController|authorisation|PspCounterService|processAuthorisation");
		
		for(String pspId : pspIdList) {
			
			WrapperRoundRobin roundRobin = roundRobinService.fetch("orgId1").stream().
					filter(a -> LocalDate.parse(a.getRoundRobin().getStartDate()).minusDays(1).isBefore(LocalDate.now())).
					filter(a -> LocalDate.parse(a.getRoundRobin().getEndDate()).plusDays(1).isAfter(LocalDate.now())).
					collect(Collectors.toList()).get(0);
			
			
			String objectJsonStr = (String) hashOperations.get(PSP_COUNTER_ORG1, "orgId1");
			OrgRequestCounter orgRequestCounter = obj.readValue(objectJsonStr, OrgRequestCounter.class);
			
			if (orgRequestCounter.getTotalCount().getCount() >= roundRobin.getRoundRobin().getSlotSize()) {
				
				//reset PSP counter cache
				cacheReset(orgRequestCounter);
			}
			
			//fetch max PSP count from round robin
			int pspMaxCount = getRoundRobinPspMaxCount(pspId,roundRobin);
			
			//check and increment PSP counter cache
			String response = cacheIncrement(pspId, orgRequestCounter,pspMaxCount);
			if("true".equals(response)) {
				return "PSP passed ".concat(pspId);
			}
			else if("fail".equals(response)){
				//call fallback PSP and return the value
				return fallBackPsps(roundRobin, pspId);
			}
			
		}
		return "All PSPs Failed";
	}
	
	//fetch max PSP count from round robin
	private int getRoundRobinPspMaxCount(String pspId, WrapperRoundRobin roundRobin) {
		
		log.info("PspController|authorisation|PspCounterService|processAuthorisation|getRoundRobinPspMaxCount");
		
		int pspMaxCount = 0;
		if(pspId.equals(roundRobin.getRoundRobin().getPsp1Id())) {
			pspMaxCount = roundRobin.getRoundRobin().getRrbn1Num();
		}
		else if(pspId.equals(roundRobin.getRoundRobin().getPsp2Id())) {
			pspMaxCount = roundRobin.getRoundRobin().getRrbn2Num();
		}
		else if(pspId.equals(roundRobin.getRoundRobin().getPsp3Id())) {
			pspMaxCount = roundRobin.getRoundRobin().getRrbn3Num();
		}
		else if(pspId.equals(roundRobin.getRoundRobin().getPsp4Id())) {
			pspMaxCount = roundRobin.getRoundRobin().getRrbn4Num();
		}
		return pspMaxCount;
	}
	
	//generate JSON string form Object
	private String generateObjtoJson(OrgRequestCounter orgRequestCounter) throws JsonProcessingException {
		
		log.info("PspController|authorisation|PspCounterService|processAuthorisation|generateObjtoJson");
		
        return obj.writeValueAsString(orgRequestCounter);
	}
	
	//reset PSP counter cache
	private void cacheReset(OrgRequestCounter orgRequestCounter) throws JsonProcessingException {
		
		log.info("PspController|authorisation|PspCounterService|processAuthorisation|cacheReset");
		
		orgRequestCounter.reset();
		
		//generate JSON string form Object
		String jsonStr = generateObjtoJson(orgRequestCounter);
        hashOperations.put(PSP_COUNTER_ORG1, orgRequestCounter.getOrgId(),jsonStr);
        log.info("PSP redis list {}", redisTemplate.opsForHash().entries(PSP_COUNTER_ORG1));
	}
	
	//check and increment PSP counter cache
	private String cacheIncrement(String pspId, OrgRequestCounter orgRequestCounter, int pspMaxCount) throws JsonProcessingException {
		
		log.info("PspController|authorisation|PspCounterService|processAuthorisation|cacheIncrement");
		
		if (pspMaxCount > orgRequestCounter.getPspCounterMap().get(pspId).getCount()) {
			orgRequestCounter.increment(pspId);
			
			//generate JSON string form Object
			String jsonStr = generateObjtoJson(orgRequestCounter);
		    hashOperations.put(PSP_COUNTER_ORG1, orgRequestCounter.getOrgId(),jsonStr);
			log.info("PSP redis list {}", redisTemplate.opsForHash().entries(PSP_COUNTER_ORG1));
			if(LocalDateTime.now().getMinute()%2==0) {return "true";}
			else {return "fail";}
		}
		return null;
	}
	
	//call fallback PSP and return the value
	private String fallBackPsps(WrapperRoundRobin roundRobin, String pspId) {
		
		log.info("PspController|authorisation|PspCounterService|processAuthorisation|fallBackPsps");
		
		//fetch fall back PSP round robin
		String fallBackPsp = getFallBackPsp(pspId, roundRobin);
		if(LocalDateTime.now().getSecond()%2==0) {
			return "PSP passed ".concat(fallBackPsp); 
		}
		return "All PSPs Failed";
	}
	
	//fetch fall back PSP round robin
	private String getFallBackPsp(String pspId, WrapperRoundRobin roundRobin) {
		
		log.info("PspController|authorisation|PspCounterService|processAuthorisation|getFallBackPsp");
		
		String fallBackPsp = null;
		if(pspId.equals(roundRobin.getRoundRobin().getPsp1Id())) {
			fallBackPsp = roundRobin.getRoundRobin().getPsp1IdFallback();
		}
		else if(pspId.equals(roundRobin.getRoundRobin().getPsp2Id())) {
			fallBackPsp = roundRobin.getRoundRobin().getPsp2IdFallback();
		}
		else if(pspId.equals(roundRobin.getRoundRobin().getPsp3Id())) {
			fallBackPsp = roundRobin.getRoundRobin().getPsp3IdFallback();
		}
		else if(pspId.equals(roundRobin.getRoundRobin().getPsp4Id())) {
			fallBackPsp = roundRobin.getRoundRobin().getPsp4IdFallback();
		}
		return fallBackPsp;
	}

}
