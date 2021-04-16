package br.ce.wcaquino.runner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;

public class ParallelRunner extends BlockJUnit4ClassRunner{

	public ParallelRunner(Class<?> klass) throws InitializationError {
		super(klass);
		setScheduler(new ThreadPoll());
	}

	private static class  ThreadPoll implements RunnerScheduler {

		private ExecutorService executorService;
	
		public ThreadPoll() {
			this.executorService = Executors.newFixedThreadPool(2);
		}
 
		public void schedule(Runnable run) {
			this.executorService.submit(run);
		}

		public void finished() {
			this.executorService.shutdown();
			try {
				this.executorService.awaitTermination(10,TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} 
		
	}
}
