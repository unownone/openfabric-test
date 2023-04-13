package ai.openfabric.api.repository;

import ai.openfabric.api.model.Worker;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface WorkerRepository extends PagingAndSortingRepository<Worker, Integer> {
    @Override
    List<Worker> findAll();
}
