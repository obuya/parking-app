/*
 * UserRegistrationActivity.java
 * 4/6/14
 * Mat Ferry & Travis Carney
 * 
 * This activity handles getting information from the user to create a new user in our application.
 * Once the user enters valid input, a POST request is sent to our backend and the user is created.
 */
package team1.parkingapp;

import team1.parkingapp.rest.PostUserTask;
import team1.parkingapp.rest.RestTaskFactory;
import team1.parkingapp.rest.Session;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class UserRegistrationActivity extends Activity {
	private final static int MIN_PASSWORD_LENGTH = 4;
	private EditText email;			
	private EditText password;		
	private EditText confirmPwd;
	private EditText firstName;
	private EditText lastName;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration);
		
        // Assign views to class variables
        email = (EditText) findViewById(R.id.txtUserRegistration_Email);
        password = (EditText) findViewById(R.id.txtUserRegistration_Password);
        confirmPwd = (EditText) findViewById(R.id.txtUserRegistration_ConfirmPassword);
        firstName = (EditText) findViewById(R.id.txtUserRegistration_FirstName);
        lastName = (EditText) findViewById(R.id.txtUserRegistration_LastName);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	/*
	 * This is the onClick() listener for the confirm button.
	 * If the user entered valid data then submit the user creation request.
	 */
	public void onConfirmClick(View v) {
		String emailAddr = email.getText().toString();
		String fName = firstName.getText().toString();
		String lName = lastName.getText().toString();
		String pwd = password.getText().toString();
		
		if (verifyInput()) {
			// Start the POST request
			PostUserTask userTask = RestTaskFactory.createNewUser(this, emailAddr, pwd, fName, lName);
			try
			{
				userTask.get();
			}
			catch(Exception e)
			{
				
			}
			finally
			{
				if(Session.getUser() == null)
				{
					Toast.makeText(this, getResources().getString(R.string.email_taken), Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(this, "User Created", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	/*
	 * This is the onClick() listener for the clear button. It reset each of the fields to their default values.
	 */
	public void onClearClick(View v) {
		EditText[] fields = {email, password, confirmPwd, firstName, lastName};
		
		// Reset each value to the empty string ("")
		for (int i = 0; i < fields.length; i++)
			fields[i].setText(R.string.empty_string);
	}
	
	/*
	 * Resets the password and the confirmation password EditTexts.
	 * Useful when the user enters two passwords that don't match.
	 */
	private void resetPasswords() {
		password.setText(R.string.empty_string);
		confirmPwd.setText(R.string.empty_string);
	}
	
	/* 
	 * Checks if the user entered something in the email, password, and name fields.
	 * It also validates the email and password fields. If all of the fields are valid, then true is returned.
	 * Otherwise this method returns false.
	 */
	private boolean verifyInput() {
		String emailAddr = email.getText().toString();
		String fName = firstName.getText().toString();
		String lName = lastName.getText().toString();
		String pwd = password.getText().toString();
		String pwdConfirm = confirmPwd.getText().toString();
		
		if (!validateEmailAddress(emailAddr)) {					// Must enter a valid email address
			Toast.makeText(getApplicationContext(), R.string.invalid_email, Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (fName.isEmpty() || lName.isEmpty()) {			// Neither of the names can be blank
			Toast.makeText(getApplicationContext(), R.string.invalid_names, Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (pwd.isEmpty()  || pwdConfirm.isEmpty()) {		// Neither of the passwords can be blank
			Toast.makeText(getApplicationContext(), R.string.invalid_password, Toast.LENGTH_SHORT).show();
			return false;
		}
		else if ( !(pwd.equals(pwdConfirm)) ) {					// The passwords must match
			Toast.makeText(getApplicationContext(), R.string.invalid_passwords, Toast.LENGTH_SHORT).show();
			resetPasswords();
			return false;
		}
		else if (pwd.length() < MIN_PASSWORD_LENGTH) {
			Toast.makeText(getApplicationContext(), R.string.invalid_password_length, Toast.LENGTH_SHORT).show();
			resetPasswords();
			return false;
		}
		
		return true;											// The user entered valid input, allow the create request to happen
	}
	
	/*
	 * Ensures that the user has entered a valid email address.
	 * Meaning that they didn't enter nothing and that it follows the correct format for an email address.
	 */
	private boolean validateEmailAddress(String emailAddr) {
		if (emailAddr.equals("")) // Can't have a blank address
			return false;
		
		return android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddr).matches();
	}
}