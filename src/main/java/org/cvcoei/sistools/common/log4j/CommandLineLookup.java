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

package org.cvcoei.sistools.common.log4j;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;
import picocli.CommandLine;
import picocli.CommandLine.ParseResult;

/**
 * Lookup plugin for Log4j2 which allows lookups on command line arguments parsed with Pico (https://picocli.info/)
 * A sane alternative to using the built-in MainMapLookup which did not work well.
 */
@Plugin(name = "cmdline", category = StrLookup.CATEGORY)
public class CommandLineLookup implements StrLookup {

    private static ParseResult parseResult;

    public static void parse(CommandLine.Model.CommandSpec spec, String[] args) {
        parseResult = new CommandLine(spec)
            .setUnmatchedArgumentsAllowed(true)
            .parseArgs(args);
    }

    @Override
    public String lookup(String key) {
        if(parseResult == null) {
            return null;
        }

        return parseResult.matchedOptionValue(key, null);
    }

    @Override
    public String lookup(LogEvent event, String key) {
        return lookup(key);
    }

}
