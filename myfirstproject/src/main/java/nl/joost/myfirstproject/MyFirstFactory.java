package nl.joost.myfirstproject;

public class MyFirstFactory {
	private static MyFirstFactory instance = null;

	public static MyFirstFactory getInstance() {
		if (instance == null) {
			instance = new MyFirstFactory();
		}
		return instance;
	}

	private MyFirstFactory() {
	}

	private String config;

	public RandomObject getRandomObject() {
		return new RandomObject(config);
	}

	public static RandomObject getRandomObject(String config) {
		return new RandomObject(config);
	}
}
