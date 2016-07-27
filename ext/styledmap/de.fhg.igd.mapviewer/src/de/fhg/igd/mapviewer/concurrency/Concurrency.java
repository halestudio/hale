/*
 * Copyright (c) 2016 Fraunhofer IGD
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.concurrency;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import de.fhg.igd.osgi.util.SingleServiceTracker;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import de.fhg.igd.slf4jplus.ATransaction;

/**
 * Helper for running concurrent jobs based on an {@link Executor} available as
 * OSGi service.
 * 
 * @author Simon Templer
 */
public class Concurrency extends SingleServiceTracker<Executor>implements Executor {

	private static final ALogger log = ALoggerFactory.getLogger(Concurrency.class);

	/**
	 * Job wrapper. Begins and ends log transactions if necessary
	 * 
	 * @param <T> the job result type
	 */
	private static class JobWrapper<T> implements IJob<T> {

		private final IJob<T> job;

		private CallbackWrapper<T> callbackWrapper;

		/**
		 * @param job the internal job
		 */
		public JobWrapper(IJob<T> job) {
			super();

			this.job = job;
		}

		/**
		 * Set a call-back wrapper
		 * 
		 * @param callbackWrapper the call-back wrapper to set
		 */
		public void setCallbackWrapper(CallbackWrapper<T> callbackWrapper) {
			this.callbackWrapper = callbackWrapper;

			if (callbackWrapper != null) {
				callbackWrapper.setCallback(job.getCallback());
			}
		}

		/**
		 * @see IJob#getCallback()
		 */
		@Override
		public Callback<T> getCallback() {
			if (callbackWrapper != null) {
				return callbackWrapper;
			}
			else {
				return job.getCallback();
			}
		}

		/**
		 * @see IJob#getName()
		 */
		@Override
		public String getName() {
			return job.getName();
		}

		/**
		 * @see IJob#isBackground()
		 */
		@Override
		public boolean isBackground() {
			return job.isBackground();
		}

		/**
		 * @see IJob#isHidden()
		 */
		@Override
		public boolean isHidden() {
			return job.isHidden();
		}

		/**
		 * @see IJob#isLogTransaction()
		 */
		@Override
		public boolean isLogTransaction() {
			return job.isLogTransaction();
		}

		/**
		 * @see IJob#isModal()
		 */
		@Override
		public boolean isModal() {
			return job.isModal();
		}

		/**
		 * @see IJob#isExclusive()
		 */
		@Override
		public boolean isExclusive() {
			return job.isExclusive();
		}

		/**
		 * @see IJob#work(Progress)
		 */
		@Override
		public T work(Progress progress) throws Exception {
			ATransaction logTrans;
			if (job.isLogTransaction()) {
				logTrans = log.begin(getName());
			}
			else {
				logTrans = null;
			}
			try {
				return job.work(progress);
			} finally {
				if (logTrans != null) {
					logTrans.end();
				}
			}
		}

	}

	/**
	 * Wrapper for call-backs that provides the means of getting notified on job
	 * completion
	 * 
	 * @param <T> the job result type
	 */
	private static abstract class CallbackWrapper<T> implements Callback<T> {

		/**
		 * The inner call-back
		 */
		private Callback<T> callback;

		/**
		 * @param callback the call-back to set
		 */
		public void setCallback(Callback<T> callback) {
			this.callback = callback;
		}

		/**
		 * @see Callback#done(java.lang.Object)
		 */
		@Override
		public void done(T result) {
			try {
				if (callback != null) {
					callback.done(result);
				}
			} finally {
				finished(true, result, null);
			}
		}

		/**
		 * Called when the job has been execution has been finished
		 * 
		 * @param success if the job completed successfully
		 * @param result the job result if any
		 * @param error the error while executing the job if it was not
		 *            successful
		 */
		protected abstract void finished(boolean success, T result, Throwable error);

		/**
		 * @see Callback#failed(Throwable)
		 */
		@Override
		public void failed(Throwable e) {
			try {
				if (callback != null) {
					callback.failed(e);
				}
			} finally {
				finished(false, null, e);
			}
		}

	}

	private static final Concurrency instance = new Concurrency();

	/**
	 * Get the job executor
	 * 
	 * @return the job executor
	 */
	public static Executor getExecutor() {
		return instance;
	}

	/**
	 * Get the concurrency instance
	 * 
	 * @return the concurrency instance
	 */
	public static Concurrency getInstance() {
		return instance;
	}

	/**
	 * Start/schedule a job if possible
	 * 
	 * @param <T> the job result type
	 * @param job the job
	 * @throws NullPointerException if there is no {@link Executor} service
	 *             available
	 */
	public static <T> void startJob(IJob<T> job) {
		instance.start(new JobWrapper<T>(job));
	}

	/**
	 * Execute a job and wait for the result
	 * 
	 * @param <T> the job result type
	 * @param job the job
	 * @return the job result
	 * @throws ExecutionException if executing the job fails
	 * @throws NullPointerException if there is no {@link Executor} service
	 *             available
	 */
	public static <T> T startAndWait(IJob<T> job) throws ExecutionException {
		final AtomicBoolean finished = new AtomicBoolean(false);

		final AtomicBoolean aSuccess = new AtomicBoolean();
		final AtomicReference<T> aResult = new AtomicReference<T>();
		final AtomicReference<Throwable> aError = new AtomicReference<Throwable>();

		final Thread current = Thread.currentThread();

		JobWrapper<T> exec = new JobWrapper<T>(job);
		exec.setCallbackWrapper(new CallbackWrapper<T>() {

			@Override
			protected void finished(boolean success, T result, Throwable error) {
				// set as finished
				finished.set(true);

				// set results
				aSuccess.set(success);
				aResult.set(result);
				aError.set(error);

				// notify thread
				synchronized (current) {
					current.notify();
				}
			}

		});

		instance.start(exec);

		synchronized (current) {
			while (!finished.get()) {
				try {
					current.wait();
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}

		if (aSuccess.get()) {
			return aResult.get();
		}
		else {
			throw new ExecutionException("Error executing job: " + job.getName(), aError.get());
		}
	}

	/**
	 * Creates a {@link Concurrency} instance
	 */
	protected Concurrency() {
		super(Executor.class);
	}

	/**
	 * @see Executor#start(IJob)
	 */
	@Override
	public <T> void start(IJob<T> job) {
		Executor exec = getService();
		if (exec != null) {
			exec.start(job);
		}
		// TODO queue jobs for later execution if there is no service?
		else
			throw new NullPointerException("No executor service available");
	}

}
