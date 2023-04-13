package ai.openfabric.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.github.dockerjava.api.model.MemoryStatsConfig;

// import lombok.Data;
import lombok.Getter;
import lombok.Setter;
// import lombok.var;

@Entity
@Table(name = "worker_memory")
public class WorkerMemory extends Datable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "of-uuid")
    @GenericGenerator(name = "of-uuid", strategy = "ai.openfabric.api.model.IDGenerator")
    @Getter
    @Setter
    @Column(length = 32)
    @NotNull
    public String id;

    public long mem_usage;
    public long mem_limit;

    @ManyToOne(fetch = javax.persistence.FetchType.LAZY, optional = true)
    @JoinColumn(name = "worker.id")
    public Worker worker;

    public WorkerMemory fromMemoryStats(Worker worker, MemoryStatsConfig memoryStats) {
        this.mem_usage = memoryStats.getUsage();
        this.mem_limit = memoryStats.getLimit();
        this.worker = worker;
        return this;
    }
}
