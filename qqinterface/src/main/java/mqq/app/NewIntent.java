package mqq.app;

import android.content.Context;
import android.content.Intent;

import mqq.observer.BusinessObserver;

public class NewIntent extends Intent {
    public NewIntent(Context context, Class<?> cls) {

    }
    public void setObserver(BusinessObserver businessObserver) {}
}
