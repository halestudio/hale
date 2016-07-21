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

/**
 * Abstract {@link IJob} implementation.
 * 
 * @author Simon Templer
 * @param <R> the job result type
 */
public abstract class Job<R> implements IJob<R> {

	private Callback<R> callback;

	private final String name;

	private boolean background = true;

	private boolean hidden = false;

	private boolean modal = false;

	/**
	 * if the job execution shall be exclusive for jobs with the same name
	 */
	private boolean exclusive = false;

	/**
	 * if a log transaction shall be created for the job
	 */
	private boolean logTransaction = false;

	/**
	 * Creates a job
	 * 
	 * @param name the job name
	 */
	public Job(String name) {
		this(name, null);
	}

	/**
	 * Creates a worker with the given call-back
	 * 
	 * @param name the job name
	 * @param callback the call-back
	 */
	public Job(String name, Callback<R> callback) {
		super();

		this.callback = callback;
		this.name = name;
	}

	/**
	 * @see IJob#getCallback()
	 */
	@Override
	public Callback<R> getCallback() {
		return callback;
	}

	/**
	 * Set the call-back
	 * 
	 * @param callback the call-back to set
	 */
	public void setCallback(Callback<R> callback) {
		this.callback = callback;
	}

	/**
	 * Set if the job execution shall be exclusive for jobs with the same name
	 * 
	 * @param exclusive if the job execution shall be exclusive for jobs with
	 *            the same name
	 */
	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}

	/**
	 * @see IJob#isExclusive()
	 */
	@Override
	public boolean isExclusive() {
		return exclusive;
	}

	/**
	 * Set if a log transaction shall be created for the job
	 * 
	 * @param logTransaction if a log transaction shall be created for the job
	 */
	public void setLogTransaction(boolean logTransaction) {
		this.logTransaction = logTransaction;
	}

	/**
	 * @see IJob#isLogTransaction()
	 */
	@Override
	public boolean isLogTransaction() {
		return logTransaction;
	}

	/**
	 * @see IJob#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @see IJob#isModal()
	 */
	@Override
	public boolean isModal() {
		return modal;
	}

	/**
	 * @param modal the modal to set
	 */
	public void setModal(boolean modal) {
		this.modal = modal;
	}

	/**
	 * @see IJob#isHidden()
	 */
	@Override
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * @param hidden the hidden to set
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * @see IJob#isBackground()
	 */
	@Override
	public boolean isBackground() {
		return background;
	}

	/**
	 * @param background the background to set
	 */
	public void setBackground(boolean background) {
		this.background = background;
	}

	/**
	 * @see IJob#work(Progress)
	 */
	@Override
	public abstract R work(Progress progress) throws Exception;

}
