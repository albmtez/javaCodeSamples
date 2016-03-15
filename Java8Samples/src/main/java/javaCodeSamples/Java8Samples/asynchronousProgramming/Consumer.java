package javaCodeSamples.Java8Samples.asynchronousProgramming;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Consumer {
	
	/** Providers list. */
	private final List<Provider> providers = Arrays.asList(new Provider("Provider 1"),
			new Provider("Provider 2"),
			new Provider("Provider 3"),
			new Provider("Provider 4"),
			new Provider("Provider 5"),
			new Provider("Provider 6"),
			new Provider("Provider 7"),
			new Provider("Provider 8"),
			new Provider("Provider 9"),
			new Provider("Provider 10"));
	
	private void iterativeSynchronous() {
		long start = System.nanoTime();
		List<String> prices = providers.stream()
				.map(p -> String.format("%s price is %.2f", p.getName(), p.getPriceSync("ProductName")))
				.collect(Collectors.toList());
		long duration = (System.nanoTime() - start) / 1_000_000;
		System.out.println(prices);
		System.out.println("Done in " + duration + " msecs");
	}
	
	private void parallelStreamSynchronous() {
		long start = System.nanoTime();
		List<String> prices = providers.parallelStream()
				.map(p -> String.format("%s price is %.2f", p.getName(), p.getPriceSync("ProductName")))
				.collect(Collectors.toList());
		long duration = (System.nanoTime() - start) / 1_000_000;
		System.out.println(prices);
		System.out.println("Done in " + duration + " msecs");
	}
	
	private void remoteAsynchronous() {
		long start = System.nanoTime();
		Future<Double> futurePrice = providers.get(0).getPriceAsync("ProductName");
		long invocation = (System.nanoTime() - start) / 1_000_000;
		System.out.println("Invocation returned after " + invocation + "msecs");
		
		// delaying....
		IntStream.range(1, 100).forEach(System.out::print);;
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		try {
			double price = futurePrice.get();
			System.out.printf("\nPrice is %.2f%n", price);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		long retrieval = (System.nanoTime() - start) / 1_000_000;
		System.out.println("Price returned after " + retrieval + "msecs");
	}
	
	private void remoteAsynchronousShorten() {
		long start = System.nanoTime();
		Future<Double> futurePrice = providers.get(0).getPriceAsync("ProductName");
		long invocation = (System.nanoTime() - start) / 1_000_000;
		System.out.println("Invocation returned after " + invocation + "msecs");
		
		// delaying....
		IntStream.range(1, 100).forEach(System.out::print);;
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		try {
			double price = futurePrice.get();
			System.out.printf("\nPrice is %.2f%n", price);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		long retrieval = (System.nanoTime() - start) / 1_000_000;
		System.out.println("Price returned after " + retrieval + "msecs");
	}
	
	private void asynchronous() {
		long start = System.nanoTime();
		List<CompletableFuture<String>> priceFutures = providers.stream()
				.map(provider -> CompletableFuture.supplyAsync(
						() -> provider.getName() + " price is " + provider.getPriceSync("ProductName")))
				.collect(Collectors.toList());
		List<String> prices = priceFutures.stream()
				.map(CompletableFuture::join)
				.collect(Collectors.toList());
		long duration = (System.nanoTime() - start) / 1_000_000;
		prices.stream().forEach(System.out::println);
		System.out.println("Done in " + duration + " msecs");
	}
	
	private void asynchronousExecutor() {
		long start = System.nanoTime();
		List<CompletableFuture<String>> priceFutures = providers.stream()
				.map(provider -> CompletableFuture.supplyAsync(
						() -> provider.getName() + " price is " + provider.getPriceSync("ProductName"), executor))
				.collect(Collectors.toList());
		List<String> prices = priceFutures.stream()
				.map(CompletableFuture::join)
				.collect(Collectors.toList());
		long duration = (System.nanoTime() - start) / 1_000_000;
		prices.stream().forEach(System.out::println);
		System.out.println("Done in " + duration + " msecs");
	}
	
	/**
	 * Custom thread pool fixed to 100 threads maximum.
	 */
	private final Executor executor = Executors.newFixedThreadPool(100, new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setDaemon(true);
				return t;
			}
	});
	
	public static void main(String[] args) {
		Consumer consumer = new Consumer();
		
		System.out.println("Iterative Synchronous");
		consumer.iterativeSynchronous();
		System.out.println("Parallel synchronous");
		consumer.parallelStreamSynchronous();
		System.out.println("Remote Asynchronous");
		consumer.remoteAsynchronous();
		System.out.println("Remote Asynchronous shorten");
		consumer.remoteAsynchronousShorten();
		System.out.println("Asynchronous");
		consumer.asynchronous();
		System.out.println("Asynchronous with executor");
		consumer.asynchronousExecutor();
	}

}
