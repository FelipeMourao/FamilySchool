package br.com.familyschool.familyschool.config;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import br.com.familyschool.familyschool.helper.Preferencias;

public class MeuFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "REG_TOKEN";
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        Preferencias preferencias = new Preferencias(this);
        preferencias.salvarToken(refreshedToken);
    }
}
