package ai.openfabric.api.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.InvocationBuilder.AsyncResultCallback;

import ai.openfabric.api.model.Worker;
import ai.openfabric.api.model.WorkerCPU;
import ai.openfabric.api.model.WorkerMemory;
import ai.openfabric.api.repository.WorkerCPURepository;
import ai.openfabric.api.repository.WorkerMemoryRepository;

class StatsResponse {
    public WorkerCPU cpu;
    public WorkerMemory memory;

    public StatsResponse(WorkerCPU cpu, WorkerMemory memory) {
        this.cpu = cpu;
        this.memory = memory;
    }
}

@RestController
@RequestMapping("${node.api.path}/worker/{workerId}")
/*
 * This controller is used to handle the request for a specific worker.
 */
public class WorkerDetailController extends WorkerControllerBase {
    @Autowired
    private WorkerCPURepository workerCPURepository;

    @Autowired
    private WorkerMemoryRepository workerMemoryRepository;

    @GetMapping(path = "/info")
    public @ResponseBody Worker getWorkerById(@PathVariable String workerId) {
        if (workerId == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "String Cannot be Null");
        }
        Worker worker = Worker.fromContainer(getContainerById(workerId));
        workerRepository.save(worker);
        return worker;

    }

    @GetMapping(path = "/stats")
    public @ResponseBody StatsResponse getWorkerStats(@PathVariable String workerId) {
        AsyncResultCallback<Statistics> callback = new AsyncResultCallback<>();

        StatsResponse response;
        try {
            Worker worker = getWorkerById(workerId);
            Statistics stats;
            dockerClient.statsCmd(workerId).exec(callback);

            stats = callback.awaitResult();
            callback.close();
            WorkerCPU worker_cpu = new WorkerCPU().fromCPUStats(worker, stats.getCpuStats());

            WorkerMemory worker_memory = new WorkerMemory().fromMemoryStats(worker, stats.getMemoryStats());

            workerCPURepository.save(worker_cpu);
            workerMemoryRepository.save(worker_memory);

            response = new StatsResponse(worker_cpu, worker_memory);

        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Container Not Found/Container Already Stopped", e);
        } catch (RuntimeException | IOException e) {
            System.out.println("HhHEHEHHE");
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error Getting Stats", e);
        }
        return response;
    }

    @PutMapping(path = "/start")
    public @ResponseBody String startWorker(@PathVariable String workerId) {
        if (workerId == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "String Cannot be Null");
        }
        try {
            dockerClient.startContainerCmd(workerId).exec();

        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Container Not Found", e);
        } catch (NotModifiedException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Container Already Running", e);
        }
        return "Container Started Successfully";
    }

    @PutMapping(path = "/stop")
    public @ResponseBody String stopWorker(@PathVariable String workerId) {
        if (workerId == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "String Cannot be Null");
        }

        try {
            dockerClient.stopContainerCmd(workerId).exec();
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Container Not Found", e);
        } catch (NotModifiedException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Container Already Stopped", e);
        }
        return "Container Stopped Successfully";
    }

}