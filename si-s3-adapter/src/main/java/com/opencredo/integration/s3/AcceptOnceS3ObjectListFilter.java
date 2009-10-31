package com.opencredo.integration.s3;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jets3t.service.model.S3Object;

public class AcceptOnceS3ObjectListFilter {
	
	private final Queue<S3Object> seen;

	private final Object monitor = new Object();

	public AcceptOnceS3ObjectListFilter(int maxCapacity) {
		this.seen = new LinkedBlockingQueue<S3Object>(maxCapacity);
	}

	/**
	 * Creates an AcceptOnceFileFilter based on an unbounded queue.
	 */
	public AcceptOnceS3ObjectListFilter() {
		this.seen = new LinkedBlockingQueue<S3Object>();
	}

	public final List<S3Object> filterS3Objects(S3Object[] s3Objects) {
		List<S3Object> accepted = new ArrayList<S3Object>();
		if (s3Objects != null) {
			for (S3Object s3Object : s3Objects) {
				if (this.accept(s3Object)) {
					accepted.add(s3Object);
				}
			}
		}
		return accepted;
	}

	protected boolean accept(S3Object s3Object) {
		synchronized (this.monitor) {
			if (seen.contains(s3Object)) {
				return false;
			}
			if (!seen.offer(s3Object)) {
				seen.poll();
				seen.add(s3Object);
			}
			return true;
		}
	}

}
