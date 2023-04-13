package ai.openfabric.api.model;

import lombok.Getter;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerPort;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import java.util.List;
import java.util.stream.Collectors;

@Entity()
@Table(name = "worker", uniqueConstraints = { @UniqueConstraint(columnNames = { "container_id" }) })
public class Worker extends Datable {

    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "of-uuid")
    // @GenericGenerator(name = "of-uuid", strategy =
    // "ai.openfabric.api.model.IDGenerator")
    // @Getter
    // @Setter
    // public String id;

    @Id
    @Getter
    public String container_id;

    public String name;

    public String image;

    public String ip;
    public Integer public_port;
    public Integer private_port;
    public String port_type;

    public String status;

    public String state;

    /**
     * Create a new worker from a container
     * 
     * @param container
     * @return Worker
     */
    public static Worker fromContainer(Container container) {
        Worker worker = new Worker();
        worker.container_id = container.getId();
        String name = container.getNames()[0];
        worker.name = name.substring(0, Math.min(20, name.length()));
        String image = container.getImage();
        worker.image = image.substring(0, Math.min(20, image.length()));
        ContainerPort[] ports = container.getPorts();

        if (ports != null && ports.length > 0) {
            worker.public_port = ports[0].getPublicPort();
            worker.private_port = ports[0].getPrivatePort();
            worker.port_type = ports[0].getType();
            worker.ip = ports[0].getIp();
        }
        String status = container.getStatus();
        String state = container.getState();
        worker.status = status.substring(0, Math.min(20, status.length()));
        worker.state = state.substring(0, Math.min(20, state.length()));
        return worker;
    }

    /**
     * Create a list of workers from a list of containers
     * 
     * @param containers
     * @return List<Worker>
     */
    public static List<Worker> fromContainers(List<Container> containers) {
        List<Worker> workers = containers.stream().map(Worker::fromContainer).collect(Collectors.toList());
        return workers;
    }
}
