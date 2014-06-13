package nz.net.dnh.eve.model.repository;

import nz.net.dnh.eve.model.domain.BlueprintTypeDecomposition;
import nz.net.dnh.eve.model.domain.BlueprintTypeDecomposition.BlueprintTypePK;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlueprintTypeDecompositionRepository extends JpaRepository<BlueprintTypeDecomposition, BlueprintTypePK> {

}
