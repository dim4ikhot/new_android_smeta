package ua.com.expertsoft.android_smeta;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ua.com.expertsoft.android_smeta.dialogs.InfoCommonDialog;
import ua.com.expertsoft.android_smeta.language.UpdateLanguage;
import ua.com.expertsoft.android_smeta.static_data.CompilerParams;
import ua.com.expertsoft.android_smeta.static_data.UserLoginInfo;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    public static final String EMAIL_KEY = "emailAddress";
    public static final String PASSWORD_KEY = "accountPassword";
    public static final String REMEMBER_ME = "rememberMe";
    public static final String SERVICE_ITEM = "whatService";
    private static final String LOGIN_LINK = /*"http://195.62.15.35:8084*/"/test_cad/php/usr_controller_api.php?action=login";
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static String[] DUMMY_CREDENTIALS = new String[1];

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    ActionBar bar;
    CharSequence title;
    private SharedPreferences pref;
    SharedPreferences.Editor edit;
    private boolean progressShown = false;
    private boolean activityShown = false;
    private boolean isSomeOperation = false;
    private CheckBox remember;
    private EncryptorPassword ep;
    private Spinner service;
    TextView registration, serviceTxt;
    int touchCounter = 0;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        updateAppConfiguration();
        setContentView(R.layout.activity_login);
        bar = getSupportActionBar();
        title = getResources().getString(R.string.title_activity_login);
        if (bar != null){
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(title);
        }
        isSomeOperation = getIntent().getBooleanExtra("isSomeOperation", false);
        pref = PreferenceManager.getDefaultSharedPreferences(this); //getPreferences(MODE_PRIVATE);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        //populateAutoComplete();
        remember = (CheckBox)findViewById(R.id.rememberMe);
        service = (Spinner)findViewById(R.id.service);
        registration = (TextView)findViewById(R.id.registration);
        registration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(service.getSelectedItem().toString()));
                startActivity(intent);
            }
        });
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        if(pref != null){
            String mail = pref.getString(EMAIL_KEY,"");
            String pass = pref.getString(PASSWORD_KEY, "");
            ep = new EncryptorPassword();
            if (! pass.equals("")){
                try {
                    pass = ep.decrypt(Base64.decode(pass, Base64.DEFAULT));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            String[] array = getResources().getStringArray(R.array.service_links);
            String[] correctServiece = new String[1];
            for(String s : array){
                switch (CompilerParams.getAppLanguage()){
                    case "ru":
                    case "uk":
                        if(s.contains(".ru")){
                            correctServiece[0] = s;
                            break;
                        }
                        break;
                    case "en":
                        if(s.contains(".net")){
                            correctServiece[0] = s;
                            break;
                        }
                        break;
                }
            }
            ArrayAdapter<String> spinnerArrayAdapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, correctServiece);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            service.setAdapter(spinnerArrayAdapter);
            serviceTxt = (TextView)findViewById(R.id.service_txt);

            serviceTxt.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        touchCounter ++;
                        if(touchCounter == 8){
                            String[] arrayLocal = getResources().getStringArray(R.array.service_links_paschal);
                            ArrayAdapter<String> spinnerArrayAdapter =
                                    new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_spinner_item, arrayLocal);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            service.setAdapter(spinnerArrayAdapter);
                            String serv = pref.getString(SERVICE_ITEM,"");
                            if(serv.equals("")){
                                service.setSelection(getItemByLocale());
                            }else{
                                int item = -1;
                                for(int i = 0; i<arrayLocal.length; i++){
                                    if(arrayLocal[i].equals(serv)){
                                        item = i;
                                        break;
                                    }
                                }
                                service.setSelection(item);
                            }
                            InfoCommonDialog dlg = new InfoCommonDialog();
                            dlg.setTitle("Докликался :)");
                            dlg.setMessage("Вы открыли сервис разработчика. " +
                                    "Если вы им не являетесь, проигнорируйте появившийся сервис.");
                            dlg.show(getSupportFragmentManager(), "developerKit");
                            touchCounter = 0;
                        }
                    }
                    return false;
                }
            });

            String serv = pref.getString(SERVICE_ITEM,"");
            if(serv.equals("")){
                service.setSelection(0/*getItemByLocale()*/);
            }else{
                int item = -1;
                for(int i = 0; i<array.length; i++){
                    if(array[i].equals(serv)){
                        item = i;
                        break;
                    }
                }
                service.setSelection(item);
            }
            DUMMY_CREDENTIALS[0] = mail + ":" + pass;
            boolean remember = pref.getBoolean(REMEMBER_ME, false);
            mPasswordView.setText(pass);
            mEmailView.setText(mail);
            this.remember.setChecked(remember);
        }
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        if(mEmailSignInButton != null) {
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
        }

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        activityShown = true;
    }

    private int getItemByLocale(){
        if(Locale.getDefault().toString().equals("ru")||
                Locale.getDefault().toString().equals("uk")){
            return 1;
            //return 0;
        }
        else{
            return 0;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String serviceTxt = service.getSelectedItem().toString();
        DUMMY_CREDENTIALS[0] = email + ":" + password;
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password,serviceTxt);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        progressShown = show;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }

    public void updateAppConfiguration(){
        UpdateLanguage.updateAppConfiguration(this, new UpdateLanguage.onUpdateLocaleListener() {
            @Override
            public void onUpdateLocale() {
                invalidateOptionsMenu();
            }
        });
    }

    public interface OnAuthorizedListener{
        void onAuthorized(boolean isAuthorized, String name);
    }


    public void startAuthoriz(Context ctx, String email, String pass, String service){
        UserLoginTask task = new UserLoginTask(email,pass,service);
        if(email.equals("")& pass.equals("")){
            DUMMY_CREDENTIALS[0] = "";
        }else {
            DUMMY_CREDENTIALS[0] = email + ":" + pass;
        }
        task.setContext(ctx);
        task.execute((Void) null);
    }

    public static String getServies(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx)
                .getString(LoginActivity.SERVICE_ITEM,"http://195.62.15.35:8084");
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mEmail;
        private String mPassword;
        private Context context;
        private String service;

        UserLoginTask(String email, String password, String service) {
            mEmail = email;
            mPassword = password;
            this.service = service;
        }

        public void setContext(Context ctx){
            context = ctx;
        }
        @Override
        protected Integer doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            int result = -1;
            try {

                ep = new EncryptorPassword();
                byte[] remember = ep.encrypt(mPassword.getBytes("UTF-8"));
                //Save this string to preferences instead the password
                mPassword = Base64.encodeToString(remember, Base64.DEFAULT);
                String loginParams = "&email="+mEmail + "&pass="+mPassword;
                URL url = new URL(service + LOGIN_LINK + loginParams);
                HttpURLConnection loginhttp = (HttpURLConnection)url.openConnection();
                loginhttp.setDoInput(true); // Allow Inputs
                loginhttp.setDoOutput(true); // Allow Outputs
                loginhttp.setUseCaches(false);
                loginhttp.setConnectTimeout(1000 * 5);
                //loginhttp.setRequestMethod("POST");
                loginhttp.setRequestMethod("GET");

                /*
                OutputStream os = loginhttp.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(mEmail, mPassword));
                writer.flush();
                writer.close();
                os.close();
                */

                loginhttp.connect();

                if(loginhttp.getResponseCode() == HttpURLConnection.HTTP_OK){
                    InputStream stream = loginhttp.getInputStream();
                    String JSONString = streamToString(stream);
                    try {
                        JSONObject jsonObject = new JSONObject(JSONString);
                        if (jsonObject.getString("message").equals("logged_in")){
                            UserLoginInfo.session = jsonObject.getString("cookie_id");
                            UserLoginInfo.userId = jsonObject.getInt("user_id");result =  0;
                            UserLoginInfo.logo = jsonObject.getString("logo_base64");
                        }else
                        if (jsonObject.getString("message").equals("wrong_pass")){
                            result =  1;
                        }
                        else
                        if (jsonObject.getString("message").equals("no_such_user")){
                            result =  2;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    result = 5;
                }
                loginhttp.disconnect();
            }catch(Exception e){
                result = 5;
                e.printStackTrace();
            }
            // TODO: register the new account here.
            if(mEmail.equals("")||mEmail.length()<3 || !mEmail.contains("@")) {
                result = 1;
            }
            return result;
        }

        private String getQuery(String email, String pass) throws UnsupportedEncodingException
        {
            StringBuilder result = new StringBuilder();
            result.append(URLEncoder.encode("email", "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(email, "UTF-8"));
            result.append("&");
            result.append(URLEncoder.encode("pass", "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pass, "UTF-8"));
            return result.toString();
        }

        private String streamToString(InputStream stream){
            String streamString = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String ch;
            try {
                while ((ch = reader.readLine()) != null ) {
                    streamString += ch + "\n";
                }
                reader.close();
            }catch(IOException e){
                e.printStackTrace();
            }
            return streamString;
        }

        @Override
        protected void onPostExecute(final Integer success) {
            mAuthTask = null;
            if(progressShown) {
                showProgress(false);
            }
            if (success == 0) {
                setResult(RESULT_OK, new Intent()
                        .putExtra("email", mEmail)
                        .putExtra("isSomeOperation", isSomeOperation));

                if(pref != null) {
                    edit = pref.edit();
                    edit.putString(EMAIL_KEY, mEmail);
                    edit.putString(PASSWORD_KEY, mPassword);
                    edit.putString(SERVICE_ITEM, service);
                    edit.putBoolean(REMEMBER_ME, remember.isChecked());
                    edit.apply();
                }
                finish();
            } else if (success == 1) {
                if(activityShown) {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
            }else if (success == 2) {
                if(activityShown) {
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    mEmailView.requestFocus();
                }
            }
            else if (success == 5){
                if(activityShown) {
                    String badAuthorization = getResources().getString(R.string.bad_authenticate_dialog_message);
                    InfoCommonDialog dlg = new InfoCommonDialog();
                    dlg.setMessage(badAuthorization);
                    dlg.show(getSupportFragmentManager(), "badAuthorizationDialog");
                }
            }
            if(context != null) {
                ((OnAuthorizedListener) context).onAuthorized(success == 0, mEmail);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

