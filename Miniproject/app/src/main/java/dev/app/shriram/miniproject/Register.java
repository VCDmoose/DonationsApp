package dev.app.shriram.miniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pshri on 4/23/2016.
 */
public class Register extends AppCompatActivity {

    private Button reg;
    private EditText inputName, inputEmail, inputPassword,Addr1,Addr2,Addr4;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword,input_layout_addr1,input_layout_addr4,input_layout_addr2;

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    int donororreceivercheck=0;
    private CheckBox ch1, ch2;
    int dr = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name1);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password1);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email1);
        input_layout_addr1=(TextInputLayout)findViewById(R.id.input_layout_addr1);
        input_layout_addr2=(TextInputLayout)findViewById(R.id.input_layout_addr2);
        input_layout_addr4=(TextInputLayout)findViewById(R.id.input_layout_addr4);
        Addr1=(EditText)findViewById(R.id.Addr1);
        Addr2=(EditText)findViewById(R.id.Addr2);
        Addr4=(EditText)findViewById(R.id.Addr4);
        inputName = (EditText) findViewById(R.id.Name1);
        inputEmail = (EditText) findViewById(R.id.Email1);
        inputPassword = (EditText) findViewById(R.id.Password1);

        reg = (Button) findViewById(R.id.register);
        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        Addr1.addTextChangedListener(new MyTextWatcher(Addr1));
        Addr2.addTextChangedListener(new MyTextWatcher(Addr2));
        Addr4.addTextChangedListener(new MyTextWatcher(Addr4));
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = prefs.edit();
                ch1 = (CheckBox) findViewById(R.id.Check3);
                ch2 = (CheckBox) findViewById(R.id.Check4);
                if (ch1.isChecked() && ch2.isChecked()) {
                    dr = 3;
                } else if (ch1.isChecked()) {
                    donororreceivercheck=1;
                    editor.putInt("Login_Check", 1);
                    editor.putInt("DRCheck", donororreceivercheck);
                    dr = 1;
                } else if (ch2.isChecked()) {
                    donororreceivercheck=2;
                    editor.putInt("Login_Check", 2);
                    editor.putInt("DRCheck", donororreceivercheck);
                    dr = 2;
                } else {
                    dr = 0;
                }
                String n = inputName.getText().toString();
                String m = inputEmail.getText().toString();
                String o = inputPassword.getText().toString();
                String p = Addr1.getText().toString();
                String q = Addr2.getText().toString();
                String r = Addr4.getText().toString();
                editor.putString("Username", n);
                editor.putString("Email", m);
                editor.putString("Password", o);
                editor.putString("Addrl1", p);
                editor.putString("Addrl2", q);
                editor.putString("Phone", r);
                registerUser(n, m, o, p, q, r);

                editor.apply();
            }
        });
    }
    private void showJSON(String response){
        Log.v("Response",response);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = prefs.edit();
        String name="";
        String email="";
        int id = 0;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");
            JSONObject collegeData = result.getJSONObject(0);
            name = collegeData.getString("name");
            Log.v("Working",name);
           String addr1=collegeData.getString("addrl1");
            String addr2=collegeData.getString("addrl2");
            String phone=collegeData.getString("phone");
            email = collegeData.getString("email");
            id=collegeData.getInt("id");
            editor.putInt("id",id);
            editor.putString("name", name);
            editor.putString("email", email);
           editor.putString("addrl1",addr1);
            editor.putString("addrl2",addr2);
            editor.putString("phone",phone);
            Log.v("Working",name);
            editor.apply();
            submitForm(dr);

        } catch (JSONException e) {
            e.printStackTrace();
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.v("Working","notworking");
        }

    }
    private void registerUser(final String name, final String email,final String password,final String addl1, final String addl2,final String phn){
        final String username = name.trim();
        final String pass = password.trim();
        final String ema=email.trim();
        final String addrl1 = addl1.trim();
        final String addrl2 = addl2.trim();
        final String phone=phn.trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,"http://trialnew.16mb.com/insert-db.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Register.this,response,Toast.LENGTH_LONG).show();
                        showJSON(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Register.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name",username);
                params.put("password",pass);
                params.put("email",ema);
                params.put("addrl1",addrl1);
                params.put("addrl2",addrl2);
                params.put("phone",phone);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void submitForm(int dr) {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }
        if (dr == 1) {
            Intent intent = new Intent(Register.this, MainActivity.class);
            Toast.makeText(Register.this, "Thanks", Toast.LENGTH_LONG).show();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (dr == 2) {
            Intent intent = new Intent(Register.this, MainActivityR.class);
            Toast.makeText(Register.this, "Thanks", Toast.LENGTH_LONG).show();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (dr == 0) {
            Toast.makeText(Register.this, "Check only one", Toast.LENGTH_LONG).show();
        } else if (dr == 3) {
            Toast.makeText(Register.this, "Check only one", Toast.LENGTH_LONG).show();
        }

    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.Name1:
                    validateName();
                    break;
                case R.id.Email1:
                    validateEmail();
                    break;
                case R.id.Password1:
                    validatePassword();
                    break;
            }
        }
    }
}
