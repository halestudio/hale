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
package de.fhg.igd.mapviewer.server.wms.wizard.pages;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.fhg.igd.mapviewer.concurrency.Callback;
import de.fhg.igd.mapviewer.concurrency.Concurrency;
import de.fhg.igd.mapviewer.concurrency.IJob;
import de.fhg.igd.mapviewer.concurrency.Job;
import de.fhg.igd.mapviewer.concurrency.Progress;

/**
 * Concurrent validator
 * 
 * @author Simon Templer
 */
public class ConcurrentValidator {

	/**
	 * Validation call-back wrapper
	 */
	private class CallbackWrapper implements Callback<Boolean> {

		private final Callback<Boolean> callback;

		/**
		 * Constructor
		 * 
		 * @param callback the wrapped call-back
		 */
		public CallbackWrapper(Callback<Boolean> callback) {
			this.callback = callback;
		}

		/**
		 * @see Callback#done(Object)
		 */
		@Override
		public void done(Boolean result) {
			if (result != null) {
				stateLock.lock();
				valid = result; // set validity value
				stateLock.unlock();
				callback.done(result);
			}
		}

		/**
		 * @see Callback#failed(Throwable)
		 */
		@Override
		public void failed(Throwable e) {
			stateLock.lock();
			valid = false; // set validity value
			stateLock.unlock();
			callback.failed(e);
		}

	}

	/**
	 * Validation interface
	 */
	public interface Validation {

		/**
		 * Validate something
		 * 
		 * @return if the validation result is valid
		 * @throws Exception if an error occurs
		 */
		public boolean validate() throws Exception;
	}

	private boolean valid;

	private final Lock stateLock = new ReentrantLock();

	private Validation lastValidation = null;

	private final Lock validationLock = new ReentrantLock();

	private final Callback<Boolean> callback;

	/**
	 * Constructor
	 * 
	 * @param callback the call-back for validation updates
	 * @param valid the initial valid state
	 */
	public ConcurrentValidator(final Callback<Boolean> callback, boolean valid) {
		this.valid = valid;
		this.callback = callback;
	}

	/**
	 * Determine the valid state
	 * 
	 * @return the valid state
	 */
	public boolean isValid() {
		stateLock.lock();
		try {
			return valid;
		} finally {
			stateLock.unlock();
		}
	}

	/**
	 * Run a validation
	 * 
	 * @param validation the validation to run
	 */
	public void runValidation(final Validation validation) {
		validationLock.lock();
		try {
			lastValidation = validation;
		} finally {
			validationLock.unlock();
		}

		IJob<Boolean> job = new Job<Boolean>(Messages.ConcurrentValidator_0,
				new CallbackWrapper(callback)) {

			@Override
			public Boolean work(Progress progress) throws Exception {
				try {
					Boolean result = validation.validate();
					validationLock.lock();
					if (lastValidation != validation) {
						// ignore this validation result
						result = null;
					}
					validationLock.unlock();
					return result;
				} catch (Exception e) {
					if (lastValidation == validation) {
						throw e; // only throw errors that result in state
									// changes
					}
					else
						return null;
				}
			}
		};

		Concurrency.startJob(job);
	}

}
