package ai.openfabric.api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import ai.openfabric.api.model.Worker;

@RestController
@RequestMapping("${node.api.path}/worker")
public class WorkerController extends WorkerControllerBase {

    @GetMapping("/")
    public @ResponseBody Page<Worker> getAllWorkersPaginated(@RequestParam(defaultValue = "1") int page) {
        page--; // Page starts at 0

        // Save latest Containers
        List<Worker> availableContainers = fetchAllWorkers();
        workerRepository.saveAll(availableContainers);
        Pageable sortedByState = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by("state"));
        // return latest container
        return workerRepository.findAll(sortedByState);
    }
}
