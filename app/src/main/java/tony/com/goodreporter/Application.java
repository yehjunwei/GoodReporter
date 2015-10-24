package tony.com.goodreporter;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by tony on 2015/10/16.
 */
public class Application extends android.app.Application {

    static final String APP_ID = "5Pi010xRwRxwBwolMFOEzemscSGDBkHIsvv52oVf";
    static final String CLIENT_KEY = "0jkB9VoumMqDHauoVY5TGubd7Om5WiUmgaRT34id";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(getApplicationContext(), APP_ID, CLIENT_KEY);
        ParseFacebookUtils.initialize(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
