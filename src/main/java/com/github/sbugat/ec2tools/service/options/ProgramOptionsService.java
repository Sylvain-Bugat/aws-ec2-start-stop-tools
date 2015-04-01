package com.github.sbugat.ec2tools.service.options;

import gnu.getopt.Getopt;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.github.sbugat.ec2tools.EC2StartStopMain;

public class ProgramOptionsService {

	/*** SLF4J XLogger. */
	private static final XLogger log = XLoggerFactory.getXLogger(ProgramOptionsService.class);

	private static final String USAGE = "Usage: [-l] [-e|-c|-p -s <section1> -s <section2> ... ]";
	private static final String USAGE_LIST = "[-l]: list configuration ";
	private static final String USAGE_EXECUTE = "[-e]: execute sections orders";
	private static final String USAGE_CHECK = "[-c]: check if sections orders can be done";
	private static final String USAGE_POST_CHECK = "[-p]: check if sections orders has been done";
	private static final String USAGE_SECTIONS = "[-s <section1> -s <section2> ... ]: sections to process";

	public ProgramOptions processProgramArgs(final String programArgs[]) {

		// Arguments checking
		final Getopt getOpt = new Getopt(EC2StartStopMain.class.getSimpleName(), programArgs, ":lecps:");
		getOpt.setOpterr(false);

		boolean list = false;
		boolean check = false;
		boolean execute = false;
		boolean postCheck = false;
		int exclusiveOptionsCount = 0;
		final List<String> sectionsToExecute = new ArrayList<String>();

		int option = getOpt.getopt();
		while (-1 != option) {

			switch (option) {

			case 'l':
				list = true;
				exclusiveOptionsCount++;
				break;

			case 'e':
				execute = true;
				exclusiveOptionsCount++;
				break;

			case 'c':
				check = true;
				exclusiveOptionsCount++;
				break;

			case 'p':
				postCheck = true;
				exclusiveOptionsCount++;
				break;

			case 's':
				sectionsToExecute.add(getOpt.getOptarg());
				break;

			case '?':
			default:
				System.err.println(getUsage());
				final IllegalArgumentException exception = new IllegalArgumentException("Unknow option:" + option);
				log.exit(exception);
				throw exception;
			}

			option = getOpt.getopt();
		}

		// Missing a mandatory option
		if (0 == exclusiveOptionsCount) {
			System.err.println(getUsage());
			final IllegalArgumentException exception = new IllegalArgumentException("One of list, execute, check and post-check options is mandatory ");
			log.exit(exception);
			throw exception;
		}
		// Multiple exclusive options
		else if (exclusiveOptionsCount > 1) {
			System.err.println(getUsage());
			final IllegalArgumentException exception = new IllegalArgumentException("List, execute, check and post-check options are exclusive");
			log.exit(exception);
			throw exception;
		}
		// Missing section option
		else if (sectionsToExecute.isEmpty() && !list) {

			System.err.println(getUsage());
			final IllegalArgumentException exception = new IllegalArgumentException("Execute, check and post-check options need one or more section option");
			log.exit(exception);
			throw exception;
		}
		// No getopt options
		else if (getOpt.getOptind() < programArgs.length) {

			System.err.println(getUsage());
			final IllegalArgumentException exception = new IllegalArgumentException("Non-option argument detected, check the usage and all arguments");
			log.exit(exception);
			throw exception;
		}

		// All check are OK
		final ProgramOptions programOptions = new ProgramOptions(execute, list, check, postCheck, sectionsToExecute);
		log.exit(programOptions);
		return programOptions;
	}

	/**
	 * Return the usage multi-line String to display
	 * 
	 * @return usage multi-line String
	 */
	public String getUsage() {
		final StringBuilder usage = new StringBuilder(USAGE);
		usage.append(System.lineSeparator());
		usage.append(USAGE_LIST);
		usage.append(System.lineSeparator());
		usage.append(USAGE_EXECUTE);
		usage.append(System.lineSeparator());
		usage.append(USAGE_CHECK);
		usage.append(System.lineSeparator());
		usage.append(USAGE_POST_CHECK);
		usage.append(System.lineSeparator());
		usage.append(USAGE_SECTIONS);
		return usage.toString();
	}
}
