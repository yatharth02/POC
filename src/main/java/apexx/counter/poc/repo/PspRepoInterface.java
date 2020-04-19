package apexx.counter.poc.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import apexx.counter.poc.model.PSP;

@Repository
public interface PspRepoInterface extends JpaRepository<PSP, Integer> {
	
	PSP findById(int id);

}
