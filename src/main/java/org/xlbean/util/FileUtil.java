package org.xlbean.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.xlbean.exception.XlBeanException;

public class FileUtil {

	/**
	 * Copy given {@code file} to on-memory {@link InputStream}.
	 * 
	 * @param file
	 * @return
	 */
	public static InputStream copyToInputStream(File file) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Files.copy(file.toPath(), baos);
			return new ByteArrayInputStream(baos.toByteArray());
		} catch (IOException e) {
			throw new XlBeanException(e);
		}
	}

	/**
	 * Copy {@code in} to on-memory {@link InputStream}.
	 * 
	 * @param in
	 * @return
	 */
	public static InputStream copyToInputStream(InputStream in) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = in.read(buf)) > 0) {
				baos.write(buf, 0, len);
			}
			return new ByteArrayInputStream(baos.toByteArray());
		} catch (IOException e) {
			throw new XlBeanException(e);
		}
	}
}
