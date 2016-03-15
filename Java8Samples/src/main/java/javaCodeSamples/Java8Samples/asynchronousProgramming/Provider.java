package javaCodeSamples.Java8Samples.asynchronousProgramming;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * Provider.
 * 
 * Simulates the remote service providing data synchronously or asynchronously.
 * 
 * @author amartinez
 *
 */
public class Provider {
	
	/** Provider's name. */
	private final String name;
	
	/**
	 * Constructor.
	 * 
	 * @param name Provider's name.
	 */
	public Provider(String name) {
		this.name = name;
	}
	
	/**
	 * Calculates de price for a product. A delay is used. Uses a synchronous communication.
	 * 
	 * @param product Product name for which the price is asked for.
	 * @return Price for the product.
	 */
	public double getPriceSync(String product) {
		return calculatePrice(product);
	}
	
	/**
	 * Calculates a product's price. A delay is used. Uses an asynchronous communication.
	 * 
	 * @param product Product name for which the price is asked for.
	 * @return Price for the product.
	 */
	public Future<Double> getPriceAsync(String product) {
		CompletableFuture<Double> futurePrice = new CompletableFuture<>();
		new Thread(() -> {
				try {
					double price = calculatePrice(product);
					futurePrice.complete(price);
				} catch (Exception ex) {
					futurePrice.completeExceptionally(ex);
				}
		}).start();
		return futurePrice;
	}
	
	/**
	 * Calculates a product's price. A delay is used. Uses an asynchronous communication.
	 * Uses a shorten way to implement using a Supplier and lambdas.
	 * 
	 * @param product Product name for which the price is asked for.
	 * @return Price for the product.
	 */
	public Future<Double> getPriceAsyncShorten(String product) {
		return CompletableFuture.supplyAsync(() -> calculatePrice(product));
	}
	
	/**
	 * Calculates a product's price. A delay is used. Uses an asynchronous communication.
	 * Uses a shorten way to implement using a Supplier and lambdas.
	 * Custom thread pool.
	 * 
	 * @param product Product name for which the price is asked for.
	 * @return Price for the product.
	 */
	public Future<Double> getPriceAsyncShortenExecutor(String product) {
		return CompletableFuture.supplyAsync(() -> calculatePrice(product), executor);
	}
	
	/**
	 * @return Provider's name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Calculates the price for a given product.
	 * A random calcul is used.
	 * 
	 * @param product Product name.
	 * @return Product price.
	 */
	private double calculatePrice(String product) {
		delay();
		return (new Random()).nextDouble() * Optional.ofNullable(product).filter(p -> p.length() >= 1).orElse("testval").charAt(0)
				+ Optional.ofNullable(product).filter(p -> p.length() >= 2).orElse("testval").charAt(1);
	}
	
	/**
	 * Simulates 1 second delay.
	 */
	private void delay() {
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
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
	
	// Testing...
	public static void main(String[] args) {
		Provider provider = new Provider("Name");
		System.out.println(provider.calculatePrice(null));
		System.out.println(provider.calculatePrice(""));
		System.out.println(provider.calculatePrice("q"));
		System.out.println(provider.calculatePrice("qertret"));
	}
	
}
