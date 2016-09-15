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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Job that allows handling partial results
 * 
 * @param <T> the partial result type
 * @author Simon Templer
 */
public abstract class AbstractContinuousJob<T> extends Job<Void> {

	private boolean completed = false;

	private List<T> results = new ArrayList<T>();

	private final Lock resultsLock = new ReentrantLock();

	private List<T> processing = new ArrayList<T>();

	private final Lock processingLock = new ReentrantLock();

	private ProcessingStrategy strategy = new DefaultProcessingStrategy(10, 100);

	/**
	 * Constructor
	 * 
	 * @param name the job name
	 */
	public AbstractContinuousJob(String name) {
		super(name);
	}

	/**
	 * @see Job#work(Progress)
	 */
	@Override
	final public Void work(Progress progress) throws Exception {
		try {
			doWork(progress);
		} catch (Throwable e) {
			error(e);
		} finally {
			completed = true;
			trigger();
		}
		return null;
	}

	/**
	 * Called when an error occurred while executing {@link #doWork(Progress)}
	 * 
	 * @param e the error
	 */
	protected abstract void error(Throwable e);

	/**
	 * Gets the work done, publish partial results using the
	 * {@link #publish(Object)} method.
	 * 
	 * @param progress the job progress
	 * @throws Exception if an error occurs
	 */
	protected abstract void doWork(Progress progress) throws Exception;

	/**
	 * Publish an object for processing
	 * 
	 * @param object the object to publish
	 */
	protected void publish(T object) {
		resultsLock.lock();
		try {
			results.add(object);
		} finally {
			resultsLock.unlock();
		}

		trigger();
	}

	/**
	 * Trigger processing
	 */
	protected void trigger() {
		if (processingLock.tryLock()) {
			if (processing.isEmpty()) {
				// last processing is finished

				resultsLock.lock();
				try {
					boolean allowed = strategy.allowProcess(results.size());

					if (allowed || completed) { // check if processing is
												// allowed
						List<T> temp = processing;
						processing = results;
						results = temp;
						resultsLock.unlock();

						if (!processing.isEmpty()) {
							// process collected results
							Thread thread = new Thread(new Runnable() {

								@Override
								public void run() {
									processingLock.lock();
									try {
										process(new ArrayList<T>(processing));
									} finally {
										processing.clear();
										processingLock.unlock();
										trigger();
									}
								}

							});
							thread.start();
						}
					}
					else {
						resultsLock.unlock();
					}
				} catch (Throwable e) {
					resultsLock.unlock();
				}
			}

			processingLock.unlock();
		}
	}

	/**
	 * Process the results
	 * 
	 * @param currentResults the list of current partial results
	 */
	protected abstract void process(List<T> currentResults);

	/**
	 * @param strategy the strategy to set
	 */
	public void setStrategy(ProcessingStrategy strategy) {
		this.strategy = strategy;
	}

}
