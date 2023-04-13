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

import com.github.dockerjava.api.model.CpuStatsConfig;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "worker_cpu")
public class WorkerCPU {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "of-uuid")
    @GenericGenerator(name = "of-uuid", strategy = "ai.openfabric.api.model.IDGenerator")
    @Getter
    @Setter
    @Column(length = 32)
    @NotNull
    public String id;

    public long cpu_total_usage;
    public long cpu_usage_in_kernelmode;
    public long cpu_usage_in_usermode;

    public long system_cpu_usage;
    public long online_cpus;

    @ManyToOne(fetch = javax.persistence.FetchType.LAZY, optional = true)
    @JoinColumn(name = "worker.id")
    public Worker worker;

    public WorkerCPU fromCPUStats(Worker worker, CpuStatsConfig cpuStats) {
        this.cpu_total_usage = cpuStats.getCpuUsage().getTotalUsage();
        this.cpu_usage_in_kernelmode = cpuStats.getCpuUsage().getUsageInKernelmode();
        this.cpu_usage_in_usermode = cpuStats.getCpuUsage().getUsageInUsermode();
        this.system_cpu_usage = cpuStats.getSystemCpuUsage();
        this.online_cpus = cpuStats.getOnlineCpus();
        this.worker = worker;
        return this;
    }
}
