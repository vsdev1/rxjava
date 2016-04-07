import static java.util.Arrays.asList;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompletableFuturesTest {

    private static final Logger LOG = LoggerFactory.getLogger(CompletableFuturesTest.class);

    ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Test
    public void blockingCall() throws Exception {
        int result = someLongRunningComputation();
        LOG.info("Result = {}", result);
    }

    @Test
    public void callable() throws Exception {
        Callable<Integer> task = () -> someLongRunningComputation();
        Future<Integer> future = executorService.submit(task);
        LOG.info("Submitted task");
        Integer result = future.get();
        LOG.info("Result = {}", result);
    }

    @Test
    public void completableFuture() throws Exception {
        final CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> someLongRunningComputation());
        LOG.info("Submitted task");
        Integer result = future.get();
        LOG.info("Result = {}", result);
    }

    @Test
    public void completableFutureWithExecutor() throws Exception {
        final CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> someLongRunningComputation(), executorService);
        LOG.info("Submitted task");
        Integer result = future.get();
        LOG.info("Result = {}", result);
    }

    @Test
    public void completableFutureCallback() throws Exception {
        final CompletableFuture<Integer> f = CompletableFuture.supplyAsync(() -> someLongRunningComputation(), executorService);
        f.thenAccept(result -> LOG.info("Result = {}", result));
        LOG.info("Method done");

        //Thread.sleep(2000);
        f.get();
    }

    @Test
    public void completableFutureChainComputation() throws Exception {
        final CompletableFuture<Integer> f = CompletableFuture.supplyAsync(() -> someLongRunningComputation(), executorService);
        final CompletableFuture<Integer> computation = f.thenApply(result -> result * 1000);
        LOG.info("Result 1 = {}", f.get());
        LOG.info("Result 2 = {}", computation.get());

        CompletableFuture.supplyAsync(this::someLongRunningComputation)
                .thenApply(result -> result * 1000)
                .thenApply(result -> result / 10);
    }

    @Test
    public void composeFutures() throws Exception {
        final CompletableFuture<Integer> result = CompletableFuture.supplyAsync(this::someLongRunningComputation)
                .thenCompose(this::doAnotherComputation);
        LOG.info("Result = {}", result.get());

    }

    @Test
    public void combineFutures() throws Exception {
        final CompletableFuture<Integer> first = CompletableFuture.supplyAsync(this::someLongRunningComputation);
        final CompletableFuture<Integer> second = CompletableFuture.supplyAsync(this::someLongRunningComputation);

        final CompletableFuture<Integer> sum = first.thenCombine(second, (result1, result2) -> result1 + result2);

        LOG.info("Result = {}", sum.get());
    }

    @Test
    public void eitherFuture() throws Exception {
        final CompletableFuture<Integer> first = CompletableFuture.supplyAsync(this::someLongRunningComputation);
        final CompletableFuture<Integer> second = CompletableFuture.supplyAsync(this::someLongRunningComputation);

        final CompletableFuture<Integer> eitherOr = first.applyToEither(second, result -> result * 1000);

        LOG.info("Result = {}", eitherOr.get());

    }

    @Test
    public void allOfFuture() throws Exception {
        final CompletableFuture<Integer> first = CompletableFuture.supplyAsync(this::someLongRunningComputation);
        final CompletableFuture<Integer> second = CompletableFuture.supplyAsync(this::someLongRunningComputation);
        final CompletableFuture<Integer> third = CompletableFuture.supplyAsync(this::someLongRunningComputation);

        final CompletableFuture<Void> allof = CompletableFuture.allOf(first, second, third);
        final CompletableFuture<List<Integer>> allFutures = allof
                .thenApply(r -> asList(first, second, third).stream().map(CompletableFuture::join).collect(Collectors.toList()));

        LOG.info("Result = {}", allFutures.get());

    }

    @Test
    public void anyOfFuture() throws Exception {
        final CompletableFuture<Integer> first = CompletableFuture.supplyAsync(this::someLongRunningComputation);
        final CompletableFuture<Integer> second = CompletableFuture.supplyAsync(this::someLongRunningComputation);
        final CompletableFuture<Integer> third = CompletableFuture.supplyAsync(this::someLongRunningComputation);

        final CompletableFuture<Object> anyof = CompletableFuture.anyOf(first, second, third);

        LOG.info("Result = {}", anyof.get());

    }

    private int someLongRunningComputation() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private CompletableFuture<Integer> doAnotherComputation(int input) {
        return CompletableFuture.completedFuture(input * 1000);
    }
}
