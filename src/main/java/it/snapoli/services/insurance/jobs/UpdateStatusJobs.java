package it.snapoli.services.insurance.jobs;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import it.snapoli.services.insurance.insurance.InsuranceEntity;
import it.snapoli.services.insurance.insurance.InsuranceEntity.Status;
import it.snapoli.services.insurance.insurance.InsuranceRepository;
import lombok.extern.jbosslog.JBossLog;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@ApplicationScoped
@JBossLog
public class UpdateStatusJobs {

  @Inject
  InsuranceRepository insuranceRepository;

  @Scheduled(cron="0 0 * * * ?")
  void cronJob(ScheduledExecution execution) {
    log.info("update status");
    List<InsuranceEntity> all = insuranceRepository.findAllByStatusIn(List.of(
        Status.ACTIVE,
        Status.OUTOFCOVERAGE,
        Status.EXPIRY,
        Status.EXPIRED)).stream().filter(v -> v.getEndTime() != null && v.getEndCoverage() != null).toList();

    all.forEach(new Consumer<InsuranceEntity>() {
      @Override
      public void accept(InsuranceEntity insuranceEntity) {
        if(insuranceEntity.getEndTime().isBefore(LocalDate.now().plusDays(1))){
          insuranceEntity.setStatus(Status.EXPIRY);
        }
        //2024
        if(insuranceEntity.getEndCoverage().isBefore(LocalDate.now().plusDays(1))){
          insuranceEntity.setStatus(Status.OUTOFCOVERAGE);
        }

        insuranceRepository.save(insuranceEntity);
      }
    });
  }
}
