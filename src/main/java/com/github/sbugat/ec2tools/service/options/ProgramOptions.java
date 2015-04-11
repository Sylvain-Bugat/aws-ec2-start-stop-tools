package com.github.sbugat.ec2tools.service.options;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Loaded program options.
 * 
 * @author Sylvain Bugat
 * 
 */
public final class ProgramOptions {

	/** Execution flag option. */
	private final boolean executionOption;

	/** List flag option. */
	private final boolean listOption;

	/** Check flag option. */
	private final boolean checkOption;

	/** Post-check flag option. */
	private final boolean postCheckOption;

	/** List of section options. */
	private final List<String> sectionOptions;

	/**
	 * Copy arguments constructor.
	 * 
	 * @param executionOptionArg execution flag
	 * @param listOptionArg list flag
	 * @param checkOptionArg check flag
	 * @param postCheckOptionArg post-check flag
	 * @param sectionOptionsArg list of section options
	 */
	public ProgramOptions(final boolean executionOptionArg, final boolean listOptionArg, final boolean checkOptionArg, final boolean postCheckOptionArg, final List<String> sectionOptionsArg) {
		executionOption = executionOptionArg;
		listOption = listOptionArg;
		checkOption = checkOptionArg;
		postCheckOption = postCheckOptionArg;

		sectionOptions = ImmutableList.copyOf(sectionOptionsArg);
	}

	/**
	 * Get the execution option.
	 * 
	 * @return execution option
	 */
	public boolean hasExecutionOption() {
		return executionOption;
	}

	/**
	 * Get the list option.
	 * 
	 * @return list option
	 */
	public boolean hasListOption() {
		return listOption;
	}

	/**
	 * Get the check option.
	 * 
	 * @return check option
	 */
	public boolean hasCheckOption() {
		return checkOption;
	}

	/**
	 * Get the post-check option.
	 * 
	 * @return post-check option
	 */
	public boolean hasPostCheckOption() {
		return postCheckOption;
	}

	/**
	 * Get the list of section options.
	 * 
	 * @return list of section options
	 */
	public List<String> getSectionOptions() {
		return sectionOptions;
	}

	/**
	 * Return if no section option exists.
	 * 
	 * @return true if no section option, false otherwise
	 */
	public boolean hasNoSectionOption() {
		return sectionOptions.isEmpty();
	}

	@Override
	public boolean equals(final Object object) {

		if (this == object) {
			return true;
		}

		if (object == null || !ProgramOptions.class.isInstance(object)) {
			return false;
		}

		final ProgramOptions programOptions = (ProgramOptions) object;

		if (executionOption != programOptions.executionOption || listOption != programOptions.listOption || checkOption != programOptions.checkOption || postCheckOption != programOptions.postCheckOption) {
			return false;
		}

		return sectionOptions.equals(programOptions.sectionOptions);
	}
}
