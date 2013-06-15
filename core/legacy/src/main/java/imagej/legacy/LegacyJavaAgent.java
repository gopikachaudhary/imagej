/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2013 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package imagej.legacy;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * A Java agent to help with legacy issues.
 * 
 * @author Johannes Schindelin
 */
public class LegacyJavaAgent implements ClassFileTransformer {
	/**
	 * The premain method started at JVM startup.
	 * 
	 * When this class is specified as <i>Premain-Class</i> in the manifest and
	 * the JVM is started with the option
	 * <tt>-javaagent:/path/to/ij-legacy.jar</tt> then this method is called
	 * some time before the <i>main</i> method of the main class is called.
	 * 
	 * @param agentArgs the optional argument passed  via <tt>-javaagent:ij-legacy.jar=ARGUMENT</tt>
	 * @param instrumentation the {@link Instrumentation} instance passed by the JVM
	 */
	public static void premain(final String agentArgs, final Instrumentation instrumentation) {
		System.err.println("The legacy agent was started with the argument: " + agentArgs);
		instrumentation.addTransformer(new LegacyJavaAgent());
	}

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		if (className.startsWith("ij.") || className.startsWith("ij/")) {
			reportCaller("Loading " + className + " into " + loader + "!");
		}
		return null;
	}

	private static void reportCaller(final String message) {
		System.err.println(message);
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		if (trace == null) return;
		int i = 0;
		// skip Thread#getStackTrace, #reportCaller and #transform
		while (i < trace.length && isCoreClass(trace[i].getClassName())) i++;
		for (; i < trace.length; i++) {
			final StackTraceElement element = trace[i];
			System.err.println("\tat " + element.getClassName() + "." + element.getMethodName()
					+ "(" + element.getFileName() + ":" + element.getLineNumber() + ")");
		}
	}

	private final static ClassLoader bootstrapClassLoader = ClassLoader.getSystemClassLoader().getParent();

	private static boolean isCoreClass(final String className) {
		if (LegacyJavaAgent.class.getName().equals(className)) return true;
		try {
			bootstrapClassLoader.loadClass(className);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
}
