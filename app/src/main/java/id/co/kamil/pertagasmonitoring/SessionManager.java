package id.co.kamil.pertagasmonitoring;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "PMONITORPREF";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_USER = "username"; //username
    public static final String KEY_TOKEN = "token"; // Token User
    public static final String KEY_WILAYAH = "wilayah"; // Wilayah User
    public static final String KEY_ID_WILAYAH = "id_wilayah"; // Wilayah User
    public static final String KEY_ID_PERUSAHAAN = "id_perusahaan"; // Wilayah User
    public static final String KEY_SINGKRON = "status_singkron"; // Status Singkron
    public static final String KEY_ROLE = "role"; // Role User
    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String user, String token,String wilayah,String id_wilayah, String role,String id_perusahaan){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USER, user);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_WILAYAH, wilayah);
        editor.putString(KEY_ID_WILAYAH, id_wilayah);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_ID_PERUSAHAAN, id_perusahaan);
        editor.commit();
    }

    public void setToken(String token){
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }
    public void setSingkron(boolean singkron){
        editor.putBoolean(KEY_SINGKRON, singkron);
        editor.commit();
    }
    public boolean getSingkron(){
        return pref.getBoolean(KEY_SINGKRON,false);
    }

    public void setUsername(String username){
        editor.putString(KEY_USER, username);
        editor.commit();
    }
    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){

        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_USER, pref.getString(KEY_USER, null));
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
        user.put(KEY_WILAYAH, pref.getString(KEY_WILAYAH, null));
        user.put(KEY_ID_WILAYAH, pref.getString(KEY_ID_WILAYAH, null));
        user.put(KEY_ROLE, pref.getString(KEY_ROLE, null));
        user.put(KEY_ID_PERUSAHAAN,  pref.getString(KEY_ID_PERUSAHAAN, null));

        return user;
    }

    public void clearData(){
        editor.clear();
        editor.commit();

        editor.putBoolean(IS_LOGIN, false);
        editor.putString(KEY_USER, "");
        editor.putString(KEY_TOKEN, "");
        editor.commit();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void updateUsername(String username) {
        editor.putString(KEY_USER, username);
        editor.commit();
    }
}
