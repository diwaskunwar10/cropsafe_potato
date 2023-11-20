package com.example.cropsafe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

	private EditText editTextName;
	private EditText editTextEmail;
	private EditText editTextPassword;
	private EditText editTextConfirmPassword;
	private Button buttonLogin,buttonRedirect;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

		// Initialize your UI elements
		editTextName = findViewById(R.id.editTextName);
		editTextEmail = findViewById(R.id.editTextEmail);
		editTextPassword = findViewById(R.id.enterPassword);
		editTextConfirmPassword = findViewById(R.id.reenterPassword);
		buttonLogin = findViewById(R.id.buttonSignUp);
		buttonRedirect=findViewById(R.id.loginRedirect);

		buttonRedirect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(getApplicationContext(),LoginActivity.class));
			}
		});
		buttonLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = editTextName.getText().toString();
				String email = editTextEmail.getText().toString();
				String password = editTextPassword.getText().toString();
				String confirmPassword = editTextConfirmPassword.getText().toString();

				if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
					Toast.makeText(SignupActivity.this, "Please fill all input fields", Toast.LENGTH_SHORT).show();
				} else if (!password.equals(confirmPassword)) {
					Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
				//} else if (!isValidEmail(email)) {
				//	Toast.makeText(SignupActivity.this, "Incorrect email format", Toast.LENGTH_SHORT).show();
				} else {
				//	Log.d("DataSent", "Name: " + name + ", Email: " + email + ", Password: " + password);
					// Perform registration process here (send data to PHP script)
					performRegistration(name, email, password);
				}
			}
		});
	}

	private boolean performRegistration(String name, String email, String password) {

		String url = "http://172.17.18.189/cropsafe/sinup.php"; // Replace with your actual server URL

		// Create a StringRequest to make a POST request
		StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// Handle the response from the server
						try {
							JSONObject jsonResponse = new JSONObject(response);
							boolean error = jsonResponse.getBoolean("error");
							String message = jsonResponse.getString("message");

							if (!error) {
								// Registration successful
								Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
							} else {
								// Registration failed
								Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(SignupActivity.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// Handle errors
						Toast.makeText(SignupActivity.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
						Log.d("error", "onErrorResponse: "+error.getMessage());
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// Set the parameters to be sent to the server
				Map<String, String> params = new HashMap<>();
				params.put("name", name);
				params.put("email", email);
				params.put("password", password);
				return params;
			}
		};
		int timeout = 30000; // 30 seconds
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(
				timeout,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
		));

		// Add the request to the Volley request queue
		Volley.newRequestQueue(this).add(stringRequest);
		return true;
	}



}
