package com.github.sbugat.ec2tools.service;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Loaded program options
 * 
 * @author Sylvain Bugat
 * 
 */
public class ProgramOptions {

	private final boolean executionOption;

	private final boolean listOption;

	private final boolean checkOption;

	private final boolean postCheckOption;

	private final List<String> sectionOptions;

	public ProgramOptions(final boolean executionOptionArg, final boolean listOptionArg, final boolean checkOptionArg, final boolean postCheckOptionArg, final List<String> sectionOptionsArg) {
		executionOption = executionOptionArg;
		listOption = listOptionArg;
		checkOption = checkOptionArg;
		postCheckOption = postCheckOptionArg;

		sectionOptions = ImmutableList.copyOf(sectionOptionsArg);
	}

	public boolean hasExecutionOption() {
		return executionOption;
	}

	public boolean hasListOption() {
		return listOption;
	}

	public boolean hasCheckOption() {
		return checkOption;
	}

	public boolean hasPostCheckOption() {
		return postCheckOption;
	}

	public List<String> getSectionOptions() {
		return sectionOptions;
	}

	public boolean hasNoSectionOption() {
		return sectionOptions.isEmpty();
	}
}