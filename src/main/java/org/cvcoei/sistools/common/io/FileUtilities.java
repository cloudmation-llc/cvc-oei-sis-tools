/*
 * Copyright 2020 California Community Colleges Chancellor's Office
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cvcoei.sistools.common.io;

import com.ibm.icu.text.CharsetDetector;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Collection of utilities for working with local files.
 */
public class FileUtilities {

    /**
     * Create a Reader with additional logic to first detect the correct character set to use. Uses ICU4J library
     * to perform the character set detection. This improves cross-platform support by seamlessly supporting
     * UTF-8 files that may be created on a Unix type system, or UTF-16 files which can be common on Windows platofrms.
     * @param path Path to source file to read
     * @return Typed pair of both the reader and detected character set
     * @throws IOException
     */
    public static ReaderWithCharset getReaderWithCharsetDetection(Path path) throws IOException {
        // Create charset detector from ICU4J library
        CharsetDetector detector = new CharsetDetector();

        // Perform detection
        detector.setText(Files.readAllBytes(path));
        final Charset detectedCharset = Charset.forName(detector.detect().getName());

        // Create a reader using the matched character set
        return new ReaderWithCharset(
            Files.newBufferedReader(path, detectedCharset),
            detectedCharset);
    }

    /**
     * Move a file without the checked IOException. If an errors it will be thrown as an unchecked RuntimeException.
     * @param source Path to source file
     * @param destination Path to destination
     * @return The destination path
     */
    public static Path move(Path source, Path destination) {
        try {
            return Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
        }
        catch(Exception exception) {
            // Rethrow as unchecked exception
            throw new RuntimeException(exception);
        }
    }

}
