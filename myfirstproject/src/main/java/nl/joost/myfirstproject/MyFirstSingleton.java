package nl.joost.myfirstproject;

public class MyFirstSingleton {

	private static MyFirstSingleton instance = null;

	public static MyFirstSingleton getInstance() {
		if (instance == null) {
			instance = new MyFirstSingleton();
		}
		return instance;
	}

	private MyFirstSingleton() {
	}
}
