package counter.poc.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import counter.poc.service.PspCounterService;
import counter.poc.service.RoundRobinService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class CounterPocController {
	
	@Autowired
	private RoundRobinService roundRobinService;
	
	@Autowired
	private PspCounterService pspCounterService;

	@GetMapping("/round/{orgId}")
	public ResponseEntity<Object> fetch(@PathVariable("orgId") String orgId){
		log.info("CounterPocController|fetch");
		return new ResponseEntity<>(roundRobinService.fetch(orgId),HttpStatus.OK);
	}
	
	@GetMapping("/save")
    public ResponseEntity<Object> resetAllPspCounter() throws JsonProcessingException {
		log.info("PspController|resetAllPspCounter");
    	return new ResponseEntity<>(roundRobinService.resetAll(),HttpStatus.OK);
    }
	
	@GetMapping("/psp")
	public ResponseEntity<Object> authorisation() throws JsonProcessingException {
		log.info("PspController|authorisation");
		List<String> pspIdList = new ArrayList<>();
		pspIdList.add("psp1");
		pspIdList.add("psp2");
		pspIdList.add("psp3");
		pspIdList.add("psp4");
		
		return new ResponseEntity<>(pspCounterService.processAuthorisation(pspIdList),HttpStatus.OK);
		
	}

}
