package com.matthisk.flow;

import static com.matthisk.flow.SyncFlow.flow;
import static com.matthisk.flow.operators.Operators.flatMap;
import static com.matthisk.flow.operators.Operators.flattenMerge;
import static com.matthisk.flow.operators.Operators.map;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) {
        ThreadFactory factory =
                Thread.builder()
                        .name("worker")
                        .virtual()
                        .daemon(false)
                        .disallowThreadLocals()
                        .factory();

        try (ExecutorService scheduler = Executors.newFixedThreadPool(1, factory)) {
            Flow<Object> fl =
                    flow(collector -> {
                                collector.emit("Hello.");
                                collector.emit("World.");
                            })
                            .flowOn(scheduler);

            scheduler.submit(
                    () -> {
                        fl.collect(
                                value ->
                                        System.out.printf(
                                                "[%s] %s\n",
                                                Thread.currentThread().getName(), value));
                    });
        }
    }

    public static void main3(String[] args) {
        ThreadFactory factory =
                Thread.builder()
                        .name("worker")
                        .virtual()
                        .daemon(false)
                        .disallowThreadLocals()
                        .factory();
        ExecutorService executorService = Executors.newFixedThreadPool(1_000_000, factory);

        AtomicInteger atomicInt = new AtomicInteger();

        Flow<String> fl =
                flow(
                        collector -> {
                            int index = atomicInt.incrementAndGet();
                            int rounds = 0;

                            while (rounds < 10) {
                                collector.emit(
                                        String.format(
                                                "[%d] time: %s",
                                                index, System.currentTimeMillis()));
                                delay(200);
                                rounds++;
                            }
                        });

        Flow<Integer> fl2 =
                flow(
                        collector -> {
                            collector.emit(1);
                            collector.emit(2);
                            collector.emit(3);
                        });

        fl2.pipe(map(value -> fl), flattenMerge(8, executorService))
                .pipe(
                        map(
                                value -> {
                                    System.out.printf(
                                            "[%s] %s\n", Thread.currentThread().getName(), value);
                                    return value.length();
                                }))
                .flowOn(executorService)
                .collect(
                        size ->
                                System.out.printf(
                                        "[%s] %s\n", Thread.currentThread().getName(), size));
    }

    public static void main2(String[] args) throws InterruptedException {

        Flow<String> flow =
                flow(
                        consumer -> {
                            consumer.emit("joe");
                            delay(1000);
                            consumer.emit("programmer");
                        });

        Flow<Integer> flow2 =
                flow(
                        collector -> {
                            collector.emit(1);
                            delay(500);
                            collector.emit(2);
                            collector.emit(3);
                        });

        flow.pipe(flatMap(value -> flow2)).collect(System.out::println);

        flow.pipe(map(String::getBytes)).collect(System.out::println);
    }

    static void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
