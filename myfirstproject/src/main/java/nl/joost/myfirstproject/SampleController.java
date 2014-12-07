package nl.joost.myfirstproject;

import java.util.LinkedList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class SampleController {

	@RequestMapping("/hello")
	@ResponseBody
	String home() {
		return "Hello World! blah";
	}

	public void main(String[] args) throws Exception {
		SpringApplication.run(SampleController.class, args);
		MyFirstSingleton blah = MyFirstSingleton.getInstance();
		MyFirstSingleton blah2 = MyFirstSingleton.getInstance();

		List<String> list = new LinkedList<String>();
		foo(list);
		foo2(list);
		foo3(new AStrategy("a"));
		foo3(new BStrategy());

	}

	private static void foo3(IStrategy strategy) {
		strategy.performAction();
	}

	private interface IStrategy {
		void performAction();
	}

	public class AStrategy implements IStrategy {

		private BStrategy bstrategy;

		public AStrategy(String a) {

		}

		@Override
		public void performAction() {
			bstrategy.performAction();
		}
	}

	public class BStrategy implements IStrategy {

		@Override
		public void performAction() {
			System.out.println("b");
			System.out.println("b");
		}
	}

	public static void foo(List<String> list) {
		for (String blah : list) {
			System.out.println(blah);
		}
		System.out.println(list.get(2));
	}

	public static void foo2(List<String> list) {
		list.addAll(list);
		for (String blah : list) {
			System.out.println(blah);
		}
	}
}
