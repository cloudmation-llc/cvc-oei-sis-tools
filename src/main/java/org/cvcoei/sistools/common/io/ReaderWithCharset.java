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

import org.apache.commons.lang3.tuple.MutablePair;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * A pair (tuple) class that carries both a Reader instance and the associated charset set for the underlying
 * file content.
 */
public class ReaderWithCharset extends MutablePair<Reader, Charset> implements Closeable {

    public ReaderWithCharset(Reader left, Charset right) {
        super(left, right);
    }

    public Reader getReader() {
        return this.left;
    }

    public Charset getCharset() {
        return this.right;
    }

    @Override
    public void close() throws IOException {
        this.left.close();
    }

}
