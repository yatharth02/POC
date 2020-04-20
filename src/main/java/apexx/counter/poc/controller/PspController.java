package apexx.counter.poc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import apexx.counter.poc.service.PspCounterService;
import apexx.counter.poc.service.PspService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/psp")
public class PspController {
	
	@Autowired
	PspService ps;
	
	@Autowired
	PspCounterService pcs;
	
	@GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable("id") int id) {
		log.info("PspController|findById");
    	return new ResponseEntity<>(ps.getById(id),HttpStatus.OK);
    }
	
	@GetMapping("/save")
    public ResponseEntity<Object> resetAllPspCounter() {
		log.info("PspController|resetAllPspCounter");
		ps.resetAll();
    	return new ResponseEntity<>("OK",HttpStatus.OK);
    }
	
	@GetMapping("/psp")
	public ResponseEntity<Object> authorisation() throws JsonProcessingException {
		String[] pspIdList = {"psp11", "psp22", "psp33", "psp44"};
		log.info("PspController|authorisation");
		String response = pcs.processAuthorisation(pspIdList);
		if(null == response) {
			response = "fail";
		}
		return new ResponseEntity<>(response,HttpStatus.OK);
		
	}
}
