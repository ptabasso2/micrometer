package com.example.demo;


import java.util.concurrent.atomic.AtomicLong;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.statsd.StatsdConfig;
import io.micrometer.statsd.StatsdFlavor;
import io.micrometer.statsd.StatsdMeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;


import java.io.IOException;

@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	StatsdConfig config = new StatsdConfig() {
		@Override
		public String get(String k) {
			return null;
		}

		@Override
		public StatsdFlavor flavor() {
			return StatsdFlavor.DATADOG;
		}
	};

	MeterRegistry registry = new StatsdMeterRegistry(config, Clock.SYSTEM);


	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) throws InterruptedException, IOException {
		method0();
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

	@GetMapping("/gauge")
	public String gauge() throws InterruptedException, IOException {

		MeterRegistry registry = new StatsdMeterRegistry(config, Clock.SYSTEM);
		Integer gauge = registry.gauge("test", getRandomIntegerBetweenRange(1,20));

/*		List<String> list = new ArrayList<>(4);

		Gauge gauge = Gauge
				.builder("pej.data.cache.size", list, List::size)
				.tag("env", "datadoghq.com")
				.register(registry);

		list.add("12");

		return String.valueOf(gauge.value());
*/

		return gauge + "\n";
	}



	@GetMapping("/counter")
	public String counter() throws InterruptedException, IOException {

		Counter compteur = Counter
				.builder("pej.data.cache.counter")
				.description("indicates instance count of the object")
				.tags("env", "datadoghq.com")
				.register(registry);


		compteur.increment(1);


		return compteur.count() + "\n";
	}



	public static int getRandomIntegerBetweenRange(int min, int max){
		int x = (int)(Math.random()*((max-min)+1))+min;
		return x;
	}


	public static void method0() throws InterruptedException, IOException {
		Thread.sleep(350);
		method1();
		callWeb1();
		Thread.sleep(200);
		callWeb2();
	}

	public static void method1() throws InterruptedException {
		Thread.sleep(250);
	}

	public static void callWeb1() throws IOException {
		HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
		HttpRequest request = requestFactory.buildGetRequest(new GenericUrl("https://github.com"));
		request.execute().parseAsString();
	}

	public static void callWeb2() throws IOException {
		HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
		HttpRequest request = requestFactory.buildGetRequest(new GenericUrl("https://www.google.com"));
		request.execute().parseAsString();
	}

}