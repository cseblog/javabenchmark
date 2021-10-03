/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sample;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1, warmups = 1, jvmArgs = {"-Xms1G", "-Xmx2G"})
public class MyBenchmark {

    @Benchmark
    public void testLoopMethod(Blackhole blackhole, BenchmarkState state) throws InterruptedException {
        for (int i = 0; i < state.N; i++) {
            String s = state.data.get(i);
            blackhole.consume(s);
        }
    }

    @Benchmark
    public void testWhileMethod(Blackhole blackhole, BenchmarkState state) {
        int i = 0;
        while (i < state.N) {
            String s = state.data.get(i);
            blackhole.consume(s);
            i++;
        }
    }

    @Benchmark
    public void testForeachMethod(Blackhole blackhole, BenchmarkState state) {
        for (String s : state.data) {
            blackhole.consume(s);
        }
    }

    @Benchmark
    public void testIterator(Blackhole blackhole, BenchmarkState state) {
        Iterator<String> iterator = state.data.iterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            blackhole.consume(s);
        }
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({"1000", "10000", "100000", "1000000"})
        private int N = 10_000_000;

        private List<String> data;

        private List<String> createData() {
            List<String> data = new ArrayList<>();
            for (int i = 0; i < N; i++) {
                data.add("Number : " + i);
            }
            return data;
        }

        @Setup
        public void setup() {
            data = createData();
        }
    }

}
