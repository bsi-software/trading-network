package com.bsiag.ethereum.fxtradingnetwork.server;

import java.util.concurrent.TimeUnit;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.ExceptionHandler;
import org.eclipse.scout.rt.platform.job.FixedDelayScheduleBuilder;
import org.eclipse.scout.rt.platform.job.Jobs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bsiag.ethereum.fxtradingnetwork.server.jobs.ReloadOrdersFromOrderBookJob;
import com.bsiag.ethereum.fxtradingnetwork.server.sql.SuperUserRunContextProducer;

public class JobUtility {
  private static final Logger LOG = LoggerFactory.getLogger(JobUtility.class);

  public static void registerJobs() {
    registerReloadOrdersFromOrderBookJob();
  }

  private static void registerReloadOrdersFromOrderBookJob() {
    Jobs.schedule(new ReloadOrdersFromOrderBookJob(),
        Jobs.newInput()
            .withName(ReloadOrdersFromOrderBookJob.ID)
            .withRunContext(BEANS.get(SuperUserRunContextProducer.class).produce())
            .withExecutionTrigger(Jobs.newExecutionTrigger()
                .withSchedule(FixedDelayScheduleBuilder.repeatForever(10, TimeUnit.SECONDS)))
            .withExceptionHandling(new ExceptionHandler() {
              @Override
              public void handle(Throwable t) {
                LOG.error("Error on execution of job " + ReloadOrdersFromOrderBookJob.ID + ": ", t);
              }
            }, true));
  }

}
