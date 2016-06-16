/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.jet.processors;


import com.hazelcast.jet.container.ProcessorContext;
import com.hazelcast.jet.data.io.ConsumerOutputStream;
import com.hazelcast.jet.data.io.ProducerInputStream;
import com.hazelcast.jet.dag.Vertex;
import com.hazelcast.jet.io.tuple.Tuple;
import com.hazelcast.jet.processor.ContainerProcessor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;


public class VerySlowProcessorOnlyForInterruptionTest implements ContainerProcessor<Tuple<Object, Object>, Tuple<Object, Object>> {
    public static volatile CountDownLatch run;
    public static volatile boolean set;

    @Override
    public boolean process(ProducerInputStream<Tuple<Object, Object>> inputStream,
                           ConsumerOutputStream<Tuple<Object, Object>> outputStream,
                           String sourceName,
                           ProcessorContext processorContext) throws Exception {
        if (!set) {
            run.countDown();
            set = true;
        }

        for (Tuple<Object, Object> t : inputStream) {
            LockSupport.parkNanos(100_000L);
            outputStream.consume(t);
        }

        return true;
    }
}