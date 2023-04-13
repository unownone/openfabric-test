package ai.openfabric.api.repository;

import org.springframework.data.repository.CrudRepository;

import ai.openfabric.api.model.WorkerCPU;

public interface WorkerCPURepository extends CrudRepository<WorkerCPU, Integer> {

}
