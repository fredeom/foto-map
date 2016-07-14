package meraschool;

import com.vaadin.Application;

public class Controller {
    private Application application;
	Controller(Application application) {
		System.out.println("Backbone db connection and state maintenance");
		this.application = application;
	}
	public Application getApplication() {
	    return application;
	}
}
