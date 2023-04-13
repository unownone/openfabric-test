package ai.openfabric.api.repository;

import org.springframework.data.repository.CrudRepository;

import ai.openfabric.api.model.WorkerMemory;

public interface WorkerMemoryRepository extends CrudRepository<WorkerMemory, Integer> {

}
