package ai.openfabric.api.controller;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import ai.openfabric.api.model.Worker;
import ai.openfabric.api.repository.WorkerRepository;

public class WorkerControllerBase {
    final int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    WorkerRepository workerRepository;

    private DockerClientConfig standard = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
    private DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(standard.getDockerHost())
            .sslConfig(standard.getSSLConfig())
            .maxConnections(100)
            .connectionTimeout(Duration.ofSeconds(30))
            .responseTimeout(Duration.ofSeconds(45))
            .build();

    protected DockerClient dockerClient = DockerClientImpl.getInstance(standard, httpClient);

    List<Worker> fetchAllWorkers() {

        return Worker.fromContainers(dockerClient.listContainersCmd().withShowAll(true).exec());
    }

    Container getContainerById(String containerId) {
        List<String> workerIds = new ArrayList<String>();
        workerIds.add(containerId);
        List<Container> containers = dockerClient.listContainersCmd().withIdFilter(workerIds).exec();
        if (containers.size() == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Container Not Found");
        }
        return containers.get(0);
    }

}
