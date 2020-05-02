package counter.poc.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import counter.poc.entity.WrapperRoundRobin;

@Repository
public interface RoundRobinRepo extends JpaRepository<WrapperRoundRobin, Integer> {
	
	List<WrapperRoundRobin> findByOrgId(String orgId);
}
